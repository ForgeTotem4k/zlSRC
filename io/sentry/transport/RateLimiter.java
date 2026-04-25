package io.sentry.transport;

import io.sentry.DataCategory;
import io.sentry.Hint;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEnvelopeItem;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.clientreport.DiscardReason;
import io.sentry.hints.Retryable;
import io.sentry.hints.SubmissionResult;
import io.sentry.util.CollectionUtils;
import io.sentry.util.HintUtils;
import io.sentry.util.StringUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RateLimiter {
  private static final int HTTP_RETRY_AFTER_DEFAULT_DELAY_MILLIS = 60000;
  
  @NotNull
  private final ICurrentDateProvider currentDateProvider;
  
  @NotNull
  private final SentryOptions options;
  
  @NotNull
  private final Map<DataCategory, Date> sentryRetryAfterLimit = new ConcurrentHashMap<>();
  
  public RateLimiter(@NotNull ICurrentDateProvider paramICurrentDateProvider, @NotNull SentryOptions paramSentryOptions) {
    this.currentDateProvider = paramICurrentDateProvider;
    this.options = paramSentryOptions;
  }
  
  public RateLimiter(@NotNull SentryOptions paramSentryOptions) {
    this(CurrentDateProvider.getInstance(), paramSentryOptions);
  }
  
  @Nullable
  public SentryEnvelope filter(@NotNull SentryEnvelope paramSentryEnvelope, @NotNull Hint paramHint) {
    ArrayList<SentryEnvelopeItem> arrayList = null;
    for (SentryEnvelopeItem sentryEnvelopeItem : paramSentryEnvelope.getItems()) {
      if (isRetryAfter(sentryEnvelopeItem.getHeader().getType().getItemType())) {
        if (arrayList == null)
          arrayList = new ArrayList(); 
        arrayList.add(sentryEnvelopeItem);
        this.options.getClientReportRecorder().recordLostEnvelopeItem(DiscardReason.RATELIMIT_BACKOFF, sentryEnvelopeItem);
      } 
    } 
    if (arrayList != null) {
      this.options.getLogger().log(SentryLevel.INFO, "%d items will be dropped due rate limiting.", new Object[] { Integer.valueOf(arrayList.size()) });
      ArrayList<SentryEnvelopeItem> arrayList1 = new ArrayList();
      for (SentryEnvelopeItem sentryEnvelopeItem : paramSentryEnvelope.getItems()) {
        if (!arrayList.contains(sentryEnvelopeItem))
          arrayList1.add(sentryEnvelopeItem); 
      } 
      if (arrayList1.isEmpty()) {
        this.options.getLogger().log(SentryLevel.INFO, "Envelope discarded due all items rate limited.", new Object[0]);
        markHintWhenSendingFailed(paramHint, false);
        return null;
      } 
      return new SentryEnvelope(paramSentryEnvelope.getHeader(), arrayList1);
    } 
    return paramSentryEnvelope;
  }
  
  public boolean isActiveForCategory(@NotNull DataCategory paramDataCategory) {
    Date date1 = new Date(this.currentDateProvider.getCurrentTimeMillis());
    Date date2 = this.sentryRetryAfterLimit.get(DataCategory.All);
    if (date2 != null && !date1.after(date2))
      return true; 
    if (DataCategory.Unknown.equals(paramDataCategory))
      return false; 
    Date date3 = this.sentryRetryAfterLimit.get(paramDataCategory);
    return (date3 != null) ? (!date1.after(date3)) : false;
  }
  
  public boolean isAnyRateLimitActive() {
    Date date = new Date(this.currentDateProvider.getCurrentTimeMillis());
    for (DataCategory dataCategory : this.sentryRetryAfterLimit.keySet()) {
      Date date1 = this.sentryRetryAfterLimit.get(dataCategory);
      if (date1 != null && !date.after(date1))
        return true; 
    } 
    return false;
  }
  
  private static void markHintWhenSendingFailed(@NotNull Hint paramHint, boolean paramBoolean) {
    HintUtils.runIfHasType(paramHint, SubmissionResult.class, paramSubmissionResult -> paramSubmissionResult.setResult(false));
    HintUtils.runIfHasType(paramHint, Retryable.class, paramRetryable -> paramRetryable.setRetry(paramBoolean));
  }
  
  private boolean isRetryAfter(@NotNull String paramString) {
    DataCategory dataCategory = getCategoryFromItemType(paramString);
    return isActiveForCategory(dataCategory);
  }
  
  @NotNull
  private DataCategory getCategoryFromItemType(@NotNull String paramString) {
    switch (paramString) {
      case "event":
        return DataCategory.Error;
      case "session":
        return DataCategory.Session;
      case "attachment":
        return DataCategory.Attachment;
      case "profile":
        return DataCategory.Profile;
      case "statsd":
        return DataCategory.MetricBucket;
      case "transaction":
        return DataCategory.Transaction;
      case "check_in":
        return DataCategory.Monitor;
    } 
    return DataCategory.Unknown;
  }
  
  public void updateRetryAfterLimits(@Nullable String paramString1, @Nullable String paramString2, int paramInt) {
    if (paramString1 != null) {
      for (String str1 : paramString1.split(",", -1)) {
        str1 = str1.replace(" ", "");
        String[] arrayOfString = str1.split(":", -1);
        String str2 = (arrayOfString.length > 4) ? arrayOfString[4] : null;
        if (arrayOfString.length > 0) {
          String str = arrayOfString[0];
          long l = parseRetryAfterOrDefault(str);
          if (arrayOfString.length > 1) {
            String str3 = arrayOfString[1];
            Date date = new Date(this.currentDateProvider.getCurrentTimeMillis() + l);
            if (str3 != null && !str3.isEmpty()) {
              String[] arrayOfString1 = str3.split(";", -1);
              for (String str4 : arrayOfString1) {
                DataCategory dataCategory = DataCategory.Unknown;
                try {
                  String str5 = StringUtils.camelCase(str4);
                  if (str5 != null) {
                    dataCategory = DataCategory.valueOf(str5);
                  } else {
                    this.options.getLogger().log(SentryLevel.ERROR, "Couldn't capitalize: %s", new Object[] { str4 });
                  } 
                } catch (IllegalArgumentException illegalArgumentException) {
                  this.options.getLogger().log(SentryLevel.INFO, illegalArgumentException, "Unknown category: %s", new Object[] { str4 });
                } 
                if (DataCategory.Unknown.equals(dataCategory))
                  continue; 
                if (DataCategory.MetricBucket.equals(dataCategory) && str2 != null && !str2.equals("")) {
                  String[] arrayOfString2 = str2.split(";", -1);
                  if (arrayOfString2.length > 0 && !CollectionUtils.contains((Object[])arrayOfString2, "custom"))
                    continue; 
                } 
                applyRetryAfterOnlyIfLonger(dataCategory, date);
                continue;
              } 
            } else {
              applyRetryAfterOnlyIfLonger(DataCategory.All, date);
            } 
          } 
        } 
      } 
    } else if (paramInt == 429) {
      long l = parseRetryAfterOrDefault(paramString2);
      Date date = new Date(this.currentDateProvider.getCurrentTimeMillis() + l);
      applyRetryAfterOnlyIfLonger(DataCategory.All, date);
    } 
  }
  
  private void applyRetryAfterOnlyIfLonger(@NotNull DataCategory paramDataCategory, @NotNull Date paramDate) {
    Date date = this.sentryRetryAfterLimit.get(paramDataCategory);
    if (date == null || paramDate.after(date))
      this.sentryRetryAfterLimit.put(paramDataCategory, paramDate); 
  }
  
  private long parseRetryAfterOrDefault(@Nullable String paramString) {
    long l = 60000L;
    if (paramString != null)
      try {
        l = (long)(Double.parseDouble(paramString) * 1000.0D);
      } catch (NumberFormatException numberFormatException) {} 
    return l;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\transport\RateLimiter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */