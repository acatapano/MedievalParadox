package com.badlogic.prototype.Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.prototype.Prototype;
import com.badlogic.prototype.Sprites.Enemies.Enemy;
import com.badlogic.prototype.Sprites.Knight;
import com.badlogic.prototype.Sprites.TileObjects.InteractiveTileObject;

// Contact listener for all contact/collisions in box2d worlds.
public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        // fixA first contact object.
        Fixture fixA = contact.getFixtureA();
        // fixB second contact object.
        Fixture fixB = contact.getFixtureB();

        // Sets cDef to the current bits/types of objects that are colliding.
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){
            // If knight and spike collide, the knight's hit() function is called.
            case Prototype.KNIGHT_BIT | Prototype.SPIKE_BIT:
                if(fixA.getFilterData().categoryBits == Prototype.KNIGHT_BIT)
                    ((Knight) fixA.getUserData()).hit((InteractiveTileObject)fixB.getUserData());
                else
                    ((Knight) fixB.getUserData()).hit((InteractiveTileObject)fixA.getUserData());
                break;
            // If an enemy and the knight collide, the knight's hitEnemy() function is called.
            case Prototype.ENEMY_BIT | Prototype.KNIGHT_BIT:
                if(fixA.getFilterData().categoryBits == Prototype.KNIGHT_BIT)
                    ((Knight) fixA.getUserData()).hitEnemy((Enemy)fixB.getUserData());
                else
                    ((Knight) fixB.getUserData()).hitEnemy((Enemy)fixA.getUserData());
                break;
            // If 2 enemies collide, calls both of their reverseVelocity() functions.
            case Prototype.ENEMY_BIT | Prototype.ENEMY_BIT:
                ((Enemy)fixA.getUserData()).hitByEnemy((Enemy)fixB.getUserData());
                ((Enemy)fixB.getUserData()).hitByEnemy((Enemy)fixA.getUserData());
                break;
            // If enemy collides w/a barrier, the enemy's reverseVelocity() function is called.
            case Prototype.ENEMY_BIT | Prototype.BARRIER_BIT:
                if(fixA.getFilterData().categoryBits == Prototype.ENEMY_BIT)
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                break;
            // If the knight collides w/its goal in the current level the knight's completeLevel() function is called.
            case Prototype.KNIGHT_BIT | Prototype.GOAL_BIT:
                if(fixA.getFilterData().categoryBits == Prototype.KNIGHT_BIT)
                    ((Knight) fixA.getUserData()).completeLevel();
                else
                    ((Knight) fixB.getUserData()).completeLevel();
                break;
            // If the attack box collides with an enemy, the enemy will die.
            case Prototype.ATTACK_BIT | Prototype.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits==Prototype.ATTACK_BIT)
                    ((Enemy) fixB.getUserData()).hit();
                else if(Knight.isViolent())
                    ((Enemy) fixA.getUserData()).hit();
                break;
        }
    }


    @Override
    public void endContact(Contact contact) { }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) { }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) { }
}
