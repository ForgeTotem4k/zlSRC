package com.google.gson;

import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.internal.NumberLimits;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public final class JsonPrimitive extends JsonElement {
  private final Object value;
  
  public JsonPrimitive(Boolean paramBoolean) {
    this.value = Objects.requireNonNull(paramBoolean);
  }
  
  public JsonPrimitive(Number paramNumber) {
    this.value = Objects.requireNonNull(paramNumber);
  }
  
  public JsonPrimitive(String paramString) {
    this.value = Objects.requireNonNull(paramString);
  }
  
  public JsonPrimitive(Character paramCharacter) {
    this.value = ((Character)Objects.<Character>requireNonNull(paramCharacter)).toString();
  }
  
  public JsonPrimitive deepCopy() {
    return this;
  }
  
  public boolean isBoolean() {
    return this.value instanceof Boolean;
  }
  
  public boolean getAsBoolean() {
    return isBoolean() ? ((Boolean)this.value).booleanValue() : Boolean.parseBoolean(getAsString());
  }
  
  public boolean isNumber() {
    return this.value instanceof Number;
  }
  
  public Number getAsNumber() {
    if (this.value instanceof Number)
      return (Number)this.value; 
    if (this.value instanceof String)
      return (Number)new LazilyParsedNumber((String)this.value); 
    throw new UnsupportedOperationException("Primitive is neither a number nor a string");
  }
  
  public boolean isString() {
    return this.value instanceof String;
  }
  
  public String getAsString() {
    if (this.value instanceof String)
      return (String)this.value; 
    if (isNumber())
      return getAsNumber().toString(); 
    if (isBoolean())
      return ((Boolean)this.value).toString(); 
    throw new AssertionError("Unexpected value type: " + this.value.getClass());
  }
  
  public double getAsDouble() {
    return isNumber() ? getAsNumber().doubleValue() : Double.parseDouble(getAsString());
  }
  
  public BigDecimal getAsBigDecimal() {
    return (this.value instanceof BigDecimal) ? (BigDecimal)this.value : NumberLimits.parseBigDecimal(getAsString());
  }
  
  public BigInteger getAsBigInteger() {
    return (this.value instanceof BigInteger) ? (BigInteger)this.value : (isIntegral(this) ? BigInteger.valueOf(getAsNumber().longValue()) : NumberLimits.parseBigInteger(getAsString()));
  }
  
  public float getAsFloat() {
    return isNumber() ? getAsNumber().floatValue() : Float.parseFloat(getAsString());
  }
  
  public long getAsLong() {
    return isNumber() ? getAsNumber().longValue() : Long.parseLong(getAsString());
  }
  
  public short getAsShort() {
    return isNumber() ? getAsNumber().shortValue() : Short.parseShort(getAsString());
  }
  
  public int getAsInt() {
    return isNumber() ? getAsNumber().intValue() : Integer.parseInt(getAsString());
  }
  
  public byte getAsByte() {
    return isNumber() ? getAsNumber().byteValue() : Byte.parseByte(getAsString());
  }
  
  @Deprecated
  public char getAsCharacter() {
    String str = getAsString();
    if (str.isEmpty())
      throw new UnsupportedOperationException("String value is empty"); 
    return str.charAt(0);
  }
  
  public int hashCode() {
    if (this.value == null)
      return 31; 
    if (isIntegral(this)) {
      long l = getAsNumber().longValue();
      return (int)(l ^ l >>> 32L);
    } 
    if (this.value instanceof Number) {
      long l = Double.doubleToLongBits(getAsNumber().doubleValue());
      return (int)(l ^ l >>> 32L);
    } 
    return this.value.hashCode();
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    JsonPrimitive jsonPrimitive = (JsonPrimitive)paramObject;
    if (this.value == null)
      return (jsonPrimitive.value == null); 
    if (isIntegral(this) && isIntegral(jsonPrimitive))
      return (this.value instanceof BigInteger || jsonPrimitive.value instanceof BigInteger) ? getAsBigInteger().equals(jsonPrimitive.getAsBigInteger()) : ((getAsNumber().longValue() == jsonPrimitive.getAsNumber().longValue())); 
    if (this.value instanceof Number && jsonPrimitive.value instanceof Number) {
      if (this.value instanceof BigDecimal && jsonPrimitive.value instanceof BigDecimal)
        return (getAsBigDecimal().compareTo(jsonPrimitive.getAsBigDecimal()) == 0); 
      double d1 = getAsDouble();
      double d2 = jsonPrimitive.getAsDouble();
      return (d1 == d2 || (Double.isNaN(d1) && Double.isNaN(d2)));
    } 
    return this.value.equals(jsonPrimitive.value);
  }
  
  private static boolean isIntegral(JsonPrimitive paramJsonPrimitive) {
    if (paramJsonPrimitive.value instanceof Number) {
      Number number = (Number)paramJsonPrimitive.value;
      return (number instanceof BigInteger || number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte);
    } 
    return false;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\JsonPrimitive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */