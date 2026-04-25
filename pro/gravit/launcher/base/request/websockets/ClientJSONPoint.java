package pro.gravit.launcher.base.request.websockets;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import pro.gravit.launcher.base.Downloader;
import pro.gravit.utils.helper.LogHelper;

public abstract class ClientJSONPoint implements WebSocket.Listener {
  private static boolean isCertificatePinning;
  
  private static final AtomicInteger counter = new AtomicInteger();
  
  private final URI uri;
  
  public boolean isClosed;
  
  private final WebSocket.Builder webSocketBuilder;
  
  protected HttpClient httpClient;
  
  protected WebSocket webSocket;
  
  protected boolean ssl = false;
  
  protected int port;
  
  private final Object syncObject = new Object();
  
  private final Object sendSyncObject = new Object();
  
  private volatile StringBuilder builder = new StringBuilder();
  
  public ClientJSONPoint(String paramString) {
    this(URI.create(paramString));
  }
  
  public ClientJSONPoint(URI paramURI) {
    this.uri = paramURI;
    String str = paramURI.getScheme();
    if (!"ws".equals(str) && !"wss".equals(str))
      throw new IllegalArgumentException("Unsupported protocol: " + str); 
    if ("wss".equals(str))
      this.ssl = true; 
    if (paramURI.getPort() == -1) {
      if ("ws".equals(str)) {
        this.port = 80;
      } else {
        this.port = 443;
      } 
    } else {
      this.port = paramURI.getPort();
    } 
    try {
      HttpClient.Builder builder = HttpClient.newBuilder();
      if (isCertificatePinning)
        builder = builder.sslContext(Downloader.makeSSLContext()); 
      this.httpClient = builder.build();
      this.webSocketBuilder = this.httpClient.newWebSocketBuilder().connectTimeout(Duration.ofSeconds(30L));
    } catch (NoSuchAlgorithmException|java.security.cert.CertificateException|java.security.KeyStoreException|java.io.IOException|java.security.KeyManagementException noSuchAlgorithmException) {
      throw new RuntimeException(noSuchAlgorithmException);
    } 
  }
  
  public void connect() throws Exception {
    this.webSocket = this.webSocketBuilder.buildAsync(this.uri, this).get();
  }
  
  public void openAsync(Runnable paramRunnable, Consumer<Throwable> paramConsumer) {
    this.webSocketBuilder.buildAsync(this.uri, this).thenAccept(paramWebSocket -> {
          this.webSocket = paramWebSocket;
          paramRunnable.run();
        }).exceptionally(paramThrowable -> {
          paramConsumer.accept(paramThrowable);
          return null;
        });
  }
  
  public CompletionStage<?> onText(WebSocket paramWebSocket, CharSequence paramCharSequence, boolean paramBoolean) {
    synchronized (this.syncObject) {
      this.builder.append(paramCharSequence);
      if (paramBoolean) {
        String str = this.builder.toString();
        this.builder = new StringBuilder();
        LogHelper.dev("Received %s", new Object[] { str });
        onMessage(str);
      } 
    } 
    return super.onText(paramWebSocket, paramCharSequence, paramBoolean);
  }
  
  public CompletionStage<?> onClose(WebSocket paramWebSocket, int paramInt, String paramString) {
    onDisconnect(paramInt, paramString);
    return super.onClose(paramWebSocket, paramInt, paramString);
  }
  
  public void onOpen(WebSocket paramWebSocket) {
    onOpen();
    super.onOpen(paramWebSocket);
  }
  
  public CompletionStage<?> onBinary(WebSocket paramWebSocket, ByteBuffer paramByteBuffer, boolean paramBoolean) {
    return super.onBinary(paramWebSocket, paramByteBuffer, paramBoolean);
  }
  
  public void onError(WebSocket paramWebSocket, Throwable paramThrowable) {
    LogHelper.error(paramThrowable);
    super.onError(paramWebSocket, paramThrowable);
  }
  
  public void send(String paramString) {
    LogHelper.dev("Send %s", new Object[] { paramString });
    this.webSocket.sendText(paramString, true);
  }
  
  abstract void onMessage(String paramString);
  
  abstract void onDisconnect(int paramInt, String paramString);
  
  abstract void onOpen();
  
  public void close() {
    this.webSocket.abort();
  }
  
  static {
    isCertificatePinning = false;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\websockets\ClientJSONPoint.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */