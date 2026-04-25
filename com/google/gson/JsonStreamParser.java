package com.google.gson;

import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class JsonStreamParser implements Iterator<JsonElement> {
  private final JsonReader parser;
  
  private final Object lock;
  
  public JsonStreamParser(String paramString) {
    this(new StringReader(paramString));
  }
  
  public JsonStreamParser(Reader paramReader) {
    this.parser = new JsonReader(paramReader);
    this.parser.setStrictness(Strictness.LENIENT);
    this.lock = new Object();
  }
  
  public JsonElement next() throws JsonParseException {
    if (!hasNext())
      throw new NoSuchElementException(); 
    try {
      return Streams.parse(this.parser);
    } catch (StackOverflowError stackOverflowError) {
      throw new JsonParseException("Failed parsing JSON source to Json", stackOverflowError);
    } catch (OutOfMemoryError outOfMemoryError) {
      throw new JsonParseException("Failed parsing JSON source to Json", outOfMemoryError);
    } 
  }
  
  public boolean hasNext() {
    synchronized (this.lock) {
      return (this.parser.peek() != JsonToken.END_DOCUMENT);
    } 
  }
  
  public void remove() {
    throw new UnsupportedOperationException();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\JsonStreamParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */