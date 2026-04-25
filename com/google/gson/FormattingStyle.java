package com.google.gson;

import java.util.Objects;

public class FormattingStyle {
  private final String newline;
  
  private final String indent;
  
  private final boolean spaceAfterSeparators;
  
  public static final FormattingStyle COMPACT = new FormattingStyle("", "", false);
  
  public static final FormattingStyle PRETTY = new FormattingStyle("\n", "  ", true);
  
  private FormattingStyle(String paramString1, String paramString2, boolean paramBoolean) {
    Objects.requireNonNull(paramString1, "newline == null");
    Objects.requireNonNull(paramString2, "indent == null");
    if (!paramString1.matches("[\r\n]*"))
      throw new IllegalArgumentException("Only combinations of \\n and \\r are allowed in newline."); 
    if (!paramString2.matches("[ \t]*"))
      throw new IllegalArgumentException("Only combinations of spaces and tabs are allowed in indent."); 
    this.newline = paramString1;
    this.indent = paramString2;
    this.spaceAfterSeparators = paramBoolean;
  }
  
  public FormattingStyle withNewline(String paramString) {
    return new FormattingStyle(paramString, this.indent, this.spaceAfterSeparators);
  }
  
  public FormattingStyle withIndent(String paramString) {
    return new FormattingStyle(this.newline, paramString, this.spaceAfterSeparators);
  }
  
  public FormattingStyle withSpaceAfterSeparators(boolean paramBoolean) {
    return new FormattingStyle(this.newline, this.indent, paramBoolean);
  }
  
  public String getNewline() {
    return this.newline;
  }
  
  public String getIndent() {
    return this.indent;
  }
  
  public boolean usesSpaceAfterSeparators() {
    return this.spaceAfterSeparators;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\FormattingStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */