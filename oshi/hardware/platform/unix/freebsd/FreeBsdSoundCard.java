package oshi.hardware.platform.unix.freebsd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.SoundCard;
import oshi.hardware.common.AbstractSoundCard;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
final class FreeBsdSoundCard extends AbstractSoundCard {
  private static final String LSHAL = "lshal";
  
  FreeBsdSoundCard(String paramString1, String paramString2, String paramString3) {
    super(paramString1, paramString2, paramString3);
  }
  
  public static List<SoundCard> getSoundCards() {
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    hashMap1.clear();
    hashMap2.clear();
    ArrayList<String> arrayList = new ArrayList();
    String str = "";
    for (String str1 : ExecutingCommand.runNative("lshal")) {
      str1 = str1.trim();
      if (str1.startsWith("udi =")) {
        str = ParseUtil.getSingleQuoteStringValue(str1);
        continue;
      } 
      if (!str.isEmpty() && !str1.isEmpty()) {
        if (str1.contains("freebsd.driver =") && "pcm".equals(ParseUtil.getSingleQuoteStringValue(str1))) {
          arrayList.add(str);
          continue;
        } 
        if (str1.contains("info.product")) {
          hashMap2.put(str, ParseUtil.getStringBetween(str1, '\''));
          continue;
        } 
        if (str1.contains("info.vendor"))
          hashMap1.put(str, ParseUtil.getStringBetween(str1, '\'')); 
      } 
    } 
    ArrayList<FreeBsdSoundCard> arrayList1 = new ArrayList();
    for (String str1 : arrayList)
      arrayList1.add(new FreeBsdSoundCard((String)hashMap2.get(str1), (String)hashMap1.get(str1) + " " + (String)hashMap2.get(str1), (String)hashMap2.get(str1))); 
    return (List)arrayList1;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdSoundCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */