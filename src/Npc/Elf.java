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
 * Elf npc Class
 *
 * @author Jacob Amaral
 */
public class Elf extends Node {

    private Game game;
    private int hp, dmg, direction;
    private float movementSpeed, timer;
    private Vector3f walkDirection = new Vector3f();
    private BetterCharacterControl elf;
    private GhostControl hitBox;
    private Node node;
    private Timer movementTimer;
    private Geometry aggroRange;
    private boolean inCombat = false, dead;
    private Vector3f playerToElf;
    private Vector3f moveDir;

    /**
     * Elf Constructor
     *
     * @param model
     * @param game
     */
    public Elf(Node node, Game game) {
        elf = new BetterCharacterControl(1f, 5f, 5f);
        hitBox = new GhostControl(new BoxCollisionShape(new Vector3f(1f, 5f, 2f)));
        elf.setPhysicsDamping(1);
        this.game = game;
        this.addControl(elf);
        this.addControl(hitBox);
        this.node = node;
        this.attachChild(this.node);

        init();
    }//end of Elf Constructor

    /**
     * init method.
     */
    private void init() {

        hp = 10;
        dmg = 10;
        movementSpeed = 5;

    }//end of init method

    /**
     * update method.
     */
    public void update(float tpf) {
        /**
         * This chunk of code controls where an Elf moves when its not in
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
                        elf.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        elf.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                    if (direction == 1) {
                        walkDirection.set(0, 0, walk.nextInt(5) - 11);
                        elf.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        elf.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                    if (direction == 2) {
                        walkDirection.set(walk.nextInt(5) - 11, 0, walk.nextInt(5));
                        elf.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        elf.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                    if (direction == 3) {
                        walkDirection.set(walk.nextInt(5), 0, walk.nextInt(5) - 11);
                        elf.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        elf.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }

                    if (direction == 4) {
                        walkDirection.set(walk.nextInt(5), 0, 0);
                        elf.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        elf.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                    if (direction == 5) {
                        walkDirection.set(walk.nextInt(5) - 11, 0, 0);
                        elf.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        elf.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                    if (direction == 6) {
                        walkDirection.set(walk.nextInt(5), 0, walk.nextInt(5));
                        elf.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        elf.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                    if (direction == 7) {
                        walkDirection.set(walk.nextInt(5) - 11, 0, walk.nextInt(5) - 11);
                        elf.setViewDirection(walkDirection.normalize().multLocal(movementSpeed));

                        elf.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                    }
                } else {
                    walkDirection.set(0, 0, 0);
                    elf.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

                }
                timer = 0;
            }
        }
        /**
         * If the elf is moving, play the walk animation.
         */
        if (hp > 0) {
            if (walkDirection.length() <= 0 && inCombat == false) {
                game.elfAnimation().setAnim("WalkCycle");
                game.elfAnimation().setSpeed(1f);
            }
        }
        /**
         * If the player aggros the elf, follow the player.
         */
        if (inCombat == true) {
            Vector3f playerLocation = game.getPlayerModel().getWorldTranslation();
            Vector3f elfLocation = game.getElfModel().getWorldTranslation();
            Vector3f offset = playerLocation.subtract(elfLocation); // offset from elf to player
            if (game.getElf().getWorldTranslation().distance(game.getPlayer().getWorldTranslation()) >= 40
                    && game.getElf().getWorldTranslation().distance(game.getPlayer().getWorldTranslation()) < 50) {
                elf.setWalkDirection(offset.normalize().mult(movementSpeed).setY(0));


            } else if (game.getElf().getWorldTranslation().distance(game.getPlayer().getWorldTranslation()) <= 40) {

                if (game.isMelee()) {
                    if (game.getPlayer().getAttackAnimation().getAnimationName().equals("Attack1")
                            || game.getPlayer().getAttackAnimation().getAnimationName().equals("Attack2")) {
                        playerToElf = game.getElf().getWorldTranslation().subtract(game.getPlayer()
                                .getWorldTranslation());

                        moveDir = playerToElf.cross(Vector3f.UNIT_X);

                        float faces1 = moveDir.dot(game.getPlayer().getCharacterControl().getViewDirection());
                        if (faces1 <= 0) {
                            elf.setWalkDirection(moveDir.negate().normalize().mult(movementSpeed).setY(0));
                        }
                    }
                }
                if (game.isMage()) {
                    playerToElf = game.getElf().getWorldTranslation().subtract(game.getPlayer().getWorldTranslation());
                    moveDir = playerToElf.cross(Vector3f.UNIT_X);
                    float faces1 = moveDir.dot(game.getPlayer().getCharacterControl().getViewDirection());
                    if (game.getPlayer().getAttackAnimation().getAnimationName().equals("SpellCast")) {

                        if (faces1 <= 0) {
                            elf.setWalkDirection(moveDir.negate().normalize().mult(movementSpeed).setY(0));

                        }
                    }
                }
                if (game.isRange()) {
                    playerToElf = game.getElf().getWorldTranslation().subtract(game.getPlayer().getWorldTranslation());
                    moveDir = playerToElf.cross(Vector3f.UNIT_X);
                    float faces1 = moveDir.dot(game.getPlayer().getCharacterControl().getViewDirection());
                    if (game.getPlayer().getAttackAnimation().getAnimationName().equals("Attack")) {

                        if (faces1 <= 0) {
                            elf.setWalkDirection(moveDir.negate().normalize().mult(movementSpeed).setY(0));

                        }
                    }
                }
            }
            elf.setViewDirection(offset.normalize().mult(movementSpeed).setY(0));
        }

    }//end of update

    /**
     * getWalkDirection method.
     *
     * @return
     */
    public Vector3f getWalkDirection() {
        return walkDirection;
    }//end of getWalkDirection

    /**
     * getHp method.
     */
    public int getHp() {
        return hp;
    }//end of getHp method

    /**
     * getHp method.
     */
    public void setHp(int amount) {
        amount = hp;
    }//end of getHp method

    /**
     * decrementHealth.
     */
    public void decrementHealth(int amount) {
        hp -= amount;
    }//end of decrementHealth

    /**
     * incrementHealth.
     */
    public void incrementHealth(int amount) {
        hp += amount;
    }//end of incrementHealth

    /**
     * getCharacterControl method.
     *
     * @return
     */
    public BetterCharacterControl getCharacterControl() {
        return elf;
    }//end of getCharacterControl method

    /**
     * getMovementSpeed method.
     *
     * @return
     */
    public float getMovementSpeed() {
        return movementSpeed;
    }//end of getMovementSpeed

    /**
     * setMovement speed method.
     *
     * @param amount
     */
    public void setMovementSpeed(int amount) {
        movementSpeed = amount;
    }//end of setMovementSPeed

    /**
     * inCombat method.
     *
     * @param combat
     */
    public void inCombat(boolean combat) {
        inCombat = true;
    }//end of inCombat

    /**
     * inCombat method.
     *
     * @param combat
     */
    public void notInCombat(boolean combat) {
        inCombat = false;
    }//end of inCombat

    /**
     * getCombat method.
     *
     * @return
     */
    public boolean getCombat() {
        return inCombat;
    }//end of inCombat

    /**
     * dead method.
     *
     * @return
     */
    public void dead(boolean answer) {
        dead = answer;
    }//end of dead

    /**
     * isDead method.
     *
     * @return
     */
    public boolean isDead() {
        return dead;
    }//end of isDead

    /**
     * respawn method.
     */
    public void respawn() {

        hp = 100;
        elf.setEnabled(true);
        hitBox.setEnabled(true);
        dead = false;
        elf.warp(new Vector3f(50f, 2f, 320f));
        game.getApp().getRootNode().attachChild(game.getUI().elfLevel());
        game.getApp().getRootNode().attachChild(game.getUI().elfText());
        game.getApp().getRootNode().attachChild(game.getElf());
        game.getBulletAppState().getPhysicsSpace().add(game.getElf());

    }//end of respawn

    /**
     * hitBox method.
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
}//end of Elf class
