package fun.typhoon.meishan.sensor.service;

import fun.typhoon.meishan.sensor.utils.WinFileReader;
import fun.typhoon.meishan.sensor.utils.WinFileWriter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GpxxService {

    public void build(String inputFilePath, String utfFilePath, String gbkFilePath,
                      int hourMinCount, int dayMinCount) throws IOException {
        WinFileReader reader = new WinFileReader(inputFilePath);
        final Pattern p = Pattern.compile(".+(\\d{4}年\\d{2}月\\d{2}日)(\\d{2}时)\\d{2}分\\d{2}秒\\s+(.+?)\\s*,.+");
        final Map<String, Long> hourMap = new HashMap<>();
        final Map<String, Long> dayMap = new HashMap<>();
        reader.line(l -> {
            Matcher m = p.matcher(l);
            if (!m.matches())
                return;
            String dayMessage = m.group(1) + "," + m.group(3);
            Long dayCount = dayMap.get(dayMessage);
            dayMap.put(dayMessage, dayCount==null? 1: dayCount+1);
            String hourMessage = m.group(1) + m.group(2) + "," + m.group(3);
            Long hourCount = hourMap.get(hourMessage);
            hourMap.put(hourMessage, hourCount==null? 1: hourCount+1);
        });
        hourMap.entrySet().removeIf(entry -> entry.getValue() < hourMinCount);
        dayMap.entrySet().removeIf(entry -> entry.getValue() < dayMinCount);

        List<Map.Entry<String, Long>> list = new ArrayList<>(hourMap.entrySet());
        list.addAll(dayMap.entrySet());
        list.sort(Map.Entry.comparingByKey(Comparator.reverseOrder()));

        try (WinFileWriter utfWriter = new WinFileWriter(utfFilePath, "UTF-8");
             WinFileWriter gbkWriter = new WinFileWriter(gbkFilePath, "GBK")) {
            list.stream()
                    .map(t -> t.getKey() + "," + t.getValue())
                    .peek(utfWriter.line())
                    .forEach(gbkWriter.line());
        }
    }
}
