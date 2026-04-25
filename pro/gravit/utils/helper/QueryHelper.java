package pro.gravit.utils.helper;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryHelper {
  public static Map<String, List<String>> splitUriQuery(URI paramURI) {
    String str = paramURI.getRawQuery();
    if (str == null)
      return Collections.emptyMap(); 
    HashMap<Object, Object> hashMap = new HashMap<>();
    String[] arrayOfString = str.split("&");
    for (String str1 : arrayOfString) {
      String[] arrayOfString1 = str1.split("=");
      List<String> list = (List)hashMap.computeIfAbsent(URLDecoder.decode(arrayOfString1[0], StandardCharsets.UTF_8), paramString -> new ArrayList(1));
      list.add(URLDecoder.decode(arrayOfString1[1], StandardCharsets.UTF_8));
    } 
    return (Map)hashMap;
  }
  
  public static String encodeFormPair(String paramString1, String paramString2) {
    return URLEncoder.encode(paramString1, StandardCharsets.UTF_8) + "=" + URLEncoder.encode(paramString1, StandardCharsets.UTF_8);
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\QueryHelper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */