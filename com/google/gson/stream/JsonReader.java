package com.google.gson.stream;

import com.google.gson.Strictness;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.TroubleshootingGuide;
import com.google.gson.internal.bind.JsonTreeReader;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Objects;

public class JsonReader implements Closeable {
  private static final long MIN_INCOMPLETE_INTEGER = -922337203685477580L;
  
  private static final int PEEKED_NONE = 0;
  
  private static final int PEEKED_BEGIN_OBJECT = 1;
  
  private static final int PEEKED_END_OBJECT = 2;
  
  private static final int PEEKED_BEGIN_ARRAY = 3;
  
  private static final int PEEKED_END_ARRAY = 4;
  
  private static final int PEEKED_TRUE = 5;
  
  private static final int PEEKED_FALSE = 6;
  
  private static final int PEEKED_NULL = 7;
  
  private static final int PEEKED_SINGLE_QUOTED = 8;
  
  private static final int PEEKED_DOUBLE_QUOTED = 9;
  
  private static final int PEEKED_UNQUOTED = 10;
  
  private static final int PEEKED_BUFFERED = 11;
  
  private static final int PEEKED_SINGLE_QUOTED_NAME = 12;
  
  private static final int PEEKED_DOUBLE_QUOTED_NAME = 13;
  
  private static final int PEEKED_UNQUOTED_NAME = 14;
  
  private static final int PEEKED_LONG = 15;
  
  private static final int PEEKED_NUMBER = 16;
  
  private static final int PEEKED_EOF = 17;
  
  private static final int NUMBER_CHAR_NONE = 0;
  
  private static final int NUMBER_CHAR_SIGN = 1;
  
  private static final int NUMBER_CHAR_DIGIT = 2;
  
  private static final int NUMBER_CHAR_DECIMAL = 3;
  
  private static final int NUMBER_CHAR_FRACTION_DIGIT = 4;
  
  private static final int NUMBER_CHAR_EXP_E = 5;
  
  private static final int NUMBER_CHAR_EXP_SIGN = 6;
  
  private static final int NUMBER_CHAR_EXP_DIGIT = 7;
  
  private final Reader in;
  
  private Strictness strictness = Strictness.LEGACY_STRICT;
  
  static final int DEFAULT_NESTING_LIMIT = 255;
  
  private int nestingLimit = 255;
  
  static final int BUFFER_SIZE = 1024;
  
  private final char[] buffer = new char[1024];
  
  private int pos = 0;
  
  private int limit = 0;
  
  private int lineNumber = 0;
  
  private int lineStart = 0;
  
  int peeked = 0;
  
  private long peekedLong;
  
  private int peekedNumberLength;
  
  private String peekedString;
  
  private int[] stack = new int[32];
  
  private int stackSize = 0;
  
  private String[] pathNames;
  
  private int[] pathIndices;
  
  public JsonReader(Reader paramReader) {
    this.stack[this.stackSize++] = 6;
    this.pathNames = new String[32];
    this.pathIndices = new int[32];
    this.in = Objects.<Reader>requireNonNull(paramReader, "in == null");
  }
  
  @Deprecated
  public final void setLenient(boolean paramBoolean) {
    setStrictness(paramBoolean ? Strictness.LENIENT : Strictness.LEGACY_STRICT);
  }
  
  public final boolean isLenient() {
    return (this.strictness == Strictness.LENIENT);
  }
  
  public final void setStrictness(Strictness paramStrictness) {
    Objects.requireNonNull(paramStrictness);
    this.strictness = paramStrictness;
  }
  
  public final Strictness getStrictness() {
    return this.strictness;
  }
  
  public final void setNestingLimit(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Invalid nesting limit: " + paramInt); 
    this.nestingLimit = paramInt;
  }
  
  public final int getNestingLimit() {
    return this.nestingLimit;
  }
  
  public void beginArray() throws IOException {
    int i = this.peeked;
    if (i == 0)
      i = doPeek(); 
    if (i == 3) {
      push(1);
      this.pathIndices[this.stackSize - 1] = 0;
      this.peeked = 0;
    } else {
      throw unexpectedTokenError("BEGIN_ARRAY");
    } 
  }
  
  public void endArray() throws IOException {
    int i = this.peeked;
    if (i == 0)
      i = doPeek(); 
    if (i == 4) {
      this.stackSize--;
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
      this.peeked = 0;
    } else {
      throw unexpectedTokenError("END_ARRAY");
    } 
  }
  
  public void beginObject() throws IOException {
    int i = this.peeked;
    if (i == 0)
      i = doPeek(); 
    if (i == 1) {
      push(3);
      this.peeked = 0;
    } else {
      throw unexpectedTokenError("BEGIN_OBJECT");
    } 
  }
  
  public void endObject() throws IOException {
    int i = this.peeked;
    if (i == 0)
      i = doPeek(); 
    if (i == 2) {
      this.stackSize--;
      this.pathNames[this.stackSize] = null;
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
      this.peeked = 0;
    } else {
      throw unexpectedTokenError("END_OBJECT");
    } 
  }
  
  public boolean hasNext() throws IOException {
    int i = this.peeked;
    if (i == 0)
      i = doPeek(); 
    return (i != 2 && i != 4 && i != 17);
  }
  
  public JsonToken peek() throws IOException {
    int i = this.peeked;
    if (i == 0)
      i = doPeek(); 
    switch (i) {
      case 1:
        return JsonToken.BEGIN_OBJECT;
      case 2:
        return JsonToken.END_OBJECT;
      case 3:
        return JsonToken.BEGIN_ARRAY;
      case 4:
        return JsonToken.END_ARRAY;
      case 12:
      case 13:
      case 14:
        return JsonToken.NAME;
      case 5:
      case 6:
        return JsonToken.BOOLEAN;
      case 7:
        return JsonToken.NULL;
      case 8:
      case 9:
      case 10:
      case 11:
        return JsonToken.STRING;
      case 15:
      case 16:
        return JsonToken.NUMBER;
      case 17:
        return JsonToken.END_DOCUMENT;
    } 
    throw new AssertionError();
  }
  
  int doPeek() throws IOException {
    int i = this.stack[this.stackSize - 1];
    if (i == 1) {
      this.stack[this.stackSize - 1] = 2;
    } else if (i == 2) {
      int m = nextNonWhitespace(true);
      switch (m) {
        case 93:
          return this.peeked = 4;
        case 59:
          checkLenient();
          break;
        case 44:
          break;
        default:
          throw syntaxError("Unterminated array");
      } 
    } else {
      if (i == 3 || i == 5) {
        this.stack[this.stackSize - 1] = 4;
        if (i == 5) {
          int n = nextNonWhitespace(true);
          switch (n) {
            case 125:
              return this.peeked = 2;
            case 59:
              checkLenient();
              break;
            case 44:
              break;
            default:
              throw syntaxError("Unterminated object");
          } 
        } 
        int m = nextNonWhitespace(true);
        switch (m) {
          case 34:
            return this.peeked = 13;
          case 39:
            checkLenient();
            return this.peeked = 12;
          case 125:
            if (i != 5)
              return this.peeked = 2; 
            throw syntaxError("Expected name");
        } 
        checkLenient();
        this.pos--;
        if (isLiteral((char)m))
          return this.peeked = 14; 
        throw syntaxError("Expected name");
      } 
      if (i == 4) {
        this.stack[this.stackSize - 1] = 5;
        int m = nextNonWhitespace(true);
        switch (m) {
          case 58:
            break;
          case 61:
            checkLenient();
            if ((this.pos < this.limit || fillBuffer(1)) && this.buffer[this.pos] == '>')
              this.pos++; 
            break;
          default:
            throw syntaxError("Expected ':'");
        } 
      } else if (i == 6) {
        if (this.strictness == Strictness.LENIENT)
          consumeNonExecutePrefix(); 
        this.stack[this.stackSize - 1] = 7;
      } else if (i == 7) {
        int m = nextNonWhitespace(false);
        if (m == -1)
          return this.peeked = 17; 
        checkLenient();
        this.pos--;
      } else if (i == 8) {
        throw new IllegalStateException("JsonReader is closed");
      } 
    } 
    int j = nextNonWhitespace(true);
    switch (j) {
      case 93:
        if (i == 1)
          return this.peeked = 4; 
      case 44:
      case 59:
        if (i == 1 || i == 2) {
          checkLenient();
          this.pos--;
          return this.peeked = 7;
        } 
        throw syntaxError("Unexpected value");
      case 39:
        checkLenient();
        return this.peeked = 8;
      case 34:
        return this.peeked = 9;
      case 91:
        return this.peeked = 3;
      case 123:
        return this.peeked = 1;
    } 
    this.pos--;
    int k = peekKeyword();
    if (k != 0)
      return k; 
    k = peekNumber();
    if (k != 0)
      return k; 
    if (!isLiteral(this.buffer[this.pos]))
      throw syntaxError("Expected value"); 
    checkLenient();
    return this.peeked = 10;
  }
  
  private int peekKeyword() throws IOException {
    String str1;
    String str2;
    byte b1;
    char c = this.buffer[this.pos];
    if (c == 't' || c == 'T') {
      str1 = "true";
      str2 = "TRUE";
      b1 = 5;
    } else if (c == 'f' || c == 'F') {
      str1 = "false";
      str2 = "FALSE";
      b1 = 6;
    } else if (c == 'n' || c == 'N') {
      str1 = "null";
      str2 = "NULL";
      b1 = 7;
    } else {
      return 0;
    } 
    boolean bool = (this.strictness != Strictness.STRICT) ? true : false;
    int i = str1.length();
    for (byte b2 = 0; b2 < i; b2++) {
      if (this.pos + b2 >= this.limit && !fillBuffer(b2 + 1))
        return 0; 
      c = this.buffer[this.pos + b2];
      boolean bool1 = (c == str1.charAt(b2) || (bool && c == str2.charAt(b2))) ? true : false;
      if (!bool1)
        return 0; 
    } 
    if ((this.pos + i < this.limit || fillBuffer(i + 1)) && isLiteral(this.buffer[this.pos + i]))
      return 0; 
    this.pos += i;
    return this.peeked = b1;
  }
  
  private int peekNumber() throws IOException {
    char[] arrayOfChar = this.buffer;
    int i = this.pos;
    int j = this.limit;
    long l = 0L;
    boolean bool = false;
    int k = 1;
    byte b1 = 0;
    byte b2;
    for (b2 = 0;; b2++) {
      if (i + b2 == j) {
        if (b2 == arrayOfChar.length)
          return 0; 
        if (!fillBuffer(b2 + 1))
          break; 
        i = this.pos;
        j = this.limit;
      } 
      char c = arrayOfChar[i + b2];
      switch (c) {
        case '-':
          if (!b1) {
            bool = true;
            b1 = 1;
            break;
          } 
          if (b1 == 5) {
            b1 = 6;
            break;
          } 
          return 0;
        case '+':
          if (b1 == 5) {
            b1 = 6;
            break;
          } 
          return 0;
        case 'E':
        case 'e':
          if (b1 == 2 || b1 == 4) {
            b1 = 5;
            break;
          } 
          return 0;
        case '.':
          if (b1 == 2) {
            b1 = 3;
            break;
          } 
          return 0;
        default:
          if (c < '0' || c > '9') {
            if (!isLiteral(c))
              break; 
            return 0;
          } 
          if (b1 == 1 || b1 == 0) {
            l = -(c - 48);
            b1 = 2;
            break;
          } 
          if (b1 == 2) {
            if (l == 0L)
              return 0; 
            long l1 = l * 10L - (c - 48);
            k &= (l > -922337203685477580L || (l == -922337203685477580L && l1 < l)) ? 1 : 0;
            l = l1;
            break;
          } 
          if (b1 == 3) {
            b1 = 4;
            break;
          } 
          if (b1 == 5 || b1 == 6)
            b1 = 7; 
          break;
      } 
    } 
    if (b1 == 2 && k != 0 && (l != Long.MIN_VALUE || bool) && (l != 0L || !bool)) {
      this.peekedLong = bool ? l : -l;
      this.pos += b2;
      return this.peeked = 15;
    } 
    if (b1 == 2 || b1 == 4 || b1 == 7) {
      this.peekedNumberLength = b2;
      return this.peeked = 16;
    } 
    return 0;
  }
  
  private boolean isLiteral(char paramChar) throws IOException {
    switch (paramChar) {
      case '#':
      case '/':
      case ';':
      case '=':
      case '\\':
        checkLenient();
      case '\t':
      case '\n':
      case '\f':
      case '\r':
      case ' ':
      case ',':
      case ':':
      case '[':
      case ']':
      case '{':
      case '}':
        return false;
    } 
    return true;
  }
  
  public String nextName() throws IOException {
    String str;
    int i = this.peeked;
    if (i == 0)
      i = doPeek(); 
    if (i == 14) {
      str = nextUnquotedValue();
    } else if (i == 12) {
      str = nextQuotedValue('\'');
    } else if (i == 13) {
      str = nextQuotedValue('"');
    } else {
      throw unexpectedTokenError("a name");
    } 
    this.peeked = 0;
    this.pathNames[this.stackSize - 1] = str;
    return str;
  }
  
  public String nextString() throws IOException {
    String str;
    int i = this.peeked;
    if (i == 0)
      i = doPeek(); 
    if (i == 10) {
      str = nextUnquotedValue();
    } else if (i == 8) {
      str = nextQuotedValue('\'');
    } else if (i == 9) {
      str = nextQuotedValue('"');
    } else if (i == 11) {
      str = this.peekedString;
      this.peekedString = null;
    } else if (i == 15) {
      str = Long.toString(this.peekedLong);
    } else if (i == 16) {
      str = new String(this.buffer, this.pos, this.peekedNumberLength);
      this.pos += this.peekedNumberLength;
    } else {
      throw unexpectedTokenError("a string");
    } 
    this.peeked = 0;
    this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
    return str;
  }
  
  public boolean nextBoolean() throws IOException {
    int i = this.peeked;
    if (i == 0)
      i = doPeek(); 
    if (i == 5) {
      this.peeked = 0;
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
      return true;
    } 
    if (i == 6) {
      this.peeked = 0;
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
      return false;
    } 
    throw unexpectedTokenError("a boolean");
  }
  
  public void nextNull() throws IOException {
    int i = this.peeked;
    if (i == 0)
      i = doPeek(); 
    if (i == 7) {
      this.peeked = 0;
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
    } else {
      throw unexpectedTokenError("null");
    } 
  }
  
  public double nextDouble() throws IOException {
    int i = this.peeked;
    if (i == 0)
      i = doPeek(); 
    if (i == 15) {
      this.peeked = 0;
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
      return this.peekedLong;
    } 
    if (i == 16) {
      this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
      this.pos += this.peekedNumberLength;
    } else if (i == 8 || i == 9) {
      this.peekedString = nextQuotedValue((i == 8) ? 39 : 34);
    } else if (i == 10) {
      this.peekedString = nextUnquotedValue();
    } else if (i != 11) {
      throw unexpectedTokenError("a double");
    } 
    this.peeked = 11;
    double d = Double.parseDouble(this.peekedString);
    if (this.strictness != Strictness.LENIENT && (Double.isNaN(d) || Double.isInfinite(d)))
      throw syntaxError("JSON forbids NaN and infinities: " + d); 
    this.peekedString = null;
    this.peeked = 0;
    this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
    return d;
  }
  
  public long nextLong() throws IOException {
    int i = this.peeked;
    if (i == 0)
      i = doPeek(); 
    if (i == 15) {
      this.peeked = 0;
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
      return this.peekedLong;
    } 
    if (i == 16) {
      this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
      this.pos += this.peekedNumberLength;
    } else if (i == 8 || i == 9 || i == 10) {
      if (i == 10) {
        this.peekedString = nextUnquotedValue();
      } else {
        this.peekedString = nextQuotedValue((i == 8) ? 39 : 34);
      } 
      try {
        long l1 = Long.parseLong(this.peekedString);
        this.peeked = 0;
        this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
        return l1;
      } catch (NumberFormatException numberFormatException) {}
    } else {
      throw unexpectedTokenError("a long");
    } 
    this.peeked = 11;
    double d = Double.parseDouble(this.peekedString);
    long l = (long)d;
    if (l != d)
      throw new NumberFormatException("Expected a long but was " + this.peekedString + locationString()); 
    this.peekedString = null;
    this.peeked = 0;
    this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
    return l;
  }
  
  private String nextQuotedValue(char paramChar) throws IOException {
    char[] arrayOfChar = this.buffer;
    StringBuilder stringBuilder = null;
    while (true) {
      int i = this.pos;
      int j = this.limit;
      int k = i;
      while (i < j) {
        char c = arrayOfChar[i++];
        if (this.strictness == Strictness.STRICT && c < ' ')
          throw syntaxError("Unescaped control characters (\\u0000-\\u001F) are not allowed in strict mode"); 
        if (c == paramChar) {
          this.pos = i;
          int m = i - k - 1;
          if (stringBuilder == null)
            return new String(arrayOfChar, k, m); 
          stringBuilder.append(arrayOfChar, k, m);
          return stringBuilder.toString();
        } 
        if (c == '\\') {
          this.pos = i;
          int m = i - k - 1;
          if (stringBuilder == null) {
            int n = (m + 1) * 2;
            stringBuilder = new StringBuilder(Math.max(n, 16));
          } 
          stringBuilder.append(arrayOfChar, k, m);
          stringBuilder.append(readEscapeCharacter());
          i = this.pos;
          j = this.limit;
          k = i;
          continue;
        } 
        if (c == '\n') {
          this.lineNumber++;
          this.lineStart = i;
        } 
      } 
      if (stringBuilder == null) {
        int m = (i - k) * 2;
        stringBuilder = new StringBuilder(Math.max(m, 16));
      } 
      stringBuilder.append(arrayOfChar, k, i - k);
      this.pos = i;
      if (!fillBuffer(1))
        throw syntaxError("Unterminated string"); 
    } 
  }
  
  private String nextUnquotedValue() throws IOException {
    StringBuilder stringBuilder = null;
    byte b = 0;
    label34: while (true) {
      while (this.pos + b < this.limit) {
        switch (this.buffer[this.pos + b]) {
          case '#':
          case '/':
          case ';':
          case '=':
          case '\\':
            checkLenient();
            break label34;
          case '\t':
            break label34;
          case '\n':
            break label34;
          case '\f':
            break label34;
          case '\r':
            break label34;
          case ' ':
            break label34;
          case ',':
            break label34;
          case ':':
            break label34;
          case '[':
            break label34;
          case ']':
            break label34;
          case '{':
            break label34;
          case '}':
            break label34;
        } 
        b++;
      } 
      if (b < this.buffer.length) {
        if (fillBuffer(b + 1))
          continue; 
        break;
      } 
      if (stringBuilder == null)
        stringBuilder = new StringBuilder(Math.max(b, 16)); 
      stringBuilder.append(this.buffer, this.pos, b);
      this.pos += b;
      b = 0;
      if (!fillBuffer(1))
        break; 
    } 
    String str = (stringBuilder == null) ? new String(this.buffer, this.pos, b) : stringBuilder.append(this.buffer, this.pos, b).toString();
    this.pos += b;
    return str;
  }
  
  private void skipQuotedValue(char paramChar) throws IOException {
    char[] arrayOfChar = this.buffer;
    while (true) {
      int i = this.pos;
      int j = this.limit;
      while (i < j) {
        char c = arrayOfChar[i++];
        if (c == paramChar) {
          this.pos = i;
          return;
        } 
        if (c == '\\') {
          this.pos = i;
          char c1 = readEscapeCharacter();
          i = this.pos;
          j = this.limit;
          continue;
        } 
        if (c == '\n') {
          this.lineNumber++;
          this.lineStart = i;
        } 
      } 
      this.pos = i;
      if (!fillBuffer(1))
        throw syntaxError("Unterminated string"); 
    } 
  }
  
  private void skipUnquotedValue() throws IOException {
    do {
      byte b;
      for (b = 0; this.pos + b < this.limit; b++) {
        switch (this.buffer[this.pos + b]) {
          case '#':
          case '/':
          case ';':
          case '=':
          case '\\':
            checkLenient();
          case '\t':
          case '\n':
          case '\f':
          case '\r':
          case ' ':
          case ',':
          case ':':
          case '[':
          case ']':
          case '{':
          case '}':
            this.pos += b;
            return;
        } 
      } 
      this.pos += b;
    } while (fillBuffer(1));
  }
  
  public int nextInt() throws IOException {
    int i = this.peeked;
    if (i == 0)
      i = doPeek(); 
    if (i == 15) {
      int k = (int)this.peekedLong;
      if (this.peekedLong != k)
        throw new NumberFormatException("Expected an int but was " + this.peekedLong + locationString()); 
      this.peeked = 0;
      this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
      return k;
    } 
    if (i == 16) {
      this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
      this.pos += this.peekedNumberLength;
    } else if (i == 8 || i == 9 || i == 10) {
      if (i == 10) {
        this.peekedString = nextUnquotedValue();
      } else {
        this.peekedString = nextQuotedValue((i == 8) ? 39 : 34);
      } 
      try {
        int k = Integer.parseInt(this.peekedString);
        this.peeked = 0;
        this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
        return k;
      } catch (NumberFormatException numberFormatException) {}
    } else {
      throw unexpectedTokenError("an int");
    } 
    this.peeked = 11;
    double d = Double.parseDouble(this.peekedString);
    int j = (int)d;
    if (j != d)
      throw new NumberFormatException("Expected an int but was " + this.peekedString + locationString()); 
    this.peekedString = null;
    this.peeked = 0;
    this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
    return j;
  }
  
  public void close() throws IOException {
    this.peeked = 0;
    this.stack[0] = 8;
    this.stackSize = 1;
    this.in.close();
  }
  
  public void skipValue() throws IOException {
    byte b = 0;
    while (true) {
      int i = this.peeked;
      if (i == 0)
        i = doPeek(); 
      switch (i) {
        case 3:
          push(1);
          b++;
          break;
        case 1:
          push(3);
          b++;
          break;
        case 4:
          this.stackSize--;
          b--;
          break;
        case 2:
          if (b == 0)
            this.pathNames[this.stackSize - 1] = null; 
          this.stackSize--;
          b--;
          break;
        case 10:
          skipUnquotedValue();
          break;
        case 8:
          skipQuotedValue('\'');
          break;
        case 9:
          skipQuotedValue('"');
          break;
        case 14:
          skipUnquotedValue();
          if (b == 0)
            this.pathNames[this.stackSize - 1] = "<skipped>"; 
          break;
        case 12:
          skipQuotedValue('\'');
          if (b == 0)
            this.pathNames[this.stackSize - 1] = "<skipped>"; 
          break;
        case 13:
          skipQuotedValue('"');
          if (b == 0)
            this.pathNames[this.stackSize - 1] = "<skipped>"; 
          break;
        case 16:
          this.pos += this.peekedNumberLength;
          break;
        case 17:
          return;
      } 
      this.peeked = 0;
      if (b <= 0) {
        this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
        return;
      } 
    } 
  }
  
  private void push(int paramInt) throws MalformedJsonException {
    if (this.stackSize - 1 >= this.nestingLimit)
      throw new MalformedJsonException("Nesting limit " + this.nestingLimit + " reached" + locationString()); 
    if (this.stackSize == this.stack.length) {
      int i = this.stackSize * 2;
      this.stack = Arrays.copyOf(this.stack, i);
      this.pathIndices = Arrays.copyOf(this.pathIndices, i);
      this.pathNames = Arrays.<String>copyOf(this.pathNames, i);
    } 
    this.stack[this.stackSize++] = paramInt;
  }
  
  private boolean fillBuffer(int paramInt) throws IOException {
    char[] arrayOfChar = this.buffer;
    this.lineStart -= this.pos;
    if (this.limit != this.pos) {
      this.limit -= this.pos;
      System.arraycopy(arrayOfChar, this.pos, arrayOfChar, 0, this.limit);
    } else {
      this.limit = 0;
    } 
    this.pos = 0;
    int i;
    while ((i = this.in.read(arrayOfChar, this.limit, arrayOfChar.length - this.limit)) != -1) {
      this.limit += i;
      if (this.lineNumber == 0 && this.lineStart == 0 && this.limit > 0 && arrayOfChar[0] == '﻿') {
        this.pos++;
        this.lineStart++;
        paramInt++;
      } 
      if (this.limit >= paramInt)
        return true; 
    } 
    return false;
  }
  
  private int nextNonWhitespace(boolean paramBoolean) throws IOException {
    char[] arrayOfChar = this.buffer;
    int i = this.pos;
    int j = this.limit;
    while (true) {
      if (i == j) {
        this.pos = i;
        if (!fillBuffer(1)) {
          if (paramBoolean)
            throw new EOFException("End of input" + locationString()); 
          return -1;
        } 
        i = this.pos;
        j = this.limit;
      } 
      char c = arrayOfChar[i++];
      if (c == '\n') {
        this.lineNumber++;
        this.lineStart = i;
        continue;
      } 
      if (c == ' ' || c == '\r' || c == '\t')
        continue; 
      if (c == '/') {
        this.pos = i;
        if (i == j) {
          this.pos--;
          boolean bool = fillBuffer(2);
          this.pos++;
          if (!bool)
            return c; 
        } 
        checkLenient();
        char c1 = arrayOfChar[this.pos];
        switch (c1) {
          case '*':
            this.pos++;
            if (!skipTo("*/"))
              throw syntaxError("Unterminated comment"); 
            i = this.pos + 2;
            j = this.limit;
            continue;
          case '/':
            this.pos++;
            skipToEndOfLine();
            i = this.pos;
            j = this.limit;
            continue;
        } 
        return c;
      } 
      if (c == '#') {
        this.pos = i;
        checkLenient();
        skipToEndOfLine();
        i = this.pos;
        j = this.limit;
        continue;
      } 
      this.pos = i;
      return c;
    } 
  }
  
  private void checkLenient() throws MalformedJsonException {
    if (this.strictness != Strictness.LENIENT)
      throw syntaxError("Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON"); 
  }
  
  private void skipToEndOfLine() throws IOException {
    while (this.pos < this.limit || fillBuffer(1)) {
      char c = this.buffer[this.pos++];
      if (c == '\n') {
        this.lineNumber++;
        this.lineStart = this.pos;
        break;
      } 
      if (c == '\r')
        break; 
    } 
  }
  
  private boolean skipTo(String paramString) throws IOException {
    int i = paramString.length();
    while (true) {
      if (this.pos + i <= this.limit || fillBuffer(i)) {
        if (this.buffer[this.pos] == '\n') {
          this.lineNumber++;
          this.lineStart = this.pos + 1;
        } else {
          byte b = 0;
          while (true) {
            if (b < i) {
              if (this.buffer[this.pos + b] != paramString.charAt(b))
                break; 
              b++;
              continue;
            } 
            return true;
          } 
        } 
        this.pos++;
        continue;
      } 
      return false;
    } 
  }
  
  public String toString() {
    return getClass().getSimpleName() + locationString();
  }
  
  String locationString() {
    int i = this.lineNumber + 1;
    int j = this.pos - this.lineStart + 1;
    return " at line " + i + " column " + j + " path " + getPath();
  }
  
  private String getPath(boolean paramBoolean) {
    StringBuilder stringBuilder = (new StringBuilder()).append('$');
    for (byte b = 0; b < this.stackSize; b++) {
      int j;
      int i = this.stack[b];
      switch (i) {
        case 1:
        case 2:
          j = this.pathIndices[b];
          if (paramBoolean && j > 0 && b == this.stackSize - 1)
            j--; 
          stringBuilder.append('[').append(j).append(']');
          break;
        case 3:
        case 4:
        case 5:
          stringBuilder.append('.');
          if (this.pathNames[b] != null)
            stringBuilder.append(this.pathNames[b]); 
          break;
        case 6:
        case 7:
        case 8:
          break;
        default:
          throw new AssertionError("Unknown scope value: " + i);
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
  
  private char readEscapeCharacter() throws IOException {
    int i;
    int j;
    int k;
    if (this.pos == this.limit && !fillBuffer(1))
      throw syntaxError("Unterminated escape sequence"); 
    char c = this.buffer[this.pos++];
    switch (c) {
      case 'u':
        if (this.pos + 4 > this.limit && !fillBuffer(4))
          throw syntaxError("Unterminated escape sequence"); 
        i = 0;
        j = this.pos;
        k = j + 4;
        while (j < k) {
          char c1 = this.buffer[j];
          i <<= 4;
          if (c1 >= '0' && c1 <= '9') {
            i += c1 - 48;
          } else if (c1 >= 'a' && c1 <= 'f') {
            i += c1 - 97 + 10;
          } else if (c1 >= 'A' && c1 <= 'F') {
            i += c1 - 65 + 10;
          } else {
            throw syntaxError("Malformed Unicode escape \\u" + new String(this.buffer, this.pos, 4));
          } 
          j++;
        } 
        this.pos += 4;
        return (char)i;
      case 't':
        return '\t';
      case 'b':
        return '\b';
      case 'n':
        return '\n';
      case 'r':
        return '\r';
      case 'f':
        return '\f';
      case '\n':
        if (this.strictness == Strictness.STRICT)
          throw syntaxError("Cannot escape a newline character in strict mode"); 
        this.lineNumber++;
        this.lineStart = this.pos;
      case '\'':
        if (this.strictness == Strictness.STRICT)
          throw syntaxError("Invalid escaped character \"'\" in strict mode"); 
      case '"':
      case '/':
      case '\\':
        return c;
    } 
    throw syntaxError("Invalid escape sequence");
  }
  
  private MalformedJsonException syntaxError(String paramString) throws MalformedJsonException {
    throw new MalformedJsonException(paramString + locationString() + "\nSee " + TroubleshootingGuide.createUrl("malformed-json"));
  }
  
  private IllegalStateException unexpectedTokenError(String paramString) throws IOException {
    JsonToken jsonToken = peek();
    String str = (jsonToken == JsonToken.NULL) ? "adapter-not-null-safe" : "unexpected-json-structure";
    return new IllegalStateException("Expected " + paramString + " but was " + peek() + locationString() + "\nSee " + TroubleshootingGuide.createUrl(str));
  }
  
  private void consumeNonExecutePrefix() throws IOException {
    int i = nextNonWhitespace(true);
    this.pos--;
    if (this.pos + 5 > this.limit && !fillBuffer(5))
      return; 
    int j = this.pos;
    char[] arrayOfChar = this.buffer;
    if (arrayOfChar[j] != ')' || arrayOfChar[j + 1] != ']' || arrayOfChar[j + 2] != '}' || arrayOfChar[j + 3] != '\'' || arrayOfChar[j + 4] != '\n')
      return; 
    this.pos += 5;
  }
  
  static {
    JsonReaderInternalAccess.INSTANCE = new JsonReaderInternalAccess() {
        public void promoteNameToValue(JsonReader param1JsonReader) throws IOException {
          if (param1JsonReader instanceof JsonTreeReader) {
            ((JsonTreeReader)param1JsonReader).promoteNameToValue();
            return;
          } 
          int i = param1JsonReader.peeked;
          if (i == 0)
            i = param1JsonReader.doPeek(); 
          if (i == 13) {
            param1JsonReader.peeked = 9;
          } else if (i == 12) {
            param1JsonReader.peeked = 8;
          } else if (i == 14) {
            param1JsonReader.peeked = 10;
          } else {
            throw param1JsonReader.unexpectedTokenError("a name");
          } 
        }
        
        static {
        
        }
      };
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\stream\JsonReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */