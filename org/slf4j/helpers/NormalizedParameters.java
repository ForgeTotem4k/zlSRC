package org.slf4j.helpers;

import org.slf4j.event.LoggingEvent;

public class NormalizedParameters {
  final String message;
  
  final Object[] arguments;
  
  final Throwable throwable;
  
  public NormalizedParameters(String paramString, Object[] paramArrayOfObject, Throwable paramThrowable) {
    this.message = paramString;
    this.arguments = paramArrayOfObject;
    this.throwable = paramThrowable;
  }
  
  public NormalizedParameters(String paramString, Object[] paramArrayOfObject) {
    this(paramString, paramArrayOfObject, null);
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public Object[] getArguments() {
    return this.arguments;
  }
  
  public Throwable getThrowable() {
    return this.throwable;
  }
  
  public static Throwable getThrowableCandidate(Object[] paramArrayOfObject) {
    if (paramArrayOfObject == null || paramArrayOfObject.length == 0)
      return null; 
    Object object = paramArrayOfObject[paramArrayOfObject.length - 1];
    return (object instanceof Throwable) ? (Throwable)object : null;
  }
  
  public static Object[] trimmedCopy(Object[] paramArrayOfObject) {
    if (paramArrayOfObject == null || paramArrayOfObject.length == 0)
      throw new IllegalStateException("non-sensical empty or null argument array"); 
    int i = paramArrayOfObject.length - 1;
    Object[] arrayOfObject = new Object[i];
    if (i > 0)
      System.arraycopy(paramArrayOfObject, 0, arrayOfObject, 0, i); 
    return arrayOfObject;
  }
  
  public static NormalizedParameters normalize(String paramString, Object[] paramArrayOfObject, Throwable paramThrowable) {
    if (paramThrowable != null)
      return new NormalizedParameters(paramString, paramArrayOfObject, paramThrowable); 
    if (paramArrayOfObject == null || paramArrayOfObject.length == 0)
      return new NormalizedParameters(paramString, paramArrayOfObject, paramThrowable); 
    Throwable throwable = getThrowableCandidate(paramArrayOfObject);
    if (throwable != null) {
      Object[] arrayOfObject = MessageFormatter.trimmedCopy(paramArrayOfObject);
      return new NormalizedParameters(paramString, arrayOfObject, throwable);
    } 
    return new NormalizedParameters(paramString, paramArrayOfObject);
  }
  
  public static NormalizedParameters normalize(LoggingEvent paramLoggingEvent) {
    return normalize(paramLoggingEvent.getMessage(), paramLoggingEvent.getArgumentArray(), paramLoggingEvent.getThrowable());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\helpers\NormalizedParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */