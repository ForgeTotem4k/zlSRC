package oshi.util;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.LibCAPI;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class FileUtil {
  private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);
  
  private static final String READING_LOG = "Reading file {}";
  
  private static final String READ_LOG = "Read {}";
  
  private static final int BUFFER_SIZE = 1024;
  
  public static List<String> readFile(String paramString) {
    return readFile(paramString, true);
  }
  
  public static List<String> readFile(String paramString, boolean paramBoolean) {
    if ((new File(paramString)).canRead()) {
      if (LOG.isDebugEnabled())
        LOG.debug("Reading file {}", paramString); 
      try {
        return Files.readAllLines(Paths.get(paramString, new String[0]), StandardCharsets.UTF_8);
      } catch (IOException iOException) {
        if (paramBoolean) {
          LOG.error("Error reading file {}. {}", paramString, iOException.getMessage());
        } else {
          LOG.debug("Error reading file {}. {}", paramString, iOException.getMessage());
        } 
      } 
    } else if (paramBoolean) {
      LOG.warn("File not found or not readable: {}", paramString);
    } 
    return Collections.emptyList();
  }
  
  public static List<String> readLines(String paramString, int paramInt) {
    return readLines(paramString, paramInt, true);
  }
  
  public static List<String> readLines(String paramString, int paramInt, boolean paramBoolean) {
    Path path = Paths.get(paramString, new String[0]);
    if (Files.isReadable(path)) {
      if (LOG.isDebugEnabled())
        LOG.debug("Reading file {}", paramString); 
      CharsetDecoder charsetDecoder = StandardCharsets.UTF_8.newDecoder();
      try {
        InputStreamReader inputStreamReader = new InputStreamReader(Files.newInputStream(path, new java.nio.file.OpenOption[0]), charsetDecoder);
        try {
          BufferedReader bufferedReader = new BufferedReader(inputStreamReader, 1024);
          try {
            ArrayList<String> arrayList1 = new ArrayList(paramInt);
            for (byte b = 0; b < paramInt; b++) {
              String str = bufferedReader.readLine();
              if (str == null)
                break; 
              arrayList1.add(str);
            } 
            ArrayList<String> arrayList2 = arrayList1;
            bufferedReader.close();
            inputStreamReader.close();
            return arrayList2;
          } catch (Throwable throwable) {
            try {
              bufferedReader.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            } 
            throw throwable;
          } 
        } catch (Throwable throwable) {
          try {
            inputStreamReader.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
      } catch (IOException iOException) {
        if (paramBoolean) {
          LOG.error("Error reading file {}. {}", paramString, iOException.getMessage());
        } else {
          LOG.debug("Error reading file {}. {}", paramString, iOException.getMessage());
        } 
      } 
    } else if (paramBoolean) {
      LOG.warn("File not found or not readable: {}", paramString);
    } 
    return Collections.emptyList();
  }
  
  public static byte[] readAllBytes(String paramString, boolean paramBoolean) {
    if ((new File(paramString)).canRead()) {
      if (LOG.isDebugEnabled())
        LOG.debug("Reading file {}", paramString); 
      try {
        return Files.readAllBytes(Paths.get(paramString, new String[0]));
      } catch (IOException iOException) {
        if (paramBoolean) {
          LOG.error("Error reading file {}. {}", paramString, iOException.getMessage());
        } else {
          LOG.debug("Error reading file {}. {}", paramString, iOException.getMessage());
        } 
      } 
    } else if (paramBoolean) {
      LOG.warn("File not found or not readable: {}", paramString);
    } 
    return new byte[0];
  }
  
  public static ByteBuffer readAllBytesAsBuffer(String paramString) {
    byte[] arrayOfByte = readAllBytes(paramString, false);
    ByteBuffer byteBuffer = ByteBuffer.allocate(arrayOfByte.length);
    byteBuffer.order(ByteOrder.nativeOrder());
    for (byte b : arrayOfByte)
      byteBuffer.put(b); 
    byteBuffer.flip();
    return byteBuffer;
  }
  
  public static byte readByteFromBuffer(ByteBuffer paramByteBuffer) {
    return (paramByteBuffer.position() < paramByteBuffer.limit()) ? paramByteBuffer.get() : 0;
  }
  
  public static short readShortFromBuffer(ByteBuffer paramByteBuffer) {
    return (paramByteBuffer.position() <= paramByteBuffer.limit() - 2) ? paramByteBuffer.getShort() : 0;
  }
  
  public static int readIntFromBuffer(ByteBuffer paramByteBuffer) {
    return (paramByteBuffer.position() <= paramByteBuffer.limit() - 4) ? paramByteBuffer.getInt() : 0;
  }
  
  public static long readLongFromBuffer(ByteBuffer paramByteBuffer) {
    return (paramByteBuffer.position() <= paramByteBuffer.limit() - 8) ? paramByteBuffer.getLong() : 0L;
  }
  
  public static NativeLong readNativeLongFromBuffer(ByteBuffer paramByteBuffer) {
    return new NativeLong((Native.LONG_SIZE == 4) ? readIntFromBuffer(paramByteBuffer) : readLongFromBuffer(paramByteBuffer));
  }
  
  public static LibCAPI.size_t readSizeTFromBuffer(ByteBuffer paramByteBuffer) {
    return new LibCAPI.size_t((Native.SIZE_T_SIZE == 4) ? readIntFromBuffer(paramByteBuffer) : readLongFromBuffer(paramByteBuffer));
  }
  
  public static void readByteArrayFromBuffer(ByteBuffer paramByteBuffer, byte[] paramArrayOfbyte) {
    if (paramByteBuffer.position() <= paramByteBuffer.limit() - paramArrayOfbyte.length)
      paramByteBuffer.get(paramArrayOfbyte); 
  }
  
  public static Pointer readPointerFromBuffer(ByteBuffer paramByteBuffer) {
    return (paramByteBuffer.position() <= paramByteBuffer.limit() - Native.POINTER_SIZE) ? ((Native.POINTER_SIZE == 4) ? new Pointer(paramByteBuffer.getInt()) : new Pointer(paramByteBuffer.getLong())) : Pointer.NULL;
  }
  
  public static long getLongFromFile(String paramString) {
    if (LOG.isDebugEnabled())
      LOG.debug("Reading file {}", paramString); 
    List<String> list = readLines(paramString, 1, false);
    if (!list.isEmpty()) {
      if (LOG.isTraceEnabled())
        LOG.trace("Read {}", list.get(0)); 
      return ParseUtil.parseLongOrDefault(list.get(0), 0L);
    } 
    return 0L;
  }
  
  public static long getUnsignedLongFromFile(String paramString) {
    if (LOG.isDebugEnabled())
      LOG.debug("Reading file {}", paramString); 
    List<String> list = readLines(paramString, 1, false);
    if (!list.isEmpty()) {
      if (LOG.isTraceEnabled())
        LOG.trace("Read {}", list.get(0)); 
      return ParseUtil.parseUnsignedLongOrDefault(list.get(0), 0L);
    } 
    return 0L;
  }
  
  public static int getIntFromFile(String paramString) {
    if (LOG.isDebugEnabled())
      LOG.debug("Reading file {}", paramString); 
    try {
      List<String> list = readLines(paramString, 1, false);
      if (!list.isEmpty()) {
        if (LOG.isTraceEnabled())
          LOG.trace("Read {}", list.get(0)); 
        return ParseUtil.parseIntOrDefault(list.get(0), 0);
      } 
    } catch (NumberFormatException numberFormatException) {
      LOG.warn("Unable to read value from {}. {}", paramString, numberFormatException.getMessage());
    } 
    return 0;
  }
  
  public static String getStringFromFile(String paramString) {
    if (LOG.isDebugEnabled())
      LOG.debug("Reading file {}", paramString); 
    List<String> list = readLines(paramString, 1, false);
    if (!list.isEmpty()) {
      if (LOG.isTraceEnabled())
        LOG.trace("Read {}", list.get(0)); 
      return list.get(0);
    } 
    return "";
  }
  
  public static Map<String, String> getKeyValueMapFromFile(String paramString1, String paramString2) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    if (LOG.isDebugEnabled())
      LOG.debug("Reading file {}", paramString1); 
    List<String> list = readFile(paramString1, false);
    for (String str : list) {
      String[] arrayOfString = str.split(paramString2);
      if (arrayOfString.length == 2)
        hashMap.put(arrayOfString[0], arrayOfString[1].trim()); 
    } 
    return (Map)hashMap;
  }
  
  public static Properties readPropertiesFromFilename(String paramString) {
    Properties properties = new Properties();
    for (ClassLoader classLoader : Stream.<ClassLoader>of(new ClassLoader[] { Thread.currentThread().getContextClassLoader(), ClassLoader.getSystemClassLoader(), FileUtil.class.getClassLoader() }).collect(Collectors.toCollection(java.util.LinkedHashSet::new))) {
      if (readPropertiesFromClassLoader(paramString, properties, classLoader))
        return properties; 
    } 
    LOG.warn("Failed to load configuration file from classloader: {}", paramString);
    return properties;
  }
  
  private static boolean readPropertiesFromClassLoader(String paramString, Properties paramProperties, ClassLoader paramClassLoader) {
    if (paramClassLoader == null)
      return false; 
    try {
      ArrayList<URL> arrayList = Collections.list(paramClassLoader.getResources(paramString));
      if (arrayList.isEmpty()) {
        LOG.debug("No {} file found from ClassLoader {}", paramString, paramClassLoader);
        return false;
      } 
      if (arrayList.size() > 1) {
        byte[] arrayOfByte = readFileAsBytes(arrayList.get(0));
        for (byte b = 1; b < arrayList.size(); b++) {
          if (!Arrays.equals(arrayOfByte, readFileAsBytes(arrayList.get(b)))) {
            LOG.warn("Configuration conflict: there is more than one {} file on the classpath: {}", paramString, arrayList);
            break;
          } 
        } 
      } 
      InputStream inputStream = ((URL)arrayList.get(0)).openStream();
      try {
        if (inputStream != null)
          paramProperties.load(inputStream); 
        if (inputStream != null)
          inputStream.close(); 
      } catch (Throwable throwable) {
        if (inputStream != null)
          try {
            inputStream.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
      return true;
    } catch (IOException iOException) {
      return false;
    } 
  }
  
  public static byte[] readFileAsBytes(URL paramURL) throws IOException {
    InputStream inputStream = paramURL.openStream();
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      byte[] arrayOfByte1 = new byte[1024];
      int i;
      while ((i = inputStream.read(arrayOfByte1, 0, arrayOfByte1.length)) != -1)
        byteArrayOutputStream.write(arrayOfByte1, 0, i); 
      byteArrayOutputStream.flush();
      byte[] arrayOfByte2 = byteArrayOutputStream.toByteArray();
      if (inputStream != null)
        inputStream.close(); 
      return arrayOfByte2;
    } catch (Throwable throwable) {
      if (inputStream != null)
        try {
          inputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  public static String readSymlinkTarget(File paramFile) {
    try {
      return Files.readSymbolicLink(Paths.get(paramFile.getAbsolutePath(), new String[0])).toString();
    } catch (IOException iOException) {
      return null;
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\FileUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */