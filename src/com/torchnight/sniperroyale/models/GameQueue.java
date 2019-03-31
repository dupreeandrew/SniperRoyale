package com.torchnight.sniperroyale.models;

import com.torchnight.sniperroyale.Main;
import com.torchnight.sniperroyale.listeners.GameEndListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * GameQueue is responsible for managing the queue.
 * Nothing more, nothing less.
 *
 * See {@link}args packages for anything else.
 */
public enum GameQueue implements GameEndListener, Listener {
    INSTANCE;

    private HashSet<String> queueSet = new HashSet<>();
    private Config config = Config.INSTANCE;
    private boolean gameIsStarted = false;
    private boolean countdownIsActive = false;

    private boolean playerIsInQueue(Player p) {
        return queueSet.contains(p.getName());
    }

    public boolean addPlayer(Player p) {
        String playerName = p.getName();

        if (playerIsInQueue(p)) {
            return false;
        }

        queueSet.add(playerName);

        if (queueSet.size() >= config.getAutoStartPlayersNum() && !countdownIsActive) {
            final String COUNTDOWN_MESSAGE = ChatColor.BLUE + "Five players entered the queue. Game will " +
                    "autostart in 10 seconds";
            notifyPlayersWithMessage(COUNTDOWN_MESSAGE);

            Main.scheduleTask(() -> {
                countdownIsActive = false;
                startGame();
            }, 200L);


            countdownIsActive = true;

        }

        return true;
    }

    private void notifyPlayersWithMessage(String message) {

        for (String playerName : queueSet) {
            Bukkit.getPlayerExact(playerName)
                    .sendMessage(message);
        }
    }

    public void removePlayerIfInQueue(Player p) {
        String playerName = p.getName();

        if (playerIsInQueue(p)) {
            queueSet.remove(playerName);
        }

    }

    public boolean startGame() {

        if (gameIsStarted) {
            return false;
        }

        if (queueSet.size() < 2) {
            notifyPlayersWithMessage(ChatColor.GREEN + "Not enough players to start the game.");
            return false;
        }

        GameSession gameSession = GameSession.startGame(queueSet);
        gameSession.addGameEndListener(this);
        queueSet.clear();
        gameIsStarted = true;
        return true;

    }

    public List<String> getPlayerNames() {
        List<String> playerNames = new ArrayList<>();
        playerNames.addAll(queueSet);
        return playerNames;
    }

    @Override
    public void onGameEnd() {
        gameIsStarted = false;
    }
}
