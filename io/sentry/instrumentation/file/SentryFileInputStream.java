package io.sentry.instrumentation.file;

import io.sentry.IScopes;
import io.sentry.ISpan;
import io.sentry.ScopesAdapter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryFileInputStream extends FileInputStream {
  @NotNull
  private final FileInputStream delegate;
  
  @NotNull
  private final FileIOSpanManager spanManager;
  
  public SentryFileInputStream(@Nullable String paramString) throws FileNotFoundException {
    this((paramString != null) ? new File(paramString) : null, (IScopes)ScopesAdapter.getInstance());
  }
  
  public SentryFileInputStream(@Nullable File paramFile) throws FileNotFoundException {
    this(paramFile, (IScopes)ScopesAdapter.getInstance());
  }
  
  public SentryFileInputStream(@NotNull FileDescriptor paramFileDescriptor) {
    this(paramFileDescriptor, (IScopes)ScopesAdapter.getInstance());
  }
  
  SentryFileInputStream(@Nullable File paramFile, @NotNull IScopes paramIScopes) throws FileNotFoundException {
    this(init(paramFile, (FileInputStream)null, paramIScopes));
  }
  
  SentryFileInputStream(@NotNull FileDescriptor paramFileDescriptor, @NotNull IScopes paramIScopes) {
    this(init(paramFileDescriptor, (FileInputStream)null, paramIScopes), paramFileDescriptor);
  }
  
  private SentryFileInputStream(@NotNull FileInputStreamInitData paramFileInputStreamInitData, @NotNull FileDescriptor paramFileDescriptor) {
    super(paramFileDescriptor);
    this.spanManager = new FileIOSpanManager(paramFileInputStreamInitData.span, paramFileInputStreamInitData.file, paramFileInputStreamInitData.options);
    this.delegate = paramFileInputStreamInitData.delegate;
  }
  
  private SentryFileInputStream(@NotNull FileInputStreamInitData paramFileInputStreamInitData) throws FileNotFoundException {
    super(getFileDescriptor(paramFileInputStreamInitData.delegate));
    this.spanManager = new FileIOSpanManager(paramFileInputStreamInitData.span, paramFileInputStreamInitData.file, paramFileInputStreamInitData.options);
    this.delegate = paramFileInputStreamInitData.delegate;
  }
  
  private static FileInputStreamInitData init(@Nullable File paramFile, @Nullable FileInputStream paramFileInputStream, @NotNull IScopes paramIScopes) throws FileNotFoundException {
    ISpan iSpan = FileIOSpanManager.startSpan(paramIScopes, "file.read");
    if (paramFileInputStream == null)
      paramFileInputStream = new FileInputStream(paramFile); 
    return new FileInputStreamInitData(paramFile, iSpan, paramFileInputStream, paramIScopes.getOptions());
  }
  
  private static FileInputStreamInitData init(@NotNull FileDescriptor paramFileDescriptor, @Nullable FileInputStream paramFileInputStream, @NotNull IScopes paramIScopes) {
    ISpan iSpan = FileIOSpanManager.startSpan(paramIScopes, "file.read");
    if (paramFileInputStream == null)
      paramFileInputStream = new FileInputStream(paramFileDescriptor); 
    return new FileInputStreamInitData(null, iSpan, paramFileInputStream, paramIScopes.getOptions());
  }
  
  public int read() throws IOException {
    AtomicInteger atomicInteger = new AtomicInteger(0);
    this.spanManager.performIO(() -> {
          int i = this.delegate.read();
          paramAtomicInteger.set(i);
          return Integer.valueOf((i != -1) ? 1 : 0);
        });
    return atomicInteger.get();
  }
  
  public int read(byte[] paramArrayOfbyte) throws IOException {
    return ((Integer)this.spanManager.<Integer>performIO(() -> Integer.valueOf(this.delegate.read(paramArrayOfbyte)))).intValue();
  }
  
  public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    return ((Integer)this.spanManager.<Integer>performIO(() -> Integer.valueOf(this.delegate.read(paramArrayOfbyte, paramInt1, paramInt2)))).intValue();
  }
  
  public long skip(long paramLong) throws IOException {
    return ((Long)this.spanManager.<Long>performIO(() -> Long.valueOf(this.delegate.skip(paramLong)))).longValue();
  }
  
  public void close() throws IOException {
    this.spanManager.finish(this.delegate);
  }
  
  private static FileDescriptor getFileDescriptor(@NotNull FileInputStream paramFileInputStream) throws FileNotFoundException {
    try {
      return paramFileInputStream.getFD();
    } catch (IOException iOException) {
      throw new FileNotFoundException("No file descriptor");
    } 
  }
  
  public static final class Factory {
    public static FileInputStream create(@NotNull FileInputStream param1FileInputStream, @Nullable String param1String) throws FileNotFoundException {
      return new SentryFileInputStream(SentryFileInputStream.init((param1String != null) ? new File(param1String) : null, param1FileInputStream, (IScopes)ScopesAdapter.getInstance()));
    }
    
    public static FileInputStream create(@NotNull FileInputStream param1FileInputStream, @Nullable File param1File) throws FileNotFoundException {
      return new SentryFileInputStream(SentryFileInputStream.init(param1File, param1FileInputStream, (IScopes)ScopesAdapter.getInstance()));
    }
    
    public static FileInputStream create(@NotNull FileInputStream param1FileInputStream, @NotNull FileDescriptor param1FileDescriptor) {
      return new SentryFileInputStream(SentryFileInputStream.init(param1FileDescriptor, param1FileInputStream, (IScopes)ScopesAdapter.getInstance()), param1FileDescriptor);
    }
    
    static FileInputStream create(@NotNull FileInputStream param1FileInputStream, @Nullable File param1File, @NotNull IScopes param1IScopes) throws FileNotFoundException {
      return new SentryFileInputStream(SentryFileInputStream.init(param1File, param1FileInputStream, param1IScopes));
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\instrumentation\file\SentryFileInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */