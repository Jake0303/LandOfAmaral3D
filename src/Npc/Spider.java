package Npc;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import game.Game;
import java.util.Random;
import java.util.Timer;

/**
 * Spider npc Class
 *
 * @author Jacob Amaral
 */
public class Spider extends Node {

    private Game game;
    private int hp, dmg, direction;
    private float movementSpeed,timer;
    private Vector3f walkDirection = new Vector3f();
    private BetterCharacterControl spider;
    private GhostControl hitBox;
    private Node node;
    private Timer movementTimer;
    private Geometry aggroRange;
    private boolean inCombat = false, dead;

    /**
     * Spider Constructor
     *
     * @param model
     * @param game
     */
    public Spider(Node node, Game game) {
        this.game = game;
        spider = new BetterCharacterControl(2f, 6f, 12f);
        hitBox = new GhostControl(new BoxCollisionShape(new Vector3f(5f, 5.5f, 4f)));
        spider.setPhysicsDamping(1f);
        this.addControl(spider);
        this.addControl(hitBox);
        this.node = node;
        this.attachChild(this.node);

        init();
    }//end of Spider Constructor

    /**
     * init method.
     */
    private void init() {

        hp = 35;
        dmg = 15;
        movementSpeed = 12f;

    }//end of init method

    /**
     * update method.
     */
    public void update(float tpf) {
               /**
         * This chunk of code controls where an Spider moves when its not in
         * combat, it moves randomly on a timer.
         */
        if (inCombat == false) {
            timer = timer + tpf;
            Random walk = new Random();
            if (timer > 1f) {
                if (walk.nextInt(3) == 1) {
                    direction = walk.nextInt(9);
                    if (direction == 0) {
                        walkDirection.set(0, 0, walk.nextInt(5));
                        spider.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        spider.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                    if (direction == 1) {
                        walkDirection.set(0, 0, walk.nextInt(5) - 11);
                        spider.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        spider.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                    if (direction == 2) {
                        walkDirection.set(walk.nextInt(5) - 11, 0, walk.nextInt(5));
                        spider.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        spider.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                    if (direction == 3) {
                        walkDirection.set(walk.nextInt(5), 0, walk.nextInt(5) - 11);
                        spider.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        spider.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }

                    if (direction == 4) {
                        walkDirection.set(walk.nextInt(5), 0, 0);
                        spider.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        spider.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                    if (direction == 5) {
                        walkDirection.set(walk.nextInt(5) - 11, 0, 0);
                        spider.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        spider.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                    if (direction == 6) {
                        walkDirection.set(walk.nextInt(5), 0, walk.nextInt(5));
                        spider.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        spider.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                    if (direction == 7) {
                        walkDirection.set(walk.nextInt(5) - 11, 0, walk.nextInt(5) - 11);
                        spider.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        spider.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                } else {
                    walkDirection.set(0, 0, 0);
                    spider.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                }
                timer = 0;
            }
        }
        /**
         * If the spider is moving, play the walk animation.
         */
        if(hp>0)
        {
        if (walkDirection.length() <= 0 && inCombat == false) {
            game.spiderAnimation().setAnim("WalkCycle");
            game.spiderAnimation().setSpeed(1f);
        }
        }
        /**
         * If the player aggros the spider, follow the player.
         */
        if (inCombat == true) {
            Vector3f playerLocation = game.getPlayerModel().getWorldTranslation();
            Vector3f spiderLocation = game.getSpiderModel().getWorldTranslation();
            Vector3f offset = playerLocation.subtract(spiderLocation); // offset from spider to player

            spider.setWalkDirection(offset.normalize().mult(movementSpeed).setY(0));
            spider.setViewDirection(offset.normalize().mult(movementSpeed).setY(0));

        }
    }//end of update

    /**
     * getWalkDirection method
     *
     * @return
     */
    public Vector3f getWalkDirection() {
        return walkDirection;
    }//end of getWalkDirection

    /**
     * getHp method
     */
    public int getHp() {
        return hp;
    }//end of getHp method

    /**
     * getHp method
     */
    public void setHp(int amount) {
        amount = hp;
    }//end of getHp method

    /**
     * decrementHealth
     */
    public void decrementHealth(int amount) {
        hp -= amount;
    }//end of decrementHealth

    /**
     * incrementHealth
     */
    public void incrementHealth(int amount) {
        hp += amount;
    }//end of incrementHealth

    /**
     * getCharacterControl method
     *
     * @return
     */
    public BetterCharacterControl getCharacterControl() {
        return spider;
    }//end of getCharacterControl method

    /**
     * getMovementSpeed method
     *
     * @return
     */
    public float getMovementSpeed() {
        return movementSpeed;
    }//end of getMovementSpeed

    /**
     * setMovement speed method
     *
     * @param amount
     */
    public void setMovementSpeed(int amount) {
        movementSpeed = amount;
    }//end of setMovementSPeed

    /**
     * inCombat method
     *
     * @param combat
     */
    public void inCombat(boolean combat) {
        inCombat = true;
    }//end of inCombat

    /**
     * inCombat method
     *
     * @param combat
     */
    public void notInCombat(boolean combat) {
        inCombat = false;
    }//end of inCombat

    /**
     * getCombat method
     *
     * @return
     */
    public boolean getCombat() {
        return inCombat;
    }//end of inCombat

    /**
     * isDead method.
     *
     * @return
     */
    public boolean isDead() {
        return dead;
    }//end of isDead

    /**
     * dead method
     *
     * @return
     */
    public void dead(boolean answer) {
        dead = answer;
    }//end of dead

    /**
     * dead method
     *
     * @return
     */
    public boolean getDead() {
        return dead;
    }//end of dead

    /**
     * respawn method
     */
    public void respawn() {
        hp = 100;
        dead = false;
        spider.setEnabled(true);
        hitBox.setEnabled(true);
        spider.warp(new Vector3f(40, 2f, -250));
        game.getApp().getRootNode().attachChild(game.getUI().spiderLevel());
        game.getApp().getRootNode().attachChild(game.getUI().spiderText());
        game.getApp().getRootNode().attachChild(game.getSpider());
        game.getBulletAppState().getPhysicsSpace().add(game.getSpider());
        game.getBulletAppState().getPhysicsSpace().add(hitBox);
    }//end of respawn

    /**
     * hitBox method
     */
    public GhostControl hitBox() {
        return hitBox;
    }//end of hitBox method

    /**
     * getDmg method
     *
     * @return the dmg
     */
    public int getDmg() {
        return dmg;
    }//end of getDmg method

    /**
     * setDmg method
     *
     * @param dmg the dmg to set
     */
    public void setDmg(int dmg) {
        this.dmg = dmg;
    }//end of setDmg method
}//end of Spider class
