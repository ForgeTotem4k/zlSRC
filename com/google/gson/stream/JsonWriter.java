package com.google.gson.stream;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.FormattingStyle;
import com.google.gson.Strictness;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

public class JsonWriter implements Closeable, Flushable {
  private static final Pattern VALID_JSON_NUMBER_PATTERN = Pattern.compile("-?(?:0|[1-9][0-9]*)(?:\\.[0-9]+)?(?:[eE][-+]?[0-9]+)?");
  
  private static final String[] REPLACEMENT_CHARS = new String[128];
  
  private static final String[] HTML_SAFE_REPLACEMENT_CHARS = (String[])REPLACEMENT_CHARS.clone();
  
  private final Writer out;
  
  private int[] stack = new int[32];
  
  private int stackSize = 0;
  
  private FormattingStyle formattingStyle;
  
  private String formattedColon;
  
  private String formattedComma;
  
  private boolean usesEmptyNewlineAndIndent;
  
  private Strictness strictness;
  
  private boolean htmlSafe;
  
  private String deferredName;
  
  private boolean serializeNulls;
  
  public JsonWriter(Writer paramWriter) {
    push(6);
    this.strictness = Strictness.LEGACY_STRICT;
    this.serializeNulls = true;
    this.out = Objects.<Writer>requireNonNull(paramWriter, "out == null");
    setFormattingStyle(FormattingStyle.COMPACT);
  }
  
  public final void setIndent(String paramString) {
    if (paramString.isEmpty()) {
      setFormattingStyle(FormattingStyle.COMPACT);
    } else {
      setFormattingStyle(FormattingStyle.PRETTY.withIndent(paramString));
    } 
  }
  
  public final void setFormattingStyle(FormattingStyle paramFormattingStyle) {
    this.formattingStyle = Objects.<FormattingStyle>requireNonNull(paramFormattingStyle);
    this.formattedComma = ",";
    if (this.formattingStyle.usesSpaceAfterSeparators()) {
      this.formattedColon = ": ";
      if (this.formattingStyle.getNewline().isEmpty())
        this.formattedComma = ", "; 
    } else {
      this.formattedColon = ":";
    } 
    this.usesEmptyNewlineAndIndent = (this.formattingStyle.getNewline().isEmpty() && this.formattingStyle.getIndent().isEmpty());
  }
  
  public final FormattingStyle getFormattingStyle() {
    return this.formattingStyle;
  }
  
  @Deprecated
  public final void setLenient(boolean paramBoolean) {
    setStrictness(paramBoolean ? Strictness.LENIENT : Strictness.LEGACY_STRICT);
  }
  
  public boolean isLenient() {
    return (this.strictness == Strictness.LENIENT);
  }
  
  public final void setStrictness(Strictness paramStrictness) {
    this.strictness = Objects.<Strictness>requireNonNull(paramStrictness);
  }
  
  public final Strictness getStrictness() {
    return this.strictness;
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
  
  @CanIgnoreReturnValue
  public JsonWriter beginArray() throws IOException {
    writeDeferredName();
    return openScope(1, '[');
  }
  
  @CanIgnoreReturnValue
  public JsonWriter endArray() throws IOException {
    return closeScope(1, 2, ']');
  }
  
  @CanIgnoreReturnValue
  public JsonWriter beginObject() throws IOException {
    writeDeferredName();
    return openScope(3, '{');
  }
  
  @CanIgnoreReturnValue
  public JsonWriter endObject() throws IOException {
    return closeScope(3, 5, '}');
  }
  
  @CanIgnoreReturnValue
  private JsonWriter openScope(int paramInt, char paramChar) throws IOException {
    beforeValue();
    push(paramInt);
    this.out.write(paramChar);
    return this;
  }
  
  @CanIgnoreReturnValue
  private JsonWriter closeScope(int paramInt1, int paramInt2, char paramChar) throws IOException {
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
  
  @CanIgnoreReturnValue
  public JsonWriter name(String paramString) throws IOException {
    Objects.requireNonNull(paramString, "name == null");
    if (this.deferredName != null)
      throw new IllegalStateException("Already wrote a name, expecting a value."); 
    int i = peek();
    if (i != 3 && i != 5)
      throw new IllegalStateException("Please begin an object before writing a name."); 
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
  
  @CanIgnoreReturnValue
  public JsonWriter value(String paramString) throws IOException {
    if (paramString == null)
      return nullValue(); 
    writeDeferredName();
    beforeValue();
    string(paramString);
    return this;
  }
  
  @CanIgnoreReturnValue
  public JsonWriter value(boolean paramBoolean) throws IOException {
    writeDeferredName();
    beforeValue();
    this.out.write(paramBoolean ? "true" : "false");
    return this;
  }
  
  @CanIgnoreReturnValue
  public JsonWriter value(Boolean paramBoolean) throws IOException {
    if (paramBoolean == null)
      return nullValue(); 
    writeDeferredName();
    beforeValue();
    this.out.write(paramBoolean.booleanValue() ? "true" : "false");
    return this;
  }
  
  @CanIgnoreReturnValue
  public JsonWriter value(float paramFloat) throws IOException {
    writeDeferredName();
    if (this.strictness != Strictness.LENIENT && (Float.isNaN(paramFloat) || Float.isInfinite(paramFloat)))
      throw new IllegalArgumentException("Numeric values must be finite, but was " + paramFloat); 
    beforeValue();
    this.out.append(Float.toString(paramFloat));
    return this;
  }
  
  @CanIgnoreReturnValue
  public JsonWriter value(double paramDouble) throws IOException {
    writeDeferredName();
    if (this.strictness != Strictness.LENIENT && (Double.isNaN(paramDouble) || Double.isInfinite(paramDouble)))
      throw new IllegalArgumentException("Numeric values must be finite, but was " + paramDouble); 
    beforeValue();
    this.out.append(Double.toString(paramDouble));
    return this;
  }
  
  @CanIgnoreReturnValue
  public JsonWriter value(long paramLong) throws IOException {
    writeDeferredName();
    beforeValue();
    this.out.write(Long.toString(paramLong));
    return this;
  }
  
  @CanIgnoreReturnValue
  public JsonWriter value(Number paramNumber) throws IOException {
    if (paramNumber == null)
      return nullValue(); 
    writeDeferredName();
    String str = paramNumber.toString();
    Class<?> clazz = paramNumber.getClass();
    if (!alwaysCreatesValidJsonNumber((Class)clazz))
      if (str.equals("-Infinity") || str.equals("Infinity") || str.equals("NaN")) {
        if (this.strictness != Strictness.LENIENT)
          throw new IllegalArgumentException("Numeric values must be finite, but was " + str); 
      } else if (clazz != Float.class && clazz != Double.class && !VALID_JSON_NUMBER_PATTERN.matcher(str).matches()) {
        throw new IllegalArgumentException("String created by " + clazz + " is not a valid JSON number: " + str);
      }  
    beforeValue();
    this.out.append(str);
    return this;
  }
  
  @CanIgnoreReturnValue
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
  
  @CanIgnoreReturnValue
  public JsonWriter jsonValue(String paramString) throws IOException {
    if (paramString == null)
      return nullValue(); 
    writeDeferredName();
    beforeValue();
    this.out.append(paramString);
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
  
  private static boolean alwaysCreatesValidJsonNumber(Class<? extends Number> paramClass) {
    return (paramClass == Integer.class || paramClass == Long.class || paramClass == Byte.class || paramClass == Short.class || paramClass == BigDecimal.class || paramClass == BigInteger.class || paramClass == AtomicInteger.class || paramClass == AtomicLong.class);
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
    if (this.usesEmptyNewlineAndIndent)
      return; 
    this.out.write(this.formattingStyle.getNewline());
    byte b = 1;
    int i = this.stackSize;
    while (b < i) {
      this.out.write(this.formattingStyle.getIndent());
      b++;
    } 
  }
  
  private void beforeName() throws IOException {
    int i = peek();
    if (i == 5) {
      this.out.write(this.formattedComma);
    } else if (i != 3) {
      throw new IllegalStateException("Nesting problem.");
    } 
    newline();
    replaceTop(4);
  }
  
  private void beforeValue() throws IOException {
    switch (peek()) {
      case 7:
        if (this.strictness != Strictness.LENIENT)
          throw new IllegalStateException("JSON must have only one top-level value."); 
      case 6:
        replaceTop(7);
        return;
      case 1:
        replaceTop(2);
        newline();
        return;
      case 2:
        this.out.append(this.formattedComma);
        newline();
        return;
      case 4:
        this.out.append(this.formattedColon);
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


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\stream\JsonWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */