package com.google.gson;

public final class JsonNull extends JsonElement {
  public static final JsonNull INSTANCE = new JsonNull();
  
  public JsonNull deepCopy() {
    return INSTANCE;
  }
  
  public int hashCode() {
    return JsonNull.class.hashCode();
  }
  
  public boolean equals(Object paramObject) {
    return paramObject instanceof JsonNull;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\JsonNull.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */