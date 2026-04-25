package io.sentry;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

@Internal
public class SpanOptions {
  @Nullable
  private SentryDate startTimestamp = null;
  
  private boolean trimStart = false;
  
  private boolean trimEnd = false;
  
  private boolean isIdle = false;
  
  @Nullable
  protected String origin = "manual";
  
  @Nullable
  public SentryDate getStartTimestamp() {
    return this.startTimestamp;
  }
  
  public void setStartTimestamp(@Nullable SentryDate paramSentryDate) {
    this.startTimestamp = paramSentryDate;
  }
  
  public boolean isTrimStart() {
    return this.trimStart;
  }
  
  public boolean isTrimEnd() {
    return this.trimEnd;
  }
  
  public boolean isIdle() {
    return this.isIdle;
  }
  
  public void setTrimStart(boolean paramBoolean) {
    this.trimStart = paramBoolean;
  }
  
  public void setTrimEnd(boolean paramBoolean) {
    this.trimEnd = paramBoolean;
  }
  
  public void setIdle(boolean paramBoolean) {
    this.isIdle = paramBoolean;
  }
  
  @Nullable
  public String getOrigin() {
    return this.origin;
  }
  
  public void setOrigin(@Nullable String paramString) {
    this.origin = paramString;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SpanOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */