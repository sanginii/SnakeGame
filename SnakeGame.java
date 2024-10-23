import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Base class for all game objects
class GameObject {
    protected int x, y;
    protected int size;

    public GameObject(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

// Movable interface for objects that can move
interface Movable {
    void move();
}

// Custom exception for game over scenarios
class GameOverException extends Exception {
    public GameOverException(String message) {
        super(message);
    }
}

// Class representing the Snake
class Snake extends GameObject implements Movable {
    private List<Point> body;
    private String direction;

    public Snake(int x, int y) {
        super(x, y, 10);
        body = new ArrayList<>();
        body.add(new Point(x, y));
        direction = "RIGHT"; // Start by moving right
    }

    public void setDirection(String newDirection) {
        // Basic validation to prevent the snake from reversing direction
        if (!((direction.equals("UP") && newDirection.equals("DOWN")) ||
                (direction.equals("DOWN") && newDirection.equals("UP")) ||
                (direction.equals("LEFT") && newDirection.equals("RIGHT")) ||
                (direction.equals("RIGHT") && newDirection.equals("LEFT")))) {
            this.direction = newDirection;
        }
    }

    @Override
    public void move() {
        Point head = body.get(0);
        Point newHead = new Point(head.x, head.y);

        switch (direction) {
            case "UP":
                newHead.y -= size;
                break;
            case "DOWN":
                newHead.y += size;
                break;
            case "LEFT":
                newHead.x -= size;
                break;
            case "RIGHT":
                newHead.x += size;
                break;
        }

        body.add(0, newHead);
        body.remove(body.size() - 1); // Remove the tail
    }

    public void grow() {
        Point tail = body.get(body.size() - 1);
        body.add(new Point(tail.x, tail.y)); // Duplicate the last segment
    }

    public List<Point> getBody() {
        return body;
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        for (Point point : body) {
            g.fillRect(point.x, point.y, size, size);
        }
    }

    public boolean collidesWithItself() {
        Point head = body.get(0);
        for (int i = 1; i < body.size(); i++) {
            if (head.equals(body.get(i))) {
                return true;
            }
        }
        return false;
    }
}

// Class representing the Food
class Food extends GameObject {
    public Food(int x, int y) {
        super(x, y, 10);
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, size, size);
    }
}

// Class for the main game logic
class Game {
    private Snake snake;
    private List<Food> foods;
    private Random random;
    private int score;
    private int screenWidth, screenHeight;

    public Game(int screenWidth, int screenHeight) {
        snake = new Snake(50, 50);
        foods = new ArrayList<>();
        random = new Random();
        score = 0;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        spawnFood();
    }

    public void spawnFood() {
        int x = random.nextInt(screenWidth / 10) * 10;
        int y = random.nextInt(screenHeight / 10) * 10;
        foods.add(new Food(x, y));
    }

    public void update() throws GameOverException {
        snake.move();

        // Check for food collision
        Food foodToRemove = null;
        for (Food food : foods) {
            if (snake.getBody().get(0).x == food.getX() && snake.getBody().get(0).y == food.getY()) {
                snake.grow();
                score += 10;
                foodToRemove = food;
                spawnFood(); // Spawn new food
                break;
            }
        }
        if (foodToRemove != null)
            foods.remove(foodToRemove);

        // Check for self-collision and boundary collision
        if (snake.collidesWithItself() || snake.getBody().get(0).x < 0 || snake.getBody().get(0).y < 0 ||
                snake.getBody().get(0).x >= screenWidth || snake.getBody().get(0).y >= screenHeight) {
            throw new GameOverException("Game Over! Score: " + score);
        }
    }

    public void draw(Graphics g) {
        snake.draw(g);
        for (Food food : foods) {
            food.draw(g);
        }
    }

    public Snake getSnake() {
        return snake;
    }
}

// Main class to run the game
public class SnakeGame extends JFrame implements ActionListener {
    private Game game;
    private Timer timer;
    private GamePanel gamePanel;
    private int screenWidth, screenHeight;

    public SnakeGame() {
        // Get screen size for full screen mode
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = screenSize.width;
        screenHeight = screenSize.height;

        game = new Game(screenWidth, screenHeight);
        gamePanel = new GamePanel();
        add(gamePanel);

        setTitle("Snake Game");
        setSize(screenWidth, screenHeight);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize the window

        timer = new Timer(100, this);
        timer.start();

        // Key listener for snake movement
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        game.getSnake().setDirection("UP");
                        break;
                    case KeyEvent.VK_DOWN:
                        game.getSnake().setDirection("DOWN");
                        break;
                    case KeyEvent.VK_LEFT:
                        game.getSnake().setDirection("LEFT");
                        break;
                    case KeyEvent.VK_RIGHT:
                        game.getSnake().setDirection("RIGHT");
                        break;
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            game.update();
            gamePanel.repaint();
        } catch (GameOverException ex) {
            timer.stop();
            showGameOverDialog(ex.getMessage());
        }
    }

    // Method to show custom Game Over dialog with OK and Restart buttons
    private void showGameOverDialog(String message) {
        String[] options = {"OK", "Restart Game"};
        int choice = JOptionPane.showOptionDialog(
                this,
                message,
                "Game Over",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 1) { // If 'Restart Game' is selected
            restartGame();
        } else { // If 'OK' is selected or dialog is closed
            System.exit(0); // Close the game
        }
    }

    // Method to restart the game
    private void restartGame() {
        game = new Game(screenWidth, screenHeight); // Reinitialize the game
        timer.start();     // Start the timer again
    }

    // Panel for rendering the game
    class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            game.draw(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SnakeGame::new);
    }
}
