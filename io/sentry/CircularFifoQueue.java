package io.sentry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class CircularFifoQueue<E> extends AbstractCollection<E> implements Queue<E>, Serializable {
  private static final long serialVersionUID = -8423413834657610406L;
  
  @NotNull
  private transient E[] elements;
  
  private transient int start = 0;
  
  private transient int end = 0;
  
  private transient boolean full = false;
  
  private final int maxElements;
  
  public CircularFifoQueue() {
    this(32);
  }
  
  CircularFifoQueue(int paramInt) {
    if (paramInt <= 0)
      throw new IllegalArgumentException("The size must be greater than 0"); 
    this.elements = (E[])new Object[paramInt];
    this.maxElements = this.elements.length;
  }
  
  public CircularFifoQueue(@NotNull Collection<? extends E> paramCollection) {
    this(paramCollection.size());
    addAll(paramCollection);
  }
  
  private void writeObject(@NotNull ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(size());
    for (E e : this)
      paramObjectOutputStream.writeObject(e); 
  }
  
  private void readObject(@NotNull ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.elements = (E[])new Object[this.maxElements];
    int i = paramObjectInputStream.readInt();
    for (byte b = 0; b < i; b++)
      this.elements[b] = (E)paramObjectInputStream.readObject(); 
    this.start = 0;
    this.full = (i == this.maxElements);
    if (this.full) {
      this.end = 0;
    } else {
      this.end = i;
    } 
  }
  
  public int size() {
    int i = 0;
    if (this.end < this.start) {
      i = this.maxElements - this.start + this.end;
    } else if (this.end == this.start) {
      i = this.full ? this.maxElements : 0;
    } else {
      i = this.end - this.start;
    } 
    return i;
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public boolean isFull() {
    return false;
  }
  
  public boolean isAtFullCapacity() {
    return (size() == this.maxElements);
  }
  
  public int maxSize() {
    return this.maxElements;
  }
  
  public void clear() {
    this.full = false;
    this.start = 0;
    this.end = 0;
    Arrays.fill((Object[])this.elements, (Object)null);
  }
  
  public boolean add(@NotNull E paramE) {
    if (null == paramE)
      throw new NullPointerException("Attempted to add null object to queue"); 
    if (isAtFullCapacity())
      remove(); 
    this.elements[this.end++] = paramE;
    if (this.end >= this.maxElements)
      this.end = 0; 
    if (this.end == this.start)
      this.full = true; 
    return true;
  }
  
  @NotNull
  public E get(int paramInt) {
    int i = size();
    if (paramInt < 0 || paramInt >= i)
      throw new NoSuchElementException(String.format("The specified index (%1$d) is outside the available range [0, %2$d)", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(i) })); 
    int j = (this.start + paramInt) % this.maxElements;
    return this.elements[j];
  }
  
  public boolean offer(@NotNull E paramE) {
    return add(paramE);
  }
  
  @Nullable
  public E poll() {
    return isEmpty() ? null : remove();
  }
  
  @Nullable
  public E element() {
    if (isEmpty())
      throw new NoSuchElementException("queue is empty"); 
    return peek();
  }
  
  @Nullable
  public E peek() {
    return isEmpty() ? null : this.elements[this.start];
  }
  
  @NotNull
  public E remove() {
    if (isEmpty())
      throw new NoSuchElementException("queue is empty"); 
    E e = this.elements[this.start];
    if (null != e) {
      this.elements[this.start++] = null;
      if (this.start >= this.maxElements)
        this.start = 0; 
      this.full = false;
    } 
    return e;
  }
  
  private int increment(int paramInt) {
    if (++paramInt >= this.maxElements)
      paramInt = 0; 
    return paramInt;
  }
  
  private int decrement(int paramInt) {
    if (--paramInt < 0)
      paramInt = this.maxElements - 1; 
    return paramInt;
  }
  
  @NotNull
  public Iterator<E> iterator() {
    return new Iterator<E>() {
        private int index = CircularFifoQueue.this.start;
        
        private int lastReturnedIndex = -1;
        
        private boolean isFirst = CircularFifoQueue.this.full;
        
        public boolean hasNext() {
          return (this.isFirst || this.index != CircularFifoQueue.this.end);
        }
        
        public E next() {
          if (!hasNext())
            throw new NoSuchElementException(); 
          this.isFirst = false;
          this.lastReturnedIndex = this.index;
          this.index = CircularFifoQueue.this.increment(this.index);
          return (E)CircularFifoQueue.this.elements[this.lastReturnedIndex];
        }
        
        public void remove() {
          if (this.lastReturnedIndex == -1)
            throw new IllegalStateException(); 
          if (this.lastReturnedIndex == CircularFifoQueue.this.start) {
            CircularFifoQueue.this.remove();
            this.lastReturnedIndex = -1;
            return;
          } 
          int i = this.lastReturnedIndex + 1;
          if (CircularFifoQueue.this.start < this.lastReturnedIndex && i < CircularFifoQueue.this.end) {
            System.arraycopy(CircularFifoQueue.this.elements, i, CircularFifoQueue.this.elements, this.lastReturnedIndex, CircularFifoQueue.this.end - i);
          } else {
            while (i != CircularFifoQueue.this.end) {
              if (i >= CircularFifoQueue.this.maxElements) {
                CircularFifoQueue.this.elements[i - 1] = CircularFifoQueue.this.elements[0];
                i = 0;
                continue;
              } 
              CircularFifoQueue.this.elements[CircularFifoQueue.this.decrement(i)] = CircularFifoQueue.this.elements[i];
              i = CircularFifoQueue.this.increment(i);
            } 
          } 
          this.lastReturnedIndex = -1;
          CircularFifoQueue.this.end = CircularFifoQueue.this.decrement(CircularFifoQueue.this.end);
          CircularFifoQueue.this.elements[CircularFifoQueue.this.end] = null;
          CircularFifoQueue.this.full = false;
          this.index = CircularFifoQueue.this.decrement(this.index);
        }
      };
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\CircularFifoQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */