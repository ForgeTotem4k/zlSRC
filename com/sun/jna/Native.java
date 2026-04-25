package com.sun.jna;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Window;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Native implements Version {
  private static final Logger LOG = Logger.getLogger(Native.class.getName());
  
  public static final Charset DEFAULT_CHARSET;
  
  public static final String DEFAULT_ENCODING;
  
  public static final boolean DEBUG_LOAD = Boolean.getBoolean("jna.debug_load");
  
  public static final boolean DEBUG_JNA_LOAD = Boolean.getBoolean("jna.debug_load.jna");
  
  private static final Level DEBUG_JNA_LOAD_LEVEL = DEBUG_JNA_LOAD ? Level.INFO : Level.FINE;
  
  static String jnidispatchPath = null;
  
  private static final Map<Class<?>, Map<String, Object>> typeOptions = Collections.synchronizedMap(new WeakHashMap<>());
  
  private static final Map<Class<?>, Reference<?>> libraries = Collections.synchronizedMap(new WeakHashMap<>());
  
  private static final String _OPTION_ENCLOSING_LIBRARY = "enclosing-library";
  
  private static final Callback.UncaughtExceptionHandler DEFAULT_HANDLER = new Callback.UncaughtExceptionHandler() {
      public void uncaughtException(Callback param1Callback, Throwable param1Throwable) {
        Native.LOG.log(Level.WARNING, "JNA: Callback " + param1Callback + " threw the following exception", param1Throwable);
      }
    };
  
  private static Callback.UncaughtExceptionHandler callbackExceptionHandler = DEFAULT_HANDLER;
  
  public static final int POINTER_SIZE = sizeof(0);
  
  public static final int LONG_SIZE = sizeof(1);
  
  public static final int WCHAR_SIZE = sizeof(2);
  
  public static final int SIZE_T_SIZE = sizeof(3);
  
  public static final int BOOL_SIZE = sizeof(4);
  
  public static final int LONG_DOUBLE_SIZE = sizeof(5);
  
  private static final int TYPE_VOIDP = 0;
  
  private static final int TYPE_LONG = 1;
  
  private static final int TYPE_WCHAR_T = 2;
  
  private static final int TYPE_SIZE_T = 3;
  
  private static final int TYPE_BOOL = 4;
  
  private static final int TYPE_LONG_DOUBLE = 5;
  
  static final int MAX_ALIGNMENT = (Platform.isSPARC() || Platform.isWindows() || (Platform.isLinux() && (Platform.isARM() || Platform.isPPC() || Platform.isMIPS() || Platform.isLoongArch())) || Platform.isAIX() || (Platform.isAndroid() && !Platform.isIntel())) ? 8 : LONG_SIZE;
  
  static final int MAX_PADDING = (Platform.isMac() && Platform.isPPC()) ? 8 : MAX_ALIGNMENT;
  
  private static final Object finalizer = new Object() {
      protected void finalize() throws Throwable {
        Native.dispose();
        super.finalize();
      }
    };
  
  static final String JNA_TMPLIB_PREFIX = "jna";
  
  private static final Map<Class<?>, long[]> registeredClasses = (Map)new WeakHashMap<>();
  
  private static final Map<Class<?>, NativeLibrary> registeredLibraries = new WeakHashMap<>();
  
  static final int CB_HAS_INITIALIZER = 1;
  
  private static final int CVT_UNSUPPORTED = -1;
  
  private static final int CVT_DEFAULT = 0;
  
  private static final int CVT_POINTER = 1;
  
  private static final int CVT_STRING = 2;
  
  private static final int CVT_STRUCTURE = 3;
  
  private static final int CVT_STRUCTURE_BYVAL = 4;
  
  private static final int CVT_BUFFER = 5;
  
  private static final int CVT_ARRAY_BYTE = 6;
  
  private static final int CVT_ARRAY_SHORT = 7;
  
  private static final int CVT_ARRAY_CHAR = 8;
  
  private static final int CVT_ARRAY_INT = 9;
  
  private static final int CVT_ARRAY_LONG = 10;
  
  private static final int CVT_ARRAY_FLOAT = 11;
  
  private static final int CVT_ARRAY_DOUBLE = 12;
  
  private static final int CVT_ARRAY_BOOLEAN = 13;
  
  private static final int CVT_BOOLEAN = 14;
  
  private static final int CVT_CALLBACK = 15;
  
  private static final int CVT_FLOAT = 16;
  
  private static final int CVT_NATIVE_MAPPED = 17;
  
  private static final int CVT_NATIVE_MAPPED_STRING = 18;
  
  private static final int CVT_NATIVE_MAPPED_WSTRING = 19;
  
  private static final int CVT_WSTRING = 20;
  
  private static final int CVT_INTEGER_TYPE = 21;
  
  private static final int CVT_POINTER_TYPE = 22;
  
  private static final int CVT_TYPE_MAPPER = 23;
  
  private static final int CVT_TYPE_MAPPER_STRING = 24;
  
  private static final int CVT_TYPE_MAPPER_WSTRING = 25;
  
  private static final int CVT_OBJECT = 26;
  
  private static final int CVT_JNIENV = 27;
  
  private static final int CVT_SHORT = 28;
  
  private static final int CVT_BYTE = 29;
  
  static final int CB_OPTION_DIRECT = 1;
  
  static final int CB_OPTION_IN_DLL = 2;
  
  private static final ThreadLocal<Memory> nativeThreadTerminationFlag = new ThreadLocal<Memory>() {
      protected Memory initialValue() {
        Memory memory = new Memory(4L);
        memory.clear();
        return memory;
      }
    };
  
  private static final Map<Thread, Pointer> nativeThreads = Collections.synchronizedMap(new WeakHashMap<>());
  
  static boolean isCompatibleVersion(String paramString1, String paramString2) {
    String[] arrayOfString1 = paramString1.split("\\.");
    String[] arrayOfString2 = paramString2.split("\\.");
    if (arrayOfString1.length < 3 || arrayOfString2.length < 3)
      return false; 
    int i = Integer.parseInt(arrayOfString1[0]);
    int j = Integer.parseInt(arrayOfString2[0]);
    int k = Integer.parseInt(arrayOfString1[1]);
    int m = Integer.parseInt(arrayOfString2[1]);
    return (i != j) ? false : (!(k > m));
  }
  
  private static void dispose() {
    CallbackReference.disposeAll();
    Memory.disposeAll();
    NativeLibrary.disposeAll();
    unregisterAll();
    jnidispatchPath = null;
    System.setProperty("jna.loaded", "false");
  }
  
  static boolean deleteLibrary(File paramFile) {
    if (paramFile.delete())
      return true; 
    markTemporaryFile(paramFile);
    return false;
  }
  
  private static native void initIDs();
  
  public static synchronized native void setProtected(boolean paramBoolean);
  
  public static synchronized native boolean isProtected();
  
  public static long getWindowID(Window paramWindow) throws HeadlessException {
    return AWT.getWindowID(paramWindow);
  }
  
  public static long getComponentID(Component paramComponent) throws HeadlessException {
    return AWT.getComponentID(paramComponent);
  }
  
  public static Pointer getWindowPointer(Window paramWindow) throws HeadlessException {
    return new Pointer(AWT.getWindowID(paramWindow));
  }
  
  public static Pointer getComponentPointer(Component paramComponent) throws HeadlessException {
    return new Pointer(AWT.getComponentID(paramComponent));
  }
  
  static native long getWindowHandle0(Component paramComponent);
  
  public static Pointer getDirectBufferPointer(Buffer paramBuffer) {
    long l = _getDirectBufferPointer(paramBuffer);
    return (l == 0L) ? null : new Pointer(l);
  }
  
  private static native long _getDirectBufferPointer(Buffer paramBuffer);
  
  private static Charset getCharset(String paramString) {
    Charset charset = null;
    if (paramString != null)
      try {
        charset = Charset.forName(paramString);
      } catch (IllegalCharsetNameException|java.nio.charset.UnsupportedCharsetException illegalCharsetNameException) {
        LOG.log(Level.WARNING, "JNA Warning: Encoding ''{0}'' is unsupported ({1})", new Object[] { paramString, illegalCharsetNameException.getMessage() });
      }  
    if (charset == null) {
      LOG.log(Level.WARNING, "JNA Warning: Using fallback encoding {0}", DEFAULT_CHARSET);
      charset = DEFAULT_CHARSET;
    } 
    return charset;
  }
  
  public static String toString(byte[] paramArrayOfbyte) {
    return toString(paramArrayOfbyte, getDefaultStringEncoding());
  }
  
  public static String toString(byte[] paramArrayOfbyte, String paramString) {
    return toString(paramArrayOfbyte, getCharset(paramString));
  }
  
  public static String toString(byte[] paramArrayOfbyte, Charset paramCharset) {
    int i = paramArrayOfbyte.length;
    for (byte b = 0; b < i; b++) {
      if (paramArrayOfbyte[b] == 0) {
        i = b;
        break;
      } 
    } 
    return (i == 0) ? "" : new String(paramArrayOfbyte, 0, i, paramCharset);
  }
  
  public static String toString(char[] paramArrayOfchar) {
    int i = paramArrayOfchar.length;
    for (byte b = 0; b < i; b++) {
      if (paramArrayOfchar[b] == '\000') {
        i = b;
        break;
      } 
    } 
    return (i == 0) ? "" : new String(paramArrayOfchar, 0, i);
  }
  
  public static List<String> toStringList(char[] paramArrayOfchar) {
    return toStringList(paramArrayOfchar, 0, paramArrayOfchar.length);
  }
  
  public static List<String> toStringList(char[] paramArrayOfchar, int paramInt1, int paramInt2) {
    ArrayList<String> arrayList = new ArrayList();
    int i = paramInt1;
    int j = paramInt1 + paramInt2;
    for (int k = paramInt1; k < j; k++) {
      if (paramArrayOfchar[k] == '\000') {
        if (i == k)
          return arrayList; 
        String str = new String(paramArrayOfchar, i, k - i);
        arrayList.add(str);
        i = k + 1;
      } 
    } 
    if (i < j) {
      String str = new String(paramArrayOfchar, i, j - i);
      arrayList.add(str);
    } 
    return arrayList;
  }
  
  public static <T extends Library> T load(Class<T> paramClass) {
    return load((String)null, paramClass);
  }
  
  public static <T extends Library> T load(Class<T> paramClass, Map<String, ?> paramMap) {
    return load(null, paramClass, paramMap);
  }
  
  public static <T extends Library> T load(String paramString, Class<T> paramClass) {
    return load(paramString, paramClass, Collections.emptyMap());
  }
  
  public static <T extends Library> T load(String paramString, Class<T> paramClass, Map<String, ?> paramMap) {
    if (!Library.class.isAssignableFrom(paramClass))
      throw new IllegalArgumentException("Interface (" + paramClass.getSimpleName() + ") of library=" + paramString + " does not extend " + Library.class.getSimpleName()); 
    Library.Handler handler = new Library.Handler(paramString, paramClass, paramMap);
    ClassLoader classLoader = paramClass.getClassLoader();
    Object object = Proxy.newProxyInstance(classLoader, new Class[] { paramClass }, handler);
    cacheOptions(paramClass, paramMap, object);
    return paramClass.cast(object);
  }
  
  @Deprecated
  public static <T> T loadLibrary(Class<T> paramClass) {
    return loadLibrary((String)null, paramClass);
  }
  
  @Deprecated
  public static <T> T loadLibrary(Class<T> paramClass, Map<String, ?> paramMap) {
    return loadLibrary(null, paramClass, paramMap);
  }
  
  @Deprecated
  public static <T> T loadLibrary(String paramString, Class<T> paramClass) {
    return loadLibrary(paramString, paramClass, Collections.emptyMap());
  }
  
  @Deprecated
  public static <T> T loadLibrary(String paramString, Class<T> paramClass, Map<String, ?> paramMap) {
    if (!Library.class.isAssignableFrom(paramClass))
      throw new IllegalArgumentException("Interface (" + paramClass.getSimpleName() + ") of library=" + paramString + " does not extend " + Library.class.getSimpleName()); 
    Library.Handler handler = new Library.Handler(paramString, paramClass, paramMap);
    ClassLoader classLoader = paramClass.getClassLoader();
    Object object = Proxy.newProxyInstance(classLoader, new Class[] { paramClass }, handler);
    cacheOptions(paramClass, paramMap, object);
    return paramClass.cast(object);
  }
  
  private static void loadLibraryInstance(Class<?> paramClass) {
    if (paramClass != null && !libraries.containsKey(paramClass))
      try {
        Field[] arrayOfField = paramClass.getFields();
        for (byte b = 0; b < arrayOfField.length; b++) {
          Field field = arrayOfField[b];
          if (field.getType() == paramClass && Modifier.isStatic(field.getModifiers())) {
            field.setAccessible(true);
            libraries.put(paramClass, new WeakReference(field.get(null)));
            break;
          } 
        } 
      } catch (Exception exception) {
        throw new IllegalArgumentException("Could not access instance of " + paramClass + " (" + exception + ")");
      }  
  }
  
  static Class<?> findEnclosingLibraryClass(Class<?> paramClass) {
    if (paramClass == null)
      return null; 
    Map map = typeOptions.get(paramClass);
    if (map != null) {
      Class<?> clazz = (Class)map.get("enclosing-library");
      return (clazz != null) ? clazz : paramClass;
    } 
    if (Library.class.isAssignableFrom(paramClass))
      return paramClass; 
    if (Callback.class.isAssignableFrom(paramClass))
      paramClass = CallbackReference.findCallbackClass(paramClass); 
    Class<?> clazz1 = paramClass.getDeclaringClass();
    Class<?> clazz2 = findEnclosingLibraryClass(clazz1);
    return (clazz2 != null) ? clazz2 : findEnclosingLibraryClass(paramClass.getSuperclass());
  }
  
  public static Map<String, Object> getLibraryOptions(Class<?> paramClass) {
    Map<String, Object> map = typeOptions.get(paramClass);
    if (map != null)
      return map; 
    Class<?> clazz = findEnclosingLibraryClass(paramClass);
    if (clazz != null) {
      loadLibraryInstance(clazz);
    } else {
      clazz = paramClass;
    } 
    map = typeOptions.get(clazz);
    if (map != null) {
      typeOptions.put(paramClass, map);
      return map;
    } 
    try {
      Field field = clazz.getField("OPTIONS");
      field.setAccessible(true);
      map = (Map<String, Object>)field.get(null);
      if (map == null)
        throw new IllegalStateException("Null options field"); 
    } catch (NoSuchFieldException noSuchFieldException) {
      map = Collections.emptyMap();
    } catch (Exception exception) {
      throw new IllegalArgumentException("OPTIONS must be a public field of type java.util.Map (" + exception + "): " + clazz);
    } 
    map = new HashMap<>(map);
    if (!map.containsKey("type-mapper"))
      map.put("type-mapper", lookupField(clazz, "TYPE_MAPPER", TypeMapper.class)); 
    if (!map.containsKey("structure-alignment"))
      map.put("structure-alignment", lookupField(clazz, "STRUCTURE_ALIGNMENT", Integer.class)); 
    if (!map.containsKey("string-encoding"))
      map.put("string-encoding", lookupField(clazz, "STRING_ENCODING", String.class)); 
    map = cacheOptions(clazz, map, null);
    if (paramClass != clazz)
      typeOptions.put(paramClass, map); 
    return map;
  }
  
  private static Object lookupField(Class<?> paramClass1, String paramString, Class<?> paramClass2) {
    try {
      Field field = paramClass1.getField(paramString);
      field.setAccessible(true);
      return field.get(null);
    } catch (NoSuchFieldException noSuchFieldException) {
      return null;
    } catch (Exception exception) {
      throw new IllegalArgumentException(paramString + " must be a public field of type " + paramClass2.getName() + " (" + exception + "): " + paramClass1);
    } 
  }
  
  public static TypeMapper getTypeMapper(Class<?> paramClass) {
    Map<String, Object> map = getLibraryOptions(paramClass);
    return (TypeMapper)map.get("type-mapper");
  }
  
  public static String getStringEncoding(Class<?> paramClass) {
    Map<String, Object> map = getLibraryOptions(paramClass);
    String str = (String)map.get("string-encoding");
    return (str != null) ? str : getDefaultStringEncoding();
  }
  
  public static String getDefaultStringEncoding() {
    return System.getProperty("jna.encoding", DEFAULT_ENCODING);
  }
  
  public static int getStructureAlignment(Class<?> paramClass) {
    Integer integer = (Integer)getLibraryOptions(paramClass).get("structure-alignment");
    return (integer == null) ? 0 : integer.intValue();
  }
  
  static byte[] getBytes(String paramString) {
    return getBytes(paramString, getDefaultStringEncoding());
  }
  
  static byte[] getBytes(String paramString1, String paramString2) {
    return getBytes(paramString1, getCharset(paramString2));
  }
  
  static byte[] getBytes(String paramString, Charset paramCharset) {
    return paramString.getBytes(paramCharset);
  }
  
  public static byte[] toByteArray(String paramString) {
    return toByteArray(paramString, getDefaultStringEncoding());
  }
  
  public static byte[] toByteArray(String paramString1, String paramString2) {
    return toByteArray(paramString1, getCharset(paramString2));
  }
  
  public static byte[] toByteArray(String paramString, Charset paramCharset) {
    byte[] arrayOfByte1 = getBytes(paramString, paramCharset);
    byte[] arrayOfByte2 = new byte[arrayOfByte1.length + 1];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, arrayOfByte1.length);
    return arrayOfByte2;
  }
  
  public static char[] toCharArray(String paramString) {
    char[] arrayOfChar1 = paramString.toCharArray();
    char[] arrayOfChar2 = new char[arrayOfChar1.length + 1];
    System.arraycopy(arrayOfChar1, 0, arrayOfChar2, 0, arrayOfChar1.length);
    return arrayOfChar2;
  }
  
  private static void loadNativeDispatchLibrary() {
    if (!Boolean.getBoolean("jna.nounpack"))
      try {
        removeTemporaryFiles();
      } catch (IOException iOException) {
        LOG.log(Level.WARNING, "JNA Warning: IOException removing temporary files", iOException);
      }  
    String str1 = System.getProperty("jna.boot.library.name", "jnidispatch");
    String str2 = System.getProperty("jna.boot.library.path");
    if (str2 != null) {
      StringTokenizer stringTokenizer = new StringTokenizer(str2, File.pathSeparator);
      while (stringTokenizer.hasMoreTokens()) {
        String str4 = stringTokenizer.nextToken();
        File file = new File(new File(str4), System.mapLibraryName(str1).replace(".dylib", ".jnilib"));
        String str5 = file.getAbsolutePath();
        LOG.log(DEBUG_JNA_LOAD_LEVEL, "Looking in {0}", str5);
        if (file.exists())
          try {
            LOG.log(DEBUG_JNA_LOAD_LEVEL, "Trying {0}", str5);
            System.setProperty("jnidispatch.path", str5);
            System.load(str5);
            jnidispatchPath = str5;
            LOG.log(DEBUG_JNA_LOAD_LEVEL, "Found jnidispatch at {0}", str5);
            return;
          } catch (UnsatisfiedLinkError unsatisfiedLinkError) {} 
        if (Platform.isMac()) {
          String str6;
          String str7;
          if (str5.endsWith("dylib")) {
            str6 = "dylib";
            str7 = "jnilib";
          } else {
            str6 = "jnilib";
            str7 = "dylib";
          } 
          str5 = str5.substring(0, str5.lastIndexOf(str6)) + str7;
          LOG.log(DEBUG_JNA_LOAD_LEVEL, "Looking in {0}", str5);
          if ((new File(str5)).exists())
            try {
              LOG.log(DEBUG_JNA_LOAD_LEVEL, "Trying {0}", str5);
              System.setProperty("jnidispatch.path", str5);
              System.load(str5);
              jnidispatchPath = str5;
              LOG.log(DEBUG_JNA_LOAD_LEVEL, "Found jnidispatch at {0}", str5);
              return;
            } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
              LOG.log(Level.WARNING, "File found at " + str5 + " but not loadable: " + unsatisfiedLinkError.getMessage(), unsatisfiedLinkError);
            }  
        } 
      } 
    } 
    String str3 = System.getProperty("jna.nosys", "true");
    if (!Boolean.parseBoolean(str3) || Platform.isAndroid())
      try {
        LOG.log(DEBUG_JNA_LOAD_LEVEL, "Trying (via loadLibrary) {0}", str1);
        System.loadLibrary(str1);
        LOG.log(DEBUG_JNA_LOAD_LEVEL, "Found jnidispatch on system path");
        return;
      } catch (UnsatisfiedLinkError unsatisfiedLinkError) {} 
    if (!Boolean.getBoolean("jna.noclasspath")) {
      loadNativeDispatchLibraryFromClasspath();
    } else {
      throw new UnsatisfiedLinkError("Unable to locate JNA native support library");
    } 
  }
  
  private static void loadNativeDispatchLibraryFromClasspath() {
    try {
      String str1 = System.mapLibraryName("jnidispatch").replace(".dylib", ".jnilib");
      if (Platform.isAIX())
        str1 = "libjnidispatch.a"; 
      String str2 = "/com/sun/jna/" + Platform.RESOURCE_PREFIX + "/" + str1;
      File file = extractFromResourcePath(str2, Native.class.getClassLoader());
      if (file == null && file == null)
        throw new UnsatisfiedLinkError("Could not find JNA native support"); 
      LOG.log(DEBUG_JNA_LOAD_LEVEL, "Trying {0}", file.getAbsolutePath());
      System.setProperty("jnidispatch.path", file.getAbsolutePath());
      System.load(file.getAbsolutePath());
      jnidispatchPath = file.getAbsolutePath();
      LOG.log(DEBUG_JNA_LOAD_LEVEL, "Found jnidispatch at {0}", jnidispatchPath);
      if (isUnpacked(file) && !Boolean.getBoolean("jnidispatch.preserve"))
        deleteLibrary(file); 
    } catch (IOException iOException) {
      throw new UnsatisfiedLinkError(iOException.getMessage());
    } 
  }
  
  static boolean isUnpacked(File paramFile) {
    return paramFile.getName().startsWith("jna");
  }
  
  public static File extractFromResourcePath(String paramString) throws IOException {
    return extractFromResourcePath(paramString, null);
  }
  
  public static File extractFromResourcePath(String paramString, ClassLoader paramClassLoader) throws IOException {
    Level level = (DEBUG_LOAD || (DEBUG_JNA_LOAD && paramString.contains("jnidispatch"))) ? Level.INFO : Level.FINE;
    if (paramClassLoader == null) {
      paramClassLoader = Thread.currentThread().getContextClassLoader();
      if (paramClassLoader == null)
        paramClassLoader = Native.class.getClassLoader(); 
    } 
    LOG.log(level, "Looking in classpath from {0} for {1}", new Object[] { paramClassLoader, paramString });
    String str1 = paramString.startsWith("/") ? paramString : NativeLibrary.mapSharedLibraryName(paramString);
    String str2 = paramString.startsWith("/") ? paramString : (Platform.RESOURCE_PREFIX + "/" + str1);
    if (str2.startsWith("/"))
      str2 = str2.substring(1); 
    URL uRL = paramClassLoader.getResource(str2);
    if (uRL == null)
      if (str2.startsWith(Platform.RESOURCE_PREFIX)) {
        if (Platform.RESOURCE_PREFIX.startsWith("darwin"))
          uRL = paramClassLoader.getResource("darwin/" + str2.substring(Platform.RESOURCE_PREFIX.length() + 1)); 
        if (uRL == null)
          uRL = paramClassLoader.getResource(str1); 
      } else if (str2.startsWith("com/sun/jna/" + Platform.RESOURCE_PREFIX + "/")) {
        if (Platform.RESOURCE_PREFIX.startsWith("com/sun/jna/darwin"))
          uRL = paramClassLoader.getResource("com/sun/jna/darwin" + str2.substring(("com/sun/jna/" + Platform.RESOURCE_PREFIX).length() + 1)); 
        if (uRL == null)
          uRL = paramClassLoader.getResource(str1); 
      }  
    if (uRL == null) {
      String str = System.getProperty("java.class.path");
      if (paramClassLoader instanceof URLClassLoader)
        str = Arrays.<URL>asList(((URLClassLoader)paramClassLoader).getURLs()).toString(); 
      throw new IOException("Native library (" + str2 + ") not found in resource path (" + str + ")");
    } 
    LOG.log(level, "Found library resource at {0}", uRL);
    File file = null;
    if (uRL.getProtocol().toLowerCase().equals("file")) {
      try {
        file = new File(new URI(uRL.toString()));
      } catch (URISyntaxException uRISyntaxException) {
        file = new File(uRL.getPath());
      } 
      LOG.log(level, "Looking in {0}", file.getAbsolutePath());
      if (!file.exists())
        throw new IOException("File URL " + uRL + " could not be properly decoded"); 
    } else if (!Boolean.getBoolean("jna.nounpack")) {
      InputStream inputStream = uRL.openStream();
      if (inputStream == null)
        throw new IOException("Can't obtain InputStream for " + str2); 
      FileOutputStream fileOutputStream = null;
      try {
        File file1 = getTempDir();
        file = File.createTempFile("jna", Platform.isWindows() ? ".dll" : null, file1);
        if (!Boolean.getBoolean("jnidispatch.preserve"))
          file.deleteOnExit(); 
        LOG.log(level, "Extracting library to {0}", file.getAbsolutePath());
        fileOutputStream = new FileOutputStream(file);
        byte[] arrayOfByte = new byte[1024];
        int i;
        while ((i = inputStream.read(arrayOfByte, 0, arrayOfByte.length)) > 0)
          fileOutputStream.write(arrayOfByte, 0, i); 
      } catch (IOException iOException) {
        throw new IOException("Failed to create temporary file for " + paramString + " library: " + iOException.getMessage());
      } finally {
        try {
          inputStream.close();
        } catch (IOException iOException) {}
        if (fileOutputStream != null)
          try {
            fileOutputStream.close();
          } catch (IOException iOException) {} 
      } 
    } 
    return file;
  }
  
  private static native int sizeof(int paramInt);
  
  private static native String getNativeVersion();
  
  private static native String getAPIChecksum();
  
  public static native int getLastError();
  
  public static native void setLastError(int paramInt);
  
  public static Library synchronizedLibrary(final Library library) {
    Class<?> clazz = library.getClass();
    if (!Proxy.isProxyClass(clazz))
      throw new IllegalArgumentException("Library must be a proxy class"); 
    InvocationHandler invocationHandler1 = Proxy.getInvocationHandler(library);
    if (!(invocationHandler1 instanceof Library.Handler))
      throw new IllegalArgumentException("Unrecognized proxy handler: " + invocationHandler1); 
    final Library.Handler handler = (Library.Handler)invocationHandler1;
    InvocationHandler invocationHandler2 = new InvocationHandler() {
        public Object invoke(Object param1Object, Method param1Method, Object[] param1ArrayOfObject) throws Throwable {
          synchronized (handler.getNativeLibrary()) {
            return handler.invoke(library, param1Method, param1ArrayOfObject);
          } 
        }
      };
    return (Library)Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), invocationHandler2);
  }
  
  public static String getWebStartLibraryPath(String paramString) {
    if (System.getProperty("javawebstart.version") == null)
      return null; 
    try {
      ClassLoader classLoader = Native.class.getClassLoader();
      Method method = AccessController.<Method>doPrivileged(new PrivilegedAction<Method>() {
            public Method run() {
              try {
                Method method = ClassLoader.class.getDeclaredMethod("findLibrary", new Class[] { String.class });
                method.setAccessible(true);
                return method;
              } catch (Exception exception) {
                return null;
              } 
            }
          });
      String str = (String)method.invoke(classLoader, new Object[] { paramString });
      return (str != null) ? (new File(str)).getParent() : null;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  static void markTemporaryFile(File paramFile) {
    try {
      File file = new File(paramFile.getParentFile(), paramFile.getName() + ".x");
      file.createNewFile();
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
  }
  
  static File getTempDir() throws IOException {
    File file;
    String str = System.getProperty("jna.tmpdir");
    if (str != null) {
      file = new File(str);
      file.mkdirs();
    } else {
      File file1 = new File(System.getProperty("java.io.tmpdir"));
      if (Platform.isMac()) {
        file = new File(System.getProperty("user.home"), "Library/Caches/JNA/temp");
      } else if (Platform.isLinux() || Platform.isSolaris() || Platform.isAIX() || Platform.isDragonFlyBSD() || Platform.isFreeBSD() || Platform.isNetBSD() || Platform.isOpenBSD() || Platform.iskFreeBSD()) {
        File file2;
        String str1 = System.getenv("XDG_CACHE_HOME");
        if (str1 == null || str1.trim().isEmpty()) {
          file2 = new File(System.getProperty("user.home"), ".cache");
        } else {
          file2 = new File(str1);
        } 
        file = new File(file2, "JNA/temp");
      } else {
        file = new File(file1, "jna-" + System.getProperty("user.name").hashCode());
      } 
      file.mkdirs();
      if (!file.exists() || !file.canWrite())
        file = file1; 
    } 
    if (!file.exists())
      throw new IOException("JNA temporary directory '" + file + "' does not exist"); 
    if (!file.canWrite())
      throw new IOException("JNA temporary directory '" + file + "' is not writable"); 
    return file;
  }
  
  static void removeTemporaryFiles() throws IOException {
    File file = getTempDir();
    FilenameFilter filenameFilter = new FilenameFilter() {
        public boolean accept(File param1File, String param1String) {
          return (param1String.endsWith(".x") && param1String.startsWith("jna"));
        }
      };
    File[] arrayOfFile = file.listFiles(filenameFilter);
    for (byte b = 0; arrayOfFile != null && b < arrayOfFile.length; b++) {
      File file1 = arrayOfFile[b];
      String str = file1.getName();
      str = str.substring(0, str.length() - 2);
      File file2 = new File(file1.getParentFile(), str);
      if (!file2.exists() || file2.delete())
        file1.delete(); 
    } 
  }
  
  public static int getNativeSize(Class<?> paramClass, Object paramObject) {
    if (paramClass.isArray()) {
      int i = Array.getLength(paramObject);
      if (i > 0) {
        Object object = Array.get(paramObject, 0);
        return i * getNativeSize(paramClass.getComponentType(), object);
      } 
      throw new IllegalArgumentException("Arrays of length zero not allowed: " + paramClass);
    } 
    if (Structure.class.isAssignableFrom(paramClass) && !Structure.ByReference.class.isAssignableFrom(paramClass))
      return Structure.size(paramClass, paramObject); 
    try {
      return getNativeSize(paramClass);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IllegalArgumentException("The type \"" + paramClass.getName() + "\" is not supported: " + illegalArgumentException.getMessage());
    } 
  }
  
  public static int getNativeSize(Class<?> paramClass) {
    if (NativeMapped.class.isAssignableFrom(paramClass))
      paramClass = NativeMappedConverter.getInstance(paramClass).nativeType(); 
    if (paramClass == boolean.class || paramClass == Boolean.class)
      return 4; 
    if (paramClass == byte.class || paramClass == Byte.class)
      return 1; 
    if (paramClass == short.class || paramClass == Short.class)
      return 2; 
    if (paramClass == char.class || paramClass == Character.class)
      return WCHAR_SIZE; 
    if (paramClass == int.class || paramClass == Integer.class)
      return 4; 
    if (paramClass == long.class || paramClass == Long.class)
      return 8; 
    if (paramClass == float.class || paramClass == Float.class)
      return 4; 
    if (paramClass == double.class || paramClass == Double.class)
      return 8; 
    if (Structure.class.isAssignableFrom(paramClass))
      return Structure.ByValue.class.isAssignableFrom(paramClass) ? Structure.size((Class)paramClass) : POINTER_SIZE; 
    if (Pointer.class.isAssignableFrom(paramClass) || (Platform.HAS_BUFFERS && Buffers.isBuffer(paramClass)) || Callback.class.isAssignableFrom(paramClass) || String.class == paramClass || WString.class == paramClass)
      return POINTER_SIZE; 
    throw new IllegalArgumentException("Native size for type \"" + paramClass.getName() + "\" is unknown");
  }
  
  public static boolean isSupportedNativeType(Class<?> paramClass) {
    if (Structure.class.isAssignableFrom(paramClass))
      return true; 
    try {
      return (getNativeSize(paramClass) != 0);
    } catch (IllegalArgumentException illegalArgumentException) {
      return false;
    } 
  }
  
  public static void setCallbackExceptionHandler(Callback.UncaughtExceptionHandler paramUncaughtExceptionHandler) {
    callbackExceptionHandler = (paramUncaughtExceptionHandler == null) ? DEFAULT_HANDLER : paramUncaughtExceptionHandler;
  }
  
  public static Callback.UncaughtExceptionHandler getCallbackExceptionHandler() {
    return callbackExceptionHandler;
  }
  
  public static void register(String paramString) {
    register(findDirectMappedClass(getCallingClass()), paramString);
  }
  
  public static void register(NativeLibrary paramNativeLibrary) {
    register(findDirectMappedClass(getCallingClass()), paramNativeLibrary);
  }
  
  static Class<?> findDirectMappedClass(Class<?> paramClass) {
    Method[] arrayOfMethod = paramClass.getDeclaredMethods();
    for (Method method : arrayOfMethod) {
      if ((method.getModifiers() & 0x100) != 0)
        return paramClass; 
    } 
    int i = paramClass.getName().lastIndexOf("$");
    if (i != -1) {
      String str = paramClass.getName().substring(0, i);
      try {
        return findDirectMappedClass(Class.forName(str, true, paramClass.getClassLoader()));
      } catch (ClassNotFoundException classNotFoundException) {}
    } 
    throw new IllegalArgumentException("Can't determine class with native methods from the current context (" + paramClass + ")");
  }
  
  static Class<?> getCallingClass() {
    Class[] arrayOfClass = (new SecurityManager() {
        public Class<?>[] getClassContext() {
          return super.getClassContext();
        }
      }).getClassContext();
    if (arrayOfClass == null)
      throw new IllegalStateException("The SecurityManager implementation on this platform is broken; you must explicitly provide the class to register"); 
    if (arrayOfClass.length < 4)
      throw new IllegalStateException("This method must be called from the static initializer of a class"); 
    return arrayOfClass[3];
  }
  
  public static void setCallbackThreadInitializer(Callback paramCallback, CallbackThreadInitializer paramCallbackThreadInitializer) {
    CallbackReference.setCallbackThreadInitializer(paramCallback, paramCallbackThreadInitializer);
  }
  
  private static void unregisterAll() {
    synchronized (registeredClasses) {
      for (Map.Entry<Class<?>, long> entry : registeredClasses.entrySet())
        unregister((Class)entry.getKey(), (long[])entry.getValue()); 
      registeredClasses.clear();
    } 
  }
  
  public static void unregister() {
    unregister(findDirectMappedClass(getCallingClass()));
  }
  
  public static void unregister(Class<?> paramClass) {
    synchronized (registeredClasses) {
      long[] arrayOfLong = registeredClasses.get(paramClass);
      if (arrayOfLong != null) {
        unregister(paramClass, arrayOfLong);
        registeredClasses.remove(paramClass);
        registeredLibraries.remove(paramClass);
      } 
    } 
  }
  
  public static boolean registered(Class<?> paramClass) {
    synchronized (registeredClasses) {
      return registeredClasses.containsKey(paramClass);
    } 
  }
  
  private static native void unregister(Class<?> paramClass, long[] paramArrayOflong);
  
  static String getSignature(Class<?> paramClass) {
    if (paramClass.isArray())
      return "[" + getSignature(paramClass.getComponentType()); 
    if (paramClass.isPrimitive()) {
      if (paramClass == void.class)
        return "V"; 
      if (paramClass == boolean.class)
        return "Z"; 
      if (paramClass == byte.class)
        return "B"; 
      if (paramClass == short.class)
        return "S"; 
      if (paramClass == char.class)
        return "C"; 
      if (paramClass == int.class)
        return "I"; 
      if (paramClass == long.class)
        return "J"; 
      if (paramClass == float.class)
        return "F"; 
      if (paramClass == double.class)
        return "D"; 
    } 
    return "L" + replace(".", "/", paramClass.getName()) + ";";
  }
  
  static String replace(String paramString1, String paramString2, String paramString3) {
    StringBuilder stringBuilder = new StringBuilder();
    while (true) {
      int i = paramString3.indexOf(paramString1);
      if (i == -1) {
        stringBuilder.append(paramString3);
      } else {
        stringBuilder.append(paramString3.substring(0, i));
        stringBuilder.append(paramString2);
        paramString3 = paramString3.substring(i + paramString1.length());
        continue;
      } 
      return stringBuilder.toString();
    } 
  }
  
  private static int getConversion(Class<?> paramClass, TypeMapper paramTypeMapper, boolean paramBoolean) {
    if (paramClass == Void.class)
      paramClass = void.class; 
    if (paramTypeMapper != null) {
      FromNativeConverter fromNativeConverter = paramTypeMapper.getFromNativeConverter(paramClass);
      ToNativeConverter toNativeConverter = paramTypeMapper.getToNativeConverter(paramClass);
      if (fromNativeConverter != null) {
        Class<?> clazz = fromNativeConverter.nativeType();
        return (clazz == String.class) ? 24 : ((clazz == WString.class) ? 25 : 23);
      } 
      if (toNativeConverter != null) {
        Class<?> clazz = toNativeConverter.nativeType();
        return (clazz == String.class) ? 24 : ((clazz == WString.class) ? 25 : 23);
      } 
    } 
    if (Pointer.class.isAssignableFrom(paramClass))
      return 1; 
    if (String.class == paramClass)
      return 2; 
    if (WString.class.isAssignableFrom(paramClass))
      return 20; 
    if (Platform.HAS_BUFFERS && Buffers.isBuffer(paramClass))
      return 5; 
    if (Structure.class.isAssignableFrom(paramClass))
      return Structure.ByValue.class.isAssignableFrom(paramClass) ? 4 : 3; 
    if (paramClass.isArray())
      switch (paramClass.getName().charAt(1)) {
        case 'Z':
          return 13;
        case 'B':
          return 6;
        case 'S':
          return 7;
        case 'C':
          return 8;
        case 'I':
          return 9;
        case 'J':
          return 10;
        case 'F':
          return 11;
        case 'D':
          return 12;
      }  
    if (paramClass.isPrimitive())
      return (paramClass == boolean.class) ? 14 : 0; 
    if (Callback.class.isAssignableFrom(paramClass))
      return 15; 
    if (IntegerType.class.isAssignableFrom(paramClass))
      return 21; 
    if (PointerType.class.isAssignableFrom(paramClass))
      return 22; 
    if (NativeMapped.class.isAssignableFrom(paramClass)) {
      Class<?> clazz = NativeMappedConverter.getInstance(paramClass).nativeType();
      return (clazz == String.class) ? 18 : ((clazz == WString.class) ? 19 : 17);
    } 
    return (JNIEnv.class == paramClass) ? 27 : (paramBoolean ? 26 : -1);
  }
  
  public static void register(Class<?> paramClass, String paramString) {
    NativeLibrary nativeLibrary = NativeLibrary.getInstance(paramString, Collections.singletonMap("classloader", paramClass.getClassLoader()));
    register(paramClass, nativeLibrary);
  }
  
  public static void register(Class<?> paramClass, NativeLibrary paramNativeLibrary) {
    Method[] arrayOfMethod = paramClass.getDeclaredMethods();
    ArrayList<Method> arrayList = new ArrayList();
    Map<String, ?> map = paramNativeLibrary.getOptions();
    TypeMapper typeMapper = (TypeMapper)map.get("type-mapper");
    boolean bool = Boolean.TRUE.equals(map.get("allow-objects"));
    map = cacheOptions(paramClass, map, null);
    for (Method method : arrayOfMethod) {
      if ((method.getModifiers() & 0x100) != 0)
        arrayList.add(method); 
    } 
    long[] arrayOfLong = new long[arrayList.size()];
    for (byte b = 0; b < arrayOfLong.length; b++) {
      long l1;
      long l2;
      Method method = arrayList.get(b);
      String str = "(";
      Class<?> clazz = method.getReturnType();
      Class[] arrayOfClass1 = method.getParameterTypes();
      long[] arrayOfLong1 = new long[arrayOfClass1.length];
      long[] arrayOfLong2 = new long[arrayOfClass1.length];
      int[] arrayOfInt = new int[arrayOfClass1.length];
      ToNativeConverter[] arrayOfToNativeConverter = new ToNativeConverter[arrayOfClass1.length];
      FromNativeConverter fromNativeConverter = null;
      int i = getConversion(clazz, typeMapper, bool);
      boolean bool1 = false;
      switch (i) {
        case -1:
          throw new IllegalArgumentException(clazz + " is not a supported return type (in method " + method.getName() + " in " + paramClass + ")");
        case 23:
        case 24:
        case 25:
          fromNativeConverter = typeMapper.getFromNativeConverter(clazz);
          l2 = (Structure.FFIType.get(clazz.isPrimitive() ? clazz : Pointer.class).getPointer()).peer;
          l1 = (Structure.FFIType.get(fromNativeConverter.nativeType()).getPointer()).peer;
          break;
        case 17:
        case 18:
        case 19:
        case 21:
        case 22:
          l2 = (Structure.FFIType.get(Pointer.class).getPointer()).peer;
          l1 = (Structure.FFIType.get(NativeMappedConverter.getInstance(clazz).nativeType()).getPointer()).peer;
          break;
        case 3:
        case 26:
          l2 = l1 = (Structure.FFIType.get(Pointer.class).getPointer()).peer;
        case 4:
          l2 = (Structure.FFIType.get(Pointer.class).getPointer()).peer;
          l1 = (Structure.FFIType.get(clazz).getPointer()).peer;
          break;
        default:
          l2 = l1 = (Structure.FFIType.get(clazz).getPointer()).peer;
          break;
      } 
      for (byte b1 = 0; b1 < arrayOfClass1.length; b1++) {
        Class<?> clazz1 = arrayOfClass1[b1];
        str = str + getSignature(clazz1);
        int j = getConversion(clazz1, typeMapper, bool);
        arrayOfInt[b1] = j;
        if (j == -1)
          throw new IllegalArgumentException(clazz1 + " is not a supported argument type (in method " + method.getName() + " in " + paramClass + ")"); 
        if (j == 17 || j == 18 || j == 19 || j == 21) {
          clazz1 = NativeMappedConverter.getInstance(clazz1).nativeType();
        } else if (j == 23 || j == 24 || j == 25) {
          arrayOfToNativeConverter[b1] = typeMapper.getToNativeConverter(clazz1);
        } 
        switch (j) {
          case 4:
          case 17:
          case 18:
          case 19:
          case 21:
          case 22:
            arrayOfLong1[b1] = (Structure.FFIType.get(clazz1).getPointer()).peer;
            arrayOfLong2[b1] = (Structure.FFIType.get(Pointer.class).getPointer()).peer;
            break;
          case 23:
          case 24:
          case 25:
            arrayOfLong2[b1] = (Structure.FFIType.get(clazz1.isPrimitive() ? clazz1 : Pointer.class).getPointer()).peer;
            arrayOfLong1[b1] = (Structure.FFIType.get(arrayOfToNativeConverter[b1].nativeType()).getPointer()).peer;
            break;
          case 0:
            arrayOfLong1[b1] = (Structure.FFIType.get(clazz1).getPointer()).peer;
            arrayOfLong2[b1] = (Structure.FFIType.get(clazz1).getPointer()).peer;
          default:
            arrayOfLong1[b1] = (Structure.FFIType.get(Pointer.class).getPointer()).peer;
            arrayOfLong2[b1] = (Structure.FFIType.get(Pointer.class).getPointer()).peer;
            break;
        } 
      } 
      str = str + ")";
      str = str + getSignature(clazz);
      Class[] arrayOfClass2 = method.getExceptionTypes();
      for (byte b2 = 0; b2 < arrayOfClass2.length; b2++) {
        if (LastErrorException.class.isAssignableFrom(arrayOfClass2[b2])) {
          bool1 = true;
          break;
        } 
      } 
      Function function = paramNativeLibrary.getFunction(method.getName(), method);
      try {
        arrayOfLong[b] = registerMethod(paramClass, method.getName(), str, arrayOfInt, arrayOfLong2, arrayOfLong1, i, l2, l1, method, function.peer, function.getCallingConvention(), bool1, arrayOfToNativeConverter, fromNativeConverter, function.encoding);
      } catch (NoSuchMethodError noSuchMethodError) {
        throw new UnsatisfiedLinkError("No method " + method.getName() + " with signature " + str + " in " + paramClass);
      } 
    } 
    synchronized (registeredClasses) {
      registeredClasses.put(paramClass, arrayOfLong);
      registeredLibraries.put(paramClass, paramNativeLibrary);
    } 
  }
  
  public static NativeLibrary getNativeLibrary(Library paramLibrary) {
    if (paramLibrary == null)
      throw new IllegalArgumentException("null passed to getNativeLibrary"); 
    if (!Proxy.isProxyClass(paramLibrary.getClass()))
      throw new IllegalArgumentException("library object passed to getNativeLibrary in not a proxy"); 
    InvocationHandler invocationHandler = Proxy.getInvocationHandler(paramLibrary);
    if (!(invocationHandler instanceof Library.Handler))
      throw new IllegalArgumentException("Object is not a properly initialized Library interface instance"); 
    return ((Library.Handler)invocationHandler).getNativeLibrary();
  }
  
  public static NativeLibrary getNativeLibrary(Class<?> paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException("null passed to getNativeLibrary"); 
    Class<?> clazz = findDirectMappedClass(paramClass);
    synchronized (registeredClasses) {
      NativeLibrary nativeLibrary = registeredLibraries.get(clazz);
      if (nativeLibrary == null)
        throw new IllegalArgumentException("Class " + paramClass.getName() + " is not currently registered"); 
      return nativeLibrary;
    } 
  }
  
  private static Map<String, Object> cacheOptions(Class<?> paramClass, Map<String, ?> paramMap, Object paramObject) {
    HashMap<String, Object> hashMap = new HashMap<>(paramMap);
    hashMap.put("enclosing-library", paramClass);
    typeOptions.put(paramClass, hashMap);
    if (paramObject != null)
      libraries.put(paramClass, new WeakReference(paramObject)); 
    if (!paramClass.isInterface() && Library.class.isAssignableFrom(paramClass)) {
      Class[] arrayOfClass = paramClass.getInterfaces();
      for (Class<?> clazz : arrayOfClass) {
        if (Library.class.isAssignableFrom(clazz)) {
          cacheOptions(clazz, hashMap, paramObject);
          break;
        } 
      } 
    } 
    return hashMap;
  }
  
  private static native long registerMethod(Class<?> paramClass, String paramString1, String paramString2, int[] paramArrayOfint, long[] paramArrayOflong1, long[] paramArrayOflong2, int paramInt1, long paramLong1, long paramLong2, Method paramMethod, long paramLong3, int paramInt2, boolean paramBoolean, ToNativeConverter[] paramArrayOfToNativeConverter, FromNativeConverter paramFromNativeConverter, String paramString3);
  
  private static NativeMapped fromNative(Class<?> paramClass, Object paramObject) {
    return (NativeMapped)NativeMappedConverter.getInstance(paramClass).fromNative(paramObject, new FromNativeContext(paramClass));
  }
  
  private static NativeMapped fromNative(Method paramMethod, Object paramObject) {
    Class<?> clazz = paramMethod.getReturnType();
    return (NativeMapped)NativeMappedConverter.getInstance(clazz).fromNative(paramObject, new MethodResultContext(clazz, null, null, paramMethod));
  }
  
  private static Class<?> nativeType(Class<?> paramClass) {
    return NativeMappedConverter.getInstance(paramClass).nativeType();
  }
  
  private static Object toNative(ToNativeConverter paramToNativeConverter, Object paramObject) {
    return paramToNativeConverter.toNative(paramObject, new ToNativeContext());
  }
  
  private static Object fromNative(FromNativeConverter paramFromNativeConverter, Object paramObject, Method paramMethod) {
    return paramFromNativeConverter.fromNative(paramObject, new MethodResultContext(paramMethod.getReturnType(), null, null, paramMethod));
  }
  
  public static native long ffi_prep_cif(int paramInt1, int paramInt2, long paramLong1, long paramLong2);
  
  public static native void ffi_call(long paramLong1, long paramLong2, long paramLong3, long paramLong4);
  
  public static native long ffi_prep_closure(long paramLong, ffi_callback paramffi_callback);
  
  public static native void ffi_free_closure(long paramLong);
  
  static native int initialize_ffi_type(long paramLong);
  
  public static void main(String[] paramArrayOfString) {
    String str1 = "Java Native Access (JNA)";
    String str2 = "5.17.0";
    String str3 = "5.17.0 (package information missing)";
    Package package_ = Native.class.getPackage();
    String str4 = (package_ != null) ? package_.getSpecificationTitle() : "Java Native Access (JNA)";
    if (str4 == null)
      str4 = "Java Native Access (JNA)"; 
    String str5 = (package_ != null) ? package_.getSpecificationVersion() : "5.17.0";
    if (str5 == null)
      str5 = "5.17.0"; 
    str4 = str4 + " API Version " + str5;
    System.out.println(str4);
    str5 = (package_ != null) ? package_.getImplementationVersion() : "5.17.0 (package information missing)";
    if (str5 == null)
      str5 = "5.17.0 (package information missing)"; 
    System.out.println("Version: " + str5);
    System.out.println(" Native: " + getNativeVersion() + " (" + getAPIChecksum() + ")");
    System.out.println(" Prefix: " + Platform.RESOURCE_PREFIX);
  }
  
  static synchronized native void freeNativeCallback(long paramLong);
  
  static synchronized native long createNativeCallback(Callback paramCallback, Method paramMethod, Class<?>[] paramArrayOfClass, Class<?> paramClass, int paramInt1, int paramInt2, String paramString);
  
  static native int invokeInt(Function paramFunction, long paramLong, int paramInt, Object[] paramArrayOfObject);
  
  static native long invokeLong(Function paramFunction, long paramLong, int paramInt, Object[] paramArrayOfObject);
  
  static native void invokeVoid(Function paramFunction, long paramLong, int paramInt, Object[] paramArrayOfObject);
  
  static native float invokeFloat(Function paramFunction, long paramLong, int paramInt, Object[] paramArrayOfObject);
  
  static native double invokeDouble(Function paramFunction, long paramLong, int paramInt, Object[] paramArrayOfObject);
  
  static native long invokePointer(Function paramFunction, long paramLong, int paramInt, Object[] paramArrayOfObject);
  
  private static native void invokeStructure(Function paramFunction, long paramLong1, int paramInt, Object[] paramArrayOfObject, long paramLong2, long paramLong3);
  
  static Structure invokeStructure(Function paramFunction, long paramLong, int paramInt, Object[] paramArrayOfObject, Structure paramStructure) {
    invokeStructure(paramFunction, paramLong, paramInt, paramArrayOfObject, (paramStructure.getPointer()).peer, (paramStructure.getTypeInfo()).peer);
    return paramStructure;
  }
  
  static native Object invokeObject(Function paramFunction, long paramLong, int paramInt, Object[] paramArrayOfObject);
  
  static long open(String paramString) {
    return open(paramString, -1);
  }
  
  static native long open(String paramString, int paramInt);
  
  static native void close(long paramLong);
  
  static native long findSymbol(long paramLong, String paramString);
  
  static native long indexOf(Pointer paramPointer, long paramLong1, long paramLong2, byte paramByte);
  
  static native void read(Pointer paramPointer, long paramLong1, long paramLong2, byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  static native void read(Pointer paramPointer, long paramLong1, long paramLong2, short[] paramArrayOfshort, int paramInt1, int paramInt2);
  
  static native void read(Pointer paramPointer, long paramLong1, long paramLong2, char[] paramArrayOfchar, int paramInt1, int paramInt2);
  
  static native void read(Pointer paramPointer, long paramLong1, long paramLong2, int[] paramArrayOfint, int paramInt1, int paramInt2);
  
  static native void read(Pointer paramPointer, long paramLong1, long paramLong2, long[] paramArrayOflong, int paramInt1, int paramInt2);
  
  static native void read(Pointer paramPointer, long paramLong1, long paramLong2, float[] paramArrayOffloat, int paramInt1, int paramInt2);
  
  static native void read(Pointer paramPointer, long paramLong1, long paramLong2, double[] paramArrayOfdouble, int paramInt1, int paramInt2);
  
  static native void write(Pointer paramPointer, long paramLong1, long paramLong2, byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  static native void write(Pointer paramPointer, long paramLong1, long paramLong2, short[] paramArrayOfshort, int paramInt1, int paramInt2);
  
  static native void write(Pointer paramPointer, long paramLong1, long paramLong2, char[] paramArrayOfchar, int paramInt1, int paramInt2);
  
  static native void write(Pointer paramPointer, long paramLong1, long paramLong2, int[] paramArrayOfint, int paramInt1, int paramInt2);
  
  static native void write(Pointer paramPointer, long paramLong1, long paramLong2, long[] paramArrayOflong, int paramInt1, int paramInt2);
  
  static native void write(Pointer paramPointer, long paramLong1, long paramLong2, float[] paramArrayOffloat, int paramInt1, int paramInt2);
  
  static native void write(Pointer paramPointer, long paramLong1, long paramLong2, double[] paramArrayOfdouble, int paramInt1, int paramInt2);
  
  static native byte getByte(Pointer paramPointer, long paramLong1, long paramLong2);
  
  static native char getChar(Pointer paramPointer, long paramLong1, long paramLong2);
  
  static native short getShort(Pointer paramPointer, long paramLong1, long paramLong2);
  
  static native int getInt(Pointer paramPointer, long paramLong1, long paramLong2);
  
  static native long getLong(Pointer paramPointer, long paramLong1, long paramLong2);
  
  static native float getFloat(Pointer paramPointer, long paramLong1, long paramLong2);
  
  static native double getDouble(Pointer paramPointer, long paramLong1, long paramLong2);
  
  static Pointer getPointer(long paramLong) {
    long l = _getPointer(paramLong);
    return (l == 0L) ? null : new Pointer(l);
  }
  
  private static native long _getPointer(long paramLong);
  
  static native String getWideString(Pointer paramPointer, long paramLong1, long paramLong2);
  
  static String getString(Pointer paramPointer, long paramLong) {
    return getString(paramPointer, paramLong, getDefaultStringEncoding());
  }
  
  static String getString(Pointer paramPointer, long paramLong, String paramString) {
    byte[] arrayOfByte = getStringBytes(paramPointer, paramPointer.peer, paramLong);
    if (paramString != null)
      try {
        return new String(arrayOfByte, paramString);
      } catch (UnsupportedEncodingException unsupportedEncodingException) {} 
    return new String(arrayOfByte);
  }
  
  static native byte[] getStringBytes(Pointer paramPointer, long paramLong1, long paramLong2);
  
  static native void setMemory(Pointer paramPointer, long paramLong1, long paramLong2, long paramLong3, byte paramByte);
  
  static native void setByte(Pointer paramPointer, long paramLong1, long paramLong2, byte paramByte);
  
  static native void setShort(Pointer paramPointer, long paramLong1, long paramLong2, short paramShort);
  
  static native void setChar(Pointer paramPointer, long paramLong1, long paramLong2, char paramChar);
  
  static native void setInt(Pointer paramPointer, long paramLong1, long paramLong2, int paramInt);
  
  static native void setLong(Pointer paramPointer, long paramLong1, long paramLong2, long paramLong3);
  
  static native void setFloat(Pointer paramPointer, long paramLong1, long paramLong2, float paramFloat);
  
  static native void setDouble(Pointer paramPointer, long paramLong1, long paramLong2, double paramDouble);
  
  static native void setPointer(Pointer paramPointer, long paramLong1, long paramLong2, long paramLong3);
  
  static native void setWideString(Pointer paramPointer, long paramLong1, long paramLong2, String paramString);
  
  static native ByteBuffer getDirectByteBuffer(Pointer paramPointer, long paramLong1, long paramLong2, long paramLong3);
  
  public static native long malloc(long paramLong);
  
  public static native void free(long paramLong);
  
  public static void detach(boolean paramBoolean) {
    Thread thread = Thread.currentThread();
    if (paramBoolean) {
      nativeThreads.remove(thread);
      Pointer pointer = nativeThreadTerminationFlag.get();
      setDetachState(true, 0L);
    } else if (!nativeThreads.containsKey(thread)) {
      Pointer pointer = nativeThreadTerminationFlag.get();
      nativeThreads.put(thread, pointer);
      setDetachState(false, pointer.peer);
    } 
  }
  
  static Pointer getTerminationFlag(Thread paramThread) {
    return nativeThreads.get(paramThread);
  }
  
  private static native void setDetachState(boolean paramBoolean, long paramLong);
  
  static {
    String str = System.getProperty("native.encoding");
    Charset charset = null;
    if (str != null)
      try {
        charset = Charset.forName(str);
      } catch (Exception exception) {
        LOG.log(Level.WARNING, "Failed to get charset for native.encoding value : '" + str + "'", exception);
      }  
    if (charset == null)
      charset = Charset.defaultCharset(); 
    DEFAULT_CHARSET = charset;
    DEFAULT_ENCODING = charset.name();
  }
  
  static {
    loadNativeDispatchLibrary();
    if (!isCompatibleVersion("7.0.4", getNativeVersion())) {
      str = System.lineSeparator();
      throw new Error(str + str + "There is an incompatible JNA native library installed on this system" + str + "Expected: " + "7.0.4" + str + "Found:    " + getNativeVersion() + str + ((jnidispatchPath != null) ? ("(at " + jnidispatchPath + ")") : System.getProperty("java.library.path")) + "." + str + "To resolve this issue you may do one of the following:" + str + " - remove or uninstall the offending library" + str + " - set the system property jna.nosys=true" + str + " - set jna.boot.library.path to include the path to the version of the " + str + "   jnidispatch library included with the JNA jar file you are using" + str);
    } 
  }
  
  static {
    initIDs();
    if (Boolean.getBoolean("jna.protected"))
      setProtected(true); 
  }
  
  static {
    System.setProperty("jna.loaded", "true");
  }
  
  private static class AWT {
    static long getWindowID(Window param1Window) throws HeadlessException {
      return getComponentID(param1Window);
    }
    
    static long getComponentID(Object param1Object) throws HeadlessException {
      if (GraphicsEnvironment.isHeadless())
        throw new HeadlessException("No native windows when headless"); 
      Component component = (Component)param1Object;
      if (component.isLightweight())
        throw new IllegalArgumentException("Component must be heavyweight"); 
      if (!component.isDisplayable())
        throw new IllegalStateException("Component must be displayable"); 
      if (Platform.isX11() && System.getProperty("java.version").startsWith("1.4") && !component.isVisible())
        throw new IllegalStateException("Component must be visible"); 
      return Native.getWindowHandle0(component);
    }
  }
  
  private static class Buffers {
    static boolean isBuffer(Class<?> param1Class) {
      return Buffer.class.isAssignableFrom(param1Class);
    }
  }
  
  public static interface ffi_callback {
    void invoke(long param1Long1, long param1Long2, long param1Long3);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\Native.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */