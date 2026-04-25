package com.google.gson;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.internal.LinkedTreeMap;
import java.util.Map;
import java.util.Set;

public final class JsonObject extends JsonElement {
  private final LinkedTreeMap<String, JsonElement> members = new LinkedTreeMap(false);
  
  public JsonObject deepCopy() {
    JsonObject jsonObject = new JsonObject();
    for (Map.Entry entry : this.members.entrySet())
      jsonObject.add((String)entry.getKey(), ((JsonElement)entry.getValue()).deepCopy()); 
    return jsonObject;
  }
  
  public void add(String paramString, JsonElement paramJsonElement) {
    this.members.put(paramString, (paramJsonElement == null) ? JsonNull.INSTANCE : paramJsonElement);
  }
  
  @CanIgnoreReturnValue
  public JsonElement remove(String paramString) {
    return (JsonElement)this.members.remove(paramString);
  }
  
  public void addProperty(String paramString1, String paramString2) {
    add(paramString1, (paramString2 == null) ? JsonNull.INSTANCE : new JsonPrimitive(paramString2));
  }
  
  public void addProperty(String paramString, Number paramNumber) {
    add(paramString, (paramNumber == null) ? JsonNull.INSTANCE : new JsonPrimitive(paramNumber));
  }
  
  public void addProperty(String paramString, Boolean paramBoolean) {
    add(paramString, (paramBoolean == null) ? JsonNull.INSTANCE : new JsonPrimitive(paramBoolean));
  }
  
  public void addProperty(String paramString, Character paramCharacter) {
    add(paramString, (paramCharacter == null) ? JsonNull.INSTANCE : new JsonPrimitive(paramCharacter));
  }
  
  public Set<Map.Entry<String, JsonElement>> entrySet() {
    return this.members.entrySet();
  }
  
  public Set<String> keySet() {
    return this.members.keySet();
  }
  
  public int size() {
    return this.members.size();
  }
  
  public boolean isEmpty() {
    return this.members.isEmpty();
  }
  
  public boolean has(String paramString) {
    return this.members.containsKey(paramString);
  }
  
  public JsonElement get(String paramString) {
    return (JsonElement)this.members.get(paramString);
  }
  
  public JsonPrimitive getAsJsonPrimitive(String paramString) {
    return (JsonPrimitive)this.members.get(paramString);
  }
  
  public JsonArray getAsJsonArray(String paramString) {
    return (JsonArray)this.members.get(paramString);
  }
  
  public JsonObject getAsJsonObject(String paramString) {
    return (JsonObject)this.members.get(paramString);
  }
  
  public Map<String, JsonElement> asMap() {
    return (Map<String, JsonElement>)this.members;
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject == this || (paramObject instanceof JsonObject && ((JsonObject)paramObject).members.equals(this.members)));
  }
  
  public int hashCode() {
    return this.members.hashCode();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\JsonObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */