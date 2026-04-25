package pro.gravit.utils.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class JavaHelper {
  private static List<JavaVersion> javaVersionsCache;
  
  public static final List<String> javaFxModules = List.of("javafx.base", "javafx.graphics", "javafx.fxml", "javafx.controls", "javafx.swing", "javafx.media", "javafx.web");
  
  public static Path tryGetOpenJFXPath(Path paramPath) {
    String str = paramPath.getFileName().toString();
    Path path1 = paramPath.getParent();
    if (path1 == null)
      return null; 
    Path path2 = path1.resolve(str.replace("openjdk", "openjfx"));
    if (Files.isDirectory(path2, new java.nio.file.LinkOption[0]))
      return path2; 
    Path path3 = path1.resolve(str.replace("jdk", "openjfx"));
    if (Files.isDirectory(path3, new java.nio.file.LinkOption[0]))
      return path3; 
    if (JVMHelper.OS_TYPE == JVMHelper.OS.LINUX) {
      Path path = Paths.get("/usr/share/openjfx", new String[0]);
      if (Files.isDirectory(path, new java.nio.file.LinkOption[0]))
        return path; 
    } 
    return null;
  }
  
  public static Path tryFindModule(Path paramPath, String paramString) {
    Path path = paramPath.resolve(paramString.concat(".jar"));
    if (!IOHelper.isFile(path)) {
      path = paramPath.resolve("lib").resolve(paramString.concat(".jar"));
    } else {
      return path;
    } 
    return !IOHelper.isFile(path) ? null : path;
  }
  
  public static boolean tryAddModule(List<Path> paramList, String paramString, StringBuilder paramStringBuilder) {
    for (Path path1 : paramList) {
      if (path1 == null)
        continue; 
      Path path2 = tryFindModule(path1, paramString);
      if (path2 != null) {
        if (!paramStringBuilder.isEmpty())
          paramStringBuilder.append(File.pathSeparatorChar); 
        paramStringBuilder.append(path2.toAbsolutePath());
        return true;
      } 
    } 
    return false;
  }
  
  public static synchronized List<JavaVersion> findJava() {
    if (javaVersionsCache != null)
      return javaVersionsCache; 
    ArrayList<String> arrayList = new ArrayList(4);
    ArrayList<JavaVersion> arrayList1 = new ArrayList(4);
    tryAddJava(arrayList, arrayList1, JavaVersion.getCurrentJavaVersion());
    String[] arrayOfString = System.getenv("PATH").split((JVMHelper.OS_TYPE == JVMHelper.OS.MUSTDIE) ? ";" : ":");
    for (String str : arrayOfString) {
      try {
        Path path1 = Paths.get(str, new String[0]);
        Path path2 = (JVMHelper.OS_TYPE == JVMHelper.OS.MUSTDIE) ? path1.resolve("java.exe") : path1.resolve("java");
        if (Files.exists(path2, new java.nio.file.LinkOption[0])) {
          path2 = path2.toRealPath(new java.nio.file.LinkOption[0]);
          path1 = path2.getParent().getParent();
          if (path1 != null) {
            tryAddJava(arrayList, arrayList1, JavaVersion.getByPath(path1));
            trySearchJava(arrayList, arrayList1, path1.getParent());
          } 
        } 
      } catch (InvalidPathException|NullPointerException invalidPathException) {
      
      } catch (IOException iOException) {
        LogHelper.error(iOException);
      } 
    } 
    if (JVMHelper.OS_TYPE == JVMHelper.OS.MUSTDIE) {
      Path path = IOHelper.getRoot();
      try {
        trySearchJava(arrayList, arrayList1, path.resolve("Program Files").resolve("Java"));
        trySearchJava(arrayList, arrayList1, path.resolve("Program Files").resolve("AdoptOpenJDK"));
        trySearchJava(arrayList, arrayList1, path.resolve("Program Files").resolve("Eclipse Foundation"));
        trySearchJava(arrayList, arrayList1, path.resolve("Program Files").resolve("Eclipse Adoptium"));
        trySearchJava(arrayList, arrayList1, path.resolve("Program Files").resolve("BellSoft"));
      } catch (IOException iOException) {
        LogHelper.error(iOException);
      } 
    } else if (JVMHelper.OS_TYPE == JVMHelper.OS.LINUX) {
      try {
        trySearchJava(arrayList, arrayList1, Paths.get("/usr/lib/jvm", new String[0]));
      } catch (IOException iOException) {
        LogHelper.error(iOException);
      } 
    } 
    javaVersionsCache = arrayList1;
    return arrayList1;
  }
  
  private static JavaVersion tryFindJavaByPath(Path paramPath) {
    if (javaVersionsCache == null)
      return null; 
    for (JavaVersion javaVersion : javaVersionsCache) {
      if (javaVersion.jvmDir.equals(paramPath))
        return javaVersion; 
    } 
    return null;
  }
  
  public static void tryAddJava(List<String> paramList, List<JavaVersion> paramList1, JavaVersion paramJavaVersion) {
    if (paramJavaVersion == null)
      return; 
    Path path = paramJavaVersion.jvmDir.toAbsolutePath();
    try {
      path = path.toRealPath(new java.nio.file.LinkOption[0]);
    } catch (IOException iOException) {}
    String str = path.toString();
    if (paramList.contains(str))
      return; 
    paramList.add(str);
    paramList1.add(paramJavaVersion);
  }
  
  public static void trySearchJava(List<String> paramList, List<JavaVersion> paramList1, Path paramPath) throws IOException {
    if (paramPath == null || !Files.isDirectory(paramPath, new java.nio.file.LinkOption[0]))
      return; 
    Files.list(paramPath).filter(paramPath -> Files.exists(paramPath.resolve("bin").resolve((JVMHelper.OS_TYPE == JVMHelper.OS.MUSTDIE) ? "java.exe" : "java"), new java.nio.file.LinkOption[0])).forEach(paramPath -> {
          tryAddJava(paramList1, paramList2, JavaVersion.getByPath(paramPath));
          if (Files.exists(paramPath.resolve("jre"), new java.nio.file.LinkOption[0]))
            tryAddJava(paramList1, paramList2, JavaVersion.getByPath(paramPath.resolve("jre"))); 
        });
  }
  
  public static JavaVersionAndBuild getJavaVersion(String paramString) {
    JavaVersionAndBuild javaVersionAndBuild = new JavaVersionAndBuild();
    if (paramString.startsWith("1.")) {
      javaVersionAndBuild.version = Integer.parseInt(paramString.substring(2, 3));
      int i = paramString.indexOf('_');
      if (i != -1)
        javaVersionAndBuild.build = Integer.parseInt(paramString.substring(i + 1)); 
    } else {
      int i = paramString.indexOf(".");
      if (i != -1) {
        javaVersionAndBuild.version = Integer.parseInt(paramString.substring(0, i));
        i = paramString.lastIndexOf(".");
        javaVersionAndBuild.build = Integer.parseInt(paramString.substring(i + 1));
      } else {
        try {
          if (paramString.endsWith("-ea"))
            paramString = paramString.substring(0, paramString.length() - 3); 
          javaVersionAndBuild.version = Integer.parseInt(paramString);
          javaVersionAndBuild.build = 0;
        } catch (NumberFormatException numberFormatException) {}
      } 
    } 
    return javaVersionAndBuild;
  }
  
  public static class JavaVersion {
    public final Path jvmDir;
    
    public final int version;
    
    public final int build;
    
    public final JVMHelper.ARCH arch;
    
    public final List<String> modules;
    
    public boolean enabledJavaFX;
    
    public JavaVersion(Path param1Path, int param1Int1, int param1Int2, JVMHelper.ARCH param1ARCH, boolean param1Boolean) {
      this.jvmDir = param1Path;
      this.version = param1Int1;
      this.build = param1Int2;
      this.arch = param1ARCH;
      this.enabledJavaFX = param1Boolean;
      if (param1Int1 > 8) {
        this.modules = JavaHelper.javaFxModules;
      } else {
        this.modules = Collections.unmodifiableList(new ArrayList<>());
      } 
    }
    
    public JavaVersion(Path param1Path, int param1Int1, int param1Int2, JVMHelper.ARCH param1ARCH, List<String> param1List, boolean param1Boolean) {
      this.jvmDir = param1Path;
      this.version = param1Int1;
      this.build = param1Int2;
      this.arch = param1ARCH;
      this.modules = param1List;
      this.enabledJavaFX = param1Boolean;
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
    
    public static JavaVersion getByPath(Path param1Path) {
      JavaVersion javaVersion1 = JavaHelper.tryFindJavaByPath(param1Path);
      if (javaVersion1 != null)
        return javaVersion1; 
      Path path = param1Path.resolve("release");
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
        javaVersionAndBuild = new JavaHelper.JavaVersionAndBuild(isExistExtJavaLibrary(param1Path, "rt") ? 8 : 9, 0); 
      JavaVersion javaVersion2 = new JavaVersion(param1Path, javaVersionAndBuild.version, javaVersionAndBuild.build, aRCH, false);
      if (javaVersionAndBuild.version <= 8) {
        javaVersion2.enabledJavaFX = isExistExtJavaLibrary(param1Path, "jfxrt");
      } else {
        if (arrayList != null)
          javaVersion2.enabledJavaFX = arrayList.contains("javafx.base"); 
        if (!javaVersion2.enabledJavaFX) {
          javaVersion2.enabledJavaFX = (JavaHelper.tryFindModule(param1Path, "javafx.base") != null);
          if (!javaVersion2.enabledJavaFX)
            javaVersion2.enabledJavaFX = (JavaHelper.tryFindModule(param1Path.resolve("jre"), "javafx.base") != null); 
        } 
      } 
      return javaVersion2;
    }
    
    private static String getProperty(Properties param1Properties, String param1String) {
      String str = param1Properties.getProperty(param1String);
      return (str == null) ? null : str.replaceAll("\"", "");
    }
    
    public static boolean isExistExtJavaLibrary(Path param1Path, String param1String) {
      Path path1 = param1Path.resolve("lib").resolve("ext").resolve(param1String.concat(".jar"));
      Path path2 = param1Path.resolve("lib").resolve(param1String.concat(".jar"));
      Path path3 = param1Path.resolve("jre").resolve("lib").resolve("ext").resolve(param1String.concat(".jar"));
      Path path4 = param1Path.resolve("jre").resolve("lib").resolve(param1String.concat(".jar"));
      return (IOHelper.isFile(path1) || IOHelper.isFile(path3) || IOHelper.isFile(path4) || IOHelper.isFile(path2));
    }
  }
  
  public static class JavaVersionAndBuild {
    public int version;
    
    public int build;
    
    public JavaVersionAndBuild(int param1Int1, int param1Int2) {
      this.version = param1Int1;
      this.build = param1Int2;
    }
    
    public JavaVersionAndBuild() {}
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\JavaHelper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */