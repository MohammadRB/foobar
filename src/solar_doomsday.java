import java.util.ArrayList;

class solar_doomsday {
    public static int[] solution(int area) {
        ArrayList<Integer> squares = new ArrayList<Integer>();

        while (area > 0)
        {
            int largestPossibleSquare = (int)Math.sqrt(area);
            int largestPossibleArea = largestPossibleSquare * largestPossibleSquare;

            squares.add(largestPossibleArea);
            area -= largestPossibleArea;
        }

        int[] array = new int[squares.size()];
        for(int i = 0; i < squares.size(); ++i) {
            array[i] = squares.get(i);
        }

        return array;
    }
}
