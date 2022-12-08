package com.badlogic.prototype.Sprites.TileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.prototype.Prototype;
import com.badlogic.prototype.Screens.Level1;
import com.badlogic.prototype.Sprites.Knight;

public class Spike extends InteractiveTileObject {
    public Spike(Level1 screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(Prototype.SPIKE_BIT);
    }

    @Override
    public void onHit(Knight knight) {
        knight.die();
    }

}
