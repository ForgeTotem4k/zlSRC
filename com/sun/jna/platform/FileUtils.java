package com.sun.jna.platform;

import com.sun.jna.platform.mac.MacFileUtils;
import com.sun.jna.platform.win32.W32FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public abstract class FileUtils {
  public boolean hasTrash() {
    return false;
  }
  
  public abstract void moveToTrash(File... paramVarArgs) throws IOException;
  
  public static FileUtils getInstance() {
    return Holder.INSTANCE;
  }
  
  private static class Holder {
    public static final FileUtils INSTANCE;
    
    static {
      String str = System.getProperty("os.name");
      if (str.startsWith("Windows")) {
        INSTANCE = (FileUtils)new W32FileUtils();
      } else if (str.startsWith("Mac")) {
        INSTANCE = (FileUtils)new MacFileUtils();
      } else {
        INSTANCE = new FileUtils.DefaultFileUtils();
      } 
    }
  }
  
  private static class DefaultFileUtils extends FileUtils {
    private DefaultFileUtils() {}
    
    private File getTrashDirectory() {
      File file1 = new File(System.getProperty("user.home"));
      File file2 = new File(file1, ".Trash");
      if (!file2.exists()) {
        file2 = new File(file1, "Trash");
        if (!file2.exists()) {
          File file = new File(file1, "Desktop");
          if (file.exists()) {
            file2 = new File(file, ".Trash");
            if (!file2.exists()) {
              file2 = new File(file, "Trash");
              if (!file2.exists())
                file2 = new File(System.getProperty("fileutils.trash", "Trash")); 
            } 
          } 
        } 
      } 
      return file2;
    }
    
    public boolean hasTrash() {
      return getTrashDirectory().exists();
    }
    
    public void moveToTrash(File... param1VarArgs) throws IOException {
      File file = getTrashDirectory();
      if (!file.exists())
        throw new IOException("No trash location found (define fileutils.trash to be the path to the trash)"); 
      ArrayList<File> arrayList = new ArrayList();
      for (byte b = 0; b < param1VarArgs.length; b++) {
        File file1 = param1VarArgs[b];
        File file2 = new File(file, file1.getName());
        if (!file1.renameTo(file2))
          arrayList.add(file1); 
      } 
      if (arrayList.size() > 0)
        throw new IOException("The following files could not be trashed: " + arrayList); 
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\FileUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */