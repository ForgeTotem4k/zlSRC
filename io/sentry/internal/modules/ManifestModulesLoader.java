package io.sentry.internal.modules;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.util.ClassLoaderUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Experimental
@Internal
public final class ManifestModulesLoader extends ModulesLoader {
  private final Pattern URL_LIB_PATTERN = Pattern.compile(".*/(.+)!/META-INF/MANIFEST.MF");
  
  private final Pattern NAME_AND_VERSION = Pattern.compile("(.*?)-(\\d+\\.\\d+.*).jar");
  
  private final ClassLoader classLoader;
  
  public ManifestModulesLoader(@NotNull ILogger paramILogger) {
    this(ManifestModulesLoader.class.getClassLoader(), paramILogger);
  }
  
  ManifestModulesLoader(@Nullable ClassLoader paramClassLoader, @NotNull ILogger paramILogger) {
    super(paramILogger);
    this.classLoader = ClassLoaderUtils.classLoaderOrDefault(paramClassLoader);
  }
  
  protected Map<String, String> loadModules() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    List<Module> list = detectModulesViaManifestFiles();
    for (Module module : list)
      hashMap.put(module.name, module.version); 
    return (Map)hashMap;
  }
  
  @NotNull
  private List<Module> detectModulesViaManifestFiles() {
    ArrayList<Module> arrayList = new ArrayList();
    try {
      Enumeration<URL> enumeration = this.classLoader.getResources("META-INF/MANIFEST.MF");
      while (enumeration.hasMoreElements()) {
        URL uRL = enumeration.nextElement();
        String str = extractDependencyNameFromUrl(uRL);
        Module module = convertOriginalNameToModule(str);
        if (module != null)
          arrayList.add(module); 
      } 
    } catch (Throwable throwable) {
      this.logger.log(SentryLevel.ERROR, "Unable to detect modules via manifest files.", throwable);
    } 
    return arrayList;
  }
  
  @Nullable
  private Module convertOriginalNameToModule(@Nullable String paramString) {
    if (paramString == null)
      return null; 
    Matcher matcher = this.NAME_AND_VERSION.matcher(paramString);
    if (matcher.matches() && matcher.groupCount() == 2) {
      String str1 = matcher.group(1);
      String str2 = matcher.group(2);
      return new Module(str1, str2);
    } 
    return null;
  }
  
  @Nullable
  private String extractDependencyNameFromUrl(@NotNull URL paramURL) {
    String str = paramURL.toString();
    Matcher matcher = this.URL_LIB_PATTERN.matcher(str);
    return (matcher.matches() && matcher.groupCount() == 1) ? matcher.group(1) : null;
  }
  
  private static final class Module {
    @NotNull
    private final String name;
    
    @NotNull
    private final String version;
    
    public Module(@NotNull String param1String1, @NotNull String param1String2) {
      this.name = param1String1;
      this.version = param1String2;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\internal\modules\ManifestModulesLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */