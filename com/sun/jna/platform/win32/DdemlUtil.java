package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.win32.W32APIOptions;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DdemlUtil {
  public static interface IDdeConnectionList extends Closeable {
    Ddeml.HCONVLIST getHandle();
    
    DdemlUtil.IDdeConnection queryNextServer(DdemlUtil.IDdeConnection param1IDdeConnection);
    
    void close();
  }
  
  public static interface IDdeClient extends Closeable {
    Integer getInstanceIdentitifier();
    
    void initialize(int param1Int) throws DdemlUtil.DdemlException;
    
    Ddeml.HSZ createStringHandle(String param1String) throws DdemlUtil.DdemlException;
    
    String queryString(Ddeml.HSZ param1HSZ) throws DdemlUtil.DdemlException;
    
    boolean freeStringHandle(Ddeml.HSZ param1HSZ);
    
    boolean keepStringHandle(Ddeml.HSZ param1HSZ);
    
    void nameService(Ddeml.HSZ param1HSZ, int param1Int) throws DdemlUtil.DdemlException;
    
    void nameService(String param1String, int param1Int) throws DdemlUtil.DdemlException;
    
    int getLastError();
    
    DdemlUtil.IDdeConnection connect(Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, Ddeml.CONVCONTEXT param1CONVCONTEXT);
    
    DdemlUtil.IDdeConnection connect(String param1String1, String param1String2, Ddeml.CONVCONTEXT param1CONVCONTEXT);
    
    Ddeml.HDDEDATA createDataHandle(Pointer param1Pointer, int param1Int1, int param1Int2, Ddeml.HSZ param1HSZ, int param1Int3, int param1Int4);
    
    void freeDataHandle(Ddeml.HDDEDATA param1HDDEDATA);
    
    Ddeml.HDDEDATA addData(Ddeml.HDDEDATA param1HDDEDATA, Pointer param1Pointer, int param1Int1, int param1Int2);
    
    int getData(Ddeml.HDDEDATA param1HDDEDATA, Pointer param1Pointer, int param1Int1, int param1Int2);
    
    Pointer accessData(Ddeml.HDDEDATA param1HDDEDATA, WinDef.DWORDByReference param1DWORDByReference);
    
    void unaccessData(Ddeml.HDDEDATA param1HDDEDATA);
    
    void postAdvise(Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2);
    
    void postAdvise(String param1String1, String param1String2);
    
    void abandonTransactions();
    
    DdemlUtil.IDdeConnectionList connectList(Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, DdemlUtil.IDdeConnectionList param1IDdeConnectionList, Ddeml.CONVCONTEXT param1CONVCONTEXT);
    
    DdemlUtil.IDdeConnectionList connectList(String param1String1, String param1String2, DdemlUtil.IDdeConnectionList param1IDdeConnectionList, Ddeml.CONVCONTEXT param1CONVCONTEXT);
    
    boolean enableCallback(int param1Int);
    
    boolean uninitialize();
    
    DdemlUtil.IDdeConnection wrap(Ddeml.HCONV param1HCONV);
    
    void registerAdvstartHandler(DdemlUtil.AdvstartHandler param1AdvstartHandler);
    
    void unregisterAdvstartHandler(DdemlUtil.AdvstartHandler param1AdvstartHandler);
    
    void registerAdvstopHandler(DdemlUtil.AdvstopHandler param1AdvstopHandler);
    
    void unregisterAdvstopHandler(DdemlUtil.AdvstopHandler param1AdvstopHandler);
    
    void registerConnectHandler(DdemlUtil.ConnectHandler param1ConnectHandler);
    
    void unregisterConnectHandler(DdemlUtil.ConnectHandler param1ConnectHandler);
    
    void registerAdvReqHandler(DdemlUtil.AdvreqHandler param1AdvreqHandler);
    
    void unregisterAdvReqHandler(DdemlUtil.AdvreqHandler param1AdvreqHandler);
    
    void registerRequestHandler(DdemlUtil.RequestHandler param1RequestHandler);
    
    void unregisterRequestHandler(DdemlUtil.RequestHandler param1RequestHandler);
    
    void registerWildconnectHandler(DdemlUtil.WildconnectHandler param1WildconnectHandler);
    
    void unregisterWildconnectHandler(DdemlUtil.WildconnectHandler param1WildconnectHandler);
    
    void registerAdvdataHandler(DdemlUtil.AdvdataHandler param1AdvdataHandler);
    
    void unregisterAdvdataHandler(DdemlUtil.AdvdataHandler param1AdvdataHandler);
    
    void registerExecuteHandler(DdemlUtil.ExecuteHandler param1ExecuteHandler);
    
    void unregisterExecuteHandler(DdemlUtil.ExecuteHandler param1ExecuteHandler);
    
    void registerPokeHandler(DdemlUtil.PokeHandler param1PokeHandler);
    
    void unregisterPokeHandler(DdemlUtil.PokeHandler param1PokeHandler);
    
    void registerConnectConfirmHandler(DdemlUtil.ConnectConfirmHandler param1ConnectConfirmHandler);
    
    void unregisterConnectConfirmHandler(DdemlUtil.ConnectConfirmHandler param1ConnectConfirmHandler);
    
    void registerDisconnectHandler(DdemlUtil.DisconnectHandler param1DisconnectHandler);
    
    void unregisterDisconnectHandler(DdemlUtil.DisconnectHandler param1DisconnectHandler);
    
    void registerErrorHandler(DdemlUtil.ErrorHandler param1ErrorHandler);
    
    void unregisterErrorHandler(DdemlUtil.ErrorHandler param1ErrorHandler);
    
    void registerRegisterHandler(DdemlUtil.RegisterHandler param1RegisterHandler);
    
    void unregisterRegisterHandler(DdemlUtil.RegisterHandler param1RegisterHandler);
    
    void registerXactCompleteHandler(DdemlUtil.XactCompleteHandler param1XactCompleteHandler);
    
    void unregisterXactCompleteHandler(DdemlUtil.XactCompleteHandler param1XactCompleteHandler);
    
    void registerUnregisterHandler(DdemlUtil.UnregisterHandler param1UnregisterHandler);
    
    void unregisterUnregisterHandler(DdemlUtil.UnregisterHandler param1UnregisterHandler);
    
    void registerMonitorHandler(DdemlUtil.MonitorHandler param1MonitorHandler);
    
    void unregisterMonitorHandler(DdemlUtil.MonitorHandler param1MonitorHandler);
  }
  
  public static interface IDdeConnection extends Closeable {
    Ddeml.HCONV getConv();
    
    void execute(String param1String, int param1Int, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
    
    void poke(Pointer param1Pointer, int param1Int1, Ddeml.HSZ param1HSZ, int param1Int2, int param1Int3, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
    
    void poke(Pointer param1Pointer, int param1Int1, String param1String, int param1Int2, int param1Int3, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
    
    Ddeml.HDDEDATA request(Ddeml.HSZ param1HSZ, int param1Int1, int param1Int2, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
    
    Ddeml.HDDEDATA request(String param1String, int param1Int1, int param1Int2, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
    
    Ddeml.HDDEDATA clientTransaction(Pointer param1Pointer, int param1Int1, Ddeml.HSZ param1HSZ, int param1Int2, int param1Int3, int param1Int4, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
    
    Ddeml.HDDEDATA clientTransaction(Pointer param1Pointer, int param1Int1, String param1String, int param1Int2, int param1Int3, int param1Int4, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
    
    void advstart(Ddeml.HSZ param1HSZ, int param1Int1, int param1Int2, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
    
    void advstart(String param1String, int param1Int1, int param1Int2, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
    
    void advstop(Ddeml.HSZ param1HSZ, int param1Int1, int param1Int2, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
    
    void advstop(String param1String, int param1Int1, int param1Int2, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR);
    
    void abandonTransaction(int param1Int);
    
    void abandonTransactions();
    
    void impersonateClient();
    
    void close();
    
    void reconnect();
    
    boolean enableCallback(int param1Int);
    
    void setUserHandle(int param1Int, BaseTSD.DWORD_PTR param1DWORD_PTR) throws DdemlUtil.DdemlException;
    
    Ddeml.CONVINFO queryConvInfo(int param1Int) throws DdemlUtil.DdemlException;
  }
  
  public static class DdemlException extends RuntimeException {
    private static final Map<Integer, String> ERROR_CODE_MAP;
    
    private final int errorCode;
    
    public static DdemlException create(int param1Int) {
      String str = ERROR_CODE_MAP.get(Integer.valueOf(param1Int));
      return new DdemlException(param1Int, String.format("%s (Code: 0x%X)", new Object[] { (str != null) ? str : "", Integer.valueOf(param1Int) }));
    }
    
    public DdemlException(int param1Int, String param1String) {
      super(param1String);
      this.errorCode = param1Int;
    }
    
    public int getErrorCode() {
      return this.errorCode;
    }
    
    static {
      HashMap<Object, Object> hashMap = new HashMap<>();
      for (Field field : Ddeml.class.getFields()) {
        String str = field.getName();
        if (str.startsWith("DMLERR_") && !str.equals("DMLERR_FIRST") && !str.equals("DMLERR_LAST"))
          try {
            hashMap.put(Integer.valueOf(field.getInt(null)), str);
          } catch (IllegalArgumentException|IllegalAccessException illegalArgumentException) {
            throw new RuntimeException(illegalArgumentException);
          }  
      } 
      ERROR_CODE_MAP = Collections.unmodifiableMap(hashMap);
    }
  }
  
  public static class DdeAdapter implements Ddeml.DdeCallback {
    private static final Logger LOG = Logger.getLogger(DdeAdapter.class.getName());
    
    private int idInst;
    
    private final List<DdemlUtil.AdvstartHandler> advstartHandler = new CopyOnWriteArrayList<>();
    
    private final List<DdemlUtil.AdvstopHandler> advstopHandler = new CopyOnWriteArrayList<>();
    
    private final List<DdemlUtil.ConnectHandler> connectHandler = new CopyOnWriteArrayList<>();
    
    private final List<DdemlUtil.AdvreqHandler> advReqHandler = new CopyOnWriteArrayList<>();
    
    private final List<DdemlUtil.RequestHandler> requestHandler = new CopyOnWriteArrayList<>();
    
    private final List<DdemlUtil.WildconnectHandler> wildconnectHandler = new CopyOnWriteArrayList<>();
    
    private final List<DdemlUtil.AdvdataHandler> advdataHandler = new CopyOnWriteArrayList<>();
    
    private final List<DdemlUtil.ExecuteHandler> executeHandler = new CopyOnWriteArrayList<>();
    
    private final List<DdemlUtil.PokeHandler> pokeHandler = new CopyOnWriteArrayList<>();
    
    private final List<DdemlUtil.ConnectConfirmHandler> connectConfirmHandler = new CopyOnWriteArrayList<>();
    
    private final List<DdemlUtil.DisconnectHandler> disconnectHandler = new CopyOnWriteArrayList<>();
    
    private final List<DdemlUtil.ErrorHandler> errorHandler = new CopyOnWriteArrayList<>();
    
    private final List<DdemlUtil.RegisterHandler> registerHandler = new CopyOnWriteArrayList<>();
    
    private final List<DdemlUtil.XactCompleteHandler> xactCompleteHandler = new CopyOnWriteArrayList<>();
    
    private final List<DdemlUtil.UnregisterHandler> unregisterHandler = new CopyOnWriteArrayList<>();
    
    private final List<DdemlUtil.MonitorHandler> monitorHandler = new CopyOnWriteArrayList<>();
    
    public void setInstanceIdentifier(int param1Int) {
      this.idInst = param1Int;
    }
    
    public WinDef.PVOID ddeCallback(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, Ddeml.HDDEDATA param1HDDEDATA, BaseTSD.ULONG_PTR param1ULONG_PTR1, BaseTSD.ULONG_PTR param1ULONG_PTR2) {
      Object object = null;
      try {
        boolean bool;
        Ddeml.HDDEDATA hDDEDATA;
        Ddeml.CONVCONTEXT cONVCONTEXT;
        int i;
        int j;
        Ddeml.HSZPAIR[] arrayOfHSZPAIR;
        int k;
        switch (param1Int1) {
          case 4144:
            bool = onAdvstart(param1Int1, param1Int2, param1HCONV, param1HSZ1, param1HSZ2);
            return new WinDef.PVOID(Pointer.createConstant((new WinDef.BOOL(bool)).intValue()));
          case 4194:
            cONVCONTEXT = null;
            if (param1ULONG_PTR1.toPointer() != null)
              cONVCONTEXT = new Ddeml.CONVCONTEXT(new Pointer(param1ULONG_PTR1.longValue())); 
            bool = onConnect(param1Int1, param1HSZ1, param1HSZ2, cONVCONTEXT, (param1ULONG_PTR2 != null && param1ULONG_PTR2.intValue() != 0));
            return new WinDef.PVOID(Pointer.createConstant((new WinDef.BOOL(bool)).intValue()));
          case 8226:
            j = param1ULONG_PTR1.intValue() & 0xFFFF;
            hDDEDATA = onAdvreq(param1Int1, param1Int2, param1HCONV, param1HSZ1, param1HSZ2, j);
            return (hDDEDATA == null) ? new WinDef.PVOID() : new WinDef.PVOID(hDDEDATA.getPointer());
          case 8368:
            hDDEDATA = onRequest(param1Int1, param1Int2, param1HCONV, param1HSZ1, param1HSZ2);
            return (hDDEDATA == null) ? new WinDef.PVOID() : new WinDef.PVOID(hDDEDATA.getPointer());
          case 8418:
            cONVCONTEXT = null;
            if (param1ULONG_PTR1.toPointer() != null)
              cONVCONTEXT = new Ddeml.CONVCONTEXT(new Pointer(param1ULONG_PTR1.longValue())); 
            arrayOfHSZPAIR = onWildconnect(param1Int1, param1HSZ1, param1HSZ2, cONVCONTEXT, (param1ULONG_PTR2 != null && param1ULONG_PTR2.intValue() != 0));
            if (arrayOfHSZPAIR == null || arrayOfHSZPAIR.length == 0)
              return new WinDef.PVOID(); 
            k = 0;
            for (Ddeml.HSZPAIR hSZPAIR : arrayOfHSZPAIR) {
              hSZPAIR.write();
              k += hSZPAIR.size();
            } 
            hDDEDATA = Ddeml.INSTANCE.DdeCreateDataHandle(this.idInst, arrayOfHSZPAIR[0].getPointer(), k, 0, null, param1Int2, 0);
            return new WinDef.PVOID(hDDEDATA.getPointer());
          case 16400:
            i = onAdvdata(param1Int1, param1Int2, param1HCONV, param1HSZ1, param1HSZ2, param1HDDEDATA);
            return new WinDef.PVOID(Pointer.createConstant(i));
          case 16464:
            i = onExecute(param1Int1, param1HCONV, param1HSZ1, param1HDDEDATA);
            Ddeml.INSTANCE.DdeFreeDataHandle(param1HDDEDATA);
            return new WinDef.PVOID(Pointer.createConstant(i));
          case 16528:
            i = onPoke(param1Int1, param1Int2, param1HCONV, param1HSZ1, param1HSZ2, param1HDDEDATA);
            return new WinDef.PVOID(Pointer.createConstant(i));
          case 32832:
            onAdvstop(param1Int1, param1Int2, param1HCONV, param1HSZ1, param1HSZ2);
            return new WinDef.PVOID();
          case 32882:
            onConnectConfirm(param1Int1, param1HCONV, param1HSZ1, param1HSZ2, (param1ULONG_PTR2 != null && param1ULONG_PTR2.intValue() != 0));
            return new WinDef.PVOID();
          case 32962:
            onDisconnect(param1Int1, param1HCONV, (param1ULONG_PTR2 != null && param1ULONG_PTR2.intValue() != 0));
            return new WinDef.PVOID();
          case 32770:
            onError(param1Int1, param1HCONV, (int)(param1ULONG_PTR2.longValue() & 0xFFFFL));
            return new WinDef.PVOID();
          case 32930:
            onRegister(param1Int1, param1HSZ1, param1HSZ2);
            return new WinDef.PVOID();
          case 32896:
            onXactComplete(param1Int1, param1Int2, param1HCONV, param1HSZ1, param1HSZ2, param1HDDEDATA, param1ULONG_PTR1, param1ULONG_PTR2);
            return new WinDef.PVOID();
          case 32978:
            onUnregister(param1Int1, param1HSZ1, param1HSZ2);
            return new WinDef.PVOID();
          case 33010:
            onMonitor(param1Int1, param1HDDEDATA, param1ULONG_PTR2.intValue());
            return new WinDef.PVOID();
        } 
        LOG.log(Level.FINE, String.format("Not implemented Operation - Transaction type: 0x%X (%s)", new Object[] { Integer.valueOf(param1Int1), object }));
      } catch (BlockException blockException) {
        return new WinDef.PVOID(Pointer.createConstant(-1));
      } catch (Throwable throwable) {
        LOG.log(Level.WARNING, "Exception in DDECallback", throwable);
      } 
      return new WinDef.PVOID();
    }
    
    public void registerAdvstartHandler(DdemlUtil.AdvstartHandler param1AdvstartHandler) {
      this.advstartHandler.add(param1AdvstartHandler);
    }
    
    public void unregisterAdvstartHandler(DdemlUtil.AdvstartHandler param1AdvstartHandler) {
      this.advstartHandler.remove(param1AdvstartHandler);
    }
    
    private boolean onAdvstart(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2) {
      boolean bool = false;
      for (DdemlUtil.AdvstartHandler advstartHandler : this.advstartHandler) {
        if (advstartHandler.onAdvstart(param1Int1, param1Int2, param1HCONV, param1HSZ1, param1HSZ2))
          bool = true; 
      } 
      return bool;
    }
    
    public void registerAdvstopHandler(DdemlUtil.AdvstopHandler param1AdvstopHandler) {
      this.advstopHandler.add(param1AdvstopHandler);
    }
    
    public void unregisterAdvstopHandler(DdemlUtil.AdvstopHandler param1AdvstopHandler) {
      this.advstopHandler.remove(param1AdvstopHandler);
    }
    
    private void onAdvstop(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2) {
      for (DdemlUtil.AdvstopHandler advstopHandler : this.advstopHandler)
        advstopHandler.onAdvstop(param1Int1, param1Int2, param1HCONV, param1HSZ1, param1HSZ2); 
    }
    
    public void registerConnectHandler(DdemlUtil.ConnectHandler param1ConnectHandler) {
      this.connectHandler.add(param1ConnectHandler);
    }
    
    public void unregisterConnectHandler(DdemlUtil.ConnectHandler param1ConnectHandler) {
      this.connectHandler.remove(param1ConnectHandler);
    }
    
    private boolean onConnect(int param1Int, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, Ddeml.CONVCONTEXT param1CONVCONTEXT, boolean param1Boolean) {
      boolean bool = false;
      for (DdemlUtil.ConnectHandler connectHandler : this.connectHandler) {
        if (connectHandler.onConnect(param1Int, param1HSZ1, param1HSZ2, param1CONVCONTEXT, param1Boolean))
          bool = true; 
      } 
      return bool;
    }
    
    public void registerAdvReqHandler(DdemlUtil.AdvreqHandler param1AdvreqHandler) {
      this.advReqHandler.add(param1AdvreqHandler);
    }
    
    public void unregisterAdvReqHandler(DdemlUtil.AdvreqHandler param1AdvreqHandler) {
      this.advReqHandler.remove(param1AdvreqHandler);
    }
    
    private Ddeml.HDDEDATA onAdvreq(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, int param1Int3) {
      for (DdemlUtil.AdvreqHandler advreqHandler : this.advReqHandler) {
        Ddeml.HDDEDATA hDDEDATA = advreqHandler.onAdvreq(param1Int1, param1Int2, param1HCONV, param1HSZ1, param1HSZ2, param1Int3);
        if (hDDEDATA != null)
          return hDDEDATA; 
      } 
      return null;
    }
    
    public void registerRequestHandler(DdemlUtil.RequestHandler param1RequestHandler) {
      this.requestHandler.add(param1RequestHandler);
    }
    
    public void unregisterRequestHandler(DdemlUtil.RequestHandler param1RequestHandler) {
      this.requestHandler.remove(param1RequestHandler);
    }
    
    private Ddeml.HDDEDATA onRequest(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2) {
      for (DdemlUtil.RequestHandler requestHandler : this.requestHandler) {
        Ddeml.HDDEDATA hDDEDATA = requestHandler.onRequest(param1Int1, param1Int2, param1HCONV, param1HSZ1, param1HSZ2);
        if (hDDEDATA != null)
          return hDDEDATA; 
      } 
      return null;
    }
    
    public void registerWildconnectHandler(DdemlUtil.WildconnectHandler param1WildconnectHandler) {
      this.wildconnectHandler.add(param1WildconnectHandler);
    }
    
    public void unregisterWildconnectHandler(DdemlUtil.WildconnectHandler param1WildconnectHandler) {
      this.wildconnectHandler.remove(param1WildconnectHandler);
    }
    
    private Ddeml.HSZPAIR[] onWildconnect(int param1Int, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, Ddeml.CONVCONTEXT param1CONVCONTEXT, boolean param1Boolean) {
      ArrayList<Ddeml.HSZPAIR> arrayList = new ArrayList(1);
      for (DdemlUtil.WildconnectHandler wildconnectHandler : this.wildconnectHandler)
        arrayList.addAll(wildconnectHandler.onWildconnect(param1Int, param1HSZ1, param1HSZ2, param1CONVCONTEXT, param1Boolean)); 
      return arrayList.<Ddeml.HSZPAIR>toArray(new Ddeml.HSZPAIR[0]);
    }
    
    public void registerAdvdataHandler(DdemlUtil.AdvdataHandler param1AdvdataHandler) {
      this.advdataHandler.add(param1AdvdataHandler);
    }
    
    public void unregisterAdvdataHandler(DdemlUtil.AdvdataHandler param1AdvdataHandler) {
      this.advdataHandler.remove(param1AdvdataHandler);
    }
    
    private int onAdvdata(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, Ddeml.HDDEDATA param1HDDEDATA) {
      for (DdemlUtil.AdvdataHandler advdataHandler : this.advdataHandler) {
        int i = advdataHandler.onAdvdata(param1Int1, param1Int2, param1HCONV, param1HSZ1, param1HSZ2, param1HDDEDATA);
        if (i != 0)
          return i; 
      } 
      return 0;
    }
    
    public void registerExecuteHandler(DdemlUtil.ExecuteHandler param1ExecuteHandler) {
      this.executeHandler.add(param1ExecuteHandler);
    }
    
    public void unregisterExecuteHandler(DdemlUtil.ExecuteHandler param1ExecuteHandler) {
      this.executeHandler.remove(param1ExecuteHandler);
    }
    
    private int onExecute(int param1Int, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ, Ddeml.HDDEDATA param1HDDEDATA) {
      for (DdemlUtil.ExecuteHandler executeHandler : this.executeHandler) {
        int i = executeHandler.onExecute(param1Int, param1HCONV, param1HSZ, param1HDDEDATA);
        if (i != 0)
          return i; 
      } 
      return 0;
    }
    
    public void registerPokeHandler(DdemlUtil.PokeHandler param1PokeHandler) {
      this.pokeHandler.add(param1PokeHandler);
    }
    
    public void unregisterPokeHandler(DdemlUtil.PokeHandler param1PokeHandler) {
      this.pokeHandler.remove(param1PokeHandler);
    }
    
    private int onPoke(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, Ddeml.HDDEDATA param1HDDEDATA) {
      for (DdemlUtil.PokeHandler pokeHandler : this.pokeHandler) {
        int i = pokeHandler.onPoke(param1Int1, param1Int2, param1HCONV, param1HSZ1, param1HSZ2, param1HDDEDATA);
        if (i != 0)
          return i; 
      } 
      return 0;
    }
    
    public void registerConnectConfirmHandler(DdemlUtil.ConnectConfirmHandler param1ConnectConfirmHandler) {
      this.connectConfirmHandler.add(param1ConnectConfirmHandler);
    }
    
    public void unregisterConnectConfirmHandler(DdemlUtil.ConnectConfirmHandler param1ConnectConfirmHandler) {
      this.connectConfirmHandler.remove(param1ConnectConfirmHandler);
    }
    
    private void onConnectConfirm(int param1Int, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, boolean param1Boolean) {
      for (DdemlUtil.ConnectConfirmHandler connectConfirmHandler : this.connectConfirmHandler)
        connectConfirmHandler.onConnectConfirm(param1Int, param1HCONV, param1HSZ1, param1HSZ2, param1Boolean); 
    }
    
    public void registerDisconnectHandler(DdemlUtil.DisconnectHandler param1DisconnectHandler) {
      this.disconnectHandler.add(param1DisconnectHandler);
    }
    
    public void unregisterDisconnectHandler(DdemlUtil.DisconnectHandler param1DisconnectHandler) {
      this.disconnectHandler.remove(param1DisconnectHandler);
    }
    
    private void onDisconnect(int param1Int, Ddeml.HCONV param1HCONV, boolean param1Boolean) {
      for (DdemlUtil.DisconnectHandler disconnectHandler : this.disconnectHandler)
        disconnectHandler.onDisconnect(param1Int, param1HCONV, param1Boolean); 
    }
    
    public void registerErrorHandler(DdemlUtil.ErrorHandler param1ErrorHandler) {
      this.errorHandler.add(param1ErrorHandler);
    }
    
    public void unregisterErrorHandler(DdemlUtil.ErrorHandler param1ErrorHandler) {
      this.errorHandler.remove(param1ErrorHandler);
    }
    
    private void onError(int param1Int1, Ddeml.HCONV param1HCONV, int param1Int2) {
      for (DdemlUtil.ErrorHandler errorHandler : this.errorHandler)
        errorHandler.onError(param1Int1, param1HCONV, param1Int2); 
    }
    
    public void registerRegisterHandler(DdemlUtil.RegisterHandler param1RegisterHandler) {
      this.registerHandler.add(param1RegisterHandler);
    }
    
    public void unregisterRegisterHandler(DdemlUtil.RegisterHandler param1RegisterHandler) {
      this.registerHandler.remove(param1RegisterHandler);
    }
    
    private void onRegister(int param1Int, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2) {
      for (DdemlUtil.RegisterHandler registerHandler : this.registerHandler)
        registerHandler.onRegister(param1Int, param1HSZ1, param1HSZ2); 
    }
    
    public void registerXactCompleteHandler(DdemlUtil.XactCompleteHandler param1XactCompleteHandler) {
      this.xactCompleteHandler.add(param1XactCompleteHandler);
    }
    
    public void xactCompleteXactCompleteHandler(DdemlUtil.XactCompleteHandler param1XactCompleteHandler) {
      this.xactCompleteHandler.remove(param1XactCompleteHandler);
    }
    
    private void onXactComplete(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, Ddeml.HDDEDATA param1HDDEDATA, BaseTSD.ULONG_PTR param1ULONG_PTR1, BaseTSD.ULONG_PTR param1ULONG_PTR2) {
      for (DdemlUtil.XactCompleteHandler xactCompleteHandler : this.xactCompleteHandler)
        xactCompleteHandler.onXactComplete(param1Int1, param1Int2, param1HCONV, param1HSZ1, param1HSZ2, param1HDDEDATA, param1ULONG_PTR1, param1ULONG_PTR2); 
    }
    
    public void registerUnregisterHandler(DdemlUtil.UnregisterHandler param1UnregisterHandler) {
      this.unregisterHandler.add(param1UnregisterHandler);
    }
    
    public void unregisterUnregisterHandler(DdemlUtil.UnregisterHandler param1UnregisterHandler) {
      this.unregisterHandler.remove(param1UnregisterHandler);
    }
    
    private void onUnregister(int param1Int, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2) {
      for (DdemlUtil.UnregisterHandler unregisterHandler : this.unregisterHandler)
        unregisterHandler.onUnregister(param1Int, param1HSZ1, param1HSZ2); 
    }
    
    public void registerMonitorHandler(DdemlUtil.MonitorHandler param1MonitorHandler) {
      this.monitorHandler.add(param1MonitorHandler);
    }
    
    public void unregisterMonitorHandler(DdemlUtil.MonitorHandler param1MonitorHandler) {
      this.monitorHandler.remove(param1MonitorHandler);
    }
    
    private void onMonitor(int param1Int1, Ddeml.HDDEDATA param1HDDEDATA, int param1Int2) {
      for (DdemlUtil.MonitorHandler monitorHandler : this.monitorHandler)
        monitorHandler.onMonitor(param1Int1, param1HDDEDATA, param1Int2); 
    }
    
    public static class BlockException extends RuntimeException {}
  }
  
  public static interface MonitorHandler {
    void onMonitor(int param1Int1, Ddeml.HDDEDATA param1HDDEDATA, int param1Int2);
  }
  
  public static interface PokeHandler {
    int onPoke(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, Ddeml.HDDEDATA param1HDDEDATA);
  }
  
  public static interface ExecuteHandler {
    int onExecute(int param1Int, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ, Ddeml.HDDEDATA param1HDDEDATA);
  }
  
  public static interface UnregisterHandler {
    void onUnregister(int param1Int, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2);
  }
  
  public static interface XactCompleteHandler {
    void onXactComplete(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, Ddeml.HDDEDATA param1HDDEDATA, BaseTSD.ULONG_PTR param1ULONG_PTR1, BaseTSD.ULONG_PTR param1ULONG_PTR2);
  }
  
  public static interface RegisterHandler {
    void onRegister(int param1Int, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2);
  }
  
  public static interface ErrorHandler {
    void onError(int param1Int1, Ddeml.HCONV param1HCONV, int param1Int2);
  }
  
  public static interface DisconnectHandler {
    void onDisconnect(int param1Int, Ddeml.HCONV param1HCONV, boolean param1Boolean);
  }
  
  public static interface ConnectConfirmHandler {
    void onConnectConfirm(int param1Int, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, boolean param1Boolean);
  }
  
  public static interface AdvdataHandler {
    int onAdvdata(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, Ddeml.HDDEDATA param1HDDEDATA);
  }
  
  public static interface WildconnectHandler {
    List<Ddeml.HSZPAIR> onWildconnect(int param1Int, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, Ddeml.CONVCONTEXT param1CONVCONTEXT, boolean param1Boolean);
  }
  
  public static interface RequestHandler {
    Ddeml.HDDEDATA onRequest(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2);
  }
  
  public static interface AdvreqHandler {
    Ddeml.HDDEDATA onAdvreq(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, int param1Int3);
  }
  
  public static interface ConnectHandler {
    boolean onConnect(int param1Int, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, Ddeml.CONVCONTEXT param1CONVCONTEXT, boolean param1Boolean);
  }
  
  public static interface AdvstopHandler {
    void onAdvstop(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2);
  }
  
  public static interface AdvstartHandler {
    boolean onAdvstart(int param1Int1, int param1Int2, Ddeml.HCONV param1HCONV, Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2);
  }
  
  public static class DdeClient implements IDdeClient {
    private Integer idInst;
    
    private final DdemlUtil.DdeAdapter ddeAdapter = new DdemlUtil.DdeAdapter();
    
    public Integer getInstanceIdentitifier() {
      return this.idInst;
    }
    
    public void initialize(int param1Int) throws DdemlUtil.DdemlException {
      WinDef.DWORDByReference dWORDByReference = new WinDef.DWORDByReference();
      Integer integer = Integer.valueOf(Ddeml.INSTANCE.DdeInitialize(dWORDByReference, this.ddeAdapter, param1Int, 0));
      if (integer.intValue() != 0)
        throw DdemlUtil.DdemlException.create(integer.intValue()); 
      this.idInst = Integer.valueOf(dWORDByReference.getValue().intValue());
      if (this.ddeAdapter instanceof DdemlUtil.DdeAdapter)
        this.ddeAdapter.setInstanceIdentifier(this.idInst.intValue()); 
    }
    
    public Ddeml.HSZ createStringHandle(String param1String) throws DdemlUtil.DdemlException {
      char c;
      if (param1String == null)
        return null; 
      if (W32APIOptions.DEFAULT_OPTIONS == W32APIOptions.UNICODE_OPTIONS) {
        c = 'Ұ';
      } else {
        c = 'Ϭ';
      } 
      Ddeml.HSZ hSZ = Ddeml.INSTANCE.DdeCreateStringHandle(this.idInst.intValue(), param1String, c);
      if (hSZ == null)
        throw DdemlUtil.DdemlException.create(getLastError()); 
      return hSZ;
    }
    
    public void nameService(Ddeml.HSZ param1HSZ, int param1Int) throws DdemlUtil.DdemlException {
      Ddeml.HDDEDATA hDDEDATA = Ddeml.INSTANCE.DdeNameService(this.idInst.intValue(), param1HSZ, new Ddeml.HSZ(), param1Int);
      if (hDDEDATA == null)
        throw DdemlUtil.DdemlException.create(getLastError()); 
    }
    
    public void nameService(String param1String, int param1Int) throws DdemlUtil.DdemlException {
      Ddeml.HSZ hSZ = null;
      try {
        hSZ = createStringHandle(param1String);
        nameService(hSZ, param1Int);
      } finally {
        freeStringHandle(hSZ);
      } 
    }
    
    public int getLastError() {
      return Ddeml.INSTANCE.DdeGetLastError(this.idInst.intValue());
    }
    
    public DdemlUtil.IDdeConnection connect(Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, Ddeml.CONVCONTEXT param1CONVCONTEXT) {
      Ddeml.HCONV hCONV = Ddeml.INSTANCE.DdeConnect(this.idInst.intValue(), param1HSZ1, param1HSZ2, param1CONVCONTEXT);
      if (hCONV == null)
        throw DdemlUtil.DdemlException.create(getLastError()); 
      return new DdemlUtil.DdeConnection(this, hCONV);
    }
    
    public DdemlUtil.IDdeConnection connect(String param1String1, String param1String2, Ddeml.CONVCONTEXT param1CONVCONTEXT) {
      Ddeml.HSZ hSZ1 = null;
      Ddeml.HSZ hSZ2 = null;
      try {
        hSZ1 = createStringHandle(param1String1);
        hSZ2 = createStringHandle(param1String2);
        return connect(hSZ1, hSZ2, param1CONVCONTEXT);
      } finally {
        freeStringHandle(hSZ2);
        freeStringHandle(hSZ1);
      } 
    }
    
    public String queryString(Ddeml.HSZ param1HSZ) throws DdemlUtil.DdemlException {
      char c;
      byte b;
      if (W32APIOptions.DEFAULT_OPTIONS == W32APIOptions.UNICODE_OPTIONS) {
        c = 'Ұ';
        b = 2;
      } else {
        c = 'Ϭ';
        b = 1;
      } 
      Memory memory = new Memory((257 * b));
      try {
        int i = Ddeml.INSTANCE.DdeQueryString(this.idInst.intValue(), param1HSZ, (Pointer)memory, 256, c);
        if (W32APIOptions.DEFAULT_OPTIONS == W32APIOptions.UNICODE_OPTIONS)
          return memory.getWideString(0L); 
        return memory.getString(0L);
      } finally {
        memory.valid();
      } 
    }
    
    public Ddeml.HDDEDATA createDataHandle(Pointer param1Pointer, int param1Int1, int param1Int2, Ddeml.HSZ param1HSZ, int param1Int3, int param1Int4) {
      Ddeml.HDDEDATA hDDEDATA = Ddeml.INSTANCE.DdeCreateDataHandle(this.idInst.intValue(), param1Pointer, param1Int1, param1Int2, param1HSZ, param1Int3, param1Int4);
      if (hDDEDATA == null)
        throw DdemlUtil.DdemlException.create(getLastError()); 
      return hDDEDATA;
    }
    
    public void freeDataHandle(Ddeml.HDDEDATA param1HDDEDATA) {
      boolean bool = Ddeml.INSTANCE.DdeFreeDataHandle(param1HDDEDATA);
      if (!bool)
        throw DdemlUtil.DdemlException.create(getLastError()); 
    }
    
    public Ddeml.HDDEDATA addData(Ddeml.HDDEDATA param1HDDEDATA, Pointer param1Pointer, int param1Int1, int param1Int2) {
      Ddeml.HDDEDATA hDDEDATA = Ddeml.INSTANCE.DdeAddData(param1HDDEDATA, param1Pointer, param1Int1, param1Int2);
      if (hDDEDATA == null)
        throw DdemlUtil.DdemlException.create(getLastError()); 
      return hDDEDATA;
    }
    
    public int getData(Ddeml.HDDEDATA param1HDDEDATA, Pointer param1Pointer, int param1Int1, int param1Int2) {
      int i = Ddeml.INSTANCE.DdeGetData(param1HDDEDATA, param1Pointer, param1Int1, param1Int2);
      int j = getLastError();
      if (j != 0)
        throw DdemlUtil.DdemlException.create(j); 
      return i;
    }
    
    public Pointer accessData(Ddeml.HDDEDATA param1HDDEDATA, WinDef.DWORDByReference param1DWORDByReference) {
      Pointer pointer = Ddeml.INSTANCE.DdeAccessData(param1HDDEDATA, param1DWORDByReference);
      if (pointer == null)
        throw DdemlUtil.DdemlException.create(getLastError()); 
      return pointer;
    }
    
    public void unaccessData(Ddeml.HDDEDATA param1HDDEDATA) {
      boolean bool = Ddeml.INSTANCE.DdeUnaccessData(param1HDDEDATA);
      if (!bool)
        throw DdemlUtil.DdemlException.create(getLastError()); 
    }
    
    public void postAdvise(Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2) {
      boolean bool = Ddeml.INSTANCE.DdePostAdvise(this.idInst.intValue(), param1HSZ1, param1HSZ2);
      if (!bool)
        throw DdemlUtil.DdemlException.create(getLastError()); 
    }
    
    public void postAdvise(String param1String1, String param1String2) {
      Ddeml.HSZ hSZ1 = null;
      Ddeml.HSZ hSZ2 = null;
      try {
        hSZ2 = createStringHandle(param1String1);
        hSZ1 = createStringHandle(param1String2);
        postAdvise(hSZ2, hSZ1);
      } finally {
        freeStringHandle(hSZ2);
        freeStringHandle(hSZ1);
      } 
    }
    
    public boolean freeStringHandle(Ddeml.HSZ param1HSZ) {
      return (param1HSZ == null) ? true : Ddeml.INSTANCE.DdeFreeStringHandle(this.idInst.intValue(), param1HSZ);
    }
    
    public boolean keepStringHandle(Ddeml.HSZ param1HSZ) {
      return Ddeml.INSTANCE.DdeKeepStringHandle(this.idInst.intValue(), param1HSZ);
    }
    
    public void abandonTransactions() {
      boolean bool = Ddeml.INSTANCE.DdeAbandonTransaction(this.idInst.intValue(), null, 0);
      if (!bool)
        throw DdemlUtil.DdemlException.create(getLastError()); 
    }
    
    public DdemlUtil.IDdeConnectionList connectList(Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, DdemlUtil.IDdeConnectionList param1IDdeConnectionList, Ddeml.CONVCONTEXT param1CONVCONTEXT) {
      Ddeml.HCONVLIST hCONVLIST = Ddeml.INSTANCE.DdeConnectList(this.idInst.intValue(), param1HSZ1, param1HSZ2, (param1IDdeConnectionList != null) ? param1IDdeConnectionList.getHandle() : null, param1CONVCONTEXT);
      if (hCONVLIST == null)
        throw DdemlUtil.DdemlException.create(getLastError()); 
      return new DdemlUtil.DdeConnectionList(this, hCONVLIST);
    }
    
    public DdemlUtil.IDdeConnectionList connectList(String param1String1, String param1String2, DdemlUtil.IDdeConnectionList param1IDdeConnectionList, Ddeml.CONVCONTEXT param1CONVCONTEXT) {
      Ddeml.HSZ hSZ1 = null;
      Ddeml.HSZ hSZ2 = null;
      try {
        hSZ1 = createStringHandle(param1String1);
        hSZ2 = createStringHandle(param1String2);
        return connectList(hSZ1, hSZ2, param1IDdeConnectionList, param1CONVCONTEXT);
      } finally {
        freeStringHandle(hSZ2);
        freeStringHandle(hSZ1);
      } 
    }
    
    public boolean enableCallback(int param1Int) {
      boolean bool = Ddeml.INSTANCE.DdeEnableCallback(this.idInst.intValue(), null, param1Int);
      if (!bool && param1Int != 2) {
        int i = getLastError();
        if (i != 0)
          throw DdemlUtil.DdemlException.create(getLastError()); 
      } 
      return bool;
    }
    
    public boolean uninitialize() {
      return Ddeml.INSTANCE.DdeUninitialize(this.idInst.intValue());
    }
    
    public void close() {
      uninitialize();
    }
    
    public DdemlUtil.IDdeConnection wrap(Ddeml.HCONV param1HCONV) {
      return new DdemlUtil.DdeConnection(this, param1HCONV);
    }
    
    public void unregisterDisconnectHandler(DdemlUtil.DisconnectHandler param1DisconnectHandler) {
      this.ddeAdapter.unregisterDisconnectHandler(param1DisconnectHandler);
    }
    
    public void registerAdvstartHandler(DdemlUtil.AdvstartHandler param1AdvstartHandler) {
      this.ddeAdapter.registerAdvstartHandler(param1AdvstartHandler);
    }
    
    public void unregisterAdvstartHandler(DdemlUtil.AdvstartHandler param1AdvstartHandler) {
      this.ddeAdapter.unregisterAdvstartHandler(param1AdvstartHandler);
    }
    
    public void registerAdvstopHandler(DdemlUtil.AdvstopHandler param1AdvstopHandler) {
      this.ddeAdapter.registerAdvstopHandler(param1AdvstopHandler);
    }
    
    public void unregisterAdvstopHandler(DdemlUtil.AdvstopHandler param1AdvstopHandler) {
      this.ddeAdapter.unregisterAdvstopHandler(param1AdvstopHandler);
    }
    
    public void registerConnectHandler(DdemlUtil.ConnectHandler param1ConnectHandler) {
      this.ddeAdapter.registerConnectHandler(param1ConnectHandler);
    }
    
    public void unregisterConnectHandler(DdemlUtil.ConnectHandler param1ConnectHandler) {
      this.ddeAdapter.unregisterConnectHandler(param1ConnectHandler);
    }
    
    public void registerAdvReqHandler(DdemlUtil.AdvreqHandler param1AdvreqHandler) {
      this.ddeAdapter.registerAdvReqHandler(param1AdvreqHandler);
    }
    
    public void unregisterAdvReqHandler(DdemlUtil.AdvreqHandler param1AdvreqHandler) {
      this.ddeAdapter.unregisterAdvReqHandler(param1AdvreqHandler);
    }
    
    public void registerRequestHandler(DdemlUtil.RequestHandler param1RequestHandler) {
      this.ddeAdapter.registerRequestHandler(param1RequestHandler);
    }
    
    public void unregisterRequestHandler(DdemlUtil.RequestHandler param1RequestHandler) {
      this.ddeAdapter.unregisterRequestHandler(param1RequestHandler);
    }
    
    public void registerWildconnectHandler(DdemlUtil.WildconnectHandler param1WildconnectHandler) {
      this.ddeAdapter.registerWildconnectHandler(param1WildconnectHandler);
    }
    
    public void unregisterWildconnectHandler(DdemlUtil.WildconnectHandler param1WildconnectHandler) {
      this.ddeAdapter.unregisterWildconnectHandler(param1WildconnectHandler);
    }
    
    public void registerAdvdataHandler(DdemlUtil.AdvdataHandler param1AdvdataHandler) {
      this.ddeAdapter.registerAdvdataHandler(param1AdvdataHandler);
    }
    
    public void unregisterAdvdataHandler(DdemlUtil.AdvdataHandler param1AdvdataHandler) {
      this.ddeAdapter.unregisterAdvdataHandler(param1AdvdataHandler);
    }
    
    public void registerExecuteHandler(DdemlUtil.ExecuteHandler param1ExecuteHandler) {
      this.ddeAdapter.registerExecuteHandler(param1ExecuteHandler);
    }
    
    public void unregisterExecuteHandler(DdemlUtil.ExecuteHandler param1ExecuteHandler) {
      this.ddeAdapter.unregisterExecuteHandler(param1ExecuteHandler);
    }
    
    public void registerPokeHandler(DdemlUtil.PokeHandler param1PokeHandler) {
      this.ddeAdapter.registerPokeHandler(param1PokeHandler);
    }
    
    public void unregisterPokeHandler(DdemlUtil.PokeHandler param1PokeHandler) {
      this.ddeAdapter.unregisterPokeHandler(param1PokeHandler);
    }
    
    public void registerConnectConfirmHandler(DdemlUtil.ConnectConfirmHandler param1ConnectConfirmHandler) {
      this.ddeAdapter.registerConnectConfirmHandler(param1ConnectConfirmHandler);
    }
    
    public void unregisterConnectConfirmHandler(DdemlUtil.ConnectConfirmHandler param1ConnectConfirmHandler) {
      this.ddeAdapter.unregisterConnectConfirmHandler(param1ConnectConfirmHandler);
    }
    
    public void registerDisconnectHandler(DdemlUtil.DisconnectHandler param1DisconnectHandler) {
      this.ddeAdapter.registerDisconnectHandler(param1DisconnectHandler);
    }
    
    public void registerErrorHandler(DdemlUtil.ErrorHandler param1ErrorHandler) {
      this.ddeAdapter.registerErrorHandler(param1ErrorHandler);
    }
    
    public void unregisterErrorHandler(DdemlUtil.ErrorHandler param1ErrorHandler) {
      this.ddeAdapter.unregisterErrorHandler(param1ErrorHandler);
    }
    
    public void registerRegisterHandler(DdemlUtil.RegisterHandler param1RegisterHandler) {
      this.ddeAdapter.registerRegisterHandler(param1RegisterHandler);
    }
    
    public void unregisterRegisterHandler(DdemlUtil.RegisterHandler param1RegisterHandler) {
      this.ddeAdapter.unregisterRegisterHandler(param1RegisterHandler);
    }
    
    public void registerXactCompleteHandler(DdemlUtil.XactCompleteHandler param1XactCompleteHandler) {
      this.ddeAdapter.registerXactCompleteHandler(param1XactCompleteHandler);
    }
    
    public void unregisterXactCompleteHandler(DdemlUtil.XactCompleteHandler param1XactCompleteHandler) {
      this.ddeAdapter.xactCompleteXactCompleteHandler(param1XactCompleteHandler);
    }
    
    public void registerUnregisterHandler(DdemlUtil.UnregisterHandler param1UnregisterHandler) {
      this.ddeAdapter.registerUnregisterHandler(param1UnregisterHandler);
    }
    
    public void unregisterUnregisterHandler(DdemlUtil.UnregisterHandler param1UnregisterHandler) {
      this.ddeAdapter.unregisterUnregisterHandler(param1UnregisterHandler);
    }
    
    public void registerMonitorHandler(DdemlUtil.MonitorHandler param1MonitorHandler) {
      this.ddeAdapter.registerMonitorHandler(param1MonitorHandler);
    }
    
    public void unregisterMonitorHandler(DdemlUtil.MonitorHandler param1MonitorHandler) {
      this.ddeAdapter.unregisterMonitorHandler(param1MonitorHandler);
    }
  }
  
  public static class DdeConnectionList implements IDdeConnectionList {
    private final DdemlUtil.IDdeClient client;
    
    private final Ddeml.HCONVLIST convList;
    
    public DdeConnectionList(DdemlUtil.IDdeClient param1IDdeClient, Ddeml.HCONVLIST param1HCONVLIST) {
      this.convList = param1HCONVLIST;
      this.client = param1IDdeClient;
    }
    
    public Ddeml.HCONVLIST getHandle() {
      return this.convList;
    }
    
    public DdemlUtil.IDdeConnection queryNextServer(DdemlUtil.IDdeConnection param1IDdeConnection) {
      Ddeml.HCONV hCONV = Ddeml.INSTANCE.DdeQueryNextServer(this.convList, (param1IDdeConnection != null) ? param1IDdeConnection.getConv() : null);
      return (hCONV != null) ? new DdemlUtil.DdeConnection(this.client, hCONV) : null;
    }
    
    public void close() {
      boolean bool = Ddeml.INSTANCE.DdeDisconnectList(this.convList);
      if (!bool)
        throw DdemlUtil.DdemlException.create(this.client.getLastError()); 
    }
  }
  
  public static class DdeConnection implements IDdeConnection {
    private Ddeml.HCONV conv;
    
    private final DdemlUtil.IDdeClient client;
    
    public DdeConnection(DdemlUtil.IDdeClient param1IDdeClient, Ddeml.HCONV param1HCONV) {
      this.conv = param1HCONV;
      this.client = param1IDdeClient;
    }
    
    public Ddeml.HCONV getConv() {
      return this.conv;
    }
    
    public void abandonTransaction(int param1Int) {
      boolean bool = Ddeml.INSTANCE.DdeAbandonTransaction(this.client.getInstanceIdentitifier().intValue(), this.conv, param1Int);
      if (!bool)
        throw DdemlUtil.DdemlException.create(this.client.getLastError()); 
    }
    
    public void abandonTransactions() {
      boolean bool = Ddeml.INSTANCE.DdeAbandonTransaction(this.client.getInstanceIdentitifier().intValue(), this.conv, 0);
      if (!bool)
        throw DdemlUtil.DdemlException.create(this.client.getLastError()); 
    }
    
    public Ddeml.HDDEDATA clientTransaction(Pointer param1Pointer, int param1Int1, Ddeml.HSZ param1HSZ, int param1Int2, int param1Int3, int param1Int4, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR) {
      if (param1Int4 == -1 && param1DWORDByReference == null)
        param1DWORDByReference = new WinDef.DWORDByReference(); 
      Ddeml.HDDEDATA hDDEDATA = Ddeml.INSTANCE.DdeClientTransaction(param1Pointer, param1Int1, this.conv, param1HSZ, param1Int2, param1Int3, param1Int4, param1DWORDByReference);
      if (hDDEDATA == null)
        throw DdemlUtil.DdemlException.create(this.client.getLastError()); 
      if (param1DWORD_PTR != null)
        if (param1Int4 != -1) {
          setUserHandle(-1, param1DWORD_PTR);
        } else {
          setUserHandle(param1DWORDByReference.getValue().intValue(), param1DWORD_PTR);
        }  
      return hDDEDATA;
    }
    
    public Ddeml.HDDEDATA clientTransaction(Pointer param1Pointer, int param1Int1, String param1String, int param1Int2, int param1Int3, int param1Int4, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR) {
      Ddeml.HSZ hSZ = null;
      try {
        hSZ = this.client.createStringHandle(param1String);
        return clientTransaction(param1Pointer, param1Int1, hSZ, param1Int2, param1Int3, param1Int4, param1DWORDByReference, param1DWORD_PTR);
      } finally {
        this.client.freeStringHandle(hSZ);
      } 
    }
    
    public void poke(Pointer param1Pointer, int param1Int1, Ddeml.HSZ param1HSZ, int param1Int2, int param1Int3, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR) {
      clientTransaction(param1Pointer, param1Int1, param1HSZ, param1Int2, 16528, param1Int3, param1DWORDByReference, param1DWORD_PTR);
    }
    
    public void poke(Pointer param1Pointer, int param1Int1, String param1String, int param1Int2, int param1Int3, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR) {
      Ddeml.HSZ hSZ = null;
      try {
        hSZ = this.client.createStringHandle(param1String);
        poke(param1Pointer, param1Int1, hSZ, param1Int2, param1Int3, param1DWORDByReference, param1DWORD_PTR);
      } finally {
        this.client.freeStringHandle(hSZ);
      } 
    }
    
    public Ddeml.HDDEDATA request(Ddeml.HSZ param1HSZ, int param1Int1, int param1Int2, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR) {
      return clientTransaction(Pointer.NULL, 0, param1HSZ, param1Int1, 8368, param1Int2, param1DWORDByReference, param1DWORD_PTR);
    }
    
    public Ddeml.HDDEDATA request(String param1String, int param1Int1, int param1Int2, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR) {
      Ddeml.HSZ hSZ = null;
      try {
        hSZ = this.client.createStringHandle(param1String);
        return request(hSZ, param1Int1, param1Int2, param1DWORDByReference, param1DWORD_PTR);
      } finally {
        this.client.freeStringHandle(hSZ);
      } 
    }
    
    public void execute(String param1String, int param1Int, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR) {
      Memory memory = new Memory((param1String.length() * 2 + 2));
      memory.setWideString(0L, param1String);
      clientTransaction((Pointer)memory, (int)memory.size(), (Ddeml.HSZ)null, 0, 16464, param1Int, param1DWORDByReference, param1DWORD_PTR);
    }
    
    public void advstart(Ddeml.HSZ param1HSZ, int param1Int1, int param1Int2, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR) {
      clientTransaction(Pointer.NULL, 0, param1HSZ, param1Int1, 4144, param1Int2, param1DWORDByReference, param1DWORD_PTR);
    }
    
    public void advstart(String param1String, int param1Int1, int param1Int2, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR) {
      Ddeml.HSZ hSZ = null;
      try {
        hSZ = this.client.createStringHandle(param1String);
        advstart(hSZ, param1Int1, param1Int2, param1DWORDByReference, param1DWORD_PTR);
      } finally {
        this.client.freeStringHandle(hSZ);
      } 
    }
    
    public void advstop(Ddeml.HSZ param1HSZ, int param1Int1, int param1Int2, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR) {
      clientTransaction(Pointer.NULL, 0, param1HSZ, param1Int1, 32832, param1Int2, param1DWORDByReference, param1DWORD_PTR);
    }
    
    public void advstop(String param1String, int param1Int1, int param1Int2, WinDef.DWORDByReference param1DWORDByReference, BaseTSD.DWORD_PTR param1DWORD_PTR) {
      Ddeml.HSZ hSZ = null;
      try {
        hSZ = this.client.createStringHandle(param1String);
        advstop(hSZ, param1Int1, param1Int2, param1DWORDByReference, param1DWORD_PTR);
      } finally {
        this.client.freeStringHandle(hSZ);
      } 
    }
    
    public void impersonateClient() {
      boolean bool = Ddeml.INSTANCE.DdeImpersonateClient(this.conv);
      if (!bool)
        throw DdemlUtil.DdemlException.create(this.client.getLastError()); 
    }
    
    public void close() {
      boolean bool = Ddeml.INSTANCE.DdeDisconnect(this.conv);
      if (!bool)
        throw DdemlUtil.DdemlException.create(this.client.getLastError()); 
    }
    
    public void reconnect() {
      Ddeml.HCONV hCONV = Ddeml.INSTANCE.DdeReconnect(this.conv);
      if (hCONV != null) {
        this.conv = hCONV;
      } else {
        throw DdemlUtil.DdemlException.create(this.client.getLastError());
      } 
    }
    
    public boolean enableCallback(int param1Int) {
      boolean bool = Ddeml.INSTANCE.DdeEnableCallback(this.client.getInstanceIdentitifier().intValue(), this.conv, param1Int);
      if (!bool && param1Int == 2)
        throw DdemlUtil.DdemlException.create(this.client.getLastError()); 
      return bool;
    }
    
    public void setUserHandle(int param1Int, BaseTSD.DWORD_PTR param1DWORD_PTR) throws DdemlUtil.DdemlException {
      boolean bool = Ddeml.INSTANCE.DdeSetUserHandle(this.conv, param1Int, param1DWORD_PTR);
      if (!bool)
        throw DdemlUtil.DdemlException.create(this.client.getLastError()); 
    }
    
    public Ddeml.CONVINFO queryConvInfo(int param1Int) throws DdemlUtil.DdemlException {
      Ddeml.CONVINFO cONVINFO = new Ddeml.CONVINFO();
      cONVINFO.cb = cONVINFO.size();
      cONVINFO.ConvCtxt.cb = cONVINFO.ConvCtxt.size();
      cONVINFO.write();
      int i = Ddeml.INSTANCE.DdeQueryConvInfo(this.conv, param1Int, cONVINFO);
      if (i == 0)
        throw DdemlUtil.DdemlException.create(this.client.getLastError()); 
      return cONVINFO;
    }
  }
  
  private static class MessageLoopWrapper implements InvocationHandler {
    private final Object delegate;
    
    private final User32Util.MessageLoopThread loopThread;
    
    public MessageLoopWrapper(User32Util.MessageLoopThread param1MessageLoopThread, Object param1Object) {
      this.loopThread = param1MessageLoopThread;
      this.delegate = param1Object;
    }
    
    public Object invoke(Object param1Object, Method param1Method, Object[] param1ArrayOfObject) throws Throwable {
      try {
        Class<DdemlUtil.IDdeClient> clazz;
        Object object = param1Method.invoke(this.delegate, param1ArrayOfObject);
        Class<DdemlUtil.IDdeConnection> clazz1 = null;
        if (object instanceof DdemlUtil.IDdeConnection) {
          clazz1 = DdemlUtil.IDdeConnection.class;
        } else if (object instanceof DdemlUtil.IDdeConnectionList) {
          Class<DdemlUtil.IDdeConnectionList> clazz2 = DdemlUtil.IDdeConnectionList.class;
        } else if (object instanceof DdemlUtil.IDdeClient) {
          clazz = DdemlUtil.IDdeClient.class;
        } 
        if (clazz != null && param1Method.getReturnType().isAssignableFrom(clazz))
          object = wrap(object, clazz); 
        return object;
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getCause();
        if (throwable instanceof Exception)
          throw (Exception)throwable; 
        throw invocationTargetException;
      } 
    }
    
    private <V> V wrap(V param1V, Class param1Class) {
      (new Class[1])[0] = param1Class;
      Objects.requireNonNull(this.loopThread);
      Object object = Proxy.newProxyInstance(DdemlUtil.StandaloneDdeClient.class.getClassLoader(), new Class[1], new User32Util.MessageLoopThread.Handler(this.loopThread, param1V));
      return (V)Proxy.newProxyInstance(DdemlUtil.StandaloneDdeClient.class.getClassLoader(), new Class[] { param1Class }, new MessageLoopWrapper(this.loopThread, object));
    }
  }
  
  public static class StandaloneDdeClient implements IDdeClient, Closeable {
    private final User32Util.MessageLoopThread messageLoop = new User32Util.MessageLoopThread();
    
    private final DdemlUtil.IDdeClient ddeClient = new DdemlUtil.DdeClient();
    
    private final DdemlUtil.IDdeClient clientDelegate;
    
    public StandaloneDdeClient() {
      (new Class[1])[0] = DdemlUtil.IDdeClient.class;
      Objects.requireNonNull(this.messageLoop);
      DdemlUtil.IDdeClient iDdeClient = (DdemlUtil.IDdeClient)Proxy.newProxyInstance(StandaloneDdeClient.class.getClassLoader(), new Class[1], new User32Util.MessageLoopThread.Handler(this.messageLoop, this.ddeClient));
      this.clientDelegate = (DdemlUtil.IDdeClient)Proxy.newProxyInstance(StandaloneDdeClient.class.getClassLoader(), new Class[] { DdemlUtil.IDdeClient.class }, new DdemlUtil.MessageLoopWrapper(this.messageLoop, iDdeClient));
      this.messageLoop.setDaemon(true);
      this.messageLoop.start();
    }
    
    public Integer getInstanceIdentitifier() {
      return this.ddeClient.getInstanceIdentitifier();
    }
    
    public void initialize(int param1Int) throws DdemlUtil.DdemlException {
      this.clientDelegate.initialize(param1Int);
    }
    
    public Ddeml.HSZ createStringHandle(String param1String) throws DdemlUtil.DdemlException {
      return this.clientDelegate.createStringHandle(param1String);
    }
    
    public void nameService(Ddeml.HSZ param1HSZ, int param1Int) throws DdemlUtil.DdemlException {
      this.clientDelegate.nameService(param1HSZ, param1Int);
    }
    
    public int getLastError() {
      return this.clientDelegate.getLastError();
    }
    
    public DdemlUtil.IDdeConnection connect(Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, Ddeml.CONVCONTEXT param1CONVCONTEXT) {
      return this.clientDelegate.connect(param1HSZ1, param1HSZ2, param1CONVCONTEXT);
    }
    
    public String queryString(Ddeml.HSZ param1HSZ) throws DdemlUtil.DdemlException {
      return this.clientDelegate.queryString(param1HSZ);
    }
    
    public Ddeml.HDDEDATA createDataHandle(Pointer param1Pointer, int param1Int1, int param1Int2, Ddeml.HSZ param1HSZ, int param1Int3, int param1Int4) {
      return this.clientDelegate.createDataHandle(param1Pointer, param1Int1, param1Int2, param1HSZ, param1Int3, param1Int4);
    }
    
    public void freeDataHandle(Ddeml.HDDEDATA param1HDDEDATA) {
      this.clientDelegate.freeDataHandle(param1HDDEDATA);
    }
    
    public Ddeml.HDDEDATA addData(Ddeml.HDDEDATA param1HDDEDATA, Pointer param1Pointer, int param1Int1, int param1Int2) {
      return this.clientDelegate.addData(param1HDDEDATA, param1Pointer, param1Int1, param1Int2);
    }
    
    public int getData(Ddeml.HDDEDATA param1HDDEDATA, Pointer param1Pointer, int param1Int1, int param1Int2) {
      return this.clientDelegate.getData(param1HDDEDATA, param1Pointer, param1Int1, param1Int2);
    }
    
    public Pointer accessData(Ddeml.HDDEDATA param1HDDEDATA, WinDef.DWORDByReference param1DWORDByReference) {
      return this.clientDelegate.accessData(param1HDDEDATA, param1DWORDByReference);
    }
    
    public void unaccessData(Ddeml.HDDEDATA param1HDDEDATA) {
      this.clientDelegate.unaccessData(param1HDDEDATA);
    }
    
    public void postAdvise(Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2) {
      this.clientDelegate.postAdvise(param1HSZ1, param1HSZ2);
    }
    
    public void close() throws IOException {
      this.clientDelegate.uninitialize();
      this.messageLoop.exit();
    }
    
    public boolean freeStringHandle(Ddeml.HSZ param1HSZ) {
      return this.clientDelegate.freeStringHandle(param1HSZ);
    }
    
    public boolean keepStringHandle(Ddeml.HSZ param1HSZ) {
      return this.clientDelegate.keepStringHandle(param1HSZ);
    }
    
    public void abandonTransactions() {
      this.clientDelegate.abandonTransactions();
    }
    
    public DdemlUtil.IDdeConnectionList connectList(Ddeml.HSZ param1HSZ1, Ddeml.HSZ param1HSZ2, DdemlUtil.IDdeConnectionList param1IDdeConnectionList, Ddeml.CONVCONTEXT param1CONVCONTEXT) {
      return this.clientDelegate.connectList(param1HSZ1, param1HSZ2, param1IDdeConnectionList, param1CONVCONTEXT);
    }
    
    public boolean enableCallback(int param1Int) {
      return this.clientDelegate.enableCallback(param1Int);
    }
    
    public DdemlUtil.IDdeConnection wrap(Ddeml.HCONV param1HCONV) {
      return this.clientDelegate.wrap(param1HCONV);
    }
    
    public DdemlUtil.IDdeConnection connect(String param1String1, String param1String2, Ddeml.CONVCONTEXT param1CONVCONTEXT) {
      return this.clientDelegate.connect(param1String1, param1String2, param1CONVCONTEXT);
    }
    
    public boolean uninitialize() {
      return this.clientDelegate.uninitialize();
    }
    
    public void postAdvise(String param1String1, String param1String2) {
      this.clientDelegate.postAdvise(param1String1, param1String2);
    }
    
    public DdemlUtil.IDdeConnectionList connectList(String param1String1, String param1String2, DdemlUtil.IDdeConnectionList param1IDdeConnectionList, Ddeml.CONVCONTEXT param1CONVCONTEXT) {
      return this.clientDelegate.connectList(param1String1, param1String2, param1IDdeConnectionList, param1CONVCONTEXT);
    }
    
    public void nameService(String param1String, int param1Int) throws DdemlUtil.DdemlException {
      this.clientDelegate.nameService(param1String, param1Int);
    }
    
    public void registerAdvstartHandler(DdemlUtil.AdvstartHandler param1AdvstartHandler) {
      this.clientDelegate.registerAdvstartHandler(param1AdvstartHandler);
    }
    
    public void unregisterAdvstartHandler(DdemlUtil.AdvstartHandler param1AdvstartHandler) {
      this.clientDelegate.unregisterAdvstartHandler(param1AdvstartHandler);
    }
    
    public void registerAdvstopHandler(DdemlUtil.AdvstopHandler param1AdvstopHandler) {
      this.clientDelegate.registerAdvstopHandler(param1AdvstopHandler);
    }
    
    public void unregisterAdvstopHandler(DdemlUtil.AdvstopHandler param1AdvstopHandler) {
      this.clientDelegate.unregisterAdvstopHandler(param1AdvstopHandler);
    }
    
    public void registerConnectHandler(DdemlUtil.ConnectHandler param1ConnectHandler) {
      this.clientDelegate.registerConnectHandler(param1ConnectHandler);
    }
    
    public void unregisterConnectHandler(DdemlUtil.ConnectHandler param1ConnectHandler) {
      this.clientDelegate.unregisterConnectHandler(param1ConnectHandler);
    }
    
    public void registerAdvReqHandler(DdemlUtil.AdvreqHandler param1AdvreqHandler) {
      this.clientDelegate.registerAdvReqHandler(param1AdvreqHandler);
    }
    
    public void unregisterAdvReqHandler(DdemlUtil.AdvreqHandler param1AdvreqHandler) {
      this.clientDelegate.unregisterAdvReqHandler(param1AdvreqHandler);
    }
    
    public void registerRequestHandler(DdemlUtil.RequestHandler param1RequestHandler) {
      this.clientDelegate.registerRequestHandler(param1RequestHandler);
    }
    
    public void unregisterRequestHandler(DdemlUtil.RequestHandler param1RequestHandler) {
      this.clientDelegate.unregisterRequestHandler(param1RequestHandler);
    }
    
    public void registerWildconnectHandler(DdemlUtil.WildconnectHandler param1WildconnectHandler) {
      this.clientDelegate.registerWildconnectHandler(param1WildconnectHandler);
    }
    
    public void unregisterWildconnectHandler(DdemlUtil.WildconnectHandler param1WildconnectHandler) {
      this.clientDelegate.unregisterWildconnectHandler(param1WildconnectHandler);
    }
    
    public void registerAdvdataHandler(DdemlUtil.AdvdataHandler param1AdvdataHandler) {
      this.clientDelegate.registerAdvdataHandler(param1AdvdataHandler);
    }
    
    public void unregisterAdvdataHandler(DdemlUtil.AdvdataHandler param1AdvdataHandler) {
      this.clientDelegate.unregisterAdvdataHandler(param1AdvdataHandler);
    }
    
    public void registerExecuteHandler(DdemlUtil.ExecuteHandler param1ExecuteHandler) {
      this.clientDelegate.registerExecuteHandler(param1ExecuteHandler);
    }
    
    public void unregisterExecuteHandler(DdemlUtil.ExecuteHandler param1ExecuteHandler) {
      this.clientDelegate.unregisterExecuteHandler(param1ExecuteHandler);
    }
    
    public void registerPokeHandler(DdemlUtil.PokeHandler param1PokeHandler) {
      this.clientDelegate.registerPokeHandler(param1PokeHandler);
    }
    
    public void unregisterPokeHandler(DdemlUtil.PokeHandler param1PokeHandler) {
      this.clientDelegate.unregisterPokeHandler(param1PokeHandler);
    }
    
    public void registerConnectConfirmHandler(DdemlUtil.ConnectConfirmHandler param1ConnectConfirmHandler) {
      this.clientDelegate.registerConnectConfirmHandler(param1ConnectConfirmHandler);
    }
    
    public void unregisterConnectConfirmHandler(DdemlUtil.ConnectConfirmHandler param1ConnectConfirmHandler) {
      this.clientDelegate.unregisterConnectConfirmHandler(param1ConnectConfirmHandler);
    }
    
    public void registerDisconnectHandler(DdemlUtil.DisconnectHandler param1DisconnectHandler) {
      this.clientDelegate.registerDisconnectHandler(param1DisconnectHandler);
    }
    
    public void unregisterDisconnectHandler(DdemlUtil.DisconnectHandler param1DisconnectHandler) {
      this.clientDelegate.unregisterDisconnectHandler(param1DisconnectHandler);
    }
    
    public void registerErrorHandler(DdemlUtil.ErrorHandler param1ErrorHandler) {
      this.clientDelegate.registerErrorHandler(param1ErrorHandler);
    }
    
    public void unregisterErrorHandler(DdemlUtil.ErrorHandler param1ErrorHandler) {
      this.clientDelegate.unregisterErrorHandler(param1ErrorHandler);
    }
    
    public void registerRegisterHandler(DdemlUtil.RegisterHandler param1RegisterHandler) {
      this.clientDelegate.registerRegisterHandler(param1RegisterHandler);
    }
    
    public void unregisterRegisterHandler(DdemlUtil.RegisterHandler param1RegisterHandler) {
      this.clientDelegate.unregisterRegisterHandler(param1RegisterHandler);
    }
    
    public void registerXactCompleteHandler(DdemlUtil.XactCompleteHandler param1XactCompleteHandler) {
      this.clientDelegate.registerXactCompleteHandler(param1XactCompleteHandler);
    }
    
    public void unregisterXactCompleteHandler(DdemlUtil.XactCompleteHandler param1XactCompleteHandler) {
      this.clientDelegate.unregisterXactCompleteHandler(param1XactCompleteHandler);
    }
    
    public void registerUnregisterHandler(DdemlUtil.UnregisterHandler param1UnregisterHandler) {
      this.clientDelegate.registerUnregisterHandler(param1UnregisterHandler);
    }
    
    public void unregisterUnregisterHandler(DdemlUtil.UnregisterHandler param1UnregisterHandler) {
      this.clientDelegate.unregisterUnregisterHandler(param1UnregisterHandler);
    }
    
    public void registerMonitorHandler(DdemlUtil.MonitorHandler param1MonitorHandler) {
      this.clientDelegate.registerMonitorHandler(param1MonitorHandler);
    }
    
    public void unregisterMonitorHandler(DdemlUtil.MonitorHandler param1MonitorHandler) {
      this.clientDelegate.unregisterMonitorHandler(param1MonitorHandler);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\DdemlUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */