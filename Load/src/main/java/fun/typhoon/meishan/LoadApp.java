package fun.typhoon.meishan;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.DecisionTree;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.HashMap;
import java.util.Map;

public class LoadApp {
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder().appName("Load Predict").getOrCreate();
        int count = Integer.parseInt(args[0]);
        Dataset<Row> rawData = spark.read().csv(args[1]);
        Dataset<Row> preData = spark.read().csv(args[2]);
        JavaRDD<LabeledPoint> trainingData = rawData.javaRDD().map(new LineToLabeledPointFunction());
        JavaRDD<LabeledPoint> testData = preData.javaRDD().map(new LineToLabeledPointFunction());

        Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<Integer, Integer>();
        categoricalFeaturesInfo.put(0, count);
        categoricalFeaturesInfo.put(5, 2);
        categoricalFeaturesInfo.put(8, 5);
        final DecisionTreeModel model = DecisionTree.trainRegressor(trainingData,
                categoricalFeaturesInfo, "variance", 20, 150);

        JavaPairRDD<Double, Double> labelAndPrediction =
                testData.mapToPair(new LabeledPointToTuplePairFunction(model));

        labelAndPrediction.sortByKey().map(new TupleToStringFunction()).saveAsTextFile(args[3]);
        spark.stop();
    }
}
