package fun.typhoon.meishan;

import fun.typhoon.meishan.utils.WinFileReader;
import fun.typhoon.meishan.utils.WinFileWriter;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Yxbw {

    public static void main(String[] args) throws IOException {
	    if (args.length != 3) {
	        System.out.println("Usage: *.jar <input> <utfOutput> <gbkOutput>");
	        return;
        }

		WinFileReader reader = new WinFileReader(args[0]);
		final Pattern p = Pattern.compile(".+\\d{2}分\\d{2}秒\\s+(.+)\\s+(合闸|分闸|遥信变位分|开关分位|开关合位).+");
		final Map<String, Long> map = new HashMap<>();
		reader.line(l -> {
			Matcher m = p.matcher(l);
			if (m.matches()) {
				String key = m.group(1);
				Long count = map.get(key);
				map.put(key, count==null? 1: count+1);
			}
		});

		List<Map.Entry<String, Long>> list = new ArrayList<>(map.entrySet());
		list.sort((t1,t2) -> t2.getValue().compareTo(t1.getValue()));

		try (WinFileWriter utfWriter = new WinFileWriter(args[1], "UTF-8");
			 WinFileWriter gbkWriter = new WinFileWriter(args[2], "GBK")) {
			list.stream()
					.map(t -> t.getKey() + "," + t.getValue())
					.peek(utfWriter.line())
					.forEach(gbkWriter.line());
		}
    }
}
