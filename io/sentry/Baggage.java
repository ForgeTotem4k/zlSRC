package io.sentry;

import io.sentry.protocol.SentryId;
import io.sentry.protocol.TransactionNameSource;
import io.sentry.util.SampleRateUtils;
import io.sentry.util.StringUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Experimental
public final class Baggage {
  @NotNull
  static final String CHARSET = "UTF-8";
  
  @NotNull
  static final Integer MAX_BAGGAGE_STRING_LENGTH = Integer.valueOf(8192);
  
  @NotNull
  static final Integer MAX_BAGGAGE_LIST_MEMBER_COUNT = Integer.valueOf(64);
  
  @NotNull
  static final String SENTRY_BAGGAGE_PREFIX = "sentry-";
  
  @NotNull
  final Map<String, String> keyValues;
  
  @Nullable
  final String thirdPartyHeader;
  
  private boolean mutable;
  
  @NotNull
  final ILogger logger;
  
  @NotNull
  public static Baggage fromHeader(@Nullable String paramString) {
    return fromHeader(paramString, false, ScopesAdapter.getInstance().getOptions().getLogger());
  }
  
  @NotNull
  public static Baggage fromHeader(@Nullable List<String> paramList) {
    return fromHeader(paramList, false, ScopesAdapter.getInstance().getOptions().getLogger());
  }
  
  @Internal
  @NotNull
  public static Baggage fromHeader(String paramString, @NotNull ILogger paramILogger) {
    return fromHeader(paramString, false, paramILogger);
  }
  
  @Internal
  @NotNull
  public static Baggage fromHeader(@Nullable List<String> paramList, @NotNull ILogger paramILogger) {
    return fromHeader(paramList, false, paramILogger);
  }
  
  @Internal
  @NotNull
  public static Baggage fromHeader(@Nullable List<String> paramList, boolean paramBoolean, @NotNull ILogger paramILogger) {
    return (paramList != null) ? fromHeader(StringUtils.join(",", paramList), paramBoolean, paramILogger) : fromHeader((String)null, paramBoolean, paramILogger);
  }
  
  @Internal
  @NotNull
  public static Baggage fromHeader(@Nullable String paramString, boolean paramBoolean, @NotNull ILogger paramILogger) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    ArrayList<String> arrayList = new ArrayList();
    boolean bool = true;
    if (paramString != null)
      try {
        String[] arrayOfString = paramString.split(",", -1);
        for (String str1 : arrayOfString) {
          if (str1.trim().startsWith("sentry-")) {
            try {
              int i = str1.indexOf("=");
              String str2 = str1.substring(0, i).trim();
              String str3 = decode(str2);
              String str4 = str1.substring(i + 1).trim();
              String str5 = decode(str4);
              hashMap.put(str3, str5);
              bool = false;
            } catch (Throwable throwable) {
              paramILogger.log(SentryLevel.ERROR, throwable, "Unable to decode baggage key value pair %s", new Object[] { str1 });
            } 
          } else if (paramBoolean) {
            arrayList.add(str1.trim());
          } 
        } 
      } catch (Throwable throwable) {
        paramILogger.log(SentryLevel.ERROR, throwable, "Unable to decode baggage header %s", new Object[] { paramString });
      }  
    String str = arrayList.isEmpty() ? null : StringUtils.join(",", arrayList);
    return new Baggage((Map)hashMap, str, bool, paramILogger);
  }
  
  @Internal
  @NotNull
  public static Baggage fromEvent(@NotNull SentryEvent paramSentryEvent, @NotNull SentryOptions paramSentryOptions) {
    Baggage baggage = new Baggage(paramSentryOptions.getLogger());
    SpanContext spanContext = paramSentryEvent.getContexts().getTrace();
    baggage.setTraceId((spanContext != null) ? spanContext.getTraceId().toString() : null);
    baggage.setPublicKey((new Dsn(paramSentryOptions.getDsn())).getPublicKey());
    baggage.setRelease(paramSentryEvent.getRelease());
    baggage.setEnvironment(paramSentryEvent.getEnvironment());
    baggage.setTransaction(paramSentryEvent.getTransaction());
    baggage.setSampleRate(null);
    baggage.setSampled(null);
    baggage.freeze();
    return baggage;
  }
  
  @Internal
  public Baggage(@NotNull ILogger paramILogger) {
    this(new HashMap<>(), null, true, paramILogger);
  }
  
  @Internal
  public Baggage(@NotNull Baggage paramBaggage) {
    this(paramBaggage.keyValues, paramBaggage.thirdPartyHeader, paramBaggage.mutable, paramBaggage.logger);
  }
  
  @Internal
  public Baggage(@NotNull Map<String, String> paramMap, @Nullable String paramString, boolean paramBoolean, @NotNull ILogger paramILogger) {
    this.keyValues = paramMap;
    this.logger = paramILogger;
    this.mutable = paramBoolean;
    this.thirdPartyHeader = paramString;
  }
  
  @Internal
  public void freeze() {
    this.mutable = false;
  }
  
  @Internal
  public boolean isMutable() {
    return this.mutable;
  }
  
  @Nullable
  public String getThirdPartyHeader() {
    return this.thirdPartyHeader;
  }
  
  @NotNull
  public String toHeaderString(@Nullable String paramString) {
    StringBuilder stringBuilder = new StringBuilder();
    String str = "";
    int i = 0;
    if (paramString != null && !paramString.isEmpty()) {
      stringBuilder.append(paramString);
      i = StringUtils.countOf(paramString, ',') + 1;
      str = ",";
    } 
    TreeSet treeSet = new TreeSet(this.keyValues.keySet());
    for (String str1 : treeSet) {
      String str2 = this.keyValues.get(str1);
      if (str2 != null) {
        if (i >= MAX_BAGGAGE_LIST_MEMBER_COUNT.intValue()) {
          this.logger.log(SentryLevel.ERROR, "Not adding baggage value %s as the total number of list members would exceed the maximum of %s.", new Object[] { str1, MAX_BAGGAGE_LIST_MEMBER_COUNT });
          continue;
        } 
        try {
          String str3 = encode(str1);
          String str4 = encode(str2);
          String str5 = str + str3 + "=" + str4;
          int j = str5.length();
          int k = stringBuilder.length() + j;
          if (k > MAX_BAGGAGE_STRING_LENGTH.intValue()) {
            this.logger.log(SentryLevel.ERROR, "Not adding baggage value %s as the total header value length would exceed the maximum of %s.", new Object[] { str1, MAX_BAGGAGE_STRING_LENGTH });
            continue;
          } 
          i++;
          stringBuilder.append(str5);
          str = ",";
        } catch (Throwable throwable) {
          this.logger.log(SentryLevel.ERROR, throwable, "Unable to encode baggage key value pair (key=%s,value=%s).", new Object[] { str1, str2 });
        } 
      } 
    } 
    return stringBuilder.toString();
  }
  
  private String encode(@NotNull String paramString) throws UnsupportedEncodingException {
    return URLEncoder.encode(paramString, "UTF-8").replaceAll("\\+", "%20");
  }
  
  private static String decode(@NotNull String paramString) throws UnsupportedEncodingException {
    return URLDecoder.decode(paramString, "UTF-8");
  }
  
  @Internal
  @Nullable
  public String get(@Nullable String paramString) {
    return (paramString == null) ? null : this.keyValues.get(paramString);
  }
  
  @Internal
  @Nullable
  public String getTraceId() {
    return get("sentry-trace_id");
  }
  
  @Internal
  public void setTraceId(@Nullable String paramString) {
    set("sentry-trace_id", paramString);
  }
  
  @Internal
  @Nullable
  public String getPublicKey() {
    return get("sentry-public_key");
  }
  
  @Internal
  public void setPublicKey(@Nullable String paramString) {
    set("sentry-public_key", paramString);
  }
  
  @Internal
  @Nullable
  public String getEnvironment() {
    return get("sentry-environment");
  }
  
  @Internal
  public void setEnvironment(@Nullable String paramString) {
    set("sentry-environment", paramString);
  }
  
  @Internal
  @Nullable
  public String getRelease() {
    return get("sentry-release");
  }
  
  @Internal
  public void setRelease(@Nullable String paramString) {
    set("sentry-release", paramString);
  }
  
  @Internal
  @Nullable
  public String getUserId() {
    return get("sentry-user_id");
  }
  
  @Internal
  public void setUserId(@Nullable String paramString) {
    set("sentry-user_id", paramString);
  }
  
  @Internal
  @Nullable
  public String getTransaction() {
    return get("sentry-transaction");
  }
  
  @Internal
  public void setTransaction(@Nullable String paramString) {
    set("sentry-transaction", paramString);
  }
  
  @Internal
  @Nullable
  public String getSampleRate() {
    return get("sentry-sample_rate");
  }
  
  @Internal
  @Nullable
  public String getSampled() {
    return get("sentry-sampled");
  }
  
  @Internal
  public void setSampleRate(@Nullable String paramString) {
    set("sentry-sample_rate", paramString);
  }
  
  @Internal
  public void setSampled(@Nullable String paramString) {
    set("sentry-sampled", paramString);
  }
  
  @Internal
  public void set(@NotNull String paramString1, @Nullable String paramString2) {
    if (this.mutable)
      this.keyValues.put(paramString1, paramString2); 
  }
  
  @Internal
  @NotNull
  public Map<String, Object> getUnknown() {
    ConcurrentHashMap<Object, Object> concurrentHashMap = new ConcurrentHashMap<>();
    for (Map.Entry<String, String> entry : this.keyValues.entrySet()) {
      String str1 = (String)entry.getKey();
      String str2 = (String)entry.getValue();
      if (!DSCKeys.ALL.contains(str1) && str2 != null) {
        String str = str1.replaceFirst("sentry-", "");
        concurrentHashMap.put(str, str2);
      } 
    } 
    return (Map)concurrentHashMap;
  }
  
  @Internal
  public void setValuesFromTransaction(@NotNull SentryId paramSentryId, @NotNull SentryOptions paramSentryOptions, @Nullable TracesSamplingDecision paramTracesSamplingDecision, @Nullable String paramString, @Nullable TransactionNameSource paramTransactionNameSource) {
    setTraceId(paramSentryId.toString());
    setPublicKey((new Dsn(paramSentryOptions.getDsn())).getPublicKey());
    setRelease(paramSentryOptions.getRelease());
    setEnvironment(paramSentryOptions.getEnvironment());
    setTransaction(isHighQualityTransactionName(paramTransactionNameSource) ? paramString : null);
    setSampleRate(sampleRateToString(sampleRate(paramTracesSamplingDecision)));
    setSampled(StringUtils.toString(sampled(paramTracesSamplingDecision)));
  }
  
  @Internal
  public void setValuesFromScope(@NotNull IScope paramIScope, @NotNull SentryOptions paramSentryOptions) {
    PropagationContext propagationContext = paramIScope.getPropagationContext();
    setTraceId(propagationContext.getTraceId().toString());
    setPublicKey((new Dsn(paramSentryOptions.getDsn())).getPublicKey());
    setRelease(paramSentryOptions.getRelease());
    setEnvironment(paramSentryOptions.getEnvironment());
    setTransaction(null);
    setSampleRate(null);
    setSampled(null);
  }
  
  @Nullable
  private static Double sampleRate(@Nullable TracesSamplingDecision paramTracesSamplingDecision) {
    return (paramTracesSamplingDecision == null) ? null : paramTracesSamplingDecision.getSampleRate();
  }
  
  @Nullable
  private static String sampleRateToString(@Nullable Double paramDouble) {
    if (!SampleRateUtils.isValidTracesSampleRate(paramDouble, false))
      return null; 
    DecimalFormat decimalFormat = new DecimalFormat("#.################", DecimalFormatSymbols.getInstance(Locale.ROOT));
    return decimalFormat.format(paramDouble);
  }
  
  @Nullable
  private static Boolean sampled(@Nullable TracesSamplingDecision paramTracesSamplingDecision) {
    return (paramTracesSamplingDecision == null) ? null : paramTracesSamplingDecision.getSampled();
  }
  
  private static boolean isHighQualityTransactionName(@Nullable TransactionNameSource paramTransactionNameSource) {
    return (paramTransactionNameSource != null && !TransactionNameSource.URL.equals(paramTransactionNameSource));
  }
  
  @Internal
  @Nullable
  public Double getSampleRateDouble() {
    String str = getSampleRate();
    if (str != null)
      try {
        double d = Double.parseDouble(str);
        if (SampleRateUtils.isValidTracesSampleRate(Double.valueOf(d), false))
          return Double.valueOf(d); 
      } catch (NumberFormatException numberFormatException) {
        return null;
      }  
    return null;
  }
  
  @Internal
  @Nullable
  public TraceContext toTraceContext() {
    String str1 = getTraceId();
    String str2 = getPublicKey();
    if (str1 != null && str2 != null) {
      TraceContext traceContext = new TraceContext(new SentryId(str1), str2, getRelease(), getEnvironment(), getUserId(), getTransaction(), getSampleRate(), getSampled());
      traceContext.setUnknown(getUnknown());
      return traceContext;
    } 
    return null;
  }
  
  @Internal
  public static final class DSCKeys {
    public static final String TRACE_ID = "sentry-trace_id";
    
    public static final String PUBLIC_KEY = "sentry-public_key";
    
    public static final String RELEASE = "sentry-release";
    
    public static final String USER_ID = "sentry-user_id";
    
    public static final String ENVIRONMENT = "sentry-environment";
    
    public static final String TRANSACTION = "sentry-transaction";
    
    public static final String SAMPLE_RATE = "sentry-sample_rate";
    
    public static final String SAMPLED = "sentry-sampled";
    
    public static final List<String> ALL = Arrays.asList(new String[] { "sentry-trace_id", "sentry-public_key", "sentry-release", "sentry-user_id", "sentry-environment", "sentry-transaction", "sentry-sample_rate", "sentry-sampled" });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\Baggage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */