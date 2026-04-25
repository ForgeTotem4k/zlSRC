package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.internal.NumberLimits;
import com.google.gson.internal.TroubleshootingGuide;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public final class TypeAdapters {
  public static final TypeAdapter<Class> CLASS = (new TypeAdapter<Class>() {
      public void write(JsonWriter param1JsonWriter, Class param1Class) throws IOException {
        throw new UnsupportedOperationException("Attempted to serialize java.lang.Class: " + param1Class.getName() + ". Forgot to register a type adapter?\nSee " + TroubleshootingGuide.createUrl("java-lang-class-unsupported"));
      }
      
      public Class read(JsonReader param1JsonReader) throws IOException {
        throw new UnsupportedOperationException("Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?\nSee " + TroubleshootingGuide.createUrl("java-lang-class-unsupported"));
      }
      
      static {
      
      }
    }).nullSafe();
  
  public static final TypeAdapterFactory CLASS_FACTORY = newFactory(Class.class, CLASS);
  
  public static final TypeAdapter<BitSet> BIT_SET = (new TypeAdapter<BitSet>() {
      public BitSet read(JsonReader param1JsonReader) throws IOException {
        BitSet bitSet = new BitSet();
        param1JsonReader.beginArray();
        byte b = 0;
        for (JsonToken jsonToken = param1JsonReader.peek(); jsonToken != JsonToken.END_ARRAY; jsonToken = param1JsonReader.peek()) {
          boolean bool;
          int i;
          switch (jsonToken) {
            case NUMBER:
            case STRING:
              i = param1JsonReader.nextInt();
              if (i == 0) {
                boolean bool1 = false;
                break;
              } 
              if (i == 1) {
                boolean bool1 = true;
                break;
              } 
              throw new JsonSyntaxException("Invalid bitset value " + i + ", expected 0 or 1; at path " + param1JsonReader.getPreviousPath());
            case BOOLEAN:
              bool = param1JsonReader.nextBoolean();
              break;
            default:
              throw new JsonSyntaxException("Invalid bitset value type: " + jsonToken + "; at path " + param1JsonReader.getPath());
          } 
          if (bool)
            bitSet.set(b); 
          b++;
        } 
        param1JsonReader.endArray();
        return bitSet;
      }
      
      public void write(JsonWriter param1JsonWriter, BitSet param1BitSet) throws IOException {
        param1JsonWriter.beginArray();
        byte b = 0;
        int i = param1BitSet.length();
        while (b < i) {
          boolean bool = param1BitSet.get(b) ? true : false;
          param1JsonWriter.value(bool);
          b++;
        } 
        param1JsonWriter.endArray();
      }
      
      static {
      
      }
    }).nullSafe();
  
  public static final TypeAdapterFactory BIT_SET_FACTORY = newFactory(BitSet.class, BIT_SET);
  
  public static final TypeAdapter<Boolean> BOOLEAN = new TypeAdapter<Boolean>() {
      public Boolean read(JsonReader param1JsonReader) throws IOException {
        JsonToken jsonToken = param1JsonReader.peek();
        if (jsonToken == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        return (jsonToken == JsonToken.STRING) ? Boolean.valueOf(Boolean.parseBoolean(param1JsonReader.nextString())) : Boolean.valueOf(param1JsonReader.nextBoolean());
      }
      
      public void write(JsonWriter param1JsonWriter, Boolean param1Boolean) throws IOException {
        param1JsonWriter.value(param1Boolean);
      }
      
      static {
      
      }
    };
  
  public static final TypeAdapter<Boolean> BOOLEAN_AS_STRING = new TypeAdapter<Boolean>() {
      public Boolean read(JsonReader param1JsonReader) throws IOException {
        if (param1JsonReader.peek() == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        return Boolean.valueOf(param1JsonReader.nextString());
      }
      
      public void write(JsonWriter param1JsonWriter, Boolean param1Boolean) throws IOException {
        param1JsonWriter.value((param1Boolean == null) ? "null" : param1Boolean.toString());
      }
      
      static {
      
      }
    };
  
  public static final TypeAdapterFactory BOOLEAN_FACTORY = newFactory(boolean.class, (Class)Boolean.class, (TypeAdapter)BOOLEAN);
  
  public static final TypeAdapter<Number> BYTE = new TypeAdapter<Number>() {
      public Number read(JsonReader param1JsonReader) throws IOException {
        int i;
        if (param1JsonReader.peek() == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        try {
          i = param1JsonReader.nextInt();
        } catch (NumberFormatException numberFormatException) {
          throw new JsonSyntaxException(numberFormatException);
        } 
        if (i > 255 || i < -128)
          throw new JsonSyntaxException("Lossy conversion from " + i + " to byte; at path " + param1JsonReader.getPreviousPath()); 
        return Byte.valueOf((byte)i);
      }
      
      public void write(JsonWriter param1JsonWriter, Number param1Number) throws IOException {
        if (param1Number == null) {
          param1JsonWriter.nullValue();
        } else {
          param1JsonWriter.value(param1Number.byteValue());
        } 
      }
      
      static {
      
      }
    };
  
  public static final TypeAdapterFactory BYTE_FACTORY = newFactory(byte.class, (Class)Byte.class, (TypeAdapter)BYTE);
  
  public static final TypeAdapter<Number> SHORT = new TypeAdapter<Number>() {
      public Number read(JsonReader param1JsonReader) throws IOException {
        int i;
        if (param1JsonReader.peek() == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        try {
          i = param1JsonReader.nextInt();
        } catch (NumberFormatException numberFormatException) {
          throw new JsonSyntaxException(numberFormatException);
        } 
        if (i > 65535 || i < -32768)
          throw new JsonSyntaxException("Lossy conversion from " + i + " to short; at path " + param1JsonReader.getPreviousPath()); 
        return Short.valueOf((short)i);
      }
      
      public void write(JsonWriter param1JsonWriter, Number param1Number) throws IOException {
        if (param1Number == null) {
          param1JsonWriter.nullValue();
        } else {
          param1JsonWriter.value(param1Number.shortValue());
        } 
      }
      
      static {
      
      }
    };
  
  public static final TypeAdapterFactory SHORT_FACTORY = newFactory(short.class, (Class)Short.class, (TypeAdapter)SHORT);
  
  public static final TypeAdapter<Number> INTEGER = new TypeAdapter<Number>() {
      public Number read(JsonReader param1JsonReader) throws IOException {
        if (param1JsonReader.peek() == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        try {
          return Integer.valueOf(param1JsonReader.nextInt());
        } catch (NumberFormatException numberFormatException) {
          throw new JsonSyntaxException(numberFormatException);
        } 
      }
      
      public void write(JsonWriter param1JsonWriter, Number param1Number) throws IOException {
        if (param1Number == null) {
          param1JsonWriter.nullValue();
        } else {
          param1JsonWriter.value(param1Number.intValue());
        } 
      }
      
      static {
      
      }
    };
  
  public static final TypeAdapterFactory INTEGER_FACTORY = newFactory(int.class, (Class)Integer.class, (TypeAdapter)INTEGER);
  
  public static final TypeAdapter<AtomicInteger> ATOMIC_INTEGER = (new TypeAdapter<AtomicInteger>() {
      public AtomicInteger read(JsonReader param1JsonReader) throws IOException {
        try {
          return new AtomicInteger(param1JsonReader.nextInt());
        } catch (NumberFormatException numberFormatException) {
          throw new JsonSyntaxException(numberFormatException);
        } 
      }
      
      public void write(JsonWriter param1JsonWriter, AtomicInteger param1AtomicInteger) throws IOException {
        param1JsonWriter.value(param1AtomicInteger.get());
      }
      
      static {
      
      }
    }).nullSafe();
  
  public static final TypeAdapterFactory ATOMIC_INTEGER_FACTORY = newFactory(AtomicInteger.class, ATOMIC_INTEGER);
  
  public static final TypeAdapter<AtomicBoolean> ATOMIC_BOOLEAN = (new TypeAdapter<AtomicBoolean>() {
      public AtomicBoolean read(JsonReader param1JsonReader) throws IOException {
        return new AtomicBoolean(param1JsonReader.nextBoolean());
      }
      
      public void write(JsonWriter param1JsonWriter, AtomicBoolean param1AtomicBoolean) throws IOException {
        param1JsonWriter.value(param1AtomicBoolean.get());
      }
      
      static {
      
      }
    }).nullSafe();
  
  public static final TypeAdapterFactory ATOMIC_BOOLEAN_FACTORY = newFactory(AtomicBoolean.class, ATOMIC_BOOLEAN);
  
  public static final TypeAdapter<AtomicIntegerArray> ATOMIC_INTEGER_ARRAY = (new TypeAdapter<AtomicIntegerArray>() {
      public AtomicIntegerArray read(JsonReader param1JsonReader) throws IOException {
        ArrayList<Integer> arrayList = new ArrayList();
        param1JsonReader.beginArray();
        while (param1JsonReader.hasNext()) {
          try {
            int j = param1JsonReader.nextInt();
            arrayList.add(Integer.valueOf(j));
          } catch (NumberFormatException numberFormatException) {
            throw new JsonSyntaxException(numberFormatException);
          } 
        } 
        param1JsonReader.endArray();
        int i = arrayList.size();
        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(i);
        for (byte b = 0; b < i; b++)
          atomicIntegerArray.set(b, ((Integer)arrayList.get(b)).intValue()); 
        return atomicIntegerArray;
      }
      
      public void write(JsonWriter param1JsonWriter, AtomicIntegerArray param1AtomicIntegerArray) throws IOException {
        param1JsonWriter.beginArray();
        byte b = 0;
        int i = param1AtomicIntegerArray.length();
        while (b < i) {
          param1JsonWriter.value(param1AtomicIntegerArray.get(b));
          b++;
        } 
        param1JsonWriter.endArray();
      }
      
      static {
      
      }
    }).nullSafe();
  
  public static final TypeAdapterFactory ATOMIC_INTEGER_ARRAY_FACTORY = newFactory(AtomicIntegerArray.class, ATOMIC_INTEGER_ARRAY);
  
  public static final TypeAdapter<Number> LONG = new TypeAdapter<Number>() {
      public Number read(JsonReader param1JsonReader) throws IOException {
        if (param1JsonReader.peek() == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        try {
          return Long.valueOf(param1JsonReader.nextLong());
        } catch (NumberFormatException numberFormatException) {
          throw new JsonSyntaxException(numberFormatException);
        } 
      }
      
      public void write(JsonWriter param1JsonWriter, Number param1Number) throws IOException {
        if (param1Number == null) {
          param1JsonWriter.nullValue();
        } else {
          param1JsonWriter.value(param1Number.longValue());
        } 
      }
      
      static {
      
      }
    };
  
  public static final TypeAdapter<Number> FLOAT = new TypeAdapter<Number>() {
      public Number read(JsonReader param1JsonReader) throws IOException {
        if (param1JsonReader.peek() == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        return Float.valueOf((float)param1JsonReader.nextDouble());
      }
      
      public void write(JsonWriter param1JsonWriter, Number param1Number) throws IOException {
        if (param1Number == null) {
          param1JsonWriter.nullValue();
        } else {
          Number number = (param1Number instanceof Float) ? param1Number : Float.valueOf(param1Number.floatValue());
          param1JsonWriter.value(number);
        } 
      }
      
      static {
      
      }
    };
  
  public static final TypeAdapter<Number> DOUBLE = new TypeAdapter<Number>() {
      public Number read(JsonReader param1JsonReader) throws IOException {
        if (param1JsonReader.peek() == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        return Double.valueOf(param1JsonReader.nextDouble());
      }
      
      public void write(JsonWriter param1JsonWriter, Number param1Number) throws IOException {
        if (param1Number == null) {
          param1JsonWriter.nullValue();
        } else {
          param1JsonWriter.value(param1Number.doubleValue());
        } 
      }
      
      static {
      
      }
    };
  
  public static final TypeAdapter<Character> CHARACTER = new TypeAdapter<Character>() {
      public Character read(JsonReader param1JsonReader) throws IOException {
        if (param1JsonReader.peek() == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        String str = param1JsonReader.nextString();
        if (str.length() != 1)
          throw new JsonSyntaxException("Expecting character, got: " + str + "; at " + param1JsonReader.getPreviousPath()); 
        return Character.valueOf(str.charAt(0));
      }
      
      public void write(JsonWriter param1JsonWriter, Character param1Character) throws IOException {
        param1JsonWriter.value((param1Character == null) ? null : String.valueOf(param1Character));
      }
      
      static {
      
      }
    };
  
  public static final TypeAdapterFactory CHARACTER_FACTORY = newFactory(char.class, (Class)Character.class, (TypeAdapter)CHARACTER);
  
  public static final TypeAdapter<String> STRING = new TypeAdapter<String>() {
      public String read(JsonReader param1JsonReader) throws IOException {
        JsonToken jsonToken = param1JsonReader.peek();
        if (jsonToken == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        return (jsonToken == JsonToken.BOOLEAN) ? Boolean.toString(param1JsonReader.nextBoolean()) : param1JsonReader.nextString();
      }
      
      public void write(JsonWriter param1JsonWriter, String param1String) throws IOException {
        param1JsonWriter.value(param1String);
      }
      
      static {
      
      }
    };
  
  public static final TypeAdapter<BigDecimal> BIG_DECIMAL = new TypeAdapter<BigDecimal>() {
      public BigDecimal read(JsonReader param1JsonReader) throws IOException {
        if (param1JsonReader.peek() == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        String str = param1JsonReader.nextString();
        try {
          return NumberLimits.parseBigDecimal(str);
        } catch (NumberFormatException numberFormatException) {
          throw new JsonSyntaxException("Failed parsing '" + str + "' as BigDecimal; at path " + param1JsonReader.getPreviousPath(), numberFormatException);
        } 
      }
      
      public void write(JsonWriter param1JsonWriter, BigDecimal param1BigDecimal) throws IOException {
        param1JsonWriter.value(param1BigDecimal);
      }
      
      static {
      
      }
    };
  
  public static final TypeAdapter<BigInteger> BIG_INTEGER = new TypeAdapter<BigInteger>() {
      public BigInteger read(JsonReader param1JsonReader) throws IOException {
        if (param1JsonReader.peek() == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        String str = param1JsonReader.nextString();
        try {
          return NumberLimits.parseBigInteger(str);
        } catch (NumberFormatException numberFormatException) {
          throw new JsonSyntaxException("Failed parsing '" + str + "' as BigInteger; at path " + param1JsonReader.getPreviousPath(), numberFormatException);
        } 
      }
      
      public void write(JsonWriter param1JsonWriter, BigInteger param1BigInteger) throws IOException {
        param1JsonWriter.value(param1BigInteger);
      }
      
      static {
      
      }
    };
  
  public static final TypeAdapter<LazilyParsedNumber> LAZILY_PARSED_NUMBER = new TypeAdapter<LazilyParsedNumber>() {
      public LazilyParsedNumber read(JsonReader param1JsonReader) throws IOException {
        if (param1JsonReader.peek() == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        return new LazilyParsedNumber(param1JsonReader.nextString());
      }
      
      public void write(JsonWriter param1JsonWriter, LazilyParsedNumber param1LazilyParsedNumber) throws IOException {
        param1JsonWriter.value((Number)param1LazilyParsedNumber);
      }
      
      static {
      
      }
    };
  
  public static final TypeAdapterFactory STRING_FACTORY = newFactory(String.class, STRING);
  
  public static final TypeAdapter<StringBuilder> STRING_BUILDER = new TypeAdapter<StringBuilder>() {
      public StringBuilder read(JsonReader param1JsonReader) throws IOException {
        if (param1JsonReader.peek() == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        return new StringBuilder(param1JsonReader.nextString());
      }
      
      public void write(JsonWriter param1JsonWriter, StringBuilder param1StringBuilder) throws IOException {
        param1JsonWriter.value((param1StringBuilder == null) ? null : param1StringBuilder.toString());
      }
      
      static {
      
      }
    };
  
  public static final TypeAdapterFactory STRING_BUILDER_FACTORY = newFactory(StringBuilder.class, STRING_BUILDER);
  
  public static final TypeAdapter<StringBuffer> STRING_BUFFER = new TypeAdapter<StringBuffer>() {
      public StringBuffer read(JsonReader param1JsonReader) throws IOException {
        if (param1JsonReader.peek() == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        return new StringBuffer(param1JsonReader.nextString());
      }
      
      public void write(JsonWriter param1JsonWriter, StringBuffer param1StringBuffer) throws IOException {
        param1JsonWriter.value((param1StringBuffer == null) ? null : param1StringBuffer.toString());
      }
      
      static {
      
      }
    };
  
  public static final TypeAdapterFactory STRING_BUFFER_FACTORY = newFactory(StringBuffer.class, STRING_BUFFER);
  
  public static final TypeAdapter<URL> URL = new TypeAdapter<URL>() {
      public URL read(JsonReader param1JsonReader) throws IOException {
        if (param1JsonReader.peek() == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        String str = param1JsonReader.nextString();
        return str.equals("null") ? null : new URL(str);
      }
      
      public void write(JsonWriter param1JsonWriter, URL param1URL) throws IOException {
        param1JsonWriter.value((param1URL == null) ? null : param1URL.toExternalForm());
      }
      
      static {
      
      }
    };
  
  public static final TypeAdapterFactory URL_FACTORY = newFactory(URL.class, URL);
  
  public static final TypeAdapter<URI> URI = new TypeAdapter<URI>() {
      public URI read(JsonReader param1JsonReader) throws IOException {
        if (param1JsonReader.peek() == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        try {
          String str = param1JsonReader.nextString();
          return str.equals("null") ? null : new URI(str);
        } catch (URISyntaxException uRISyntaxException) {
          throw new JsonIOException(uRISyntaxException);
        } 
      }
      
      public void write(JsonWriter param1JsonWriter, URI param1URI) throws IOException {
        param1JsonWriter.value((param1URI == null) ? null : param1URI.toASCIIString());
      }
      
      static {
      
      }
    };
  
  public static final TypeAdapterFactory URI_FACTORY = newFactory(URI.class, URI);
  
  public static final TypeAdapter<InetAddress> INET_ADDRESS = new TypeAdapter<InetAddress>() {
      public InetAddress read(JsonReader param1JsonReader) throws IOException {
        if (param1JsonReader.peek() == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        return InetAddress.getByName(param1JsonReader.nextString());
      }
      
      public void write(JsonWriter param1JsonWriter, InetAddress param1InetAddress) throws IOException {
        param1JsonWriter.value((param1InetAddress == null) ? null : param1InetAddress.getHostAddress());
      }
      
      static {
      
      }
    };
  
  public static final TypeAdapterFactory INET_ADDRESS_FACTORY = newTypeHierarchyFactory(InetAddress.class, INET_ADDRESS);
  
  public static final TypeAdapter<UUID> UUID = new TypeAdapter<UUID>() {
      public UUID read(JsonReader param1JsonReader) throws IOException {
        if (param1JsonReader.peek() == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        String str = param1JsonReader.nextString();
        try {
          return UUID.fromString(str);
        } catch (IllegalArgumentException illegalArgumentException) {
          throw new JsonSyntaxException("Failed parsing '" + str + "' as UUID; at path " + param1JsonReader.getPreviousPath(), illegalArgumentException);
        } 
      }
      
      public void write(JsonWriter param1JsonWriter, UUID param1UUID) throws IOException {
        param1JsonWriter.value((param1UUID == null) ? null : param1UUID.toString());
      }
      
      static {
      
      }
    };
  
  public static final TypeAdapterFactory UUID_FACTORY = newFactory(UUID.class, UUID);
  
  public static final TypeAdapter<Currency> CURRENCY = (new TypeAdapter<Currency>() {
      public Currency read(JsonReader param1JsonReader) throws IOException {
        String str = param1JsonReader.nextString();
        try {
          return Currency.getInstance(str);
        } catch (IllegalArgumentException illegalArgumentException) {
          throw new JsonSyntaxException("Failed parsing '" + str + "' as Currency; at path " + param1JsonReader.getPreviousPath(), illegalArgumentException);
        } 
      }
      
      public void write(JsonWriter param1JsonWriter, Currency param1Currency) throws IOException {
        param1JsonWriter.value(param1Currency.getCurrencyCode());
      }
      
      static {
      
      }
    }).nullSafe();
  
  public static final TypeAdapterFactory CURRENCY_FACTORY = newFactory(Currency.class, CURRENCY);
  
  public static final TypeAdapter<Calendar> CALENDAR = new TypeAdapter<Calendar>() {
      private static final String YEAR = "year";
      
      private static final String MONTH = "month";
      
      private static final String DAY_OF_MONTH = "dayOfMonth";
      
      private static final String HOUR_OF_DAY = "hourOfDay";
      
      private static final String MINUTE = "minute";
      
      private static final String SECOND = "second";
      
      public Calendar read(JsonReader param1JsonReader) throws IOException {
        if (param1JsonReader.peek() == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        param1JsonReader.beginObject();
        int i = 0;
        int j = 0;
        int k = 0;
        int m = 0;
        int n = 0;
        int i1 = 0;
        while (param1JsonReader.peek() != JsonToken.END_OBJECT) {
          String str = param1JsonReader.nextName();
          int i2 = param1JsonReader.nextInt();
          switch (str) {
            case "year":
              i = i2;
            case "month":
              j = i2;
            case "dayOfMonth":
              k = i2;
            case "hourOfDay":
              m = i2;
            case "minute":
              n = i2;
            case "second":
              i1 = i2;
          } 
        } 
        param1JsonReader.endObject();
        return new GregorianCalendar(i, j, k, m, n, i1);
      }
      
      public void write(JsonWriter param1JsonWriter, Calendar param1Calendar) throws IOException {
        if (param1Calendar == null) {
          param1JsonWriter.nullValue();
          return;
        } 
        param1JsonWriter.beginObject();
        param1JsonWriter.name("year");
        param1JsonWriter.value(param1Calendar.get(1));
        param1JsonWriter.name("month");
        param1JsonWriter.value(param1Calendar.get(2));
        param1JsonWriter.name("dayOfMonth");
        param1JsonWriter.value(param1Calendar.get(5));
        param1JsonWriter.name("hourOfDay");
        param1JsonWriter.value(param1Calendar.get(11));
        param1JsonWriter.name("minute");
        param1JsonWriter.value(param1Calendar.get(12));
        param1JsonWriter.name("second");
        param1JsonWriter.value(param1Calendar.get(13));
        param1JsonWriter.endObject();
      }
    };
  
  public static final TypeAdapterFactory CALENDAR_FACTORY = newFactoryForMultipleTypes(Calendar.class, (Class)GregorianCalendar.class, CALENDAR);
  
  public static final TypeAdapter<Locale> LOCALE = new TypeAdapter<Locale>() {
      public Locale read(JsonReader param1JsonReader) throws IOException {
        if (param1JsonReader.peek() == JsonToken.NULL) {
          param1JsonReader.nextNull();
          return null;
        } 
        String str1 = param1JsonReader.nextString();
        StringTokenizer stringTokenizer = new StringTokenizer(str1, "_");
        String str2 = null;
        String str3 = null;
        String str4 = null;
        if (stringTokenizer.hasMoreElements())
          str2 = stringTokenizer.nextToken(); 
        if (stringTokenizer.hasMoreElements())
          str3 = stringTokenizer.nextToken(); 
        if (stringTokenizer.hasMoreElements())
          str4 = stringTokenizer.nextToken(); 
        return (str3 == null && str4 == null) ? new Locale(str2) : ((str4 == null) ? new Locale(str2, str3) : new Locale(str2, str3, str4));
      }
      
      public void write(JsonWriter param1JsonWriter, Locale param1Locale) throws IOException {
        param1JsonWriter.value((param1Locale == null) ? null : param1Locale.toString());
      }
      
      static {
      
      }
    };
  
  public static final TypeAdapterFactory LOCALE_FACTORY = newFactory(Locale.class, LOCALE);
  
  public static final TypeAdapter<JsonElement> JSON_ELEMENT = JsonElementTypeAdapter.ADAPTER;
  
  public static final TypeAdapterFactory JSON_ELEMENT_FACTORY = newTypeHierarchyFactory(JsonElement.class, JSON_ELEMENT);
  
  public static final TypeAdapterFactory ENUM_FACTORY = EnumTypeAdapter.FACTORY;
  
  private TypeAdapters() {
    throw new UnsupportedOperationException();
  }
  
  public static <TT> TypeAdapterFactory newFactory(final TypeToken<TT> type, final TypeAdapter<TT> typeAdapter) {
    return new TypeAdapterFactory() {
        public <T> TypeAdapter<T> create(Gson param1Gson, TypeToken<T> param1TypeToken) {
          return param1TypeToken.equals(type) ? typeAdapter : null;
        }
      };
  }
  
  public static <TT> TypeAdapterFactory newFactory(final Class<TT> type, final TypeAdapter<TT> typeAdapter) {
    return new TypeAdapterFactory() {
        public <T> TypeAdapter<T> create(Gson param1Gson, TypeToken<T> param1TypeToken) {
          return (param1TypeToken.getRawType() == type) ? typeAdapter : null;
        }
        
        public String toString() {
          return "Factory[type=" + type.getName() + ",adapter=" + typeAdapter + "]";
        }
      };
  }
  
  public static <TT> TypeAdapterFactory newFactory(final Class<TT> unboxed, final Class<TT> boxed, final TypeAdapter<? super TT> typeAdapter) {
    return new TypeAdapterFactory() {
        public <T> TypeAdapter<T> create(Gson param1Gson, TypeToken<T> param1TypeToken) {
          Class clazz = param1TypeToken.getRawType();
          return (clazz == unboxed || clazz == boxed) ? typeAdapter : null;
        }
        
        public String toString() {
          return "Factory[type=" + boxed.getName() + "+" + unboxed.getName() + ",adapter=" + typeAdapter + "]";
        }
      };
  }
  
  public static <TT> TypeAdapterFactory newFactoryForMultipleTypes(final Class<TT> base, final Class<? extends TT> sub, final TypeAdapter<? super TT> typeAdapter) {
    return new TypeAdapterFactory() {
        public <T> TypeAdapter<T> create(Gson param1Gson, TypeToken<T> param1TypeToken) {
          Class clazz = param1TypeToken.getRawType();
          return (clazz == base || clazz == sub) ? typeAdapter : null;
        }
        
        public String toString() {
          return "Factory[type=" + base.getName() + "+" + sub.getName() + ",adapter=" + typeAdapter + "]";
        }
      };
  }
  
  public static <T1> TypeAdapterFactory newTypeHierarchyFactory(final Class<T1> clazz, final TypeAdapter<T1> typeAdapter) {
    return new TypeAdapterFactory() {
        public <T2> TypeAdapter<T2> create(Gson param1Gson, TypeToken<T2> param1TypeToken) {
          final Class<?> requestedType = param1TypeToken.getRawType();
          return !clazz.isAssignableFrom(clazz) ? null : new TypeAdapter<T1>() {
              public void write(JsonWriter param2JsonWriter, T1 param2T1) throws IOException {
                typeAdapter.write(param2JsonWriter, param2T1);
              }
              
              public T1 read(JsonReader param2JsonReader) throws IOException {
                Object object = typeAdapter.read(param2JsonReader);
                if (object != null && !requestedType.isInstance(object))
                  throw new JsonSyntaxException("Expected a " + requestedType.getName() + " but was " + object.getClass().getName() + "; at path " + param2JsonReader.getPreviousPath()); 
                return (T1)object;
              }
            };
        }
        
        public String toString() {
          return "Factory[typeHierarchy=" + clazz.getName() + ",adapter=" + typeAdapter + "]";
        }
      };
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\bind\TypeAdapters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */