import java.util.ArrayList;

class Node {
    public int Number;
    public int VisitIteration;
    public int MatchIteration;
    public ArrayList<Node> Edges;

    public Node(int number) {
        Number = number;
        VisitIteration = -1;
        MatchIteration = -1;
        Edges = new ArrayList<>();
    }

    public void addNode(Node node) {
        Edges.add(node);
    }
}

public class distract_the_trainers {
    public static int solution(int[] banana_list) {
        ArrayList<Node> allNodes = createGraph(banana_list);

        int bestMatchesCount = findMatchesInGraph(allNodes);

        return allNodes.size() - bestMatchesCount * 2;
    }

    private static ArrayList<Node> createGraph(int[] banana_list) {
        ArrayList<Node> allNodes = new ArrayList<>();

        for (int n : banana_list) {
            Node node = new Node(n);
            allNodes.add(node);
        }

        for (int i = 0; i < banana_list.length - 1; ++i) {
            Node sourceNode = allNodes.get(i);

            for (int j = i + 1; j < banana_list.length; ++j) {
                Node destinationNode = allNodes.get(j);
                boolean hasLoop = hasLoop(sourceNode.Number, destinationNode.Number);

                if(hasLoop) {
                    sourceNode.addNode(destinationNode);
                    destinationNode.addNode(sourceNode);
                }
            }
        }

        // Sort edges so nodes with the least possible edges will be matched first
        allNodes.sort((n1, n2) -> n1.Edges.size() - n2.Edges.size());
        for (Node node : allNodes) {
            node.Edges.sort((n1, n2) -> n1.Edges.size() - n2.Edges.size());
        }

        return allNodes;
    }

    private static int findMatchesInGraph(ArrayList<Node> nodes) {
        int bestMatch = 0;
        int iteration = 0;

        for (Node node : nodes) {
            int dfs = depthFirstSearch(null, node, iteration);
            bestMatch = Math.max(bestMatch, dfs);

            ++iteration;
        }

        return bestMatch;
    }

    private static int depthFirstSearch(Node parent, Node node, int iteration) {
        int bestMatch = 0;
        node.VisitIteration = iteration;

        // Try to match consecutive nodes which are not matched before in traverse path
        if (parent != null && parent.MatchIteration != iteration) {
            parent.MatchIteration = iteration;
            node.MatchIteration = iteration;
            ++bestMatch;
        }

        for (Node edge : node.Edges) {
            if (edge.VisitIteration == iteration) {
                continue;
            }

            int edgeBestMatches = depthFirstSearch(node, edge, iteration);
            bestMatch += edgeBestMatches;
        }

        return bestMatch;
    }

    private static boolean hasLoop(int a, int b) {
        while (true) {
            boolean pairsAreEqual = a == b;
            if (pairsAreEqual) {
                return false;
            }

            boolean firstIsEven = isEven(a);
            boolean secondIsEven = isEven(b);
            boolean pairsAreOpposite = firstIsEven ^ secondIsEven;
            if (pairsAreOpposite) {
                return true;
            }

            int min = Math.min(a, b);
            int max = Math.max(a, b);
            max -= min;
            min += min;

            int gcd = greatestCommonDivisor(a, b);
            a = min / gcd;
            b = max / gcd;
        }
    }

    public static int greatestCommonDivisor(int a, int b) {
        int max = Math.max(a, b);
        int min = Math.min(a, b);

        while (min != 0) {
            int lastMax = max;

            max = min;
            min = lastMax % min;
        }

        return max;
    }

    private static boolean isEven(int number) {
        return (number & 0b1) == 0;
    }
}
