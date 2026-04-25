package pro.gravit.launcher.gui.helper;

public class LookupException extends RuntimeException {
  public LookupException(String[] paramArrayOfString, int paramInt) {
    super(buildStack(paramArrayOfString, paramInt));
  }
  
  private static String buildStack(String[] paramArrayOfString, int paramInt) {
    StringBuilder stringBuilder = new StringBuilder("Lookup failed ");
    boolean bool = true;
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (!bool)
        stringBuilder.append("->"); 
      stringBuilder.append(paramArrayOfString[b]);
      if (b == paramInt)
        stringBuilder.append("(E)"); 
      bool = false;
    } 
    return stringBuilder.toString();
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\helper\LookupHelper$LookupException.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */