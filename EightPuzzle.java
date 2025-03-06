package puzzle;

import java.util.*;

class PuzzleState {
    int[][] board;
    int zeroRow, zeroCol;
    int cost;
    String move;
    PuzzleState parent;

    public PuzzleState(int[][] board, int zeroRow, int zeroCol, int cost, String move, PuzzleState parent) {
        this.board = new int[3][3];
        for (int i = 0; i < 3; i++) {
            this.board[i] = Arrays.copyOf(board[i], 3);
        }
        this.zeroRow = zeroRow;
        this.zeroCol = zeroCol;
        this.cost = cost;
        this.move = move;
        this.parent = parent;
    }

    public boolean isGoal() {
        int[][] goal = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
        return Arrays.deepEquals(board, goal);
    }

    public List<PuzzleState> getSuccessors() {
        List<PuzzleState> successors = new ArrayList<>();
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        String[] moves = {"UP", "DOWN", "LEFT", "RIGHT"};

        for (int i = 0; i < 4; i++) {
            int newRow = zeroRow + dr[i];
            int newCol = zeroCol + dc[i];

            if (newRow >= 0 && newRow < 3 && newCol >= 0 && newCol < 3) {
                int[][] newBoard = new int[3][3];
                for (int r = 0; r < 3; r++) {
                    newBoard[r] = Arrays.copyOf(board[r], 3);
                }
                newBoard[zeroRow][zeroCol] = newBoard[newRow][newCol];
                newBoard[newRow][newCol] = 0;
                successors.add(new PuzzleState(newBoard, newRow, newCol, cost + 1, moves[i], this));
            }
        }
        return successors;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PuzzleState other = (PuzzleState) obj;
        return Arrays.deepEquals(board, other.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int[] row : board) {
            sb.append(Arrays.toString(row)).append("\n");
        }
        return sb.toString();
    }
}

public class EightPuzzle {
    public static void main(String[] args) {
        int[][] initialBoard = {
            {7, 2, 4},
            {5, 0, 6},
            {8, 3, 1}
        };

        PuzzleState initialState = new PuzzleState(initialBoard, 1, 1, 0, "", null);

        Result dfsResult = depthFirstSearch(initialState);
        Result bfsResult = breadthFirstSearch(initialState);
        Result idfsResult = iterativeDeepeningDFS(initialState);
        Result ucsResult = uniformCostSearch(initialState);

        printResults(dfsResult, bfsResult, idfsResult, ucsResult);
    }

    public static Result depthFirstSearch(PuzzleState initialState) {
        Stack<PuzzleState> stack = new Stack<>();
        Set<PuzzleState> visited = new HashSet<>();
        stack.push(initialState);
        int nodesExpanded = 0;
        int maxNodes = 10000; // Limit to prevent infinite loops

        while (!stack.isEmpty() && nodesExpanded < maxNodes) {
            PuzzleState current = stack.pop();
            nodesExpanded++;

            if (current.isGoal()) {
                System.out.println("DFS: Goal found!");
                return new Result("DFS", nodesExpanded, current.cost, getPath(current));
            }

            if (!visited.contains(current)) {
                visited.add(current);
                for (PuzzleState successor : current.getSuccessors()) {
                    stack.push(successor);
                }
            }
        }
        System.out.println("DFS: Reached max nodes without finding a solution.");
        return new Result("DFS", nodesExpanded, -1, null);
    }

    public static Result breadthFirstSearch(PuzzleState initialState) {
        Queue<PuzzleState> queue = new LinkedList<>();
        Set<PuzzleState> visited = new HashSet<>();
        queue.add(initialState);
        int nodesExpanded = 0;
        int maxNodes = 10000; // Limit to prevent infinite loops

        while (!queue.isEmpty() && nodesExpanded < maxNodes) {
            PuzzleState current = queue.poll();
            nodesExpanded++;

            if (current.isGoal()) {
                System.out.println("BFS: Goal found!");
                return new Result("BFS", nodesExpanded, current.cost, getPath(current));
            }

            if (!visited.contains(current)) {
                visited.add(current);
                for (PuzzleState successor : current.getSuccessors()) {
                    queue.add(successor);
                }
            }
        }
        System.out.println("BFS: Reached max nodes without finding a solution.");
        return new Result("BFS", nodesExpanded, -1, null);
    }

    public static Result iterativeDeepeningDFS(PuzzleState initialState) {
        int depth = 0;
        int nodesExpanded = 0;
        int maxNodes = 10000; // Limit to prevent infinite loops

        while (true) {
            Result result = depthLimitedDFS(initialState, depth, nodesExpanded, maxNodes);
            if (result.path != null) {
                System.out.println("IDFS: Goal found!");
                return result;
            }
            if (nodesExpanded >= maxNodes) {
                System.out.println("IDFS: Reached max nodes without finding a solution.");
                return new Result("IDFS", nodesExpanded, -1, null);
            }
            depth++;
        }
    }

    public static Result depthLimitedDFS(PuzzleState state, int depth, int nodesExpanded, int maxNodes) {
        if (depth == 0 && state.isGoal()) {
            return new Result("IDFS", nodesExpanded, state.cost, getPath(state));
        }
        if (depth > 0) {
            for (PuzzleState successor : state.getSuccessors()) {
                if (nodesExpanded >= maxNodes) {
                    return new Result("IDFS", nodesExpanded, -1, null);
                }
                Result result = depthLimitedDFS(successor, depth - 1, nodesExpanded + 1, maxNodes);
                if (result.path != null) {
                    return result;
                }
            }
        }
        return new Result("IDFS", nodesExpanded, -1, null);
    }

    public static Result uniformCostSearch(PuzzleState initialState) {
        PriorityQueue<PuzzleState> queue = new PriorityQueue<>(Comparator.comparingInt(s -> s.cost));
        Set<PuzzleState> visited = new HashSet<>();
        queue.add(initialState);
        int nodesExpanded = 0;
        int maxNodes = 10000; // Limit to prevent infinite loops

        while (!queue.isEmpty() && nodesExpanded < maxNodes) {
            PuzzleState current = queue.poll();
            nodesExpanded++;

            if (current.isGoal()) {
                System.out.println("UCS: Goal found!");
                return new Result("UCS", nodesExpanded, current.cost, getPath(current));
            }

            if (!visited.contains(current)) {
                visited.add(current);
                for (PuzzleState successor : current.getSuccessors()) {
                    queue.add(successor);
                }
            }
        }
        System.out.println("UCS: Reached max nodes without finding a solution.");
        return new Result("UCS", nodesExpanded, -1, null);
    }

    public static List<String> getPath(PuzzleState state) {
        List<String> path = new ArrayList<>();
        while (state != null) {
            if (!state.move.isEmpty()) {
                path.add(state.move);
            }
            state = state.parent;
        }
        Collections.reverse(path);
        return path;
    }

    public static void printResults(Result dfsResult, Result bfsResult, Result idfsResult, Result ucsResult) {
        System.out.println("Method\t\tNodes Expanded\tCost\tPath");
        System.out.println("--------------------------------------------------");
        printResult(dfsResult);
        printResult(bfsResult);
        printResult(idfsResult);
        printResult(ucsResult);
    }

    public static void printResult(Result result) {
        System.out.println(result.method + "\t\t" + result.nodesExpanded + "\t\t\t" + result.cost + "\t" + result.path);
    }

    static class Result {
        String method;
        int nodesExpanded;
        int cost;
        List<String> path;

        public Result(String method, int nodesExpanded, int cost, List<String> path) {
            this.method = method;
            this.nodesExpanded = nodesExpanded;
            this.cost = cost;
            this.path = path;
        }
    }
}