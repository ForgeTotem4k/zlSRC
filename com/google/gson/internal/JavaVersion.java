package com.google.gson.internal;

public final class JavaVersion {
  private static final int majorJavaVersion = determineMajorJavaVersion();
  
  private static int determineMajorJavaVersion() {
    String str = System.getProperty("java.version");
    return parseMajorJavaVersion(str);
  }
  
  static int parseMajorJavaVersion(String paramString) {
    int i = parseDotted(paramString);
    if (i == -1)
      i = extractBeginningInt(paramString); 
    return (i == -1) ? 6 : i;
  }
  
  private static int parseDotted(String paramString) {
    try {
      String[] arrayOfString = paramString.split("[._]", 3);
      int i = Integer.parseInt(arrayOfString[0]);
      return (i == 1 && arrayOfString.length > 1) ? Integer.parseInt(arrayOfString[1]) : i;
    } catch (NumberFormatException numberFormatException) {
      return -1;
    } 
  }
  
  private static int extractBeginningInt(String paramString) {
    try {
      StringBuilder stringBuilder = new StringBuilder();
      byte b = 0;
      while (b < paramString.length()) {
        char c = paramString.charAt(b);
        if (Character.isDigit(c)) {
          stringBuilder.append(c);
          b++;
        } 
      } 
      return Integer.parseInt(stringBuilder.toString());
    } catch (NumberFormatException numberFormatException) {
      return -1;
    } 
  }
  
  public static int getMajorJavaVersion() {
    return majorJavaVersion;
  }
  
  public static boolean isJava9OrLater() {
    return (majorJavaVersion >= 9);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\JavaVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */