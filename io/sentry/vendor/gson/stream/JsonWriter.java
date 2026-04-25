package io.sentry.vendor.gson.stream;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

@Internal
public class JsonWriter implements Closeable, Flushable {
  private static final String[] REPLACEMENT_CHARS = new String[128];
  
  private static final String[] HTML_SAFE_REPLACEMENT_CHARS = (String[])REPLACEMENT_CHARS.clone();
  
  private final Writer out;
  
  private int[] stack = new int[32];
  
  private int stackSize = 0;
  
  private String indent;
  
  private String separator;
  
  private boolean lenient;
  
  private boolean htmlSafe;
  
  private String deferredName;
  
  private boolean serializeNulls;
  
  public JsonWriter(Writer paramWriter) {
    push(6);
    this.separator = ":";
    this.serializeNulls = true;
    if (paramWriter == null)
      throw new NullPointerException("out == null"); 
    this.out = paramWriter;
  }
  
  public final void setIndent(String paramString) {
    if (paramString.length() == 0) {
      this.indent = null;
      this.separator = ":";
    } else {
      this.indent = paramString;
      this.separator = ": ";
    } 
  }
  
  public final void setLenient(boolean paramBoolean) {
    this.lenient = paramBoolean;
  }
  
  public boolean isLenient() {
    return this.lenient;
  }
  
  public final void setHtmlSafe(boolean paramBoolean) {
    this.htmlSafe = paramBoolean;
  }
  
  public final boolean isHtmlSafe() {
    return this.htmlSafe;
  }
  
  public final void setSerializeNulls(boolean paramBoolean) {
    this.serializeNulls = paramBoolean;
  }
  
  public final boolean getSerializeNulls() {
    return this.serializeNulls;
  }
  
  public JsonWriter beginArray() throws IOException {
    writeDeferredName();
    return open(1, '[');
  }
  
  public JsonWriter endArray() throws IOException {
    return close(1, 2, ']');
  }
  
  public JsonWriter beginObject() throws IOException {
    writeDeferredName();
    return open(3, '{');
  }
  
  public JsonWriter endObject() throws IOException {
    return close(3, 5, '}');
  }
  
  private JsonWriter open(int paramInt, char paramChar) throws IOException {
    beforeValue();
    push(paramInt);
    this.out.write(paramChar);
    return this;
  }
  
  private JsonWriter close(int paramInt1, int paramInt2, char paramChar) throws IOException {
    int i = peek();
    if (i != paramInt2 && i != paramInt1)
      throw new IllegalStateException("Nesting problem."); 
    if (this.deferredName != null)
      throw new IllegalStateException("Dangling name: " + this.deferredName); 
    this.stackSize--;
    if (i == paramInt2)
      newline(); 
    this.out.write(paramChar);
    return this;
  }
  
  private void push(int paramInt) {
    if (this.stackSize == this.stack.length)
      this.stack = Arrays.copyOf(this.stack, this.stackSize * 2); 
    this.stack[this.stackSize++] = paramInt;
  }
  
  private int peek() {
    if (this.stackSize == 0)
      throw new IllegalStateException("JsonWriter is closed."); 
    return this.stack[this.stackSize - 1];
  }
  
  private void replaceTop(int paramInt) {
    this.stack[this.stackSize - 1] = paramInt;
  }
  
  public JsonWriter name(String paramString) throws IOException {
    if (paramString == null)
      throw new NullPointerException("name == null"); 
    if (this.deferredName != null)
      throw new IllegalStateException(); 
    if (this.stackSize == 0)
      throw new IllegalStateException("JsonWriter is closed."); 
    this.deferredName = paramString;
    return this;
  }
  
  private void writeDeferredName() throws IOException {
    if (this.deferredName != null) {
      beforeName();
      string(this.deferredName);
      this.deferredName = null;
    } 
  }
  
  public JsonWriter value(@Nullable String paramString) throws IOException {
    if (paramString == null)
      return nullValue(); 
    writeDeferredName();
    beforeValue();
    string(paramString);
    return this;
  }
  
  public JsonWriter jsonValue(@Nullable String paramString) throws IOException {
    if (paramString == null)
      return nullValue(); 
    writeDeferredName();
    beforeValue();
    this.out.append(paramString);
    return this;
  }
  
  public JsonWriter nullValue() throws IOException {
    if (this.deferredName != null)
      if (this.serializeNulls) {
        writeDeferredName();
      } else {
        this.deferredName = null;
        return this;
      }  
    beforeValue();
    this.out.write("null");
    return this;
  }
  
  public JsonWriter value(boolean paramBoolean) throws IOException {
    writeDeferredName();
    beforeValue();
    this.out.write(paramBoolean ? "true" : "false");
    return this;
  }
  
  public JsonWriter value(@Nullable Boolean paramBoolean) throws IOException {
    if (paramBoolean == null)
      return nullValue(); 
    writeDeferredName();
    beforeValue();
    this.out.write(paramBoolean.booleanValue() ? "true" : "false");
    return this;
  }
  
  public JsonWriter value(double paramDouble) throws IOException {
    writeDeferredName();
    if (!this.lenient && (Double.isNaN(paramDouble) || Double.isInfinite(paramDouble)))
      throw new IllegalArgumentException("Numeric values must be finite, but was " + paramDouble); 
    beforeValue();
    this.out.append(Double.toString(paramDouble));
    return this;
  }
  
  public JsonWriter value(long paramLong) throws IOException {
    writeDeferredName();
    beforeValue();
    this.out.write(Long.toString(paramLong));
    return this;
  }
  
  public JsonWriter value(@Nullable Number paramNumber) throws IOException {
    if (paramNumber == null)
      return nullValue(); 
    writeDeferredName();
    String str = paramNumber.toString();
    if (!this.lenient && (str.equals("-Infinity") || str.equals("Infinity") || str.equals("NaN")))
      throw new IllegalArgumentException("Numeric values must be finite, but was " + paramNumber); 
    beforeValue();
    this.out.append(str);
    return this;
  }
  
  public void flush() throws IOException {
    if (this.stackSize == 0)
      throw new IllegalStateException("JsonWriter is closed."); 
    this.out.flush();
  }
  
  public void close() throws IOException {
    this.out.close();
    int i = this.stackSize;
    if (i > 1 || (i == 1 && this.stack[i - 1] != 7))
      throw new IOException("Incomplete document"); 
    this.stackSize = 0;
  }
  
  private void string(String paramString) throws IOException {
    String[] arrayOfString = this.htmlSafe ? HTML_SAFE_REPLACEMENT_CHARS : REPLACEMENT_CHARS;
    this.out.write(34);
    int i = 0;
    int j = paramString.length();
    for (byte b = 0; b < j; b++) {
      String str;
      char c = paramString.charAt(b);
      if (c < '') {
        str = arrayOfString[c];
        if (str == null)
          continue; 
      } else if (c == ' ') {
        str = "\\u2028";
      } else if (c == ' ') {
        str = "\\u2029";
      } else {
        continue;
      } 
      if (i < b)
        this.out.write(paramString, i, b - i); 
      this.out.write(str);
      i = b + 1;
      continue;
    } 
    if (i < j)
      this.out.write(paramString, i, j - i); 
    this.out.write(34);
  }
  
  private void newline() throws IOException {
    if (this.indent == null)
      return; 
    this.out.write(10);
    byte b = 1;
    int i = this.stackSize;
    while (b < i) {
      this.out.write(this.indent);
      b++;
    } 
  }
  
  private void beforeName() throws IOException {
    int i = peek();
    if (i == 5) {
      this.out.write(44);
    } else if (i != 3) {
      throw new IllegalStateException("Nesting problem.");
    } 
    newline();
    replaceTop(4);
  }
  
  private void beforeValue() throws IOException {
    switch (peek()) {
      case 7:
        if (!this.lenient)
          throw new IllegalStateException("JSON must have only one top-level value."); 
      case 6:
        replaceTop(7);
        return;
      case 1:
        replaceTop(2);
        newline();
        return;
      case 2:
        this.out.append(',');
        newline();
        return;
      case 4:
        this.out.append(this.separator);
        replaceTop(5);
        return;
    } 
    throw new IllegalStateException("Nesting problem.");
  }
  
  static {
    HTML_SAFE_REPLACEMENT_CHARS[60] = "\\u003c";
    HTML_SAFE_REPLACEMENT_CHARS[62] = "\\u003e";
    HTML_SAFE_REPLACEMENT_CHARS[38] = "\\u0026";
    HTML_SAFE_REPLACEMENT_CHARS[61] = "\\u003d";
    HTML_SAFE_REPLACEMENT_CHARS[39] = "\\u0027";
  }
  
  static {
    for (byte b = 0; b <= 31; b++) {
      REPLACEMENT_CHARS[b] = String.format("\\u%04x", new Object[] { Integer.valueOf(b) });
    } 
    REPLACEMENT_CHARS[34] = "\\\"";
    REPLACEMENT_CHARS[92] = "\\\\";
    REPLACEMENT_CHARS[9] = "\\t";
    REPLACEMENT_CHARS[8] = "\\b";
    REPLACEMENT_CHARS[10] = "\\n";
    REPLACEMENT_CHARS[13] = "\\r";
    REPLACEMENT_CHARS[12] = "\\f";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\vendor\gson\stream\JsonWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */