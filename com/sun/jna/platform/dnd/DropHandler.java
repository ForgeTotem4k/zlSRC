package com.sun.jna.platform.dnd;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DropHandler implements DropTargetListener {
  private static final Logger LOG = Logger.getLogger(DropHandler.class.getName());
  
  private int acceptedActions;
  
  private List<DataFlavor> acceptedFlavors;
  
  private DropTarget dropTarget;
  
  private boolean active = true;
  
  private DropTargetPainter painter;
  
  private String lastAction;
  
  public DropHandler(Component paramComponent, int paramInt) {
    this(paramComponent, paramInt, new DataFlavor[0]);
  }
  
  public DropHandler(Component paramComponent, int paramInt, DataFlavor[] paramArrayOfDataFlavor) {
    this(paramComponent, paramInt, paramArrayOfDataFlavor, null);
  }
  
  public DropHandler(Component paramComponent, int paramInt, DataFlavor[] paramArrayOfDataFlavor, DropTargetPainter paramDropTargetPainter) {
    this.acceptedActions = paramInt;
    this.acceptedFlavors = Arrays.asList(paramArrayOfDataFlavor);
    this.painter = paramDropTargetPainter;
    this.dropTarget = new DropTarget(paramComponent, paramInt, this, this.active);
  }
  
  protected DropTarget getDropTarget() {
    return this.dropTarget;
  }
  
  public boolean isActive() {
    return this.active;
  }
  
  public void setActive(boolean paramBoolean) {
    this.active = paramBoolean;
    if (this.dropTarget != null)
      this.dropTarget.setActive(paramBoolean); 
  }
  
  protected int getDropActionsForFlavors(DataFlavor[] paramArrayOfDataFlavor) {
    return this.acceptedActions;
  }
  
  protected int getDropAction(DropTargetEvent paramDropTargetEvent) {
    int i = 0;
    int j = 0;
    Point point = null;
    DataFlavor[] arrayOfDataFlavor = new DataFlavor[0];
    if (paramDropTargetEvent instanceof DropTargetDragEvent) {
      DropTargetDragEvent dropTargetDragEvent = (DropTargetDragEvent)paramDropTargetEvent;
      i = dropTargetDragEvent.getDropAction();
      j = dropTargetDragEvent.getSourceActions();
      arrayOfDataFlavor = dropTargetDragEvent.getCurrentDataFlavors();
      point = dropTargetDragEvent.getLocation();
    } else if (paramDropTargetEvent instanceof DropTargetDropEvent) {
      DropTargetDropEvent dropTargetDropEvent = (DropTargetDropEvent)paramDropTargetEvent;
      i = dropTargetDropEvent.getDropAction();
      j = dropTargetDropEvent.getSourceActions();
      arrayOfDataFlavor = dropTargetDropEvent.getCurrentDataFlavors();
      point = dropTargetDropEvent.getLocation();
    } 
    if (isSupported(arrayOfDataFlavor)) {
      int k = getDropActionsForFlavors(arrayOfDataFlavor);
      i = getDropAction(paramDropTargetEvent, i, j, k);
      if (i != 0 && canDrop(paramDropTargetEvent, i, point))
        return i; 
    } 
    return 0;
  }
  
  protected int getDropAction(DropTargetEvent paramDropTargetEvent, int paramInt1, int paramInt2, int paramInt3) {
    boolean bool = modifiersActive(paramInt1);
    if ((paramInt1 & paramInt3) == 0 && !bool) {
      int i = paramInt3 & paramInt2;
      paramInt1 = i;
    } else if (bool) {
      int i = paramInt1 & paramInt3 & paramInt2;
      if (i != paramInt1)
        paramInt1 = i; 
    } 
    return paramInt1;
  }
  
  protected boolean modifiersActive(int paramInt) {
    int i = DragHandler.getModifiers();
    return (i == -1) ? ((paramInt == 1073741824 || paramInt == 1)) : ((i != 0));
  }
  
  private void describe(String paramString, DropTargetEvent paramDropTargetEvent) {
    if (LOG.isLoggable(Level.FINE)) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("drop: ");
      stringBuilder.append(paramString);
      if (paramDropTargetEvent instanceof DropTargetDragEvent) {
        DropTargetContext dropTargetContext = paramDropTargetEvent.getDropTargetContext();
        DropTarget dropTarget = dropTargetContext.getDropTarget();
        DropTargetDragEvent dropTargetDragEvent = (DropTargetDragEvent)paramDropTargetEvent;
        stringBuilder.append(": src=");
        stringBuilder.append(DragHandler.actionString(dropTargetDragEvent.getSourceActions()));
        stringBuilder.append(" tgt=");
        stringBuilder.append(DragHandler.actionString(dropTarget.getDefaultActions()));
        stringBuilder.append(" act=");
        stringBuilder.append(DragHandler.actionString(dropTargetDragEvent.getDropAction()));
      } else if (paramDropTargetEvent instanceof DropTargetDropEvent) {
        DropTargetContext dropTargetContext = paramDropTargetEvent.getDropTargetContext();
        DropTarget dropTarget = dropTargetContext.getDropTarget();
        DropTargetDropEvent dropTargetDropEvent = (DropTargetDropEvent)paramDropTargetEvent;
        stringBuilder.append(": src=");
        stringBuilder.append(DragHandler.actionString(dropTargetDropEvent.getSourceActions()));
        stringBuilder.append(" tgt=");
        stringBuilder.append(DragHandler.actionString(dropTarget.getDefaultActions()));
        stringBuilder.append(" act=");
        stringBuilder.append(DragHandler.actionString(dropTargetDropEvent.getDropAction()));
      } 
      String str = stringBuilder.toString();
      if (!str.equals(this.lastAction)) {
        LOG.log(Level.FINE, str);
        this.lastAction = str;
      } 
    } 
  }
  
  protected int acceptOrReject(DropTargetDragEvent paramDropTargetDragEvent) {
    int i = getDropAction(paramDropTargetDragEvent);
    if (i != 0) {
      paramDropTargetDragEvent.acceptDrag(i);
    } else {
      paramDropTargetDragEvent.rejectDrag();
    } 
    return i;
  }
  
  public void dragEnter(DropTargetDragEvent paramDropTargetDragEvent) {
    describe("enter(tgt)", paramDropTargetDragEvent);
    int i = acceptOrReject(paramDropTargetDragEvent);
    paintDropTarget(paramDropTargetDragEvent, i, paramDropTargetDragEvent.getLocation());
  }
  
  public void dragOver(DropTargetDragEvent paramDropTargetDragEvent) {
    describe("over(tgt)", paramDropTargetDragEvent);
    int i = acceptOrReject(paramDropTargetDragEvent);
    paintDropTarget(paramDropTargetDragEvent, i, paramDropTargetDragEvent.getLocation());
  }
  
  public void dragExit(DropTargetEvent paramDropTargetEvent) {
    describe("exit(tgt)", paramDropTargetEvent);
    paintDropTarget(paramDropTargetEvent, 0, null);
  }
  
  public void dropActionChanged(DropTargetDragEvent paramDropTargetDragEvent) {
    describe("change(tgt)", paramDropTargetDragEvent);
    int i = acceptOrReject(paramDropTargetDragEvent);
    paintDropTarget(paramDropTargetDragEvent, i, paramDropTargetDragEvent.getLocation());
  }
  
  public void drop(DropTargetDropEvent paramDropTargetDropEvent) {
    describe("drop(tgt)", paramDropTargetDropEvent);
    int i = getDropAction(paramDropTargetDropEvent);
    if (i != 0) {
      paramDropTargetDropEvent.acceptDrop(i);
      try {
        drop(paramDropTargetDropEvent, i);
        paramDropTargetDropEvent.dropComplete(true);
      } catch (Exception exception) {
        paramDropTargetDropEvent.dropComplete(false);
      } 
    } else {
      paramDropTargetDropEvent.rejectDrop();
    } 
    paintDropTarget(paramDropTargetDropEvent, 0, paramDropTargetDropEvent.getLocation());
  }
  
  protected boolean isSupported(DataFlavor[] paramArrayOfDataFlavor) {
    HashSet hashSet = new HashSet(Arrays.asList((Object[])paramArrayOfDataFlavor));
    hashSet.retainAll(this.acceptedFlavors);
    return !hashSet.isEmpty();
  }
  
  protected void paintDropTarget(DropTargetEvent paramDropTargetEvent, int paramInt, Point paramPoint) {
    if (this.painter != null)
      this.painter.paintDropTarget(paramDropTargetEvent, paramInt, paramPoint); 
  }
  
  protected boolean canDrop(DropTargetEvent paramDropTargetEvent, int paramInt, Point paramPoint) {
    return true;
  }
  
  protected abstract void drop(DropTargetDropEvent paramDropTargetDropEvent, int paramInt) throws UnsupportedFlavorException, IOException;
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\dnd\DropHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */