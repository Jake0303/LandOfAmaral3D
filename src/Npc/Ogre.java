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
 * Ogre npc Class
 *
 * @author Jacob Amaral
 */
public class Ogre extends Node {

    private Game game;
    private int hp, dmg, direction;
    private float movementSpeed;
    private Vector3f walkDirection = new Vector3f();
    private BetterCharacterControl ogre;
    private GhostControl hitBox;
    private Node node;
    private Timer movementTimer;
    private Geometry aggroRange;
    private boolean inCombat = false, dead;
    private Vector3f result;
    private float timer;

    /**
     * Ogre Constructor
     *
     * @param model
     * @param game
     */
    public Ogre(Node node, Game game) {
        this.game = game;
        ogre = new BetterCharacterControl(1f, 8f, 10f);
        hitBox = new GhostControl(new BoxCollisionShape(new Vector3f(2f, 10f, 2.5f)));
        ogre.setPhysicsDamping(1f);
        this.addControl(ogre);
        this.addControl(hitBox);
        this.node = node;
        this.attachChild(this.node);

        init();
    }//end of Ogre Constructor

    /**
     * init method.
     */
    private void init() {

        hp = 25;
        dmg = 10;
        movementSpeed = 10f;

    }//end of init method

    /**
     * update method.
     */
    public void update(float tpf) {
        /**
         * This chunk of code controls where an Ogre moves when its not in
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
                        ogre.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        ogre.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                    if (direction == 1) {
                        walkDirection.set(0, 0, walk.nextInt(5) - 11);
                        ogre.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        ogre.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                    if (direction == 2) {
                        walkDirection.set(walk.nextInt(5) - 11, 0, walk.nextInt(5));
                        ogre.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        ogre.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                    if (direction == 3) {
                        walkDirection.set(walk.nextInt(5), 0, walk.nextInt(5) - 11);
                        ogre.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        ogre.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }

                    if (direction == 4) {
                        walkDirection.set(walk.nextInt(5), 0, 0);
                        ogre.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        ogre.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                    if (direction == 5) {
                        walkDirection.set(walk.nextInt(5) - 11, 0, 0);
                        ogre.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        ogre.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                    if (direction == 6) {
                        walkDirection.set(walk.nextInt(5), 0, walk.nextInt(5));
                        ogre.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        ogre.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                    if (direction == 7) {
                        walkDirection.set(walk.nextInt(5) - 11, 0, walk.nextInt(5) - 11);
                        ogre.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        ogre.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                } else {
                    walkDirection.set(0, 0, 0);
                    ogre.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                }
                timer = 0;
            }
        }

        /**
         * If the ogre is moving, play the walk animation.
         */
        if (hp > 0) {

            if (walkDirection.length()<= 0 && inCombat == false) {

                game.ogreAnimation().setAnim("WalkCycle");
                game.ogreAnimation().setSpeed(1f);
            }
//            } else if(inCombat == false) {
//                if (walkDirection.length() > 0) {
//                    game.ogreAnimation().setAnim("Idle");
//                    game.ogreAnimation().setSpeed(1f);
//
//                }
//            }

        }
        /**
         * If the player aggros the ogre, follow the player.
         */
        if (inCombat == true) {
            Vector3f playerLocation = game.getPlayerModel().getWorldTranslation();
            Vector3f ogreLocation = game.getOgreModel().getWorldTranslation();
            Vector3f offset = playerLocation.subtract(ogreLocation);

            ogre.setWalkDirection(offset.normalize().mult(movementSpeed).setY(0));
            ogre.setViewDirection(offset.normalize().mult(movementSpeed).setY(0));
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
     * setHp method.
     */
    public void setHp(int amount) {
        hp = amount;
    }//end of setHp method

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
        return ogre;
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
     * respawn method
     */
    public void respawn() {
        hp = 100;
        dead = false;
        hitBox.setEnabled(true);
        ogre.setEnabled(true);
        ogre.warp(new Vector3f(-180f, 2f, -200f));
        game.getApp().getRootNode().attachChild(game.getUI().ogreLevel());
        game.getApp().getRootNode().attachChild(game.getUI().ogreText());
        game.getBulletAppState().getPhysicsSpace().add(game.getOgre());
        game.getApp().getRootNode().attachChild(game.getOgre());
        game.getBulletAppState().getPhysicsSpace().add(hitBox);

    }//end of respawn

    /**
     * hitBox method
     */
    public GhostControl hitBox() {
        return hitBox;
    }//end of hitBox method

    /**
     * getDamage method.
     *
     * @return dmg
     */
    public int getDamage() {
        return dmg;
    }//end of getDamage method

    /**
     * setDamage method.
     *
     * @return
     */
    public void setDamage(int amount) {
        dmg = amount;
    }//end of getDamage method
}//end of Ogre class
