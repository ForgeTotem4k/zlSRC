package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.util.ArrayList;

public abstract class Netapi32Util {
  public static String getDCName() {
    return getDCName(null, null);
  }
  
  public static String getDCName(String paramString1, String paramString2) {
    PointerByReference pointerByReference = new PointerByReference();
    try {
      int i = Netapi32.INSTANCE.NetGetDCName(paramString2, paramString1, pointerByReference);
      if (0 != i)
        throw new Win32Exception(i); 
      return pointerByReference.getValue().getWideString(0L);
    } finally {
      if (0 != Netapi32.INSTANCE.NetApiBufferFree(pointerByReference.getValue()))
        throw new Win32Exception(Kernel32.INSTANCE.GetLastError()); 
    } 
  }
  
  public static int getJoinStatus() {
    return getJoinStatus(null);
  }
  
  public static int getJoinStatus(String paramString) {
    PointerByReference pointerByReference = new PointerByReference();
    IntByReference intByReference = new IntByReference();
    try {
      int i = Netapi32.INSTANCE.NetGetJoinInformation(paramString, pointerByReference, intByReference);
      if (0 != i)
        throw new Win32Exception(i); 
      return intByReference.getValue();
    } finally {
      if (pointerByReference.getPointer() != null) {
        int i = Netapi32.INSTANCE.NetApiBufferFree(pointerByReference.getValue());
        if (0 != i)
          throw new Win32Exception(i); 
      } 
    } 
  }
  
  public static String getDomainName(String paramString) {
    PointerByReference pointerByReference = new PointerByReference();
    IntByReference intByReference = new IntByReference();
    try {
      int i = Netapi32.INSTANCE.NetGetJoinInformation(paramString, pointerByReference, intByReference);
      if (0 != i)
        throw new Win32Exception(i); 
      return pointerByReference.getValue().getWideString(0L);
    } finally {
      if (pointerByReference.getPointer() != null) {
        int i = Netapi32.INSTANCE.NetApiBufferFree(pointerByReference.getValue());
        if (0 != i)
          throw new Win32Exception(i); 
      } 
    } 
  }
  
  public static LocalGroup[] getLocalGroups() {
    return getLocalGroups(null);
  }
  
  public static LocalGroup[] getLocalGroups(String paramString) {
    PointerByReference pointerByReference = new PointerByReference();
    IntByReference intByReference1 = new IntByReference();
    IntByReference intByReference2 = new IntByReference();
    try {
      int i = Netapi32.INSTANCE.NetLocalGroupEnum(paramString, 1, pointerByReference, -1, intByReference1, intByReference2, null);
      if (0 != i || pointerByReference.getValue() == Pointer.NULL)
        throw new Win32Exception(i); 
      ArrayList<LocalGroup> arrayList = new ArrayList();
      if (intByReference1.getValue() > 0) {
        LMAccess.LOCALGROUP_INFO_1 lOCALGROUP_INFO_1 = new LMAccess.LOCALGROUP_INFO_1(pointerByReference.getValue());
        LMAccess.LOCALGROUP_INFO_1[] arrayOfLOCALGROUP_INFO_1 = (LMAccess.LOCALGROUP_INFO_1[])lOCALGROUP_INFO_1.toArray(intByReference1.getValue());
        for (LMAccess.LOCALGROUP_INFO_1 lOCALGROUP_INFO_11 : arrayOfLOCALGROUP_INFO_1) {
          LocalGroup localGroup = new LocalGroup();
          localGroup.name = lOCALGROUP_INFO_11.lgrui1_name;
          localGroup.comment = lOCALGROUP_INFO_11.lgrui1_comment;
          arrayList.add(localGroup);
        } 
      } 
      return arrayList.<LocalGroup>toArray(new LocalGroup[0]);
    } finally {
      if (pointerByReference.getValue() != Pointer.NULL) {
        int i = Netapi32.INSTANCE.NetApiBufferFree(pointerByReference.getValue());
        if (0 != i)
          throw new Win32Exception(i); 
      } 
    } 
  }
  
  public static Group[] getGlobalGroups() {
    return getGlobalGroups(null);
  }
  
  public static Group[] getGlobalGroups(String paramString) {
    PointerByReference pointerByReference = new PointerByReference();
    IntByReference intByReference1 = new IntByReference();
    IntByReference intByReference2 = new IntByReference();
    try {
      int i = Netapi32.INSTANCE.NetGroupEnum(paramString, 1, pointerByReference, -1, intByReference1, intByReference2, null);
      if (0 != i || pointerByReference.getValue() == Pointer.NULL)
        throw new Win32Exception(i); 
      ArrayList<LocalGroup> arrayList = new ArrayList();
      if (intByReference1.getValue() > 0) {
        LMAccess.GROUP_INFO_1 gROUP_INFO_1 = new LMAccess.GROUP_INFO_1(pointerByReference.getValue());
        LMAccess.GROUP_INFO_1[] arrayOfGROUP_INFO_1 = (LMAccess.GROUP_INFO_1[])gROUP_INFO_1.toArray(intByReference1.getValue());
        for (LMAccess.GROUP_INFO_1 gROUP_INFO_11 : arrayOfGROUP_INFO_1) {
          LocalGroup localGroup = new LocalGroup();
          localGroup.name = gROUP_INFO_11.grpi1_name;
          localGroup.comment = gROUP_INFO_11.grpi1_comment;
          arrayList.add(localGroup);
        } 
      } 
      return arrayList.<Group>toArray((Group[])new LocalGroup[0]);
    } finally {
      if (pointerByReference.getValue() != Pointer.NULL) {
        int i = Netapi32.INSTANCE.NetApiBufferFree(pointerByReference.getValue());
        if (0 != i)
          throw new Win32Exception(i); 
      } 
    } 
  }
  
  public static User[] getUsers() {
    return getUsers(null);
  }
  
  public static User[] getUsers(String paramString) {
    PointerByReference pointerByReference = new PointerByReference();
    IntByReference intByReference1 = new IntByReference();
    IntByReference intByReference2 = new IntByReference();
    try {
      int i = Netapi32.INSTANCE.NetUserEnum(paramString, 1, 0, pointerByReference, -1, intByReference1, intByReference2, null);
      if (0 != i || pointerByReference.getValue() == Pointer.NULL)
        throw new Win32Exception(i); 
      ArrayList<User> arrayList = new ArrayList();
      if (intByReference1.getValue() > 0) {
        LMAccess.USER_INFO_1 uSER_INFO_1 = new LMAccess.USER_INFO_1(pointerByReference.getValue());
        LMAccess.USER_INFO_1[] arrayOfUSER_INFO_1 = (LMAccess.USER_INFO_1[])uSER_INFO_1.toArray(intByReference1.getValue());
        for (LMAccess.USER_INFO_1 uSER_INFO_11 : arrayOfUSER_INFO_1) {
          User user = new User();
          if (uSER_INFO_11.usri1_name != null)
            user.name = uSER_INFO_11.usri1_name; 
          arrayList.add(user);
        } 
      } 
      return arrayList.<User>toArray(new User[0]);
    } finally {
      if (pointerByReference.getValue() != Pointer.NULL) {
        int i = Netapi32.INSTANCE.NetApiBufferFree(pointerByReference.getValue());
        if (0 != i)
          throw new Win32Exception(i); 
      } 
    } 
  }
  
  public static Group[] getCurrentUserLocalGroups() {
    return getUserLocalGroups(Secur32Util.getUserNameEx(2));
  }
  
  public static Group[] getUserLocalGroups(String paramString) {
    return getUserLocalGroups(paramString, null);
  }
  
  public static Group[] getUserLocalGroups(String paramString1, String paramString2) {
    PointerByReference pointerByReference = new PointerByReference();
    IntByReference intByReference1 = new IntByReference();
    IntByReference intByReference2 = new IntByReference();
    try {
      int i = Netapi32.INSTANCE.NetUserGetLocalGroups(paramString2, paramString1, 0, 0, pointerByReference, -1, intByReference1, intByReference2);
      if (i != 0)
        throw new Win32Exception(i); 
      ArrayList<LocalGroup> arrayList = new ArrayList();
      if (intByReference1.getValue() > 0) {
        LMAccess.LOCALGROUP_USERS_INFO_0 lOCALGROUP_USERS_INFO_0 = new LMAccess.LOCALGROUP_USERS_INFO_0(pointerByReference.getValue());
        LMAccess.LOCALGROUP_USERS_INFO_0[] arrayOfLOCALGROUP_USERS_INFO_0 = (LMAccess.LOCALGROUP_USERS_INFO_0[])lOCALGROUP_USERS_INFO_0.toArray(intByReference1.getValue());
        for (LMAccess.LOCALGROUP_USERS_INFO_0 lOCALGROUP_USERS_INFO_01 : arrayOfLOCALGROUP_USERS_INFO_0) {
          LocalGroup localGroup = new LocalGroup();
          if (lOCALGROUP_USERS_INFO_01.lgrui0_name != null)
            localGroup.name = lOCALGROUP_USERS_INFO_01.lgrui0_name; 
          arrayList.add(localGroup);
        } 
      } 
      return arrayList.<Group>toArray(new Group[0]);
    } finally {
      if (pointerByReference.getValue() != Pointer.NULL) {
        int i = Netapi32.INSTANCE.NetApiBufferFree(pointerByReference.getValue());
        if (0 != i)
          throw new Win32Exception(i); 
      } 
    } 
  }
  
  public static Group[] getUserGroups(String paramString) {
    return getUserGroups(paramString, null);
  }
  
  public static Group[] getUserGroups(String paramString1, String paramString2) {
    PointerByReference pointerByReference = new PointerByReference();
    IntByReference intByReference1 = new IntByReference();
    IntByReference intByReference2 = new IntByReference();
    try {
      int i = Netapi32.INSTANCE.NetUserGetGroups(paramString2, paramString1, 0, pointerByReference, -1, intByReference1, intByReference2);
      if (i != 0)
        throw new Win32Exception(i); 
      ArrayList<Group> arrayList = new ArrayList();
      if (intByReference1.getValue() > 0) {
        LMAccess.GROUP_USERS_INFO_0 gROUP_USERS_INFO_0 = new LMAccess.GROUP_USERS_INFO_0(pointerByReference.getValue());
        LMAccess.GROUP_USERS_INFO_0[] arrayOfGROUP_USERS_INFO_0 = (LMAccess.GROUP_USERS_INFO_0[])gROUP_USERS_INFO_0.toArray(intByReference1.getValue());
        for (LMAccess.GROUP_USERS_INFO_0 gROUP_USERS_INFO_01 : arrayOfGROUP_USERS_INFO_0) {
          Group group = new Group();
          if (gROUP_USERS_INFO_01.grui0_name != null)
            group.name = gROUP_USERS_INFO_01.grui0_name; 
          arrayList.add(group);
        } 
      } 
      return arrayList.<Group>toArray(new Group[0]);
    } finally {
      if (pointerByReference.getValue() != Pointer.NULL) {
        int i = Netapi32.INSTANCE.NetApiBufferFree(pointerByReference.getValue());
        if (0 != i)
          throw new Win32Exception(i); 
      } 
    } 
  }
  
  public static DomainController getDC() {
    DsGetDC.PDOMAIN_CONTROLLER_INFO pDOMAIN_CONTROLLER_INFO = new DsGetDC.PDOMAIN_CONTROLLER_INFO();
    int i = Netapi32.INSTANCE.DsGetDcName(null, null, null, null, 0, pDOMAIN_CONTROLLER_INFO);
    if (0 != i)
      throw new Win32Exception(i); 
    DomainController domainController = new DomainController();
    domainController.address = pDOMAIN_CONTROLLER_INFO.dci.DomainControllerAddress;
    domainController.addressType = pDOMAIN_CONTROLLER_INFO.dci.DomainControllerAddressType;
    domainController.clientSiteName = pDOMAIN_CONTROLLER_INFO.dci.ClientSiteName;
    domainController.dnsForestName = pDOMAIN_CONTROLLER_INFO.dci.DnsForestName;
    domainController.domainGuid = pDOMAIN_CONTROLLER_INFO.dci.DomainGuid;
    domainController.domainName = pDOMAIN_CONTROLLER_INFO.dci.DomainName;
    domainController.flags = pDOMAIN_CONTROLLER_INFO.dci.Flags;
    domainController.name = pDOMAIN_CONTROLLER_INFO.dci.DomainControllerName;
    i = Netapi32.INSTANCE.NetApiBufferFree(pDOMAIN_CONTROLLER_INFO.dci.getPointer());
    if (0 != i)
      throw new Win32Exception(i); 
    return domainController;
  }
  
  public static DomainTrust[] getDomainTrusts() {
    return getDomainTrusts(null);
  }
  
  public static DomainTrust[] getDomainTrusts(String paramString) {
    IntByReference intByReference = new IntByReference();
    PointerByReference pointerByReference = new PointerByReference();
    int i = Netapi32.INSTANCE.DsEnumerateDomainTrusts(paramString, 63, pointerByReference, intByReference);
    if (0 != i)
      throw new Win32Exception(i); 
    try {
      ArrayList<DomainTrust> arrayList = new ArrayList(intByReference.getValue());
      if (intByReference.getValue() > 0) {
        DsGetDC.DS_DOMAIN_TRUSTS dS_DOMAIN_TRUSTS = new DsGetDC.DS_DOMAIN_TRUSTS(pointerByReference.getValue());
        DsGetDC.DS_DOMAIN_TRUSTS[] arrayOfDS_DOMAIN_TRUSTS = (DsGetDC.DS_DOMAIN_TRUSTS[])dS_DOMAIN_TRUSTS.toArray((Structure[])new DsGetDC.DS_DOMAIN_TRUSTS[intByReference.getValue()]);
        for (DsGetDC.DS_DOMAIN_TRUSTS dS_DOMAIN_TRUSTS1 : arrayOfDS_DOMAIN_TRUSTS) {
          DomainTrust domainTrust = new DomainTrust();
          if (dS_DOMAIN_TRUSTS1.DnsDomainName != null)
            domainTrust.DnsDomainName = dS_DOMAIN_TRUSTS1.DnsDomainName; 
          if (dS_DOMAIN_TRUSTS1.NetbiosDomainName != null)
            domainTrust.NetbiosDomainName = dS_DOMAIN_TRUSTS1.NetbiosDomainName; 
          domainTrust.DomainSid = dS_DOMAIN_TRUSTS1.DomainSid;
          if (dS_DOMAIN_TRUSTS1.DomainSid != null)
            domainTrust.DomainSidString = Advapi32Util.convertSidToStringSid(dS_DOMAIN_TRUSTS1.DomainSid); 
          domainTrust.DomainGuid = dS_DOMAIN_TRUSTS1.DomainGuid;
          if (dS_DOMAIN_TRUSTS1.DomainGuid != null)
            domainTrust.DomainGuidString = Ole32Util.getStringFromGUID(dS_DOMAIN_TRUSTS1.DomainGuid); 
          domainTrust.flags = dS_DOMAIN_TRUSTS1.Flags;
          arrayList.add(domainTrust);
        } 
      } 
      return arrayList.<DomainTrust>toArray(new DomainTrust[0]);
    } finally {
      i = Netapi32.INSTANCE.NetApiBufferFree(pointerByReference.getValue());
      if (0 != i)
        throw new Win32Exception(i); 
    } 
  }
  
  public static UserInfo getUserInfo(String paramString) {
    return getUserInfo(paramString, getDCName());
  }
  
  public static UserInfo getUserInfo(String paramString1, String paramString2) {
    PointerByReference pointerByReference = new PointerByReference();
    try {
      int i = Netapi32.INSTANCE.NetUserGetInfo(paramString2, paramString1, 23, pointerByReference);
      if (i == 0) {
        LMAccess.USER_INFO_23 uSER_INFO_23 = new LMAccess.USER_INFO_23(pointerByReference.getValue());
        UserInfo userInfo = new UserInfo();
        userInfo.comment = uSER_INFO_23.usri23_comment;
        userInfo.flags = uSER_INFO_23.usri23_flags;
        userInfo.fullName = uSER_INFO_23.usri23_full_name;
        userInfo.name = uSER_INFO_23.usri23_name;
        if (uSER_INFO_23.usri23_user_sid != null)
          userInfo.sidString = Advapi32Util.convertSidToStringSid(uSER_INFO_23.usri23_user_sid); 
        userInfo.sid = uSER_INFO_23.usri23_user_sid;
        return userInfo;
      } 
      throw new Win32Exception(i);
    } finally {
      if (pointerByReference.getValue() != Pointer.NULL)
        Netapi32.INSTANCE.NetApiBufferFree(pointerByReference.getValue()); 
    } 
  }
  
  public static class LocalGroup extends Group {
    public String comment;
  }
  
  public static class Group {
    public String name;
  }
  
  public static class User {
    public String name;
    
    public String comment;
  }
  
  public static class DomainController {
    public String name;
    
    public String address;
    
    public int addressType;
    
    public Guid.GUID domainGuid;
    
    public String domainName;
    
    public String dnsForestName;
    
    public int flags;
    
    public String clientSiteName;
  }
  
  public static class DomainTrust {
    public String NetbiosDomainName;
    
    public String DnsDomainName;
    
    public WinNT.PSID DomainSid;
    
    public String DomainSidString;
    
    public Guid.GUID DomainGuid;
    
    public String DomainGuidString;
    
    private int flags;
    
    public boolean isInForest() {
      return ((this.flags & 0x1) != 0);
    }
    
    public boolean isOutbound() {
      return ((this.flags & 0x2) != 0);
    }
    
    public boolean isRoot() {
      return ((this.flags & 0x4) != 0);
    }
    
    public boolean isPrimary() {
      return ((this.flags & 0x8) != 0);
    }
    
    public boolean isNativeMode() {
      return ((this.flags & 0x10) != 0);
    }
    
    public boolean isInbound() {
      return ((this.flags & 0x20) != 0);
    }
  }
  
  public static class UserInfo extends User {
    public String fullName;
    
    public String sidString;
    
    public WinNT.PSID sid;
    
    public int flags;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\Netapi32Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */