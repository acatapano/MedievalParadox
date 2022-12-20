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
import com.badlogic.prototype.Screens.Level2;
import com.badlogic.prototype.Sprites.Enemies.Enemy;
import com.badlogic.prototype.Sprites.Enemies.Tank;
import com.badlogic.prototype.Sprites.TileObjects.Spike;

public class B2WorldCreator2 {
    public Array<Tank> tanks;

    public B2WorldCreator2(Level2 screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        //create body and fixture variables
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //create ground bodies/fixtures
        for (MapObject object : map.getLayers().get(10).getObjects().getByType(RectangleMapObject.class)) {
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

        for (MapObject object : map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)) {
            new Spike(screen, object);
        }

        tanks = new Array<Tank>();
        for (MapObject object : map.getLayers().get(11).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            tanks.add(new Tank(screen, rect.getX() / Prototype.PPM, rect.getY() / Prototype.PPM, 0.3f, -0.1f));
        }

        for (MapObject object : map.getLayers().get(12).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Prototype.PPM, (rect.getY() + rect.getHeight() / 2) / Prototype.PPM);
            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / Prototype.PPM, rect.getHeight() / 2 / Prototype.PPM);
            fdef.filter.categoryBits = Prototype.BARRIER_BIT;
            fdef.filter.maskBits = Prototype.ENEMY_BIT;
            body.createFixture(fdef).setUserData(this);
            // body.createFixture(fdef);
        }

        for (MapObject object : map.getLayers().get(9).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Prototype.PPM, (rect.getY() + rect.getHeight() / 2) / Prototype.PPM);
            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / Prototype.PPM, rect.getHeight() / 2 / Prototype.PPM);
            fdef.filter.categoryBits = Prototype.GOAL_BIT;
            fdef.filter.maskBits = Prototype.KNIGHT_BIT;
            body.createFixture(fdef).setUserData(this);
            // body.createFixture(fdef);
        }

    }

    public Array<Tank> getTanks(){
        return tanks;
    }

    public Array<Enemy> getEnemies(){
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(tanks);
        return enemies;
    }

}