package oshi.hardware.platform.mac;

import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.SoundCard;
import oshi.hardware.common.AbstractSoundCard;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;

@Immutable
final class MacSoundCard extends AbstractSoundCard {
  private static final String APPLE = "Apple Inc.";
  
  MacSoundCard(String paramString1, String paramString2, String paramString3) {
    super(paramString1, paramString2, paramString3);
  }
  
  public static List<SoundCard> getSoundCards() {
    ArrayList<MacSoundCard> arrayList = new ArrayList();
    String str1 = "Apple Inc.";
    String str2 = "AppleHDAController";
    String str3 = "AppleHDACodec";
    boolean bool = false;
    String str4 = "<key>com.apple.driver.AppleHDAController</key>";
    for (String str : FileUtil.readFile("/System/Library/Extensions/AppleHDA.kext/Contents/Info.plist")) {
      if (str.contains(str4)) {
        bool = true;
        continue;
      } 
      if (bool) {
        str2 = "AppleHDAController " + ParseUtil.getTextBetweenStrings(str, "<string>", "</string>");
        bool = false;
      } 
    } 
    arrayList.add(new MacSoundCard(str2, str1, str3));
    return (List)arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\mac\MacSoundCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */