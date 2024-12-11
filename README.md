# SnakeGame
A classic childhood game where the player controls a snake that moves around the screen to eat food. With each piece of food eaten, the snake grows in length. The game ends if the snake collides with itself or the screen boundary.

## Game Logic
- Snake Movement: Updates via a list of Point objects.  
- Collision Detection: Checks for self, boundary, and food collisions.  
- Food Respawn: Generates food at random positions.  
- Score Tracking: Updates score on food consumption.  
 
## OOP Concepts  
- Encapsulation: Game objects like Snake and Food encapsulate their data and behaviors.  
- Inheritance: Snake and Food inherit from the base class GameObject.  
- Polymorphism: draw(Graphics g) is overridden for custom rendering.  
- Interface: Movable ensures consistent movement behavior.  
- Exception Handling: GameOverException handles game-over scenarios.  
 
## Swing GUI  
- JFrame: Creates the game window.  
- JPanel: GamePanel renders the game and displays the score.  
- Timer: Ensures smooth updates for movement.  
