package pro.gravit.launcher.base.profiles;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class GsonSerializer implements JsonSerializer<ClientProfile.Version>, JsonDeserializer<ClientProfile.Version> {
  public ClientProfile.Version deserialize(JsonElement paramJsonElement, Type paramType, JsonDeserializationContext paramJsonDeserializationContext) throws JsonParseException {
    if (paramJsonElement.isJsonObject()) {
      JsonObject jsonObject = paramJsonElement.getAsJsonObject();
      String str = jsonObject.get("name").getAsString();
      long[] arrayOfLong = (long[])paramJsonDeserializationContext.deserialize(jsonObject.get("data"), long[].class);
      return new ClientProfile.Version(arrayOfLong, str, true);
    } 
    if (paramJsonElement.isJsonArray()) {
      long[] arrayOfLong = (long[])paramJsonDeserializationContext.deserialize(paramJsonElement, long[].class);
      return new ClientProfile.Version(arrayOfLong, ClientProfile.Version.join(arrayOfLong), false);
    } 
    return ClientProfile.Version.of(paramJsonElement.getAsString());
  }
  
  public JsonElement serialize(ClientProfile.Version paramVersion, Type paramType, JsonSerializationContext paramJsonSerializationContext) {
    if (paramVersion.isObjectSerialized) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.add("name", (JsonElement)new JsonPrimitive(paramVersion.original));
      JsonArray jsonArray = new JsonArray();
      for (long l : paramVersion.data)
        jsonArray.add(Long.valueOf(l)); 
      jsonObject.add("data", (JsonElement)jsonArray);
      return (JsonElement)jsonObject;
    } 
    return (JsonElement)new JsonPrimitive(paramVersion.toString());
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\profiles\ClientProfile$Version$GsonSerializer.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */