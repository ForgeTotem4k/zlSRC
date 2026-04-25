package com.sun.jna.platform.win32;

import com.sun.jna.ptr.IntByReference;

public abstract class WinspoolUtil {
  public static Winspool.PRINTER_INFO_1[] getPrinterInfo1() {
    IntByReference intByReference1 = new IntByReference();
    IntByReference intByReference2 = new IntByReference();
    Winspool.INSTANCE.EnumPrinters(2, null, 1, null, 0, intByReference1, intByReference2);
    if (intByReference1.getValue() <= 0)
      return new Winspool.PRINTER_INFO_1[0]; 
    Winspool.PRINTER_INFO_1 pRINTER_INFO_1 = new Winspool.PRINTER_INFO_1(intByReference1.getValue());
    if (!Winspool.INSTANCE.EnumPrinters(2, null, 1, pRINTER_INFO_1.getPointer(), intByReference1.getValue(), intByReference1, intByReference2))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    pRINTER_INFO_1.read();
    return (Winspool.PRINTER_INFO_1[])pRINTER_INFO_1.toArray(intByReference2.getValue());
  }
  
  public static Winspool.PRINTER_INFO_2[] getPrinterInfo2() {
    return getPrinterInfo2(2);
  }
  
  public static Winspool.PRINTER_INFO_2[] getAllPrinterInfo2() {
    return getPrinterInfo2(6);
  }
  
  private static Winspool.PRINTER_INFO_2[] getPrinterInfo2(int paramInt) {
    IntByReference intByReference1 = new IntByReference();
    IntByReference intByReference2 = new IntByReference();
    Winspool.INSTANCE.EnumPrinters(paramInt, null, 2, null, 0, intByReference1, intByReference2);
    if (intByReference1.getValue() <= 0)
      return new Winspool.PRINTER_INFO_2[0]; 
    Winspool.PRINTER_INFO_2 pRINTER_INFO_2 = new Winspool.PRINTER_INFO_2(intByReference1.getValue());
    if (!Winspool.INSTANCE.EnumPrinters(paramInt, null, 2, pRINTER_INFO_2.getPointer(), intByReference1.getValue(), intByReference1, intByReference2))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    pRINTER_INFO_2.read();
    return (Winspool.PRINTER_INFO_2[])pRINTER_INFO_2.toArray(intByReference2.getValue());
  }
  
  public static Winspool.PRINTER_INFO_2 getPrinterInfo2(String paramString) {
    Win32Exception win32Exception;
    IntByReference intByReference1 = new IntByReference();
    IntByReference intByReference2 = new IntByReference();
    WinNT.HANDLEByReference hANDLEByReference = new WinNT.HANDLEByReference();
    if (!Winspool.INSTANCE.OpenPrinter(paramString, hANDLEByReference, null))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    Throwable throwable = null;
    Winspool.PRINTER_INFO_2 pRINTER_INFO_2 = null;
    try {
      Winspool.INSTANCE.GetPrinter(hANDLEByReference.getValue(), 2, null, 0, intByReference1);
      if (intByReference1.getValue() <= 0)
        return new Winspool.PRINTER_INFO_2(); 
      pRINTER_INFO_2 = new Winspool.PRINTER_INFO_2(intByReference1.getValue());
      if (!Winspool.INSTANCE.GetPrinter(hANDLEByReference.getValue(), 2, pRINTER_INFO_2.getPointer(), intByReference1.getValue(), intByReference2))
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
      pRINTER_INFO_2.read();
    } catch (Win32Exception win32Exception1) {
      win32Exception = win32Exception1;
    } finally {
      if (!Winspool.INSTANCE.ClosePrinter(hANDLEByReference.getValue())) {
        Win32Exception win32Exception1 = new Win32Exception(Kernel32.INSTANCE.GetLastError());
        if (win32Exception != null)
          win32Exception1.addSuppressedReflected((Throwable)win32Exception); 
      } 
    } 
    if (win32Exception != null)
      throw win32Exception; 
    return pRINTER_INFO_2;
  }
  
  public static Winspool.PRINTER_INFO_4[] getPrinterInfo4() {
    IntByReference intByReference1 = new IntByReference();
    IntByReference intByReference2 = new IntByReference();
    Winspool.INSTANCE.EnumPrinters(2, null, 4, null, 0, intByReference1, intByReference2);
    if (intByReference1.getValue() <= 0)
      return new Winspool.PRINTER_INFO_4[0]; 
    Winspool.PRINTER_INFO_4 pRINTER_INFO_4 = new Winspool.PRINTER_INFO_4(intByReference1.getValue());
    if (!Winspool.INSTANCE.EnumPrinters(2, null, 4, pRINTER_INFO_4.getPointer(), intByReference1.getValue(), intByReference1, intByReference2))
      throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    pRINTER_INFO_4.read();
    return (Winspool.PRINTER_INFO_4[])pRINTER_INFO_4.toArray(intByReference2.getValue());
  }
  
  public static Winspool.JOB_INFO_1[] getJobInfo1(WinNT.HANDLEByReference paramHANDLEByReference) {
    IntByReference intByReference1 = new IntByReference();
    IntByReference intByReference2 = new IntByReference();
    Winspool.INSTANCE.EnumJobs(paramHANDLEByReference.getValue(), 0, 255, 1, null, 0, intByReference1, intByReference2);
    if (intByReference1.getValue() <= 0)
      return new Winspool.JOB_INFO_1[0]; 
    int i = 0;
    while (true) {
      Winspool.JOB_INFO_1 jOB_INFO_1 = new Winspool.JOB_INFO_1(intByReference1.getValue());
      if (!Winspool.INSTANCE.EnumJobs(paramHANDLEByReference.getValue(), 0, 255, 1, jOB_INFO_1.getPointer(), intByReference1.getValue(), intByReference1, intByReference2))
        i = Kernel32.INSTANCE.GetLastError(); 
      if (i != 122) {
        if (i != 0)
          throw new Win32Exception(i); 
        if (intByReference2.getValue() <= 0)
          return new Winspool.JOB_INFO_1[0]; 
        jOB_INFO_1.read();
        return (Winspool.JOB_INFO_1[])jOB_INFO_1.toArray(intByReference2.getValue());
      } 
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\WinspoolUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */