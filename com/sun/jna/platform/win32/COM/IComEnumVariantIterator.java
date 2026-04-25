package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.COM.util.IDispatch;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.ptr.PointerByReference;
import java.io.Closeable;
import java.util.Iterator;

public class IComEnumVariantIterator implements Iterable<Variant.VARIANT>, Iterator<Variant.VARIANT>, Closeable {
  private Variant.VARIANT nextValue;
  
  private EnumVariant backingIteration;
  
  public static IComEnumVariantIterator wrap(IDispatch paramIDispatch) {
    PointerByReference pointerByReference = new PointerByReference();
    IUnknown iUnknown = (IUnknown)paramIDispatch.getProperty(IUnknown.class, OaIdl.DISPID_NEWENUM, new Object[0]);
    iUnknown.QueryInterface(EnumVariant.REFIID, pointerByReference);
    iUnknown.Release();
    EnumVariant enumVariant = new EnumVariant(pointerByReference.getValue());
    return new IComEnumVariantIterator(enumVariant);
  }
  
  public IComEnumVariantIterator(EnumVariant paramEnumVariant) {
    this.backingIteration = paramEnumVariant;
    retrieveNext();
  }
  
  public boolean hasNext() {
    return (this.nextValue != null);
  }
  
  public Variant.VARIANT next() {
    Variant.VARIANT vARIANT = this.nextValue;
    retrieveNext();
    return vARIANT;
  }
  
  private void retrieveNext() {
    if (this.backingIteration == null)
      return; 
    Variant.VARIANT[] arrayOfVARIANT = this.backingIteration.Next(1);
    if (arrayOfVARIANT.length == 0) {
      close();
    } else {
      this.nextValue = arrayOfVARIANT[0];
    } 
  }
  
  public void close() {
    if (this.backingIteration != null) {
      this.nextValue = null;
      this.backingIteration.Release();
      this.backingIteration = null;
    } 
  }
  
  protected void finalize() throws Throwable {
    close();
    super.finalize();
  }
  
  public Iterator<Variant.VARIANT> iterator() {
    return this;
  }
  
  public void remove() {
    throw new UnsupportedOperationException("remove");
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\IComEnumVariantIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */