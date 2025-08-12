package Utils;

import Objects.MatchFeatures;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Writes a list of MatchFeatures to a CSV file for XGBoost training.
 */
public class CSVWriterXGBoost {

    public static void writeToCSV(List<MatchFeatures> matches) {
        String filePath = "TennisPredictor3.0/Data/train_data.csv";

        try (FileWriter writer = new FileWriter(filePath)) {

            // Write header
            writer.append("player1,player2,surface,round,player1Rank,player2Rank,player1Elo,player2Elo,")
                    .append("h2hWinsP1,h2hWinsP2,h2hWinRateP1,h2hWinRateP2,")
                    .append("formWinsP1,formLossesP1,formWinRateP1,")
                    .append("formWinsP2,formLossesP2,formWinRateP2,")
                    .append("oddsP1,oddsP2,winner\n");

            // Write data
            for (MatchFeatures mf : matches) {
                writer.append(escape(mf.getPlayer1())).append(",")
                        .append(escape(mf.getPlayer2())).append(",")
                        .append(escape(mf.getSurface())).append(",")
                        .append(escape(mf.getRound())).append(",")
                        .append(String.valueOf(mf.getPlayer1Rank())).append(",")
                        .append(String.valueOf(mf.getPlayer2Rank())).append(",")
                        .append(String.valueOf(mf.getPlayer1Elo())).append(",")
                        .append(String.valueOf(mf.getPlayer2Elo())).append(",")
                        .append(String.valueOf(mf.getH2hWinsP1())).append(",")
                        .append(String.valueOf(mf.getH2hWinsP2())).append(",")
                        .append(String.valueOf(mf.getH2hWinRateP1())).append(",")
                        .append(String.valueOf(mf.getH2hWinRateP2())).append(",")
                        .append(String.valueOf(mf.getFormWinsP1())).append(",")
                        .append(String.valueOf(mf.getFormLossesP1())).append(",")
                        .append(String.valueOf(mf.getFormWinRateP1())).append(",")
                        .append(String.valueOf(mf.getFormWinsP2())).append(",")
                        .append(String.valueOf(mf.getFormLossesP2())).append(",")
                        .append(String.valueOf(mf.getFormWinRateP2())).append(",")
                        .append(String.valueOf(mf.getOddsP1())).append(",")
                        .append(String.valueOf(mf.getOddsP2())).append(",")
                        .append(String.valueOf(mf.getWinner()))
                        .append("\n");
            }

            System.out.println("CSV successfully written to: " + filePath);

        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }

    // Helper to escape quotes in strings
    private static String escape(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
