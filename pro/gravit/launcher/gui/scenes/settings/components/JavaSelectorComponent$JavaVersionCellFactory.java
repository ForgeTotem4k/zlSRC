package pro.gravit.launcher.gui.scenes.settings.components;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import pro.gravit.utils.helper.JavaHelper;

class JavaVersionCellFactory implements Callback<ListView<JavaHelper.JavaVersion>, ListCell<JavaHelper.JavaVersion>> {
  private final StringConverter<JavaHelper.JavaVersion> converter;
  
  public JavaVersionCellFactory(StringConverter<JavaHelper.JavaVersion> paramStringConverter) {
    this.converter = paramStringConverter;
  }
  
  public ListCell<JavaHelper.JavaVersion> call(ListView<JavaHelper.JavaVersion> paramListView) {
    return new JavaSelectorComponent.JavaVersionListCell(this.converter);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\settings\components\JavaSelectorComponent$JavaVersionCellFactory.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */