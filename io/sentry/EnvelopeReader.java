package io.sentry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class EnvelopeReader implements IEnvelopeReader {
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  
  @NotNull
  private final ISerializer serializer;
  
  public EnvelopeReader(@NotNull ISerializer paramISerializer) {
    this.serializer = paramISerializer;
  }
  
  @Nullable
  public SentryEnvelope read(@NotNull InputStream paramInputStream) throws IOException {
    byte[] arrayOfByte = new byte[1024];
    int i = 0;
    int j = -1;
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try {
      int k;
      while ((k = paramInputStream.read(arrayOfByte)) > 0) {
        for (byte b = 0; j == -1 && b < k; b++) {
          if (arrayOfByte[b] == 10) {
            j = i + b;
            break;
          } 
        } 
        byteArrayOutputStream.write(arrayOfByte, 0, k);
        i += k;
      } 
      byte[] arrayOfByte1 = byteArrayOutputStream.toByteArray();
      if (arrayOfByte1.length == 0)
        throw new IllegalArgumentException("Empty stream."); 
      if (j == -1)
        throw new IllegalArgumentException("Envelope contains no header."); 
      SentryEnvelopeHeader sentryEnvelopeHeader = deserializeEnvelopeHeader(arrayOfByte1, 0, j);
      if (sentryEnvelopeHeader == null)
        throw new IllegalArgumentException("Envelope header is null."); 
      int m = j + 1;
      ArrayList<SentryEnvelopeItem> arrayList = new ArrayList();
      while (true) {
        int i1 = -1;
        for (int i2 = m; i2 < arrayOfByte1.length; i2++) {
          if (arrayOfByte1[i2] == 10) {
            i1 = i2;
            break;
          } 
        } 
        if (i1 == -1)
          throw new IllegalArgumentException("Invalid envelope. Item at index '" + arrayList.size() + "'. has no header delimiter."); 
        SentryEnvelopeItemHeader sentryEnvelopeItemHeader = deserializeEnvelopeItemHeader(arrayOfByte1, m, i1 - m);
        if (sentryEnvelopeItemHeader == null || sentryEnvelopeItemHeader.getLength() <= 0)
          throw new IllegalArgumentException("Item header at index '" + arrayList.size() + "' is null or empty."); 
        int n = i1 + sentryEnvelopeItemHeader.getLength() + 1;
        if (n > arrayOfByte1.length)
          throw new IllegalArgumentException("Invalid length for item at index '" + arrayList.size() + "'. Item is '" + n + "' bytes. There are '" + arrayOfByte1.length + "' in the buffer."); 
        byte[] arrayOfByte2 = Arrays.copyOfRange(arrayOfByte1, i1 + 1, n);
        SentryEnvelopeItem sentryEnvelopeItem = new SentryEnvelopeItem(sentryEnvelopeItemHeader, arrayOfByte2);
        arrayList.add(sentryEnvelopeItem);
        if (n == arrayOfByte1.length)
          break; 
        if (n + 1 == arrayOfByte1.length) {
          if (arrayOfByte1[n] == 10)
            break; 
          throw new IllegalArgumentException("Envelope has invalid data following an item.");
        } 
        m = n + 1;
      } 
      SentryEnvelope sentryEnvelope = new SentryEnvelope(sentryEnvelopeHeader, arrayList);
      byteArrayOutputStream.close();
      return sentryEnvelope;
    } catch (Throwable throwable) {
      try {
        byteArrayOutputStream.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  @Nullable
  private SentryEnvelopeHeader deserializeEnvelopeHeader(@NotNull byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    String str = new String(paramArrayOfbyte, paramInt1, paramInt2, UTF_8);
    StringReader stringReader = new StringReader(str);
    try {
      SentryEnvelopeHeader sentryEnvelopeHeader = this.serializer.<SentryEnvelopeHeader>deserialize(stringReader, SentryEnvelopeHeader.class);
      stringReader.close();
      return sentryEnvelopeHeader;
    } catch (Throwable throwable) {
      try {
        stringReader.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  @Nullable
  private SentryEnvelopeItemHeader deserializeEnvelopeItemHeader(@NotNull byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    String str = new String(paramArrayOfbyte, paramInt1, paramInt2, UTF_8);
    StringReader stringReader = new StringReader(str);
    try {
      SentryEnvelopeItemHeader sentryEnvelopeItemHeader = this.serializer.<SentryEnvelopeItemHeader>deserialize(stringReader, SentryEnvelopeItemHeader.class);
      stringReader.close();
      return sentryEnvelopeItemHeader;
    } catch (Throwable throwable) {
      try {
        stringReader.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\EnvelopeReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */