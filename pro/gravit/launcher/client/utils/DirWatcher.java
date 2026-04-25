package pro.gravit.launcher.client.utils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import pro.gravit.launcher.client.ClientLauncherMethods;
import pro.gravit.launcher.core.hasher.FileNameMatcher;
import pro.gravit.launcher.core.hasher.HashedDir;
import pro.gravit.launcher.core.hasher.HashedEntry;
import pro.gravit.launcher.core.hasher.HashedFile;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.JVMHelper;
import pro.gravit.utils.helper.LogHelper;

public final class DirWatcher implements Runnable, AutoCloseable {
  public static final boolean FILE_TREE_SUPPORTED = (JVMHelper.OS_TYPE == JVMHelper.OS.MUSTDIE);
  
  public static final String IGN_OVERFLOW = "launcher.dirwatcher.ignoreOverflows";
  
  private static final WatchEvent.Kind<?>[] KINDS = new WatchEvent.Kind[] { StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE };
  
  private static final boolean PROP_IGN_OVERFLOW = Boolean.parseBoolean(System.getProperty("launcher.dirwatcher.ignoreOverflows", "true"));
  
  private final Path dir;
  
  private final HashedDir hdir;
  
  private final FileNameMatcher matcher;
  
  private final WatchService service;
  
  private final boolean digest;
  
  public DirWatcher(Path paramPath, HashedDir paramHashedDir, FileNameMatcher paramFileNameMatcher, boolean paramBoolean) throws IOException {
    this.dir = Objects.<Path>requireNonNull(paramPath, "dir");
    this.hdir = Objects.<HashedDir>requireNonNull(paramHashedDir, "hdir");
    this.matcher = paramFileNameMatcher;
    this.digest = paramBoolean;
    this.service = paramPath.getFileSystem().newWatchService();
    IOHelper.walk(paramPath, new RegisterFileVisitor(), true);
    LogHelper.subInfo("DirWatcher %s", new Object[] { paramPath.toString() });
  }
  
  private static void handleError(Throwable paramThrowable) {
    LogHelper.error(paramThrowable);
    ClientLauncherMethods.exitLauncher(-123);
  }
  
  private static Deque<String> toPath(Iterable<Path> paramIterable) {
    LinkedList<String> linkedList = new LinkedList();
    for (Path path : paramIterable)
      linkedList.add(path.toString()); 
    return linkedList;
  }
  
  public void close() throws IOException {
    this.service.close();
  }
  
  private void processKey(WatchKey paramWatchKey) throws IOException {
    Path path = (Path)paramWatchKey.watchable();
    for (WatchEvent<Path> watchEvent : paramWatchKey.pollEvents()) {
      WatchEvent.Kind kind = watchEvent.kind();
      if (kind.equals(StandardWatchEventKinds.OVERFLOW)) {
        if (PROP_IGN_OVERFLOW)
          continue; 
        throw new IOException("Overflow");
      } 
      Path path1 = path.resolve(watchEvent.context());
      Deque<String> deque = toPath(this.dir.relativize(path1));
      if (this.matcher != null && !this.matcher.shouldVerify(deque))
        continue; 
      if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
        HashedEntry hashedEntry = this.hdir.resolve(deque);
        if (hashedEntry != null && (hashedEntry.getType() != HashedEntry.Type.FILE || ((HashedFile)hashedEntry).isSame(path1, this.digest)))
          continue; 
      } 
      throw new SecurityException(String.format("Forbidden modification (%s, %d times): '%s'", new Object[] { kind, Integer.valueOf(watchEvent.count()), path1 }));
    } 
    paramWatchKey.reset();
  }
  
  private void processLoop() throws IOException, InterruptedException {
    LogHelper.debug("WatchService start processing");
    while (!Thread.interrupted())
      processKey(this.service.take()); 
    LogHelper.debug("WatchService closed");
  }
  
  public void run() {
    try {
      processLoop();
    } catch (InterruptedException|java.nio.file.ClosedWatchServiceException interruptedException) {
      LogHelper.debug("WatchService closed 2");
    } catch (Throwable throwable) {
      handleError(throwable);
    } 
  }
  
  private final class RegisterFileVisitor extends SimpleFileVisitor<Path> {
    public FileVisitResult preVisitDirectory(Path param1Path, BasicFileAttributes param1BasicFileAttributes) throws IOException {
      FileVisitResult fileVisitResult = super.preVisitDirectory(param1Path, param1BasicFileAttributes);
      param1Path.register(DirWatcher.this.service, DirWatcher.KINDS);
      return fileVisitResult;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\clien\\utils\DirWatcher.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */