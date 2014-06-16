package UI;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.ui.Picture;
import game.Game;

/**
 *
 * @author Jacob Amaral
 */
public class UI {

    private Game game;
    private Picture hpBar, emptyHpBar, expBar;
    private BitmapText hpAmount, dmgAmount, ogreText, spiderText, elfText, trollText, centaurText, trollLevel, elfLevel, centaurLevel, spiderLevel, ogreLevel, dialogText,
            hp, exp, levelUp, goldText, healthText, takingDamage, playerName, expText;
    private AssetManager assetManager;
    private BitmapFont hpFont, dmgFont, dialogFont, npcNameFont;

    /**
     * UI Constructor
     *
     * @param game
     */
    public UI(Game game, AssetManager assetManager) {
        this.game = game;

        hpFont = assetManager.loadFont("Interface/Fonts/BerlinSansFB.fnt");
        npcNameFont = assetManager.loadFont("Interface/Fonts/LucidaSansUnicode.fnt");
        dialogFont = assetManager.loadFont("Interface/Fonts/CopperplateGothicBold.fnt");
        dmgFont = assetManager.loadFont("Interface/Fonts/WideLatin.fnt");

        hpAmount = new BitmapText(hpFont, false);
        hp = new BitmapText(hpFont, false);
        exp = new BitmapText(hpFont, false);
        dmgAmount = new BitmapText(dmgFont, false);
        levelUp = new BitmapText(dmgFont, false);
        goldText = new BitmapText(dmgFont, false);
        healthText = new BitmapText(dmgFont, false);
        takingDamage = new BitmapText(dmgFont, false);
        expText = new BitmapText(dmgFont, false);
        playerName = new BitmapText(npcNameFont, false);
        dialogText = new BitmapText(dialogFont, false);
        ogreText = new BitmapText(npcNameFont, false);
        ogreLevel = new BitmapText(npcNameFont, false);
        spiderText = new BitmapText(npcNameFont, false);
        spiderLevel = new BitmapText(npcNameFont, false);
        centaurText = new BitmapText(npcNameFont, false);
        centaurLevel = new BitmapText(npcNameFont, false);
        trollText = new BitmapText(npcNameFont, false);
        trollLevel = new BitmapText(npcNameFont, false);
        elfText = new BitmapText(npcNameFont, false);
        elfLevel = new BitmapText(npcNameFont, false);

        hpBar = new Picture("Hp Bar");
        hpBar.setImage(assetManager, "Interface/hp.png", true);

        expBar = new Picture("Exp Bar");
        expBar.setImage(assetManager, "Interface/exp.png", true);

        emptyHpBar = new Picture("Hp Bar");
        emptyHpBar.setImage(assetManager, "Interface/emptyhp.png", true);

        init();

    }//end of UI

    /**
     * init method
     */
    private void init() {
        //Health Bar
        hpBar.setHeight(30f);
        hpBar.setWidth(300f);
        //Exp Bar
        expBar.setHeight(15f);
        expBar.setWidth(1f);
        //Red health bar
        emptyHpBar.setHeight(30f);
        emptyHpBar.setWidth(300f);
        //White hp text inside health bar
        hpAmount.setColor(ColorRGBA.White);
        hpAmount.setSize(20);

        hp.setColor(ColorRGBA.White);
        hp.setSize(20);

        exp.setColor(ColorRGBA.White);
        exp.setSize(20);

        trollText.setText("Troll");
        trollText.setColor(ColorRGBA.Orange);
        trollText.setSize(2f);
        trollText.setBox(new Rectangle(-dmgAmount.getLineWidth() / 2, 0f, dmgAmount.getLineWidth() + 80, dmgAmount.getLineHeight() + 50));
        trollText.setQueueBucket(RenderQueue.Bucket.Transparent);
        trollText.setAlignment(BitmapFont.Align.Left);
        trollText.setVerticalAlignment(BitmapFont.VAlign.Top);
        trollText.setLineWrapMode(LineWrapMode.Word);

        trollLevel.setText("Lv. 8");
        trollLevel.setColor(new ColorRGBA(255 / 255, 135 / 255, 0, 1));
        trollLevel.setSize(1f);
        trollLevel.setBox(new Rectangle(-dmgAmount.getLineWidth() / 2, 0f, dmgAmount.getLineWidth() + 80, dmgAmount.getLineHeight() + 50));
        trollLevel.setQueueBucket(RenderQueue.Bucket.Transparent);
        trollLevel.setAlignment(BitmapFont.Align.Left);
        trollLevel.setVerticalAlignment(BitmapFont.VAlign.Top);
        trollLevel.setLineWrapMode(LineWrapMode.NoWrap);

        ogreText.setText("Ogre");
        ogreText.setColor(ColorRGBA.Orange);
        ogreText.setSize(2f);
        ogreText.setBox(new Rectangle(-dmgAmount.getLineWidth() / 2.0f, 0f, dmgAmount.getLineWidth() + 20, dmgAmount.getLineHeight()));
        ogreText.setQueueBucket(RenderQueue.Bucket.Transparent);
        ogreText.setAlignment(BitmapFont.Align.Left);
        ogreText.setVerticalAlignment(BitmapFont.VAlign.Top);
        ogreText.setLineWrapMode(LineWrapMode.NoWrap);

        ogreLevel.setText("Lv. 5");
        ogreLevel.setColor(new ColorRGBA(255 / 255, 135 / 255, 0, 1));
        ogreLevel.setSize(1f);
        ogreLevel.setBox(new Rectangle(-dmgAmount.getLineWidth() / 2, 0f, dmgAmount.getLineWidth() + 80, dmgAmount.getLineHeight() + 50));
        ogreLevel.setQueueBucket(RenderQueue.Bucket.Transparent);
        ogreLevel.setAlignment(BitmapFont.Align.Left);
        ogreLevel.setVerticalAlignment(BitmapFont.VAlign.Top);
        ogreLevel.setLineWrapMode(LineWrapMode.NoWrap);

        spiderText.setText("Spider");
        spiderText.setColor(ColorRGBA.Orange);
        spiderText.setSize(2f);
        spiderText.setBox(new Rectangle(-dmgAmount.getLineWidth() / 2.0f, 0f, dmgAmount.getLineWidth() + 20, dmgAmount.getLineHeight()));
        spiderText.setQueueBucket(RenderQueue.Bucket.Transparent);
        spiderText.setAlignment(BitmapFont.Align.Left);
        spiderText.setVerticalAlignment(BitmapFont.VAlign.Top);
        spiderText.setLineWrapMode(LineWrapMode.NoWrap);

        spiderLevel.setText("Lv. 6");
        spiderLevel.setColor(new ColorRGBA(255 / 255, 135 / 255, 0, 1));
        spiderLevel.setSize(1f);
        spiderLevel.setBox(new Rectangle(-dmgAmount.getLineWidth() / 2, 0f, dmgAmount.getLineWidth() + 80, dmgAmount.getLineHeight() + 50));
        spiderLevel.setQueueBucket(RenderQueue.Bucket.Transparent);
        spiderLevel.setAlignment(BitmapFont.Align.Left);
        spiderLevel.setVerticalAlignment(BitmapFont.VAlign.Top);
        spiderLevel.setLineWrapMode(LineWrapMode.NoWrap);

        centaurText.setText("Centaur");
        centaurText.setColor(ColorRGBA.Orange);
        centaurText.setSize(2f);
        centaurText.setBox(new Rectangle(-dmgAmount.getLineWidth() / 2.0f, 0f, dmgAmount.getLineWidth() + 20, dmgAmount.getLineHeight()));
        centaurText.setQueueBucket(RenderQueue.Bucket.Transparent);
        centaurText.setAlignment(BitmapFont.Align.Left);
        centaurText.setVerticalAlignment(BitmapFont.VAlign.Top);
        centaurText.setLineWrapMode(LineWrapMode.NoWrap);

        centaurLevel.setText("Lv. 4");
        centaurLevel.setColor(new ColorRGBA(255 / 255, 135 / 255, 0, 1));
        centaurLevel.setSize(1f);
        centaurLevel.setBox(new Rectangle(-dmgAmount.getLineWidth() / 2, 0f, dmgAmount.getLineWidth() + 80, dmgAmount.getLineHeight() + 50));
        centaurLevel.setQueueBucket(RenderQueue.Bucket.Transparent);
        centaurLevel.setAlignment(BitmapFont.Align.Left);
        centaurLevel.setVerticalAlignment(BitmapFont.VAlign.Top);
        centaurLevel.setLineWrapMode(LineWrapMode.NoWrap);

        elfText.setText("Elf");
        elfText.setColor(ColorRGBA.Orange);
        elfText.setSize(2f);
        elfText.setBox(new Rectangle(-dmgAmount.getLineWidth() / 2.0f, 0f, dmgAmount.getLineWidth() + 20, dmgAmount.getLineHeight()));
        elfText.setQueueBucket(RenderQueue.Bucket.Transparent);
        elfText.setAlignment(BitmapFont.Align.Left);
        elfText.setVerticalAlignment(BitmapFont.VAlign.Top);
        elfText.setLineWrapMode(LineWrapMode.NoWrap);

        elfLevel.setText("Lv. 2");
        elfLevel.setColor(new ColorRGBA(255 / 255, 135 / 255, 0, 1));
        elfLevel.setSize(1f);
        elfLevel.setBox(new Rectangle(-dmgAmount.getLineWidth() / 2, 0f, dmgAmount.getLineWidth() + 80, dmgAmount.getLineHeight() + 50));
        elfLevel.setQueueBucket(RenderQueue.Bucket.Transparent);
        elfLevel.setAlignment(BitmapFont.Align.Left);
        elfLevel.setVerticalAlignment(BitmapFont.VAlign.Top);
        elfLevel.setLineWrapMode(LineWrapMode.NoWrap);


        levelUp.setColor(ColorRGBA.Green);
        levelUp.setSize(1f);
        levelUp.setBox(new Rectangle(-levelUp.getLineWidth(), 0f, levelUp.getLineWidth() + 30, levelUp.getLineHeight()));
        levelUp.setQueueBucket(RenderQueue.Bucket.Transparent);
        levelUp.setAlignment(Align.Left);
        levelUp.setVerticalAlignment(BitmapFont.VAlign.Bottom);
        levelUp.setLineWrapMode(LineWrapMode.Character);

        dmgAmount.setColor(new ColorRGBA(20, 0, 0, 1));
        dmgAmount.setSize(1f);
        dmgAmount.setBox(new Rectangle(-dmgAmount.getLineWidth(), 0f, dmgAmount.getLineWidth() + 7, dmgAmount.getLineHeight()));
        dmgAmount.setQueueBucket(RenderQueue.Bucket.Transparent);
        dmgAmount.setAlignment(Align.Center);
        dmgAmount.setVerticalAlignment(BitmapFont.VAlign.Top);

        expText.setColor(ColorRGBA.LightGray);
        expText.setSize(0.7f);
        expText.setBox(new Rectangle(-dmgAmount.getLineWidth(), 0f, dmgAmount.getLineWidth() + 7, dmgAmount.getLineHeight()));
        expText.setQueueBucket(RenderQueue.Bucket.Transparent);
        expText.setAlignment(Align.Left);
        expText.setVerticalAlignment(BitmapFont.VAlign.Bottom);

        goldText.setColor(ColorRGBA.Yellow);
        goldText.setSize(1f);
        goldText.setBox(new Rectangle(-goldText.getLineWidth(), 0f, goldText.getLineWidth() + 7, goldText.getLineHeight()));
        goldText.setQueueBucket(RenderQueue.Bucket.Transparent);
        goldText.setAlignment(Align.Left);
        goldText.setVerticalAlignment(BitmapFont.VAlign.Top);

        playerName.setColor(ColorRGBA.DarkGray);
        playerName.setSize(0.4f);
        playerName.setBox(new Rectangle(-goldText.getLineWidth(), 0f, goldText.getLineWidth() + 7, goldText.getLineHeight()));
        playerName.setQueueBucket(RenderQueue.Bucket.Transparent);
        playerName.setAlignment(Align.Center);
        playerName.setVerticalAlignment(BitmapFont.VAlign.Bottom);

        healthText.setColor(ColorRGBA.Green);
        healthText.setSize(1f);
        healthText.setBox(new Rectangle(-healthText.getLineWidth(), 0f, healthText.getLineWidth() + 7, healthText.getLineHeight()));
        healthText.setQueueBucket(RenderQueue.Bucket.Transparent);
        healthText.setAlignment(Align.Center);
        healthText.setVerticalAlignment(BitmapFont.VAlign.Top);

        takingDamage.setColor(new ColorRGBA(20, 255, 0, 0));
        takingDamage.setSize(0.5f);
        takingDamage.setBox(new Rectangle(-healthText.getLineWidth(), 0f, healthText.getLineWidth() + 7, healthText.getLineHeight()));
        takingDamage.setQueueBucket(RenderQueue.Bucket.Transparent);
        takingDamage.setAlignment(Align.Center);
        takingDamage.setVerticalAlignment(BitmapFont.VAlign.Center);


        dialogText.setColor(ColorRGBA.White);
        dialogText.setSize(20f);

    }//end of init

    public void update() {
        //This shows the HP text in your hp bar.
        hpAmount.setText(String.valueOf(game.getPlayer().getHealth()
                + "/" + game.getPlayer().getTotalHealth()));
        //This displays the green hp bar.
        if (game.getPlayer().getLevel() == 1) {
            hpBar.setWidth(game.getPlayer().getHealth() * (300/game.getPlayer().getTotalHealth()));
        }
        if (game.getPlayer().getLevel() == 2) {
            hpBar.setWidth(game.getPlayer().getHealth() * (300/game.getPlayer().getTotalHealth()));
        }
        if (game.getPlayer().getLevel() == 3) {
            hpBar.setWidth(game.getPlayer().getHealth() * (300/game.getPlayer().getTotalHealth()));
        }
        if (game.getPlayer().getLevel() == 4) {
            hpBar.setWidth(game.getPlayer().getHealth() * (300/game.getPlayer().getTotalHealth()));
        }
        if (game.getPlayer().getLevel() == 5) {
            hpBar.setWidth(game.getPlayer().getHealth() * (300/game.getPlayer().getTotalHealth()));
        }
        if (game.getPlayer().getLevel() == 6) {
            hpBar.setWidth(game.getPlayer().getHealth() * (300/game.getPlayer().getTotalHealth()));
        }
        if (game.getPlayer().getLevel() == 7) {
            hpBar.setWidth(game.getPlayer().getHealth() * (300/game.getPlayer().getTotalHealth()));
        }
        if (game.getPlayer().getLevel() == 8) {
            hpBar.setWidth(game.getPlayer().getHealth() * (300/game.getPlayer().getTotalHealth()));
        }
        if (game.getPlayer().getLevel() == 9) {
            hpBar.setWidth(game.getPlayer().getHealth() * (300/game.getPlayer().getTotalHealth()));
        }
        if (game.getPlayer().getLevel() == 10) {
            hpBar.setWidth(game.getPlayer().getHealth() * (300/game.getPlayer().getTotalHealth()));
        }
        //This displays the exp bar
        if (game.getPlayer().getLevel() == 1) {
            expBar.setWidth(game.getPlayer().getExp());
        } else if (game.getPlayer().getLevel() == 2) {
            expBar.setWidth(game.getPlayer().getExp());

        } else if (game.getPlayer().getLevel() == 3) {
            expBar.setWidth(game.getPlayer().getExp());

        } else if (game.getPlayer().getLevel() == 4) {
            expBar.setWidth(game.getPlayer().getExp() / 2);

        } else if (game.getPlayer().getLevel() == 5) {
            expBar.setWidth(game.getPlayer().getExp() / 3);

        } else if (game.getPlayer().getLevel() == 6) {
            expBar.setWidth(game.getPlayer().getExp() / 4);

        } else if (game.getPlayer().getLevel() == 7) {
            expBar.setWidth(game.getPlayer().getExp() / 5);

        } else if (game.getPlayer().getLevel() == 8) {
            expBar.setWidth(game.getPlayer().getExp() / 6);

        } else if (game.getPlayer().getLevel() == 9) {
            expBar.setWidth(game.getPlayer().getExp() / 7);

        } else if (game.getPlayer().getLevel() == 10) {
            expBar.setWidth(game.getPlayer().getExp() / 8);

        }
        /**
         * These are here so that the text above each npc (e.g Ogre Lv5) remains
         * above the npc at all times.
         */
        trollText.setLocalTranslation(game.getTroll().getWorldTranslation().add(0, 25, 0));
        trollLevel.setLocalTranslation(game.getTroll().getWorldTranslation().add(0, 22, 0));

        ogreLevel.setLocalTranslation(game.getOgreModel().getWorldTranslation().add(0, 12, 0));
        ogreText.setLocalTranslation(game.getOgreModel().getWorldTranslation().add(0, 15, 0));

        spiderLevel.setLocalTranslation(game.getSpider().getWorldTranslation().add(0, 8, 0));
        spiderText.setLocalTranslation(game.getSpider().getWorldTranslation().add(0, 10, 0));

        elfLevel.setLocalTranslation(game.getElf().getWorldTranslation().add(0, 7, 0));
        elfText.setLocalTranslation(game.getElf().getWorldTranslation().add(0, 10, 0));

        centaurLevel.setLocalTranslation(game.getCentaur().getWorldTranslation().add(0, 12, 0));
        centaurText.setLocalTranslation(game.getCentaur().getWorldTranslation().add(0, 15, 0));


    }

    /**
     * hpAmount method
     *
     * @return
     */
    public BitmapText hpAmount() {
        return hpAmount;
    }//end of hpAmount

    /**
     * hp method
     *
     * @return
     */
    public BitmapText hp() {
        return hp;
    }//end of hp

    /**
     * exp method
     *
     * @return
     */
    public BitmapText exp() {
        return exp;
    }//end of exp

    /**
     * dmgAmount method
     *
     * @return
     */
    public BitmapText dmgAmount() {
        return dmgAmount;
    }//end of dmgAmount

    /**
     * ogreText method
     *
     * @return
     */
    public BitmapText ogreText() {
        return ogreText;
    }//end of ogreText

    /**
     * spiderText method
     *
     * @return
     */
    public BitmapText spiderText() {
        return spiderText;
    }//end of spiderText

    /**
     * trollText method
     *
     * @return
     */
    public BitmapText trollText() {
        return trollText;
    }//end of trollText

    /**
     * elfText method
     *
     * @return
     */
    public BitmapText elfText() {
        return elfText;
    }//end of elfText

    /**
     * centaurText method
     *
     * @return
     */
    public BitmapText centaurText() {
        return centaurText;
    }//end of centaurText

    /**
     * ogreLevel method
     *
     * @return
     */
    public BitmapText ogreLevel() {
        return ogreLevel;
    }//end of ogreLevel

    /**
     * trollLevel method
     *
     * @return
     */
    public BitmapText trollLevel() {
        return trollLevel;
    }//end of trollLevel

    /**
     * centaurLevel method
     *
     * @return
     */
    public BitmapText centaurLevel() {
        return centaurLevel;
    }//end of centaurLevel

    /**
     * spiderLevel method
     *
     * @return
     */
    public BitmapText spiderLevel() {
        return spiderLevel;
    }//end of spiderLevel

    /**
     * elfLevel method
     *
     * @return
     */
    public BitmapText elfLevel() {
        return elfLevel;
    }//end of elfLevel

    /**
     * dialogText method
     *
     * @return
     */
    public BitmapText dialogText() {
        return dialogText;
    }//end of dialogText

    /**
     * goldText method
     *
     * @return
     */
    public BitmapText goldText() {
        return goldText;
    }//end of goldText

    /**
     * healthText method
     *
     * @return
     */
    public BitmapText healthText() {
        return healthText;
    }//end of healthText

    /**
     * takingDamageText method
     *
     * @return
     */
    public BitmapText takingDamageText() {
        return takingDamage;
    }//end of takingDamage

    /**
     * levelUp method
     *
     * @return
     */
    public BitmapText levelUp() {
        return levelUp;
    }//end of levelUp

    /**
     * playerName method
     *
     * @return
     */
    public BitmapText playerName() {
        return playerName;
    }//end of playerName

    /**
     * expText method
     *
     * @return
     */
    public BitmapText expText() {
        return expText;
    }//end of expText

    /**
     * hpBar method
     *
     * @return
     */
    public Picture hpBar() {
        return hpBar;
    }//end of hpBar

    /**
     * expBar method
     *
     * @return
     */
    public Picture expBar() {
        return expBar;
    }//end of expBar

    /**
     * emptyHpBar method
     *
     * @return
     */
    public Picture emptyHpBar() {
        return emptyHpBar;
    }//end of emptyHpBar
}//end of UI
