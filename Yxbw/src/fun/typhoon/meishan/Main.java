package fun.typhoon.meishan;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws IOException {
	    if (args.length != 2) {
	        System.out.println("Usage: *.jar <input> <output>");
	        return;
        }

	    Map<String, Long> map = new HashMap<>();
		FileReader fileReader = new FileReader(args[0]);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		//System.out.println(fileReader.getEncoding());
		final Pattern p = Pattern.compile(".+\\d{2}分\\d{2}秒\\s+(.+)\\s+(合闸|分闸|遥信变位分|开关分位|开关合位).+");
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			Matcher m = p.matcher(line);
			if (m.matches()) {
				String key = m.group(1);
				if (map.containsKey(key))
					map.put(key, map.get(key)+1);
				else
					map.put(key, 1L);
			}
		}
		bufferedReader.close();
		fileReader.close();

		List<Map.Entry<String, Long>> list = new ArrayList<>(map.entrySet());
		list.sort((t1,t2) -> t2.getValue().compareTo(t1.getValue()));

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
