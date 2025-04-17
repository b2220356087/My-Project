import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SpaceInvader extends Application {

    public void start(Stage stage) {
        // Show the SpaceInvader.TitleScreen as the first screen of the application

        TitleScreen titleScreen = new TitleScreen(stage);

        titleScreen.show();
    }

    public static void main(String[] args) {
        launch(args); // Start the JavaFX application
    }

    public static class TitleScreen {
        private Stage stage;
        private Scene scene;

        public TitleScreen(Stage stage) {
            this.stage = stage;
            initialze();

        }

        private void initialze() {
            Pane root = new Pane();

            // Taking background ımage for Title Screen

            String filePath = new File("src/assets/main.png").toURI().toString();
            Image background = new Image(filePath); // Note the "file:" prefix
            ImageView backgroundImage = new ImageView(background);
            backgroundImage.setFitHeight(700);
            backgroundImage.setFitWidth(800);
            root.getChildren().add(backgroundImage);

            //Starting Game Instructions
            Text pressEnterToPlay = new Text("Press Enter To Play");
            pressEnterToPlay.setX(290);
            pressEnterToPlay.setY(250);
            pressEnterToPlay.setStyle("-fx-font-size: 30px; -fx-fill: white; -fx-font-weight: bold;");
            root.getChildren().add(pressEnterToPlay);

            // Exit Instuctions
            Text pressESCtoExit = new Text("Press ESC to Exit");
            pressESCtoExit.setX(320);
            pressESCtoExit.setY(300);
            pressESCtoExit.setStyle("-fx-font-size: 25px; -fx-fill: white; -fx-font-weight: bold;");
            root.getChildren().add(pressESCtoExit);

            this.scene = new Scene(root, 800, 700);
            this.scene.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case ENTER:
                        System.out.println("Start Game");
                        new GameScreen(stage).show(); // Starting Game
                        break;
                    case ESCAPE:
                        System.out.println("Exit Game");
                        stage.close();
                        break;
                }
            });




        }

        public void show() {
            stage.setScene(scene);
            stage.show();

        }

    }

    public static class GameScreen {
        private Stage stage;
        private Scene scene;
        private Pane root;
        private ImageView player;
        private Text scoreText;
        private static int score;
        private List<Projectile> projectiles;
        private List<Enemy> enemies;
        private AnimationTimer gameLoop;
        private boolean isPaused;
        private List<Loot> loots = new ArrayList<>();
        private boolean isPowerUpActive = false; // Tracks if the power-up is active
        private long powerUpStartTime; // Records when the power-up started
        private long lootMessageTime;
        private Image enemyImage;

        public GameScreen(Stage stage) {
            this.stage = stage;
            initialize();
        }

        private void initialize() {
            root = new Pane();
            //Black Bacground for more clear experience
            root.setStyle("-fx-background-color: black;");
            projectiles = new ArrayList<>();
            enemies = new ArrayList<>();
            score = 0;

            //SpaceInvader.Enemy setup
            File enemyFile = new File("src/assets/enemy.png");
            enemyImage = new Image(enemyFile.toURI().toString());

            // Player setup
            String playerFilePath = new File("src/assets/player.png").toURI().toString();
            Image playerImage = new Image(playerFilePath);
            player = new ImageView(playerImage);
            player.setFitWidth(50);
            player.setFitHeight(50);
            player.setX(375);
            player.setY(600);
            root.getChildren().add(player);

            // Score display
            scoreText = new Text("Score: " + score);
            scoreText.setStyle("-fx-font-size: 24px; -fx-fill: white; -fx-font-weight: bold;");
            scoreText.setX(10);
            scoreText.setY(30);
            root.getChildren().add(scoreText);

            // Key Listener for Player Movement
            this.scene = new Scene(root, 800, 700);
            this.scene.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case LEFT:
                        if (!isPaused) {
                            //Set screen borders
                            if (player.getX() > -5) {
                        player.setX(player.getX() - 12);
                        break;}
                        else break;}
                    case RIGHT:
                        if (!isPaused) {
                            //Set screen borders
                            if (player.getX() < 750) {
                        player.setX(player.getX() + 12);
                        break;}
                        else break;}
                    case SPACE:
                        if (!isPaused) {
                        shootProjectile();
                        break;}
                        else break;
                    case P:
                        togglePause();
                        break;

                }
            });

            // Initialize Game Loop
            gameLoop = new AnimationTimer() {
                private long lastEnemySpawnTime = 0;

                @Override
                public void handle(long now) {
                    if (!isPaused) {
                    updateProjectiles();
                    updateEnemies();
                    updateLoots();
                    // Spawn enemies every 2 seconds represent as nanoecons
                    if (now - lastEnemySpawnTime > 2_000_000_000L) {
                        spawnEnemy();
                        lastEnemySpawnTime = now;
                    }

                    checkPlayerEnemyCollision(); // Check for collision between player and enemies
                    checkCollisions();
                }
                }
            };
            gameLoop.start();
        }

        private void togglePause() {
            isPaused = !isPaused; // Toggle the paused state
            if (isPaused) {
                gameLoop.stop();
            } else {
                gameLoop.start();
            }
        }

        private void spawnLoot(double x, double y) {
            double dropChance = Math.random(); // Generate a random number between 0 and 1
            if (dropChance > 0.6) {
                return; // If chance is greater than 60%, don't spawn loot
            }

            String lootType = null;

            if (dropChance >= 0.4 && dropChance < 0.6) {
                lootType = "bonus";
            } else if (dropChance >= 0.2 && dropChance < 0.4) {
                lootType = "penalty";
            } else if (dropChance >= 0.0 && dropChance < 0.2) {
                lootType = "power-up";
            }

            if (lootType != null) {
                Loot loot = new Loot(x, y, lootType);
                loots.add(loot);
                root.getChildren().add(loot.getLootImageView());
            }
        }

        private void updateLoots() {
            Iterator<Loot> iterator = loots.iterator();

            while (iterator.hasNext()) {
                Loot loot = iterator.next();
                loot.moveDown();

                // Check if loot collides with the player
                if (loot.getLootImageView().getBoundsInParent().intersects(player.getBoundsInParent())) {
                    root.getChildren().remove(loot.getLootImageView());
                    iterator.remove();

                    if (loot.getType().equals("bonus")) {
                        score += 50; // Increment score
                        scoreText.setText("Score: " + score);
                        lootMessage("bonus");

                    } else if (loot.getType().equals("penalty")) {
                        score -= 50; // Deduct score
                        scoreText.setText("Score: " + score);
                        lootMessage("penalty");

                    } else if (loot.getType().equals("power-up")) {
                        activatePowerUp(); // Activate power-up
                    }


                }

                // Remove loot if it goes off-screen
                if (loot.getLootImageView().getY() > 700) {
                    root.getChildren().remove(loot.getLootImageView());
                    iterator.remove();
                }
            }
        }

        private void shootProjectile() {
            // Create the standard forward projectile
            Projectile forwardProjectile = new Projectile(
                    player.getX() + player.getFitWidth() / 2,
                    player.getY()
            );
            projectiles.add(forwardProjectile);
            root.getChildren().add(forwardProjectile.getProjectileCircle());

            // If power-up is active, create diagonal projectiles
            if (isPowerUpActive) {
                Projectile leftProjectile = new Projectile(
                        player.getX() + player.getFitWidth() / 2,
                        player.getY()
                );
                Projectile rightProjectile = new Projectile(
                        player.getX() + player.getFitWidth() / 2,
                        player.getY()
                );

                // Adjust the diagonal directions
                leftProjectile.setDiagonalDirection(-1); // Left
                rightProjectile.setDiagonalDirection(1); // Right

                projectiles.add(leftProjectile);
                projectiles.add(rightProjectile);

                root.getChildren().add(leftProjectile.getProjectileCircle());
                root.getChildren().add(rightProjectile.getProjectileCircle());
            }
        }

        private void updateProjectiles() {
            Iterator<Projectile> iterator = projectiles.iterator();
            while (iterator.hasNext()) {
                Projectile projectile = iterator.next();
                projectile.moveUp();

                if (projectile.getProjectileCircle().getCenterY() < 0) {
                    root.getChildren().remove(projectile.getProjectileCircle());
                    iterator.remove();
                }
            }
        }

        private void lootMessage(String loot) {
            lootMessageTime = System.currentTimeMillis();


            if (loot == "bonus") {
            Text lootMessage = new Text("+50 Bonus Score");
            lootMessage.setStyle("-fx-font-size: 10px; -fx-fill: green;");
            lootMessage.setX(10);
            lootMessage.setY(65);
            root.getChildren().add(lootMessage);

            //
            new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (System.currentTimeMillis() - lootMessageTime >= 2000) {

                        root.getChildren().remove(lootMessage); // Remove message
                        stop();
                    }
                }
            }.start();
        }
            else if (loot == "penalty") {
                Text lootMessage = new Text("-50 Penalty Score");
                lootMessage.setStyle("-fx-font-size: 10px; -fx-fill: red;");
                lootMessage.setX(10);
                lootMessage.setY(75);
                root.getChildren().add(lootMessage);

                //
                new AnimationTimer() {
                    @Override
                    public void handle(long now) {
                        if (System.currentTimeMillis() - lootMessageTime >= 2000) {

                            root.getChildren().remove(lootMessage); // Remove message
                            stop();
                        }
                    }
                }.start();
            }










        }

        private void activatePowerUp() {
            isPowerUpActive = true;
            powerUpStartTime = System.currentTimeMillis();

            // Show power-up message
            Text powerUpMessage = new Text("Power-Up Activated!");
            powerUpMessage.setStyle("-fx-font-size: 10px; -fx-fill: green;");
            powerUpMessage.setX(10);
            powerUpMessage.setY(55);
            root.getChildren().add(powerUpMessage);

            // Schedule power-up deactivation after 5 seconds
            new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (System.currentTimeMillis() - powerUpStartTime >= 5000) {
                        isPowerUpActive = false;
                        root.getChildren().remove(powerUpMessage); // Remove message
                        stop();
                    }
                }
            }.start();
        }

        private void spawnEnemy() {
            ImageView enemyImageView = new ImageView(enemyImage); // Use the preloaded enemy image
            enemyImageView.setFitWidth(50);
            enemyImageView.setFitHeight(50);
            enemyImageView.setX(Math.random() * (800 - 50)); // Random x position within screen bounds
            enemyImageView.setY(0); // Start at the top
            root.getChildren().add(enemyImageView);

            // Add to the enemies list
            enemies.add(new Enemy(enemyImageView));
        }

        private void updateEnemies() {
            Iterator<Enemy> iterator = enemies.iterator();

            while (iterator.hasNext()) {
                Enemy enemy = iterator.next();
                enemy.moveDown();

                // Check collision with player
                if (enemy.getEnemyImageView().getBoundsInParent().intersects(player.getBoundsInParent())) {
                    endGame(); // Trigger game over if enemy collides with player
                    return;
                }

                // Remove enemy if it goes off-screen
                if (enemy.getEnemyImageView().getY() > 700) {
                    root.getChildren().remove(enemy.getEnemyImageView());
                    iterator.remove();
                }
            }
        }

        private void checkPlayerEnemyCollision() {
            // Check if any enemy collides with the player
            for (Enemy enemy : enemies) {
                if (player.getBoundsInParent().intersects(enemy.getEnemyImageView().getBoundsInParent())) {
                    endGame(); // End the game if collision detected
                    return;
                }
            }
        }

        private void checkCollisions() {
            Iterator<Projectile> projectileIterator = projectiles.iterator();
            while (projectileIterator.hasNext()) {
                Projectile projectile = projectileIterator.next();

                Iterator<Enemy> enemyIterator = enemies.iterator();
                while (enemyIterator.hasNext()) {
                    Enemy enemy = enemyIterator.next();
                        // Collision Check for projectile and enemy
                    if (projectile.getProjectileCircle().getBoundsInParent().intersects(enemy.getEnemyImageView().getBoundsInParent())) {
                        // Collision detectection and spawning loot
                        spawnLoot(enemy.getEnemyImageView().getX(), enemy.getEnemyImageView().getY()); // Spawn loot
                        root.getChildren().removeAll(projectile.getProjectileCircle(), enemy.getEnemyImageView());
                        // Removing used projectile
                        projectileIterator.remove();
                        // Removing shooted enemy
                        enemyIterator.remove();

                        // Update score
                        score += 100;
                        scoreText.setText("Score: " + score);
                        break;
                    }
                }
            }
        }

        public void endGame() {
            gameLoop.stop();
            new GameOverScreen(stage, score).show();
        }

        public void show() {
            stage.setScene(scene);
            stage.show();
        }
    }

    public static class Enemy {
        private ImageView enemyImageView;

        public Enemy(ImageView enemyImageView) {
            // Use the provided ImageView
            this.enemyImageView = enemyImageView;
        }

        public ImageView getEnemyImageView() {
            return enemyImageView;
        }

        public void moveDown() {
            // Move the enemy down
            enemyImageView.setY(enemyImageView.getY() + 2);
        }
    }

    public static class Loot {
        private ImageView lootImageView;
        private String type; // E.g., "bonus", "penalty", "power-up"

        public Loot(double x, double y, String type) {
            this.type = type;

            // Assign file paths based on type
            if (type.equals("bonus")) {
                String filePath = new File("src/assets/reward.png").toURI().toString();
                Image lootImage = new Image(filePath);
                lootImageView = new ImageView(lootImage);
                lootImageView.setFitWidth(25);
                lootImageView.setFitHeight(25);
                lootImageView.setX(x);
                lootImageView.setY(y);
            } else if (type.equals("penalty")) {
                String filePath = new File("src/assets/punishment.png").toURI().toString();
                Image lootImage = new Image(filePath);
                lootImageView = new ImageView(lootImage);
                lootImageView.setFitWidth(25);
                lootImageView.setFitHeight(25);
                lootImageView.setX(x);
                lootImageView.setY(y);
            } else if (type.equals("power-up")) {
                String filePath = new File("src/assets/reward.png").toURI().toString();
                Image lootImage = new Image(filePath);
                lootImageView = new ImageView(lootImage);
                lootImageView.setFitWidth(25);
                lootImageView.setFitHeight(25);
                lootImageView.setX(x);
                lootImageView.setY(y);
            }



        }

        public ImageView getLootImageView() {
            return lootImageView;
        }

        public String getType() {
            return type;
        }

        public void moveDown() {

            lootImageView.setY(lootImageView.getY() + 3); // Move loot down (faster than enemy)
        }
    }

    public static class Projectile {
        private Circle projectileCircle;
        private int diagonalDirection = 0; // 0 = straight, -1 = left, 1 = right

        public Projectile(double x, double y) {
            projectileCircle = new Circle(4); // Radius of the projectile
            projectileCircle.setFill(Color.BLUE); // Set the color of the projectile
            projectileCircle.setCenterX(x); // Set the initial X position
            projectileCircle.setCenterY(y); // Set the initial Y position
        }

        public Circle getProjectileCircle() {

            return projectileCircle;
        }

        public void moveUp() {
            projectileCircle.setCenterY(projectileCircle.getCenterY() - 10); // Move the projectile upward

            // Add diagonal movement if applicable
            if (diagonalDirection != 0) {
                projectileCircle.setCenterX(projectileCircle.getCenterX() + (diagonalDirection * 5));
            }
        }

        public void setDiagonalDirection(int direction) {

            this.diagonalDirection = direction;
        }
    }

    public static class GameOverScreen {
        private Stage stage;
        private Scene scene;
        private Pane root;
        private int score;

        public GameOverScreen(Stage stage, int score) {
            this.stage = stage;
            this.score = score;
            initialize();
        }

        private void initialize() {
            root = new Pane();
            root.setStyle("-fx-background-color: black;");

            // Game Over Text
            Text gameOverText = new Text("GAME OVER");
            gameOverText.setStyle("-fx-font-size: 50px; -fx-fill: red; -fx-font-weight: bold;");
            gameOverText.setX(200);
            gameOverText.setY(200);

            // Score Display
            Text scoreText = new Text("Your Score: " + score);
            scoreText.setStyle("-fx-font-size: 30px; -fx-fill: white;");
            scoreText.setX(250);
            scoreText.setY(300);

            // Restart Instruction
            Text restartText = new Text("Press R to Restart");
            restartText.setStyle("-fx-font-size: 20px; -fx-fill: white;");
            restartText.setX(250);
            restartText.setY(400);

            // Return Main Menu Instruction
            Text returnMenuText = new Text("Press ESC to Menu");
            returnMenuText.setStyle("-fx-font-size: 20px; -fx-fill: white;");
            returnMenuText.setX(250);
            returnMenuText.setY(420);

            root.getChildren().addAll(gameOverText, scoreText, restartText, returnMenuText);

            this.scene = new Scene(root, 800, 700);
            this.scene.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case R:
                        System.out.println("Restarting Game");
                        new GameScreen(stage).show(); // Transition logic
                        break;
                    case ESCAPE:
                        System.out.println("Returning Menu");
                        new TitleScreen(stage).show();
                        break;
                }
            });
        }

        public void show() {
            stage.setScene(scene);
            stage.show();
        }
    }
}
