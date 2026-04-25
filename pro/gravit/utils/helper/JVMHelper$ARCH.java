package pro.gravit.utils.helper;

public enum ARCH {
  X86("x86"),
  X86_64("x86-64"),
  ARM64("arm64"),
  ARM32("arm32");
  
  public final String name;
  
  ARCH(String paramString1) {
    this.name = paramString1;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\JVMHelper$ARCH.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */