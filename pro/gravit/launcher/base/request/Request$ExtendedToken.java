package pro.gravit.launcher.base.request;

public class ExtendedToken {
  public final String token;
  
  public final long expire;
  
  public ExtendedToken(String paramString, long paramLong) {
    this.token = paramString;
    long l = System.currentTimeMillis();
    this.expire = (paramLong < l / 2L) ? (l + paramLong) : paramLong;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\Request$ExtendedToken.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */