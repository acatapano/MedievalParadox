package com.badlogic.prototype.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.prototype.Prototype;
import com.badlogic.prototype.Screens.Level;
import com.badlogic.prototype.Sprites.Enemies.Enemy;
import com.badlogic.prototype.Sprites.TileObjects.InteractiveTileObject;

// Makes Knight a subclass of sprite.
public class Knight extends Sprite {
    // Sets up the knight's states
    public enum State { FALLING, JUMPING, STANDING, RUNNING, DEAD, ATTACK };
    public State currentState;      // Knight's current state.
    public State previousState;     // Knight's previous state.

    // Box2D
    public World world;     // The world the knight will be in.
    public Body b2body;     // Knights box2d body.

    // Atlas and Animations
    private TextureAtlas textureAtlas;  // Creates knight's atlas for all frames.
    // Creates all animations the knight has.
    private Animation idleAnimation;
    private Animation jumpingAnimation;
    private Animation fallingAnimation;
    private Animation dyingAnimation;
    private Animation attackAnimation;
    private Animation runningAnimation;

    // Frames for all animations.
    private Array<TextureAtlas.AtlasRegion> idleFrames;
    private Array<TextureAtlas.AtlasRegion> jumpingFrames;
    private Array<TextureAtlas.AtlasRegion> fallingFrames;
    private Array<TextureAtlas.AtlasRegion> dyingFrames;
    private Array<TextureAtlas.AtlasRegion> attackingFrames;
    private Array<TextureAtlas.AtlasRegion> runningFrames;

    private float stateTimer;       // Tracks how long the knight has been in the current state.
    private boolean runningRight;   // Tracks if the knight is running right.
    private boolean knightIsDead;   // Tracks if the knight is dead.
    private Level screen;           // Holds the level the knight is on.

    boolean levelComplete;          // Tracks if the knight has completed the current level.

    // Knight constructor w/level and starting coordinates.
    public Knight(Level screen, float startingX, float startingY){
        // Initializes default values
        this.screen = screen;               // Sets screen = to the current level.
        this.world = screen.getWorld();     // Sets the knight's world to the current level's world.
        currentState = State.STANDING;      // Sets default current state to STANDING aka IDLE
        previousState = State.STANDING;     // Sets default previous state to STANDING aka IDLE
        stateTimer = 0;                     // Sets state timer to 0.
        runningRight = true;                // Sets running right to true because the knight is initially facing right.
        levelComplete = false;              // Sets levelComplete to false because the current level has not been completed.

        // Sets up atalas and all animations according to their frame labels in the atlas and how many frames for each animation there are.
        textureAtlas = new TextureAtlas(Gdx.files.internal("player/blue1.atlas"));
        idleFrames = textureAtlas.findRegions("idle");
        idleAnimation = new Animation(1/10f, idleFrames);
        jumpingFrames = textureAtlas.findRegions("jumpflyup");
        jumpingAnimation = new Animation(1/4f, jumpingFrames);
        fallingFrames = textureAtlas.findRegions("fall");
        fallingAnimation = new Animation(1/4f,fallingFrames);
        dyingFrames = textureAtlas.findRegions("death");
        dyingAnimation = new Animation(1/10f, dyingFrames);
        attackingFrames = textureAtlas.findRegions("attack");
        attackAnimation = new Animation(1/10f, attackingFrames);
        runningFrames = textureAtlas.findRegions("run");
        runningAnimation = new Animation(1/10f, runningFrames);

        // Sets the knights scale to 3x so he is bigger.
        setScale(3f);

        // Defines Knight in Box2d
        defineKnight(startingX, startingY);

        // Sets initial values for knight's location, width, and height. And initial frame as the first idle frame.
        setBounds(0, 0, 16 / Prototype.PPM, 16 / Prototype.PPM);
        setRegion(idleFrames.get(0));

    }

    public void update(float elapsedTime, float dt){

        // If the time is up on the HUD, the knight is killed.
        if (screen.getHud().isTimeUp() && !isDead()) {
            die();
        }

        // Updates the knight's sprite position to be centered in it's body every frame.
        setPosition(b2body.getPosition().x - getWidth()*1.5f, b2body.getPosition().y - getHeight()/1.1f);

        // Updates sprite with the correct frame depending on knight's current state/action.
        setRegion(getFrame(elapsedTime, dt));
    }

    // TODO: FIX DEATH ANIMATION
    public TextureRegion getFrame(float elapsedTime, float dt){
        // Get knight's current state.
        currentState = getState();

        // Creates a TextureRegion variable to hold the knight's determined next frame.
        TextureRegion region;

        // Depending on the state, get corresponding animation keyFrame.
        switch(currentState){
            case DEAD:
                region = ((TextureRegion) dyingAnimation.getKeyFrame(elapsedTime, true));
                break;
            case JUMPING:
                region = ((TextureRegion)jumpingAnimation.getKeyFrame(elapsedTime, true));
                break;
            case RUNNING:
                region = ((TextureRegion) runningAnimation.getKeyFrame(elapsedTime, true));
                break;
            case FALLING:
                region = ((TextureRegion) fallingAnimation.getKeyFrame(elapsedTime, true));
                break;
            default:
                region = ((TextureRegion) idleAnimation.getKeyFrame(elapsedTime, true));
                break;
        }

        // If knight is running left and the texture isn't facing left, flip it.
        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }

        // If knight is running right and the texture isn't facing right, flip it.
        else if((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }

        // If the current state is the same as the previous state increase the state timer.
        // Otherwise the state has changed and the timer is reset.
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        // Updates previous state.
        previousState = currentState;
        // Returns final adjusted frame.
        return region;
    }

    public State getState(){
        // If knight is dead the knight is dead.
        if(knightIsDead)
            return State.DEAD;
        // If knight is going positive in Y-Axis he is jumping.
        else if((b2body.getLinearVelocity().y > 0 && currentState == State.JUMPING) || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        // If knight is going negative in Y-Axis knight is falling
        else if(b2body.getLinearVelocity().y < 0 && b2body.getLinearVelocity().y != 0)
            return State.FALLING;
        // If knight is going positive or negative in the X axis he is running
        else if(b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        // If none of these return then he is idle
        else
            return State.STANDING;
    }

    // Kills the knight if he isn't already dead.
    public void die() {
        if (!isDead()) {
            knightIsDead = true;
        }
    }

    // Return whether the knight is dead.
    public boolean isDead(){
        return knightIsDead;
    }

    // Returns the state timer.
    public float getStateTimer(){
        return stateTimer;
    }

    // Makes knight jump by applying a linear impulse up the y axis if the knight isn't already jumping.
    public void jump(){
        if ( currentState != State.JUMPING ) {
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            currentState = State.JUMPING;
        }
    }

    // Defines all of the knight's box2d attributes.
    public void defineKnight(float startingX, float startingY){
        // Creates new BodyDef for the knight and sets its sprite starting position to the statring x and y / the PPM.
        BodyDef bdef = new BodyDef();
        setPosition(startingX/ Prototype.PPM,startingY/ Prototype.PPM);
        // Sets the BodyDef's position to be centered on the knight in the sprite.
        bdef.position.set(getX() + getWidth() / 2, getY() + getHeight() / 2);
        bdef.type = BodyDef.BodyType.DynamicBody;   // Makes the body into a dynamic body that can be affected by gravity and velocities in the x and y directions.
        b2body = world.createBody(bdef);            // Creates the body in the world.

        FixtureDef fdef = new FixtureDef();         // Creates FixtureDef for body that will hold all of it's physics attributes.
        PolygonShape shape = new PolygonShape();    // Creates the shape of the fdef.
        shape.setAsBox(7/ Prototype.PPM,13/ Prototype.PPM); // Sets the shape to a box around the knight in the sprite.
        fdef.filter.categoryBits = Prototype.KNIGHT_BIT;           // Sets the fixture's category to KNIGHT_BIT for contact/collision detection.
        fdef.filter.maskBits = Prototype.GROUND_BIT |              // Sets up all of the other bits/objects the knight can bump into.
                Prototype.SPIKE_BIT |
                Prototype.GOAL_BIT |
                Prototype.ENEMY_BIT;
        fdef.shape = shape; // Sets the fdef's shape to the box.
        fdef.friction = .5f; // Sets the knight's friction so that it doesn't slide as much.
        b2body.createFixture(fdef).setUserData(this); // Sets the body's user data to that of the fdef.
    }

    // Draws the knight in batch.
    public void draw(Batch batch){
        super.draw(batch);
    }

    // If the knight is hit by a spike, he dies.
    public void hit(InteractiveTileObject spike){
        die();
    }

    // If the knight is hit by an enemy, he dies.
    public void hitEnemy(Enemy enemy) { die(); }

    // If the knight completes the current level, set levelComplete to true when called.
    public void completeLevel() { levelComplete = true; }

    // Returns if the current level is complete.
    public boolean getLevelComplete() { return levelComplete; }

    // ???
    public void attack()
    {

    }
}
