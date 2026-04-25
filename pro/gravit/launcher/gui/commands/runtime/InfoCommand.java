package pro.gravit.launcher.gui.commands.runtime;

import java.util.ArrayList;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.utils.command.Command;
import pro.gravit.utils.helper.JVMHelper;
import pro.gravit.utils.helper.LogHelper;

public class InfoCommand extends Command {
  private final JavaFXApplication application;
  
  public InfoCommand(JavaFXApplication paramJavaFXApplication) {
    this.application = paramJavaFXApplication;
  }
  
  public String getArgsDescription() {
    return "[]";
  }
  
  public String getUsageDescription() {
    return "show javafx info";
  }
  
  public void invoke(String... paramVarArgs) {
    Platform.runLater(() -> {
          LogHelper.info("OS %s ARCH %s Java %d", new Object[] { JVMHelper.OS_TYPE.name(), JVMHelper.ARCH_TYPE.name(), Integer.valueOf(JVMHelper.JVM_VERSION) });
          LogHelper.info("JavaFX version: %s", new Object[] { System.getProperty("javafx.runtime.version") });
          ArrayList<String> arrayList1 = new ArrayList();
          ArrayList<String> arrayList2 = new ArrayList();
          for (ConditionalFeature conditionalFeature : ConditionalFeature.values()) {
            if (Platform.isSupported(conditionalFeature)) {
              arrayList1.add(conditionalFeature.name());
            } else {
              arrayList2.add(conditionalFeature.name());
            } 
          } 
          LogHelper.info("JavaFX supported features: [%s]", new Object[] { String.join(",", (Iterable)arrayList1) });
          LogHelper.info("JavaFX unsupported features: [%s]", new Object[] { String.join(",", (Iterable)arrayList2) });
          LogHelper.info("Is accessibility active %s", new Object[] { Platform.isAccessibilityActive() ? "true" : "false" });
        });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\commands\runtime\InfoCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */