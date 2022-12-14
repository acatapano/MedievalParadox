package com.badlogic.prototype.Sprites;

//Right now, I'm essentially going after what the "Knight" class had going on, so whoever wrote that, thnx -Anthony

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.prototype.Prototype;
import com.badlogic.prototype.Screens.Level1;

public class Crawler extends Sprite
{
    //states
    public enum State {ATTACK, CD, DEATH, IDLE, HURT, MOVE}
    public State currentState;
    public State prevState;
    //Box2D
    public World world;
    public Body b2body;
    //atlas and anims
    private TextureAtlas textureAtlas;
    private Animation deathAnim;
    private Animation idleAnim;
    private Animation hurtAnim;
    private Animation moveAnim;
    private Animation cdAnim;
    private Animation attackAnim;
    //frames
    private Array<TextureAtlas.AtlasRegion> deathFrames;
    private Array<TextureAtlas.AtlasRegion> idleFrames;
    private Array<TextureAtlas.AtlasRegion> hurtFrames;
    private Array<TextureAtlas.AtlasRegion> moveFrames;
    private Array<TextureAtlas.AtlasRegion> cdFrames;
    private Array<TextureAtlas.AtlasRegion> attackFrames;
    //other
    private float stateTimer;
    private boolean movingRight;
    private boolean isAction; //checks to see if drone is in the middle of an action
    private Level1 screen; //since we are going to add more levels, is this going to change? if so, change this
    //methods
    public Crawler(Level1 screen)
    {
        //initialization
        this.screen=screen;
        this.world=screen.getWorld();
        currentState= State.IDLE;
        prevState= State.IDLE;
        stateTimer=0;
        movingRight=true;
        //anims and frames
        textureAtlas = new TextureAtlas(Gdx.files.internal("Enemies/Drone2/Drone2.atlas"));
        deathFrames=textureAtlas.findRegions("death");
        deathAnim = new Animation(1/13f,deathFrames);
        idleFrames=textureAtlas.findRegions("idle");
        idleAnim = new Animation(1/4f,idleFrames);
        hurtFrames=textureAtlas.findRegions("hurt");
        hurtAnim = new Animation(1/4f,hurtFrames);
        moveFrames=textureAtlas.findRegions("move");
        moveAnim = new Animation(1/4f,moveFrames);
        cdFrames=textureAtlas.findRegions("cd");
        cdAnim = new Animation(1/4f,cdFrames);
        attackFrames=textureAtlas.findRegions("attack");
        attackAnim = new Animation(1/7f,attackFrames);
        setScale(3f); //following knight, may need to change
        //define Flier in Box2D
        defineFlier();
        //I don't know how setBounds() would work here, but the first frame should prob be the first idle tho
        //setBounds(???);
        setRegion(idleFrames.get(0));
    }
    public void update(float elapsedTime, float dt)
    {
        //setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setPosition(b2body.getPosition().x - getWidth()*1.5f, b2body.getPosition().y - getHeight()/1.1f);

        //update sprite with the correct frame depending on drone's current action
        setRegion(getFrame(elapsedTime, dt));
    }
    //judging by what was written in the knight sprite the death anim might need some tinkering
    public TextureRegion getFrame(float elapsedTime, float dt)
    {
        //obtain current state
        currentState=getState(elapsedTime);
        TextureRegion region;
        //switch case to determine animation state
        switch(currentState)
        {
            case DEATH:
                region = ((TextureRegion) deathAnim.getKeyFrame(elapsedTime, false));
                break;
            case HURT:
                region = ((TextureRegion) hurtAnim.getKeyFrame(elapsedTime, false));
                break;
            case MOVE:
                region = ((TextureRegion) moveAnim.getKeyFrame(elapsedTime, true));
                break;
            case CD:
                region = ((TextureRegion) cdAnim.getKeyFrame(elapsedTime, false));
                break;
            case ATTACK:
                region = ((TextureRegion) attackAnim.getKeyFrame(elapsedTime, false));
                break;
            default:
                region = ((TextureRegion) idleAnim.getKeyFrame(elapsedTime, true));
                break;
        }
        //if drone is moving left and the texture isn't facing left, flip it.
        if((b2body.getLinearVelocity().x < 0 || !movingRight) && !region.isFlipX())
        {
            region.flip(true, false);
            movingRight = false;
        }
        //if drone is moving right and the texture isn't facing right, flip it.
        else if((b2body.getLinearVelocity().x > 0 || movingRight) && region.isFlipX())
        {
            region.flip(true, false);
            movingRight = true;
        }
        //if state same, increment timer; if differ, reset timer
        stateTimer=currentState==prevState?stateTimer+dt:0;
        //update prev state
        prevState=currentState;
        return region;
    }
    public State getState(float elapsedTime)
    {
        if(isAction)
        {
            switch(currentState)
            {
                case HURT:
                    //checks to see if we're on last frame of anim, if so, returns idle so it wont loop or something
                    return hurtAnim.getKeyFrame(elapsedTime)==hurtFrames.get(hurtFrames.size-1)? State.IDLE: State.HURT;
                case ATTACK:
                    return attackAnim.getKeyFrame(elapsedTime)==attackFrames.get(attackFrames.size-1)? State.IDLE: State.ATTACK;
                case CD:
                    return cdAnim.getKeyFrame(elapsedTime)==cdFrames.get(cdFrames.size-1)? State.IDLE: State.CD;
                case DEATH:
                    return deathAnim.getKeyFrame(elapsedTime)==deathFrames.get(deathFrames.size-1)? State.IDLE: State.DEATH;
                    //unsure if mentioned before, how to destroy after death anim ends? hm.
            }
        }
        else if(b2body.getLinearVelocity().x!=0)
        {
            if(IsAction())
                isAction=false;
            return State.MOVE;
        }
        else
        {
            if(IsAction())
                isAction=false;
            return State.IDLE;
        }
    } //griping about missing return statement, but I thought I covered every ground? need to look into
    //HOW TO DESTROY OBJECT ONCE DEAD
    public void die()
    {
        if(!IsAction())
        {
            isAction = true;
            currentState= State.DEATH;
        }
    }
    public void attack()
    {
        if(!IsAction())
        {
            isAction=true;
            currentState= State.ATTACK;
        }
    }
    public void hurt()
    {
        if(!IsAction())
        {
            isAction=true;
            currentState= State.HURT;
        }
    }
    //what the fuck does "cd" mean
    public void cd()
    {
        if(!IsAction())
        {
            isAction=true;
            currentState= State.CD;
        }
    }
    public boolean IsAction()
    {
        return isAction;
    }
    public float getStateTimer()
    {
        return stateTimer;
    }
    public void defineFlier()
    {
        BodyDef def = new BodyDef();
        //setPosition(???) where set? idk
        //def.position.set(???)
        def.type=BodyDef.BodyType.KinematicBody;
        b2body=world.createBody(def);
        FixtureDef fDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(24/Prototype.PPM,24/Prototype.PPM);
        //fDef.filter.categoryBits leaving here for posterity, unsure how you guys want to account for bits in this stuff
        fDef.shape=shape;
        fDef.friction=0; //since movement is dictated, I don't think friction will be an issue
        b2body.createFixture(fDef).setUserData(this);

        b2body.createFixture(fDef).setUserData(this); //is duplication typo?
    }
    public void draw(Batch batch){
        super.draw(batch);
    }
}
