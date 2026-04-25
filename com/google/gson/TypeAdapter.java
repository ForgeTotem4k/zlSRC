package com.google.gson;

import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public abstract class TypeAdapter<T> {
  public abstract void write(JsonWriter paramJsonWriter, T paramT) throws IOException;
  
  public final void toJson(Writer paramWriter, T paramT) throws IOException {
    JsonWriter jsonWriter = new JsonWriter(paramWriter);
    write(jsonWriter, paramT);
  }
  
  public final String toJson(T paramT) {
    StringWriter stringWriter = new StringWriter();
    try {
      toJson(stringWriter, paramT);
    } catch (IOException iOException) {
      throw new JsonIOException(iOException);
    } 
    return stringWriter.toString();
  }
  
  public final JsonElement toJsonTree(T paramT) {
    try {
      JsonTreeWriter jsonTreeWriter = new JsonTreeWriter();
      write((JsonWriter)jsonTreeWriter, paramT);
      return jsonTreeWriter.get();
    } catch (IOException iOException) {
      throw new JsonIOException(iOException);
    } 
  }
  
  public abstract T read(JsonReader paramJsonReader) throws IOException;
  
  public final T fromJson(Reader paramReader) throws IOException {
    JsonReader jsonReader = new JsonReader(paramReader);
    return read(jsonReader);
  }
  
  public final T fromJson(String paramString) throws IOException {
    return fromJson(new StringReader(paramString));
  }
  
  public final T fromJsonTree(JsonElement paramJsonElement) {
    try {
      JsonTreeReader jsonTreeReader = new JsonTreeReader(paramJsonElement);
      return read((JsonReader)jsonTreeReader);
    } catch (IOException iOException) {
      throw new JsonIOException(iOException);
    } 
  }
  
  public final TypeAdapter<T> nullSafe() {
    return !(this instanceof NullSafeTypeAdapter) ? new NullSafeTypeAdapter() : this;
  }
  
  static {
  
  }
  
  private final class NullSafeTypeAdapter extends TypeAdapter<T> {
    private NullSafeTypeAdapter() {}
    
    public void write(JsonWriter param1JsonWriter, T param1T) throws IOException {
      if (param1T == null) {
        param1JsonWriter.nullValue();
      } else {
        TypeAdapter.this.write(param1JsonWriter, param1T);
      } 
    }
    
    public T read(JsonReader param1JsonReader) throws IOException {
      if (param1JsonReader.peek() == JsonToken.NULL) {
        param1JsonReader.nextNull();
        return null;
      } 
      return TypeAdapter.this.read(param1JsonReader);
    }
    
    public String toString() {
      return "NullSafeTypeAdapter[" + TypeAdapter.this + "]";
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\TypeAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */