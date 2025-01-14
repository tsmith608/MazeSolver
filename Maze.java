import java.util.*;

class Point {
    int x;
    int y;
    boolean visited;
    int data;
    public char type;

    // Constructor to initialize a point with coordinates.
    Point(int x, int y) {
        this.x = x;
        this.y = y;
        this.visited = false;
        this.data = 0;
        this.type = ' '; // Default to empty space
    }
}


public class Maze {
    private Point[][] maze;
    private Point start;
    private Point end;
    private int numRows;
    private int numColumns;
    private Map<Point, Point> pathMap;

    // Constructor to initialize the maze with specified number of rows and columns.
    public Maze(int numRows, int numColumns) {
        this.numRows = numRows;
        this.numColumns = numColumns;
        maze = new Point[numRows][numColumns];
        initializeMaze();
    }

    // Initializes the maze by setting walls around the edges.
    private void initializeMaze() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                maze[i][j] = new Point(i, j);
            }
        }

        // Set walls on the left and right borders
        for (int i = 0; i < numRows; i++) {
            setWall(i, 0);
            setWall(i, numColumns - 1);
        }

        // Set walls on the top and bottom borders
        for (int j = 0; j < numColumns; j++) {
            setWall(0, j);
            setWall(numRows - 1, j);
        }
    }

    // Sets a point as a wall at the given row and column.
    public void setWall(int row, int col) {
        maze[row][col].x = -1;
        maze[row][col].y = -1;
        maze[row][col].type = 'X';
    }

    // Sets a point as a path at the given row and column.
    public void setPath(int row, int col) {
        maze[row][col].x = row;
        maze[row][col].y = col;
        maze[row][col].type = ' ';
    }

    // Sets the start point of the maze.
    public void setStart(int row, int col) {
        setPath(row, col);
        start = maze[row][col];
        start.type = 'S';
    }

    // Sets the end point of the maze.
    public void setEnd(int row, int col) {
        setPath(row, col);
        end = maze[row][col];
        end.type = 'E';
    }

    // Sets random start and end points in the maze.
    public void setRandomStartAndEnd() {
        Random rand = new Random();

        // Set random start position
        int startRow, startCol;
        do {
            startRow = rand.nextInt(numRows);
            startCol = rand.nextInt(numColumns);
        } while (maze[startRow][startCol].x == -1);

        setStart(startRow, startCol);

        // Set random end position
        int endRow, endCol;
        do {
            endRow = rand.nextInt(numRows);
            endCol = rand.nextInt(numColumns);
        } while (maze[endRow][endCol].x == -1 || (endRow == startRow && endCol == startCol));

        setEnd(endRow, endCol);
    }

    // Generates the maze with random walls and paths.
    public void generateMaze() {
        Random rand = new Random();
        for (int i = 1; i < numRows - 1; i++) {
            for (int j = 1; j < numColumns - 1; j++) {
                if (rand.nextDouble() < 0.7) {
                    setPath(i, j);
                } else {
                    setWall(i, j);
                }
            }
        }
    }

    // Prints the current state of the maze.
    public void printMaze() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                System.out.print(maze[i][j].type + " ");
            }
            System.out.println();
        }
    }

    // Solves the maze using Breadth-First Search (BFS).
    public boolean bfs() {
        if (start == null || end == null) {
            System.out.println("Start or end point is not properly set.");
            return false;
        }

        Queue<Point> queue = new LinkedList<>();
        queue.offer(start);
        pathMap = new HashMap<>();
        pathMap.put(start, null); // Start has no parent

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current == end) {
                markPath();
                return true;
            }

            int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };

            // Explore all four directions (up, down, left, right)
            for (int[] dir : directions) {
                int newRow = current.x + dir[0];
                int newCol = current.y + dir[1];

                // Check if the move is valid
                if (isValidMove(newRow, newCol)) {
                    Point neighbor = maze[newRow][newCol];
                    if (!pathMap.containsKey(neighbor)) {
                        queue.offer(neighbor);
                        pathMap.put(neighbor, current); // Track parent
                    }
                }
            }
        }

        System.out.println("No path found with BFS.");
        return false;
    }

    // Solves the maze using Depth-First Search (DFS).
    public boolean dfs() {
        if (start == null || end == null) {
            System.out.println("Start or end point is not properly set.");
            return false;
        }

        Stack<Point> stack = new Stack<>();
        stack.push(start);
        pathMap = new HashMap<>();
        pathMap.put(start, null); // Start has no parent

        while (!stack.isEmpty()) {
            Point current = stack.pop();

            if (current == end) {
                markPath();
                return true;
            }

            int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };

            // Explore all four directions (up, down, left, right)
            for (int[] dir : directions) {
                int newRow = current.x + dir[0];
                int newCol = current.y + dir[1];

                // Check if the move is valid
                if (isValidMove(newRow, newCol)) {
                    Point neighbor = maze[newRow][newCol];
                    if (!pathMap.containsKey(neighbor)) {
                        stack.push(neighbor);
                        pathMap.put(neighbor, current); // Track parent
                    }
                }
            }
        }

        System.out.println("No path found with DFS.");
        return false;
    }

    // Checks if a move to the given row and column is valid.
    private boolean isValidMove(int row, int col) {
        if (row >= 0 && row < numRows && col >= 0 && col < numColumns) {
            Point point = maze[row][col];
            return point.x != -1 && !point.visited;
        }
        return false;
    }

    // Marks the path from start to end in the maze.
    private void markPath() {
        if (pathMap == null || pathMap.isEmpty() || end == null) {
            return;
        }

        Point current = end;

        while (current != null && current != start) {
            current.type = '+'; // Mark path with '+'
            current = pathMap.get(current);
        }
    }

    // Main method to run the maze generation and solving algorithms.
    public static void main(String[] args) {
        int numRows = 20;
        int numColumns = 20;

        try {
            Maze mazeBuilder = new Maze(numRows, numColumns);
            mazeBuilder.generateMaze();
            mazeBuilder.setRandomStartAndEnd();

            System.out.println("Generated Maze (Before Solving):");
            mazeBuilder.printMaze();

            System.out.println("\nSolving with BFS...");
            if (mazeBuilder.bfs()) {
                System.out.println("Solved Maze with BFS (After Solving):");
                mazeBuilder.printMaze();
            }

            System.out.println("\nResetting Maze for DFS...");
            mazeBuilder = new Maze(numRows, numColumns);
            mazeBuilder.generateMaze();
            mazeBuilder.setRandomStartAndEnd();

            System.out.println("\nGenerated Maze (Before Solving):");
            mazeBuilder.printMaze();

            System.out.println("\nSolving with DFS...");
            if (mazeBuilder.dfs()) {
                System.out.println("Solved Maze with DFS (After Solving):");
                mazeBuilder.printMaze();
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}
