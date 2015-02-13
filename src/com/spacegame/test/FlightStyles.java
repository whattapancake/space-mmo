package com.spacegame.test;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.RadialBlurFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.ui.Picture;
import com.jme3.util.SkyFactory;

/**
 *
 * @author Brendan
 */
public class FlightStyles extends SimpleApplication {

    boolean forward = false, backward = false, left = false, right = false, up = false, down = false, rLeft = false, rRight = false, speedUp = false;
    Vector3f moveDir;
    Spatial fighter;
    float aspect, curFOV = 60.0F, spinInertia = 0.0F;
    RadialBlurFilter rbf;

    public static void main(String... args) {
        FlightStyles app = new FlightStyles();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        FilterPostProcessor fpp = new FilterPostProcessor(this.assetManager);
        rbf = new RadialBlurFilter();
        rbf.setSampleDistance(0.2F);
//        rbf.setEnabled(false);
        fpp.addFilter(rbf);
        viewPort.addProcessor(fpp);

//        flyCam.setMoveSpeed(10.0F);
        flyCam.setZoomSpeed(0.0F);
        aspect = (float) cam.getWidth() / (float) cam.getHeight();
        cam.setFrustumPerspective(60.0F, aspect, 0.1F, 1000.0F);

        rootNode.attachChild(SkyFactory.createSky(assetManager,
                assetManager.loadTexture("Textures/SkyMap/Left.png"),
                assetManager.loadTexture("Textures/SkyMap/Right.png"),
                assetManager.loadTexture("Textures/SkyMap/Forward.png"),
                assetManager.loadTexture("Textures/SkyMap/Backward.png"),
                assetManager.loadTexture("Textures/SkyMap/Up.png"),
                assetManager.loadTexture("Textures/SkyMap/Down.png")).scale(1000.0F));

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(0.0058544376F, 0.006835953F, 0.99995947F).negateLocal().normalizeLocal());
        rootNode.addLight(dl);

        rootNode.addLight(new AmbientLight());

        fighter = assetManager.loadModel("Models/Interceptor/Maps/Interceptor.j3o");
        fighter.scale(0.02F);
        rootNode.attachChild(fighter);

        fighter.addControl(new CustomChaseCamera(cam,
                new Vector3f(7, 2.6F, 0), new Vector3f(0, 1.6F, 0), 15.0F,
                new Vector3f(-1.75F, 0.05F, 0), new Vector3f(-8, 0, 0), -1));
        fighter.addControl(new ReticleControl(cam, assetManager, guiNode));

        //Flight style 1
        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_C));
        inputManager.addMapping("RotateLeft", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("RotateRight", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping("Speed", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("CameraSwap", new KeyTrigger(KeyInput.KEY_TAB));
        inputManager.addListener(new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                switch (name) {
                    case "Forward":
                        if (isPressed) {
                            forward = true;
                        } else {
                            forward = false;
                        }
                        break;
                    case "Backward":
                        if (isPressed) {
                            backward = true;
                        } else {
                            backward = false;
                        }
                        break;
                    case "Left":
                        if (isPressed) {
                            left = true;
                        } else {
                            left = false;
                        }
                        break;
                    case "Right":
                        if (isPressed) {
                            right = true;
                        } else {
                            right = false;
                        }
                        break;
                    case "Up":
                        if (isPressed) {
                            up = true;
                        } else {
                            up = false;
                        }
                        break;
                    case "Down":
                        if (isPressed) {
                            down = true;
                        } else {
                            down = false;
                        }
                        break;
                    case "RotateLeft":
                        if (isPressed) {
                            rLeft = true;
                        } else {
                            rLeft = false;
                        }
                        break;
                    case "RotateRight":
                        if (isPressed) {
                            rRight = true;
                        } else {
                            rRight = false;
                        }
                        break;
                    case "Speed":
                        if (isPressed) {
                            speedUp = true;
                        } else {
                            speedUp = false;
                        }
                        break;
                    case "CameraSwap":
                        if (!isPressed) {
                            fighter.getControl(CustomChaseCamera.class).swapCam();
                        }
                        break;
                }
            }
        }, "Forward", "Backward", "Left", "Right", "Up", "Down", "RotateLeft", "RotateRight", "Speed", "CameraSwap");
        inputManager.addMapping("X+", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("Y+", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("X-", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("Y-", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        final float sensitivity = 1.0F;
        inputManager.addListener(new AnalogListener() {
            @Override
            public void onAnalog(String name, float value, float tpf) {
                switch (name) {
                    case "X+":
                        fighter.rotate(0.0F, -value * sensitivity, 0.0F);
                        break;
                    case "X-":
                        fighter.rotate(0.0F, value * sensitivity, 0.0F);
                        break;
                    case "Y+":
                        fighter.rotate(0.0F, 0.0F, -value * sensitivity);
                        break;
                    case "Y-":
                        fighter.rotate(0.0F, 0.0F, value * sensitivity);
                        break;
                }
            }
        }, "X+", "Y+", "X-", "Y-");

        rootNode.attachChild(assetManager.loadModel("Models/HN t42 Battle cruiser/Maps/HN t42 Envorian Battlecruiser.j3o").scale(0.2F));
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (moveDir == null) {
            moveDir = new Vector3f();
        }

        curFOV = 60.0F + FastMath.clamp(moveDir.length() - 20.0F, 0.0F, 15.0F);
        rbf.setSampleStrength(FastMath.clamp((curFOV - 60.0F) / 2.0F, 0.0F, 15.0F));
        cam.setFrustumPerspective(curFOV, aspect, 0.1F, 1000.0F);

        Vector3f impulse = new Vector3f();
        Vector3f forwardVec = fighter.getWorldRotation().mult(Vector3f.UNIT_X).normalizeLocal().negateLocal();
        Vector3f leftVec = fighter.getWorldRotation().mult(Vector3f.UNIT_Z).normalizeLocal();
        Vector3f upVec = fighter.getWorldRotation().mult(Vector3f.UNIT_Y).normalizeLocal();

        if (forward) {
            impulse.addLocal(forwardVec);
        }
        if (backward) {
            impulse.addLocal(forwardVec.negate());
        }
        if (left) {
            impulse.addLocal(leftVec);
        }
        if (right) {
            impulse.addLocal(leftVec.negate());
        }
        if (up) {
            impulse.addLocal(upVec);
        }
        if (down) {
            impulse.addLocal(upVec.negate());
        }
        impulse.normalizeLocal().multLocal(20.0F * (speedUp ? 2.0F : 1.0F)); //20 = ship speed

        //Now calculate an inertia-correct spin speed
        float rotSpeed = 4.0F;
        if (rLeft) {
            spinInertia = FastMath.interpolateLinear(rotSpeed * tpf, spinInertia, 0.05F);
        } else if (rRight) {
            spinInertia = FastMath.interpolateLinear(rotSpeed * tpf, spinInertia, -0.05F);
        } else {
            spinInertia = FastMath.interpolateLinear(rotSpeed * tpf * 1.25F, spinInertia, 0.0F);
        }
        fighter.rotate(spinInertia, 0.0F, 0.0F);

        moveDir.interpolateLocal(impulse, tpf * 1.5F); //eventually tpf * acceleration value

        fighter.move(moveDir.mult(tpf));
    }
}

class CustomChaseCamera extends AbstractControl {

    protected Camera cam;
    protected Vector3f camOffset1, centerOffset1, camOffset2, centerOffset2;
    protected float interp1, interp2;
    protected boolean useSet = true;

    public CustomChaseCamera(Camera cam, Vector3f camOffset, Vector3f centerOffset, float interp1, Vector3f camOff2, Vector3f centerOff2, float interp2) {
        this.camOffset1 = camOffset;
        this.centerOffset1 = centerOffset;
        this.interp1 = interp1;

        this.camOffset2 = camOff2;
        this.centerOffset2 = centerOff2;
        this.interp2 = interp2;
        this.cam = cam;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (useSet) {
            Vector3f correctOffset = spatial.getWorldRotation().mult(camOffset1);
            Vector3f correctLookOffset = spatial.getWorldRotation().mult(centerOffset1);
            Vector3f correctPosition = spatial.getWorldTranslation().add(correctOffset);
            if (interp1 < 0.0F) {
                cam.setLocation(correctPosition);
            } else {
                cam.setLocation(cam.getLocation().clone().interpolateLocal(correctPosition, Math.min(interp1 * tpf, 1.0F)));
            }
            cam.lookAt(spatial.getWorldTranslation().add(correctLookOffset), spatial.getWorldRotation().mult(Vector3f.UNIT_Y).normalizeLocal());
        } else {
            Vector3f correctOffset = spatial.getWorldRotation().mult(camOffset2);
            Vector3f correctLookOffset = spatial.getWorldRotation().mult(centerOffset2);
            Vector3f correctPosition = spatial.getWorldTranslation().add(correctOffset);
            if (interp2 < 0.0F) {
                cam.setLocation(correctPosition);
            } else {
                cam.setLocation(cam.getLocation().clone().interpolateLocal(correctPosition, Math.min(interp2 * tpf, 1.0F)));
            }
            cam.lookAt(spatial.getWorldTranslation().add(correctLookOffset), spatial.getWorldRotation().mult(Vector3f.UNIT_Y).normalizeLocal());
        }
    }

    public void swapCam() {
        useSet = !useSet;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}

class ReticleControl extends AbstractControl {

    protected Camera cam;
    protected Node reticle, guiNode;

    public ReticleControl(Camera cam, AssetManager am, Node guiNode) {
        this.cam = cam;
        this.guiNode = guiNode;
        reticle = new Node("Reticle");
        Picture retPic = new Picture("ReticlePicture");
        retPic.setImage(am, "Interface/Reticle.png", true);
        retPic.setPosition(-30.0F, -30.0F);
        retPic.setWidth(60.0F);
        retPic.setHeight(60.0F);
        reticle.attachChild(retPic);
        guiNode.attachChild(reticle);
    }

    @Override
    protected void controlUpdate(float tpf) {
        Vector3f reticlePos = spatial.getWorldTranslation().add(spatial.getWorldRotation().mult(Vector3f.UNIT_X).normalizeLocal().mult(-20.0F));
        Vector3f screenPos = cam.getScreenCoordinates(reticlePos);
        if (screenPos.z < 1.0F) {
            guiNode.attachChild(reticle);
            reticle.setLocalTranslation(screenPos);
        } else {
            reticle.removeFromParent();
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}