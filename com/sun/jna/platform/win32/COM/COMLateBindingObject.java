package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import java.util.Date;

public class COMLateBindingObject extends COMBindingBaseObject {
  public COMLateBindingObject(IDispatch paramIDispatch) {
    super(paramIDispatch);
  }
  
  public COMLateBindingObject(Guid.CLSID paramCLSID, boolean paramBoolean) {
    super(paramCLSID, paramBoolean);
  }
  
  public COMLateBindingObject(String paramString, boolean paramBoolean) throws COMException {
    super(paramString, paramBoolean);
  }
  
  protected IDispatch getAutomationProperty(String paramString) {
    Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
    oleMethod(2, byReference, paramString);
    return (IDispatch)byReference.getValue();
  }
  
  protected IDispatch getAutomationProperty(String paramString, Variant.VARIANT paramVARIANT) {
    Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
    oleMethod(2, byReference, paramString, paramVARIANT);
    return (IDispatch)byReference.getValue();
  }
  
  @Deprecated
  protected IDispatch getAutomationProperty(String paramString, COMLateBindingObject paramCOMLateBindingObject) {
    Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
    oleMethod(2, byReference, paramString);
    return (IDispatch)byReference.getValue();
  }
  
  @Deprecated
  protected IDispatch getAutomationProperty(String paramString, COMLateBindingObject paramCOMLateBindingObject, Variant.VARIANT paramVARIANT) {
    Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
    oleMethod(2, byReference, paramString, paramVARIANT);
    return (IDispatch)byReference.getValue();
  }
  
  @Deprecated
  protected IDispatch getAutomationProperty(String paramString, IDispatch paramIDispatch) {
    Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
    oleMethod(2, byReference, paramString);
    return (IDispatch)byReference.getValue();
  }
  
  protected boolean getBooleanProperty(String paramString) {
    Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
    oleMethod(2, byReference, paramString);
    return byReference.booleanValue();
  }
  
  protected Date getDateProperty(String paramString) {
    Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
    oleMethod(2, byReference, paramString);
    return byReference.dateValue();
  }
  
  protected int getIntProperty(String paramString) {
    Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
    oleMethod(2, byReference, paramString);
    return byReference.intValue();
  }
  
  protected short getShortProperty(String paramString) {
    Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
    oleMethod(2, byReference, paramString);
    return byReference.shortValue();
  }
  
  protected String getStringProperty(String paramString) {
    Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
    oleMethod(2, byReference, paramString);
    String str = byReference.stringValue();
    OleAuto.INSTANCE.VariantClear((Variant.VARIANT)byReference);
    return str;
  }
  
  protected Variant.VARIANT invoke(String paramString) {
    Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
    oleMethod(1, byReference, paramString);
    return (Variant.VARIANT)byReference;
  }
  
  protected Variant.VARIANT invoke(String paramString, Variant.VARIANT paramVARIANT) {
    Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
    oleMethod(1, byReference, paramString, paramVARIANT);
    return (Variant.VARIANT)byReference;
  }
  
  protected Variant.VARIANT invoke(String paramString, Variant.VARIANT[] paramArrayOfVARIANT) {
    Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
    oleMethod(1, byReference, paramString, paramArrayOfVARIANT);
    return (Variant.VARIANT)byReference;
  }
  
  protected Variant.VARIANT invoke(String paramString, Variant.VARIANT paramVARIANT1, Variant.VARIANT paramVARIANT2) {
    return invoke(paramString, new Variant.VARIANT[] { paramVARIANT1, paramVARIANT2 });
  }
  
  protected Variant.VARIANT invoke(String paramString, Variant.VARIANT paramVARIANT1, Variant.VARIANT paramVARIANT2, Variant.VARIANT paramVARIANT3) {
    return invoke(paramString, new Variant.VARIANT[] { paramVARIANT1, paramVARIANT2, paramVARIANT3 });
  }
  
  protected Variant.VARIANT invoke(String paramString, Variant.VARIANT paramVARIANT1, Variant.VARIANT paramVARIANT2, Variant.VARIANT paramVARIANT3, Variant.VARIANT paramVARIANT4) {
    return invoke(paramString, new Variant.VARIANT[] { paramVARIANT1, paramVARIANT2, paramVARIANT3, paramVARIANT4 });
  }
  
  @Deprecated
  protected void invokeNoReply(String paramString, IDispatch paramIDispatch) {
    oleMethod(1, (Variant.VARIANT.ByReference)null, paramIDispatch, paramString);
  }
  
  @Deprecated
  protected void invokeNoReply(String paramString, COMLateBindingObject paramCOMLateBindingObject) {
    oleMethod(1, (Variant.VARIANT.ByReference)null, paramCOMLateBindingObject.getIDispatch(), paramString);
  }
  
  protected void invokeNoReply(String paramString, Variant.VARIANT paramVARIANT) {
    oleMethod(1, (Variant.VARIANT.ByReference)null, paramString, paramVARIANT);
  }
  
  @Deprecated
  protected void invokeNoReply(String paramString, IDispatch paramIDispatch, Variant.VARIANT paramVARIANT) {
    oleMethod(1, (Variant.VARIANT.ByReference)null, paramIDispatch, paramString, paramVARIANT);
  }
  
  @Deprecated
  protected void invokeNoReply(String paramString, IDispatch paramIDispatch, Variant.VARIANT paramVARIANT1, Variant.VARIANT paramVARIANT2) {
    oleMethod(1, (Variant.VARIANT.ByReference)null, paramIDispatch, paramString, new Variant.VARIANT[] { paramVARIANT1, paramVARIANT2 });
  }
  
  @Deprecated
  protected void invokeNoReply(String paramString, COMLateBindingObject paramCOMLateBindingObject, Variant.VARIANT paramVARIANT1, Variant.VARIANT paramVARIANT2) {
    oleMethod(1, (Variant.VARIANT.ByReference)null, paramCOMLateBindingObject.getIDispatch(), paramString, new Variant.VARIANT[] { paramVARIANT1, paramVARIANT2 });
  }
  
  @Deprecated
  protected void invokeNoReply(String paramString, COMLateBindingObject paramCOMLateBindingObject, Variant.VARIANT paramVARIANT) {
    oleMethod(1, (Variant.VARIANT.ByReference)null, paramCOMLateBindingObject.getIDispatch(), paramString, paramVARIANT);
  }
  
  @Deprecated
  protected void invokeNoReply(String paramString, IDispatch paramIDispatch, Variant.VARIANT[] paramArrayOfVARIANT) {
    oleMethod(1, (Variant.VARIANT.ByReference)null, paramIDispatch, paramString, paramArrayOfVARIANT);
  }
  
  protected void invokeNoReply(String paramString) {
    Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
    oleMethod(1, byReference, paramString);
  }
  
  protected void invokeNoReply(String paramString, Variant.VARIANT[] paramArrayOfVARIANT) {
    Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
    oleMethod(1, byReference, paramString, paramArrayOfVARIANT);
  }
  
  protected void invokeNoReply(String paramString, Variant.VARIANT paramVARIANT1, Variant.VARIANT paramVARIANT2) {
    invokeNoReply(paramString, new Variant.VARIANT[] { paramVARIANT1, paramVARIANT2 });
  }
  
  protected void invokeNoReply(String paramString, Variant.VARIANT paramVARIANT1, Variant.VARIANT paramVARIANT2, Variant.VARIANT paramVARIANT3) {
    invokeNoReply(paramString, new Variant.VARIANT[] { paramVARIANT1, paramVARIANT2, paramVARIANT3 });
  }
  
  protected void invokeNoReply(String paramString, Variant.VARIANT paramVARIANT1, Variant.VARIANT paramVARIANT2, Variant.VARIANT paramVARIANT3, Variant.VARIANT paramVARIANT4) {
    invokeNoReply(paramString, new Variant.VARIANT[] { paramVARIANT1, paramVARIANT2, paramVARIANT3, paramVARIANT4 });
  }
  
  protected void setProperty(String paramString, boolean paramBoolean) {
    oleMethod(4, (Variant.VARIANT.ByReference)null, paramString, new Variant.VARIANT(paramBoolean));
  }
  
  protected void setProperty(String paramString, Date paramDate) {
    oleMethod(4, (Variant.VARIANT.ByReference)null, paramString, new Variant.VARIANT(paramDate));
  }
  
  protected void setProperty(String paramString, Dispatch paramDispatch) {
    oleMethod(4, (Variant.VARIANT.ByReference)null, paramString, new Variant.VARIANT(paramDispatch));
  }
  
  @Deprecated
  protected void setProperty(String paramString, IDispatch paramIDispatch) {
    oleMethod(4, (Variant.VARIANT.ByReference)null, paramString, new Variant.VARIANT(paramIDispatch));
  }
  
  protected void setProperty(String paramString, int paramInt) {
    oleMethod(4, (Variant.VARIANT.ByReference)null, paramString, new Variant.VARIANT(paramInt));
  }
  
  protected void setProperty(String paramString, short paramShort) {
    oleMethod(4, (Variant.VARIANT.ByReference)null, paramString, new Variant.VARIANT(paramShort));
  }
  
  protected void setProperty(String paramString1, String paramString2) {
    Variant.VARIANT vARIANT = new Variant.VARIANT(paramString2);
    try {
      oleMethod(4, (Variant.VARIANT.ByReference)null, paramString1, vARIANT);
    } finally {
      OleAuto.INSTANCE.VariantClear(vARIANT);
    } 
  }
  
  protected void setProperty(String paramString, Variant.VARIANT paramVARIANT) {
    oleMethod(4, (Variant.VARIANT.ByReference)null, paramString, paramVARIANT);
  }
  
  @Deprecated
  protected void setProperty(String paramString, IDispatch paramIDispatch, Variant.VARIANT paramVARIANT) {
    oleMethod(4, (Variant.VARIANT.ByReference)null, paramIDispatch, paramString, paramVARIANT);
  }
  
  @Deprecated
  protected void setProperty(String paramString, COMLateBindingObject paramCOMLateBindingObject, Variant.VARIANT paramVARIANT) {
    oleMethod(4, (Variant.VARIANT.ByReference)null, paramCOMLateBindingObject.getIDispatch(), paramString, paramVARIANT);
  }
  
  public Variant.VARIANT toVariant() {
    return new Variant.VARIANT(getIDispatch());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\COM\COMLateBindingObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */