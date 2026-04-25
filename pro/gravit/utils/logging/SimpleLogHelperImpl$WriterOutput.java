package pro.gravit.utils.logging;

import java.io.IOException;
import java.io.Writer;
import pro.gravit.utils.helper.LogHelper;

public class WriterOutput implements LogHelper.Output, AutoCloseable {
  private final Writer writer;
  
  public WriterOutput(Writer paramWriter) {
    this.writer = paramWriter;
  }
  
  public void close() throws IOException {
    this.writer.close();
  }
  
  public void println(String paramString) {
    try {
      this.writer.write(paramString + paramString);
      this.writer.flush();
    } catch (IOException iOException) {}
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\logging\SimpleLogHelperImpl$WriterOutput.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */