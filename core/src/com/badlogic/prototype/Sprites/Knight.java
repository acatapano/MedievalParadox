package com.badlogic.prototype.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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

public class Knight extends Sprite {
    // States
    public enum State { FALLING, JUMPING, STANDING, WALKING, RUNNING, DEAD, ATTACK };
    public State currentState;
    public State previousState;

    // Sound effects:
    private Sound jumpSound;
    private Sound dieSound;
    private Sound attackSound;

    // Box2D
    public World world;
    public Body b2body;
    public Body attack;

    // Atlas and Animations
    private TextureAtlas textureAtlas;
    private Animation idleAnimation;
    private Animation jumpingAnimation;
    private Animation fallingAnimation;
    private Animation dyingAnimation;
    private Animation attackAnimation;
    private Animation runningAnimation;
    private Animation walkingAnimation;

    // Frames
    private Array<TextureAtlas.AtlasRegion> idleFrames;
    private Array<TextureAtlas.AtlasRegion> jumpingFrames;
    private Array<TextureAtlas.AtlasRegion> fallingFrames;
    private Array<TextureAtlas.AtlasRegion> dyingFrames;
    private Array<TextureAtlas.AtlasRegion> attackingFrames;
    private Array<TextureAtlas.AtlasRegion> runningFrames;
    private Array<TextureAtlas.AtlasRegion> walkingFrames;

    private float stateTimer;
    private boolean runningRight;
    private boolean knightIsDead;
    public static boolean attacking;
    private Level screen;

    boolean levelComplete;

    public Knight(Level screen, float startingX, float startingY){
        //initialize default values
        this.screen = screen;
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;
        attacking=false;
        levelComplete = false;

        // Set up sound effects.
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("cartoon-jump-6462.mp3"));
        dieSound = Gdx.audio.newSound(Gdx.files.internal("male_hurt7-48124.mp3"));
        attackSound = Gdx.audio.newSound(Gdx.files.internal("swinging-staff-whoosh-strong-08-44658.mp3"));

        // Sets up all player animations and frames using the atlas labels.
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
        walkingFrames = textureAtlas.findRegions("walk");
        walkingAnimation = new Animation(1/10f, walkingFrames); //f was missing here, fixed

        // Sets knight scale to 3x
        setScale(3f);

        //define Knight in Box2d
        defineKnight(startingX, startingY);

        attack.setActive(false);

        //set initial values for knight's location, width, and height. And initial frame as the first idle frame.
        setBounds(0, 0, 16 / Prototype.PPM, 16 / Prototype.PPM);
        setRegion(idleFrames.get(0));

    }

    public void update(float elapsedTime, float dt){

        // time is up : too late knight dies
        // the !isDead() method is used to check if the knight is already dead
        if (screen.getHud().isTimeUp() && !isDead()) {
            die();
        }

        //setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setPosition(b2body.getPosition().x - getWidth()*1.5f, b2body.getPosition().y - getHeight()/1.1f);

        //attack box thing
        if(runningRight)
        {
            attack.setTransform(new Vector2(b2body.getPosition().x+(10/Prototype.PPM),b2body.getPosition().y),0f);
        }
        else
        {
            attack.setTransform(new Vector2(b2body.getPosition().x-(10/Prototype.PPM),b2body.getPosition().y),0f);
        }

        //update sprite with the correct frame depending on knight's current action
        setRegion(getFrame(elapsedTime, dt));
    }

    public TextureRegion getFrame(float elapsedTime, float dt){
        //get knight's current state. ie. jumping, running, standing...
        currentState = getState();

        TextureRegion region;

        //depending on the state, get corresponding animation keyFrame.
        switch(currentState){
            case DEAD:
                region = ((TextureRegion) dyingAnimation.getKeyFrame(elapsedTime, false));
                break;
            case ATTACK:
                region=((TextureRegion)attackAnimation.getKeyFrame(elapsedTime,true));
                break;
            case JUMPING:
                region = ((TextureRegion)jumpingAnimation.getKeyFrame(elapsedTime, true));
                break;
            case RUNNING:
                region = ((TextureRegion) runningAnimation.getKeyFrame(elapsedTime, true));
                break;
            case WALKING:
                region = ((TextureRegion) walkingAnimation.getKeyFrame(elapsedTime, true));
                break;
            case FALLING:
                region = ((TextureRegion) fallingAnimation.getKeyFrame(elapsedTime, true));
                break;
            default:
                region = ((TextureRegion) idleAnimation.getKeyFrame(elapsedTime, true));
                break;
        }

        //if knight is running left and the texture isn't facing left, flip it.
        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }

        //if knight is running right and the texture isn't facing right, flip it.
        else if((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }

        //if the current state is the same as the previous state increase the state timer.
        //otherwise the state has changed and we need to reset timer.
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        //update previous state
        previousState = currentState;
        //return our final adjusted frame
        return region;
    }

    public State getState(){
        //Test to Box2D for velocity on the X and Y-Axis
        //if knight is going positive in Y-Axis he is jumping... or if he just jumped and is falling remain in jump state
        if(knightIsDead)
            return State.DEAD;
        else if(attacking)
            return State.ATTACK;
        else if((b2body.getLinearVelocity().y > 0 && currentState == State.JUMPING) || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        //if negative in Y-Axis knight is falling
        else if(b2body.getLinearVelocity().y < 0 && b2body.getLinearVelocity().y != 0)
            return State.FALLING;
        //if knight is positive or negative in the X axis he is running
        else if(b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        //if none of these return then he must be standing
        else
            return State.STANDING;
    }

    public void die() {
        if (!isDead()) {
            knightIsDead = true;
            dieSound.play();
        }
    }

    public boolean isDead(){
        return knightIsDead;
    }

    public float getStateTimer(){
        return stateTimer;
    }

    public void jump(){
        if ( currentState != State.JUMPING ) {
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            currentState = State.JUMPING;
            jumpSound.play();
        }
    }

    public void defineKnight(float startingX, float startingY){
        // Creates knight body.
        BodyDef bdef = new BodyDef();
        setPosition(startingX/ Prototype.PPM,startingY/ Prototype.PPM);
        bdef.position.set(getX() + getWidth() / 2, getY() + getHeight() / 2);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // Creates attack body.
        BodyDef adef = new BodyDef();
        adef.position.set(getX()+getWidth()+(10/Prototype.PPM),getY()+getHeight()/2);
        adef.type=BodyDef.BodyType.KinematicBody;
        attack=world.createBody(adef);

        // Creates knight fixture with contact filters.
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(7/ Prototype.PPM,13/ Prototype.PPM);
        fdef.filter.categoryBits = Prototype.KNIGHT_BIT;
        fdef.filter.maskBits = Prototype.GROUND_BIT |
                Prototype.SPIKE_BIT |
                Prototype.GOAL_BIT |
                Prototype.ENEMY_BIT;
        fdef.shape = shape;
        fdef.friction = .5f;
        b2body.createFixture(fdef).setUserData(this);

        // Creates attack fixture.
        FixtureDef afdef = new FixtureDef();
        afdef.filter.categoryBits=Prototype.ATTACK_BIT;
        afdef.filter.maskBits=Prototype.ENEMY_BIT;
        afdef.isSensor=true;
        afdef.shape=shape;
        afdef.friction=0f;
        afdef.density=0f;
        attack.createFixture(afdef);
        shape.dispose();
    }

    // Draws the knight in the batch.
    public void draw(Batch batch){
        super.draw(batch);
    }

    // If the player hits a spike, they die.
    public void hit(InteractiveTileObject spike){
        die();
    }

    // If the player hits an enemy, they die.
    public void hitEnemy(Enemy enemy) { die(); }

    // If the player completes the level it set levelComplete to true
    public void completeLevel() { levelComplete = true; }

    // Returns if the level is complete or not.
    public boolean getLevelComplete() { return levelComplete; }

    // Starts attack and sets attack body to active. Also plays attack sound once.
    public void beginAttack()
    {
        attacking=true;
        attack.setActive(true);
        if(previousState != State.ATTACK)
        {
            attackSound.play();
        }
    }
    // Ends attack and sets attack body to inactive.
    public void endAttack()
    {
        attacking=false;
        attack.setActive(false);
    }
    // Tracks if the player is attacking.
    public static boolean isViolent()
    {
        return attacking;
    }
}
