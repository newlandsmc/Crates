package com.semivanilla.oldexpeditions.commands;

import com.semivanilla.oldexpeditions.Expeditions;
import com.semivanilla.oldexpeditions.manager.ConfigManager;
import com.semivanilla.oldexpeditions.manager.MessageManager;
import com.semivanilla.oldexpeditions.manager.PlayerManager;
import com.semivanilla.oldexpeditions.menu.ExpeditionsMenu;
import com.semivanilla.oldexpeditions.object.ExpeditionType;
import com.semivanilla.oldexpeditions.object.PlayerData;
import net.badbird5907.blib.command.BaseCommand;
import net.badbird5907.blib.command.Command;
import net.badbird5907.blib.command.CommandResult;
import net.badbird5907.blib.command.Sender;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpeditionsCommand extends BaseCommand {
    @Command(name = "oldexpeditions", aliases = "spoils", playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        PlayerData data = PlayerManager.getData(sender.getPlayer().getUniqueId());
        new ExpeditionsMenu(data).open(sender);
        return CommandResult.SUCCESS;
    }

    @Command(name = "oldexpeditionsadmin", permission = "expeditions.admin")
    public CommandResult executeAdmin(Sender sender, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("testvote")) {
                if (args.length > 1) {
                    LocalDate now = LocalDate.now();
                    PlayerData data = PlayerManager.getData(sender.getPlayer().getUniqueId());
                    data.addVote(now.minusDays(6));
                    data.addVote(now.minusDays(5));
                    data.addVote(now.minusDays(4));
                    data.addVote(now.minusDays(3));
                    data.addVote(now.minusDays(2));
                    data.addVote(now.minusDays(1));
                    data.addVote(now);
                    data.checkPremium();
                    sender.sendMessage(CC.GREEN + "done!");
                }
                PlayerManager.getData(sender.getPlayer().getUniqueId()).onVote();
                sender.sendMessage(CC.GREEN + "Done!");
                return CommandResult.SUCCESS;
            } else if (args[0].equalsIgnoreCase("givepremium")) {
                if (args.length >= 2) {
                    String target = args[1];
                    int amount = 1;
                    if (args.length >= 3) {
                        amount = Integer.parseInt(args[2]);
                    }
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(target);
                    if (!offlinePlayer.hasPlayedBefore()) {
                        sender.sendMessage(CC.RED + offlinePlayer.getName() + " has never played before!");
                        return CommandResult.SUCCESS;
                    }
                    boolean offline = false;
                    PlayerData data;
                    if (Bukkit.getPlayer(target) != null) {
                        data = PlayerManager.getData(offlinePlayer.getUniqueId());
                    } else {
                        Logger.info("Player %1 is not online. Loading their data...", target);
                        sender.sendMessage("Loading data, please wait...");
                        data = PlayerManager.load(offlinePlayer.getUniqueId());
                        offline = true;
                    }

                    for (int i = 0; i < amount; i++) {
                        data.getExpeditionTypes().add(ExpeditionType.PREMIUM);
                    }
                    if (offlinePlayer.isOnline()) {
                        data.save();
                        Map<String, String> placeholders = new HashMap<>();
                        placeholders.put("%player%", offlinePlayer.getName());
                        placeholders.put("%count%", amount + "");
                        placeholders.put("%type%", "Premium");
                        List<Component> components = MessageManager.parse(ConfigManager.getExpeditionsGainedMessage(), placeholders);
                        Player player = Bukkit.getPlayer(target);
                        assert player != null;
                        for (Component component : components) {
                            player.sendMessage(component);
                        }
                        sender.sendMessage(CC.GREEN + "Done!");
                        return CommandResult.SUCCESS;
                    }
                    if (offline && !offlinePlayer.isOnline()) {
                        data.setOfflineEarned(data.getOfflineEarned() + amount);
                        PlayerManager.unload(offlinePlayer.getUniqueId());
                    }
                    sender.sendMessage(CC.GREEN + "Done!");
                    return CommandResult.SUCCESS;
                } else {
                    sender.sendMessage(CC.RED + "Usage: /expeditionsadmin givepremium <player> [amount]");
                    return CommandResult.SUCCESS;
                }
            } else if (args[0].equalsIgnoreCase("supervotecheck")) {
                if (args.length == 2 && args[1].equalsIgnoreCase("all")) {
                    try {
                        for (String arg : args) {
                            PlayerData data = PlayerManager.getData(Bukkit.getPlayer(arg).getUniqueId());
                            data.checkPremium();
                            sender.sendMessage(CC.GREEN + "Checked " + data.getName());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return CommandResult.ERROR;
                    }
                } else {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        PlayerData data = PlayerManager.getData(onlinePlayer.getUniqueId());
                        data.checkPremium();
                    }
                    sender.sendMessage(CC.GREEN + "Done!");
                }
                return CommandResult.SUCCESS;
            } else if (args[0].equalsIgnoreCase("reload")) {
                long start = System.currentTimeMillis();
                Expeditions.getInstance().reloadConfig();
                long end = System.currentTimeMillis();
                sender.sendMessage(CC.GREEN + "Reloaded config in " + (end - start) + "ms");
                return CommandResult.SUCCESS;
            }
        }
        sender.sendMessage(CC.GREEN + "OLD Expeditions V." + Expeditions.getInstance().getDescription().getVersion());
        sender.sendMessage(CC.GREEN + "Commands:");
        sender.sendMessage(CC.AQUA + "/oldexpeditionsadmin givepremium <player> [amount]");
        sender.sendMessage(CC.AQUA + "/oldexpeditionsadmin reload");
        sender.sendMessage(CC.AQUA + "/oldexpeditionsadmin supervotecheck [player/all] - Force the server to check if players should get a super vote expedition");
        sender.sendMessage(CC.AQUA + "/oldexpeditionsadmin testvote [week=true|false] - command for testing, don't use");
        return CommandResult.SUCCESS;
    }
}
