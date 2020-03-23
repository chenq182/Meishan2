package fun.typhoon.meishan;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.out.println("Usage: *.jar <input> <output> 10 50");
            return;
        }
        final int hourThreshold = Integer.parseInt(args[2]);
        final int dayThreshold = Integer.parseInt(args[3]);

        Map<String, Long> hourMap = new HashMap<>();
        Map<String, Long> dayMap = new HashMap<>();
        FileReader fileReader = new FileReader(args[0]);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        //System.out.println(fileReader.getEncoding());
        final Pattern p = Pattern.compile(".+,(\\d{4}年\\d{2}月\\d{2}日)(\\d{2}时)\\d{2}分\\d{2}秒\\s+(.+?)\\s*,.+");
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            Matcher m = p.matcher(line);
            if (m.matches()) {
                String dayMessage = m.group(1) + "," + m.group(3);
                if (dayMap.containsKey(dayMessage))
                    dayMap.put(dayMessage, dayMap.get(dayMessage) + 1L);
                else
                    dayMap.put(dayMessage, 1L);
                String hourMessage = m.group(1) + m.group(2) + "," + m.group(3);
                if (hourMap.containsKey(hourMessage))
                    hourMap.put(hourMessage, hourMap.get(hourMessage) + 1L);
                else
                    hourMap.put(hourMessage, 1L);
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
