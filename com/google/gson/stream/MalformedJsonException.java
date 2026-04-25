package com.google.gson.stream;

import java.io.IOException;

public final class MalformedJsonException extends IOException {
  private static final long serialVersionUID = 1L;
  
  public MalformedJsonException(String paramString) {
    super(paramString);
  }
  
  public MalformedJsonException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }
  
  public MalformedJsonException(Throwable paramThrowable) {
    super(paramThrowable);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\stream\MalformedJsonException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */