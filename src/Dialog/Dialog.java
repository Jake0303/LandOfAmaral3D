package Dialog;

import game.Game;

/**
 * Dialog Class
 *
 * @author Jacob Amaral
 */
public class Dialog {

    /**
     * sendMessage method
     *
     * @param game
     * @param text
     */
    public static void sendMessage(Game game, String text) {
        game.getUI().dialogText().setText(text);
    }//end of sendMessage
}//end of Dialog class
