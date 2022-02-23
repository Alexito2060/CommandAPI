package dev.alex.net.utilities.command.command;

import com.google.common.collect.Lists;
import dev.alex.net.utilities.chat.Chat;
import dev.alex.net.utilities.command.completer.CommandCompleter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class CommandExecutor implements org.bukkit.command.CommandExecutor, TabCompleter {

    @Getter private final String name;
    @Getter private final String description;
    @Getter private final String permission;
    @Getter private final String[] aliases;
    @Getter private final List<CommandArgument> arguments = Lists.newArrayList();

    public CommandExecutor(String name, String description, String permission) { this(name, description, permission, new String[0]); }
    public CommandExecutor(String name, String description, String[] aliases) { this(name, description, null, aliases); }
    public CommandExecutor(String name, String description) { this(name, description, null, new String[0]); }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Chat.style("&f&m--------------------------------"));
            sender.sendMessage("");
            sender.sendMessage(Chat.style("&f&l" + label + " &7Help&f: "));
            sender.sendMessage("");
            this.arguments.forEach(argument -> sender.sendMessage(Chat.style("&f- &7/&f"+ label + " " + argument.getUsage(label) + " &7-&e " + argument.getDescription())));
            sender.sendMessage("");
            sender.sendMessage(Chat.style("&f&m--------------------------------"));
            return false;
        }
        CommandArgument argument = this.getArgument(args[0]);
        if (argument == null) {
            sender.sendMessage(Chat.style("&f" + label +  " &ccommand argument &f" + args[0] + " &cnot found"));
            return false;
        }
        if (argument.getPermission() != null && !sender.hasPermission(argument.getPermission())) {
            sender.sendMessage(Chat.style("&cYou don't have permissions to execute this command"));
            return false;
        }
        argument.onCommand(sender, label, fixArgs(args));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = Lists.newArrayList();
        if (args.length < 2) {
            for (CommandArgument argument : this.arguments) {
                if (argument.getPermission() != null && sender.hasPermission(argument.getPermission())) { completions.add(argument.getName()); }
            }
        } else {
            CommandArgument argument = this.getArgument(args[0]);
            if (argument == null) { return completions; }
            if (argument.getPermission() != null && sender.hasPermission(argument.getPermission())) { completions = argument.onTabComplete(sender, label, fixArgs(args)); }
        }
        return new CommandCompleter().getCompletions(args, completions);
    }

    private String[] fixArgs(String[] args) {
        String[] fixed = new String[args.length - 1];
        System.arraycopy(args, 1, fixed, 0, args.length - 1);
        return fixed;
    }

    public void registerArgument(CommandArgument argument) { this.arguments.add(argument); }

    public void unregisterArgument(CommandArgument argument) { this.arguments.remove(argument); }

    public CommandArgument getArgument(String name) { return this.arguments.stream().filter(argument -> argument.getName().equals(name) || Arrays.asList(argument.getAliases()).contains(name)).findFirst().orElse(null); }
}
