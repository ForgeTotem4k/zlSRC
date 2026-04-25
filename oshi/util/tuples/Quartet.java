package oshi.util.tuples;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class Quartet<A, B, C, D> {
  private final A a;
  
  private final B b;
  
  private final C c;
  
  private final D d;
  
  public Quartet(A paramA, B paramB, C paramC, D paramD) {
    this.a = paramA;
    this.b = paramB;
    this.c = paramC;
    this.d = paramD;
  }
  
  public final A getA() {
    return this.a;
  }
  
  public final B getB() {
    return this.b;
  }
  
  public final C getC() {
    return this.c;
  }
  
  public final D getD() {
    return this.d;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\tuples\Quartet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */