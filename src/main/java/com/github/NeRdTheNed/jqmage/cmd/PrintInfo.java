package com.github.NeRdTheNed.jqmage.cmd;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "print-info", mixinStandardHelpOptions = true, description = "Print info about a Qmage file")
class PrintInfo implements Callable<Integer> {
    @Parameters(index = "0", description = "The input Qmage image")
    private Path inputFile;

    private static boolean printInfo(final Path inputFile) throws IOException {
        // TODO Printing logic
        return false;
    }

    @Override
    public Integer call() throws Exception {
        final boolean didPrint = printInfo(inputFile);

        if (!didPrint) {
            System.err.println("Failed to print info for " + inputFile);
        }

        return didPrint ? CommandLine.ExitCode.OK : 1;
    }
}
