public class numbers_station_coded_messages {
    public static int[] solution(int[] l, int t) {
        int start = 0, end = 0;
        int currentSum = 0;

        while (start <= end && currentSum != t) {
            if(currentSum < t && end < l.length) {
                currentSum += l[end];
                ++end;
            } else if(start < l.length) {
                currentSum -= l[start];
                ++start;
            } else {
                start = -1;
                end = 0;
                break;
            }
        }

        return new int[] {start, end - 1};
    }
}
