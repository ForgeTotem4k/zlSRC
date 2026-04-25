package pro.gravit.utils.command;

import java.util.List;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

public class JLineConsoleCompleter implements Completer {
  public void complete(LineReader paramLineReader, ParsedLine paramParsedLine, List<Candidate> paramList) {
    String str = paramParsedLine.word();
    if (paramParsedLine.wordIndex() == 0) {
      JLineCommandHandler.this.walk((paramCategory, paramString2, paramCommand) -> {
            if (paramString2.startsWith(paramString1))
              paramList.add(paramCommand.buildCandidate(paramCategory, paramString2)); 
          });
    } else {
      Command command = JLineCommandHandler.this.findCommand(paramParsedLine.words().get(0));
      if (command == null)
        return; 
      List<String> list = paramParsedLine.words();
      List<Candidate> list1 = command.complete(list.subList(1, list.size()), paramParsedLine.wordIndex() - 1, str);
      paramList.addAll(list1);
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\command\JLineCommandHandler$JLineConsoleCompleter.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */