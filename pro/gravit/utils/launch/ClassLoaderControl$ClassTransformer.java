package pro.gravit.utils.launch;

import java.security.ProtectionDomain;

public interface ClassTransformer {
  boolean filter(String paramString1, String paramString2);
  
  byte[] transform(String paramString1, String paramString2, ProtectionDomain paramProtectionDomain, byte[] paramArrayOfbyte);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\launch\ClassLoaderControl$ClassTransformer.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */