package pro.gravit.launcher.gui.scenes.update;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import pro.gravit.launcher.base.Downloader;
import pro.gravit.launcher.base.events.request.UpdateRequestEvent;
import pro.gravit.launcher.base.profiles.optional.OptionalView;
import pro.gravit.launcher.base.profiles.optional.actions.OptionalAction;
import pro.gravit.launcher.base.profiles.optional.actions.OptionalActionFile;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.update.UpdateRequest;
import pro.gravit.launcher.core.hasher.FileNameMatcher;
import pro.gravit.launcher.core.hasher.HashedDir;
import pro.gravit.launcher.core.hasher.HashedEntry;
import pro.gravit.launcher.core.hasher.HashedFile;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.impl.ContextHelper;
import pro.gravit.launcher.gui.utils.AssetIndexHelper;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.LogHelper;

public class VisualDownloader {
  private final JavaFXApplication application;
  
  private final AtomicLong totalDownloaded = new AtomicLong(0L);
  
  private final AtomicLong lastUpdateTime = new AtomicLong(0L);
  
  private final AtomicLong lastDownloaded = new AtomicLong(0L);
  
  private final AtomicLong totalSize = new AtomicLong();
  
  private volatile Downloader downloader;
  
  private final ProgressBar progressBar;
  
  private final Label speed;
  
  private final Label volume;
  
  private final Consumer<Throwable> errorHandle;
  
  private final Consumer<String> addLog;
  
  private final Consumer<UpdateScene.DownloadStatus> updateStatus;
  
  private final ExecutorService executor;
  
  public VisualDownloader(JavaFXApplication paramJavaFXApplication, ProgressBar paramProgressBar, Label paramLabel1, Label paramLabel2, Consumer<Throwable> paramConsumer, Consumer<String> paramConsumer1, Consumer<UpdateScene.DownloadStatus> paramConsumer2) {
    this.application = paramJavaFXApplication;
    this.progressBar = paramProgressBar;
    this.speed = paramLabel1;
    this.volume = paramLabel2;
    this.errorHandle = paramConsumer;
    this.addLog = paramConsumer1;
    this.executor = new ForkJoinPool(paramJavaFXApplication.guiModuleConfig.downloadThreads, paramForkJoinPool -> {
          ForkJoinWorkerThread forkJoinWorkerThread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(paramForkJoinPool);
          forkJoinWorkerThread.setDaemon(true);
          return forkJoinWorkerThread;
        }null, true);
    this.updateStatus = paramConsumer2;
  }
  
  public void sendUpdateAssetRequest(String paramString1, Path paramPath, FileNameMatcher paramFileNameMatcher, boolean paramBoolean1, String paramString2, boolean paramBoolean2, Consumer<HashedDir> paramConsumer) {
    if (paramBoolean2) {
      this.addLog.accept("Hashing %s".formatted(new Object[] { paramString1 }));
      this.updateStatus.accept(UpdateScene.DownloadStatus.HASHING);
      this.application.workers.submit(() -> {
            try {
              HashedDir hashedDir = new HashedDir(paramPath, paramFileNameMatcher, false, paramBoolean);
              this.updateStatus.accept(UpdateScene.DownloadStatus.COMPLETE);
              paramConsumer.accept(hashedDir);
            } catch (IOException iOException) {
              this.updateStatus.accept(UpdateScene.DownloadStatus.ERROR);
              this.errorHandle.accept(iOException);
            } 
          });
      return;
    } 
    UpdateRequest updateRequest = new UpdateRequest(paramString1);
    try {
      this.updateStatus.accept(UpdateScene.DownloadStatus.REQUEST);
      this.application.service.request((Request)updateRequest).thenAccept(paramUpdateRequestEvent -> {
            LogHelper.dev("Start updating %s", new Object[] { paramString1 });
            try {
              downloadAsset(paramString1, paramPath, paramFileNameMatcher, paramBoolean, paramString2, paramConsumer, paramUpdateRequestEvent.hdir, paramUpdateRequestEvent.url);
            } catch (Exception exception) {
              this.updateStatus.accept(UpdateScene.DownloadStatus.ERROR);
              ContextHelper.runInFxThreadStatic(());
            } 
          }).exceptionally(paramThrowable -> {
            this.updateStatus.accept(UpdateScene.DownloadStatus.ERROR);
            ContextHelper.runInFxThreadStatic(());
            return null;
          });
    } catch (IOException iOException) {
      this.updateStatus.accept(UpdateScene.DownloadStatus.ERROR);
      this.errorHandle.accept(iOException);
    } 
  }
  
  public void sendUpdateRequest(String paramString, Path paramPath, FileNameMatcher paramFileNameMatcher, boolean paramBoolean1, OptionalView paramOptionalView, boolean paramBoolean2, boolean paramBoolean3, Consumer<HashedDir> paramConsumer) {
    if (paramBoolean3) {
      this.addLog.accept("Hashing %s".formatted(new Object[] { paramString }));
      this.updateStatus.accept(UpdateScene.DownloadStatus.HASHING);
      this.application.workers.submit(() -> {
            try {
              HashedDir hashedDir = new HashedDir(paramPath, paramFileNameMatcher, false, paramBoolean);
              this.updateStatus.accept(UpdateScene.DownloadStatus.COMPLETE);
              paramConsumer.accept(hashedDir);
            } catch (IOException iOException) {
              this.updateStatus.accept(UpdateScene.DownloadStatus.ERROR);
              this.errorHandle.accept(iOException);
            } 
          });
      return;
    } 
    UpdateRequest updateRequest = new UpdateRequest(paramString);
    try {
      this.updateStatus.accept(UpdateScene.DownloadStatus.REQUEST);
      this.application.service.request((Request)updateRequest).thenAccept(paramUpdateRequestEvent -> {
            LogHelper.dev("Start updating %s", new Object[] { paramString });
            try {
              download(paramString, paramPath, paramFileNameMatcher, paramBoolean1, paramOptionalView, paramBoolean2, paramConsumer, paramUpdateRequestEvent.hdir, paramUpdateRequestEvent.url);
            } catch (Exception exception) {
              this.updateStatus.accept(UpdateScene.DownloadStatus.ERROR);
              ContextHelper.runInFxThreadStatic(());
            } 
          }).exceptionally(paramThrowable -> {
            this.updateStatus.accept(UpdateScene.DownloadStatus.ERROR);
            ContextHelper.runInFxThreadStatic(());
            return null;
          });
    } catch (IOException iOException) {
      this.updateStatus.accept(UpdateScene.DownloadStatus.ERROR);
      this.errorHandle.accept(iOException);
    } 
  }
  
  private void download(String paramString1, Path paramPath, FileNameMatcher paramFileNameMatcher, boolean paramBoolean1, OptionalView paramOptionalView, boolean paramBoolean2, Consumer<HashedDir> paramConsumer, HashedDir paramHashedDir, String paramString2) throws Exception {
    LinkedList<PathRemapperData> linkedList = paramBoolean2 ? getPathRemapper(paramOptionalView, paramHashedDir) : new LinkedList();
    this.addLog.accept("Hashing %s".formatted(new Object[] { paramString1 }));
    this.updateStatus.accept(UpdateScene.DownloadStatus.HASHING);
    if (!IOHelper.exists(paramPath))
      Files.createDirectories(paramPath, (FileAttribute<?>[])new FileAttribute[0]); 
    HashedDir hashedDir = new HashedDir(paramPath, paramFileNameMatcher, false, paramBoolean1);
    HashedDir.Diff diff = paramHashedDir.diff(hashedDir, paramFileNameMatcher);
    List<Downloader.SizedFile> list = getFilesList(paramPath, linkedList, diff.mismatch);
    LogHelper.info("Diff %d %d", new Object[] { Long.valueOf(diff.mismatch.size()), Long.valueOf(diff.extra.size()) });
    this.addLog.accept("Downloading %s...".formatted(new Object[] { paramString1 }));
    downloadFiles(paramPath, list, paramString2, () -> {
          try {
            this.addLog.accept("Delete Extra files %s".formatted(new Object[] { paramString }));
            this.updateStatus.accept(UpdateScene.DownloadStatus.DELETE);
            deleteExtraDir(paramPath, paramDiff.extra, paramDiff.extra.flag);
            paramConsumer.accept(paramHashedDir);
          } catch (IOException iOException) {
            this.updateStatus.accept(UpdateScene.DownloadStatus.ERROR);
            this.errorHandle.accept(iOException);
          } 
        });
  }
  
  private void downloadAsset(String paramString1, Path paramPath, FileNameMatcher paramFileNameMatcher, boolean paramBoolean, String paramString2, Consumer<HashedDir> paramConsumer, HashedDir paramHashedDir, String paramString3) throws Exception {
    boolean bool;
    LinkedList linkedList = new LinkedList();
    this.addLog.accept("Check assetIndex %s".formatted(new Object[] { paramString2 }));
    if (!IOHelper.exists(paramPath))
      Files.createDirectories(paramPath, (FileAttribute<?>[])new FileAttribute[0]); 
    Consumer<HashedDir> consumer = paramHashedDir -> {
        try {
          HashedDir hashedDir = new HashedDir(paramPath, paramFileNameMatcher, false, paramBoolean);
          HashedDir.Diff diff = paramHashedDir.diff(hashedDir, paramFileNameMatcher);
          List<Downloader.SizedFile> list = getFilesList(paramPath, paramLinkedList, diff.mismatch);
          LogHelper.info("Diff %d %d", new Object[] { Long.valueOf(diff.mismatch.size()), Long.valueOf(diff.extra.size()) });
          this.addLog.accept("Downloading %s...".formatted(new Object[] { paramString1 }));
          downloadFiles(paramPath, list, paramString2, ());
        } catch (Throwable throwable) {
          this.updateStatus.accept(UpdateScene.DownloadStatus.ERROR);
          this.errorHandle.accept(throwable);
        } 
      };
    String str = "indexes/".concat(paramString2).concat(".json");
    Path path = paramPath.resolve(str);
    HashedDir.FindRecursiveResult findRecursiveResult = paramHashedDir.findRecursive(str);
    if (!(findRecursiveResult.entry instanceof HashedFile)) {
      this.addLog.accept("ERROR: assetIndex %s not found".formatted(new Object[] { paramString2 }));
      this.updateStatus.accept(UpdateScene.DownloadStatus.ERROR);
      this.errorHandle.accept(new RuntimeException("assetIndex not found"));
      return;
    } 
    if (Files.exists(path, new java.nio.file.LinkOption[0])) {
      HashedFile hashedFile = new HashedFile(path, Files.size(path), true);
      bool = !((HashedFile)findRecursiveResult.entry).isSame(hashedFile) ? true : false;
    } else {
      IOHelper.createParentDirs(path);
      bool = true;
    } 
    if (bool) {
      ArrayList<Downloader.SizedFile> arrayList = new ArrayList(1);
      arrayList.add(new Downloader.SizedFile(str, ((HashedFile)findRecursiveResult.entry).size));
      downloadFiles(paramPath, arrayList, paramString3, () -> {
            try {
              AssetIndexHelper.AssetIndex assetIndex = AssetIndexHelper.parse(paramPath);
              AssetIndexHelper.modifyHashedDir(assetIndex, paramHashedDir);
              paramConsumer.accept(paramHashedDir);
            } catch (Exception exception) {
              this.updateStatus.accept(UpdateScene.DownloadStatus.ERROR);
              this.errorHandle.accept(exception);
            } 
          });
    } else {
      try {
        AssetIndexHelper.AssetIndex assetIndex = AssetIndexHelper.parse(path);
        AssetIndexHelper.modifyHashedDir(assetIndex, paramHashedDir);
        consumer.accept(paramHashedDir);
      } catch (Exception exception) {
        this.updateStatus.accept(UpdateScene.DownloadStatus.ERROR);
        this.errorHandle.accept(exception);
      } 
    } 
  }
  
  private void downloadFiles(Path paramPath, List<Downloader.SizedFile> paramList, String paramString, Runnable paramRunnable) {
    ContextHelper.runInFxThreadStatic(this::resetProgress).thenAccept(paramVoid -> {
          try {
            this.downloader = Downloader.downloadList(paramList, paramString, paramPath, new Downloader.DownloadCallback() {
                  public void apply(long param1Long) {
                    long l = VisualDownloader.this.totalDownloaded.getAndAdd(param1Long);
                    VisualDownloader.this.updateProgress(l, l + param1Long);
                  }
                  
                  public void onComplete(Path param1Path) {}
                }this.executor, this.application.guiModuleConfig.downloadThreads);
            this.updateStatus.accept(UpdateScene.DownloadStatus.DOWNLOAD);
            this.downloader.getFuture().thenAccept(()).exceptionally(());
          } catch (Throwable throwable) {
            this.updateStatus.accept(UpdateScene.DownloadStatus.ERROR);
            ContextHelper.runInFxThreadStatic(());
          } 
        });
  }
  
  private void resetProgress() {
    this.totalDownloaded.set(0L);
    this.lastUpdateTime.set(System.currentTimeMillis());
    this.lastDownloaded.set(0L);
    this.progressBar.progressProperty().setValue(Integer.valueOf(0));
  }
  
  private List<Downloader.SizedFile> getFilesList(Path paramPath, LinkedList<PathRemapperData> paramLinkedList, HashedDir paramHashedDir) throws IOException {
    this.totalSize.set(0L);
    ArrayList<Downloader.SizedFile> arrayList = new ArrayList();
    paramHashedDir.walk("/", (paramString1, paramString2, paramHashedEntry) -> {
          HashedFile hashedFile;
          String str = paramString1;
          switch (paramHashedEntry.getType()) {
            case FILE:
              hashedFile = (HashedFile)paramHashedEntry;
              this.totalSize.addAndGet(hashedFile.size);
              for (PathRemapperData pathRemapperData : paramLinkedList) {
                if (paramString1.startsWith(pathRemapperData.key)) {
                  str = paramString1.replace(pathRemapperData.key, pathRemapperData.value);
                  LogHelper.dev("Remap found: injected url path: %s | calculated original url path: %s", new Object[] { paramString1, str });
                } 
              } 
              try {
                Files.deleteIfExists(paramPath.resolve(paramString1));
              } catch (IOException iOException) {
                throw new RuntimeException(iOException);
              } 
              paramList.add(new Downloader.SizedFile(str, paramString1, hashedFile.size));
              break;
            case DIR:
              try {
                Files.createDirectories(paramPath.resolve(paramString1), (FileAttribute<?>[])new FileAttribute[0]);
              } catch (IOException iOException) {
                throw new RuntimeException(iOException);
              } 
              break;
          } 
          return HashedDir.WalkAction.CONTINUE;
        });
    return arrayList;
  }
  
  private LinkedList<PathRemapperData> getPathRemapper(OptionalView paramOptionalView, HashedDir paramHashedDir) {
    for (OptionalAction optionalAction : paramOptionalView.getDisabledActions()) {
      if (optionalAction instanceof OptionalActionFile) {
        OptionalActionFile optionalActionFile = (OptionalActionFile)optionalAction;
        optionalActionFile.disableInHashedDir(paramHashedDir);
      } 
    } 
    LinkedList<PathRemapperData> linkedList = new LinkedList();
    Set set = paramOptionalView.getActionsByClass(OptionalActionFile.class);
    for (OptionalActionFile optionalActionFile : set) {
      optionalActionFile.injectToHashedDir(paramHashedDir);
      optionalActionFile.files.forEach((paramString1, paramString2) -> {
            if (paramString2 == null || paramString2.isEmpty())
              return; 
            paramLinkedList.add(new PathRemapperData(paramString2, paramString1));
            LogHelper.dev("Remap prepare %s to %s", new Object[] { paramString2, paramString1 });
          });
    } 
    linkedList.sort(Comparator.comparingInt(paramPathRemapperData -> -paramPathRemapperData.key.length()));
    return linkedList;
  }
  
  private void deleteExtraDir(Path paramPath, HashedDir paramHashedDir, boolean paramBoolean) throws IOException {
    for (Map.Entry entry : paramHashedDir.map().entrySet()) {
      String str = (String)entry.getKey();
      Path path = paramPath.resolve(str);
      HashedEntry hashedEntry = (HashedEntry)entry.getValue();
      HashedEntry.Type type = hashedEntry.getType();
      switch (type) {
        case FILE:
          Files.delete(path);
          continue;
        case DIR:
          deleteExtraDir(path, (HashedDir)hashedEntry, (paramBoolean || hashedEntry.flag));
          continue;
      } 
      throw new AssertionError("Unsupported hashed entry type: " + type.name());
    } 
    if (paramBoolean)
      Files.delete(paramPath); 
  }
  
  private void updateProgress(long paramLong1, long paramLong2) {
    double d = (paramLong2 - paramLong1) / this.totalSize.get();
    DoubleProperty doubleProperty = this.progressBar.progressProperty();
    doubleProperty.set(doubleProperty.get() + d);
    long l1 = this.lastUpdateTime.get();
    long l2 = System.currentTimeMillis();
    if (l2 - l1 >= 130L) {
      double d1 = (paramLong2 - this.lastDownloaded.get()) / (l2 - l1) * 1000.0D;
      String str = "%.1f".formatted(new Object[] { Double.valueOf(d1 * 8.0D / 1000000.0D) });
      ContextHelper.runInFxThreadStatic(() -> {
            this.volume.setText("%.1f/%.1f MB".formatted(new Object[] { Double.valueOf(paramLong / 1048576.0D), Double.valueOf(this.totalSize.get() / 1048576.0D) }));
            this.speed.setText(paramString);
          });
      this.lastUpdateTime.set(l2);
      this.lastDownloaded.set(paramLong2);
    } 
  }
  
  public boolean isDownload() {
    return (this.downloader != null && !this.downloader.getFuture().isDone());
  }
  
  public CompletableFuture<Void> getFuture() {
    return this.downloader.getFuture();
  }
  
  public void cancel() {
    this.downloader.cancel();
  }
  
  public boolean isCanceled() {
    return this.downloader.isCanceled();
  }
  
  private static class PathRemapperData {
    public String key;
    
    public String value;
    
    public PathRemapperData(String param1String1, String param1String2) {
      this.key = param1String1;
      this.value = param1String2;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scene\\update\VisualDownloader.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */