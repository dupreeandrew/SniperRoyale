package com.torchnight.sniperroyale.models;

import com.torchnight.sniperroyale.Main;
import com.torchnight.sniperroyale.listeners.GameEndListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;

public class GameSession implements Listener {

    private Config config = Config.INSTANCE;
    private HashMap<String, Integer> playerScores = new HashMap<>();
    private HashMap<Player, Team> teamSaver = new HashMap<>();
    private List<GameEndListener> gameEndListeners = new ArrayList<>();
    private final int SECONDS_OF_GAME = 600;
    private int ticksOfGameRemaining = SECONDS_OF_GAME * 20;
    private final String OBJECTIVE_NAME = "sr-scoreboard";
    private final String POINTS_TEXT = ChatColor.GREEN + "Points:";

    static GameSession startGame(HashSet<String> playerSet) {
        return new GameSession(playerSet);
    }

    private GameSession(HashSet<String> playerNameSet) {


        for (String playerName : playerNameSet) {
            playerScores.put(playerName, 0);
            Player p = Bukkit.getPlayer(playerName);
            p.teleport(config.getSpawnPoint());
            p.setWalkSpeed(.3f);
            PlayerLookup.INSTANCE.addPlayerToGameLookup(p);

            InventorySaver.INSTANCE.saveInventory(p);
            p.getInventory().clear();
            StarterKit.give(p);

            addScoreBoard(p);

        }

        initTimer();
        Main.registerListener(this);

    }


    private void addScoreBoard(Player p) {
        Scoreboard scoreBoard = Bukkit.getScoreboardManager().getNewScoreboard();
        final String DISPLAY_NAME = ChatColor.BLUE + "Sniper Royale";
        Objective obj = scoreBoard.registerNewObjective(OBJECTIVE_NAME, "no-criteria", DISPLAY_NAME);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        p.setScoreboard(scoreBoard);

        Team team = scoreBoard.registerNewTeam(p.getName() + "t");
        team.addEntry(p.getName());
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);

    }

    private void initTimer() {
        Main.scheduleRepeatingTask(() -> {
            ticksOfGameRemaining -= 20;
            updateScoreboardTimer();
            if (ticksOfGameRemaining <= 0) {
                endGame();
            }
        }, 20L, ticksOfGameRemaining); // at the time, ticksOfGameRemaining never subtracted.
    }

    private void updateScoreboardTimer() {
        for (String playerName : playerScores.keySet()) {
            int secondsRemaining = ticksOfGameRemaining  / 20;
            Player player = Bukkit.getPlayerExact(playerName);
            player.getScoreboard().getObjective(OBJECTIVE_NAME).getScore(ChatColor.GOLD + "Time Left:")
                    .setScore(secondsRemaining);
        }
    }

    private boolean playerIsInMap(Player p) {
        String playerNameAsString = p.getName();
        return playerScores.containsKey(playerNameAsString);
    }

    @EventHandler
    public void onMagicalDeath(PlayerDeathEvent event) {

        Player playerThatDied = event.getEntity();
        if (!playerIsInMap(playerThatDied)) {
            return;
        }

        event.setKeepInventory(true);
        playerThatDied.spigot().respawn();

        /*
        Player killer = playerThatDied.getKiller();
        updateScore(playerThatDied, killer);
        sendEventMessages(playerThatDied, killer);
        playKillEventSound(killer);
        */

        Main.scheduleTask(() -> {
            playerThatDied.teleport(config.getRespawnPoint());
            playerThatDied.setHealth(20);
        }, 1L);

    }

    @EventHandler
    public void onPlayerDeath(EntityDamageByEntityEvent event) {
        if (setupIsValid(event)) {
            event.setCancelled(true);

            Player playerThatDied = (Player)event.getEntity();
            Player killer = getGameKiller(event);

            if (killer != null) {
                updateScore(playerThatDied, killer);
                sendEventMessages(playerThatDied, killer);
                playKillEventSound(killer);
            }

            playerThatDied.teleport(config.getRespawnPoint());
            playerThatDied.setHealth(20);
            playKillEventSound(killer);
        }
    }

    private boolean setupIsValid(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return false;
        }

        Player personWhoDied = (Player)event.getEntity();

        if (!PlayerLookup.INSTANCE.isPlayerIngame(personWhoDied)) {
            return false;
        }

        if (event.getDamage() < personWhoDied.getHealth()) {
            return false;
        }

        if (personWhoDied.getLocation().getBlockY() > 168) {
            event.setCancelled(true);
            return false;
        }

        return true;

    }

    private Player getGameKiller(EntityDamageByEntityEvent event) {
        Player killer = null;
        if (event.getDamager() instanceof Arrow) {
            Arrow arrowDamager = (Arrow)event.getDamager();
            killer = (Player)arrowDamager.getShooter();
            arrowDamager.remove(); // unclean but fits.
        }
        if (event.getDamager() instanceof Player) {
            killer = (Player)event.getDamager();
        }
        return killer;
    }

    private void sendEventMessages(Player playerThatDied, Player killer) {

        final String DEATH_MESSAGE = "&cOh no! &eYou were killed by &c" + killer.getName() + "&e. &c-1 point";
        final String KILL_MESSAGE = "&2Bullseye! &aYou killed &e" + playerThatDied.getName() + "&a. &2+2 points!";

        playerThatDied.sendMessage(ChatColor.translateAlternateColorCodes('&', DEATH_MESSAGE));
        killer.sendMessage(ChatColor.translateAlternateColorCodes('&', KILL_MESSAGE));
        sendStatsMessage(playerThatDied);
        sendStatsMessage(killer);

    }

    private void playKillEventSound(Player p) {
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
    }

    private void sendStatsMessage(Player p) {
        Integer score = playerScores.get(p.getName());
        p.sendMessage("Current Score: " + score);
    }

    @EventHandler
    public void onPlayerShop(PlayerCommandPreprocessEvent event) {

        if (!event.getMessage().equals("/sniperroyale-buy-firework")) {
            return;
        }

        Player player = event.getPlayer();
        if (!playerIsInMap(player)) {
            player.sendMessage(ChatColor.RED + "You must be in a SniperRoyale game to do this.");
            return;
        }

        if (playerScores.get(player.getName()) < 1) {
            player.sendMessage(ChatColor.RED + "Insufficient Funds.");
            return;
        }


        event.setCancelled(true);
        Integer newScore = playerScores.get(player.getName()) - 1;
        playerScores.put(player.getName(), newScore);
        updateScoreOnScoreBoard(player, newScore);

        player.getInventory().addItem(new ItemStack(Material.FIREWORK_ROCKET, 1));
        player.sendMessage(ChatColor.GREEN + "You bought 1 Firework Rocket in exchange for 1 point");

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (playerIsInMap(player)) {
            playerScores.remove(player.getName());
            removePlayerFromGame(player);
        }
    }

    private void updateScore(Player playerThatDied, Player killer) {

        if (killer == null) {
            return;
        }

        String playerThatDiedName = playerThatDied.getName();
        String killerName = killer.getName();
        Integer playerThatDiedNewScore = playerScores.get(playerThatDiedName) - 1;
        Integer killerNewScore = playerScores.get(killerName) + 2;
        updateScoreOnScoreBoard(playerThatDied, playerThatDiedNewScore);
        updateScoreOnScoreBoard(killer, killerNewScore);
        killer.getScoreboard().getObjective(OBJECTIVE_NAME).getScore(POINTS_TEXT).setScore(killerNewScore);
        playerScores.put(playerThatDiedName, playerThatDiedNewScore);
        playerScores.put(killerName, killerNewScore);
    }

    private void updateScoreOnScoreBoard(Player p, int newScore) {
        p
                .getScoreboard()
                .getObjective(OBJECTIVE_NAME)
                .getScore(POINTS_TEXT)
                .setScore(newScore);
    }

    private void endGame() {
        announceTopThree();
        notifyListeners();
        HandlerList.unregisterAll(this);
        iterateThroughPlayers();
    }

    private void iterateThroughPlayers() {
        for (String playerName : playerScores.keySet()) {
            Player p = Bukkit.getPlayerExact(playerName);
            removePlayerFromGame(p);
        }
    }

    /**
     * Essentially eliminates a player from the game, and they can act like a normal player.
     */
    private void removePlayerFromGame(Player p) {
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        p.teleport(config.getEndGamePoint());
        PlayerLookup.INSTANCE.removePlayerFromGameLookup(p);
        messagePlayerTheirScore(p);
        p.setWalkSpeed(0.2f);
        p.setHealth(20);
        p.getInventory().clear();
        InventorySaver.INSTANCE.recoverInventory(p);
    }

    private void messagePlayerTheirScore(Player p) {
        p.sendMessage("Game ended. Your score was: " + playerScores.get(p.getName()));
    }

    private void announceTopThree() {
        Main.broadcastMessage("&6&l- Sniper Royale -");
        Main.broadcastMessage("&eGame has ended!");
        Main.broadcastMessage("&eTop 3 Players were:");

        int iteration = 1;
        List<Entry<String, Integer>> topThreePlayers = getTopThreeAsEntryList();
        for (Entry<String, Integer> playerEntry : topThreePlayers) {
            String playerName = playerEntry.getKey();
            Integer playerScore = playerEntry.getValue();
            Main.broadcastMessage("&b" + iteration + ") " + playerName + " - &e" + playerScore + " points");
            iteration++;
        }
    }

    private List<Entry<String, Integer>> getTopThreeAsEntryList() {
        return playerScores.entrySet().stream()
                .sorted(comparing(Entry::getValue, reverseOrder()))
                .limit(3)
                .collect(toList());
    }

    private void notifyListeners() {
        for (GameEndListener listener : gameEndListeners) {
            listener.onGameEnd();
        }
    }

    void addGameEndListener(GameEndListener gameEndListener) {
        gameEndListeners.add(gameEndListener);
    }

}
