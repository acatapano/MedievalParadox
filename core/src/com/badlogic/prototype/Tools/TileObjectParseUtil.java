package com.badlogic.prototype.Tools;

import static com.badlogic.prototype.Prototype.PPM;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

// implement the collision layer of tile maps
public class TileObjectParseUtil
{
    public static void parseTiledObjLayer(World world, MapObjects mapObjs)
    {
        for (MapObject obj : mapObjs)
        {
            Shape shape;

            if (obj instanceof PolygonMapObject)
            {
                shape = createPolyLine((PolygonMapObject) obj);
            }
            else {
                continue;
            }

            Body body;
            BodyDef bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.StaticBody;
            body = world.createBody(bdef);
            body.createFixture(shape, 1.0f);
            shape.dispose();
        }
    }

    // take each line in the obj layer and chain them together to create objects
    private static ChainShape createPolyLine(PolygonMapObject polyLine)
    {
        float[] vertices = polyLine.getPolygon().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];
        // ^ divide by 2 to avoid counting same vertices twice

        for (int i = 0; i < worldVertices.length; i++)
        {
            worldVertices[i] = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM);
        }

        ChainShape chain = new ChainShape();
        chain.createChain(worldVertices);
        return chain;
    }
}
