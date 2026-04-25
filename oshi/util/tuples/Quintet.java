package oshi.util.tuples;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class Quintet<A, B, C, D, E> {
  private final A a;
  
  private final B b;
  
  private final C c;
  
  private final D d;
  
  private final E e;
  
  public Quintet(A paramA, B paramB, C paramC, D paramD, E paramE) {
    this.a = paramA;
    this.b = paramB;
    this.c = paramC;
    this.d = paramD;
    this.e = paramE;
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
  
  public final E getE() {
    return this.e;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\tuples\Quintet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */