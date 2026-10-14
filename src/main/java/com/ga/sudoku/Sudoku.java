package com.ga.sudoku;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
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
