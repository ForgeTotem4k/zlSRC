package pro.gravit.launcher.base.request;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class JsonResultSerializeAdapter implements JsonSerializer<WebSocketEvent> {
  private static final String PROP_NAME = "type";
  
  public JsonElement serialize(WebSocketEvent paramWebSocketEvent, Type paramType, JsonSerializationContext paramJsonSerializationContext) {
    JsonObject jsonObject = paramJsonSerializationContext.serialize(paramWebSocketEvent).getAsJsonObject();
    String str = paramWebSocketEvent.getType();
    jsonObject.add("type", (JsonElement)new JsonPrimitive(str));
    return (JsonElement)jsonObject;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\JsonResultSerializeAdapter.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */