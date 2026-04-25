package com.google.gson.internal;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.math.BigDecimal;

public final class LazilyParsedNumber extends Number {
  private final String value;
  
  public LazilyParsedNumber(String paramString) {
    this.value = paramString;
  }
  
  private BigDecimal asBigDecimal() {
    return NumberLimits.parseBigDecimal(this.value);
  }
  
  public int intValue() {
    try {
      return Integer.parseInt(this.value);
    } catch (NumberFormatException numberFormatException) {
      try {
        return (int)Long.parseLong(this.value);
      } catch (NumberFormatException numberFormatException1) {
        return asBigDecimal().intValue();
      } 
    } 
  }
  
  public long longValue() {
    try {
      return Long.parseLong(this.value);
    } catch (NumberFormatException numberFormatException) {
      return asBigDecimal().longValue();
    } 
  }
  
  public float floatValue() {
    return Float.parseFloat(this.value);
  }
  
  public double doubleValue() {
    return Double.parseDouble(this.value);
  }
  
  public String toString() {
    return this.value;
  }
  
  private Object writeReplace() throws ObjectStreamException {
    return asBigDecimal();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException {
    throw new InvalidObjectException("Deserialization is unsupported");
  }
  
  public int hashCode() {
    return this.value.hashCode();
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof LazilyParsedNumber) {
      LazilyParsedNumber lazilyParsedNumber = (LazilyParsedNumber)paramObject;
      return this.value.equals(lazilyParsedNumber.value);
    } 
    return false;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\LazilyParsedNumber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */