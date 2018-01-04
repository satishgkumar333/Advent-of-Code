package nl.nibsi.aoc.y17.d9;

import java.io.*;
import java.util.*;

final class StreamProcessor {

  private final Reader reader;

  private final Deque<Scope> scopeStack = new ArrayDeque<>();

  private boolean isRootScopeClosed;

  private int garbageRemovedCount;

  StreamProcessor(Reader reader) {
    if (reader == null)
      throw new IllegalArgumentException();

    this.reader = reader;
  }

  void processNextChar() throws IOException {
    int c = reader.read();
    switch (c) {
      case -1  : assertDone();  break;
      case '!' : reader.read(); break;
      case '<' : dropGarbage(); break;
      case '{' : openScope();   break;
      case '}' : closeScope();  break;

      default  : currentScope().appendChar((char) c);
    }
  }

  private boolean isRootScopeOpened() {
    return !scopeStack.isEmpty();
  }

  boolean isRootScopeClosed() {
    return isRootScopeClosed;
  }

  private void assertDone() throws EOFException {
    if (!isRootScopeClosed)
      throw new EOFException();
  }

  Scope currentScope() {
    if (!isRootScopeOpened())
      throw new IllegalStateException();

    return scopeStack.peek();
  }

  int garbageRemovedCount() {
    return garbageRemovedCount;
  }

  private void openScope() {
    if (isRootScopeClosed)
      throw new IllegalStateException();

    Scope parent = isRootScopeOpened() ? currentScope() : null;
    Scope scope = new Scope(parent);

    if (parent != null)
      parent.addSubscope(scope);

    scopeStack.push(scope);
  }

  private void closeScope() {
    if (isRootScopeClosed)
      throw new IllegalStateException();

    if (currentScope().isRoot()) {
      isRootScopeClosed = true;
      return;
    }

    scopeStack.pop();
  }

  private void dropGarbage() throws IOException {
    int c = reader.read();
    while (c != '>' && c != -1) {
      if (c == '!')
        reader.read();
      else
        garbageRemovedCount++;

      c = reader.read();
    }
  }
}