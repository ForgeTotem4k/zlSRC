package pro.gravit.launcher.gui.impl;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.utils.JavaFxUtils;
import pro.gravit.utils.helper.LogHelper;

public abstract class AbstractStage {
  protected final JavaFXApplication application;
  
  protected final Stage stage;
  
  protected final Scene scene;
  
  protected final StackPane stackPane;
  
  protected AbstractVisualComponent visualComponent;
  
  protected AbstractVisualComponent background;
  
  protected Pane disablePane;
  
  protected VBox notificationsVBox;
  
  protected AnchorPane notifications;
  
  private final AtomicInteger disableCounter = new AtomicInteger(0);
  
  protected final AtomicInteger scenePosition = new AtomicInteger(0);
  
  protected List<String> sceneFlow = new LinkedList<>();
  
  protected AbstractStage(JavaFXApplication paramJavaFXApplication, Stage paramStage) {
    this.application = paramJavaFXApplication;
    this.stage = paramStage;
    this.stackPane = new StackPane();
    this.scene = new Scene((Parent)this.stackPane);
    this.stage.setScene(this.scene);
    resetStyles();
  }
  
  protected void setClipRadius(double paramDouble1, double paramDouble2) {
    Rectangle rectangle = new Rectangle(this.stackPane.getPrefWidth(), this.stackPane.getPrefHeight());
    rectangle.heightProperty().bind((ObservableValue)this.stackPane.heightProperty());
    rectangle.widthProperty().bind((ObservableValue)this.stackPane.widthProperty());
    rectangle.setArcHeight(paramDouble2);
    rectangle.setArcWidth(paramDouble1);
    this.stackPane.setClip((Node)rectangle);
  }
  
  public void hide() {
    this.stage.setIconified(true);
  }
  
  public void close() {
    this.stage.hide();
  }
  
  public void resetStyles() {
    try {
      this.scene.getStylesheets().clear();
      this.scene.getStylesheets().add(JavaFxUtils.getStyleUrl("styles/variables").toString());
      this.scene.getStylesheets().add(JavaFxUtils.getStyleUrl("styles/global").toString());
    } catch (IOException iOException) {
      LogHelper.error(iOException);
    } 
  }
  
  public void enableMouseDrag(Node paramNode) {
    AtomicReference atomicReference = new AtomicReference();
    paramNode.setOnMousePressed(paramMouseEvent -> paramAtomicReference.set(new Point2D(paramMouseEvent.getSceneX(), paramMouseEvent.getSceneY())));
    paramNode.setOnMouseDragged(paramMouseEvent -> {
          if (paramAtomicReference.get() == null)
            return; 
          this.stage.setX(paramMouseEvent.getScreenX() - ((Point2D)paramAtomicReference.get()).getX());
          this.stage.setY(paramMouseEvent.getScreenY() - ((Point2D)paramAtomicReference.get()).getY());
        });
  }
  
  public AbstractVisualComponent getVisualComponent() {
    return this.visualComponent;
  }
  
  public void setScene(AbstractVisualComponent paramAbstractVisualComponent, boolean paramBoolean) throws Exception {
    if (paramAbstractVisualComponent == null) {
      if (!this.stackPane.getChildren().isEmpty())
        this.stackPane.getChildren().set(this.scenePosition.get(), new Pane()); 
      return;
    } 
    if (paramBoolean)
      this.sceneFlow.add(paramAbstractVisualComponent.getName()); 
    paramAbstractVisualComponent.currentStage = this;
    if (!paramAbstractVisualComponent.isInit())
      paramAbstractVisualComponent.init(); 
    if (paramAbstractVisualComponent.isResetOnShow)
      paramAbstractVisualComponent.reset(); 
    if (this.stackPane.getChildren().isEmpty()) {
      this.stackPane.getChildren().add(paramAbstractVisualComponent.getFxmlRoot());
    } else {
      Node node = (Node)this.stackPane.getChildren().get(this.scenePosition.get());
      Effect effect = node.getEffect();
      if (effect instanceof GaussianBlur) {
        GaussianBlur gaussianBlur = (GaussianBlur)effect;
        node.setEffect(null);
        paramAbstractVisualComponent.getFxmlRootPrivate().setEffect((Effect)gaussianBlur);
      } 
      this.stackPane.getChildren().set(this.scenePosition.get(), paramAbstractVisualComponent.getFxmlRoot());
    } 
    this.stage.sizeToScene();
    paramAbstractVisualComponent.postInit();
    this.visualComponent = paramAbstractVisualComponent;
  }
  
  public AbstractVisualComponent back() throws Exception {
    if (this.sceneFlow.size() <= 1)
      return null; 
    while (true) {
      String str = this.sceneFlow.get(this.sceneFlow.size() - 2);
      AbstractVisualComponent abstractVisualComponent = this.application.gui.getByName(str);
      if (abstractVisualComponent == null)
        return null; 
      this.sceneFlow.remove(this.sceneFlow.get(this.sceneFlow.size() - 1));
      if (!abstractVisualComponent.isDisableReturnBack()) {
        setScene(abstractVisualComponent, false);
        return abstractVisualComponent;
      } 
    } 
  }
  
  public void push(Node paramNode) {
    this.stackPane.getChildren().add(paramNode);
  }
  
  public boolean contains(Node paramNode) {
    return this.stackPane.getChildren().contains(paramNode);
  }
  
  public void pull(Node paramNode) {
    this.stackPane.getChildren().remove(paramNode);
  }
  
  public void addAfter(Node paramNode1, Node paramNode2) {
    int i = this.stackPane.getChildren().indexOf(paramNode1);
    if (i >= 0)
      this.stackPane.getChildren().add(i + 1, paramNode2); 
  }
  
  public void addBefore(Node paramNode1, Node paramNode2) {
    int i = this.stackPane.getChildren().indexOf(paramNode1);
    if (i >= 0)
      this.stackPane.getChildren().add(i, paramNode2); 
  }
  
  public int getScenePosition() {
    return this.scenePosition.get();
  }
  
  protected void pushNotification(Node paramNode) {
    if (this.notifications == null) {
      this.notifications = new AnchorPane();
      this.notificationsVBox = new VBox();
      this.notificationsVBox.setAlignment(Pos.BOTTOM_RIGHT);
      this.notifications.setPickOnBounds(false);
      this.notificationsVBox.setPickOnBounds(false);
      this.notifications.getChildren().add(this.notificationsVBox);
      AnchorPane.setRightAnchor((Node)this.notificationsVBox, Double.valueOf(10.0D));
      AnchorPane.setTopAnchor((Node)this.notificationsVBox, Double.valueOf(10.0D));
      AnchorPane.setBottomAnchor((Node)this.notificationsVBox, Double.valueOf(10.0D));
      this.notificationsVBox.setSpacing(10.0D);
      push((Node)this.notifications);
    } 
    this.notificationsVBox.getChildren().add(paramNode);
  }
  
  protected void pullNotification(Node paramNode) {
    if (this.notifications != null)
      this.notificationsVBox.getChildren().remove(paramNode); 
  }
  
  public boolean isShowing() {
    return this.stage.isShowing();
  }
  
  public final boolean isNullScene() {
    return (this.visualComponent == null);
  }
  
  public Stage getStage() {
    return this.stage;
  }
  
  public void show() {
    this.stage.show();
  }
  
  public void disable() {
    int i = this.disableCounter.incrementAndGet();
    LogHelper.dev("Disable scene: stack_num: %s | blur: %s | counter: %s", new Object[] { Integer.valueOf(this.stackPane.getChildren().size()), (this.disablePane == null) ? "null" : "not null", Integer.valueOf(i) });
    if (i != 1)
      return; 
    Pane pane = (Pane)this.stackPane.getChildren().get(this.scenePosition.get());
    pane.setEffect((Effect)new GaussianBlur(150.0D));
    if (this.disablePane == null) {
      this.disablePane = new Pane();
      this.disablePane.setPrefHeight(pane.getPrefHeight());
      this.disablePane.setPrefWidth(pane.getPrefWidth());
      addAfter((Node)pane, (Node)this.disablePane);
    } 
    this.disablePane.setVisible(true);
  }
  
  public void enable() {
    int i = this.disableCounter.decrementAndGet();
    LogHelper.dev("Enable scene: stack_num: %s | blur: %s | counter: %s", new Object[] { Integer.valueOf(this.stackPane.getChildren().size()), (this.disablePane == null) ? "null" : "not null", Integer.valueOf(i) });
    if (i != 0)
      return; 
    Pane pane = (Pane)this.stackPane.getChildren().get(this.scenePosition.get());
    pane.setEffect(null);
    this.disablePane.setVisible(false);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\impl\AbstractStage.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */