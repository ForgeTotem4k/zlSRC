package io.sentry;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public enum DataCategory {
  All("__all__"),
  Default("default"),
  Error("error"),
  Session("session"),
  Attachment("attachment"),
  Monitor("monitor"),
  Profile("profile"),
  MetricBucket("metric_bucket"),
  Transaction("transaction"),
  Span("span"),
  Security("security"),
  UserReport("user_report"),
  Unknown("unknown");
  
  private final String category;
  
  DataCategory(String paramString1) {
    this.category = paramString1;
  }
  
  public String getCategory() {
    return this.category;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\DataCategory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */