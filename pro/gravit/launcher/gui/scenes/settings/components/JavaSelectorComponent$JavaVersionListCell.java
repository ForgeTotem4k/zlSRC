package pro.gravit.launcher.gui.scenes.settings.components;

import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.stage.PopupWindow;
import javafx.util.StringConverter;
import pro.gravit.utils.helper.JavaHelper;

class JavaVersionListCell extends ListCell<JavaHelper.JavaVersion> {
  private final StringConverter<JavaHelper.JavaVersion> converter;
  
  public JavaVersionListCell(StringConverter<JavaHelper.JavaVersion> paramStringConverter) {
    this.converter = paramStringConverter;
  }
  
  protected void updateItem(JavaHelper.JavaVersion paramJavaVersion, boolean paramBoolean) {
    super.updateItem(paramJavaVersion, paramBoolean);
    if (paramBoolean || paramJavaVersion == null) {
      setText(null);
      setTooltip(null);
    } else {
      setText(this.converter.toString(paramJavaVersion));
      Tooltip tooltip = new Tooltip(paramJavaVersion.jvmDir.toString());
      tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);
      setTooltip(tooltip);
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\settings\components\JavaSelectorComponent$JavaVersionListCell.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */