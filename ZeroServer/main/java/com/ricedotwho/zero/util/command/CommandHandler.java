package com.ricedotwho.zero.util.command;

@FunctionalInterface
public interface CommandHandler {
    void run(String[] args);
}
