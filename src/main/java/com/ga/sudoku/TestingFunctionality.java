package com.ga.sudoku;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TestingFunctionality {
    private Sudoku soduku;

    @GetMapping("/getGrid")
    public int[] getGrid() {
        soduku.populateGrid();
        return soduku.getFullGrid();
    }

}
