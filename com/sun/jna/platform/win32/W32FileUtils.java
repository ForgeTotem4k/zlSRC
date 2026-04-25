package com.sun.jna.platform.win32;

import com.sun.jna.platform.FileUtils;
import java.io.File;
import java.io.IOException;

public class W32FileUtils extends FileUtils {
  public boolean hasTrash() {
    return true;
  }
  
  public void moveToTrash(File... paramVarArgs) throws IOException {
    Shell32 shell32 = Shell32.INSTANCE;
    ShellAPI.SHFILEOPSTRUCT sHFILEOPSTRUCT = new ShellAPI.SHFILEOPSTRUCT();
    sHFILEOPSTRUCT.wFunc = 3;
    String[] arrayOfString = new String[paramVarArgs.length];
    int i;
    for (i = 0; i < arrayOfString.length; i++)
      arrayOfString[i] = paramVarArgs[i].getAbsolutePath(); 
    sHFILEOPSTRUCT.pFrom = sHFILEOPSTRUCT.encodePaths(arrayOfString);
    sHFILEOPSTRUCT.fFlags = 1620;
    i = shell32.SHFileOperation(sHFILEOPSTRUCT);
    if (i != 0)
      throw new IOException("Move to trash failed: " + sHFILEOPSTRUCT.pFrom + ": " + Kernel32Util.formatMessageFromLastErrorCode(i)); 
    if (sHFILEOPSTRUCT.fAnyOperationsAborted)
      throw new IOException("Move to trash aborted"); 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\W32FileUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */