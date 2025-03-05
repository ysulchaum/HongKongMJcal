package edu.cuhk.csci3310;

public class RoundRecord {
    private int Faan;
    private int Score;
    private int Round;
    private String WinPlayer;
    private String LosePlayer;
    public RoundRecord(int Faan, int Score, int Round,
                       String WinPlayer, String LosePlayer) {
        this.Faan = Faan;
        this.Score = Score;
        this.Round = Round;
        this.WinPlayer = WinPlayer;
        this.LosePlayer = LosePlayer;
    }

    // Add toString() for meaningful display
    @Override
    public String toString() {
        return String.format(
                "Round %d: %s won %d faan (Score: +%d) vs %s",
                Round, WinPlayer, Faan, Score, LosePlayer
        );
    }
}
