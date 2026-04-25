package pro.gravit.launcher.gui.helper;

public enum PositionInfo {
  TOP_LEFT(0.0D, 0.0D, true, true),
  TOP_RIGHT(Double.MAX_VALUE, 0.0D, false, true),
  BOTTOM_LEFT(0.0D, Double.MAX_VALUE, false, true),
  BOTTOM_RIGHT(Double.MAX_VALUE, Double.MAX_VALUE, false, false);
  
  public final double startX;
  
  public final double startY;
  
  public final boolean offsetXPlus;
  
  public final boolean offsetYPlus;
  
  PositionInfo(double paramDouble1, double paramDouble2, boolean paramBoolean1, boolean paramBoolean2) {
    this.startX = paramDouble1;
    this.startY = paramDouble2;
    this.offsetXPlus = paramBoolean1;
    this.offsetYPlus = paramBoolean2;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\helper\PositionHelper$PositionInfo.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */