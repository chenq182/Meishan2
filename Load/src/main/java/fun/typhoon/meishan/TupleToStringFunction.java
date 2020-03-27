package fun.typhoon.meishan;

import org.apache.spark.api.java.function.Function;
import scala.Tuple2;

public class TupleToStringFunction implements Function<Tuple2<Double, Double>, String> {
    public String call(Tuple2<Double, Double> t) {
        return String.format("%05d,%.3f", t._1.intValue(), t._2);
    }
}
