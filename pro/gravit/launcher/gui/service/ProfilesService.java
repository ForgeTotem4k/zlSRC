package pro.gravit.launcher.gui.service;

import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.events.request.ProfilesRequestEvent;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.base.profiles.optional.OptionalFile;
import pro.gravit.launcher.base.profiles.optional.OptionalView;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.runtime.client.DirBridge;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.LogHelper;

public class ProfilesService {
  private final JavaFXApplication application;
  
  private List<ClientProfile> profiles;
  
  private ClientProfile profile;
  
  private Map<ClientProfile, OptionalView> optionalViewMap;
  
  public ProfilesService(JavaFXApplication paramJavaFXApplication) {
    this.application = paramJavaFXApplication;
  }
  
  public Map<ClientProfile, OptionalView> getOptionalViewMap() {
    return this.optionalViewMap;
  }
  
  public OptionalView getOptionalView() {
    return this.optionalViewMap.get(this.profile);
  }
  
  public OptionalView getOptionalView(ClientProfile paramClientProfile) {
    return this.optionalViewMap.get(paramClientProfile);
  }
  
  public void setOptionalView(ClientProfile paramClientProfile, OptionalView paramOptionalView) {
    this.optionalViewMap.put(paramClientProfile, paramOptionalView);
  }
  
  public void setProfilesResult(ProfilesRequestEvent paramProfilesRequestEvent) {
    this.profiles = paramProfilesRequestEvent.profiles;
    this.profiles.sort(ClientProfile::compareTo);
    if (this.optionalViewMap == null)
      this.optionalViewMap = new HashMap<>(); 
    for (ClientProfile clientProfile : this.profiles) {
      clientProfile.updateOptionalGraph();
      OptionalView optionalView1 = this.optionalViewMap.get(clientProfile);
      OptionalView optionalView2 = (optionalView1 != null) ? new OptionalView(clientProfile, optionalView1) : new OptionalView(clientProfile);
      this.optionalViewMap.put(clientProfile, optionalView2);
    } 
    for (ClientProfile clientProfile : this.profiles)
      this.application.triggerManager.process(clientProfile, getOptionalView(clientProfile)); 
  }
  
  public List<ClientProfile> getProfiles() {
    return this.profiles;
  }
  
  public ClientProfile getProfile() {
    return this.profile;
  }
  
  public void setProfile(ClientProfile paramClientProfile) {
    this.profile = paramClientProfile;
  }
  
  public void saveAll() throws IOException {
    if (this.profiles == null)
      return; 
    Path path = DirBridge.dir.resolve("options.json");
    ArrayList<OptionalListEntry> arrayList = new ArrayList(5);
    Iterator<ClientProfile> iterator = this.profiles.iterator();
    while (iterator.hasNext()) {
      ClientProfile clientProfile = iterator.next();
      OptionalListEntry optionalListEntry = new OptionalListEntry();
      optionalListEntry.name = clientProfile.getTitle();
      optionalListEntry.profileUUID = clientProfile.getUUID();
      OptionalView optionalView = this.optionalViewMap.get(clientProfile);
      optionalView.all.forEach(paramOptionalFile -> {
            if (paramOptionalFile.visible) {
              boolean bool = paramOptionalView.enabled.contains(paramOptionalFile);
              OptionalView.OptionalFileInstallInfo optionalFileInstallInfo = (OptionalView.OptionalFileInstallInfo)paramOptionalView.installInfo.get(paramOptionalFile);
              paramOptionalListEntry.enabled.add(new OptionalListEntryPair(paramOptionalFile, bool, optionalFileInstallInfo));
            } 
          });
      arrayList.add(optionalListEntry);
    } 
    BufferedWriter bufferedWriter = IOHelper.newWriter(path);
    try {
      Launcher.gsonManager.gson.toJson(arrayList, bufferedWriter);
      if (bufferedWriter != null)
        bufferedWriter.close(); 
    } catch (Throwable throwable) {
      if (bufferedWriter != null)
        try {
          bufferedWriter.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  public void loadAll() throws IOException {
    if (this.profiles == null)
      return; 
    Path path = DirBridge.dir.resolve("options.json");
    if (!Files.exists(path, new java.nio.file.LinkOption[0]))
      return; 
    Type type = (new TypeToken<List<OptionalListEntry>>() {
      
      }).getType();
    BufferedReader bufferedReader = IOHelper.newReader(path);
    try {
      List list = (List)Launcher.gsonManager.gson.fromJson(bufferedReader, type);
      for (OptionalListEntry optionalListEntry : list) {
        ClientProfile clientProfile = null;
        for (ClientProfile clientProfile1 : this.profiles) {
          if ((optionalListEntry.profileUUID != null) ? optionalListEntry.profileUUID.equals(clientProfile1.getUUID()) : clientProfile1.getTitle().equals(optionalListEntry.name))
            clientProfile = clientProfile1; 
        } 
        if (clientProfile == null) {
          LogHelper.warning("Optional: profile %s(%s) not found", new Object[] { optionalListEntry.name, optionalListEntry.profileUUID });
          continue;
        } 
        OptionalView optionalView = this.optionalViewMap.get(clientProfile);
        for (OptionalListEntryPair optionalListEntryPair : optionalListEntry.enabled) {
          try {
            OptionalFile optionalFile = clientProfile.getOptionalFile(optionalListEntryPair.name);
            if (optionalFile.visible) {
              if (optionalListEntryPair.mark) {
                optionalView.enable(optionalFile, (optionalListEntryPair.installInfo != null && optionalListEntryPair.installInfo.isManual), null);
                continue;
              } 
              optionalView.disable(optionalFile, null);
            } 
          } catch (Exception exception) {
            LogHelper.warning("Optional: in profile %s markOptional mod %s failed", new Object[] { clientProfile.getTitle(), optionalListEntryPair.name });
          } 
        } 
      } 
      if (bufferedReader != null)
        bufferedReader.close(); 
    } catch (Throwable throwable) {
      if (bufferedReader != null)
        try {
          bufferedReader.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  public static class OptionalListEntry {
    @LauncherNetworkAPI
    public List<ProfilesService.OptionalListEntryPair> enabled = new LinkedList<>();
    
    @LauncherNetworkAPI
    public String name;
    
    @LauncherNetworkAPI
    public UUID profileUUID;
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (param1Object == null || getClass() != param1Object.getClass())
        return false; 
      OptionalListEntry optionalListEntry = (OptionalListEntry)param1Object;
      return (Objects.equals(this.profileUUID, optionalListEntry.profileUUID) && Objects.equals(this.name, optionalListEntry.name));
    }
    
    public int hashCode() {
      return Objects.hash(new Object[] { this.name, this.profileUUID });
    }
  }
  
  public static class OptionalListEntryPair {
    @LauncherNetworkAPI
    public String name;
    
    @LauncherNetworkAPI
    public boolean mark;
    
    @LauncherNetworkAPI
    public OptionalView.OptionalFileInstallInfo installInfo;
    
    public OptionalListEntryPair(OptionalFile param1OptionalFile, boolean param1Boolean, OptionalView.OptionalFileInstallInfo param1OptionalFileInstallInfo) {
      this.name = param1OptionalFile.name;
      this.mark = param1Boolean;
      this.installInfo = param1OptionalFileInstallInfo;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\service\ProfilesService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */