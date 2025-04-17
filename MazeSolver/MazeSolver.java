import java.io.*;

/* IMPORTANT NOTE 1: I changed left hand rule a bit. The program tries to go right way at every move but if it cant
                     and there is a wall, then it tries turn left.
   IMPORTANT NOTE 2: Insted of turning two times left, I added turning back(it works similar) and used 4 directions
                      for a hierarchical circle. */


public class MazeSolver {
    private static final char WALL = '#';
    private static final char EXIT = 'X';
    private static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // Right, Down, Left, Up (Absulute directions)

    //I didn't want to use backtracking for avoiding loops ,so I add movement limit.

    private static final int MOVE_LIMIT = 100;

    public static char[][] readMazeGrid(String input) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(input));
        String line;
        int rows = 0;

        // Count rows
        while ((line = reader.readLine()) != null) {
            rows++;
        }
        reader.close();

        // Read the maze into a 2D char array
        char[][] mazeGrid = new char[rows][];
        reader = new BufferedReader(new FileReader(input));
        int row = 0;
        while ((line = reader.readLine()) != null) {
            mazeGrid[row] = line.toCharArray();
            row++;
        }
        reader.close();
        return mazeGrid;
    }
//  Maze printer func is there.

    public static void printMaze(char [][] mazeGrid){
        for(int i = 0; i < mazeGrid.length; i++){
            for(int j = 0; j < mazeGrid[i].length; j++){
                System.out.print(mazeGrid[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void solveMaze(char[][] maze, int startX, int startY) {
        int rows = maze.length;
        int cols = maze[0].length;

        int direction = 0;  // Start as facing "right"
        int x = startX, y = startY;
        int moveCount = 0;

        while (true) {
            // Check if we reached the exit
            if (maze[x][y] == EXIT) {
                System.out.println("x:" + x + ", y:" + y);
                return;
            }
            // You can print current position by this ,so I didn't delete it

            //System.out.println("Current position: x:" + x + ", y:" + y);

            // Check for our movement limit
            moveCount++;
            if (moveCount > MOVE_LIMIT) {
                System.out.println("There is no solution!");
                return;
            }

            // Try turning right (relative to facing direction)
            int rightDirection = (direction + 1) % 4;
            int newX = x + DIRECTIONS[rightDirection][0];
            int newY = y + DIRECTIONS[rightDirection][1];

            // If right is valid, move there and update direction
            if (isValidMove(maze, newX, newY, rows, cols)) {
                direction = rightDirection;
                x = newX;
                y = newY;
                continue;
            }

            // Try moving forward (current direction)
            newX = x + DIRECTIONS[direction][0];
            newY = y + DIRECTIONS[direction][1];

            if (isValidMove(maze, newX, newY, rows, cols)) {
                x = newX;
                y = newY;
                continue;
            }

            // Try turning left (relative to facing direction)
            int leftDirection = (direction + 3) % 4;
            newX = x + DIRECTIONS[leftDirection][0];
            newY = y + DIRECTIONS[leftDirection][1];

            if (isValidMove(maze, newX, newY, rows, cols)) {
                direction = leftDirection;
                x = newX;
                y = newY;
                continue;
            }

            // Turn 180 degrees (backwards) if all else fails
            int backwardDirection = (direction + 2) % 4;
            newX = x + DIRECTIONS[backwardDirection][0];
            newY = y + DIRECTIONS[backwardDirection][1];

            if (isValidMove(maze, newX, newY, rows, cols)) {
                direction = backwardDirection;
                x = newX;
                y = newY;
                continue;
            }

            // If no direction is valid, print failure and return
            System.out.println("There is no solution!");
            return;
        }
    }

    private static boolean isValidMove(char[][] maze, int x, int y, int rows, int cols) {
        // Check if the move is out of bounds or hits a wall
        if (x < 0 || x >= rows) return false;
        if (y < 0 || y >= cols) return false;
        if (maze[x][y] == '#') return false; //'#' represents a wall
        return true;
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Please enter the starting coordinates and maze file path.");
            return;
        }

        int startX = Integer.parseInt(args[0]);
        int startY = Integer.parseInt(args[1]);
        String filePath = args[2];

        char[][] maze = readMazeGrid(filePath);
        //printMaze(maze);
        solveMaze(maze, startX, startY);
    }
}
