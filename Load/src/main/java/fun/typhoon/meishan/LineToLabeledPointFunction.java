package fun.typhoon.meishan;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.sql.Row;

public class LineToLabeledPointFunction implements Function<Row, LabeledPoint> {
    public LabeledPoint call(Row r) {
        double[] feature = new double[r.size()-1];
        for (int i=0; i<r.size()-1; i++)
            feature[i] = Double.parseDouble(r.getString(i));
        Vector featureVector = Vectors.dense(feature);
        double label = Double.parseDouble(r.getString(r.size()-1));
        return new LabeledPoint(label, featureVector);
    }
}
