package com.badlogic.prototype.Sprites.TileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.prototype.Prototype;
import com.badlogic.prototype.Screens.Level;
import com.badlogic.prototype.Sprites.Knight;

// Makes Spike a subclass of InteractiveTileObject.
public class Spike extends InteractiveTileObject {
    // Creates a spike with the category of SPIKE_BIT.
    public Spike(Level screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(Prototype.SPIKE_BIT);
    }

    // If knight collides w/spike, kill knight.
    @Override
    public void onHit(Knight knight) {
        knight.die();
    }

}
