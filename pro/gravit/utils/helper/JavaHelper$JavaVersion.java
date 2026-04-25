package pro.gravit.utils.helper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class JavaVersion {
  public final Path jvmDir;
  
  public final int version;
  
  public final int build;
  
  public final JVMHelper.ARCH arch;
  
  public final List<String> modules;
  
  public boolean enabledJavaFX;
  
  public JavaVersion(Path paramPath, int paramInt1, int paramInt2, JVMHelper.ARCH paramARCH, boolean paramBoolean) {
    this.jvmDir = paramPath;
    this.version = paramInt1;
    this.build = paramInt2;
    this.arch = paramARCH;
    this.enabledJavaFX = paramBoolean;
    if (paramInt1 > 8) {
      this.modules = JavaHelper.javaFxModules;
    } else {
      this.modules = Collections.unmodifiableList(new ArrayList<>());
    } 
  }
  
  public JavaVersion(Path paramPath, int paramInt1, int paramInt2, JVMHelper.ARCH paramARCH, List<String> paramList, boolean paramBoolean) {
    this.jvmDir = paramPath;
    this.version = paramInt1;
    this.build = paramInt2;
    this.arch = paramARCH;
    this.modules = paramList;
    this.enabledJavaFX = paramBoolean;
  }
  
  public static JavaVersion getCurrentJavaVersion() {
    return new JavaVersion(Paths.get(System.getProperty("java.home"), new String[0]), JVMHelper.getVersion(), JVMHelper.JVM_BUILD, JVMHelper.ARCH_TYPE, isCurrentJavaSupportJavaFX());
  }
  
  private static boolean isCurrentJavaSupportJavaFX() {
    try {
      Class.forName("javafx.application.Application");
      return true;
    } catch (ClassNotFoundException classNotFoundException) {
      if (JVMHelper.getVersion() > 8) {
        Path path = Paths.get(System.getProperty("java.home"), new String[0]);
        return (JavaHelper.tryFindModule(path, "javafx.base") != null);
      } 
      return false;
    } 
  }
  
  public static JavaVersion getByPath(Path paramPath) {
    JavaVersion javaVersion1 = JavaHelper.tryFindJavaByPath(paramPath);
    if (javaVersion1 != null)
      return javaVersion1; 
    Path path = paramPath.resolve("release");
    JavaHelper.JavaVersionAndBuild javaVersionAndBuild = null;
    ArrayList arrayList = null;
    JVMHelper.ARCH aRCH = JVMHelper.ARCH_TYPE;
    if (IOHelper.isFile(path))
      try {
        Properties properties = new Properties();
        properties.load(IOHelper.newReader(path));
        String str1 = getProperty(properties, "JAVA_VERSION");
        if (str1 != null)
          javaVersionAndBuild = JavaHelper.getJavaVersion(str1); 
        try {
          String str = getProperty(properties, "OS_ARCH");
          if (str != null)
            aRCH = JVMHelper.getArch(str); 
        } catch (Throwable throwable) {}
        String str2 = getProperty(properties, "MODULES");
        if (str2 != null)
          arrayList = new ArrayList(Arrays.asList((Object[])str2.split(" "))); 
      } catch (IOException iOException) {} 
    if (javaVersionAndBuild == null)
      javaVersionAndBuild = new JavaHelper.JavaVersionAndBuild(isExistExtJavaLibrary(paramPath, "rt") ? 8 : 9, 0); 
    JavaVersion javaVersion2 = new JavaVersion(paramPath, javaVersionAndBuild.version, javaVersionAndBuild.build, aRCH, false);
    if (javaVersionAndBuild.version <= 8) {
      javaVersion2.enabledJavaFX = isExistExtJavaLibrary(paramPath, "jfxrt");
    } else {
      if (arrayList != null)
        javaVersion2.enabledJavaFX = arrayList.contains("javafx.base"); 
      if (!javaVersion2.enabledJavaFX) {
        javaVersion2.enabledJavaFX = (JavaHelper.tryFindModule(paramPath, "javafx.base") != null);
        if (!javaVersion2.enabledJavaFX)
          javaVersion2.enabledJavaFX = (JavaHelper.tryFindModule(paramPath.resolve("jre"), "javafx.base") != null); 
      } 
    } 
    return javaVersion2;
  }
  
  private static String getProperty(Properties paramProperties, String paramString) {
    String str = paramProperties.getProperty(paramString);
    return (str == null) ? null : str.replaceAll("\"", "");
  }
  
  public static boolean isExistExtJavaLibrary(Path paramPath, String paramString) {
    Path path1 = paramPath.resolve("lib").resolve("ext").resolve(paramString.concat(".jar"));
    Path path2 = paramPath.resolve("lib").resolve(paramString.concat(".jar"));
    Path path3 = paramPath.resolve("jre").resolve("lib").resolve("ext").resolve(paramString.concat(".jar"));
    Path path4 = paramPath.resolve("jre").resolve("lib").resolve(paramString.concat(".jar"));
    return (IOHelper.isFile(path1) || IOHelper.isFile(path3) || IOHelper.isFile(path4) || IOHelper.isFile(path2));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\JavaHelper$JavaVersion.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */