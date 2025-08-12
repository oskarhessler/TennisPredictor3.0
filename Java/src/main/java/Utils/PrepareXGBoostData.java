package Utils;

import Model.*;
import Objects.*;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrepareXGBoostData {

    public class Main {
        public static void main(String[] args) {
            String inputCSV = "Data/merged_matches.csv";
            String outputCSV = "Data/xgboost_input.csv";

            FeatureEngineer fe = new FeatureEngineer();
            try {
                List<MatchResult> matches = fe.loadMatches(inputCSV);
                List<MatchFeatures> features = fe.generateFeatures(matches);
                CSVWriterXGBoost.writeFeaturesToCSV(features, outputCSV);
                System.out.println("Feature CSV for XGBoost created: " + outputCSV);
            } catch (IOException e) {
                System.err.println("Error creating XGBoost input data: " + e.getMessage());
            }
        }
    }

}
