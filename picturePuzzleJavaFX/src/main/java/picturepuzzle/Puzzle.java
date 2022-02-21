package picturepuzzle;

import java.io.FileNotFoundException;
import javafx.scene.image.*;

public class Puzzle
{
    /**The name of the puzzle as displayed to the player */
    private String name;
    /**The description of the puzzle. Is displayed to the player */
    private String description;
    /**The location of the puzzle folder relative to the root folder of the project (ex. "puzzle 1/"). Puzzle folder contains the 16 images and the preview image */
    private String location;
    /**The integer difficulty of the puzzle. Is displayed to the player */
    private int difficulty;
    private static final String[] SOLVED_STATE = {"imgA.png", "imgB.png", "imgC.png", "imgD.png", "imgE.png", "imgF.png", "imgG.png", "imgH.png", "imgI.png", "imgJ.png", "imgK.png", "imgL.png", "imgM.png", "imgN.png", "imgO.png", "imgP.png"};
    /**The imageStates array contains the string names of each image for comparison purposes. It is parallel to the javafx Image objects array */
    private String[] imageStates = SOLVED_STATE.clone();
    /**An array of javafx Image objects */
    private Image[] images;
    /**The puzzle preview javafx image object for the uncompleted puzzle image preview*/
    private Image uncompletedPreview;
    /**The puzzle preview javafx image object for the completed puzzle image preview*/
    private Image completedPreview;
    /**Whether this puzzle has been completed or not */
    private boolean completed;
    
    /**
     * Each Puzzle object contains arrays for 16 javafx Image objects. The image puzzle is arranged in a 4x4 grid, with images:<p> A-D, E-H, I-L, M-P <p>left to right, and top to bottom.
     * <p>For best results, make the individual images no larger than 300 pixels wide, and the preview images no larger than 1200 pixels wide. Square images should be no larger than 200px wide (previews 800px)
     * @param name The name of the puzzle as displayed to the player
     * @param location The location of the puzzle folder relative to the root folder of the project (ex. "images/puzzle1/"). Puzzle folder contains the 16 images and the preview image. <b>MUST CONTAIN A TRAILING SLASH</b>
     * @param description The description of the puzzle. Is displayed to the player
     * @param difficulty The integer difficulty of the puzzle. Is displayed to the player
     */
    public Puzzle(String name, String description, String location, int difficulty) throws FileNotFoundException
    {
        this.name = name;
        this.description = description;
        this.location = location;
        this.difficulty = difficulty;
        this.completed = false;

        //creation of the image objects
        images = new Image[16];
        for(int i = 0; i < images.length; i++) //FULFILLING PROJECT REQUIREMENT: A for loop is better than a while loop because we can access the items in the image array with i
        {
            
            images[i] = new Image(getClass().getResource("/" + location + imageStates[i]).toString());
        }

        uncompletedPreview = new Image(getClass().getResource("/" + location + "uncompletedPreview.png").toString());
        completedPreview = new Image(getClass().getResource("/" + location + "completedPreview.png").toString());
    }

    /**
     * Gets the array that stores the state of the puzzle (which images are in which positions)
     * @return a copy of the images array.
     */
    public String[] getStateArray()
    {
        return imageStates.clone();
    }

    /**
     * Gets the array that stores the javafx Image objects of the puzzle
     * @return the image objects array (not a copy).
     */
    public Image[] getImageArray()
    {
        return images;
    }

    /**
     * Gets the puzzle preview javafx Image object
     * @return the puzzle preview javafx Image object
     */
    public Image getPreviewImage()
    {
        if(completed)
            return completedPreview;
        
        return uncompletedPreview;
    }

    /**
     * @return the name of the puzzle 
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the description of the puzzle 
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @return the folder location of the images, relative to the root folder of the project 
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * @return the integer difficulty of the puzzle 
     */
    public int getDifficulty()
    {
        return difficulty;
    }

    /**
     * Resets the puzzle back to the solved state.
     */
    public void reset()
    {
        for(int i = 0; i < imageStates.length; i++)
        {
            //finds the index of each object and moves it to the solved order. (finds imgA and moves it to index 0, finds imgB and moves it to index 1 etc.)
            int index = -1;
            for(int j = i; j < imageStates.length; j++)
            {
                if(imageStates[j].equals(SOLVED_STATE[i]))
                    index = j;
            }
            swap(i, index);
        }
    }

    /**
     * Shuffles the arrays to a random state.
     */
    public void shuffle()
    {
        for(int i = 0; i < imageStates.length; i++)
        {
            int randomIndex = (int) (Math.random() * imageStates.length);

            String temp = imageStates[i];
            imageStates[i] = imageStates[randomIndex];
            imageStates[randomIndex] = temp;

            Image tempImg = images[i];
            images[i] = images[randomIndex];
            images[randomIndex] = tempImg;
        }
    }

    /**
     * Swaps two elements in the image arrays.
     * <p>Precondition: indices specified are within bounds (0-15)
     * <p>Postcondition: the images and imageStates arrays local to this object are modified
     * @param indexOne The first index to swap
     * @param indexTwo The second index to swap
     */
    public void swap(int indexOne, int indexTwo)
    {
        String temp = imageStates[indexOne];
        imageStates[indexOne] = imageStates[indexTwo];
        imageStates[indexTwo] = temp;

        Image tempImg = images[indexOne];
        images[indexOne] = images[indexTwo];
        images[indexTwo] = tempImg;
    }

    /**
     * Checks if the puzzle is solved.
     * @return true if the puzzle is in the solved state.
     */
    public boolean isSolved()
    {
        for(int i = 0; i < imageStates.length; i++)
        {
            if(!imageStates[i].equals(SOLVED_STATE[i]))
                return false;
        }
        completed = true;
        return true;
    }

    /**
     * @return true if this puzzle has been completed previously
     */
    public boolean wasCompleted()
    {
        return completed;
    }

    public String toString()
    {
        String output = name + ": ";
        for(String img : imageStates)
        {
            output += img + " ";
        }
        return output;
    }
}
