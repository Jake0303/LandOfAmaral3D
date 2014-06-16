package MainMenu;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.input.InputManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.system.AppSettings;
import game.Game;
import java.io.File;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.buttons.CheckBox;
import tonegod.gui.controls.text.TextField;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import tonegod.gui.core.utils.UIDUtil;

/**
 * Main Menu Class.
 *
 * @author Jacob Amaral
 */
public class MainMenu extends AbstractAppState {

    private SimpleApplication app;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private AppSettings gameSettings;
    private InputManager inputManager;
    private Screen screen;
    private boolean startGame, loadGame, options, credits, chooseName;
    private Game game = new Game();
    private Window win;
    private TextField textField, textField2;
    private ButtonAdapter button, button2, button3, button4, button5, button6, button7,
            button8, button9, button10, button11;
    private Element element;
    private Element backgroundImage;
    private CheckBox shadowCheckBox;
    private ButtonAdapter button12;
    private File file;
    private boolean searchFile;
    private String search;

    /**
     * initialize method.
     *
     * @param stateManager
     * @param app
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
        this.app.setDisplayStatView(false);
        this.app.setDisplayFps(true);

        screen = new Screen(this.app);
        screen.initialize();
        this.app.getGuiNode().addControl(screen);
        inputManager.setCursorVisible(true);

        /**
         * Background image.
         */
        backgroundImage = new Element(
                screen, // Reference to the screen
                UIDUtil.getUID(), // A random ID generator or any unique string you like
                Vector2f.ZERO, // We’ll center the element after we attach it to a parent
                new Vector2f(800, 600), // A 477×68 pixel image
                Vector4f.ZERO, // A non-nine-patch-ish scaling element
                "Interface/background.png" // Here is where you define the image to use.
                );

        screen.addElement(backgroundImage);
        /**
         * Window Background
         */
        win = new Window(screen, "win", new Vector2f((screen.getWidth() / 2) - 100, (screen.getHeight() / 2) - 125), new Vector2f(200, 300));
        win.setIsResizable(false);
        win.setIsMovable(false);

        /**
         * New Game button
         */
        button = new ButtonAdapter(screen, "Button", new Vector2f(50, 60)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                chooseName = true;
            }
        };
        /**
         * Load Game button
         */
        button2 = new ButtonAdapter(screen, "Button2", new Vector2f(50, 100)) {
            

            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                
                win.setText("Please enter your characters name to load");
                button.setIsVisible(false);
                button2.setIsVisible(false);
                button3.setIsVisible(false);
                button4.setIsVisible(false);
                button5.setIsVisible(false);
                button6.setIsVisible(false);
                button7.setIsVisible(false);
                button8.setIsVisible();
                textField2.setIsVisible();
                button12.setIsVisible();
                searchFile=true;
                

            }
        };
        /**
         * Options button
         */
        button3 = new ButtonAdapter(screen, "Button3", new Vector2f(50, 140)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                options = true;
            }
        };
        /**
         * Load button
         */
        button12 = new ButtonAdapter(screen, "Button3", new Vector2f(50, 220)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                if (file.exists()) {
                game.setName(textField2.getText());
                game.setLoadGame(true);
                screen.removeElement(backgroundImage);
                screen.removeElement(win);
                screen.removeElement(element);
                this.app.getInputManager().setCursorVisible(false);
                this.app.getStateManager().detach(this.app.getStateManager().getState(MainMenu.class));
                this.app.getStateManager().attach(game);
                }
            }
        };
        /**
         * searchFile textfield
         */
        textField2 = new TextField(screen, "TextField", new Vector2f(40, 120)) {
           
        };
        
        /**
         * Credits button
         */
        button7 = new ButtonAdapter(screen, "Button4", new Vector2f(50, 180)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                credits = true;
            }
        };
        /**
         * Quit button
         */
        button4 = new ButtonAdapter(screen, "Button4", new Vector2f(50, 220)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                System.exit(0);
            }
        };
        /**
         * done button
         */
        button5 = new ButtonAdapter(screen, "Button5", new Vector2f(50, 180)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                chooseName = false;
                win.setText("Choose your class");
                textField.setIsVisible(false);
                button5.setIsVisible(false);
                button9.setIsVisible();
                button10.setIsVisible();
                button11.setIsVisible();

            }
        };
        /**
         * melee class button
         */
        button9 = new ButtonAdapter(screen, "Button9", new Vector2f(50, 100)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                game.setMelee(true);
                screen.removeElement(backgroundImage);
                screen.removeElement(win);
                screen.removeElement(element);
                this.app.getInputManager().setCursorVisible(false);
                this.app.getStateManager().detach(this.app.getStateManager().getState(MainMenu.class));
                this.app.getStateManager().attach(game);
                game.setName(textField.getText());
            }
        };
        /**
         * range class button
         */
        button10 = new ButtonAdapter(screen, "Button10", new Vector2f(50, 140)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                game.setRange(true);
                screen.removeElement(backgroundImage);
                screen.removeElement(win);
                screen.removeElement(element);
                this.app.getInputManager().setCursorVisible(false);
                this.app.getStateManager().detach(this.app.getStateManager().getState(MainMenu.class));
                this.app.getStateManager().attach(game);
                game.setName(textField.getText());
            }
        };
        /**
         * mage class button
         */
        button11 = new ButtonAdapter(screen, "Button11", new Vector2f(50, 180)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                game.setMage(true);
                screen.removeElement(backgroundImage);
                screen.removeElement(win);
                screen.removeElement(element);
                this.app.getInputManager().setCursorVisible(false);
                this.app.getStateManager().detach(this.app.getStateManager().getState(MainMenu.class));
                this.app.getStateManager().attach(game);
                game.setName(textField.getText());
            }
        };
        /**
         * save button
         */
        button6 = new ButtonAdapter(screen, "Button6", new Vector2f(50, 180)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                button.setIsVisible();
                button2.setIsVisible();
                button3.setIsVisible();
                button4.setIsVisible();
                button5.setIsVisible();
                button5.setIsVisible();

                win.setText("Main Menu");
                win.setTextPosition(2, 10);
                win.setTextAlign(BitmapFont.Align.Center);

                textField.setIsVisible(false);
                button6.setIsVisible(false);
                button7.setIsVisible(true);
                shadowCheckBox.setIsVisible(false);
                options = false;
            }
        };
        /**
         * back button
         */
        button8 = new ButtonAdapter(screen, "Button8", new Vector2f(50, 180)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                button.setIsVisible();
                button2.setIsVisible();
                button3.setIsVisible();
                button4.setIsVisible();
                button5.setIsVisible();
                button5.setIsVisible();

                win.setText("Main Menu");
                win.setTextPosition(2, 10);
                win.setTextAlign(BitmapFont.Align.Center);

                textField.setIsVisible(false);
                button6.setIsVisible(false);
                button7.setIsVisible(true);
                button8.setIsVisible(false);
                button12.setIsVisible(false);
                textField2.setIsVisible(false);
                shadowCheckBox.setIsVisible(false);
                credits = false;
            }
        };
        /**
         * setName textfield
         */
        textField = new TextField(screen, "TextField", new Vector2f(40, 120)) {
        };

        /**
         * shadowCheckBox
         */
        shadowCheckBox = new CheckBox(screen, "CheckBox", new Vector2f(40, 120)) {
        };
        shadowCheckBox.setLabelText("Shadows");
        shadowCheckBox.setIsChecked(true);
        shadowCheckBox.setIsVisible(false);

        button.setText("New Game");
        button2.setText("Load Game");
        button3.setText("Options");
        button4.setText("Quit");
        button5.setIsVisible(false);
        button5.setText("Done");
        button6.setText("Save");
        button6.setIsVisible(false);
        button7.setText("Credits");
        button7.setIsVisible(true);
        button8.setText("Back");
        button8.setIsVisible(false);
        button9.setText("Warrior");
        button10.setText("Ranger");
        button11.setText("Mage");
        button9.setIsVisible(false);
        button10.setIsVisible(false);
        button11.setIsVisible(false);
        button12.setIsVisible(false);
        button12.setText("Load");

        win.setText("Main Menu");
        win.setTextPosition(2, 10);
        win.setTextAlign(BitmapFont.Align.Center);

        textField.setIsVisible(false);
        textField2.setIsVisible(false);
        win.addChild(textField);
        win.addChild(textField2);
        win.addChild(button);
        win.addChild(button2);
        win.addChild(button3);
        win.addChild(button4);
        win.addChild(button5);
        win.addChild(button6);
        win.addChild(button7);
        win.addChild(button8);
        win.addChild(button9);
        win.addChild(button10);
        win.addChild(button11);
        win.addChild(button12);
        win.addChild(shadowCheckBox);
        screen.addElement(win);


        /**
         * Menu title image.
         */
        element = new Element(
                screen, // Reference to the screen
                UIDUtil.getUID(), // A random ID generator or any unique string you like
                Vector2f.ZERO, // We’ll center the element after we attach it to a parent
                new Vector2f(477, 68), // A 477×68 pixel image
                Vector4f.ZERO, // A non-nine-patch-ish scaling element
                "Interface/menutitle.png" // Here is where you define the image to use.
                );
        screen.addElement(element);
        element.setX(175);
        element.setY(500);
    }//end of initialize method

    /**
     * update method.
     *
     * @param tpf
     */
    @Override
    public void update(float tpf) {
        /**
         * If a player presses the New Game button, ask them to enter a name.
         */
        
        search=textField2.getText();
        file = new File("assets/Saves/" +search+ ".txt");
        if (chooseName) {
            win.setText("Please choose a name");
            win.setTextPosition(2, 30);
            button.setIsVisible(false);
            button2.setIsVisible(false);
            button3.setIsVisible(false);
            button4.setIsVisible(false);
            button7.setIsVisible(false);
            button5.setIsVisible(true);
            textField.setIsVisible(true);
        }
        /**
         * Options menu
         */
        if (options) {
            win.setText("Options");
            win.setTextPosition(2, 30);
            button.setIsVisible(false);
            button2.setIsVisible(false);
            button3.setIsVisible(false);
            button4.setIsVisible(false);
            button5.setIsVisible(false);
            button7.setIsVisible(false);
            textField.setIsVisible(false);
            shadowCheckBox.setIsVisible(true);
            button6.setIsVisible(true);
        }
        /**
         * Shadow CheckBox in the options menu
         */
        if (shadowCheckBox.getIsChecked()) {
            game.enableShadows(true);
        } else {
            game.enableShadows(false);
        }
        /**
         * credits menu
         */
        if (credits) {
            win.setText("Thanks to Spencer C and Aras for my 3d models!\n I suck at art :(\n"
                    + "Also thanks to the jMonkey team for this awesome engine!");
            win.setTextPosition(2, 30);
            button.setIsVisible(false);
            button2.setIsVisible(false);
            button3.setIsVisible(false);
            button4.setIsVisible(false);
            button5.setIsVisible(false);
            button7.setIsVisible(false);
            textField.setIsVisible(false);
            shadowCheckBox.setIsVisible(false);
            button6.setIsVisible(false);
            button8.setIsVisible(true);
        }
    }//end of update method

    /**
     * getName method.
     *
     * @return
     */
    public TextField getName() {
        return textField;
    }//end of getName method

    /**
     * getShadowCheckBox method.
     */
    public CheckBox getShadowCheckBox() {
        return shadowCheckBox;
    }//end of getShadowCheckBox
}//end of MainMenu class
