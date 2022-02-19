package picturepuzzle;

import java.io.FileNotFoundException;
import javafx.scene.input.*;
import javafx.application.Application;
import javafx.event.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.*;

public class App extends Application 
{
    @Override
    public void start(Stage primaryStage) throws FileNotFoundException
    {
        PicturePuzzleGame game = new PicturePuzzleGame();

        /**
         * The Main Menu screen.
         * Contains all of the instructions, and level select needed.
         * Level select adapts to the puzzles array.
         * Any game options go here.
         */
        Pane mainMenu = new Pane();
        ToggleGroup levelSelect = new ToggleGroup();

        //generate the level select javafx radio buttons based on the puzzles in the game object
        RadioButton[] radioButtons = new RadioButton[16];
        //FULFILLING UNIT 4 VI: This for loop executes once for each puzzle in the game. Each puzzle gets a radio button as the option to select that puzzle to play.
        for(int i = 0; i < game.getPuzzleArray().length; i++)
        {
            radioButtons[i] = new RadioButton(game.getPuzzleArray()[i].getName());
            radioButtons[i].setToggleGroup(levelSelect);
            radioButtons[i].setLayoutX(90);
            radioButtons[i].setLayoutY(200 + (55 * i));
            radioButtons[i].setFont(game.getButtonFont());

            //Preselect the first radio button (so the main menu doesn't load with no option selected)
            if(i == 0)
                radioButtons[i].setSelected(true);
            
            //Adds click functionality to the radio buttons to change the preview image
            radioButtons[i].setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    //Determines which radio button is selected event.getTarget().getText()
                    int index = -1;
                    for(int i = 0; i < radioButtons.length; i++)
                    {
                        if(radioButtons[i].equals(event.getTarget()))
                        {
                            index = i;
                            break;
                        }
                    }
                    
                    game.setActivePuzzle(index);
                }
            });

            mainMenu.getChildren().add(radioButtons[i]);
        }
        //Make the description for the radio buttons
        Text radioButtonInstruction = new Text(90, 170, "Puzzle Select");
        radioButtonInstruction.setFont(new Font(35));
        mainMenu.getChildren().addAll(game.getPreviewImageView(), game.getPreviewImageText(), radioButtonInstruction);
        Scene mainMenuScene = new Scene(mainMenu, 1920, 1080);

        /**
         * The actual picture puzzle game screen.
         * This contains all of the actual game.
         * This scene is modified to the puzzle object currently active.
         * Also contains all of the necessary controls/buttons to reset or go back to the main menu.
         */
        Pane gameScreen = new Pane();

        //handles deselecting puzzle images when the escape key is pressed.
        gameScreen.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent event)
            {
                //Checks for if the key pressed was the escape key, then handles unselecting the puzzle item.
                if(event.getCode().getName().equals("Esc"))
                {
                    game.deselectImage();
                    System.out.println("Deselected image");
                }
            }
        });

        //The button to shuffle the puzzle
        Button shuffleButton = new Button("Shuffle Puzzle");
        shuffleButton.setLayoutX(50);
        shuffleButton.setLayoutY(535);
        shuffleButton.setFont(game.getButtonFont());
        shuffleButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                game.shuffle();
            }
        });
        gameScreen.getChildren().add(shuffleButton);

        //The button to reset the puzzle
        Button resetButton = new Button("Reset Puzzle");
        resetButton.setLayoutX(50);
        resetButton.setLayoutY(635);
        resetButton.setFont(game.getButtonFont());
        resetButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                game.reset();
            }
        });
        gameScreen.getChildren().add(resetButton);

        gameScreen.getChildren().addAll(game.getImageViewGameObjects());
        gameScreen.getChildren().addAll(game.getPuzzleDescriptionText(), game.getFeedbackMessageText());
        Scene gameScreenScene = new Scene(gameScreen, 1920, 1080);
        gameScreenScene.getStylesheets().add("picturepuzzle/game.css");

        /* 
            Buttons for scene control
            These buttons need to be after the two scenes are declared so they can change the screen scene.
        */
        //The button to go back to the main menu
        Button goToMainMenu = new Button("Main Menu");
        goToMainMenu.setLayoutX(50);
        goToMainMenu.setLayoutY(735);
        goToMainMenu.setFont(game.getButtonFont());
        goToMainMenu.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                if(game.isAllowButtons())
                {
                    game.reset();
                    primaryStage.setScene(mainMenuScene);
                    primaryStage.setTitle("Picture Puzzle Game");
                }
            }
        });
        gameScreen.getChildren().add(goToMainMenu);

        //The button to go to the puzzle game screen scene
        Button goToGameScreen = new Button("Play the puzzle");
        goToGameScreen.setLayoutX(90);
        goToGameScreen.setLayoutY(700);
        goToGameScreen.setFont(game.getButtonFont());
        goToGameScreen.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                primaryStage.setScene(gameScreenScene);
                primaryStage.setTitle("Current Puzzle: " + game.getActiveName());
            }
        });
        mainMenu.getChildren().add(goToGameScreen);

        primaryStage.setTitle("Picture Puzzle Game");
        primaryStage.setScene(mainMenuScene);
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
