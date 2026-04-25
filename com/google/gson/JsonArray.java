package com.google.gson;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.internal.NonNullElementWrapperList;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class JsonArray extends JsonElement implements Iterable<JsonElement> {
  private final ArrayList<JsonElement> elements = new ArrayList<>();
  
  public JsonArray() {}
  
  public JsonArray(int paramInt) {}
  
  public JsonArray deepCopy() {
    if (!this.elements.isEmpty()) {
      JsonArray jsonArray = new JsonArray(this.elements.size());
      for (JsonElement jsonElement : this.elements)
        jsonArray.add(jsonElement.deepCopy()); 
      return jsonArray;
    } 
    return new JsonArray();
  }
  
  public void add(Boolean paramBoolean) {
    this.elements.add((paramBoolean == null) ? JsonNull.INSTANCE : new JsonPrimitive(paramBoolean));
  }
  
  public void add(Character paramCharacter) {
    this.elements.add((paramCharacter == null) ? JsonNull.INSTANCE : new JsonPrimitive(paramCharacter));
  }
  
  public void add(Number paramNumber) {
    this.elements.add((paramNumber == null) ? JsonNull.INSTANCE : new JsonPrimitive(paramNumber));
  }
  
  public void add(String paramString) {
    this.elements.add((paramString == null) ? JsonNull.INSTANCE : new JsonPrimitive(paramString));
  }
  
  public void add(JsonElement paramJsonElement) {
    if (paramJsonElement == null)
      paramJsonElement = JsonNull.INSTANCE; 
    this.elements.add(paramJsonElement);
  }
  
  public void addAll(JsonArray paramJsonArray) {
    this.elements.addAll(paramJsonArray.elements);
  }
  
  @CanIgnoreReturnValue
  public JsonElement set(int paramInt, JsonElement paramJsonElement) {
    return this.elements.set(paramInt, (paramJsonElement == null) ? JsonNull.INSTANCE : paramJsonElement);
  }
  
  @CanIgnoreReturnValue
  public boolean remove(JsonElement paramJsonElement) {
    return this.elements.remove(paramJsonElement);
  }
  
  @CanIgnoreReturnValue
  public JsonElement remove(int paramInt) {
    return this.elements.remove(paramInt);
  }
  
  public boolean contains(JsonElement paramJsonElement) {
    return this.elements.contains(paramJsonElement);
  }
  
  public int size() {
    return this.elements.size();
  }
  
  public boolean isEmpty() {
    return this.elements.isEmpty();
  }
  
  public Iterator<JsonElement> iterator() {
    return this.elements.iterator();
  }
  
  public JsonElement get(int paramInt) {
    return this.elements.get(paramInt);
  }
  
  private JsonElement getAsSingleElement() {
    int i = this.elements.size();
    if (i == 1)
      return this.elements.get(0); 
    throw new IllegalStateException("Array must have size 1, but has size " + i);
  }
  
  public Number getAsNumber() {
    return getAsSingleElement().getAsNumber();
  }
  
  public String getAsString() {
    return getAsSingleElement().getAsString();
  }
  
  public double getAsDouble() {
    return getAsSingleElement().getAsDouble();
  }
  
  public BigDecimal getAsBigDecimal() {
    return getAsSingleElement().getAsBigDecimal();
  }
  
  public BigInteger getAsBigInteger() {
    return getAsSingleElement().getAsBigInteger();
  }
  
  public float getAsFloat() {
    return getAsSingleElement().getAsFloat();
  }
  
  public long getAsLong() {
    return getAsSingleElement().getAsLong();
  }
  
  public int getAsInt() {
    return getAsSingleElement().getAsInt();
  }
  
  public byte getAsByte() {
    return getAsSingleElement().getAsByte();
  }
  
  @Deprecated
  public char getAsCharacter() {
    return getAsSingleElement().getAsCharacter();
  }
  
  public short getAsShort() {
    return getAsSingleElement().getAsShort();
  }
  
  public boolean getAsBoolean() {
    return getAsSingleElement().getAsBoolean();
  }
  
  public List<JsonElement> asList() {
    return (List<JsonElement>)new NonNullElementWrapperList(this.elements);
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject == this || (paramObject instanceof JsonArray && ((JsonArray)paramObject).elements.equals(this.elements)));
  }
  
  public int hashCode() {
    return this.elements.hashCode();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\JsonArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */