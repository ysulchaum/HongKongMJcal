package edu.cuhk.csci3310;
// Yu Sui Chung 1155177344
// Wong Tin Po 1155177337
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EnterFaanActivity extends AppCompatActivity {
    HKMJ game = HKMJ.getInstance();
    private List<String> selectedTiles = new ArrayList<>();
    private FlexboxLayout playerHandLayout;
    private FlexboxLayout faanInfoLayout;
    private String roundDir = "dir1"; // for testing
    private String selfDir = "dir1";
    private int calculatedFaan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enter_faan);

        playerHandLayout = findViewById(R.id.playerHand);

        // update current seat
        selfDir = SeatToDir(game.getCurrentEat());
        roundDir = SeatToDir(game.getCurrentRoundSeat());

        // Set click listener for Calculate button
        Button btnCalculate = findViewById(R.id.btnCalculate);
        btnCalculate.setOnClickListener(v -> calculateFaan(roundDir,selfDir));

        Button btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> deleteTile());

        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(v -> clearTile());

        Button btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(v -> confirmFaan());

        // Make sure the app's content is not drawn under the system bars
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(0, insets.top, 0, insets.bottom);
            return windowInsets;
        });

        Button mainPageButton = findViewById(R.id.btnCancel);
        if (mainPageButton != null) {
            mainPageButton.setOnClickListener(v -> finish());
        } else {
            Log.w("RecordActivity", "mainPage button not found in layout");
        }

        tileBtn();

    }
    // Remove the static variable and the code in onCreate that sets the result prematurely:
// Delete this line in onCreate:
// setResult(RESULT_OK, resultIntent);

    // Update confirmFaan() to set the result:
    private void confirmFaan() {
        game.putHandFaan(calculatedFaan);
        finish();
    }
    private void tileBtn(){
        LinearLayout imageContainerDot = findViewById(R.id.imageContainerDot); // Replace with your LinearLayout ID
        if (imageContainerDot != null) {
            addImageButtons(imageContainerDot, "d", 9);
        }

        LinearLayout imageContainerBamboo = findViewById(R.id.imageContainerBamboo); // Replace with your LinearLayout ID
        if (imageContainerBamboo != null) {
            addImageButtons(imageContainerBamboo, "b", 9);
        }

        LinearLayout imageContainerCash = findViewById(R.id.imageContainerCash); // Replace with your LinearLayout ID
        if (imageContainerCash != null) {
            addImageButtons(imageContainerCash, "c", 9);
        }

        LinearLayout imageContainerDir = findViewById(R.id.imageContainerDir); // Replace with your LinearLayout ID
        if (imageContainerDir != null) {
            addImageButtons(imageContainerDir, "dir", 4);
        }

        LinearLayout imageContainerSpecial = findViewById(R.id.imageContainerSpecial); // Replace with your LinearLayout ID
        if (imageContainerSpecial != null) {
            addImageButtons(imageContainerSpecial, "special", 3);
        }

        LinearLayout imageContainerFlower = findViewById(R.id.imageContainerFlower); // Replace with your LinearLayout ID
        if (imageContainerFlower != null) {
            addImageButtons(imageContainerFlower, "flower", 4);
        }

        LinearLayout imageContainerSeason = findViewById(R.id.imageContainerSeason); // Replace with your LinearLayout ID
        if (imageContainerSeason != null) {
            addImageButtons(imageContainerSeason, "season", 4);
        }
    }

    private void deleteTile(){
        if (!selectedTiles.isEmpty() && playerHandLayout.getChildCount() > 0) {
            // Remove the last item from the selectedTiles list
            selectedTiles.remove(selectedTiles.size() - 1);

            // Remove the last ImageView from the layout
            playerHandLayout.removeViewAt(playerHandLayout.getChildCount() - 1);
        }
    }

    private void clearTile(){
        // Clear the selectedTiles list
        selectedTiles.clear();

        // Remove all views from the playerHandLayout
        playerHandLayout.removeAllViews();
    }

    private void addImageButtons(LinearLayout container, String sName, int num) {
        float density = getResources().getDisplayMetrics().density;
        int sizeInPx = (int) (45 * density); // Convert 100dp to pixels

        for (int i = 1; i <= num; i++) {
            String imageName = sName + i;
            ImageButton imageButton = new ImageButton(this);

            // Set ImageButton ID (e.g., R.id.d1, R.id.d2, ...)
            int buttonId = getResources().getIdentifier(imageName, "id", getPackageName());
            imageButton.setId(buttonId);

            // Load the image resource (e.g., R.drawable.d1)
            @SuppressLint("DiscouragedApi")
            int drawableId = getResources().getIdentifier(imageName, "drawable", getPackageName());
            if (drawableId != 0) {
                imageButton.setImageResource(drawableId);
            } else {
                Log.e("ImageError", "Image not found: " + imageName);
            }

            // Layout parameters
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizeInPx, sizeInPx);
            params.setMargins(0, 20, 0, 20);
            imageButton.setLayoutParams(params);
            imageButton.setPadding(0, 0, 0, 0);
            imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageButton.setBackgroundColor(Color.TRANSPARENT); // Remove default button background

            // Optional: Add click listeners
            imageButton.setOnClickListener(v -> {
                Log.d("ButtonClick", "Clicked: " + imageName);
                Toast.makeText(getApplicationContext(), "Clicked: " + imageName, Toast.LENGTH_SHORT).show();

            });

            container.addView(imageButton);


            imageButton.setOnClickListener(v -> {
                // Add tile to selected list and UI
                selectedTiles.add(imageName);
                ImageView tileImage = new ImageView(this);
                tileImage.setImageResource(drawableId);
                playerHandLayout.addView(tileImage);
            });
        }
    }

    private void calculateFaan(String roundDir, String selfDir) {
        int faan = 0;

        // 平胡检查
        if (isPingHu(selectedTiles)) {
            faan += 1;
            Toast.makeText(this, "All Sequences", Toast.LENGTH_SHORT).show();
        }

        // 無花检查
        boolean hasFlowerOrSeason = selectedTiles.stream().anyMatch(t -> t.startsWith("flower") || t.startsWith("season"));
        if (!hasFlowerOrSeason) faan += 1;

        // 正花检查（花牌和季节牌各加1番）---------------
        int zhengHuaCount = countZhengHua(selectedTiles, selfDir);
        faan += zhengHuaCount;
        if (zhengHuaCount > 0) {
            String msg = "正花 (" + zhengHuaCount + " 番)";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }

        // 小三元/中發白检查
        DragonCheckResult dragonResult = checkDragons(selectedTiles);
        faan += dragonResult.faan;
        if (dragonResult.isSanYuan) {
            Toast.makeText(this, "Three Dragons", Toast.LENGTH_SHORT).show();
        }

        // 混一色检查
        String suitType = getDominantSuit(selectedTiles);
        if (isMixedOneSuit(selectedTiles, suitType)) {
            faan += 3;
        }

        // 对对胡检查
        if (isDuiDuiHu(selectedTiles)) {
            faan += 3;
            Toast.makeText(this, "Dui Dui Hu", Toast.LENGTH_SHORT).show();
        }


        // Example: 清一色 (Pure One Suit)
        boolean isPureOneSuit = !suitType.isEmpty() &&
                selectedTiles.stream().noneMatch(t -> t.startsWith("dir") || t.startsWith("special")) &&
                selectedTiles.stream().filter(t -> t.matches("^[dbc].*")).allMatch(t -> t.startsWith(suitType));
        if (isPureOneSuit) faan += 7;

        // 十三么 (Thirteen Orphans) - 7 faan
        if (isThirteenOrphans(selectedTiles)) {
            faan = 10;
            Toast.makeText(this, "Thirteen Orphans", Toast.LENGTH_SHORT).show();
        }

        // 四喜/ winds
        WindCheckResult windResult = checkWinds(selectedTiles, roundDir, selfDir);
        faan += windResult.faan;
        if (windResult.hasMenFeng) {
            Toast.makeText(this, "門風刻子", Toast.LENGTH_SHORT).show();
        }
        if (windResult.hasQuanFeng) {
            Toast.makeText(this, "圈風刻子", Toast.LENGTH_SHORT).show();
        }

        // 小四喜检查
        if (isXiaoSiXi(selectedTiles)) {
            faan += 10; // 爆棚上限值根据规则调整
            Toast.makeText(this, "小四喜", Toast.LENGTH_SHORT).show();
        }

        // 大四喜检查
        if (isDaSiXi(selectedTiles)) {
            faan += 10; // 爆棚上限值
            Toast.makeText(this, "大四喜", Toast.LENGTH_SHORT).show();
        }


        // 限制faan上限
        if(faan > 10){
            calculatedFaan = 10;
        } else if (faan < 3) { // prevent error
            calculatedFaan = faan;
            Toast.makeText(this, "faan < 3 not enough to eat", Toast.LENGTH_SHORT).show();
        }else {
            calculatedFaan = faan;
        }

        TextView tvResult = findViewById(R.id.tvResult);
        tvResult.setText("Total Faan: " + calculatedFaan); // Use calculatedFaan here
    }
    private String SeatToDir(HKMJ.Seat seat) {
        switch (seat) {
            case EAST: return "dir1";
            case SOUTH: return "dir2";
            case WEST: return "dir3";
            case NORTH: return "dir4";
            default: return null;
        }
    }

    private boolean isThirteenOrphans(List<String> tiles) {
        Set<String> required = new HashSet<>(Arrays.asList(
                "d1", "d9", "b1", "b9", "c1", "c9", "dir1", "dir2", "dir3", "dir4",
                "special1", "special2", "special3"
        ));

        // Must have exactly 14 tiles
        if (tiles.size() != 14) return false;

        // Count occurrences of each tile
        Map<String, Integer> counts = new HashMap<>();
        for (String tile : tiles) {
            counts.put(tile, counts.getOrDefault(tile, 0) + 1);
        }

        // Check all required tiles are present at least once
        for (String reqTile : required) {
            if (!counts.containsKey(reqTile) || counts.get(reqTile) < 1) {
                return false;
            }
        }

        // Check for exactly one pair (count=2) and no higher counts
        int pairCount = 0;
        for (int count : counts.values()) {
            if (count > 2) return false; // No triplets or more allowed
            if (count == 2) pairCount++;
        }

        // Must have exactly one pair and 13 unique tiles from required set
        return pairCount == 1 && counts.size() == 13 && counts.keySet().stream().allMatch(required::contains);
    } //  十三么

    private boolean isPingHu(List<String> tiles) {
        // Filter out flower and season tiles
        List<String> coreTiles = tiles.stream()
                .filter(t -> !t.startsWith("flower") && !t.startsWith("season"))
                .collect(Collectors.toList());

        if (coreTiles.size() != 14) return false;

        if (coreTiles.stream().anyMatch(t -> t.startsWith("dir") || t.startsWith("special"))) {
            return false;
        }

        Map<String, Integer> tileCounts = new HashMap<>();
        for (String tile : coreTiles) {
            tileCounts.put(tile, tileCounts.getOrDefault(tile, 0) + 1);
        }

        for (String potentialPair : tileCounts.keySet()) {
            if (tileCounts.get(potentialPair) >= 2) {
                List<String> remainingTiles = new ArrayList<>(coreTiles);
                remainingTiles.remove(potentialPair);
                remainingTiles.remove(potentialPair);

                if (isValidAllSequences(remainingTiles)) {
                    Log.d("PingHu", "Valid pair: " + potentialPair);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isValidAllSequences(List<String> tiles) {
        if (tiles.size() % 3 != 0) return false;

        Map<String, List<Integer>> tileGroups = new HashMap<>();
        for (String tile : tiles) {
            if (tile.matches("^[dbc].*")) {
                String type = tile.substring(0, 1);
                int number = Integer.parseInt(tile.substring(1));
                tileGroups.computeIfAbsent(type, k -> new ArrayList<>()).add(number);
            }
        }

        for (List<Integer> group : tileGroups.values()) {
            if (!isValidSequenceGroup(group)) return false;
        }
        return true;
    }

    private boolean isValidSequenceGroup(List<Integer> numbers) {
        if (numbers.size() % 3 != 0) return false;

        List<Integer> remaining = new ArrayList<>(numbers);
        Collections.sort(remaining);

        while (!remaining.isEmpty()) {
            if (remaining.size() < 3) return false;
            int a = remaining.get(0);
            if (!remaining.contains(a + 1) || !remaining.contains(a + 2)) return false;
            remaining.remove(Integer.valueOf(a));
            remaining.remove(Integer.valueOf(a + 1));
            remaining.remove(Integer.valueOf(a + 2));
        }
        return true;
    }

    private boolean isDuiDuiHu(List<String> tiles) {
        // Filter out flower and season tiles
        List<String> coreTiles = tiles.stream()
                .filter(t -> !t.startsWith("flower") && !t.startsWith("season"))
                .collect(Collectors.toList());

        if (coreTiles.size() != 14) return false;

        Map<String, Integer> tileCounts = new HashMap<>();
        for (String tile : coreTiles) {
            tileCounts.put(tile, tileCounts.getOrDefault(tile, 0) + 1);
        }

        int pairCount = 0;
        int pungCount = 0;

        for (int count : tileCounts.values()) {
            if (count == 2) pairCount++;
            else if (count >= 3) pungCount++; // Count Pungs or Kongs (Kongs treated as Pungs here)
            else return false; // Any count of 1 means it’s not All Pungs
        }

        // Must have exactly 1 pair and 4 Pungs/Kongs
        boolean isValid = pairCount == 1 && pungCount == 4;
        if (isValid) {
            Log.d("DuiDuiHu", "Valid Dui Dui Hu detected");
        }
        return isValid;
    }

    // 封装三元牌检查逻辑
    private DragonCheckResult checkDragons(List<String> tiles) {
        Map<String, Long> dragonCounts = tiles.stream()
                .filter(t -> t.startsWith("special"))
                .collect(Collectors.groupingBy(t -> t, Collectors.counting()));

        // 小三元检查
        int triplets = 0, pair = 0;
        for (Long count : dragonCounts.values()) {
            if (count >= 3) triplets++;
            else if (count >= 2) pair++;
        }
        boolean isXiaoSanYuan = (triplets == 2 && pair >= 1);

        // 大三元检查
        int triplets2 = 0, pair2 = 0;
        for (Long count : dragonCounts.values()) {
            if (count >= 3) triplets2++;
        }
        boolean isDaSanYuan = (triplets == 3);

        // 返回结果对象
        if (isDaSanYuan){
            return new DragonCheckResult(8, true); // 大三元
        } else if (isXiaoSanYuan) {
            return new DragonCheckResult(5, true); // 小三元成立时固定加5番
        } else {
            // 普通中發白刻子计算
            long normalTriplets = dragonCounts.values().stream()
                    .filter(count -> count >= 3)
                    .count();
            return new DragonCheckResult((int) normalTriplets, false);
        }
    }

    // 辅助类用于返回复合结果
    private static class DragonCheckResult {
        int faan;
        boolean isSanYuan;

        DragonCheckResult(int faan, boolean isSanYuan) {
            this.faan = faan;
            this.isSanYuan = isSanYuan;
        }
    }

    // 提取花色判断逻辑
    private String getDominantSuit(List<String> tiles) {
        return tiles.stream()
                .filter(t -> t.matches("^[dbc].*"))
                .findFirst()
                .map(t -> t.substring(0, 1))
                .orElse("");
    }

    // 混一色检查封装
    private boolean isMixedOneSuit(List<String> tiles, String suitType) {
        return !suitType.isEmpty() &&
                tiles.stream()
                        .filter(t -> t.matches("^[dbc].*"))
                        .allMatch(t -> t.startsWith(suitType)) &&
                tiles.stream().anyMatch(t -> t.startsWith("dir") || t.startsWith("special"));
    }

    // 风牌检查结果包装类
    private static class WindCheckResult {
        int faan;
        boolean hasMenFeng;
        boolean hasQuanFeng;

        WindCheckResult(int faan, boolean hasMenFeng, boolean hasQuanFeng) {
            this.faan = faan;
            this.hasMenFeng = hasMenFeng;
            this.hasQuanFeng = hasQuanFeng;
        }
    }

    // 門風/圈風检查
    private WindCheckResult checkWinds(List<String> tiles, String roundWind, String selfWind) {
        int windFaan = 0;
        boolean hasMenFeng = false;
        boolean hasQuanFeng = false;

        // 统计所有风牌数量
        Map<String, Long> windCounts = tiles.stream()
                .filter(t -> t.startsWith("dir"))
                .collect(Collectors.groupingBy(t -> t, Collectors.counting()));

        // 检查門風刻子
        String menFeng = "dir" + selfWind.charAt(3); // selfWind格式如"dir1"
        if (windCounts.getOrDefault(menFeng, 0L) >= 3) {
            windFaan += 1;
            hasMenFeng = true;
        }

        // 检查圈風刻子
        String quanFeng = "dir" + roundWind.charAt(3); // roundWind格式如"dir2"
        if (windCounts.getOrDefault(quanFeng, 0L) >= 3) {
            windFaan += 1;
            hasQuanFeng = true;
        }

        return new WindCheckResult(windFaan, hasMenFeng, hasQuanFeng);
    }

    // 小四喜检查（三刻+一对）
    private boolean isXiaoSiXi(List<String> tiles) {
        Map<String, Long> windCounts = tiles.stream()
                .filter(t -> t.startsWith("dir"))
                .collect(Collectors.groupingBy(t -> t, Collectors.counting()));

        int triplets = 0;
        int pair = 0;

        for (Long count : windCounts.values()) {
            if (count >= 3) triplets++;
            else if (count >= 2) pair++;
        }

        return triplets == 3 && pair >= 1;
    }

    // 大四喜检查（四刻）
    private boolean isDaSiXi(List<String> tiles) {
        Map<String, Long> windCounts = tiles.stream()
                .filter(t -> t.startsWith("dir"))
                .collect(Collectors.groupingBy(t -> t, Collectors.counting()));

        return windCounts.values().stream()
                .filter(count -> count >= 3)
                .count() == 4;
    }

    // 正花计数逻辑：返回匹配的花牌+季节牌数量（0~2）
    private int countZhengHua(List<String> tiles, String selfWind) {
        // 1. 获取門風对应的花牌编号（東=1, 南=2, 西=3, 北=4）
        int expectedNumber = Integer.parseInt(selfWind.replace("dir", ""));

        // 2. 检查是否包含匹配的flower和season
        boolean hasFlower = tiles.stream()
                .filter(t -> t.startsWith("flower"))
                .anyMatch(t -> getTileNumber(t) == expectedNumber);

        boolean hasSeason = tiles.stream()
                .filter(t -> t.startsWith("season"))
                .anyMatch(t -> getTileNumber(t) == expectedNumber);

        // 3. 每个匹配类型加1番（最多2番）
        return (hasFlower ? 1 : 0) + (hasSeason ? 1 : 0);
    }

    // 辅助方法：从牌名中提取数字（例如 flower3 → 3）
    private int getTileNumber(String tileName) {
        try {
            return Integer.parseInt(tileName.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return -1; // 非法牌名处理
        }
    }




}


