package pro.gravit.launcher.core.serialize.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import pro.gravit.launcher.core.serialize.HInput;
import pro.gravit.launcher.core.serialize.HOutput;
import pro.gravit.utils.helper.IOHelper;

public abstract class StreamObject {
  public final byte[] write() throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = IOHelper.newByteArrayOutput();
    try {
      HOutput hOutput = new HOutput(byteArrayOutputStream);
      try {
        write(hOutput);
        hOutput.close();
      } catch (Throwable throwable) {
        try {
          hOutput.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
      byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
      if (byteArrayOutputStream != null)
        byteArrayOutputStream.close(); 
      return arrayOfByte;
    } catch (Throwable throwable) {
      if (byteArrayOutputStream != null)
        try {
          byteArrayOutputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  public abstract void write(HOutput paramHOutput) throws IOException;
  
  static {
  
  }
  
  @FunctionalInterface
  public static interface Adapter<O extends StreamObject> {
    O convert(HInput param1HInput);
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\serialize\stream\StreamObject.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */