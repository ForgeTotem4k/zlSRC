package com.google.gson.internal.bind;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class JsonTreeWriter extends JsonWriter {
  private static final Writer UNWRITABLE_WRITER = new Writer() {
      public void write(char[] param1ArrayOfchar, int param1Int1, int param1Int2) {
        throw new AssertionError();
      }
      
      public void flush() {
        throw new AssertionError();
      }
      
      public void close() {
        throw new AssertionError();
      }
      
      static {
      
      }
    };
  
  private static final JsonPrimitive SENTINEL_CLOSED = new JsonPrimitive("closed");
  
  private final List<JsonElement> stack = new ArrayList<>();
  
  private String pendingName;
  
  private JsonElement product = (JsonElement)JsonNull.INSTANCE;
  
  public JsonTreeWriter() {
    super(UNWRITABLE_WRITER);
  }
  
  public JsonElement get() {
    if (!this.stack.isEmpty())
      throw new IllegalStateException("Expected one JSON element but was " + this.stack); 
    return this.product;
  }
  
  private JsonElement peek() {
    return this.stack.get(this.stack.size() - 1);
  }
  
  private void put(JsonElement paramJsonElement) {
    if (this.pendingName != null) {
      if (!paramJsonElement.isJsonNull() || getSerializeNulls()) {
        JsonObject jsonObject = (JsonObject)peek();
        jsonObject.add(this.pendingName, paramJsonElement);
      } 
      this.pendingName = null;
    } else if (this.stack.isEmpty()) {
      this.product = paramJsonElement;
    } else {
      JsonElement jsonElement = peek();
      if (jsonElement instanceof JsonArray) {
        ((JsonArray)jsonElement).add(paramJsonElement);
      } else {
        throw new IllegalStateException();
      } 
    } 
  }
  
  @CanIgnoreReturnValue
  public JsonWriter beginArray() throws IOException {
    JsonArray jsonArray = new JsonArray();
    put((JsonElement)jsonArray);
    this.stack.add(jsonArray);
    return this;
  }
  
  @CanIgnoreReturnValue
  public JsonWriter endArray() throws IOException {
    if (this.stack.isEmpty() || this.pendingName != null)
      throw new IllegalStateException(); 
    JsonElement jsonElement = peek();
    if (jsonElement instanceof JsonArray) {
      this.stack.remove(this.stack.size() - 1);
      return this;
    } 
    throw new IllegalStateException();
  }
  
  @CanIgnoreReturnValue
  public JsonWriter beginObject() throws IOException {
    JsonObject jsonObject = new JsonObject();
    put((JsonElement)jsonObject);
    this.stack.add(jsonObject);
    return this;
  }
  
  @CanIgnoreReturnValue
  public JsonWriter endObject() throws IOException {
    if (this.stack.isEmpty() || this.pendingName != null)
      throw new IllegalStateException(); 
    JsonElement jsonElement = peek();
    if (jsonElement instanceof JsonObject) {
      this.stack.remove(this.stack.size() - 1);
      return this;
    } 
    throw new IllegalStateException();
  }
  
  @CanIgnoreReturnValue
  public JsonWriter name(String paramString) throws IOException {
    Objects.requireNonNull(paramString, "name == null");
    if (this.stack.isEmpty() || this.pendingName != null)
      throw new IllegalStateException("Did not expect a name"); 
    JsonElement jsonElement = peek();
    if (jsonElement instanceof JsonObject) {
      this.pendingName = paramString;
      return this;
    } 
    throw new IllegalStateException("Please begin an object before writing a name.");
  }
  
  @CanIgnoreReturnValue
  public JsonWriter value(String paramString) throws IOException {
    if (paramString == null)
      return nullValue(); 
    put((JsonElement)new JsonPrimitive(paramString));
    return this;
  }
  
  @CanIgnoreReturnValue
  public JsonWriter value(boolean paramBoolean) throws IOException {
    put((JsonElement)new JsonPrimitive(Boolean.valueOf(paramBoolean)));
    return this;
  }
  
  @CanIgnoreReturnValue
  public JsonWriter value(Boolean paramBoolean) throws IOException {
    if (paramBoolean == null)
      return nullValue(); 
    put((JsonElement)new JsonPrimitive(paramBoolean));
    return this;
  }
  
  @CanIgnoreReturnValue
  public JsonWriter value(float paramFloat) throws IOException {
    if (!isLenient() && (Float.isNaN(paramFloat) || Float.isInfinite(paramFloat)))
      throw new IllegalArgumentException("JSON forbids NaN and infinities: " + paramFloat); 
    put((JsonElement)new JsonPrimitive(Float.valueOf(paramFloat)));
    return this;
  }
  
  @CanIgnoreReturnValue
  public JsonWriter value(double paramDouble) throws IOException {
    if (!isLenient() && (Double.isNaN(paramDouble) || Double.isInfinite(paramDouble)))
      throw new IllegalArgumentException("JSON forbids NaN and infinities: " + paramDouble); 
    put((JsonElement)new JsonPrimitive(Double.valueOf(paramDouble)));
    return this;
  }
  
  @CanIgnoreReturnValue
  public JsonWriter value(long paramLong) throws IOException {
    put((JsonElement)new JsonPrimitive(Long.valueOf(paramLong)));
    return this;
  }
  
  @CanIgnoreReturnValue
  public JsonWriter value(Number paramNumber) throws IOException {
    if (paramNumber == null)
      return nullValue(); 
    if (!isLenient()) {
      double d = paramNumber.doubleValue();
      if (Double.isNaN(d) || Double.isInfinite(d))
        throw new IllegalArgumentException("JSON forbids NaN and infinities: " + paramNumber); 
    } 
    put((JsonElement)new JsonPrimitive(paramNumber));
    return this;
  }
  
  @CanIgnoreReturnValue
  public JsonWriter nullValue() throws IOException {
    put((JsonElement)JsonNull.INSTANCE);
    return this;
  }
  
  public JsonWriter jsonValue(String paramString) throws IOException {
    throw new UnsupportedOperationException();
  }
  
  public void flush() throws IOException {}
  
  public void close() throws IOException {
    if (!this.stack.isEmpty())
      throw new IOException("Incomplete document"); 
    this.stack.add(SENTINEL_CLOSED);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\bind\JsonTreeWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */