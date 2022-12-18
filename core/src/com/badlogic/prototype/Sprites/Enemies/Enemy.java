package com.badlogic.prototype.Sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.prototype.Screens.Level1;
import com.badlogic.prototype.Sprites.Knight;

public abstract class Enemy extends Sprite {
    protected World world;
    protected Level1 screen;
    public Body b2body;
    public Vector2 velocity;

    public Enemy(Level1 screen, float x, float y){
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x,y);
        defineEnemy();
        velocity = new Vector2(-1,-2);
        b2body.setActive(true);
    }

    protected abstract void defineEnemy();
    public abstract void update(float elapsedTime, float dt);
    public abstract void hit(Knight knight);
    public abstract void hitByEnemy(Enemy enemy);

    public void reverseVelocity(boolean x, boolean y){
        if(x){
            velocity.x = -velocity.x;
        }
        if(y){
            velocity.y = -velocity.y;
        }
    }
}
