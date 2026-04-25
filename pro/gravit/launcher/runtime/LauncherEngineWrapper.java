package pro.gravit.launcher.runtime;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.launch.ClassLoaderControl;
import pro.gravit.utils.launch.LaunchOptions;
import pro.gravit.utils.launch.ModuleLaunch;

@LauncherNetworkAPI
public class LauncherEngineWrapper {
  private static final List<String> modules = new ArrayList<>();
  
  public static void main(String[] paramArrayOfString) throws Throwable {
    ModuleLaunch moduleLaunch = new ModuleLaunch();
    LaunchOptions launchOptions = new LaunchOptions();
    launchOptions.disablePackageDelegateSupport = true;
    launchOptions.moduleConf = new LaunchOptions.ModuleConf();
    ArrayList<Path> arrayList = new ArrayList();
    arrayList.add(IOHelper.getCodeSource(LauncherEngine.class));
    Path path = Path.of(System.getProperty("java.home"), new String[0]).resolve("lib");
    for (String str : modules) {
      Path path1 = path.resolve(str.concat(".jar"));
      if (Files.exists(path1, new java.nio.file.LinkOption[0])) {
        launchOptions.moduleConf.modules.add(str);
        launchOptions.moduleConf.modulePath.add(path1.toAbsolutePath().toString());
      } 
    } 
    ClassLoaderControl classLoaderControl = moduleLaunch.init(arrayList, null, launchOptions);
    moduleLaunch.launch(LauncherEngine.class.getName(), null, List.of(paramArrayOfString));
  }
  
  static {
    modules.add("javafx.base");
    modules.add("javafx.graphics");
    modules.add("javafx.fxml");
    modules.add("javafx.controls");
    modules.add("javafx.media");
    modules.add("javafx.web");
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\LauncherEngineWrapper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */