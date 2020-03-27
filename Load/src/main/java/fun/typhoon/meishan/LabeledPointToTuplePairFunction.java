package fun.typhoon.meishan;

import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import scala.Tuple2;

public class LabeledPointToTuplePairFunction implements PairFunction<LabeledPoint, Double, Double> {
    DecisionTreeModel model;
    public LabeledPointToTuplePairFunction(DecisionTreeModel model) {
        this.model = model;
    }

    public Tuple2<Double, Double> call(LabeledPoint testPoint) {
        return new Tuple2<Double, Double>(testPoint.label(), model.predict(testPoint.features()));
    }
}
