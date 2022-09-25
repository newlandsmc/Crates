package com.semivanilla.crates.commands;

import com.semivanilla.crates.Crates;
import com.semivanilla.crates.manager.ConfigManager;
import com.semivanilla.crates.manager.MessageManager;
import com.semivanilla.crates.manager.PlayerManager;
import com.semivanilla.crates.menu.CratesMenu;
import com.semivanilla.crates.object.CrateType;
import com.semivanilla.crates.object.PlayerData;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import meteordevelopment.starscript.value.ValueMap;
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
import java.util.List;

public class CratesCommand extends BaseCommand {
    @Command(name = "crates", aliases = {"spoils", "expeditions"}, playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        try {
            if (Crates.isDisabled()) {
                sender.sendMessage(CC.RED + "Crates is temporarily disabled!");
                return CommandResult.SUCCESS;
            }
            PlayerData data = PlayerManager.getData(sender.getPlayer().getUniqueId());
            if (data == null) {
                sender.sendMessage(CC.RED + "An error occurred! Please open a bug report ticket in the discord, and send a screenshot of this! " + CC.GRAY + "(" + System.currentTimeMillis() + ")" + CC.GOLD + " (1)");
                Logger.severe("(1) Data was null for %1 (%2) | %3", sender.getPlayer().getName(), sender.getPlayer().getUniqueId(), System.currentTimeMillis());
                return CommandResult.SUCCESS;
            }
            new CratesMenu(data).open(sender);
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(CC.RED + "An error occurred! Please open a bug report ticket in the discord, and send a screenshot of this! " + CC.GRAY + "(" + System.currentTimeMillis() + ")" + CC.GOLD + " (5)");
        }
        return CommandResult.SUCCESS;
    }

    @Command(name = "cratesadmin", permission = "crates.admin")
    public CommandResult executeAdmin(Sender sender, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("testvote")) {
                if (args.length > 1) {
                    if (args.length == 3) {
                        OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
                        String service = args[2];
                        Bukkit.getServer().getPluginManager().callEvent(new VotifierEvent(new Vote(service, op.getName(), "",LocalDate.now().toString())));
                        sender.sendMessage("Done!");
                        return CommandResult.SUCCESS;
                    }
                }else {
                    //PlayerManager.getData(sender.getPlayer().getUniqueId()).onVote();
                    PlayerManager.getVoteQueue().add(sender.getPlayer().getUniqueId());
                    sender.sendMessage(CC.GREEN + "Done!");
                    return CommandResult.SUCCESS;
                }
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
                        data.getCrateTypes().add(CrateType.PREMIUM);
                    }
                    if (offlinePlayer.isOnline()) {
                        data.save();
                        ValueMap valueMap = new ValueMap();
                        valueMap.set("player", offlinePlayer.getName());
                        valueMap.set("count", amount);
                        valueMap.set("type", "Premium");
                        List<Component> components = MessageManager.parse(ConfigManager.getCrateGainedMessage(), valueMap);
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
                    sender.sendMessage(CC.RED + "Usage: /cratessadmin givepremium <player> [amount]");
                    return CommandResult.SUCCESS;
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                long start = System.currentTimeMillis();
                Crates.getInstance().reloadConfig();
                long end = System.currentTimeMillis();
                sender.sendMessage(CC.GREEN + "Reloaded config in " + (end - start) + "ms");
                return CommandResult.SUCCESS;
            }else if (args[0].equalsIgnoreCase("disable")) {
                Crates.setDisabled(!Crates.isDisabled());
                sender.sendMessage(CC.GREEN + "Expeditions are now " + (Crates.isDisabled() ? "disabled" : "enabled"));
                return CommandResult.SUCCESS;
            }
        }
        sender.sendMessage(CC.GREEN + "Expeditions V." + Crates.getInstance().getDescription().getVersion());
        sender.sendMessage(CC.GREEN + "Commands:");
        sender.sendMessage(CC.AQUA + "/expeditionsadmin givepremium <player> [amount]");
        sender.sendMessage(CC.AQUA + "/expeditionsadmin reload");
        sender.sendMessage(CC.AQUA + "/expeditionsadmin testvote <player> <ServiceName> - command for testing, don't use");
        sender.sendMessage(CC.AQUA + "/expeditionsadmin disable - Disable/enable expeditions");
        return CommandResult.SUCCESS;
    }
}
