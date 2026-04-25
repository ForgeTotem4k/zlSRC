package io.sentry;

import io.sentry.clientreport.DiscardReason;
import io.sentry.exception.SentryEnvelopeException;
import io.sentry.hints.AbnormalExit;
import io.sentry.hints.Backfillable;
import io.sentry.hints.DiskFlushNotification;
import io.sentry.hints.TransactionEnd;
import io.sentry.metrics.EncodedMetrics;
import io.sentry.metrics.IMetricsClient;
import io.sentry.metrics.NoopMetricsAggregator;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.transport.ITransport;
import io.sentry.transport.RateLimiter;
import io.sentry.util.CheckInUtils;
import io.sentry.util.HintUtils;
import io.sentry.util.Objects;
import io.sentry.util.TracingUtils;
import java.io.Closeable;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

public final class SentryClient implements ISentryClient, IMetricsClient {
  static final String SENTRY_PROTOCOL_VERSION = "7";
  
  private boolean enabled;
  
  @NotNull
  private final SentryOptions options;
  
  @NotNull
  private final ITransport transport;
  
  @Nullable
  private final SecureRandom random;
  
  @NotNull
  private final SortBreadcrumbsByDate sortBreadcrumbsByDate = new SortBreadcrumbsByDate();
  
  @NotNull
  private final IMetricsAggregator metricsAggregator;
  
  public boolean isEnabled() {
    return this.enabled;
  }
  
  SentryClient(@NotNull SentryOptions paramSentryOptions) {
    this.options = (SentryOptions)Objects.requireNonNull(paramSentryOptions, "SentryOptions is required.");
    this.enabled = true;
    ITransportFactory iTransportFactory = paramSentryOptions.getTransportFactory();
    if (iTransportFactory instanceof NoOpTransportFactory) {
      iTransportFactory = new AsyncHttpTransportFactory();
      paramSentryOptions.setTransportFactory(iTransportFactory);
    } 
    RequestDetailsResolver requestDetailsResolver = new RequestDetailsResolver(paramSentryOptions);
    this.transport = iTransportFactory.create(paramSentryOptions, requestDetailsResolver.resolve());
    this.metricsAggregator = paramSentryOptions.isEnableMetrics() ? new MetricsAggregator(paramSentryOptions, this) : (IMetricsAggregator)NoopMetricsAggregator.getInstance();
    this.random = (paramSentryOptions.getSampleRate() == null) ? null : new SecureRandom();
  }
  
  private boolean shouldApplyScopeData(@NotNull SentryBaseEvent paramSentryBaseEvent, @NotNull Hint paramHint) {
    if (HintUtils.shouldApplyScopeData(paramHint))
      return true; 
    this.options.getLogger().log(SentryLevel.DEBUG, "Event was cached so not applying scope: %s", new Object[] { paramSentryBaseEvent.getEventId() });
    return false;
  }
  
  private boolean shouldApplyScopeData(@NotNull CheckIn paramCheckIn, @NotNull Hint paramHint) {
    if (HintUtils.shouldApplyScopeData(paramHint))
      return true; 
    this.options.getLogger().log(SentryLevel.DEBUG, "Check-in was cached so not applying scope: %s", new Object[] { paramCheckIn.getCheckInId() });
    return false;
  }
  
  @NotNull
  public SentryId captureEvent(@NotNull SentryEvent paramSentryEvent, @Nullable IScope paramIScope, @Nullable Hint paramHint) {
    Objects.requireNonNull(paramSentryEvent, "SentryEvent is required.");
    if (paramHint == null)
      paramHint = new Hint(); 
    if (shouldApplyScopeData(paramSentryEvent, paramHint))
      addScopeAttachmentsToHint(paramIScope, paramHint); 
    this.options.getLogger().log(SentryLevel.DEBUG, "Capturing event: %s", new Object[] { paramSentryEvent.getEventId() });
    if (paramSentryEvent != null) {
      Throwable throwable = paramSentryEvent.getThrowable();
      if (throwable != null && this.options.containsIgnoredExceptionForType(throwable)) {
        this.options.getLogger().log(SentryLevel.DEBUG, "Event was dropped as the exception %s is ignored", new Object[] { throwable.getClass() });
        this.options.getClientReportRecorder().recordLostEvent(DiscardReason.EVENT_PROCESSOR, DataCategory.Error);
        return SentryId.EMPTY_ID;
      } 
    } 
    if (shouldApplyScopeData(paramSentryEvent, paramHint)) {
      paramSentryEvent = applyScope(paramSentryEvent, paramIScope, paramHint);
      if (paramSentryEvent == null) {
        this.options.getLogger().log(SentryLevel.DEBUG, "Event was dropped by applyScope", new Object[0]);
        return SentryId.EMPTY_ID;
      } 
    } 
    paramSentryEvent = processEvent(paramSentryEvent, paramHint, this.options.getEventProcessors());
    if (paramSentryEvent != null) {
      paramSentryEvent = executeBeforeSend(paramSentryEvent, paramHint);
      if (paramSentryEvent == null) {
        this.options.getLogger().log(SentryLevel.DEBUG, "Event was dropped by beforeSend", new Object[0]);
        this.options.getClientReportRecorder().recordLostEvent(DiscardReason.BEFORE_SEND, DataCategory.Error);
      } 
    } 
    if (paramSentryEvent == null)
      return SentryId.EMPTY_ID; 
    Session session1 = (paramIScope != null) ? paramIScope.withSession(paramSession -> {
        
        }) : null;
    Session session2 = null;
    if (paramSentryEvent != null) {
      if (session1 == null || !session1.isTerminated())
        session2 = updateSessionData(paramSentryEvent, paramHint, paramIScope); 
      if (!sample()) {
        this.options.getLogger().log(SentryLevel.DEBUG, "Event %s was dropped due to sampling decision.", new Object[] { paramSentryEvent.getEventId() });
        this.options.getClientReportRecorder().recordLostEvent(DiscardReason.SAMPLE_RATE, DataCategory.Error);
        paramSentryEvent = null;
      } 
    } 
    boolean bool = shouldSendSessionUpdateForDroppedEvent(session1, session2);
    if (paramSentryEvent == null && !bool) {
      this.options.getLogger().log(SentryLevel.DEBUG, "Not sending session update for dropped event as it did not cause the session health to change.", new Object[0]);
      return SentryId.EMPTY_ID;
    } 
    SentryId sentryId = SentryId.EMPTY_ID;
    if (paramSentryEvent != null && paramSentryEvent.getEventId() != null)
      sentryId = paramSentryEvent.getEventId(); 
    try {
      TraceContext traceContext = null;
      if (HintUtils.hasType(paramHint, Backfillable.class)) {
        if (paramSentryEvent != null) {
          Baggage baggage = Baggage.fromEvent(paramSentryEvent, this.options);
          traceContext = baggage.toTraceContext();
        } 
      } else if (paramIScope != null) {
        ITransaction iTransaction = paramIScope.getTransaction();
        if (iTransaction != null) {
          traceContext = iTransaction.traceContext();
        } else {
          PropagationContext propagationContext = TracingUtils.maybeUpdateBaggage(paramIScope, this.options);
          traceContext = propagationContext.traceContext();
        } 
      } 
      boolean bool1 = (paramSentryEvent != null) ? true : false;
      List<Attachment> list = bool1 ? getAttachments(paramHint) : null;
      SentryEnvelope sentryEnvelope = buildEnvelope(paramSentryEvent, list, session2, traceContext, null);
      paramHint.clear();
      if (sentryEnvelope != null)
        sentryId = sendEnvelope(sentryEnvelope, paramHint); 
    } catch (IOException|SentryEnvelopeException iOException) {
      this.options.getLogger().log(SentryLevel.WARNING, iOException, "Capturing event %s failed.", new Object[] { sentryId });
      sentryId = SentryId.EMPTY_ID;
    } 
    if (paramIScope != null) {
      ITransaction iTransaction = paramIScope.getTransaction();
      if (iTransaction != null && HintUtils.hasType(paramHint, TransactionEnd.class)) {
        Object object = HintUtils.getSentrySdkHint(paramHint);
        if (object instanceof DiskFlushNotification) {
          ((DiskFlushNotification)object).setFlushable(iTransaction.getEventId());
          iTransaction.forceFinish(SpanStatus.ABORTED, false, paramHint);
        } else {
          iTransaction.forceFinish(SpanStatus.ABORTED, false, null);
        } 
      } 
    } 
    return sentryId;
  }
  
  private void addScopeAttachmentsToHint(@Nullable IScope paramIScope, @NotNull Hint paramHint) {
    if (paramIScope != null)
      paramHint.addAttachments(paramIScope.getAttachments()); 
  }
  
  private boolean shouldSendSessionUpdateForDroppedEvent(@Nullable Session paramSession1, @Nullable Session paramSession2) {
    if (paramSession2 == null)
      return false; 
    if (paramSession1 == null)
      return true; 
    boolean bool1 = (paramSession2.getStatus() == Session.State.Crashed && paramSession1.getStatus() != Session.State.Crashed) ? true : false;
    if (bool1)
      return true; 
    boolean bool2 = (paramSession2.errorCount() > 0 && paramSession1.errorCount() <= 0) ? true : false;
    return bool2;
  }
  
  @Nullable
  private List<Attachment> getAttachments(@NotNull Hint paramHint) {
    List<Attachment> list = paramHint.getAttachments();
    Attachment attachment1 = paramHint.getScreenshot();
    if (attachment1 != null)
      list.add(attachment1); 
    Attachment attachment2 = paramHint.getViewHierarchy();
    if (attachment2 != null)
      list.add(attachment2); 
    Attachment attachment3 = paramHint.getThreadDump();
    if (attachment3 != null)
      list.add(attachment3); 
    return list;
  }
  
  @Nullable
  private SentryEnvelope buildEnvelope(@Nullable SentryBaseEvent paramSentryBaseEvent, @Nullable List<Attachment> paramList, @Nullable Session paramSession, @Nullable TraceContext paramTraceContext, @Nullable ProfilingTraceData paramProfilingTraceData) throws IOException, SentryEnvelopeException {
    SentryId sentryId = null;
    ArrayList<SentryEnvelopeItem> arrayList = new ArrayList();
    if (paramSentryBaseEvent != null) {
      SentryEnvelopeItem sentryEnvelopeItem = SentryEnvelopeItem.fromEvent(this.options.getSerializer(), paramSentryBaseEvent);
      arrayList.add(sentryEnvelopeItem);
      sentryId = paramSentryBaseEvent.getEventId();
    } 
    if (paramSession != null) {
      SentryEnvelopeItem sentryEnvelopeItem = SentryEnvelopeItem.fromSession(this.options.getSerializer(), paramSession);
      arrayList.add(sentryEnvelopeItem);
    } 
    if (paramProfilingTraceData != null) {
      SentryEnvelopeItem sentryEnvelopeItem = SentryEnvelopeItem.fromProfilingTrace(paramProfilingTraceData, this.options.getMaxTraceFileSize(), this.options.getSerializer());
      arrayList.add(sentryEnvelopeItem);
      if (sentryId == null)
        sentryId = new SentryId(paramProfilingTraceData.getProfileId()); 
    } 
    if (paramList != null)
      for (Attachment attachment : paramList) {
        SentryEnvelopeItem sentryEnvelopeItem = SentryEnvelopeItem.fromAttachment(this.options.getSerializer(), this.options.getLogger(), attachment, this.options.getMaxAttachmentSize());
        arrayList.add(sentryEnvelopeItem);
      }  
    if (!arrayList.isEmpty()) {
      SentryEnvelopeHeader sentryEnvelopeHeader = new SentryEnvelopeHeader(sentryId, this.options.getSdkVersion(), paramTraceContext);
      return new SentryEnvelope(sentryEnvelopeHeader, arrayList);
    } 
    return null;
  }
  
  @Nullable
  private SentryEvent processEvent(@NotNull SentryEvent paramSentryEvent, @NotNull Hint paramHint, @NotNull List<EventProcessor> paramList) {
    for (EventProcessor eventProcessor : paramList) {
      try {
        boolean bool1 = eventProcessor instanceof BackfillingEventProcessor;
        boolean bool2 = HintUtils.hasType(paramHint, Backfillable.class);
        if (bool2 && bool1) {
          paramSentryEvent = eventProcessor.process(paramSentryEvent, paramHint);
        } else if (!bool2 && !bool1) {
          paramSentryEvent = eventProcessor.process(paramSentryEvent, paramHint);
        } 
      } catch (Throwable throwable) {
        this.options.getLogger().log(SentryLevel.ERROR, throwable, "An exception occurred while processing event by processor: %s", new Object[] { eventProcessor.getClass().getName() });
      } 
      if (paramSentryEvent == null) {
        this.options.getLogger().log(SentryLevel.DEBUG, "Event was dropped by a processor: %s", new Object[] { eventProcessor.getClass().getName() });
        this.options.getClientReportRecorder().recordLostEvent(DiscardReason.EVENT_PROCESSOR, DataCategory.Error);
        break;
      } 
    } 
    return paramSentryEvent;
  }
  
  @Nullable
  private SentryTransaction processTransaction(@NotNull SentryTransaction paramSentryTransaction, @NotNull Hint paramHint, @NotNull List<EventProcessor> paramList) {
    for (EventProcessor eventProcessor : paramList) {
      int i = paramSentryTransaction.getSpans().size();
      try {
        paramSentryTransaction = eventProcessor.process(paramSentryTransaction, paramHint);
      } catch (Throwable throwable) {
        this.options.getLogger().log(SentryLevel.ERROR, throwable, "An exception occurred while processing transaction by processor: %s", new Object[] { eventProcessor.getClass().getName() });
      } 
      byte b = (paramSentryTransaction == null) ? 0 : paramSentryTransaction.getSpans().size();
      if (paramSentryTransaction == null) {
        this.options.getLogger().log(SentryLevel.DEBUG, "Transaction was dropped by a processor: %s", new Object[] { eventProcessor.getClass().getName() });
        this.options.getClientReportRecorder().recordLostEvent(DiscardReason.EVENT_PROCESSOR, DataCategory.Transaction);
        this.options.getClientReportRecorder().recordLostEvent(DiscardReason.EVENT_PROCESSOR, DataCategory.Span, (i + 1));
        break;
      } 
      if (b < i) {
        int j = i - b;
        this.options.getLogger().log(SentryLevel.DEBUG, "%d spans were dropped by a processor: %s", new Object[] { Integer.valueOf(j), eventProcessor.getClass().getName() });
        this.options.getClientReportRecorder().recordLostEvent(DiscardReason.EVENT_PROCESSOR, DataCategory.Span, j);
      } 
    } 
    return paramSentryTransaction;
  }
  
  public void captureUserFeedback(@NotNull UserFeedback paramUserFeedback) {
    Objects.requireNonNull(paramUserFeedback, "SentryEvent is required.");
    if (SentryId.EMPTY_ID.equals(paramUserFeedback.getEventId())) {
      this.options.getLogger().log(SentryLevel.WARNING, "Capturing userFeedback without a Sentry Id.", new Object[0]);
      return;
    } 
    this.options.getLogger().log(SentryLevel.DEBUG, "Capturing userFeedback: %s", new Object[] { paramUserFeedback.getEventId() });
    try {
      SentryEnvelope sentryEnvelope = buildEnvelope(paramUserFeedback);
      sendEnvelope(sentryEnvelope, null);
    } catch (IOException iOException) {
      this.options.getLogger().log(SentryLevel.WARNING, iOException, "Capturing user feedback %s failed.", new Object[] { paramUserFeedback.getEventId() });
    } 
  }
  
  @NotNull
  private SentryEnvelope buildEnvelope(@NotNull UserFeedback paramUserFeedback) {
    ArrayList<SentryEnvelopeItem> arrayList = new ArrayList();
    SentryEnvelopeItem sentryEnvelopeItem = SentryEnvelopeItem.fromUserFeedback(this.options.getSerializer(), paramUserFeedback);
    arrayList.add(sentryEnvelopeItem);
    SentryEnvelopeHeader sentryEnvelopeHeader = new SentryEnvelopeHeader(paramUserFeedback.getEventId(), this.options.getSdkVersion());
    return new SentryEnvelope(sentryEnvelopeHeader, arrayList);
  }
  
  @NotNull
  private SentryEnvelope buildEnvelope(@NotNull CheckIn paramCheckIn, @Nullable TraceContext paramTraceContext) {
    ArrayList<SentryEnvelopeItem> arrayList = new ArrayList();
    SentryEnvelopeItem sentryEnvelopeItem = SentryEnvelopeItem.fromCheckIn(this.options.getSerializer(), paramCheckIn);
    arrayList.add(sentryEnvelopeItem);
    SentryEnvelopeHeader sentryEnvelopeHeader = new SentryEnvelopeHeader(paramCheckIn.getCheckInId(), this.options.getSdkVersion(), paramTraceContext);
    return new SentryEnvelope(sentryEnvelopeHeader, arrayList);
  }
  
  @TestOnly
  @Nullable
  Session updateSessionData(@NotNull SentryEvent paramSentryEvent, @NotNull Hint paramHint, @Nullable IScope paramIScope) {
    Session session = null;
    if (HintUtils.shouldApplyScopeData(paramHint))
      if (paramIScope != null) {
        session = paramIScope.withSession(paramSession -> {
              if (paramSession != null) {
                Session.State state = null;
                if (paramSentryEvent.isCrashed())
                  state = Session.State.Crashed; 
                boolean bool = false;
                if (Session.State.Crashed == state || paramSentryEvent.isErrored())
                  bool = true; 
                String str1 = null;
                if (paramSentryEvent.getRequest() != null && paramSentryEvent.getRequest().getHeaders() != null && paramSentryEvent.getRequest().getHeaders().containsKey("user-agent"))
                  str1 = (String)paramSentryEvent.getRequest().getHeaders().get("user-agent"); 
                Object object = HintUtils.getSentrySdkHint(paramHint);
                String str2 = null;
                if (object instanceof AbnormalExit) {
                  str2 = ((AbnormalExit)object).mechanism();
                  state = Session.State.Abnormal;
                } 
                if (paramSession.update(state, str1, bool, str2) && paramSession.isTerminated())
                  paramSession.end(); 
              } else {
                this.options.getLogger().log(SentryLevel.INFO, "Session is null on scope.withSession", new Object[0]);
              } 
            });
      } else {
        this.options.getLogger().log(SentryLevel.INFO, "Scope is null on client.captureEvent", new Object[0]);
      }  
    return session;
  }
  
  @Internal
  public void captureSession(@NotNull Session paramSession, @Nullable Hint paramHint) {
    SentryEnvelope sentryEnvelope;
    Objects.requireNonNull(paramSession, "Session is required.");
    if (paramSession.getRelease() == null || paramSession.getRelease().isEmpty()) {
      this.options.getLogger().log(SentryLevel.WARNING, "Sessions can't be captured without setting a release.", new Object[0]);
      return;
    } 
    try {
      sentryEnvelope = SentryEnvelope.from(this.options.getSerializer(), paramSession, this.options.getSdkVersion());
    } catch (IOException iOException) {
      this.options.getLogger().log(SentryLevel.ERROR, "Failed to capture session.", iOException);
      return;
    } 
    captureEnvelope(sentryEnvelope, paramHint);
  }
  
  @Internal
  @NotNull
  public SentryId captureEnvelope(@NotNull SentryEnvelope paramSentryEnvelope, @Nullable Hint paramHint) {
    Objects.requireNonNull(paramSentryEnvelope, "SentryEnvelope is required.");
    if (paramHint == null)
      paramHint = new Hint(); 
    try {
      paramHint.clear();
      return sendEnvelope(paramSentryEnvelope, paramHint);
    } catch (IOException iOException) {
      this.options.getLogger().log(SentryLevel.ERROR, "Failed to capture envelope.", iOException);
      return SentryId.EMPTY_ID;
    } 
  }
  
  @NotNull
  private SentryId sendEnvelope(@NotNull SentryEnvelope paramSentryEnvelope, @Nullable Hint paramHint) throws IOException {
    SentryOptions.BeforeEnvelopeCallback beforeEnvelopeCallback = this.options.getBeforeEnvelopeCallback();
    if (beforeEnvelopeCallback != null)
      try {
        beforeEnvelopeCallback.execute(paramSentryEnvelope, paramHint);
      } catch (Throwable throwable) {
        this.options.getLogger().log(SentryLevel.ERROR, "The BeforeEnvelope callback threw an exception.", throwable);
      }  
    if (paramHint == null) {
      this.transport.send(paramSentryEnvelope);
    } else {
      this.transport.send(paramSentryEnvelope, paramHint);
    } 
    SentryId sentryId = paramSentryEnvelope.getHeader().getEventId();
    return (sentryId != null) ? sentryId : SentryId.EMPTY_ID;
  }
  
  @NotNull
  public SentryId captureTransaction(@NotNull SentryTransaction paramSentryTransaction, @Nullable TraceContext paramTraceContext, @Nullable IScope paramIScope, @Nullable Hint paramHint, @Nullable ProfilingTraceData paramProfilingTraceData) {
    Objects.requireNonNull(paramSentryTransaction, "Transaction is required.");
    if (paramHint == null)
      paramHint = new Hint(); 
    if (shouldApplyScopeData((SentryBaseEvent)paramSentryTransaction, paramHint))
      addScopeAttachmentsToHint(paramIScope, paramHint); 
    this.options.getLogger().log(SentryLevel.DEBUG, "Capturing transaction: %s", new Object[] { paramSentryTransaction.getEventId() });
    SentryId sentryId = SentryId.EMPTY_ID;
    if (paramSentryTransaction.getEventId() != null)
      sentryId = paramSentryTransaction.getEventId(); 
    if (shouldApplyScopeData((SentryBaseEvent)paramSentryTransaction, paramHint)) {
      paramSentryTransaction = applyScope(paramSentryTransaction, paramIScope);
      if (paramSentryTransaction != null && paramIScope != null)
        paramSentryTransaction = processTransaction(paramSentryTransaction, paramHint, paramIScope.getEventProcessors()); 
      if (paramSentryTransaction == null)
        this.options.getLogger().log(SentryLevel.DEBUG, "Transaction was dropped by applyScope", new Object[0]); 
    } 
    if (paramSentryTransaction != null)
      paramSentryTransaction = processTransaction(paramSentryTransaction, paramHint, this.options.getEventProcessors()); 
    if (paramSentryTransaction == null) {
      this.options.getLogger().log(SentryLevel.DEBUG, "Transaction was dropped by Event processors.", new Object[0]);
      return SentryId.EMPTY_ID;
    } 
    int i = paramSentryTransaction.getSpans().size();
    paramSentryTransaction = executeBeforeSendTransaction(paramSentryTransaction, paramHint);
    byte b = (paramSentryTransaction == null) ? 0 : paramSentryTransaction.getSpans().size();
    if (paramSentryTransaction == null) {
      this.options.getLogger().log(SentryLevel.DEBUG, "Transaction was dropped by beforeSendTransaction.", new Object[0]);
      this.options.getClientReportRecorder().recordLostEvent(DiscardReason.BEFORE_SEND, DataCategory.Transaction);
      this.options.getClientReportRecorder().recordLostEvent(DiscardReason.BEFORE_SEND, DataCategory.Span, (i + 1));
      return SentryId.EMPTY_ID;
    } 
    if (b < i) {
      int j = i - b;
      this.options.getLogger().log(SentryLevel.DEBUG, "%d spans were dropped by beforeSendTransaction.", new Object[] { Integer.valueOf(j) });
      this.options.getClientReportRecorder().recordLostEvent(DiscardReason.BEFORE_SEND, DataCategory.Span, j);
    } 
    try {
      SentryEnvelope sentryEnvelope = buildEnvelope((SentryBaseEvent)paramSentryTransaction, filterForTransaction(getAttachments(paramHint)), null, paramTraceContext, paramProfilingTraceData);
      paramHint.clear();
      if (sentryEnvelope != null)
        sentryId = sendEnvelope(sentryEnvelope, paramHint); 
    } catch (IOException|SentryEnvelopeException iOException) {
      this.options.getLogger().log(SentryLevel.WARNING, iOException, "Capturing transaction %s failed.", new Object[] { sentryId });
      sentryId = SentryId.EMPTY_ID;
    } 
    return sentryId;
  }
  
  @Experimental
  @NotNull
  public SentryId captureCheckIn(@NotNull CheckIn paramCheckIn, @Nullable IScope paramIScope, @Nullable Hint paramHint) {
    if (paramHint == null)
      paramHint = new Hint(); 
    if (paramCheckIn.getEnvironment() == null)
      paramCheckIn.setEnvironment(this.options.getEnvironment()); 
    if (paramCheckIn.getRelease() == null)
      paramCheckIn.setRelease(this.options.getRelease()); 
    if (shouldApplyScopeData(paramCheckIn, paramHint))
      paramCheckIn = applyScope(paramCheckIn, paramIScope); 
    if (CheckInUtils.isIgnored(this.options.getIgnoredCheckIns(), paramCheckIn.getMonitorSlug())) {
      this.options.getLogger().log(SentryLevel.DEBUG, "Check-in was dropped as slug %s is ignored", new Object[] { paramCheckIn.getMonitorSlug() });
      return SentryId.EMPTY_ID;
    } 
    this.options.getLogger().log(SentryLevel.DEBUG, "Capturing check-in: %s", new Object[] { paramCheckIn.getCheckInId() });
    SentryId sentryId = paramCheckIn.getCheckInId();
    try {
      TraceContext traceContext = null;
      if (paramIScope != null) {
        ITransaction iTransaction = paramIScope.getTransaction();
        if (iTransaction != null) {
          traceContext = iTransaction.traceContext();
        } else {
          PropagationContext propagationContext = TracingUtils.maybeUpdateBaggage(paramIScope, this.options);
          traceContext = propagationContext.traceContext();
        } 
      } 
      SentryEnvelope sentryEnvelope = buildEnvelope(paramCheckIn, traceContext);
      paramHint.clear();
      sentryId = sendEnvelope(sentryEnvelope, paramHint);
    } catch (IOException iOException) {
      this.options.getLogger().log(SentryLevel.WARNING, iOException, "Capturing check-in %s failed.", new Object[] { sentryId });
      sentryId = SentryId.EMPTY_ID;
    } 
    return sentryId;
  }
  
  @Nullable
  private List<Attachment> filterForTransaction(@Nullable List<Attachment> paramList) {
    if (paramList == null)
      return null; 
    ArrayList<Attachment> arrayList = new ArrayList();
    for (Attachment attachment : paramList) {
      if (attachment.isAddToTransactions())
        arrayList.add(attachment); 
    } 
    return arrayList;
  }
  
  @Nullable
  private SentryEvent applyScope(@NotNull SentryEvent paramSentryEvent, @Nullable IScope paramIScope, @NotNull Hint paramHint) {
    if (paramIScope != null) {
      applyScope(paramSentryEvent, paramIScope);
      if (paramSentryEvent.getTransaction() == null)
        paramSentryEvent.setTransaction(paramIScope.getTransactionName()); 
      if (paramSentryEvent.getFingerprints() == null)
        paramSentryEvent.setFingerprints(paramIScope.getFingerprint()); 
      if (paramIScope.getLevel() != null)
        paramSentryEvent.setLevel(paramIScope.getLevel()); 
      ISpan iSpan = paramIScope.getSpan();
      if (paramSentryEvent.getContexts().getTrace() == null)
        if (iSpan == null) {
          paramSentryEvent.getContexts().setTrace(TransactionContext.fromPropagationContext(paramIScope.getPropagationContext()));
        } else {
          paramSentryEvent.getContexts().setTrace(iSpan.getSpanContext());
        }  
      paramSentryEvent = processEvent(paramSentryEvent, paramHint, paramIScope.getEventProcessors());
    } 
    return paramSentryEvent;
  }
  
  @NotNull
  private CheckIn applyScope(@NotNull CheckIn paramCheckIn, @Nullable IScope paramIScope) {
    if (paramIScope != null) {
      ISpan iSpan = paramIScope.getSpan();
      if (paramCheckIn.getContexts().getTrace() == null)
        if (iSpan == null) {
          paramCheckIn.getContexts().setTrace(TransactionContext.fromPropagationContext(paramIScope.getPropagationContext()));
        } else {
          paramCheckIn.getContexts().setTrace(iSpan.getSpanContext());
        }  
    } 
    return paramCheckIn;
  }
  
  @NotNull
  private <T extends SentryBaseEvent> T applyScope(@NotNull T paramT, @Nullable IScope paramIScope) {
    if (paramIScope != null) {
      if (paramT.getRequest() == null)
        paramT.setRequest(paramIScope.getRequest()); 
      if (paramT.getUser() == null)
        paramT.setUser(paramIScope.getUser()); 
      if (paramT.getTags() == null) {
        paramT.setTags(new HashMap<>(paramIScope.getTags()));
      } else {
        for (Map.Entry<String, String> entry : paramIScope.getTags().entrySet()) {
          if (!paramT.getTags().containsKey(entry.getKey()))
            paramT.getTags().put((String)entry.getKey(), (String)entry.getValue()); 
        } 
      } 
      if (paramT.getBreadcrumbs() == null) {
        paramT.setBreadcrumbs(new ArrayList<>(paramIScope.getBreadcrumbs()));
      } else {
        sortBreadcrumbsByDate((SentryBaseEvent)paramT, paramIScope.getBreadcrumbs());
      } 
      if (paramT.getExtras() == null) {
        paramT.setExtras(new HashMap<>(paramIScope.getExtras()));
      } else {
        for (Map.Entry<String, Object> entry : paramIScope.getExtras().entrySet()) {
          if (!paramT.getExtras().containsKey(entry.getKey()))
            paramT.getExtras().put((String)entry.getKey(), entry.getValue()); 
        } 
      } 
      Contexts contexts = paramT.getContexts();
      for (Map.Entry entry : (new Contexts(paramIScope.getContexts())).entrySet()) {
        if (!contexts.containsKey(entry.getKey()))
          contexts.put((String)entry.getKey(), entry.getValue()); 
      } 
    } 
    return paramT;
  }
  
  private void sortBreadcrumbsByDate(@NotNull SentryBaseEvent paramSentryBaseEvent, @NotNull Collection<Breadcrumb> paramCollection) {
    List<Breadcrumb> list = paramSentryBaseEvent.getBreadcrumbs();
    if (list != null && !paramCollection.isEmpty()) {
      list.addAll(paramCollection);
      Collections.sort(list, this.sortBreadcrumbsByDate);
    } 
  }
  
  @Nullable
  private SentryEvent executeBeforeSend(@NotNull SentryEvent paramSentryEvent, @NotNull Hint paramHint) {
    SentryOptions.BeforeSendCallback beforeSendCallback = this.options.getBeforeSend();
    if (beforeSendCallback != null)
      try {
        paramSentryEvent = beforeSendCallback.execute(paramSentryEvent, paramHint);
      } catch (Throwable throwable) {
        this.options.getLogger().log(SentryLevel.ERROR, "The BeforeSend callback threw an exception. It will be added as breadcrumb and continue.", throwable);
        paramSentryEvent = null;
      }  
    return paramSentryEvent;
  }
  
  @Nullable
  private SentryTransaction executeBeforeSendTransaction(@NotNull SentryTransaction paramSentryTransaction, @NotNull Hint paramHint) {
    SentryOptions.BeforeSendTransactionCallback beforeSendTransactionCallback = this.options.getBeforeSendTransaction();
    if (beforeSendTransactionCallback != null)
      try {
        paramSentryTransaction = beforeSendTransactionCallback.execute(paramSentryTransaction, paramHint);
      } catch (Throwable throwable) {
        this.options.getLogger().log(SentryLevel.ERROR, "The BeforeSendTransaction callback threw an exception. It will be added as breadcrumb and continue.", throwable);
        paramSentryTransaction = null;
      }  
    return paramSentryTransaction;
  }
  
  public void close() {
    close(false);
  }
  
  public void close(boolean paramBoolean) {
    this.options.getLogger().log(SentryLevel.INFO, "Closing SentryClient.", new Object[0]);
    try {
      this.metricsAggregator.close();
    } catch (IOException iOException) {
      this.options.getLogger().log(SentryLevel.WARNING, "Failed to close the metrics aggregator.", iOException);
    } 
    try {
      flush(paramBoolean ? 0L : this.options.getShutdownTimeoutMillis());
      this.transport.close(paramBoolean);
    } catch (IOException iOException) {
      this.options.getLogger().log(SentryLevel.WARNING, "Failed to close the connection to the Sentry Server.", iOException);
    } 
    for (EventProcessor eventProcessor : this.options.getEventProcessors()) {
      if (eventProcessor instanceof Closeable)
        try {
          ((Closeable)eventProcessor).close();
        } catch (IOException iOException) {
          this.options.getLogger().log(SentryLevel.WARNING, "Failed to close the event processor {}.", new Object[] { eventProcessor, iOException });
        }  
    } 
    this.enabled = false;
  }
  
  public void flush(long paramLong) {
    this.transport.flush(paramLong);
  }
  
  @Nullable
  public RateLimiter getRateLimiter() {
    return this.transport.getRateLimiter();
  }
  
  public boolean isHealthy() {
    return this.transport.isHealthy();
  }
  
  private boolean sample() {
    if (this.options.getSampleRate() != null && this.random != null) {
      double d = this.options.getSampleRate().doubleValue();
      return (d >= this.random.nextDouble());
    } 
    return true;
  }
  
  @NotNull
  public IMetricsAggregator getMetricsAggregator() {
    return this.metricsAggregator;
  }
  
  @NotNull
  public SentryId captureMetrics(@NotNull EncodedMetrics paramEncodedMetrics) {
    SentryEnvelopeItem sentryEnvelopeItem = SentryEnvelopeItem.fromMetrics(paramEncodedMetrics);
    SentryEnvelopeHeader sentryEnvelopeHeader = new SentryEnvelopeHeader(new SentryId(), this.options.getSdkVersion(), null);
    SentryEnvelope sentryEnvelope = new SentryEnvelope(sentryEnvelopeHeader, Collections.singleton(sentryEnvelopeItem));
    SentryId sentryId = captureEnvelope(sentryEnvelope);
    return (sentryId != null) ? sentryId : SentryId.EMPTY_ID;
  }
  
  private static final class SortBreadcrumbsByDate implements Comparator<Breadcrumb> {
    private SortBreadcrumbsByDate() {}
    
    public int compare(@NotNull Breadcrumb param1Breadcrumb1, @NotNull Breadcrumb param1Breadcrumb2) {
      return param1Breadcrumb1.getTimestamp().compareTo(param1Breadcrumb2.getTimestamp());
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */