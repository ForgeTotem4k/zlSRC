package pro.gravit.utils.helper;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class null extends FilterOutputStream {
  null(OutputStream paramOutputStream) {
    super(paramOutputStream);
  }
  
  public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    this.out.write(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public void close() {}
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\IOHelper$2.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */