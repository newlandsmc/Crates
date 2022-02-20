package com.semivanilla.expeditions.commands;

import com.semivanilla.expeditions.Expeditions;
import com.semivanilla.expeditions.manager.LootManager;
import com.semivanilla.expeditions.manager.PlayerManager;
import com.semivanilla.expeditions.menu.ClaimExpeditionAnimationMenu;
import com.semivanilla.expeditions.menu.ClaimExpeditionMenu;
import com.semivanilla.expeditions.menu.ExpeditionsMenu;
import com.semivanilla.expeditions.object.ExpeditionType;
import com.semivanilla.expeditions.object.MenuUpdateTask;
import com.semivanilla.expeditions.object.PlayerData;
import net.badbird5907.blib.command.BaseCommand;
import net.badbird5907.blib.command.Command;
import net.badbird5907.blib.command.CommandResult;
import net.badbird5907.blib.command.Sender;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ExpeditionsCommand extends BaseCommand {
    @Command(name = "expeditionstest")
    public CommandResult execute(Sender sender, String[] args) {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            stacks.add(new ItemBuilder(Material.DIAMOND).name("test").build());
        }
        ClaimExpeditionAnimationMenu menu = new ClaimExpeditionAnimationMenu(stacks, ExpeditionType.DAILY);
        menu.open(sender);
        new MenuUpdateTask(menu,sender.getPlayer()).runTaskTimer(Expeditions.getInstance(),10l,10l);
        return CommandResult.SUCCESS;
    }
    @Command(name = "fakevote",playerOnly = true)
    public CommandResult fakeVote(Sender sender, String[] args) {
        PlayerManager.getData(sender.getPlayer().getUniqueId()).onVote();
        return CommandResult.SUCCESS;
    }
    @Command(name = "expeditions",aliases = "spoils",playerOnly = true)
    public CommandResult openExpeditions(Sender sender, String[] args) {
        PlayerData data = PlayerManager.getData(sender.getPlayer().getUniqueId());
        new ExpeditionsMenu(data).open(sender);
        return CommandResult.SUCCESS;
    }
    @Command(name = "expeditionsadmin")
    public CommandResult executeAdmin(Sender sender, String[] args) {
        if (args.length > 0){
            if (args[0].equalsIgnoreCase("reload")){
                long start = System.currentTimeMillis();
                Expeditions.getInstance().reloadConfig();
                long end = System.currentTimeMillis();
                sender.sendMessage(CC.GREEN + "Reloaded config in " + (end - start) + "ms");
            }
        }
        return CommandResult.SUCCESS;
    }
    @Command(name = "rollloot",playerOnly = true)
    public CommandResult rollLoot(Sender sender, String[] args) {
        LootManager.test(sender.getPlayer());
        return CommandResult.SUCCESS;
    }
}
