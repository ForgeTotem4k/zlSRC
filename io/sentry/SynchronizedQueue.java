package io.sentry;

import java.util.Collection;
import java.util.Queue;

final class SynchronizedQueue<E> extends SynchronizedCollection<E> implements Queue<E> {
  private static final long serialVersionUID = 1L;
  
  static <E> SynchronizedQueue<E> synchronizedQueue(Queue<E> paramQueue) {
    return new SynchronizedQueue<>(paramQueue);
  }
  
  private SynchronizedQueue(Queue<E> paramQueue) {
    super(paramQueue);
  }
  
  protected SynchronizedQueue(Queue<E> paramQueue, Object paramObject) {
    super(paramQueue, paramObject);
  }
  
  protected Queue<E> decorated() {
    return (Queue<E>)super.decorated();
  }
  
  public E element() {
    synchronized (this.lock) {
      return decorated().element();
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    synchronized (this.lock) {
      return decorated().equals(paramObject);
    } 
  }
  
  public int hashCode() {
    synchronized (this.lock) {
      return decorated().hashCode();
    } 
  }
  
  public boolean offer(E paramE) {
    synchronized (this.lock) {
      return decorated().offer(paramE);
    } 
  }
  
  public E peek() {
    synchronized (this.lock) {
      return decorated().peek();
    } 
  }
  
  public E poll() {
    synchronized (this.lock) {
      return decorated().poll();
    } 
  }
  
  public E remove() {
    synchronized (this.lock) {
      return decorated().remove();
    } 
  }
  
  public Object[] toArray() {
    synchronized (this.lock) {
      return decorated().toArray();
    } 
  }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    synchronized (this.lock) {
      return (T[])decorated().toArray((Object[])paramArrayOfT);
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SynchronizedQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */