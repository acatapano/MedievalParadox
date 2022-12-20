package com.badlogic.prototype.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.prototype.Prototype;
import com.badlogic.prototype.Screens.Level1;
import com.badlogic.prototype.Sprites.Enemies.Enemy;
import com.badlogic.prototype.Sprites.Enemies.Tank;
import com.badlogic.prototype.Sprites.TileObjects.Spike;

// Creates all box2d bodies off of the tile map for Level1
public class B2WorldCreator {
    // Array of tank enemies.
    public Array<Tank> tanks;

    public B2WorldCreator(Level1 screen){
        // Sets up the world and map.
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        // Create body and fixture variables to be used for all map objects
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        // Creates all ground bodies at index 7 of the tile map layers because that is the ground object layer.
        for(MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Prototype.PPM, (rect.getY() + rect.getHeight() / 2) / Prototype.PPM);
            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / Prototype.PPM, rect.getHeight() / 2 / Prototype.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = Prototype.GROUND_BIT;
            fdef.filter.maskBits = Prototype.ENEMY_BIT |
                    Prototype.KNIGHT_BIT;
            body.createFixture(fdef);
        }

        // Creates all spike bodies at index 8 of the tile map layers because that is the trap object layer.
        for(MapObject object : map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)){
            new Spike(screen, object);
        }

        // Creates all tank bodies at index 9 of the tile map layers because that is the enemy object layer.
        tanks = new Array<Tank>();
        for(MapObject object : map.getLayers().get(9).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            tanks.add(new Tank(screen,rect.getX() / Prototype.PPM, rect.getY() / Prototype.PPM, 2, 0.55f));
        }

        // Creates all barrier bodies at index 10 of the tile map layers because that is the barrier object layer. Used to keep enemies in place.
        for(MapObject object : map.getLayers().get(10).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Prototype.PPM, (rect.getY() + rect.getHeight() / 2) / Prototype.PPM);
            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / Prototype.PPM, rect.getHeight() / 2 / Prototype.PPM);
            fdef.filter.categoryBits = Prototype.BARRIER_BIT;
            fdef.filter.maskBits = Prototype.ENEMY_BIT;
            body.createFixture(fdef).setUserData(this);
        }

        // Creates all goal bodies at index 11 of the tile map layers because that is the goal object layer.
        for(MapObject object : map.getLayers().get(11).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Prototype.PPM, (rect.getY() + rect.getHeight() / 2) / Prototype.PPM);
            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / Prototype.PPM, rect.getHeight() / 2 / Prototype.PPM);
            fdef.filter.categoryBits = Prototype.GOAL_BIT;
            fdef.filter.maskBits = Prototype.KNIGHT_BIT;
            body.createFixture(fdef).setUserData(this);
        }

    }

    // Returns the array of enemies in the world.
    public Array<Enemy> getEnemies(){
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(tanks);
        return enemies;
    }
}
