package com.google.gson.internal;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.RandomAccess;

public class NonNullElementWrapperList<E> extends AbstractList<E> implements RandomAccess {
  private final ArrayList<E> delegate;
  
  public NonNullElementWrapperList(ArrayList<E> paramArrayList) {
    this.delegate = Objects.<ArrayList<E>>requireNonNull(paramArrayList);
  }
  
  public E get(int paramInt) {
    return this.delegate.get(paramInt);
  }
  
  public int size() {
    return this.delegate.size();
  }
  
  private E nonNull(E paramE) {
    if (paramE == null)
      throw new NullPointerException("Element must be non-null"); 
    return paramE;
  }
  
  public E set(int paramInt, E paramE) {
    return this.delegate.set(paramInt, nonNull(paramE));
  }
  
  public void add(int paramInt, E paramE) {
    this.delegate.add(paramInt, nonNull(paramE));
  }
  
  public E remove(int paramInt) {
    return this.delegate.remove(paramInt);
  }
  
  public void clear() {
    this.delegate.clear();
  }
  
  public boolean remove(Object paramObject) {
    return this.delegate.remove(paramObject);
  }
  
  public boolean removeAll(Collection<?> paramCollection) {
    return this.delegate.removeAll(paramCollection);
  }
  
  public boolean retainAll(Collection<?> paramCollection) {
    return this.delegate.retainAll(paramCollection);
  }
  
  public boolean contains(Object paramObject) {
    return this.delegate.contains(paramObject);
  }
  
  public int indexOf(Object paramObject) {
    return this.delegate.indexOf(paramObject);
  }
  
  public int lastIndexOf(Object paramObject) {
    return this.delegate.lastIndexOf(paramObject);
  }
  
  public Object[] toArray() {
    return this.delegate.toArray();
  }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    return this.delegate.toArray(paramArrayOfT);
  }
  
  public boolean equals(Object paramObject) {
    return this.delegate.equals(paramObject);
  }
  
  public int hashCode() {
    return this.delegate.hashCode();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\NonNullElementWrapperList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */