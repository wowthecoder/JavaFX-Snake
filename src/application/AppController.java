package application;

import javafx.application.*;
import javafx.geometry.Pos;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.shape.*;
import javafx.event.*;
import java.io.*;

public class AppController extends Application {
	Stage stage;
	int modeNum = 1;
    long CfirstBest, CsecondBest, CthirdBest, CfourthBest, CfifthBest;
	long WfirstBest, WsecondBest, WthirdBest, WfourthBest, WfifthBest;
    public static void main(String[] args) {
    	launch(args);
    }
    
    public void start(Stage primaryStage) {
    	try {
    		stage = new Stage();
            
        	Scene menu = setup();
        	menu.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        	stage.setScene(menu);
        	stage.getIcons().add(new Image("resources/snake-icon.jpg"));
    		stage.setTitle("SNAKE GAME");
    		stage.show();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public void mode(String titleText) {
    	if (titleText.equals("War Mode"))
    		modeNum = 2;
    	
    	BorderPane root2 = new BorderPane();
    	root2.getChildren().clear();
    	
    	Label title = new Label(titleText);
    	title.getStyleClass().add("modeTitle");
    	
    	Button beginnerBtn = new Button("Beginner");
    	beginnerBtn.getStyleClass().add("modeButtons");
    	beginnerBtn.setOnAction(e -> startGame(modeNum, 4));
    	
    	Button easyBtn = new Button("Easy");
    	easyBtn.getStyleClass().add("modeButtons");
    	easyBtn.setOnAction(e -> startGame(modeNum, 5));
    	
    	Button mediumBtn = new Button("Medium");
    	mediumBtn.getStyleClass().add("modeButtons");
    	mediumBtn.setOnAction(e -> startGame(modeNum, 6));
    	
    	Button hardBtn = new Button("Hard");
    	hardBtn.getStyleClass().add("modeButtons");
    	hardBtn.setOnAction(e -> startGame(modeNum, 8));
    	
    	Button insaneBtn = new Button("Insane");
    	insaneBtn.getStyleClass().add("modeButtons");
    	insaneBtn.setOnAction(e -> startGame(modeNum, 10));
    	
    	VBox pane = new VBox(title, beginnerBtn, easyBtn, mediumBtn, hardBtn, insaneBtn);
    	pane.setAlignment(Pos.TOP_CENTER);
    	pane.setSpacing(10);
    	
    	root2.setLeft(pane);
    	Scene scene = new Scene(root2, 800, 500);
    	scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
    	stage.setScene(scene);
    }
    
    public void highScores() {
    	//Classic Mode highScores
    	try {
    		BufferedReader br = new BufferedReader(new FileReader("data\\classicdata.txt"));
    		String classicModeData = br.readLine();
    	    String[] highScores = classicModeData.split("/%/");
    	    CfirstBest = Long.parseLong(highScores[0]);
    	    CsecondBest = Long.parseLong(highScores[1]);
    	    CthirdBest = Long.parseLong(highScores[2]);
    	    CfourthBest = Long.parseLong(highScores[3]);
    	    CfifthBest = Long.parseLong(highScores[4]);
    	    br.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    		CfirstBest = 0;
    		CsecondBest = 0;
    		CthirdBest = 0;
    		CfourthBest = 0;
    		CfifthBest = 0;
    	}
    	// War mode high scores
    	try {
    		BufferedReader br = new BufferedReader(new FileReader("data\\wardata.txt"));
    		String warModeData = br.readLine();
    	    String[] highScores = warModeData.split("/%/");
    	    WfirstBest = Long.parseLong(highScores[0]);
    	    WsecondBest = Long.parseLong(highScores[1]);
    	    WthirdBest = Long.parseLong(highScores[2]);
    	    WfourthBest = Long.parseLong(highScores[3]);
    	    WfifthBest = Long.parseLong(highScores[4]);
    	    br.close();
    	} catch (Exception e) {
    		WfirstBest = 0;
    		WsecondBest = 0;
    		WthirdBest = 0;
    		WfourthBest = 0;
    		WfifthBest = 0;
    	}
    	Label classicTitle = new Label("Classic Mode");
    	classicTitle.getStyleClass().add("highScoreTitle");
    	
    	Label firstC = new Label("1st: " + CfirstBest);
    	firstC.getStyleClass().add("highScoreLabel");
    	
    	Label secondC = new Label("2nd: " + CsecondBest);
    	secondC.getStyleClass().add("highScoreLabel");
    	
    	Label thirdC = new Label("3rd: " + CthirdBest);
    	thirdC.getStyleClass().add("highScoreLabel");
    	
    	Label fourthC = new Label("4th: " + CfourthBest);
    	fourthC.getStyleClass().add("highScoreLabel");
    	
    	Label fifthC = new Label("5th: " + CfifthBest);
    	fifthC.getStyleClass().add("highScoreLabel");
    	
    	Button backToMenu = new Button("Back to Menu");
    	backToMenu.getStyleClass().add("gameButtons");
    	backToMenu.setOnAction(new EventHandler<ActionEvent>() {
    		public void handle(ActionEvent ae) {
    			Scene menu = setup();
    			menu.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
    			stage.setScene(menu);
    		}
    	});
    	
    	VBox classicBox = new VBox(classicTitle, firstC, secondC, thirdC, fourthC, fifthC, backToMenu);   	
    	classicBox.setSpacing(20);
    	
    	Rectangle verticalLine = new Rectangle(10, 580);
    	
    	Label warTitle = new Label("War Mode");
    	warTitle.getStyleClass().add("highScoreTitle");
    	
    	Label firstW = new Label("1st: " + WfirstBest);
    	firstW.getStyleClass().add("highScoreLabel");
    	
    	Label secondW = new Label("2nd: " + WsecondBest);
    	secondW.getStyleClass().add("highScoreLabel");
    	
    	Label thirdW = new Label("3rd: " + WthirdBest);
    	thirdW.getStyleClass().add("highScoreLabel");
    	
    	Label fourthW = new Label("4th: " + WfourthBest);
    	fourthW.getStyleClass().add("highScoreLabel");
    	
    	Label fifthW = new Label("5th: " + WfifthBest);
    	fifthW.getStyleClass().add("highScoreLabel");
    	
    	VBox warBox = new VBox(warTitle, firstW, secondW, thirdW, fourthW, fifthW);
    	warBox.setSpacing(20);
    	
    	HBox root = new HBox(classicBox, verticalLine, warBox);
    	root.setSpacing(50);
    	root.setAlignment(Pos.TOP_LEFT);
    	
    	Scene scoresDisplay = new Scene(root, 1000, 600);
    	scoresDisplay.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
    	stage.setScene(scoresDisplay);
    }
    
    public void startGame(int mode, int speed) {
    	//Classic Mode
    	if (mode == 1) 
    	{
    		ClassicMode cm = new ClassicMode();
    		cm.start(this, speed);
    	}
    	// War Mode
    	else
    	{
    		WarMode wm = new WarMode();
    		wm.start(this, speed, 0);
    	    /*SecondWarMap swm = new SecondWarMap();
    		swm.start(this, speed, 816);*/
    		/*ThirdWarMap twm = new ThirdWarMap();
    		twm.start(this, speed, 1602);*/
    	}
    }
    
    public Scene setup() {
    	BorderPane root = new BorderPane();
    	Font.loadFont(AppController.class.getResource("Lobster-Regular.ttf").toExternalForm(), 36);
    	Font.loadFont(AppController.class.getResource("PressStart2P-Regular.ttf").toExternalForm(), 36);
    	
    	ImageView img = new ImageView(new Image("resources/gameTitle.png"));
        img.setFitWidth(450);
        img.setPreserveRatio(true);
        Button classicBtn = new Button("Classic Mode");
        classicBtn.getStyleClass().add("choose");
        classicBtn.setOnAction(e -> mode("Classic Mode"));
        
        Button warBtn = new Button("War Mode");
        warBtn.getStyleClass().add("choose");
        warBtn.setOnAction(e -> mode("War Mode"));
        
        Button highScoresBtn = new Button("High Scores");
        highScoresBtn.getStyleClass().add("choose");
        highScoresBtn.setOnAction(e -> highScores());
        
        VBox display = new VBox(img, classicBtn, warBtn, highScoresBtn);
        display.setSpacing(20);
        display.setAlignment(Pos.TOP_CENTER);
		root.setLeft(display);
		
		Scene menu = new Scene(root, 800, 500);
		menu.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		return menu;
    }
}
