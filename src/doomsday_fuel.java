import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class FractionNumber {
    public int Numerator;
    public int Denominator;

    public FractionNumber(int numerator) {
        this(numerator, 1);
    }

    public FractionNumber(int numerator, int denominator) {
        if (denominator <= 0) {
            throw new InvalidParameterException("denominator cannot be less than or equal zero");
        }

        Numerator = numerator;
        Denominator = denominator;
    }

    public void set(int numerator, int denominator) {
        if (denominator <= 0) {
            throw new InvalidParameterException("denominator cannot be less than or equal zero");
        }

        Numerator = numerator;
        Denominator = denominator;
    }

    public void add(FractionNumber other) {
        Numerator *= other.Denominator;
        Numerator += other.Numerator * Denominator;
        Denominator = Denominator * other.Denominator;
        simplify();
    }

    public void subtract(FractionNumber other) {
        Numerator *= other.Denominator;
        Numerator -= other.Numerator * Denominator;
        Denominator = Denominator * other.Denominator;
        simplify();
    }

    public void multiply(int multiplier) {
        Numerator *= multiplier;
        Denominator *= Math.abs(multiplier);
        simplify();
    }

    public void multiply(FractionNumber other) {
        Numerator *= other.Numerator;
        Denominator *= other.Denominator;
        simplify();
    }

    public void divide(FractionNumber other) {
        Numerator *= other.Denominator;
        Denominator *= other.Numerator;
        simplify();
    }

    public void simplify() {
        int gcd = GreatestCommonDivisor(Numerator, Denominator);
        Numerator /= gcd;
        Denominator /= gcd;
    }

    public FractionNumber clone() {
        return new FractionNumber(Numerator, Denominator);
    }

    public static int GreatestCommonDivisor(int a, int b) {
        int max = Math.abs(Math.max(a, b));
        int min = Math.abs(Math.min(a, b));

        while (min != 0) {
            int lastMax = max;

            max = min;
            min = lastMax % min;
        }

        return max;
    }

    public static int LeastCommonMultiple(int a, int b) {
        int gcd = GreatestCommonDivisor(a, b);
        int lcm = a / gcd * b;
        return lcm;
    }

    public static void MakeCommonDenominator(FractionNumber[] numbers) {
        int lcm = 1;
        for (int i = 1; i < numbers.length; ++i) {
            int newLcm = LeastCommonMultiple(numbers[i - 1].Denominator, numbers[i].Denominator);
            lcm = Math.max(newLcm, lcm);
        }

        for (FractionNumber number : numbers) {
            number.Numerator *= (lcm / number.Denominator);
            number.Denominator = lcm;
        }
    }
}

class Matrix {
    private FractionNumber[][] array;

    public Matrix(int row, int column) {
        array = new FractionNumber[row][column];

        for(int r = 0; r < row; ++r) {
            for(int c = 0; c < column; ++c) {
                array[r][c] = new FractionNumber(0);
            }
        }
    }

    public int row() {
        return array.length;
    }

    public int column() {
        return array.length > 0 ? array[0].length : 0;
    }

    public FractionNumber get(int row, int column) {
        return array[row][column];
    }

    public void set(int row, int column, int numerator, int denominator) {
        array[row][column].set(numerator, denominator);
    }

    public void add(Matrix other) {
        for(int r = 0; r < row(); ++r) {
            for(int c = 0; c < column(); ++c) {
                FractionNumber my = get(r, c);
                FractionNumber its = other.get(r, c);

                my.add(its);
            }
        }
    }

    public void subtract(Matrix other) {
        for(int r = 0; r < row(); ++r) {
            for(int c = 0; c < column(); ++c) {
                FractionNumber my = get(r, c);
                FractionNumber its = other.get(r, c);

                my.subtract(its);
            }
        }
    }

    public Matrix multiply(Matrix other) {
        int row = row();
        int column = other.column();
        Matrix result = new Matrix(row, column);

        for(int r = 0; r < row; ++r) {
            for(int c = 0; c < column; ++c) {

                for(int mid = 0; mid < row; ++mid) {
                    FractionNumber my = get(r, mid);
                    FractionNumber its = other.get(mid, c);
                    FractionNumber multiplied = my.clone();
                    multiplied.multiply(its);

                    result.get(r, c).add(multiplied);
                }
            }
        }

        return result;
    }

    public void makeIdentity() {
        for(int r = 0; r < row(); ++r) {
            for(int c = 0; c < column(); ++c) {
                int numerator = 0, denominator = 1;
                if(r == c) {
                    numerator = denominator = 1;
                }

                set(r, c, numerator, denominator);
            }
        }
    }

    public boolean makeInverse() {
        // https://www.geeksforgeeks.org/adjoint-inverse-matrix
        int n = array.length;

        FractionNumber det = determinant(array, n);
        if (det.Numerator == 0) {
            return false;
        }

        FractionNumber[][] adj = createFractionArray(n, n);
        adjoint(array, adj);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                adj[i][j].divide(det);
                array[i][j] = adj[i][j];
            }
        }

        return true;
    }

    public static Matrix Identity(int row, int column) {
        Matrix matrix = new Matrix(row, column);
        matrix.makeIdentity();
        return matrix;
    }

    private static void adjoint(FractionNumber[][] array, FractionNumber[][] adj) {
        int n = array.length;
        if (n == 1) {
            adj[0][0].set(1, 1);
            return;
        }

        int sign = 1;
        FractionNumber[][] temp = createFractionArray(n, n);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                getCofactor(array, temp, i, j, n);

                sign = ((i + j) % 2 == 0) ? 1: -1;

                FractionNumber determinant = determinant(temp, n-1);
                determinant.multiply(sign);

                adj[j][i] = determinant;
            }
        }
    }

    private static FractionNumber determinant(FractionNumber[][] array, int n) {
        FractionNumber D = new FractionNumber(0);

        if (n == 1) {
            return array[0][0].clone();
        }

        FractionNumber[][] temp = createFractionArray(n, n);

        int sign = 1;

        for (int f = 0; f < n; f++) {
            getCofactor(array, temp, 0, f, n);

            FractionNumber fItem = array[0][f].clone();
            fItem.multiply(sign);
            fItem.multiply(determinant(temp, n - 1));

            D.add(fItem);

            sign = -sign;
        }

        return D;
    }

    private static void getCofactor(FractionNumber[][] array, FractionNumber[][] temp, int p, int q, int n) {
        int i = 0, j = 0;

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (row != p && col != q) {
                    temp[i][j++].set(array[row][col].Numerator, array[row][col].Denominator);

                    if (j == n - 1) {
                        j = 0;
                        i++;
                    }
                }
            }
        }
    }

    private static FractionNumber[][] createFractionArray(int row, int col) {
        FractionNumber[][] array = new FractionNumber[row][col];

        for (int r = 0; r < row; ++r) {
            for (int c = 0; c < col; ++c) {
                array[r][c] = new FractionNumber(0);
            }
        }

        return array;
    }
}

public class doomsday_fuel {
    // Based on Absorbing Markov Chains. https://www.youtube.com/watch?v=bTeKu7WdbT8
    public static int[] solution(int[][] m) {
        List<int[]> terminals = Arrays.stream(m)
                .filter(a -> Arrays.stream(a).allMatch(aa -> aa == 0))
                .collect(Collectors.toList());
        int[] terminalsIndexes = IntStream.range(0, m.length)
                .filter(i -> Arrays.stream(m[i]).allMatch(aa -> aa == 0))
                .toArray();
        List<int[]> nonTerminals = Arrays.stream(m)
                .filter(a -> Arrays.stream(a).anyMatch(aa -> aa != 0))
                .collect(Collectors.toList());
        int[] nonTerminalsIndexes = IntStream.range(0, m.length)
                .filter(i -> Arrays.stream(m[i]).anyMatch(aa -> aa != 0))
                .toArray();

        Matrix rMatrix = new Matrix(nonTerminals.size(), terminals.size());
        Matrix qMatrix = new Matrix(nonTerminals.size(), nonTerminals.size());

        for (int r = 0; r < nonTerminals.size(); ++r) {
            int[] nonTerminalArray = nonTerminals.get(r);
            int denominator = Arrays.stream(nonTerminalArray).sum();

            int c = 0;
            for (int terminalIndex : terminalsIndexes) {
                int numerator = nonTerminalArray[terminalIndex];
                rMatrix.set(r, c, numerator, denominator);
                ++c;
            }

            c = 0;
            for (int nonTerminalIndex : nonTerminalsIndexes) {
                int numerator = nonTerminalArray[nonTerminalIndex];
                qMatrix.set(r, c, numerator, denominator);
                ++c;
            }
        }

        Matrix fMatrix = Matrix.Identity(qMatrix.row(), qMatrix.column());
        fMatrix.subtract(qMatrix);
        fMatrix.makeInverse();

        Matrix frMatrix = fMatrix.multiply(rMatrix);
        FractionNumber[] fractionResults = new FractionNumber[terminals.size()];
        boolean hasNonTerminalStates = frMatrix.row() > 0;

        if (hasNonTerminalStates) {
            for (int c = 0; c < fractionResults.length; ++c) {
                fractionResults[c] = frMatrix.get(0, c).clone();
                fractionResults[c].simplify();
            }
        } else {
            // if we do not have any non-terminal state the only state we can visit is first state
            for (int c = 0; c < fractionResults.length; ++c) {
                fractionResults[c] = new FractionNumber(0);
            }
            fractionResults[0].set(1, 1);
        }

        FractionNumber.MakeCommonDenominator(fractionResults);

        int[] result = new int[fractionResults.length + 1];
        for (int i = 0; i < fractionResults.length; ++i) {
            result[i] = fractionResults[i].Numerator;
        }
        result[result.length - 1] = fractionResults[0].Denominator;

        return result;
    }
}
