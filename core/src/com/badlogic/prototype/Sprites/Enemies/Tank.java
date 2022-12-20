package com.badlogic.prototype.Sprites.Enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.prototype.Prototype;
import com.badlogic.prototype.Screens.Level;
import com.badlogic.prototype.Sprites.Knight;

// Creates Tank subclass of Enemy.
public class Tank extends com.badlogic.prototype.Sprites.Enemies.Enemy{
    // Tank States.
    public enum State { ALIVE, DEAD };

    // Sound effects:
    private Sound deathSound;

    // Tank animation and sprite variables.
    private float stateTimer;
    private TextureAtlas atlas;
    private Animation moveAnimation;
    private Array<TextureAtlas.AtlasRegion> moveFrames;
    private Animation deathAnimation;
    private Array<TextureAtlas.AtlasRegion> deathFrames;
    // If the tank is attacked, it will be set to be destroyed.
    private boolean setToDestroy;
    // Tracks if the tanks is destroyed.
    private boolean destroyed;
    float angle;

    // Tracks previous and current states.
    public State currentState;
    public State previousState;

    // Tank constructor.
    public Tank(Level screen, float x, float y, float xAdjust, float yAdjust){
        super(screen,x,y, xAdjust, yAdjust);
        // Default/Starting state is alive.
        currentState = State.ALIVE;
        previousState = State.ALIVE;

        // Set up deathSound
        deathSound = Gdx.audio.newSound(Gdx.files.internal("big-impact-7054.mp3"));

        // Sets up tank atlas and and animation frames.
        atlas = new TextureAtlas(Gdx.files.internal("Enemies/Tank/Tank.atlas"));
        moveFrames = atlas.findRegions("move");
        moveAnimation = new Animation(1/8, moveFrames);
        deathFrames = atlas.findRegions("death");
        deathAnimation = new Animation(1/11, deathFrames);

        // Sets tank scale, bounds, and destroy booleans.
        setScale(1.5f);
        stateTimer = 0;
        setBounds(getX(),getY(),32/Prototype.PPM,32/ Prototype.PPM);
        setToDestroy = false;
        destroyed = false;
        angle = 0;
    }

    // If the tank is set to be destroyed, the tank is destroyed and if the tank isn't destroyed, the tank's velocity and position is updated.
    public void update(float elapsedTime, float dt){
        stateTimer += dt;
        if(setToDestroy && !destroyed){
            deathSound.play();
            world.destroyBody(b2body);
            destroyed = true;
            stateTimer = 0;
        }
        else if(!destroyed){
            b2body.setLinearVelocity(velocity);
            // Centers sprite on box2dbody.
            setPosition(b2body.getPosition().x - getWidth()/1.3f, b2body.getPosition().y - .1f);
        }

        // Sets the current frame to the next frame in the current animation.
        setRegion(getFrame(elapsedTime, dt));
    }

    // Returns the next frame in the current animation. Same as knight's getFrame() function.
    public TextureRegion getFrame(float elapsedTime, float dt){
        TextureRegion region;

        //depending on the state, get corresponding animation keyFrame.
        switch(currentState){
            case DEAD:
                region = ((TextureRegion) deathAnimation.getKeyFrame(elapsedTime, false));
                break;
            case ALIVE:
                region = ((TextureRegion) moveAnimation.getKeyFrame(elapsedTime, true));
                break;
            default:
                region = ((TextureRegion) moveAnimation.getKeyFrame(elapsedTime, true));
                break;
        }

        //if knight is running left and the texture isn't facing left, flip it.
        if((b2body.getLinearVelocity().x > 0) && !region.isFlipX()){
            region.flip(true, false);
        }

        //if knight is running right and the texture isn't facing right, flip it.
        else if((b2body.getLinearVelocity().x < 0) && region.isFlipX()){
            region.flip(true, false);
        }

        //if the current state is the same as the previous state increase the state timer.
        //otherwise the state has changed and we need to reset timer.
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        //update previous state
        previousState = currentState;
        //return our final adjusted frame
        return region;
    }

    // Defines tank's box2d body. Same as knights defineKnight() function.
    @Override
    protected void defineEnemy(float xAdjust, float yAdjust){
        // Sets up tanks, box2d body at its correct position with the x and y adjusts in the world.
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() - xAdjust,getY() - yAdjust);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // Sets up the tank's fixture with it's correct shape and collision filters.
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(15/ Prototype.PPM,8/ Prototype.PPM);
        fdef.filter.categoryBits = Prototype.ENEMY_BIT;
        fdef.filter.maskBits = Prototype.GROUND_BIT |
                Prototype.KNIGHT_BIT |
                Prototype.BARRIER_BIT |
                Prototype.ENEMY_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
    }

    // If the tank isn't destroyed, the tank is drawn in the batch.
    public void draw(Batch batch){
        if(!destroyed || stateTimer < 1){
            super.draw(batch);
        }
    }

    // If the tank is hit by the knight, the tank is set to be destroyed.
    @Override
    public void hit(){
        setToDestroy = true;
        currentState = State.DEAD;
    }

    // If the tank is hit by another enemy, it's velocity if reversed.
    @Override
    public void hitByEnemy(Enemy enemy){
        reverseVelocity(true,false);
    }
}
