package com.google.gson;

import com.google.errorprone.annotations.InlineMe;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public final class JsonParser {
  public static JsonElement parseString(String paramString) throws JsonSyntaxException {
    return parseReader(new StringReader(paramString));
  }
  
  public static JsonElement parseReader(Reader paramReader) throws JsonIOException, JsonSyntaxException {
    try {
      JsonReader jsonReader = new JsonReader(paramReader);
      JsonElement jsonElement = parseReader(jsonReader);
      if (!jsonElement.isJsonNull() && jsonReader.peek() != JsonToken.END_DOCUMENT)
        throw new JsonSyntaxException("Did not consume the entire document."); 
      return jsonElement;
    } catch (MalformedJsonException malformedJsonException) {
      throw new JsonSyntaxException(malformedJsonException);
    } catch (IOException iOException) {
      throw new JsonIOException(iOException);
    } catch (NumberFormatException numberFormatException) {
      throw new JsonSyntaxException(numberFormatException);
    } 
  }
  
  public static JsonElement parseReader(JsonReader paramJsonReader) throws JsonIOException, JsonSyntaxException {
    Strictness strictness = paramJsonReader.getStrictness();
    if (strictness == Strictness.LEGACY_STRICT)
      paramJsonReader.setStrictness(Strictness.LENIENT); 
    try {
      return Streams.parse(paramJsonReader);
    } catch (StackOverflowError stackOverflowError) {
      throw new JsonParseException("Failed parsing JSON source: " + paramJsonReader + " to Json", stackOverflowError);
    } catch (OutOfMemoryError outOfMemoryError) {
      throw new JsonParseException("Failed parsing JSON source: " + paramJsonReader + " to Json", outOfMemoryError);
    } finally {
      paramJsonReader.setStrictness(strictness);
    } 
  }
  
  @Deprecated
  @InlineMe(replacement = "JsonParser.parseString(json)", imports = {"com.google.gson.JsonParser"})
  public JsonElement parse(String paramString) throws JsonSyntaxException {
    return parseString(paramString);
  }
  
  @Deprecated
  @InlineMe(replacement = "JsonParser.parseReader(json)", imports = {"com.google.gson.JsonParser"})
  public JsonElement parse(Reader paramReader) throws JsonIOException, JsonSyntaxException {
    return parseReader(paramReader);
  }
  
  @Deprecated
  @InlineMe(replacement = "JsonParser.parseReader(json)", imports = {"com.google.gson.JsonParser"})
  public JsonElement parse(JsonReader paramJsonReader) throws JsonIOException, JsonSyntaxException {
    return parseReader(paramJsonReader);
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\JsonParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */