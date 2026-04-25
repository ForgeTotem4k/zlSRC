package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;

public abstract class Secur32Util {
  public static String getUserNameEx(int paramInt) {
    char[] arrayOfChar = new char[128];
    IntByReference intByReference = new IntByReference(arrayOfChar.length);
    boolean bool = Secur32.INSTANCE.GetUserNameEx(paramInt, arrayOfChar, intByReference);
    if (!bool) {
      int i = Kernel32.INSTANCE.GetLastError();
      switch (i) {
        case 234:
          arrayOfChar = new char[intByReference.getValue() + 1];
          break;
        default:
          throw new Win32Exception(Native.getLastError());
      } 
      bool = Secur32.INSTANCE.GetUserNameEx(paramInt, arrayOfChar, intByReference);
    } 
    if (!bool)
      throw new Win32Exception(Native.getLastError()); 
    return Native.toString(arrayOfChar);
  }
  
  public static SecurityPackage[] getSecurityPackages() {
    IntByReference intByReference = new IntByReference();
    Sspi.PSecPkgInfo pSecPkgInfo = new Sspi.PSecPkgInfo();
    int i = Secur32.INSTANCE.EnumerateSecurityPackages(intByReference, pSecPkgInfo);
    if (0 != i)
      throw new Win32Exception(i); 
    Sspi.SecPkgInfo.ByReference[] arrayOfByReference = pSecPkgInfo.toArray(intByReference.getValue());
    ArrayList<SecurityPackage> arrayList = new ArrayList(intByReference.getValue());
    for (Sspi.SecPkgInfo.ByReference byReference : arrayOfByReference) {
      SecurityPackage securityPackage = new SecurityPackage();
      securityPackage.name = byReference.Name.toString();
      securityPackage.comment = byReference.Comment.toString();
      arrayList.add(securityPackage);
    } 
    i = Secur32.INSTANCE.FreeContextBuffer(pSecPkgInfo.pPkgInfo.getPointer());
    if (0 != i)
      throw new Win32Exception(i); 
    return arrayList.<SecurityPackage>toArray(new SecurityPackage[0]);
  }
  
  public static class SecurityPackage {
    public String name;
    
    public String comment;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\Secur32Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */