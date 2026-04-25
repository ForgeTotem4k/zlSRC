package pro.gravit.launcher.base;

import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.function.Consumer;

class ConsumerObject {
  Consumer<HttpResponse<Path>> next = null;
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\Downloader$ConsumerObject.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */