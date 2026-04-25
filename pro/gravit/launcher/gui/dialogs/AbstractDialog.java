package pro.gravit.launcher.gui.dialogs;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Pane;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.impl.AbstractVisualComponent;
import pro.gravit.launcher.gui.impl.ContextHelper;

public abstract class AbstractDialog extends AbstractVisualComponent {
  private final List<ContextHelper.GuiExceptionRunnable> onClose = new ArrayList<>(1);
  
  protected AbstractDialog(String paramString, JavaFXApplication paramJavaFXApplication) {
    super(paramString, paramJavaFXApplication);
  }
  
  protected void doPostInit() {}
  
  public void reset() {}
  
  public void disable() {}
  
  public void enable() {}
  
  public void setOnClose(ContextHelper.GuiExceptionRunnable paramGuiExceptionRunnable) {
    this.onClose.add(paramGuiExceptionRunnable);
  }
  
  public void close() throws Throwable {
    for (ContextHelper.GuiExceptionRunnable guiExceptionRunnable : this.onClose)
      guiExceptionRunnable.call(); 
  }
  
  public LookupHelper.Point2D getOutSceneCoords(Rectangle2D paramRectangle2D) {
    return new LookupHelper.Point2D((paramRectangle2D.getMaxX() - this.layout.getPrefWidth()) / 2.0D, (paramRectangle2D.getMaxY() - this.layout.getPrefHeight()) / 2.0D);
  }
  
  public LookupHelper.Point2D getSceneCoords(Pane paramPane) {
    return new LookupHelper.Point2D((paramPane.getPrefWidth() - this.layout.getPrefWidth()) / 2.0D, (paramPane.getPrefHeight() - this.layout.getPrefHeight()) / 2.0D);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\dialogs\AbstractDialog.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */