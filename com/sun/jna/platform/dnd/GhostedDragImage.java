package com.sun.jna.platform.dnd;

import com.sun.jna.platform.WindowUtils;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GhostedDragImage {
  private static final float DEFAULT_ALPHA = 0.5F;
  
  private Window dragImage;
  
  private Point origin;
  
  private static final int SLIDE_INTERVAL = 33;
  
  public GhostedDragImage(Component paramComponent, final Icon icon, Point paramPoint1, final Point cursorOffset) {
    Window window = (paramComponent instanceof Window) ? (Window)paramComponent : SwingUtilities.getWindowAncestor(paramComponent);
    GraphicsConfiguration graphicsConfiguration = window.getGraphicsConfiguration();
    this.dragImage = new Window(JOptionPane.getRootFrame(), graphicsConfiguration) {
        private static final long serialVersionUID = 1L;
        
        public void paint(Graphics param1Graphics) {
          icon.paintIcon(this, param1Graphics, 0, 0);
        }
        
        public Dimension getPreferredSize() {
          return new Dimension(icon.getIconWidth(), icon.getIconHeight());
        }
        
        public Dimension getMinimumSize() {
          return getPreferredSize();
        }
        
        public Dimension getMaximumSize() {
          return getPreferredSize();
        }
      };
    this.dragImage.setFocusableWindowState(false);
    this.dragImage.setName("###overrideRedirect###");
    Icon icon = new Icon() {
        public int getIconHeight() {
          return icon.getIconHeight();
        }
        
        public int getIconWidth() {
          return icon.getIconWidth();
        }
        
        public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
          param1Graphics = param1Graphics.create();
          Area area = new Area(new Rectangle(param1Int1, param1Int2, getIconWidth(), getIconHeight()));
          area.subtract(new Area(new Rectangle(param1Int1 + cursorOffset.x - 1, param1Int2 + cursorOffset.y - 1, 3, 3)));
          param1Graphics.setClip(area);
          icon.paintIcon(param1Component, param1Graphics, param1Int1, param1Int2);
          param1Graphics.dispose();
        }
      };
    this.dragImage.pack();
    WindowUtils.setWindowMask(this.dragImage, icon);
    WindowUtils.setWindowAlpha(this.dragImage, 0.5F);
    move(paramPoint1);
    this.dragImage.setVisible(true);
  }
  
  public void setAlpha(float paramFloat) {
    WindowUtils.setWindowAlpha(this.dragImage, paramFloat);
  }
  
  public void dispose() {
    this.dragImage.dispose();
    this.dragImage = null;
  }
  
  public void move(Point paramPoint) {
    if (this.origin == null)
      this.origin = paramPoint; 
    this.dragImage.setLocation(paramPoint.x, paramPoint.y);
  }
  
  public void returnToOrigin() {
    final Timer timer = new Timer(33, null);
    timer.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent param1ActionEvent) {
            Point point1 = GhostedDragImage.this.dragImage.getLocationOnScreen();
            Point point2 = new Point(GhostedDragImage.this.origin);
            int i = (point2.x - point1.x) / 2;
            int j = (point2.y - point1.y) / 2;
            if (i != 0 || j != 0) {
              point1.translate(i, j);
              GhostedDragImage.this.move(point1);
            } else {
              timer.stop();
              GhostedDragImage.this.dispose();
            } 
          }
        });
    timer.setInitialDelay(0);
    timer.start();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\dnd\GhostedDragImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */