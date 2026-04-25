package pro.gravit.launcher.core.hasher;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class HashedEntryAdapter implements JsonSerializer<HashedEntry>, JsonDeserializer<HashedEntry> {
  private static final String PROP_NAME = "type";
  
  public HashedEntry deserialize(JsonElement paramJsonElement, Type paramType, JsonDeserializationContext paramJsonDeserializationContext) throws JsonParseException {
    Class<HashedFile> clazz;
    String str = paramJsonElement.getAsJsonObject().getAsJsonPrimitive("type").getAsString();
    Class<HashedDir> clazz1 = null;
    if (str.equals("dir"))
      clazz1 = HashedDir.class; 
    if (str.equals("file"))
      clazz = HashedFile.class; 
    return (HashedEntry)paramJsonDeserializationContext.deserialize(paramJsonElement, clazz);
  }
  
  public JsonElement serialize(HashedEntry paramHashedEntry, Type paramType, JsonSerializationContext paramJsonSerializationContext) {
    JsonObject jsonObject = paramJsonSerializationContext.serialize(paramHashedEntry).getAsJsonObject();
    HashedEntry.Type type = paramHashedEntry.getType();
    if (type == HashedEntry.Type.DIR)
      jsonObject.add("type", (JsonElement)new JsonPrimitive("dir")); 
    if (type == HashedEntry.Type.FILE)
      jsonObject.add("type", (JsonElement)new JsonPrimitive("file")); 
    return (JsonElement)jsonObject;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\hasher\HashedEntryAdapter.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */