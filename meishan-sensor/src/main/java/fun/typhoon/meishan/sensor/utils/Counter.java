package fun.typhoon.meishan.sensor.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Counter<T> extends HashMap<T, Long> {

    public void put(T element) {
        Long num = this.get(element);
        this.put(element, num==null? 1L: num+1L);
    }

    public void removeLessCount(long minCount) {
        this.entrySet().removeIf(entry -> entry.getValue()<minCount);
    }

    public List<Counter.Entry<T, Long>> entryList(
            Comparator<? super Counter.Entry<T, Long>> comparator, Counter<T>... counters){
        List<Counter.Entry<T, Long>> list = new ArrayList<>();
        for (Counter<T> counter: counters)
            list.addAll(counter.entrySet());
        if (comparator != null)
            list.sort(comparator);
        return list;
    }
}
