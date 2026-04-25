package com.google.gson.internal.bind;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Map;

class JsonElementTypeAdapter extends TypeAdapter<JsonElement> {
  static final JsonElementTypeAdapter ADAPTER = new JsonElementTypeAdapter();
  
  private JsonElement tryBeginNesting(JsonReader paramJsonReader, JsonToken paramJsonToken) throws IOException {
    switch (paramJsonToken) {
      case BEGIN_ARRAY:
        paramJsonReader.beginArray();
        return (JsonElement)new JsonArray();
      case BEGIN_OBJECT:
        paramJsonReader.beginObject();
        return (JsonElement)new JsonObject();
    } 
    return null;
  }
  
  private JsonElement readTerminal(JsonReader paramJsonReader, JsonToken paramJsonToken) throws IOException {
    String str;
    switch (paramJsonToken) {
      case STRING:
        return (JsonElement)new JsonPrimitive(paramJsonReader.nextString());
      case NUMBER:
        str = paramJsonReader.nextString();
        return (JsonElement)new JsonPrimitive((Number)new LazilyParsedNumber(str));
      case BOOLEAN:
        return (JsonElement)new JsonPrimitive(Boolean.valueOf(paramJsonReader.nextBoolean()));
      case NULL:
        paramJsonReader.nextNull();
        return (JsonElement)JsonNull.INSTANCE;
    } 
    throw new IllegalStateException("Unexpected token: " + paramJsonToken);
  }
  
  public JsonElement read(JsonReader paramJsonReader) throws IOException {
    if (paramJsonReader instanceof JsonTreeReader)
      return ((JsonTreeReader)paramJsonReader).nextJsonElement(); 
    JsonToken jsonToken = paramJsonReader.peek();
    JsonElement jsonElement = tryBeginNesting(paramJsonReader, jsonToken);
    if (jsonElement == null)
      return readTerminal(paramJsonReader, jsonToken); 
    ArrayDeque<JsonElement> arrayDeque = new ArrayDeque();
    while (true) {
      while (paramJsonReader.hasNext()) {
        String str = null;
        if (jsonElement instanceof JsonObject)
          str = paramJsonReader.nextName(); 
        jsonToken = paramJsonReader.peek();
        JsonElement jsonElement1 = tryBeginNesting(paramJsonReader, jsonToken);
        boolean bool = (jsonElement1 != null) ? true : false;
        if (jsonElement1 == null)
          jsonElement1 = readTerminal(paramJsonReader, jsonToken); 
        if (jsonElement instanceof JsonArray) {
          ((JsonArray)jsonElement).add(jsonElement1);
        } else {
          ((JsonObject)jsonElement).add(str, jsonElement1);
        } 
        if (bool) {
          arrayDeque.addLast(jsonElement);
          jsonElement = jsonElement1;
        } 
      } 
      if (jsonElement instanceof JsonArray) {
        paramJsonReader.endArray();
      } else {
        paramJsonReader.endObject();
      } 
      if (arrayDeque.isEmpty())
        return jsonElement; 
      jsonElement = arrayDeque.removeLast();
    } 
  }
  
  public void write(JsonWriter paramJsonWriter, JsonElement paramJsonElement) throws IOException {
    if (paramJsonElement == null || paramJsonElement.isJsonNull()) {
      paramJsonWriter.nullValue();
    } else if (paramJsonElement.isJsonPrimitive()) {
      JsonPrimitive jsonPrimitive = paramJsonElement.getAsJsonPrimitive();
      if (jsonPrimitive.isNumber()) {
        paramJsonWriter.value(jsonPrimitive.getAsNumber());
      } else if (jsonPrimitive.isBoolean()) {
        paramJsonWriter.value(jsonPrimitive.getAsBoolean());
      } else {
        paramJsonWriter.value(jsonPrimitive.getAsString());
      } 
    } else if (paramJsonElement.isJsonArray()) {
      paramJsonWriter.beginArray();
      for (JsonElement jsonElement : paramJsonElement.getAsJsonArray())
        write(paramJsonWriter, jsonElement); 
      paramJsonWriter.endArray();
    } else if (paramJsonElement.isJsonObject()) {
      paramJsonWriter.beginObject();
      for (Map.Entry entry : paramJsonElement.getAsJsonObject().entrySet()) {
        paramJsonWriter.name((String)entry.getKey());
        write(paramJsonWriter, (JsonElement)entry.getValue());
      } 
      paramJsonWriter.endObject();
    } else {
      throw new IllegalArgumentException("Couldn't write " + paramJsonElement.getClass());
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\bind\JsonElementTypeAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */