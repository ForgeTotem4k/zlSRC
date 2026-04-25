package oshi.util;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class FileSystemUtil {
  private static final String GLOB_PREFIX = "glob:";
  
  private static final String REGEX_PREFIX = "regex:";
  
  public static boolean isFileStoreExcluded(String paramString1, String paramString2, List<PathMatcher> paramList1, List<PathMatcher> paramList2, List<PathMatcher> paramList3, List<PathMatcher> paramList4) {
    Path path1 = Paths.get(paramString1, new String[0]);
    Path path2 = Paths.get(paramString2, new String[0]);
    return (matches(path1, paramList1) || matches(path2, paramList3)) ? false : ((matches(path1, paramList2) || matches(path2, paramList4)));
  }
  
  public static List<PathMatcher> loadAndParseFileSystemConfig(String paramString) {
    String str = GlobalConfig.get(paramString, "");
    return parseFileSystemConfig(str);
  }
  
  public static List<PathMatcher> parseFileSystemConfig(String paramString) {
    FileSystem fileSystem = FileSystems.getDefault();
    ArrayList<PathMatcher> arrayList = new ArrayList();
    for (String str : paramString.split(",")) {
      if (str.length() > 0) {
        if (!str.startsWith("glob:") && !str.startsWith("regex:"))
          str = "glob:" + str; 
        arrayList.add(fileSystem.getPathMatcher(str));
      } 
    } 
    return arrayList;
  }
  
  public static boolean matches(Path paramPath, List<PathMatcher> paramList) {
    for (PathMatcher pathMatcher : paramList) {
      if (pathMatcher.matches(paramPath))
        return true; 
    } 
    return false;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\FileSystemUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */