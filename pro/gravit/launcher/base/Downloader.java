package pro.gravit.launcher.base;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import pro.gravit.launcher.core.CertificatePinningTrustManager;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.LogHelper;

public class Downloader {
  private static boolean isCertificatePinning = false;
  
  private static boolean isNoHttp2;
  
  private static volatile SSLSocketFactory sslSocketFactory;
  
  private static volatile SSLContext sslContext;
  
  protected final HttpClient client;
  
  protected final ExecutorService executor;
  
  protected final Queue<DownloadTask> tasks = new ConcurrentLinkedDeque<>();
  
  protected CompletableFuture<Void> future;
  
  protected Downloader(HttpClient paramHttpClient, ExecutorService paramExecutorService) {
    this.client = paramHttpClient;
    this.executor = paramExecutorService;
  }
  
  public static ThreadFactory getDaemonThreadFactory(String paramString) {
    return paramRunnable -> {
        Thread thread = new Thread(paramRunnable);
        thread.setName(paramString);
        thread.setDaemon(true);
        return thread;
      };
  }
  
  public static HttpClient.Builder newHttpClientBuilder() {
    try {
      return isCertificatePinning ? HttpClient.newBuilder().sslContext(makeSSLContext()).version(isNoHttp2 ? HttpClient.Version.HTTP_1_1 : HttpClient.Version.HTTP_2).followRedirects(HttpClient.Redirect.NORMAL) : HttpClient.newBuilder().version(isNoHttp2 ? HttpClient.Version.HTTP_1_1 : HttpClient.Version.HTTP_2).followRedirects(HttpClient.Redirect.NORMAL);
    } catch (NoSuchAlgorithmException|CertificateException|KeyStoreException|IOException|KeyManagementException noSuchAlgorithmException) {
      throw new RuntimeException(noSuchAlgorithmException);
    } 
  }
  
  public static SSLSocketFactory makeSSLSocketFactory() throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException, KeyManagementException {
    if (sslSocketFactory != null)
      return sslSocketFactory; 
    SSLContext sSLContext = makeSSLContext();
    sslSocketFactory = sSLContext.getSocketFactory();
    return sslSocketFactory;
  }
  
  public static SSLContext makeSSLContext() throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException, KeyManagementException {
    if (sslContext != null)
      return sslContext; 
    SSLContext sSLContext = SSLContext.getInstance("TLS");
    sSLContext.init(null, CertificatePinningTrustManager.getTrustManager().getTrustManagers(), new SecureRandom());
    return sSLContext;
  }
  
  public static Downloader downloadFile(URI paramURI, Path paramPath, ExecutorService paramExecutorService) {
    boolean bool = false;
    if (paramExecutorService == null) {
      paramExecutorService = Executors.newSingleThreadExecutor(getDaemonThreadFactory("Downloader"));
      bool = true;
    } 
    Downloader downloader = newDownloader(paramExecutorService);
    downloader.future = downloader.downloadFile(paramURI, paramPath);
    if (bool) {
      ExecutorService executorService = paramExecutorService;
      downloader.future = downloader.future.thenAccept(paramVoid -> paramExecutorService.shutdownNow()).exceptionallyCompose(paramThrowable -> {
            paramExecutorService.shutdownNow();
            return CompletableFuture.failedFuture(paramThrowable);
          });
    } 
    return downloader;
  }
  
  public static Downloader downloadList(List<SizedFile> paramList, String paramString, Path paramPath, DownloadCallback paramDownloadCallback, ExecutorService paramExecutorService, int paramInt) throws Exception {
    boolean bool = false;
    LogHelper.info("Download with Java 11+ HttpClient");
    if (paramExecutorService == null) {
      paramExecutorService = Executors.newWorkStealingPool(Math.min(3, paramInt));
      bool = true;
    } 
    Downloader downloader = newDownloader(paramExecutorService);
    downloader.future = downloader.downloadFiles(paramList, paramString, paramPath, paramDownloadCallback, paramExecutorService, paramInt);
    if (bool) {
      ExecutorService executorService = paramExecutorService;
      downloader.future = downloader.future.thenAccept(paramVoid -> paramExecutorService.shutdownNow()).exceptionallyCompose(paramThrowable -> {
            paramExecutorService.shutdownNow();
            return CompletableFuture.failedFuture(paramThrowable);
          });
    } 
    return downloader;
  }
  
  public static Downloader newDownloader(ExecutorService paramExecutorService) {
    if (paramExecutorService == null)
      throw new NullPointerException(); 
    HttpClient.Builder builder = newHttpClientBuilder().executor(paramExecutorService);
    HttpClient httpClient = builder.build();
    return new Downloader(httpClient, paramExecutorService);
  }
  
  public void cancel() {
    for (DownloadTask downloadTask : this.tasks) {
      if (!downloadTask.isCompleted())
        downloadTask.cancel(); 
    } 
    this.tasks.clear();
  }
  
  public boolean isCanceled() {
    return this.executor.isTerminated();
  }
  
  public CompletableFuture<Void> getFuture() {
    return this.future;
  }
  
  public CompletableFuture<Void> downloadFile(URI paramURI, Path paramPath) {
    try {
      IOHelper.createParentDirs(paramPath);
    } catch (IOException iOException) {
      return CompletableFuture.failedFuture(iOException);
    } 
    return this.client.<Path>sendAsync(HttpRequest.newBuilder().GET().uri(paramURI).build(), HttpResponse.BodyHandlers.ofFile(paramPath, new OpenOption[] { StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING })).thenCompose(paramHttpResponse -> (paramHttpResponse.statusCode() < 200 || paramHttpResponse.statusCode() >= 400) ? CompletableFuture.failedFuture(new IOException(String.format("Failed to download %s: code %d", new Object[] { paramURI.toString(), Integer.valueOf(paramHttpResponse.statusCode()) }))) : CompletableFuture.completedFuture(null));
  }
  
  public CompletableFuture<Void> downloadFile(String paramString, Path paramPath, DownloadCallback paramDownloadCallback, ExecutorService paramExecutorService) throws Exception {
    return downloadFiles(new ArrayList<>(List.of(new SizedFile(paramString, paramPath.getFileName().toString()))), null, paramPath.getParent(), paramDownloadCallback, paramExecutorService, 1);
  }
  
  public CompletableFuture<Void> downloadFile(String paramString, Path paramPath, long paramLong, DownloadCallback paramDownloadCallback, ExecutorService paramExecutorService) throws Exception {
    return downloadFiles(new ArrayList<>(List.of(new SizedFile(paramString, paramPath.getFileName().toString(), paramLong))), null, paramPath.getParent(), paramDownloadCallback, paramExecutorService, 1);
  }
  
  public CompletableFuture<Void> downloadFiles(List<SizedFile> paramList, String paramString, Path paramPath, DownloadCallback paramDownloadCallback, ExecutorService paramExecutorService, int paramInt) throws Exception {
    URI uRI = (paramString == null) ? null : new URI(paramString);
    Collections.shuffle(paramList);
    ConcurrentLinkedDeque<SizedFile> concurrentLinkedDeque = new ConcurrentLinkedDeque<>(paramList);
    CompletableFuture<Void> completableFuture = new CompletableFuture();
    AtomicInteger atomicInteger = new AtomicInteger(paramInt);
    ConsumerObject consumerObject = new ConsumerObject();
    Consumer<HttpResponse<Path>> consumer = paramHttpResponse -> {
        if (paramDownloadCallback != null && paramHttpResponse != null)
          paramDownloadCallback.onComplete(paramHttpResponse.body()); 
        SizedFile sizedFile = paramQueue.poll();
        if (sizedFile == null) {
          if (paramAtomicInteger.decrementAndGet() == 0)
            paramCompletableFuture.complete(null); 
          return;
        } 
        try {
          DownloadTask downloadTask = sendAsync(sizedFile, paramURI, paramPath, paramDownloadCallback);
          downloadTask.completableFuture.<HttpResponse<Path>>thenCompose(()).thenAccept(paramConsumerObject.next).exceptionally(());
        } catch (Exception exception) {
          LogHelper.error(exception);
          paramCompletableFuture.completeExceptionally(exception);
        } 
      };
    consumerObject.next = consumer;
    for (byte b = 0; b < paramInt; b++)
      consumer.accept(null); 
    return completableFuture;
  }
  
  protected DownloadTask sendAsync(SizedFile paramSizedFile, URI paramURI, Path paramPath, DownloadCallback paramDownloadCallback) throws Exception {
    IOHelper.createParentDirs(paramPath.resolve(paramSizedFile.filePath));
    ProgressTrackingBodyHandler<Path> progressTrackingBodyHandler = makeBodyHandler(paramPath.resolve(paramSizedFile.filePath), paramDownloadCallback);
    CompletableFuture<HttpResponse<Path>> completableFuture = this.client.sendAsync(makeHttpRequest(paramURI, paramSizedFile.urlPath), progressTrackingBodyHandler);
    AtomicReference<DownloadTask> atomicReference = new AtomicReference(null);
    atomicReference.set(new DownloadTask(progressTrackingBodyHandler, null));
    this.tasks.add(atomicReference.get());
    ((DownloadTask)atomicReference.get()).completableFuture = completableFuture.thenApply(paramHttpResponse -> {
          this.tasks.remove(paramAtomicReference.get());
          return paramHttpResponse;
        });
    return atomicReference.get();
  }
  
  protected HttpRequest makeHttpRequest(URI paramURI, String paramString) throws URISyntaxException {
    URI uRI;
    if (paramURI != null) {
      String str1 = paramURI.getScheme();
      String str2 = paramURI.getHost();
      int i = paramURI.getPort();
      if (i != -1)
        str2 = str2 + ":" + str2; 
      String str3 = paramURI.getPath();
      uRI = new URI(str1, str2, str3 + str3, "", "");
    } else {
      uRI = new URI(paramString);
    } 
    return HttpRequest.newBuilder().GET().uri(uRI).header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36").build();
  }
  
  protected ProgressTrackingBodyHandler<Path> makeBodyHandler(Path paramPath, DownloadCallback paramDownloadCallback) {
    return new ProgressTrackingBodyHandler<>(HttpResponse.BodyHandlers.ofFile(paramPath, new OpenOption[] { StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING }), paramDownloadCallback);
  }
  
  public static interface DownloadCallback {
    void apply(long param1Long);
    
    void onComplete(Path param1Path);
    
    static {
    
    }
  }
  
  public static class DownloadTask {
    public final Downloader.ProgressTrackingBodyHandler<Path> bodyHandler;
    
    public CompletableFuture<HttpResponse<Path>> completableFuture;
    
    public DownloadTask(Downloader.ProgressTrackingBodyHandler<Path> param1ProgressTrackingBodyHandler, CompletableFuture<HttpResponse<Path>> param1CompletableFuture) {
      this.bodyHandler = param1ProgressTrackingBodyHandler;
      this.completableFuture = param1CompletableFuture;
    }
    
    public boolean isCompleted() {
      return this.completableFuture.isDone() | this.completableFuture.isCompletedExceptionally();
    }
    
    public void cancel() {
      this.bodyHandler.cancel();
    }
  }
  
  public static class SizedFile {
    public final String urlPath;
    
    public final String filePath;
    
    public final long size;
    
    public SizedFile(String param1String, long param1Long) {
      this.urlPath = param1String;
      this.filePath = param1String;
      this.size = param1Long;
    }
    
    public SizedFile(String param1String1, String param1String2, long param1Long) {
      this.urlPath = param1String1;
      this.filePath = param1String2;
      this.size = param1Long;
    }
    
    public SizedFile(String param1String1, String param1String2) {
      this.urlPath = param1String1;
      this.filePath = param1String2;
      this.size = -1L;
    }
  }
  
  private static class ConsumerObject {
    Consumer<HttpResponse<Path>> next = null;
  }
  
  public static class ProgressTrackingBodyHandler<T> implements HttpResponse.BodyHandler<T> {
    private final HttpResponse.BodyHandler<T> delegate;
    
    private final Downloader.DownloadCallback callback;
    
    private ProgressTrackingBodySubscriber subscriber;
    
    private boolean isCanceled = false;
    
    public ProgressTrackingBodyHandler(HttpResponse.BodyHandler<T> param1BodyHandler, Downloader.DownloadCallback param1DownloadCallback) {
      this.delegate = param1BodyHandler;
      this.callback = param1DownloadCallback;
    }
    
    public HttpResponse.BodySubscriber<T> apply(HttpResponse.ResponseInfo param1ResponseInfo) {
      this.subscriber = new ProgressTrackingBodySubscriber(this.delegate.apply(param1ResponseInfo));
      if (this.isCanceled)
        this.subscriber.cancel(); 
      return this.subscriber;
    }
    
    public void cancel() {
      this.isCanceled = true;
      if (this.subscriber != null)
        this.subscriber.cancel(); 
    }
    
    private class ProgressTrackingBodySubscriber implements HttpResponse.BodySubscriber<T> {
      private final HttpResponse.BodySubscriber<T> delegate;
      
      private Flow.Subscription subscription;
      
      private boolean isCanceled = false;
      
      public ProgressTrackingBodySubscriber(HttpResponse.BodySubscriber<T> param2BodySubscriber) {
        this.delegate = param2BodySubscriber;
      }
      
      public CompletionStage<T> getBody() {
        return this.delegate.getBody();
      }
      
      public void onSubscribe(Flow.Subscription param2Subscription) {
        this.subscription = param2Subscription;
        if (this.isCanceled)
          param2Subscription.cancel(); 
        this.delegate.onSubscribe(param2Subscription);
      }
      
      public void onNext(List<ByteBuffer> param2List) {
        long l = 0L;
        for (ByteBuffer byteBuffer : param2List)
          l += byteBuffer.remaining(); 
        if (Downloader.ProgressTrackingBodyHandler.this.callback != null)
          Downloader.ProgressTrackingBodyHandler.this.callback.apply(l); 
        this.delegate.onNext(param2List);
      }
      
      public void onError(Throwable param2Throwable) {
        this.delegate.onError(param2Throwable);
      }
      
      public void onComplete() {
        this.delegate.onComplete();
      }
      
      public void cancel() {
        this.isCanceled = true;
        if (this.subscription != null)
          this.subscription.cancel(); 
      }
    }
  }
  
  private class ProgressTrackingBodySubscriber implements HttpResponse.BodySubscriber<T> {
    private final HttpResponse.BodySubscriber<T> delegate;
    
    private Flow.Subscription subscription;
    
    private boolean isCanceled = false;
    
    public ProgressTrackingBodySubscriber(HttpResponse.BodySubscriber<T> param1BodySubscriber) {
      this.delegate = param1BodySubscriber;
    }
    
    public CompletionStage<T> getBody() {
      return this.delegate.getBody();
    }
    
    public void onSubscribe(Flow.Subscription param1Subscription) {
      this.subscription = param1Subscription;
      if (this.isCanceled)
        param1Subscription.cancel(); 
      this.delegate.onSubscribe(param1Subscription);
    }
    
    public void onNext(List<ByteBuffer> param1List) {
      long l = 0L;
      for (ByteBuffer byteBuffer : param1List)
        l += byteBuffer.remaining(); 
      if (this.this$0.callback != null)
        this.this$0.callback.apply(l); 
      this.delegate.onNext(param1List);
    }
    
    public void onError(Throwable param1Throwable) {
      this.delegate.onError(param1Throwable);
    }
    
    public void onComplete() {
      this.delegate.onComplete();
    }
    
    public void cancel() {
      this.isCanceled = true;
      if (this.subscription != null)
        this.subscription.cancel(); 
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\Downloader.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */