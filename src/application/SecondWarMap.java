package application;
	
import java.util.*;
import java.io.*;
import application.ClassicMode.Corner;
import application.ClassicMode.Dir;
import javafx.animation.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.scene.control.*;


public class SecondWarMap {
	//variables
	static int speed = 5;
	static int score = 800;
	static int scoreAdd = 10;
	static int scoreRequirement = 1600;
	static int foodcolor = 0;
	static int width = 30;
	static int height = 30;
	static int foodX = 0;
	static int foodY = 0;
	static int BonusX = 0;
	static int BonusY = 0;
	static boolean gotBonus = false;
	static int cornersize = 20;
	static List<Corner> snake = new ArrayList<>();
	static Dir direction = Dir.left;
	static boolean gameOver = false;
	static Random rand = new Random();
	static AppController appController;
	static Pane root;
	static AnimationTimer timer;
	static Button newGame, backToMenu;
	static int foodCount;
	static long BonusStartTime;
	static Circle gemImage;
	static Rectangle sliderBackground;
	static Rectangle sliderFill;;
	static long firstBest, secondBest, thirdBest, fourthBest, fifthBest;
	static Label nextLvSliderLabel;
	static ProgressBar nextLvSlider;
	static HBox sliderBox;
	static ArrayList<Corner> walls;

	
	public enum Dir {
		left, right, up, down;
	}
	
	public static class Corner {
		int x;
		int y;
		public Corner(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	public void start(AppController appC, int speed, int score) {
		try {
			walls = new ArrayList<Corner>();
			newFood();
			if (speed == 4)
				scoreAdd = 5;
			else if (speed == 5)
				scoreAdd = 8;
			else if (speed == 6)
				scoreAdd = 10;
			else if (speed == 8)
				scoreAdd = 12;
			else if (speed == 10)
				scoreAdd = 15;
			this.score = score;
	        scoreRequirement = ((int)Math.floor(score/800) + 1) * 800;
	        this.speed = speed;
			this.speed += ((scoreRequirement / 800) - 1);
			direction = Dir.left;
			
			gemImage = new Circle(30, height*cornersize+145,25,Color.RED);
			sliderBackground = new Rectangle(80, height*cornersize+125, 500, 40);
			sliderBackground.setFill(Color.WHITE);
			sliderFill = new Rectangle(80, height*cornersize+125, 500, 40);
			sliderFill.setFill(Color.RED);
			
			Font.loadFont(WarMode.class.getResource("PressStart2P-Regular.ttf").toExternalForm(), 36);
			
			Canvas c = new Canvas(width*cornersize, height*cornersize);
			GraphicsContext gc = c.getGraphicsContext2D();
			root = new Pane(c);
			
			// next level progress bar
			nextLvSliderLabel = new Label("Next\nLevel");
			nextLvSliderLabel.getStyleClass().add("nextLevelSliderLabel");
			nextLvSlider = new ProgressBar();
			//nextLvSlider.getStyleClass().add("nextLevelProgressBar");
			nextLvSlider.setPrefSize(430, 40);
			sliderBox = new HBox(nextLvSliderLabel, nextLvSlider);
			sliderBox.setSpacing(20);
			sliderBox.setLayoutX(5);
			sliderBox.setLayoutY(height*cornersize+20);
			root.getChildren().add(sliderBox);
			
			timer = new AnimationTimer() {
				long lastTick = 0;
				public void handle(long now) {
					if (lastTick == 0)
					{
						lastTick = now;
						tick(gc);
						addWalls();
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {

							e.printStackTrace();
						}
						return;
					}
					if (now - lastTick > 500000000 / speed)
					{
						lastTick = now;
					    tick(gc);
					}
				}
			};
			timer.start();
			
			Scene scene = new Scene(root,width*cornersize,(height+2)*cornersize+150);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			//control
			scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
				if (key.getCode() == KeyCode.W || key.getCode() == KeyCode.UP) 
				{
					if (direction != Dir.down)
					    direction = Dir.up;
				}
				if (key.getCode() == KeyCode.A || key.getCode() == KeyCode.LEFT) 
				{
					if (direction != Dir.right)
					    direction = Dir.left;
				}
				if (key.getCode() == KeyCode.S || key.getCode() == KeyCode.DOWN) 
				{
					if (direction != Dir.up)
					    direction = Dir.down;
				}
				if (key.getCode() == KeyCode.D || key.getCode() == KeyCode.RIGHT) 
				{
					if (direction != Dir.left)
					    direction = Dir.right;
				}
			});
			
			//add start snake parts
			snake.add(new Corner(width/2, height/2));
			snake.add(new Corner(width/2, height/2));
			snake.add(new Corner(width/2, height/2));
			
			loadHighScores();
			
			appController = appC;
			appController.stage.setScene(scene);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadHighScores() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("data\\wardata.txt"));
			String warScores = br.readLine();
			String[] highScores = warScores.split("/%/");
			firstBest = Long.parseLong(highScores[0]);
			secondBest = Long.parseLong(highScores[1]);
			thirdBest = Long.parseLong(highScores[2]);
			fourthBest = Long.parseLong(highScores[3]);
			fifthBest = Long.parseLong(highScores[4]);
		} catch (Exception e) {
			e.printStackTrace();
			firstBest = 0;
			secondBest = 0;
			thirdBest = 0;
			fourthBest = 0;
			fifthBest = 0;
		}
	}
	
	
	public static void restart() {
		gameOver = false;
		snake.clear();
		score = 0;
		WarMode wm = new WarMode();
		wm.start(appController, speed, 0);
	}	
	
	public static void addWalls() {
		// top left L shape
		for (int i = 0; i < 7; i++)
		{
			walls.add(new Corner(i, 0));
		}
		for (int i = 0; i < 6; i++) 
		{
			walls.add(new Corner(0, i+1));
		}
		// top right L shape
		for (int i = 0; i < 7; i++)
		{
			walls.add(new Corner(width-i-1, 0));
		}
		for (int i = 0; i < 6; i++)
		{
			walls.add(new Corner(width-1, i+1));
		}
		//bottom left L shape
		for (int i = 0; i < 7; i++)
		{
			walls.add(new Corner(i, height-1));
		}
		for (int i = height-2; i > height-8; i--)
		{
			walls.add(new Corner(0, i));
		}
		//bottom right L shape
		for (int i = 0; i < 7; i++)
		{
			walls.add(new Corner((width-i-1), height-1));
		}
		for (int i = height-2; i > height-8; i--)
		{
			walls.add(new Corner(width-1, i));
		}
		//The = shape in the middle
		for (int i = 0; i < 18; i++)
		{
			walls.add(new Corner(i+6, 10));
			walls.add(new Corner(i+6, 11));
			walls.add(new Corner(i+6, 18));
			walls.add(new Corner(i+6, 19));
		}
	}
	
	//tick
	public static void tick(GraphicsContext gc) {
		if (gameOver)
		{
			gc.setFill(Color.RED);
			gc.setFont(new Font("", 50));
			gc.fillText("GAME OVER", 150, 250); // somewhere in the middle of the screen
			// check and write high scores to file
						if (score > firstBest)
						{
							//every score move down one step
							fifthBest = fourthBest;
							fourthBest = thirdBest;
							thirdBest = secondBest;
							secondBest = firstBest;
							firstBest = score;
						}
						else if (score > secondBest) 
						{
							//Same thing, but no need to modify firstBest
							fifthBest = fourthBest;
							fourthBest = thirdBest;
							thirdBest = secondBest;
							secondBest = score;
						}
						else if (score > thirdBest)
						{
							//Same thing, but no need to modify firstBest and secondBest
							fifthBest = fourthBest;
							fourthBest = thirdBest;
							thirdBest = score;
						}
						else if (score > fourthBest)
						{
							//Same thing, but no need to modify firstBest and secondBest and thirdBest
							fifthBest = fourthBest;
							fourthBest = score;
						}
						else if (score > fifthBest)
						{
							// Just modify fifthBest only
							fifthBest = score;
						}
						//Now after checking and modifying the scores we write them to file
						try {
							File file = new File("data\\wardata.txt");
							FileWriter writer = new FileWriter(file);
							writer.write(firstBest + "/%/"+ secondBest + "/%/"+ thirdBest + "/%/"+ fourthBest + "/%/"+ fifthBest);
							writer.close();
						} catch (Exception e) {
							e.printStackTrace();
						}			
			newGame = new Button("New Game");
			newGame.setOnAction(e -> restart());
			backToMenu = new Button("Back to menu");
			backToMenu.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					gameOver = false;
					score = 0;
					foodCount = 0;
					snake.clear();
					Scene scene = appController.setup();
					appController.stage.setScene(scene);
				}
			});
			newGame.setLayoutX(190);
			newGame.setLayoutY(270);
			newGame.getStyleClass().add("gameButtons");
			backToMenu.setLayoutX(190);
			backToMenu.setLayoutY(330);
			backToMenu.getStyleClass().add("gameButtons");
			root.getChildren().addAll(newGame, backToMenu);
			timer.stop();
			return;
		}
		
		for (int i = snake.size() - 1; i >= 1; i--) {
			snake.get(i).x = snake.get(i-1).x;
			snake.get(i).y = snake.get(i-1).y;
		}
		
		//Game Over if the snake touches the border
		switch(direction) {
		case up: 
			snake.get(0).y--;
			if (snake.get(0).y < 0)
				snake.get(0).y = height-1;
			break;
		case down: 
			snake.get(0).y++;
			if (snake.get(0).y > (height-1))
				snake.get(0).y = 0;
			break;
		case left: 
			snake.get(0).x--;
			if (snake.get(0).x < 0)
				snake.get(0).x = width-1;
			break;
		case right: 
			snake.get(0).x++;
			if (snake.get(0).x > (width-1))
				snake.get(0).x = 0;
			break;
		}
		
		//eat food
		if (foodX == snake.get(0).x && foodY == snake.get(0).y)
		{
	    	snake.add(new Corner(-1, -1));
			score += scoreAdd;
			// Bonus red food
			foodCount++;
			if (foodCount >= 7)
			{
				newBonusFood();
				foodCount = 0;
				gotBonus = true;
			}
			newFood();
		}
		
		//eat Bonus food
		if (gotBonus && ((snake.get(0).x == BonusX && snake.get(0).y == BonusY) || (snake.get(0).x == BonusX+1 && snake.get(0).y == BonusY)
					|| (snake.get(0).x == BonusX && snake.get(0).y == BonusY+1) || (snake.get(0).x == BonusX+1 && snake.get(0).y == BonusY+1)))
		{
			snake.add(new Corner(-1, -1));
			snake.add(new Corner(-1, -1));
			snake.add(new Corner(-1, -1));
			score += (scoreAdd * 4);
			gotBonus = false;
		}
		
		//Game over if snake hits itself
		for (int i = 1; i < snake.size(); i++)
		{
			if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y)
				gameOver = true;
		}
		
		//Game over if snake hits one of the walls
		for (Corner w : walls)
		{
			if (snake.get(0).x == w.x && snake.get(0).y == w.y)
			{
				gameOver = true;
				break;
			}
		}
		
		//fill 
		// background
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, width*cornersize, height*cornersize);
		
		// walls
		// save wall x and y positions
		gc.setFill(Color.DARKORANGE);
		// top left L shape
		for (int i = 0; i < 7; i++)
		{
			gc.fillRect(i*cornersize, 0, (cornersize-2), (cornersize-2));
		}
		for (int i = 0; i < 6; i++) 
		{
			gc.fillRect(0, (i+1)*cornersize, (cornersize-2), (cornersize-2));
		}
		// top right L shape
		for (int i = 0; i < 7; i++)
		{
			gc.fillRect((width-i-1)*cornersize, 0, (cornersize-2), (cornersize-2));
		}
		for (int i = 0; i < 6; i++)
		{
			gc.fillRect((width-1)*cornersize, (i+1)*cornersize, (cornersize-2), (cornersize-2));
		}
		//bottom left L shape
		for (int i = 0; i < 7; i++)
		{
			gc.fillRect(i*cornersize, (height-1)*cornersize, (cornersize-2), (cornersize-2));
		}
		for (int i = height-2; i > height-8; i--)
		{
			gc.fillRect(0, i*cornersize, (cornersize-2), (cornersize-2));
		}
		//bottom right L shape
		for (int i = 0; i < 7; i++)
		{
			gc.fillRect((width-i-1)*cornersize, (height - 1)*cornersize, (cornersize-2), (cornersize-2));
		}
		for (int i = height-2; i > height-8; i--)
		{
			gc.fillRect((width-1)*cornersize, i*cornersize, (cornersize-2), (cornersize-2));
		}
		//The = shape in the middle
		for (int i = 0; i < 18; i++)
		{
			gc.fillRect((i+6)*cornersize, 10*cornersize, (cornersize-2), (cornersize-2));
			gc.fillRect((i+6)*cornersize, 11*cornersize, (cornersize-2), (cornersize-2));
			gc.fillRect((i+6)*cornersize, 18*cornersize, (cornersize-2), (cornersize-2));
			gc.fillRect((i+6)*cornersize, 19*cornersize, (cornersize-2), (cornersize-2));
		}
		
		//score
		gc.setFill(Color.WHITE);
		gc.setFont(new Font("", 30));// 30 is the size
		gc.fillText("Score: " + score, 10, 30);// 10 and 30 is the x and y position
		
		//To next level progress slider
		nextLvSlider.setProgress(((double)(score - scoreRequirement + 800) / (double)800)); // 800 is the score to next level
		if (score >= scoreRequirement)
		{
			timer.stop();
			snake.clear();
			gameOver = false;
			foodCount = 0;
			ThirdWarMap twm = new ThirdWarMap();
			twm.start(appController, speed, score);
			score = 0;
		}
	    
		//food color
		Color cc = Color.WHITE;
		switch(foodcolor) {
		case 0: 
			cc = Color.PURPLE;
		    break;
		case 1: 
			cc = Color.BLUE;
		    break;
		case 2: 
			cc = Color.DARKCYAN;
		    break;
		case 3: 
			cc = Color.PINK;
		    break;
		case 4:
			cc = Color.WHITE;
			break;
		}
		gc.setFill(cc);
		gc.fillOval(foodX * cornersize, foodY * cornersize, cornersize, cornersize);
		// bonus food 
				if (gotBonus)
				{
					gc.setFill(Color.RED);
					gc.fillOval(BonusX * cornersize, BonusY * cornersize, cornersize * 2, cornersize * 2);
					long currentTime = System.nanoTime();
					long timeElapsed = currentTime - BonusStartTime;
					if (!root.getChildren().contains(gemImage))
					    root.getChildren().add(gemImage);
					if (!root.getChildren().contains(sliderBackground))
					    root.getChildren().add(sliderBackground);	
					if (!root.getChildren().contains(sliderFill))
					    root.getChildren().add(sliderFill);	
				    else
				    {
				    	double fillLength = ((double)(5000000000L - timeElapsed) / (double)5000000000L) * 500; // 500 is the original length of the slider
				    	System.out.println(timeElapsed+" "+fillLength);
				    	sliderFill.setWidth(fillLength);
				    }
				}
				else 
				{
					root.getChildren().remove(gemImage);
					root.getChildren().remove(sliderBackground);
					root.getChildren().remove(sliderFill);
				}
	    
		//snake(light green as shadow and green as foreground)
		for (Corner c : snake) 
		{
			gc.setFill(Color.LIGHTGREEN);
			gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize-1, cornersize-1);
			gc.setFill(Color.GREEN);
			gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize-2, cornersize-2);
		}
	}
	
	//food 
	public static void newFood() {
		start: while(true) {
			foodX = rand.nextInt(width);
			foodY = rand.nextInt(height);
			
			for (Corner c : snake) {
				if (c.x == foodX && c.y == foodY)
					continue start;
			}
			for (Corner w: walls) {
				if (w.x == foodX && w.y == foodY)
					continue start;
			}
			foodcolor = rand.nextInt(5);
			//speed++;
			break;
		}
	}
	
	public static void newBonusFood() {
		start: while(true) {
			BonusX = rand.nextInt(width-1);
			BonusY = rand.nextInt(height-1);
			
			for (Corner c : snake) {
				if ((c.x == BonusX && c.y == BonusY) || (c.x == BonusX+1 && c.y == BonusY) || (c.x == BonusX && c.y == BonusY+1) || (c.x == BonusX+1 && c.y == BonusY+1))
					continue start;
			}
			for (Corner w : walls) {
				if ((w.x == BonusX && w.y == BonusY) || (w.x == BonusX+1 && w.y == BonusY) || (w.x == BonusX && w.y == BonusY+1) || (w.x == BonusX+1 && w.y == BonusY+1))
					continue start;
			}
			//speed++;
			break;
		}
	    Timer timer = new Timer();
        TimerTask task = new TimerTask() {
	        public void run() {
		        gotBonus = false;
	        }
        };
        timer.schedule(task, 5000);
        BonusStartTime = System.nanoTime();
	}
}
