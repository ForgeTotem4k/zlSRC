package oshi.hardware.platform.linux;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.SoundCard;
import oshi.hardware.common.AbstractSoundCard;
import oshi.util.FileUtil;
import oshi.util.platform.linux.ProcPath;

@Immutable
final class LinuxSoundCard extends AbstractSoundCard {
  private static final Logger LOG = LoggerFactory.getLogger(LinuxSoundCard.class);
  
  private static final String CARD_FOLDER = "card";
  
  private static final String CARDS_FILE = "cards";
  
  private static final String ID_FILE = "id";
  
  LinuxSoundCard(String paramString1, String paramString2, String paramString3) {
    super(paramString1, paramString2, paramString3);
  }
  
  private static List<File> getCardFolders() {
    File file = new File(ProcPath.ASOUND);
    ArrayList<File> arrayList = new ArrayList();
    File[] arrayOfFile = file.listFiles();
    if (arrayOfFile != null) {
      for (File file1 : arrayOfFile) {
        if (file1.getName().startsWith("card") && file1.isDirectory())
          arrayList.add(file1); 
      } 
    } else {
      LOG.warn("No Audio Cards Found");
    } 
    return arrayList;
  }
  
  private static String getSoundCardVersion() {
    String str = FileUtil.getStringFromFile(ProcPath.ASOUND + "version");
    return str.isEmpty() ? "not available" : str;
  }
  
  private static String getCardCodec(File paramFile) {
    String str = "";
    File[] arrayOfFile = paramFile.listFiles();
    if (arrayOfFile != null)
      for (File file : arrayOfFile) {
        if (file.getName().startsWith("codec"))
          if (!file.isDirectory()) {
            str = (String)FileUtil.getKeyValueMapFromFile(file.getPath(), ":").get("Codec");
          } else {
            File[] arrayOfFile1 = file.listFiles();
            if (arrayOfFile1 != null)
              for (File file1 : arrayOfFile1) {
                if (!file1.isDirectory() && file1.getName().contains("#")) {
                  str = file1.getName().substring(0, file1.getName().indexOf('#'));
                  break;
                } 
              }  
          }  
      }  
    return str;
  }
  
  private static String getCardName(File paramFile) {
    String str1 = "Not Found..";
    Map map = FileUtil.getKeyValueMapFromFile(ProcPath.ASOUND + "/" + "cards", ":");
    String str2 = FileUtil.getStringFromFile(paramFile.getPath() + "/" + "id");
    for (Map.Entry entry : map.entrySet()) {
      if (((String)entry.getKey()).contains(str2))
        return (String)entry.getValue(); 
    } 
    return str1;
  }
  
  public static List<SoundCard> getSoundCards() {
    ArrayList<LinuxSoundCard> arrayList = new ArrayList();
    for (File file : getCardFolders())
      arrayList.add(new LinuxSoundCard(getSoundCardVersion(), getCardName(file), getCardCodec(file))); 
    return (List)arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\linux\LinuxSoundCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */