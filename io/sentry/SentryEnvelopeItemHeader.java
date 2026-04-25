package io.sentry;

import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class SentryEnvelopeItemHeader implements JsonSerializable, JsonUnknown {
  @Nullable
  private final String contentType;
  
  @Nullable
  private final String fileName;
  
  @NotNull
  private final SentryItemType type;
  
  private final int length;
  
  @Nullable
  private final Callable<Integer> getLength;
  
  @Nullable
  private final String attachmentType;
  
  @Nullable
  private Map<String, Object> unknown;
  
  @NotNull
  public SentryItemType getType() {
    return this.type;
  }
  
  public int getLength() {
    if (this.getLength != null)
      try {
        return ((Integer)this.getLength.call()).intValue();
      } catch (Throwable throwable) {
        return -1;
      }  
    return this.length;
  }
  
  @Nullable
  public String getContentType() {
    return this.contentType;
  }
  
  @Nullable
  public String getFileName() {
    return this.fileName;
  }
  
  @Internal
  public SentryEnvelopeItemHeader(@NotNull SentryItemType paramSentryItemType, int paramInt, @Nullable String paramString1, @Nullable String paramString2, @Nullable String paramString3) {
    this.type = (SentryItemType)Objects.requireNonNull(paramSentryItemType, "type is required");
    this.contentType = paramString1;
    this.length = paramInt;
    this.fileName = paramString2;
    this.getLength = null;
    this.attachmentType = paramString3;
  }
  
  SentryEnvelopeItemHeader(@NotNull SentryItemType paramSentryItemType, @Nullable Callable<Integer> paramCallable, @Nullable String paramString1, @Nullable String paramString2, @Nullable String paramString3) {
    this.type = (SentryItemType)Objects.requireNonNull(paramSentryItemType, "type is required");
    this.contentType = paramString1;
    this.length = -1;
    this.fileName = paramString2;
    this.getLength = paramCallable;
    this.attachmentType = paramString3;
  }
  
  SentryEnvelopeItemHeader(@NotNull SentryItemType paramSentryItemType, @Nullable Callable<Integer> paramCallable, @Nullable String paramString1, @Nullable String paramString2) {
    this(paramSentryItemType, paramCallable, paramString1, paramString2, (String)null);
  }
  
  @Nullable
  public String getAttachmentType() {
    return this.attachmentType;
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    if (this.contentType != null)
      paramObjectWriter.name("content_type").value(this.contentType); 
    if (this.fileName != null)
      paramObjectWriter.name("filename").value(this.fileName); 
    paramObjectWriter.name("type").value(paramILogger, this.type);
    if (this.attachmentType != null)
      paramObjectWriter.name("attachment_type").value(this.attachmentType); 
    paramObjectWriter.name("length").value(getLength());
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
  
  public static final class JsonKeys {
    public static final String CONTENT_TYPE = "content_type";
    
    public static final String FILENAME = "filename";
    
    public static final String TYPE = "type";
    
    public static final String ATTACHMENT_TYPE = "attachment_type";
    
    public static final String LENGTH = "length";
  }
  
  public static final class Deserializer implements JsonDeserializer<SentryEnvelopeItemHeader> {
    @NotNull
    public SentryEnvelopeItemHeader deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      String str1 = null;
      String str2 = null;
      SentryItemType sentryItemType = null;
      int i = 0;
      String str3 = null;
      HashMap<Object, Object> hashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "content_type":
            str1 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "filename":
            str2 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "type":
            sentryItemType = param1JsonObjectReader.<SentryItemType>nextOrNull(param1ILogger, new SentryItemType.Deserializer());
            continue;
          case "length":
            i = param1JsonObjectReader.nextInt();
            continue;
          case "attachment_type":
            str3 = param1JsonObjectReader.nextStringOrNull();
            continue;
        } 
        if (hashMap == null)
          hashMap = new HashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, (Map)hashMap, str);
      } 
      if (sentryItemType == null)
        throw missingRequiredFieldException("type", param1ILogger); 
      SentryEnvelopeItemHeader sentryEnvelopeItemHeader = new SentryEnvelopeItemHeader(sentryItemType, i, str1, str2, str3);
      sentryEnvelopeItemHeader.setUnknown((Map)hashMap);
      param1JsonObjectReader.endObject();
      return sentryEnvelopeItemHeader;
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


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryEnvelopeItemHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */