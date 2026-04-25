package io.sentry;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

class SynchronizedCollection<E> implements Collection<E>, Serializable {
  private static final long serialVersionUID = 2412805092710877986L;
  
  private final Collection<E> collection;
  
  final Object lock;
  
  public static <T> SynchronizedCollection<T> synchronizedCollection(Collection<T> paramCollection) {
    return new SynchronizedCollection<>(paramCollection);
  }
  
  SynchronizedCollection(Collection<E> paramCollection) {
    if (paramCollection == null)
      throw new NullPointerException("Collection must not be null."); 
    this.collection = paramCollection;
    this.lock = this;
  }
  
  SynchronizedCollection(Collection<E> paramCollection, Object paramObject) {
    if (paramCollection == null)
      throw new NullPointerException("Collection must not be null."); 
    if (paramObject == null)
      throw new NullPointerException("Lock must not be null."); 
    this.collection = paramCollection;
    this.lock = paramObject;
  }
  
  protected Collection<E> decorated() {
    return this.collection;
  }
  
  public boolean add(E paramE) {
    synchronized (this.lock) {
      return decorated().add(paramE);
    } 
  }
  
  public boolean addAll(Collection<? extends E> paramCollection) {
    synchronized (this.lock) {
      return decorated().addAll(paramCollection);
    } 
  }
  
  public void clear() {
    synchronized (this.lock) {
      decorated().clear();
    } 
  }
  
  public boolean contains(Object paramObject) {
    synchronized (this.lock) {
      return decorated().contains(paramObject);
    } 
  }
  
  public boolean containsAll(Collection<?> paramCollection) {
    synchronized (this.lock) {
      return decorated().containsAll(paramCollection);
    } 
  }
  
  public boolean isEmpty() {
    synchronized (this.lock) {
      return decorated().isEmpty();
    } 
  }
  
  public Iterator<E> iterator() {
    return decorated().iterator();
  }
  
  public Object[] toArray() {
    synchronized (this.lock) {
      return decorated().toArray();
    } 
  }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    synchronized (this.lock) {
      return decorated().toArray(paramArrayOfT);
    } 
  }
  
  public boolean remove(Object paramObject) {
    synchronized (this.lock) {
      return decorated().remove(paramObject);
    } 
  }
  
  public boolean removeAll(Collection<?> paramCollection) {
    synchronized (this.lock) {
      return decorated().removeAll(paramCollection);
    } 
  }
  
  public boolean retainAll(Collection<?> paramCollection) {
    synchronized (this.lock) {
      return decorated().retainAll(paramCollection);
    } 
  }
  
  public int size() {
    synchronized (this.lock) {
      return decorated().size();
    } 
  }
  
  public boolean equals(Object paramObject) {
    synchronized (this.lock) {
      if (paramObject == this)
        return true; 
      return (paramObject == this || decorated().equals(paramObject));
    } 
  }
  
  public int hashCode() {
    synchronized (this.lock) {
      return decorated().hashCode();
    } 
  }
  
  public String toString() {
    synchronized (this.lock) {
      return decorated().toString();
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SynchronizedCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */