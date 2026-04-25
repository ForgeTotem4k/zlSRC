package pro.gravit.launcher.gui.scenes.settings.components;

import javafx.util.StringConverter;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.utils.helper.JavaHelper;

class JavaVersionConverter extends StringConverter<JavaHelper.JavaVersion> {
  private final ClientProfile profile;
  
  private JavaVersionConverter(ClientProfile paramClientProfile) {
    this.profile = paramClientProfile;
  }
  
  public String toString(JavaHelper.JavaVersion paramJavaVersion) {
    if (paramJavaVersion == null)
      return "Unknown"; 
    String str = "";
    if (paramJavaVersion.version == this.profile.getRecommendJavaVersion())
      str = "[RECOMMENDED]"; 
    return "Java %d b%d %s".formatted(new Object[] { Integer.valueOf(paramJavaVersion.version), Integer.valueOf(paramJavaVersion.build), str });
  }
  
  public JavaHelper.JavaVersion fromString(String paramString) {
    return null;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\settings\components\JavaSelectorComponent$JavaVersionConverter.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */