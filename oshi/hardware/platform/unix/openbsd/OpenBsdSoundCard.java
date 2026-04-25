package oshi.hardware.platform.unix.openbsd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.SoundCard;
import oshi.hardware.common.AbstractSoundCard;
import oshi.util.ExecutingCommand;

@Immutable
final class OpenBsdSoundCard extends AbstractSoundCard {
  private static final Pattern AUDIO_AT = Pattern.compile("audio\\d+ at (.+)");
  
  private static final Pattern PCI_AT = Pattern.compile("(.+) at pci\\d+ dev \\d+ function \\d+ \"(.*)\" (rev .+):.*");
  
  OpenBsdSoundCard(String paramString1, String paramString2, String paramString3) {
    super(paramString1, paramString2, paramString3);
  }
  
  public static List<SoundCard> getSoundCards() {
    List list = ExecutingCommand.runNative("dmesg");
    HashSet<String> hashSet = new HashSet();
    for (String str1 : list) {
      Matcher matcher = AUDIO_AT.matcher(str1);
      if (matcher.matches())
        hashSet.add(matcher.group(1)); 
    } 
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    HashMap<Object, Object> hashMap3 = new HashMap<>();
    String str = "";
    for (String str1 : list) {
      Matcher matcher = PCI_AT.matcher(str1);
      if (matcher.matches() && hashSet.contains(matcher.group(1))) {
        str = matcher.group(1);
        hashMap1.put(str, matcher.group(2));
        hashMap3.put(str, matcher.group(3));
        continue;
      } 
      if (!str.isEmpty()) {
        int i = str1.indexOf("codec");
        if (i >= 0) {
          i = str1.indexOf(':');
          hashMap2.put(str, str1.substring(i + 1).trim());
        } 
        str = "";
      } 
    } 
    ArrayList<OpenBsdSoundCard> arrayList = new ArrayList();
    for (Map.Entry<Object, Object> entry : hashMap1.entrySet())
      arrayList.add(new OpenBsdSoundCard((String)hashMap3.get(entry.getKey()), (String)entry.getValue(), (String)hashMap2.get(entry.getKey()))); 
    return (List)arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\openbsd\OpenBsdSoundCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */