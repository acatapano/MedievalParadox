package com.badlogic.prototype.Sprites.Enemies;

import com.badlogic.gdx.Gdx;
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

public class Tank extends com.badlogic.prototype.Sprites.Enemies.Enemy{
    public enum State { ALIVE, DEAD };

    private float stateTimer;
    private TextureAtlas atlas;
    private Animation moveAnimation;
    private Array<TextureAtlas.AtlasRegion> moveFrames;
    private Animation deathAnimation;
    private Array<TextureAtlas.AtlasRegion> deathFrames;
    private boolean setToDestroy;
    private boolean destroyed;
    float angle;

    public State currentState;
    public State previousState;

    public Tank(Level screen, float x, float y, float xAdjust, float yAdjust){
        super(screen,x,y, xAdjust, yAdjust);
        currentState = State.ALIVE;
        previousState = State.ALIVE;

        atlas = new TextureAtlas(Gdx.files.internal("Enemies/Tank/Tank.atlas"));
        moveFrames = atlas.findRegions("move");
        moveAnimation = new Animation(1/8, moveFrames);
        deathFrames = atlas.findRegions("death");
        deathAnimation = new Animation(1/11, deathFrames);

        setScale(1.5f);
        stateTimer = 0;
        setBounds(getX(),getY(),32/Prototype.PPM,32/ Prototype.PPM);
        setToDestroy = false;
        destroyed = false;
        angle = 0;
    }

    public void update(float elapsedTime, float dt){
        stateTimer += dt;
        if(setToDestroy && !destroyed){
            world.destroyBody(b2body);
            destroyed = true;
            stateTimer = 0;
        }
        else if(!destroyed){
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth()/1.3f, b2body.getPosition().y - .1f);
        }

        setRegion(getFrame(elapsedTime, dt));
    }

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

    @Override
    protected void defineEnemy(float xAdjust, float yAdjust){
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() - xAdjust,getY() - yAdjust);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

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

    public void draw(Batch batch){
        if(!destroyed || stateTimer < 1){
            super.draw(batch);
        }
    }

    @Override
    public void hit(Knight knight){
        setToDestroy = true;
        currentState = State.DEAD;
    }

    @Override
    public void hitByEnemy(Enemy enemy){
        reverseVelocity(true,false);
    }
}
