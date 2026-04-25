package com.sun.jna.platform.win32;

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.Union;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APITypeMapper;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public interface WinBase extends WinDef, BaseTSD {
  public static final WinNT.HANDLE INVALID_HANDLE_VALUE = new WinNT.HANDLE(Pointer.createConstant((Native.POINTER_SIZE == 8) ? -1L : 4294967295L));
  
  public static final int WAIT_FAILED = -1;
  
  public static final int WAIT_OBJECT_0 = 0;
  
  public static final int WAIT_ABANDONED = 128;
  
  public static final int WAIT_ABANDONED_0 = 128;
  
  public static final int MAX_COMPUTERNAME_LENGTH = Platform.isMac() ? 15 : 31;
  
  public static final int LOGON32_LOGON_INTERACTIVE = 2;
  
  public static final int LOGON32_LOGON_NETWORK = 3;
  
  public static final int LOGON32_LOGON_BATCH = 4;
  
  public static final int LOGON32_LOGON_SERVICE = 5;
  
  public static final int LOGON32_LOGON_UNLOCK = 7;
  
  public static final int LOGON32_LOGON_NETWORK_CLEARTEXT = 8;
  
  public static final int LOGON32_LOGON_NEW_CREDENTIALS = 9;
  
  public static final int LOGON32_PROVIDER_DEFAULT = 0;
  
  public static final int LOGON32_PROVIDER_WINNT35 = 1;
  
  public static final int LOGON32_PROVIDER_WINNT40 = 2;
  
  public static final int LOGON32_PROVIDER_WINNT50 = 3;
  
  public static final int HANDLE_FLAG_INHERIT = 1;
  
  public static final int HANDLE_FLAG_PROTECT_FROM_CLOSE = 2;
  
  public static final int STARTF_USESHOWWINDOW = 1;
  
  public static final int STARTF_USESIZE = 2;
  
  public static final int STARTF_USEPOSITION = 4;
  
  public static final int STARTF_USECOUNTCHARS = 8;
  
  public static final int STARTF_USEFILLATTRIBUTE = 16;
  
  public static final int STARTF_RUNFULLSCREEN = 32;
  
  public static final int STARTF_FORCEONFEEDBACK = 64;
  
  public static final int STARTF_FORCEOFFFEEDBACK = 128;
  
  public static final int STARTF_USESTDHANDLES = 256;
  
  public static final int DEBUG_PROCESS = 1;
  
  public static final int DEBUG_ONLY_THIS_PROCESS = 2;
  
  public static final int CREATE_SUSPENDED = 4;
  
  public static final int DETACHED_PROCESS = 8;
  
  public static final int CREATE_NEW_CONSOLE = 16;
  
  public static final int CREATE_NEW_PROCESS_GROUP = 512;
  
  public static final int CREATE_UNICODE_ENVIRONMENT = 1024;
  
  public static final int CREATE_SEPARATE_WOW_VDM = 2048;
  
  public static final int CREATE_SHARED_WOW_VDM = 4096;
  
  public static final int CREATE_FORCEDOS = 8192;
  
  public static final int INHERIT_PARENT_AFFINITY = 65536;
  
  public static final int CREATE_PROTECTED_PROCESS = 262144;
  
  public static final int EXTENDED_STARTUPINFO_PRESENT = 524288;
  
  public static final int CREATE_BREAKAWAY_FROM_JOB = 16777216;
  
  public static final int CREATE_PRESERVE_CODE_AUTHZ_LEVEL = 33554432;
  
  public static final int CREATE_DEFAULT_ERROR_MODE = 67108864;
  
  public static final int CREATE_NO_WINDOW = 134217728;
  
  public static final int FILE_ENCRYPTABLE = 0;
  
  public static final int FILE_IS_ENCRYPTED = 1;
  
  public static final int FILE_SYSTEM_ATTR = 2;
  
  public static final int FILE_ROOT_DIR = 3;
  
  public static final int FILE_SYSTEM_DIR = 4;
  
  public static final int FILE_UNKNOWN = 5;
  
  public static final int FILE_SYSTEM_NOT_SUPPORT = 6;
  
  public static final int FILE_USER_DISALLOWED = 7;
  
  public static final int FILE_READ_ONLY = 8;
  
  public static final int FILE_DIR_DISALOWED = 9;
  
  public static final int CREATE_FOR_IMPORT = 1;
  
  public static final int CREATE_FOR_DIR = 2;
  
  public static final int OVERWRITE_HIDDEN = 4;
  
  public static final int INVALID_FILE_SIZE = -1;
  
  public static final int INVALID_SET_FILE_POINTER = -1;
  
  public static final int INVALID_FILE_ATTRIBUTES = -1;
  
  public static final int STILL_ACTIVE = 259;
  
  public static final int FileBasicInfo = 0;
  
  public static final int FileStandardInfo = 1;
  
  public static final int FileNameInfo = 2;
  
  public static final int FileRenameInfo = 3;
  
  public static final int FileDispositionInfo = 4;
  
  public static final int FileAllocationInfo = 5;
  
  public static final int FileEndOfFileInfo = 6;
  
  public static final int FileStreamInfo = 7;
  
  public static final int FileCompressionInfo = 8;
  
  public static final int FileAttributeTagInfo = 9;
  
  public static final int FileIdBothDirectoryInfo = 10;
  
  public static final int FileIdBothDirectoryRestartInfo = 11;
  
  public static final int FileIoPriorityHintInfo = 12;
  
  public static final int FileRemoteProtocolInfo = 13;
  
  public static final int FileFullDirectoryInfo = 14;
  
  public static final int FileFullDirectoryRestartInfo = 15;
  
  public static final int FileStorageInfo = 16;
  
  public static final int FileAlignmentInfo = 17;
  
  public static final int FileIdInfo = 18;
  
  public static final int FileIdExtdDirectoryInfo = 19;
  
  public static final int FileIdExtdDirectoryRestartInfo = 20;
  
  public static final int FILE_MAP_COPY = 1;
  
  public static final int FILE_MAP_WRITE = 2;
  
  public static final int FILE_MAP_READ = 4;
  
  public static final int FILE_MAP_ALL_ACCESS = 983071;
  
  public static final int FILE_MAP_EXECUTE = 32;
  
  public static final int FindExInfoStandard = 0;
  
  public static final int FindExInfoBasic = 1;
  
  public static final int FindExInfoMaxInfoLevel = 2;
  
  public static final int FindExSearchNameMatch = 0;
  
  public static final int FindExSearchLimitToDirectories = 1;
  
  public static final int FindExSearchLimitToDevices = 2;
  
  public static final int LMEM_FIXED = 0;
  
  public static final int LMEM_MOVEABLE = 2;
  
  public static final int LMEM_NOCOMPACT = 16;
  
  public static final int LMEM_NODISCARD = 32;
  
  public static final int LMEM_ZEROINIT = 64;
  
  public static final int LMEM_MODIFY = 128;
  
  public static final int LMEM_DISCARDABLE = 3840;
  
  public static final int LMEM_VALID_FLAGS = 3954;
  
  public static final int LMEM_INVALID_HANDLE = 32768;
  
  public static final int LHND = 66;
  
  public static final int LPTR = 64;
  
  public static final int LMEM_DISCARDED = 16384;
  
  public static final int LMEM_LOCKCOUNT = 255;
  
  public static final int FORMAT_MESSAGE_ALLOCATE_BUFFER = 256;
  
  public static final int FORMAT_MESSAGE_IGNORE_INSERTS = 512;
  
  public static final int FORMAT_MESSAGE_FROM_STRING = 1024;
  
  public static final int FORMAT_MESSAGE_FROM_HMODULE = 2048;
  
  public static final int FORMAT_MESSAGE_FROM_SYSTEM = 4096;
  
  public static final int FORMAT_MESSAGE_ARGUMENT_ARRAY = 8192;
  
  public static final int DRIVE_UNKNOWN = 0;
  
  public static final int DRIVE_NO_ROOT_DIR = 1;
  
  public static final int DRIVE_REMOVABLE = 2;
  
  public static final int DRIVE_FIXED = 3;
  
  public static final int DRIVE_REMOTE = 4;
  
  public static final int DRIVE_CDROM = 5;
  
  public static final int DRIVE_RAMDISK = 6;
  
  public static final int INFINITE = -1;
  
  public static final int MOVEFILE_COPY_ALLOWED = 2;
  
  public static final int MOVEFILE_CREATE_HARDLINK = 16;
  
  public static final int MOVEFILE_DELAY_UNTIL_REBOOT = 4;
  
  public static final int MOVEFILE_FAIL_IF_NOT_TRACKABLE = 32;
  
  public static final int MOVEFILE_REPLACE_EXISTING = 1;
  
  public static final int MOVEFILE_WRITE_THROUGH = 8;
  
  public static final int PIPE_CLIENT_END = 0;
  
  public static final int PIPE_SERVER_END = 1;
  
  public static final int PIPE_ACCESS_DUPLEX = 3;
  
  public static final int PIPE_ACCESS_INBOUND = 1;
  
  public static final int PIPE_ACCESS_OUTBOUND = 2;
  
  public static final int PIPE_TYPE_BYTE = 0;
  
  public static final int PIPE_TYPE_MESSAGE = 4;
  
  public static final int PIPE_READMODE_BYTE = 0;
  
  public static final int PIPE_READMODE_MESSAGE = 2;
  
  public static final int PIPE_WAIT = 0;
  
  public static final int PIPE_NOWAIT = 1;
  
  public static final int PIPE_ACCEPT_REMOTE_CLIENTS = 0;
  
  public static final int PIPE_REJECT_REMOTE_CLIENTS = 8;
  
  public static final int PIPE_UNLIMITED_INSTANCES = 255;
  
  public static final int NMPWAIT_USE_DEFAULT_WAIT = 0;
  
  public static final int NMPWAIT_NOWAIT = 1;
  
  public static final int NMPWAIT_WAIT_FOREVER = -1;
  
  public static final int NOPARITY = 0;
  
  public static final int ODDPARITY = 1;
  
  public static final int EVENPARITY = 2;
  
  public static final int MARKPARITY = 3;
  
  public static final int SPACEPARITY = 4;
  
  public static final int ONESTOPBIT = 0;
  
  public static final int ONE5STOPBITS = 1;
  
  public static final int TWOSTOPBITS = 2;
  
  public static final int CBR_110 = 110;
  
  public static final int CBR_300 = 300;
  
  public static final int CBR_600 = 600;
  
  public static final int CBR_1200 = 1200;
  
  public static final int CBR_2400 = 2400;
  
  public static final int CBR_4800 = 4800;
  
  public static final int CBR_9600 = 9600;
  
  public static final int CBR_14400 = 14400;
  
  public static final int CBR_19200 = 19200;
  
  public static final int CBR_38400 = 38400;
  
  public static final int CBR_56000 = 56000;
  
  public static final int CBR_128000 = 128000;
  
  public static final int CBR_256000 = 256000;
  
  public static final int DTR_CONTROL_DISABLE = 0;
  
  public static final int DTR_CONTROL_ENABLE = 1;
  
  public static final int DTR_CONTROL_HANDSHAKE = 2;
  
  public static final int RTS_CONTROL_DISABLE = 0;
  
  public static final int RTS_CONTROL_ENABLE = 1;
  
  public static final int RTS_CONTROL_HANDSHAKE = 2;
  
  public static final int RTS_CONTROL_TOGGLE = 3;
  
  public static final int ES_AWAYMODE_REQUIRED = 64;
  
  public static final int ES_CONTINUOUS = -2147483648;
  
  public static final int ES_DISPLAY_REQUIRED = 2;
  
  public static final int ES_SYSTEM_REQUIRED = 1;
  
  public static final int ES_USER_PRESENT = 4;
  
  public static final int MUTEX_MODIFY_STATE = 1;
  
  public static final int MUTEX_ALL_ACCESS = 2031617;
  
  public static interface EnumResNameProc extends Callback {
    boolean invoke(WinDef.HMODULE param1HMODULE, Pointer param1Pointer1, Pointer param1Pointer2, Pointer param1Pointer3);
  }
  
  public static interface EnumResTypeProc extends Callback {
    boolean invoke(WinDef.HMODULE param1HMODULE, Pointer param1Pointer1, Pointer param1Pointer2);
  }
  
  @FieldOrder({"DCBlength", "BaudRate", "controllBits", "wReserved", "XonLim", "XoffLim", "ByteSize", "Parity", "StopBits", "XonChar", "XoffChar", "ErrorChar", "EofChar", "EvtChar", "wReserved1"})
  public static class DCB extends Structure {
    public WinDef.DWORD DCBlength = new WinDef.DWORD(size());
    
    public WinDef.DWORD BaudRate;
    
    public DCBControllBits controllBits;
    
    public WinDef.WORD wReserved;
    
    public WinDef.WORD XonLim;
    
    public WinDef.WORD XoffLim;
    
    public WinDef.BYTE ByteSize;
    
    public WinDef.BYTE Parity;
    
    public WinDef.BYTE StopBits;
    
    public char XonChar;
    
    public char XoffChar;
    
    public char ErrorChar;
    
    public char EofChar;
    
    public char EvtChar;
    
    public WinDef.WORD wReserved1;
    
    public static class DCBControllBits extends WinDef.DWORD {
      private static final long serialVersionUID = 8574966619718078579L;
      
      public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('<');
        stringBuilder.append("fBinary:1=");
        stringBuilder.append(getfBinary() ? 49 : 48);
        stringBuilder.append(", fParity:1=");
        stringBuilder.append(getfParity() ? 49 : 48);
        stringBuilder.append(", fOutxCtsFlow:1=");
        stringBuilder.append(getfOutxCtsFlow() ? 49 : 48);
        stringBuilder.append(", fOutxDsrFlow:1=");
        stringBuilder.append(getfOutxDsrFlow() ? 49 : 48);
        stringBuilder.append(", fDtrControl:2=");
        stringBuilder.append(getfDtrControl());
        stringBuilder.append(", fDsrSensitivity:1=");
        stringBuilder.append(getfDsrSensitivity() ? 49 : 48);
        stringBuilder.append(", fTXContinueOnXoff:1=");
        stringBuilder.append(getfTXContinueOnXoff() ? 49 : 48);
        stringBuilder.append(", fOutX:1=");
        stringBuilder.append(getfOutX() ? 49 : 48);
        stringBuilder.append(", fInX:1=");
        stringBuilder.append(getfInX() ? 49 : 48);
        stringBuilder.append(", fErrorChar:1=");
        stringBuilder.append(getfErrorChar() ? 49 : 48);
        stringBuilder.append(", fNull:1=");
        stringBuilder.append(getfNull() ? 49 : 48);
        stringBuilder.append(", fRtsControl:2=");
        stringBuilder.append(getfRtsControl());
        stringBuilder.append(", fAbortOnError:1=");
        stringBuilder.append(getfAbortOnError() ? 49 : 48);
        stringBuilder.append(", fDummy2:17=");
        stringBuilder.append(getfDummy2());
        stringBuilder.append('>');
        return stringBuilder.toString();
      }
      
      public boolean getfAbortOnError() {
        return ((intValue() & 0x4000) != 0);
      }
      
      public boolean getfBinary() {
        return ((intValue() & 0x1) != 0);
      }
      
      public boolean getfDsrSensitivity() {
        return ((intValue() & 0x40) != 0);
      }
      
      public int getfDtrControl() {
        return intValue() >>> 4 & 0x3;
      }
      
      public boolean getfErrorChar() {
        return ((intValue() & 0x400) != 0);
      }
      
      public boolean getfInX() {
        return ((intValue() & 0x200) != 0);
      }
      
      public boolean getfNull() {
        return ((intValue() & 0x800) != 0);
      }
      
      public boolean getfOutX() {
        return ((intValue() & 0x100) != 0);
      }
      
      public boolean getfOutxCtsFlow() {
        return ((intValue() & 0x4) != 0);
      }
      
      public boolean getfOutxDsrFlow() {
        return ((intValue() & 0x8) != 0);
      }
      
      public boolean getfParity() {
        return ((intValue() & 0x2) != 0);
      }
      
      public int getfRtsControl() {
        return intValue() >>> 12 & 0x3;
      }
      
      public int getfDummy2() {
        return intValue() >>> 15 & 0x1FFFF;
      }
      
      public boolean getfTXContinueOnXoff() {
        return ((intValue() & 0x80) != 0);
      }
      
      public void setfAbortOnError(boolean param2Boolean) {
        int i = leftShiftMask(param2Boolean ? 1 : 0, (byte)14, 1, intValue());
        setValue(i);
      }
      
      public void setfBinary(boolean param2Boolean) {
        int i = leftShiftMask(param2Boolean ? 1 : 0, (byte)0, 1, intValue());
        setValue(i);
      }
      
      public void setfDsrSensitivity(boolean param2Boolean) {
        int i = leftShiftMask(param2Boolean ? 1 : 0, (byte)6, 1, intValue());
        setValue(i);
      }
      
      public void setfDtrControl(int param2Int) {
        int i = leftShiftMask(param2Int, (byte)4, 3, intValue());
        setValue(i);
      }
      
      public void setfErrorChar(boolean param2Boolean) {
        int i = leftShiftMask(param2Boolean ? 1 : 0, (byte)10, 1, intValue());
        setValue(i);
      }
      
      public void setfInX(boolean param2Boolean) {
        int i = leftShiftMask(param2Boolean ? 1 : 0, (byte)9, 1, intValue());
        setValue(i);
      }
      
      public void setfNull(boolean param2Boolean) {
        int i = leftShiftMask(param2Boolean ? 1 : 0, (byte)11, 1, intValue());
        setValue(i);
      }
      
      public void setfOutX(boolean param2Boolean) {
        int i = leftShiftMask(param2Boolean ? 1 : 0, (byte)8, 1, intValue());
        setValue(i);
      }
      
      public void setfOutxCtsFlow(boolean param2Boolean) {
        int i = leftShiftMask(param2Boolean ? 1 : 0, (byte)2, 1, intValue());
        setValue(i);
      }
      
      public void setfOutxDsrFlow(boolean param2Boolean) {
        int i = leftShiftMask(param2Boolean ? 1 : 0, (byte)3, 1, intValue());
        setValue(i);
      }
      
      public void setfParity(boolean param2Boolean) {
        int i = leftShiftMask(param2Boolean ? 1 : 0, (byte)1, 1, intValue());
        setValue(i);
      }
      
      public void setfRtsControl(int param2Int) {
        int i = leftShiftMask(param2Int, (byte)12, 3, intValue());
        setValue(i);
      }
      
      public void setfTXContinueOnXoff(boolean param2Boolean) {
        int i = leftShiftMask(param2Boolean ? 1 : 0, (byte)7, 1, intValue());
        setValue(i);
      }
      
      private static int leftShiftMask(int param2Int1, byte param2Byte, int param2Int2, int param2Int3) {
        int i = param2Int3;
        i &= param2Int2 << param2Byte ^ 0xFFFFFFFF;
        i |= (param2Int1 & param2Int2) << param2Byte;
        return i;
      }
    }
  }
  
  @FieldOrder({"ReadIntervalTimeout", "ReadTotalTimeoutMultiplier", "ReadTotalTimeoutConstant", "WriteTotalTimeoutMultiplier", "WriteTotalTimeoutConstant"})
  public static class COMMTIMEOUTS extends Structure {
    public WinDef.DWORD ReadIntervalTimeout;
    
    public WinDef.DWORD ReadTotalTimeoutMultiplier;
    
    public WinDef.DWORD ReadTotalTimeoutConstant;
    
    public WinDef.DWORD WriteTotalTimeoutMultiplier;
    
    public WinDef.DWORD WriteTotalTimeoutConstant;
  }
  
  public static interface FE_IMPORT_FUNC extends StdCallLibrary.StdCallCallback {
    WinDef.DWORD callback(Pointer param1Pointer1, Pointer param1Pointer2, WinDef.ULONGByReference param1ULONGByReference);
  }
  
  public static interface FE_EXPORT_FUNC extends StdCallLibrary.StdCallCallback {
    WinDef.DWORD callback(Pointer param1Pointer1, Pointer param1Pointer2, WinDef.ULONG param1ULONG);
  }
  
  public static interface COMPUTER_NAME_FORMAT {
    public static final int ComputerNameNetBIOS = 0;
    
    public static final int ComputerNameDnsHostname = 1;
    
    public static final int ComputerNameDnsDomain = 2;
    
    public static final int ComputerNameDnsFullyQualified = 3;
    
    public static final int ComputerNamePhysicalNetBIOS = 4;
    
    public static final int ComputerNamePhysicalDnsHostname = 5;
    
    public static final int ComputerNamePhysicalDnsDomain = 6;
    
    public static final int ComputerNamePhysicalDnsFullyQualified = 7;
    
    public static final int ComputerNameMax = 8;
  }
  
  @FieldOrder({"foreignLocation"})
  public static class FOREIGN_THREAD_START_ROUTINE extends Structure {
    public WinDef.LPVOID foreignLocation;
  }
  
  public static interface THREAD_START_ROUTINE extends StdCallLibrary.StdCallCallback {
    WinDef.DWORD apply(WinDef.LPVOID param1LPVOID);
  }
  
  @FieldOrder({"hProcess", "hThread", "dwProcessId", "dwThreadId"})
  public static class PROCESS_INFORMATION extends Structure {
    public WinNT.HANDLE hProcess;
    
    public WinNT.HANDLE hThread;
    
    public WinDef.DWORD dwProcessId;
    
    public WinDef.DWORD dwThreadId;
    
    public PROCESS_INFORMATION() {}
    
    public PROCESS_INFORMATION(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public static class ByReference extends PROCESS_INFORMATION implements Structure.ByReference {
      public ByReference() {}
      
      public ByReference(Pointer param2Pointer) {
        super(param2Pointer);
      }
    }
  }
  
  @FieldOrder({"cb", "lpReserved", "lpDesktop", "lpTitle", "dwX", "dwY", "dwXSize", "dwYSize", "dwXCountChars", "dwYCountChars", "dwFillAttribute", "dwFlags", "wShowWindow", "cbReserved2", "lpReserved2", "hStdInput", "hStdOutput", "hStdError"})
  public static class STARTUPINFO extends Structure {
    public WinDef.DWORD cb = new WinDef.DWORD(size());
    
    public String lpReserved;
    
    public String lpDesktop;
    
    public String lpTitle;
    
    public WinDef.DWORD dwX;
    
    public WinDef.DWORD dwY;
    
    public WinDef.DWORD dwXSize;
    
    public WinDef.DWORD dwYSize;
    
    public WinDef.DWORD dwXCountChars;
    
    public WinDef.DWORD dwYCountChars;
    
    public WinDef.DWORD dwFillAttribute;
    
    public int dwFlags;
    
    public WinDef.WORD wShowWindow;
    
    public WinDef.WORD cbReserved2;
    
    public ByteByReference lpReserved2;
    
    public WinNT.HANDLE hStdInput;
    
    public WinNT.HANDLE hStdOutput;
    
    public WinNT.HANDLE hStdError;
    
    public STARTUPINFO() {
      super(W32APITypeMapper.DEFAULT);
    }
  }
  
  @FieldOrder({"dwLength", "lpSecurityDescriptor", "bInheritHandle"})
  public static class SECURITY_ATTRIBUTES extends Structure {
    public WinDef.DWORD dwLength = new WinDef.DWORD(size());
    
    public Pointer lpSecurityDescriptor;
    
    public boolean bInheritHandle;
  }
  
  @FieldOrder({"dwLength", "dwMemoryLoad", "ullTotalPhys", "ullAvailPhys", "ullTotalPageFile", "ullAvailPageFile", "ullTotalVirtual", "ullAvailVirtual", "ullAvailExtendedVirtual"})
  public static class MEMORYSTATUSEX extends Structure {
    public WinDef.DWORD dwLength = new WinDef.DWORD(size());
    
    public WinDef.DWORD dwMemoryLoad;
    
    public WinDef.DWORDLONG ullTotalPhys;
    
    public WinDef.DWORDLONG ullAvailPhys;
    
    public WinDef.DWORDLONG ullTotalPageFile;
    
    public WinDef.DWORDLONG ullAvailPageFile;
    
    public WinDef.DWORDLONG ullTotalVirtual;
    
    public WinDef.DWORDLONG ullAvailVirtual;
    
    public WinDef.DWORDLONG ullAvailExtendedVirtual;
  }
  
  @FieldOrder({"processorArchitecture", "dwPageSize", "lpMinimumApplicationAddress", "lpMaximumApplicationAddress", "dwActiveProcessorMask", "dwNumberOfProcessors", "dwProcessorType", "dwAllocationGranularity", "wProcessorLevel", "wProcessorRevision"})
  public static class SYSTEM_INFO extends Structure {
    public UNION processorArchitecture;
    
    public WinDef.DWORD dwPageSize;
    
    public Pointer lpMinimumApplicationAddress;
    
    public Pointer lpMaximumApplicationAddress;
    
    public BaseTSD.DWORD_PTR dwActiveProcessorMask;
    
    public WinDef.DWORD dwNumberOfProcessors;
    
    public WinDef.DWORD dwProcessorType;
    
    public WinDef.DWORD dwAllocationGranularity;
    
    public WinDef.WORD wProcessorLevel;
    
    public WinDef.WORD wProcessorRevision;
    
    public static class UNION extends Union {
      public WinDef.DWORD dwOemID;
      
      public WinBase.SYSTEM_INFO.PI pi;
      
      public void read() {
        setType("dwOemID");
        super.read();
        setType("pi");
        super.read();
      }
      
      public static class ByReference extends UNION implements Structure.ByReference {}
    }
    
    @FieldOrder({"wProcessorArchitecture", "wReserved"})
    public static class PI extends Structure {
      public WinDef.WORD wProcessorArchitecture;
      
      public WinDef.WORD wReserved;
      
      public static class ByReference extends PI implements Structure.ByReference {}
    }
  }
  
  @FieldOrder({"Internal", "InternalHigh", "Offset", "OffsetHigh", "hEvent"})
  public static class OVERLAPPED extends Structure {
    public BaseTSD.ULONG_PTR Internal;
    
    public BaseTSD.ULONG_PTR InternalHigh;
    
    public int Offset;
    
    public int OffsetHigh;
    
    public WinNT.HANDLE hEvent;
  }
  
  @FieldOrder({"Bias", "StandardName", "StandardDate", "StandardBias", "DaylightName", "DaylightDate", "DaylightBias"})
  public static class TIME_ZONE_INFORMATION extends Structure {
    public WinDef.LONG Bias;
    
    public String StandardName;
    
    public WinBase.SYSTEMTIME StandardDate;
    
    public WinDef.LONG StandardBias;
    
    public String DaylightName;
    
    public WinBase.SYSTEMTIME DaylightDate;
    
    public WinDef.LONG DaylightBias;
    
    public TIME_ZONE_INFORMATION() {
      super(W32APITypeMapper.DEFAULT);
    }
  }
  
  @FieldOrder({"wYear", "wMonth", "wDayOfWeek", "wDay", "wHour", "wMinute", "wSecond", "wMilliseconds"})
  public static class SYSTEMTIME extends Structure {
    public short wYear;
    
    public short wMonth;
    
    public short wDayOfWeek;
    
    public short wDay;
    
    public short wHour;
    
    public short wMinute;
    
    public short wSecond;
    
    public short wMilliseconds;
    
    public SYSTEMTIME() {}
    
    public SYSTEMTIME(Date param1Date) {
      this(param1Date.getTime());
    }
    
    public SYSTEMTIME(long param1Long) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(param1Long);
      fromCalendar(calendar);
    }
    
    public SYSTEMTIME(Calendar param1Calendar) {
      fromCalendar(param1Calendar);
    }
    
    public void fromCalendar(Calendar param1Calendar) {
      this.wYear = (short)param1Calendar.get(1);
      this.wMonth = (short)(1 + param1Calendar.get(2) - 0);
      this.wDay = (short)param1Calendar.get(5);
      this.wHour = (short)param1Calendar.get(11);
      this.wMinute = (short)param1Calendar.get(12);
      this.wSecond = (short)param1Calendar.get(13);
      this.wMilliseconds = (short)param1Calendar.get(14);
      this.wDayOfWeek = (short)(param1Calendar.get(7) - 1);
    }
    
    public Calendar toCalendar() {
      Calendar calendar = Calendar.getInstance();
      calendar.set(1, this.wYear);
      calendar.set(2, 0 + this.wMonth - 1);
      calendar.set(5, this.wDay);
      calendar.set(11, this.wHour);
      calendar.set(12, this.wMinute);
      calendar.set(13, this.wSecond);
      calendar.set(14, this.wMilliseconds);
      return calendar;
    }
    
    public String toString() {
      if (this.wYear == 0 && this.wMonth == 0 && this.wDay == 0 && this.wHour == 0 && this.wMinute == 0 && this.wSecond == 0 && this.wMilliseconds == 0)
        return super.toString(); 
      DateFormat dateFormat = DateFormat.getDateTimeInstance();
      Calendar calendar = toCalendar();
      return dateFormat.format(calendar.getTime());
    }
  }
  
  @FieldOrder({"dwLowDateTime", "dwHighDateTime"})
  public static class FILETIME extends Structure {
    public int dwLowDateTime;
    
    public int dwHighDateTime;
    
    private static final long EPOCH_DIFF = 11644473600000L;
    
    public FILETIME(Date param1Date) {
      long l = dateToFileTime(param1Date);
      this.dwHighDateTime = (int)(l >> 32L & 0xFFFFFFFFL);
      this.dwLowDateTime = (int)(l & 0xFFFFFFFFL);
    }
    
    public FILETIME(WinNT.LARGE_INTEGER param1LARGE_INTEGER) {
      this.dwHighDateTime = param1LARGE_INTEGER.getHigh().intValue();
      this.dwLowDateTime = param1LARGE_INTEGER.getLow().intValue();
    }
    
    public FILETIME() {}
    
    public FILETIME(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public static Date filetimeToDate(int param1Int1, int param1Int2) {
      long l1 = param1Int1 << 32L | param1Int2 & 0xFFFFFFFFL;
      long l2 = l1 / 10000L;
      long l3 = l2 - 11644473600000L;
      return new Date(l3);
    }
    
    public static long dateToFileTime(Date param1Date) {
      long l1 = param1Date.getTime();
      long l2 = l1 + 11644473600000L;
      return l2 * 1000L * 10L;
    }
    
    public Date toDate() {
      return filetimeToDate(this.dwHighDateTime, this.dwLowDateTime);
    }
    
    public long toTime() {
      return toDate().getTime();
    }
    
    public WinDef.DWORDLONG toDWordLong() {
      return new WinDef.DWORDLONG(this.dwHighDateTime << 32L | this.dwLowDateTime & 0xFFFFFFFFL);
    }
    
    public String toString() {
      return super.toString() + ": " + toDate().toString();
    }
    
    public static class ByReference extends FILETIME implements Structure.ByReference {
      public ByReference() {}
      
      public ByReference(Pointer param2Pointer) {
        super(param2Pointer);
      }
    }
  }
  
  @FieldOrder({"dwFileAttributes", "ftCreationTime", "ftLastAccessTime", "ftLastWriteTime", "nFileSizeHigh", "nFileSizeLow", "dwReserved0", "dwReserved1", "cFileName", "cAlternateFileName"})
  public static class WIN32_FIND_DATA extends Structure {
    public int dwFileAttributes;
    
    public WinBase.FILETIME ftCreationTime;
    
    public WinBase.FILETIME ftLastAccessTime;
    
    public WinBase.FILETIME ftLastWriteTime;
    
    public int nFileSizeHigh;
    
    public int nFileSizeLow;
    
    public int dwReserved0;
    
    public int dwReserved1;
    
    public char[] cFileName = new char[260];
    
    public char[] cAlternateFileName = new char[14];
    
    public static int sizeOf() {
      return Native.getNativeSize(WIN32_FIND_DATA.class, null);
    }
    
    public WIN32_FIND_DATA() {
      super(W32APITypeMapper.DEFAULT);
    }
    
    public WIN32_FIND_DATA(Pointer param1Pointer) {
      super(param1Pointer, 0, W32APITypeMapper.DEFAULT);
      read();
    }
    
    public WIN32_FIND_DATA(int param1Int1, WinBase.FILETIME param1FILETIME1, WinBase.FILETIME param1FILETIME2, WinBase.FILETIME param1FILETIME3, int param1Int2, int param1Int3, int param1Int4, int param1Int5, char[] param1ArrayOfchar1, char[] param1ArrayOfchar2) {
      this.dwFileAttributes = param1Int1;
      this.ftCreationTime = param1FILETIME1;
      this.ftLastAccessTime = param1FILETIME2;
      this.ftLastWriteTime = param1FILETIME3;
      this.nFileSizeHigh = param1Int2;
      this.nFileSizeLow = param1Int3;
      this.dwReserved0 = param1Int4;
      this.cFileName = param1ArrayOfchar1;
      this.cAlternateFileName = param1ArrayOfchar2;
      write();
    }
    
    public String getFileName() {
      return Native.toString(this.cFileName);
    }
    
    public String getAlternateFileName() {
      return Native.toString(this.cAlternateFileName);
    }
    
    public static class ByReference extends WIN32_FIND_DATA implements Structure.ByReference {
      public ByReference() {}
      
      public ByReference(Pointer param2Pointer) {
        super(param2Pointer);
      }
    }
  }
  
  @FieldOrder({"VolumeSerialNumber", "FileId"})
  public static class FILE_ID_INFO extends Structure {
    public long VolumeSerialNumber;
    
    public FILE_ID_128 FileId;
    
    public static int sizeOf() {
      return Native.getNativeSize(FILE_ID_INFO.class, null);
    }
    
    public FILE_ID_INFO() {}
    
    public FILE_ID_INFO(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public FILE_ID_INFO(long param1Long, FILE_ID_128 param1FILE_ID_128) {
      this.VolumeSerialNumber = param1Long;
      this.FileId = param1FILE_ID_128;
      write();
    }
    
    @FieldOrder({"Identifier"})
    public static class FILE_ID_128 extends Structure {
      public WinDef.BYTE[] Identifier = new WinDef.BYTE[16];
      
      public FILE_ID_128() {}
      
      public FILE_ID_128(Pointer param2Pointer) {
        super(param2Pointer);
        read();
      }
      
      public FILE_ID_128(WinDef.BYTE[] param2ArrayOfBYTE) {
        this.Identifier = param2ArrayOfBYTE;
        write();
      }
    }
    
    public static class ByReference extends FILE_ID_INFO implements Structure.ByReference {
      public ByReference() {}
      
      public ByReference(Pointer param2Pointer) {
        super(param2Pointer);
      }
    }
  }
  
  @FieldOrder({"FileAttributes", "ReparseTag"})
  public static class FILE_ATTRIBUTE_TAG_INFO extends Structure {
    public int FileAttributes;
    
    public int ReparseTag;
    
    public static int sizeOf() {
      return Native.getNativeSize(FILE_ATTRIBUTE_TAG_INFO.class, null);
    }
    
    public FILE_ATTRIBUTE_TAG_INFO() {}
    
    public FILE_ATTRIBUTE_TAG_INFO(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public FILE_ATTRIBUTE_TAG_INFO(int param1Int1, int param1Int2) {
      this.FileAttributes = param1Int1;
      this.ReparseTag = param1Int2;
      write();
    }
    
    public static class ByReference extends FILE_ATTRIBUTE_TAG_INFO implements Structure.ByReference {
      public ByReference() {}
      
      public ByReference(Pointer param2Pointer) {
        super(param2Pointer);
      }
    }
  }
  
  @FieldOrder({"CompressedFileSize", "CompressionFormat", "CompressionUnitShift", "ChunkShift", "ClusterShift", "Reserved"})
  public static class FILE_COMPRESSION_INFO extends Structure {
    public WinNT.LARGE_INTEGER CompressedFileSize;
    
    public short CompressionFormat;
    
    public byte CompressionUnitShift;
    
    public byte ChunkShift;
    
    public byte ClusterShift;
    
    public byte[] Reserved = new byte[3];
    
    public static int sizeOf() {
      return Native.getNativeSize(FILE_COMPRESSION_INFO.class, null);
    }
    
    public FILE_COMPRESSION_INFO() {
      super(W32APITypeMapper.DEFAULT);
    }
    
    public FILE_COMPRESSION_INFO(Pointer param1Pointer) {
      super(param1Pointer, 0, W32APITypeMapper.DEFAULT);
      read();
    }
    
    public FILE_COMPRESSION_INFO(WinNT.LARGE_INTEGER param1LARGE_INTEGER, short param1Short, byte param1Byte1, byte param1Byte2, byte param1Byte3) {
      this.CompressedFileSize = param1LARGE_INTEGER;
      this.CompressionFormat = param1Short;
      this.CompressionUnitShift = param1Byte1;
      this.ChunkShift = param1Byte2;
      this.ClusterShift = param1Byte3;
      this.Reserved = new byte[3];
      write();
    }
    
    public static class ByReference extends FILE_COMPRESSION_INFO implements Structure.ByReference {
      public ByReference() {}
      
      public ByReference(Pointer param2Pointer) {
        super(param2Pointer);
      }
    }
  }
  
  @FieldOrder({"DeleteFile"})
  public static class FILE_DISPOSITION_INFO extends Structure {
    public boolean DeleteFile;
    
    public static int sizeOf() {
      return Native.getNativeSize(FILE_DISPOSITION_INFO.class, null);
    }
    
    public FILE_DISPOSITION_INFO() {}
    
    public FILE_DISPOSITION_INFO(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public FILE_DISPOSITION_INFO(boolean param1Boolean) {
      this.DeleteFile = param1Boolean;
      write();
    }
    
    public static class ByReference extends FILE_DISPOSITION_INFO implements Structure.ByReference {
      public ByReference() {}
      
      public ByReference(Pointer param2Pointer) {
        super(param2Pointer);
      }
    }
  }
  
  @FieldOrder({"AllocationSize", "EndOfFile", "NumberOfLinks", "DeletePending", "Directory"})
  public static class FILE_STANDARD_INFO extends Structure {
    public WinNT.LARGE_INTEGER AllocationSize;
    
    public WinNT.LARGE_INTEGER EndOfFile;
    
    public int NumberOfLinks;
    
    public boolean DeletePending;
    
    public boolean Directory;
    
    public static int sizeOf() {
      return Native.getNativeSize(FILE_STANDARD_INFO.class, null);
    }
    
    public FILE_STANDARD_INFO() {}
    
    public FILE_STANDARD_INFO(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public FILE_STANDARD_INFO(WinNT.LARGE_INTEGER param1LARGE_INTEGER1, WinNT.LARGE_INTEGER param1LARGE_INTEGER2, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      this.AllocationSize = param1LARGE_INTEGER1;
      this.EndOfFile = param1LARGE_INTEGER2;
      this.NumberOfLinks = param1Int;
      this.DeletePending = param1Boolean1;
      this.Directory = param1Boolean2;
      write();
    }
    
    public static class ByReference extends FILE_STANDARD_INFO implements Structure.ByReference {
      public ByReference() {}
      
      public ByReference(Pointer param2Pointer) {
        super(param2Pointer);
      }
    }
  }
  
  @FieldOrder({"CreationTime", "LastAccessTime", "LastWriteTime", "ChangeTime", "FileAttributes"})
  public static class FILE_BASIC_INFO extends Structure {
    public WinNT.LARGE_INTEGER CreationTime;
    
    public WinNT.LARGE_INTEGER LastAccessTime;
    
    public WinNT.LARGE_INTEGER LastWriteTime;
    
    public WinNT.LARGE_INTEGER ChangeTime;
    
    public int FileAttributes;
    
    public static int sizeOf() {
      return Native.getNativeSize(FILE_BASIC_INFO.class, null);
    }
    
    public FILE_BASIC_INFO() {}
    
    public FILE_BASIC_INFO(Pointer param1Pointer) {
      super(param1Pointer);
      read();
      this.CreationTime = new WinNT.LARGE_INTEGER(this.CreationTime.getValue());
      this.LastAccessTime = new WinNT.LARGE_INTEGER(this.LastAccessTime.getValue());
      this.LastWriteTime = new WinNT.LARGE_INTEGER(this.LastWriteTime.getValue());
      this.ChangeTime = new WinNT.LARGE_INTEGER(this.ChangeTime.getValue());
    }
    
    public FILE_BASIC_INFO(WinBase.FILETIME param1FILETIME1, WinBase.FILETIME param1FILETIME2, WinBase.FILETIME param1FILETIME3, WinBase.FILETIME param1FILETIME4, int param1Int) {
      this.CreationTime = new WinNT.LARGE_INTEGER(param1FILETIME1.toTime());
      this.LastAccessTime = new WinNT.LARGE_INTEGER(param1FILETIME2.toTime());
      this.LastWriteTime = new WinNT.LARGE_INTEGER(param1FILETIME3.toTime());
      this.ChangeTime = new WinNT.LARGE_INTEGER(param1FILETIME4.toTime());
      this.FileAttributes = param1Int;
      write();
    }
    
    public FILE_BASIC_INFO(WinNT.LARGE_INTEGER param1LARGE_INTEGER1, WinNT.LARGE_INTEGER param1LARGE_INTEGER2, WinNT.LARGE_INTEGER param1LARGE_INTEGER3, WinNT.LARGE_INTEGER param1LARGE_INTEGER4, int param1Int) {
      this.CreationTime = param1LARGE_INTEGER1;
      this.LastAccessTime = param1LARGE_INTEGER2;
      this.LastWriteTime = param1LARGE_INTEGER3;
      this.ChangeTime = param1LARGE_INTEGER4;
      this.FileAttributes = param1Int;
      write();
    }
    
    public static class ByReference extends FILE_BASIC_INFO implements Structure.ByReference {
      public ByReference() {}
      
      public ByReference(Pointer param2Pointer) {
        super(param2Pointer);
      }
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\WinBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */