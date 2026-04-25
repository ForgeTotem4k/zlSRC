package io.sentry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Hint {
  @NotNull
  private static final Map<String, Class<?>> PRIMITIVE_MAPPINGS = new HashMap<>();
  
  @NotNull
  private final Map<String, Object> internalStorage = new HashMap<>();
  
  @NotNull
  private final List<Attachment> attachments = new ArrayList<>();
  
  @Nullable
  private Attachment screenshot = null;
  
  @Nullable
  private Attachment viewHierarchy = null;
  
  @Nullable
  private Attachment threadDump = null;
  
  @NotNull
  public static Hint withAttachment(@Nullable Attachment paramAttachment) {
    Hint hint = new Hint();
    hint.addAttachment(paramAttachment);
    return hint;
  }
  
  @NotNull
  public static Hint withAttachments(@Nullable List<Attachment> paramList) {
    Hint hint = new Hint();
    hint.addAttachments(paramList);
    return hint;
  }
  
  public synchronized void set(@NotNull String paramString, @Nullable Object paramObject) {
    this.internalStorage.put(paramString, paramObject);
  }
  
  @Nullable
  public synchronized Object get(@NotNull String paramString) {
    return this.internalStorage.get(paramString);
  }
  
  @Nullable
  public synchronized <T> T getAs(@NotNull String paramString, @NotNull Class<T> paramClass) {
    Object object = this.internalStorage.get(paramString);
    return (T)(paramClass.isInstance(object) ? object : (isCastablePrimitive(object, paramClass) ? object : null));
  }
  
  public synchronized void remove(@NotNull String paramString) {
    this.internalStorage.remove(paramString);
  }
  
  public void addAttachment(@Nullable Attachment paramAttachment) {
    if (paramAttachment != null)
      this.attachments.add(paramAttachment); 
  }
  
  public void addAttachments(@Nullable List<Attachment> paramList) {
    if (paramList != null)
      this.attachments.addAll(paramList); 
  }
  
  @NotNull
  public List<Attachment> getAttachments() {
    return new ArrayList<>(this.attachments);
  }
  
  public void replaceAttachments(@Nullable List<Attachment> paramList) {
    clearAttachments();
    addAttachments(paramList);
  }
  
  public void clearAttachments() {
    this.attachments.clear();
  }
  
  @Internal
  public synchronized void clear() {
    Iterator<Map.Entry> iterator = this.internalStorage.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry entry = iterator.next();
      if (entry.getKey() == null || !((String)entry.getKey()).startsWith("sentry:"))
        iterator.remove(); 
    } 
  }
  
  public void setScreenshot(@Nullable Attachment paramAttachment) {
    this.screenshot = paramAttachment;
  }
  
  @Nullable
  public Attachment getScreenshot() {
    return this.screenshot;
  }
  
  public void setViewHierarchy(@Nullable Attachment paramAttachment) {
    this.viewHierarchy = paramAttachment;
  }
  
  @Nullable
  public Attachment getViewHierarchy() {
    return this.viewHierarchy;
  }
  
  public void setThreadDump(@Nullable Attachment paramAttachment) {
    this.threadDump = paramAttachment;
  }
  
  @Nullable
  public Attachment getThreadDump() {
    return this.threadDump;
  }
  
  private boolean isCastablePrimitive(@Nullable Object paramObject, @NotNull Class<?> paramClass) {
    Class clazz = PRIMITIVE_MAPPINGS.get(paramClass.getCanonicalName());
    return (paramObject != null && paramClass.isPrimitive() && clazz != null && clazz.isInstance(paramObject));
  }
  
  static {
    PRIMITIVE_MAPPINGS.put("boolean", Boolean.class);
    PRIMITIVE_MAPPINGS.put("char", Character.class);
    PRIMITIVE_MAPPINGS.put("byte", Byte.class);
    PRIMITIVE_MAPPINGS.put("short", Short.class);
    PRIMITIVE_MAPPINGS.put("int", Integer.class);
    PRIMITIVE_MAPPINGS.put("long", Long.class);
    PRIMITIVE_MAPPINGS.put("float", Float.class);
    PRIMITIVE_MAPPINGS.put("double", Double.class);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\Hint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */