package com.badlogic.prototype.Sprites.TileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.prototype.Prototype;
import com.badlogic.prototype.Screens.Level;
import com.badlogic.prototype.Sprites.Knight;

// Creates an abstract InteractiveTileObject for all interactive tile objects that will be generated.
public abstract class InteractiveTileObject {
    // All variables needed to create/set up an InteractiveTileObject.
    protected World world;
    protected TiledMap map;
    protected Rectangle bounds;
    protected Body body;
    protected Level screen;
    protected MapObject object;

    protected Fixture fixture;

    public InteractiveTileObject(Level screen, MapObject object){
        // Initializes all variables.
        this.object = object;
        this.screen = screen;
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.bounds = ((RectangleMapObject) object).getRectangle();

        // Sets up box2d variables for object.
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        // Makes the object a StaticBody that cannot move/be affected by impulses.
        bdef.type = BodyDef.BodyType.StaticBody;
        // Sets the bdef sets to the map object's.
        bdef.position.set((bounds.getX() + bounds.getWidth() / 2) / Prototype.PPM, (bounds.getY() + bounds.getHeight() / 2) / Prototype.PPM);
        // Creates the body in the world.
        body = world.createBody(bdef);

        // Sets the shape of the fdef to tht of the map object's.
        shape.setAsBox(bounds.getWidth() / 2 / Prototype.PPM, bounds.getHeight() / 2 / Prototype.PPM);
        fdef.shape = shape;
        fixture = body.createFixture(fdef);

    }

    // Abstract onHit() function for when the object hits the knight.
    public abstract void onHit(Knight knight);

    // Method for setting the object's filter according to the type of interactive tile object it is.
    public void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

}
