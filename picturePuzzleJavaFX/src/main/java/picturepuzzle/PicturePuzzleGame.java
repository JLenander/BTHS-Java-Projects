package picturepuzzle;

import java.io.FileNotFoundException;
import javafx.scene.image.*;
import javafx.scene.text.*;
import javafx.scene.transform.*;
import javafx.util.Duration;
import javafx.scene.input.MouseEvent;
import javafx.event.*;
import javafx.animation.*;

import javafx.scene.shape.*;
import javafx.scene.paint.*;

/**
 * Handles all the game logic
 */
public class PicturePuzzleGame
{
    /**The array of puzzles objects that the gaeme can pull from. */
    private Puzzle[] puzzles;
    /**The current puzzle the game is using */
    private Puzzle activePuzzle;
    /**The preview imageView element */
    private ImageView previewImageView;
    /**The feedback text for any messages to the player */
    private Text feedbackMessage;
    /**The Puzzle description object located in the gameScreenScene*/
    private Text puzzleDescriptionText;
    /**The preview image description. Includes the description property of a puzzle and the difficulty property of the puzzle, located in the mainMenuScene */
    private Text previewImageText;
    /**The 16 imageView objects that make up the split image in the game */
    private ImageView[] imageViewGameObjects;
    /**The 16 rectangle objects that enable selected image border coloring (imageViews have no javafx border css attribute). Parallel to the imageViewGameObjects array */
    private Rectangle[] imageViewRectangles;
    /**Stores the indices of the game images that are to be swapped */
    private int indexOne = -1, indexTwo = -1;
    /**The javafx swap animation used when swapping two images the user selects*/
    private SequentialTransition swapAnimationOne, swapAnimationTwo;
    private RotateTransition imgOneFirstRotate, imgOneSecondRotate, imgTwoFirstRotate, imgTwoSecondRotate;
    /**The javafx puzzle solved animation played when the game is won (the gaps dissapear and the images form the preview image)*/
    private SequentialTransition puzzleWinAnimation;
    private TranslateTransition[] puzzleWinTranslateTransitions;
    /**The javafx animation used to reverse the puzzle solved animation (Yea kinda couldn't find another workaround so this is it)*/
    private SequentialTransition reversedPuzzleWinAnimation;
    private TranslateTransition[] reversedPuzzleWinTranslateTransitions;
    /**Boolean used to know when the player has won. (The animation to reverse the puzzle win translations must be played only when the player has won and is resetting the game) */
    private boolean gameWon = false;
    /**Boolean used to prevent players from using the buttons when the win animation is playing */
    private boolean allowButtons = true;
    /**Boolean used to prevent selecting images during an animation or before the game starts*/
    private boolean allowSelection = false;
    /**The font element for all button text */
    private Font buttonFont;
    /**Used to track the number of swaps the user had to make to solve the puzzle */
    private int numSwaps = 0;

    /**
     * Creates the default picturepuzzlegame with preconfigured puzzles.
     */
    public PicturePuzzleGame() throws FileNotFoundException
    {
        puzzles = new Puzzle[8];
        puzzles[0] = new Puzzle("The moon Io", "This is a TRUE-COLOR image of one of Jupiter's moons. The moon Io is the most geologically active object in our solar system. The volcanic activity produces a very interesting and colorful surface.", "picturepuzzle/images/puzzle1/", 1);
        puzzles[1] = new Puzzle("City 17", "City 17. The home of the citadel and the center of events in the Half-Life Universe. Pictured here in the VR game Half-Life Alyx.", "picturepuzzle/images/puzzle2/", 2);
        puzzles[2] = new Puzzle("Smash Bros", "This rendition of the characters in the popular fighting game series Super Smash Bros Ultimate is full of detail, making for an excellent matching experience.", "picturepuzzle/images/puzzle3/", 3);
        puzzles[3] = new Puzzle("XCOM Berserker", "The Berserker is a terrifying enemy appearing in the XCOM series. This puzzle is trickier than it might seem.", "picturepuzzle/images/puzzle4/", 3);
        puzzles[4] = new Puzzle("Elite Dangerous", "This spaceship fighting game features lots of space and lots of ships. Similar colors and straight laser blasts make this a medium difficulty puzzle.", "picturepuzzle/images/puzzle5/", 4);
        puzzles[5] = new Puzzle("An Australian Coast", "This beautiful Australian coastal image may look simple, but symmetry provides a moderate challenge.", "picturepuzzle/images/puzzle6/", 6);
        puzzles[6] = new Puzzle("Thargoid Encounter", "The bizzare shapes and curves of the Thargoid alien ship makes this puzzle tricky for those unfamiliar with this fictional spacecraft.", "picturepuzzle/images/puzzle7/", 8);
        puzzles[7] = new Puzzle("An American suburb", "Black and White combined with the meaningless patterns of American Suburbs make this the most difficult puzzle to master. Study the preview image carefully!", "picturepuzzle/images/puzzle8/", 10);
        activePuzzle = puzzles[0];
        System.out.println("Puzzle layout example, puzzle 0: " + activePuzzle);

        //The feedback text for any messages to the player (ex. You clicked while the animation was playing, You need to shuffle the array to start the game, You won!)
        feedbackMessage = new Text(50, 200, "Shuffle the puzzle to start playing. Press the escape key to deselect selected images.");
        feedbackMessage.setFont(new Font(30));
        feedbackMessage.setWrappingWidth(250);

        //The text for the puzzle description (found in the puzzle game screen)
        puzzleDescriptionText = new Text(475, 920, activePuzzle.getDescription());
        puzzleDescriptionText.setFont(new Font(25));
        puzzleDescriptionText.setWrappingWidth(1025);
        // puzzleDescriptionText.setLayoutY(920);
        // puzzleDescriptionText.setLayoutX(475);
        
        //The text for the description of the preview image (found in the main menu screen)
        previewImageText = new Text(600, 900, "Estimated difficulty: " + activePuzzle.getDifficulty() + " out of 10. " + activePuzzle.getDescription());
        previewImageText.setFont(new Font(30));
        previewImageText.setWrappingWidth(1200);
        // previewImageText.setLayoutX(600);
        // previewImageText.setLayoutY(900);
        
        //The puzzle preview image (found in the main menu screen)
        previewImageView = new ImageView(getActivePreviewImage());
        previewImageView.setLayoutX(600);
        previewImageView.setLayoutY(90);

        imageViewRectangles = new Rectangle[16];
        imageViewGameObjects = new ImageView[16];
        for(int i = 0; i < getActiveImageArray().length; i++)
        {
            imageViewGameObjects[i] = new ImageView(getActiveImageArray()[i]);
            imageViewGameObjects[i].setPreserveRatio(true);

            //Rectangle position is set in updateActiveImages()
            imageViewRectangles[i] = new Rectangle(0, 0, Paint.valueOf("whitesmoke"));

            //Sets the action of the images when clicked.
            imageViewGameObjects[i].setOnMouseClicked(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event)
                {
                    if(allowSelection)
                    {
                        int index = -1;
                        //figures out which object is being clicked 
                        for(int i = 0; i < imageViewGameObjects.length; i++)
                        {
                            if(event.getTarget().equals(imageViewGameObjects[i]))
                            {
                                index = i;
                                break;
                            }
                        }
                        
                        if(indexOne == -1 && indexTwo == -1) //No other images are selected, store the first selected image
                        {
                            indexOne = index;
                            imageViewRectangles[indexOne].setFill(Paint.valueOf("deepskyblue"));
                            }
                        else if(indexOne == index) //deselect both images. Do not swap image with itself.
                        {
                            deselectImage();
                        }
                        else //store the second selected image and then swap the images.
                        {
                            indexTwo = index;
                            allowSelection = false;
                            swap();
                        }
                    }
                    else
                    {
                        System.out.println("allowSelection is false");
                    }
                }
            });


            buttonFont = new Font(30);
        }

        //configures the image swap animation. The firstRotate rotates the image 90deg then swaps the image to the other one.
        //firstRotate for the first image to be swapped (this is required for simulatenous image swap animations)
        imgOneFirstRotate = initRotateTransition(true, true);
        //firstRotate for the second image to be swapped
        imgTwoFirstRotate = initRotateTransition(true, false);
        //secondRotate for the first image to be swapped
        imgOneSecondRotate = initRotateTransition(false, true);
        //secondRotate for the second image to be swapped
        imgTwoSecondRotate = initRotateTransition(false, false);

        //two swap animations are required for simulatenous swaps
        swapAnimationOne = new SequentialTransition(imgOneFirstRotate, imgOneSecondRotate);
        swapAnimationTwo = new SequentialTransition(imgTwoFirstRotate, imgTwoSecondRotate);
        swapAnimationTwo.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event)
            {
                allowButtons = true;

                //Resets the index variables after the animation is finished.
                activePuzzle.swap(indexOne, indexTwo);
                deselectImage();

                //Checks if the puzzle is solved
                if(activePuzzle.isSolved())
                {
                    feedbackMessage.setText("You Won with just " + numSwaps + " swaps!");
                    allowButtons = false; //prevent the player from using the buttons when the win animation is playing. Is reset when the animation finishes.
                    gameWon = true;
                    disableImages();
                    puzzleWinAnimation.play();
                    System.out.println("Puzzle has been solved");
                }
                else
                {
                    allowSelection = true;
                }
            }
        });

        //Initializes the puzzle win animations. The actual animations are puzzle dependant and are modified in updateActiveImages();
        puzzleWinTranslateTransitions = new TranslateTransition[16];
        //makes the animation to reverse the puzzle win animation. Only change is animation 16 is mapped to the first image, animation 15 is mapped to the second image (and the animation is quicker)
        reversedPuzzleWinTranslateTransitions = new TranslateTransition[16];
        for(int i = 0; i < puzzleWinTranslateTransitions.length; i++)
        {
            puzzleWinTranslateTransitions[i] = new TranslateTransition(Duration.millis(200), imageViewGameObjects[i]);

            reversedPuzzleWinTranslateTransitions[i] = new TranslateTransition(Duration.millis(1), imageViewGameObjects[i]);
        }

        
        //adds the transitions the the animations
        puzzleWinAnimation = new SequentialTransition();
        puzzleWinAnimation.setAutoReverse(false);
        puzzleWinAnimation.setInterpolator(Interpolator.LINEAR);
        reversedPuzzleWinAnimation = new SequentialTransition();
        for(int i = 0; i < puzzleWinTranslateTransitions.length; i++)
        {
            puzzleWinAnimation.getChildren().add(puzzleWinTranslateTransitions[i]);

            reversedPuzzleWinAnimation.getChildren().add(reversedPuzzleWinTranslateTransitions[i]);
        }

        puzzleWinAnimation.setOnFinished(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                allowButtons = true;
            }
        });

        updateActiveImages();
        reset();
    }

    /**
     * Shuffles the puzzle state. This restarts the current game.
     */
    public void shuffle()
    {
        if(allowButtons)
        {
            if(gameWon) //Reset the puzzle animation if the game was won but hasn't been reset yet
            {
                reversedPuzzleWinAnimation.play();
                gameWon = false;
            }
        
            feedbackMessage.setText("");
            activePuzzle.shuffle();
            allowSelection = true;
            numSwaps = 0;
            deselectImage();
            enableImages();
            updateActiveImages();
        }
    }

    /**
     * Resets the puzzle to the solved state. This restarts the game.
     */
    public void reset()
    {
        if(allowButtons)
        {
            if(gameWon) //Reset the puzzle animation if the game was won but hasn't been reset yet
            {
                reversedPuzzleWinAnimation.play();
                gameWon = false;
            }

            feedbackMessage.setText("Shuffle the puzzle to start playing. Press the escape key to deselect selected images.");
            activePuzzle.reset();
            allowSelection = false;
            numSwaps = 0;
            deselectImage();
            disableImages();
            updateActiveImages();
        }
    }

    /**
     * @return if the game is allowing buttons to be used currently (mainly for animations).
     */
    public boolean isAllowButtons()
    {
        return allowButtons;
    }

    /**
     * Clears the visual indicator for selected images.
     * Generally called by deselectImage but can also be called standalone.
     */
    public void clearSelectStyle()
    {
        imageViewRectangles[indexOne].setFill(Paint.valueOf("whitesmoke"));
    }

    /**
     * Deselects any selected images. Also clears the visual indicator for selected images.
     */
    public void deselectImage()
    {
        if(indexOne > -1)
        {
            clearSelectStyle();
        }

        indexOne = -1;
        indexTwo = -1;
    }

    /**
     * Initializes the first half of the swap animation. Multiple transitions are needed for simulatenous animations of the two images swapping.
     * @param first Whether this is the first or second rotate transition. Changes the direction of rotation.
     * @param imgOne Whether this is image one or image two.
     * @return The RotateTransition object, fully initialized.
     */
    private RotateTransition initRotateTransition(boolean first, boolean imgOne)
    {
        RotateTransition rt = new RotateTransition(Duration.millis(500));
        rt.setInterpolator(Interpolator.LINEAR);
        rt.setAxis(Rotate.Y_AXIS);

        //sets the action to be taken when the halves of the rotate animation finish
        if(first)
        {
            rt.setByAngle(90);
            if(imgOne)
            {
                rt.setOnFinished(new EventHandler<ActionEvent>()
                {
                    @Override
                    public void handle(ActionEvent event)
                    {
                        //Swaps this imageView's image source to be the other selected image.
                        imageViewGameObjects[indexOne].setImage(getActiveImageArray()[indexTwo]);
                    }
                });
            }
            else
            {
                rt.setOnFinished(new EventHandler<ActionEvent>()
                {
                    @Override
                    public void handle(ActionEvent event)
                    {
                        //Swaps this imageView's image source to be the other selected image.
                        imageViewGameObjects[indexTwo].setImage(getActiveImageArray()[indexOne]);
                    }
                });
            }
        }
        else
        {
            rt.setByAngle(-90);
        }

        return rt;
    }

    /**
     * Updates the javafx objects of puzzle specific elements (The preview screen and the puzzle game screen)
     * Also generates the puzzle win animations.
     * Also updates the size of the select style rectangles.
     */
    private void updateActiveImages()
    {
        //Updates puzzle specific text objects
        puzzleDescriptionText.setText(activePuzzle.getDescription());

        previewImageText.setText("Estimated difficulty: " + activePuzzle.getDifficulty() + " out of 10.");
        if (!activePuzzle.wasCompleted())
            previewImageText.setText(previewImageText.getText() + "\nYou have not completed this puzzle yet. Complete it to unblur this preview!");
        previewImageText.setText(previewImageText.getText() + "\n" + activePuzzle.getDescription());

        previewImageView.setImage(activePuzzle.getPreviewImage());

        //Calculates the necessary image offsets
        int imageWidth = (int) getActiveImageArray()[0].getWidth(), imageHeight = (int) getActiveImageArray()[0].getHeight();
        int targetGap = 12, xOffset = 0, yOffset = 0; //The targeted space between images in the puzzle and the offset variables that change for every object MUST BE EVEN
        int baseXOffset, baseYOffset = 90; //The Base offset for the entire array. This is the top left corner of the first image.

        //centers the xOffset of the puzzle based on the targetgap and the image width.
        baseXOffset = (1920 - (imageWidth*4) - (targetGap*3))/2;

        //Updates the images in the puzzle game screen
        for(int i = 0; i < imageViewGameObjects.length; i++)
        {
            imageViewGameObjects[i].setImage(getActiveImageArray()[i]);

            //Sets the offset for the images based on the offset of the entire array of images (baseoffset) and the individual image offset
            imageViewGameObjects[i].setLayoutX(baseXOffset + xOffset);
            imageViewGameObjects[i].setLayoutY(baseYOffset + yOffset);

            //Sets the select style rectangle sizes and positions. These rectangles cover image size plus halfway to the next image (5px border)
            imageViewRectangles[i].setHeight(imageHeight + (targetGap));
            imageViewRectangles[i].setWidth(imageWidth + (targetGap));
            imageViewRectangles[i].setX(baseXOffset + xOffset - (targetGap/2));
            imageViewRectangles[i].setY(baseYOffset + yOffset - (targetGap/2));
            
            //Modifies the individual offset for the next image
            if((i + 1) % 4 == 0) //We are at the 4th image in the row, reset the xOffset and move to the next row
            {
                xOffset = 0;
                yOffset += targetGap + imageHeight;
            }
            else //Increment the xOffset to achieve the desired image gap
            {
                xOffset += targetGap + imageWidth;
            }
        }

        /*
            calculates the win animation for the current active puzzle and the reverse of it as follows
            as we move left to right across the row, the movement is the targetGap + targetGap/2 to the right, minus TargetGap each time (into the negative).
            as we move top to bottom down the column, the movement is the targetGap + targetGap/2 to the downards, minus TargetGap each time.
        */
        xOffset = targetGap + targetGap/2;
        yOffset = targetGap + targetGap/2;
        for(int i = 0; i < puzzleWinTranslateTransitions.length; i++)
        {
            puzzleWinTranslateTransitions[i].setByX(xOffset);
            puzzleWinTranslateTransitions[i].setByY(yOffset);

            reversedPuzzleWinTranslateTransitions[i].setByX(-1 * xOffset);
            reversedPuzzleWinTranslateTransitions[i].setByY(-1 * yOffset);

            if((i + 1) % 4 == 0) //We are at the 4th image in the row, reset the xOffset and move to the next row
            {
                xOffset = targetGap + targetGap/2;
                yOffset -= targetGap;
            }
            else //Increment the xOffset by the rules described above.
            {
                xOffset -= targetGap;
            }
        }
    }

    /**
     * Plays the swap animation for the two imageview objects and then swaps the two images.
     * When the swap animation finishes, it calls the swap method for the activePuzzle.
     */
    private void swap()
    {
        feedbackMessage.setText("");

        if(indexOne == -1 || indexTwo == -1)
        {
            System.out.println("Invalid swap indices");
            deselectImage();
        }
        else
        {
            //clear the selected image style
            clearSelectStyle();

            //change the animations to the selected images
            setAnimationNode(imageViewGameObjects[indexOne], imageViewGameObjects[indexTwo]);
            allowButtons = false; //This is reset at the end of the animation
            swapAnimationOne.play();
            swapAnimationTwo.play();
            numSwaps++;
        }
    }

    /**
     * Sets the nodes for all of the swap animation objects
     * @param imgViewOne The imageView object to set the first swap animation nodes to.
     * @param imgViewTwo The imageView object to set the second swap animation nodes to.
     */
    private void setAnimationNode(ImageView imgViewOne, ImageView imageViewTwo)
    {
        swapAnimationOne.setNode(imgViewOne);
        imgOneFirstRotate.setNode(imgViewOne);
        imgOneSecondRotate.setNode(imgViewOne);

        swapAnimationTwo.setNode(imageViewTwo);
        imgTwoFirstRotate.setNode(imageViewTwo);
        imgTwoSecondRotate.setNode(imageViewTwo);
    }

    /**
     * Sets the disabled property of all of the imageview objects to false.
     * This re-enables the scale change on hover and other css pseudo classes.
     */
    public void enableImages()
    {
        for(int i = 0; i < imageViewGameObjects.length; i++)
        {
            imageViewGameObjects[i].setDisable(false);
        }
    }

    /**
     * Sets the disabled property of all of the imageview objects to true.
     * This prevents the scale change on hover.
     */
    public void disableImages()
    {
        for(int i = 0; i < imageViewGameObjects.length; i++)
        {
            imageViewGameObjects[i].setDisable(true);
        }
    }

    /**
     * Gets the currently active puzzle (for use in the current game or for the level select screen)
     * @return the active puzzle object
     */
    public Puzzle getActivePuzzle()
    {
        return activePuzzle;
    }

    /**
     * Sets the currently active puzzle.
     * @param activePuzzle The new puzzle number. Should be in the bounds of the puzzle array.
     */
    public void setActivePuzzle(int newActivePuzzle)
    {
        this.activePuzzle = puzzles[newActivePuzzle];
        updateActiveImages();
    }

    /**
     * @return The javafx font used by all buttons
     */
    public Font getButtonFont()
    {
        return buttonFont;
    }

    /**
     * @return The preview javafx ImageView object for use in the main screen.
     */
    public ImageView getPreviewImageView()
    {
        return previewImageView;
    }

    /**
     * @return The preview javafx Text object for use in the main screen. (contains description and difficulty)
     */
    public Text getPreviewImageText()
    {
        return previewImageText;
    }

    /**
     * @return The array of 16 javafx ImageView objects used for the matching part of the puzzle game.
     */
    public ImageView[] getImageViewGameObjects()
    {
        return imageViewGameObjects;
    }

    /**
     * @return The array of 16 javafx Rectangle objects that sit behind the imageViewGameObjects
     */
    public Rectangle[] getImageViewRectangles()
    {
        return imageViewRectangles;
    }

    /**
     * @return The array of puzzle objects
     */
    public Puzzle[] getPuzzleArray()
    {
        return puzzles;
    }

    /**
     * @return The feedback message javafx text object
     */
    public Text getFeedbackMessageText()
    {
        return feedbackMessage;
    }

    /**
     * @return The description of the puzzle for the gameScreenScene
     */
    public Text getPuzzleDescriptionText()
    {
        return puzzleDescriptionText;
    }

    /**
     * Gets the array of javafx image objects from the current active puzzle.
     * @return the active puzzle's array of image objects.
     */
    public Image[] getActiveImageArray()
    {
        return activePuzzle.getImageArray();
    }

    /**
     * Gets the preview image's javafx image object from the current active puzzle.
     * @return the active puzzle's preview image javafx object.
     */
    public Image getActivePreviewImage()
    {
        return activePuzzle.getPreviewImage();
    }

    /**
     * @return The Active Puzzle's name property
     */
    public String getActiveName()
    {
        return activePuzzle.getName();
    }

    /**
     * @return The Active Puzzle's description property
     */
    public String getActiveDescription()
    {
        return activePuzzle.getDescription();
    }

    /**
     * @return The Active Puzzle's difficulty property
     */
    public int getActiveDifficulty()
    {
        return activePuzzle.getDifficulty();
    }
}
