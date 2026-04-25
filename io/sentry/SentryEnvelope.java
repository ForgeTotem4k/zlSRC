package io.sentry;

import io.sentry.exception.SentryEnvelopeException;
import io.sentry.protocol.SdkVersion;
import io.sentry.protocol.SentryId;
import io.sentry.util.Objects;
import java.io.IOException;
import java.util.ArrayList;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class SentryEnvelope {
  @NotNull
  private final SentryEnvelopeHeader header;
  
  @NotNull
  private final Iterable<SentryEnvelopeItem> items;
  
  @NotNull
  public Iterable<SentryEnvelopeItem> getItems() {
    return this.items;
  }
  
  @NotNull
  public SentryEnvelopeHeader getHeader() {
    return this.header;
  }
  
  public SentryEnvelope(@NotNull SentryEnvelopeHeader paramSentryEnvelopeHeader, @NotNull Iterable<SentryEnvelopeItem> paramIterable) {
    this.header = (SentryEnvelopeHeader)Objects.requireNonNull(paramSentryEnvelopeHeader, "SentryEnvelopeHeader is required.");
    this.items = (Iterable<SentryEnvelopeItem>)Objects.requireNonNull(paramIterable, "SentryEnvelope items are required.");
  }
  
  public SentryEnvelope(@Nullable SentryId paramSentryId, @Nullable SdkVersion paramSdkVersion, @NotNull Iterable<SentryEnvelopeItem> paramIterable) {
    this.header = new SentryEnvelopeHeader(paramSentryId, paramSdkVersion);
    this.items = (Iterable<SentryEnvelopeItem>)Objects.requireNonNull(paramIterable, "SentryEnvelope items are required.");
  }
  
  public SentryEnvelope(@Nullable SentryId paramSentryId, @Nullable SdkVersion paramSdkVersion, @NotNull SentryEnvelopeItem paramSentryEnvelopeItem) {
    Objects.requireNonNull(paramSentryEnvelopeItem, "SentryEnvelopeItem is required.");
    this.header = new SentryEnvelopeHeader(paramSentryId, paramSdkVersion);
    ArrayList<SentryEnvelopeItem> arrayList = new ArrayList(1);
    arrayList.add(paramSentryEnvelopeItem);
    this.items = arrayList;
  }
  
  @NotNull
  public static SentryEnvelope from(@NotNull ISerializer paramISerializer, @NotNull Session paramSession, @Nullable SdkVersion paramSdkVersion) throws IOException {
    Objects.requireNonNull(paramISerializer, "Serializer is required.");
    Objects.requireNonNull(paramSession, "session is required.");
    return new SentryEnvelope(null, paramSdkVersion, SentryEnvelopeItem.fromSession(paramISerializer, paramSession));
  }
  
  @NotNull
  public static SentryEnvelope from(@NotNull ISerializer paramISerializer, @NotNull SentryBaseEvent paramSentryBaseEvent, @Nullable SdkVersion paramSdkVersion) throws IOException {
    Objects.requireNonNull(paramISerializer, "Serializer is required.");
    Objects.requireNonNull(paramSentryBaseEvent, "item is required.");
    return new SentryEnvelope(paramSentryBaseEvent.getEventId(), paramSdkVersion, SentryEnvelopeItem.fromEvent(paramISerializer, paramSentryBaseEvent));
  }
  
  @NotNull
  public static SentryEnvelope from(@NotNull ISerializer paramISerializer, @NotNull ProfilingTraceData paramProfilingTraceData, long paramLong, @Nullable SdkVersion paramSdkVersion) throws SentryEnvelopeException {
    Objects.requireNonNull(paramISerializer, "Serializer is required.");
    Objects.requireNonNull(paramProfilingTraceData, "Profiling trace data is required.");
    return new SentryEnvelope(new SentryId(paramProfilingTraceData.getProfileId()), paramSdkVersion, SentryEnvelopeItem.fromProfilingTrace(paramProfilingTraceData, paramLong, paramISerializer));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryEnvelope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */