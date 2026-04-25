package io.sentry.clientreport;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public enum DiscardReason {
  QUEUE_OVERFLOW("queue_overflow"),
  CACHE_OVERFLOW("cache_overflow"),
  RATELIMIT_BACKOFF("ratelimit_backoff"),
  NETWORK_ERROR("network_error"),
  SAMPLE_RATE("sample_rate"),
  BEFORE_SEND("before_send"),
  EVENT_PROCESSOR("event_processor"),
  BACKPRESSURE("backpressure");
  
  private final String reason;
  
  DiscardReason(String paramString1) {
    this.reason = paramString1;
  }
  
  public String getReason() {
    return this.reason;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\clientreport\DiscardReason.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */