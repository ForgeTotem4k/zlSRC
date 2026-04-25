package pro.gravit.utils.helper;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import pro.gravit.utils.command.CommandException;
import pro.gravit.utils.launch.LaunchOptions;

public final class CommonHelper {
  public static String low(String paramString) {
    return paramString.toLowerCase(Locale.US);
  }
  
  public static boolean multiMatches(Pattern[] paramArrayOfPattern, String paramString) {
    for (Pattern pattern : paramArrayOfPattern) {
      if (pattern.matcher(paramString).matches())
        return true; 
    } 
    return false;
  }
  
  public static String multiReplace(Pattern[] paramArrayOfPattern, String paramString1, String paramString2) {
    String str = null;
    for (Pattern pattern : paramArrayOfPattern) {
      Matcher matcher = pattern.matcher(paramString1);
      if (matcher.matches())
        str = matcher.replaceAll(paramString2); 
    } 
    return (str != null) ? str : paramString1;
  }
  
  @Deprecated
  public static ScriptEngine newScriptEngine() {
    throw new UnsupportedOperationException("ScriptEngine not supported");
  }
  
  public static Thread newThread(String paramString, boolean paramBoolean, Runnable paramRunnable) {
    Thread thread = new Thread(paramRunnable);
    thread.setDaemon(paramBoolean);
    if (paramString != null)
      thread.setName(paramString); 
    return thread;
  }
  
  public static String replace(String paramString, String... paramVarArgs) {
    for (byte b = 0; b < paramVarArgs.length; b += 2)
      paramString = paramString.replace("%" + paramVarArgs[b] + "%", paramVarArgs[b + 1]); 
    return paramString;
  }
  
  public static String replace(Map<String, String> paramMap, String paramString) {
    for (Map.Entry<String, String> entry : paramMap.entrySet())
      paramString = paramString.replace((CharSequence)entry.getKey(), (CharSequence)entry.getValue()); 
    return paramString;
  }
  
  public static List<String> replace(Map<String, String> paramMap, List<String> paramList) {
    ArrayList<String> arrayList = new ArrayList(paramList.size());
    for (String str : paramList)
      arrayList.add(replace(paramMap, str)); 
    return arrayList;
  }
  
  public static String[] parseCommand(CharSequence paramCharSequence) throws CommandException {
    boolean bool1 = false;
    boolean bool2 = false;
    LinkedList<String> linkedList = new LinkedList();
    StringBuilder stringBuilder = new StringBuilder(100);
    for (byte b = 0; b <= paramCharSequence.length(); b++) {
      boolean bool3 = (b >= paramCharSequence.length()) ? true : false;
      boolean bool4 = bool3 ? false : paramCharSequence.charAt(b);
      if (bool3 || (!bool1 && Character.isWhitespace(bool4))) {
        if (bool3 && bool1)
          throw new CommandException("Quotes wasn't closed"); 
        if (bool2 || !stringBuilder.isEmpty())
          linkedList.add(stringBuilder.toString()); 
        bool2 = false;
        stringBuilder.setLength(0);
      } else {
        char c;
        switch (bool4) {
          case true:
            bool1 = !bool1 ? true : false;
            bool2 = true;
            break;
          case true:
            if (b + 1 >= paramCharSequence.length())
              throw new CommandException("Escape character is not specified"); 
            c = paramCharSequence.charAt(b + 1);
            stringBuilder.append(c);
            b++;
            break;
          default:
            stringBuilder.append(bool4);
            break;
        } 
      } 
    } 
    return linkedList.<String>toArray(new String[0]);
  }
  
  public static GsonBuilder newBuilder() {
    return (new GsonBuilder()).registerTypeHierarchyAdapter(byte[].class, ByteArrayToBase64TypeAdapter.INSTANCE);
  }
  
  public static ArgsParseResult parseJavaArgs(List<String> paramList) {
    ArrayList<String> arrayList1 = new ArrayList();
    ArrayList<String> arrayList2 = new ArrayList();
    ArrayList<String> arrayList3 = new ArrayList();
    String str1 = null;
    String str2 = null;
    String str3 = null;
    LaunchOptions.ModuleConf moduleConf = new LaunchOptions.ModuleConf();
    PrevArgType prevArgType = PrevArgType.NONE;
    boolean bool1 = false;
    boolean bool2 = false;
    for (String str : paramList) {
      byte b;
      String[] arrayOfString1;
      String[] arrayOfString2;
      if (bool1) {
        arrayList3.add(str);
        continue;
      } 
      if (!bool2) {
        if (!str.startsWith("-"))
          continue; 
        bool2 = true;
      } 
      switch (prevArgType.ordinal()) {
        case 1:
          b = 58;
          null = str.indexOf(b);
          if (null < 0)
            b = 59; 
          arrayOfString2 = str.split(Character.toString(b));
          moduleConf.modulePath.addAll(Arrays.asList(arrayOfString2));
          prevArgType = PrevArgType.NONE;
          continue;
        case 6:
          b = 58;
          null = str.indexOf(b);
          if (null < 0)
            b = 59; 
          arrayOfString2 = str.split(Character.toString(b));
          arrayList1.addAll(Arrays.asList(arrayOfString2));
          prevArgType = PrevArgType.POST_CLASSPATH;
          continue;
        case 2:
          arrayOfString1 = str.split(",");
          moduleConf.modules.addAll(Arrays.asList(arrayOfString1));
          prevArgType = PrevArgType.NONE;
          continue;
        case 3:
          arrayOfString1 = str.split("=");
          moduleConf.opens.put(arrayOfString1[0], arrayOfString1[1]);
          prevArgType = PrevArgType.NONE;
          continue;
        case 4:
          arrayOfString1 = str.split("=");
          moduleConf.exports.put(arrayOfString1[0], arrayOfString1[1]);
          prevArgType = PrevArgType.NONE;
          continue;
        case 5:
          arrayOfString1 = str.split("=");
          if (arrayOfString1.length != 2)
            continue; 
          moduleConf.reads.put(arrayOfString1[0], arrayOfString1[1]);
          prevArgType = PrevArgType.NONE;
          continue;
        case 10:
          arrayOfString1 = str.split("/");
          str3 = arrayOfString1[0];
          str2 = arrayOfString1[1];
          bool1 = true;
          prevArgType = PrevArgType.NONE;
          continue;
        case 7:
          str2 = str;
          bool1 = true;
          prevArgType = PrevArgType.NONE;
          continue;
        case 8:
          str1 = str;
          bool1 = true;
          prevArgType = PrevArgType.NONE;
          continue;
      } 
      switch (str) {
        case "--module-path":
        case "-p":
          prevArgType = PrevArgType.MODULE_PATH;
          continue;
        case "--classpath":
        case "-cp":
          prevArgType = PrevArgType.CLASSPATH;
          continue;
        case "--add-modules":
          prevArgType = PrevArgType.ADD_MODULES;
          continue;
        case "--add-opens":
          prevArgType = PrevArgType.ADD_OPENS;
          continue;
        case "--add-exports":
          prevArgType = PrevArgType.ADD_EXPORTS;
          continue;
        case "--add-reads":
          prevArgType = PrevArgType.ADD_READS;
          continue;
        case "--module":
        case "-m":
          prevArgType = PrevArgType.MODULE;
          continue;
        case "-jar":
          prevArgType = PrevArgType.JAR;
          continue;
      } 
      arrayList2.add(str);
    } 
    return new ArgsParseResult(moduleConf, arrayList1, arrayList2, str2, str3, str1, paramList);
  }
  
  public static <K, V> V multimapFirstOrNullValue(K paramK, Map<K, List<V>> paramMap) {
    List list = paramMap.getOrDefault(paramK, Collections.emptyList());
    return (V)(list.isEmpty() ? null : list.getFirst());
  }
  
  static {
  
  }
  
  private static class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
    private static final ByteArrayToBase64TypeAdapter INSTANCE = new ByteArrayToBase64TypeAdapter();
    
    private final Base64.Decoder decoder = Base64.getUrlDecoder();
    
    private final Base64.Encoder encoder = Base64.getUrlEncoder();
    
    public byte[] deserialize(JsonElement param1JsonElement, Type param1Type, JsonDeserializationContext param1JsonDeserializationContext) throws JsonParseException {
      if (param1JsonElement.isJsonArray()) {
        JsonArray jsonArray = param1JsonElement.getAsJsonArray();
        byte[] arrayOfByte = new byte[jsonArray.size()];
        for (byte b = 0; b < arrayOfByte.length; b++)
          arrayOfByte[b] = jsonArray.get(b).getAsByte(); 
        return arrayOfByte;
      } 
      return this.decoder.decode(param1JsonElement.getAsString());
    }
    
    public JsonElement serialize(byte[] param1ArrayOfbyte, Type param1Type, JsonSerializationContext param1JsonSerializationContext) {
      return (JsonElement)new JsonPrimitive(this.encoder.encodeToString(param1ArrayOfbyte));
    }
  }
  
  private enum PrevArgType {
    NONE, MODULE_PATH, ADD_MODULES, ADD_OPENS, ADD_EXPORTS, ADD_READS, CLASSPATH, POST_CLASSPATH, JAR, MAINCLASS, MODULE;
  }
  
  public static final class ArgsParseResult extends Record {
    private final LaunchOptions.ModuleConf conf;
    
    private final List<String> classpath;
    
    private final List<String> jvmArgs;
    
    private final String mainClass;
    
    private final String mainModule;
    
    private final String jarFile;
    
    private final List<String> args;
    
    public ArgsParseResult(LaunchOptions.ModuleConf param1ModuleConf, List<String> param1List1, List<String> param1List2, String param1String1, String param1String2, String param1String3, List<String> param1List3) {
      this.conf = param1ModuleConf;
      this.classpath = param1List1;
      this.jvmArgs = param1List2;
      this.mainClass = param1String1;
      this.mainModule = param1String2;
      this.jarFile = param1String3;
      this.args = param1List3;
    }
    
    public final String toString() {
      // Byte code:
      //   0: aload_0
      //   1: <illegal opcode> toString : (Lpro/gravit/utils/helper/CommonHelper$ArgsParseResult;)Ljava/lang/String;
      //   6: areturn
    }
    
    public final int hashCode() {
      // Byte code:
      //   0: aload_0
      //   1: <illegal opcode> hashCode : (Lpro/gravit/utils/helper/CommonHelper$ArgsParseResult;)I
      //   6: ireturn
    }
    
    public final boolean equals(Object param1Object) {
      // Byte code:
      //   0: aload_0
      //   1: aload_1
      //   2: <illegal opcode> equals : (Lpro/gravit/utils/helper/CommonHelper$ArgsParseResult;Ljava/lang/Object;)Z
      //   7: ireturn
    }
    
    public LaunchOptions.ModuleConf conf() {
      return this.conf;
    }
    
    public List<String> classpath() {
      return this.classpath;
    }
    
    public List<String> jvmArgs() {
      return this.jvmArgs;
    }
    
    public String mainClass() {
      return this.mainClass;
    }
    
    public String mainModule() {
      return this.mainModule;
    }
    
    public String jarFile() {
      return this.jarFile;
    }
    
    public List<String> args() {
      return this.args;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\CommonHelper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */