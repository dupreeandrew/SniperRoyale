package com.torchnight.sniperroyale.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface iCommand {
    boolean onCommand(CommandSender sender, Command command, String[] args);
}
