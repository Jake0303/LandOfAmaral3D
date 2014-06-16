package game;

import MainMenu.MainMenu;
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;

/**
 * Main class
 *
 *
 * @author Jacob Amaral
 */
public class Main extends SimpleApplication {


    private static AppSettings gameSettings;
    private MainMenu menu = new MainMenu();

    /**
     * Main method
     *
     * @param args
     */
    public static void main(String[] args) {
        gameSettings = new AppSettings(true);
        gameSettings.setResolution(800, 600);
        gameSettings.setFullscreen(false);
        gameSettings.setVSync(false);
        gameSettings.setTitle("Land of Amaral 3D - By Jacob Amaral");
        gameSettings.setUseInput(true);
        gameSettings.setFrameRate(200);


        Main app = new Main();
        app.setSettings(gameSettings);
        app.start();
    }//end of main

    /**
     * simpleInitApp method-Initializes the main menu.
     */
    @Override
    public void simpleInitApp() {
        stateManager.attach(menu);
    }//end of simpleInitApp
}//end of Main class