package com.ricedotwho.zero.util.command;

public record CommandBase(String description, String usage, CommandHandler handler) {

}
