package pro.gravit.launcher.gui.helper;

import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.Parent;

public class LookupHelper {
  public static <T extends Node> T lookup(Node paramNode, String... paramVarArgs) {
    Node node = paramNode;
    if (node == null)
      throw new NullPointerException(); 
    for (byte b = 0; b < paramVarArgs.length; b++) {
      node = node.lookup(paramVarArgs[b]);
      if (node == null)
        throw new LookupException(paramVarArgs, b); 
    } 
    return (T)node;
  }
  
  public static <T extends Node> Optional<T> lookupIfPossible(Node paramNode, String... paramVarArgs) {
    Node node = paramNode;
    if (node == null)
      return Optional.empty(); 
    for (String str : paramVarArgs) {
      node = node.lookup(str);
      if (node == null)
        return Optional.empty(); 
    } 
    return Optional.of((T)node);
  }
  
  public static <T extends Node> Point2D getAbsoluteCords(Node paramNode1, Node paramNode2) {
    Point2D point2D = new Point2D(0.0D, 0.0D);
    Parent parent = (Parent)paramNode1;
    while (parent != paramNode2) {
      point2D.x += parent.getLayoutX();
      point2D.y += parent.getLayoutY();
      parent = parent.getParent();
      if (parent == null)
        break; 
    } 
    return point2D;
  }
  
  static {
  
  }
  
  public static class LookupException extends RuntimeException {
    public LookupException(String[] param1ArrayOfString, int param1Int) {
      super(buildStack(param1ArrayOfString, param1Int));
    }
    
    private static String buildStack(String[] param1ArrayOfString, int param1Int) {
      StringBuilder stringBuilder = new StringBuilder("Lookup failed ");
      boolean bool = true;
      for (byte b = 0; b < param1ArrayOfString.length; b++) {
        if (!bool)
          stringBuilder.append("->"); 
        stringBuilder.append(param1ArrayOfString[b]);
        if (b == param1Int)
          stringBuilder.append("(E)"); 
        bool = false;
      } 
      return stringBuilder.toString();
    }
    
    static {
    
    }
  }
  
  public static class Point2D {
    public double x;
    
    public double y;
    
    public Point2D(double param1Double1, double param1Double2) {
      this.x = param1Double1;
      this.y = param1Double2;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\helper\LookupHelper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */