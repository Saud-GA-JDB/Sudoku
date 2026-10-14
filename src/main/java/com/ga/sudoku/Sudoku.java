package com.ga.sudoku;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

@Data
@Service
public class Sudoku {
    private final Path filePath;

    private int[] fullGrid;

    public Sudoku() {
        this.filePath = Path.of("puzzles/puzzle1.txt");
        this.fullGrid = new int[81];
    }

    public HashSet<Integer> getRow(HashSet<Integer> set, int elementIndex) {
        int rowStart = (elementIndex / 9) * 9;
        for (int col = 0; col < 9; col++) {
            int value = fullGrid[rowStart + col];
            if (value != 0) {
                set.add(value);
            }
        }
        return set;
    }

    public HashSet<Integer> getCol(HashSet<Integer> set, int elementIndex) {
        int col = elementIndex % 9;
        for (int row = 0; row < 9; row++) {
            int value = fullGrid[row * 9 + col];
            if (value != 0) {
                set.add(value);
            }
        }
        return set;
    }

    public HashSet<Integer> getGrid(HashSet<Integer> set, int elementIndex) {
        int rowStart = (elementIndex / 9 / 3) * 3;
        int colStart = (elementIndex % 9 / 3) * 3;
        for (int row = rowStart; row < rowStart + 3; row++) {
            for (int col = colStart; col < colStart + 3; col++) {
                int value = fullGrid[row * 9 + col];
                if (value != 0) {
                    set.add(value);
                }
            }
        }
        return set;
    }

    public HashSet<Integer> getRelated(HashSet<Integer> set, int elementIndex) {
        getRow(set, elementIndex);
        getCol(set, elementIndex);
        getGrid(set, elementIndex);
        return set;
    }

    public void populateGrid() {
        try (Scanner scanner = new Scanner(filePath)) {
            int index = 0;
            while (scanner.hasNext()) {
                if (scanner.hasNextInt()) {
                    if (index >= fullGrid.length) {
                        throw new IllegalArgumentException("Puzzle contains more than 81 integers.");
                    }
                    fullGrid[index++] = scanner.nextInt();
                } else {
                    scanner.next();
                }
            }
            System.out.println(Arrays.toString(fullGrid));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
