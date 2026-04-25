package oshi.util;

import com.sun.jna.Platform;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class ExecutingCommand {
  private static final Logger LOG = LoggerFactory.getLogger(ExecutingCommand.class);
  
  private static final String[] DEFAULT_ENV = getDefaultEnv();
  
  private static String[] getDefaultEnv() {
    return Platform.isWindows() ? new String[] { "LANGUAGE=C" } : new String[] { "LC_ALL=C" };
  }
  
  public static List<String> runNative(String paramString) {
    String[] arrayOfString = paramString.split(" ");
    return runNative(arrayOfString);
  }
  
  public static List<String> runNative(String[] paramArrayOfString) {
    return runNative(paramArrayOfString, DEFAULT_ENV);
  }
  
  public static List<String> runNative(String[] paramArrayOfString1, String[] paramArrayOfString2) {
    Process process = null;
    try {
      process = Runtime.getRuntime().exec(paramArrayOfString1, paramArrayOfString2);
      return getProcessOutput(process, paramArrayOfString1);
    } catch (SecurityException|IOException securityException) {
      LOG.trace("Couldn't run command {}: {}", Arrays.toString((Object[])paramArrayOfString1), securityException.getMessage());
    } finally {
      if (process != null) {
        if (Platform.isWindows() || Platform.isSolaris()) {
          try {
            process.getOutputStream().close();
          } catch (IOException iOException) {}
          try {
            process.getInputStream().close();
          } catch (IOException iOException) {}
          try {
            process.getErrorStream().close();
          } catch (IOException iOException) {}
        } 
        process.destroy();
      } 
    } 
    return Collections.emptyList();
  }
  
  private static List<String> getProcessOutput(Process paramProcess, String[] paramArrayOfString) {
    ArrayList<String> arrayList = new ArrayList();
    try {
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(paramProcess.getInputStream(), Charset.defaultCharset()));
      try {
        String str;
        while ((str = bufferedReader.readLine()) != null)
          arrayList.add(str); 
        paramProcess.waitFor();
        bufferedReader.close();
      } catch (Throwable throwable) {
        try {
          bufferedReader.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (IOException iOException) {
      LOG.trace("Problem reading output from {}: {}", Arrays.toString((Object[])paramArrayOfString), iOException.getMessage());
    } catch (InterruptedException interruptedException) {
      LOG.trace("Interrupted while reading output from {}: {}", Arrays.toString((Object[])paramArrayOfString), interruptedException.getMessage());
      Thread.currentThread().interrupt();
    } 
    return arrayList;
  }
  
  public static String getFirstAnswer(String paramString) {
    return getAnswerAt(paramString, 0);
  }
  
  public static String getAnswerAt(String paramString, int paramInt) {
    List<String> list = runNative(paramString);
    return (paramInt >= 0 && paramInt < list.size()) ? list.get(paramInt) : "";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\ExecutingCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */