package io.sentry;

import io.sentry.util.Objects;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;
import org.jetbrains.annotations.NotNull;

final class Stack {
  @NotNull
  private final Deque<StackItem> items = new LinkedBlockingDeque<>();
  
  @NotNull
  private final ILogger logger;
  
  public Stack(@NotNull ILogger paramILogger, @NotNull StackItem paramStackItem) {
    this.logger = (ILogger)Objects.requireNonNull(paramILogger, "logger is required");
    this.items.push((StackItem)Objects.requireNonNull(paramStackItem, "rootStackItem is required"));
  }
  
  public Stack(@NotNull Stack paramStack) {
    this(paramStack.logger, new StackItem(paramStack.items.getLast()));
    Iterator<StackItem> iterator = paramStack.items.descendingIterator();
    if (iterator.hasNext())
      iterator.next(); 
    while (iterator.hasNext())
      push(new StackItem(iterator.next())); 
  }
  
  @NotNull
  StackItem peek() {
    return this.items.peek();
  }
  
  void pop() {
    synchronized (this.items) {
      if (this.items.size() != 1) {
        this.items.pop();
      } else {
        this.logger.log(SentryLevel.WARNING, "Attempt to pop the root scope.", new Object[0]);
      } 
    } 
  }
  
  void push(@NotNull StackItem paramStackItem) {
    this.items.push(paramStackItem);
  }
  
  int size() {
    return this.items.size();
  }
  
  static final class StackItem {
    private final SentryOptions options;
    
    @NotNull
    private volatile ISentryClient client;
    
    @NotNull
    private volatile IScope scope;
    
    StackItem(@NotNull SentryOptions param1SentryOptions, @NotNull ISentryClient param1ISentryClient, @NotNull IScope param1IScope) {
      this.client = (ISentryClient)Objects.requireNonNull(param1ISentryClient, "ISentryClient is required.");
      this.scope = (IScope)Objects.requireNonNull(param1IScope, "Scope is required.");
      this.options = (SentryOptions)Objects.requireNonNull(param1SentryOptions, "Options is required");
    }
    
    StackItem(@NotNull StackItem param1StackItem) {
      this.options = param1StackItem.options;
      this.client = param1StackItem.client;
      this.scope = param1StackItem.scope.clone();
    }
    
    @NotNull
    public ISentryClient getClient() {
      return this.client;
    }
    
    public void setClient(@NotNull ISentryClient param1ISentryClient) {
      this.client = param1ISentryClient;
    }
    
    @NotNull
    public IScope getScope() {
      return this.scope;
    }
    
    @NotNull
    public SentryOptions getOptions() {
      return this.options;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\Stack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */