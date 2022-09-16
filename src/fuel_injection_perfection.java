import java.math.BigInteger;

public class fuel_injection_perfection {
    public static int solution(String x) {
        BigInteger number = new BigInteger(x);
        BigInteger one = BigInteger.ONE;
        BigInteger two = BigInteger.valueOf(2);
        int steps = 0;

        while (number.compareTo(one) != 0) {
            boolean isEven = isEven(number);

            if (!isEven) {
                BigInteger minusOne = number.subtract(one);
                BigInteger minusOneDivided = minusOne.divide(two);

                if (isEven(minusOneDivided) || minusOneDivided.compareTo(one) == 0) {
                    number = minusOneDivided;
                } else {
                    // convert it to plus one divided
                    number = minusOneDivided.add(one);
                }

                ++steps;
            }
            else {
                number = number.divide(two);
            }

            ++steps;
        }

        return steps;
    }

    private static boolean isEven(BigInteger number) {
        return !number.testBit(0);
    }
}
