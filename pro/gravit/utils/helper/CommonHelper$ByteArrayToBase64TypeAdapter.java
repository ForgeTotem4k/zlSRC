package pro.gravit.utils.helper;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Base64;

class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
  private static final ByteArrayToBase64TypeAdapter INSTANCE = new ByteArrayToBase64TypeAdapter();
  
  private final Base64.Decoder decoder = Base64.getUrlDecoder();
  
  private final Base64.Encoder encoder = Base64.getUrlEncoder();
  
  public byte[] deserialize(JsonElement paramJsonElement, Type paramType, JsonDeserializationContext paramJsonDeserializationContext) throws JsonParseException {
    if (paramJsonElement.isJsonArray()) {
      JsonArray jsonArray = paramJsonElement.getAsJsonArray();
      byte[] arrayOfByte = new byte[jsonArray.size()];
      for (byte b = 0; b < arrayOfByte.length; b++)
        arrayOfByte[b] = jsonArray.get(b).getAsByte(); 
      return arrayOfByte;
    } 
    return this.decoder.decode(paramJsonElement.getAsString());
  }
  
  public JsonElement serialize(byte[] paramArrayOfbyte, Type paramType, JsonSerializationContext paramJsonSerializationContext) {
    return (JsonElement)new JsonPrimitive(this.encoder.encodeToString(paramArrayOfbyte));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\CommonHelper$ByteArrayToBase64TypeAdapter.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */