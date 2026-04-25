package com.google.gson;

import java.lang.reflect.Type;

public interface JsonSerializationContext {
  JsonElement serialize(Object paramObject);
  
  JsonElement serialize(Object paramObject, Type paramType);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\JsonSerializationContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */