package oshi.hardware.platform.unix.aix;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.SoundCard;
import oshi.hardware.common.AbstractSoundCard;
import oshi.util.ParseUtil;

@Immutable
final class AixSoundCard extends AbstractSoundCard {
  AixSoundCard(String paramString1, String paramString2, String paramString3) {
    super(paramString1, paramString2, paramString3);
  }
  
  public static List<SoundCard> getSoundCards(Supplier<List<String>> paramSupplier) {
    ArrayList<AixSoundCard> arrayList = new ArrayList();
    for (String str1 : paramSupplier.get()) {
      String str2 = str1.trim();
      if (str2.startsWith("paud")) {
        String[] arrayOfString = ParseUtil.whitespaces.split(str2, 3);
        if (arrayOfString.length == 3)
          arrayList.add(new AixSoundCard("unknown", arrayOfString[2], "unknown")); 
      } 
    } 
    return (List)arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\aix\AixSoundCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */