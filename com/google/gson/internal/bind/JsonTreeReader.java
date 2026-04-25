package com.google.gson.internal.bind;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public final class JsonTreeReader extends JsonReader {
  private static final Reader UNREADABLE_READER = new Reader() {
      public int read(char[] param1ArrayOfchar, int param1Int1, int param1Int2) {
        throw new AssertionError();
      }
      
      public void close() {
        throw new AssertionError();
      }
      
      static {
      
      }
    };
  
  private static final Object SENTINEL_CLOSED = new Object();
  
  private Object[] stack = new Object[32];
  
  private int stackSize = 0;
  
  private String[] pathNames = new String[32];
  
  private int[] pathIndices = new int[32];
  
  public JsonTreeReader(JsonElement paramJsonElement) {
    super(UNREADABLE_READER);
    push(paramJsonElement);
  }
  
  public void beginArray() throws IOException {
    expect(JsonToken.BEGIN_ARRAY);
    JsonArray jsonArray = (JsonArray)peekStack();
    push(jsonArray.iterator());
    this.pathIndices[this.stackSize - 1] = 0;
  }
  
  public void endArray() throws IOException {
    expect(JsonToken.END_ARRAY);
    popStack();
    popStack();
    if (this.stackSize > 0)
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1; 
  }
  
  public void beginObject() throws IOException {
    expect(JsonToken.BEGIN_OBJECT);
    JsonObject jsonObject = (JsonObject)peekStack();
    push(jsonObject.entrySet().iterator());
  }
  
  public void endObject() throws IOException {
    expect(JsonToken.END_OBJECT);
    this.pathNames[this.stackSize - 1] = null;
    popStack();
    popStack();
    if (this.stackSize > 0)
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1; 
  }
  
  public boolean hasNext() throws IOException {
    JsonToken jsonToken = peek();
    return (jsonToken != JsonToken.END_OBJECT && jsonToken != JsonToken.END_ARRAY && jsonToken != JsonToken.END_DOCUMENT);
  }
  
  public JsonToken peek() throws IOException {
    if (this.stackSize == 0)
      return JsonToken.END_DOCUMENT; 
    Object object = peekStack();
    if (object instanceof Iterator) {
      boolean bool = this.stack[this.stackSize - 2] instanceof JsonObject;
      Iterator iterator = (Iterator)object;
      if (iterator.hasNext()) {
        if (bool)
          return JsonToken.NAME; 
        push(iterator.next());
        return peek();
      } 
      return bool ? JsonToken.END_OBJECT : JsonToken.END_ARRAY;
    } 
    if (object instanceof JsonObject)
      return JsonToken.BEGIN_OBJECT; 
    if (object instanceof JsonArray)
      return JsonToken.BEGIN_ARRAY; 
    if (object instanceof JsonPrimitive) {
      JsonPrimitive jsonPrimitive = (JsonPrimitive)object;
      if (jsonPrimitive.isString())
        return JsonToken.STRING; 
      if (jsonPrimitive.isBoolean())
        return JsonToken.BOOLEAN; 
      if (jsonPrimitive.isNumber())
        return JsonToken.NUMBER; 
      throw new AssertionError();
    } 
    if (object instanceof com.google.gson.JsonNull)
      return JsonToken.NULL; 
    if (object == SENTINEL_CLOSED)
      throw new IllegalStateException("JsonReader is closed"); 
    throw new MalformedJsonException("Custom JsonElement subclass " + object.getClass().getName() + " is not supported");
  }
  
  private Object peekStack() {
    return this.stack[this.stackSize - 1];
  }
  
  @CanIgnoreReturnValue
  private Object popStack() {
    Object object = this.stack[--this.stackSize];
    this.stack[this.stackSize] = null;
    return object;
  }
  
  private void expect(JsonToken paramJsonToken) throws IOException {
    if (peek() != paramJsonToken)
      throw new IllegalStateException("Expected " + paramJsonToken + " but was " + peek() + locationString()); 
  }
  
  private String nextName(boolean paramBoolean) throws IOException {
    expect(JsonToken.NAME);
    Iterator<Map.Entry> iterator = (Iterator)peekStack();
    Map.Entry entry = iterator.next();
    String str = (String)entry.getKey();
    this.pathNames[this.stackSize - 1] = paramBoolean ? "<skipped>" : str;
    push(entry.getValue());
    return str;
  }
  
  public String nextName() throws IOException {
    return nextName(false);
  }
  
  public String nextString() throws IOException {
    JsonToken jsonToken = peek();
    if (jsonToken != JsonToken.STRING && jsonToken != JsonToken.NUMBER)
      throw new IllegalStateException("Expected " + JsonToken.STRING + " but was " + jsonToken + locationString()); 
    String str = ((JsonPrimitive)popStack()).getAsString();
    if (this.stackSize > 0)
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1; 
    return str;
  }
  
  public boolean nextBoolean() throws IOException {
    expect(JsonToken.BOOLEAN);
    boolean bool = ((JsonPrimitive)popStack()).getAsBoolean();
    if (this.stackSize > 0)
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1; 
    return bool;
  }
  
  public void nextNull() throws IOException {
    expect(JsonToken.NULL);
    popStack();
    if (this.stackSize > 0)
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1; 
  }
  
  public double nextDouble() throws IOException {
    JsonToken jsonToken = peek();
    if (jsonToken != JsonToken.NUMBER && jsonToken != JsonToken.STRING)
      throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + jsonToken + locationString()); 
    double d = ((JsonPrimitive)peekStack()).getAsDouble();
    if (!isLenient() && (Double.isNaN(d) || Double.isInfinite(d)))
      throw new MalformedJsonException("JSON forbids NaN and infinities: " + d); 
    popStack();
    if (this.stackSize > 0)
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1; 
    return d;
  }
  
  public long nextLong() throws IOException {
    JsonToken jsonToken = peek();
    if (jsonToken != JsonToken.NUMBER && jsonToken != JsonToken.STRING)
      throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + jsonToken + locationString()); 
    long l = ((JsonPrimitive)peekStack()).getAsLong();
    popStack();
    if (this.stackSize > 0)
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1; 
    return l;
  }
  
  public int nextInt() throws IOException {
    JsonToken jsonToken = peek();
    if (jsonToken != JsonToken.NUMBER && jsonToken != JsonToken.STRING)
      throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + jsonToken + locationString()); 
    int i = ((JsonPrimitive)peekStack()).getAsInt();
    popStack();
    if (this.stackSize > 0)
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1; 
    return i;
  }
  
  JsonElement nextJsonElement() throws IOException {
    JsonToken jsonToken = peek();
    if (jsonToken == JsonToken.NAME || jsonToken == JsonToken.END_ARRAY || jsonToken == JsonToken.END_OBJECT || jsonToken == JsonToken.END_DOCUMENT)
      throw new IllegalStateException("Unexpected " + jsonToken + " when reading a JsonElement."); 
    JsonElement jsonElement = (JsonElement)peekStack();
    skipValue();
    return jsonElement;
  }
  
  public void close() throws IOException {
    this.stack = new Object[] { SENTINEL_CLOSED };
    this.stackSize = 1;
  }
  
  public void skipValue() throws IOException {
    String str;
    JsonToken jsonToken = peek();
    switch (jsonToken) {
      case NAME:
        str = nextName(true);
      case END_ARRAY:
        endArray();
      case END_OBJECT:
        endObject();
      case END_DOCUMENT:
        return;
    } 
    popStack();
    if (this.stackSize > 0)
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1; 
  }
  
  public String toString() {
    return getClass().getSimpleName() + locationString();
  }
  
  public void promoteNameToValue() throws IOException {
    expect(JsonToken.NAME);
    Iterator<Map.Entry> iterator = (Iterator)peekStack();
    Map.Entry entry = iterator.next();
    push(entry.getValue());
    push(new JsonPrimitive((String)entry.getKey()));
  }
  
  private void push(Object paramObject) {
    if (this.stackSize == this.stack.length) {
      int i = this.stackSize * 2;
      this.stack = Arrays.copyOf(this.stack, i);
      this.pathIndices = Arrays.copyOf(this.pathIndices, i);
      this.pathNames = Arrays.<String>copyOf(this.pathNames, i);
    } 
    this.stack[this.stackSize++] = paramObject;
  }
  
  private String getPath(boolean paramBoolean) {
    StringBuilder stringBuilder = (new StringBuilder()).append('$');
    for (byte b = 0; b < this.stackSize; b++) {
      if (this.stack[b] instanceof JsonArray) {
        if (++b < this.stackSize && this.stack[b] instanceof Iterator) {
          int i = this.pathIndices[b];
          if (paramBoolean && i > 0 && (b == this.stackSize - 1 || b == this.stackSize - 2))
            i--; 
          stringBuilder.append('[').append(i).append(']');
        } 
      } else if (this.stack[b] instanceof JsonObject && ++b < this.stackSize && this.stack[b] instanceof Iterator) {
        stringBuilder.append('.');
        if (this.pathNames[b] != null)
          stringBuilder.append(this.pathNames[b]); 
      } 
    } 
    return stringBuilder.toString();
  }
  
  public String getPath() {
    return getPath(false);
  }
  
  public String getPreviousPath() {
    return getPath(true);
  }
  
  private String locationString() {
    return " at path " + getPath();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\bind\JsonTreeReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */