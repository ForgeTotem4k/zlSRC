package io.sentry.instrumentation.file;

import io.sentry.IScopes;
import io.sentry.ISpan;
import io.sentry.ScopesAdapter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryFileOutputStream extends FileOutputStream {
  @NotNull
  private final FileOutputStream delegate;
  
  @NotNull
  private final FileIOSpanManager spanManager;
  
  public SentryFileOutputStream(@Nullable String paramString) throws FileNotFoundException {
    this((paramString != null) ? new File(paramString) : null, false, (IScopes)ScopesAdapter.getInstance());
  }
  
  public SentryFileOutputStream(@Nullable String paramString, boolean paramBoolean) throws FileNotFoundException {
    this(init((paramString != null) ? new File(paramString) : null, paramBoolean, null, (IScopes)ScopesAdapter.getInstance()));
  }
  
  public SentryFileOutputStream(@Nullable File paramFile) throws FileNotFoundException {
    this(paramFile, false, (IScopes)ScopesAdapter.getInstance());
  }
  
  public SentryFileOutputStream(@Nullable File paramFile, boolean paramBoolean) throws FileNotFoundException {
    this(init(paramFile, paramBoolean, null, (IScopes)ScopesAdapter.getInstance()));
  }
  
  public SentryFileOutputStream(@NotNull FileDescriptor paramFileDescriptor) {
    this(init(paramFileDescriptor, null, (IScopes)ScopesAdapter.getInstance()), paramFileDescriptor);
  }
  
  SentryFileOutputStream(@Nullable File paramFile, boolean paramBoolean, @NotNull IScopes paramIScopes) throws FileNotFoundException {
    this(init(paramFile, paramBoolean, null, paramIScopes));
  }
  
  private SentryFileOutputStream(@NotNull FileOutputStreamInitData paramFileOutputStreamInitData, @NotNull FileDescriptor paramFileDescriptor) {
    super(paramFileDescriptor);
    this.spanManager = new FileIOSpanManager(paramFileOutputStreamInitData.span, paramFileOutputStreamInitData.file, paramFileOutputStreamInitData.options);
    this.delegate = paramFileOutputStreamInitData.delegate;
  }
  
  private SentryFileOutputStream(@NotNull FileOutputStreamInitData paramFileOutputStreamInitData) throws FileNotFoundException {
    super(getFileDescriptor(paramFileOutputStreamInitData.delegate));
    this.spanManager = new FileIOSpanManager(paramFileOutputStreamInitData.span, paramFileOutputStreamInitData.file, paramFileOutputStreamInitData.options);
    this.delegate = paramFileOutputStreamInitData.delegate;
  }
  
  private static FileOutputStreamInitData init(@Nullable File paramFile, boolean paramBoolean, @Nullable FileOutputStream paramFileOutputStream, @NotNull IScopes paramIScopes) throws FileNotFoundException {
    ISpan iSpan = FileIOSpanManager.startSpan(paramIScopes, "file.write");
    if (paramFileOutputStream == null)
      paramFileOutputStream = new FileOutputStream(paramFile, paramBoolean); 
    return new FileOutputStreamInitData(paramFile, paramBoolean, iSpan, paramFileOutputStream, paramIScopes.getOptions());
  }
  
  private static FileOutputStreamInitData init(@NotNull FileDescriptor paramFileDescriptor, @Nullable FileOutputStream paramFileOutputStream, @NotNull IScopes paramIScopes) {
    ISpan iSpan = FileIOSpanManager.startSpan(paramIScopes, "file.write");
    if (paramFileOutputStream == null)
      paramFileOutputStream = new FileOutputStream(paramFileDescriptor); 
    return new FileOutputStreamInitData(null, false, iSpan, paramFileOutputStream, paramIScopes.getOptions());
  }
  
  public void write(int paramInt) throws IOException {
    this.spanManager.performIO(() -> {
          this.delegate.write(paramInt);
          return Integer.valueOf(1);
        });
  }
  
  public void write(byte[] paramArrayOfbyte) throws IOException {
    this.spanManager.performIO(() -> {
          this.delegate.write(paramArrayOfbyte);
          return Integer.valueOf(paramArrayOfbyte.length);
        });
  }
  
  public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    this.spanManager.performIO(() -> {
          this.delegate.write(paramArrayOfbyte, paramInt1, paramInt2);
          return Integer.valueOf(paramInt2);
        });
  }
  
  public void close() throws IOException {
    this.spanManager.finish(this.delegate);
  }
  
  private static FileDescriptor getFileDescriptor(@NotNull FileOutputStream paramFileOutputStream) throws FileNotFoundException {
    try {
      return paramFileOutputStream.getFD();
    } catch (IOException iOException) {
      throw new FileNotFoundException("No file descriptor");
    } 
  }
  
  public static final class Factory {
    public static FileOutputStream create(@NotNull FileOutputStream param1FileOutputStream, @Nullable String param1String) throws FileNotFoundException {
      return new SentryFileOutputStream(SentryFileOutputStream.init((param1String != null) ? new File(param1String) : null, false, param1FileOutputStream, (IScopes)ScopesAdapter.getInstance()));
    }
    
    public static FileOutputStream create(@NotNull FileOutputStream param1FileOutputStream, @Nullable String param1String, boolean param1Boolean) throws FileNotFoundException {
      return new SentryFileOutputStream(SentryFileOutputStream.init((param1String != null) ? new File(param1String) : null, param1Boolean, param1FileOutputStream, (IScopes)ScopesAdapter.getInstance()));
    }
    
    public static FileOutputStream create(@NotNull FileOutputStream param1FileOutputStream, @Nullable File param1File) throws FileNotFoundException {
      return new SentryFileOutputStream(SentryFileOutputStream.init(param1File, false, param1FileOutputStream, (IScopes)ScopesAdapter.getInstance()));
    }
    
    public static FileOutputStream create(@NotNull FileOutputStream param1FileOutputStream, @Nullable File param1File, boolean param1Boolean) throws FileNotFoundException {
      return new SentryFileOutputStream(SentryFileOutputStream.init(param1File, param1Boolean, param1FileOutputStream, (IScopes)ScopesAdapter.getInstance()));
    }
    
    public static FileOutputStream create(@NotNull FileOutputStream param1FileOutputStream, @NotNull FileDescriptor param1FileDescriptor) {
      return new SentryFileOutputStream(SentryFileOutputStream.init(param1FileDescriptor, param1FileOutputStream, (IScopes)ScopesAdapter.getInstance()), param1FileDescriptor);
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\instrumentation\file\SentryFileOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */