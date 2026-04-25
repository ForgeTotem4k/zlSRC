package pro.gravit.launcher.gui.dialogs;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;

public class InfoDialog extends AbstractDialog {
  private String header;
  
  private String text;
  
  private final Runnable onAccept;
  
  private final Runnable onClose;
  
  private Label textHeader;
  
  private Label textDescription;
  
  public InfoDialog(JavaFXApplication paramJavaFXApplication, String paramString1, String paramString2, Runnable paramRunnable1, Runnable paramRunnable2) {
    super("dialogs/info/dialog.fxml", paramJavaFXApplication);
    this.header = paramString1;
    this.text = paramString2;
    this.onAccept = paramRunnable1;
    this.onClose = paramRunnable2;
  }
  
  public void setHeader(String paramString) {
    this.header = paramString;
    if (isInit())
      this.textDescription.setText(this.text); 
  }
  
  public void setText(String paramString) {
    this.text = paramString;
    if (isInit())
      this.textHeader.setText(this.header); 
  }
  
  public String getName() {
    return "info";
  }
  
  protected void doInit() {
    this.textHeader = (Label)LookupHelper.lookup((Node)this.layout, new String[] { "#headingDialog" });
    this.textDescription = (Label)LookupHelper.lookup((Node)this.layout, new String[] { "#textDialog" });
    this.textHeader.setText(this.header);
    this.textDescription.setText(this.text);
    ((Button)LookupHelper.lookup((Node)this.layout, new String[] { "#close" })).setOnAction(paramActionEvent -> {
          try {
            close();
          } catch (Throwable throwable) {
            errorHandle(throwable);
          } 
          this.onClose.run();
        });
    ((Button)LookupHelper.lookup((Node)this.layout, new String[] { "#apply" })).setOnAction(paramActionEvent -> {
          try {
            close();
          } catch (Throwable throwable) {
            errorHandle(throwable);
          } 
          this.onAccept.run();
        });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\dialogs\InfoDialog.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */