package Item;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import game.Game;
import java.util.Random;
import jme3tools.optimize.GeometryBatchFactory;

/**
 * Item Class
 *
 * @author Jacob Amaral
 */
public class Item {

    private Game game;
    private Spatial gold, healthPotion;
    private Spatial[] amountOfGold;
    private Spatial[] amountOfPotions = new Spatial[50];
    private Node goldCoins, healthPotions, arrows;
    private Random random = new Random();
    private float timer, arrowRemoveTimer;
    private float movingTextY = 4f, movingTextZ;
    private int goldAmount, potionAmount;
    private float dropTimer;
    private boolean goldIsThere, potionIsThere, playerIsHealing, pickingUpGold;
    private Spatial arrow;
    private Spatial[] createArrow;
    private RigidBodyControl arrowPhysics;
    private CollisionShape arrowShape;
    private GhostControl arrowHitBox;
    private ParticleEmitter arrowStreak;
    private Node arrowEffect;

    /**
     * Item Constructor
     *
     * @param game
     */
    public Item(Game game) {
        this.game = game;
    }//end of Item constructor

    /**
     * dropRandomItem method
     *
     * @param assetManager
     */
    public void dropRandomItem(AssetManager assetManager, Vector3f location, int amount) {
        /**
         * Gold coins that drop on the ground.
         */
        if (random.nextInt(3) == 1) {
            gold = assetManager.loadModel("Models/Items/Gold/Cylinder.mesh.j3o");
            gold.setMaterial(assetManager.loadMaterial("Models/Items/Gold/gold.j3m"));

            goldCoins = new Node();

            amountOfGold = new Spatial[amount + 1];

            for (int i = 0; i < amount; i++) {
                amountOfGold[i] = gold.clone();
                amountOfGold[i].setShadowMode(ShadowMode.Cast);
                amountOfGold[i].rotate(300f, 20f, 300f);
                amountOfGold[i].setMaterial(assetManager.loadMaterial("Models/Items/Gold/gold.j3m"));
                amountOfGold[i].setLocalScale(0.25f, 0.25f, 0.25f);
                amountOfGold[i].setName("Gold");
                goldCoins.attachChild(amountOfGold[i]);
                for (float r = 0; r < 0.25; r += 0.01) {
                    amountOfGold[i].setLocalTranslation(location.add(new Vector3f(random.nextInt(4), r, random.nextInt(4))));
                }
            }
            game.getApp().getRootNode().attachChild(goldCoins);

            GeometryBatchFactory.optimize(goldCoins, true);
            goldAmount = random.nextInt(4) + 1;
            goldIsThere = true;
        } /**
         * Health potions that drop on the ground.
         */
        else{

            healthPotion = assetManager.loadModel("Models/Items/Potion/Cylinder.mesh.j3o");
            healthPotion.setMaterial(assetManager.loadMaterial("Models/Items/Potion/potion.j3m"));

            healthPotions = new Node();


            for (int i = 0; i < 1; i++) {
                amountOfPotions[i] = healthPotion.clone();
                amountOfPotions[i].rotate(6f, 0f, 7f);
                amountOfPotions[i].setMaterial(assetManager.loadMaterial("Models/Items/Potion/potion.j3m"));
                amountOfPotions[i].setShadowMode(ShadowMode.Cast);
                amountOfPotions[i].setLocalTranslation(location.add(new Vector3f(random.nextInt(3), 1, random.nextInt(3))));

                amountOfPotions[i].setLocalScale(0.2f, 0.2f, 0.2f);
                healthPotions.attachChild(amountOfPotions[i]);
            }
            game.getApp().getRootNode().attachChild(healthPotions);
            GeometryBatchFactory.optimize(healthPotions, true);
            potionAmount = random.nextInt(4) + 1;
            potionIsThere = true;
        }


    }//end of dropRandomItem

    /**
     * removeItem method. Removes items from the ground.
     */
    public void removeItem() {
        if (goldIsThere) {
            goldCoins.detachAllChildren();
        }
        if (potionIsThere) {
            healthPotions.detachAllChildren();
        }

    }//end of removeItem method

    /**
     * removeArrow method. Removes arrows from the ground.
     */
    public void removeArrow() {
        arrowHitBox.setEnabled(false);
        arrowPhysics.setEnabled(false);
        arrow.removeFromParent();
        game.getApp().getRootNode().detachChild(arrows);
    }//end of removeItem method

    /**
     * createArrow method - arrow is spawned when the player is a range class
     * and they press the spacebar.
     */
    public void createArrow(AssetManager assetManager, BulletAppState bulletAppState) {
        //Arrow model
        arrow = assetManager.loadModel("Models/Player/Range/arrow.j3o");
        arrow.setMaterial(assetManager.loadMaterial("Models/Player/Range/arrowMaterial.j3m"));
        //Arrow hitbox
        arrowHitBox = new GhostControl(new BoxCollisionShape(new Vector3f(1f, 1.5f, 1f)));
        /**
         * This spawns the physical arrow.
         */
        arrows = new Node();
        arrow.scale(2f);
        arrow.setLocalTranslation(game.getPlayer().getWorldTranslation().add(new Vector3f(0, 6, 0)));
        arrow.setLocalRotation(game.getPlayer().getLocalRotation());
        arrow.rotate(3.15f, 0, 0);
        arrow.setShadowMode(ShadowMode.Cast);
        arrowShape = CollisionShapeFactory.createBoxShape(arrow);
        arrowPhysics = new RigidBodyControl(arrowShape);
        arrowPhysics.setLinearVelocity(game.getApp().getCamera().getDirection().multLocal(new Vector3f(200, 10, 200)));
        arrow.addControl(arrowPhysics);
        arrowHitBox.setPhysicsLocation(new Vector3f(0, 0, -2f));
        arrows.attachChild(arrow);
        arrow.addControl(arrowHitBox);
        arrows.setLocalTranslation(game.getPlayer().getLocalTranslation().add(new Vector3f(0, 0, 2).mult(1.8f).addLocal(new Vector3f(0, 0, 2).mult(0.9f))));
        /**
         * Arrowstreak effect when shot.
         */
        arrowStreak =
                new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        Material mat_red = new Material(assetManager,
                "Common/MatDefs/Misc/Particle.j3md");
        mat_red.setTexture("Texture", assetManager.loadTexture(
                "Effects/default.png"));
        arrowStreak.setMaterial(mat_red);
        arrowStreak.setImagesX(1);
        arrowStreak.setImagesY(1);
        arrowStreak.setEndColor(ColorRGBA.DarkGray);
        arrowStreak.setStartColor(ColorRGBA.DarkGray);
        arrowStreak.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        arrowStreak.setStartSize(0.5f);
        arrowStreak.setEndSize(0.5f);
        arrowStreak.setGravity(0, 0, 0);
        arrowStreak.setLowLife(2.5f);
        arrowStreak.setHighLife(5f);
        arrowStreak.getParticleInfluencer().setVelocityVariation(0.3f);
        arrowEffect = new Node();
        arrowEffect.attachChild(arrowStreak);
        game.getApp().getRootNode().attachChild(arrowEffect);
        bulletAppState.getPhysicsSpace().add(arrowPhysics);
        bulletAppState.getPhysicsSpace().add(arrowHitBox);
        game.getApp().getRootNode().attachChild(arrows);
    }//end of createArrow method

    /**
     * Update method.
     */
    public void update(float tpf) {
        /**
         * Scrolling Gold text and removing gold from the ground.
         */
        timer = timer + tpf;
        dropTimer = dropTimer + tpf;
        if (goldIsThere) {
            if (amountOfGold[0] != null) {
                if (game.getPlayer().getWorldTranslation().distance(amountOfGold[0].getWorldTranslation()) <= 9) {
                    timer = timer + tpf;
                    if (timer > 0.1f) {
                        game.getPlayer().incrementGold(amountOfGold.length);
                        pickingUpGold = true;
                        removeItem();
                        goldIsThere = false;
                        timer = 0;
                    }
                }
            }
        }
        if (pickingUpGold) {
            timer = timer + tpf;

            if (timer > 0.0001f) {
                game.goldTextNode().attachChild(game.getUI().goldText());
                game.getApp().getRootNode().attachChild(game.goldTextNode());
                game.getUI().goldText().setText("+" + String.valueOf(amountOfGold.length) + "g");

                game.goldTextNode().setLocalTranslation(game.getPlayer().getWorldTranslation()
                        .add(0, movingTextY += 0.03f, 0));
                game.getUI().goldText().setAlpha(1);

                if (movingTextY >= 4.1f) {
                    removeItem();
                    game.goldTextNode().setLocalTranslation(game.getPlayer().getWorldTranslation()
                            .add(0, movingTextY, movingTextZ += 0.01f));
                }
                if (movingTextY >= 4.2f && movingTextY <= 4.3f) {
                }
                if (movingTextY >= 6f && movingTextY <= 6.49f) {
                    game.getUI().goldText().setAlpha(-200000);

                } else if (movingTextY >= 6.5f) {
                    game.getUI().goldText().setAlpha(-4000000);
                } else if (movingTextY >= 7f) {
                    game.getUI().goldText().setAlpha(-40000000);

                }
                if (movingTextY > 7.5f) {
                    game.getApp().getRootNode().detachChild(game.goldTextNode());
                    pickingUpGold = false;
                    goldIsThere = false;
                    movingTextY = 4f;
                    movingTextZ = 0f;
                    timer = 0;
                }
            }

        }
        //end of Gold text and removing gold
        /**
         * Scrolling health text and removing hp potions.
         */
        if (potionIsThere) {
            if (game.getPlayer().getWorldTranslation().distance(amountOfPotions[0].getWorldTranslation()) <= 9) {
                timer = timer + tpf;
                if (timer > 0.1f) {
                    game.getPlayer().incrementHealth(20);
                    playerIsHealing = true;
                    removeItem();
                    potionIsThere = false;
                    timer = 0;
                }
            }
        }
        if (playerIsHealing) {
            timer = timer + tpf;

            if (timer > 0.0001f) {
                game.healthTextNode().attachChild(game.getUI().healthText());
                game.getApp().getRootNode().attachChild(game.healthTextNode());
                game.getUI().healthText().setText("+" + String.valueOf(20));

                game.healthTextNode().setLocalTranslation(game.getPlayer().getWorldTranslation()
                        .add(0, movingTextY += 0.03f, 0));
                game.getUI().healthText().setAlpha(1);

                if (movingTextY >= 4.1f) {
                    removeItem();
                    game.healthTextNode().setLocalTranslation(game.getPlayer().getWorldTranslation()
                            .add(0, movingTextY, movingTextZ += 0.01f));
                }
                if (movingTextY >= 4.2f && movingTextY <= 4.3f) {
                }
                if (movingTextY >= 6f && movingTextY <= 6.49f) {
                    game.getUI().healthText().setAlpha(-200000);

                } else if (movingTextY >= 6.5f) {
                    game.getUI().healthText().setAlpha(-4000000);
                } else if (movingTextY >= 7f) {
                    game.getUI().healthText().setAlpha(-40000000);

                }
                if (movingTextY > 7.5f) {
                    game.getApp().getRootNode().detachChild(game.healthTextNode());
                    playerIsHealing = false;
                    potionIsThere = false;
                    movingTextY = 4f;
                    movingTextZ = 0f;
                    timer = 0;
                }
            }

        }

    }//end of update method.

    /**
     * goldCoin method
     *
     * @return gold
     */
    public Spatial goldCoin() {
        return gold;
    }//end of goldCoin

    /**
     * getArrow method
     *
     * @return
     */
    public GhostControl arrowHitbox() {
        return arrowHitBox;
    }//end of getArrow method

    /**
     * getArrowEffect method
     *
     * @return
     */
    public Node getArrowEffect() {
        return arrowEffect;
    }//end of arrowEffect

    /**
     * arrowPhysics method
     *
     * @return
     */
    public RigidBodyControl arrowPhysics() {
        return arrowPhysics;
    }//end of arrowPhysics

    /**
     * arrowStreak method
     *
     * @return
     */
    public ParticleEmitter arrowStreak() {
        return arrowStreak;
    }//end of arrowStreak
}//end of Item class
