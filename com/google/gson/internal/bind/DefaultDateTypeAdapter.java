package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.JavaVersion;
import com.google.gson.internal.PreJava9DateFormatProvider;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public final class DefaultDateTypeAdapter<T extends Date> extends TypeAdapter<T> {
  private static final String SIMPLE_NAME = "DefaultDateTypeAdapter";
  
  public static final TypeAdapterFactory DEFAULT_STYLE_FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson param1Gson, TypeToken<T> param1TypeToken) {
        return (param1TypeToken.getRawType() == Date.class) ? new DefaultDateTypeAdapter<>(DefaultDateTypeAdapter.DateType.DATE, 2, 2) : null;
      }
      
      public String toString() {
        return "DefaultDateTypeAdapter#DEFAULT_STYLE_FACTORY";
      }
      
      static {
      
      }
    };
  
  private final DateType<T> dateType;
  
  private final List<DateFormat> dateFormats = new ArrayList<>();
  
  private DefaultDateTypeAdapter(DateType<T> paramDateType, String paramString) {
    this.dateType = Objects.<DateType<T>>requireNonNull(paramDateType);
    this.dateFormats.add(new SimpleDateFormat(paramString, Locale.US));
    if (!Locale.getDefault().equals(Locale.US))
      this.dateFormats.add(new SimpleDateFormat(paramString)); 
  }
  
  private DefaultDateTypeAdapter(DateType<T> paramDateType, int paramInt1, int paramInt2) {
    this.dateType = Objects.<DateType<T>>requireNonNull(paramDateType);
    this.dateFormats.add(DateFormat.getDateTimeInstance(paramInt1, paramInt2, Locale.US));
    if (!Locale.getDefault().equals(Locale.US))
      this.dateFormats.add(DateFormat.getDateTimeInstance(paramInt1, paramInt2)); 
    if (JavaVersion.isJava9OrLater())
      this.dateFormats.add(PreJava9DateFormatProvider.getUsDateTimeFormat(paramInt1, paramInt2)); 
  }
  
  public void write(JsonWriter paramJsonWriter, Date paramDate) throws IOException {
    String str;
    if (paramDate == null) {
      paramJsonWriter.nullValue();
      return;
    } 
    DateFormat dateFormat = this.dateFormats.get(0);
    synchronized (this.dateFormats) {
      str = dateFormat.format(paramDate);
    } 
    paramJsonWriter.value(str);
  }
  
  public T read(JsonReader paramJsonReader) throws IOException {
    if (paramJsonReader.peek() == JsonToken.NULL) {
      paramJsonReader.nextNull();
      return null;
    } 
    Date date = deserializeToDate(paramJsonReader);
    return this.dateType.deserialize(date);
  }
  
  private Date deserializeToDate(JsonReader paramJsonReader) throws IOException {
    String str = paramJsonReader.nextString();
    synchronized (this.dateFormats) {
      for (DateFormat dateFormat : this.dateFormats) {
        TimeZone timeZone = dateFormat.getTimeZone();
        try {
          return dateFormat.parse(str);
        } catch (ParseException parseException) {
        
        } finally {
          dateFormat.setTimeZone(timeZone);
        } 
      } 
    } 
    try {
      return ISO8601Utils.parse(str, new ParsePosition(0));
    } catch (ParseException parseException) {
      throw new JsonSyntaxException("Failed parsing '" + str + "' as Date; at path " + paramJsonReader.getPreviousPath(), parseException);
    } 
  }
  
  public String toString() {
    DateFormat dateFormat = this.dateFormats.get(0);
    return (dateFormat instanceof SimpleDateFormat) ? ("DefaultDateTypeAdapter(" + ((SimpleDateFormat)dateFormat).toPattern() + ')') : ("DefaultDateTypeAdapter(" + dateFormat.getClass().getSimpleName() + ')');
  }
  
  public static abstract class DateType<T extends Date> {
    public static final DateType<Date> DATE = new DateType<Date>(Date.class) {
        protected Date deserialize(Date param2Date) {
          return param2Date;
        }
        
        static {
        
        }
      };
    
    private final Class<T> dateClass;
    
    protected DateType(Class<T> param1Class) {
      this.dateClass = param1Class;
    }
    
    protected abstract T deserialize(Date param1Date);
    
    private TypeAdapterFactory createFactory(DefaultDateTypeAdapter<T> param1DefaultDateTypeAdapter) {
      return TypeAdapters.newFactory(this.dateClass, param1DefaultDateTypeAdapter);
    }
    
    public final TypeAdapterFactory createAdapterFactory(String param1String) {
      return createFactory(new DefaultDateTypeAdapter<>(this, param1String));
    }
    
    public final TypeAdapterFactory createAdapterFactory(int param1Int1, int param1Int2) {
      return createFactory(new DefaultDateTypeAdapter<>(this, param1Int1, param1Int2));
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\bind\DefaultDateTypeAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */