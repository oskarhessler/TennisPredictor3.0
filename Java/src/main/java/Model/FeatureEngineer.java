package Model;

import Objects.MatchFeatures;
import java.util.*;
import java.util.stream.Collectors;

public class FeatureEngineer {

    private final List<Map<String, Object>> historicalMatches; // All past matches
    private final int rollingWindow = 5; // Number of matches for rolling stats

    public FeatureEngineer(List<Map<String, Object>> historicalMatches) {
        this.historicalMatches = historicalMatches;
    }

    public MatchFeatures createMatchFeatures(Map<String, Object> match) {
        String player1 = (String) match.get("player1");
        String player2 = (String) match.get("player2");
        String surface = (String) match.get("surface");
        String round = (String) match.get("round");
        int winnerFlag = (int) match.get("winner"); // 1 if player1 wins, 0 otherwise

        // Example: pulling from match or historical data
        double player1Rank = getRank(player1);
        double player2Rank = getRank(player2);
        double player1Elo = getElo(player1);
        double player2Elo = getElo(player2);

        // Head-to-head
        int h2hWinsP1 = getH2HWins(player1, player2);
        int h2hWinsP2 = getH2HWins(player2, player1);
        double h2hWinRateP1 = computeWinRate(h2hWinsP1, h2hWinsP1 + h2hWinsP2);
        double h2hWinRateP2 = computeWinRate(h2hWinsP2, h2hWinsP1 + h2hWinsP2);

        // Form stats
        int formWinsP1 = getRecentWins(player1, rollingWindow);
        int formLossesP1 = rollingWindow - formWinsP1;
        double formWinRateP1 = computeWinRate(formWinsP1, rollingWindow);

        int formWinsP2 = getRecentWins(player2, rollingWindow);
        int formLossesP2 = rollingWindow - formWinsP2;
        double formWinRateP2 = computeWinRate(formWinsP2, rollingWindow);

        // Odds (example placeholders, in practice read from CSV)
        double oddsP1 = match.containsKey("oddsP1") ? (double) match.get("oddsP1") : 0.0;
        double oddsP2 = match.containsKey("oddsP2") ? (double) match.get("oddsP2") : 0.0;

        // Rolling averages
        double winnerRollingWinPctLast5 = getRollingWinPct(player1, rollingWindow);
        double winnerRollingAceAvgLast5 = getRollingAceAvg(player1, rollingWindow);
        double winnerRollingDFAvgLast5 = getRollingDFAvg(player1, rollingWindow);
        double winnerRollingBPConversionLast5 = getRollingBPConversion(player1, rollingWindow);

        double loserRollingWinPctLast5 = getRollingWinPct(player2, rollingWindow);
        double loserRollingAceAvgLast5 = getRollingAceAvg(player2, rollingWindow);
        double loserRollingDFAvgLast5 = getRollingDFAvg(player2, rollingWindow);
        double loserRollingBPConversionLast5 = getRollingBPConversion(player2, rollingWindow);

        // H2H win rates (percentage of wins against the opponent historically)
        double winnerH2HWinPct = h2hWinRateP1;
        double loserH2HWinPct = h2hWinRateP2;

        // Surface-specific performance
        double winnerSurfaceWinPct = getSurfaceWinPct(player1, surface);
        double loserSurfaceWinPct = getSurfaceWinPct(player2, surface);
        double winnerSurfaceAceRate = getSurfaceAceRate(player1, surface);
        double loserSurfaceAceRate = getSurfaceAceRate(player2, surface);

        return new MatchFeatures(
                player1, player2, surface, round,
                player1Rank, player2Rank, player1Elo, player2Elo,
                h2hWinsP1, h2hWinsP2, h2hWinRateP1, h2hWinRateP2,
                formWinsP1, formLossesP1, formWinRateP1,
                formWinsP2, formLossesP2, formWinRateP2,
                oddsP1, oddsP2,
                winnerRollingWinPctLast5, winnerRollingAceAvgLast5,
                winnerRollingDFAvgLast5, winnerRollingBPConversionLast5,
                loserRollingWinPctLast5, loserRollingAceAvgLast5,
                loserRollingDFAvgLast5, loserRollingBPConversionLast5,
                winnerH2HWinPct, loserH2HWinPct,
                winnerSurfaceWinPct, loserSurfaceWinPct,
                winnerSurfaceAceRate, loserSurfaceAceRate,
                winnerFlag
        );
    }

    // ===== Helper methods for computing stats =====

    private double getRank(String player) {
        // TODO: Pull from rankings table
        return 0.0;
    }

    private double getElo(String player) {
        // TODO: Pull from Elo ratings
        return 0.0;
    }

    private int getH2HWins(String player, String opponent) {
        return (int) historicalMatches.stream()
                .filter(m -> m.get("winner").equals(player) && m.get("loser").equals(opponent))
                .count();
    }

    private int getRecentWins(String player, int n) {
        return (int) historicalMatches.stream()
                .filter(m -> m.get("player").equals(player))
                .limit(n)
                .filter(m -> m.get("winner").equals(player))
                .count();
    }

    private double getRollingWinPct(String player, int n) {
        int wins = getRecentWins(player, n);
        return computeWinRate(wins, n);
    }

    private double getRollingAceAvg(String player, int n) {
        return historicalMatches.stream()
                .filter(m -> m.get("player").equals(player))
                .limit(n)
                .mapToDouble(m -> (double) m.getOrDefault("aces", 0.0))
                .average()
                .orElse(0.0);
    }

    private double getRollingDFAvg(String player, int n) {
        return historicalMatches.stream()
                .filter(m -> m.get("player").equals(player))
                .limit(n)
                .mapToDouble(m -> (double) m.getOrDefault("double_faults", 0.0))
                .average()
                .orElse(0.0);
    }

    private double getRollingBPConversion(String player, int n) {
        return historicalMatches.stream()
                .filter(m -> m.get("player").equals(player))
                .limit(n)
                .mapToDouble(m -> {
                    double bpWon = (double) m.getOrDefault("bp_won", 0.0);
                    double bpTotal = (double) m.getOrDefault("bp_total", 0.0);
                    return bpTotal > 0 ? bpWon / bpTotal : 0.0;
                })
                .average()
                .orElse(0.0);
    }

    private double getSurfaceWinPct(String player, String surface) {
        List<Map<String, Object>> matches = historicalMatches.stream()
                .filter(m -> m.get("surface").equals(surface) &&
                        (m.get("winner").equals(player) || m.get("loser").equals(player)))
                .collect(Collectors.toList());
        long wins = matches.stream().filter(m -> m.get("winner").equals(player)).count();
        return computeWinRate((int) wins, matches.size());
    }

    private double getSurfaceAceRate(String player, String surface) {
        return historicalMatches.stream()
                .filter(m -> m.get("surface").equals(surface) && m.get("player").equals(player))
                .mapToDouble(m -> (double) m.getOrDefault("aces", 0.0))
                .average()
                .orElse(0.0);
    }

    private double computeWinRate(int wins, int total) {
        return total > 0 ? (double) wins / total : 0.0;
    }
}
