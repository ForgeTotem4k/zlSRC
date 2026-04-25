package oshi.util.tuples;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class Pair<A, B> {
  private final A a;
  
  private final B b;
  
  public Pair(A paramA, B paramB) {
    this.a = paramA;
    this.b = paramB;
  }
  
  public final A getA() {
    return this.a;
  }
  
  public final B getB() {
    return this.b;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\tuples\Pair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */