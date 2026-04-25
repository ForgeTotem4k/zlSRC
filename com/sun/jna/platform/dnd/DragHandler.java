package com.sun.jna.platform.dnd;

import com.sun.jna.Platform;
import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DragSourceMotionListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;

public abstract class DragHandler implements DragSourceListener, DragSourceMotionListener, DragGestureListener {
  private static final Logger LOG = Logger.getLogger(DragHandler.class.getName());
  
  public static final Dimension MAX_GHOST_SIZE = new Dimension(250, 250);
  
  public static final float DEFAULT_GHOST_ALPHA = 0.5F;
  
  public static final int UNKNOWN_MODIFIERS = -1;
  
  public static final Transferable UNKNOWN_TRANSFERABLE = null;
  
  protected static final int MOVE = 2;
  
  protected static final int COPY = 1;
  
  protected static final int LINK = 1073741824;
  
  protected static final int NONE = 0;
  
  static final int MOVE_MASK = 64;
  
  static final boolean OSX = Platform.isMac();
  
  static final int COPY_MASK = OSX ? 512 : 128;
  
  static final int LINK_MASK = OSX ? 768 : 192;
  
  static final int KEY_MASK = 9152;
  
  private static int modifiers = -1;
  
  private static Transferable transferable = UNKNOWN_TRANSFERABLE;
  
  private int supportedActions;
  
  private boolean fixCursor = true;
  
  private Component dragSource;
  
  private GhostedDragImage ghost;
  
  private Point imageOffset;
  
  private Dimension maxGhostSize = MAX_GHOST_SIZE;
  
  private float ghostAlpha = 0.5F;
  
  private String lastAction;
  
  private boolean moved;
  
  static int getModifiers() {
    return modifiers;
  }
  
  public static Transferable getTransferable(DropTargetEvent paramDropTargetEvent) {
    if (paramDropTargetEvent instanceof DropTargetDragEvent) {
      try {
        return ((DropTargetDragEvent)paramDropTargetEvent).getTransferable();
      } catch (Exception exception) {}
    } else if (paramDropTargetEvent instanceof DropTargetDropEvent) {
      return ((DropTargetDropEvent)paramDropTargetEvent).getTransferable();
    } 
    return transferable;
  }
  
  protected DragHandler(Component paramComponent, int paramInt) {
    this.dragSource = paramComponent;
    this.supportedActions = paramInt;
    try {
      String str1 = System.getProperty("DragHandler.alpha");
      if (str1 != null)
        try {
          this.ghostAlpha = Float.parseFloat(str1);
        } catch (NumberFormatException numberFormatException) {} 
      String str2 = System.getProperty("DragHandler.maxDragImageSize");
      if (str2 != null) {
        String[] arrayOfString = str2.split("x");
        if (arrayOfString.length == 2)
          try {
            this.maxGhostSize = new Dimension(Integer.parseInt(arrayOfString[0]), Integer.parseInt(arrayOfString[1]));
          } catch (NumberFormatException numberFormatException) {} 
      } 
    } catch (SecurityException securityException) {}
    disableSwingDragSupport(paramComponent);
    DragSource dragSource = DragSource.getDefaultDragSource();
    dragSource.createDefaultDragGestureRecognizer(paramComponent, this.supportedActions, this);
  }
  
  private void disableSwingDragSupport(Component paramComponent) {
    if (paramComponent instanceof JTree) {
      ((JTree)paramComponent).setDragEnabled(false);
    } else if (paramComponent instanceof JList) {
      ((JList)paramComponent).setDragEnabled(false);
    } else if (paramComponent instanceof JTable) {
      ((JTable)paramComponent).setDragEnabled(false);
    } else if (paramComponent instanceof JTextComponent) {
      ((JTextComponent)paramComponent).setDragEnabled(false);
    } else if (paramComponent instanceof JColorChooser) {
      ((JColorChooser)paramComponent).setDragEnabled(false);
    } else if (paramComponent instanceof JFileChooser) {
      ((JFileChooser)paramComponent).setDragEnabled(false);
    } 
  }
  
  protected boolean canDrag(DragGestureEvent paramDragGestureEvent) {
    int i = paramDragGestureEvent.getTriggerEvent().getModifiersEx() & 0x23C0;
    return (i == 64) ? (((this.supportedActions & 0x2) != 0)) : ((i == COPY_MASK) ? (((this.supportedActions & 0x1) != 0)) : ((i == LINK_MASK) ? (((this.supportedActions & 0x40000000) != 0)) : true));
  }
  
  protected void setModifiers(int paramInt) {
    modifiers = paramInt;
  }
  
  protected abstract Transferable getTransferable(DragGestureEvent paramDragGestureEvent);
  
  protected Icon getDragIcon(DragGestureEvent paramDragGestureEvent, Point paramPoint) {
    return null;
  }
  
  protected void dragStarted(DragGestureEvent paramDragGestureEvent) {}
  
  public void dragGestureRecognized(DragGestureEvent paramDragGestureEvent) {
    if ((paramDragGestureEvent.getDragAction() & this.supportedActions) != 0 && canDrag(paramDragGestureEvent)) {
      setModifiers(paramDragGestureEvent.getTriggerEvent().getModifiersEx() & 0x23C0);
      Transferable transferable = getTransferable(paramDragGestureEvent);
      if (transferable == null)
        return; 
      try {
        Point point1 = new Point(0, 0);
        Icon icon1 = getDragIcon(paramDragGestureEvent, point1);
        Point point2 = paramDragGestureEvent.getDragOrigin();
        this.imageOffset = new Point(point1.x - point2.x, point1.y - point2.y);
        Icon icon2 = scaleDragIcon(icon1, this.imageOffset);
        Cursor cursor = null;
        if (icon2 != null && DragSource.isDragImageSupported()) {
          GraphicsConfiguration graphicsConfiguration = paramDragGestureEvent.getComponent().getGraphicsConfiguration();
          paramDragGestureEvent.startDrag(cursor, createDragImage(graphicsConfiguration, icon2), this.imageOffset, transferable, this);
        } else {
          if (icon2 != null) {
            Point point3 = this.dragSource.getLocationOnScreen();
            point3.translate(point2.x, point2.y);
            Point point4 = new Point(-this.imageOffset.x, -this.imageOffset.y);
            this.ghost = new GhostedDragImage(this.dragSource, icon2, getImageLocation(point3), point4);
            this.ghost.setAlpha(this.ghostAlpha);
          } 
          paramDragGestureEvent.startDrag(cursor, transferable, this);
        } 
        dragStarted(paramDragGestureEvent);
        this.moved = false;
        paramDragGestureEvent.getDragSource().addDragSourceMotionListener(this);
        transferable = transferable;
      } catch (InvalidDnDOperationException invalidDnDOperationException) {
        if (this.ghost != null) {
          this.ghost.dispose();
          this.ghost = null;
        } 
      } 
    } 
  }
  
  protected Icon scaleDragIcon(Icon paramIcon, Point paramPoint) {
    return paramIcon;
  }
  
  protected Image createDragImage(GraphicsConfiguration paramGraphicsConfiguration, Icon paramIcon) {
    int i = paramIcon.getIconWidth();
    int j = paramIcon.getIconHeight();
    BufferedImage bufferedImage = paramGraphicsConfiguration.createCompatibleImage(i, j, 3);
    Graphics2D graphics2D = (Graphics2D)bufferedImage.getGraphics();
    graphics2D.setComposite(AlphaComposite.Clear);
    graphics2D.fillRect(0, 0, i, j);
    graphics2D.setComposite(AlphaComposite.getInstance(2, this.ghostAlpha));
    paramIcon.paintIcon(this.dragSource, graphics2D, 0, 0);
    graphics2D.dispose();
    return bufferedImage;
  }
  
  private int reduce(int paramInt) {
    return ((paramInt & 0x2) != 0 && paramInt != 2) ? 2 : (((paramInt & 0x1) != 0 && paramInt != 1) ? 1 : paramInt);
  }
  
  protected Cursor getCursorForAction(int paramInt) {
    switch (paramInt) {
      case 2:
        return DragSource.DefaultMoveDrop;
      case 1:
        return DragSource.DefaultCopyDrop;
      case 1073741824:
        return DragSource.DefaultLinkDrop;
    } 
    return DragSource.DefaultMoveNoDrop;
  }
  
  protected int getAcceptableDropAction(int paramInt) {
    return reduce(this.supportedActions & paramInt);
  }
  
  protected int getDropAction(DragSourceEvent paramDragSourceEvent) {
    if (paramDragSourceEvent instanceof DragSourceDragEvent) {
      DragSourceDragEvent dragSourceDragEvent = (DragSourceDragEvent)paramDragSourceEvent;
      return dragSourceDragEvent.getDropAction();
    } 
    return (paramDragSourceEvent instanceof DragSourceDropEvent) ? ((DragSourceDropEvent)paramDragSourceEvent).getDropAction() : 0;
  }
  
  protected int adjustDropAction(DragSourceEvent paramDragSourceEvent) {
    int i = getDropAction(paramDragSourceEvent);
    if (paramDragSourceEvent instanceof DragSourceDragEvent) {
      DragSourceDragEvent dragSourceDragEvent = (DragSourceDragEvent)paramDragSourceEvent;
      if (i == 0) {
        int j = dragSourceDragEvent.getGestureModifiersEx() & 0x23C0;
        if (j == 0)
          i = getAcceptableDropAction(dragSourceDragEvent.getTargetActions()); 
      } 
    } 
    return i;
  }
  
  protected void updateCursor(DragSourceEvent paramDragSourceEvent) {
    if (!this.fixCursor)
      return; 
    Cursor cursor = getCursorForAction(adjustDropAction(paramDragSourceEvent));
    paramDragSourceEvent.getDragSourceContext().setCursor(cursor);
  }
  
  static String actionString(int paramInt) {
    switch (paramInt) {
      case 2:
        return "MOVE";
      case 3:
        return "MOVE|COPY";
      case 1073741826:
        return "MOVE|LINK";
      case 1073741827:
        return "MOVE|COPY|LINK";
      case 1:
        return "COPY";
      case 1073741825:
        return "COPY|LINK";
      case 1073741824:
        return "LINK";
    } 
    return "NONE";
  }
  
  private void describe(String paramString, DragSourceEvent paramDragSourceEvent) {
    if (LOG.isLoggable(Level.FINE)) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("drag: ");
      stringBuilder.append(paramString);
      DragSourceContext dragSourceContext = paramDragSourceEvent.getDragSourceContext();
      if (paramDragSourceEvent instanceof DragSourceDragEvent) {
        DragSourceDragEvent dragSourceDragEvent = (DragSourceDragEvent)paramDragSourceEvent;
        stringBuilder.append(": src=");
        stringBuilder.append(actionString(dragSourceContext.getSourceActions()));
        stringBuilder.append(" usr=");
        stringBuilder.append(actionString(dragSourceDragEvent.getUserAction()));
        stringBuilder.append(" tgt=");
        stringBuilder.append(actionString(dragSourceDragEvent.getTargetActions()));
        stringBuilder.append(" act=");
        stringBuilder.append(actionString(dragSourceDragEvent.getDropAction()));
        stringBuilder.append(" mods=");
        stringBuilder.append(dragSourceDragEvent.getGestureModifiersEx());
      } else {
        stringBuilder.append(": e=");
        stringBuilder.append(paramDragSourceEvent);
      } 
      String str = stringBuilder.toString();
      if (!str.equals(this.lastAction)) {
        LOG.log(Level.FINE, str);
        this.lastAction = str;
      } 
    } 
  }
  
  public void dragDropEnd(DragSourceDropEvent paramDragSourceDropEvent) {
    describe("end", paramDragSourceDropEvent);
    setModifiers(-1);
    transferable = UNKNOWN_TRANSFERABLE;
    if (this.ghost != null) {
      if (paramDragSourceDropEvent.getDropSuccess()) {
        this.ghost.dispose();
      } else {
        this.ghost.returnToOrigin();
      } 
      this.ghost = null;
    } 
    DragSource dragSource = paramDragSourceDropEvent.getDragSourceContext().getDragSource();
    dragSource.removeDragSourceMotionListener(this);
    this.moved = false;
  }
  
  private Point getImageLocation(Point paramPoint) {
    paramPoint.translate(this.imageOffset.x, this.imageOffset.y);
    return paramPoint;
  }
  
  public void dragEnter(DragSourceDragEvent paramDragSourceDragEvent) {
    describe("enter", paramDragSourceDragEvent);
    if (this.ghost != null)
      this.ghost.move(getImageLocation(paramDragSourceDragEvent.getLocation())); 
    updateCursor(paramDragSourceDragEvent);
  }
  
  public void dragMouseMoved(DragSourceDragEvent paramDragSourceDragEvent) {
    describe("move", paramDragSourceDragEvent);
    if (this.ghost != null)
      this.ghost.move(getImageLocation(paramDragSourceDragEvent.getLocation())); 
    if (this.moved)
      updateCursor(paramDragSourceDragEvent); 
    this.moved = true;
  }
  
  public void dragOver(DragSourceDragEvent paramDragSourceDragEvent) {
    describe("over", paramDragSourceDragEvent);
    if (this.ghost != null)
      this.ghost.move(getImageLocation(paramDragSourceDragEvent.getLocation())); 
    updateCursor(paramDragSourceDragEvent);
  }
  
  public void dragExit(DragSourceEvent paramDragSourceEvent) {
    describe("exit", paramDragSourceEvent);
  }
  
  public void dropActionChanged(DragSourceDragEvent paramDragSourceDragEvent) {
    describe("change", paramDragSourceDragEvent);
    setModifiers(paramDragSourceDragEvent.getGestureModifiersEx() & 0x23C0);
    if (this.ghost != null)
      this.ghost.move(getImageLocation(paramDragSourceDragEvent.getLocation())); 
    updateCursor(paramDragSourceDragEvent);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\dnd\DragHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */