package com.google.gson.internal;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public final class LinkedTreeMap<K, V> extends AbstractMap<K, V> implements Serializable {
  private static final Comparator<Comparable> NATURAL_ORDER = new Comparator<Comparable>() {
      public int compare(Comparable<Comparable> param1Comparable1, Comparable param1Comparable2) {
        return param1Comparable1.compareTo(param1Comparable2);
      }
      
      static {
      
      }
    };
  
  private final Comparator<? super K> comparator;
  
  private final boolean allowNullValues;
  
  Node<K, V> root;
  
  int size = 0;
  
  int modCount = 0;
  
  final Node<K, V> header;
  
  private EntrySet entrySet;
  
  private KeySet keySet;
  
  public LinkedTreeMap() {
    this((Comparator)NATURAL_ORDER, true);
  }
  
  public LinkedTreeMap(boolean paramBoolean) {
    this((Comparator)NATURAL_ORDER, paramBoolean);
  }
  
  public LinkedTreeMap(Comparator<? super K> paramComparator, boolean paramBoolean) {
    this.comparator = (paramComparator != null) ? paramComparator : (Comparator)NATURAL_ORDER;
    this.allowNullValues = paramBoolean;
    this.header = new Node<>(paramBoolean);
  }
  
  public int size() {
    return this.size;
  }
  
  public V get(Object paramObject) {
    Node<K, V> node = findByObject(paramObject);
    return (node != null) ? node.value : null;
  }
  
  public boolean containsKey(Object paramObject) {
    return (findByObject(paramObject) != null);
  }
  
  @CanIgnoreReturnValue
  public V put(K paramK, V paramV) {
    if (paramK == null)
      throw new NullPointerException("key == null"); 
    if (paramV == null && !this.allowNullValues)
      throw new NullPointerException("value == null"); 
    Node<K, V> node = find(paramK, true);
    V v = node.value;
    node.value = paramV;
    return v;
  }
  
  public void clear() {
    this.root = null;
    this.size = 0;
    this.modCount++;
    Node<K, V> node = this.header;
    node.next = node.prev = node;
  }
  
  public V remove(Object paramObject) {
    Node<K, V> node = removeInternalByKey(paramObject);
    return (node != null) ? node.value : null;
  }
  
  Node<K, V> find(K paramK, boolean paramBoolean) {
    Node<K, V> node3;
    Comparator<? super K> comparator = this.comparator;
    Node<K, V> node1 = this.root;
    int i = 0;
    if (node1 != null) {
      Comparable<K> comparable = (comparator == NATURAL_ORDER) ? (Comparable)paramK : null;
      while (true) {
        i = (comparable != null) ? comparable.compareTo(node1.key) : comparator.compare(paramK, node1.key);
        if (i == 0)
          return node1; 
        node3 = (i < 0) ? node1.left : node1.right;
        if (node3 == null)
          break; 
        node1 = node3;
      } 
    } 
    if (!paramBoolean)
      return null; 
    Node<K, V> node2 = this.header;
    if (node1 == null) {
      if (comparator == NATURAL_ORDER && !(paramK instanceof Comparable))
        throw new ClassCastException(paramK.getClass().getName() + " is not Comparable"); 
      node3 = new Node<>(this.allowNullValues, node1, paramK, node2, node2.prev);
      this.root = node3;
    } else {
      node3 = new Node<>(this.allowNullValues, node1, paramK, node2, node2.prev);
      if (i < 0) {
        node1.left = node3;
      } else {
        node1.right = node3;
      } 
      rebalance(node1, true);
    } 
    this.size++;
    this.modCount++;
    return node3;
  }
  
  Node<K, V> findByObject(Object paramObject) {
    try {
      return (paramObject != null) ? find((K)paramObject, false) : null;
    } catch (ClassCastException classCastException) {
      return null;
    } 
  }
  
  Node<K, V> findByEntry(Map.Entry<?, ?> paramEntry) {
    Node<K, V> node = findByObject(paramEntry.getKey());
    boolean bool = (node != null && equal(node.value, paramEntry.getValue())) ? true : false;
    return bool ? node : null;
  }
  
  private static boolean equal(Object paramObject1, Object paramObject2) {
    return Objects.equals(paramObject1, paramObject2);
  }
  
  void removeInternal(Node<K, V> paramNode, boolean paramBoolean) {
    if (paramBoolean) {
      paramNode.prev.next = paramNode.next;
      paramNode.next.prev = paramNode.prev;
    } 
    Node<K, V> node1 = paramNode.left;
    Node<K, V> node2 = paramNode.right;
    Node<K, V> node3 = paramNode.parent;
    if (node1 != null && node2 != null) {
      Node<K, V> node = (node1.height > node2.height) ? node1.last() : node2.first();
      removeInternal(node, false);
      int i = 0;
      node1 = paramNode.left;
      if (node1 != null) {
        i = node1.height;
        node.left = node1;
        node1.parent = node;
        paramNode.left = null;
      } 
      int j = 0;
      node2 = paramNode.right;
      if (node2 != null) {
        j = node2.height;
        node.right = node2;
        node2.parent = node;
        paramNode.right = null;
      } 
      node.height = Math.max(i, j) + 1;
      replaceInParent(paramNode, node);
      return;
    } 
    if (node1 != null) {
      replaceInParent(paramNode, node1);
      paramNode.left = null;
    } else if (node2 != null) {
      replaceInParent(paramNode, node2);
      paramNode.right = null;
    } else {
      replaceInParent(paramNode, null);
    } 
    rebalance(node3, false);
    this.size--;
    this.modCount++;
  }
  
  Node<K, V> removeInternalByKey(Object paramObject) {
    Node<K, V> node = findByObject(paramObject);
    if (node != null)
      removeInternal(node, true); 
    return node;
  }
  
  private void replaceInParent(Node<K, V> paramNode1, Node<K, V> paramNode2) {
    Node<K, V> node = paramNode1.parent;
    paramNode1.parent = null;
    if (paramNode2 != null)
      paramNode2.parent = node; 
    if (node != null) {
      if (node.left == paramNode1) {
        node.left = paramNode2;
      } else {
        assert node.right == paramNode1;
        node.right = paramNode2;
      } 
    } else {
      this.root = paramNode2;
    } 
  }
  
  private void rebalance(Node<K, V> paramNode, boolean paramBoolean) {
    for (Node<K, V> node = paramNode; node != null; node = node.parent) {
      Node<K, V> node1 = node.left;
      Node<K, V> node2 = node.right;
      byte b1 = (node1 != null) ? node1.height : 0;
      byte b2 = (node2 != null) ? node2.height : 0;
      int i = b1 - b2;
      if (i == -2) {
        Node<K, V> node3 = node2.left;
        Node<K, V> node4 = node2.right;
        byte b3 = (node4 != null) ? node4.height : 0;
        byte b4 = (node3 != null) ? node3.height : 0;
        int j = b4 - b3;
        if (j == -1 || (j == 0 && !paramBoolean)) {
          rotateLeft(node);
        } else {
          assert j == 1;
          rotateRight(node2);
          rotateLeft(node);
        } 
        if (paramBoolean)
          break; 
      } else if (i == 2) {
        Node<K, V> node3 = node1.left;
        Node<K, V> node4 = node1.right;
        byte b3 = (node4 != null) ? node4.height : 0;
        byte b4 = (node3 != null) ? node3.height : 0;
        int j = b4 - b3;
        if (j == 1 || (j == 0 && !paramBoolean)) {
          rotateRight(node);
        } else {
          assert j == -1;
          rotateLeft(node1);
          rotateRight(node);
        } 
        if (paramBoolean)
          break; 
      } else if (i == 0) {
        node.height = b1 + 1;
        if (paramBoolean)
          break; 
      } else {
        assert i == -1 || i == 1;
        node.height = Math.max(b1, b2) + 1;
        if (!paramBoolean)
          break; 
      } 
    } 
  }
  
  private void rotateLeft(Node<K, V> paramNode) {
    Node<K, V> node1 = paramNode.left;
    Node<K, V> node2 = paramNode.right;
    Node<K, V> node3 = node2.left;
    Node<K, V> node4 = node2.right;
    paramNode.right = node3;
    if (node3 != null)
      node3.parent = paramNode; 
    replaceInParent(paramNode, node2);
    node2.left = paramNode;
    paramNode.parent = node2;
    paramNode.height = Math.max((node1 != null) ? node1.height : 0, (node3 != null) ? node3.height : 0) + 1;
    node2.height = Math.max(paramNode.height, (node4 != null) ? node4.height : 0) + 1;
  }
  
  private void rotateRight(Node<K, V> paramNode) {
    Node<K, V> node1 = paramNode.left;
    Node<K, V> node2 = paramNode.right;
    Node<K, V> node3 = node1.left;
    Node<K, V> node4 = node1.right;
    paramNode.left = node4;
    if (node4 != null)
      node4.parent = paramNode; 
    replaceInParent(paramNode, node1);
    node1.right = paramNode;
    paramNode.parent = node1;
    paramNode.height = Math.max((node2 != null) ? node2.height : 0, (node4 != null) ? node4.height : 0) + 1;
    node1.height = Math.max(paramNode.height, (node3 != null) ? node3.height : 0) + 1;
  }
  
  public Set<Map.Entry<K, V>> entrySet() {
    EntrySet entrySet = this.entrySet;
    return (entrySet != null) ? entrySet : (this.entrySet = new EntrySet());
  }
  
  public Set<K> keySet() {
    KeySet keySet = this.keySet;
    return (keySet != null) ? keySet : (this.keySet = new KeySet());
  }
  
  private Object writeReplace() throws ObjectStreamException {
    return new LinkedHashMap<>(this);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException {
    throw new InvalidObjectException("Deserialization is unsupported");
  }
  
  final class KeySet extends AbstractSet<K> {
    public int size() {
      return LinkedTreeMap.this.size;
    }
    
    public Iterator<K> iterator() {
      return new LinkedTreeMap<K, V>.LinkedTreeMapIterator<K>() {
          public K next() {
            return (nextNode()).key;
          }
        };
    }
    
    public boolean contains(Object param1Object) {
      return LinkedTreeMap.this.containsKey(param1Object);
    }
    
    public boolean remove(Object param1Object) {
      return (LinkedTreeMap.this.removeInternalByKey(param1Object) != null);
    }
    
    public void clear() {
      LinkedTreeMap.this.clear();
    }
  }
  
  class EntrySet extends AbstractSet<Map.Entry<K, V>> {
    public int size() {
      return LinkedTreeMap.this.size;
    }
    
    public Iterator<Map.Entry<K, V>> iterator() {
      return new LinkedTreeMap<K, V>.LinkedTreeMapIterator<Map.Entry<K, V>>() {
          public Map.Entry<K, V> next() {
            return nextNode();
          }
        };
    }
    
    public boolean contains(Object param1Object) {
      return (param1Object instanceof Map.Entry && LinkedTreeMap.this.findByEntry((Map.Entry<?, ?>)param1Object) != null);
    }
    
    public boolean remove(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      LinkedTreeMap.Node node = LinkedTreeMap.this.findByEntry((Map.Entry<?, ?>)param1Object);
      if (node == null)
        return false; 
      LinkedTreeMap.this.removeInternal(node, true);
      return true;
    }
    
    public void clear() {
      LinkedTreeMap.this.clear();
    }
  }
  
  private abstract class LinkedTreeMapIterator<T> implements Iterator<T> {
    LinkedTreeMap.Node<K, V> next = LinkedTreeMap.this.header.next;
    
    LinkedTreeMap.Node<K, V> lastReturned = null;
    
    int expectedModCount = LinkedTreeMap.this.modCount;
    
    public final boolean hasNext() {
      return (this.next != LinkedTreeMap.this.header);
    }
    
    final LinkedTreeMap.Node<K, V> nextNode() {
      LinkedTreeMap.Node<K, V> node = this.next;
      if (node == LinkedTreeMap.this.header)
        throw new NoSuchElementException(); 
      if (LinkedTreeMap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      this.next = node.next;
      return this.lastReturned = node;
    }
    
    public final void remove() {
      if (this.lastReturned == null)
        throw new IllegalStateException(); 
      LinkedTreeMap.this.removeInternal(this.lastReturned, true);
      this.lastReturned = null;
      this.expectedModCount = LinkedTreeMap.this.modCount;
    }
  }
  
  static final class Node<K, V> implements Map.Entry<K, V> {
    Node<K, V> parent;
    
    Node<K, V> left;
    
    Node<K, V> right;
    
    Node<K, V> next;
    
    Node<K, V> prev;
    
    final K key;
    
    final boolean allowNullValue;
    
    V value;
    
    int height;
    
    Node(boolean param1Boolean) {
      this.key = null;
      this.allowNullValue = param1Boolean;
      this.next = this.prev = this;
    }
    
    Node(boolean param1Boolean, Node<K, V> param1Node1, K param1K, Node<K, V> param1Node2, Node<K, V> param1Node3) {
      this.parent = param1Node1;
      this.key = param1K;
      this.allowNullValue = param1Boolean;
      this.height = 1;
      this.next = param1Node2;
      this.prev = param1Node3;
      param1Node3.next = this;
      param1Node2.prev = this;
    }
    
    public K getKey() {
      return this.key;
    }
    
    public V getValue() {
      return this.value;
    }
    
    public V setValue(V param1V) {
      if (param1V == null && !this.allowNullValue)
        throw new NullPointerException("value == null"); 
      V v = this.value;
      this.value = param1V;
      return v;
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object instanceof Map.Entry) {
        Map.Entry entry = (Map.Entry)param1Object;
        return (((this.key == null) ? (entry.getKey() == null) : this.key.equals(entry.getKey())) && ((this.value == null) ? (entry.getValue() == null) : this.value.equals(entry.getValue())));
      } 
      return false;
    }
    
    public int hashCode() {
      return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
    }
    
    public String toString() {
      return (new StringBuilder()).append(this.key).append("=").append(this.value).toString();
    }
    
    public Node<K, V> first() {
      Node<K, V> node1 = this;
      for (Node<K, V> node2 = node1.left; node2 != null; node2 = node1.left)
        node1 = node2; 
      return node1;
    }
    
    public Node<K, V> last() {
      Node<K, V> node1 = this;
      for (Node<K, V> node2 = node1.right; node2 != null; node2 = node1.right)
        node1 = node2; 
      return node1;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\LinkedTreeMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */