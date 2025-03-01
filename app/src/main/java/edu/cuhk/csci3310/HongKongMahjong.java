package edu.cuhk.csci3310;

import java.util.ArrayList;
import java.util.List;

public class HongKongMahjong {

    // 定義玩家座位
    enum Seat {
        EAST, SOUTH, WEST, NORTH
    }

    // 定義遊戲狀態
    public Seat currentDealer; // 當前莊家
    private int consecutiveWins; // 連莊次數
    private List<Seat> players; // 玩家座位順序
    private List<Integer> scores; // 玩家得分
    private List<String> playerNames; // 玩家名稱

    public HongKongMahjong() {
        // 初始化遊戲，東風為起始莊家
        currentDealer = Seat.EAST;
        consecutiveWins = 0;
        players = new ArrayList<>();
        players.add(Seat.EAST);
        players.add(Seat.SOUTH);
        players.add(Seat.WEST);
        players.add(Seat.NORTH);
    }

    // 輪轉座位順序
    public void rotateSeats() {
        currentDealer = players.get((players.indexOf(currentDealer) + 1) % players.size());
        System.out.println("下一局莊家: " + currentDealer);
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
        System.out.println("新一局開始，莊家: " + currentDealer);
    }

//    public static void main(String[] args) {
//        HongKongMahjong game = new HongKongMahjong();
//
//        // 模擬幾局遊戲
//        game.startNewGame(true);  // 莊家贏
//    }

}
