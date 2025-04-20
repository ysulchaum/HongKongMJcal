package edu.cuhk.csci3310;
// Yu Sui Chung 1155177344
// Wong Tin Po 1155177337

import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


// 主遊戲類：香港麻將遊戲
public class HKMJ {
    private static HKMJ instance;  // 单例实例

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
    private int tempHandFaan = 0;
    public Seat currentDealer = Seat.EAST;   // 當前莊家座位
    public Seat currentEat = Seat.EAST;
    public Seat currentRoundSeat = Seat.EAST;

    // 遊戲構造函數
    public HKMJ() {
        // 初始化座位映射表
        seatMap.put(Seat.EAST, null);   // 東位初始化為空
        seatMap.put(Seat.SOUTH, null);  // 南位初始化為空
        seatMap.put(Seat.WEST, null);   // 西位初始化為空
        seatMap.put(Seat.NORTH, null);  // 北位初始化為空

        consecutiveWins = 0;  // 初始化連莊次數為0
    }

    // Override resetInstance to delete the file if needed resetInstance
    public static void resetInstance(File baseDirectory) {
        File directory = new File(baseDirectory, SUBDIRECTORY);
        File file = new File(directory, GAME_STATE_FILE);
        if (file.exists()) file.delete();
        instance = null;
        instance = new HKMJ();
    }
    public static HKMJ getInstance() { //确保游戏数据通过单例持久化
        if (instance == null) {
            instance = new HKMJ();
        }
        return instance;
    }
    // who enter hand faan
    public void putHandFaan(int faan){
        tempHandFaan = faan;
    }

    public int getHandFaan(){
        return tempHandFaan;
    }

    // who press the eat panel
    public void putCurrentEat(Seat seat){
        currentEat = seat;
    }

    public Seat getCurrentEat(){
        return currentEat;
    }

    public Seat getCurrentRoundSeat(){
        return currentRoundSeat;
    }

    public void setConsecutiveWins(int con){
        consecutiveWins = con;
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

    public boolean nextRoundSeat(){
        Seat[] seats = Seat.values();  // 獲取所有座位枚舉值
        // 計算下一個莊家座位索引
        if (currentDealer == Seat.EAST && consecutiveWins == 0){
            int nextIndex = (currentRoundSeat.ordinal() + 1) % seats.length;
            currentRoundSeat = seats[nextIndex];
            return true;
        }
        return false;
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
    // 新增方法：通过玩家名称获取座位
    public Seat getSeatByPlayerName(String playerName) {
        for (Map.Entry<Seat, Player> entry : seatMap.entrySet()) {
            Player player = entry.getValue();
            if (player != null && player.getName().equals(playerName)) {
                return entry.getKey();
            }
        }
        return null; // 未找到返回null
    }

    // Method to get the player's name by their seat
    public String getPlayerNameBySeat(Seat seat) {
        Player player = seatMap.get(seat); // Get the player assigned to the specified seat
        return player != null ? player.getName() : null; // Return null if no player is found
    }


    // Method to add score by player name
    // Method to add score by seat
    public boolean addScoreBySeat(Seat seat, int points) {
        Player player = seatMap.get(seat); // Get the player assigned to the specified seat
        if (player != null) {
            player.addScore(points); // Add points to the player's score
            return true; // Operation successful
        }
        return false; // No player found in the specified seat
    }

    // Method to get the score of a player by their seat
    public int getScoreBySeat(Seat seat) {
        Player player = seatMap.get(seat); // Get the player assigned to the specified seat
        if (player != null) {
            return player.getScore(); // Return the player's score
        }
        throw new IllegalArgumentException("No player found at the specified seat.");
    }



    //
//    // 開始新一局遊戲
//    public void startNewGame(boolean isDealerWin) {
//        handleConsecutiveWin(isDealerWin);  // 處理連莊狀態
//        System.out.println("新一局開始，莊家: " + getCurrentDealer().getName());
//    }
// 新增枚举：赢的类型
    enum WinType {
        SELF_DRAW,   // 自摸
        EAT_PLAYER,   // 吃胡
        DRAW,   // Draw
    }
    // 新增回合记录类
    class Round {
        private final Seat winnerSit; // winner's seat
        private Seat roundSit;
        private String winner;
        private WinType winType;
        private List<String> losers;
        private Map<String, Integer> scoreChanges;
        private Seat dealerSeat;
        private int dealerConsecutiveWins;
        private Seat RoundSeat;

        public Round(String winner, WinType winType, List<String> losers, Map<String, Integer> scoreChanges) {
            this.winner = winner;
            this.winType = winType;
            this.losers = new ArrayList<>(losers);
            this.scoreChanges = new HashMap<>(scoreChanges);
            this.dealerSeat = currentDealer;
            this.dealerConsecutiveWins = consecutiveWins;
            this.RoundSeat = currentRoundSeat;
            this.winnerSit = getSeatByPlayerName(winner);
            this.roundSit = currentRoundSeat;
        }

        // Getter方法
        public String getWinner() { return winner; }
        public WinType getWinType() { return winType; }
        public Seat getWinSit() { return winnerSit; }
        public Seat getRoundSit() { return roundSit; }
        public int getDealerConsecutiveWins() { return dealerConsecutiveWins; }
        public List<String> getLosers() { return new ArrayList<>(losers); }
        public Map<String, Integer> getScoreChanges() { return new HashMap<>(scoreChanges); }


        // 新增方法：判断是否是出銃局
//            public boolean isPlayerLoseRound() {
//                return winType == WinType.PLAYER_LOSE;
//            }
    }

    // 新增游戏记录管理类
    class GameRecord {
        private List<Round> rounds = new ArrayList<>();

        public void addRound(Round round) {
            rounds.add(round);
        }

        // for update to the last total score and pop the newest round
        public Round pop() {
            if (rounds.isEmpty()) return null;

            // 获取最后一个回合记录
            Round lastRound = rounds.get(rounds.size() - 1);

            // 逆向计算分数
            Map<String, Integer> scoreChanges = lastRound.getScoreChanges();
            for (Map.Entry<String, Integer> entry : scoreChanges.entrySet()) {
                String playerName = entry.getKey();
                int points = entry.getValue();

                // 获取玩家座位
                Seat seat = getSeatByPlayerName(playerName);
                if (seat != null) {
                    // 逆向操作：减去原来的分数变化量
                    addScoreBySeat(seat, -points);
                }
            }
            currentDealer = lastRound.dealerSeat;
            currentRoundSeat = lastRound.RoundSeat;

            // 移除并返回最后一个回合
            return rounds.remove(rounds.size() - 1);
        }

        public int getLoseCount(String playerName) {
            return (int) rounds.stream()
                    .filter(r -> r.getWinType() == WinType.EAT_PLAYER)  // Filter for EAT_PLAYER rounds
                    .filter(r -> r.getLosers().contains(playerName))    // Check if player is a loser
                    .count();
        }

        // 保持其他方法不变
        public int getSelfDrawCount(String playerName) {
            return (int) rounds.stream()
                    .filter(r -> r.getWinner().equals(playerName))
                    .filter(r -> r.getWinType() == WinType.SELF_DRAW)
                    .count();
        }

        public int getEatPlayerCount(String playerName) {
            return (int) rounds.stream()
                    .filter(r -> r.getWinner().equals(playerName))
                    .filter(r -> r.getWinType() == WinType.EAT_PLAYER)
                    .count();
        }

//        public List<Round> getAllRounds() {
//            return new ArrayList<>(rounds);
//        }
    }

    // 在HKMJ类中添加以下字段
    private GameRecord gameRecord = new GameRecord();

    // 添加新方法用于记录回合
    public void recordRound(String winner, WinType winType, List<String> losers, Map<String, Integer> scoreChanges) {

        gameRecord.addRound(new Round(winner, winType, losers, scoreChanges));
    }

    // 添加访问游戏记录的方法
    public GameRecord getGameRecord() {
        return gameRecord;
    }

    public Round popGameRecord() {
        // 移除并返回最后一个回合
        return gameRecord.pop();
    }

    public List<Round> gameList() {
        // 移除并返回最后一个回合
        return gameRecord.rounds;
    }

    // for calculate who pay who *********************************
    public static class Transaction{
        String fromPlayer;
        String toPlayer;
        int amount;

        public Transaction(String from, String to, int amount) {
            this.fromPlayer = from;
            this.toPlayer = to;
            this.amount = amount;
        }

        @Override
        public String toString() {
            return fromPlayer + " pays " + toPlayer + " $" + amount;
        }
    }

    // Calculate smart transitions for settlement
    public List<Transaction> calculateSettlement() {
        List<Transaction> transactions = new ArrayList<>();

        // Create copies of scores to avoid modifying real data
        Map<Player, Integer> scoreCopies = new HashMap<>();
        for (Map.Entry<Seat, Player> entry : seatMap.entrySet()) {
            Player p = entry.getValue();
            if (p != null) {
                scoreCopies.put(p, p.getScore());
            }
        }

        // Separate creditors (positive) and debtors (negative) using copied scores
        List<Player> creditors = new ArrayList<>();
        List<Player> debtors = new ArrayList<>();
        for (Player p : scoreCopies.keySet()) {
            int score = scoreCopies.get(p);
            if (score > 0) creditors.add(p);
            else if (score < 0) debtors.add(p);
        }

        // Sort creditors (highest first) and debtors (lowest first)
        creditors.sort((a, b) -> Integer.compare(scoreCopies.get(b), scoreCopies.get(a)));
        debtors.sort(Comparator.comparingInt(scoreCopies::get));

        int i = 0, j = 0;
        while (i < creditors.size() && j < debtors.size()) {
            Player creditor = creditors.get(i);
            Player debtor = debtors.get(j);

            int remainingCredit = scoreCopies.get(creditor);
            int remainingDebt = -scoreCopies.get(debtor); // Convert to positive

            if (remainingCredit == 0) {
                i++;
                continue;
            }
            if (remainingDebt == 0) {
                j++;
                continue;
            }

            int amount = Math.min(remainingCredit, remainingDebt);
            transactions.add(new Transaction(debtor.getName(), creditor.getName(), amount));

            // Update copied scores (no UI/class data modified)
            scoreCopies.put(creditor, remainingCredit - amount);
            scoreCopies.put(debtor, scoreCopies.get(debtor) + amount);

            if (scoreCopies.get(creditor) == 0) i++;
            if (scoreCopies.get(debtor) == 0) j++;
        }

        return transactions;
    }
// ******************************************************
// File name for saving game state
    // Define the file name and subdirectory
    private static final String SUBDIRECTORY = "edu.cuhk.csci3310/files";
    private static final String GAME_STATE_FILE = "game_state.json";

    // Save game state to JSON file
    public void saveToFile(File baseDirectory) {
        try {
            // Create the subdirectory if it doesn’t exist
            File directory = new File(baseDirectory, SUBDIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs(); // Creates the directory structure if it doesn’t exist
            }


            JSONObject json = new JSONObject();

            // Save players and scores
            JSONObject playersJson = new JSONObject();
            for (Seat seat : Seat.values()) {
                Player player = seatMap.get(seat);
                if (player != null) {
                    JSONObject playerJson = new JSONObject();
                    playerJson.put("name", player.getName());
                    playerJson.put("score", player.getScore());
                    playerJson.put("isActive", player.isActive());
                    playersJson.put(seat.name(), playerJson);
                }
            }
            json.put("players", playersJson);

            // Save game state
            json.put("currentDealer", currentDealer.name());
            json.put("currentRoundSeat", currentRoundSeat.name());
            json.put("consecutiveWins", consecutiveWins);
            json.put("tempHandFaan", tempHandFaan);

            // Save game records
            JSONArray roundsJson = new JSONArray();
            for (Round round : gameRecord.rounds) {
                JSONObject roundJson = new JSONObject();
                roundJson.put("winner", round.getWinner());
                roundJson.put("winType", round.getWinType().name());
                roundJson.put("roundSit", round.getRoundSit().name());
                JSONArray losersJson = new JSONArray(round.getLosers());
                roundJson.put("losers", losersJson);

                JSONObject scoreChangesJson = new JSONObject();
                for (Map.Entry<String, Integer> entry : round.getScoreChanges().entrySet()) {
                    scoreChangesJson.put(entry.getKey(), entry.getValue());
                }
                roundJson.put("scoreChanges", scoreChangesJson);
                roundsJson.put(roundJson);
            }
            json.put("rounds", roundsJson);

            // Write to file
            File file = new File(directory, GAME_STATE_FILE);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(json.toString().getBytes());
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    // Load game state from JSON file
    public void loadFromFile(File baseDirectory) {
        File directory = new File(baseDirectory, SUBDIRECTORY);
        File file = new File(directory, GAME_STATE_FILE);
        if (!file.exists()) return; // No saved state
        try {
            // Read file content
            StringBuilder content = new StringBuilder();
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    content.append(new String(buffer, 0, bytesRead));
                }
            }

            JSONObject json = new JSONObject(content.toString());

            // Load players
            seatMap.clear();
            allPlayers.clear();
            JSONObject playersJson = json.getJSONObject("players");
            for (Seat seat : Seat.values()) {
                if (playersJson.has(seat.name())) {
                    JSONObject playerJson = playersJson.getJSONObject(seat.name());
                    Player player = new Player(playerJson.getString("name"));
                    player.addScore(playerJson.getInt("score"));
                    player.setActive(playerJson.getBoolean("isActive"));
                    seatMap.put(seat, player);
                    allPlayers.add(player);
                } else {
                    seatMap.put(seat, null);
                }
            }

            // Load game state
            currentDealer = Seat.valueOf(json.getString("currentDealer"));
            currentRoundSeat = Seat.valueOf(json.getString("currentRoundSeat"));
            consecutiveWins = json.getInt("consecutiveWins");
            tempHandFaan = json.getInt("tempHandFaan");

            // Load rounds
            gameRecord.rounds.clear();
            JSONArray roundsJson = json.getJSONArray("rounds");
            for (int i = 0; i < roundsJson.length(); i++) {
                JSONObject roundJson = roundsJson.getJSONObject(i);
                String winner = roundJson.getString("winner");
                WinType winType = WinType.valueOf(roundJson.getString("winType"));

                JSONArray losersJson = roundJson.getJSONArray("losers");
                List<String> losers = new ArrayList<>();
                for (int j = 0; j < losersJson.length(); j++) {
                    losers.add(losersJson.getString(j));
                }

                JSONObject scoreChangesJson = roundJson.getJSONObject("scoreChanges");
                Map<String, Integer> scoreChanges = new HashMap<>();
                Iterator<String> keys = scoreChangesJson.keys(); // Use keys() instead of keySet()
                while (keys.hasNext()) {
                    String key = keys.next();
                    scoreChanges.put(key, scoreChangesJson.getInt(key));
                }

                gameRecord.addRound(new Round(winner, winType, losers, scoreChanges));
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }





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

        Map<String, Integer> scoreChanges = new HashMap<>();
        scoreChanges.put("Player1", 6);
        scoreChanges.put("Player2", -2);
        scoreChanges.put("Player3", -2);
        scoreChanges.put("Player4", -2);
        game.recordRound("Player1", WinType.SELF_DRAW,
                Arrays.asList("Player2", "Player3","player4"), scoreChanges);

        // 查询统计数据
        int selfDraws = game.getGameRecord().getSelfDrawCount("Player1");
    }
} // 類定義結束