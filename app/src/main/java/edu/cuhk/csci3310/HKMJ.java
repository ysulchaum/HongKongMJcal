package edu.cuhk.csci3310;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HKMJ {
    // 定義玩家座位
    enum Seat {
        EAST, SOUTH, WEST, NORTH
    }

    class Player {
        private String name;
        private int score;
        private boolean isActive;

        public Player(String name) {
            this.name = name;
            this.score = 0;
            this.isActive = false;
        }

        // Getters and setters
        public String getName() { return name; }
        public int getScore() { return score; }
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
        public void addScore(int points) { score += points; }
    }

    // 遊戲狀態
    private Map<Seat, Player> seatMap = new HashMap<>();
    private List<Player> allPlayers = new ArrayList<>();
    private int consecutiveWins;
    private Seat currentDealer;

    public HKMJ() {
        // 初始化座位
        seatMap.put(Seat.EAST, null);
        seatMap.put(Seat.SOUTH, null);
        seatMap.put(Seat.WEST, null);
        seatMap.put(Seat.NORTH, null);

        consecutiveWins = 0;
    }

    // 新增玩家到玩家池
    public void addPlayer(Player player) {
        allPlayers.add(player);
        // 自動分配座位如果還有空位
        for (Seat seat : Seat.values()) {
            if (seatMap.get(seat) == null && !allPlayers.isEmpty()) {
                seatMap.put(seat, player);
                player.setActive(true);
                break;
            }
        }
    }

    // 替換玩家
    public boolean replacePlayer(Seat seat, String newPlayerName) {
        Player newPlayer = allPlayers.stream()
                .filter(p -> p.getName().equals(newPlayerName) && !p.isActive())
                .findFirst()
                .orElse(null);

        if (newPlayer != null) {
            // 移除舊玩家狀態
            Player oldPlayer = seatMap.get(seat);
            if (oldPlayer != null) {
                oldPlayer.setActive(false);
            }

            // 設置新玩家
            seatMap.put(seat, newPlayer);
            newPlayer.setActive(true);
            return true;
        }
        return false;
    }

    // 獲取當前莊家
    public Player getCurrentDealer() {
        return seatMap.get(currentDealer);
    }

    // 輪轉座位順序
    public void rotateSeats() {
        Seat[] seats = Seat.values();
        currentDealer = seats[(currentDealer.ordinal() + 1) % seats.length];
        System.out.println("下一局莊家: " + getCurrentDealer().getName());
    }

    // 處理冧莊（連莊）
    public void handleConsecutiveWin(boolean isDealerWin) {
        if (isDealerWin) {
            consecutiveWins++;
            System.out.println("冧莊！連莊次數: " + consecutiveWins);
        } else {
            consecutiveWins = 0;
            rotateSeats();
        }
    }

    // 開始新一局遊戲
    public void startNewGame(boolean isDealerWin) {
        handleConsecutiveWin(isDealerWin);
        System.out.println("新一局開始，莊家: " + getCurrentDealer().getName());
    }

    public static void main(String[] args) {
        HKMJ game = new HKMJ();

        // 創建5個玩家
        game.addPlayer(game.new Player("Player1"));
        game.addPlayer(game.new Player("Player2"));
        game.addPlayer(game.new Player("Player3"));
        game.addPlayer(game.new Player("Player4"));
        game.addPlayer(game.new Player("Player5"));  // 候補玩家

        // 正常遊戲
        game.startNewGame(true);  // 莊家贏

        // 替換玩家（替換東位玩家）
        boolean success = game.replacePlayer(Seat.EAST, "Player5");
        if (success) {
            System.out.println("已更換東位玩家為 Player5");
        }

        // 繼續遊戲
        game.startNewGame(false); // 莊家輸
    }
}