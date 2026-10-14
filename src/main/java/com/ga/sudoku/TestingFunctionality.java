package com.ga.sudoku;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
