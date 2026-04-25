package com.sun.jna;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Platform {
  public static final int UNSPECIFIED = -1;
  
  public static final int MAC = 0;
  
  public static final int LINUX = 1;
  
  public static final int WINDOWS = 2;
  
  public static final int SOLARIS = 3;
  
  public static final int FREEBSD = 4;
  
  public static final int OPENBSD = 5;
  
  public static final int WINDOWSCE = 6;
  
  public static final int AIX = 7;
  
  public static final int ANDROID = 8;
  
  public static final int GNU = 9;
  
  public static final int KFREEBSD = 10;
  
  public static final int NETBSD = 11;
  
  public static final int DRAGONFLYBSD = 12;
  
  public static final boolean RO_FIELDS;
  
  public static final boolean HAS_BUFFERS;
  
  public static final boolean HAS_AWT = (osType != 6 && osType != 8 && osType != 7);
  
  public static final boolean HAS_JAWT = (HAS_AWT && osType != 0);
  
  public static final String MATH_LIBRARY_NAME;
  
  public static final String C_LIBRARY_NAME;
  
  public static final boolean HAS_DLL_CALLBACKS;
  
  public static final String RESOURCE_PREFIX;
  
  private static final int osType;
  
  public static final String ARCH;
  
  public static final int getOSType() {
    return osType;
  }
  
  public static final boolean isMac() {
    return (osType == 0);
  }
  
  public static final boolean isAndroid() {
    return (osType == 8);
  }
  
  public static final boolean isLinux() {
    return (osType == 1);
  }
  
  public static final boolean isAIX() {
    return (osType == 7);
  }
  
  public static final boolean isWindowsCE() {
    return (osType == 6);
  }
  
  public static final boolean isWindows() {
    return (osType == 2 || osType == 6);
  }
  
  public static final boolean isSolaris() {
    return (osType == 3);
  }
  
  public static final boolean isDragonFlyBSD() {
    return (osType == 12);
  }
  
  public static final boolean isFreeBSD() {
    return (osType == 4);
  }
  
  public static final boolean isOpenBSD() {
    return (osType == 5);
  }
  
  public static final boolean isNetBSD() {
    return (osType == 11);
  }
  
  public static final boolean isGNU() {
    return (osType == 9);
  }
  
  public static final boolean iskFreeBSD() {
    return (osType == 10);
  }
  
  public static final boolean isX11() {
    return (!isWindows() && !isMac());
  }
  
  public static final boolean hasRuntimeExec() {
    return !(isWindowsCE() && "J9".equals(System.getProperty("java.vm.name")));
  }
  
  public static final boolean is64Bit() {
    String str = System.getProperty("sun.arch.data.model", System.getProperty("com.ibm.vm.bitmode"));
    return (str != null) ? "64".equals(str) : (("x86-64".equals(ARCH) || "ia64".equals(ARCH) || "ppc64".equals(ARCH) || "ppc64le".equals(ARCH) || "sparcv9".equals(ARCH) || "mips64".equals(ARCH) || "mips64el".equals(ARCH) || "loongarch64".equals(ARCH) || "amd64".equals(ARCH) || "aarch64".equals(ARCH)) ? true : ((Native.POINTER_SIZE == 8)));
  }
  
  public static final boolean isIntel() {
    return ARCH.startsWith("x86");
  }
  
  public static final boolean isPPC() {
    return ARCH.startsWith("ppc");
  }
  
  public static final boolean isARM() {
    return (ARCH.startsWith("arm") || ARCH.startsWith("aarch"));
  }
  
  public static final boolean isSPARC() {
    return ARCH.startsWith("sparc");
  }
  
  public static final boolean isMIPS() {
    return (ARCH.equals("mips") || ARCH.equals("mips64") || ARCH.equals("mipsel") || ARCH.equals("mips64el"));
  }
  
  public static final boolean isLoongArch() {
    return ARCH.startsWith("loongarch");
  }
  
  static String getCanonicalArchitecture(String paramString, int paramInt) {
    paramString = paramString.toLowerCase().trim();
    if ("powerpc".equals(paramString)) {
      paramString = "ppc";
    } else if ("powerpc64".equals(paramString)) {
      paramString = "ppc64";
    } else if ("i386".equals(paramString) || "i686".equals(paramString)) {
      paramString = "x86";
    } else if ("x86_64".equals(paramString) || "amd64".equals(paramString)) {
      paramString = "x86-64";
    } else if ("zarch_64".equals(paramString)) {
      paramString = "s390x";
    } 
    if ("ppc64".equals(paramString) && "little".equals(System.getProperty("sun.cpu.endian")))
      paramString = "ppc64le"; 
    if ("arm".equals(paramString) && paramInt == 1 && isSoftFloat())
      paramString = "armel"; 
    return paramString;
  }
  
  static boolean isSoftFloat() {
    try {
      File file = new File("/proc/self/exe");
      if (file.exists()) {
        ELFAnalyser eLFAnalyser = ELFAnalyser.analyse(file.getCanonicalPath());
        return !eLFAnalyser.isArmHardFloat();
      } 
    } catch (IOException iOException) {
      Logger.getLogger(Platform.class.getName()).log(Level.INFO, "Failed to read '/proc/self/exe' or the target binary.", iOException);
    } catch (SecurityException securityException) {
      Logger.getLogger(Platform.class.getName()).log(Level.INFO, "SecurityException while analysing '/proc/self/exe' or the target binary.", securityException);
    } 
    return false;
  }
  
  static String getNativeLibraryResourcePrefix() {
    String str = System.getProperty("jna.prefix");
    return (str != null) ? str : getNativeLibraryResourcePrefix(getOSType(), System.getProperty("os.arch"), System.getProperty("os.name"));
  }
  
  static String getNativeLibraryResourcePrefix(int paramInt, String paramString1, String paramString2) {
    paramString1 = getCanonicalArchitecture(paramString1, paramInt);
    switch (paramInt) {
      case 8:
        if (paramString1.startsWith("arm"))
          paramString1 = "arm"; 
        return "android-" + paramString1;
      case 2:
        return "win32-" + paramString1;
      case 6:
        return "w32ce-" + paramString1;
      case 0:
        return "darwin-" + paramString1;
      case 1:
        return "linux-" + paramString1;
      case 3:
        return "sunos-" + paramString1;
      case 12:
        return "dragonflybsd-" + paramString1;
      case 4:
        return "freebsd-" + paramString1;
      case 5:
        return "openbsd-" + paramString1;
      case 11:
        return "netbsd-" + paramString1;
      case 10:
        return "kfreebsd-" + paramString1;
    } 
    null = paramString2.toLowerCase();
    int i = null.indexOf(" ");
    if (i != -1)
      null = null.substring(0, i); 
    return null + "-" + paramString1;
  }
  
  static {
    HAS_BUFFERS = bool;
    RO_FIELDS = (osType != 6);
    C_LIBRARY_NAME = (osType == 2) ? "msvcrt" : ((osType == 6) ? "coredll" : "c");
    MATH_LIBRARY_NAME = (osType == 2) ? "msvcrt" : ((osType == 6) ? "coredll" : "m");
    ARCH = getCanonicalArchitecture(System.getProperty("os.arch"), osType);
    HAS_DLL_CALLBACKS = (osType == 2 && !ARCH.startsWith("aarch"));
    RESOURCE_PREFIX = getNativeLibraryResourcePrefix();
  }
  
  static {
    String str = System.getProperty("os.name");
    if (str.startsWith("Linux")) {
      if ("dalvik".equals(System.getProperty("java.vm.name").toLowerCase())) {
        osType = 8;
        System.setProperty("jna.nounpack", "true");
      } else {
        osType = 1;
      } 
    } else if (str.startsWith("AIX")) {
      osType = 7;
    } else if (str.startsWith("Mac") || str.startsWith("Darwin")) {
      osType = 0;
    } else if (str.startsWith("Windows CE")) {
      osType = 6;
    } else if (str.startsWith("Windows")) {
      osType = 2;
    } else if (str.startsWith("Solaris") || str.startsWith("SunOS")) {
      osType = 3;
    } else if (str.startsWith("FreeBSD")) {
      osType = 4;
    } else if (str.startsWith("OpenBSD")) {
      osType = 5;
    } else if (str.equalsIgnoreCase("gnu")) {
      osType = 9;
    } else if (str.equalsIgnoreCase("gnu/kfreebsd")) {
      osType = 10;
    } else if (str.equalsIgnoreCase("netbsd")) {
      osType = 11;
    } else if (str.equalsIgnoreCase("dragonflybsd")) {
      osType = 12;
    } else {
      osType = -1;
    } 
    boolean bool = false;
    try {
      Class.forName("java.nio.Buffer");
      bool = true;
    } catch (ClassNotFoundException classNotFoundException) {}
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\Platform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */