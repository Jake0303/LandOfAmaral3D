package Player;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import game.Game;

/**
 * ThirdPersonCamera class
 *
 * @author Berzee edited by Jacob Amaral
 */
public class ThirdPersonCamera {
    //The "pivot" Node allows for easy third-person mouselook! It's actually
    //just an empty Node that gets attached to the center of the Player.
    //
    //The CameraNode is set up to always position itself behind the *pivot*
    //instead of behind the Player. So when we want to mouselook around the
    //Player, we simply need to spin the pivot! The camera will orbit behind it
    //while the Player object remains still.
    //
    //NOTE: Currently only vertical mouselook (around the X axis) is working.
    //The other two axes could be added fairly easily, once you have an idea
    //for how they should actually behave (min and max angles, et cetera).

    private Node pivot;
    private CameraNode cameraNode;
    private Node teleportNode;
    private Node spellNode;
    private Game game;
    //Change these as you desire. Lower verticalAngle values will put the camera
    //closer to the ground.
    private float teleportDistance = 40;
    private float shotDistance = 5;
    public float followDistance = 30;
    public float verticalAngle = 90 * FastMath.DEG_TO_RAD;
    public float horizontalAngle = 360 * FastMath.DEG_TO_RAD;
    //These bounds keep the camera from spinning too far and clipping through
    //the floor or turning upside-down. You can change them as needed but it is
    //recommended to keep the values in the (-90,90) range.
    public float maxVerticalAngle = 85 * FastMath.DEG_TO_RAD;
    public float minVerticalAngle = 6 * FastMath.DEG_TO_RAD;
    public float maxHorizontalAngle = 355 * FastMath.DEG_TO_RAD;
    public float minHorizontalAngle = -6 * FastMath.DEG_TO_RAD;

    /**
     * ThirdPersonCamera constructor
     *
     * @param name
     * @param cam
     * @param player
     */
    public ThirdPersonCamera(String name, Camera cam, Node player, Game game) {
        pivot = new Node("CamTrack");
        player.attachChild(pivot);

        this.game = game;

        teleportNode = new Node();
        spellNode = new Node();

        cameraNode = new CameraNode(name, cam);
        cameraNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        pivot.attachChild(cameraNode);
        pivot.attachChild(spellNode);
        cameraNode.setLocalTranslation(new Vector3f(0, 5, followDistance));
        cameraNode.lookAt(pivot.getLocalTranslation(), Vector3f.UNIT_Y);

        pivot.getLocalRotation().fromAngleAxis(-verticalAngle, Vector3f.UNIT_X);

        pivot.attachChild(teleportNode);
        teleportNode.setLocalTranslation(new Vector3f(0, 10, -teleportDistance));
        spellNode.setLocalTranslation(new Vector3f(0, 10, -shotDistance));
    }//end of ThirdPersonCamera Constructor

    /**
     * verticalRotate method
     *
     * @param angle
     */
    public void verticalRotate(float angle) {
        verticalAngle += angle;

        if (verticalAngle > maxVerticalAngle) {
            verticalAngle = maxVerticalAngle;
        } else if (verticalAngle < minVerticalAngle) {
            verticalAngle = minVerticalAngle;
        }

        pivot.getLocalRotation().fromAngleAxis(-verticalAngle, Vector3f.UNIT_X);
    }//end of verticalRotate

    /**
     * getCameraNode method
     *
     * @return
     */
    public CameraNode getCameraNode() {
        return cameraNode;
    }//end of getCameraNode method

    /**
     * getCameraTrack method
     *
     * @return
     */
    public Node getCameraTrack() {
        return pivot;
    }//end of getCameraTrack method

    /**
     * getTeleportNode method
     *
     * @return the teleportNode
     */
    public Node getTeleportNode() {
        return teleportNode;
    }//end of getTeleportNode method
    /**
     * getSpellNode method
     *
     * @return the teleportDistance
     */
    public Node getSpellNode() {
        return spellNode;
    }//end of getSpellNode method
}//end of ThirdPersonCamera method