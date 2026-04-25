package pro.gravit.launcher.gui.scenes.settings;

import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.scenes.AbstractScene;

public abstract class BaseSettingsScene extends AbstractScene {
  protected Pane componentList;
  
  protected Pane settingsList;
  
  public BaseSettingsScene(String paramString, JavaFXApplication paramJavaFXApplication) {
    super(paramString, paramJavaFXApplication);
  }
  
  protected void doInit() {
    this.componentList = (Pane)((ScrollPane)LookupHelper.lookup((Node)this.layout, new String[] { "#settingslist" })).getContent();
    this.settingsList = (Pane)LookupHelper.lookup((Node)this.layout, new String[] { "#settings-list" });
  }
  
  public void reset() {
    this.settingsList.getChildren().clear();
    Label label = new Label(this.application.getTranslation("runtime.scenes.settings.header.options"));
    label.getStyleClass().add("settings-header");
    this.settingsList.getChildren().add(label);
  }
  
  public void add(String paramString, boolean paramBoolean1, Consumer<Boolean> paramConsumer, boolean paramBoolean2) {
    String str2;
    String str1 = "runtime.scenes.settings.properties.%s.name".formatted(new Object[] { paramString.toLowerCase() });
    if (paramBoolean2) {
      str2 = "runtime.scenes.settings.properties.%s.disabled".formatted(new Object[] { paramString.toLowerCase() });
    } else {
      str2 = "runtime.scenes.settings.properties.%s.description".formatted(new Object[] { paramString.toLowerCase() });
    } 
    add(this.application.getTranslation(str1, paramString), this.application.getTranslation(str2, paramString), paramBoolean1, paramConsumer, paramBoolean2);
  }
  
  public void add(String paramString1, String paramString2, boolean paramBoolean1, Consumer<Boolean> paramConsumer, boolean paramBoolean2) {
    HBox hBox = new HBox();
    CheckBox checkBox = new CheckBox();
    Label label1 = new Label();
    Label label2 = new Label();
    VBox vBox = new VBox();
    hBox.getStyleClass().add("settings-container");
    checkBox.getStyleClass().add("settings-checkbox");
    label1.getStyleClass().add("settings-label-header");
    label2.getStyleClass().add("settings-label");
    checkBox.setSelected(paramBoolean1);
    if (!paramBoolean2) {
      checkBox.setOnAction(paramActionEvent -> paramConsumer.accept(Boolean.valueOf(paramCheckBox.isSelected())));
    } else {
      checkBox.setDisable(true);
    } 
    label1.setText(paramString1);
    label2.setText(paramString2);
    label2.setWrapText(true);
    vBox.getChildren().add(label1);
    vBox.getChildren().add(label2);
    hBox.getChildren().add(checkBox);
    hBox.getChildren().add(vBox);
    this.settingsList.getChildren().add(hBox);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\settings\BaseSettingsScene.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */