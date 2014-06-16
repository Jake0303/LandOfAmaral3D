package Combat;

import com.jme3.animation.LoopMode;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import game.Game;
import java.util.Random;

/**
 * Combat Class
 *
 * @author Jacob Amaral
 */
public class Combat {

    private Game game;
    private Random random;
    private float timer, attackTimer;
    private float movingTextY = 2f, movingTextZ;
    private float decreasingTextY = 6f;
    private boolean underAttack, attackingOgre, attackingElf, attackingSpider, attackingTroll, attackingCentaur;
    private int playerDamage, enemyDamage;
    private boolean crit;
    private float expTextY = 2f, expTextZ = 0;
    private float removeTimer;
    private boolean removeText;
    private ParticleEmitter elfSpell;
    private Node spellNode;
    private GhostControl elfSpellHitBox;
    private RigidBodyControl elfSpellPhysics;
    private Geometry sphere;
    private boolean castSpell;
    private boolean spellEffect;
    private CollisionResults results;
    private Ray ray;

    /**
     * Combat Constructor
     *
     * @param game
     */
    public Combat(Game game) {
        this.game = game;
        init();
    }//end of Combat Constructor

    /**
     * init method.
     */
    private void init() {


        random = new Random();
        playerDamage = random.nextInt(game.getPlayer().getStr()) + game.getPlayer().getLevel() * random.nextInt(6);
        enemyDamage = random.nextInt(2) + 1;
    }//end of init

    /**
     * update method.
     */
    public void update(float tpf) {
        handleAggro();
        handleAttack(tpf);
        handleDeaths(tpf);
    }//end of update

    /**
     * handleAggro method-If you are in a certain distance, the npc will start
     * following you.
     */
    private void handleAggro() {
        if (game.getPlayerModel().getWorldTranslation().distance(game.getOgreModel().getWorldTranslation()) < 50
                || attackingOgre) {
            game.getOgre().inCombat(true);
        } else if (!attackingOgre) {
            game.getOgre().notInCombat(true);
        }
        if (game.getPlayerModel().getWorldTranslation().distance(game.getElf().getWorldTranslation()) < 50
                || attackingElf) {
            game.getElf().inCombat(true);
        } else if (!attackingElf) {
            game.getElf().notInCombat(true);
        }
        if (game.getPlayerModel().getWorldTranslation().distance(game.getCentaur().getWorldTranslation()) < 50
                || attackingCentaur) {
            game.getCentaur().inCombat(true);
        } else if (!attackingCentaur) {
            game.getCentaur().notInCombat(true);
        }
        if (game.getPlayerModel().getWorldTranslation().distance(game.getTroll().getWorldTranslation()) < 65
                || attackingTroll) {
            game.getTroll().inCombat(true);
        } else if (!attackingTroll) {
            game.getTroll().notInCombat(true);
        }
        if (game.getPlayerModel().getWorldTranslation().distance(game.getSpider().getWorldTranslation()) < 55
                || attackingSpider) {
            game.getSpider().inCombat(true);
        } else if (!attackingSpider) {
            game.getSpider().notInCombat(true);
        }
    }//end of handleAggro

    /**
     * handleAttack method.
     *
     * @param tpf - tpf stands for time per frame, calculates the time between
     * each frame, very useful for timers.
     */
    private void handleAttack(float tpf) {
        /**
         * Combat dealing with Ogre.
         */
        /**
         * Player dealing damage to the ogre.
         */
        if (game.isMelee()) {
            if (game.getOgre().hitBox().getOverlappingObjects().contains(game.getPlayer().hitBox())
                    && game.getPlayerModel().getWorldTranslation().distance(game.getOgreModel().getWorldTranslation()) < 4
                    && game.getPlayer().getAttackAnimation().getAnimationName().equals("Attack1")
                    || game.getOgre().hitBox().getOverlappingObjects().contains(game.getPlayer().hitBox())
                    && game.getPlayerModel().getWorldTranslation().distance(game.getOgreModel().getWorldTranslation()) < 4
                    && game.getPlayer().getAttackAnimation().getAnimationName().equals("Attack2")) {
                timer = timer + tpf;
                attackingOgre = true;
                if (timer > 1f) {
                    if (random.nextInt(100 - game.getPlayer().getCritChance()) == 1) {
                        game.getOgre().decrementHealth(playerDamage * 2);
                        crit = true;
                        timer = 0;
                    } else {
                        game.getOgre().decrementHealth(playerDamage);
                        crit = false;
                        timer = 0;
                    }
                }
            }
        }
        if (game.isRange()) {
            if (game.getOgre().hitBox().getOverlappingObjects().contains(game.getItem().arrowHitbox())) {
                game.getOgre().inCombat(true);
                timer = timer + tpf;
                game.getItem().removeArrow();
                attackingOgre = true;
                if (timer > 1f) {
                    game.getOgre().decrementHealth(playerDamage);
                    timer = 0;
                }
            }
        }
        if (game.isMage()) {
            if (game.getOgre().hitBox().getOverlappingObjects().contains(game.getPlayer().getSpellCastHitBox())) {
                timer = timer + tpf;

                game.getApp().getRootNode().detachChild(game.getPlayer().getSpellCastNode());
                game.getApp().getRootNode().detachChild(game.getPlayer().getSpellEffectNode());
                game.getPlayer().getSpellCastHitBox().setEnabled(false);
                game.getPlayer().getSpellCastPhysics().setEnabled(false);
                game.getPlayer().getSpellBloom().setEnabled(false);
                attackingOgre = true;
                game.getOgre().inCombat(true);
                if (timer > 1f) {
                    game.getOgre().decrementHealth(playerDamage);
                    timer = 0;
                }
            }
        }
        /**
         * Scrolling Damage text for the Ogre.
         */
        if (attackingOgre) {
            timer = timer + tpf;
            if (timer > 0.0001f) {
                game.dmgTextNode().attachChild(game.getUI().dmgAmount());
                game.getApp().getRootNode().attachChild(game.dmgTextNode());
                game.dmgTextNode().setLocalTranslation(game.getOgreModel().getWorldTranslation()
                        .add(0, movingTextY += 0.06f, 0));
                if (crit) {
                    game.getUI().dmgAmount().setText("Critical!");
                } else {
                    game.getUI().dmgAmount().setText(String.valueOf(playerDamage));
                }
                game.getUI().dmgAmount().setAlpha(1);
                if (movingTextY >= 2.1f) {
                    game.dmgTextNode().setLocalTranslation(game.getOgreModel().getWorldTranslation()
                            .add(0, movingTextY, movingTextZ += 0.01f));
                }
                if (movingTextY >= 4f && movingTextY <= 4.49f) {
                    game.getUI().dmgAmount().setAlpha(-200000);

                } else if (movingTextY >= 4.5f) {
                    game.getUI().dmgAmount().setAlpha(-4000000);
                } else if (movingTextY >= 5f) {
                    game.getUI().dmgAmount().setAlpha(-400000000);

                }
                if (movingTextY >= 7f) {
                    playerDamage = random.nextInt(game.getPlayer().getStr()) + game.getPlayer().getLevel() * random.nextInt(3);
                    crit = false;
                    attackingOgre = false;
                    movingTextY = 2f;
                    movingTextZ = 0f;
                    timer = 0;
                }

            }
        }
        /**
         * This snippet of code deals with the Ogre attacking the Player.
         */
        if (game.getOgre().getHp() > 0
                && game.getPlayerModel().getWorldTranslation()
                .distance(game.getOgreModel().getWorldTranslation()) <= 10) {
            attackTimer = attackTimer + tpf;
            if (attackTimer > 1f) {
                if (game.ogreAnimation().getAnimationName().equalsIgnoreCase("Attack1")
                        || game.ogreAnimation().getAnimationName().equalsIgnoreCase("Attack2")) {

                    enemyDamage = random.nextInt(game.getOgre().getDamage());
                    game.getPlayer().decrementHealth(enemyDamage);
                    underAttack = true;
                    attackTimer = 0;
                }
            }
        }//end of Combat with Ogre
        /**
         * This displays text when the player is taking damage from the Ogre.
         */
        if (underAttack && game.getPlayerModel().getWorldTranslation().distance(game.getOgreModel().getWorldTranslation()) <= 50) {
            timer = timer + tpf;
            if (timer > 0.0001f) {
                game.takingDamageNode().attachChild(game.getUI().takingDamageText());
                game.getApp().getRootNode().attachChild(game.takingDamageNode());
                game.takingDamageNode().setLocalTranslation(game.getPlayer().getWorldTranslation()
                        .add(0, decreasingTextY -= 0.06f, 0));
                game.getUI().takingDamageText().setText("-" + String.valueOf(enemyDamage));
                game.getUI().takingDamageText().setAlpha(1);
                if (decreasingTextY <= 5.9f) {
                    game.takingDamageNode().setLocalTranslation(game.getPlayer().getWorldTranslation()
                            .add(0, decreasingTextY, movingTextZ += 0.01f));
                }
                if (decreasingTextY <= 4f && decreasingTextY >= 3.5f) {
                    game.getUI().takingDamageText().setAlpha(-200000);

                } else if (decreasingTextY <= 3.49f) {
                    game.getUI().takingDamageText().setAlpha(-4000000);
                } else if (decreasingTextY <= 3f) {
                    game.getUI().takingDamageText().setAlpha(-400000000);
                }
                if (decreasingTextY <= 2.5f) {
                    enemyDamage = random.nextInt(game.getOgre().getDamage()) + 1;
                    underAttack = false;
                    decreasingTextY = 6f;
                    movingTextZ = 0f;
                    timer = 0;
                }
            }
        }//end of combat logic dealing with the Ogre.
        /**
         * Combat dealing with Elf.
         */
        /**
         * Player dealing damage to the Elf.
         */
        if (game.isMelee()) {
            if (game.getElf().hitBox().getOverlappingObjects().contains(game.getPlayer().hitBox())
                    && game.getPlayerModel().getWorldTranslation().distance(game.getElf().getWorldTranslation()) < 4
                    && game.getPlayer().getAttackAnimation().getAnimationName().equals("Attack1")
                    || game.getElf().hitBox().getOverlappingObjects().contains(game.getPlayer().hitBox())
                    && game.getPlayerModel().getWorldTranslation().distance(game.getElf().getWorldTranslation()) < 4
                    && game.getPlayer().getAttackAnimation().getAnimationName().equals("Attack2")) {
                timer = timer + tpf;
                attackingElf = true;
                if (timer > 1f) {
                    if (random.nextInt(100 - game.getPlayer().getCritChance()) == 1) {
                        game.getElf().decrementHealth(playerDamage * 2);
                        crit = true;
                        timer = 0;
                    } else {
                        game.getElf().decrementHealth(playerDamage);
                        crit = false;
                        timer = 0;
                    }
                }
            }
        }
        if (game.isRange()) {
            if (game.getElf().hitBox().getOverlappingObjects().contains(game.getItem().arrowHitbox())) {
                timer = timer + tpf;
                attackingElf = true;
                game.getItem().removeArrow();
                if (timer > 1f) {
                    game.getElf().decrementHealth(playerDamage);
                    timer = 0;
                }
            }
        }

        if (game.isMage()) {
            if (game.getElf().hitBox().getOverlappingObjects().contains(game.getPlayer().getSpellCastHitBox())) {
                timer = timer + tpf;

                game.getApp().getRootNode().detachChild(game.getPlayer().getSpellCastNode());
                game.getApp().getRootNode().detachChild(game.getPlayer().getSpellEffectNode());
                game.getPlayer().getSpellCastHitBox().setEnabled(false);
                game.getPlayer().getSpellCastPhysics().setEnabled(false);
                game.getPlayer().getSpellBloom().setEnabled(false);
                attackingElf = true;
                if (timer > 1f) {
                    game.getElf().decrementHealth(playerDamage);
                    timer = 0;
                }
            }
        }

        /**
         * Scrolling Damage text for the Elf.
         */
        if (attackingElf) {
            timer = timer + tpf;
            if (timer > 0.0001f) {
                game.dmgTextNode().attachChild(game.getUI().dmgAmount());
                game.getApp().getRootNode().attachChild(game.dmgTextNode());
                game.dmgTextNode().setLocalTranslation(game.getElf().getWorldTranslation()
                        .add(0, movingTextY += 0.06f, 0));
                if (crit) {
                    game.getUI().dmgAmount().setText("Critical!");
                } else {
                    game.getUI().dmgAmount().setText(String.valueOf(playerDamage));
                }
                game.getUI().dmgAmount().setAlpha(1);
                if (movingTextY >= 2.1f) {
                    game.dmgTextNode().setLocalTranslation(game.getElf().getWorldTranslation()
                            .add(0, movingTextY, movingTextZ += 0.01f));
                }
                if (movingTextY >= 4f && movingTextY <= 4.49f) {
                    game.getUI().dmgAmount().setAlpha(-200000);

                } else if (movingTextY >= 4.5f) {
                    game.getUI().dmgAmount().setAlpha(-4000000);
                } else if (movingTextY >= 5f) {
                    game.getUI().dmgAmount().setAlpha(-400000000);
                }
                if (movingTextY >= 7f) {
                    playerDamage = random.nextInt(game.getPlayer().getStr()) + game.getPlayer().getLevel() * random.nextInt(3);
                    attackingElf = false;
                    movingTextY = 2f;
                    movingTextZ = 0f;
                    timer = 0;
                }
            }
        }
        /**
         * This snippet of code deals with the Elf attacking the Player.
         */
        if (game.getElf().getHp() > 0
                && game.getPlayerModel().getWorldTranslation()
                .distance(game.getElf().getWorldTranslation()) <= 51) {

            attackTimer = attackTimer + tpf;
            if (attackTimer > 3f) {
                results = new CollisionResults();
                //Aim a ray from the elfs location and direction
                ray = new Ray(game.getElf().getWorldTranslation(), game.getElf().getCharacterControl().getViewDirection());
                //Collect intersections between Ray and anything in the scene
                game.getApp().getRootNode().collideWith(ray, results);
                game.elfAnimation().setAnim("Attack1");
                game.elfAnimation().setLoopMode(LoopMode.DontLoop);
                castSpell = true;
                attackTimer = 0;
            }
            //if (castSpell) {
            /**
             * Spell effect that elves cast on the player
             */
            if (castSpell) {
                if (game.elfAnimation().getTime() > 0.3f) {
                    spellNode = new Node();
                    elfSpellHitBox = new GhostControl(new SphereCollisionShape(3f));
                    elfSpellPhysics = new RigidBodyControl(new SphereCollisionShape(3f));

                    elfSpell = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
                    Material spellMaterial =
                            new Material(game.getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
                    spellMaterial.setTexture("Texture", game.getAssetManager().loadTexture(
                            "Effects/shockwave.png"));
                    elfSpell.setMaterial(spellMaterial);
                    elfSpell.setNumParticles(100);
                    elfSpell.setParticlesPerSec(100);
                    elfSpell.setLowLife(6f);
                    elfSpell.setHighLife(12f);
                    elfSpell.setVelocityVariation(0.7f);
                    elfSpell.setStartColor(ColorRGBA.Cyan);
                    elfSpell.setImagesX(2);
                    elfSpell.setImagesY(2);
                    elfSpell.setGravity(0, 0, 0);
                    elfSpell.setInWorldSpace(false);

                    game.getElf().attachChild(spellNode);
                    spellNode.setLocalTranslation(new Vector3f(0, 6, 4));

                    spellNode.addControl(elfSpellPhysics);

                    spellNode.addControl(elfSpellHitBox);

                    spellNode.attachChild(elfSpell);
                    spellNode.setLocalTranslation(game.getElf().getWorldTranslation().add(new Vector3f(0, 0, 2).mult(1.8f).addLocal(new Vector3f(0, 0, 2).mult(0.9f))));
                    game.getBulletAppState().getPhysicsSpace().add(elfSpellPhysics);
                    game.getBulletAppState().getPhysicsSpace().add(elfSpellHitBox);
                    game.getApp().getRootNode().attachChild(spellNode);
                    elfSpellHitBox.setEnabled(true);
                    elfSpellPhysics.setEnabled(true);
                    elfSpellPhysics.setLinearVelocity(game.getPlayer().getWorldTranslation()
                            .subtract(game.getElf().getWorldTranslation()));
                    castSpell = false;
                    spellEffect = true;
                }
            }
        }//end of Combat with Elf
        /**
         * Remove the spell effects from the elf.
         */
        if (spellEffect) {
            if (elfSpellPhysics.getLinearVelocity().length() > 0) {
                spellNode.setLocalTranslation(elfSpellHitBox.getPhysicsLocation());
            }
            if (game.getPlayer().getSpellCastHitBox() != null) {
                if (game.getPlayer().getSpellCastHitBox().getOverlappingObjects().contains(elfSpellHitBox)) {
                    elfSpellHitBox.setEnabled(false);
                    elfSpellPhysics.setEnabled(false);
                    spellNode.removeFromParent();
                    game.getApp().getRootNode().detachChild(spellNode);
                    elfSpell.killAllParticles();
                    spellEffect = false;
                }
            }
            if (game.getItem().arrowHitbox() != null) {
                if (game.getItem().arrowHitbox().getOverlappingObjects().contains(elfSpellHitBox)) {
                    elfSpellHitBox.setEnabled(false);
                    elfSpellPhysics.setEnabled(false);
                    spellNode.removeFromParent();
                    game.getApp().getRootNode().detachChild(spellNode);
                    elfSpell.killAllParticles();
                    spellEffect = false;
                }
            }
            if (game.getPlayer().hitBox().getOverlappingObjects().contains(elfSpellHitBox)) {
                enemyDamage = random.nextInt(game.getElf().getDmg());
                game.getPlayer().decrementHealth(enemyDamage);
                underAttack = true;
                elfSpellHitBox.setEnabled(false);
                elfSpellPhysics.setEnabled(false);
                spellNode.removeFromParent();
                game.getApp().getRootNode().detachChild(spellNode);
                elfSpell.killAllParticles();
                spellEffect = false;
            }

            if (elfSpellPhysics.getPhysicsLocation().getY() <= 3) {
                elfSpellHitBox.setEnabled(false);
                elfSpellPhysics.setEnabled(false);
                spellNode.removeFromParent();
                elfSpell.killAllParticles();
                game.getApp().getRootNode().detachChild(spellNode);
                castSpell = false;
                spellEffect = false;
            }
            if (results.size() > 0) {
                timer = timer + tpf;
                if (timer > 1f) {
                    elfSpellHitBox.setEnabled(false);
                    elfSpellPhysics.setEnabled(false);
                    spellNode.removeFromParent();
                    elfSpell.killAllParticles();
                    game.getApp().getRootNode().detachChild(spellNode);
                    castSpell = false;
                    spellEffect = false;
                    timer = 0;
                }
            }
        }
        /**
         * This displays text when the player is taking damage from the Elf.
         */
        if (underAttack && game.getPlayerModel()
                .getWorldTranslation().distance(game.getElf().getWorldTranslation()) <= 60) {
            timer = timer + tpf;
            if (timer > 0.0001f) {
                game.takingDamageNode().attachChild(game.getUI().takingDamageText());
                game.getApp().getRootNode().attachChild(game.takingDamageNode());
                game.takingDamageNode().setLocalTranslation(game.getPlayer().getWorldTranslation()
                        .add(0, decreasingTextY -= 0.06f, 0));
                game.getUI().takingDamageText().setText("-" + String.valueOf(enemyDamage));
                game.getUI().takingDamageText().setAlpha(1);
                if (decreasingTextY <= 5.9f) {
                    game.takingDamageNode().setLocalTranslation(game.getPlayer().getWorldTranslation()
                            .add(0, decreasingTextY, movingTextZ += 0.01f));
                }
                if (decreasingTextY <= 4f && decreasingTextY >= 3.5f) {
                    game.getUI().takingDamageText().setAlpha(-200000);

                } else if (decreasingTextY <= 3.49f) {
                    game.getUI().takingDamageText().setAlpha(-4000000);
                } else if (decreasingTextY <= 3f) {
                    game.getUI().takingDamageText().setAlpha(-400000000);
                }
                if (decreasingTextY <= 2.5f) {
                    enemyDamage = random.nextInt(game.getOgre().getDamage()) + 1;
                    underAttack = false;
                    decreasingTextY = 6f;
                    movingTextZ = 0f;
                    timer = 0;
                }
            }
        }//end of combat logic dealing with the Elf.

        /**
         * Combat with Centaur.
         */
        /**
         * Centaur dealing damage to the player.
         */
        if (game.isMelee()) {
            if (game.getCentaur().hitBox().getOverlappingObjects().contains(game.getPlayer().hitBox())
                    && game.getPlayerModel().getWorldTranslation().distance(game.getCentaur().getWorldTranslation())
                    < 4
                    && game.getPlayer().getAttackAnimation().getAnimationName().equals("Attack1")
                    || game.getCentaur().hitBox().getOverlappingObjects().contains(game.getPlayer().hitBox())
                    && game.getPlayerModel().getWorldTranslation().distance(game.getCentaur().getWorldTranslation())
                    < 4
                    && game.getPlayer().getAttackAnimation().getAnimationName().equals("Attack2")) {
                timer = timer + tpf;
                attackingCentaur = true;
                if (timer > 1f) {
                    if (random.nextInt(100 - game.getPlayer().getCritChance()) == 1) {
                        game.getCentaur().decrementHealth(playerDamage * 2);
                        crit = true;
                        timer = 0;
                    } else {
                        game.getCentaur().decrementHealth(playerDamage);
                        crit = false;
                        timer = 0;
                    }
                }
            }
        }

        if (game.isRange()) {
            if (game.getCentaur().hitBox().getOverlappingObjects().contains(game.getItem().arrowHitbox())) {
                timer = timer + tpf;
                game.getItem().removeArrow();
                attackingCentaur = true;
                if (timer > 1f) {
                    game.getCentaur().decrementHealth(playerDamage);
                    timer = 0;
                }
            }
        }

        if (game.isMage()) {
            if (game.getCentaur().hitBox().getOverlappingObjects().contains(game.getPlayer().getSpellCastHitBox())) {
                timer = timer + tpf;

                game.getApp().getRootNode().detachChild(game.getPlayer().getSpellCastNode());
                game.getApp().getRootNode().detachChild(game.getPlayer().getSpellEffectNode());
                game.getPlayer().getSpellCastHitBox().setEnabled(false);
                game.getPlayer().getSpellCastPhysics().setEnabled(false);
                game.getPlayer().getSpellBloom().setEnabled(false);
                attackingCentaur = true;
                if (timer > 1f) {
                    game.getCentaur().decrementHealth(playerDamage);
                    timer = 0;
                }
            }
        }
        /**
         * Scrolling Damage text for the Centaur.
         */
        if (attackingCentaur) {
            timer = timer + tpf;
            if (timer > 0.0001f) {
                game.dmgTextNode().attachChild(game.getUI().dmgAmount());
                game.getApp().getRootNode().attachChild(game.dmgTextNode());
                game.dmgTextNode().setLocalTranslation(game.getCentaur().getWorldTranslation()
                        .add(0, movingTextY += 0.06f, 0));
                if (crit) {
                    game.getUI().dmgAmount().setText("Critical!");
                } else {
                    game.getUI().dmgAmount().setText(String.valueOf(playerDamage));
                }
                game.getUI().dmgAmount().setAlpha(1);
                if (movingTextY >= 2.1f) {
                    game.dmgTextNode().setLocalTranslation(game.getCentaur().getWorldTranslation()
                            .add(0, movingTextY, movingTextZ += 0.01f));
                }
                if (movingTextY >= 4f
                        && movingTextY <= 4.49f) {
                    game.getUI().dmgAmount().setAlpha(-200000);

                } else if (movingTextY >= 4.5f) {
                    game.getUI().dmgAmount().setAlpha(-4000000);
                } else if (movingTextY
                        >= 5f) {
                    game.getUI().dmgAmount().setAlpha(-400000000);
                }
                if (movingTextY >= 7f) {
                    playerDamage =
                            random.nextInt(game.getPlayer().getStr())
                            + game.getPlayer().getLevel() * random.nextInt(3);
                    attackingCentaur = false;
                    movingTextY = 2f;
                    movingTextZ = 0f;
                    timer = 0;
                }
            }
        }

        /**
         * This snippet of code deals with the Centaur attacking the Player.
         */
        if (game.getCentaur()
                .getHp() > 0
                && game.getPlayerModel().getWorldTranslation()
                .distance(game.getCentaur().getWorldTranslation()) < 10) {
            attackTimer = attackTimer + tpf;
            if (attackTimer > 1f) {
                if (game.centaurAnimation().getAnimationName().equalsIgnoreCase("Attack1")
                        || game.centaurAnimation().getAnimationName().equalsIgnoreCase("Attack2")) {

                    enemyDamage = random.nextInt(game.getCentaur().getDmg());
                    game.getPlayer().decrementHealth(enemyDamage);
                    underAttack = true;
                    attackTimer = 0;
                }
            }
        }//end of Combat with the Centaur
        /**
         * This displays text when the player is taking damage from the Centaur.
         */
        if (underAttack
                && game.getPlayerModel()
                .getWorldTranslation().distance(game.getCentaur().getWorldTranslation()) <= 50) {
            timer = timer + tpf;
            if (timer > 0.0001f) {
                game.takingDamageNode().attachChild(game.getUI().takingDamageText());
                game.getApp().getRootNode().attachChild(game.takingDamageNode());
                game.takingDamageNode().setLocalTranslation(game.getPlayer().getWorldTranslation()
                        .add(0, decreasingTextY -= 0.06f, 0));
                game.getUI().takingDamageText().setText("-"
                        + String.valueOf(enemyDamage));
                game.getUI().takingDamageText().setAlpha(1);
                if (decreasingTextY
                        <= 5.9f) {
                    game.takingDamageNode().setLocalTranslation(game.getPlayer().getWorldTranslation()
                            .add(0, decreasingTextY, movingTextZ += 0.01f));
                }
                if (decreasingTextY <= 4f && decreasingTextY >= 3.5f) {
                    game.getUI().takingDamageText().setAlpha(-200000);

                } else if (decreasingTextY <= 3.49f) {
                    game.getUI().takingDamageText().setAlpha(-4000000);
                } else if (decreasingTextY <= 3f) {
                    game.getUI().takingDamageText().setAlpha(-400000000);
                }
                if (decreasingTextY <= 2.5f) {
                    enemyDamage =
                            random.nextInt(game.getCentaur().getDmg()) + 1;
                    underAttack = false;
                    decreasingTextY = 6f;
                    movingTextZ = 0f;
                    timer = 0;
                }
            }
        }
        //end of combat logic dealing with the Centaur.

        /**
         * Combat dealing with Troll.
         *
         */
        /**
         * Player dealing damage to the troll.
         */
        if (game.isMelee()) {
            if (game.getTroll().hitBox().getOverlappingObjects().contains(game.getPlayer().hitBox())
                    && game.getPlayerModel().getWorldTranslation().distance(game.getTroll().getWorldTranslation())
                    < 4
                    && game.getPlayer().getAttackAnimation().getAnimationName().equals("Attack1")
                    || game.getTroll().hitBox().getOverlappingObjects().contains(game.getPlayer().hitBox())
                    && game.getPlayerModel().getWorldTranslation().distance(game.getTroll().getWorldTranslation())
                    < 4
                    && game.getPlayer().getAttackAnimation().getAnimationName().equals("Attack2")) {
                timer = timer + tpf;
                attackingTroll = true;
                if (timer > 1f) {
                    if (random.nextInt(100 - game.getPlayer().getCritChance()) == 1) {
                        game.getTroll().decrementHealth(playerDamage * 2);
                        crit = true;
                        timer = 0;
                    } else {
                        game.getTroll().decrementHealth(playerDamage);
                        crit = false;
                        timer = 0;
                    }
                }
            }
        }

        if (game.isRange()) {
            if (game.getTroll().hitBox().getOverlappingObjects().contains(game.getItem().arrowHitbox())) {
                game.getItem().removeArrow();
                timer = timer + tpf;
                attackingTroll = true;
                if (timer > 1f) {
                    game.getTroll().decrementHealth(playerDamage);
                    timer = 0;
                }
            }
        }

        if (game.isMage()) {
            if (game.getTroll().hitBox().getOverlappingObjects().contains(game.getPlayer().getSpellCastHitBox())) {
                timer = timer + tpf;

                game.getApp().getRootNode().detachChild(game.getPlayer().getSpellCastNode());
                game.getApp().getRootNode().detachChild(game.getPlayer().getSpellEffectNode());
                game.getPlayer().getSpellCastHitBox().setEnabled(false);
                game.getPlayer().getSpellCastPhysics().setEnabled(false);
                game.getPlayer().getSpellBloom().setEnabled(false);
                attackingTroll = true;
                if (timer > 1f) {
                    game.getTroll().decrementHealth(playerDamage);
                    timer = 0;
                }
            }
        }
        /**
         * Scrolling Damage text for the Troll.
         */
        if (attackingTroll) {
            timer = timer + tpf;
            if (timer > 0.0001f) {
                game.dmgTextNode().attachChild(game.getUI().dmgAmount());
                game.getApp().getRootNode().attachChild(game.dmgTextNode());
                game.dmgTextNode().setLocalTranslation(game.getTroll().getWorldTranslation()
                        .add(0, movingTextY += 0.06f, 0));
                if (crit) {
                    game.getUI().dmgAmount().setText("Critical!");
                } else {
                    game.getUI().dmgAmount().setText(String.valueOf(playerDamage));
                }
                game.getUI().dmgAmount().setAlpha(1);
                if (movingTextY >= 2.1f) {
                    game.dmgTextNode().setLocalTranslation(game.getTroll().getWorldTranslation()
                            .add(0, movingTextY, movingTextZ += 0.01f));
                }
                if (movingTextY >= 4f
                        && movingTextY <= 4.49f) {
                    game.getUI().dmgAmount().setAlpha(-200000);

                } else if (movingTextY >= 4.5f) {
                    game.getUI().dmgAmount().setAlpha(-4000000);
                } else if (movingTextY
                        >= 5f) {
                    game.getUI().dmgAmount().setAlpha(-400000000);
                }
                if (movingTextY >= 7f) {
                    playerDamage =
                            random.nextInt(game.getPlayer().getStr())
                            + game.getPlayer().getLevel() * random.nextInt(3);
                    attackingTroll = false;
                    movingTextY = 2f;
                    movingTextZ = 0f;
                    timer = 0;
                }
            }
        }

        /**
         * This snippet of code deals with the Troll attacking the Player.
         */
        if (game.getTroll()
                .getHp() > 0
                && game.getPlayerModel().getWorldTranslation()
                .distance(game.getTroll().getWorldTranslation()) < 14) {
            attackTimer = attackTimer + tpf;
            if (attackTimer > 1f) {
                if (game.trollAnimation().getAnimationName().equalsIgnoreCase("Attack1")
                        || game.trollAnimation().getAnimationName().equalsIgnoreCase("Attack2")) {

                    enemyDamage = random.nextInt(game.getTroll().getDmg());
                    game.getPlayer().decrementHealth(enemyDamage);
                    underAttack = true;
                    attackTimer = 0;
                }
            }
        }//end of Combat with Troll
        /**
         * This displays text when the player is taking damage from the Troll.
         */
        if (underAttack
                && game.getPlayerModel()
                .getWorldTranslation().distance(game.getTroll().getWorldTranslation()) <= 60) {
            timer = timer + tpf;
            if (timer > 0.0001f) {
                game.takingDamageNode().attachChild(game.getUI().takingDamageText());
                game.getApp().getRootNode().attachChild(game.takingDamageNode());
                game.takingDamageNode().setLocalTranslation(game.getPlayer().getWorldTranslation()
                        .add(0, decreasingTextY -= 0.06f, 0));
                game.getUI().takingDamageText().setText("-"
                        + String.valueOf(enemyDamage));
                game.getUI().takingDamageText().setAlpha(1);
                if (decreasingTextY
                        <= 5.9f) {
                    game.takingDamageNode().setLocalTranslation(game.getPlayer().getWorldTranslation()
                            .add(0, decreasingTextY, movingTextZ += 0.01f));
                }
                if (decreasingTextY <= 4f && decreasingTextY >= 3.5f) {
                    game.getUI().takingDamageText().setAlpha(-200000);

                } else if (decreasingTextY <= 3.49f) {
                    game.getUI().takingDamageText().setAlpha(-4000000);
                } else if (decreasingTextY <= 3f) {
                    game.getUI().takingDamageText().setAlpha(-400000000);
                }
                if (decreasingTextY <= 2.5f) {
                    enemyDamage =
                            random.nextInt(game.getTroll().getDmg()) + 1;
                    underAttack = false;
                    decreasingTextY = 6f;
                    movingTextZ = 0f;
                    timer = 0;
                }
            }
        }
        //end of combat logic dealing with the Troll.

        /**
         * Combat dealing with Spider.
         */
        /**
         * Player dealing damage to the spider.
         */
        if (game.isMelee()) {
            if (game.getSpider().hitBox().getOverlappingObjects().contains(game.getPlayer().hitBox())
                    && game.getPlayerModel().getWorldTranslation().distance(game.getSpider().getWorldTranslation())
                    < 4
                    && game.getPlayer().getAttackAnimation().getAnimationName().equals("Attack1")
                    || game.getSpider().hitBox().getOverlappingObjects().contains(game.getPlayer().hitBox())
                    && game.getPlayerModel().getWorldTranslation().distance(game.getSpider().getWorldTranslation())
                    < 4
                    && game.getPlayer().getAttackAnimation().getAnimationName().equals("Attack2")) {
                timer = timer + tpf;
                attackingSpider = true;
                if (timer > 1f) {
                    if (random.nextInt(100 - game.getPlayer().getCritChance()) == 1) {
                        game.getSpider().decrementHealth(playerDamage * 2);
                        crit = true;
                        timer = 0;
                    } else {
                        game.getSpider().decrementHealth(playerDamage);
                        crit = false;
                        timer = 0;
                    }
                }

            }
        }

        if (game.isRange()) {
            if (game.getSpider().hitBox().getOverlappingObjects().contains(game.getItem().arrowHitbox())) {
                game.getItem().removeArrow();
                timer = timer + tpf;
                attackingSpider = true;
                if (timer > 1f) {
                    game.getSpider().decrementHealth(playerDamage);
                    timer = 0;
                }
            }
        }

        if (game.isMage()) {
            if (game.getSpider().hitBox().getOverlappingObjects().contains(game.getPlayer().getSpellCastHitBox())) {
                timer = timer + tpf;
                game.getApp().getRootNode().detachChild(game.getPlayer().getSpellCastNode());
                game.getApp().getRootNode().detachChild(game.getPlayer().getSpellEffectNode());
                game.getPlayer().getSpellCastHitBox().setEnabled(false);
                game.getPlayer().getSpellCastPhysics().setEnabled(false);
                game.getPlayer().getSpellBloom().setEnabled(false);
                attackingSpider = true;
                if (timer > 1f) {
                    game.getSpider().decrementHealth(playerDamage);
                    timer = 0;
                }
            }
        }
        /**
         * Scrolling Damage text for the Spider.
         */
        if (attackingSpider) {
            timer = timer + tpf;
            if (timer > 0.0001f) {
                game.dmgTextNode().attachChild(game.getUI().dmgAmount());
                game.getApp().getRootNode().attachChild(game.dmgTextNode());
                game.dmgTextNode().setLocalTranslation(game.getSpider().getWorldTranslation()
                        .add(0, movingTextY += 0.06f, 0));
                if (crit) {
                    game.getUI().dmgAmount().setText("Critical!");
                } else {
                    game.getUI().dmgAmount().setText(String.valueOf(playerDamage));
                }
                game.getUI().dmgAmount().setAlpha(1);
                if (movingTextY >= 2.1f) {
                    game.dmgTextNode().setLocalTranslation(game.getSpider().getWorldTranslation()
                            .add(0, movingTextY, movingTextZ += 0.01f));
                }
                if (movingTextY >= 4f
                        && movingTextY <= 4.49f) {
                    game.getUI().dmgAmount().setAlpha(-200000);

                } else if (movingTextY >= 4.5f) {
                    game.getUI().dmgAmount().setAlpha(-4000000);
                } else if (movingTextY
                        >= 5f) {
                    game.getUI().dmgAmount().setAlpha(-400000000);
                }
                if (movingTextY >= 7f) {
                    playerDamage =
                            random.nextInt(game.getPlayer().getStr())
                            + game.getPlayer().getLevel() * random.nextInt(3);
                    attackingSpider = false;
                    movingTextY = 2f;
                    movingTextZ = 0f;
                    timer = 0;
                }
            }
        }

        /**
         * This snippet of code deals with the Spider attacking the Player.
         */
        if (game.getSpider()
                .getHp() > 0
                && game.getPlayerModel().getWorldTranslation()
                .distance(game.getSpider().getWorldTranslation()) < 11) {
            attackTimer = attackTimer + tpf;
            if (attackTimer > 1f) {
                if (game.spiderAnimation().getAnimationName().equalsIgnoreCase("Attack1")
                        || game.spiderAnimation().getAnimationName().equalsIgnoreCase("Attack2")) {

                    enemyDamage = random.nextInt(game.getSpider().getDmg());
                    game.getPlayer().decrementHealth(enemyDamage);
                    underAttack = true;
                    attackTimer = 0;
                }
            }
        }
        //end of Combat with Spider
        /**
         * This displays text when the player is taking damage from the Spider.
         */
        if (underAttack
                && game.getPlayerModel()
                .getWorldTranslation().distance(game.getSpider().getWorldTranslation()) <= 50) {
            timer = timer + tpf;
            if (timer > 0.0001f) {
                game.takingDamageNode().attachChild(game.getUI().takingDamageText());
                game.getApp().getRootNode().attachChild(game.takingDamageNode());
                game.takingDamageNode().setLocalTranslation(game.getPlayer().getWorldTranslation()
                        .add(0, decreasingTextY -= 0.06f, 0));
                game.getUI().takingDamageText().setText("-"
                        + String.valueOf(enemyDamage));
                game.getUI().takingDamageText().setAlpha(1);
                if (decreasingTextY
                        <= 5.9f) {
                    game.takingDamageNode().setLocalTranslation(game.getPlayer().getWorldTranslation()
                            .add(0, decreasingTextY, movingTextZ += 0.01f));
                }
                if (decreasingTextY <= 4f && decreasingTextY >= 3.5f) {
                    game.getUI().takingDamageText().setAlpha(-200000);

                } else if (decreasingTextY <= 3.49f) {
                    game.getUI().takingDamageText().setAlpha(-4000000);
                } else if (decreasingTextY <= 3f) {
                    game.getUI().takingDamageText().setAlpha(-400000000);
                }
                if (decreasingTextY <= 2.5f) {
                    enemyDamage =
                            random.nextInt(game.getSpider().getDmg()) + 1;
                    underAttack = false;
                    decreasingTextY = 6f;
                    movingTextZ = 0f;
                    timer = 0;
                }
            }
        }//end of combat logic dealing with the Spider.
    }//end of handleAttack method

    /**
     * handleDeaths method
     *
     * @param tpf - tpf stands for time per frame, calculates the time between
     * each frame, very useful for timers.
     */
    public void handleDeaths(float tpf) {
        /**
         * Spider Deaths.
         */
        if (game.getSpider().getHp() <= 0) {
            timer = timer + tpf;
            game.getApp().getGuiNode().attachChild(game.getUI().dialogText());
            game.getSpider().hitBox().setEnabled(false);
            //Respawn after 20 seconds.
            if (timer > 20f) {
                game.getSpider().respawn();
                game.spiderAnimation().setAnim("WalkCycle");
                game.getApp().getGuiNode().detachChild(game.getUI().dialogText());
                game.getSpider().setHp(100);

                timer = 0;
            }
        }
        /**
         * Show exp text.
         */
        if (game.getSpider().isDead()) {
            timer = timer + tpf;
            if (timer > 0.0001f) {

                game.getPlayer().attachChild(game.getUI().expText());
                game.getUI().expText().setLocalTranslation(new Vector3f(0, expTextY += 0.06f, 0));
                game.getUI().expText().setText("+55xp");

                game.getUI().expText().setAlpha(1);

                if (expTextY
                        >= 3.1) {
                    game.getUI().expText().setLocalTranslation(new Vector3f(0, expTextY, expTextZ += 0.01));
                }
                if (expTextY >= 4.1 && expTextY <= 5f) {
                    game.getUI().expText().setAlpha(-200000);

                } else if (expTextY >= 5f) {
                    game.getUI().expText().setAlpha(-4000000);
                } else if (expTextY > 5.5f) {
                    game.getUI().expText().setAlpha(-400000000);
                }
                if (expTextY >= 6f) {
                    game.getUI().expText().removeFromParent();
                    expTextZ = 0;
                    expTextY = 2f;
                    game.getSpider().dead(false);
                    removeTimer = 0;
                    timer = 0;

                }
            }
        }
        /**
         * Ogre Deaths.
         */
        if (game.getOgre()
                .getHp() <= 0) {
            timer = timer + tpf;
            game.getOgre().hitBox().setEnabled(false);
            game.getApp().getGuiNode().attachChild(game.getUI().dialogText());
            //Respawn after 20 seconds
            if (timer > 20f) {

                game.getOgre().respawn();
                game.ogreAnimation().setAnim("WalkCycle");
                game.getOgre().setHp(100);

                game.getApp().getGuiNode().detachChild(game.getUI().dialogText());
                timer = 0;
            }
        }
        /**
         * Show exp text.
         */
        if (game.getOgre().isDead()) {
            timer = timer + tpf;
            if (timer > 0.0001f) {

                game.getPlayer().attachChild(game.getUI().expText());
                game.getUI().expText().setLocalTranslation(new Vector3f(0, expTextY += 0.06f, 0));
                game.getUI().expText().setText("+40xp");

                game.getUI().expText().setAlpha(1);
                if (expTextY
                        >= 3.1) {
                    game.getUI().expText().setLocalTranslation(new Vector3f(0, expTextY, expTextZ += 0.01));
                }
                if (expTextY >= 4.1 && expTextY <= 5f) {
                    game.getUI().expText().setAlpha(-200000);

                } else if (expTextY >= 5f) {
                    game.getUI().expText().setAlpha(-4000000);
                } else if (expTextY > 5.5f) {
                    game.getUI().expText().setAlpha(-400000000);
                }
                if (expTextY >= 6f) {
                    game.getUI().expText().removeFromParent();
                    expTextZ = 0;
                    expTextY = 2f;
                    game.getOgre().dead(false);
                    timer = 0;
                }
            }
        }
        /**
         * Troll Deaths.
         */
        if (game.getTroll()
                .getHp() <= 0) {
            timer = timer + tpf;
            game.getTroll().hitBox().setEnabled(false);
            game.getApp().getGuiNode().attachChild(game.getUI().dialogText());
            game.getTroll().hitBox().setEnabled(false);
            //Respawn after 20 seconds.
            if (timer > 20f) {

                game.getTroll().respawn();
                game.trollAnimation().setAnim("Walk");
                game.getTroll().setHp(100);

                game.getApp().getGuiNode().detachChild(game.getUI().dialogText());
                timer = 0;
            }
        }
        /**
         * Show exp text.
         */
        if (game.getTroll().isDead()) {
            timer = timer + tpf;
            if (timer > 0.0001f) {

                game.getPlayer().attachChild(game.getUI().expText());
                game.getUI().expText().setLocalTranslation(new Vector3f(0, expTextY += 0.06f, 0));
                game.getUI().expText().setText("+80xp");

                game.getUI().expText().setAlpha(1);
                if (expTextY
                        >= 3.1) {
                    game.getUI().expText().setLocalTranslation(new Vector3f(0, expTextY, expTextZ += 0.01));
                }
                if (expTextY >= 4.1 && expTextY <= 5f) {
                    game.getUI().expText().setAlpha(-200000);

                } else if (expTextY >= 5f) {
                    game.getUI().expText().setAlpha(-4000000);
                } else if (expTextY > 5.5f) {
                    game.getUI().expText().setAlpha(-400000000);
                }
                if (expTextY >= 6f) {
                    game.getUI().expText().removeFromParent();
                    expTextZ = 0;
                    expTextY = 2f;
                    game.getTroll().dead(false);
                    timer = 0;
                }
            }
        }
        /**
         * Elf Deaths.
         */
        if (game.getElf()
                .getHp() <= 0) {
            timer = timer + tpf;
            game.getElf().hitBox().setEnabled(false);
            game.getApp().getGuiNode().attachChild(game.getUI().dialogText());
            //Respawn after 20 seconds
            if (timer > 20f) {

                game.getElf().respawn();
                game.elfAnimation().setAnim("WalkCycle");
                game.getElf().setHp(100);

                game.getApp().getGuiNode().detachChild(game.getUI().dialogText());
                timer = 0;
            }
        }
        /**
         * Show exp text.
         */
        if (game.getElf().isDead()) {
            timer = timer + tpf;
            if (timer > 0.0001f) {
                game.getPlayer().attachChild(game.getUI().expText());
                game.getUI().expText().setLocalTranslation(new Vector3f(0, expTextY += 0.06f, 0));
                game.getUI().expText().setText("+20xp");

                game.getUI().expText().setAlpha(1);
                if (expTextY
                        >= 3.1) {
                    game.getUI().expText().setLocalTranslation(new Vector3f(0, expTextY, expTextZ += 0.01));
                }
                if (expTextY >= 4.1 && expTextY <= 5f) {
                    game.getUI().expText().setAlpha(-200000);

                } else if (expTextY >= 5f) {
                    game.getUI().expText().setAlpha(-4000000);
                } else if (expTextY > 5.5f) {
                    game.getUI().expText().setAlpha(-400000000);
                }
                if (expTextY >= 6f) {
                    game.getUI().expText().removeFromParent();
                    expTextZ = 0;
                    expTextY = 2f;
                    game.getElf().dead(false);
                    timer = 0;
                }
            }
        }
        /**
         * Centaur Deaths.
         */
        if (game.getCentaur()
                .getHp() <= 0) {
            timer = timer + tpf;
            game.getCentaur().hitBox().setEnabled(false);
            game.getApp().getGuiNode().attachChild(game.getUI().dialogText());
            //Respawn after 20 seconds
            if (timer > 20f) {

                game.getCentaur().respawn();
                game.centaurAnimation().setAnim("Walk");
                game.getCentaur().setHp(100);

                game.getApp().getGuiNode().detachChild(game.getUI().dialogText());
                timer = 0;
            }
        }
        /*
         * Show exp text.
         */
        if (game.getCentaur().isDead()) {
            timer = timer + tpf;
            if (timer > 0.0001f) {

                game.getPlayer().attachChild(game.getUI().expText());
                game.getUI().expText().setLocalTranslation(new Vector3f(0, expTextY += 0.06f, 0));
                game.getUI().expText().setText("+30xp");

                game.getUI().expText().setAlpha(1);
                if (expTextY
                        >= 3.1) {
                    game.getUI().expText().setLocalTranslation(new Vector3f(0, expTextY, expTextZ += 0.01));
                }
                if (expTextY >= 4.1 && expTextY <= 5f) {
                    game.getUI().expText().setAlpha(-200000);

                } else if (expTextY >= 5f) {
                    game.getUI().expText().setAlpha(-4000000);
                } else if (expTextY > 5.5f) {
                    game.getUI().expText().setAlpha(-400000000);
                }
                if (expTextY >= 6f) {
                    game.getUI().expText().removeFromParent();
                    expTextZ = 0;
                    expTextY = 2f;
                    game.getCentaur().dead(false);
                    timer = 0;
                }
            }
        }
    }//end of handleDeaths method
}//end of Combat Class.