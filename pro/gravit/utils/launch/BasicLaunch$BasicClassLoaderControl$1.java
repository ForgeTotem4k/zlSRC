package pro.gravit.utils.launch;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

class null implements ClassFileTransformer {
  public byte[] transform(ClassLoader paramClassLoader, String paramString, Class<?> paramClass, ProtectionDomain paramProtectionDomain, byte[] paramArrayOfbyte) {
    return transformer.filter(null, paramString) ? transformer.transform(null, paramString, paramProtectionDomain, paramArrayOfbyte) : paramArrayOfbyte;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\launch\BasicLaunch$BasicClassLoaderControl$1.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */