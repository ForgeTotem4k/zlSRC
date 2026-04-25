package com.google.gson;

import java.lang.reflect.Field;
import java.util.Locale;

public enum FieldNamingPolicy implements FieldNamingStrategy {
  IDENTITY {
    public String translateName(Field param1Field) {
      return param1Field.getName();
    }
    
    static {
    
    }
  },
  UPPER_CAMEL_CASE {
    public String translateName(Field param1Field) {
      return null.upperCaseFirstLetter(param1Field.getName());
    }
    
    static {
    
    }
  },
  UPPER_CAMEL_CASE_WITH_SPACES {
    public String translateName(Field param1Field) {
      return null.upperCaseFirstLetter(null.separateCamelCase(param1Field.getName(), ' '));
    }
    
    static {
    
    }
  },
  UPPER_CASE_WITH_UNDERSCORES {
    public String translateName(Field param1Field) {
      return null.separateCamelCase(param1Field.getName(), '_').toUpperCase(Locale.ENGLISH);
    }
    
    static {
    
    }
  },
  LOWER_CASE_WITH_UNDERSCORES {
    public String translateName(Field param1Field) {
      return null.separateCamelCase(param1Field.getName(), '_').toLowerCase(Locale.ENGLISH);
    }
    
    static {
    
    }
  },
  LOWER_CASE_WITH_DASHES {
    public String translateName(Field param1Field) {
      return null.separateCamelCase(param1Field.getName(), '-').toLowerCase(Locale.ENGLISH);
    }
    
    static {
    
    }
  },
  LOWER_CASE_WITH_DOTS {
    public String translateName(Field param1Field) {
      return null.separateCamelCase(param1Field.getName(), '.').toLowerCase(Locale.ENGLISH);
    }
    
    static {
    
    }
  };
  
  static String separateCamelCase(String paramString, char paramChar) {
    StringBuilder stringBuilder = new StringBuilder();
    byte b = 0;
    int i = paramString.length();
    while (b < i) {
      char c = paramString.charAt(b);
      if (Character.isUpperCase(c) && stringBuilder.length() != 0)
        stringBuilder.append(paramChar); 
      stringBuilder.append(c);
      b++;
    } 
    return stringBuilder.toString();
  }
  
  static String upperCaseFirstLetter(String paramString) {
    int i = paramString.length();
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (Character.isLetter(c)) {
        if (Character.isUpperCase(c))
          return paramString; 
        char c1 = Character.toUpperCase(c);
        return (b == 0) ? (c1 + paramString.substring(1)) : (paramString.substring(0, b) + c1 + paramString.substring(b + 1));
      } 
    } 
    return paramString;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\FieldNamingPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */