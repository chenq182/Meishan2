package fun.typhoon.meishan.sensor.utils;

import java.io.*;
import java.util.function.Consumer;

public class WinFileReader {
    private final String inputFilePath;

    public WinFileReader(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public String getEncoding() throws IOException {
        InputStream ios = new FileInputStream(inputFilePath);
        byte[] b = new byte[3];
        ios.read(b);
        ios.close();
        if (b[0]==-17 && b[1]==-69 && b[2]==-65)
            return "UTF-8";
        return "GBK";
    }

    public void line(Consumer<String> lineConsumer) throws IOException {
        InputStreamReader input = new InputStreamReader(new FileInputStream(inputFilePath), getEncoding());
        BufferedReader reader = new BufferedReader(input);
        String line;
        while ((line = reader.readLine()) != null)
            lineConsumer.accept(line);
        reader.close();
        input.close();
    }
}
