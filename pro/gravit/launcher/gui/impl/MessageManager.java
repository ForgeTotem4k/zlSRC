package pro.gravit.launcher.gui.impl;

import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.dialogs.AbstractDialog;
import pro.gravit.launcher.gui.dialogs.ApplyDialog;
import pro.gravit.launcher.gui.dialogs.InfoDialog;
import pro.gravit.launcher.gui.dialogs.NotificationDialog;
import pro.gravit.launcher.gui.helper.PositionHelper;
import pro.gravit.launcher.gui.scenes.AbstractScene;
import pro.gravit.launcher.gui.stage.DialogStage;
import pro.gravit.launcher.gui.stage.PrimaryStage;

public class MessageManager {
  public final JavaFXApplication application;
  
  public MessageManager(JavaFXApplication paramJavaFXApplication) {
    this.application = paramJavaFXApplication;
  }
  
  public void createNotification(String paramString1, String paramString2) {
    createNotification(paramString1, paramString2, (this.application.getCurrentScene() != null));
  }
  
  public void initDialogInScene(AbstractScene paramAbstractScene, AbstractDialog paramAbstractDialog) {
    Pane pane = (Pane)paramAbstractDialog.getFxmlRootPrivate();
    if (!paramAbstractDialog.isInit())
      try {
        paramAbstractDialog.currentStage = paramAbstractScene.currentStage;
        paramAbstractDialog.init();
      } catch (Exception exception) {
        paramAbstractScene.errorHandle(exception);
      }  
    paramAbstractDialog.setOnClose(() -> {
          paramAbstractScene.currentStage.pull((Node)paramPane);
          paramAbstractScene.currentStage.enable();
        });
    paramAbstractScene.disable();
    paramAbstractScene.currentStage.push((Node)pane);
  }
  
  public void createNotification(String paramString1, String paramString2, boolean paramBoolean) {
    NotificationDialog notificationDialog = new NotificationDialog(this.application, paramString1, paramString2);
    if (paramBoolean) {
      PrimaryStage primaryStage = this.application.getMainStage();
      if (primaryStage == null)
        throw new NullPointerException("Try show launcher notification in application.getMainStage() == null"); 
      ContextHelper.runInFxThreadStatic(() -> {
            paramNotificationDialog.init();
            paramAbstractStage.pushNotification((Node)paramNotificationDialog.getFxmlRootPrivate());
            paramNotificationDialog.setOnClose(());
          });
    } else {
      AtomicReference atomicReference = new AtomicReference(null);
      ContextHelper.runInFxThreadStatic(() -> {
            NotificationDialog.NotificationSlot notificationSlot = new NotificationDialog.NotificationSlot((), ((Pane)paramNotificationDialog.getFxmlRootPrivate()).getPrefHeight() + 20.0D);
            paramNotificationDialog.setPosition(PositionHelper.PositionInfo.BOTTOM_RIGHT, notificationSlot);
            paramNotificationDialog.setOnClose(());
            paramAtomicReference.set(new DialogStage(this.application, paramString, (AbstractDialog)paramNotificationDialog));
            ((DialogStage)paramAtomicReference.get()).show();
          });
    } 
  }
  
  public void showDialog(String paramString1, String paramString2, Runnable paramRunnable1, Runnable paramRunnable2, boolean paramBoolean) {
    InfoDialog infoDialog = new InfoDialog(this.application, paramString1, paramString2, paramRunnable1, paramRunnable2);
    showAbstractDialog((AbstractDialog)infoDialog, paramString1, paramBoolean);
  }
  
  public void showApplyDialog(String paramString1, String paramString2, Runnable paramRunnable1, Runnable paramRunnable2, boolean paramBoolean) {
    showApplyDialog(paramString1, paramString2, paramRunnable1, paramRunnable2, paramRunnable2, paramBoolean);
  }
  
  public void showApplyDialog(String paramString1, String paramString2, Runnable paramRunnable1, Runnable paramRunnable2, Runnable paramRunnable3, boolean paramBoolean) {
    ApplyDialog applyDialog = new ApplyDialog(this.application, paramString1, paramString2, paramRunnable1, paramRunnable2, paramRunnable3);
    showAbstractDialog((AbstractDialog)applyDialog, paramString1, paramBoolean);
  }
  
  public void showAbstractDialog(AbstractDialog paramAbstractDialog, String paramString, boolean paramBoolean) {
    if (paramBoolean) {
      AbstractScene abstractScene = this.application.getCurrentScene();
      if (abstractScene == null)
        throw new NullPointerException("Try show launcher dialog in application.getCurrentScene() == null"); 
      ContextHelper.runInFxThreadStatic(() -> initDialogInScene(paramAbstractScene, paramAbstractDialog));
    } else {
      AtomicReference atomicReference = new AtomicReference(null);
      ContextHelper.runInFxThreadStatic(() -> {
            paramAtomicReference.set(new DialogStage(this.application, paramString, paramAbstractDialog));
            ((DialogStage)paramAtomicReference.get()).show();
          });
      paramAbstractDialog.setOnClose(() -> {
            ((DialogStage)paramAtomicReference.get()).close();
            ((DialogStage)paramAtomicReference.get()).stage.setScene(null);
          });
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\impl\MessageManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */