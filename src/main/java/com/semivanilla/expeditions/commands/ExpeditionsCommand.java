package com.semivanilla.expeditions.commands;

import net.badbird5907.blib.command.BaseCommand;
import net.badbird5907.blib.command.Command;
import net.badbird5907.blib.command.CommandResult;
import net.badbird5907.blib.command.Sender;

public class ExpeditionsCommand extends BaseCommand {
    @Command(name = "expeditions", aliases = {"spoils"})
    public CommandResult execute(Sender sender, String[] args) {
        return CommandResult.SUCCESS;
    }
}
