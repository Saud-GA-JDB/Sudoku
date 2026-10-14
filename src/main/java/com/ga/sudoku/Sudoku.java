package com.ga.sudoku;

import lombok.Data;

import java.nio.file.Path;
import java.nio.file.Paths;

@Data
public class Sudoku {
    private final static Path puzzleDir = Paths.get("puzzles").resolve("puzzle1.txt");
}
