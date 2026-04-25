package pro.gravit.utils.logging;

import java.io.OutputStream;
import pro.gravit.utils.helper.IOHelper;

public final class JAnsiOutput extends SimpleLogHelperImpl.WriterOutput {
  private JAnsiOutput(OutputStream paramOutputStream) {
    super(IOHelper.newWriter(paramOutputStream));
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\logging\SimpleLogHelperImpl$JAnsiOutput.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */