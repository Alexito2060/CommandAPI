package dev.alex.net.utilities.command.completer;

import java.util.List;
import java.util.stream.Collectors;

public final class CommandCompleter {

    public List<String> getCompletions(String[] args, List<String> input) {
        String argument = args[args.length - 1];
        return input.stream().filter(string -> string.regionMatches(true, 0, argument, 0, argument.length())).limit(80).collect(Collectors.toList());
    }
}
