package com.github.NeRdTheNed.jqmage.cmd;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import com.github.NeRdTheNed.jqmage.format.QmageImage;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "convert-to-png", mixinStandardHelpOptions = true, description = "Convert Qmage file to PNG")
class ConvertToPNG implements Callable<Integer> {
    @Parameters(index = "0", description = "The input Qmage image")
    private Path inputFile;

    @Parameters(index = "1", description = "The output PNG image")
    private Path outputFile;

    private static boolean convert(final Path inputFile, final Path outputFile) throws IOException {
        final QmageImage image = new QmageImage();

        try (FileInputStream fis = new FileInputStream(inputFile.toFile())) {
            image.read(fis);
        }

        // TODO Conversion logic
        return false;
    }

    @Override
    public Integer call() throws Exception {
        final boolean didConvert = convert(inputFile, outputFile);

        if (!didConvert) {
            System.err.println("Failed to convert " + inputFile);
        }

        return didConvert ? CommandLine.ExitCode.OK : 1;
    }
}
