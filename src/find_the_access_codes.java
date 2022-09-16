import java.util.ArrayList;

class StackNumber {
    public int Number;
    public int LastSearchIndex;

    public StackNumber(int number, int lastSearchIndex) {
        Number = number;
        LastSearchIndex = lastSearchIndex;
    }

    public void reset(int number, int lastSearchIndex) {
        Number = number;
        LastSearchIndex = lastSearchIndex;
    }
}

public class find_the_access_codes {
    // this indicates the size of paired numbers. for triples, set 3 or for quadruples set 4
    private static final int Stack_Size = 3;

    public static int solution(int[] l) {
        int solutionCount = 0;
        int stackTop = -1;
        ArrayList<StackNumber> stack = new ArrayList<>();

        for (int i = 0; i < Stack_Size; ++i) {
            stack.add(new StackNumber(0, 0));
        }

        for (int i = 0; i < l.length; ++i) {
            stack.get(++stackTop).reset(l[i], i);

            while (stackTop > -1) {
                StackNumber lastNumber = stack.get(stackTop);

                int nextNumber = -1;
                for (int j = lastNumber.LastSearchIndex + 1; j < l.length; ++j) {
                    int currNumber = l[j];
                    if (currNumber % lastNumber.Number == 0) {
                        nextNumber = currNumber;
                        lastNumber.LastSearchIndex = j;
                        break;
                    }
                }

                if (nextNumber == -1) {
                    --stackTop;
                    continue;
                }

                stack.get(++stackTop).reset(nextNumber, lastNumber.LastSearchIndex);

                if (stackTop == Stack_Size - 1) {
                    ++solutionCount;
                    --stackTop;
                }
            }
        }

        return solutionCount;
    }
}
