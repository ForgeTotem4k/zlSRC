package com.google.gson.internal.sql;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

class SqlTimestampTypeAdapter extends TypeAdapter<Timestamp> {
  static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson param1Gson, TypeToken<T> param1TypeToken) {
        if (param1TypeToken.getRawType() == Timestamp.class) {
          TypeAdapter typeAdapter = param1Gson.getAdapter(Date.class);
          return new SqlTimestampTypeAdapter(typeAdapter);
        } 
        return null;
      }
      
      static {
      
      }
    };
  
  private final TypeAdapter<Date> dateTypeAdapter;
  
  private SqlTimestampTypeAdapter(TypeAdapter<Date> paramTypeAdapter) {
    this.dateTypeAdapter = paramTypeAdapter;
  }
  
  public Timestamp read(JsonReader paramJsonReader) throws IOException {
    Date date = (Date)this.dateTypeAdapter.read(paramJsonReader);
    return (date != null) ? new Timestamp(date.getTime()) : null;
  }
  
  public void write(JsonWriter paramJsonWriter, Timestamp paramTimestamp) throws IOException {
    this.dateTypeAdapter.write(paramJsonWriter, paramTimestamp);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\sql\SqlTimestampTypeAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */