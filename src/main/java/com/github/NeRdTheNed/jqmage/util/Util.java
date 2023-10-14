package com.github.NeRdTheNed.jqmage.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Util {
    /**
     * Write an integer to the output stream as bytes in BE order.
     *
     * @param out the output stream
     * @param value the value to write
     */
    public static void writeIntBE(final OutputStream out, final int value) throws IOException {
        out.write((value >> 24) & 0xFF);
        out.write((value >> 16) & 0xFF);
        out.write((value >> 8) & 0xFF);
        out.write(value & 0xFF);
    }

    /**
     * Write an integer to the output stream as bytes in LE order.
     *
     * @param out the output stream
     * @param value the value to write
     */
    public static void writeIntLE(final OutputStream out, final int value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
        out.write((value >> 16) & 0xFF);
        out.write((value >> 24) & 0xFF);
    }

    /**
     * Write a short to the output stream as bytes in LE order.
     *
     * @param out the output stream
     * @param value the value to write
     */
    public static void writeShortLE(final OutputStream out, final int value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
    }

    /** Writes a byte array to a file */
    public static boolean writeFile(final Path file, final byte[] bytes) {
        try {
            Files.write(file, bytes);
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Writes a byte array to a file */
    public static boolean writeFile(final File file, final byte[] bytes) {
        return writeFile(file.toPath(), bytes);
    }

    /** Writes a byte array to a file */
    public static boolean writeFile(final String file, final byte[] bytes) {
        return writeFile(new File(file), bytes);
    }

    /** Reads the contents of an input stream to a byte array */
    public static byte[] convertInputStreamToBytes(final InputStream in) throws IOException {
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1024];
        int length;

        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        return result.toByteArray();
    }

    /** Decompresses deflate compressed data */
    public static byte[] decompressDeflate(final byte[] data) throws IOException {
        Inflater inflater = null;

        try {
            inflater = new Inflater(true);
            inflater.setInput(data);
            final ByteArrayOutputStream result = new ByteArrayOutputStream(data.length);
            final byte[] buffer = new byte[1024];

            while (!inflater.finished()) {
                if (inflater.needsDictionary() || inflater.needsInput()) {
                    throw new IOException("Invalid deflate decoding state!");
                }

                int inflated;

                try {
                    inflated = inflater.inflate(buffer);
                } catch (final DataFormatException e) {
                    throw new IOException(e);
                }

                result.write(buffer, 0, inflated);
            }

            return result.toByteArray();
        } finally {
            if (inflater != null) {
                inflater.end();
            }
        }
    }

    /** Compresses data to a deflate stream */
    public static byte[] compressDeflate(final byte[] data) throws IOException {
        final Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION, true);
        deflater.setInput(data);
        deflater.finish();

        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length)) {
            final byte[] buffer = new byte[1024];

            while (!deflater.finished()) {
                final int deflated = deflater.deflate(buffer);
                outputStream.write(buffer, 0, deflated);
            }

            return outputStream.toByteArray();
        } finally {
            deflater.end();
        }
    }
}
