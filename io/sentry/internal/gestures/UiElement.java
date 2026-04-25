package io.sentry.internal.gestures;

import io.sentry.util.Objects;
import java.lang.ref.WeakReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UiElement {
  @NotNull
  final WeakReference<Object> viewRef;
  
  @Nullable
  final String className;
  
  @Nullable
  final String resourceName;
  
  @Nullable
  final String tag;
  
  @NotNull
  final String origin;
  
  public UiElement(@Nullable Object paramObject, @Nullable String paramString1, @Nullable String paramString2, @Nullable String paramString3, @NotNull String paramString4) {
    this.viewRef = new WeakReference(paramObject);
    this.className = paramString1;
    this.resourceName = paramString2;
    this.tag = paramString3;
    this.origin = paramString4;
  }
  
  @Nullable
  public String getClassName() {
    return this.className;
  }
  
  @Nullable
  public String getResourceName() {
    return this.resourceName;
  }
  
  @Nullable
  public String getTag() {
    return this.tag;
  }
  
  @NotNull
  public String getOrigin() {
    return this.origin;
  }
  
  @NotNull
  public String getIdentifier() {
    return (this.resourceName != null) ? this.resourceName : (String)Objects.requireNonNull(this.tag, "UiElement.tag can't be null");
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    UiElement uiElement = (UiElement)paramObject;
    return (Objects.equals(this.className, uiElement.className) && Objects.equals(this.resourceName, uiElement.resourceName) && Objects.equals(this.tag, uiElement.tag));
  }
  
  @Nullable
  public Object getView() {
    return this.viewRef.get();
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.viewRef, this.resourceName, this.tag });
  }
  
  public enum Type {
    CLICKABLE, SCROLLABLE;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\internal\gestures\UiElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */