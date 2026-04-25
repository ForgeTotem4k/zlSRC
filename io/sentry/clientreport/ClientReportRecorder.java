package io.sentry.clientreport;

import io.sentry.DataCategory;
import io.sentry.DateUtils;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEnvelopeItem;
import io.sentry.SentryItemType;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.protocol.SentryTransaction;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class ClientReportRecorder implements IClientReportRecorder {
  @NotNull
  private final IClientReportStorage storage;
  
  @NotNull
  private final SentryOptions options;
  
  public ClientReportRecorder(@NotNull SentryOptions paramSentryOptions) {
    this.options = paramSentryOptions;
    this.storage = new AtomicClientReportStorage();
  }
  
  @NotNull
  public SentryEnvelope attachReportToEnvelope(@NotNull SentryEnvelope paramSentryEnvelope) {
    ClientReport clientReport = resetCountsAndGenerateClientReport();
    if (clientReport == null)
      return paramSentryEnvelope; 
    try {
      this.options.getLogger().log(SentryLevel.DEBUG, "Attaching client report to envelope.", new Object[0]);
      ArrayList<SentryEnvelopeItem> arrayList = new ArrayList();
      for (SentryEnvelopeItem sentryEnvelopeItem : paramSentryEnvelope.getItems())
        arrayList.add(sentryEnvelopeItem); 
      arrayList.add(SentryEnvelopeItem.fromClientReport(this.options.getSerializer(), clientReport));
      return new SentryEnvelope(paramSentryEnvelope.getHeader(), arrayList);
    } catch (Throwable throwable) {
      this.options.getLogger().log(SentryLevel.ERROR, throwable, "Unable to attach client report to envelope.", new Object[0]);
      return paramSentryEnvelope;
    } 
  }
  
  public void recordLostEnvelope(@NotNull DiscardReason paramDiscardReason, @Nullable SentryEnvelope paramSentryEnvelope) {
    if (paramSentryEnvelope == null)
      return; 
    try {
      for (SentryEnvelopeItem sentryEnvelopeItem : paramSentryEnvelope.getItems())
        recordLostEnvelopeItem(paramDiscardReason, sentryEnvelopeItem); 
    } catch (Throwable throwable) {
      this.options.getLogger().log(SentryLevel.ERROR, throwable, "Unable to record lost envelope.", new Object[0]);
    } 
  }
  
  public void recordLostEnvelopeItem(@NotNull DiscardReason paramDiscardReason, @Nullable SentryEnvelopeItem paramSentryEnvelopeItem) {
    if (paramSentryEnvelopeItem == null)
      return; 
    try {
      SentryItemType sentryItemType = paramSentryEnvelopeItem.getHeader().getType();
      if (SentryItemType.ClientReport.equals(sentryItemType)) {
        try {
          ClientReport clientReport = paramSentryEnvelopeItem.getClientReport(this.options.getSerializer());
          restoreCountsFromClientReport(clientReport);
        } catch (Exception exception) {
          this.options.getLogger().log(SentryLevel.ERROR, "Unable to restore counts from previous client report.", new Object[0]);
        } 
      } else {
        DataCategory dataCategory = categoryFromItemType(sentryItemType);
        if (dataCategory.equals(DataCategory.Transaction)) {
          SentryTransaction sentryTransaction = paramSentryEnvelopeItem.getTransaction(this.options.getSerializer());
          if (sentryTransaction != null) {
            List list = sentryTransaction.getSpans();
            recordLostEventInternal(paramDiscardReason.getReason(), DataCategory.Span.getCategory(), Long.valueOf(list.size() + 1L));
          } 
        } 
        recordLostEventInternal(paramDiscardReason.getReason(), dataCategory.getCategory(), Long.valueOf(1L));
      } 
    } catch (Throwable throwable) {
      this.options.getLogger().log(SentryLevel.ERROR, throwable, "Unable to record lost envelope item.", new Object[0]);
    } 
  }
  
  public void recordLostEvent(@NotNull DiscardReason paramDiscardReason, @NotNull DataCategory paramDataCategory) {
    recordLostEvent(paramDiscardReason, paramDataCategory, 1L);
  }
  
  public void recordLostEvent(@NotNull DiscardReason paramDiscardReason, @NotNull DataCategory paramDataCategory, long paramLong) {
    try {
      recordLostEventInternal(paramDiscardReason.getReason(), paramDataCategory.getCategory(), Long.valueOf(paramLong));
    } catch (Throwable throwable) {
      this.options.getLogger().log(SentryLevel.ERROR, throwable, "Unable to record lost event.", new Object[0]);
    } 
  }
  
  private void recordLostEventInternal(@NotNull String paramString1, @NotNull String paramString2, @NotNull Long paramLong) {
    ClientReportKey clientReportKey = new ClientReportKey(paramString1, paramString2);
    this.storage.addCount(clientReportKey, paramLong);
  }
  
  @Nullable
  ClientReport resetCountsAndGenerateClientReport() {
    Date date = DateUtils.getCurrentDateTime();
    List<DiscardedEvent> list = this.storage.resetCountsAndGet();
    return list.isEmpty() ? null : new ClientReport(date, list);
  }
  
  private void restoreCountsFromClientReport(@Nullable ClientReport paramClientReport) {
    if (paramClientReport == null)
      return; 
    for (DiscardedEvent discardedEvent : paramClientReport.getDiscardedEvents())
      recordLostEventInternal(discardedEvent.getReason(), discardedEvent.getCategory(), discardedEvent.getQuantity()); 
  }
  
  private DataCategory categoryFromItemType(SentryItemType paramSentryItemType) {
    return SentryItemType.Event.equals(paramSentryItemType) ? DataCategory.Error : (SentryItemType.Session.equals(paramSentryItemType) ? DataCategory.Session : (SentryItemType.Transaction.equals(paramSentryItemType) ? DataCategory.Transaction : (SentryItemType.UserFeedback.equals(paramSentryItemType) ? DataCategory.UserReport : (SentryItemType.Profile.equals(paramSentryItemType) ? DataCategory.Profile : (SentryItemType.Statsd.equals(paramSentryItemType) ? DataCategory.MetricBucket : (SentryItemType.Attachment.equals(paramSentryItemType) ? DataCategory.Attachment : (SentryItemType.CheckIn.equals(paramSentryItemType) ? DataCategory.Monitor : DataCategory.Default)))))));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\clientreport\ClientReportRecorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */