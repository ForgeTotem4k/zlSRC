package pro.gravit.utils.helper;

import java.lang.invoke.MethodHandles;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Locale;

public final class JVMHelper {
  public static final RuntimeMXBean RUNTIME_MXBEAN = ManagementFactory.getRuntimeMXBean();
  
  public static final OperatingSystemMXBean OPERATING_SYSTEM_MXBEAN = ManagementFactory.getOperatingSystemMXBean();
  
  public static final OS OS_TYPE = OS.byName(OPERATING_SYSTEM_MXBEAN.getName());
  
  public static final int OS_BITS = getCorrectOSArch();
  
  public static final String OS_VERSION = OPERATING_SYSTEM_MXBEAN.getVersion();
  
  public static final ARCH ARCH_TYPE = getArch(System.getProperty("os.arch"));
  
  public static final String NATIVE_EXTENSION = getNativeExtension(OS_TYPE);
  
  public static final String NATIVE_PREFIX = getNativePrefix(OS_TYPE);
  
  public static final int JVM_BITS = Integer.parseInt(System.getProperty("sun.arch.data.model"));
  
  public static final Runtime RUNTIME = Runtime.getRuntime();
  
  public static final ClassLoader LOADER = ClassLoader.getSystemClassLoader();
  
  public static final int JVM_VERSION = getVersion();
  
  public static final int JVM_BUILD = getBuild();
  
  public static ARCH getArch(String paramString) {
    if (paramString.equals("amd64") || paramString.equals("x86-64") || paramString.equals("x86_64"))
      return ARCH.X86_64; 
    if (paramString.equals("i386") || paramString.equals("i586") || paramString.equals("i686") || paramString.equals("x86"))
      return ARCH.X86; 
    if (paramString.startsWith("armv8") || paramString.startsWith("aarch64"))
      return ARCH.ARM64; 
    if (paramString.startsWith("arm") || paramString.startsWith("aarch32"))
      return ARCH.ARM32; 
    throw new InternalError(String.format("Unsupported arch '%s'", new Object[] { paramString }));
  }
  
  public static int getVersion() {
    return Runtime.version().feature();
  }
  
  public static int getBuild() {
    return Runtime.version().update();
  }
  
  public static String getNativeExtension(OS paramOS) {
    switch (paramOS.ordinal()) {
      default:
        throw new IncompatibleClassChangeError();
      case 0:
      
      case 1:
      
      case 2:
        break;
    } 
    return ".dylib";
  }
  
  public static String getNativePrefix(OS paramOS) {
    switch (paramOS.ordinal()) {
      case 1:
      case 2:
      
    } 
    return "";
  }
  
  public static void fullGC() {
    RUNTIME.gc();
    LogHelper.debug("Used heap: %d MiB", new Object[] { Long.valueOf(RUNTIME.totalMemory() - RUNTIME.freeMemory() >> 20L) });
  }
  
  public static X509Certificate[] getCertificates(Class<?> paramClass) {
    Object[] arrayOfObject = paramClass.getSigners();
    return (arrayOfObject == null) ? null : (X509Certificate[])Arrays.<Object>stream(arrayOfObject).filter(paramObject -> paramObject instanceof X509Certificate).map(paramObject -> (X509Certificate)paramObject).toArray(paramInt -> new X509Certificate[paramInt]);
  }
  
  public static void checkStackTrace(Class<?> paramClass) {
    LogHelper.debug("Testing stacktrace");
    Exception exception = new Exception("Testing stacktrace");
    StackTraceElement[] arrayOfStackTraceElement = exception.getStackTrace();
    if (!arrayOfStackTraceElement[arrayOfStackTraceElement.length - 1].getClassName().equals(paramClass.getName()))
      throw new SecurityException(String.format("Invalid StackTraceElement: %s", new Object[] { arrayOfStackTraceElement[arrayOfStackTraceElement.length - 1].getClassName() })); 
  }
  
  private static int getCorrectOSArch() {
    return (OS_TYPE == OS.MUSTDIE) ? ((System.getenv("ProgramFiles(x86)") == null) ? 32 : 64) : (System.getProperty("os.arch").contains("64") ? 64 : 32);
  }
  
  public static boolean isJVMMatchesSystemArch() {
    return (JVM_BITS == OS_BITS);
  }
  
  public static String jvmProperty(String paramString1, String paramString2) {
    return String.format("-D%s=%s", new Object[] { paramString1, paramString2 });
  }
  
  public static void verifySystemProperties(Class<?> paramClass, boolean paramBoolean) {
    Locale.setDefault(Locale.US);
    LogHelper.debug("Verifying class loader");
    if (paramBoolean && !paramClass.getClassLoader().equals(LOADER))
      throw new SecurityException("ClassLoader should be system"); 
    LogHelper.debug("Verifying JVM architecture");
  }
  
  static {
    try {
      MethodHandles.publicLookup();
    } catch (Throwable throwable) {
      throw new InternalError(throwable);
    } 
  }
  
  public enum ARCH {
    X86("x86"),
    X86_64("x86-64"),
    ARM64("arm64"),
    ARM32("arm32");
    
    public final String name;
    
    ARCH(String param1String1) {
      this.name = param1String1;
    }
  }
  
  public enum OS {
    MUSTDIE("mustdie"),
    LINUX("linux"),
    MACOSX("macosx");
    
    public final String name;
    
    OS(String param1String1) {
      this.name = param1String1;
    }
    
    public static OS byName(String param1String) {
      if (param1String.startsWith("Windows"))
        return MUSTDIE; 
      if (param1String.startsWith("Linux"))
        return LINUX; 
      if (param1String.startsWith("Mac OS X"))
        return MACOSX; 
      throw new RuntimeException(String.format("This shit is not yet supported: '%s'", new Object[] { param1String }));
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\JVMHelper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */