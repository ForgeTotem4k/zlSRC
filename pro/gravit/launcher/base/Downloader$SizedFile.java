package pro.gravit.launcher.base;

public class SizedFile {
  public final String urlPath;
  
  public final String filePath;
  
  public final long size;
  
  public SizedFile(String paramString, long paramLong) {
    this.urlPath = paramString;
    this.filePath = paramString;
    this.size = paramLong;
  }
  
  public SizedFile(String paramString1, String paramString2, long paramLong) {
    this.urlPath = paramString1;
    this.filePath = paramString2;
    this.size = paramLong;
  }
  
  public SizedFile(String paramString1, String paramString2) {
    this.urlPath = paramString1;
    this.filePath = paramString2;
    this.size = -1L;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\Downloader$SizedFile.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */