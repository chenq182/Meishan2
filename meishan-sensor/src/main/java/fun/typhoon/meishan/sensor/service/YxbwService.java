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
public class YxbwService {

    public void build(String inputFilePath, String utfFilePath, String gbkFilePath) throws IOException {
		WinFileReader reader = new WinFileReader(inputFilePath);
		final Pattern p = Pattern.compile(".+\\d{2}分\\d{2}秒\\s+(.+)\\s+(合闸|分闸|遥信变位分|开关分位|开关合位).+");
		final Counter<String> counter = new Counter<>();
		reader.line(l -> {
			Matcher m = p.matcher(l);
			if (m.matches())
				counter.put(m.group(1));
		});
		List<Counter.Entry<String, Long>> list = Counter.entryList(
				Map.Entry.comparingByValue(Comparator.reverseOrder()), counter);

		try (WinFileWriter utfWriter = new WinFileWriter(utfFilePath, "UTF-8");
			 WinFileWriter gbkWriter = new WinFileWriter(gbkFilePath, "GBK")) {
			list.stream()
					.map(t -> t.getKey() + "," + t.getValue())
					.peek(utfWriter.line())
					.forEach(gbkWriter.line());
		}
    }
}
