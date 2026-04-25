package pro.gravit.utils.helper;

import java.util.List;
import pro.gravit.utils.launch.LaunchOptions;

public final class ArgsParseResult extends Record {
  private final LaunchOptions.ModuleConf conf;
  
  private final List<String> classpath;
  
  private final List<String> jvmArgs;
  
  private final String mainClass;
  
  private final String mainModule;
  
  private final String jarFile;
  
  private final List<String> args;
  
  public ArgsParseResult(LaunchOptions.ModuleConf paramModuleConf, List<String> paramList1, List<String> paramList2, String paramString1, String paramString2, String paramString3, List<String> paramList3) {
    this.conf = paramModuleConf;
    this.classpath = paramList1;
    this.jvmArgs = paramList2;
    this.mainClass = paramString1;
    this.mainModule = paramString2;
    this.jarFile = paramString3;
    this.args = paramList3;
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
  
  public final boolean equals(Object paramObject) {
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


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\CommonHelper$ArgsParseResult.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */