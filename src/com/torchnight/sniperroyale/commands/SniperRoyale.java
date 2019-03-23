package com.torchnight.sniperroyale.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SniperRoyale implements iCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String[] args) {

        if (!isSetupValid(sender, command)) {
            return true;
        }

        Player p = (Player)sender;

        if (args.length == 0) {
            sendHelpMenu(p);
            return true;
        }

        handleArgs(p, command, args);


        return true;
    }

    private boolean isSetupValid(CommandSender sender, Command command) {
        return (sender instanceof Player);
    }

    private void sendHelpMenu(Player p) {
        String[] lines = {
                "&9-- &6Sniper Royale &9--",
                "&e/sniperroyale join &b- Join the game!",
                "&e/sniperroyale leave &b - Leave the queue",
                "&e/sniperroyale queue &b - View list of players joining"
        };

        for (String line : lines) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        }

    }

    public SniperRoyale() {
    }

    private void handleArgs(Player p, Command command, String[] args) {
        String firstArg = args[0].toLowerCase();
        switch (firstArg) {
            case "join":
                Join joinArg = new Join(p);
                joinArg.execute();
                break;
            case "start":
                Start startArg = new Start(p);
                startArg.execute();
                break;
            case "queue":
                Queue queueArg = new Queue(p);
                queueArg.execute();
                break;
            case "ban":
                if (args.length != 2) {
                    return;
                }
                Ban.banPlayer(p, Bukkit.getPlayerExact(args[1]).getUniqueId().toString());
                break;
            case "unban":
                if (args.length != 2) {
                    return;
                }
                Unban.unbanPlayer(p, Bukkit.getPlayerExact(args[1]).getUniqueId().toString());
                break;
        }
    }
}
