package dev.alex.net.utilities.command;

import com.google.common.collect.Lists;
import dev.alex.net.utilities.chat.Chat;
import dev.alex.net.utilities.command.command.CommandExecutor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public final class Command {

    private final List<CommandExecutor> commands = Lists.newArrayList();
    private final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(Command.class);
    @Getter private final String prefix;
    @Getter private final Boolean logger;

    @SneakyThrows
    public void registerAll() {
        for (CommandExecutor executor : this.commands) {
            PluginCommand command = this.getCommand(executor.getName());
            command.setPermissionMessage(Chat.style("&cYou don't have permissions to execute this command"));
            if (executor.getPermission() != null) { command.setPermission(executor.getPermission()); }
            if (executor.getDescription() != null) { command.setDescription(executor.getDescription()); }
            if (executor.getAliases().length > 0) { command.setAliases(Arrays.asList(executor.getAliases())); }
            command.setExecutor(executor);
            command.setTabCompleter(executor);
            if (!this.getCommandMap().register(this.plugin.getName(), command)) return;
            command.unregister(this.getCommandMap());
            this.getCommandMap().register(this.plugin.getName(), command);
        }
        if (this.logger) this.plugin.getLogger().info(Chat.style(this.prefix + " &f" + this.commands.size() + " &3have been successfully registered"));
    }

    @SneakyThrows
    public void unregisterAll() {
        for (CommandExecutor executor : this.commands) {
            PluginCommand command = this.getCommand(executor.getName());
            if (!this.getCommandMap().register(this.plugin.getName(), command)) return;
            command.unregister(this.getCommandMap());
        }
        if (this.logger) this.plugin.getLogger().info(Chat.style(this.prefix + " &f" + this.commands.size() + " &3have been successfully unregistered"));
    }

    private CommandMap getCommandMap() throws Exception {
        Field field = this.plugin.getServer().getClass().getDeclaredField("commandMap");
        field.setAccessible(true);
        return (CommandMap) field.get(Bukkit.getServer());
    }

    private PluginCommand getCommand(String name) throws Exception {
        Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
        constructor.setAccessible(true);
        return constructor.newInstance(name, this.plugin);
    }

    public void register(CommandExecutor command) { this.commands.add(command); }

    public void unregister(CommandExecutor command) { this.commands.remove(command); }

}
