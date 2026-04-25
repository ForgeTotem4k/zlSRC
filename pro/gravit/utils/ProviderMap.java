package pro.gravit.utils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import pro.gravit.utils.helper.VerifyHelper;

public class ProviderMap<R> {
  protected final Map<String, Class<? extends R>> PROVIDERS = new ConcurrentHashMap<>(4);
  
  protected final String name;
  
  protected boolean registredProviders = false;
  
  public ProviderMap(String paramString) {
    this.name = paramString;
  }
  
  public ProviderMap() {
    this.name = "Unnamed";
  }
  
  public String getName() {
    return this.name;
  }
  
  public void register(String paramString, Class<? extends R> paramClass) {
    VerifyHelper.verifyIDName(paramString);
    VerifyHelper.putIfAbsent(this.PROVIDERS, paramString, Objects.<Class<?>>requireNonNull(paramClass, "adapter"), String.format("%s has been already registered: '%s'", new Object[] { this.name, paramString }));
  }
  
  public Class<? extends R> getClass(String paramString) {
    return this.PROVIDERS.get(paramString);
  }
  
  public String getName(Class<? extends R> paramClass) {
    for (Map.Entry<String, Class<? extends R>> entry : this.PROVIDERS.entrySet()) {
      if (((Class)entry.getValue()).equals(paramClass))
        return (String)entry.getKey(); 
    } 
    return null;
  }
  
  public Class<? extends R> unregister(String paramString) {
    return this.PROVIDERS.remove(paramString);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\ProviderMap.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */