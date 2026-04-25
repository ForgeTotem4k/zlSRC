package pro.gravit.launcher.core.hasher;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.launcher.core.serialize.HInput;
import pro.gravit.launcher.core.serialize.HOutput;
import pro.gravit.launcher.core.serialize.stream.EnumSerializer;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.VerifyHelper;

public final class HashedDir extends HashedEntry {
  @LauncherNetworkAPI
  private final Map<String, HashedEntry> map = new HashMap<>(32);
  
  public HashedDir() {}
  
  public HashedDir(HInput paramHInput) throws IOException {
    int i = paramHInput.readLength(0);
    for (byte b = 0; b < i; b++) {
      String str = IOHelper.verifyFileName(paramHInput.readString(255));
      HashedEntry.Type type = HashedEntry.Type.read(paramHInput);
      switch (type) {
        default:
          throw new IncompatibleClassChangeError();
        case FILE:
        
        case DIR:
          break;
      } 
      HashedDir hashedDir = new HashedDir(paramHInput);
      VerifyHelper.putIfAbsent(this.map, str, hashedDir, String.format("Duplicate dir entry: '%s'", new Object[] { str }));
    } 
  }
  
  public HashedDir(Path paramPath, FileNameMatcher paramFileNameMatcher, boolean paramBoolean1, boolean paramBoolean2) throws IOException {
    IOHelper.walk(paramPath, new HashFileVisitor(paramPath, paramFileNameMatcher, paramBoolean1, paramBoolean2), true);
  }
  
  public Diff diff(HashedDir paramHashedDir, FileNameMatcher paramFileNameMatcher) {
    HashedDir hashedDir1 = sideDiff(paramHashedDir, paramFileNameMatcher, new LinkedList<>(), true);
    HashedDir hashedDir2 = paramHashedDir.sideDiff(this, paramFileNameMatcher, new LinkedList<>(), false);
    return new Diff(hashedDir1, hashedDir2);
  }
  
  public Diff compare(HashedDir paramHashedDir, FileNameMatcher paramFileNameMatcher) {
    HashedDir hashedDir1 = sideDiff(paramHashedDir, paramFileNameMatcher, new LinkedList<>(), true);
    HashedDir hashedDir2 = paramHashedDir.sideDiff(this, paramFileNameMatcher, new LinkedList<>(), false);
    return new Diff(hashedDir1, hashedDir2);
  }
  
  public void remove(String paramString) {
    this.map.remove(paramString);
  }
  
  public void moveTo(String paramString1, HashedDir paramHashedDir, String paramString2) {
    HashedEntry hashedEntry = this.map.remove(paramString1);
    paramHashedDir.map.put(paramString2, hashedEntry);
  }
  
  public FindRecursiveResult findRecursive(String paramString) {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, "/");
    HashedDir hashedDir = this;
    HashedEntry hashedEntry = null;
    String str = null;
    while (stringTokenizer.hasMoreTokens()) {
      str = stringTokenizer.nextToken();
      HashedEntry hashedEntry1 = hashedDir.map.get(str);
      if (hashedEntry1 == null && !stringTokenizer.hasMoreTokens())
        break; 
      if (hashedEntry1 == null)
        throw new RuntimeException(String.format("Directory %s not found", new Object[] { str })); 
      if (hashedEntry1.getType() == HashedEntry.Type.DIR) {
        if (!stringTokenizer.hasMoreTokens()) {
          hashedEntry = hashedEntry1;
          break;
        } 
        hashedDir = (HashedDir)hashedEntry1;
        continue;
      } 
      hashedEntry = hashedEntry1;
    } 
    return new FindRecursiveResult(hashedDir, hashedEntry, str);
  }
  
  public HashedEntry getEntry(String paramString) {
    return this.map.get(paramString);
  }
  
  public HashedEntry.Type getType() {
    return HashedEntry.Type.DIR;
  }
  
  public boolean isEmpty() {
    return this.map.isEmpty();
  }
  
  public Map<String, HashedEntry> map() {
    return Collections.unmodifiableMap(this.map);
  }
  
  public HashedEntry resolve(Iterable<String> paramIterable) {
    HashedEntry hashedEntry = this;
    for (String str : paramIterable) {
      if (hashedEntry instanceof HashedDir) {
        hashedEntry = ((HashedDir)hashedEntry).map.get(str);
        continue;
      } 
      return null;
    } 
    return hashedEntry;
  }
  
  private HashedDir sideDiff(HashedDir paramHashedDir, FileNameMatcher paramFileNameMatcher, Deque<String> paramDeque, boolean paramBoolean) {
    HashedDir hashedDir = new HashedDir();
    for (Map.Entry<String, HashedEntry> entry : this.map.entrySet()) {
      HashedFile hashedFile1;
      HashedFile hashedFile2;
      HashedDir hashedDir1;
      HashedDir hashedDir2;
      String str = (String)entry.getKey();
      HashedEntry hashedEntry1 = (HashedEntry)entry.getValue();
      paramDeque.add(str);
      boolean bool = (paramFileNameMatcher == null || paramFileNameMatcher.shouldUpdate(paramDeque)) ? true : false;
      HashedEntry.Type type = hashedEntry1.getType();
      HashedEntry hashedEntry2 = paramHashedDir.map.get(str);
      if (hashedEntry2 == null || hashedEntry2.getType() != type) {
        if (bool || (paramBoolean && hashedEntry2 == null)) {
          hashedDir.map.put(str, hashedEntry1);
          if (!paramBoolean)
            hashedEntry1.flag = true; 
        } 
        paramDeque.removeLast();
        continue;
      } 
      switch (type) {
        case FILE:
          hashedFile1 = (HashedFile)hashedEntry1;
          hashedFile2 = (HashedFile)hashedEntry2;
          if (paramBoolean && bool && !hashedFile1.isSame(hashedFile2))
            hashedDir.map.put(str, hashedEntry1); 
          break;
        case DIR:
          hashedDir1 = (HashedDir)hashedEntry1;
          hashedDir2 = (HashedDir)hashedEntry2;
          if (paramBoolean || bool) {
            HashedDir hashedDir3 = hashedDir1.sideDiff(hashedDir2, paramFileNameMatcher, paramDeque, paramBoolean);
            if (!hashedDir3.isEmpty())
              hashedDir.map.put(str, hashedDir3); 
          } 
          break;
        default:
          throw new AssertionError("Unsupported hashed entry type: " + type.name());
      } 
      paramDeque.removeLast();
    } 
    return hashedDir;
  }
  
  public HashedDir sideCompare(HashedDir paramHashedDir, FileNameMatcher paramFileNameMatcher, Deque<String> paramDeque, boolean paramBoolean) {
    HashedDir hashedDir = new HashedDir();
    for (Map.Entry<String, HashedEntry> entry : this.map.entrySet()) {
      HashedFile hashedFile1;
      HashedFile hashedFile2;
      HashedDir hashedDir1;
      HashedDir hashedDir2;
      String str = (String)entry.getKey();
      HashedEntry hashedEntry1 = (HashedEntry)entry.getValue();
      paramDeque.add(str);
      boolean bool = (paramFileNameMatcher == null || paramFileNameMatcher.shouldUpdate(paramDeque)) ? true : false;
      HashedEntry.Type type = hashedEntry1.getType();
      HashedEntry hashedEntry2 = paramHashedDir.map.get(str);
      if (hashedEntry2 == null || hashedEntry2.getType() != type) {
        if (bool || (paramBoolean && hashedEntry2 == null)) {
          hashedDir.map.put(str, hashedEntry1);
          if (!paramBoolean)
            hashedEntry1.flag = true; 
        } 
        paramDeque.removeLast();
        continue;
      } 
      switch (type) {
        case FILE:
          hashedFile1 = (HashedFile)hashedEntry1;
          hashedFile2 = (HashedFile)hashedEntry2;
          if (paramBoolean && bool && hashedFile1.isSame(hashedFile2))
            hashedDir.map.put(str, hashedEntry1); 
          break;
        case DIR:
          hashedDir1 = (HashedDir)hashedEntry1;
          hashedDir2 = (HashedDir)hashedEntry2;
          if (paramBoolean || bool) {
            HashedDir hashedDir3 = hashedDir1.sideCompare(hashedDir2, paramFileNameMatcher, paramDeque, paramBoolean);
            if (!hashedDir3.isEmpty())
              hashedDir.map.put(str, hashedDir3); 
          } 
          break;
        default:
          throw new AssertionError("Unsupported hashed entry type: " + type.name());
      } 
      paramDeque.removeLast();
    } 
    return hashedDir;
  }
  
  public long size() {
    return this.map.values().stream().mapToLong(HashedEntry::size).sum();
  }
  
  public void write(HOutput paramHOutput) throws IOException {
    Set<Map.Entry<String, HashedEntry>> set = this.map.entrySet();
    paramHOutput.writeLength(set.size(), 0);
    for (Map.Entry<String, HashedEntry> entry : set) {
      paramHOutput.writeString((String)entry.getKey(), 255);
      HashedEntry hashedEntry = (HashedEntry)entry.getValue();
      EnumSerializer.write(paramHOutput, hashedEntry.getType());
      hashedEntry.write(paramHOutput);
    } 
  }
  
  public void walk(CharSequence paramCharSequence, WalkCallback paramWalkCallback) throws IOException {
    String str = "";
    walk(str, paramCharSequence, paramWalkCallback, true);
  }
  
  private WalkAction walk(String paramString, CharSequence paramCharSequence, WalkCallback paramWalkCallback, boolean paramBoolean) throws IOException {
    for (Map.Entry<String, HashedEntry> entry : this.map.entrySet()) {
      String str;
      HashedEntry hashedEntry = (HashedEntry)entry.getValue();
      if (hashedEntry.getType() == HashedEntry.Type.FILE) {
        if (paramBoolean) {
          WalkAction walkAction2 = paramWalkCallback.walked(paramString + paramString, (String)entry.getKey(), hashedEntry);
          if (walkAction2 == WalkAction.STOP)
            return walkAction2; 
          continue;
        } 
        WalkAction walkAction1 = paramWalkCallback.walked(paramString + paramString + String.valueOf(paramCharSequence), (String)entry.getKey(), hashedEntry);
        if (walkAction1 == WalkAction.STOP)
          return walkAction1; 
        continue;
      } 
      if (paramBoolean) {
        str = paramString + paramString;
      } else {
        str = paramString + paramString + String.valueOf(paramCharSequence);
      } 
      WalkAction walkAction = paramWalkCallback.walked(str, (String)entry.getKey(), hashedEntry);
      if (walkAction == WalkAction.STOP)
        return walkAction; 
      walkAction = ((HashedDir)hashedEntry).walk(str, paramCharSequence, paramWalkCallback, false);
      if (walkAction == WalkAction.STOP)
        return walkAction; 
    } 
    return WalkAction.CONTINUE;
  }
  
  private final class HashFileVisitor extends SimpleFileVisitor<Path> {
    private final Path dir;
    
    private final FileNameMatcher matcher;
    
    private final boolean allowSymlinks;
    
    private final boolean digest;
    
    private final Deque<String> path = new LinkedList<>();
    
    private final Deque<HashedDir> stack = new LinkedList<>();
    
    private HashedDir current = HashedDir.this;
    
    private HashFileVisitor(Path param1Path, FileNameMatcher param1FileNameMatcher, boolean param1Boolean1, boolean param1Boolean2) {
      this.dir = param1Path;
      this.matcher = param1FileNameMatcher;
      this.allowSymlinks = param1Boolean1;
      this.digest = param1Boolean2;
    }
    
    public FileVisitResult postVisitDirectory(Path param1Path, IOException param1IOException) throws IOException {
      FileVisitResult fileVisitResult = super.postVisitDirectory(param1Path, param1IOException);
      if (this.dir.equals(param1Path))
        return fileVisitResult; 
      HashedDir hashedDir = this.stack.removeLast();
      hashedDir.map.put(this.path.removeLast(), this.current);
      this.current = hashedDir;
      return fileVisitResult;
    }
    
    public FileVisitResult preVisitDirectory(Path param1Path, BasicFileAttributes param1BasicFileAttributes) throws IOException {
      FileVisitResult fileVisitResult = super.preVisitDirectory(param1Path, param1BasicFileAttributes);
      if (this.dir.equals(param1Path))
        return fileVisitResult; 
      if (!this.allowSymlinks && param1BasicFileAttributes.isSymbolicLink())
        throw new SecurityException("Symlinks are not allowed"); 
      this.stack.add(this.current);
      this.current = new HashedDir();
      this.path.add(IOHelper.getFileName(param1Path));
      return fileVisitResult;
    }
    
    public FileVisitResult visitFile(Path param1Path, BasicFileAttributes param1BasicFileAttributes) throws IOException {
      if (!this.allowSymlinks && param1BasicFileAttributes.isSymbolicLink())
        throw new SecurityException("Symlinks are not allowed"); 
      this.path.add(IOHelper.getFileName(param1Path));
      boolean bool = (this.digest && (this.matcher == null || this.matcher.shouldUpdate(this.path))) ? true : false;
      this.current.map.put(this.path.removeLast(), new HashedFile(param1Path, param1BasicFileAttributes.size(), bool));
      return super.visitFile(param1Path, param1BasicFileAttributes);
    }
  }
  
  public static final class Diff {
    public final HashedDir mismatch;
    
    public final HashedDir extra;
    
    private Diff(HashedDir param1HashedDir1, HashedDir param1HashedDir2) {
      this.mismatch = param1HashedDir1;
      this.extra = param1HashedDir2;
    }
    
    public boolean isSame() {
      return (this.mismatch.isEmpty() && this.extra.isEmpty());
    }
  }
  
  public static class FindRecursiveResult {
    public final HashedDir parent;
    
    public final HashedEntry entry;
    
    public final String name;
    
    public FindRecursiveResult(HashedDir param1HashedDir, HashedEntry param1HashedEntry, String param1String) {
      this.parent = param1HashedDir;
      this.entry = param1HashedEntry;
      this.name = param1String;
    }
  }
  
  @FunctionalInterface
  public static interface WalkCallback {
    HashedDir.WalkAction walked(String param1String1, String param1String2, HashedEntry param1HashedEntry);
    
    static {
    
    }
  }
  
  public enum WalkAction {
    STOP, CONTINUE;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\hasher\HashedDir.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */