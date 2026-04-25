package com.google.gson.internal;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;
import java.io.EOFException;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

public final class Streams {
  private Streams() {
    throw new UnsupportedOperationException();
  }
  
  public static JsonElement parse(JsonReader paramJsonReader) throws JsonParseException {
    boolean bool = true;
    try {
      JsonToken jsonToken = paramJsonReader.peek();
      bool = false;
      return (JsonElement)TypeAdapters.JSON_ELEMENT.read(paramJsonReader);
    } catch (EOFException eOFException) {
      if (bool)
        return (JsonElement)JsonNull.INSTANCE; 
      throw new JsonSyntaxException(eOFException);
    } catch (MalformedJsonException malformedJsonException) {
      throw new JsonSyntaxException(malformedJsonException);
    } catch (IOException iOException) {
      throw new JsonIOException(iOException);
    } catch (NumberFormatException numberFormatException) {
      throw new JsonSyntaxException(numberFormatException);
    } 
  }
  
  public static void write(JsonElement paramJsonElement, JsonWriter paramJsonWriter) throws IOException {
    TypeAdapters.JSON_ELEMENT.write(paramJsonWriter, paramJsonElement);
  }
  
  public static Writer writerForAppendable(Appendable paramAppendable) {
    return (paramAppendable instanceof Writer) ? (Writer)paramAppendable : new AppendableWriter(paramAppendable);
  }
  
  static {
  
  }
  
  private static final class AppendableWriter extends Writer {
    private final Appendable appendable;
    
    private final CurrentWrite currentWrite = new CurrentWrite();
    
    AppendableWriter(Appendable param1Appendable) {
      this.appendable = param1Appendable;
    }
    
    public void write(char[] param1ArrayOfchar, int param1Int1, int param1Int2) throws IOException {
      this.currentWrite.setChars(param1ArrayOfchar);
      this.appendable.append(this.currentWrite, param1Int1, param1Int1 + param1Int2);
    }
    
    public void flush() {}
    
    public void close() {}
    
    public void write(int param1Int) throws IOException {
      this.appendable.append((char)param1Int);
    }
    
    public void write(String param1String, int param1Int1, int param1Int2) throws IOException {
      Objects.requireNonNull(param1String);
      this.appendable.append(param1String, param1Int1, param1Int1 + param1Int2);
    }
    
    public Writer append(CharSequence param1CharSequence) throws IOException {
      this.appendable.append(param1CharSequence);
      return this;
    }
    
    public Writer append(CharSequence param1CharSequence, int param1Int1, int param1Int2) throws IOException {
      this.appendable.append(param1CharSequence, param1Int1, param1Int2);
      return this;
    }
    
    private static class CurrentWrite implements CharSequence {
      private char[] chars;
      
      private String cachedString;
      
      private CurrentWrite() {}
      
      void setChars(char[] param2ArrayOfchar) {
        this.chars = param2ArrayOfchar;
        this.cachedString = null;
      }
      
      public int length() {
        return this.chars.length;
      }
      
      public char charAt(int param2Int) {
        return this.chars[param2Int];
      }
      
      public CharSequence subSequence(int param2Int1, int param2Int2) {
        return new String(this.chars, param2Int1, param2Int2 - param2Int1);
      }
      
      public String toString() {
        if (this.cachedString == null)
          this.cachedString = new String(this.chars); 
        return this.cachedString;
      }
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\Streams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */