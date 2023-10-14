package com.github.NeRdTheNed.jqmage.cmd;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "jQmage", version = "jQmage v1.0.0-alpha", subcommands = { PrintInfo.class, ConvertToPNG.class })
public class Main {
    public static void main(final String[] args) {
        final int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
