import java.util.Random;
import java.util.Stack;

public class MazeGenerator {
    private static final char WALL = '#';
    private static final char PATH = '.';
    private static final char START = 'S';
    private static final char EXIT = 'E';

    private int rows;
    private int cols;
    private char[][] maze;
    private Random random = new Random();

    public MazeGenerator(int rows, int cols) {
        if (rows % 2 == 0 || cols % 2 == 0) {
            throw new IllegalArgumentException("Rows and columns must be odd numbers");
        }
        this.rows = rows;
        this.cols = cols;
        this.maze = new char[rows][cols];
    }

    public void generateMaze() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = WALL;
            }
        }

        int startX = 1;
        int startY = 1;
        maze[startX][startY] = PATH;

        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{startX, startY});

        int[][] directions = {{-2, 0}, {0, 2}, {2, 0}, {0, -2}};

        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int x = current[0];
            int y = current[1];

            boolean[] visitedDirs = new boolean[4];
            boolean hasUnvisited = false;

            for (int i = 0; i < 4; i++) {
                int randIndex = random.nextInt(4);
                while (visitedDirs[randIndex]) {
                    randIndex = random.nextInt(4);
                }
                visitedDirs[randIndex] = true;

                int newx = x + directions[randIndex][0];
                int newy = y + directions[randIndex][1];

                if (newx > 0 && newx < rows - 1 && newy > 0 && newy < cols - 1 && maze[newx][newy] == WALL) {
                    maze[(x + newx)/2][(y + newy)/2] = PATH;
                    maze[newx][newy] = PATH;
                    stack.push(new int[]{newx, newy});
                    hasUnvisited = true;
                    break;
                }
            }

            if (!hasUnvisited) {
                stack.pop();
            }
        }

        maze[1][1] = START;
        maze[rows-2][cols-2] = EXIT;

        if (maze[rows-2][cols-3] == WALL && maze[rows-3][cols-2] == WALL) {
            if (random.nextBoolean()) {
                maze[rows-2][cols-3] = PATH;
            } else {
                maze[rows-3][cols-2] = PATH;
            }
        }
    }

    public void printMaze() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(maze[i][j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java MazeGenerator <rows> <cols>");
            System.out.println("Note: rows and cols must be odd numbers between 5 and 101");
            return;
        }

        int rows = Integer.parseInt(args[0]);
        int cols = Integer.parseInt(args[1]);

        if (rows < 5 || rows > 101 || cols < 5 || cols > 101 ||
                rows % 2 == 0 || cols % 2 == 0) {
            System.out.println("Invalid dimensions. Rows and columns must be odd numbers between 5 and 101");
            return;
        }

        MazeGenerator generator = new MazeGenerator(rows, cols);
        generator.generateMaze();
        generator.printMaze();
    }
}