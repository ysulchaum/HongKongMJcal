// 包聲明，指定類所屬的包
package edu.cuhk.csci3310;

// 導入所需的Java工具類
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 主遊戲類：香港麻將遊戲
public class HKMJ {

    // 座位枚舉，定義四個遊戲座位方向
    enum Seat {
        EAST,   // 東位
        SOUTH,  // 南位
        WEST,   // 西位
        NORTH   // 北位
    }

    // 內部類：玩家類
    class Player {
        // 玩家屬性
        private String name;    // 玩家名稱
        private int score;      // 玩家當前分數
        private boolean isActive; // 是否在遊戲中

        // 玩家構造函數
        public Player(String name) {
            this.name = name;    // 初始化玩家名稱
            this.score = 0;      // 初始分數設為0
            this.isActive = false; // 默認非激活狀態
        }

        // Getter方法：獲取玩家名稱
        public String getName() { return name; }
        // Getter方法：獲取玩家分數
        public int getScore() { return score; }
        // 檢查玩家是否處於激活狀態
        public boolean isActive() { return isActive; }
        // 設置玩家激活狀態
        public void setActive(boolean active) { isActive = active; }
        // 增加玩家分數
        public void addScore(int points) { score += points; }
    }

    // 遊戲狀態管理
    private Map<Seat, Player> seatMap = new HashMap<>(); // 座位與玩家的映射
    List<Player> allPlayers = new ArrayList<>(); // 所有玩家列表（包含候補）
    private int consecutiveWins;  // 連莊次數計數器
    public Seat currentDealer = Seat.EAST;   // 當前莊家座位

    // 遊戲構造函數
    public HKMJ() {
        // 初始化座位映射表
        seatMap.put(Seat.EAST, null);   // 東位初始化為空
        seatMap.put(Seat.SOUTH, null);  // 南位初始化為空
        seatMap.put(Seat.WEST, null);   // 西位初始化為空
        seatMap.put(Seat.NORTH, null);  // 北位初始化為空

        consecutiveWins = 0;  // 初始化連莊次數為0
    }

    // 添加玩家到玩家池
    public void addPlayer(Player player, Seat seat) {
        allPlayers.add(player);  // 將玩家添加到總列表
        seatMap.put(seat, player);  // 將玩家分配到指定座位
        player.setActive(true);    // 設置玩家為激活狀態
    }

    // 替換指定座位的玩家
    public boolean replacePlayer(Seat seat, String newPlayerName) {
        // 在玩家池中尋找符合條件的新玩家
        Player newPlayer = allPlayers.stream()
                .filter(p -> p.getName().equals(newPlayerName) && !p.isActive()) // 名稱匹配且未激活
                .findFirst()
                .orElse(null);  // 找不到返回null

        if (newPlayer != null) {
            // 處理舊玩家
            Player oldPlayer = seatMap.get(seat);  // 獲取原座位玩家
            if (oldPlayer != null) {
                oldPlayer.setActive(false);  // 設置原玩家為非激活
            }

            // 設置新玩家
            seatMap.put(seat, newPlayer);  // 更新座位映射
            newPlayer.setActive(true);     // 激活新玩家
            return true;  // 替換成功
        }
        return false;  // 替換失敗
    }

    // 獲取當前莊家玩家對象
    public Player getCurrentDealer() {
        return seatMap.get(currentDealer);  // 從座位映射中獲取
    }

    // 輪轉座位順序（換風）
    public void rotateSeats() {
        Seat[] seats = Seat.values();  // 獲取所有座位枚舉值
        // 計算下一個莊家座位索引
        int nextIndex = (currentDealer.ordinal() + 1) % seats.length;
        currentDealer = seats[nextIndex];  // 更新當前莊家
        System.out.println("下一局莊家: " + getCurrentDealer().getName());
    }

    // 處理連莊邏輯
    public void handleConsecutiveWin(boolean isDealerWin) {
        if (isDealerWin) {
            consecutiveWins++;  // 增加連莊次數
            //System.out.println("冧莊！連莊次數: " + consecutiveWins);
        } else {
            consecutiveWins = 0;  // 重置連莊計數
            //rotateSeats();        // 輪轉座位
        }
    }
//
//    // 開始新一局遊戲
//    public void startNewGame(boolean isDealerWin) {
//        handleConsecutiveWin(isDealerWin);  // 處理連莊狀態
//        System.out.println("新一局開始，莊家: " + getCurrentDealer().getName());
//    }

    // 主方法（測試用）
    public static void main(String[] args) {
        HKMJ game = new HKMJ();  // 創建遊戲實例

        // 初始化5個玩家（4個激活+1個候補）
        game.addPlayer(game.new Player("Player1"), Seat.EAST);
        game.addPlayer(game.new Player("Player2"), Seat.SOUTH);
        game.addPlayer(game.new Player("Player3"), Seat.WEST);
        game.addPlayer(game.new Player("Player4"), Seat.NORTH);
        game.new Player("Player5");  // 候補玩家

        // 模擬遊戲流程
        game.replacePlayer(Seat.EAST, "Player5");  // 替換東位玩家
    }
} // 類定義結束