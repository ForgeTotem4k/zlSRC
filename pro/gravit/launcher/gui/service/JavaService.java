package pro.gravit.launcher.gui.service;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.runtime.client.DirBridge;
import pro.gravit.utils.helper.JVMHelper;
import pro.gravit.utils.helper.JavaHelper;
import pro.gravit.utils.helper.LogHelper;

public class JavaService {
  private static final Pattern JAVA_VERSION_PATTERN = Pattern.compile("Java (?<version>.+) b(?<build>.+) (?<os>.+) (?<arch>.+) javafx (?<javafx>.+)");
  
  public volatile List<JavaHelper.JavaVersion> javaVersions;
  
  private final JavaFXApplication application;
  
  public JavaService(JavaFXApplication paramJavaFXApplication) {
    this.application = paramJavaFXApplication;
    update();
  }
  
  public void update() {
    LinkedList<JavaHelper.JavaVersion> linkedList = new LinkedList();
    if (this.application.guiModuleConfig.javaList != null)
      for (Map.Entry entry : this.application.guiModuleConfig.javaList.entrySet()) {
        String str1 = (String)entry.getKey();
        String str2 = (String)entry.getValue();
        Matcher matcher = JAVA_VERSION_PATTERN.matcher(str2);
        if (matcher.matches()) {
          String str = matcher.group("os");
          int i = Integer.parseInt(matcher.group("version"));
          int j = Integer.parseInt(matcher.group("build"));
          JVMHelper.ARCH aRCH = JVMHelper.ARCH.valueOf(matcher.group("arch"));
          boolean bool = Boolean.parseBoolean(matcher.group("javafx"));
          if (!isArchAvailable(aRCH) || !JVMHelper.OS_TYPE.name.equals(str))
            continue; 
          Path path = DirBridge.dirUpdates.resolve(str1);
          LogHelper.debug("In-Launcher Java Version found: Java %d b%d %s javafx %s", new Object[] { Integer.valueOf(i), Integer.valueOf(j), aRCH.name, Boolean.toString(bool) });
          JavaHelper.JavaVersion javaVersion = new JavaHelper.JavaVersion(path, i, j, aRCH, bool);
          linkedList.add(javaVersion);
          continue;
        } 
        LogHelper.warning("Java Version: %s does not match", new Object[] { str2 });
      }  
    if (!this.application.guiModuleConfig.forceDownloadJava || linkedList.isEmpty())
      linkedList.addAll(JavaHelper.findJava()); 
    this.javaVersions = Collections.unmodifiableList(linkedList);
  }
  
  public boolean isArchAvailable(JVMHelper.ARCH paramARCH) {
    return (JVMHelper.ARCH_TYPE == paramARCH) ? true : ((paramARCH == JVMHelper.ARCH.X86_64 && JVMHelper.OS_TYPE == JVMHelper.OS.MUSTDIE && ((JVMHelper.ARCH_TYPE == JVMHelper.ARCH.X86 && !JVMHelper.isJVMMatchesSystemArch()) || JVMHelper.ARCH_TYPE == JVMHelper.ARCH.ARM64)) ? true : ((paramARCH == JVMHelper.ARCH.X86_64 && JVMHelper.OS_TYPE == JVMHelper.OS.MACOSX && JVMHelper.ARCH_TYPE == JVMHelper.ARCH.ARM64)));
  }
  
  public boolean isIncompatibleJava(JavaHelper.JavaVersion paramJavaVersion, ClientProfile paramClientProfile) {
    return (paramJavaVersion.version > paramClientProfile.getMaxJavaVersion() || paramJavaVersion.version < paramClientProfile.getMinJavaVersion());
  }
  
  public boolean contains(Path paramPath) {
    for (JavaHelper.JavaVersion javaVersion : this.javaVersions) {
      if (javaVersion.jvmDir.toAbsolutePath().equals(paramPath.toAbsolutePath()))
        return true; 
    } 
    return false;
  }
  
  public JavaHelper.JavaVersion getRecommendJavaVersion(ClientProfile paramClientProfile) {
    int i = paramClientProfile.getMinJavaVersion();
    int j = paramClientProfile.getMaxJavaVersion();
    int k = paramClientProfile.getRecommendJavaVersion();
    JavaHelper.JavaVersion javaVersion = null;
    for (JavaHelper.JavaVersion javaVersion1 : this.javaVersions) {
      if (javaVersion1.version < i || javaVersion1.version > j || isIncompatibleJava(javaVersion1, paramClientProfile))
        continue; 
      if (javaVersion == null) {
        javaVersion = javaVersion1;
        continue;
      } 
      if (javaVersion.version != k && javaVersion1.version == k) {
        javaVersion = javaVersion1;
        continue;
      } 
      if (((javaVersion.version == k) ? true : false) == ((javaVersion1.version == k) ? true : false)) {
        if (javaVersion.version < javaVersion1.version) {
          javaVersion = javaVersion1;
          continue;
        } 
        if ((javaVersion.arch == JVMHelper.ARCH.X86 && javaVersion1.arch == JVMHelper.ARCH.X86_64) || (javaVersion.arch == JVMHelper.ARCH.X86_64 && javaVersion1.arch == JVMHelper.ARCH.ARM64))
          javaVersion = javaVersion1; 
        if (javaVersion.version == javaVersion1.version && javaVersion.build < javaVersion1.build)
          javaVersion = javaVersion1; 
      } 
    } 
    return javaVersion;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\service\JavaService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */