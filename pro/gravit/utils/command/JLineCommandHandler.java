package pro.gravit.utils.command;

import java.io.IOException;
import java.util.List;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

public class JLineCommandHandler extends CommandHandler {
  private final Terminal terminal;
  
  private final LineReader reader;
  
  public JLineCommandHandler() throws IOException {
    TerminalBuilder terminalBuilder = TerminalBuilder.builder();
    this.terminal = terminalBuilder.build();
    JLineConsoleCompleter jLineConsoleCompleter = new JLineConsoleCompleter();
    this.reader = LineReaderBuilder.builder().terminal(this.terminal).completer(jLineConsoleCompleter).build();
  }
  
  public void bell() {
    this.terminal.puts(InfoCmp.Capability.bell, new Object[0]);
  }
  
  public void clear() {
    this.terminal.puts(InfoCmp.Capability.clear_screen, new Object[0]);
  }
  
  public String readLine() {
    try {
      return this.reader.readLine();
    } catch (UserInterruptException userInterruptException) {
      System.exit(0);
      return null;
    } 
  }
  
  public class JLineConsoleCompleter implements Completer {
    public void complete(LineReader param1LineReader, ParsedLine param1ParsedLine, List<Candidate> param1List) {
      String str = param1ParsedLine.word();
      if (param1ParsedLine.wordIndex() == 0) {
        JLineCommandHandler.this.walk((param1Category, param1String2, param1Command) -> {
              if (param1String2.startsWith(param1String1))
                param1List.add(param1Command.buildCandidate(param1Category, param1String2)); 
            });
      } else {
        Command command = JLineCommandHandler.this.findCommand(param1ParsedLine.words().get(0));
        if (command == null)
          return; 
        List<String> list = param1ParsedLine.words();
        List<Candidate> list1 = command.complete(list.subList(1, list.size()), param1ParsedLine.wordIndex() - 1, str);
        param1List.addAll(list1);
      } 
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\command\JLineCommandHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */