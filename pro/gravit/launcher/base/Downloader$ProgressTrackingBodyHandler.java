package pro.gravit.launcher.base;

import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

public class ProgressTrackingBodyHandler<T> implements HttpResponse.BodyHandler<T> {
  private final HttpResponse.BodyHandler<T> delegate;
  
  private final Downloader.DownloadCallback callback;
  
  private ProgressTrackingBodySubscriber subscriber;
  
  private boolean isCanceled = false;
  
  public ProgressTrackingBodyHandler(HttpResponse.BodyHandler<T> paramBodyHandler, Downloader.DownloadCallback paramDownloadCallback) {
    this.delegate = paramBodyHandler;
    this.callback = paramDownloadCallback;
  }
  
  public HttpResponse.BodySubscriber<T> apply(HttpResponse.ResponseInfo paramResponseInfo) {
    this.subscriber = new ProgressTrackingBodySubscriber(this.delegate.apply(paramResponseInfo));
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


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\Downloader$ProgressTrackingBodyHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */