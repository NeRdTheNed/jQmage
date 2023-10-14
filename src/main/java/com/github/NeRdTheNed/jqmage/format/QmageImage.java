package com.github.NeRdTheNed.jqmage.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import com.github.NeRdTheNed.jqmage.util.Util;

public class QmageImage {
    public static final int COMPRESSION_TYPE_RLE = 0x06;

    public static final int FLAG_PREMULTIPLIED = 0b00100000;
    public static final int FLAG_COLOR_TABLE   = 0b00010000;

    char firstSig;
    char secondSig;

    byte versionMajor;
    byte versionMinor;
    byte versionPatch;

    byte flags;
    byte quality;

    int width;
    int height;

    byte extraLength;

    byte[] headerExtra;

    byte colors;
    int colorTableLength;
    byte[] compressedColorTable;
    short[] uncompressedColorTable;

    byte compressionMethod;

    byte[] compressedData;

    public boolean hasColorTable() {
        return (flags & FLAG_COLOR_TABLE) != 0;
    }

    public boolean knownCompressionMethod() {
        // TODO
        return false;
    }

    private static final boolean PRINT_PARSE = true;

    public boolean read(final InputStream is) throws IOException {
        if (is == null) {
            return false;
        }

        // Check if data is likely Qmage
        firstSig = (char)(is.read() & 0xff);

        if (firstSig != 'Q') {
            return false;
        }

        secondSig = (char)(is.read() & 0xff);

        if ((secondSig != 'G') && (secondSig != 'M')) {
            return false;
        }

        if (PRINT_PARSE) {
            System.out.println("Read file signature " + firstSig + secondSig);
        }

        // Read version information
        versionMajor = (byte)(is.read() & 0xff);
        versionMinor = (byte)(is.read() & 0xff);
        versionPatch = (byte)(is.read() & 0xff);

        if (PRINT_PARSE) {
            System.out.println("Read file version " + versionMajor + "." + versionMinor + "." + versionPatch);
        }

        // Read image information
        flags = (byte)(is.read() & 0xff);
        quality = (byte)(is.read() & 0xff);
        width = ((is.read() & 0xff) + ((is.read() & 0xff) << 8));
        height = ((is.read() & 0xff) + ((is.read() & 0xff) << 8));

        if (PRINT_PARSE) {
            System.out.println("Flags " + Integer.toBinaryString(flags));
            System.out.println("Quality " + quality);
            System.out.println("Width " + width);
            System.out.println("Height " + height);
        }

        // Read extra
        extraLength = (byte)(is.read() & 0xff);
        headerExtra = extraLength > 0 ? Util.readFromInputStream(is, extraLength) : new byte[0];

        if (PRINT_PARSE && (extraLength > 0)) {
            System.out.println("Extra was " + Arrays.toString(headerExtra));
        }

        // Read color table

        if (hasColorTable()) {
            colors = (byte)(is.read() & 0xff);
            colorTableLength = ((is.read() & 0xff) + ((is.read() & 0xff) << 8));
        }

        compressedColorTable = colorTableLength > 0 ? Util.readFromInputStream(is, colorTableLength) : new byte[0];
        uncompressedColorTable = colorTableLength > 0 ? Util.bytesToShort(Util.decompressDeflate(compressedColorTable, false)) : new short[0];

        if (PRINT_PARSE && (colorTableLength > 0)) {
            System.out.println("Uncompressed color table was " + Arrays.toString(uncompressedColorTable));
        }

        if (hasColorTable() && (uncompressedColorTable.length != (colors + 1))) {
            if (PRINT_PARSE) {
                System.out.println("Incorrect decompressed color table, colors was " + colors + ", length was " + uncompressedColorTable.length);
            }

            return false;
        }

        // Read stream marker
        final byte streamMarker1 = (byte)(is.read() & 0xff);

        if (streamMarker1 != 0xFF) {
            return false;
        }

        final byte streamMarker2 = (byte)(is.read() & 0xff);

        if (streamMarker2 != 0x00) {
            return false;
        }

        // Read compressed data
        compressionMethod = (byte)(is.read() & 0xff);

        if (PRINT_PARSE) {
            System.out.println("Compression method was " + compressionMethod);
        }

        if (!knownCompressionMethod()) {
            if (PRINT_PARSE) {
                System.out.println("Unknown compression method " + compressionMethod);
            }

            return false;
        }

        // TODO read compressed data here
        // Read end of stream marker
        final byte endStreamMarker1 = (byte)(is.read() & 0xff);

        if (endStreamMarker1 != 0xFF) {
            return false;
        }

        final byte endStreamMarker2 = (byte)(is.read() & 0xff);

        if (endStreamMarker2 != 0x00) {
            return false;
        }

        return true;
    }

    public boolean write(final OutputStream os) throws IOException {
        os.write(firstSig);
        os.write(secondSig);
        os.write(versionMajor);
        os.write(versionMinor);
        os.write(versionPatch);
        os.write(flags);
        os.write(quality);
        Util.writeShortLE(os, width);
        Util.writeShortLE(os, height);
        os.write(extraLength);
        os.write(headerExtra);

        if (hasColorTable()) {
            os.write(colors);
            Util.writeShortLE(os, colorTableLength);
            os.write(compressedColorTable);
        }

        os.write(0xff);
        os.write(0x00);
        os.write(compressedData);
        os.write(0xff);
        os.write(0x00);
        return true;
    }
}
