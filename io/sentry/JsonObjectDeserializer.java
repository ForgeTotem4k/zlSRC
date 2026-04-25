package io.sentry;

import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class JsonObjectDeserializer {
  private final ArrayList<Token> tokens = new ArrayList<>();
  
  @Nullable
  public Object deserialize(@NotNull JsonObjectReader paramJsonObjectReader) throws IOException {
    parse(paramJsonObjectReader);
    Token token = getCurrentToken();
    return (token != null) ? token.getValue() : null;
  }
  
  private void parse(@NotNull JsonObjectReader paramJsonObjectReader) throws IOException {
    boolean bool = false;
    switch (paramJsonObjectReader.peek()) {
      case BEGIN_ARRAY:
        paramJsonObjectReader.beginArray();
        pushCurrentToken(new TokenArray());
        break;
      case END_ARRAY:
        paramJsonObjectReader.endArray();
        bool = handleArrayOrMapEnd();
        break;
      case BEGIN_OBJECT:
        paramJsonObjectReader.beginObject();
        pushCurrentToken(new TokenMap());
        break;
      case END_OBJECT:
        paramJsonObjectReader.endObject();
        bool = handleArrayOrMapEnd();
        break;
      case NAME:
        pushCurrentToken(new TokenName(paramJsonObjectReader.nextName()));
        break;
      case STRING:
        bool = handlePrimitive(() -> paramJsonObjectReader.nextString());
        break;
      case NUMBER:
        bool = handlePrimitive(() -> nextNumber(paramJsonObjectReader));
        break;
      case BOOLEAN:
        bool = handlePrimitive(() -> Boolean.valueOf(paramJsonObjectReader.nextBoolean()));
        break;
      case NULL:
        paramJsonObjectReader.nextNull();
        bool = handlePrimitive(() -> null);
        break;
      case END_DOCUMENT:
        bool = true;
        break;
    } 
    if (!bool)
      parse(paramJsonObjectReader); 
  }
  
  private boolean handleArrayOrMapEnd() {
    if (hasOneToken())
      return true; 
    Token token = getCurrentToken();
    popCurrentToken();
    if (getCurrentToken() instanceof TokenName) {
      TokenName tokenName = (TokenName)getCurrentToken();
      popCurrentToken();
      TokenMap tokenMap = (TokenMap)getCurrentToken();
      if (tokenName != null && token != null && tokenMap != null)
        tokenMap.value.put(tokenName.value, token.getValue()); 
    } else if (getCurrentToken() instanceof TokenArray) {
      TokenArray tokenArray = (TokenArray)getCurrentToken();
      if (token != null && tokenArray != null)
        tokenArray.value.add(token.getValue()); 
    } 
    return false;
  }
  
  private boolean handlePrimitive(NextValue paramNextValue) throws IOException {
    Object object = paramNextValue.nextValue();
    if (getCurrentToken() == null && object != null) {
      pushCurrentToken(new TokenPrimitive(object));
      return true;
    } 
    if (getCurrentToken() instanceof TokenName) {
      TokenName tokenName = (TokenName)getCurrentToken();
      popCurrentToken();
      TokenMap tokenMap = (TokenMap)getCurrentToken();
      tokenMap.value.put(tokenName.value, object);
    } else if (getCurrentToken() instanceof TokenArray) {
      TokenArray tokenArray = (TokenArray)getCurrentToken();
      tokenArray.value.add(object);
    } 
    return false;
  }
  
  private Object nextNumber(JsonObjectReader paramJsonObjectReader) throws IOException {
    try {
      return Integer.valueOf(paramJsonObjectReader.nextInt());
    } catch (Exception exception) {
      try {
        return Double.valueOf(paramJsonObjectReader.nextDouble());
      } catch (Exception exception1) {
        return Long.valueOf(paramJsonObjectReader.nextLong());
      } 
    } 
  }
  
  @Nullable
  private Token getCurrentToken() {
    return this.tokens.isEmpty() ? null : this.tokens.get(this.tokens.size() - 1);
  }
  
  private void pushCurrentToken(Token paramToken) {
    this.tokens.add(paramToken);
  }
  
  private void popCurrentToken() {
    if (this.tokens.isEmpty())
      return; 
    this.tokens.remove(this.tokens.size() - 1);
  }
  
  private boolean hasOneToken() {
    return (this.tokens.size() == 1);
  }
  
  private static interface Token {
    @NotNull
    Object getValue();
    
    static {
    
    }
  }
  
  private static final class TokenArray implements Token {
    final ArrayList<Object> value = new ArrayList();
    
    private TokenArray() {}
    
    @NotNull
    public Object getValue() {
      return this.value;
    }
  }
  
  private static final class TokenMap implements Token {
    final HashMap<String, Object> value = new HashMap<>();
    
    private TokenMap() {}
    
    @NotNull
    public Object getValue() {
      return this.value;
    }
  }
  
  private static final class TokenName implements Token {
    final String value;
    
    TokenName(@NotNull String param1String) {
      this.value = param1String;
    }
    
    @NotNull
    public Object getValue() {
      return this.value;
    }
  }
  
  private static interface NextValue {
    @Nullable
    Object nextValue() throws IOException;
    
    static {
    
    }
  }
  
  private static final class TokenPrimitive implements Token {
    final Object value;
    
    TokenPrimitive(@NotNull Object param1Object) {
      this.value = param1Object;
    }
    
    @NotNull
    public Object getValue() {
      return this.value;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\JsonObjectDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */