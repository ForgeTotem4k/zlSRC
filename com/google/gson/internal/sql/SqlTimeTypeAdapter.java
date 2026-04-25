package com.google.gson.internal.sql;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

final class SqlTimeTypeAdapter extends TypeAdapter<Time> {
  static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson param1Gson, TypeToken<T> param1TypeToken) {
        return (param1TypeToken.getRawType() == Time.class) ? new SqlTimeTypeAdapter() : null;
      }
      
      static {
      
      }
    };
  
  private final DateFormat format = new SimpleDateFormat("hh:mm:ss a");
  
  private SqlTimeTypeAdapter() {}
  
  public Time read(JsonReader paramJsonReader) throws IOException {
    if (paramJsonReader.peek() == JsonToken.NULL) {
      paramJsonReader.nextNull();
      return null;
    } 
    String str = paramJsonReader.nextString();
    synchronized (this) {
      TimeZone timeZone = this.format.getTimeZone();
      try {
        Date date = this.format.parse(str);
        return new Time(date.getTime());
      } catch (ParseException parseException) {
        throw new JsonSyntaxException("Failed parsing '" + str + "' as SQL Time; at path " + paramJsonReader.getPreviousPath(), parseException);
      } finally {
        this.format.setTimeZone(timeZone);
      } 
    } 
  }
  
  public void write(JsonWriter paramJsonWriter, Time paramTime) throws IOException {
    String str;
    if (paramTime == null) {
      paramJsonWriter.nullValue();
      return;
    } 
    synchronized (this) {
      str = this.format.format(paramTime);
    } 
    paramJsonWriter.value(str);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\sql\SqlTimeTypeAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */