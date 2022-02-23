package dev.alex.net.utilities.command.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public abstract class CommandArgument {

    @Getter private final String name;
    @Getter private final String description;
    @Getter private final String permission;
    @Getter private final String[] aliases;

    public CommandArgument(String name, String description) { this(name, description, null, new String[0]); }

    public CommandArgument(String name, String description, String permission) { this(name, description, permission, new String[0]); }

    public abstract String getUsage(String label);

    public abstract boolean onCommand(CommandSender sender, String label, String[] args);

    public List<String> onTabComplete(CommandSender sender, String label, String[] args) { return Collections.emptyList(); }

}
