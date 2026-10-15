package com.ga.sudoku;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;

@RestController
@AllArgsConstructor
public class TestingFunctionality {
    private Sudoku soduku;

    @GetMapping("/getGrid")
    public int[] getGrid() {
        soduku.populateGrid();
        return soduku.getFullGrid();
    }

    @GetMapping("/getSet")
    public HashSet<Integer> getSet() {
        HashSet<Integer> set = new HashSet<>();
//        soduku.getCol(set, 2);
//        soduku.getRow(set, 2);
//        soduku.getGrid(set, 2);
        soduku.getRelated(set, 2);
        System.out.println(set);
        return set;
    }

    @GetMapping("/getPencilMarks")
    public BitSet[] getBitSet() {
        soduku.pencilMarkTraverse();
        System.out.println(Arrays.toString(soduku.getPencilMark()));
        return soduku.getPencilMark();
    }

    @GetMapping("/solveEasyPencilMarks")
    public BitSet[] solveEasyPencilMarks() {
        soduku.solveEasyPencilMarks();
        System.out.println(Arrays.toString(soduku.getPencilMark()));
        return soduku.getPencilMark();
    }

    @GetMapping("/seeResult")
    public int[] Results() {
        System.out.println(Arrays.toString(soduku.getFullGrid()));
        return soduku.getFullGrid();
    }

    @GetMapping("/solveMedium")
    public int[] solveMedium() {
        for (int i=0; i<81; i++)
            soduku.solveMedium(i);
        System.out.println(Arrays.toString(soduku.getFullGrid()));
        return soduku.getFullGrid();
    }

    @GetMapping("/terminate")
    public int[] terminate() {
        for (int i=0; i<81; i++) {
            soduku.terminatePossiblePencilMarksCol(i);
            soduku.terminatePossiblePencilMarksRow(i);
        }
        System.out.println(Arrays.toString(soduku.getFullGrid()));
        return soduku.getFullGrid();
    }

    @GetMapping("/solveBacktracking")
    public int[] backtracking() {
        soduku.solveBacktracking();
        System.out.println(Arrays.toString(soduku.getFullGrid()));
        return soduku.getFullGrid();
    }
}
