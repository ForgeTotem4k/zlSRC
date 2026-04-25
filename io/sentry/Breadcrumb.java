package io.sentry;

import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.util.UrlUtils;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Breadcrumb implements JsonUnknown, JsonSerializable, Comparable<Breadcrumb> {
  @NotNull
  private final Date timestamp;
  
  @NotNull
  private final Long nanos = Long.valueOf(System.nanoTime());
  
  @Nullable
  private String message;
  
  @Nullable
  private String type;
  
  @NotNull
  private Map<String, Object> data = new ConcurrentHashMap<>();
  
  @Nullable
  private String category;
  
  @Nullable
  private SentryLevel level;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public Breadcrumb(@NotNull Date paramDate) {
    this.timestamp = paramDate;
  }
  
  Breadcrumb(@NotNull Breadcrumb paramBreadcrumb) {
    this.timestamp = paramBreadcrumb.timestamp;
    this.message = paramBreadcrumb.message;
    this.type = paramBreadcrumb.type;
    this.category = paramBreadcrumb.category;
    Map<String, Object> map = CollectionUtils.newConcurrentHashMap(paramBreadcrumb.data);
    if (map != null)
      this.data = map; 
    this.unknown = CollectionUtils.newConcurrentHashMap(paramBreadcrumb.unknown);
    this.level = paramBreadcrumb.level;
  }
  
  public static Breadcrumb fromMap(@NotNull Map<String, Object> paramMap, @NotNull SentryOptions paramSentryOptions) {
    Date date = DateUtils.getCurrentDateTime();
    String str1 = null;
    String str2 = null;
    ConcurrentHashMap<Object, Object> concurrentHashMap1 = new ConcurrentHashMap<>();
    String str3 = null;
    SentryLevel sentryLevel = null;
    ConcurrentHashMap<Object, Object> concurrentHashMap2 = null;
    for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
      Map map;
      String str;
      Object object = entry.getValue();
      switch ((String)entry.getKey()) {
        case "timestamp":
          if (object instanceof String) {
            Date date1 = JsonObjectReader.dateOrNull((String)object, paramSentryOptions.getLogger());
            if (date1 != null)
              date = date1; 
          } 
          continue;
        case "message":
          str1 = (object instanceof String) ? (String)object : null;
          continue;
        case "type":
          str2 = (object instanceof String) ? (String)object : null;
          continue;
        case "data":
          map = (object instanceof Map) ? (Map)object : null;
          if (map != null)
            for (Map.Entry entry1 : map.entrySet()) {
              if (entry1.getKey() instanceof String && entry1.getValue() != null) {
                concurrentHashMap1.put(entry1.getKey(), entry1.getValue());
                continue;
              } 
              paramSentryOptions.getLogger().log(SentryLevel.WARNING, "Invalid key or null value in data map.", new Object[0]);
            }  
          continue;
        case "category":
          str3 = (object instanceof String) ? (String)object : null;
          continue;
        case "level":
          str = (object instanceof String) ? (String)object : null;
          if (str != null)
            try {
              sentryLevel = SentryLevel.valueOf(str.toUpperCase(Locale.ROOT));
            } catch (Exception exception) {} 
          continue;
      } 
      if (concurrentHashMap2 == null)
        concurrentHashMap2 = new ConcurrentHashMap<>(); 
      concurrentHashMap2.put(entry.getKey(), entry.getValue());
    } 
    Breadcrumb breadcrumb = new Breadcrumb(date);
    breadcrumb.message = str1;
    breadcrumb.type = str2;
    breadcrumb.data = (Map)concurrentHashMap1;
    breadcrumb.category = str3;
    breadcrumb.level = sentryLevel;
    breadcrumb.setUnknown((Map)concurrentHashMap2);
    return breadcrumb;
  }
  
  @NotNull
  public static Breadcrumb http(@NotNull String paramString1, @NotNull String paramString2) {
    Breadcrumb breadcrumb = new Breadcrumb();
    UrlUtils.UrlDetails urlDetails = UrlUtils.parse(paramString1);
    breadcrumb.setType("http");
    breadcrumb.setCategory("http");
    if (urlDetails.getUrl() != null)
      breadcrumb.setData("url", urlDetails.getUrl()); 
    breadcrumb.setData("method", paramString2.toUpperCase(Locale.ROOT));
    if (urlDetails.getQuery() != null)
      breadcrumb.setData("http.query", urlDetails.getQuery()); 
    if (urlDetails.getFragment() != null)
      breadcrumb.setData("http.fragment", urlDetails.getFragment()); 
    return breadcrumb;
  }
  
  @NotNull
  public static Breadcrumb http(@NotNull String paramString1, @NotNull String paramString2, @Nullable Integer paramInteger) {
    Breadcrumb breadcrumb = http(paramString1, paramString2);
    if (paramInteger != null)
      breadcrumb.setData("status_code", paramInteger); 
    return breadcrumb;
  }
  
  @NotNull
  public static Breadcrumb graphqlOperation(@Nullable String paramString1, @Nullable String paramString2, @Nullable String paramString3) {
    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setType("graphql");
    if (paramString1 != null)
      breadcrumb.setData("operation_name", paramString1); 
    if (paramString2 != null) {
      breadcrumb.setData("operation_type", paramString2);
      breadcrumb.setCategory(paramString2);
    } else {
      breadcrumb.setCategory("graphql.operation");
    } 
    if (paramString3 != null)
      breadcrumb.setData("operation_id", paramString3); 
    return breadcrumb;
  }
  
  @NotNull
  public static Breadcrumb graphqlDataFetcher(@Nullable String paramString1, @Nullable String paramString2, @Nullable String paramString3, @Nullable String paramString4) {
    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setType("graphql");
    breadcrumb.setCategory("graphql.fetcher");
    if (paramString1 != null)
      breadcrumb.setData("path", paramString1); 
    if (paramString2 != null)
      breadcrumb.setData("field", paramString2); 
    if (paramString3 != null)
      breadcrumb.setData("type", paramString3); 
    if (paramString4 != null)
      breadcrumb.setData("object_type", paramString4); 
    return breadcrumb;
  }
  
  @NotNull
  public static Breadcrumb graphqlDataLoader(@NotNull Iterable<?> paramIterable, @Nullable Class<?> paramClass1, @Nullable Class<?> paramClass2, @Nullable String paramString) {
    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setType("graphql");
    breadcrumb.setCategory("graphql.data_loader");
    ArrayList<String> arrayList = new ArrayList();
    for (Object object : paramIterable)
      arrayList.add(object.toString()); 
    breadcrumb.setData("keys", arrayList);
    if (paramClass1 != null)
      breadcrumb.setData("key_type", paramClass1.getName()); 
    if (paramClass2 != null)
      breadcrumb.setData("value_type", paramClass2.getName()); 
    if (paramString != null)
      breadcrumb.setData("name", paramString); 
    return breadcrumb;
  }
  
  @NotNull
  public static Breadcrumb navigation(@NotNull String paramString1, @NotNull String paramString2) {
    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setCategory("navigation");
    breadcrumb.setType("navigation");
    breadcrumb.setData("from", paramString1);
    breadcrumb.setData("to", paramString2);
    return breadcrumb;
  }
  
  @NotNull
  public static Breadcrumb transaction(@NotNull String paramString) {
    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setType("default");
    breadcrumb.setCategory("sentry.transaction");
    breadcrumb.setMessage(paramString);
    return breadcrumb;
  }
  
  @NotNull
  public static Breadcrumb debug(@NotNull String paramString) {
    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setType("debug");
    breadcrumb.setMessage(paramString);
    breadcrumb.setLevel(SentryLevel.DEBUG);
    return breadcrumb;
  }
  
  @NotNull
  public static Breadcrumb error(@NotNull String paramString) {
    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setType("error");
    breadcrumb.setMessage(paramString);
    breadcrumb.setLevel(SentryLevel.ERROR);
    return breadcrumb;
  }
  
  @NotNull
  public static Breadcrumb info(@NotNull String paramString) {
    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setType("info");
    breadcrumb.setMessage(paramString);
    breadcrumb.setLevel(SentryLevel.INFO);
    return breadcrumb;
  }
  
  @NotNull
  public static Breadcrumb query(@NotNull String paramString) {
    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setType("query");
    breadcrumb.setMessage(paramString);
    return breadcrumb;
  }
  
  @NotNull
  public static Breadcrumb ui(@NotNull String paramString1, @NotNull String paramString2) {
    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setType("default");
    breadcrumb.setCategory("ui." + paramString1);
    breadcrumb.setMessage(paramString2);
    return breadcrumb;
  }
  
  @NotNull
  public static Breadcrumb user(@NotNull String paramString1, @NotNull String paramString2) {
    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setType("user");
    breadcrumb.setCategory(paramString1);
    breadcrumb.setMessage(paramString2);
    return breadcrumb;
  }
  
  @NotNull
  public static Breadcrumb userInteraction(@NotNull String paramString1, @Nullable String paramString2, @Nullable String paramString3) {
    return userInteraction(paramString1, paramString2, paramString3, Collections.emptyMap());
  }
  
  @NotNull
  public static Breadcrumb userInteraction(@NotNull String paramString1, @Nullable String paramString2, @Nullable String paramString3, @Nullable String paramString4, @NotNull Map<String, Object> paramMap) {
    Breadcrumb breadcrumb = new Breadcrumb();
    breadcrumb.setType("user");
    breadcrumb.setCategory("ui." + paramString1);
    if (paramString2 != null)
      breadcrumb.setData("view.id", paramString2); 
    if (paramString3 != null)
      breadcrumb.setData("view.class", paramString3); 
    if (paramString4 != null)
      breadcrumb.setData("view.tag", paramString4); 
    for (Map.Entry<String, Object> entry : paramMap.entrySet())
      breadcrumb.getData().put((String)entry.getKey(), entry.getValue()); 
    breadcrumb.setLevel(SentryLevel.INFO);
    return breadcrumb;
  }
  
  @NotNull
  public static Breadcrumb userInteraction(@NotNull String paramString1, @Nullable String paramString2, @Nullable String paramString3, @NotNull Map<String, Object> paramMap) {
    return userInteraction(paramString1, paramString2, paramString3, null, paramMap);
  }
  
  public Breadcrumb() {
    this(DateUtils.getCurrentDateTime());
  }
  
  public Breadcrumb(@Nullable String paramString) {
    this();
    this.message = paramString;
  }
  
  @NotNull
  public Date getTimestamp() {
    return (Date)this.timestamp.clone();
  }
  
  @Nullable
  public String getMessage() {
    return this.message;
  }
  
  public void setMessage(@Nullable String paramString) {
    this.message = paramString;
  }
  
  @Nullable
  public String getType() {
    return this.type;
  }
  
  public void setType(@Nullable String paramString) {
    this.type = paramString;
  }
  
  @Internal
  @NotNull
  public Map<String, Object> getData() {
    return this.data;
  }
  
  @Nullable
  public Object getData(@NotNull String paramString) {
    return this.data.get(paramString);
  }
  
  public void setData(@NotNull String paramString, @NotNull Object paramObject) {
    this.data.put(paramString, paramObject);
  }
  
  public void removeData(@NotNull String paramString) {
    this.data.remove(paramString);
  }
  
  @Nullable
  public String getCategory() {
    return this.category;
  }
  
  public void setCategory(@Nullable String paramString) {
    this.category = paramString;
  }
  
  @Nullable
  public SentryLevel getLevel() {
    return this.level;
  }
  
  public void setLevel(@Nullable SentryLevel paramSentryLevel) {
    this.level = paramSentryLevel;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    Breadcrumb breadcrumb = (Breadcrumb)paramObject;
    return (this.timestamp.getTime() == breadcrumb.timestamp.getTime() && Objects.equals(this.message, breadcrumb.message) && Objects.equals(this.type, breadcrumb.type) && Objects.equals(this.category, breadcrumb.category) && this.level == breadcrumb.level);
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.timestamp, this.message, this.type, this.category, this.level });
  }
  
  @Nullable
  public Map<String, Object> getUnknown() {
    return this.unknown;
  }
  
  public void setUnknown(@Nullable Map<String, Object> paramMap) {
    this.unknown = paramMap;
  }
  
  public int compareTo(@NotNull Breadcrumb paramBreadcrumb) {
    return this.nanos.compareTo(paramBreadcrumb.nanos);
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    paramObjectWriter.name("timestamp").value(paramILogger, this.timestamp);
    if (this.message != null)
      paramObjectWriter.name("message").value(this.message); 
    if (this.type != null)
      paramObjectWriter.name("type").value(this.type); 
    paramObjectWriter.name("data").value(paramILogger, this.data);
    if (this.category != null)
      paramObjectWriter.name("category").value(this.category); 
    if (this.level != null)
      paramObjectWriter.name("level").value(paramILogger, this.level); 
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str);
        paramObjectWriter.value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String TIMESTAMP = "timestamp";
    
    public static final String MESSAGE = "message";
    
    public static final String TYPE = "type";
    
    public static final String DATA = "data";
    
    public static final String CATEGORY = "category";
    
    public static final String LEVEL = "level";
  }
  
  public static final class Deserializer implements JsonDeserializer<Breadcrumb> {
    @NotNull
    public Breadcrumb deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      param1JsonObjectReader.beginObject();
      Date date = DateUtils.getCurrentDateTime();
      String str1 = null;
      String str2 = null;
      Map map = new ConcurrentHashMap<>();
      String str3 = null;
      SentryLevel sentryLevel = null;
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        Date date1;
        Map map1;
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "timestamp":
            date1 = param1JsonObjectReader.nextDateOrNull(param1ILogger);
            if (date1 != null)
              date = date1; 
            continue;
          case "message":
            str1 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "type":
            str2 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "data":
            map1 = CollectionUtils.newConcurrentHashMap((Map)param1JsonObjectReader.nextObjectOrNull());
            if (map1 != null)
              map = map1; 
            continue;
          case "category":
            str3 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "level":
            try {
              sentryLevel = (new SentryLevel.Deserializer()).deserialize(param1JsonObjectReader, param1ILogger);
            } catch (Exception exception) {
              param1ILogger.log(SentryLevel.ERROR, exception, "Error when deserializing SentryLevel", new Object[0]);
            } 
            continue;
        } 
        if (concurrentHashMap == null)
          concurrentHashMap = new ConcurrentHashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, (Map)concurrentHashMap, str);
      } 
      Breadcrumb breadcrumb = new Breadcrumb(date);
      breadcrumb.message = str1;
      breadcrumb.type = str2;
      breadcrumb.data = map;
      breadcrumb.category = str3;
      breadcrumb.level = sentryLevel;
      breadcrumb.setUnknown((Map)concurrentHashMap);
      param1JsonObjectReader.endObject();
      return breadcrumb;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\Breadcrumb.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */