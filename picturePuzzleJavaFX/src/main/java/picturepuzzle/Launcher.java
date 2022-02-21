package picturepuzzle;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Launcher {
    public static void main(String[] args) {

        Path puzzle1ImgA = Paths.get("images/puzzle1/imgA.png");
        Path puzzle8ImgP = Paths.get("images/puzzle8/imgP.png");

        if(Files.exists(puzzle1ImgA) && Files.exists(puzzle8ImgP))
        {
            // Assume images exist and launch the application.
            App.main(args);
        }
        else
        {
            // Create a warning dialog that the images do not exist in the current directory and exit.
            JFrame jFrame = new JFrame();
            JOptionPane.showMessageDialog(jFrame, "Could not load images. Please ensure the images directory is in the same directory as this jar file.", "Images Not Found", JOptionPane.ERROR_MESSAGE);
            jFrame.dispose();
        }
    }
}
