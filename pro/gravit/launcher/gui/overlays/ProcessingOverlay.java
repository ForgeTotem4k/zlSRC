package pro.gravit.launcher.gui.overlays;

import java.io.IOException;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.WebSocketEvent;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.impl.AbstractStage;
import pro.gravit.launcher.gui.impl.ContextHelper;
import pro.gravit.utils.helper.LogHelper;

public class ProcessingOverlay extends AbstractOverlay {
  private Labeled description;
  
  public ProcessingOverlay(JavaFXApplication paramJavaFXApplication) {
    super("overlay/processing/processing.fxml", paramJavaFXApplication);
  }
  
  public String getName() {
    return "processing";
  }
  
  protected void doInit() {
    this.description = (Labeled)LookupHelper.lookup((Node)this.layout, new String[] { "#description" });
  }
  
  public void reset() {
    this.description.textProperty().unbind();
    this.description.getStyleClass().remove("error");
    this.description.setText("...");
  }
  
  public void errorHandle(Throwable paramThrowable) {
    super.errorHandle(paramThrowable);
    this.description.textProperty().unbind();
    this.description.getStyleClass().add("error");
    this.description.setText(paramThrowable.toString());
  }
  
  public final <T extends WebSocketEvent> void processRequest(AbstractStage paramAbstractStage, String paramString, Request<T> paramRequest, Consumer<T> paramConsumer, EventHandler<ActionEvent> paramEventHandler) {
    processRequest(paramAbstractStage, paramString, paramRequest, paramConsumer, (Consumer<Throwable>)null, paramEventHandler);
  }
  
  public final <T extends WebSocketEvent> void processRequest(AbstractStage paramAbstractStage, String paramString, Request<T> paramRequest, Consumer<T> paramConsumer, Consumer<Throwable> paramConsumer1, EventHandler<ActionEvent> paramEventHandler) {
    try {
      ContextHelper.runInFxThreadStatic(() -> show(paramAbstractStage, ()));
    } catch (Exception exception) {
      ContextHelper.runInFxThreadStatic(() -> errorHandle(paramException));
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\overlays\ProcessingOverlay.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */