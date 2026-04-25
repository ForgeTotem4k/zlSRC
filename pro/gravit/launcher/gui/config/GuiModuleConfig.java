package pro.gravit.launcher.gui.config;

import com.google.gson.internal.LinkedTreeMap;
import java.util.HashMap;
import java.util.Map;

public class GuiModuleConfig {
  public String createAccountURL;
  
  public String forgotPassURL;
  
  public String hastebinServer;
  
  public boolean forceDownloadJava;
  
  public Map<String, String> javaList;
  
  public boolean lazy;
  
  public boolean disableOfflineMode;
  
  public boolean disableDebugPermissions;
  
  public boolean autoAuth;
  
  public String locale;
  
  public int downloadThreads;
  
  public GuiModuleConfig() {
    4;
    this.downloadThreads = 4;
    this.createAccountURL = "https://gravit.pro/createAccount.php";
    this.forgotPassURL = "https://gravit.pro/fogotPass.php";
    this.hastebinServer = "https://hastebin.com";
    this.forceDownloadJava = true;
    (new LinkedTreeMap()).put("java21-windows-x64", "Java 21 b6 mustdie X86_64 javafx true");
    (new LinkedTreeMap()).put("jre-8u202-win32", "Java 8 b6 mustdie X86 javafx true");
    (new LinkedTreeMap()).put("jre-8u202-win64", "Java 8 b6 mustdie X86_64 javafx true");
    (new LinkedTreeMap()).put("java21-windows-arm-64", "Java 21 b6 mustdie ARM64 javafx false");
    (new LinkedTreeMap()).put("java21-linux-x86", "Java 21 b6 linux X86 javafx false");
    (new LinkedTreeMap()).put("java21-linux-x86-64", "Java 21 b6 linux X86_64 javafx false");
    (new LinkedTreeMap()).put("java21-linux-arm-64", "Java 21 b6 linux ARM64 javafx false");
    (new LinkedTreeMap()).put("java21-macosx-64", "Java 21 b6 macosx X86_64 javafx true");
    (new LinkedTreeMap()).put("java21-macosx-arm-64", "Java 21 b6 macosx ARM64 javafx true");
    (new LinkedTreeMap()).put("java24-windows-x64", "Java 24 b6 mustdie X86_64 javafx false");
    (new LinkedTreeMap()).put("java24-linux-x86-64", "Java 24 b6 linux X86_64 javafx false");
    (new LinkedTreeMap()).put("java24-macosx-64", "Java 24 b6 macosx X86_64 javafx false");
    (new LinkedTreeMap()).put("java24-windows-arm-64", "Java 24 b6 mustdie ARM64 javafx false");
    this.javaList = (Map<String, String>)new LinkedTreeMap();
    this.lazy = false;
    this.disableOfflineMode = false;
    this.disableDebugPermissions = false;
    this.autoAuth = false;
    this.locale = "RUSSIAN";
  }
  
  public static Object getDefault() {
    GuiModuleConfig guiModuleConfig = new GuiModuleConfig();
    guiModuleConfig.createAccountURL = "https://gravit.pro/createAccount.php";
    guiModuleConfig.forgotPassURL = "https://gravit.pro/fogotPass.php";
    guiModuleConfig.hastebinServer = "https://hastebin.com";
    guiModuleConfig.lazy = false;
    guiModuleConfig.javaList = new HashMap<>();
    guiModuleConfig.disableOfflineMode = false;
    guiModuleConfig.autoAuth = false;
    guiModuleConfig.locale = "RUSSIAN";
    guiModuleConfig.downloadThreads = 4;
    return guiModuleConfig;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\config\GuiModuleConfig.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */