package oshi.util.tuples;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class Triplet<A, B, C> {
  private final A a;
  
  private final B b;
  
  private final C c;
  
  public Triplet(A paramA, B paramB, C paramC) {
    this.a = paramA;
    this.b = paramB;
    this.c = paramC;
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
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\tuples\Triplet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */