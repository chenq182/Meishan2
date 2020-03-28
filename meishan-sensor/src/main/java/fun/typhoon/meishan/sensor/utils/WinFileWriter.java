package fun.typhoon.meishan.sensor.utils;

import java.io.*;
import java.util.function.Consumer;

public class WinFileWriter implements Closeable, Flushable {
    private final OutputStreamWriter output;
    private final BufferedWriter writer;
    private final Consumer<String> lineConsumer;

    public WinFileWriter(String outputFilePath, String encoding) throws IOException {
        output = new OutputStreamWriter(new FileOutputStream(outputFilePath), encoding);
        writer = new BufferedWriter(output);
        lineConsumer = s -> {
            try {
                writer.write(s);
                writer.newLine();
            } catch (IOException ignored) {}
        };
    }

    public Consumer<String> line() {
        return lineConsumer;
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
        output.flush();
    }

    @Override
    public void close() throws IOException {
        flush();
        writer.close();
        output.close();
    }
}
