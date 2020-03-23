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
        final Pattern p1 = Pattern.compile(".+(\\d{4}年\\d{2}月\\d{2}日)(\\d{2}时)\\d{2}分\\d{2}秒\\s+(.+--[1-6]).+");
        final Pattern p2 = Pattern.compile(".+(\\d{4}年\\d{2}月\\d{2}日)(\\d{2}时)\\d{2}分\\d{2}秒\\s+(.+)\\s+动作.+");
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("复归"))
                continue;
            String dayAlarm;
            String hourAlarm;
            Matcher m1 = p1.matcher(line);
            if (m1.matches()) {
                dayAlarm = m1.group(1) + "," + m1.group(3);
                hourAlarm = m1.group(1) + m1.group(2) + "," + m1.group(3);
            } else {
                Matcher m2 = p2.matcher(line);
                if (m2.matches()) {
                    dayAlarm = m2.group(1) + "," + m2.group(3);
                    hourAlarm = m2.group(1) + m2.group(2) + "," + m2.group(3);
                } else
                    continue;
            }
            if (dayMap.containsKey(dayAlarm))
                dayMap.put(dayAlarm, dayMap.get(dayAlarm) + 1L);
            else
                dayMap.put(dayAlarm, 1L);
            if (hourMap.containsKey(hourAlarm))
                hourMap.put(hourAlarm, hourMap.get(hourAlarm) + 1L);
            else
                hourMap.put(hourAlarm, 1L);
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
            bufferedWriter.write(t.getKey() + "," + classification(t.getKey()) + "," + t.getValue());
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
        fileWriter.flush();
        bufferedWriter.close();
        fileWriter.close();
    }

    private static final Pattern[] p = new Pattern[18];
    static {
        p[0] = Pattern.compile(".+(SF6气压低闭锁|油压低.+闭锁|N2泄漏闭锁|空气压力低.+闭锁|储能电机故障|小电流接地告警|弹簧未储能).*");
        p[1] = Pattern.compile(".+(冷却器电源消失|冷却器风扇故障|冷却器强迫油循环故障|冷却器全停告警|油温过高告警|母线接地).*");
        p[2] = Pattern.compile(".+(接地告警).*");

        p[3] = Pattern.compile(".+(SF6气压低告警|油压低告警|N2泄漏告警|油泵打压超时|气泵打压超时|气泵.+告警|加热器故障|机构就地控制).*");
        p[4] = Pattern.compile(".+(压力释放告警|压力突变告警|油温高告警|油位异常|有载轻瓦斯告警|有载压力释放告警|有载油位异常|过载闭锁有载调压).*");

        p[5] = Pattern.compile(".+(控制回路断线|控制电源消失|保护重合闸闭锁|保护.+断线|保护.+失电|保护装置故障|保护通道异常|测控装置通信中断|机构就地控制|测控保护装置通信中断).*");
        p[6] = Pattern.compile(".+(过流保护出口).*");
        p[7] = Pattern.compile(".+(冷却器控制器故障).*");
        p[8] = Pattern.compile(".+(TV.+跳开|母差.+告警).*");
        p[9] = Pattern.compile(".+(备自投装置故障|直流系统接地|直流电源系统交流输入故障).*");
        p[10] = Pattern.compile(".+(监控系统故障|监控逆变电源故障|消防装置故障告警).*");
        p[11] = Pattern.compile(".+(合并单元装置故障|智能终端装置故障).*");

        p[12] = Pattern.compile(".+(保护远跳.+信|保护切换.+接通|保护装置异常|保护装置通信中断|测控装置异常).*");
        p[13] = Pattern.compile(".+(非电气量保护装置异常|保护过负荷告警).*");
        p[14] = Pattern.compile(".+(母差开入信号异常告警|保护开入.*异常).*");
        p[15] = Pattern.compile(".+(备自投出口|电源异常|备自投装置异常|直流母线电压异常|直流电源控制装置通信中断|直流系统异常).*");
        p[16] = Pattern.compile(".+(监控系统异常|远动装置异常|GPS.*异常|PMU.*异常|故障录波.*异常|监控逆变电源异常|其它公共设备异常|消防装置火灾告警|高压脉冲防盗告警|边界防盗告警).*");
        p[17] = Pattern.compile(".+(合并.+异常|智能终端.+异常|GOOSE|SV|就地控制|对时异常).*");
    }
    private static String classification(String alarm) {
        int i;
        for (i = 0; i < p.length; i++) {
            Matcher m = p[i].matcher(alarm);
            if (m.matches())
                break;
        }
        if (i < 3)
            return "一次故障";
        if (i < 5)
            return "一次告警";
        if (i < 12)
            return "二次故障";
        if (i < 18)
            return "二次告警";
        return "";
    }
}
