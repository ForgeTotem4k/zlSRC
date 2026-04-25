package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.ToNumberPolicy;
import com.google.gson.ToNumberStrategy;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

public final class NumberTypeAdapter extends TypeAdapter<Number> {
  private static final TypeAdapterFactory LAZILY_PARSED_NUMBER_FACTORY = newFactory((ToNumberStrategy)ToNumberPolicy.LAZILY_PARSED_NUMBER);
  
  private final ToNumberStrategy toNumberStrategy;
  
  private NumberTypeAdapter(ToNumberStrategy paramToNumberStrategy) {
    this.toNumberStrategy = paramToNumberStrategy;
  }
  
  private static TypeAdapterFactory newFactory(ToNumberStrategy paramToNumberStrategy) {
    final NumberTypeAdapter adapter = new NumberTypeAdapter(paramToNumberStrategy);
    return new TypeAdapterFactory() {
        public <T> TypeAdapter<T> create(Gson param1Gson, TypeToken<T> param1TypeToken) {
          return (param1TypeToken.getRawType() == Number.class) ? adapter : null;
        }
      };
  }
  
  public static TypeAdapterFactory getFactory(ToNumberStrategy paramToNumberStrategy) {
    return (paramToNumberStrategy == ToNumberPolicy.LAZILY_PARSED_NUMBER) ? LAZILY_PARSED_NUMBER_FACTORY : newFactory(paramToNumberStrategy);
  }
  
  public Number read(JsonReader paramJsonReader) throws IOException {
    JsonToken jsonToken = paramJsonReader.peek();
    switch (jsonToken) {
      case NULL:
        paramJsonReader.nextNull();
        return null;
      case NUMBER:
      case STRING:
        return this.toNumberStrategy.readNumber(paramJsonReader);
    } 
    throw new JsonSyntaxException("Expecting number, got: " + jsonToken + "; at path " + paramJsonReader.getPath());
  }
  
  public void write(JsonWriter paramJsonWriter, Number paramNumber) throws IOException {
    paramJsonWriter.value(paramNumber);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\bind\NumberTypeAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */