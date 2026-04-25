package io.sentry;

import io.sentry.vendor.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Writer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class JsonObjectWriter implements ObjectWriter {
  @NotNull
  private final JsonWriter jsonWriter;
  
  @NotNull
  private final JsonObjectSerializer jsonObjectSerializer;
  
  public JsonObjectWriter(@NotNull Writer paramWriter, int paramInt) {
    this.jsonWriter = new JsonWriter(paramWriter);
    this.jsonObjectSerializer = new JsonObjectSerializer(paramInt);
  }
  
  public JsonObjectWriter beginArray() throws IOException {
    this.jsonWriter.beginArray();
    return this;
  }
  
  public JsonObjectWriter endArray() throws IOException {
    this.jsonWriter.endArray();
    return this;
  }
  
  public JsonObjectWriter beginObject() throws IOException {
    this.jsonWriter.beginObject();
    return this;
  }
  
  public JsonObjectWriter endObject() throws IOException {
    this.jsonWriter.endObject();
    return this;
  }
  
  public JsonObjectWriter name(@NotNull String paramString) throws IOException {
    this.jsonWriter.name(paramString);
    return this;
  }
  
  public JsonObjectWriter value(@Nullable String paramString) throws IOException {
    this.jsonWriter.value(paramString);
    return this;
  }
  
  public JsonObjectWriter nullValue() throws IOException {
    this.jsonWriter.nullValue();
    return this;
  }
  
  public JsonObjectWriter value(boolean paramBoolean) throws IOException {
    this.jsonWriter.value(paramBoolean);
    return this;
  }
  
  public JsonObjectWriter value(@Nullable Boolean paramBoolean) throws IOException {
    this.jsonWriter.value(paramBoolean);
    return this;
  }
  
  public JsonObjectWriter value(double paramDouble) throws IOException {
    this.jsonWriter.value(paramDouble);
    return this;
  }
  
  public JsonObjectWriter value(long paramLong) throws IOException {
    this.jsonWriter.value(paramLong);
    return this;
  }
  
  public JsonObjectWriter value(@Nullable Number paramNumber) throws IOException {
    this.jsonWriter.value(paramNumber);
    return this;
  }
  
  public JsonObjectWriter value(@NotNull ILogger paramILogger, @Nullable Object paramObject) throws IOException {
    this.jsonObjectSerializer.serialize(this, paramILogger, paramObject);
    return this;
  }
  
  public void setIndent(@NotNull String paramString) {
    this.jsonWriter.setIndent(paramString);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\JsonObjectWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */