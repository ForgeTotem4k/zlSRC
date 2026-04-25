package oshi.util;

import com.sun.jna.Platform;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class UserGroupInfo {
  private static final Supplier<Map<String, String>> USERS_ID_MAP = Memoizer.memoize(UserGroupInfo::getUserMap, TimeUnit.MINUTES.toNanos(5L));
  
  private static final Supplier<Map<String, String>> GROUPS_ID_MAP = Memoizer.memoize(UserGroupInfo::getGroupMap, TimeUnit.MINUTES.toNanos(5L));
  
  private static final boolean ELEVATED = (0 == ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("id -u"), -1));
  
  public static boolean isElevated() {
    return ELEVATED;
  }
  
  public static String getUser(String paramString) {
    return (String)((Map)USERS_ID_MAP.get()).getOrDefault(paramString, getentPasswd(paramString));
  }
  
  public static String getGroupName(String paramString) {
    return (String)((Map)GROUPS_ID_MAP.get()).getOrDefault(paramString, getentGroup(paramString));
  }
  
  private static Map<String, String> getUserMap() {
    return parsePasswd(FileUtil.readFile("/etc/passwd"));
  }
  
  private static String getentPasswd(String paramString) {
    if (Platform.isAIX())
      return "unknown"; 
    Map<String, String> map = parsePasswd(ExecutingCommand.runNative("getent passwd " + paramString));
    ((Map<String, String>)USERS_ID_MAP.get()).putAll(map);
    return map.getOrDefault(paramString, "unknown");
  }
  
  private static Map<String, String> parsePasswd(List<String> paramList) {
    ConcurrentHashMap<Object, Object> concurrentHashMap = new ConcurrentHashMap<>();
    for (String str : paramList) {
      String[] arrayOfString = str.split(":");
      if (arrayOfString.length > 2) {
        String str1 = arrayOfString[0];
        String str2 = arrayOfString[2];
        concurrentHashMap.putIfAbsent(str2, str1);
      } 
    } 
    return (Map)concurrentHashMap;
  }
  
  private static Map<String, String> getGroupMap() {
    return parseGroup(FileUtil.readFile("/etc/group"));
  }
  
  private static String getentGroup(String paramString) {
    if (Platform.isAIX())
      return "unknown"; 
    Map<String, String> map = parseGroup(ExecutingCommand.runNative("getent group " + paramString));
    ((Map<String, String>)GROUPS_ID_MAP.get()).putAll(map);
    return map.getOrDefault(paramString, "unknown");
  }
  
  private static Map<String, String> parseGroup(List<String> paramList) {
    ConcurrentHashMap<Object, Object> concurrentHashMap = new ConcurrentHashMap<>();
    for (String str : paramList) {
      String[] arrayOfString = str.split(":");
      if (arrayOfString.length > 2) {
        String str1 = arrayOfString[0];
        String str2 = arrayOfString[2];
        concurrentHashMap.putIfAbsent(str2, str1);
      } 
    } 
    return (Map)concurrentHashMap;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\UserGroupInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */