package com.semivanilla.expeditions.commands;

import com.semivanilla.expeditions.Expeditions;
import com.semivanilla.expeditions.manager.LootManager;
import com.semivanilla.expeditions.menu.ClaimExpeditionMenu;
import com.semivanilla.expeditions.object.MenuUpdateTask;
import net.badbird5907.blib.command.BaseCommand;
import net.badbird5907.blib.command.Command;
import net.badbird5907.blib.command.CommandResult;
import net.badbird5907.blib.command.Sender;
import net.badbird5907.blib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ExpeditionsCommand extends BaseCommand {
    @Command(name = "expeditions", aliases = {"spoils"})
    public CommandResult execute(Sender sender, String[] args) {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            stacks.add(new ItemBuilder(Material.DIAMOND).name("test").build());
        }
        ClaimExpeditionMenu menu = new ClaimExpeditionMenu(stacks);
        menu.open(sender);
        new MenuUpdateTask(menu,sender.getPlayer()).runTaskTimer(Expeditions.getInstance(),10l,10l);
        return CommandResult.SUCCESS;
    }
    @Command(name = "rollloot",playerOnly = true)
    public CommandResult rollLoot(Sender sender, String[] args) {
        LootManager.test(sender.getPlayer());
        return CommandResult.SUCCESS;
    }
}
