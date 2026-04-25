package com.google.gson.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PreJava9DateFormatProvider {
  public static DateFormat getUsDateTimeFormat(int paramInt1, int paramInt2) {
    String str = getDatePartOfDateTimePattern(paramInt1) + " " + getTimePartOfDateTimePattern(paramInt2);
    return new SimpleDateFormat(str, Locale.US);
  }
  
  private static String getDatePartOfDateTimePattern(int paramInt) {
    switch (paramInt) {
      case 3:
        return "M/d/yy";
      case 2:
        return "MMM d, yyyy";
      case 1:
        return "MMMM d, yyyy";
      case 0:
        return "EEEE, MMMM d, yyyy";
    } 
    throw new IllegalArgumentException("Unknown DateFormat style: " + paramInt);
  }
  
  private static String getTimePartOfDateTimePattern(int paramInt) {
    switch (paramInt) {
      case 3:
        return "h:mm a";
      case 2:
        return "h:mm:ss a";
      case 0:
      case 1:
        return "h:mm:ss a z";
    } 
    throw new IllegalArgumentException("Unknown DateFormat style: " + paramInt);
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\PreJava9DateFormatProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */