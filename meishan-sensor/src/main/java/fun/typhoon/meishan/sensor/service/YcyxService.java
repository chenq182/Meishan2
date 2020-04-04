package fun.typhoon.meishan.sensor.service;

import fun.typhoon.meishan.sensor.utils.Counter;
import fun.typhoon.meishan.sensor.utils.WinFileReader;
import fun.typhoon.meishan.sensor.utils.WinFileWriter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class YcyxService {

    public void build(String inputFilePath, String utfFilePath, String gbkFilePath,
                      int hourMinCount, int dayMinCount, double overLimit) throws IOException {
        WinFileReader reader = new WinFileReader(inputFilePath);
        final Pattern p = Pattern.compile(".+,(\\d{4}年\\d{2}月\\d{2}日)(\\d{2}时)\\d{2}分\\d{2}秒\\s+(.+)\\s+(线电压幅值|电流值|[ABC]相电压幅值|[ABC]相电流|有功值|无功值|实测值).+,限值:\\s*([^\\s]+)\\s+([-\\d\\.]+).+");
        final Counter<String> hourCounter = new Counter<>();
        final Counter<String> dayCounter = new Counter<>();
        reader.line(l -> {
            if (!l.contains(",限值"))
                return;
            l = l.replace("\"", "");
            Matcher m = p.matcher(l);
            if (!m.matches())
                return;
            double limit = Double.parseDouble(m.group(5));
            double value = Double.parseDouble(m.group(6));
            if (value > limit*overLimit)
                return;
            hourCounter.put(m.group(1) + m.group(2) + "," + m.group(3));
            dayCounter.put(m.group(1) + "," + m.group(3));
        });
        hourCounter.removeLessCount(hourMinCount);
        dayCounter.removeLessCount(dayMinCount);
        List<Counter.Entry<String, Long>> list = Counter.entryList(Map.Entry.comparingByKey(Comparator.reverseOrder()),
                hourCounter, dayCounter);

        try (WinFileWriter utfWriter = new WinFileWriter(utfFilePath, "UTF-8");
             WinFileWriter gbkWriter = new WinFileWriter(gbkFilePath, "GBK")) {
            list.stream()
                    .map(t -> t.getKey() + "," + t.getValue())
                    .peek(utfWriter.line())
                    .forEach(gbkWriter.line());
        }
    }
}
