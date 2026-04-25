package io.sentry;

import io.sentry.protocol.ViewHierarchy;
import java.io.File;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Attachment {
  @Nullable
  private byte[] bytes;
  
  @Nullable
  private final JsonSerializable serializable;
  
  @Nullable
  private String pathname;
  
  @NotNull
  private final String filename;
  
  @Nullable
  private final String contentType;
  
  private final boolean addToTransactions;
  
  @Nullable
  private String attachmentType = "event.attachment";
  
  private static final String DEFAULT_ATTACHMENT_TYPE = "event.attachment";
  
  private static final String VIEW_HIERARCHY_ATTACHMENT_TYPE = "event.view_hierarchy";
  
  public Attachment(@NotNull byte[] paramArrayOfbyte, @NotNull String paramString) {
    this(paramArrayOfbyte, paramString, (String)null);
  }
  
  public Attachment(@NotNull byte[] paramArrayOfbyte, @NotNull String paramString1, @Nullable String paramString2) {
    this(paramArrayOfbyte, paramString1, paramString2, false);
  }
  
  public Attachment(@NotNull byte[] paramArrayOfbyte, @NotNull String paramString1, @Nullable String paramString2, boolean paramBoolean) {
    this(paramArrayOfbyte, paramString1, paramString2, "event.attachment", paramBoolean);
  }
  
  public Attachment(@NotNull byte[] paramArrayOfbyte, @NotNull String paramString1, @Nullable String paramString2, @Nullable String paramString3, boolean paramBoolean) {
    this.bytes = paramArrayOfbyte;
    this.serializable = null;
    this.filename = paramString1;
    this.contentType = paramString2;
    this.attachmentType = paramString3;
    this.addToTransactions = paramBoolean;
  }
  
  public Attachment(@NotNull JsonSerializable paramJsonSerializable, @NotNull String paramString1, @Nullable String paramString2, @Nullable String paramString3, boolean paramBoolean) {
    this.bytes = null;
    this.serializable = paramJsonSerializable;
    this.filename = paramString1;
    this.contentType = paramString2;
    this.attachmentType = paramString3;
    this.addToTransactions = paramBoolean;
  }
  
  public Attachment(@NotNull String paramString) {
    this(paramString, (new File(paramString)).getName());
  }
  
  public Attachment(@NotNull String paramString1, @NotNull String paramString2) {
    this(paramString1, paramString2, (String)null);
  }
  
  public Attachment(@NotNull String paramString1, @NotNull String paramString2, @Nullable String paramString3) {
    this(paramString1, paramString2, paramString3, "event.attachment", false);
  }
  
  public Attachment(@NotNull String paramString1, @NotNull String paramString2, @Nullable String paramString3, @Nullable String paramString4, boolean paramBoolean) {
    this.pathname = paramString1;
    this.filename = paramString2;
    this.serializable = null;
    this.contentType = paramString3;
    this.attachmentType = paramString4;
    this.addToTransactions = paramBoolean;
  }
  
  public Attachment(@NotNull String paramString1, @NotNull String paramString2, @Nullable String paramString3, boolean paramBoolean) {
    this.pathname = paramString1;
    this.filename = paramString2;
    this.serializable = null;
    this.contentType = paramString3;
    this.addToTransactions = paramBoolean;
  }
  
  public Attachment(@NotNull String paramString1, @NotNull String paramString2, @Nullable String paramString3, boolean paramBoolean, @Nullable String paramString4) {
    this.pathname = paramString1;
    this.filename = paramString2;
    this.serializable = null;
    this.contentType = paramString3;
    this.addToTransactions = paramBoolean;
    this.attachmentType = paramString4;
  }
  
  @Nullable
  public byte[] getBytes() {
    return this.bytes;
  }
  
  @Nullable
  public JsonSerializable getSerializable() {
    return this.serializable;
  }
  
  @Nullable
  public String getPathname() {
    return this.pathname;
  }
  
  @NotNull
  public String getFilename() {
    return this.filename;
  }
  
  @Nullable
  public String getContentType() {
    return this.contentType;
  }
  
  boolean isAddToTransactions() {
    return this.addToTransactions;
  }
  
  @Nullable
  public String getAttachmentType() {
    return this.attachmentType;
  }
  
  @NotNull
  public static Attachment fromScreenshot(byte[] paramArrayOfbyte) {
    return new Attachment(paramArrayOfbyte, "screenshot.png", "image/png", false);
  }
  
  @NotNull
  public static Attachment fromViewHierarchy(ViewHierarchy paramViewHierarchy) {
    return new Attachment((JsonSerializable)paramViewHierarchy, "view-hierarchy.json", "application/json", "event.view_hierarchy", false);
  }
  
  @NotNull
  public static Attachment fromThreadDump(byte[] paramArrayOfbyte) {
    return new Attachment(paramArrayOfbyte, "thread-dump.txt", "text/plain", false);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\Attachment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */