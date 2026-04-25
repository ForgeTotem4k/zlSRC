package com.google.gson;

public enum LongSerializationPolicy {
  DEFAULT {
    public JsonElement serialize(Long param1Long) {
      return (JsonElement)((param1Long == null) ? JsonNull.INSTANCE : new JsonPrimitive(param1Long));
    }
    
    static {
    
    }
  },
  STRING {
    public JsonElement serialize(Long param1Long) {
      return (JsonElement)((param1Long == null) ? JsonNull.INSTANCE : new JsonPrimitive(param1Long.toString()));
    }
    
    static {
    
    }
  };
  
  public abstract JsonElement serialize(Long paramLong);
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\LongSerializationPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */