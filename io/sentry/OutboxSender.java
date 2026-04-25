package io.sentry;

import io.sentry.hints.Flushable;
import io.sentry.hints.Resettable;
import io.sentry.hints.Retryable;
import io.sentry.hints.SubmissionResult;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.util.CollectionUtils;
import io.sentry.util.HintUtils;
import io.sentry.util.LogUtils;
import io.sentry.util.Objects;
import io.sentry.util.SampleRateUtils;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class OutboxSender extends DirectoryProcessor implements IEnvelopeSender {
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  
  @NotNull
  private final IScopes scopes;
  
  @NotNull
  private final IEnvelopeReader envelopeReader;
  
  @NotNull
  private final ISerializer serializer;
  
  @NotNull
  private final ILogger logger;
  
  public OutboxSender(@NotNull IScopes paramIScopes, @NotNull IEnvelopeReader paramIEnvelopeReader, @NotNull ISerializer paramISerializer, @NotNull ILogger paramILogger, long paramLong, int paramInt) {
    super(paramIScopes, paramILogger, paramLong, paramInt);
    this.scopes = (IScopes)Objects.requireNonNull(paramIScopes, "Scopes are required.");
    this.envelopeReader = (IEnvelopeReader)Objects.requireNonNull(paramIEnvelopeReader, "Envelope reader is required.");
    this.serializer = (ISerializer)Objects.requireNonNull(paramISerializer, "Serializer is required.");
    this.logger = (ILogger)Objects.requireNonNull(paramILogger, "Logger is required.");
  }
  
  protected void processFile(@NotNull File paramFile, @NotNull Hint paramHint) {
    Objects.requireNonNull(paramFile, "File is required.");
    if (!isRelevantFileName(paramFile.getName())) {
      this.logger.log(SentryLevel.DEBUG, "File '%s' should be ignored.", new Object[] { paramFile.getAbsolutePath() });
      return;
    } 
    try {
      BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(paramFile));
      try {
        SentryEnvelope sentryEnvelope = this.envelopeReader.read(bufferedInputStream);
        if (sentryEnvelope == null) {
          this.logger.log(SentryLevel.ERROR, "Stream from path %s resulted in a null envelope.", new Object[] { paramFile.getAbsolutePath() });
        } else {
          processEnvelope(sentryEnvelope, paramHint);
          this.logger.log(SentryLevel.DEBUG, "File '%s' is done.", new Object[] { paramFile.getAbsolutePath() });
        } 
        bufferedInputStream.close();
      } catch (Throwable throwable) {
        try {
          bufferedInputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (IOException iOException) {
      this.logger.log(SentryLevel.ERROR, "Error processing envelope.", iOException);
    } finally {
      HintUtils.runIfHasTypeLogIfNot(paramHint, Retryable.class, this.logger, paramRetryable -> {
            if (!paramRetryable.isRetry())
              try {
                if (!paramFile.delete())
                  this.logger.log(SentryLevel.ERROR, "Failed to delete: %s", new Object[] { paramFile.getAbsolutePath() }); 
              } catch (RuntimeException runtimeException) {
                this.logger.log(SentryLevel.ERROR, runtimeException, "Failed to delete: %s", new Object[] { paramFile.getAbsolutePath() });
              }  
          });
    } 
  }
  
  protected boolean isRelevantFileName(@Nullable String paramString) {
    return (paramString != null && !paramString.startsWith("session") && !paramString.startsWith("previous_session") && !paramString.startsWith("startup_crash"));
  }
  
  public void processEnvelopeFile(@NotNull String paramString, @NotNull Hint paramHint) {
    Objects.requireNonNull(paramString, "Path is required.");
    processFile(new File(paramString), paramHint);
  }
  
  private void processEnvelope(@NotNull SentryEnvelope paramSentryEnvelope, @NotNull Hint paramHint) throws IOException {
    this.logger.log(SentryLevel.DEBUG, "Processing Envelope with %d item(s)", new Object[] { Integer.valueOf(CollectionUtils.size(paramSentryEnvelope.getItems())) });
    byte b = 0;
    for (SentryEnvelopeItem sentryEnvelopeItem : paramSentryEnvelope.getItems()) {
      b++;
      if (sentryEnvelopeItem.getHeader() == null) {
        this.logger.log(SentryLevel.ERROR, "Item %d has no header", new Object[] { Integer.valueOf(b) });
        continue;
      } 
      if (SentryItemType.Event.equals(sentryEnvelopeItem.getHeader().getType())) {
        try {
          BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(sentryEnvelopeItem.getData()), UTF_8));
          try {
            SentryEvent sentryEvent = this.serializer.<SentryEvent>deserialize(bufferedReader, SentryEvent.class);
            if (sentryEvent == null) {
              logEnvelopeItemNull(sentryEnvelopeItem, b);
            } else {
              if (sentryEvent.getSdk() != null)
                HintUtils.setIsFromHybridSdk(paramHint, sentryEvent.getSdk().getName()); 
              if (paramSentryEnvelope.getHeader().getEventId() != null && !paramSentryEnvelope.getHeader().getEventId().equals(sentryEvent.getEventId())) {
                logUnexpectedEventId(paramSentryEnvelope, sentryEvent.getEventId(), b);
                bufferedReader.close();
                continue;
              } 
              this.scopes.captureEvent(sentryEvent, paramHint);
              logItemCaptured(b);
              if (!waitFlush(paramHint)) {
                logTimeout(sentryEvent.getEventId());
                bufferedReader.close();
                break;
              } 
            } 
            bufferedReader.close();
          } catch (Throwable throwable) {
            try {
              bufferedReader.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            } 
            throw throwable;
          } 
        } catch (Throwable throwable) {
          this.logger.log(SentryLevel.ERROR, "Item failed to process.", throwable);
        } 
      } else if (SentryItemType.Transaction.equals(sentryEnvelopeItem.getHeader().getType())) {
        try {
          BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(sentryEnvelopeItem.getData()), UTF_8));
          try {
            SentryTransaction sentryTransaction = this.serializer.<SentryTransaction>deserialize(bufferedReader, SentryTransaction.class);
            if (sentryTransaction == null) {
              logEnvelopeItemNull(sentryEnvelopeItem, b);
            } else {
              if (paramSentryEnvelope.getHeader().getEventId() != null && !paramSentryEnvelope.getHeader().getEventId().equals(sentryTransaction.getEventId())) {
                logUnexpectedEventId(paramSentryEnvelope, sentryTransaction.getEventId(), b);
                bufferedReader.close();
                continue;
              } 
              TraceContext traceContext = paramSentryEnvelope.getHeader().getTraceContext();
              if (sentryTransaction.getContexts().getTrace() != null)
                sentryTransaction.getContexts().getTrace().setSamplingDecision(extractSamplingDecision(traceContext)); 
              this.scopes.captureTransaction(sentryTransaction, traceContext, paramHint);
              logItemCaptured(b);
              if (!waitFlush(paramHint)) {
                logTimeout(sentryTransaction.getEventId());
                bufferedReader.close();
                break;
              } 
            } 
            bufferedReader.close();
          } catch (Throwable throwable) {
            try {
              bufferedReader.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            } 
            throw throwable;
          } 
        } catch (Throwable throwable) {
          this.logger.log(SentryLevel.ERROR, "Item failed to process.", throwable);
        } 
      } else {
        SentryEnvelope sentryEnvelope = new SentryEnvelope(paramSentryEnvelope.getHeader().getEventId(), paramSentryEnvelope.getHeader().getSdkVersion(), sentryEnvelopeItem);
        this.scopes.captureEnvelope(sentryEnvelope, paramHint);
        this.logger.log(SentryLevel.DEBUG, "%s item %d is being captured.", new Object[] { sentryEnvelopeItem.getHeader().getType().getItemType(), Integer.valueOf(b) });
        if (!waitFlush(paramHint)) {
          this.logger.log(SentryLevel.WARNING, "Timed out waiting for item type submission: %s", new Object[] { sentryEnvelopeItem.getHeader().getType().getItemType() });
          break;
        } 
      } 
      Object object = HintUtils.getSentrySdkHint(paramHint);
      if (object instanceof SubmissionResult && !((SubmissionResult)object).isSuccess()) {
        this.logger.log(SentryLevel.WARNING, "Envelope had a failed capture at item %d. No more items will be sent.", new Object[] { Integer.valueOf(b) });
        break;
      } 
      HintUtils.runIfHasType(paramHint, Resettable.class, paramResettable -> paramResettable.reset());
    } 
  }
  
  @NotNull
  private TracesSamplingDecision extractSamplingDecision(@Nullable TraceContext paramTraceContext) {
    if (paramTraceContext != null) {
      String str = paramTraceContext.getSampleRate();
      if (str != null)
        try {
          Double double_ = Double.valueOf(Double.parseDouble(str));
          if (!SampleRateUtils.isValidTracesSampleRate(double_, false)) {
            this.logger.log(SentryLevel.ERROR, "Invalid sample rate parsed from TraceContext: %s", new Object[] { str });
          } else {
            return new TracesSamplingDecision(Boolean.valueOf(true), double_);
          } 
        } catch (Exception exception) {
          this.logger.log(SentryLevel.ERROR, "Unable to parse sample rate from TraceContext: %s", new Object[] { str });
        }  
    } 
    return new TracesSamplingDecision(Boolean.valueOf(true));
  }
  
  private void logEnvelopeItemNull(@NotNull SentryEnvelopeItem paramSentryEnvelopeItem, int paramInt) {
    this.logger.log(SentryLevel.ERROR, "Item %d of type %s returned null by the parser.", new Object[] { Integer.valueOf(paramInt), paramSentryEnvelopeItem.getHeader().getType() });
  }
  
  private void logUnexpectedEventId(@NotNull SentryEnvelope paramSentryEnvelope, @Nullable SentryId paramSentryId, int paramInt) {
    this.logger.log(SentryLevel.ERROR, "Item %d of has a different event id (%s) to the envelope header (%s)", new Object[] { Integer.valueOf(paramInt), paramSentryEnvelope.getHeader().getEventId(), paramSentryId });
  }
  
  private void logItemCaptured(int paramInt) {
    this.logger.log(SentryLevel.DEBUG, "Item %d is being captured.", new Object[] { Integer.valueOf(paramInt) });
  }
  
  private void logTimeout(@Nullable SentryId paramSentryId) {
    this.logger.log(SentryLevel.WARNING, "Timed out waiting for event id submission: %s", new Object[] { paramSentryId });
  }
  
  private boolean waitFlush(@NotNull Hint paramHint) {
    Object object = HintUtils.getSentrySdkHint(paramHint);
    if (object instanceof Flushable)
      return ((Flushable)object).waitFlush(); 
    LogUtils.logNotInstanceOf(Flushable.class, object, this.logger);
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\OutboxSender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */