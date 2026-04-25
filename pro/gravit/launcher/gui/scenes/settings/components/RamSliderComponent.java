package pro.gravit.launcher.gui.scenes.settings.components;

import java.text.MessageFormat;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import oshi.SystemInfo;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.components.UserBlock;
import pro.gravit.launcher.gui.config.RuntimeSettings;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.utils.SystemMemory;
import pro.gravit.utils.helper.JVMHelper;

public class RamSliderComponent {
  private static final long MAX_JAVA_MEMORY_X64 = 32768L;
  
  private static final long MAX_JAVA_MEMORY_X32 = 1536L;
  
  private Label ramLabel;
  
  protected Pane componentList;
  
  private Slider ramSlider;
  
  private RuntimeSettings.ProfileSettingsView profileSettings;
  
  private UserBlock userBlock;
  
  private final JavaFXApplication application;
  
  public RamSliderComponent(JavaFXApplication paramJavaFXApplication, Pane paramPane) {
    long l;
    this.application = paramJavaFXApplication;
    this.componentList = (Pane)((ScrollPane)LookupHelper.lookup((Node)paramPane, new String[] { "#settingslist" })).getContent();
    this.ramSlider = (Slider)LookupHelper.lookup((Node)this.componentList, new String[] { "#ramSlider" });
    this.ramLabel = (Label)LookupHelper.lookup((Node)this.componentList, new String[] { "#ramLabel" });
    try {
      SystemInfo systemInfo = new SystemInfo();
      l = systemInfo.getHardware().getMemory().getTotal() >> 20L;
      if (l > 8192L)
        l = 8192L; 
    } catch (Throwable throwable) {
      try {
        l = SystemMemory.getPhysicalMemorySize() >> 20L;
      } catch (Throwable throwable1) {
        l = 2048L;
      } 
    } 
    this.ramSlider.setMax(Math.min(l, getJavaMaxMemory()));
    this.ramSlider.setSnapToTicks(true);
    this.ramSlider.setShowTickMarks(true);
    this.ramSlider.setShowTickLabels(true);
    this.ramSlider.setMinorTickCount(1);
    this.ramSlider.setMajorTickUnit(1024.0D);
    this.ramSlider.setBlockIncrement(1024.0D);
    this.ramSlider.setLabelFormatter(new StringConverter<Double>() {
          public String toString(Double param1Double) {
            return "%.0fG".formatted(new Object[] { Double.valueOf(param1Double.doubleValue() / 1024.0D) });
          }
          
          public Double fromString(String param1String) {
            return null;
          }
        });
    reset();
  }
  
  private long getJavaMaxMemory() {
    return (this.application.javaService.isArchAvailable(JVMHelper.ARCH.X86_64) || this.application.javaService.isArchAvailable(JVMHelper.ARCH.ARM64)) ? 32768L : 1536L;
  }
  
  public void reset() {
    int i = this.application.runtimeSettings.globalSettings.ram;
    this.ramSlider.setValue(i);
    this.ramSlider.valueProperty().addListener((paramObservableValue, paramNumber1, paramNumber2) -> {
          this.application.runtimeSettings.globalSettings.ram = paramNumber2.intValue();
          updateRamLabel();
        });
    updateRamLabel();
  }
  
  public void updateRamLabel() {
    int i = this.application.runtimeSettings.globalSettings.ram;
    this.ramLabel.setText((i == 0) ? this.application.getTranslation("runtime.scenes.settings.ramAuto") : MessageFormat.format(this.application.getTranslation("runtime.scenes.settings.ram"), new Object[] { Integer.valueOf(i) }));
  }
  
  public void save() {
    this.application.runtimeSettings.globalSettings.ram = (int)this.ramSlider.getValue();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\settings\components\RamSliderComponent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */