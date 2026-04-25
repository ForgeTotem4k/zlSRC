package com.sun.jna.platform.win32;

import com.sun.jna.ptr.IntByReference;
import java.util.HashMap;
import java.util.Map;

public abstract class Rasapi32Util {
  private static final int RASP_PppIp = 32801;
  
  private static Object phoneBookMutex = new Object();
  
  public static final Map CONNECTION_STATE_TEXT = new HashMap<>();
  
  public static String getRasErrorString(int paramInt) {
    char[] arrayOfChar = new char[1024];
    int i = Rasapi32.INSTANCE.RasGetErrorString(paramInt, arrayOfChar, arrayOfChar.length);
    if (i != 0)
      return "Unknown error " + paramInt; 
    byte b;
    for (b = 0; b < arrayOfChar.length && arrayOfChar[b] != '\000'; b++);
    return new String(arrayOfChar, 0, b);
  }
  
  public static String getRasConnectionStatusText(int paramInt) {
    return !CONNECTION_STATE_TEXT.containsKey(Integer.valueOf(paramInt)) ? Integer.toString(paramInt) : (String)CONNECTION_STATE_TEXT.get(Integer.valueOf(paramInt));
  }
  
  public static WinNT.HANDLE getRasConnection(String paramString) throws Ras32Exception {
    IntByReference intByReference1 = new IntByReference(0);
    IntByReference intByReference2 = new IntByReference();
    int i = Rasapi32.INSTANCE.RasEnumConnections(null, intByReference1, intByReference2);
    if (i != 0 && i != 603)
      throw new Ras32Exception(i); 
    if (intByReference1.getValue() == 0)
      return null; 
    WinRas.RASCONN[] arrayOfRASCONN = new WinRas.RASCONN[intByReference2.getValue()];
    byte b;
    for (b = 0; b < intByReference2.getValue(); b++)
      arrayOfRASCONN[b] = new WinRas.RASCONN(); 
    intByReference1 = new IntByReference((arrayOfRASCONN[0]).dwSize * intByReference2.getValue());
    i = Rasapi32.INSTANCE.RasEnumConnections(arrayOfRASCONN, intByReference1, intByReference2);
    if (i != 0)
      throw new Ras32Exception(i); 
    for (b = 0; b < intByReference2.getValue(); b++) {
      if ((new String((arrayOfRASCONN[b]).szEntryName)).equals(paramString))
        return (arrayOfRASCONN[b]).hrasconn; 
    } 
    return null;
  }
  
  public static void hangupRasConnection(String paramString) throws Ras32Exception {
    WinNT.HANDLE hANDLE = getRasConnection(paramString);
    if (hANDLE == null)
      return; 
    int i = Rasapi32.INSTANCE.RasHangUp(hANDLE);
    if (i != 0)
      throw new Ras32Exception(i); 
  }
  
  public static void hangupRasConnection(WinNT.HANDLE paramHANDLE) throws Ras32Exception {
    if (paramHANDLE == null)
      return; 
    int i = Rasapi32.INSTANCE.RasHangUp(paramHANDLE);
    if (i != 0)
      throw new Ras32Exception(i); 
  }
  
  public static WinRas.RASPPPIP getIPProjection(WinNT.HANDLE paramHANDLE) throws Ras32Exception {
    WinRas.RASPPPIP rASPPPIP = new WinRas.RASPPPIP();
    IntByReference intByReference = new IntByReference(rASPPPIP.size());
    rASPPPIP.write();
    int i = Rasapi32.INSTANCE.RasGetProjectionInfo(paramHANDLE, 32801, rASPPPIP.getPointer(), intByReference);
    if (i != 0)
      throw new Ras32Exception(i); 
    rASPPPIP.read();
    return rASPPPIP;
  }
  
  public static WinRas.RASENTRY.ByReference getPhoneBookEntry(String paramString) throws Ras32Exception {
    synchronized (phoneBookMutex) {
      WinRas.RASENTRY.ByReference byReference = new WinRas.RASENTRY.ByReference();
      IntByReference intByReference = new IntByReference(byReference.size());
      int i = Rasapi32.INSTANCE.RasGetEntryProperties(null, paramString, byReference, intByReference, null, null);
      if (i != 0)
        throw new Ras32Exception(i); 
      return byReference;
    } 
  }
  
  public static void setPhoneBookEntry(String paramString, WinRas.RASENTRY.ByReference paramByReference) throws Ras32Exception {
    synchronized (phoneBookMutex) {
      int i = Rasapi32.INSTANCE.RasSetEntryProperties(null, paramString, paramByReference, paramByReference.size(), null, 0);
      if (i != 0)
        throw new Ras32Exception(i); 
    } 
  }
  
  public static WinRas.RASDIALPARAMS getPhoneBookDialingParams(String paramString) throws Ras32Exception {
    synchronized (phoneBookMutex) {
      WinRas.RASDIALPARAMS.ByReference byReference = new WinRas.RASDIALPARAMS.ByReference();
      System.arraycopy(byReference.szEntryName, 0, paramString.toCharArray(), 0, paramString.length());
      WinDef.BOOLByReference bOOLByReference = new WinDef.BOOLByReference();
      int i = Rasapi32.INSTANCE.RasGetEntryDialParams(null, byReference, bOOLByReference);
      if (i != 0)
        throw new Ras32Exception(i); 
      return byReference;
    } 
  }
  
  public static WinNT.HANDLE dialEntry(String paramString) throws Ras32Exception {
    WinRas.RASCREDENTIALS.ByReference byReference = new WinRas.RASCREDENTIALS.ByReference();
    synchronized (phoneBookMutex) {
      byReference.dwMask = 7;
      int j = Rasapi32.INSTANCE.RasGetCredentials(null, paramString, byReference);
      if (j != 0)
        throw new Ras32Exception(j); 
    } 
    WinRas.RASDIALPARAMS.ByReference byReference1 = new WinRas.RASDIALPARAMS.ByReference();
    System.arraycopy(paramString.toCharArray(), 0, byReference1.szEntryName, 0, paramString.length());
    System.arraycopy(byReference.szUserName, 0, byReference1.szUserName, 0, byReference.szUserName.length);
    System.arraycopy(byReference.szPassword, 0, byReference1.szPassword, 0, byReference.szPassword.length);
    System.arraycopy(byReference.szDomain, 0, byReference1.szDomain, 0, byReference.szDomain.length);
    WinNT.HANDLEByReference hANDLEByReference = new WinNT.HANDLEByReference();
    int i = Rasapi32.INSTANCE.RasDial(null, null, byReference1, 0, null, hANDLEByReference);
    if (i != 0) {
      if (hANDLEByReference.getValue() != null)
        Rasapi32.INSTANCE.RasHangUp(hANDLEByReference.getValue()); 
      throw new Ras32Exception(i);
    } 
    return hANDLEByReference.getValue();
  }
  
  public static WinNT.HANDLE dialEntry(String paramString, WinRas.RasDialFunc2 paramRasDialFunc2) throws Ras32Exception {
    WinRas.RASCREDENTIALS.ByReference byReference = new WinRas.RASCREDENTIALS.ByReference();
    synchronized (phoneBookMutex) {
      byReference.dwMask = 7;
      int j = Rasapi32.INSTANCE.RasGetCredentials(null, paramString, byReference);
      if (j != 0)
        throw new Ras32Exception(j); 
    } 
    WinRas.RASDIALPARAMS.ByReference byReference1 = new WinRas.RASDIALPARAMS.ByReference();
    System.arraycopy(paramString.toCharArray(), 0, byReference1.szEntryName, 0, paramString.length());
    System.arraycopy(byReference.szUserName, 0, byReference1.szUserName, 0, byReference.szUserName.length);
    System.arraycopy(byReference.szPassword, 0, byReference1.szPassword, 0, byReference.szPassword.length);
    System.arraycopy(byReference.szDomain, 0, byReference1.szDomain, 0, byReference.szDomain.length);
    WinNT.HANDLEByReference hANDLEByReference = new WinNT.HANDLEByReference();
    int i = Rasapi32.INSTANCE.RasDial(null, null, byReference1, 2, paramRasDialFunc2, hANDLEByReference);
    if (i != 0) {
      if (hANDLEByReference.getValue() != null)
        Rasapi32.INSTANCE.RasHangUp(hANDLEByReference.getValue()); 
      throw new Ras32Exception(i);
    } 
    return hANDLEByReference.getValue();
  }
  
  static {
    CONNECTION_STATE_TEXT.put(Integer.valueOf(0), "Opening the port...");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(1), "Port has been opened successfully");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(2), "Connecting to the device...");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(3), "The device has connected successfully.");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(4), "All devices in the device chain have successfully connected.");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(5), "Verifying the user name and password...");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(6), "An authentication event has occurred.");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(7), "Requested another validation attempt with a new user.");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(8), "Server has requested a callback number.");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(9), "The client has requested to change the password");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(10), "Registering your computer on the network...");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(11), "The link-speed calculation phase is starting...");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(12), "An authentication request is being acknowledged.");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(13), "Reauthentication (after callback) is starting.");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(14), "The client has successfully completed authentication.");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(15), "The line is about to disconnect for callback.");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(16), "Delaying to give the modem time to reset for callback.");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(17), "Waiting for an incoming call from server.");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(18), "Projection result information is available.");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(19), "User authentication is being initiated or retried.");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(20), "Client has been called back and is about to resume authentication.");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(21), "Logging on to the network...");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(22), "Subentry has been connected");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(23), "Subentry has been disconnected");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(4096), "Terminal state supported by RASPHONE.EXE.");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(4097), "Retry authentication state supported by RASPHONE.EXE.");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(4098), "Callback state supported by RASPHONE.EXE.");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(4099), "Change password state supported by RASPHONE.EXE.");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(4100), "Displaying authentication UI");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(8192), "Connected to remote server successfully");
    CONNECTION_STATE_TEXT.put(Integer.valueOf(8193), "Disconnected");
  }
  
  public static class Ras32Exception extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private final int code;
    
    public int getCode() {
      return this.code;
    }
    
    public Ras32Exception(int param1Int) {
      super(Rasapi32Util.getRasErrorString(param1Int));
      this.code = param1Int;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\Rasapi32Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */