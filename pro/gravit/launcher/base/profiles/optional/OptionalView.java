package pro.gravit.launcher.base.profiles.optional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.base.profiles.optional.actions.OptionalAction;

public class OptionalView {
  public Set<OptionalFile> enabled = new HashSet<>();
  
  public Map<OptionalFile, OptionalFileInstallInfo> installInfo = new HashMap<>();
  
  public Set<OptionalFile> all;
  
  public OptionalView(ClientProfile paramClientProfile) {
    this.all = paramClientProfile.getOptional();
    for (OptionalFile optionalFile : this.all) {
      if (optionalFile.mark)
        enable(optionalFile, true, null); 
    } 
  }
  
  public OptionalView(OptionalView paramOptionalView) {
    this.enabled = new HashSet<>(paramOptionalView.enabled);
    this.installInfo = new HashMap<>(paramOptionalView.installInfo);
    this.all = paramOptionalView.all;
    fixDependencies();
  }
  
  public OptionalView(ClientProfile paramClientProfile, OptionalView paramOptionalView) {
    this(paramClientProfile);
    for (OptionalFile optionalFile1 : paramOptionalView.all) {
      OptionalFile optionalFile2 = findByName(optionalFile1.name);
      if (optionalFile2 == null)
        continue; 
      if (paramOptionalView.isEnabled(optionalFile1)) {
        enable(optionalFile2, ((OptionalFileInstallInfo)paramOptionalView.installInfo.get(optionalFile1)).isManual, (paramOptionalFile, paramBoolean) -> {
            
            });
        continue;
      } 
      disable(optionalFile2, (paramOptionalFile, paramBoolean) -> {
          
          });
    } 
    fixDependencies();
  }
  
  public <T extends OptionalAction> Set<T> getActionsByClass(Class<T> paramClass) {
    HashSet<OptionalAction> hashSet = new HashSet();
    for (OptionalFile optionalFile : this.enabled) {
      if (optionalFile.actions != null)
        for (OptionalAction optionalAction : optionalFile.actions) {
          if (paramClass.isAssignableFrom(optionalAction.getClass()))
            hashSet.add(optionalAction); 
        }  
    } 
    return (Set)hashSet;
  }
  
  public OptionalFile findByName(String paramString) {
    for (OptionalFile optionalFile : this.all) {
      if (paramString.equals(optionalFile.name))
        return optionalFile; 
    } 
    return null;
  }
  
  public boolean isEnabled(OptionalFile paramOptionalFile) {
    return this.enabled.contains(paramOptionalFile);
  }
  
  public Set<OptionalAction> getEnabledActions() {
    HashSet<OptionalAction> hashSet = new HashSet();
    for (OptionalFile optionalFile : this.enabled) {
      if (optionalFile.actions != null)
        hashSet.addAll(optionalFile.actions); 
    } 
    return hashSet;
  }
  
  public void fixDependencies() {
    Set set = (Set)this.all.stream().filter(paramOptionalFile -> !isEnabled(paramOptionalFile)).collect(Collectors.toSet());
    for (OptionalFile optionalFile : set) {
      if (optionalFile.group != null && optionalFile.group.length > 0 && Arrays.<OptionalFile>stream(optionalFile.group).noneMatch(this::isEnabled))
        enable(optionalFile.group[0], false, null); 
    } 
    for (OptionalFile optionalFile : this.enabled) {
      if (optionalFile.dependencies != null)
        for (OptionalFile optionalFile1 : optionalFile.dependencies)
          enable(optionalFile1, false, null);  
      if (optionalFile.conflict != null)
        for (OptionalFile optionalFile1 : optionalFile.conflict)
          disable(optionalFile1, null);  
      if (optionalFile.group != null)
        for (OptionalFile optionalFile1 : optionalFile.group)
          disable(optionalFile1, null);  
    } 
  }
  
  public Set<OptionalAction> getDisabledActions() {
    HashSet<OptionalAction> hashSet = new HashSet();
    for (OptionalFile optionalFile : this.all) {
      if (!this.enabled.contains(optionalFile) && optionalFile.actions != null)
        hashSet.addAll(optionalFile.actions); 
    } 
    return hashSet;
  }
  
  public void enable(OptionalFile paramOptionalFile, boolean paramBoolean, BiConsumer<OptionalFile, Boolean> paramBiConsumer) {
    if (this.enabled.contains(paramOptionalFile))
      return; 
    this.enabled.add(paramOptionalFile);
    if (paramBiConsumer != null)
      paramBiConsumer.accept(paramOptionalFile, Boolean.valueOf(true)); 
    OptionalFileInstallInfo optionalFileInstallInfo = this.installInfo.computeIfAbsent(paramOptionalFile, paramOptionalFile -> new OptionalFileInstallInfo());
    optionalFileInstallInfo.isManual = paramBoolean;
    if (paramOptionalFile.dependencies != null)
      for (OptionalFile optionalFile : paramOptionalFile.dependencies)
        enable(optionalFile, false, paramBiConsumer);  
    if (paramOptionalFile.conflict != null)
      for (OptionalFile optionalFile : paramOptionalFile.conflict)
        disable(optionalFile, paramBiConsumer);  
    if (paramOptionalFile.group != null)
      for (OptionalFile optionalFile : paramOptionalFile.group)
        disable(optionalFile, paramBiConsumer);  
  }
  
  public void disable(OptionalFile paramOptionalFile, BiConsumer<OptionalFile, Boolean> paramBiConsumer) {
    if (!this.enabled.remove(paramOptionalFile))
      return; 
    if (paramBiConsumer != null)
      paramBiConsumer.accept(paramOptionalFile, Boolean.valueOf(false)); 
    for (OptionalFile optionalFile : this.all) {
      if (optionalFile.dependencies != null && contains(paramOptionalFile, optionalFile.dependencies))
        disable(optionalFile, paramBiConsumer); 
    } 
    if (paramOptionalFile.dependencies != null)
      for (OptionalFile optionalFile : paramOptionalFile.dependencies) {
        OptionalFileInstallInfo optionalFileInstallInfo = this.installInfo.get(optionalFile);
        if (optionalFileInstallInfo != null && !optionalFileInstallInfo.isManual)
          disable(paramOptionalFile, paramBiConsumer); 
      }  
    if (paramOptionalFile.group != null && paramOptionalFile.group.length != 0 && Arrays.<OptionalFile>stream(paramOptionalFile.group).noneMatch(this::isEnabled))
      enable(paramOptionalFile.group[0], false, paramBiConsumer); 
  }
  
  private boolean contains(OptionalFile paramOptionalFile, OptionalFile[] paramArrayOfOptionalFile) {
    for (OptionalFile optionalFile : paramArrayOfOptionalFile) {
      if (optionalFile == paramOptionalFile)
        return true; 
    } 
    return false;
  }
  
  public static class OptionalFileInstallInfo {
    public boolean isManual;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\profiles\optional\OptionalView.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */