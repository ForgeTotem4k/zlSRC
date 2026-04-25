package pro.gravit.launcher.core.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import pro.gravit.launcher.core.hasher.HashedEntry;
import pro.gravit.launcher.core.hasher.HashedEntryAdapter;
import pro.gravit.utils.helper.CommonHelper;

public class GsonManager {
  public GsonBuilder gsonBuilder;
  
  public Gson gson;
  
  public GsonBuilder configGsonBuilder;
  
  public Gson configGson;
  
  public void initGson() {
    this.gsonBuilder = CommonHelper.newBuilder();
    this.configGsonBuilder = CommonHelper.newBuilder();
    this.configGsonBuilder.setPrettyPrinting().disableHtmlEscaping();
    registerAdapters(this.gsonBuilder);
    registerAdapters(this.configGsonBuilder);
    preConfigGson(this.configGsonBuilder);
    preGson(this.gsonBuilder);
    this.gson = this.gsonBuilder.create();
    this.configGson = this.configGsonBuilder.create();
  }
  
  public void registerAdapters(GsonBuilder paramGsonBuilder) {
    paramGsonBuilder.registerTypeAdapter(HashedEntry.class, new HashedEntryAdapter());
  }
  
  public void preConfigGson(GsonBuilder paramGsonBuilder) {}
  
  public void preGson(GsonBuilder paramGsonBuilder) {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\managers\GsonManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */