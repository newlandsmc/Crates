package com.semivanilla.expeditions.commands;

import com.semivanilla.expeditions.Expeditions;
import com.semivanilla.expeditions.manager.PlayerManager;
import com.semivanilla.expeditions.menu.ClaimExpeditionMenu;
import com.semivanilla.expeditions.object.MenuUpdateTask;
import net.badbird5907.blib.command.BaseCommand;
import net.badbird5907.blib.command.Command;
import net.badbird5907.blib.command.CommandResult;
import net.badbird5907.blib.command.Sender;

import java.util.ArrayList;

public class ExpeditionsCommand extends BaseCommand {
    @Command(name = "expeditions", aliases = {"spoils"})
    public CommandResult execute(Sender sender, String[] args) {
        ClaimExpeditionMenu menu = new ClaimExpeditionMenu(new ArrayList<>());
        menu.open(sender);
        new MenuUpdateTask(menu,sender.getPlayer()).runTaskTimer(Expeditions.getInstance(),10l,10l);
        return CommandResult.SUCCESS;
    }
}
