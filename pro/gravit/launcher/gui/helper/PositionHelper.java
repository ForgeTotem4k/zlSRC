package pro.gravit.launcher.gui.helper;

public class PositionHelper {
  private PositionHelper() {
    throw new UnsupportedOperationException();
  }
  
  public static LookupHelper.Point2D calculate(PositionInfo paramPositionInfo, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6) {
    double d1 = paramPositionInfo.startX;
    double d2 = paramPositionInfo.startY;
    d1 = Math.min(d1, paramDouble5);
    d2 = Math.min(d2, paramDouble6);
    if (paramPositionInfo.offsetXPlus) {
      d1 += paramDouble1;
      d1 += paramDouble3;
    } else {
      d1 -= paramDouble1;
      d1 -= paramDouble3;
    } 
    if (paramPositionInfo.offsetYPlus) {
      d2 += paramDouble2;
      d2 += paramDouble4;
    } else {
      d2 -= paramDouble2;
      d2 -= paramDouble4;
    } 
    return new LookupHelper.Point2D(d1, d2);
  }
  
  static {
  
  }
  
  public enum PositionInfo {
    TOP_LEFT(0.0D, 0.0D, true, true),
    TOP_RIGHT(Double.MAX_VALUE, 0.0D, false, true),
    BOTTOM_LEFT(0.0D, Double.MAX_VALUE, false, true),
    BOTTOM_RIGHT(Double.MAX_VALUE, Double.MAX_VALUE, false, false);
    
    public final double startX;
    
    public final double startY;
    
    public final boolean offsetXPlus;
    
    public final boolean offsetYPlus;
    
    PositionInfo(double param1Double1, double param1Double2, boolean param1Boolean1, boolean param1Boolean2) {
      this.startX = param1Double1;
      this.startY = param1Double2;
      this.offsetXPlus = param1Boolean1;
      this.offsetYPlus = param1Boolean2;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\helper\PositionHelper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */