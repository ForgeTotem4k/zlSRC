package pro.gravit.launcher.runtime.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.net.ssl.HttpsURLConnection;
import pro.gravit.launcher.base.Downloader;
import pro.gravit.launcher.base.request.update.LauncherRequest;
import pro.gravit.launcher.runtime.LauncherEngine;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.LogHelper;
import pro.gravit.utils.helper.SecurityHelper;

public class LauncherUpdater {
  private static boolean isCertificatePinning = false;
  
  public static void nothing() {}
  
  private static Path getLauncherPath() {
    Path path1 = IOHelper.getCodeSource(IOHelper.class);
    Path path2 = IOHelper.getCodeSource(LauncherRequest.class);
    Path path3 = IOHelper.getCodeSource(LauncherUpdater.class);
    if (path1.equals(path2) && path1.equals(path3))
      return path1; 
    throw new SecurityException("Found split-jar launcher");
  }
  
  public static Path prepareUpdate(URL paramURL) throws Exception {
    Path path1 = getLauncherPath();
    Path path2 = Files.createTempFile("launcher-update-", ".jar", (FileAttribute<?>[])new FileAttribute[0]);
    URLConnection uRLConnection = paramURL.openConnection();
    if (isCertificatePinning) {
      HttpsURLConnection httpsURLConnection = (HttpsURLConnection)uRLConnection;
      try {
        httpsURLConnection.setSSLSocketFactory(Downloader.makeSSLSocketFactory());
      } catch (NoSuchAlgorithmException|java.security.cert.CertificateException|java.security.KeyStoreException|java.security.KeyManagementException noSuchAlgorithmException) {
        throw new IOException(noSuchAlgorithmException);
      } 
    } 
    InputStream inputStream = uRLConnection.getInputStream();
    try {
      IOHelper.transfer(inputStream, path2);
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
    if (Arrays.equals(SecurityHelper.digest(SecurityHelper.DigestAlgorithm.MD5, path2), SecurityHelper.digest(SecurityHelper.DigestAlgorithm.MD5, path1)))
      throw new IOException("Invalid update (launcher needs update, but link has old launcher), check LaunchServer config..."); 
    return path2;
  }
  
  public static void restart() {
    ArrayList<String> arrayList = new ArrayList(8);
    arrayList.add(IOHelper.resolveJavaBin(null).toString());
    arrayList.add("-jar");
    arrayList.add(IOHelper.getCodeSource(LauncherUpdater.class).toString());
    ProcessBuilder processBuilder = new ProcessBuilder(arrayList.<String>toArray(new String[0]));
    processBuilder.inheritIO();
    try {
      processBuilder.start();
    } catch (IOException iOException) {
      LogHelper.error(iOException);
    } 
    LauncherEngine.forceExit(0);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtim\\utils\LauncherUpdater.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */