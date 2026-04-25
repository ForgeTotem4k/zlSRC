package com.google.gson;

import java.lang.reflect.Field;

public interface FieldNamingStrategy {
  String translateName(Field paramField);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\FieldNamingStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */