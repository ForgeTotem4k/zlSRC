package pro.gravit.launcher.base.request;

import java.io.IOException;

public final class RequestException extends IOException {
  public RequestException(String paramString) {
    super(paramString);
  }
  
  public RequestException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }
  
  public RequestException(Throwable paramThrowable) {
    super(paramThrowable);
  }
  
  public String toString() {
    return getMessage();
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\RequestException.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */