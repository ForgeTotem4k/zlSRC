package pro.gravit.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class UniversalJsonAdapter<R> implements JsonSerializer<R>, JsonDeserializer<R> {
  public final ProviderMap<R> providerMap;
  
  public final String name;
  
  public final String PROP_NAME;
  
  public Class<? extends R> defaultClass;
  
  public UniversalJsonAdapter(ProviderMap<R> paramProviderMap) {
    this.providerMap = paramProviderMap;
    this.name = paramProviderMap.getName();
    this.PROP_NAME = "type";
  }
  
  public UniversalJsonAdapter(ProviderMap<R> paramProviderMap, String paramString) {
    this.providerMap = paramProviderMap;
    this.name = paramProviderMap.getName();
    this.PROP_NAME = paramString;
  }
  
  public UniversalJsonAdapter(ProviderMap<R> paramProviderMap, String paramString, Class<? extends R> paramClass) {
    this.providerMap = paramProviderMap;
    this.name = paramString;
    this.defaultClass = paramClass;
    this.PROP_NAME = "type";
  }
  
  public UniversalJsonAdapter(ProviderMap<R> paramProviderMap, Class<? extends R> paramClass) {
    this.providerMap = paramProviderMap;
    this.defaultClass = paramClass;
    this.name = paramProviderMap.getName();
    this.PROP_NAME = "type";
  }
  
  public UniversalJsonAdapter(ProviderMap<R> paramProviderMap, String paramString1, String paramString2, Class<? extends R> paramClass) {
    this.providerMap = paramProviderMap;
    this.name = paramString1;
    this.PROP_NAME = paramString2;
    this.defaultClass = paramClass;
  }
  
  public R deserialize(JsonElement paramJsonElement, Type paramType, JsonDeserializationContext paramJsonDeserializationContext) throws JsonParseException {
    String str = paramJsonElement.getAsJsonObject().getAsJsonPrimitive(this.PROP_NAME).getAsString();
    if (str == null)
      throw new JsonParseException(String.format("%s: missing type property", new Object[] { this.name })); 
    Class<? extends R> clazz = this.providerMap.getClass(str);
    if (clazz == null) {
      if (this.defaultClass != null)
        return (R)paramJsonDeserializationContext.deserialize(paramJsonElement, this.defaultClass); 
      throw new JsonParseException(String.format("%s: type %s not registered", new Object[] { this.name, str }));
    } 
    return (R)paramJsonDeserializationContext.deserialize(paramJsonElement, clazz);
  }
  
  public JsonElement serialize(R paramR, Type paramType, JsonSerializationContext paramJsonSerializationContext) {
    JsonObject jsonObject = paramJsonSerializationContext.serialize(paramR).getAsJsonObject();
    String str = this.providerMap.getName((Class)paramR.getClass());
    if (str == null && paramR instanceof TypeSerializeInterface)
      str = ((TypeSerializeInterface)paramR).getType(); 
    if (str != null) {
      jsonObject.add(this.PROP_NAME, (JsonElement)new JsonPrimitive(str));
    } else {
      throw new JsonParseException(String.format("Class %s not registered", new Object[] { paramR.getClass().getName() }));
    } 
    return (JsonElement)jsonObject;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\UniversalJsonAdapter.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */