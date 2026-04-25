package io.sentry;

import io.sentry.protocol.User;
import io.sentry.util.StringUtils;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Session implements JsonUnknown, JsonSerializable {
  @NotNull
  private final Date started;
  
  @Nullable
  private Date timestamp;
  
  @NotNull
  private final AtomicInteger errorCount;
  
  @Nullable
  private final String distinctId;
  
  @Nullable
  private final UUID sessionId;
  
  @Nullable
  private Boolean init;
  
  @NotNull
  private State status;
  
  @Nullable
  private Long sequence;
  
  @Nullable
  private Double duration;
  
  @Nullable
  private final String ipAddress;
  
  @Nullable
  private String userAgent;
  
  @Nullable
  private final String environment;
  
  @NotNull
  private final String release;
  
  @Nullable
  private String abnormalMechanism;
  
  @NotNull
  private final Object sessionLock = new Object();
  
  @Nullable
  private Map<String, Object> unknown;
  
  public Session(@NotNull State paramState, @NotNull Date paramDate1, @Nullable Date paramDate2, int paramInt, @Nullable String paramString1, @Nullable UUID paramUUID, @Nullable Boolean paramBoolean, @Nullable Long paramLong, @Nullable Double paramDouble, @Nullable String paramString2, @Nullable String paramString3, @Nullable String paramString4, @NotNull String paramString5, @Nullable String paramString6) {
    this.status = paramState;
    this.started = paramDate1;
    this.timestamp = paramDate2;
    this.errorCount = new AtomicInteger(paramInt);
    this.distinctId = paramString1;
    this.sessionId = paramUUID;
    this.init = paramBoolean;
    this.sequence = paramLong;
    this.duration = paramDouble;
    this.ipAddress = paramString2;
    this.userAgent = paramString3;
    this.environment = paramString4;
    this.release = paramString5;
    this.abnormalMechanism = paramString6;
  }
  
  public Session(@Nullable String paramString1, @Nullable User paramUser, @Nullable String paramString2, @NotNull String paramString3) {
    this(State.Ok, DateUtils.getCurrentDateTime(), DateUtils.getCurrentDateTime(), 0, paramString1, UUID.randomUUID(), Boolean.valueOf(true), null, null, (paramUser != null) ? paramUser.getIpAddress() : null, null, paramString2, paramString3, null);
  }
  
  public boolean isTerminated() {
    return (this.status != State.Ok);
  }
  
  @Nullable
  public Date getStarted() {
    return (this.started == null) ? null : (Date)this.started.clone();
  }
  
  @Nullable
  public String getDistinctId() {
    return this.distinctId;
  }
  
  @Nullable
  public UUID getSessionId() {
    return this.sessionId;
  }
  
  @Nullable
  public String getIpAddress() {
    return this.ipAddress;
  }
  
  @Nullable
  public String getUserAgent() {
    return this.userAgent;
  }
  
  @Nullable
  public String getEnvironment() {
    return this.environment;
  }
  
  @NotNull
  public String getRelease() {
    return this.release;
  }
  
  @Nullable
  public Boolean getInit() {
    return this.init;
  }
  
  @Internal
  public void setInitAsTrue() {
    this.init = Boolean.valueOf(true);
  }
  
  public int errorCount() {
    return this.errorCount.get();
  }
  
  @NotNull
  public State getStatus() {
    return this.status;
  }
  
  @Nullable
  public Long getSequence() {
    return this.sequence;
  }
  
  @Nullable
  public Double getDuration() {
    return this.duration;
  }
  
  @Nullable
  public String getAbnormalMechanism() {
    return this.abnormalMechanism;
  }
  
  @Nullable
  public Date getTimestamp() {
    Date date = this.timestamp;
    return (date != null) ? (Date)date.clone() : null;
  }
  
  public void end() {
    end(DateUtils.getCurrentDateTime());
  }
  
  public void end(@Nullable Date paramDate) {
    synchronized (this.sessionLock) {
      this.init = null;
      if (this.status == State.Ok)
        this.status = State.Exited; 
      if (paramDate != null) {
        this.timestamp = paramDate;
      } else {
        this.timestamp = DateUtils.getCurrentDateTime();
      } 
      if (this.timestamp != null) {
        this.duration = Double.valueOf(calculateDurationTime(this.timestamp));
        this.sequence = Long.valueOf(getSequenceTimestamp(this.timestamp));
      } 
    } 
  }
  
  private double calculateDurationTime(@NotNull Date paramDate) {
    long l = Math.abs(paramDate.getTime() - this.started.getTime());
    return l / 1000.0D;
  }
  
  public boolean update(@Nullable State paramState, @Nullable String paramString, boolean paramBoolean) {
    return update(paramState, paramString, paramBoolean, null);
  }
  
  public boolean update(@Nullable State paramState, @Nullable String paramString1, boolean paramBoolean, @Nullable String paramString2) {
    synchronized (this.sessionLock) {
      boolean bool = false;
      if (paramState != null) {
        this.status = paramState;
        bool = true;
      } 
      if (paramString1 != null) {
        this.userAgent = paramString1;
        bool = true;
      } 
      if (paramBoolean) {
        this.errorCount.addAndGet(1);
        bool = true;
      } 
      if (paramString2 != null) {
        this.abnormalMechanism = paramString2;
        bool = true;
      } 
      if (bool) {
        this.init = null;
        this.timestamp = DateUtils.getCurrentDateTime();
        if (this.timestamp != null)
          this.sequence = Long.valueOf(getSequenceTimestamp(this.timestamp)); 
      } 
      return bool;
    } 
  }
  
  private long getSequenceTimestamp(@NotNull Date paramDate) {
    long l = paramDate.getTime();
    if (l < 0L)
      l = Math.abs(l); 
    return l;
  }
  
  @NotNull
  public Session clone() {
    return new Session(this.status, this.started, this.timestamp, this.errorCount.get(), this.distinctId, this.sessionId, this.init, this.sequence, this.duration, this.ipAddress, this.userAgent, this.environment, this.release, this.abnormalMechanism);
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    if (this.sessionId != null)
      paramObjectWriter.name("sid").value(this.sessionId.toString()); 
    if (this.distinctId != null)
      paramObjectWriter.name("did").value(this.distinctId); 
    if (this.init != null)
      paramObjectWriter.name("init").value(this.init); 
    paramObjectWriter.name("started").value(paramILogger, this.started);
    paramObjectWriter.name("status").value(paramILogger, this.status.name().toLowerCase(Locale.ROOT));
    if (this.sequence != null)
      paramObjectWriter.name("seq").value(this.sequence); 
    paramObjectWriter.name("errors").value(this.errorCount.intValue());
    if (this.duration != null)
      paramObjectWriter.name("duration").value(this.duration); 
    if (this.timestamp != null)
      paramObjectWriter.name("timestamp").value(paramILogger, this.timestamp); 
    if (this.abnormalMechanism != null)
      paramObjectWriter.name("abnormal_mechanism").value(paramILogger, this.abnormalMechanism); 
    paramObjectWriter.name("attrs");
    paramObjectWriter.beginObject();
    paramObjectWriter.name("release").value(paramILogger, this.release);
    if (this.environment != null)
      paramObjectWriter.name("environment").value(paramILogger, this.environment); 
    if (this.ipAddress != null)
      paramObjectWriter.name("ip_address").value(paramILogger, this.ipAddress); 
    if (this.userAgent != null)
      paramObjectWriter.name("user_agent").value(paramILogger, this.userAgent); 
    paramObjectWriter.endObject();
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str);
        paramObjectWriter.value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  @Nullable
  public Map<String, Object> getUnknown() {
    return this.unknown;
  }
  
  public void setUnknown(@Nullable Map<String, Object> paramMap) {
    this.unknown = paramMap;
  }
  
  public enum State {
    Ok, Exited, Crashed, Abnormal;
  }
  
  public static final class JsonKeys {
    public static final String SID = "sid";
    
    public static final String DID = "did";
    
    public static final String INIT = "init";
    
    public static final String STARTED = "started";
    
    public static final String STATUS = "status";
    
    public static final String SEQ = "seq";
    
    public static final String ERRORS = "errors";
    
    public static final String DURATION = "duration";
    
    public static final String TIMESTAMP = "timestamp";
    
    public static final String ATTRS = "attrs";
    
    public static final String RELEASE = "release";
    
    public static final String ENVIRONMENT = "environment";
    
    public static final String IP_ADDRESS = "ip_address";
    
    public static final String USER_AGENT = "user_agent";
    
    public static final String ABNORMAL_MECHANISM = "abnormal_mechanism";
  }
  
  public static final class Deserializer implements JsonDeserializer<Session> {
    @NotNull
    public Session deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      Date date1 = null;
      Date date2 = null;
      Integer integer = null;
      String str1 = null;
      UUID uUID = null;
      Boolean bool = null;
      Session.State state = null;
      Long long_ = null;
      Double double_ = null;
      String str2 = null;
      String str3 = null;
      String str4 = null;
      String str5 = null;
      String str6 = null;
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str8;
        String str9;
        String str7 = param1JsonObjectReader.nextName();
        switch (str7) {
          case "sid":
            str8 = null;
            try {
              str8 = param1JsonObjectReader.nextStringOrNull();
              uUID = UUID.fromString(str8);
            } catch (IllegalArgumentException illegalArgumentException) {
              param1ILogger.log(SentryLevel.ERROR, "%s sid is not valid.", new Object[] { str8 });
            } 
            continue;
          case "did":
            str1 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "init":
            bool = param1JsonObjectReader.nextBooleanOrNull();
            continue;
          case "started":
            date1 = param1JsonObjectReader.nextDateOrNull(param1ILogger);
            continue;
          case "status":
            str9 = StringUtils.capitalize(param1JsonObjectReader.nextStringOrNull());
            if (str9 != null)
              state = Session.State.valueOf(str9); 
            continue;
          case "seq":
            long_ = param1JsonObjectReader.nextLongOrNull();
            continue;
          case "errors":
            integer = param1JsonObjectReader.nextIntegerOrNull();
            continue;
          case "duration":
            double_ = param1JsonObjectReader.nextDoubleOrNull();
            continue;
          case "timestamp":
            date2 = param1JsonObjectReader.nextDateOrNull(param1ILogger);
            continue;
          case "abnormal_mechanism":
            str6 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "attrs":
            param1JsonObjectReader.beginObject();
            while (param1JsonObjectReader.peek() == JsonToken.NAME) {
              String str = param1JsonObjectReader.nextName();
              switch (str) {
                case "release":
                  str5 = param1JsonObjectReader.nextStringOrNull();
                  continue;
                case "environment":
                  str4 = param1JsonObjectReader.nextStringOrNull();
                  continue;
                case "ip_address":
                  str2 = param1JsonObjectReader.nextStringOrNull();
                  continue;
                case "user_agent":
                  str3 = param1JsonObjectReader.nextStringOrNull();
                  continue;
              } 
              param1JsonObjectReader.skipValue();
            } 
            param1JsonObjectReader.endObject();
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, (Map)concurrentHashMap, str7);
      } 
      if (state == null)
        throw missingRequiredFieldException("status", param1ILogger); 
      if (date1 == null)
        throw missingRequiredFieldException("started", param1ILogger); 
      if (integer == null)
        throw missingRequiredFieldException("errors", param1ILogger); 
      if (str5 == null)
        throw missingRequiredFieldException("release", param1ILogger); 
      Session session = new Session(state, date1, date2, integer.intValue(), str1, uUID, bool, long_, double_, str2, str3, str4, str5, str6);
      session.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return session;
    }
    
    private Exception missingRequiredFieldException(String param1String, ILogger param1ILogger) {
      String str = "Missing required field \"" + param1String + "\"";
      IllegalStateException illegalStateException = new IllegalStateException(str);
      param1ILogger.log(SentryLevel.ERROR, str, illegalStateException);
      return illegalStateException;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\Session.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */