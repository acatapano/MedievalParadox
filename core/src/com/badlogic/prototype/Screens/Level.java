package com.badlogic.prototype.Screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.prototype.Scenes.Hud;

// Creates an abstract Level class w/methods that all levels will have.
public abstract class Level implements Screen {
    public Level(){}

    protected abstract void update(float dt);
    public abstract World getWorld();
    public abstract Hud getHud();
    public abstract TiledMap getMap();
}
