package com.google.gson;

import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.internal.NumberLimits;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.math.BigDecimal;

public enum ToNumberPolicy implements ToNumberStrategy {
  DOUBLE {
    public Double readNumber(JsonReader param1JsonReader) throws IOException {
      return Double.valueOf(param1JsonReader.nextDouble());
    }
    
    static {
    
    }
  },
  LAZILY_PARSED_NUMBER {
    public Number readNumber(JsonReader param1JsonReader) throws IOException {
      return (Number)new LazilyParsedNumber(param1JsonReader.nextString());
    }
    
    static {
    
    }
  },
  LONG_OR_DOUBLE {
    public Number readNumber(JsonReader param1JsonReader) throws IOException, JsonParseException {
      String str = param1JsonReader.nextString();
      if (str.indexOf('.') >= 0)
        return parseAsDouble(str, param1JsonReader); 
      try {
        return Long.valueOf(Long.parseLong(str));
      } catch (NumberFormatException numberFormatException) {
        return parseAsDouble(str, param1JsonReader);
      } 
    }
    
    private Number parseAsDouble(String param1String, JsonReader param1JsonReader) throws IOException {
      try {
        Double double_ = Double.valueOf(param1String);
        if ((double_.isInfinite() || double_.isNaN()) && !param1JsonReader.isLenient())
          throw new MalformedJsonException("JSON forbids NaN and infinities: " + double_ + "; at path " + param1JsonReader.getPreviousPath()); 
        return double_;
      } catch (NumberFormatException numberFormatException) {
        throw new JsonParseException("Cannot parse " + param1String + "; at path " + param1JsonReader.getPreviousPath(), numberFormatException);
      } 
    }
    
    static {
    
    }
  },
  BIG_DECIMAL {
    public BigDecimal readNumber(JsonReader param1JsonReader) throws IOException {
      String str = param1JsonReader.nextString();
      try {
        return NumberLimits.parseBigDecimal(str);
      } catch (NumberFormatException numberFormatException) {
        throw new JsonParseException("Cannot parse " + str + "; at path " + param1JsonReader.getPreviousPath(), numberFormatException);
      } 
    }
    
    static {
    
    }
  };
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\ToNumberPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */