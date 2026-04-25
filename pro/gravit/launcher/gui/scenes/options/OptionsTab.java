package pro.gravit.launcher.gui.scenes.options;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pro.gravit.launcher.base.profiles.optional.OptionalFile;
import pro.gravit.launcher.base.profiles.optional.OptionalView;
import pro.gravit.launcher.gui.JavaFXApplication;

public class OptionsTab {
  private final TabPane tabPane;
  
  private final JavaFXApplication application;
  
  private final Map<String, Tab> tabs = new HashMap<>();
  
  private OptionalView optionalView;
  
  private final Map<OptionalFile, Consumer<Boolean>> watchers = new HashMap<>();
  
  public OptionsTab(JavaFXApplication paramJavaFXApplication, TabPane paramTabPane) {
    this.tabPane = paramTabPane;
    this.application = paramJavaFXApplication;
  }
  
  void callWatcher(OptionalFile paramOptionalFile, Boolean paramBoolean) {
    for (Map.Entry<OptionalFile, Consumer<Boolean>> entry : this.watchers.entrySet()) {
      if (entry.getKey() == paramOptionalFile) {
        ((Consumer<Boolean>)entry.getValue()).accept(paramBoolean);
        break;
      } 
    } 
  }
  
  public void addProfileOptionals(OptionalView paramOptionalView) {
    this.optionalView = new OptionalView(paramOptionalView);
    this.watchers.clear();
    Iterator<OptionalFile> iterator = this.optionalView.all.iterator();
    while (iterator.hasNext()) {
      OptionalFile optionalFile = iterator.next();
      if (!optionalFile.visible)
        continue; 
      List<E> list = (optionalFile.dependencies == null) ? List.<E>of() : Arrays.<OptionalFile>stream(optionalFile.dependencies).map(OptionalFile::getName).toList();
      Consumer<Boolean> consumer = add((optionalFile.category == null) ? "GLOBAL" : optionalFile.category, optionalFile.name, optionalFile.info, this.optionalView.enabled.contains(optionalFile), optionalFile.subTreeLevel, paramBoolean -> {
            if (paramBoolean.booleanValue()) {
              this.optionalView.enable(paramOptionalFile, true, this::callWatcher);
            } else {
              this.optionalView.disable(paramOptionalFile, this::callWatcher);
            } 
          }(List)list);
      this.watchers.put(optionalFile, consumer);
    } 
  }
  
  public VBox addTab(String paramString1, String paramString2) {
    Tab tab = new Tab();
    tab.setText(paramString2);
    VBox vBox = new VBox();
    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setContent((Node)vBox);
    scrollPane.setFitToWidth(true);
    tab.setContent((Node)scrollPane);
    this.tabs.put(paramString1, tab);
    this.tabPane.getTabs().add(tab);
    return vBox;
  }
  
  public Consumer<Boolean> add(String paramString1, String paramString2, String paramString3, boolean paramBoolean, int paramInt, Consumer<Boolean> paramConsumer, List<String> paramList) {
    VBox vBox2;
    VBox vBox1 = new VBox();
    CheckBox checkBox = new CheckBox();
    Label label = new Label();
    vBox1.getChildren().add(checkBox);
    vBox1.getChildren().add(label);
    VBox.setMargin((Node)vBox1, new Insets(0.0D, 0.0D, 0.0D, (30 * --paramInt)));
    vBox1.setOnMouseClicked(paramMouseEvent -> {
          if (paramMouseEvent.getButton() == MouseButton.PRIMARY) {
            paramCheckBox.setSelected(!paramCheckBox.isSelected());
            paramConsumer.accept(Boolean.valueOf(paramCheckBox.isSelected()));
          } 
        });
    vBox1.setOnTouchPressed(paramTouchEvent -> {
          paramCheckBox.setSelected(!paramCheckBox.isSelected());
          paramConsumer.accept(Boolean.valueOf(paramCheckBox.isSelected()));
        });
    vBox1.getStyleClass().add("optional-container");
    checkBox.setSelected(paramBoolean);
    checkBox.setText(paramString2);
    checkBox.setOnAction(paramActionEvent -> paramConsumer.accept(Boolean.valueOf(paramCheckBox.isSelected())));
    checkBox.getStyleClass().add("optional-checkbox");
    label.setText(paramString3);
    label.setWrapText(true);
    label.getStyleClass().add("optional-label");
    if (!paramList.isEmpty()) {
      HBox hBox = new HBox();
      hBox.getStyleClass().add("optional-library-container");
      for (String str : paramList) {
        Label label1 = new Label();
        label1.setText(str);
        label1.getStyleClass().add("optional-library");
        hBox.getChildren().add(label1);
      } 
      vBox1.getChildren().add(hBox);
    } 
    boolean bool = this.tabs.isEmpty();
    if (this.tabs.containsKey(paramString1)) {
      vBox2 = (VBox)((ScrollPane)((Tab)this.tabs.get(paramString1)).getContent()).getContent();
    } else {
      vBox2 = addTab(paramString1, this.application.getTranslation(String.format("runtime.scenes.options.tabs.%s", new Object[] { paramString1 }), paramString1));
    } 
    vBox2.getChildren().add(vBox1);
    if (bool)
      this.tabPane.getSelectionModel().select(0); 
    Objects.requireNonNull(checkBox);
    return checkBox::setSelected;
  }
  
  public void clear() {
    this.tabPane.getTabs().clear();
    this.tabs.clear();
  }
  
  public OptionalView getOptionalView() {
    return this.optionalView;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\options\OptionsTab.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */