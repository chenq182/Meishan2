package fun.typhoon.meishan;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 5) {
            System.out.println("Usage: *.jar <input> <output> 10 30 1.6");
            return;
        }
        final int hourThreshold = Integer.parseInt(args[2]);
        final int dayThreshold = Integer.parseInt(args[3]);
        final double overThreshold = Double.parseDouble(args[4]);

        Map<String, Long> hourMap = new HashMap<>();
        Map<String, Long> dayMap = new HashMap<>();
        FileReader fileReader = new FileReader(args[0]);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        //System.out.println(fileReader.getEncoding());
        final Pattern p = Pattern.compile(".+,(\\d{4}年\\d{2}月\\d{2}日)(\\d{2}时)\\d{2}分\\d{2}秒\\s+(.+)\\s+(线电压幅值|电流值|[ABC]相电压幅值|[ABC]相电流|有功值|无功值|实测值).+,限值:\\s*([^\\s]+)\\s+([-\\w\\.]+).+");
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (!line.contains(",限值"))
                continue;
            Matcher m = p.matcher(line);
            if (m.matches()) {
                double v2 = Double.parseDouble(m.group(5));
                double v3 = Double.parseDouble(m.group(6));
                if (v3 > v2*overThreshold)
                    continue;
                String dayDevice = m.group(1) + "," + m.group(3);
                if (dayMap.containsKey(dayDevice))
                    dayMap.put(dayDevice, dayMap.get(dayDevice) + 1L);
                else
                    dayMap.put(dayDevice, 1L);
                String hourDevice = m.group(1) + m.group(2) + "," + m.group(3);
                if (hourMap.containsKey(hourDevice))
                    hourMap.put(hourDevice, hourMap.get(hourDevice) + 1L);
                else
                    hourMap.put(hourDevice, 1L);
            }
        }
        bufferedReader.close();
        fileReader.close();
        hourMap.entrySet().removeIf(entry -> entry.getValue() < hourThreshold);
        dayMap.entrySet().removeIf(entry -> entry.getValue() < dayThreshold);

        List<Map.Entry<String, Long>> list = new ArrayList<>(hourMap.entrySet());
        list.addAll(dayMap.entrySet());
        list.sort(Map.Entry.comparingByKey(Comparator.reverseOrder()));

        FileWriter fileWriter = new FileWriter(args[1]);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (Map.Entry<String, Long> t: list) {
            bufferedWriter.write(t.getKey() + "," + t.getValue());
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
        fileWriter.flush();
        bufferedWriter.close();
        fileWriter.close();
    }
}
