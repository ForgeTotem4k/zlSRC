package com.sun.jna.platform.win32;

import com.sun.jna.FromNativeContext;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeMapped;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.Union;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APITypeMapper;

public interface WinCrypt {
  public static final int CRYPTPROTECT_PROMPT_ON_UNPROTECT = 1;
  
  public static final int CRYPTPROTECT_PROMPT_ON_PROTECT = 2;
  
  public static final int CRYPTPROTECT_PROMPT_RESERVED = 4;
  
  public static final int CRYPTPROTECT_PROMPT_STRONG = 8;
  
  public static final int CRYPTPROTECT_PROMPT_REQUIRE_STRONG = 16;
  
  public static final int CRYPTPROTECT_UI_FORBIDDEN = 1;
  
  public static final int CRYPTPROTECT_LOCAL_MACHINE = 4;
  
  public static final int CRYPTPROTECT_CRED_SYNC = 8;
  
  public static final int CRYPTPROTECT_AUDIT = 16;
  
  public static final int CRYPTPROTECT_NO_RECOVERY = 32;
  
  public static final int CRYPTPROTECT_VERIFY_PROTECTION = 64;
  
  public static final int CRYPTPROTECT_CRED_REGENERATE = 128;
  
  public static final int CRYPT_E_ASN1_ERROR = -2146881280;
  
  public static final int CRYPT_E_ASN1_INTERNAL = -2146881279;
  
  public static final int CRYPT_E_ASN1_EOD = -2146881278;
  
  public static final int CRYPT_E_ASN1_CORRUPT = -2146881277;
  
  public static final int CRYPT_E_ASN1_LARGE = -2146881276;
  
  public static final int CRYPT_E_ASN1_CONSTRAINT = -2146881275;
  
  public static final int CRYPT_E_ASN1_MEMORY = -2146881274;
  
  public static final int CRYPT_E_ASN1_OVERFLOW = -2146881273;
  
  public static final int CRYPT_E_ASN1_BADPDU = -2146881272;
  
  public static final int CRYPT_E_ASN1_BADARGS = -2146881271;
  
  public static final int CRYPT_E_ASN1_BADREAL = -2146881270;
  
  public static final int CRYPT_E_ASN1_BADTAG = -2146881269;
  
  public static final int CRYPT_E_ASN1_CHOICE = -2146881268;
  
  public static final int CRYPT_E_ASN1_RULE = -2146881267;
  
  public static final int CRYPT_E_ASN1_UTF8 = -2146881266;
  
  public static final int CRYPT_E_ASN1_PDU_TYPE = -2146881229;
  
  public static final int CRYPT_E_ASN1_NYI = -2146881228;
  
  public static final int CRYPT_E_ASN1_EXTENDED = -2146881023;
  
  public static final int CRYPT_E_ASN1_NOEOD = -2146881022;
  
  public static final int CRYPT_ASN_ENCODING = 1;
  
  public static final int CRYPT_NDR_ENCODING = 2;
  
  public static final int X509_ASN_ENCODING = 1;
  
  public static final int X509_NDR_ENCODING = 2;
  
  public static final int PKCS_7_ASN_ENCODING = 65536;
  
  public static final int PKCS_7_NDR_ENCODING = 131072;
  
  public static final int USAGE_MATCH_TYPE_AND = 0;
  
  public static final int USAGE_MATCH_TYPE_OR = 1;
  
  public static final int PP_CLIENT_HWND = 1;
  
  public static final int CERT_SIMPLE_NAME_STR = 1;
  
  public static final int CERT_OID_NAME_STR = 2;
  
  public static final int CERT_X500_NAME_STR = 3;
  
  public static final int CERT_XML_NAME_STR = 4;
  
  public static final int CERT_CHAIN_POLICY_BASE = 1;
  
  public static final String szOID_RSA_SHA1RSA = "1.2.840.113549.1.1.5";
  
  public static final HCERTCHAINENGINE HCCE_CURRENT_USER = new HCERTCHAINENGINE(Pointer.createConstant(0));
  
  public static final HCERTCHAINENGINE HCCE_LOCAL_MACHINE = new HCERTCHAINENGINE(Pointer.createConstant(1));
  
  public static final HCERTCHAINENGINE HCCE_SERIAL_LOCAL_MACHINE = new HCERTCHAINENGINE(Pointer.createConstant(2));
  
  public static final int CERT_COMPARE_SHIFT = 16;
  
  public static final int CERT_COMPARE_NAME_STR_W = 8;
  
  public static final int CERT_INFO_SUBJECT_FLAG = 7;
  
  public static final int CERT_FIND_SUBJECT_STR_W = 524295;
  
  public static final int CERT_FIND_SUBJECT_STR = 524295;
  
  public static final int CRYPT_EXPORTABLE = 1;
  
  public static final int CRYPT_USER_PROTECTED = 2;
  
  public static final int CRYPT_MACHINE_KEYSET = 32;
  
  public static final int CRYPT_USER_KEYSET = 4096;
  
  public static final int PKCS12_PREFER_CNG_KSP = 256;
  
  public static final int PKCS12_ALWAYS_CNG_KSP = 512;
  
  public static final int PKCS12_ALLOW_OVERWRITE_KEY = 16384;
  
  public static final int PKCS12_NO_PERSIST_KEY = 32768;
  
  public static final int PKCS12_INCLUDE_EXTENDED_PROPERTIES = 16;
  
  public static final int CERT_STORE_PROV_MSG = 1;
  
  public static final int CERT_STORE_PROV_MEMORY = 2;
  
  public static final int CERT_STORE_PROV_FILE = 3;
  
  public static final int CERT_STORE_PROV_REG = 4;
  
  public static final int CERT_STORE_PROV_PKCS7 = 5;
  
  public static final int CERT_STORE_PROV_SERIALIZED = 6;
  
  public static final int CERT_STORE_PROV_FILENAME_A = 7;
  
  public static final int CERT_STORE_PROV_FILENAME_W = 8;
  
  public static final int CERT_STORE_PROV_FILENAME = 8;
  
  public static final int CERT_STORE_PROV_SYSTEM_A = 9;
  
  public static final int CERT_STORE_PROV_SYSTEM_W = 10;
  
  public static final int CERT_STORE_PROV_SYSTEM = 10;
  
  public static final int CERT_STORE_PROV_COLLECTION = 11;
  
  public static final int CERT_STORE_PROV_SYSTEM_REGISTRY_A = 12;
  
  public static final int CERT_STORE_PROV_SYSTEM_REGISTRY_W = 13;
  
  public static final int CERT_STORE_PROV_SYSTEM_REGISTRY = 13;
  
  public static final int CERT_STORE_PROV_PHYSICAL_W = 14;
  
  public static final int CERT_STORE_PROV_PHYSICAL = 14;
  
  public static final int CERT_STORE_PROV_SMART_CARD_W = 15;
  
  public static final int CERT_STORE_PROV_SMART_CARD = 15;
  
  public static final int CERT_STORE_PROV_LDAP_W = 16;
  
  public static final int CERT_STORE_PROV_LDAP = 16;
  
  public static final int CERT_STORE_NO_CRYPT_RELEASE_FLAG = 1;
  
  public static final int CERT_STORE_SET_LOCALIZED_NAME_FLAG = 2;
  
  public static final int CERT_STORE_DEFER_CLOSE_UNTIL_LAST_FREE_FLAG = 4;
  
  public static final int CERT_STORE_DELETE_FLAG = 16;
  
  public static final int CERT_STORE_UNSAFE_PHYSICAL_FLAG = 32;
  
  public static final int CERT_STORE_SHARE_STORE_FLAG = 64;
  
  public static final int CERT_STORE_SHARE_CONTEXT_FLAG = 128;
  
  public static final int CERT_STORE_MANIFOLD_FLAG = 256;
  
  public static final int CERT_STORE_ENUM_ARCHIVED_FLAG = 512;
  
  public static final int CERT_STORE_UPDATE_KEYID_FLAG = 1024;
  
  public static final int CERT_STORE_BACKUP_RESTORE_FLAG = 2048;
  
  public static final int CERT_STORE_MAXIMUM_ALLOWED_FLAG = 4096;
  
  public static final int CERT_STORE_CREATE_NEW_FLAG = 8192;
  
  public static final int CERT_STORE_OPEN_EXISTING_FLAG = 16384;
  
  public static final int CERT_STORE_READONLY_FLAG = 32768;
  
  public static final int CERT_SYSTEM_STORE_CURRENT_USER = 65536;
  
  public static final int CERT_SYSTEM_STORE_LOCAL_MACHINE = 131072;
  
  public static final int CERT_SYSTEM_STORE_CURRENT_SERVICE = 262144;
  
  public static final int CERT_SYSTEM_STORE_SERVICES = 327680;
  
  public static final int CERT_SYSTEM_STORE_USERS = 393216;
  
  public static final int CERT_SYSTEM_STORE_CURRENT_USER_GROUP_POLICY = 458752;
  
  public static final int CERT_SYSTEM_STORE_LOCAL_MACHINE_GROUP_POLICY = 524288;
  
  public static final int CERT_SYSTEM_STORE_LOCAL_MACHINE_ENTERPRISE = 589824;
  
  public static final int CERT_SYSTEM_STORE_UNPROTECTED_FLAG = 1073741824;
  
  public static final int CERT_SYSTEM_STORE_RELOCATE_FLAG = -2147483648;
  
  public static final int CERT_CLOSE_STORE_FORCE_FLAG = 1;
  
  public static final int CERT_CLOSE_STORE_CHECK_FLAG = 2;
  
  public static final int CERT_QUERY_CONTENT_CERT = 1;
  
  public static final int CERT_QUERY_CONTENT_CTL = 2;
  
  public static final int CERT_QUERY_CONTENT_CRL = 3;
  
  public static final int CERT_QUERY_CONTENT_SERIALIZED_STORE = 4;
  
  public static final int CERT_QUERY_CONTENT_SERIALIZED_CERT = 5;
  
  public static final int CERT_QUERY_CONTENT_SERIALIZED_CTL = 6;
  
  public static final int CERT_QUERY_CONTENT_SERIALIZED_CRL = 7;
  
  public static final int CERT_QUERY_CONTENT_PKCS7_SIGNED = 8;
  
  public static final int CERT_QUERY_CONTENT_PKCS7_UNSIGNED = 9;
  
  public static final int CERT_QUERY_CONTENT_PKCS7_SIGNED_EMBED = 10;
  
  public static final int CERT_QUERY_CONTENT_PKCS10 = 11;
  
  public static final int CERT_QUERY_CONTENT_PFX = 12;
  
  public static final int CERT_QUERY_CONTENT_CERT_PAIR = 13;
  
  public static final int CERT_QUERY_CONTENT_PFX_AND_LOAD = 14;
  
  public static final int CERT_QUERY_CONTENT_FLAG_CERT = 2;
  
  public static final int CERT_QUERY_CONTENT_FLAG_CTL = 4;
  
  public static final int CERT_QUERY_CONTENT_FLAG_CRL = 8;
  
  public static final int CERT_QUERY_CONTENT_FLAG_SERIALIZED_STORE = 16;
  
  public static final int CERT_QUERY_CONTENT_FLAG_SERIALIZED_CERT = 32;
  
  public static final int CERT_QUERY_CONTENT_FLAG_SERIALIZED_CTL = 64;
  
  public static final int CERT_QUERY_CONTENT_FLAG_SERIALIZED_CRL = 128;
  
  public static final int CERT_QUERY_CONTENT_FLAG_PKCS7_SIGNED = 256;
  
  public static final int CERT_QUERY_CONTENT_FLAG_PKCS7_UNSIGNED = 512;
  
  public static final int CERT_QUERY_CONTENT_FLAG_PKCS7_SIGNED_EMBED = 1024;
  
  public static final int CERT_QUERY_CONTENT_FLAG_PKCS10 = 2048;
  
  public static final int CERT_QUERY_CONTENT_FLAG_PFX = 4096;
  
  public static final int CERT_QUERY_CONTENT_FLAG_CERT_PAIR = 8192;
  
  public static final int CERT_QUERY_CONTENT_FLAG_PFX_AND_LOAD = 16384;
  
  public static final int CERT_QUERY_CONTENT_FLAG_ALL = 16382;
  
  public static final int CERT_QUERY_FORMAT_BINARY = 1;
  
  public static final int CERT_QUERY_FORMAT_BASE64_ENCODED = 2;
  
  public static final int CERT_QUERY_FORMAT_ASN_ASCII_HEX_ENCODED = 3;
  
  public static final int CERT_QUERY_FORMAT_FLAG_BINARY = 2;
  
  public static final int CERT_QUERY_FORMAT_FLAG_BASE64_ENCODED = 4;
  
  public static final int CERT_QUERY_FORMAT_FLAG_ASN_ASCII_HEX_ENCODED = 8;
  
  public static final int CERT_QUERY_FORMAT_FLAG_ALL = 14;
  
  public static final int CERT_QUERY_OBJECT_FILE = 1;
  
  public static final int CERT_QUERY_OBJECT_BLOB = 2;
  
  public static class HCERTCHAINENGINE extends WinNT.HANDLE {
    public HCERTCHAINENGINE() {}
    
    public HCERTCHAINENGINE(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  public static class CertStoreProviderName implements NativeMapped {
    private final Pointer pointer;
    
    private CertStoreProviderName(Pointer param1Pointer) {
      this.pointer = param1Pointer;
    }
    
    public CertStoreProviderName() {
      this.pointer = Pointer.NULL;
    }
    
    public CertStoreProviderName(int param1Int) {
      this.pointer = Pointer.createConstant(param1Int);
    }
    
    public CertStoreProviderName(String param1String) {
      byte[] arrayOfByte = Native.toByteArray(param1String);
      this.pointer = (Pointer)new Memory(arrayOfByte.length);
      this.pointer.write(0L, arrayOfByte, 0, arrayOfByte.length);
    }
    
    public Object fromNative(Object param1Object, FromNativeContext param1FromNativeContext) {
      return (param1Object == null) ? null : new CertStoreProviderName((Pointer)param1Object);
    }
    
    public Object toNative() {
      return this.pointer;
    }
    
    public Class<?> nativeType() {
      return Pointer.class;
    }
  }
  
  @FieldOrder({"cbSize", "dwPromptFlags", "hwndApp", "szPrompt"})
  public static class CRYPTPROTECT_PROMPTSTRUCT extends Structure {
    public int cbSize;
    
    public int dwPromptFlags;
    
    public WinDef.HWND hwndApp;
    
    public String szPrompt;
    
    public CRYPTPROTECT_PROMPTSTRUCT() {
      super(W32APITypeMapper.DEFAULT);
    }
    
    public CRYPTPROTECT_PROMPTSTRUCT(Pointer param1Pointer) {
      super(param1Pointer, 0, W32APITypeMapper.DEFAULT);
      read();
    }
  }
  
  public static class HCRYPTPROV_LEGACY extends BaseTSD.ULONG_PTR {
    public HCRYPTPROV_LEGACY() {}
    
    public HCRYPTPROV_LEGACY(long param1Long) {
      super(param1Long);
    }
  }
  
  public static class HCRYPTMSG extends WinNT.HANDLE {
    public HCRYPTMSG() {}
    
    public HCRYPTMSG(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  public static class HCERTSTORE extends WinNT.HANDLE {
    public HCERTSTORE() {}
    
    public HCERTSTORE(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  @FieldOrder({"cbSize", "dwMsgAndCertEncodingType", "hCryptProv", "pfnGetSignerCertificate", "pvGetArg", "pStrongSignPara"})
  public static class CRYPT_VERIFY_MESSAGE_PARA extends Structure {
    public int cbSize;
    
    public int dwMsgAndCertEncodingType;
    
    public WinCrypt.HCRYPTPROV_LEGACY hCryptProv;
    
    public WinCrypt.CryptGetSignerCertificateCallback pfnGetSignerCertificate;
    
    public Pointer pvGetArg;
    
    public WinCrypt.CERT_STRONG_SIGN_PARA.ByReference pStrongSignPara;
    
    public void write() {
      this.cbSize = size();
      super.write();
    }
    
    public static class ByReference extends WinCrypt.CRYPT_SIGN_MESSAGE_PARA implements Structure.ByReference {}
  }
  
  public static interface CryptGetSignerCertificateCallback extends StdCallLibrary.StdCallCallback {
    WinCrypt.CERT_CONTEXT.ByReference callback(Pointer param1Pointer, int param1Int, WinCrypt.CERT_INFO param1CERT_INFO, WinCrypt.HCERTSTORE param1HCERTSTORE);
  }
  
  @FieldOrder({"cbSize", "dwMsgEncodingType", "pSigningCert", "HashAlgorithm", "pvHashAuxInfo", "cMsgCert", "rgpMsgCert", "cMsgCrl", "rgpMsgCrl", "cAuthAttr", "rgAuthAttr", "cUnauthAttr", "rgUnauthAttr", "dwFlags", "dwInnerContentType", "HashEncryptionAlgorithm", "pvHashEncryptionAuxInfo"})
  public static class CRYPT_SIGN_MESSAGE_PARA extends Structure {
    public int cbSize;
    
    public int dwMsgEncodingType;
    
    public WinCrypt.CERT_CONTEXT.ByReference pSigningCert;
    
    public WinCrypt.CRYPT_ALGORITHM_IDENTIFIER HashAlgorithm;
    
    public Pointer pvHashAuxInfo;
    
    public int cMsgCert;
    
    public Pointer rgpMsgCert = null;
    
    public int cMsgCrl;
    
    public Pointer rgpMsgCrl = null;
    
    public int cAuthAttr;
    
    public Pointer rgAuthAttr = null;
    
    public int cUnauthAttr;
    
    public Pointer rgUnauthAttr = null;
    
    public int dwFlags;
    
    public int dwInnerContentType;
    
    public WinCrypt.CRYPT_ALGORITHM_IDENTIFIER HashEncryptionAlgorithm;
    
    public Pointer pvHashEncryptionAuxInfo;
    
    public WinCrypt.CERT_CONTEXT[] getRgpMsgCert() {
      WinCrypt.CERT_CONTEXT[] arrayOfCERT_CONTEXT = new WinCrypt.CERT_CONTEXT[this.cMsgCrl];
      for (byte b = 0; b < arrayOfCERT_CONTEXT.length; b++) {
        arrayOfCERT_CONTEXT[b] = (WinCrypt.CERT_CONTEXT)Structure.newInstance(WinCrypt.CERT_CONTEXT.class, this.rgpMsgCert.getPointer((b * Native.POINTER_SIZE)));
        arrayOfCERT_CONTEXT[b].read();
      } 
      return arrayOfCERT_CONTEXT;
    }
    
    public WinCrypt.CRL_CONTEXT[] getRgpMsgCrl() {
      WinCrypt.CRL_CONTEXT[] arrayOfCRL_CONTEXT = new WinCrypt.CRL_CONTEXT[this.cMsgCrl];
      for (byte b = 0; b < arrayOfCRL_CONTEXT.length; b++) {
        arrayOfCRL_CONTEXT[b] = (WinCrypt.CRL_CONTEXT)Structure.newInstance(WinCrypt.CRL_CONTEXT.class, this.rgpMsgCrl.getPointer((b * Native.POINTER_SIZE)));
        arrayOfCRL_CONTEXT[b].read();
      } 
      return arrayOfCRL_CONTEXT;
    }
    
    public WinCrypt.CRYPT_ATTRIBUTE[] getRgAuthAttr() {
      return (this.cAuthAttr == 0) ? new WinCrypt.CRYPT_ATTRIBUTE[0] : (WinCrypt.CRYPT_ATTRIBUTE[])((WinCrypt.CRYPT_ATTRIBUTE)Structure.newInstance(WinCrypt.CRYPT_ATTRIBUTE.class, this.rgAuthAttr)).toArray(this.cAuthAttr);
    }
    
    public WinCrypt.CRYPT_ATTRIBUTE[] getRgUnauthAttr() {
      return (this.cUnauthAttr == 0) ? new WinCrypt.CRYPT_ATTRIBUTE[0] : (WinCrypt.CRYPT_ATTRIBUTE[])((WinCrypt.CRYPT_ATTRIBUTE)Structure.newInstance(WinCrypt.CRYPT_ATTRIBUTE.class, this.rgUnauthAttr)).toArray(this.cUnauthAttr);
    }
    
    public static class ByReference extends CRYPT_SIGN_MESSAGE_PARA implements Structure.ByReference {}
  }
  
  @FieldOrder({"dwParam", "pbData", "cbData", "dwFlags"})
  public static class CRYPT_KEY_PROV_PARAM extends Structure {
    public int dwParam;
    
    public Pointer pbData;
    
    public int cbData;
    
    public int dwFlags;
    
    public static class ByReference extends CRYPT_KEY_PROV_PARAM implements Structure.ByReference {}
  }
  
  @FieldOrder({"pwszContainerName", "pwszProvName", "dwProvType", "dwFlags", "cProvParam", "rgProvParam", "dwKeySpec"})
  public static class CRYPT_KEY_PROV_INFO extends Structure {
    public String pwszContainerName;
    
    public String pwszProvName;
    
    public int dwProvType;
    
    public int dwFlags;
    
    public int cProvParam;
    
    public Pointer rgProvParam;
    
    public int dwKeySpec;
    
    public CRYPT_KEY_PROV_INFO() {
      super(W32APITypeMapper.UNICODE);
    }
    
    public WinCrypt.CRYPT_KEY_PROV_PARAM[] getRgProvParam() {
      WinCrypt.CRYPT_KEY_PROV_PARAM[] arrayOfCRYPT_KEY_PROV_PARAM = new WinCrypt.CRYPT_KEY_PROV_PARAM[this.cProvParam];
      for (byte b = 0; b < arrayOfCRYPT_KEY_PROV_PARAM.length; b++) {
        arrayOfCRYPT_KEY_PROV_PARAM[b] = (WinCrypt.CRYPT_KEY_PROV_PARAM)Structure.newInstance(WinCrypt.CRYPT_KEY_PROV_PARAM.class, this.rgProvParam.getPointer((b * Native.POINTER_SIZE)));
        arrayOfCRYPT_KEY_PROV_PARAM[b].read();
      } 
      return arrayOfCRYPT_KEY_PROV_PARAM;
    }
    
    public static class ByReference extends CRYPT_KEY_PROV_INFO implements Structure.ByReference {}
  }
  
  @FieldOrder({"cbData", "pbData", "cUnusedBits"})
  public static class CRYPT_BIT_BLOB extends Structure {
    public int cbData;
    
    public Pointer pbData;
    
    public int cUnusedBits;
    
    public static class ByReference extends CRYPT_BIT_BLOB implements Structure.ByReference {}
  }
  
  @FieldOrder({"pszObjId", "cValue", "rgValue"})
  public static class CRYPT_ATTRIBUTE extends Structure {
    public String pszObjId;
    
    public int cValue;
    
    public WinCrypt.DATA_BLOB.ByReference rgValue;
    
    public WinCrypt.DATA_BLOB[] getRgValue() {
      return (WinCrypt.DATA_BLOB[])this.rgValue.toArray(this.cValue);
    }
    
    public CRYPT_ATTRIBUTE() {
      super(W32APITypeMapper.ASCII);
    }
    
    public static class ByReference extends CRYPT_ATTRIBUTE implements Structure.ByReference {}
  }
  
  @FieldOrder({"pszObjId", "Parameters"})
  public static class CRYPT_ALGORITHM_IDENTIFIER extends Structure {
    public String pszObjId;
    
    public WinCrypt.DATA_BLOB Parameters;
    
    public CRYPT_ALGORITHM_IDENTIFIER() {
      super(W32APITypeMapper.ASCII);
    }
    
    public static class ByReference extends CRYPT_ALGORITHM_IDENTIFIER implements Structure.ByReference {}
  }
  
  @FieldOrder({"dwVersion", "SignatureAlgorithm", "Issuer", "ThisUpdate", "NextUpdate", "cCRLEntry", "rgCRLEntry", "cExtension", "rgExtension"})
  public static class CRL_INFO extends Structure {
    public int dwVersion;
    
    public WinCrypt.CRYPT_ALGORITHM_IDENTIFIER SignatureAlgorithm;
    
    public WinCrypt.DATA_BLOB Issuer;
    
    public WinBase.FILETIME ThisUpdate;
    
    public WinBase.FILETIME NextUpdate;
    
    public int cCRLEntry;
    
    public Pointer rgCRLEntry;
    
    public int cExtension;
    
    public Pointer rgExtension;
    
    public WinCrypt.CRL_ENTRY[] getRgCRLEntry() {
      if (this.cCRLEntry == 0)
        return new WinCrypt.CRL_ENTRY[0]; 
      WinCrypt.CRL_ENTRY[] arrayOfCRL_ENTRY = (WinCrypt.CRL_ENTRY[])((WinCrypt.CRL_ENTRY)Structure.newInstance(WinCrypt.CRL_ENTRY.class, this.rgCRLEntry)).toArray(this.cCRLEntry);
      arrayOfCRL_ENTRY[0].read();
      return arrayOfCRL_ENTRY;
    }
    
    public WinCrypt.CERT_EXTENSION[] getRgExtension() {
      if (this.cExtension == 0)
        return new WinCrypt.CERT_EXTENSION[0]; 
      WinCrypt.CERT_EXTENSION[] arrayOfCERT_EXTENSION = (WinCrypt.CERT_EXTENSION[])((WinCrypt.CERT_EXTENSION)Structure.newInstance(WinCrypt.CERT_EXTENSION.class, this.rgExtension)).toArray(this.cExtension);
      arrayOfCERT_EXTENSION[0].read();
      return arrayOfCERT_EXTENSION;
    }
    
    public static class ByReference extends CRL_INFO implements Structure.ByReference {}
  }
  
  @FieldOrder({"SerialNumber", "RevocationDate", "cExtension", "rgExtension"})
  public static class CRL_ENTRY extends Structure {
    public WinCrypt.DATA_BLOB SerialNumber;
    
    public WinBase.FILETIME RevocationDate;
    
    public int cExtension;
    
    public Pointer rgExtension;
    
    public WinCrypt.CERT_EXTENSION[] getRgExtension() {
      if (this.cExtension == 0)
        return new WinCrypt.CERT_EXTENSION[0]; 
      WinCrypt.CERT_EXTENSION[] arrayOfCERT_EXTENSION = (WinCrypt.CERT_EXTENSION[])((WinCrypt.CERT_EXTENSION)Structure.newInstance(WinCrypt.CERT_EXTENSION.class, this.rgExtension)).toArray(this.cExtension);
      arrayOfCERT_EXTENSION[0].read();
      return arrayOfCERT_EXTENSION;
    }
    
    public static class ByReference extends CRL_ENTRY implements Structure.ByReference {}
  }
  
  @FieldOrder({"dwCertEncodingType", "pbCrlEncoded", "cbCrlEncoded", "pCrlInfo", "hCertStore"})
  public static class CRL_CONTEXT extends Structure {
    public int dwCertEncodingType;
    
    public Pointer pbCrlEncoded;
    
    public int cbCrlEncoded;
    
    public WinCrypt.CRL_INFO.ByReference pCrlInfo;
    
    public WinCrypt.HCERTSTORE hCertStore;
    
    public static class ByReference extends CRL_CONTEXT implements Structure.ByReference {}
  }
  
  @FieldOrder({"Algorithm", "PublicKey"})
  public static class CERT_PUBLIC_KEY_INFO extends Structure {
    public WinCrypt.CRYPT_ALGORITHM_IDENTIFIER Algorithm;
    
    public WinCrypt.CRYPT_BIT_BLOB PublicKey;
    
    public static class ByReference extends CERT_PUBLIC_KEY_INFO implements Structure.ByReference {}
  }
  
  @FieldOrder({"dwVersion", "SerialNumber", "SignatureAlgorithm", "Issuer", "NotBefore", "NotAfter", "Subject", "SubjectPublicKeyInfo", "IssuerUniqueId", "SubjectUniqueId", "cExtension", "rgExtension"})
  public static class CERT_INFO extends Structure {
    public int dwVersion;
    
    public WinCrypt.DATA_BLOB SerialNumber;
    
    public WinCrypt.CRYPT_ALGORITHM_IDENTIFIER SignatureAlgorithm;
    
    public WinCrypt.DATA_BLOB Issuer;
    
    public WinBase.FILETIME NotBefore;
    
    public WinBase.FILETIME NotAfter;
    
    public WinCrypt.DATA_BLOB Subject;
    
    public WinCrypt.CERT_PUBLIC_KEY_INFO SubjectPublicKeyInfo;
    
    public WinCrypt.CRYPT_BIT_BLOB IssuerUniqueId;
    
    public WinCrypt.CRYPT_BIT_BLOB SubjectUniqueId;
    
    public int cExtension;
    
    public Pointer rgExtension;
    
    public WinCrypt.CERT_EXTENSION[] getRgExtension() {
      if (this.cExtension == 0)
        return new WinCrypt.CERT_EXTENSION[0]; 
      WinCrypt.CERT_EXTENSION[] arrayOfCERT_EXTENSION = (WinCrypt.CERT_EXTENSION[])((WinCrypt.CERT_EXTENSION)Structure.newInstance(WinCrypt.CERT_EXTENSION.class, this.rgExtension)).toArray(this.cExtension);
      arrayOfCERT_EXTENSION[0].read();
      return arrayOfCERT_EXTENSION;
    }
    
    public static class ByReference extends CERT_INFO implements Structure.ByReference {}
  }
  
  @FieldOrder({"cExtension", "rgExtension"})
  public static class CERT_EXTENSIONS extends Structure {
    public int cExtension;
    
    public Pointer rgExtension;
    
    public WinCrypt.CERT_EXTENSION[] getRgExtension() {
      if (this.cExtension == 0)
        return new WinCrypt.CERT_EXTENSION[0]; 
      WinCrypt.CERT_EXTENSION[] arrayOfCERT_EXTENSION = (WinCrypt.CERT_EXTENSION[])((WinCrypt.CERT_EXTENSION)Structure.newInstance(WinCrypt.CERT_EXTENSION.class, this.rgExtension)).toArray(this.cExtension);
      arrayOfCERT_EXTENSION[0].read();
      return arrayOfCERT_EXTENSION;
    }
    
    public static class ByReference extends CERT_EXTENSIONS implements Structure.ByReference {}
  }
  
  @FieldOrder({"pszObjId", "fCritical", "Value"})
  public static class CERT_EXTENSION extends Structure {
    public String pszObjId;
    
    public boolean fCritical;
    
    public WinCrypt.DATA_BLOB Value;
    
    public CERT_EXTENSION() {
      super(W32APITypeMapper.ASCII);
    }
    
    public static class ByReference extends CERT_EXTENSION implements Structure.ByReference {}
  }
  
  @FieldOrder({"dwCertEncodingType", "pbCertEncoded", "cbCertEncoded", "pCertInfo", "hCertStore"})
  public static class CERT_CONTEXT extends Structure {
    public int dwCertEncodingType;
    
    public Pointer pbCertEncoded;
    
    public int cbCertEncoded;
    
    public WinCrypt.CERT_INFO.ByReference pCertInfo;
    
    public WinCrypt.HCERTSTORE hCertStore;
    
    public static class ByReference extends CERT_CONTEXT implements Structure.ByReference {}
  }
  
  @FieldOrder({"cbSize", "TrustStatus", "cChain", "rgpChain", "cLowerQualityChainContext", "rgpLowerQualityChainContext", "fHasRevocationFreshnessTime", "dwRevocationFreshnessTime", "dwCreateFlags", "ChainId"})
  public static class CERT_CHAIN_CONTEXT extends Structure {
    public int cbSize;
    
    public WinCrypt.CERT_TRUST_STATUS TrustStatus;
    
    public int cChain;
    
    public Pointer rgpChain;
    
    public int cLowerQualityChainContext;
    
    public Pointer rgpLowerQualityChainContext;
    
    public boolean fHasRevocationFreshnessTime;
    
    public int dwRevocationFreshnessTime;
    
    public int dwCreateFlags;
    
    public Guid.GUID ChainId;
    
    public WinCrypt.CERT_SIMPLE_CHAIN[] getRgpChain() {
      WinCrypt.CERT_SIMPLE_CHAIN[] arrayOfCERT_SIMPLE_CHAIN = new WinCrypt.CERT_SIMPLE_CHAIN[this.cChain];
      for (byte b = 0; b < arrayOfCERT_SIMPLE_CHAIN.length; b++) {
        arrayOfCERT_SIMPLE_CHAIN[b] = (WinCrypt.CERT_SIMPLE_CHAIN)Structure.newInstance(WinCrypt.CERT_SIMPLE_CHAIN.class, this.rgpChain.getPointer((b * Native.POINTER_SIZE)));
        arrayOfCERT_SIMPLE_CHAIN[b].read();
      } 
      return arrayOfCERT_SIMPLE_CHAIN;
    }
    
    public CERT_CHAIN_CONTEXT[] getRgpLowerQualityChainContext() {
      CERT_CHAIN_CONTEXT[] arrayOfCERT_CHAIN_CONTEXT = new CERT_CHAIN_CONTEXT[this.cLowerQualityChainContext];
      for (byte b = 0; b < arrayOfCERT_CHAIN_CONTEXT.length; b++) {
        arrayOfCERT_CHAIN_CONTEXT[b] = (CERT_CHAIN_CONTEXT)Structure.newInstance(CERT_CHAIN_CONTEXT.class, this.rgpLowerQualityChainContext.getPointer((b * Native.POINTER_SIZE)));
        arrayOfCERT_CHAIN_CONTEXT[b].read();
      } 
      return arrayOfCERT_CHAIN_CONTEXT;
    }
    
    public CERT_CHAIN_CONTEXT() {
      super(W32APITypeMapper.DEFAULT);
    }
    
    public static class ByReference extends CERT_CHAIN_CONTEXT implements Structure.ByReference {}
  }
  
  @FieldOrder({"cbSize", "dwFlags", "pvExtraPolicyPara"})
  public static class CERT_CHAIN_POLICY_PARA extends Structure {
    public int cbSize;
    
    public int dwFlags;
    
    public Pointer pvExtraPolicyPara;
    
    public static class ByReference extends CERT_CHAIN_POLICY_PARA implements Structure.ByReference {}
  }
  
  @FieldOrder({"cbSize", "TrustStatus", "cElement", "rgpElement", "pTrustListInfo", "fHasRevocationFreshnessTime", "dwRevocationFreshnessTime"})
  public static class CERT_SIMPLE_CHAIN extends Structure {
    public int cbSize;
    
    public WinCrypt.CERT_TRUST_STATUS TrustStatus;
    
    public int cElement;
    
    public Pointer rgpElement;
    
    public WinCrypt.CERT_TRUST_LIST_INFO.ByReference pTrustListInfo;
    
    public boolean fHasRevocationFreshnessTime;
    
    public int dwRevocationFreshnessTime;
    
    public CERT_SIMPLE_CHAIN() {
      super(W32APITypeMapper.DEFAULT);
    }
    
    public WinCrypt.CERT_CHAIN_ELEMENT[] getRgpElement() {
      WinCrypt.CERT_CHAIN_ELEMENT[] arrayOfCERT_CHAIN_ELEMENT = new WinCrypt.CERT_CHAIN_ELEMENT[this.cElement];
      for (byte b = 0; b < arrayOfCERT_CHAIN_ELEMENT.length; b++) {
        arrayOfCERT_CHAIN_ELEMENT[b] = (WinCrypt.CERT_CHAIN_ELEMENT)Structure.newInstance(WinCrypt.CERT_CHAIN_ELEMENT.class, this.rgpElement.getPointer((b * Native.POINTER_SIZE)));
        arrayOfCERT_CHAIN_ELEMENT[b].read();
      } 
      return arrayOfCERT_CHAIN_ELEMENT;
    }
    
    public static class ByReference extends CERT_SIMPLE_CHAIN implements Structure.ByReference {}
  }
  
  @FieldOrder({"cbSize", "dwError", "lChainIndex", "lElementIndex", "pvExtraPolicyStatus"})
  public static class CERT_CHAIN_POLICY_STATUS extends Structure {
    public int cbSize;
    
    public int dwError;
    
    public int lChainIndex;
    
    public int lElementIndex;
    
    public Pointer pvExtraPolicyStatus;
    
    public static class ByReference extends CERT_CHAIN_POLICY_STATUS implements Structure.ByReference {}
  }
  
  @FieldOrder({"dwFlags", "pwszCNGSignHashAlgids", "pwszCNGPubKeyMinBitLengths"})
  public static class CERT_STRONG_SIGN_SERIALIZED_INFO extends Structure {
    public int dwFlags;
    
    public String pwszCNGSignHashAlgids;
    
    public String pwszCNGPubKeyMinBitLengths;
    
    public CERT_STRONG_SIGN_SERIALIZED_INFO() {
      super(W32APITypeMapper.UNICODE);
    }
    
    public static class ByReference extends WinCrypt.CERT_CHAIN_PARA implements Structure.ByReference {}
  }
  
  @FieldOrder({"cbSize", "dwInfoChoice", "DUMMYUNIONNAME"})
  public static class CERT_STRONG_SIGN_PARA extends Structure {
    public int cbSize;
    
    public int dwInfoChoice;
    
    public DUMMYUNION DUMMYUNIONNAME;
    
    public class DUMMYUNION extends Union {
      Pointer pvInfo;
      
      WinCrypt.CERT_STRONG_SIGN_SERIALIZED_INFO.ByReference pSerializedInfo;
      
      WTypes.LPSTR pszOID;
    }
    
    public static class ByReference extends WinCrypt.CERT_CHAIN_PARA implements Structure.ByReference {}
  }
  
  @FieldOrder({"cbSize", "RequestedUsage", "RequestedIssuancePolicy", "dwUrlRetrievalTimeout", "fCheckRevocationFreshnessTime", "dwRevocationFreshnessTime", "pftCacheResync", "pStrongSignPara", "dwStrongSignFlags"})
  public static class CERT_CHAIN_PARA extends Structure {
    public int cbSize;
    
    public WinCrypt.CERT_USAGE_MATCH RequestedUsage;
    
    public WinCrypt.CERT_USAGE_MATCH RequestedIssuancePolicy;
    
    public int dwUrlRetrievalTimeout;
    
    public boolean fCheckRevocationFreshnessTime;
    
    public int dwRevocationFreshnessTime;
    
    public WinBase.FILETIME.ByReference pftCacheResync;
    
    public WinCrypt.CERT_STRONG_SIGN_PARA.ByReference pStrongSignPara;
    
    public int dwStrongSignFlags;
    
    public CERT_CHAIN_PARA() {
      super(W32APITypeMapper.DEFAULT);
    }
    
    public static class ByReference extends CERT_CHAIN_PARA implements Structure.ByReference {}
  }
  
  @FieldOrder({"dwType", "Usage"})
  public static class CERT_USAGE_MATCH extends Structure {
    public int dwType;
    
    public WinCrypt.CTL_USAGE Usage;
    
    public static class ByReference extends CERT_USAGE_MATCH implements Structure.ByReference {}
  }
  
  @FieldOrder({"cUsageIdentifier", "rgpszUsageIdentifier"})
  public static class CTL_USAGE extends Structure {
    public int cUsageIdentifier;
    
    public Pointer rgpszUsageIdentifier;
    
    public String[] getRgpszUsageIdentier() {
      return (this.cUsageIdentifier == 0) ? new String[0] : this.rgpszUsageIdentifier.getStringArray(0L, this.cUsageIdentifier);
    }
    
    public void setRgpszUsageIdentier(String[] param1ArrayOfString) {
      if (param1ArrayOfString == null || param1ArrayOfString.length == 0) {
        this.cUsageIdentifier = 0;
        this.rgpszUsageIdentifier = null;
      } else {
        this.cUsageIdentifier = param1ArrayOfString.length;
        this.rgpszUsageIdentifier = (Pointer)new StringArray(param1ArrayOfString);
      } 
    }
    
    public static class ByReference extends CTL_USAGE implements Structure.ByReference {}
  }
  
  @FieldOrder({"cbSize", "pCtlEntry", "pCtlContext"})
  public static class CERT_TRUST_LIST_INFO extends Structure {
    public int cbSize;
    
    public WinCrypt.CTL_ENTRY.ByReference pCtlEntry;
    
    public WinCrypt.CTL_CONTEXT.ByReference pCtlContext;
    
    public static class ByReference extends CERT_TRUST_LIST_INFO implements Structure.ByReference {}
  }
  
  @FieldOrder({"dwMsgAndCertEncodingType", "pbCtlEncoded", "cbCtlEncoded", "pCtlInfo", "hCertStore", "hCryptMsg", "pbCtlContent", "cbCtlContent"})
  public static class CTL_CONTEXT extends Structure {
    public int dwMsgAndCertEncodingType;
    
    public Pointer pbCtlEncoded;
    
    public int cbCtlEncoded;
    
    public WinCrypt.CTL_INFO.ByReference pCtlInfo;
    
    public WinCrypt.HCERTSTORE hCertStore;
    
    public WinCrypt.HCRYPTMSG hCryptMsg;
    
    public Pointer pbCtlContent;
    
    public int cbCtlContent;
    
    public static class ByReference extends CTL_CONTEXT implements Structure.ByReference {}
  }
  
  @FieldOrder({"dwVersion", "SubjectUsage", "ListIdentifier", "SequenceNumber", "ThisUpdate", "NextUpdate", "SubjectAlgorithm", "cCTLEntry", "rgCTLEntry", "cExtension", "rgExtension"})
  public static class CTL_INFO extends Structure {
    public int dwVersion;
    
    public WinCrypt.CTL_USAGE SubjectUsage;
    
    public WinCrypt.DATA_BLOB ListIdentifier;
    
    public WinCrypt.DATA_BLOB SequenceNumber;
    
    public WinBase.FILETIME ThisUpdate;
    
    public WinBase.FILETIME NextUpdate;
    
    public WinCrypt.CRYPT_ALGORITHM_IDENTIFIER SubjectAlgorithm;
    
    public int cCTLEntry;
    
    public Pointer rgCTLEntry;
    
    public int cExtension;
    
    public Pointer rgExtension;
    
    public WinCrypt.CTL_ENTRY[] getRgCTLEntry() {
      if (this.cCTLEntry == 0)
        return new WinCrypt.CTL_ENTRY[0]; 
      WinCrypt.CTL_ENTRY[] arrayOfCTL_ENTRY = (WinCrypt.CTL_ENTRY[])((WinCrypt.CTL_ENTRY)Structure.newInstance(WinCrypt.CTL_ENTRY.class, this.rgCTLEntry)).toArray(this.cCTLEntry);
      arrayOfCTL_ENTRY[0].read();
      return arrayOfCTL_ENTRY;
    }
    
    public WinCrypt.CERT_EXTENSION[] getRgExtension() {
      if (this.cExtension == 0)
        return new WinCrypt.CERT_EXTENSION[0]; 
      WinCrypt.CERT_EXTENSION[] arrayOfCERT_EXTENSION = (WinCrypt.CERT_EXTENSION[])((WinCrypt.CERT_EXTENSION)Structure.newInstance(WinCrypt.CERT_EXTENSION.class, this.rgExtension)).toArray(this.cExtension);
      arrayOfCERT_EXTENSION[0].read();
      return arrayOfCERT_EXTENSION;
    }
    
    public static class ByReference extends CTL_INFO implements Structure.ByReference {}
  }
  
  @FieldOrder({"cbSize", "pCertContext", "TrustStatus", "pRevocationInfo", "pIssuanceUsage", "pApplicationUsage", "pwszExtendedErrorInfo"})
  public static class CERT_CHAIN_ELEMENT extends Structure {
    public int cbSize;
    
    public WinCrypt.CERT_CONTEXT.ByReference pCertContext;
    
    public WinCrypt.CERT_TRUST_STATUS TrustStatus;
    
    public WinCrypt.CERT_REVOCATION_INFO.ByReference pRevocationInfo;
    
    public WinCrypt.CTL_USAGE.ByReference pIssuanceUsage;
    
    public WinCrypt.CTL_USAGE.ByReference pApplicationUsage;
    
    public String pwszExtendedErrorInfo;
    
    public CERT_CHAIN_ELEMENT() {
      super(W32APITypeMapper.UNICODE);
    }
    
    public CERT_CHAIN_ELEMENT(Pointer param1Pointer) {
      super(param1Pointer, 0, W32APITypeMapper.UNICODE);
    }
    
    public static class ByReference extends CERT_CHAIN_ELEMENT implements Structure.ByReference {}
  }
  
  @FieldOrder({"cbSize", "dwRevocationResult", "pszRevocationOid", "pvOidSpecificInfo", "fHasFreshnessTime", "dwFreshnessTime", "pCrlInfo"})
  public static class CERT_REVOCATION_INFO extends Structure {
    public int cbSize;
    
    public int dwRevocationResult;
    
    public String pszRevocationOid;
    
    public Pointer pvOidSpecificInfo;
    
    public boolean fHasFreshnessTime;
    
    public int dwFreshnessTime;
    
    public WinCrypt.CERT_REVOCATION_CRL_INFO.ByReference pCrlInfo;
    
    public CERT_REVOCATION_INFO() {
      super(W32APITypeMapper.ASCII);
    }
    
    public static class ByReference extends CERT_REVOCATION_INFO implements Structure.ByReference {}
  }
  
  @FieldOrder({"cbSize", "pBaseCRLContext", "pDeltaCRLContext", "pCrlEntry", "fDeltaCrlEntry"})
  public static class CERT_REVOCATION_CRL_INFO extends Structure {
    public int cbSize;
    
    public WinCrypt.CRL_CONTEXT.ByReference pBaseCRLContext;
    
    public WinCrypt.CRL_CONTEXT.ByReference pDeltaCRLContext;
    
    public WinCrypt.CRL_ENTRY.ByReference pCrlEntry;
    
    public boolean fDeltaCrlEntry;
    
    public CERT_REVOCATION_CRL_INFO() {
      super(W32APITypeMapper.DEFAULT);
    }
    
    public static class ByReference extends CERT_REVOCATION_CRL_INFO implements Structure.ByReference {}
  }
  
  @FieldOrder({"SubjectIdentifier", "cAttribute", "rgAttribute"})
  public static class CTL_ENTRY extends Structure {
    public WinCrypt.DATA_BLOB SubjectIdentifier;
    
    public int cAttribute;
    
    public Pointer rgAttribute;
    
    public WinCrypt.CRYPT_ATTRIBUTE[] getRgAttribute() {
      if (this.cAttribute == 0)
        return new WinCrypt.CRYPT_ATTRIBUTE[0]; 
      WinCrypt.CRYPT_ATTRIBUTE[] arrayOfCRYPT_ATTRIBUTE = (WinCrypt.CRYPT_ATTRIBUTE[])((WinCrypt.CRYPT_ATTRIBUTE)Structure.newInstance(WinCrypt.CRYPT_ATTRIBUTE.class, this.rgAttribute)).toArray(this.cAttribute);
      arrayOfCRYPT_ATTRIBUTE[0].read();
      return arrayOfCRYPT_ATTRIBUTE;
    }
    
    public static class ByReference extends CTL_ENTRY implements Structure.ByReference {}
  }
  
  @FieldOrder({"dwErrorStatus", "dwInfoStatus"})
  public static class CERT_TRUST_STATUS extends Structure {
    public int dwErrorStatus;
    
    public int dwInfoStatus;
    
    public static class ByReference extends CERT_TRUST_STATUS implements Structure.ByReference {}
  }
  
  @FieldOrder({"cbData", "pbData"})
  public static class DATA_BLOB extends Structure {
    public int cbData;
    
    public Pointer pbData;
    
    public DATA_BLOB() {}
    
    public DATA_BLOB(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public DATA_BLOB(byte[] param1ArrayOfbyte) {
      if (param1ArrayOfbyte.length > 0) {
        this.pbData = (Pointer)new Memory(param1ArrayOfbyte.length);
        this.pbData.write(0L, param1ArrayOfbyte, 0, param1ArrayOfbyte.length);
        this.cbData = param1ArrayOfbyte.length;
      } else {
        this.pbData = (Pointer)new Memory(1L);
        this.cbData = 0;
      } 
    }
    
    public DATA_BLOB(String param1String) {
      this(Native.toByteArray(param1String));
    }
    
    public byte[] getData() {
      return (this.pbData == null) ? null : this.pbData.getByteArray(0L, this.cbData);
    }
    
    public static class ByReference extends DATA_BLOB implements Structure.ByReference {}
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\WinCrypt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */