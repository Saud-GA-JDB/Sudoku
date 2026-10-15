package com.ga.sudoku;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Scanner;

@Data
@Service
public class Sudoku {
    private final Path filePath;

    private int[] fullGrid;
    private BitSet[] pencilMark;

    public Sudoku() {
        this.filePath = Path.of("puzzles/puzzle5.txt");
        this.fullGrid = new int[81];
        this.pencilMark = new BitSet[81];
        for (int index = 0; index < pencilMark.length; index++) {
            pencilMark[index] = new BitSet(10);
        }
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

    public BitSet getRowPencilMarks(BitSet set, int elementIndex) {
        int rowStart = (elementIndex / 9) * 9;
        for (int index = rowStart; index < rowStart + 9; index++) {
            if (index != elementIndex && fullGrid[index] == 0) {
                set.or(pencilMark[index]);
            }
        }
        return set;
    }

    public BitSet getColPencilMarks(BitSet set, int elementIndex) {
        for (int index = elementIndex % 9; index < fullGrid.length; index += 9) {
            if (index != elementIndex && fullGrid[index] == 0) {
                set.or(pencilMark[index]);
            }
        }
        return set;
    }

    public BitSet getGridPencilMarks(BitSet set, int elementIndex) {
        int rowStart = (elementIndex / 9 / 3) * 3;
        int colStart = (elementIndex % 9 / 3) * 3;
        for (int row = rowStart; row < rowStart + 3; row++) {
            for (int col = colStart; col < colStart + 3; col++) {
                int index = row * 9 + col;
                if (index != elementIndex && fullGrid[index] == 0) {
                    set.or(pencilMark[index]);
                }
            }
        }
        return set;
    }

    public void solveMedium(int elementIndex) {
        if (fullGrid[elementIndex] != 0 || pencilMark[elementIndex].isEmpty()) {
            return;
        }
        int solution = 0;
        for (int pass = 0; pass < 3; pass++) {
            BitSet marks = new BitSet(10);
            if (pass == 0) {
                getGridPencilMarks(marks, elementIndex);
            } else if (pass == 1) {
                getRowPencilMarks(marks, elementIndex);
            } else {
                getColPencilMarks(marks, elementIndex);
            }
            BitSet unique = (BitSet) pencilMark[elementIndex].clone();
            unique.andNot(marks);
            if (unique.cardinality() == 1) {
                solution = unique.nextSetBit(1); //gets the first on bit at 1 or after
                fullGrid[elementIndex] = solution;
                pencilMark[elementIndex].clear();
                break;
            }
        }
        if (solution != 0) {
            updateRelatedPencilMarks(elementIndex, solution);
        }
    }
    // look at note 4 to get full explanation
    public void terminatePossiblePencilMarksRow(int elementIndex) {
        if (fullGrid[elementIndex] != 0) {
            return;
        }
        int cellRow = elementIndex / 9;
        int rowStart = (cellRow / 3) * 3;
        int colStart = (elementIndex % 9 / 3) * 3;
        BitSet ownCellUniquePencilMarks = (BitSet) pencilMark[elementIndex].clone();
        for (int row = rowStart; row < rowStart + 3; row++) {
            if (row == cellRow) {
                continue;
            }
            for (int col = colStart; col < colStart + 3; col++) {
                int index = row * 9 + col;
                if (fullGrid[index] == 0) {
                    ownCellUniquePencilMarks.andNot(pencilMark[index]);
                }
            }
        }
        for (int col = 0; col < 9; col++) {
            int index = cellRow * 9 + col;
            if ((col < colStart || col >= colStart + 3) && fullGrid[index] == 0) { //checks that is outside the same 3*3grid
                pencilMark[index].andNot(ownCellUniquePencilMarks);
            }
        }
    }

    public void terminatePossiblePencilMarksCol(int elementIndex) {
        if (fullGrid[elementIndex] != 0) {
            return;
        }
        int cellCol = elementIndex % 9;
        int rowStart = (elementIndex / 9 / 3) * 3;
        int colStart = (cellCol / 3) * 3;
        BitSet ownCellUniquePencilMarks = (BitSet) pencilMark[elementIndex].clone();
        for (int col = colStart; col < colStart + 3; col++) {
            if (col == cellCol) {
                continue;
            }
            for (int row = rowStart; row < rowStart + 3; row++) {
                int index = row * 9 + col;
                if (fullGrid[index] == 0) {
                    ownCellUniquePencilMarks.andNot(pencilMark[index]);
                }
            }
        }
        for (int row = 0; row < 9; row++) {
            int index = row * 9 + cellCol;
            if ((row < rowStart || row >= rowStart + 3) && fullGrid[index] == 0) {
                pencilMark[index].andNot(ownCellUniquePencilMarks);
            }
        }
    }

    public void solveEasyPencilMarks() {
        boolean hasSingleCandidate = true;
        while (hasSingleCandidate) {
            hasSingleCandidate = false;
            for (int index = 0; index < fullGrid.length; index++) {
                if (fullGrid[index] == 0 && pencilMark[index].cardinality() == 1) {
                    int solution = pencilMark[index].nextSetBit(1);
                    fullGrid[index] = solution;
                    pencilMark[index].clear();
                    updateRelatedPencilMarks(index, solution);
                    hasSingleCandidate = true;
                }
            }
        }
    }

    public boolean solveBacktracking() {
        int[] savedGrid = fullGrid.clone();
        BitSet[] savedMarks = copyPencilMarks();
        solveEasyPencilMarks();
        if (!isGridValid()) {
            restoreState(savedGrid, savedMarks);
            return false;
        }
        // find cell with least pencilMarks
        int selectedIndex = -1;
        int fewestCandidates = 10;
        for (int index = 0; index < fullGrid.length; index++) {
            if (fullGrid[index] != 0) {
                continue;
            }
            int count = pencilMark[index].cardinality();
            if (count == 0) {
                restoreState(savedGrid, savedMarks);
                return false;
            }
            if (count < fewestCandidates) {
                selectedIndex = index;
                fewestCandidates = count;
            }
        }
        if (selectedIndex == -1) {
            return true;
        }

        int[] beforeGuessGrid = fullGrid.clone();
        BitSet[] beforeGuessMarks = copyPencilMarks();
        BitSet candidates = beforeGuessMarks[selectedIndex];
        for (int value = candidates.nextSetBit(1); value >= 1 && value <= 9;
                value = candidates.nextSetBit(value + 1)) {
            if (getRelated(new HashSet<>(), selectedIndex).contains(value)) {
                continue;
            }
            fullGrid[selectedIndex] = value;
            pencilMark[selectedIndex].clear();
            updateRelatedPencilMarks(selectedIndex, value);
            if (solveBacktracking()) {
                return true;
            }
            restoreState(beforeGuessGrid, beforeGuessMarks);
        }
        restoreState(savedGrid, savedMarks);
        return false;
    }

    private BitSet[] copyPencilMarks() {
        BitSet[] copy = new BitSet[pencilMark.length];
        for (int index = 0; index < pencilMark.length; index++) {
            copy[index] = (BitSet) pencilMark[index].clone();
        }
        return copy;
    }

    private void restoreState(int[] savedGrid, BitSet[] savedMarks) {
        System.arraycopy(savedGrid, 0, fullGrid, 0, fullGrid.length);
        for (int index = 0; index < pencilMark.length; index++) {
            pencilMark[index].clear();
            pencilMark[index].or(savedMarks[index]);
        }
    }

    private boolean isGridValid() {
        for (int index = 0; index < fullGrid.length; index++) {
            int value = fullGrid[index];
            if (value < 0 || value > 9) {
                return false;
            }
            if (value == 0) {
                continue;
            }
            for (int other = index + 1; other < fullGrid.length; other++) {
                boolean related = index / 9 == other / 9 || index % 9 == other % 9
                        || (index / 27 == other / 27 && index % 9 / 3 == other % 9 / 3);
                if (related && fullGrid[other] == value) {
                    return false;
                }
            }
        }
        return true;
    }

    public void pencilMarkTraverse() {
        for (BitSet candidates : pencilMark) {
            candidates.clear();
        }
        for (int index = 0; index < fullGrid.length; index++) {
            if (fullGrid[index] != 0) {
                continue;
            }

            HashSet<Integer> related = getRelated(new HashSet<>(), index);
            for (int value = 1; value <= 9; value++) {
                if (!related.contains(value)) {
                    pencilMark[index].set(value);
                }
            }

            if (pencilMark[index].cardinality() == 1) {
                int solution = pencilMark[index].nextSetBit(1);
                fullGrid[index] = solution;
                pencilMark[index].clear();
                updateRelatedPencilMarks(index, solution);
            }
        }
    }

    private void updateRelatedPencilMarks(int elementIndex, int solution) {
        int row = elementIndex / 9;
        int col = elementIndex % 9;
        for (int index = 0; index < fullGrid.length; index++) {
            if (fullGrid[index] != 0) {
                continue;
            }
            int previousRow = index / 9;
            int previousCol = index % 9;
            if (previousRow == row || previousCol == col || (previousRow / 3 == row / 3 && previousCol / 3 == col / 3)) {
                pencilMark[index].clear(solution);
            }
        }
    }

    public void populateGrid() {
        try (Scanner scanner = new Scanner(filePath)) {
            int index = 0;
            while (scanner.hasNext()) {
                if (scanner.hasNextInt()) {
                    if (index >= fullGrid.length) {
                        throw new IllegalArgumentException("Puzzle contains more than 81 cells");
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
