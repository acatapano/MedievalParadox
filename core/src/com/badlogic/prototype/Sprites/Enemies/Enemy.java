package com.badlogic.prototype.Sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.prototype.Screens.Level;
import com.badlogic.prototype.Sprites.Knight;

// Creates abstract class Enemy that is a subclass of Sprite
public abstract class Enemy extends Sprite {
    // Sets up box2d variables.
    protected World world;
    protected Level screen;
    public Body b2body;
    public Vector2 velocity;

    // Enemy constructor with screen and x and y adjustments for enemy creation.
    public Enemy(Level screen, float x, float y, float xAdjust, float yAdjust){
        this.world = screen.getWorld();
        this.screen = screen;
        // Sets enemy position.
        setPosition(x,y);
        // Defines enemy box2d body.
        defineEnemy(xAdjust, yAdjust);
        // Sets velocity of enemy movement.
        velocity = new Vector2(-.7f,-2f);
        // Sets body as active.
        b2body.setActive(true);
    }

    // Abstract methods for all enemies.
    protected abstract void defineEnemy(float xAdjust, float yAdjust);
    public abstract void update(float elapsedTime, float dt);
    public abstract void hit();
    public abstract void hitByEnemy(Enemy enemy);

    // Reverses the enmies velocity so that it moves the opposite direction.
    public void reverseVelocity(boolean x, boolean y){
        if(x){
            velocity.x = -velocity.x;
        }
        if(y){
            velocity.y = -velocity.y;
        }
    }
}
