package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectWriter;
import io.sentry.SentryLockReason;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryStackFrame implements JsonUnknown, JsonSerializable {
  @Nullable
  private List<String> preContext;
  
  @Nullable
  private List<String> postContext;
  
  @Nullable
  private Map<String, String> vars;
  
  @Nullable
  private List<Integer> framesOmitted;
  
  @Nullable
  private String filename;
  
  @Nullable
  private String function;
  
  @Nullable
  private String module;
  
  @Nullable
  private Integer lineno;
  
  @Nullable
  private Integer colno;
  
  @Nullable
  private String absPath;
  
  @Nullable
  private String contextLine;
  
  @Nullable
  private Boolean inApp;
  
  @Nullable
  private String _package;
  
  @Nullable
  private Boolean _native;
  
  @Nullable
  private String platform;
  
  @Nullable
  private String imageAddr;
  
  @Nullable
  private String symbolAddr;
  
  @Nullable
  private String instructionAddr;
  
  @Nullable
  private String symbol;
  
  @Nullable
  private Map<String, Object> unknown;
  
  @Nullable
  private String rawFunction;
  
  @Nullable
  private SentryLockReason lock;
  
  @Nullable
  public List<String> getPreContext() {
    return this.preContext;
  }
  
  public void setPreContext(@Nullable List<String> paramList) {
    this.preContext = paramList;
  }
  
  @Nullable
  public List<String> getPostContext() {
    return this.postContext;
  }
  
  public void setPostContext(@Nullable List<String> paramList) {
    this.postContext = paramList;
  }
  
  @Nullable
  public Map<String, String> getVars() {
    return this.vars;
  }
  
  public void setVars(@Nullable Map<String, String> paramMap) {
    this.vars = paramMap;
  }
  
  @Nullable
  public List<Integer> getFramesOmitted() {
    return this.framesOmitted;
  }
  
  public void setFramesOmitted(@Nullable List<Integer> paramList) {
    this.framesOmitted = paramList;
  }
  
  @Nullable
  public String getFilename() {
    return this.filename;
  }
  
  public void setFilename(@Nullable String paramString) {
    this.filename = paramString;
  }
  
  @Nullable
  public String getFunction() {
    return this.function;
  }
  
  public void setFunction(@Nullable String paramString) {
    this.function = paramString;
  }
  
  @Nullable
  public String getModule() {
    return this.module;
  }
  
  public void setModule(@Nullable String paramString) {
    this.module = paramString;
  }
  
  @Nullable
  public Integer getLineno() {
    return this.lineno;
  }
  
  public void setLineno(@Nullable Integer paramInteger) {
    this.lineno = paramInteger;
  }
  
  @Nullable
  public Integer getColno() {
    return this.colno;
  }
  
  public void setColno(@Nullable Integer paramInteger) {
    this.colno = paramInteger;
  }
  
  @Nullable
  public String getAbsPath() {
    return this.absPath;
  }
  
  public void setAbsPath(@Nullable String paramString) {
    this.absPath = paramString;
  }
  
  @Nullable
  public String getContextLine() {
    return this.contextLine;
  }
  
  public void setContextLine(@Nullable String paramString) {
    this.contextLine = paramString;
  }
  
  @Nullable
  public Boolean isInApp() {
    return this.inApp;
  }
  
  public void setInApp(@Nullable Boolean paramBoolean) {
    this.inApp = paramBoolean;
  }
  
  @Nullable
  public String getPackage() {
    return this._package;
  }
  
  public void setPackage(@Nullable String paramString) {
    this._package = paramString;
  }
  
  @Nullable
  public String getPlatform() {
    return this.platform;
  }
  
  public void setPlatform(@Nullable String paramString) {
    this.platform = paramString;
  }
  
  @Nullable
  public String getImageAddr() {
    return this.imageAddr;
  }
  
  public void setImageAddr(@Nullable String paramString) {
    this.imageAddr = paramString;
  }
  
  @Nullable
  public String getSymbolAddr() {
    return this.symbolAddr;
  }
  
  public void setSymbolAddr(@Nullable String paramString) {
    this.symbolAddr = paramString;
  }
  
  @Nullable
  public String getInstructionAddr() {
    return this.instructionAddr;
  }
  
  public void setInstructionAddr(@Nullable String paramString) {
    this.instructionAddr = paramString;
  }
  
  @Nullable
  public Boolean isNative() {
    return this._native;
  }
  
  public void setNative(@Nullable Boolean paramBoolean) {
    this._native = paramBoolean;
  }
  
  @Nullable
  public String getRawFunction() {
    return this.rawFunction;
  }
  
  public void setRawFunction(@Nullable String paramString) {
    this.rawFunction = paramString;
  }
  
  @Nullable
  public String getSymbol() {
    return this.symbol;
  }
  
  public void setSymbol(@Nullable String paramString) {
    this.symbol = paramString;
  }
  
  @Nullable
  public SentryLockReason getLock() {
    return this.lock;
  }
  
  public void setLock(@Nullable SentryLockReason paramSentryLockReason) {
    this.lock = paramSentryLockReason;
  }
  
  @Nullable
  public Map<String, Object> getUnknown() {
    return this.unknown;
  }
  
  public void setUnknown(@Nullable Map<String, Object> paramMap) {
    this.unknown = paramMap;
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    if (this.filename != null)
      paramObjectWriter.name("filename").value(this.filename); 
    if (this.function != null)
      paramObjectWriter.name("function").value(this.function); 
    if (this.module != null)
      paramObjectWriter.name("module").value(this.module); 
    if (this.lineno != null)
      paramObjectWriter.name("lineno").value(this.lineno); 
    if (this.colno != null)
      paramObjectWriter.name("colno").value(this.colno); 
    if (this.absPath != null)
      paramObjectWriter.name("abs_path").value(this.absPath); 
    if (this.contextLine != null)
      paramObjectWriter.name("context_line").value(this.contextLine); 
    if (this.inApp != null)
      paramObjectWriter.name("in_app").value(this.inApp); 
    if (this._package != null)
      paramObjectWriter.name("package").value(this._package); 
    if (this._native != null)
      paramObjectWriter.name("native").value(this._native); 
    if (this.platform != null)
      paramObjectWriter.name("platform").value(this.platform); 
    if (this.imageAddr != null)
      paramObjectWriter.name("image_addr").value(this.imageAddr); 
    if (this.symbolAddr != null)
      paramObjectWriter.name("symbol_addr").value(this.symbolAddr); 
    if (this.instructionAddr != null)
      paramObjectWriter.name("instruction_addr").value(this.instructionAddr); 
    if (this.rawFunction != null)
      paramObjectWriter.name("raw_function").value(this.rawFunction); 
    if (this.symbol != null)
      paramObjectWriter.name("symbol").value(this.symbol); 
    if (this.lock != null)
      paramObjectWriter.name("lock").value(paramILogger, this.lock); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str);
        paramObjectWriter.value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String FILENAME = "filename";
    
    public static final String FUNCTION = "function";
    
    public static final String MODULE = "module";
    
    public static final String LINENO = "lineno";
    
    public static final String COLNO = "colno";
    
    public static final String ABS_PATH = "abs_path";
    
    public static final String CONTEXT_LINE = "context_line";
    
    public static final String IN_APP = "in_app";
    
    public static final String PACKAGE = "package";
    
    public static final String NATIVE = "native";
    
    public static final String PLATFORM = "platform";
    
    public static final String IMAGE_ADDR = "image_addr";
    
    public static final String SYMBOL_ADDR = "symbol_addr";
    
    public static final String INSTRUCTION_ADDR = "instruction_addr";
    
    public static final String RAW_FUNCTION = "raw_function";
    
    public static final String SYMBOL = "symbol";
    
    public static final String LOCK = "lock";
  }
  
  public static final class Deserializer implements JsonDeserializer<SentryStackFrame> {
    @NotNull
    public SentryStackFrame deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      SentryStackFrame sentryStackFrame = new SentryStackFrame();
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "filename":
            sentryStackFrame.filename = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "function":
            sentryStackFrame.function = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "module":
            sentryStackFrame.module = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "lineno":
            sentryStackFrame.lineno = param1JsonObjectReader.nextIntegerOrNull();
            continue;
          case "colno":
            sentryStackFrame.colno = param1JsonObjectReader.nextIntegerOrNull();
            continue;
          case "abs_path":
            sentryStackFrame.absPath = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "context_line":
            sentryStackFrame.contextLine = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "in_app":
            sentryStackFrame.inApp = param1JsonObjectReader.nextBooleanOrNull();
            continue;
          case "package":
            sentryStackFrame._package = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "native":
            sentryStackFrame._native = param1JsonObjectReader.nextBooleanOrNull();
            continue;
          case "platform":
            sentryStackFrame.platform = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "image_addr":
            sentryStackFrame.imageAddr = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "symbol_addr":
            sentryStackFrame.symbolAddr = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "instruction_addr":
            sentryStackFrame.instructionAddr = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "raw_function":
            sentryStackFrame.rawFunction = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "symbol":
            sentryStackFrame.symbol = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "lock":
            sentryStackFrame.lock = (SentryLockReason)param1JsonObjectReader.nextOrNull(param1ILogger, (JsonDeserializer)new SentryLockReason.Deserializer());
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, concurrentHashMap, str);
      } 
      sentryStackFrame.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return sentryStackFrame;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\SentryStackFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */