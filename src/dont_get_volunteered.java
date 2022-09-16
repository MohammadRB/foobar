import java.util.LinkedList;
import java.util.Queue;

class Square {
    public int Number;
    public int Row;
    public int Column;
    public int Iteration;

    Square(int number, int row, int column) {
        Number = number;
        Row = row;
        Column = column;
        Iteration = -1;
    }
}

class Board {
    static final int numRows = 8;
    static final int numColumns = 8;
    private Square[][] Squares;

    public Board() {
        Squares = new Square[numRows][numColumns];

        for(int r = 0; r < numRows; ++r) {
            for(int c = 0; c < numColumns; ++c) {
                Squares[r][c] = new Square(r * numColumns + c, r, c);
            }
        }
    }

    public Square get(int row, int column) {
        if(row < 0 || row >= numRows || column < 0 || column >= numColumns) {
            return null;
        }
        return Squares[row][column];
    }

    // return 8 neighbors by below order
    // left-bottom left-top
    // top-left top-right
    // right-top right-bottom
    // bottom-right bottom-left
    public Square[] getNeighbors(Square src) {
        Square[] neighbors = new Square[8];
        neighbors[0] = move(src, -2, 1);    // left-bottom
        neighbors[1] = move(src, -2, -1);   // left-top
        neighbors[2] = move(src, -1, -2);   // top-left
        neighbors[3] = move(src, 1, -2);    // top-right
        neighbors[4] = move(src, 2, -1);    // right-top
        neighbors[5] = move(src, 2, 1);     // right-bottom
        neighbors[6] = move(src, 1, 2);     // bottom-right
        neighbors[7] = move(src, -1, 2);    // bottom-left

        return neighbors;
    }

    public Square move(Square src, int rowMove, int columnMove) {
        int row = src.Row + columnMove;
        int column = src.Column + rowMove;
        return get(row, column);
    }
}

public class dont_get_volunteered {
    public static int solution(int src, int dest) {
        int srcRow = src / Board.numRows;
        int srcColumn = src % Board.numColumns;
        int destRow = dest / Board.numRows;
        int destColumn = dest % Board.numColumns;

        Board board = new Board();
        Square source = board.get(srcRow, srcColumn);
        Square destination = board.get(destRow, destColumn);

        if(source == destination) {
            return 0;
        }

        Queue<Square> toVisitQueue = new LinkedList<Square>();
        toVisitQueue.add(source);
        source.Iteration = 0;

        int shortestPath = Integer.MAX_VALUE;

        // Perform BFS on graph
        while (!toVisitQueue.isEmpty()) {
            Square toVisit = toVisitQueue.poll();
            Square[] neighbors = board.getNeighbors(toVisit);

            for(int i = 0; i < neighbors.length; ++i) {
                Square neighbor = neighbors[i];

                // is invalid or visited neighbor
                if (neighbor == null || neighbor.Iteration != -1) {
                    continue;
                }

                neighbor.Iteration = toVisit.Iteration + 1;

                if(neighbor == destination) {
                    shortestPath = neighbor.Iteration;
                    break;
                }

                toVisitQueue.add(neighbor);
            }

            if(shortestPath != Integer.MAX_VALUE) {
                break;
            }
        }

        return shortestPath;
    }
}
