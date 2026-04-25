package pro.gravit.launcher.base;

import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

class ProgressTrackingBodySubscriber implements HttpResponse.BodySubscriber<T> {
  private final HttpResponse.BodySubscriber<T> delegate;
  
  private Flow.Subscription subscription;
  
  private boolean isCanceled = false;
  
  public ProgressTrackingBodySubscriber(HttpResponse.BodySubscriber<T> paramBodySubscriber) {
    this.delegate = paramBodySubscriber;
  }
  
  public CompletionStage<T> getBody() {
    return this.delegate.getBody();
  }
  
  public void onSubscribe(Flow.Subscription paramSubscription) {
    this.subscription = paramSubscription;
    if (this.isCanceled)
      paramSubscription.cancel(); 
    this.delegate.onSubscribe(paramSubscription);
  }
  
  public void onNext(List<ByteBuffer> paramList) {
    long l = 0L;
    for (ByteBuffer byteBuffer : paramList)
      l += byteBuffer.remaining(); 
    if (Downloader.ProgressTrackingBodyHandler.this.callback != null)
      Downloader.ProgressTrackingBodyHandler.this.callback.apply(l); 
    this.delegate.onNext(paramList);
  }
  
  public void onError(Throwable paramThrowable) {
    this.delegate.onError(paramThrowable);
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


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\Downloader$ProgressTrackingBodyHandler$ProgressTrackingBodySubscriber.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */