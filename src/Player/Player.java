package Player;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.animation.Skeleton;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import game.Game;
import java.util.Random;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import java.io.IOException;

/**
 * Player Class
 *
 * @author Jacob Amaral
 */
public class Player extends Node implements ActionListener, AnalogListener, AnimEventListener{
    //3rd Person view

    private Game game;
    private ThirdPersonCamera camera;
    private Camera cam;
    private Spatial model;
    private BetterCharacterControl characterControl;
    private GhostControl hitBox;
    private AnimChannel legs, attackChannel, fullBody;
    private AnimControl animControl;
    private InputManager inputManager;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false,
            dead, roll, teleporting, shotArrow, arrowEffect, attack;
    private float mouselookSpeed = FastMath.PI;
    private int level, hp, exp, totalHp, strength, gold, critChance;
    private float movementSpeed;
    private Random random = new Random();
    private String idleAnim = "Idle", walkAnim = "Run", attackAnim1 = "Attack1",
            attackAnim2 = "Attack2", deathAnim = "Death", name;
    private AssetManager assetManager;
    private BulletAppState bulletAppState;
    private float timer;
    private ParticleEmitter teleportEffect, spellCastEffect;
    private Node teleportNode1, teleportNode2, spellCastNode, spellEffectNode;
    private GhostControl spellCastHitBox;
    private RigidBodyControl spellCastPhysics;
    private CollisionShape spellShape;
    private Geometry sphere;
    private boolean castSpell;
    private BloomFilter bloom;
    private boolean removeSpell;
    private CollisionResults results;
    private Ray ray;
    private boolean canCast = true;
    private boolean canShoot = true;
    private int health;
    private Savable playerModel;
    private Savable location;

    /**
     * Player constructor
     *
     * @param model
     * @param inputManager
     * @param cam
     */
    public Player(Spatial model, InputManager inputManager, Camera cam, Game game, AssetManager assetManager, BulletAppState bulletAppState) {
        super();
        this.game = game;
        this.cam = cam;
        this.inputManager = inputManager;
        this.assetManager = assetManager;
        this.bulletAppState = bulletAppState;
        camera = new ThirdPersonCamera("CamNode", cam, this, game);
        /**
         * Models features, size and where it spawns.
         */
        this.model = model;
        this.model.scale(.03f);
        this.model.setLocalTranslation(0f, 0, 0f);
        this.attachChild(this.model);
        hitBox = new GhostControl(new BoxCollisionShape(new Vector3f(1.5f, 6f, 2f)));
        setUpKeys();

        init();
    }//end of ThirdPersonCameraNode

    /**
     * init method - initializes variables for the player.
     */
    private void init() {
        hp = 50;
        critChance = 10;
        totalHp = 50;
        movementSpeed = 40f;
        level = 1;
        gold = 0;
        strength = 1;
        exp = 0;
        dead = false;

        /**
         * Physics of the player
         */
        characterControl = new BetterCharacterControl(1.5f, 5f, 20f);

        this.addControl(characterControl);
        this.addControl(hitBox);
        /**
         * Animations, bones are seperated into different channels to play
         * animations simultaneously.
         */
        animControl = game.getPlayerModel().getControl(AnimControl.class);

        animControl.addListener(Player.this);
        legs = animControl.createChannel();
        attackChannel = animControl.createChannel();
        fullBody = animControl.createChannel();

        legs.addFromRootBone("thigh.R");
        legs.addFromRootBone("thigh.L");

        attackChannel.addFromRootBone("Bone");
        if (game.isRange()) {
            attackChannel.addFromRootBone("Bone.001");
            attackChannel.addFromRootBone("Bone.002");
            attackChannel.addFromRootBone("Bone.003");
            attackChannel.addFromRootBone("Bone.004");
            attackChannel.addFromRootBone("Bone.005");
        }
        if (game.isMage()) {
            attackChannel.addFromRootBone("Bone.001");
            attackChannel.addFromRootBone("Bone");
            attackChannel.addFromRootBone("StaffBone");
        }
        attackChannel.addFromRootBone("spine");

        attackChannel.setAnim(idleAnim);

    }//end of init

    /**
     * update method.
     */
    public void update(float tpf) {

        /**
         * This allows the player to move.
         */
        Vector3f camDir = cam.getDirection().clone();
        camDir.y = 0;
        Vector3f camLeft = cam.getLeft().clone();
        camLeft.y = 0;
        walkDirection.set(0, 0, 0);

        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
        characterControl.setWalkDirection(walkDirection.normalize().multLocal(movementSpeed));

        handleAnimations(tpf);
        /**
         * We dont want players having more hp then their max.
         */
        if (level == 1) {
            if (hp > 50) {
                hp = 50;
            }
        }
        if (level == 2) {
            if (hp > 75) {
                hp = 75;
            }
        }
        if (level == 3) {
            if (hp > 105) {
                hp = 105;
            }
        }
        if (level == 4) {
            if (hp > 140) {
                hp = 140;
            }
        }
        if (level == 5) {
            if (hp > 180) {
                hp = 180;
            }
        }
        if (level == 6) {
            if (hp > 220) {
                hp = 220;
            }
        }
        if (level == 7) {
            if (hp > 260) {
                hp = 260;
            }
        }
        if (level == 8) {
            if (hp > 300) {
                hp = 300;
            }
        }
        if (level == 9) {
            if (hp > 350) {
                hp = 350;
            }
        }
        if (level == 10) {
            if (hp > 400) {
                hp = 400;
            }
        }
        /**
         * We dont want players having -hp.
         */
        if (hp <= 0) {
            walkDirection.set(0, 0, 0);
            characterControl.setWalkDirection(walkDirection);
            hp = 0;
        }
        /**
         * If the player presses space, shoot an arrow!
         */
        if (game.isRange()) {
            if (shotArrow) {
                if (canShoot) {
                    if (attackChannel.getTime() > 0.5f) {
                        game.getItem().createArrow(assetManager, bulletAppState);
                        results = new CollisionResults();
                        //Aim a ray from the cameras location and direction
                        ray = new Ray(cam.getLocation(), cam.getDirection());
                        //Collect intersections between Ray and anything in the scene
                        game.getApp().getRootNode().collideWith(ray, results);
                        canShoot = false;
                        arrowEffect = true;
                        shotArrow = false;
                    }
                }
            }
            /**
             * If an arrow is shot, display some effects that follow the arrow.
             */
            if (arrowEffect) {
                if (game.getItem().arrowPhysics().getLinearVelocity().length() > 0) {
                    game.getItem().arrowStreak().setLocalTranslation(game.getItem().arrowHitbox().getPhysicsLocation());

                }
                /**
                 * Remove the arrow effects.
                 */
                if (results.size() > 0) {
                    timer = timer + tpf;
                    if (timer > 1f) {
                        // The closest collision point is what was truly hit:
                        CollisionResult closest = results.getClosestCollision();
                        game.getItem().getArrowEffect().detachAllChildren();
                        game.getItem().getArrowEffect().removeFromParent();
                        game.getApp().getRootNode().detachChild(game.getItem().getArrowEffect());
                        game.getItem().removeArrow();
                        arrowEffect = false;
                        canShoot = true;
                        timer = 0;
                    }
                }
            }
        }
        /**
         * If a player is teleporting (and the mage class) display an effect!
         * This statement is before you actually teleport/move, so just when the
         * player is doing the animation.
         */
        if (teleporting) {
            teleportNode1 = new Node();
            characterControl.setWalkDirection(new Vector3f(0, 0, 0));
            /**
             * Cool effect for teleporting mages.
             */
            teleportEffect = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
            teleportEffect.setNumParticles(100);
            teleportEffect.setParticlesPerSec(100);
            teleportEffect.setLowLife(6f);
            teleportEffect.setHighLife(12f);
            teleportEffect.setVelocityVariation(0.35f);
            Material mat = new Material(assetManager,
                    "Common/MatDefs/Misc/Particle.j3md");
            mat.setTexture("Texture", assetManager.loadTexture(
                    "Effects/default.png"));
            teleportEffect.setStartColor(ColorRGBA.Green);
            teleportEffect.setEndColor(ColorRGBA.DarkGray);
            teleportEffect.setImagesX(10);
            teleportEffect.setImagesY(10);
            teleportEffect.setGravity(0, 1, 0);
            teleportEffect.setMaterial(mat);
            teleportEffect.setLocalTranslation(game.getPlayer().getWorldTranslation().add(0, 5f, 0));
            teleportEffect.setShadowMode(ShadowMode.Cast);
            teleportNode1.attachChild(teleportEffect);
            game.getApp().getRootNode().attachChild(teleportNode1);
            teleporting = false;
        }
        /**
         * If a player is teleporting (and the mage class) display an effect!
         * This is when the player has actually moved when teleporting.
         */
        if (attackChannel.getAnimationName().equalsIgnoreCase("Transportation")) {
            characterControl.setWalkDirection(new Vector3f(0, 0, 0));
            if (attackChannel.getTime() >= 1.5f && attackChannel.getTime() <= 1.53f) {
                characterControl.warp(camera.getTeleportNode().getWorldTranslation().setY(1f));

            }
            /**
             * Cool effect for teleporting mages.
             */
            teleportNode2 = new Node();
            teleportEffect = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
            teleportEffect.setNumParticles(100);
            teleportEffect.setParticlesPerSec(100);
            teleportEffect.setLowLife(6f);
            teleportEffect.setHighLife(12f);
            teleportEffect.setVelocityVariation(0.35f);
            Material mat = new Material(assetManager,
                    "Common/MatDefs/Misc/Particle.j3md");
            mat.setTexture("Texture", assetManager.loadTexture(
                    "Effects/default.png"));
            teleportEffect.setStartColor(ColorRGBA.Green);
            teleportEffect.setEndColor(ColorRGBA.DarkGray);
            teleportEffect.setImagesX(10);
            teleportEffect.setImagesY(10);
            teleportEffect.setGravity(0, 1, 0);
            teleportEffect.setMaterial(mat);
            teleportEffect.setShadowMode(ShadowMode.Cast);
            teleportEffect.setLocalTranslation(game.getPlayer().getWorldTranslation().add(0, 5f, 0));
            teleportNode1.attachChild(teleportEffect);
            game.getApp().getRootNode().attachChild(teleportNode2);
            setUpKeys();
        }
        /**
         * Remove the teleporting particles.
         */
        if (game.getApp().getRootNode().hasChild(teleportNode1)) {
            timer = timer + tpf;
            if (timer >= 4f) {
                teleportNode1.removeFromParent();
                teleportNode1.detachAllChildren();
                game.getApp().getRootNode().detachChild(teleportNode1);
                timer = 0;
            }
        }
        /**
         * Remove the teleporting particles.
         */
        if (game.getApp().getRootNode().hasChild(teleportNode2)) {
            timer = timer + tpf;
            if (timer >= 4f) {
                teleportNode2.removeFromParent();
                teleportNode2.detachAllChildren();
                game.getApp().getRootNode().detachChild(teleportNode2);
                timer = 0;
            }
        }

    }//end of update method

    /**
     * handleAnimations method.
     */
    private void handleAnimations(float tpf) {
        if (hp > 0) {
            if (game.isMelee()) {
                /**
                 * If a player is a melee class and presses space do an attack
                 * animation.
                 */
                if (attack) {
                    if (random.nextInt(2) == 1) {
                        attackChannel.setAnim(attackAnim1, 2f);
                        attackChannel.setSpeed(3f);
                        attackChannel.setLoopMode(LoopMode.DontLoop);
                    } else {
                        attackChannel.setAnim(attackAnim2, 2f);
                        attackChannel.setSpeed(3f);
                        attackChannel.setLoopMode(LoopMode.DontLoop);
                    }
                }
            }
            if (game.isRange()) {

                /**
                 * If a player is rolling and a range class, increase the
                 * movement speed.
                 */
                if (attackChannel.getAnimationName().equalsIgnoreCase("Roll")) {
                    movementSpeed = 60f;

                } else {
                    movementSpeed = 30f;
                }
            }
            if (game.isMage()) {
                /**
                 * If a mage presses space, cast a spell.
                 */
                if (castSpell) {
                    if (canCast) {
                        if (attackChannel.getTime() >= 0.5f) {
                            results = new CollisionResults();
                            //Aim a ray from the cameras location and direction
                            ray = new Ray(cam.getLocation(), cam.getDirection());
                            //Collect intersections between Ray and anything in the scene
                            game.getApp().getRootNode().collideWith(ray, results);

                            Sphere ball = new Sphere(32, 32, 2f);
                            sphere = new Geometry("Spell", ball);
                            Material mat = new Material(assetManager,
                                    "Common/MatDefs/Misc/Unshaded.j3md");

                            mat.setColor("GlowColor", ColorRGBA.Green);
                            //Bloom
                            bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
                            game.getFpp().addFilter(bloom);
                            bloom.setDownSamplingFactor(3f);
                            sphere.setMaterial(mat);
                            setSpellCastHitBox(new GhostControl(new SphereCollisionShape(3f)));
                            setSpellCastNode(new Node());
                            spellCastPhysics = new RigidBodyControl(new SphereCollisionShape(3f));

                            sphere.setShadowMode(ShadowMode.Cast);
                            sphere.setLocalScale(1f, 1f, 1f);

                            sphere.setLocalTranslation(camera.getSpellNode().getWorldTranslation().setY(5f));

                            sphere.addControl(getSpellCastPhysics());
                            getSpellCastPhysics().setLinearVelocity(game.getApp().getCamera().getDirection().multLocal(new Vector3f(200, 10, 200)));
                            sphere.addControl(getSpellCastHitBox());

                            getSpellCastNode().attachChild(sphere);
                            spellEffectNode = new Node();


                            getSpellCastNode().setLocalTranslation(game.getPlayer().getWorldTranslation().add(new Vector3f(0, 0, 2).mult(1.8f).addLocal(new Vector3f(0, 0, 2).mult(0.9f))));
                            game.getBulletAppState().getPhysicsSpace().add(getSpellCastPhysics());
                            game.getBulletAppState().getPhysicsSpace().add(getSpellCastHitBox());
                            game.getApp().getRootNode().attachChild(getSpellCastNode());
                            removeSpell = true;
                            canCast = false;
                            castSpell = false;
                        }
                    }
                }
                if (removeSpell) {
                    /**
                     * If the spell collides with something, remove it.
                     */
                    if (results.size() > 0) {
                        timer = timer + tpf;
                        if (timer > 1f) {
                            // The closest collision point is what was truly hit:
                            CollisionResult closest = results.getClosestCollision();

                            game.getApp().getRootNode().detachChild(spellCastNode);
                            game.getApp().getRootNode().detachChild(spellEffectNode);
                            game.getPlayer().getSpellCastHitBox().setEnabled(false);
                            game.getPlayer().getSpellCastPhysics().setEnabled(false);
                            removeSpell = false;
                            canCast = true;
                            timer = 0;
                        }
                    }
                }
            }
        }

    }//end of handleAnimations method

    /**
     * setUpKeys method
     */
    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Attack", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Roll", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("TurnLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("TurnRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("MouselookDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping("MouselookUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("Exit", new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Attack");
        inputManager.addListener(this, "Roll");
        inputManager.addListener(this, "TurnLeft");
        inputManager.addListener(this, "TurnRight");
        inputManager.addListener(this, "MouselookDown");
        inputManager.addListener(this, "MouselookUp");
        inputManager.addListener(this, "Exit");
    }//end of setupKeys method

    /**
     * onAction method
     *
     * @param binding
     * @param value
     * @param tpf
     */
    @Override
    public void onAction(String binding, boolean value, float tpf) {
        if (hp > 0) {
            if (binding.equals("Left")) {
                left = value;
                legs.setAnim(walkAnim);
                legs.setSpeed(2f);
                legs.setLoopMode(LoopMode.Loop);
            } else if (binding.equals("Right")) {
                right = value;
                legs.setAnim(walkAnim);
                legs.setSpeed(2f);
                legs.setLoopMode(LoopMode.Loop);
            } else if (binding.equals("Up")) {
                up = value;
                legs.setAnim(walkAnim);
                legs.setSpeed(2f);
                legs.setLoopMode(LoopMode.Loop);
            } else if (binding.equals("Down")) {
                down = value;
                legs.setAnim(walkAnim);
                legs.setSpeed(2f);
                legs.setLoopMode(LoopMode.Loop);

            }
            if (game.isMage() || game.isRange()) {
                if (binding.equals("Attack") && !value) {
                    attack = value;
                    if (game.isRange()) {
                        attackChannel.setAnim("Attack");
                        attackChannel.setSpeed(1.5f);
                        attackChannel.setLoopMode(LoopMode.DontLoop);
                        shotArrow = true;
                    } else if (game.isMage()) {
                        castSpell = true;
                        attackChannel.setAnim("SpellCast");
                        attackChannel.setSpeed(1.5f);
                        attackChannel.setLoopMode(LoopMode.DontLoop);
                    }
                }
            } else {
                if (binding.equals("Attack")) {
                    attack = value;
                }
            }
            if (binding.equals("Roll") && !value) {
                roll = value;
                if (game.isRange()) {
                    if (attackChannel.getTime() == 0
                            || attackChannel.getAnimMaxTime() > 0.6f) {
                        fullBody.setAnim("Roll");
                        attackChannel.setAnim("Roll");
                        fullBody.setTime(0.5f);
                        attackChannel.setTime(0.5f);
                        fullBody.setSpeed(1f);
                        attackChannel.setSpeed(1f);
                    }
                }
                if (game.isMage()) {
                    inputManager.clearMappings();
                    characterControl.setWalkDirection(new Vector3f(0, 0, 0));
                    fullBody.setAnim("Transportation");
                    attackChannel.setAnim("Transportation");
                    fullBody.setSpeed(0.6f);
                    attackChannel.setSpeed(0.6f);
                    teleporting = true;
                }
            }
        }
        if (binding.equals("Exit")) {
            System.exit(0);
        }
    }//end of onAction

    /**
     * onAnimCycleDone - when an animation is done, do something
     *
     * @param control
     * @param channel
     * @param animName
     */
    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if (game.isMelee()) {
            if (game.getPlayer().getHealth() <= 0) {
                walkDirection.set(0, 0, 0);
                characterControl.setWalkDirection(walkDirection);
                if (animName.equals("Attack1") || animName.equals("Attack2") || animName.equals("Walk")
                        || animName.equals("Idle")) {
                    fullBody.setAnim("Death");
                }
            }
        } else if (game.isRange()) {
            if (game.getPlayer().getHealth() <= 0) {
                walkDirection.set(0, 0, 0);
                characterControl.setWalkDirection(walkDirection);
                if (animName.equals("Attack") || animName.equals("Walk") || animName.equals("Roll")
                        || animName.equals("Idle")) {
                    fullBody.setAnim("Death");
                    attackChannel.setAnim("Death");
                    legs.setAnim("Death");
                }
            }
        } else if (game.isMage()) {
            if (game.getPlayer().getHealth() <= 0) {
                walkDirection.set(0, 0, 0);
                characterControl.setWalkDirection(walkDirection);
                if (animName.equals("SpellCast") || animName.equals("Run")
                        || animName.equals("Idle")) {
                    fullBody.setAnim("Death");
                    attackChannel.setAnim("Death");
                    legs.setAnim("Death");
                }
            }
        }
        if (animName.equals(deathAnim)) {
            if (walkDirection.length() <= 0) {
                channel.setAnim("Idle");
            }
            respawn();
        }

        if (hp > 0) {
            if (walkDirection.length() <= 0) {
                channel.setSpeed(100f);
                channel.setLoopMode(LoopMode.DontLoop);
                if (!attack && !left && !right && !up && !down) {

                    channel.setAnim(idleAnim);
                    channel.setSpeed(0.4f);
                }
                if (game.isMelee()) {
                    if (animName.equals(attackAnim1) || animName.equals(attackAnim2)) {
                        channel.setAnim(idleAnim, 0.5f);
                        channel.setSpeed(0.4f);
                    }
                } else if (game.isRange()) {
                    if (animName.equals("Attack")) {
                        channel.setAnim("Idle", 0.5f);
                        channel.setSpeed(0.4f);
                    }
                } else if (game.isMage()) {
                    if (animName.equals("SpellCast")) {
                        channel.setAnim("Idle", 0.5f);
                        channel.setSpeed(0.4f);
                    }
                }
            } else if (walkDirection.length() > 0) {
                channel.setAnim(walkAnim);
                channel.setSpeed(2f);
                channel.setLoopMode(LoopMode.Loop);
                if (game.isMelee()) {
                    if (animName.equals(attackAnim1) || animName.equals(attackAnim2)) {
                        channel.setAnim(walkAnim);
                        channel.setSpeed(2f);
                        channel.setLoopMode(LoopMode.Loop);
                    }
                } else if (game.isRange()) {
                    if (animName.equals("Attack")) {
                        channel.setAnim(walkAnim);
                        channel.setSpeed(2f);
                        channel.setLoopMode(LoopMode.Loop);
                    }
                } else if (game.isMage()) {
                    if (animName.equals("SpellCast")) {
                        channel.setAnim(walkAnim);
                        channel.setSpeed(2f);
                        channel.setLoopMode(LoopMode.Loop);
                    }
                }
            }
        }
    }//end of onAnimCycleDone

    /**
     * onAnimChange method
     *
     * @param control
     * @param channel
     * @param animName
     */
    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        if (animName.equals("Idle")) {
            if (walkDirection.length() > 0) {
                attackChannel.setAnim(walkAnim);
            }
            if (walkDirection.length() <= 0) {
                if (animName.equals("walkAnim")) {
                    attackChannel.setAnim("Idle");
                }
            }
        }
    }//end of onAnimChange

    /**
     * onAnalog method
     *
     * @param binding
     * @param value
     * @param tpf
     */
    //Analog handler for mouse movement events.
    //It is assumed that we want horizontal movements to turn the character,
    //while vertical movements only make the camera rotate up or down.
    @Override
    public void onAnalog(String binding, float value, float tpf) {
        if (binding.equals("TurnLeft")) {
            Quaternion turn = new Quaternion();
            turn.fromAngleAxis(mouselookSpeed * value, Vector3f.UNIT_Y);
            characterControl.setViewDirection(turn.mult(characterControl.getViewDirection()));

        } else if (binding.equals("TurnRight")) {
            Quaternion turn = new Quaternion();
            turn.fromAngleAxis(-mouselookSpeed * value, Vector3f.UNIT_Y);
            characterControl.setViewDirection(turn.mult(characterControl.getViewDirection()));
        } else if (binding.equals("MouselookDown")) {
            camera.verticalRotate(mouselookSpeed * value);
        } else if (binding.equals("MouselookUp")) {
            camera.verticalRotate(-mouselookSpeed * value);
        }
    }//end of onAnalog

    /**
     * setName method
     *
     * @param name
     */
    @Override
    public void setName(String name) {
        this.name = name;
    } //end of setName method

    /**
     * setMovementSpeed method
     *
     * @param amount
     */
    public void setMovementSpeed(int amount) {
        movementSpeed = amount;
    }//end of setMovementSpeed

    /**
     * getMovementSpeed method
     *
     * @return
     */
    public float getMovementSpeed() {
        return movementSpeed;
    }//end of getMovementSpeed

    /**
     * decrementHealth method
     *
     * @param amount
     */
    public void decrementHealth(int amount) {
        hp -= amount;
    } //end of decrementHealth method

    /**
     * incrementHealth method
     *
     * @param amount
     */
    public void incrementHealth(int amount) {
        hp += amount;
    } //end of incrementHealth method

    public void decrementExp(int amount) {
        exp -= amount;
    } //end of decrementExp method

    /**
     * incrementExp method
     *
     * @param amount
     */
    public void incrementExp(int amount) {
        exp += amount;
    } //end of incrementExp method

    /**
     * getHealth method
     *
     * @return health
     */
    public int getHealth() {
        return hp;
    } //end of getHealth method

    /**
     * getTotalHealth method
     *
     * @return health
     */
    public int getTotalHealth() {
        return totalHp;
    } //end of getTotalHealth method

    /**
     * setTotalHealth method
     */
    public void setTotalHealth(int amount) {
        totalHp = amount;
    }//end of setTotalHealth

    /**
     * getExp method
     *
     * @return
     */
    public int getExp() {
        return exp;
    } //end of getExp method

    /**
     * setExp method
     */
    public void setExp(int amount) {
        this.exp = amount;
    } //end of setExp method

    /**
     * setHealth method
     *
     * @param amount
     */
    public void setHealth(int amount) {
        hp = amount;
    } //end of setHealth method

    /**
     * getLevel
     *
     * @return level
     */
    public int getLevel() {
        return level;
    } //end of getLevel method

    /**
     * setLevel method
     */
    public void setLevel(int amount) {
        this.level = amount;
    } //end of setLevel method

    /**
     * geStr
     *
     * @return strength
     */
    public int getStr() {
        return strength;
    } //end of getStrength method

    /**
     * setStr method
     */
    public void setStr(int amount) {
        this.strength = amount;
    } //end of setStr method

    /**
     * incrementLevel method
     *
     * @param amount
     */
    public void incrementLevel(int amount) {
        level += amount;
    } //end of incrementLevel method

    /**
     * getName method
     *
     * @return
     */
    @Override
    public String getName() {
        return name;
    } //end of getName method

    /**
     * setDead method
     *
     * @param answer
     */
    public void setDead(boolean answer) {
        dead = answer;
    } //end of setDead method

    /**
     * getCharacterControl method
     *
     * @return
     */
    public BetterCharacterControl getCharacterControl() {

        return characterControl;
    }//end of getCharacterControl method

    /**
     * getCameraNode method
     *
     * @return
     */
    public ThirdPersonCamera getCameraNode() {
        return camera;
    }//end of getCameraNode method

    /**
     * walkDirection method
     *
     * @return
     */
    public Vector3f walkDirection() {
        return walkDirection;
    }//end of getWalkDirection

    /**
     * walkDirection method
     *
     * @return
     */
    public Skeleton getSkeleton() {
        return this.animControl.getSkeleton();
    }//end of getWalkDirection

    /**
     * respawn method
     */
    public void respawn() {
        hp = 50;
        characterControl.warp(new Vector3f(300, 3f, 320f));
        game.getUI().dialogText().removeFromParent();
    }//end of respawn

    /**
     * playerAnimation
     *
     * @return animChannel
     */
    public AnimChannel getPlayerAnimation() {
        return legs;
    }//end of playerAnimation

    /**
     * attackAnimation
     *
     * @return animChannel
     */
    public AnimChannel getAttackAnimation() {
        return attackChannel;
    }//end of attackAnimation

    /**
     * hitBox method
     */
    public GhostControl hitBox() {
        return hitBox;
    }//end of hitBox method

    /**
     * getGold method
     *
     * @return gold
     */
    public int getGold() {
        return gold;
    }//end of getGold

    /**
     * setGold method
     *
     * @param amount
     */
    public void setGold(int amount) {
        this.gold = amount;
    }//end of setGold

    /**
     * incrementGold method
     *
     * @return gold
     */
    public void incrementGold(int amount) {
        gold += amount;
    }//end of incrementGold

    /**
     * decrementGold method
     *
     * @param amount
     */
    public void decrementGold(int amount) {
        this.gold -= amount;
    }//end of decrementGold

    /**
     * isAttacking method
     *
     * @return
     */
    public boolean isAttacking() {
        return attack;
    }//end of isAttacking

    /**
     * getSpellCastHitBox method
     *
     * @return the spellCastHitBox
     */
    public GhostControl getSpellCastHitBox() {
        return spellCastHitBox;
    }//end of getSpellHitBox method

    /**
     * setSpellCastHitBox method
     *
     * @param spellCastHitBox the spellCastHitBox to set
     */
    public void setSpellCastHitBox(GhostControl spellCastHitBox) {
        this.spellCastHitBox = spellCastHitBox;
    }//end of setSpellCastHitBox method

    /**
     * getSpellCastNode method
     *
     * @return the spellCastNode
     */
    public Node getSpellCastNode() {
        return spellCastNode;
    }//end of getSpellCastNode

    /**
     * setSpellCastNode method
     *
     * @param spellCastNode the spellCastNode to set
     */
    public void setSpellCastNode(Node spellCastNode) {
        this.spellCastNode = spellCastNode;
    }//end of setSpellCastNode method

    /**
     * getSpellEffectNode method
     *
     * @return the spellEffectNode
     */
    public Node getSpellEffectNode() {
        return spellEffectNode;
    }//end of getSpellEffectNode

    /**
     * getSpellCastPhysics method
     *
     * @return the spellCastPhysics
     */
    public RigidBodyControl getSpellCastPhysics() {
        return spellCastPhysics;
    }//end of getSpellCastPhysics

    /**
     * getSpellBloom method
     *
     * @return
     */
    public BloomFilter getSpellBloom() {
        return bloom;
    }//end of getSpellBloom

    /**
     * getCritChance method
     *
     * @return the critChance
     */
    public int getCritChance() {
        return critChance;
    }//end of getCritChance

    /**
     * setCristChance method
     *
     * @param critChance the critChance to set
     */
    public void setCritChance(int critChance) {
        this.critChance = critChance;
    }//end of setCritChance method


}//end of Player class
