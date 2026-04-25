package com.sun.jna;

import com.sun.jna.internal.Cleaner;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NativeLibrary implements Closeable {
  private static final Logger LOG = Logger.getLogger(NativeLibrary.class.getName());
  
  private static final Level DEBUG_LOAD_LEVEL = Native.DEBUG_LOAD ? Level.INFO : Level.FINE;
  
  private static final SymbolProvider NATIVE_SYMBOL_PROVIDER = new SymbolProvider() {
      public long getSymbolAddress(long param1Long, String param1String, SymbolProvider param1SymbolProvider) {
        return Native.findSymbol(param1Long, param1String);
      }
    };
  
  private final Cleaner.Cleanable cleanable;
  
  private volatile long handle;
  
  private final String libraryName;
  
  private final String libraryPath;
  
  private final Map<String, Function> functions = new HashMap<>();
  
  private final SymbolProvider symbolProvider;
  
  private final int callFlags;
  
  private final String encoding;
  
  private final Map<String, ?> options;
  
  private static final Map<String, Reference<NativeLibrary>> libraries = new HashMap<>();
  
  private static final Map<String, List<String>> searchPaths = new ConcurrentHashMap<>();
  
  private static final LinkedHashSet<String> librarySearchPath = new LinkedHashSet<>();
  
  private static final int DEFAULT_OPEN_OPTIONS = -1;
  
  private static Method addSuppressedMethod = null;
  
  private static String functionKey(String paramString1, int paramInt, String paramString2) {
    return paramString1 + "|" + paramInt + "|" + paramString2;
  }
  
  private NativeLibrary(String paramString1, String paramString2, long paramLong, Map<String, ?> paramMap) {
    this.libraryName = getLibraryName(paramString1);
    this.libraryPath = paramString2;
    this.handle = paramLong;
    this.cleanable = Cleaner.getCleaner().register(this, new NativeLibraryDisposer(paramLong));
    Object object = paramMap.get("calling-convention");
    boolean bool = (object instanceof Number) ? ((Number)object).intValue() : false;
    this.callFlags = bool;
    this.options = paramMap;
    SymbolProvider symbolProvider = (SymbolProvider)paramMap.get("symbol-provider");
    if (symbolProvider == null) {
      this.symbolProvider = NATIVE_SYMBOL_PROVIDER;
    } else {
      this.symbolProvider = symbolProvider;
    } 
    String str = (String)paramMap.get("string-encoding");
    if (str == null)
      str = Native.getDefaultStringEncoding(); 
    this.encoding = str;
    if (Platform.isWindows() && "kernel32".equals(this.libraryName.toLowerCase()))
      synchronized (this.functions) {
        Function function = new Function(this, "GetLastError", 63, this.encoding) {
            Object invoke(Object[] param1ArrayOfObject, Class<?> param1Class, boolean param1Boolean, int param1Int) {
              return Integer.valueOf(Native.getLastError());
            }
            
            Object invoke(Method param1Method, Class<?>[] param1ArrayOfClass, Class<?> param1Class, Object[] param1ArrayOfObject, Map<String, ?> param1Map) {
              return Integer.valueOf(Native.getLastError());
            }
          };
        this.functions.put(functionKey("GetLastError", this.callFlags, this.encoding), function);
      }  
  }
  
  private static int openFlags(Map<String, ?> paramMap) {
    Object object = paramMap.get("open-flags");
    return (object instanceof Number) ? ((Number)object).intValue() : -1;
  }
  
  private static NativeLibrary loadLibrary(String paramString, Map<String, ?> paramMap) {
    LOG.log(DEBUG_LOAD_LEVEL, "Looking for library '" + paramString + "'");
    ArrayList<UnsatisfiedLinkError> arrayList = new ArrayList();
    boolean bool = (new File(paramString)).isAbsolute();
    LinkedHashSet<String> linkedHashSet = new LinkedHashSet();
    int i = openFlags(paramMap);
    List list = searchPaths.get(paramString);
    if (list != null)
      synchronized (list) {
        linkedHashSet.addAll(list);
      }  
    String str1 = Native.getWebStartLibraryPath(paramString);
    if (str1 != null) {
      LOG.log(DEBUG_LOAD_LEVEL, "Adding web start path " + str1);
      linkedHashSet.add(str1);
    } 
    LOG.log(DEBUG_LOAD_LEVEL, "Adding paths from jna.library.path: " + System.getProperty("jna.library.path"));
    linkedHashSet.addAll(initPaths("jna.library.path"));
    String str2 = findLibraryPath(paramString, linkedHashSet);
    long l = 0L;
    try {
      LOG.log(DEBUG_LOAD_LEVEL, "Trying " + str2);
      l = Native.open(str2, i);
    } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
      LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + unsatisfiedLinkError.getMessage());
      LOG.log(DEBUG_LOAD_LEVEL, "Adding system paths: " + librarySearchPath);
      arrayList.add(unsatisfiedLinkError);
      linkedHashSet.addAll(librarySearchPath);
    } 
    try {
      if (l == 0L) {
        str2 = findLibraryPath(paramString, linkedHashSet);
        LOG.log(DEBUG_LOAD_LEVEL, "Trying " + str2);
        l = Native.open(str2, i);
        if (l == 0L)
          throw new UnsatisfiedLinkError("Failed to load library '" + paramString + "'"); 
      } 
    } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
      LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + unsatisfiedLinkError.getMessage());
      arrayList.add(unsatisfiedLinkError);
      if (Platform.isAndroid()) {
        try {
          LOG.log(DEBUG_LOAD_LEVEL, "Preload (via System.loadLibrary) " + paramString);
          System.loadLibrary(paramString);
          l = Native.open(str2, i);
        } catch (UnsatisfiedLinkError unsatisfiedLinkError1) {
          LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + unsatisfiedLinkError1.getMessage());
          arrayList.add(unsatisfiedLinkError1);
        } 
      } else if (Platform.isLinux() || Platform.isFreeBSD()) {
        LOG.log(DEBUG_LOAD_LEVEL, "Looking for version variants");
        str2 = matchLibrary(paramString, linkedHashSet);
        if (str2 != null) {
          LOG.log(DEBUG_LOAD_LEVEL, "Trying " + str2);
          try {
            l = Native.open(str2, i);
          } catch (UnsatisfiedLinkError unsatisfiedLinkError1) {
            LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + unsatisfiedLinkError1.getMessage());
            arrayList.add(unsatisfiedLinkError1);
          } 
        } 
      } else if (Platform.isMac() && !paramString.endsWith(".dylib")) {
        String[] arrayOfString = matchFramework(paramString);
        int j = arrayOfString.length;
        byte b = 0;
        while (b < j) {
          String str = arrayOfString[b];
          try {
            LOG.log(DEBUG_LOAD_LEVEL, "Trying " + str);
            l = Native.open(str, i);
            break;
          } catch (UnsatisfiedLinkError unsatisfiedLinkError1) {
            LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + unsatisfiedLinkError1.getMessage());
            arrayList.add(unsatisfiedLinkError1);
            b++;
          } 
        } 
      } else if (Platform.isWindows() && !bool) {
        LOG.log(DEBUG_LOAD_LEVEL, "Looking for lib- prefix");
        str2 = findLibraryPath("lib" + paramString, linkedHashSet);
        if (str2 != null) {
          LOG.log(DEBUG_LOAD_LEVEL, "Trying " + str2);
          try {
            l = Native.open(str2, i);
          } catch (UnsatisfiedLinkError unsatisfiedLinkError1) {
            LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + unsatisfiedLinkError1.getMessage());
            arrayList.add(unsatisfiedLinkError1);
          } 
        } 
      } 
      if (l == 0L)
        try {
          File file = Native.extractFromResourcePath(paramString, (ClassLoader)paramMap.get("classloader"));
          if (file != null)
            try {
              l = Native.open(file.getAbsolutePath(), i);
              str2 = file.getAbsolutePath();
            } finally {
              if (Native.isUnpacked(file))
                Native.deleteLibrary(file); 
            }  
        } catch (IOException iOException) {
          LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + iOException.getMessage());
          arrayList.add(iOException);
        }  
      if (l == 0L) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unable to load library '");
        stringBuilder.append(paramString);
        stringBuilder.append("':");
        for (Throwable throwable : arrayList) {
          stringBuilder.append("\n");
          stringBuilder.append(throwable.getMessage());
        } 
        UnsatisfiedLinkError unsatisfiedLinkError1 = new UnsatisfiedLinkError(stringBuilder.toString());
        for (Throwable throwable : arrayList)
          addSuppressedReflected(unsatisfiedLinkError1, throwable); 
        throw unsatisfiedLinkError1;
      } 
    } 
    LOG.log(DEBUG_LOAD_LEVEL, "Found library '" + paramString + "' at " + str2);
    return new NativeLibrary(paramString, str2, l, paramMap);
  }
  
  private static void addSuppressedReflected(Throwable paramThrowable1, Throwable paramThrowable2) {
    if (addSuppressedMethod == null)
      return; 
    try {
      addSuppressedMethod.invoke(paramThrowable1, new Object[] { paramThrowable2 });
    } catch (IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException illegalAccessException) {
      throw new RuntimeException("Failed to call addSuppressedMethod", illegalAccessException);
    } 
  }
  
  static String[] matchFramework(String paramString) {
    LinkedHashSet<String> linkedHashSet = new LinkedHashSet();
    File file = new File(paramString);
    if (file.isAbsolute()) {
      if (paramString.contains(".framework")) {
        if (file.exists())
          return new String[] { file.getAbsolutePath() }; 
        linkedHashSet.add(file.getAbsolutePath());
      } else {
        file = new File(new File(file.getParentFile(), file.getName() + ".framework"), file.getName());
        if (file.exists())
          return new String[] { file.getAbsolutePath() }; 
        linkedHashSet.add(file.getAbsolutePath());
      } 
    } else {
      String[] arrayOfString = { System.getProperty("user.home"), "", "/System" };
      String str = !paramString.contains(".framework") ? (paramString + ".framework/" + paramString) : paramString;
      for (String str1 : arrayOfString) {
        file = new File(str1 + "/Library/Frameworks/" + str);
        if (file.exists())
          return new String[] { file.getAbsolutePath() }; 
        linkedHashSet.add(file.getAbsolutePath());
      } 
    } 
    return linkedHashSet.<String>toArray(new String[0]);
  }
  
  private String getLibraryName(String paramString) {
    String str1 = paramString;
    String str2 = "---";
    String str3 = mapSharedLibraryName("---");
    int i = str3.indexOf("---");
    if (i > 0 && str1.startsWith(str3.substring(0, i)))
      str1 = str1.substring(i); 
    String str4 = str3.substring(i + "---".length());
    int j = str1.indexOf(str4);
    if (j != -1)
      str1 = str1.substring(0, j); 
    return str1;
  }
  
  public static final NativeLibrary getInstance(String paramString) {
    return getInstance(paramString, Collections.emptyMap());
  }
  
  public static final NativeLibrary getInstance(String paramString, ClassLoader paramClassLoader) {
    return getInstance(paramString, Collections.singletonMap("classloader", paramClassLoader));
  }
  
  public static final NativeLibrary getInstance(String paramString, Map<String, ?> paramMap) {
    HashMap<String, Object> hashMap = new HashMap<>(paramMap);
    if (hashMap.get("calling-convention") == null)
      hashMap.put("calling-convention", Integer.valueOf(0)); 
    if ((Platform.isLinux() || Platform.isFreeBSD() || Platform.isAIX()) && Platform.C_LIBRARY_NAME.equals(paramString))
      paramString = null; 
    synchronized (libraries) {
      Reference<NativeLibrary> reference = libraries.get(paramString + hashMap);
      NativeLibrary nativeLibrary = (reference != null) ? reference.get() : null;
      if (nativeLibrary == null) {
        if (paramString == null) {
          nativeLibrary = new NativeLibrary("<process>", null, Native.open(null, openFlags(hashMap)), hashMap);
        } else {
          nativeLibrary = loadLibrary(paramString, hashMap);
        } 
        reference = new WeakReference<>(nativeLibrary);
        libraries.put(nativeLibrary.getName() + hashMap, reference);
        File file = nativeLibrary.getFile();
        if (file != null) {
          libraries.put(file.getAbsolutePath() + hashMap, reference);
          libraries.put(file.getName() + hashMap, reference);
        } 
      } 
      return nativeLibrary;
    } 
  }
  
  public static final synchronized NativeLibrary getProcess() {
    return getInstance(null);
  }
  
  public static final synchronized NativeLibrary getProcess(Map<String, ?> paramMap) {
    return getInstance((String)null, paramMap);
  }
  
  public static final void addSearchPath(String paramString1, String paramString2) {
    List<?> list = searchPaths.get(paramString1);
    if (list == null) {
      list = Collections.synchronizedList(new ArrayList());
      searchPaths.put(paramString1, list);
    } 
    list.add(paramString2);
  }
  
  public Function getFunction(String paramString) {
    return getFunction(paramString, this.callFlags);
  }
  
  Function getFunction(String paramString, Method paramMethod) {
    FunctionMapper functionMapper = (FunctionMapper)this.options.get("function-mapper");
    if (functionMapper != null)
      paramString = functionMapper.getFunctionName(this, paramMethod); 
    String str = System.getProperty("jna.profiler.prefix", "$$YJP$$");
    if (paramString.startsWith(str))
      paramString = paramString.substring(str.length()); 
    int i = this.callFlags;
    Class[] arrayOfClass = paramMethod.getExceptionTypes();
    for (byte b = 0; b < arrayOfClass.length; b++) {
      if (LastErrorException.class.isAssignableFrom(arrayOfClass[b]))
        i |= 0x40; 
    } 
    return getFunction(paramString, i);
  }
  
  public Function getFunction(String paramString, int paramInt) {
    return getFunction(paramString, paramInt, this.encoding);
  }
  
  public Function getFunction(String paramString1, int paramInt, String paramString2) {
    if (paramString1 == null)
      throw new NullPointerException("Function name may not be null"); 
    synchronized (this.functions) {
      String str = functionKey(paramString1, paramInt, paramString2);
      Function function = this.functions.get(str);
      if (function == null) {
        function = new Function(this, paramString1, paramInt, paramString2);
        this.functions.put(str, function);
      } 
      return function;
    } 
  }
  
  public Map<String, ?> getOptions() {
    return this.options;
  }
  
  public Pointer getGlobalVariableAddress(String paramString) {
    try {
      return new Pointer(getSymbolAddress(paramString));
    } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
      throw new UnsatisfiedLinkError("Error looking up '" + paramString + "': " + unsatisfiedLinkError.getMessage());
    } 
  }
  
  long getSymbolAddress(String paramString) {
    if (this.handle == 0L)
      throw new UnsatisfiedLinkError("Library has been unloaded"); 
    return this.symbolProvider.getSymbolAddress(this.handle, paramString, NATIVE_SYMBOL_PROVIDER);
  }
  
  public String toString() {
    return "Native Library <" + this.libraryPath + "@" + this.handle + ">";
  }
  
  public String getName() {
    return this.libraryName;
  }
  
  public File getFile() {
    return (this.libraryPath == null) ? null : new File(this.libraryPath);
  }
  
  static void disposeAll() {
    LinkedHashSet linkedHashSet;
    synchronized (libraries) {
      linkedHashSet = new LinkedHashSet(libraries.values());
    } 
    for (Reference<NativeLibrary> reference : (Iterable<Reference<NativeLibrary>>)linkedHashSet) {
      NativeLibrary nativeLibrary = reference.get();
      if (nativeLibrary != null)
        nativeLibrary.close(); 
    } 
  }
  
  public void close() {
    HashSet<String> hashSet = new HashSet();
    synchronized (libraries) {
      for (Map.Entry<String, Reference<NativeLibrary>> entry : libraries.entrySet()) {
        Reference<NativeLibrary> reference = (Reference)entry.getValue();
        if (reference.get() == this)
          hashSet.add((String)entry.getKey()); 
      } 
      for (String str : hashSet)
        libraries.remove(str); 
    } 
    synchronized (this) {
      if (this.handle != 0L) {
        this.handle = 0L;
        this.cleanable.clean();
      } 
    } 
  }
  
  @Deprecated
  public void dispose() {
    close();
  }
  
  private static List<String> initPaths(String paramString) {
    String str = System.getProperty(paramString, "");
    if ("".equals(str))
      return Collections.emptyList(); 
    StringTokenizer stringTokenizer = new StringTokenizer(str, File.pathSeparator);
    ArrayList<String> arrayList = new ArrayList();
    while (stringTokenizer.hasMoreTokens()) {
      String str1 = stringTokenizer.nextToken();
      if (!"".equals(str1))
        arrayList.add(str1); 
    } 
    return arrayList;
  }
  
  private static String findLibraryPath(String paramString, Collection<String> paramCollection) {
    if ((new File(paramString)).isAbsolute())
      return paramString; 
    String str = mapSharedLibraryName(paramString);
    for (String str1 : paramCollection) {
      File file = new File(str1, str);
      if (file.exists())
        return file.getAbsolutePath(); 
      if (Platform.isMac() && str.endsWith(".dylib")) {
        file = new File(str1, str.substring(0, str.lastIndexOf(".dylib")) + ".jnilib");
        if (file.exists())
          return file.getAbsolutePath(); 
      } 
    } 
    return str;
  }
  
  static String mapSharedLibraryName(String paramString) {
    if (Platform.isMac()) {
      if (paramString.startsWith("lib") && (paramString.endsWith(".dylib") || paramString.endsWith(".jnilib")))
        return paramString; 
      String str1 = System.mapLibraryName(paramString);
      return str1.endsWith(".jnilib") ? (str1.substring(0, str1.lastIndexOf(".jnilib")) + ".dylib") : str1;
    } 
    if (Platform.isLinux() || Platform.isFreeBSD()) {
      if (isVersionedName(paramString) || paramString.endsWith(".so"))
        return paramString; 
    } else if (Platform.isAIX()) {
      if (isVersionedName(paramString) || paramString.endsWith(".so") || paramString.startsWith("lib") || paramString.endsWith(".a"))
        return paramString; 
    } else if (Platform.isWindows() && (paramString.endsWith(".drv") || paramString.endsWith(".dll") || paramString.endsWith(".ocx"))) {
      return paramString;
    } 
    String str = System.mapLibraryName(paramString);
    return (Platform.isAIX() && str.endsWith(".so")) ? str.replaceAll(".so$", ".a") : str;
  }
  
  private static boolean isVersionedName(String paramString) {
    if (paramString.startsWith("lib")) {
      int i = paramString.lastIndexOf(".so.");
      if (i != -1 && i + 4 < paramString.length()) {
        for (int j = i + 4; j < paramString.length(); j++) {
          char c = paramString.charAt(j);
          if (!Character.isDigit(c) && c != '.')
            return false; 
        } 
        return true;
      } 
    } 
    return false;
  }
  
  static String matchLibrary(final String libName, Collection<String> paramCollection) {
    File file = new File(libName);
    if (file.isAbsolute())
      paramCollection = Arrays.asList(new String[] { file.getParent() }); 
    FilenameFilter filenameFilter = new FilenameFilter() {
        public boolean accept(File param1File, String param1String) {
          return ((param1String.startsWith("lib" + libName + ".so") || (param1String.startsWith(libName + ".so") && libName.startsWith("lib"))) && NativeLibrary.isVersionedName(param1String));
        }
      };
    LinkedList linkedList = new LinkedList();
    for (String str1 : paramCollection) {
      File[] arrayOfFile = (new File(str1)).listFiles(filenameFilter);
      if (arrayOfFile != null && arrayOfFile.length > 0)
        linkedList.addAll(Arrays.asList(arrayOfFile)); 
    } 
    double d = -1.0D;
    String str = null;
    for (File file1 : linkedList) {
      String str1 = file1.getAbsolutePath();
      String str2 = str1.substring(str1.lastIndexOf(".so.") + 4);
      double d1 = parseVersion(str2);
      if (d1 > d) {
        d = d1;
        str = str1;
      } 
    } 
    return str;
  }
  
  static double parseVersion(String paramString) {
    double d1 = 0.0D;
    double d2 = 1.0D;
    int i = paramString.indexOf(".");
    while (paramString != null) {
      String str;
      if (i != -1) {
        str = paramString.substring(0, i);
        paramString = paramString.substring(i + 1);
        i = paramString.indexOf(".");
      } else {
        str = paramString;
        paramString = null;
      } 
      try {
        d1 += Integer.parseInt(str) / d2;
      } catch (NumberFormatException numberFormatException) {
        return 0.0D;
      } 
      d2 *= 100.0D;
    } 
    return d1;
  }
  
  private static String getMultiArchPath() {
    String str1 = Platform.ARCH;
    String str2 = Platform.iskFreeBSD() ? "-kfreebsd" : (Platform.isGNU() ? "" : "-linux");
    String str3 = "-gnu";
    if (Platform.isIntel()) {
      str1 = Platform.is64Bit() ? "x86_64" : "i386";
    } else if (Platform.isPPC()) {
      str1 = Platform.is64Bit() ? "powerpc64" : "powerpc";
    } else if (Platform.isARM()) {
      str1 = "arm";
      str3 = "-gnueabi";
    } else if (Platform.ARCH.equals("mips64el")) {
      str3 = "-gnuabi64";
    } 
    return str1 + str2 + str3;
  }
  
  private static ArrayList<String> getLinuxLdPaths() {
    ArrayList<String> arrayList = new ArrayList();
    Process process = null;
    BufferedReader bufferedReader = null;
    try {
      process = Runtime.getRuntime().exec("/sbin/ldconfig -p");
      bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String str;
      while ((str = bufferedReader.readLine()) != null) {
        int i = str.indexOf(" => ");
        int j = str.lastIndexOf('/');
        if (i != -1 && j != -1 && i < j) {
          String str1 = str.substring(i + 4, j);
          if (!arrayList.contains(str1))
            arrayList.add(str1); 
        } 
      } 
    } catch (Exception exception) {
    
    } finally {
      if (bufferedReader != null)
        try {
          bufferedReader.close();
        } catch (IOException iOException) {} 
      if (process != null)
        try {
          process.waitFor();
        } catch (InterruptedException interruptedException) {} 
    } 
    return arrayList;
  }
  
  static {
    try {
      addSuppressedMethod = Throwable.class.getMethod("addSuppressed", new Class[] { Throwable.class });
    } catch (NoSuchMethodException noSuchMethodException) {
    
    } catch (SecurityException securityException) {
      Logger.getLogger(NativeLibrary.class.getName()).log(Level.SEVERE, "Failed to initialize 'addSuppressed' method", securityException);
    } 
    String str = Native.getWebStartLibraryPath("jnidispatch");
    if (str != null)
      librarySearchPath.add(str); 
    if (System.getProperty("jna.platform.library.path") == null && !Platform.isWindows()) {
      String str1 = "";
      String str2 = "";
      String str3 = "";
      if (Platform.isLinux() || Platform.isSolaris() || Platform.isFreeBSD() || Platform.iskFreeBSD())
        str3 = (Platform.isSolaris() ? "/" : "") + (Native.POINTER_SIZE * 8); 
      String[] arrayOfString = { "/usr/lib" + str3, "/lib" + str3, "/usr/lib", "/lib" };
      if (Platform.isLinux() || Platform.iskFreeBSD() || Platform.isGNU()) {
        String str4 = getMultiArchPath();
        arrayOfString = new String[] { "/usr/lib/" + str4, "/lib/" + str4, "/usr/lib" + str3, "/lib" + str3, "/usr/lib", "/lib" };
      } 
      if (Platform.isLinux()) {
        ArrayList<String> arrayList = getLinuxLdPaths();
        for (int i = arrayOfString.length - 1; 0 <= i; i--) {
          int j = arrayList.indexOf(arrayOfString[i]);
          if (j != -1)
            arrayList.remove(j); 
          arrayList.add(0, arrayOfString[i]);
        } 
        arrayOfString = arrayList.<String>toArray(new String[0]);
      } 
      for (byte b = 0; b < arrayOfString.length; b++) {
        File file = new File(arrayOfString[b]);
        if (file.exists() && file.isDirectory()) {
          str1 = str1 + str2 + arrayOfString[b];
          str2 = File.pathSeparator;
        } 
      } 
      if (!"".equals(str1))
        System.setProperty("jna.platform.library.path", str1); 
    } 
    librarySearchPath.addAll(initPaths("jna.platform.library.path"));
  }
  
  static {
    if (Native.POINTER_SIZE == 0)
      throw new Error("Native library not initialized"); 
  }
  
  private static final class NativeLibraryDisposer implements Runnable {
    private long handle;
    
    public NativeLibraryDisposer(long param1Long) {
      this.handle = param1Long;
    }
    
    public synchronized void run() {
      if (this.handle != 0L)
        try {
          Native.close(this.handle);
        } finally {
          this.handle = 0L;
        }  
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\NativeLibrary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */