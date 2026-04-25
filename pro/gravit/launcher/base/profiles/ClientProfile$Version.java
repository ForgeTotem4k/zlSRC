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
import java.util.Arrays;

public class Version implements Comparable<ClientProfile.Version> {
  private final long[] data;
  
  private final String original;
  
  private final boolean isObjectSerialized;
  
  public static Version of(String paramString) {
    String str = paramString.replaceAll("[^.0-9]", ".");
    String[] arrayOfString = str.split("\\.");
    return new Version(Arrays.<String>stream(arrayOfString).filter(paramString -> !paramString.isEmpty()).mapToLong(Long::parseLong).toArray(), paramString);
  }
  
  private Version(long[] paramArrayOflong, String paramString) {
    this.data = paramArrayOflong;
    this.original = paramString;
    this.isObjectSerialized = false;
  }
  
  public Version(long[] paramArrayOflong, String paramString, boolean paramBoolean) {
    this.data = paramArrayOflong;
    this.original = paramString;
    this.isObjectSerialized = paramBoolean;
  }
  
  public int compareTo(Version paramVersion) {
    int i = 0;
    if (this.data.length == paramVersion.data.length) {
      for (byte b = 0; b < this.data.length; b++) {
        i = Long.compare(this.data[b], paramVersion.data[b]);
        if (i != 0)
          return i; 
      } 
    } else if (this.data.length < paramVersion.data.length) {
      int j;
      for (j = 0; j < this.data.length; j++) {
        i = Long.compare(this.data[j], paramVersion.data[j]);
        if (i != 0)
          return i; 
      } 
      for (j = this.data.length; j < paramVersion.data.length; j++) {
        if (paramVersion.data[j] > 0L)
          return -1; 
      } 
    } else {
      int j;
      for (j = 0; j < paramVersion.data.length; j++) {
        i = Long.compare(this.data[j], paramVersion.data[j]);
        if (i != 0)
          return i; 
      } 
      for (j = paramVersion.data.length; j < this.data.length; j++) {
        if (this.data[j] > 0L)
          return 1; 
      } 
    } 
    return i;
  }
  
  public String toCleanString() {
    return join(this.data);
  }
  
  private static String join(long[] paramArrayOflong) {
    return String.join(".", (CharSequence[])Arrays.stream(paramArrayOflong).mapToObj(String::valueOf).toArray(paramInt -> new String[paramInt]));
  }
  
  public String toString() {
    return this.original;
  }
  
  public static class GsonSerializer implements JsonSerializer<Version>, JsonDeserializer<Version> {
    public ClientProfile.Version deserialize(JsonElement param2JsonElement, Type param2Type, JsonDeserializationContext param2JsonDeserializationContext) throws JsonParseException {
      if (param2JsonElement.isJsonObject()) {
        JsonObject jsonObject = param2JsonElement.getAsJsonObject();
        String str = jsonObject.get("name").getAsString();
        long[] arrayOfLong = (long[])param2JsonDeserializationContext.deserialize(jsonObject.get("data"), long[].class);
        return new ClientProfile.Version(arrayOfLong, str, true);
      } 
      if (param2JsonElement.isJsonArray()) {
        long[] arrayOfLong = (long[])param2JsonDeserializationContext.deserialize(param2JsonElement, long[].class);
        return new ClientProfile.Version(arrayOfLong, ClientProfile.Version.join(arrayOfLong), false);
      } 
      return ClientProfile.Version.of(param2JsonElement.getAsString());
    }
    
    public JsonElement serialize(ClientProfile.Version param2Version, Type param2Type, JsonSerializationContext param2JsonSerializationContext) {
      if (param2Version.isObjectSerialized) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("name", (JsonElement)new JsonPrimitive(param2Version.original));
        JsonArray jsonArray = new JsonArray();
        for (long l : param2Version.data)
          jsonArray.add(Long.valueOf(l)); 
        jsonObject.add("data", (JsonElement)jsonArray);
        return (JsonElement)jsonObject;
      } 
      return (JsonElement)new JsonPrimitive(param2Version.toString());
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\profiles\ClientProfile$Version.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */