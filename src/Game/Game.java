package game;

import Combat.Combat;
import Dialog.Dialog;
import Item.Item;
import Npc.Centaur;
import Npc.Elf;
import Npc.Ogre;
import Npc.Spider;
import Npc.Troll;
import Player.Player;
import UI.UI;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture2D;
import com.jme3.water.WaterFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Game class, handles physics,makes the terrain spawns npcs and the player.
 * Initializes everything.
 *
 * @author Jacob Amaral
 */
public class Game extends AbstractAppState {

    /**
     * Initializers.
     */
    private SimpleApplication app;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private AppSettings gameSettings;
    /**
     * Player.
     */
    private Spatial playerModel;
    private Player player;
    private UI ui;
    private Random random = new Random();
    /**
     * Npcs.
     */
    //Ogre
    private Node ogreModel;
    private Ogre ogre;
    private AnimControl ogreControl;
    private AnimChannel ogreAnimation;
    //Elf
    private Node elfModel;
    private Elf elf;
    private AnimControl elfControl;
    private AnimChannel elfAnimation;
    //Centaur
    private Node centaurModel;
    private Centaur centaur;
    private AnimControl centaurControl;
    private AnimChannel centaurAnimation;
    //Spider
    private Node spiderModel;
    private Spider spider;
    private AnimControl spiderControl;
    private AnimChannel spiderAnimation;
    //Troll
    private Node trollModel;
    private Troll troll;
    private AnimControl trollControl;
    private AnimChannel trollAnimation;
    /**
     * Physics.
     */
    private BulletAppState bulletAppState;
    private RigidBodyControl terrain;
    /**
     * Movement and Light vectors.
     */
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    private Vector3f sunDir = new Vector3f(-50f, -50f, -50f);
    private Vector3f sunDir2 = new Vector3f(1f, 1f, 1f);
    private Combat combat;
    /**
     * Ui.
     */
    private Node textNode, ogreTextNode, spiderTextNode, trollTextNode, elfTextNode,
            centaurTextNode, dmgTextNode, levelUpTextNode, goldTextNode, healthTextNode, takingDamageNode;
    private BillboardControl ogreText, spiderText, elfText, trollText, centaurText,
            dmgText, ogreLevel, trollLevel, elfLevel, spiderLevel, centaurLevel,
            levelUpText, goldText, healthText, takingDamageText;
    /**
     * Items
     */
    private Item item;
    private int amountOfGold;
    /**
     * Misc.
     *
     */
    private boolean debug = false;
    private float timer, movingTextY = 2f, movingTextZ = 0;
    private BillboardControl playerNameText;
    private Node playerNameNode;
    private DirectionalLightShadowRenderer dlsr;
    private Node sunModel;
    private BillboardControl expText;
    private WaterFilter water;
    private FilterPostProcessor fpp;
    private WaterFilter water2;
    private Spatial treeModel;
    private Material material;
    private Spatial treeModel2;
    private Material material2;
    private Spatial treeModel3;
    private Material material3;
    private Node tree;
    private Spatial[] trees;
    private Spatial[] trees2;
    private Spatial[] trees3;
    private int amountOfTrees;
    private RigidBodyControl treePhysics;
    private Spatial rockModel;
    private Node rockNode;
    private Spatial[] rocks;
    private int amountOfRocks;
    private RigidBodyControl[] rockPhysics;
    private String name;
    private AmbientLight al;
    private DirectionalLight sun;
    private boolean enableShadows, melee, mage, range;
    private float saverTimer;
    private float saveTimer;
    private boolean loadGame;
    private Node myPlayer;
    private File file;
    private BinaryExporter exporter;
    private File savePlayer;
    private PrintWriter fileWriter;

    /**
     * initialize method - starts everything.
     *
     * @param stateManager
     * @param app
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.assetManager = app.getAssetManager();

        this.app.setDisplayStatView(false);
        this.app.setDisplayFps(true);
        savePlayer = new File("assets/Saves/" + name + ".txt");

        /**
         * Options.
         */
        gameSettings = new AppSettings(true);
        gameSettings.setResolution(800, 600);
        app.setSettings(gameSettings);
        /**
         * Enable physics aka bulletAppState.
         */
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        //Terrain Model
        Spatial land = assetManager.loadModel("Scenes/terrain.j3o");
        land.setShadowMode(ShadowMode.Receive);
        /**
         * Terrain of the game.
         */
        CollisionShape sceneShape =
                CollisionShapeFactory.createMeshShape((Node) land);
        terrain = new RigidBodyControl(sceneShape, 0);
        land.addControl(terrain);
        this.bulletAppState.getPhysicsSpace().add(terrain);
        this.app.getRootNode().attachChild(land);
        /**
         * Player models and Physics.
         */
        if (!loadGame) {
            if (melee) {
                playerModel = assetManager.loadModel("Models/Player/Melee/Character_Plane.002.mesh.j3o");
                playerModel.setMaterial(assetManager.loadMaterial("Models/Player/Melee/playerMaterial.j3m"));
                playerModel.setLocalScale(25, 30, 25);
                playerModel.setShadowMode(ShadowMode.CastAndReceive);
            }
            if (mage) {
                playerModel = assetManager.loadModel("Models/Player/Mage/mage.j3o");
                playerModel.setMaterial(assetManager.loadMaterial("Models/Player/Mage/mageMaterial.j3m"));
                playerModel.setLocalScale(10, 10, 10);//for mage
                playerModel.rotate(0, 3.2f, 0);
                playerModel.setShadowMode(ShadowMode.CastAndReceive);
            }

            if (range) {
                playerModel = assetManager.loadModel("Models/Player/Range/range.j3o");
                playerModel.setMaterial(assetManager.loadMaterial("Models/Player/Range/rangeMaterial.j3m"));
                playerModel.setLocalScale(25, 30, 25);
                playerModel.setShadowMode(ShadowMode.CastAndReceive);
                playerModel.rotate(0, 3.2f, 0);
            }
        }



        if (loadGame) {
            try (Scanner scanner = new Scanner(savePlayer)) {
                String[] values = new String[9];
                String value = "";
                int i = 0;
                while (i < 9) {
                    scanner.nextLine();

                    value = scanner.next();
                    values[i] = value.substring(value.indexOf(':') + 1, value.length());

                    i++;
                }
                if (values[8].contains("melee")) {
                    melee = true;

                    playerModel = assetManager.loadModel("Saves/Character_Plane.002.mesh.j3o");
                    playerModel.setMaterial(assetManager.loadMaterial("Models/Player/Melee/playerMaterial.j3m"));
                    playerModel.setLocalScale(25, 30, 25);
                    playerModel.setShadowMode(ShadowMode.CastAndReceive);
                }
                if (values[8].contains("range")) {
                    range = true;
                    playerModel = assetManager.loadModel("Models/Player/Range/range.j3o");
                    playerModel.setMaterial(assetManager.loadMaterial("Models/Player/Range/rangeMaterial.j3m"));
                    playerModel.setLocalScale(25, 30, 25);
                    playerModel.setShadowMode(ShadowMode.CastAndReceive);
                    playerModel.rotate(0, 3.2f, 0);
                }
                if (values[8].contains("mage")) {
                    mage = true;
                    playerModel = assetManager.loadModel("Models/Player/Mage/mage.j3o");
                    playerModel.setMaterial(assetManager.loadMaterial("Models/Player/Mage/mageMaterial.j3m"));
                    playerModel.setLocalScale(10, 10, 10);//for mage
                    playerModel.setShadowMode(ShadowMode.CastAndReceive);
                    playerModel.rotate(0, 3.2f, 0);
                }
                player = new Player(playerModel, this.app.getInputManager(), this.app.getCamera(), this, assetManager, bulletAppState);
                player.getCharacterControl().warp(new Vector3f(300, 3f, 320f));//this is where the player spawns

                this.app.getRootNode().attachChild(player);
                this.bulletAppState.getPhysicsSpace().add(player.getCharacterControl());
                this.bulletAppState.getPhysicsSpace().add(player);
                this.bulletAppState.getPhysicsSpace().add(player.hitBox());

                player.setName(values[0]);
                player.setStr(Integer.parseInt(values[1]));
                player.setGold(Integer.parseInt(values[2]));
                player.setHealth(Integer.parseInt(values[3]));
                player.getCharacterControl().warp(new Vector3f(Float.parseFloat(values[4]),
                        Float.parseFloat(values[5]), Float.parseFloat(values[6])));
                player.setExp(Integer.parseInt(values[7]));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            player = new Player(playerModel, this.app.getInputManager(), this.app.getCamera(), this, assetManager, bulletAppState);
            player.setShadowMode(ShadowMode.Cast);
            player.getCharacterControl().warp(new Vector3f(300, 3f, 320f));//this is where the player spawns

            this.app.getRootNode().attachChild(player);
            this.bulletAppState.getPhysicsSpace().add(player.getCharacterControl());
            this.bulletAppState.getPhysicsSpace().add(player);
            this.bulletAppState.getPhysicsSpace().add(player.hitBox());
        }
        /**
         * Enable debug mode.
         */
        if (debug) {
            this.bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        }
        //end of Player variables
        /**
         * Ogre models,animations and Physics.
         */
        ogreModel = (Node) assetManager.loadModel("Models/Ogre/Ogre.j3o");
        ogreModel.setMaterial(assetManager.loadMaterial("Models/Ogre/Ogre.j3m"));
        ogreModel.setShadowMode(ShadowMode.Cast);
        ogre = new Ogre(ogreModel, this);
        ogre.getCharacterControl().warp(new Vector3f(-180f, 2f, -200f));

        ogreControl = ogreModel.getControl(AnimControl.class); // get control over this model

        ogreAnimation = ogreControl.createChannel();
        ogreControl.addListener(new AnimEventListener() {
            @Override
            public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
                if (ogre.getHp() <= 0) {
                    if (animName.equals("Attack1") || animName.equals("Attack2") || animName.equals("WalkCycle")) {
                        channel.setAnim("Death");
                    }
                }
                if (animName.equals("Death")) {
                    amountOfGold = random.nextInt(16);
                    item.dropRandomItem(assetManager, ogreModel.getWorldTranslation(), amountOfGold);
                    ogre.dead(true);
                    ogre.getCharacterControl().setEnabled(false);
                    ogre.removeFromParent();
                    ui.ogreLevel().removeFromParent();
                    ui.ogreText().removeFromParent();
                    player.incrementExp(40);

                }
                if (ogre.getHp() > 0) {
                    if (playerModel.getWorldTranslation().distance(ogreModel.getWorldTranslation()) > 12) {
                        if (animName.equals("Attack1") || animName.equals("Attack2")) {
                            channel.setAnim("WalkCycle");
                        }
                    }
                }
                if (ogre.getHp() > 0) {
                    if (playerModel.getWorldTranslation().distance(ogreModel.getWorldTranslation()) <= 12) {
                        channel.setAnim("Attack1");

                        if (animName.equalsIgnoreCase("Attack1")) {
                            channel.setAnim("Attack2");

                        }
                        if (animName.equalsIgnoreCase("Attack2")) {
                            channel.setAnim("Attack1");
                        }
                    }
                }
            }

            @Override
            public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
                if (ogre.getHp() <= 0) {
                    if (animName.equals("WalkCycle")) {
                        channel.setAnim("Death");
                    }
                }
            }
        }); // add listener
        this.bulletAppState.getPhysicsSpace().add(ogre);
        this.app.getRootNode().attachChild(ogre);
        this.bulletAppState.getPhysicsSpace().add(ogre.hitBox());
        //End of Ogre Variables
        /**
         * Elf models,animations and Physics.
         */
        elfModel = (Node) assetManager.loadModel("Models/Elf/Cube.mesh.j3o");
        elfModel.setMaterial(assetManager.loadMaterial("Models/Elf/Elf.j3m"));
        elfModel.setShadowMode(ShadowMode.Cast);

        elf = new Elf(elfModel, this);
        elf.getCharacterControl().warp(new Vector3f(50f, 2f, 320f));

        elfControl = elfModel.getControl(AnimControl.class); // get control over this model

        elfAnimation = elfControl.createChannel();
        elfControl.addListener(new AnimEventListener() {
            @Override
            public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
                if (elf.getHp() <= 0) {
                    channel.setAnim("Death");


                }
                if (animName.equals("Death")) {
                    amountOfGold = random.nextInt(8);
                    item.dropRandomItem(assetManager, elf.getWorldTranslation(), amountOfGold);
                    elf.dead(true);
                    elf.setLocalTranslation(99, 99, 99);
                    elf.removeFromParent();
                    elf.getCharacterControl().setEnabled(false);
                    elf.hitBox().setEnabled(false);
                    ui.elfLevel().removeFromParent();
                    ui.elfText().removeFromParent();
                    player.incrementExp(20);

                }
                if (elf.getHp() > 0) {
                    if (playerModel.getWorldTranslation().distance(elf.getWorldTranslation()) > 50) {
                        if (animName.equals("Attack1") || animName.equals("Attack2")) {
                            channel.setAnim("WalkCycle");

                        }
                    }
                }
                if (elf.getHp() > 0) {
                    if (playerModel.getWorldTranslation().distance(elf.getWorldTranslation()) <= 50) {

                        if (animName.equalsIgnoreCase("Attack1")) {
                            channel.setAnim("WalkCycle");

                        }

                    }
                }
            }

            @Override
            public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
                if (elf.getHp() <= 0) {
                    if (animName.equals("WalkCycle")) {
                        channel.setAnim("Death");
                    }
                }
            }
        }); // add listener
        this.app.getRootNode().attachChild(elf);
        this.bulletAppState.getPhysicsSpace().add(elf);
        this.bulletAppState.getPhysicsSpace().add(elf.hitBox());
        //End of Elf Variables
        /**
         * Centaur models,animations and Physics.
         */
        centaurModel = (Node) assetManager.loadModel("Models/Centaur/Cube.001.mesh_1.j3o");
        centaurModel.setMaterial(assetManager.loadMaterial("Models/Centaur/Centaur.j3m"));
        centaurModel.setShadowMode(ShadowMode.Cast);
        centaurModel.setLocalScale(2f, 2f, 2f);

        centaur = new Centaur(centaurModel, this);
        centaur.getCharacterControl().warp(new Vector3f(-170f, 2f, 100f));

        centaurControl = centaurModel.getControl(AnimControl.class); // get control over this model

        centaurAnimation = centaurControl.createChannel();
        centaurControl.addListener(new AnimEventListener() {
            @Override
            public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
                if (centaur.getHp() <= 0) {
                    if (animName.equals("Attack1") || animName.equals("Attack2") || animName.equals("Walk")) {
                        channel.setAnim("Death");
                    }
                }
                if (animName.equals("Death")) {
                    amountOfGold = random.nextInt(11);
                    item.dropRandomItem(assetManager, centaur.getWorldTranslation(), amountOfGold);
                    centaur.dead(true);
                    centaur.setLocalTranslation(99, 99, 99);
                    centaur.removeFromParent();
                    centaur.getCharacterControl().setEnabled(false);
                    ui.centaurLevel().removeFromParent();
                    ui.centaurText().removeFromParent();
                    player.incrementExp(30);
                    channel.setAnim("Walk");
                }

                if (centaur.getHp() > 0) {
                    if (playerModel.getWorldTranslation().distance(centaur.getWorldTranslation()) > 12) {
                        if (animName.equals("Attack1") || animName.equals("Attack2")) {
                            channel.setAnim("Walk");
                        }
                    }
                }
                if (centaur.getHp() > 0) {
                    if (playerModel.getWorldTranslation().distance(centaur.getWorldTranslation()) <= 12) {
                        channel.setAnim("Attack1");

                        if (animName.equalsIgnoreCase("Attack1")) {
                            channel.setAnim("Attack2");

                        }
                        if (animName.equalsIgnoreCase("Attack2")) {
                            channel.setAnim("Attack1");

                        }
                    }
                }
            }

            @Override
            public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
                if (centaur.getHp() <= 0) {
                    if (animName.equals("WalkCycle")) {
                        channel.setAnim("Death");
                    }
                }
            }
        }); // add listener
        this.app.getRootNode()
                .attachChild(centaur);
        this.bulletAppState.getPhysicsSpace()
                .add(centaur);
        this.bulletAppState.getPhysicsSpace()
                .add(centaur.hitBox());
        //End of Centaur Variables
        /**
         * Spider models,animations and Physics.
         */
        spiderModel = (Node) assetManager.loadModel("Models/Spider/Cube.mesh.j3o");
        spiderModel.setShadowMode(ShadowMode.Cast);
        spiderModel.setMaterial(assetManager.loadMaterial("Models/Spider/Spider.j3m"));

        spider = new Spider(spiderModel, this);

        spider.getCharacterControl()
                .warp(new Vector3f(40, 2f, -250));

        spiderControl = spiderModel.getControl(AnimControl.class); // get control over this model
        spiderAnimation = spiderControl.createChannel();

        spiderControl.addListener(
                new AnimEventListener() {
            @Override
            public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
                if (spider.getHp() <= 0) {
                    if (animName.equals("Attack1") || animName.equals("Attack2") || animName.equals("WalkCycle")) {
                        channel.setAnim("Death");
                    }
                }
                if (spider.getHp() <= 0) {
                    if (animName.equals("Death")) {
                        amountOfGold = random.nextInt(18);
                        item.dropRandomItem(assetManager, spider.getWorldTranslation(), amountOfGold);
                        spider.dead(true);
                        ui.spiderLevel().removeFromParent();
                        ui.spiderText().removeFromParent();
                        spider.removeFromParent();
                        spider.getCharacterControl().setEnabled(false);
                        player.incrementExp(55);
                        channel.setLoopMode(LoopMode.DontLoop);

                    }
                }
                if (spider.getHp() > 0) {
                    if (playerModel.getWorldTranslation().distance(spider.getWorldTranslation()) > 14) {
                        if (animName.equals("Attack1") || animName.equals("Attack2")) {
                            channel.setAnim("WalkCycle");
                        }
                    }
                }
                if (spider.getHp() > 0) {
                    if (playerModel.getWorldTranslation().distance(spider.getWorldTranslation()) <= 14) {
                        channel.setAnim("Attack1");

                        if (animName.equalsIgnoreCase("Attack1")) {
                            channel.setAnim("Attack2");

                        }
                        if (animName.equalsIgnoreCase("Attack2")) {
                            channel.setAnim("Attack1");

                        }
                    }
                }
            }

            @Override
            public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
                if (spider.getHp() <= 0) {
                    if (animName.equals("WalkCycle")) {
                        channel.setAnim("Death");
                    }
                }
            }
        }); // add listener
        this.app.getRootNode()
                .attachChild(spider);
        this.bulletAppState.getPhysicsSpace()
                .add(spider);
        this.bulletAppState.getPhysicsSpace()
                .add(spider.hitBox());
        //End of Spider Variables

        /**
         * Troll models,animations and Physics.
         */
        trollModel = (Node) assetManager.loadModel("Models/Troll/Cube.mesh.j3o");
        trollModel.setShadowMode(ShadowMode.Cast);
        trollModel.setMaterial(assetManager.loadMaterial("Models/Troll/Troll.j3m"));
        troll = new Troll(trollModel, this);

        troll.getCharacterControl()
                .warp(new Vector3f(30f, 2f, 15f));
        trollControl = trollModel.getControl(AnimControl.class); // get control over this model
        trollAnimation = trollControl.createChannel();

        trollControl.addListener(
                new AnimEventListener() {
            @Override
            public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
                if (troll.getHp() <= 0) {
                    if (animName.equals("Attack1") || animName.equals("Attack2") || animName.equals("Walk")) {
                        channel.setAnim("Death");
                    }
                }
                if (animName.equals("Death")) {

                    amountOfGold = random.nextInt(50);
                    item.dropRandomItem(assetManager, troll.getWorldTranslation(), amountOfGold);
                    troll.dead(true);
                    ui.trollLevel().removeFromParent();
                    ui.trollText().removeFromParent();
                    troll.removeFromParent();
                    troll.getCharacterControl().setEnabled(false);
                    player.incrementExp(80);
                }
                if (troll.getHp() > 0) {
                    if (playerModel.getWorldTranslation().distance(troll.getWorldTranslation()) > 6) {
                        if (animName.equals("Attack1") || animName.equals("Attack2")) {
                            channel.setAnim("Walk");
                        }
                    }
                }
                if (troll.getHp() > 0) {
                    if (playerModel.getWorldTranslation().distance(troll.getWorldTranslation()) <= 15) {
                        channel.setAnim("Attack1");

                        if (animName.equalsIgnoreCase("Attack1")) {
                            channel.setAnim("Attack2");

                        }
                        if (animName.equalsIgnoreCase("Attack2")) {
                            channel.setAnim("Attack1");

                        }
                    }
                }
            }

            @Override
            public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
                if (troll.getHp() <= 0) {
                    if (troll.getCharacterControl().getWalkDirection().length() > 0) {
                        if (animName.equals("Walk")) {
                            channel.setAnim("Idle");
                        }
                    }
                }
                if (troll.getHp() <= 0) {
                    if (animName.equals("WalkCycle")) {
                        channel.setAnim("Death");
                    }
                }
            }
        }); // add listener
        this.app.getRootNode()
                .attachChild(troll);
        this.bulletAppState.getPhysicsSpace()
                .add(troll);
        this.bulletAppState.getPhysicsSpace()
                .add(troll.hitBox());
        //End of Troll Variables
        /**
         * Lights and Shadows.
         */
        sunModel = (Node) assetManager.loadModel("Models/Sun/sunModel.j3o");

        sunModel.setMaterial(assetManager.loadMaterial("Models/Sun/sunMaterial.j3m"));
        sunModel.setLocalScale(
                40f, 40f, 40f);
        sunModel.setLocalTranslation(
                2500, 350, 2500);

        sun = new DirectionalLight();

        sun.setColor(ColorRGBA.White);

        sun.setDirection(sunDir);

        sunModel.addLight(sun);

        this.app.getRootNode()
                .attachChild(sunModel);
        this.app.getRootNode()
                .addLight(sun);
        if (enableShadows) {
            dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 3);

            dlsr.setLight(sun);

            dlsr.setLambda(0.75f);
            dlsr.setShadowIntensity(0.9f);
            dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);

            app.getViewPort()
                    .addProcessor(dlsr);
        }
        fpp = new FilterPostProcessor(assetManager);
        BloomFilter bf = new BloomFilter(BloomFilter.GlowMode.Objects);

        fpp.addFilter(bf);
        this.app.getViewPort().addProcessor(fpp);

        bf.setBloomIntensity(5f);
        bf.setExposureCutOff(.0f);
        bf.setExposurePower(5f);

        this.app.getCamera().setFrustumFar(4000);

        al = new AmbientLight();

        al.setColor(ColorRGBA.Gray);

        this.app.getRootNode()
                .addLight(al);
        /**
         * Water
         *
         */
        water = new WaterFilter(this.app.getRootNode(), sunModel.getWorldTranslation());
        water.setCenter(new Vector3f(-483.91f, -5f, 478.03848f));
        water.setRadius(220);
        water.setWaveScale(0.002f);
        water.setMaxAmplitude(2.5f);
        water.setFoamExistence(new Vector3f(1f, 4, 0.5f));
        water.setFoamTexture((Texture2D) assetManager.loadTexture("Common/MatDefs/Water/Textures/foam2.jpg"));
        water.setRefractionStrength(0.2f);
        water.setWaterHeight(-3);
        water.setFoamIntensity(0.8f);
        water.setShoreHardness(0.01f);

        fpp.addFilter(water);

        water2 = new WaterFilter(this.app.getRootNode(), sunModel.getWorldTranslation());
        water2.setCenter(new Vector3f(201, -2f, 60));
        water2.setRadius(180);
        water2.setWaveScale(0.002f);
        water2.setMaxAmplitude(2.5f);
        water2.setFoamExistence(new Vector3f(1f, 4, 0.5f));
        water2.setFoamTexture((Texture2D) assetManager.loadTexture("Common/MatDefs/Water/Textures/foam2.jpg"));
        water2.setRefractionStrength(0.2f);
        water2.setWaterHeight(-6f);
        water2.setFoamIntensity(0.9f);
        water2.setShoreHardness(0.01f);


        fpp.addFilter(water2);

        /**
         * Ui.
         */
        ui = new UI(this, this.app.getAssetManager());

        ui.emptyHpBar()
                .setLocalTranslation(gameSettings.getWidth() / 2 - 145, gameSettings.getHeight() / 2 - 200, 0);
        ui.hpBar()
                .setLocalTranslation(gameSettings.getWidth() / 2 - 145, gameSettings.getHeight() / 2 - 200, 1);
        ui.expBar()
                .setLocalTranslation(gameSettings.getWidth() / 2 - 145, gameSettings.getHeight() / 2 - 250, 1);

        ui.hpAmount()
                .setLocalTranslation(gameSettings.getWidth() / 2 - 10, gameSettings.getHeight() / 2 - 172, 1);
        ui.hp()
                .setLocalTranslation(gameSettings.getWidth() / 3, gameSettings.getHeight() / 2 - 172, 1);
        ui.hp()
                .setText("HP");

        ui.exp()
                .setLocalTranslation(gameSettings.getWidth() / 3, gameSettings.getHeight() / 2 - 230, 1);
        ui.exp()
                .setText("Exp");

        ui.dialogText()
                .setLocalTranslation(gameSettings.getWidth() / 50, gameSettings.getHeight() / 2 - 80, 1);

        this.app.getGuiNode()
                .attachChild(ui.hpBar());
        this.app.getGuiNode()
                .attachChild(ui.hpAmount());
        this.app.getGuiNode()
                .attachChild(ui.dialogText());
        this.app.getGuiNode()
                .attachChild(ui.hp());
        this.app.getGuiNode()
                .attachChild(ui.expBar());
        this.app.getGuiNode()
                .attachChild(ui.exp());
        this.app.getGuiNode()
                .attachChild(ui.emptyHpBar());

        playerNameText = new BillboardControl();

        playerNameText.setAlignment(BillboardControl.Alignment.Screen);
        goldText = new BillboardControl();

        goldText.setAlignment(BillboardControl.Alignment.Screen);
        takingDamageText = new BillboardControl();

        takingDamageText.setAlignment(BillboardControl.Alignment.Screen);
        healthText = new BillboardControl();

        healthText.setAlignment(BillboardControl.Alignment.Screen);
        levelUpText = new BillboardControl();

        levelUpText.setAlignment(BillboardControl.Alignment.Screen);
        ogreText = new BillboardControl();

        ogreText.setAlignment(BillboardControl.Alignment.Screen);
        spiderText = new BillboardControl();

        spiderText.setAlignment(BillboardControl.Alignment.Screen);
        elfText = new BillboardControl();

        elfText.setAlignment(BillboardControl.Alignment.Screen);
        trollText = new BillboardControl();

        trollText.setAlignment(BillboardControl.Alignment.Screen);
        centaurText = new BillboardControl();

        centaurText.setAlignment(BillboardControl.Alignment.Screen);
        dmgText = new BillboardControl();

        dmgText.setAlignment(BillboardControl.Alignment.Screen);
        ogreLevel = new BillboardControl();

        ogreLevel.setAlignment(BillboardControl.Alignment.Screen);
        trollLevel = new BillboardControl();

        trollLevel.setAlignment(BillboardControl.Alignment.Screen);
        elfLevel = new BillboardControl();

        elfLevel.setAlignment(BillboardControl.Alignment.Screen);
        spiderLevel = new BillboardControl();

        spiderLevel.setAlignment(BillboardControl.Alignment.Screen);
        centaurLevel = new BillboardControl();

        centaurLevel.setAlignment(BillboardControl.Alignment.Screen);
        expText = new BillboardControl();

        expText.setAlignment(BillboardControl.Alignment.Screen);

        ui.playerName()
                .addControl(playerNameText);
        player.attachChild(ui.playerName());
        ui.playerName()
                .setText(player.getName());
        ui.playerName().setShadowMode(ShadowMode.Off);

        ui.expText()
                .addControl(expText);
        ui.expText().setShadowMode(ShadowMode.Off);

        dmgTextNode = new Node("Node for damage text");

        dmgTextNode.setQueueBucket(Bucket.Transparent);

        dmgTextNode.addControl(dmgText);
        goldTextNode = new Node("Node for gold text");

        goldTextNode.setQueueBucket(Bucket.Transparent);

        goldTextNode.addControl(goldText);
        healthTextNode = new Node("Node for health text");

        healthTextNode.setQueueBucket(Bucket.Transparent);

        healthTextNode.addControl(healthText);
        takingDamageNode = new Node("Node for the player taking damage");

        takingDamageNode.setQueueBucket(Bucket.Transparent);

        takingDamageNode.addControl(takingDamageText);
        ogreTextNode = new Node("Node for text");

        ogreTextNode.setQueueBucket(Bucket.Transparent);

        ogreTextNode.attachChild(ui.ogreText());
        ogreTextNode.attachChild(ui.ogreLevel());
        ui.ogreText()
                .addControl(ogreText);
        ui.ogreLevel()
                .addControl(ogreLevel);

        this.app.getRootNode()
                .attachChild(ogreTextNode);

        spiderTextNode = new Node("Node for text");

        spiderTextNode.setQueueBucket(Bucket.Transparent);

        spiderTextNode.attachChild(ui.spiderText());
        spiderTextNode.attachChild(ui.spiderLevel());
        ui.spiderText()
                .addControl(spiderText);
        ui.spiderLevel()
                .addControl(spiderLevel);

        this.app.getRootNode()
                .attachChild(spiderTextNode);

        trollTextNode = new Node("Troll title above head");

        trollTextNode.setQueueBucket(Bucket.Transparent);

        trollTextNode.attachChild(ui.trollText());
        trollTextNode.attachChild(ui.trollLevel());
        ui.trollText()
                .addControl(trollText);
        ui.trollLevel()
                .addControl(trollLevel);


        this.app.getRootNode()
                .attachChild(trollTextNode);


        elfTextNode = new Node("Node for text");

        elfTextNode.setQueueBucket(Bucket.Transparent);

        elfTextNode.attachChild(ui.elfText());
        elfTextNode.attachChild(ui.elfLevel());

        ui.elfText()
                .addControl(elfText);
        ui.elfLevel()
                .addControl(elfLevel);

        this.app.getRootNode()
                .attachChild(elfTextNode);

        centaurTextNode = new Node("Node for text");

        centaurTextNode.setQueueBucket(Bucket.Transparent);

        centaurTextNode.attachChild(ui.centaurText());
        centaurTextNode.attachChild(ui.centaurLevel());
        ui.centaurLevel()
                .addControl(centaurLevel);
        ui.centaurText()
                .addControl(centaurText);

        this.app.getRootNode()
                .attachChild(centaurTextNode);



        ui.levelUp()
                .addControl(levelUpText);
        ui.levelUp().setShadowMode(ShadowMode.Off);
        //End of UI Variables

        //Instantiating objects.
        combat = new Combat(this);
        item = new Item(this);
        player.setName(name);
        player.getCharacterControl().setGravity(new Vector3f(0, -175f, 0));

        exporter = BinaryExporter.getInstance();

        if (range) {
            file = new File("assets/Saves/range.j3o");
        }
        if (melee) {
            file = new File("assets/Saves/Character_Plane.002.mesh.j3o");
        }
        if (mage) {
            file = new File("assets/Saves/mage.j3o");
        }
        try {
            exporter.save(playerModel, file);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Error: Failed to save game!", ex);
        }
    }//end of Initialize method

    /**
     * Update method - runs this code constantly in each frame.
     *
     * @param tpf
     */
    @Override
    public void update(float tpf) {
        /**
         * Save the players info
         */
        saveTimer = saveTimer + tpf;

        if (saveTimer > 10f) {
            try {
                fileWriter = new PrintWriter(savePlayer);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
            fileWriter.println("[CHARACTER]");
            fileWriter.println("Name:" + player.getName());
            fileWriter.println("Strength:" + player.getStr());
            fileWriter.println("Gold:" + player.getGold());
            fileWriter.println("Health:" + player.getHealth());
            fileWriter.println("LocationX:" + player.getWorldTranslation().getX());
            fileWriter.println("LocationY:" + player.getWorldTranslation().getY());
            fileWriter.println("LocationZ:" + player.getWorldTranslation().getZ());
            fileWriter.println("Exp:" + player.getExp());
            if (melee) {
                fileWriter.println("Class:" + "melee");
            }
            if (range) {
                fileWriter.println("Class:" + "range");
            }
            if (mage) {
                fileWriter.println("Class:" + "mage");
            }
            fileWriter.println("[END]");
            fileWriter.close();
            System.out.println("Saving");
            saveTimer = 0;
        }


        super.update(tpf);
        player.update(tpf);
        ogre.update(tpf);
        combat.update(tpf);
        elf.update(tpf);
        centaur.update(tpf);
        spider.update(tpf);
        troll.update(tpf);
        ui.update();
        item.update(tpf);
        timer = timer + tpf;

        /**
         * If someone dies, display a message.
         */
        if (ogre.getHp() <= 0) {
            Dialog.sendMessage(this, "You killed an ogre");
        }
        if (elf.getHp() <= 0) {
            Dialog.sendMessage(this, "You killed an elf");
        }
        if (troll.getHp() <= 0) {
            Dialog.sendMessage(this, "You killed a troll");
        }
        if (spider.getHp() <= 0) {
            Dialog.sendMessage(this, "You killed a spider");
        }
        if (centaur.getHp() <= 0) {
            Dialog.sendMessage(this, "You killed a centaur");
        }
        if (player.getHealth() <= 0) {
            Dialog.sendMessage(this, "You died!");
        }//end
        /**
         * If the player levels up, display a message.
         */
        if (player.getLevel() == 1 && player.getExp() >= 15) {
            timer = timer + tpf;
            /**
             * Scrolling Levelup text
             */
            if (timer > 0.01f) {
                player.attachChild(ui.levelUp());
                ui.levelUp().setLocalTranslation(new Vector3f(0, movingTextY += 0.03f, 0));
                ui.levelUp().setText("Level up!");
                ui.levelUp().setAlpha(1f);
                if (movingTextY >= 2.2f) {
                    ui.levelUp().setLocalTranslation(new Vector3f(0, movingTextY, movingTextZ += 0.02f));
                }
                if (movingTextY >= 4f
                        && movingTextY <= 4.49f) {
                    ui.levelUp().setAlpha(-200000);

                } else if (movingTextY >= 5.5f) {
                    ui.levelUp().setAlpha(-4000000);
                }
                if (movingTextY >= 5f) {
                    movingTextY = 2f;
                    movingTextZ = 0f;
                    ui.levelUp().setAlpha(1);
                    player.setLevel(2);
                    player.setTotalHealth(75);
                    player.setStr(25);
                    player.setExp(0);
                    player.detachChild(ui.levelUp());
                    timer = 0;
                }
            }
        }
        /**
         * Level 2.
         */
        if (player.getLevel() == 2 && player.getExp() >= 30) {
            player.attachChild(ui.levelUp());
            timer = timer + tpf;
            /**
             * Scrolling Levelup text
             */
            if (timer > 0.01f) {
                player.attachChild(ui.levelUp());
                ui.levelUp().setLocalTranslation(new Vector3f(0, movingTextY += 0.03f, 0));
                ui.levelUp().setText("Level up!");
                ui.levelUp().setAlpha(1f);
                if (movingTextY >= 2.2f) {
                    ui.levelUp().setLocalTranslation(new Vector3f(0, movingTextY, movingTextZ += 0.01f));
                }
                if (movingTextY >= 4f
                        && movingTextY <= 4.49f) {
                    ui.levelUp().setAlpha(-200000);

                } else if (movingTextY >= 5.5f) {
                    ui.levelUp().setAlpha(-4000000);
                }
                if (movingTextY >= 5f) {
                    movingTextY = 2f;
                    movingTextZ = 0f;
                    ui.levelUp().setAlpha(1);
                    player.setLevel(3);
                    player.setTotalHealth(105);
                    player.setStr(35);
                    player.setExp(0);
                    player.detachChild(ui.levelUp());
                    timer = 0;
                }
            }
        }
        /**
         * Level 3.
         */
        if (player.getLevel() == 3 && player.getExp() >= 50) {
            player.attachChild(ui.levelUp());
            timer = timer + tpf;
            /**
             * Scrolling Levelup text
             */
            if (timer > 0.01f) {
                player.attachChild(ui.levelUp());
                ui.levelUp().setLocalTranslation(new Vector3f(0, movingTextY += 0.03f, 0));
                ui.levelUp().setText("Level up!");
                ui.levelUp().setAlpha(1f);
                if (movingTextY >= 2.2f) {
                    ui.levelUp().setLocalTranslation(new Vector3f(0, movingTextY, movingTextZ += 0.01f));
                }
                if (movingTextY >= 4f
                        && movingTextY <= 4.49f) {
                    ui.levelUp().setAlpha(-200000);

                } else if (movingTextY >= 5.5f) {
                    ui.levelUp().setAlpha(-4000000);
                }
                if (movingTextY >= 5f) {
                    movingTextY = 2f;
                    movingTextZ = 0f;
                    ui.levelUp().setAlpha(1);
                    player.setLevel(4);
                    player.setTotalHealth(140);
                    player.setStr(4);
                    player.setExp(0);
                    player.detachChild(ui.levelUp());
                    timer = 0;
                }
            }
        }
        /**
         * Level 4.
         */
        if (player.getLevel() == 4 && player.getExp() >= 85) {
            player.attachChild(ui.levelUp());
            timer = timer + tpf;
            /**
             * Scrolling Levelup text
             */
            if (timer > 0.01f) {
                player.attachChild(ui.levelUp());
                ui.levelUp().setLocalTranslation(new Vector3f(0, movingTextY += 0.03f, 0));
                ui.levelUp().setText("Level up!");
                ui.levelUp().setAlpha(1f);
                if (movingTextY >= 2.2f) {
                    ui.levelUp().setLocalTranslation(new Vector3f(0, movingTextY, movingTextZ += 0.01f));
                }
                if (movingTextY >= 4f
                        && movingTextY <= 4.49f) {
                    ui.levelUp().setAlpha(-200000);

                } else if (movingTextY >= 5.5f) {
                    ui.levelUp().setAlpha(-4000000);
                }
                if (movingTextY >= 5f) {
                    movingTextY = 2f;
                    movingTextZ = 0f;
                    ui.levelUp().setAlpha(1);
                    player.setTotalHealth(180);
                    player.setLevel(5);
                    player.setStr(6);
                    player.setExp(0);
                    player.detachChild(ui.levelUp());
                    timer = 0;
                }
            }
        }
        /**
         * Level 5.
         */
        if (player.getLevel() == 5 && player.getExp() >= 100) {
            player.attachChild(ui.levelUp());

            timer = timer + tpf;
            /**
             * Scrolling Levelup text
             */
            if (timer > 0.01f) {
                player.attachChild(ui.levelUp());
                ui.levelUp().setLocalTranslation(new Vector3f(0, movingTextY += 0.03f, 0));
                ui.levelUp().setText("Level up!");
                ui.levelUp().setAlpha(1f);
                if (movingTextY >= 2.2f) {
                    ui.levelUp().setLocalTranslation(new Vector3f(0, movingTextY, movingTextZ += 0.01f));
                }
                if (movingTextY >= 4f
                        && movingTextY <= 4.49f) {
                    ui.levelUp().setAlpha(-200000);

                } else if (movingTextY >= 5.5f) {
                    ui.levelUp().setAlpha(-4000000);
                }
                if (movingTextY >= 5f) {
                    movingTextY = 2f;
                    movingTextZ = 0f;
                    ui.levelUp().setAlpha(1);
                    player.setLevel(6);
                    player.setTotalHealth(220);
                    player.setStr(8);
                    player.setExp(0);
                    player.detachChild(ui.levelUp());
                    timer = 0;
                }
            }
        }
        /**
         * Level 6.
         */
        if (player.getLevel() == 6 && player.getExp() >= 150) {
            player.attachChild(ui.levelUp());

            timer = timer + tpf;
            /**
             * Scrolling Levelup text
             */
            if (timer > 0.01f) {
                player.attachChild(ui.levelUp());
                ui.levelUp().setLocalTranslation(new Vector3f(0, movingTextY += 0.03f, 0));
                ui.levelUp().setText("Level up!");
                ui.levelUp().setAlpha(1f);
                if (movingTextY >= 2.2f) {
                    ui.levelUp().setLocalTranslation(new Vector3f(0, movingTextY, movingTextZ += 0.01f));
                }
                if (movingTextY >= 4f
                        && movingTextY <= 4.49f) {
                    ui.levelUp().setAlpha(-200000);

                } else if (movingTextY >= 5.5f) {
                    ui.levelUp().setAlpha(-4000000);
                }
                if (movingTextY >= 5f) {
                    movingTextY = 2f;
                    movingTextZ = 0f;
                    ui.levelUp().setAlpha(1);
                    player.setLevel(7);
                    player.setStr(9);
                    player.setTotalHealth(260);
                    player.setExp(0);
                    player.detachChild(ui.levelUp());
                    timer = 0;
                }
            }
        }
        /**
         * Level 7.
         */
        if (player.getLevel() == 7 && player.getExp() >= 200) {
            player.attachChild(ui.levelUp());

            timer = timer + tpf;
            /**
             * Scrolling Levelup text
             */
            if (timer > 0.01f) {
                player.attachChild(ui.levelUp());
                ui.levelUp().setLocalTranslation(new Vector3f(0, movingTextY += 0.03f, 0));
                ui.levelUp().setText("Level up!");
                ui.levelUp().setAlpha(1f);
                if (movingTextY >= 2.2f) {
                    ui.levelUp().setLocalTranslation(new Vector3f(0, movingTextY, movingTextZ += 0.01f));
                }
                if (movingTextY >= 4f
                        && movingTextY <= 4.49f) {
                    ui.levelUp().setAlpha(-200000);

                } else if (movingTextY >= 5.5f) {
                    ui.levelUp().setAlpha(-4000000);
                }
                if (movingTextY >= 5f) {
                    movingTextY = 2f;
                    movingTextZ = 0f;
                    ui.levelUp().setAlpha(1);
                    player.setLevel(8);
                    player.setStr(10);
                    player.setTotalHealth(300);
                    player.setExp(0);
                    player.detachChild(ui.levelUp());
                    timer = 0;
                }
            }
        }
        /**
         * Level 8.
         */
        if (player.getLevel() == 8 && player.getExp() >= 275) {
            player.attachChild(ui.levelUp());

            timer = timer + tpf;
            /**
             * Scrolling Levelup text
             */
            if (timer > 0.01f) {
                player.attachChild(ui.levelUp());
                ui.levelUp().setLocalTranslation(new Vector3f(0, movingTextY += 0.03f, 0));
                ui.levelUp().setText("Level up!");
                ui.levelUp().setAlpha(1f);
                if (movingTextY >= 2.2f) {
                    ui.levelUp().setLocalTranslation(new Vector3f(0, movingTextY, movingTextZ += 0.01f));
                }
                if (movingTextY >= 4f
                        && movingTextY <= 4.49f) {
                    ui.levelUp().setAlpha(-200000);

                } else if (movingTextY >= 5.5f) {
                    ui.levelUp().setAlpha(-4000000);
                }
                if (movingTextY >= 5f) {
                    movingTextY = 2f;
                    movingTextZ = 0f;
                    ui.levelUp().setAlpha(1);
                    player.setLevel(9);
                    player.setStr(12);
                    player.setTotalHealth(350);
                    player.setExp(0);
                    player.detachChild(ui.levelUp());
                    timer = 0;
                }
            }
        }
        /**
         * Level 9.
         */
        if (player.getLevel() == 9 && player.getExp() >= 350) {
            player.attachChild(ui.levelUp());

            timer = timer + tpf;
            /**
             * Scrolling Levelup text
             */
            if (timer > 0.01f) {
                player.attachChild(ui.levelUp());
                ui.levelUp().setLocalTranslation(new Vector3f(0, movingTextY += 0.03f, 0));
                ui.levelUp().setText("Level up!");
                ui.levelUp().setAlpha(1f);
                if (movingTextY >= 2.2f) {
                    ui.levelUp().setLocalTranslation(new Vector3f(0, movingTextY, movingTextZ += 0.01f));
                }
                if (movingTextY >= 4f
                        && movingTextY <= 4.49f) {
                    ui.levelUp().setAlpha(-200000);

                } else if (movingTextY >= 5.5f) {
                    ui.levelUp().setAlpha(-4000000);
                }
                if (movingTextY >= 5f) {
                    movingTextY = 2f;
                    movingTextZ = 0f;
                    ui.levelUp().setAlpha(1);
                    player.setLevel(10);
                    player.setStr(200);
                    player.setTotalHealth(400);
                    player.setExp(0);
                    player.detachChild(ui.levelUp());
                    timer = 0;
                }
            }
        }
        optimizeGame();
    }//end of update method

    /**
     * optimizeGame method.
     */
    private void optimizeGame() {

        /**
         * Dont display water if the player is far away.
         */
        if (player.getWorldTranslation().distance(water.getCenter()) > 650) {
            water.setEnabled(false);
        } else if (player.getWorldTranslation().distance(water.getCenter()) <= 650) {
            water.setEnabled(true);
        }
        if (player.getWorldTranslation().distance(water2.getCenter()) > 450) {
            water2.setEnabled(false);
        } else if (player.getWorldTranslation().distance(water2.getCenter()) <= 450) {
            water2.setEnabled(true);
        }

    }//end of optimizeGame method.

    /**
     * Cleanup method, everytime you click ESC key or the x-button.
     */
    @Override
    public void cleanup() {


        System.exit(0);
    }//end of cleanup method

    /**
     * getPlayer method
     *
     * @return
     */
    public Player getPlayer() {
        return player;
    }//end of getPlayer method

    /**
     * getPlayerModel method
     *
     * @return
     */
    public Spatial getPlayerModel() {
        return playerModel;
    }//end of getPlayerModel method

    /**
     * getOgre method
     *
     * @return
     */
    public Ogre getOgre() {
        return ogre;
    }//end of getOgre

    /**
     * getOgreModel method
     *
     * @return
     */
    public Spatial getOgreModel() {
        return ogreModel;
    }//end of getOgreModel method

    /**
     * getTroll method
     *
     * @return
     */
    public Troll getTroll() {
        return troll;
    }//end of getTroll

    /**
     * getOgreModel method
     *
     * @return
     */
    public Spatial getTrollModel() {
        return trollModel;
    }//end of getTrollModel method

    /**
     * getSpider method
     *
     * @return
     */
    public Spider getSpider() {
        return spider;
    }//end of getSpider

    /**
     * geSpiderModel method
     *
     * @return
     */
    public Spatial getSpiderModel() {
        return spiderModel;
    }//end of getSpiderModel method

    /**
     * getElf method
     *
     * @return
     */
    public Elf getElf() {
        return elf;
    }//end of getElf

    /**
     * getElfModel method
     *
     * @return
     */
    public Spatial getElfModel() {
        return elfModel;
    }//end of getElfModel method

    /**
     * getCentaur method
     *
     * @return
     */
    public Centaur getCentaur() {
        return centaur;
    }//end of getOgre

    /**
     * getCentaurModel method
     *
     * @return
     */
    public Spatial getCentaurModel() {
        return centaurModel;
    }//end of getOgreModel method

    /**
     * getUi method
     */
    public UI getUI() {
        return ui;
    }//end of getUI method

    /**
     * gameSettings
     *
     * @return
     */
    public AppSettings gameSettings() {
        return gameSettings;
    }//end of gameSettings

    /**
     * ogreWalk - method
     */
    public AnimChannel ogreAnimation() {
        return ogreAnimation;
    }//end of walk

    /**
     * elfWalk - method
     */
    public AnimChannel elfAnimation() {
        return elfAnimation;
    }//end of walk

    /**
     * centaurWalk - method
     */
    public AnimChannel centaurAnimation() {
        return centaurAnimation;
    }//end of walk

    /**
     * spiderWalk - method
     */
    public AnimChannel spiderAnimation() {
        return spiderAnimation;
    }//end of walk

    /**
     * trollWalk - method
     */
    public AnimChannel trollAnimation() {
        return trollAnimation;
    }//end of walk

    /**
     * getApp method
     */
    public SimpleApplication getApp() {
        return this.app;
    }//end of getApp

    /**
     * getBulletAppState-method
     *
     * @return
     */
    public BulletAppState getBulletAppState() {
        return this.bulletAppState;
    }//end of getBulletAppState method

    /**
     * getCombat-method
     */
    public Combat getCombat() {
        return combat;
    }//end of getCombat

    /**
     * getItem method
     *
     * @return
     */
    public Item getItem() {
        return item;
    }//end of getItem

    /**
     * dmgTextNode
     *
     * @return
     */
    public Node dmgTextNode() {
        return dmgTextNode;
    }//end of dmgTextNode

    /**
     * goldTextNode
     *
     * @return
     */
    public Node goldTextNode() {
        return goldTextNode;
    }//end of goldTextNode

    /**
     * healthTextNode
     *
     * @return
     */
    public Node healthTextNode() {
        return healthTextNode;
    }//end of healthTextNode

    /**
     * takingDamageNode
     *
     * @return
     */
    public Node takingDamageNode() {
        return takingDamageNode;
    }//end of takingDamageNode

    /**
     * getAssetManager method.
     */
    public AssetManager getAssetManager() {
        return assetManager;
    }//end of getAssetManager method

    /**
     * getWater method
     *
     * @return
     */
    public WaterFilter getWater() {
        return water;
    }//end of getWater method

    public FilterPostProcessor getFpp() {
        return fpp;
    }

    /**
     * setName method
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }//end of setName method

    /**
     * enableShadows method.
     *
     * @return
     */
    public void enableShadows(boolean answer) {
        enableShadows = answer;
    }//end of enableShadows method.

    /**
     * isMelee
     */
    public boolean isMelee() {
        return melee;
    }

    /**
     * isRange
     */
    public boolean isRange() {
        return range;
    }

    /**
     * isMage
     */
    public boolean isMage() {
        return mage;
    }

    /**
     * @param melee the melee to set
     */
    public void setMelee(boolean melee) {
        this.melee = melee;
    }

    /**
     * @param mage the mage to set
     */
    public void setMage(boolean mage) {
        this.mage = mage;
    }

    /**
     * @param range the range to set
     */
    public void setRange(boolean range) {
        this.range = range;
    }

    /**
     * @return the loadGame
     */
    public boolean isLoadGame() {
        return loadGame;
    }

    /**
     * @param loadGame the loadGame to set
     */
    public void setLoadGame(boolean loadGame) {
        this.loadGame = loadGame;
    }
}//end of Game class