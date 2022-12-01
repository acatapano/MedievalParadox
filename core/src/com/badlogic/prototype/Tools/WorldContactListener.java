package com.badlogic.prototype.Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.prototype.Prototype;
import com.badlogic.prototype.Sprites.Knight;
import com.badlogic.prototype.Sprites.TileObjects.InteractiveTileObject;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){
            case Prototype.KNIGHT_BIT | Prototype.SPIKE_BIT:
                if(fixA.getFilterData().categoryBits == Prototype.KNIGHT_BIT)
                    ((Knight) fixA.getUserData()).hit((InteractiveTileObject)fixB.getUserData());
                else
                    ((Knight) fixB.getUserData()).hit((InteractiveTileObject)fixA.getUserData());
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
