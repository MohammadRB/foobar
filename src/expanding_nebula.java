/*
This solution find every possible combinations of each column.
In each column we have a series of cells with four possible values.
So each column can be represented using two district columns of values. Inner left column and inner right column.
Each column combination will be converted to two integers base on values in inner left column and inner right column.
For each consecutive columns, we try to find matching combinations by comparing the inner right column hash of
the left column with the inner left column hash of the right column.
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class Cell {
    private final byte topLeft;
    private final byte topRight;
    private final byte bottomLeft;
    private final byte bottomRight;

    public Cell(int topLeft, int topRight, int bottomLeft, int bottomRight) {
        this.topLeft = (byte)topLeft;
        this.topRight = (byte)topRight;
        this.bottomLeft = (byte)bottomLeft;
        this.bottomRight = (byte)bottomRight;
    }

    public byte getTopLeft() {
        return topLeft;
    }

    public byte getTopRight() {
        return topRight;
    }

    public byte getBottomLeft() {
        return bottomLeft;
    }

    public byte getBottomRight() {
        return bottomRight;
    }

    public boolean willDeduceTo() {
        int topLeft = getTopLeft();
        int topRight = getTopRight();
        int bottomLeft = getBottomLeft();
        int bottomRight = getBottomRight();
        return (topLeft + topRight + bottomLeft + bottomRight) == 1;
    }

    /**
     * Compare the cell with another cell that is supposed to be its upper cell and return whether the cell
     * can be place below the upper cell and will deduce a value equal to the second parameter.
     * @param topCell The cell that is located in top of current cell.
     * @param deduceTo The value that the cell must deduce to.
     * @return
     */
    public boolean compareTopCellAndDeducingValue(Cell topCell, boolean deduceTo) {
        boolean topRowIsAlike = getTopLeft() == topCell.getBottomLeft() && getTopRight() == topCell.getBottomRight();
        if(!topRowIsAlike) {
            return false;
        }

        boolean deducingValue = willDeduceTo();
        return deducingValue == deduceTo;
    }

    public static List<Cell> getAllCombinationsForValue(boolean value) {
        return value ? trueCombinations : falseCombinations;
    }

    private static final ArrayList<Cell> trueCombinations = new ArrayList<Cell>(4) {{
        add(new Cell(1, 0, 0, 0));
        add(new Cell(0, 1, 0, 0));
        add(new Cell(0, 0, 1, 0));
        add(new Cell(0, 0, 0, 1));
    }};
    private static final ArrayList<Cell> falseCombinations = new ArrayList<Cell>(12) {{
        add(new Cell(0, 0, 0, 0));      // all false
        add(new Cell(1, 1, 0, 0));      // two true
        add(new Cell(0, 1, 0, 1));      // two true
        add(new Cell(0, 0, 1, 1));      // two true
        add(new Cell(1, 0, 1, 0));      // two true
        add(new Cell(1, 0, 0, 1));      // two true
        add(new Cell(0, 1, 1, 0));      // two true
        add(new Cell(1, 1, 0, 1));      // three true
        add(new Cell(0, 1, 1, 1));      // three true
        add(new Cell(1, 0, 1, 1));      // three true
        add(new Cell(1, 1, 1, 0));      // three true
        add(new Cell(1, 1, 1, 1));      // four true
    }};
}

class CellColumn {
    private Cell lastCell;
    private int leftColumnHash = 0;
    private int rightColumnHash = 0;

    private CellColumn() {
        //cells = new ArrayList<>();
    }

    public CellColumn(Cell initialCell) {
        addCell(initialCell);
    }

    /**
     * Compute all the possible combinations that can be added to the previous computed combinations.
     * @param deducingValue New value that combinations should be computed base on it.
     * @param result Array to add All the new combinations.
     */
    public void computeCombinations(boolean deducingValue, ArrayList<CellColumn> result) {
        List<Cell> availableCells = Cell.getAllCombinationsForValue(deducingValue);

        for (Cell cell : availableCells) {
            boolean isFit = cell.compareTopCellAndDeducingValue(lastCell, deducingValue);
            if (!isFit) {
                continue;
            }

            CellColumn newColumn = clone();
            newColumn.addCell(cell);
            result.add(newColumn);
        }
    }

    public int getLeftColumnHash() {
        return leftColumnHash;
    }

    public int getRightColumnHash() {
        return rightColumnHash;
    }

    public CellColumn clone() {
        CellColumn newInstance = new CellColumn();
        newInstance.lastCell = lastCell;
        newInstance.leftColumnHash = leftColumnHash;
        newInstance.rightColumnHash = rightColumnHash;

        return newInstance;
    }

    private void addCell(Cell cell) {
        if (lastCell == null) {
            leftColumnHash = (leftColumnHash << 1) + cell.getTopLeft();
            rightColumnHash = (rightColumnHash << 1) + cell.getTopRight();
        }

        leftColumnHash = (leftColumnHash << 1) + cell.getBottomLeft();
        rightColumnHash = (rightColumnHash << 1) + cell.getBottomRight();
        lastCell = cell;
    }

    public static ArrayList<CellColumn> createInitialCombinations(boolean value) {
        if (trueCombinations == null || falseCombinations == null) {
            trueCombinations = createCombinations(true);
            falseCombinations = createCombinations(false);
        }

        if (value) {
            return trueCombinations;
        } else {
            return falseCombinations;
        }
    }

    private static ArrayList<CellColumn> createCombinations(boolean value) {
        List<Cell> possibleCells = Cell.getAllCombinationsForValue(value);
        ArrayList<CellColumn> rows = new ArrayList<>(possibleCells.size());

        for(Cell cell : possibleCells) {
            rows.add(new CellColumn(cell));
        }

        return rows;
    }

    private static ArrayList<CellColumn> trueCombinations;
    private static ArrayList<CellColumn> falseCombinations;
}

public class expanding_nebula {
    public static int solution(boolean[][] g) {
        int rowCount = g.length;
        int columnCount = g[0].length;

        HashMap<Integer, Integer> lastCombinationMap = null;

        for (int c = 0; c < columnCount; ++c) {
            ArrayList<CellColumn> columnsCombinations = CellColumn.createInitialCombinations(g[0][c]);

            // Expand initial combinations by iterating over each row
            for (int r = 1; r < rowCount; ++r) {
                ArrayList<CellColumn> expandedCombinations = new ArrayList<>(columnsCombinations.size());

                for (CellColumn column : columnsCombinations) {
                    column.computeCombinations(g[r][c], expandedCombinations);
                }

                columnsCombinations = expandedCombinations;
            }

            if (lastCombinationMap == null) {
                lastCombinationMap = createMapUsingRightColumnHashes(columnsCombinations);
            } else {
                lastCombinationMap = createMapUsingRightColumnHashes(lastCombinationMap, columnsCombinations);
            }
        }

        return lastCombinationMap.values().stream().reduce(0, Integer::sum);
    }

    private static HashMap<Integer, Integer> createMapUsingRightColumnHashes(ArrayList<CellColumn> columnCombinations) {
        HashMap<Integer, Integer> map = new HashMap<>();

        for (CellColumn column : columnCombinations) {
            map.merge(column.getRightColumnHash(), 1, Integer::sum);
        }

        return map;
    }

    private static HashMap<Integer, Integer> createMapUsingRightColumnHashes(HashMap<Integer, Integer> leftColumnMap, ArrayList<CellColumn> rightColumns) {
        HashMap<Integer, Integer> result = new HashMap<>();

        for (CellColumn rightColumn : rightColumns) {
            int rightColumnHash = rightColumn.getLeftColumnHash();
            Integer leftColumnCombinationsCount = leftColumnMap.get(rightColumnHash);

            if (leftColumnCombinationsCount != null) {
                result.merge(rightColumn.getRightColumnHash(), leftColumnCombinationsCount, Integer::sum);
            }
        }

        return result;
    }
}