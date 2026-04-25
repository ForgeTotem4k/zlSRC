package pro.gravit.launcher.runtime.debug;

import java.io.BufferedReader;
import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import pro.gravit.launcher.base.ClientPermissions;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.api.AuthService;
import pro.gravit.launcher.base.api.ClientService;
import pro.gravit.launcher.base.events.request.AuthRequestEvent;
import pro.gravit.launcher.base.events.request.ProfilesRequestEvent;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.auth.AuthRequest;
import pro.gravit.launcher.base.request.update.ProfilesRequest;
import pro.gravit.launcher.runtime.LauncherEngine;
import pro.gravit.launcher.runtime.gui.RuntimeProvider;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.LogHelper;
import pro.gravit.utils.launch.BasicLaunch;
import pro.gravit.utils.launch.ClassLoaderControl;
import pro.gravit.utils.launch.LaunchOptions;
import pro.gravit.utils.launch.LegacyLaunch;
import pro.gravit.utils.launch.ModuleLaunch;

public class ClientRuntimeProvider implements RuntimeProvider {
  public void run(String[] paramArrayOfString) {
    ArrayList<String> arrayList = new ArrayList(Arrays.asList((Object[])paramArrayOfString));
    try {
      BasicLaunch basicLaunch;
      LegacyLaunch legacyLaunch;
      ModuleLaunch moduleLaunch;
      LaunchOptions launchOptions;
      String str1 = System.getProperty("launcher.runtime.username", null);
      String str2 = System.getProperty("launcher.runtime.uuid", null);
      String str3 = System.getProperty("launcher.runtime.login", str1);
      String str4 = System.getProperty("launcher.runtime.password", "Player");
      String str5 = System.getProperty("launcher.runtime.auth.authid", "std");
      String str6 = System.getProperty("launcher.runtime.auth.accesstoken", null);
      String str7 = System.getProperty("launcher.runtime.auth.refreshtoken", null);
      String str8 = System.getProperty("launcher.runtime.auth.minecraftaccesstoken", "DEBUG");
      long l = Long.parseLong(System.getProperty("launcher.runtime.auth.expire", "0"));
      String str9 = System.getProperty("launcher.runtime.profileuuid", null);
      String str10 = System.getProperty("launcher.runtime.mainclass", null);
      String str11 = System.getProperty("launcher.runtime.mainmodule", null);
      String str12 = System.getProperty("launcher.runtime.launch", "basic");
      String str13 = System.getProperty("launcher.runtime.launch.compat", null);
      String str14 = System.getProperty("launcher.runtime.launch.natives", "natives");
      String str15 = System.getProperty("launcher.runtime.launch.options", null);
      boolean bool = Boolean.getBoolean("launcher.runtime.launch.enablehacks");
      ClientPermissions clientPermissions = new ClientPermissions();
      if (str10 == null)
        throw new NullPointerException("Add `-Dlauncher.runtime.mainclass=YOUR_MAIN_CLASS` to jvmArgs"); 
      if (str2 == null)
        if (str6 != null) {
          Request.setOAuth(str5, new AuthRequestEvent.OAuthRequestEvent(str6, str7, l));
          Request.RequestRestoreReport requestRestoreReport = Request.restore(true, false, true);
          clientPermissions = requestRestoreReport.userInfo.permissions;
          str1 = requestRestoreReport.userInfo.playerProfile.username;
          str2 = requestRestoreReport.userInfo.playerProfile.uuid.toString();
          if (requestRestoreReport.userInfo.accessToken != null)
            str8 = requestRestoreReport.userInfo.accessToken; 
        } else {
          AuthRequest authRequest = new AuthRequest(str3, str4, str5, AuthRequest.ConnectTypes.API);
          AuthRequestEvent authRequestEvent = (AuthRequestEvent)authRequest.request();
          Request.setOAuth(str5, authRequestEvent.oauth);
          if (authRequestEvent.accessToken != null)
            str8 = authRequestEvent.accessToken; 
          str1 = authRequestEvent.playerProfile.username;
          str2 = authRequestEvent.playerProfile.uuid.toString();
        }  
      if (str9 != null) {
        UUID uUID = UUID.fromString(str9);
        ProfilesRequest profilesRequest = new ProfilesRequest();
        ProfilesRequestEvent profilesRequestEvent = (ProfilesRequestEvent)profilesRequest.request();
        for (ClientProfile clientProfile : profilesRequestEvent.profiles) {
          if (clientProfile.getUUID().equals(uUID))
            AuthService.profile = clientProfile; 
        } 
      } 
      if (str1 == null)
        str1 = "Player"; 
      if (str2 == null)
        str2 = "a7899336-e61c-4e51-b480-0c815b18aed8"; 
      replaceOrCreateArgument(arrayList, "--username", str1);
      replaceOrCreateArgument(arrayList, "--uuid", str2);
      replaceOrCreateArgument(arrayList, "--accessToken", str8);
      AuthService.uuid = UUID.fromString(str2);
      AuthService.username = str1;
      AuthService.permissions = clientPermissions;
      switch (str12) {
        case "basic":
          basicLaunch = new BasicLaunch();
          break;
        case "legacy":
          legacyLaunch = new LegacyLaunch();
          break;
        case "module":
          moduleLaunch = new ModuleLaunch();
          break;
        default:
          throw new UnsupportedOperationException(String.format("Unknown launch mode: '%s'", new Object[] { str12 }));
      } 
      ArrayList<Path> arrayList1 = new ArrayList();
      try {
        for (String str : System.getProperty("java.class.path").split(File.pathSeparator))
          arrayList1.add(Paths.get(str, new String[0])); 
      } catch (Throwable throwable) {
        LogHelper.error(throwable);
      } 
      if (str15 != null) {
        BufferedReader bufferedReader = IOHelper.newReader(Paths.get(str15, new String[0]));
        try {
          launchOptions = (LaunchOptions)Launcher.gsonManager.gson.fromJson(bufferedReader, LaunchOptions.class);
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
      } else {
        launchOptions = new LaunchOptions();
      } 
      launchOptions.enableHacks = bool;
      ClassLoaderControl classLoaderControl = moduleLaunch.init(arrayList1, str14, launchOptions);
      ClientService.classLoaderControl = classLoaderControl;
      if (str13 != null) {
        String[] arrayOfString = str13.split(",");
        for (String str : arrayOfString) {
          Class<?> clazz = classLoaderControl.getClass(str);
          MethodHandle methodHandle = MethodHandles.lookup().findStatic(clazz, "run", MethodType.methodType(void.class, ClassLoaderControl.class));
          methodHandle.invoke(classLoaderControl);
        } 
      } 
      moduleLaunch.launch(str10, str11, Arrays.asList(paramArrayOfString));
    } catch (Throwable throwable) {
      LogHelper.error(throwable);
      LauncherEngine.exitLauncher(-15);
    } 
  }
  
  public void replaceOrCreateArgument(ArrayList<String> paramArrayList, String paramString1, String paramString2) {
    int i = paramArrayList.indexOf(paramString1);
    if (i < 0) {
      paramArrayList.add(paramString1);
      if (paramString2 != null)
        paramArrayList.add(paramString2); 
      return;
    } 
    if (paramString2 != null) {
      int j = i + 1;
      paramArrayList.set(j, paramString2);
    } 
  }
  
  public void preLoad() {}
  
  public void init(boolean paramBoolean) {}
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\debug\ClientRuntimeProvider.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */