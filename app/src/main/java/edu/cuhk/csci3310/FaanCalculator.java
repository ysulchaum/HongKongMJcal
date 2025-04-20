package edu.cuhk.csci3310;
// Yu Sui Chung 1155177344
// Wong Tin Po 1155177337
public class FaanCalculator {

    public static int calculateScore(int fan, int base) {
        // Validate input
        if (fan < 0 || fan > 10) {
            throw new IllegalArgumentException("Fan must be between 0 and 10.");
        }

        if (base != 32 && base != 64 && base != 128) {
            throw new IllegalArgumentException("Base must be 32, 64, or 128.");
        }

        // Define the score table based on the provided data
        double[][] scoreTable = {
                {0.25, 0.5, 1},   // 雞胡 (0 fan)
                {0.5, 1, 2},      // 1 fan
                {1, 2, 4},        // 2 fan
                {2, 4, 8},        // 3 fan
                {4, 8, 16},       // 4 fan
                {6, 12, 24},      // 5 fan
                {8, 16, 32},      // 6 fan
                {12, 24, 48},     // 7 fan
                {16, 32, 64},     // 8 fan
                {24, 48, 96},     // 9 fan
                {32, 64, 128}     // 10 fan
        };

        // Determine the column index based on the base value
        int columnIndex;
        switch (base) {
            case 32:
                columnIndex = 0; // 二五雞
                break;
            case 64:
                columnIndex = 1; // 五一
                break;
            case 128:
                columnIndex = 2; // 一二蚊
                break;
            default:
                throw new IllegalArgumentException("Invalid base value.");
        }

        // Calculate the score
        double score = scoreTable[fan][columnIndex];

        return (int) score;
    }

    public static void main(String[] args) {
        // Test cases
        System.out.println("calculateScore(9, 32) = " + calculateScore(9, 32)); // Expected: 24
        System.out.println("calculateScore(9, 64) = " + calculateScore(9, 64)); // Expected: 48
        System.out.println("calculateScore(8, 128) = " + calculateScore(8, 128)); // Expected: 64
        System.out.println("calculateScore(0, 32) = " + calculateScore(0, 32)); // Expected: 0
        System.out.println("calculateScore(1, 32) = " + calculateScore(1, 32)); // Expected: 0
        System.out.println("calculateScore(10, 128) = " + calculateScore(10, 128)); // Expected: 128
    }
}