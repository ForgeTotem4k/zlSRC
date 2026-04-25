package com.google.gson.internal;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberLimits {
  private static final int MAX_NUMBER_STRING_LENGTH = 10000;
  
  private static void checkNumberStringLength(String paramString) {
    if (paramString.length() > 10000)
      throw new NumberFormatException("Number string too large: " + paramString.substring(0, 30) + "..."); 
  }
  
  public static BigDecimal parseBigDecimal(String paramString) throws NumberFormatException {
    checkNumberStringLength(paramString);
    BigDecimal bigDecimal = new BigDecimal(paramString);
    if (Math.abs(bigDecimal.scale()) >= 10000L)
      throw new NumberFormatException("Number has unsupported scale: " + paramString); 
    return bigDecimal;
  }
  
  public static BigInteger parseBigInteger(String paramString) throws NumberFormatException {
    checkNumberStringLength(paramString);
    return new BigInteger(paramString);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\NumberLimits.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */