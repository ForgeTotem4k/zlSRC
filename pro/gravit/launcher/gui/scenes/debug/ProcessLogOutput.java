package pro.gravit.launcher.gui.scenes.debug;

import java.util.Map;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import pro.gravit.launcher.gui.impl.ContextHelper;
import pro.gravit.launcher.gui.service.LaunchService;

public class ProcessLogOutput implements LaunchService.ClientInstance.ProcessListener {
  static final long MAX_LENGTH = 262144L;
  
  static final int REMOVE_LENGTH = 16384;
  
  private final TextArea output;
  
  private final Object syncObject = new Object();
  
  private String appendString = "";
  
  private boolean isOutputRunned;
  
  public ProcessLogOutput(TextArea paramTextArea) {
    this.output = paramTextArea;
  }
  
  public String getText() {
    return this.output.getText();
  }
  
  public void clear() {
    this.output.clear();
  }
  
  public void copyToClipboard() {
    ClipboardContent clipboardContent = new ClipboardContent();
    clipboardContent.putString(getText());
    Clipboard clipboard = Clipboard.getSystemClipboard();
    clipboard.setContent((Map)clipboardContent);
  }
  
  public void append(String paramString) {
    boolean bool = false;
    synchronized (this.syncObject) {
      if (this.appendString.length() > 262144L) {
        this.appendString = "<logs buffer overflow>\n".concat(paramString);
      } else {
        this.appendString = this.appendString.concat(paramString);
      } 
      if (!this.isOutputRunned) {
        bool = true;
        this.isOutputRunned = true;
      } 
    } 
    if (bool)
      ContextHelper.runInFxThreadStatic(() -> {
            synchronized (this.syncObject) {
              if (this.output.lengthProperty().get() > 262144L)
                this.output.deleteText(0, 16384); 
              this.output.appendText(this.appendString);
              this.appendString = "";
              this.isOutputRunned = false;
            } 
          }); 
  }
  
  public void onNext(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    append(new String(paramArrayOfbyte, paramInt1, paramInt2));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\debug\ProcessLogOutput.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */