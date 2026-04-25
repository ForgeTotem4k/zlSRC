package com.sun.jna.platform;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.PsapiUtil;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

public class WindowUtils {
  private static final Logger LOG = Logger.getLogger(WindowUtils.class.getName());
  
  private static final String TRANSPARENT_OLD_BG = "transparent-old-bg";
  
  private static final String TRANSPARENT_OLD_OPAQUE = "transparent-old-opaque";
  
  private static final String TRANSPARENT_ALPHA = "transparent-alpha";
  
  public static final Shape MASK_NONE = null;
  
  private static NativeWindowUtils getInstance() {
    return Holder.INSTANCE;
  }
  
  public static void setWindowMask(Window paramWindow, Shape paramShape) {
    getInstance().setWindowMask(paramWindow, paramShape);
  }
  
  public static void setComponentMask(Component paramComponent, Shape paramShape) {
    getInstance().setWindowMask(paramComponent, paramShape);
  }
  
  public static void setWindowMask(Window paramWindow, Icon paramIcon) {
    getInstance().setWindowMask(paramWindow, paramIcon);
  }
  
  public static boolean isWindowAlphaSupported() {
    return getInstance().isWindowAlphaSupported();
  }
  
  public static GraphicsConfiguration getAlphaCompatibleGraphicsConfiguration() {
    return getInstance().getAlphaCompatibleGraphicsConfiguration();
  }
  
  public static void setWindowAlpha(Window paramWindow, float paramFloat) {
    getInstance().setWindowAlpha(paramWindow, Math.max(0.0F, Math.min(paramFloat, 1.0F)));
  }
  
  public static void setWindowTransparent(Window paramWindow, boolean paramBoolean) {
    getInstance().setWindowTransparent(paramWindow, paramBoolean);
  }
  
  public static BufferedImage getWindowIcon(WinDef.HWND paramHWND) {
    return getInstance().getWindowIcon(paramHWND);
  }
  
  public static Dimension getIconSize(WinDef.HICON paramHICON) {
    return getInstance().getIconSize(paramHICON);
  }
  
  public static List<DesktopWindow> getAllWindows(boolean paramBoolean) {
    return getInstance().getAllWindows(paramBoolean);
  }
  
  public static String getWindowTitle(WinDef.HWND paramHWND) {
    return getInstance().getWindowTitle(paramHWND);
  }
  
  public static String getProcessFilePath(WinDef.HWND paramHWND) {
    return getInstance().getProcessFilePath(paramHWND);
  }
  
  public static Rectangle getWindowLocationAndSize(WinDef.HWND paramHWND) {
    return getInstance().getWindowLocationAndSize(paramHWND);
  }
  
  private static class Holder {
    public static boolean requiresVisible;
    
    public static final WindowUtils.NativeWindowUtils INSTANCE;
    
    static {
      if (Platform.isWindows()) {
        INSTANCE = new WindowUtils.W32WindowUtils();
      } else if (Platform.isMac()) {
        INSTANCE = new WindowUtils.MacWindowUtils();
      } else if (Platform.isX11()) {
        INSTANCE = new WindowUtils.X11WindowUtils();
        requiresVisible = System.getProperty("java.version").matches("^1\\.4\\..*");
      } else {
        String str = System.getProperty("os.name");
        throw new UnsupportedOperationException("No support for " + str);
      } 
    }
  }
  
  public static abstract class NativeWindowUtils {
    protected Window getWindow(Component param1Component) {
      return (param1Component instanceof Window) ? (Window)param1Component : SwingUtilities.getWindowAncestor(param1Component);
    }
    
    protected void whenDisplayable(Component param1Component, final Runnable action) {
      if (param1Component.isDisplayable() && (!WindowUtils.Holder.requiresVisible || param1Component.isVisible())) {
        action.run();
      } else if (WindowUtils.Holder.requiresVisible) {
        getWindow(param1Component).addWindowListener(new WindowAdapter() {
              public void windowOpened(WindowEvent param2WindowEvent) {
                param2WindowEvent.getWindow().removeWindowListener(this);
                action.run();
              }
              
              public void windowClosed(WindowEvent param2WindowEvent) {
                param2WindowEvent.getWindow().removeWindowListener(this);
              }
            });
      } else {
        param1Component.addHierarchyListener(new HierarchyListener() {
              public void hierarchyChanged(HierarchyEvent param2HierarchyEvent) {
                if ((param2HierarchyEvent.getChangeFlags() & 0x2L) != 0L && param2HierarchyEvent.getComponent().isDisplayable()) {
                  param2HierarchyEvent.getComponent().removeHierarchyListener(this);
                  action.run();
                } 
              }
            });
      } 
    }
    
    protected Raster toRaster(Shape param1Shape) {
      WritableRaster writableRaster = null;
      if (param1Shape != WindowUtils.MASK_NONE) {
        Rectangle rectangle = param1Shape.getBounds();
        if (rectangle.width > 0 && rectangle.height > 0) {
          BufferedImage bufferedImage = new BufferedImage(rectangle.x + rectangle.width, rectangle.y + rectangle.height, 12);
          Graphics2D graphics2D = bufferedImage.createGraphics();
          graphics2D.setColor(Color.black);
          graphics2D.fillRect(0, 0, rectangle.x + rectangle.width, rectangle.y + rectangle.height);
          graphics2D.setColor(Color.white);
          graphics2D.fill(param1Shape);
          writableRaster = bufferedImage.getRaster();
        } 
      } 
      return writableRaster;
    }
    
    protected Raster toRaster(Component param1Component, Icon param1Icon) {
      WritableRaster writableRaster = null;
      if (param1Icon != null) {
        Rectangle rectangle = new Rectangle(0, 0, param1Icon.getIconWidth(), param1Icon.getIconHeight());
        BufferedImage bufferedImage = new BufferedImage(rectangle.width, rectangle.height, 2);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Clear);
        graphics2D.fillRect(0, 0, rectangle.width, rectangle.height);
        graphics2D.setComposite(AlphaComposite.SrcOver);
        param1Icon.paintIcon(param1Component, graphics2D, 0, 0);
        writableRaster = bufferedImage.getAlphaRaster();
      } 
      return writableRaster;
    }
    
    protected Shape toShape(Raster param1Raster) {
      final Area area = new Area(new Rectangle(0, 0, 0, 0));
      RasterRangesUtils.outputOccupiedRanges(param1Raster, new RasterRangesUtils.RangesOutput() {
            public boolean outputRange(int param2Int1, int param2Int2, int param2Int3, int param2Int4) {
              area.add(new Area(new Rectangle(param2Int1, param2Int2, param2Int3, param2Int4)));
              return true;
            }
          });
      return area;
    }
    
    public void setWindowAlpha(Window param1Window, float param1Float) {}
    
    public boolean isWindowAlphaSupported() {
      return false;
    }
    
    public GraphicsConfiguration getAlphaCompatibleGraphicsConfiguration() {
      GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
      return graphicsDevice.getDefaultConfiguration();
    }
    
    public void setWindowTransparent(Window param1Window, boolean param1Boolean) {}
    
    protected void setDoubleBuffered(Component param1Component, boolean param1Boolean) {
      if (param1Component instanceof JComponent)
        ((JComponent)param1Component).setDoubleBuffered(param1Boolean); 
      if (param1Component instanceof JRootPane && param1Boolean) {
        ((JRootPane)param1Component).setDoubleBuffered(true);
      } else if (param1Component instanceof Container) {
        Component[] arrayOfComponent = ((Container)param1Component).getComponents();
        for (byte b = 0; b < arrayOfComponent.length; b++)
          setDoubleBuffered(arrayOfComponent[b], param1Boolean); 
      } 
    }
    
    protected void setLayersTransparent(Window param1Window, boolean param1Boolean) {
      Color color = param1Boolean ? new Color(0, 0, 0, 0) : null;
      if (param1Window instanceof RootPaneContainer) {
        RootPaneContainer rootPaneContainer = (RootPaneContainer)param1Window;
        JRootPane jRootPane = rootPaneContainer.getRootPane();
        JLayeredPane jLayeredPane = jRootPane.getLayeredPane();
        Container container = jRootPane.getContentPane();
        JComponent jComponent = (container instanceof JComponent) ? (JComponent)container : null;
        if (param1Boolean) {
          jLayeredPane.putClientProperty("transparent-old-opaque", Boolean.valueOf(jLayeredPane.isOpaque()));
          jLayeredPane.setOpaque(false);
          jRootPane.putClientProperty("transparent-old-opaque", Boolean.valueOf(jRootPane.isOpaque()));
          jRootPane.setOpaque(false);
          if (jComponent != null) {
            jComponent.putClientProperty("transparent-old-opaque", Boolean.valueOf(jComponent.isOpaque()));
            jComponent.setOpaque(false);
          } 
          jRootPane.putClientProperty("transparent-old-bg", jRootPane.getParent().getBackground());
        } else {
          jLayeredPane.setOpaque(Boolean.TRUE.equals(jLayeredPane.getClientProperty("transparent-old-opaque")));
          jLayeredPane.putClientProperty("transparent-old-opaque", (Object)null);
          jRootPane.setOpaque(Boolean.TRUE.equals(jRootPane.getClientProperty("transparent-old-opaque")));
          jRootPane.putClientProperty("transparent-old-opaque", (Object)null);
          if (jComponent != null) {
            jComponent.setOpaque(Boolean.TRUE.equals(jComponent.getClientProperty("transparent-old-opaque")));
            jComponent.putClientProperty("transparent-old-opaque", (Object)null);
          } 
          color = (Color)jRootPane.getClientProperty("transparent-old-bg");
          jRootPane.putClientProperty("transparent-old-bg", (Object)null);
        } 
      } 
      param1Window.setBackground(color);
    }
    
    protected void setMask(Component param1Component, Raster param1Raster) {
      throw new UnsupportedOperationException("Window masking is not available");
    }
    
    protected void setWindowMask(Component param1Component, Raster param1Raster) {
      if (param1Component.isLightweight())
        throw new IllegalArgumentException("Component must be heavyweight: " + param1Component); 
      setMask(param1Component, param1Raster);
    }
    
    public void setWindowMask(Component param1Component, Shape param1Shape) {
      setWindowMask(param1Component, toRaster(param1Shape));
    }
    
    public void setWindowMask(Component param1Component, Icon param1Icon) {
      setWindowMask(param1Component, toRaster(param1Component, param1Icon));
    }
    
    protected void setForceHeavyweightPopups(Window param1Window, boolean param1Boolean) {
      if (!(param1Window instanceof WindowUtils.HeavyweightForcer)) {
        Window[] arrayOfWindow = param1Window.getOwnedWindows();
        for (byte b = 0; b < arrayOfWindow.length; b++) {
          if (arrayOfWindow[b] instanceof WindowUtils.HeavyweightForcer) {
            if (param1Boolean)
              return; 
            arrayOfWindow[b].dispose();
          } 
        } 
        Boolean bool = Boolean.valueOf(System.getProperty("jna.force_hw_popups", "true"));
        if (param1Boolean && bool.booleanValue())
          new WindowUtils.HeavyweightForcer(param1Window); 
      } 
    }
    
    protected BufferedImage getWindowIcon(WinDef.HWND param1HWND) {
      throw new UnsupportedOperationException("This platform is not supported, yet.");
    }
    
    protected Dimension getIconSize(WinDef.HICON param1HICON) {
      throw new UnsupportedOperationException("This platform is not supported, yet.");
    }
    
    protected List<DesktopWindow> getAllWindows(boolean param1Boolean) {
      throw new UnsupportedOperationException("This platform is not supported, yet.");
    }
    
    protected String getWindowTitle(WinDef.HWND param1HWND) {
      throw new UnsupportedOperationException("This platform is not supported, yet.");
    }
    
    protected String getProcessFilePath(WinDef.HWND param1HWND) {
      throw new UnsupportedOperationException("This platform is not supported, yet.");
    }
    
    protected Rectangle getWindowLocationAndSize(WinDef.HWND param1HWND) {
      throw new UnsupportedOperationException("This platform is not supported, yet.");
    }
    
    protected abstract class TransparentContentPane extends JPanel implements AWTEventListener {
      private static final long serialVersionUID = 1L;
      
      private boolean transparent;
      
      public TransparentContentPane(Container param2Container) {
        super(new BorderLayout());
        add(param2Container, "Center");
        setTransparent(true);
        if (param2Container instanceof JPanel)
          ((JComponent)param2Container).setOpaque(false); 
      }
      
      public void addNotify() {
        super.addNotify();
        Toolkit.getDefaultToolkit().addAWTEventListener(this, 2L);
      }
      
      public void removeNotify() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
        super.removeNotify();
      }
      
      public void setTransparent(boolean param2Boolean) {
        this.transparent = param2Boolean;
        setOpaque(!param2Boolean);
        setDoubleBuffered(!param2Boolean);
        repaint();
      }
      
      public void eventDispatched(AWTEvent param2AWTEvent) {
        if (param2AWTEvent.getID() == 300 && SwingUtilities.isDescendingFrom(((ContainerEvent)param2AWTEvent).getChild(), this)) {
          Component component = ((ContainerEvent)param2AWTEvent).getChild();
          WindowUtils.NativeWindowUtils.this.setDoubleBuffered(component, false);
        } 
      }
      
      public void paint(Graphics param2Graphics) {
        if (this.transparent) {
          Rectangle rectangle = param2Graphics.getClipBounds();
          int i = rectangle.width;
          int j = rectangle.height;
          if (getWidth() > 0 && getHeight() > 0) {
            BufferedImage bufferedImage = new BufferedImage(i, j, 3);
            Graphics2D graphics2D = bufferedImage.createGraphics();
            graphics2D.setComposite(AlphaComposite.Clear);
            graphics2D.fillRect(0, 0, i, j);
            graphics2D.dispose();
            graphics2D = bufferedImage.createGraphics();
            graphics2D.translate(-rectangle.x, -rectangle.y);
            super.paint(graphics2D);
            graphics2D.dispose();
            paintDirect(bufferedImage, rectangle);
          } 
        } else {
          super.paint(param2Graphics);
        } 
      }
      
      protected abstract void paintDirect(BufferedImage param2BufferedImage, Rectangle param2Rectangle);
    }
  }
  
  private static class X11WindowUtils extends NativeWindowUtils {
    private boolean didCheck;
    
    private long[] alphaVisualIDs = new long[0];
    
    private static final long OPAQUE = 4294967295L;
    
    private static final String OPACITY = "_NET_WM_WINDOW_OPACITY";
    
    private X11WindowUtils() {}
    
    private static X11.Pixmap createBitmap(X11.Display param1Display, X11.Window param1Window, Raster param1Raster) {
      X11 x11 = X11.INSTANCE;
      Rectangle rectangle = param1Raster.getBounds();
      int i = rectangle.x + rectangle.width;
      int j = rectangle.y + rectangle.height;
      X11.Pixmap pixmap = x11.XCreatePixmap(param1Display, (X11.Drawable)param1Window, i, j, 1);
      X11.GC gC = x11.XCreateGC(param1Display, (X11.Drawable)pixmap, new NativeLong(0L), null);
      if (gC == null)
        return null; 
      x11.XSetForeground(param1Display, gC, new NativeLong(0L));
      x11.XFillRectangle(param1Display, (X11.Drawable)pixmap, gC, 0, 0, i, j);
      final ArrayList<Rectangle> rlist = new ArrayList();
      try {
        RasterRangesUtils.outputOccupiedRanges(param1Raster, new RasterRangesUtils.RangesOutput() {
              public boolean outputRange(int param2Int1, int param2Int2, int param2Int3, int param2Int4) {
                rlist.add(new Rectangle(param2Int1, param2Int2, param2Int3, param2Int4));
                return true;
              }
            });
        X11.XRectangle[] arrayOfXRectangle = (X11.XRectangle[])(new X11.XRectangle()).toArray(arrayList.size());
        byte b;
        for (b = 0; b < arrayOfXRectangle.length; b++) {
          Rectangle rectangle1 = arrayList.get(b);
          (arrayOfXRectangle[b]).x = (short)rectangle1.x;
          (arrayOfXRectangle[b]).y = (short)rectangle1.y;
          (arrayOfXRectangle[b]).width = (short)rectangle1.width;
          (arrayOfXRectangle[b]).height = (short)rectangle1.height;
          Pointer pointer = arrayOfXRectangle[b].getPointer();
          pointer.setShort(0L, (short)rectangle1.x);
          pointer.setShort(2L, (short)rectangle1.y);
          pointer.setShort(4L, (short)rectangle1.width);
          pointer.setShort(6L, (short)rectangle1.height);
          arrayOfXRectangle[b].setAutoSynch(false);
        } 
        b = 1;
        x11.XSetForeground(param1Display, gC, new NativeLong(1L));
        x11.XFillRectangles(param1Display, (X11.Drawable)pixmap, gC, arrayOfXRectangle, arrayOfXRectangle.length);
      } finally {
        x11.XFreeGC(param1Display, gC);
      } 
      return pixmap;
    }
    
    public boolean isWindowAlphaSupported() {
      return ((getAlphaVisualIDs()).length > 0);
    }
    
    private static long getVisualID(GraphicsConfiguration param1GraphicsConfiguration) {
      try {
        Object object = param1GraphicsConfiguration.getClass().getMethod("getVisual", (Class[])null).invoke(param1GraphicsConfiguration, (Object[])null);
        return ((Number)object).longValue();
      } catch (Exception exception) {
        exception.printStackTrace();
        return -1L;
      } 
    }
    
    public GraphicsConfiguration getAlphaCompatibleGraphicsConfiguration() {
      if (isWindowAlphaSupported()) {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] arrayOfGraphicsDevice = graphicsEnvironment.getScreenDevices();
        for (byte b = 0; b < arrayOfGraphicsDevice.length; b++) {
          GraphicsConfiguration[] arrayOfGraphicsConfiguration = arrayOfGraphicsDevice[b].getConfigurations();
          for (byte b1 = 0; b1 < arrayOfGraphicsConfiguration.length; b1++) {
            long l = getVisualID(arrayOfGraphicsConfiguration[b1]);
            long[] arrayOfLong = getAlphaVisualIDs();
            for (byte b2 = 0; b2 < arrayOfLong.length; b2++) {
              if (l == arrayOfLong[b2])
                return arrayOfGraphicsConfiguration[b1]; 
            } 
          } 
        } 
      } 
      return super.getAlphaCompatibleGraphicsConfiguration();
    }
    
    private synchronized long[] getAlphaVisualIDs() {
      if (this.didCheck)
        return this.alphaVisualIDs; 
      this.didCheck = true;
      X11 x11 = X11.INSTANCE;
      X11.Display display = x11.XOpenDisplay(null);
      if (display == null)
        return this.alphaVisualIDs; 
      X11.XVisualInfo xVisualInfo = null;
      try {
        int i = x11.XDefaultScreen(display);
        X11.XVisualInfo xVisualInfo1 = new X11.XVisualInfo();
        xVisualInfo1.screen = i;
        xVisualInfo1.depth = 32;
        xVisualInfo1.c_class = 4;
        NativeLong nativeLong = new NativeLong(14L);
        IntByReference intByReference = new IntByReference();
        xVisualInfo = x11.XGetVisualInfo(display, nativeLong, xVisualInfo1, intByReference);
        if (xVisualInfo != null) {
          ArrayList<X11.VisualID> arrayList = new ArrayList();
          X11.XVisualInfo[] arrayOfXVisualInfo = (X11.XVisualInfo[])xVisualInfo.toArray(intByReference.getValue());
          byte b;
          for (b = 0; b < arrayOfXVisualInfo.length; b++) {
            X11.Xrender.XRenderPictFormat xRenderPictFormat = X11.Xrender.INSTANCE.XRenderFindVisualFormat(display, (arrayOfXVisualInfo[b]).visual);
            if (xRenderPictFormat.type == 1 && xRenderPictFormat.direct.alphaMask != 0)
              arrayList.add((arrayOfXVisualInfo[b]).visualid); 
          } 
          this.alphaVisualIDs = new long[arrayList.size()];
          for (b = 0; b < this.alphaVisualIDs.length; b++)
            this.alphaVisualIDs[b] = ((Number)arrayList.get(b)).longValue(); 
          return this.alphaVisualIDs;
        } 
      } finally {
        if (xVisualInfo != null)
          x11.XFree(xVisualInfo.getPointer()); 
        x11.XCloseDisplay(display);
      } 
      return this.alphaVisualIDs;
    }
    
    private static X11.Window getContentWindow(Window param1Window, X11.Display param1Display, X11.Window param1Window1, Point param1Point) {
      if ((param1Window instanceof Frame && !((Frame)param1Window).isUndecorated()) || (param1Window instanceof Dialog && !((Dialog)param1Window).isUndecorated())) {
        X11 x11 = X11.INSTANCE;
        X11.WindowByReference windowByReference1 = new X11.WindowByReference();
        X11.WindowByReference windowByReference2 = new X11.WindowByReference();
        PointerByReference pointerByReference = new PointerByReference();
        IntByReference intByReference = new IntByReference();
        x11.XQueryTree(param1Display, param1Window1, windowByReference1, windowByReference2, pointerByReference, intByReference);
        Pointer pointer = pointerByReference.getValue();
        int[] arrayOfInt1 = pointer.getIntArray(0L, intByReference.getValue());
        int[] arrayOfInt2 = arrayOfInt1;
        int i = arrayOfInt2.length;
        byte b = 0;
        if (b < i) {
          int j = arrayOfInt2[b];
          X11.Window window = new X11.Window(j);
          X11.XWindowAttributes xWindowAttributes = new X11.XWindowAttributes();
          x11.XGetWindowAttributes(param1Display, window, xWindowAttributes);
          param1Point.x = -xWindowAttributes.x;
          param1Point.y = -xWindowAttributes.y;
          param1Window1 = window;
        } 
        if (pointer != null)
          x11.XFree(pointer); 
      } 
      return param1Window1;
    }
    
    private static X11.Window getDrawable(Component param1Component) {
      int i = (int)Native.getComponentID(param1Component);
      return (i == 0) ? null : new X11.Window(i);
    }
    
    public void setWindowAlpha(final Window w, final float alpha) {
      if (!isWindowAlphaSupported())
        throw new UnsupportedOperationException("This X11 display does not provide a 32-bit visual"); 
      Runnable runnable = new Runnable() {
          public void run() {
            X11 x11 = X11.INSTANCE;
            X11.Display display = x11.XOpenDisplay(null);
            if (display == null)
              return; 
            try {
              X11.Window window = WindowUtils.X11WindowUtils.getDrawable(w);
              if (alpha == 1.0F) {
                x11.XDeleteProperty(display, window, x11.XInternAtom(display, "_NET_WM_WINDOW_OPACITY", false));
              } else {
                int i = (int)((long)(alpha * 4.2949673E9F) & 0xFFFFFFFFFFFFFFFFL);
                IntByReference intByReference = new IntByReference(i);
                x11.XChangeProperty(display, window, x11.XInternAtom(display, "_NET_WM_WINDOW_OPACITY", false), X11.XA_CARDINAL, 32, 0, intByReference.getPointer(), 1);
              } 
            } finally {
              x11.XCloseDisplay(display);
            } 
          }
        };
      whenDisplayable(w, runnable);
    }
    
    public void setWindowTransparent(final Window w, final boolean transparent) {
      if (!(w instanceof RootPaneContainer))
        throw new IllegalArgumentException("Window must be a RootPaneContainer"); 
      if (!isWindowAlphaSupported())
        throw new UnsupportedOperationException("This X11 display does not provide a 32-bit visual"); 
      if (!w.getGraphicsConfiguration().equals(getAlphaCompatibleGraphicsConfiguration()))
        throw new IllegalArgumentException("Window GraphicsConfiguration '" + w.getGraphicsConfiguration() + "' does not support transparency"); 
      boolean bool = (w.getBackground() != null && w.getBackground().getAlpha() == 0);
      if (transparent == bool)
        return; 
      whenDisplayable(w, new Runnable() {
            public void run() {
              JRootPane jRootPane = ((RootPaneContainer)w).getRootPane();
              JLayeredPane jLayeredPane = jRootPane.getLayeredPane();
              Container container = jRootPane.getContentPane();
              if (container instanceof WindowUtils.X11WindowUtils.X11TransparentContentPane) {
                ((WindowUtils.X11WindowUtils.X11TransparentContentPane)container).setTransparent(transparent);
              } else if (transparent) {
                WindowUtils.X11WindowUtils.X11TransparentContentPane x11TransparentContentPane = new WindowUtils.X11WindowUtils.X11TransparentContentPane(container);
                jRootPane.setContentPane(x11TransparentContentPane);
                jLayeredPane.add(new WindowUtils.RepaintTrigger(x11TransparentContentPane), JLayeredPane.DRAG_LAYER);
              } 
              WindowUtils.X11WindowUtils.this.setLayersTransparent(w, transparent);
              WindowUtils.X11WindowUtils.this.setForceHeavyweightPopups(w, transparent);
              WindowUtils.X11WindowUtils.this.setDoubleBuffered(w, !transparent);
            }
          });
    }
    
    private void setWindowShape(final Window w, final PixmapSource src) {
      Runnable runnable = new Runnable() {
          public void run() {
            X11 x11 = X11.INSTANCE;
            X11.Display display = x11.XOpenDisplay(null);
            if (display == null)
              return; 
            X11.Pixmap pixmap = null;
            try {
              X11.Window window = WindowUtils.X11WindowUtils.getDrawable(w);
              pixmap = src.getPixmap(display, window);
              X11.Xext xext = X11.Xext.INSTANCE;
              xext.XShapeCombineMask(display, window, 0, 0, 0, (pixmap == null) ? X11.Pixmap.None : pixmap, 0);
            } finally {
              if (pixmap != null)
                x11.XFreePixmap(display, pixmap); 
              x11.XCloseDisplay(display);
            } 
            WindowUtils.X11WindowUtils.this.setForceHeavyweightPopups(WindowUtils.X11WindowUtils.this.getWindow(w), (pixmap != null));
          }
        };
      whenDisplayable(w, runnable);
    }
    
    protected void setMask(Component param1Component, final Raster raster) {
      setWindowShape(getWindow(param1Component), new PixmapSource() {
            public X11.Pixmap getPixmap(X11.Display param2Display, X11.Window param2Window) {
              return (raster != null) ? WindowUtils.X11WindowUtils.createBitmap(param2Display, param2Window, raster) : null;
            }
          });
    }
    
    private static interface PixmapSource {
      X11.Pixmap getPixmap(X11.Display param2Display, X11.Window param2Window);
    }
    
    private class X11TransparentContentPane extends WindowUtils.NativeWindowUtils.TransparentContentPane {
      private static final long serialVersionUID = 1L;
      
      private Memory buffer;
      
      private int[] pixels;
      
      private final int[] pixel = new int[4];
      
      public X11TransparentContentPane(Container param2Container) {
        super(param2Container);
      }
      
      protected void paintDirect(BufferedImage param2BufferedImage, Rectangle param2Rectangle) {
        Window window = SwingUtilities.getWindowAncestor(this);
        X11 x11 = X11.INSTANCE;
        X11.Display display = x11.XOpenDisplay(null);
        X11.Window window1 = WindowUtils.X11WindowUtils.getDrawable(window);
        Point point = new Point();
        window1 = WindowUtils.X11WindowUtils.getContentWindow(window, display, window1, point);
        X11.GC gC = x11.XCreateGC(display, (X11.Drawable)window1, new NativeLong(0L), null);
        Raster raster = param2BufferedImage.getData();
        int i = param2Rectangle.width;
        int j = param2Rectangle.height;
        if (this.buffer == null || this.buffer.size() != (i * j * 4)) {
          this.buffer = new Memory((i * j * 4));
          this.pixels = new int[i * j];
        } 
        for (byte b = 0; b < j; b++) {
          for (byte b1 = 0; b1 < i; b1++) {
            raster.getPixel(b1, b, this.pixel);
            int k = this.pixel[3] & 0xFF;
            int m = this.pixel[2] & 0xFF;
            int n = this.pixel[1] & 0xFF;
            int i1 = this.pixel[0] & 0xFF;
            this.pixels[b * i + b1] = k << 24 | i1 << 16 | n << 8 | m;
          } 
        } 
        X11.XWindowAttributes xWindowAttributes = new X11.XWindowAttributes();
        x11.XGetWindowAttributes(display, window1, xWindowAttributes);
        X11.XImage xImage = x11.XCreateImage(display, xWindowAttributes.visual, 32, 2, 0, (Pointer)this.buffer, i, j, 32, i * 4);
        this.buffer.write(0L, this.pixels, 0, this.pixels.length);
        point.x += param2Rectangle.x;
        point.y += param2Rectangle.y;
        x11.XPutImage(display, (X11.Drawable)window1, gC, xImage, 0, 0, point.x, point.y, i, j);
        x11.XFree(xImage.getPointer());
        x11.XFreeGC(display, gC);
        x11.XCloseDisplay(display);
      }
    }
  }
  
  private static class MacWindowUtils extends NativeWindowUtils {
    private static final String WDRAG = "apple.awt.draggableWindowBackground";
    
    private MacWindowUtils() {}
    
    public boolean isWindowAlphaSupported() {
      return true;
    }
    
    private OSXMaskingContentPane installMaskingPane(Window param1Window) {
      OSXMaskingContentPane oSXMaskingContentPane;
      if (param1Window instanceof RootPaneContainer) {
        RootPaneContainer rootPaneContainer = (RootPaneContainer)param1Window;
        Container container = rootPaneContainer.getContentPane();
        if (container instanceof OSXMaskingContentPane) {
          oSXMaskingContentPane = (OSXMaskingContentPane)container;
        } else {
          oSXMaskingContentPane = new OSXMaskingContentPane(container);
          rootPaneContainer.setContentPane(oSXMaskingContentPane);
        } 
      } else {
        Component component = (param1Window.getComponentCount() > 0) ? param1Window.getComponent(0) : null;
        if (component instanceof OSXMaskingContentPane) {
          oSXMaskingContentPane = (OSXMaskingContentPane)component;
        } else {
          oSXMaskingContentPane = new OSXMaskingContentPane(component);
          param1Window.add(oSXMaskingContentPane);
        } 
      } 
      return oSXMaskingContentPane;
    }
    
    public void setWindowTransparent(Window param1Window, boolean param1Boolean) {
      boolean bool = (param1Window.getBackground() != null && param1Window.getBackground().getAlpha() == 0);
      if (param1Boolean != bool)
        setBackgroundTransparent(param1Window, param1Boolean, "setWindowTransparent"); 
    }
    
    private void fixWindowDragging(Window param1Window, String param1String) {
      if (param1Window instanceof RootPaneContainer) {
        JRootPane jRootPane = ((RootPaneContainer)param1Window).getRootPane();
        Boolean bool = (Boolean)jRootPane.getClientProperty("apple.awt.draggableWindowBackground");
        if (bool == null) {
          jRootPane.putClientProperty("apple.awt.draggableWindowBackground", Boolean.FALSE);
          if (param1Window.isDisplayable())
            WindowUtils.LOG.log(Level.WARNING, "{0}(): To avoid content dragging, {1}() must be called before the window is realized, or apple.awt.draggableWindowBackground must be set to Boolean.FALSE before the window is realized.  If you really want content dragging, set apple.awt.draggableWindowBackground on the window''s root pane to Boolean.TRUE before calling {2}() to hide this message.", new Object[] { param1String, param1String, param1String }); 
        } 
      } 
    }
    
    public void setWindowAlpha(final Window w, final float alpha) {
      if (w instanceof RootPaneContainer) {
        JRootPane jRootPane = ((RootPaneContainer)w).getRootPane();
        jRootPane.putClientProperty("Window.alpha", Float.valueOf(alpha));
        fixWindowDragging(w, "setWindowAlpha");
      } 
      whenDisplayable(w, new Runnable() {
            public void run() {
              try {
                Method method1 = w.getClass().getMethod("getPeer", new Class[0]);
                Object object = method1.invoke(w, new Object[0]);
                Method method2 = object.getClass().getMethod("setAlpha", new Class[] { float.class });
                method2.invoke(object, new Object[] { Float.valueOf(this.val$alpha) });
              } catch (Exception exception) {}
            }
          });
    }
    
    protected void setWindowMask(Component param1Component, Raster param1Raster) {
      if (param1Raster != null) {
        setWindowMask(param1Component, toShape(param1Raster));
      } else {
        setWindowMask(param1Component, new Rectangle(0, 0, param1Component.getWidth(), param1Component.getHeight()));
      } 
    }
    
    public void setWindowMask(Component param1Component, Shape param1Shape) {
      if (param1Component instanceof Window) {
        Window window = (Window)param1Component;
        OSXMaskingContentPane oSXMaskingContentPane = installMaskingPane(window);
        oSXMaskingContentPane.setMask(param1Shape);
        setBackgroundTransparent(window, (param1Shape != WindowUtils.MASK_NONE), "setWindowMask");
      } 
    }
    
    private void setBackgroundTransparent(Window param1Window, boolean param1Boolean, String param1String) {
      JRootPane jRootPane = (param1Window instanceof RootPaneContainer) ? ((RootPaneContainer)param1Window).getRootPane() : null;
      if (param1Boolean) {
        if (jRootPane != null)
          jRootPane.putClientProperty("transparent-old-bg", param1Window.getBackground()); 
        param1Window.setBackground(new Color(0, 0, 0, 0));
      } else if (jRootPane != null) {
        Color color = (Color)jRootPane.getClientProperty("transparent-old-bg");
        if (color != null)
          color = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()); 
        param1Window.setBackground(color);
        jRootPane.putClientProperty("transparent-old-bg", (Object)null);
      } else {
        param1Window.setBackground((Color)null);
      } 
      fixWindowDragging(param1Window, param1String);
    }
    
    private static class OSXMaskingContentPane extends JPanel {
      private static final long serialVersionUID = 1L;
      
      private Shape shape;
      
      public OSXMaskingContentPane(Component param2Component) {
        super(new BorderLayout());
        if (param2Component != null)
          add(param2Component, "Center"); 
      }
      
      public void setMask(Shape param2Shape) {
        this.shape = param2Shape;
        repaint();
      }
      
      public void paint(Graphics param2Graphics) {
        Graphics2D graphics2D = (Graphics2D)param2Graphics.create();
        graphics2D.setComposite(AlphaComposite.Clear);
        graphics2D.fillRect(0, 0, getWidth(), getHeight());
        graphics2D.dispose();
        if (this.shape != null) {
          graphics2D = (Graphics2D)param2Graphics.create();
          graphics2D.setClip(this.shape);
          super.paint(graphics2D);
          graphics2D.dispose();
        } else {
          super.paint(param2Graphics);
        } 
      }
    }
  }
  
  private static class W32WindowUtils extends NativeWindowUtils {
    private W32WindowUtils() {}
    
    private WinDef.HWND getHWnd(Component param1Component) {
      WinDef.HWND hWND = new WinDef.HWND();
      hWND.setPointer(Native.getComponentPointer(param1Component));
      return hWND;
    }
    
    public boolean isWindowAlphaSupported() {
      return Boolean.getBoolean("sun.java2d.noddraw");
    }
    
    private boolean usingUpdateLayeredWindow(Window param1Window) {
      if (param1Window instanceof RootPaneContainer) {
        JRootPane jRootPane = ((RootPaneContainer)param1Window).getRootPane();
        return (jRootPane.getClientProperty("transparent-old-bg") != null);
      } 
      return false;
    }
    
    private void storeAlpha(Window param1Window, byte param1Byte) {
      if (param1Window instanceof RootPaneContainer) {
        JRootPane jRootPane = ((RootPaneContainer)param1Window).getRootPane();
        Byte byte_ = (param1Byte == -1) ? null : Byte.valueOf(param1Byte);
        jRootPane.putClientProperty("transparent-alpha", byte_);
      } 
    }
    
    private byte getAlpha(Window param1Window) {
      if (param1Window instanceof RootPaneContainer) {
        JRootPane jRootPane = ((RootPaneContainer)param1Window).getRootPane();
        Byte byte_ = (Byte)jRootPane.getClientProperty("transparent-alpha");
        if (byte_ != null)
          return byte_.byteValue(); 
      } 
      return -1;
    }
    
    public void setWindowAlpha(final Window w, final float alpha) {
      if (!isWindowAlphaSupported())
        throw new UnsupportedOperationException("Set sun.java2d.noddraw=true to enable transparent windows"); 
      whenDisplayable(w, new Runnable() {
            public void run() {
              WinDef.HWND hWND = WindowUtils.W32WindowUtils.this.getHWnd(w);
              User32 user32 = User32.INSTANCE;
              int i = user32.GetWindowLong(hWND, -20);
              byte b = (byte)((int)(255.0F * alpha) & 0xFF);
              if (WindowUtils.W32WindowUtils.this.usingUpdateLayeredWindow(w)) {
                WinUser.BLENDFUNCTION bLENDFUNCTION = new WinUser.BLENDFUNCTION();
                bLENDFUNCTION.SourceConstantAlpha = b;
                bLENDFUNCTION.AlphaFormat = 1;
                user32.UpdateLayeredWindow(hWND, null, null, null, null, null, 0, bLENDFUNCTION, 2);
              } else if (alpha == 1.0F) {
                i &= 0xFFF7FFFF;
                user32.SetWindowLong(hWND, -20, i);
              } else {
                i |= 0x80000;
                user32.SetWindowLong(hWND, -20, i);
                user32.SetLayeredWindowAttributes(hWND, 0, b, 2);
              } 
              WindowUtils.W32WindowUtils.this.setForceHeavyweightPopups(w, (alpha != 1.0F));
              WindowUtils.W32WindowUtils.this.storeAlpha(w, b);
            }
          });
    }
    
    public void setWindowTransparent(final Window w, final boolean transparent) {
      if (!(w instanceof RootPaneContainer))
        throw new IllegalArgumentException("Window must be a RootPaneContainer"); 
      if (!isWindowAlphaSupported())
        throw new UnsupportedOperationException("Set sun.java2d.noddraw=true to enable transparent windows"); 
      boolean bool = (w.getBackground() != null && w.getBackground().getAlpha() == 0);
      if (transparent == bool)
        return; 
      whenDisplayable(w, new Runnable() {
            public void run() {
              User32 user32 = User32.INSTANCE;
              WinDef.HWND hWND = WindowUtils.W32WindowUtils.this.getHWnd(w);
              int i = user32.GetWindowLong(hWND, -20);
              JRootPane jRootPane = ((RootPaneContainer)w).getRootPane();
              JLayeredPane jLayeredPane = jRootPane.getLayeredPane();
              Container container = jRootPane.getContentPane();
              if (container instanceof WindowUtils.W32WindowUtils.W32TransparentContentPane) {
                ((WindowUtils.W32WindowUtils.W32TransparentContentPane)container).setTransparent(transparent);
              } else if (transparent) {
                WindowUtils.W32WindowUtils.W32TransparentContentPane w32TransparentContentPane = new WindowUtils.W32WindowUtils.W32TransparentContentPane(container);
                jRootPane.setContentPane(w32TransparentContentPane);
                jLayeredPane.add(new WindowUtils.RepaintTrigger(w32TransparentContentPane), JLayeredPane.DRAG_LAYER);
              } 
              if (transparent && !WindowUtils.W32WindowUtils.this.usingUpdateLayeredWindow(w)) {
                i |= 0x80000;
                user32.SetWindowLong(hWND, -20, i);
              } else if (!transparent && WindowUtils.W32WindowUtils.this.usingUpdateLayeredWindow(w)) {
                i &= 0xFFF7FFFF;
                user32.SetWindowLong(hWND, -20, i);
              } 
              WindowUtils.W32WindowUtils.this.setLayersTransparent(w, transparent);
              WindowUtils.W32WindowUtils.this.setForceHeavyweightPopups(w, transparent);
              WindowUtils.W32WindowUtils.this.setDoubleBuffered(w, !transparent);
            }
          });
    }
    
    public void setWindowMask(Component param1Component, Shape param1Shape) {
      if (param1Shape instanceof Area && ((Area)param1Shape).isPolygonal()) {
        setMask(param1Component, (Area)param1Shape);
      } else {
        super.setWindowMask(param1Component, param1Shape);
      } 
    }
    
    private void setWindowRegion(final Component w, final WinDef.HRGN hrgn) {
      whenDisplayable(w, new Runnable() {
            public void run() {
              GDI32 gDI32 = GDI32.INSTANCE;
              User32 user32 = User32.INSTANCE;
              WinDef.HWND hWND = WindowUtils.W32WindowUtils.this.getHWnd(w);
              try {
                user32.SetWindowRgn(hWND, hrgn, true);
                WindowUtils.W32WindowUtils.this.setForceHeavyweightPopups(WindowUtils.W32WindowUtils.this.getWindow(w), (hrgn != null));
              } finally {
                gDI32.DeleteObject((WinNT.HANDLE)hrgn);
              } 
            }
          });
    }
    
    private void setMask(Component param1Component, Area param1Area) {
      GDI32 gDI32 = GDI32.INSTANCE;
      PathIterator pathIterator = param1Area.getPathIterator(null);
      boolean bool = (pathIterator.getWindingRule() == 1) ? true : true;
      float[] arrayOfFloat = new float[6];
      ArrayList<WinDef.POINT> arrayList = new ArrayList();
      byte b1 = 0;
      ArrayList<Integer> arrayList1 = new ArrayList();
      while (!pathIterator.isDone()) {
        int i = pathIterator.currentSegment(arrayOfFloat);
        if (i == 0) {
          b1 = 1;
          arrayList.add(new WinDef.POINT((int)arrayOfFloat[0], (int)arrayOfFloat[1]));
        } else if (i == 1) {
          b1++;
          arrayList.add(new WinDef.POINT((int)arrayOfFloat[0], (int)arrayOfFloat[1]));
        } else if (i == 4) {
          arrayList1.add(Integer.valueOf(b1));
        } else {
          throw new RuntimeException("Area is not polygonal: " + param1Area);
        } 
        pathIterator.next();
      } 
      WinDef.POINT[] arrayOfPOINT1 = (WinDef.POINT[])(new WinDef.POINT()).toArray(arrayList.size());
      WinDef.POINT[] arrayOfPOINT2 = arrayList.<WinDef.POINT>toArray(new WinDef.POINT[arrayList.size()]);
      for (byte b2 = 0; b2 < arrayOfPOINT1.length; b2++) {
        (arrayOfPOINT1[b2]).x = (arrayOfPOINT2[b2]).x;
        (arrayOfPOINT1[b2]).y = (arrayOfPOINT2[b2]).y;
      } 
      int[] arrayOfInt = new int[arrayList1.size()];
      for (byte b3 = 0; b3 < arrayOfInt.length; b3++)
        arrayOfInt[b3] = ((Integer)arrayList1.get(b3)).intValue(); 
      WinDef.HRGN hRGN = gDI32.CreatePolyPolygonRgn(arrayOfPOINT1, arrayOfInt, arrayOfInt.length, bool);
      setWindowRegion(param1Component, hRGN);
    }
    
    protected void setMask(Component param1Component, Raster param1Raster) {
      GDI32 gDI32 = GDI32.INSTANCE;
      final WinDef.HRGN region = (param1Raster != null) ? gDI32.CreateRectRgn(0, 0, 0, 0) : null;
      if (hRGN != null) {
        final WinDef.HRGN tempRgn = gDI32.CreateRectRgn(0, 0, 0, 0);
        try {
          RasterRangesUtils.outputOccupiedRanges(param1Raster, new RasterRangesUtils.RangesOutput() {
                public boolean outputRange(int param2Int1, int param2Int2, int param2Int3, int param2Int4) {
                  GDI32 gDI32 = GDI32.INSTANCE;
                  gDI32.SetRectRgn(tempRgn, param2Int1, param2Int2, param2Int1 + param2Int3, param2Int2 + param2Int4);
                  return (gDI32.CombineRgn(region, region, tempRgn, 2) != 0);
                }
              });
        } finally {
          gDI32.DeleteObject((WinNT.HANDLE)hRGN1);
        } 
      } 
      setWindowRegion(param1Component, hRGN);
    }
    
    public BufferedImage getWindowIcon(WinDef.HWND param1HWND) {
      WinDef.DWORDByReference dWORDByReference = new WinDef.DWORDByReference();
      WinDef.LRESULT lRESULT = User32.INSTANCE.SendMessageTimeout(param1HWND, 127, new WinDef.WPARAM(1L), new WinDef.LPARAM(0L), 2, 500, dWORDByReference);
      if (lRESULT.intValue() == 0)
        lRESULT = User32.INSTANCE.SendMessageTimeout(param1HWND, 127, new WinDef.WPARAM(0L), new WinDef.LPARAM(0L), 2, 500, dWORDByReference); 
      if (lRESULT.intValue() == 0)
        lRESULT = User32.INSTANCE.SendMessageTimeout(param1HWND, 127, new WinDef.WPARAM(2L), new WinDef.LPARAM(0L), 2, 500, dWORDByReference); 
      if (lRESULT.intValue() == 0) {
        lRESULT = new WinDef.LRESULT(User32.INSTANCE.GetClassLongPtr(param1HWND, -14).intValue());
        dWORDByReference.getValue().setValue(lRESULT.intValue());
      } 
      if (lRESULT.intValue() == 0) {
        lRESULT = new WinDef.LRESULT(User32.INSTANCE.GetClassLongPtr(param1HWND, -34).intValue());
        dWORDByReference.getValue().setValue(lRESULT.intValue());
      } 
      if (lRESULT.intValue() == 0)
        return null; 
      WinDef.HICON hICON = new WinDef.HICON(new Pointer(dWORDByReference.getValue().longValue()));
      Dimension dimension = getIconSize(hICON);
      if (dimension.width == 0 || dimension.height == 0)
        return null; 
      int i = dimension.width;
      int j = dimension.height;
      byte b = 24;
      byte[] arrayOfByte1 = new byte[i * j * 24 / 8];
      Memory memory1 = new Memory(arrayOfByte1.length);
      byte[] arrayOfByte2 = new byte[i * j * 24 / 8];
      Memory memory2 = new Memory(arrayOfByte2.length);
      WinGDI.BITMAPINFO bITMAPINFO = new WinGDI.BITMAPINFO();
      WinGDI.BITMAPINFOHEADER bITMAPINFOHEADER = new WinGDI.BITMAPINFOHEADER();
      bITMAPINFO.bmiHeader = bITMAPINFOHEADER;
      bITMAPINFOHEADER.biWidth = i;
      bITMAPINFOHEADER.biHeight = j;
      bITMAPINFOHEADER.biPlanes = 1;
      bITMAPINFOHEADER.biBitCount = 24;
      bITMAPINFOHEADER.biCompression = 0;
      bITMAPINFOHEADER.write();
      bITMAPINFO.write();
      WinDef.HDC hDC = User32.INSTANCE.GetDC(null);
      WinGDI.ICONINFO iCONINFO = new WinGDI.ICONINFO();
      User32.INSTANCE.GetIconInfo(hICON, iCONINFO);
      iCONINFO.read();
      GDI32.INSTANCE.GetDIBits(hDC, iCONINFO.hbmColor, 0, j, (Pointer)memory1, bITMAPINFO, 0);
      memory1.read(0L, arrayOfByte1, 0, arrayOfByte1.length);
      GDI32.INSTANCE.GetDIBits(hDC, iCONINFO.hbmMask, 0, j, (Pointer)memory2, bITMAPINFO, 0);
      memory2.read(0L, arrayOfByte2, 0, arrayOfByte2.length);
      BufferedImage bufferedImage = new BufferedImage(i, j, 2);
      int k = 0;
      int m = j - 1;
      int n;
      for (n = 0; n < arrayOfByte1.length; n += 3) {
        int i3 = arrayOfByte1[n] & 0xFF;
        int i2 = arrayOfByte1[n + 1] & 0xFF;
        int i1 = arrayOfByte1[n + 2] & 0xFF;
        int i4 = 255 - arrayOfByte2[n] & 0xFF;
        int i5 = i4 << 24 | i1 << 16 | i2 << 8 | i3;
        bufferedImage.setRGB(k, m, i5);
        k = (k + 1) % i;
        if (k == 0)
          m--; 
      } 
      User32.INSTANCE.ReleaseDC(null, hDC);
      return bufferedImage;
    }
    
    public Dimension getIconSize(WinDef.HICON param1HICON) {
      WinGDI.ICONINFO iCONINFO = new WinGDI.ICONINFO();
      try {
        if (!User32.INSTANCE.GetIconInfo(param1HICON, iCONINFO))
          return new Dimension(); 
        iCONINFO.read();
        WinGDI.BITMAP bITMAP = new WinGDI.BITMAP();
        if (iCONINFO.hbmColor != null && iCONINFO.hbmColor.getPointer() != Pointer.NULL) {
          int i = GDI32.INSTANCE.GetObject((WinNT.HANDLE)iCONINFO.hbmColor, bITMAP.size(), bITMAP.getPointer());
          bITMAP.read();
          if (i > 0)
            return new Dimension(bITMAP.bmWidth.intValue(), bITMAP.bmHeight.intValue()); 
        } else if (iCONINFO.hbmMask != null && iCONINFO.hbmMask.getPointer() != Pointer.NULL) {
          int i = GDI32.INSTANCE.GetObject((WinNT.HANDLE)iCONINFO.hbmMask, bITMAP.size(), bITMAP.getPointer());
          bITMAP.read();
          if (i > 0)
            return new Dimension(bITMAP.bmWidth.intValue(), bITMAP.bmHeight.intValue() / 2); 
        } 
      } finally {
        if (iCONINFO.hbmColor != null && iCONINFO.hbmColor.getPointer() != Pointer.NULL)
          GDI32.INSTANCE.DeleteObject((WinNT.HANDLE)iCONINFO.hbmColor); 
        if (iCONINFO.hbmMask != null && iCONINFO.hbmMask.getPointer() != Pointer.NULL)
          GDI32.INSTANCE.DeleteObject((WinNT.HANDLE)iCONINFO.hbmMask); 
      } 
      return new Dimension();
    }
    
    public List<DesktopWindow> getAllWindows(final boolean onlyVisibleWindows) {
      final LinkedList<DesktopWindow> result = new LinkedList();
      WinUser.WNDENUMPROC wNDENUMPROC = new WinUser.WNDENUMPROC() {
          public boolean callback(WinDef.HWND param2HWND, Pointer param2Pointer) {
            try {
              boolean bool = (!onlyVisibleWindows || User32.INSTANCE.IsWindowVisible(param2HWND)) ? true : false;
              if (bool) {
                String str1 = WindowUtils.W32WindowUtils.this.getWindowTitle(param2HWND);
                String str2 = WindowUtils.W32WindowUtils.this.getProcessFilePath(param2HWND);
                Rectangle rectangle = WindowUtils.W32WindowUtils.this.getWindowLocationAndSize(param2HWND);
                result.add(new DesktopWindow(param2HWND, str1, str2, rectangle));
              } 
            } catch (Exception exception) {
              exception.printStackTrace();
            } 
            return true;
          }
        };
      if (!User32.INSTANCE.EnumWindows(wNDENUMPROC, null))
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
      return linkedList;
    }
    
    public String getWindowTitle(WinDef.HWND param1HWND) {
      int i = User32.INSTANCE.GetWindowTextLength(param1HWND) + 1;
      char[] arrayOfChar = new char[i];
      int j = User32.INSTANCE.GetWindowText(param1HWND, arrayOfChar, arrayOfChar.length);
      return Native.toString(Arrays.copyOfRange(arrayOfChar, 0, j));
    }
    
    public String getProcessFilePath(WinDef.HWND param1HWND) {
      IntByReference intByReference = new IntByReference();
      User32.INSTANCE.GetWindowThreadProcessId(param1HWND, intByReference);
      WinNT.HANDLE hANDLE = Kernel32.INSTANCE.OpenProcess(1024, false, intByReference.getValue());
      if (hANDLE == null) {
        if (Kernel32.INSTANCE.GetLastError() != 5)
          throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
        hANDLE = Kernel32.INSTANCE.OpenProcess(4096, false, intByReference.getValue());
        if (hANDLE == null) {
          if (Kernel32.INSTANCE.GetLastError() != 5)
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
          return "";
        } 
      } 
      try {
        String str = PsapiUtil.GetProcessImageFileName(hANDLE);
        if (str.startsWith("\\Device\\Mup\\"))
          return "\\" + str.substring(11); 
        char[] arrayOfChar = new char[50];
        WinNT.HANDLE hANDLE1 = Kernel32.INSTANCE.FindFirstVolume(arrayOfChar, 50);
        if (hANDLE1 == null || hANDLE1.equals(WinBase.INVALID_HANDLE_VALUE))
          throw new Win32Exception(Native.getLastError()); 
      } finally {
        Kernel32.INSTANCE.CloseHandle(hANDLE);
      } 
    }
    
    public Rectangle getWindowLocationAndSize(WinDef.HWND param1HWND) {
      WinDef.RECT rECT = new WinDef.RECT();
      if (!User32.INSTANCE.GetWindowRect(param1HWND, rECT))
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
      return new Rectangle(rECT.left, rECT.top, Math.abs(rECT.right - rECT.left), Math.abs(rECT.bottom - rECT.top));
    }
    
    private class W32TransparentContentPane extends WindowUtils.NativeWindowUtils.TransparentContentPane {
      private static final long serialVersionUID = 1L;
      
      private WinDef.HDC memDC;
      
      private WinDef.HBITMAP hBitmap;
      
      private Pointer pbits;
      
      private Dimension bitmapSize;
      
      public W32TransparentContentPane(Container param2Container) {
        super(param2Container);
      }
      
      private void disposeBackingStore() {
        GDI32 gDI32 = GDI32.INSTANCE;
        if (this.hBitmap != null) {
          gDI32.DeleteObject((WinNT.HANDLE)this.hBitmap);
          this.hBitmap = null;
        } 
        if (this.memDC != null) {
          gDI32.DeleteDC(this.memDC);
          this.memDC = null;
        } 
      }
      
      public void removeNotify() {
        super.removeNotify();
        disposeBackingStore();
      }
      
      public void setTransparent(boolean param2Boolean) {
        super.setTransparent(param2Boolean);
        if (!param2Boolean)
          disposeBackingStore(); 
      }
      
      protected void paintDirect(BufferedImage param2BufferedImage, Rectangle param2Rectangle) {
        Window window = SwingUtilities.getWindowAncestor(this);
        GDI32 gDI32 = GDI32.INSTANCE;
        User32 user32 = User32.INSTANCE;
        int i = param2Rectangle.x;
        int j = param2Rectangle.y;
        Point point = SwingUtilities.convertPoint(this, i, j, window);
        int k = param2Rectangle.width;
        int m = param2Rectangle.height;
        int n = window.getWidth();
        int i1 = window.getHeight();
        WinDef.HDC hDC = user32.GetDC(null);
        WinNT.HANDLE hANDLE = null;
        try {
          if (this.memDC == null)
            this.memDC = gDI32.CreateCompatibleDC(hDC); 
          if (this.hBitmap == null || !window.getSize().equals(this.bitmapSize)) {
            if (this.hBitmap != null) {
              gDI32.DeleteObject((WinNT.HANDLE)this.hBitmap);
              this.hBitmap = null;
            } 
            WinGDI.BITMAPINFO bITMAPINFO = new WinGDI.BITMAPINFO();
            bITMAPINFO.bmiHeader.biWidth = n;
            bITMAPINFO.bmiHeader.biHeight = i1;
            bITMAPINFO.bmiHeader.biPlanes = 1;
            bITMAPINFO.bmiHeader.biBitCount = 32;
            bITMAPINFO.bmiHeader.biCompression = 0;
            bITMAPINFO.bmiHeader.biSizeImage = n * i1 * 4;
            PointerByReference pointerByReference = new PointerByReference();
            this.hBitmap = gDI32.CreateDIBSection(this.memDC, bITMAPINFO, 0, pointerByReference, null, 0);
            this.pbits = pointerByReference.getValue();
            this.bitmapSize = new Dimension(n, i1);
          } 
          hANDLE = gDI32.SelectObject(this.memDC, (WinNT.HANDLE)this.hBitmap);
          Raster raster = param2BufferedImage.getData();
          int[] arrayOfInt1 = new int[4];
          int[] arrayOfInt2 = new int[k];
          for (byte b = 0; b < m; b++) {
            int i2;
            for (i2 = 0; i2 < k; i2++) {
              raster.getPixel(i2, b, arrayOfInt1);
              int i3 = (arrayOfInt1[3] & 0xFF) << 24;
              int i4 = arrayOfInt1[2] & 0xFF;
              int i5 = (arrayOfInt1[1] & 0xFF) << 8;
              int i6 = (arrayOfInt1[0] & 0xFF) << 16;
              arrayOfInt2[i2] = i3 | i4 | i5 | i6;
            } 
            i2 = i1 - point.y + b - 1;
            this.pbits.write(((i2 * n + point.x) * 4), arrayOfInt2, 0, arrayOfInt2.length);
          } 
          WinUser.SIZE sIZE = new WinUser.SIZE();
          sIZE.cx = window.getWidth();
          sIZE.cy = window.getHeight();
          WinDef.POINT pOINT1 = new WinDef.POINT();
          pOINT1.x = window.getX();
          pOINT1.y = window.getY();
          WinDef.POINT pOINT2 = new WinDef.POINT();
          WinUser.BLENDFUNCTION bLENDFUNCTION = new WinUser.BLENDFUNCTION();
          WinDef.HWND hWND = WindowUtils.W32WindowUtils.this.getHWnd(window);
          ByteByReference byteByReference = new ByteByReference();
          IntByReference intByReference = new IntByReference();
          byte b1 = WindowUtils.W32WindowUtils.this.getAlpha(window);
          try {
            if (user32.GetLayeredWindowAttributes(hWND, null, byteByReference, intByReference) && (intByReference.getValue() & 0x2) != 0)
              b1 = byteByReference.getValue(); 
          } catch (UnsatisfiedLinkError unsatisfiedLinkError) {}
          bLENDFUNCTION.SourceConstantAlpha = b1;
          bLENDFUNCTION.AlphaFormat = 1;
          user32.UpdateLayeredWindow(hWND, hDC, pOINT1, sIZE, this.memDC, pOINT2, 0, bLENDFUNCTION, 2);
        } finally {
          user32.ReleaseDC(null, hDC);
          if (this.memDC != null && hANDLE != null)
            gDI32.SelectObject(this.memDC, hANDLE); 
        } 
      }
    }
  }
  
  protected static class RepaintTrigger extends JComponent {
    private static final long serialVersionUID = 1L;
    
    private final Listener listener = createListener();
    
    private final JComponent content;
    
    private Rectangle dirty;
    
    public RepaintTrigger(JComponent param1JComponent) {
      this.content = param1JComponent;
    }
    
    public void addNotify() {
      super.addNotify();
      Window window = SwingUtilities.getWindowAncestor(this);
      setSize(getParent().getSize());
      window.addComponentListener(this.listener);
      window.addWindowListener(this.listener);
      Toolkit.getDefaultToolkit().addAWTEventListener(this.listener, 48L);
    }
    
    public void removeNotify() {
      Toolkit.getDefaultToolkit().removeAWTEventListener(this.listener);
      Window window = SwingUtilities.getWindowAncestor(this);
      window.removeComponentListener(this.listener);
      window.removeWindowListener(this.listener);
      super.removeNotify();
    }
    
    protected void paintComponent(Graphics param1Graphics) {
      Rectangle rectangle = param1Graphics.getClipBounds();
      if (this.dirty == null || !this.dirty.contains(rectangle)) {
        if (this.dirty == null) {
          this.dirty = rectangle;
        } else {
          this.dirty = this.dirty.union(rectangle);
        } 
        this.content.repaint(this.dirty);
      } else {
        this.dirty = null;
      } 
    }
    
    protected Listener createListener() {
      return new Listener();
    }
    
    protected class Listener extends WindowAdapter implements ComponentListener, HierarchyListener, AWTEventListener {
      public void windowOpened(WindowEvent param2WindowEvent) {
        WindowUtils.RepaintTrigger.this.repaint();
      }
      
      public void componentHidden(ComponentEvent param2ComponentEvent) {}
      
      public void componentMoved(ComponentEvent param2ComponentEvent) {}
      
      public void componentResized(ComponentEvent param2ComponentEvent) {
        WindowUtils.RepaintTrigger.this.setSize(WindowUtils.RepaintTrigger.this.getParent().getSize());
        WindowUtils.RepaintTrigger.this.repaint();
      }
      
      public void componentShown(ComponentEvent param2ComponentEvent) {
        WindowUtils.RepaintTrigger.this.repaint();
      }
      
      public void hierarchyChanged(HierarchyEvent param2HierarchyEvent) {
        WindowUtils.RepaintTrigger.this.repaint();
      }
      
      public void eventDispatched(AWTEvent param2AWTEvent) {
        if (param2AWTEvent instanceof MouseEvent) {
          Component component = ((MouseEvent)param2AWTEvent).getComponent();
          if (component != null && SwingUtilities.isDescendingFrom(component, WindowUtils.RepaintTrigger.this.content)) {
            MouseEvent mouseEvent = SwingUtilities.convertMouseEvent(component, (MouseEvent)param2AWTEvent, WindowUtils.RepaintTrigger.this.content);
            Component component1 = SwingUtilities.getDeepestComponentAt(WindowUtils.RepaintTrigger.this.content, mouseEvent.getX(), mouseEvent.getY());
            if (component1 != null)
              WindowUtils.RepaintTrigger.this.setCursor(component1.getCursor()); 
          } 
        } 
      }
    }
  }
  
  private static class HeavyweightForcer extends Window {
    private static final long serialVersionUID = 1L;
    
    private final boolean packed;
    
    public HeavyweightForcer(Window param1Window) {
      super(param1Window);
      pack();
      this.packed = true;
    }
    
    public boolean isVisible() {
      return this.packed;
    }
    
    public Rectangle getBounds() {
      return getOwner().getBounds();
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\WindowUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */