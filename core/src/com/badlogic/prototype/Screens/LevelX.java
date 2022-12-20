
package com.badlogic.prototype.Screens;

import static com.badlogic.prototype.Prototype.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.prototype.Prototype;
import com.badlogic.prototype.Scenes.Hud;
import com.badlogic.prototype.Sprites.Enemies.Enemy;
import com.badlogic.prototype.Sprites.Knight;
import com.badlogic.prototype.Tools.B2WorldCreator;
import com.badlogic.prototype.Tools.TileObjectParseUtil;
import com.badlogic.prototype.Tools.WorldContactListener;

public class LevelX extends com.badlogic.prototype.Screens.Level implements Screen
{
    private Prototype game;

    private boolean debugOn = true;

    private OrthographicCamera camera;
    private final float SCALE = 2.0f;

    private Hud hud;

    // tile map stuff
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;

    // sprites
    private Knight player;

    private float elapsedTime;

    public LevelX(Prototype game)
    {
        this.game = game;

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, width/SCALE, height/SCALE);

        //create Box2D world with no gravity in X, -10 gravity in Y
        world = new World(new Vector2(0f, -9.8f), false);
        b2dr = new Box2DDebugRenderer(); // debug renderer

        // load tile map and renderer
        map = new TmxMapLoader().load("LevelX/LevelX.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        // load the tile map collision layer
        TileObjectParseUtil.parseTiledObjLayer(world, map.getLayers().get("Collision Layer").getObjects());

        // draw Box2D debug lines
        if (debugOn)
        {
            b2dr.render(world, camera.combined);
        }

        //create game HUD
        hud = new Hud(game.batch, "3");

        // create player knight
        player = new Knight(this);



        //initially set gamcam to be centered correctly at the start of of map
        //camera.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        //creator = new B2WorldCreator(this);

        world.setContactListener(new WorldContactListener());
    }

    public void update(float dt)
    {
        // set tile map view
        renderer.setView(camera);

        // 1 step in the physics simulation (60 per second)
        world.step(1 / 60f, 6, 2);

        // enable player to control their character
        playerControls();
        player.update(elapsedTime, dt);

        // attach camera to player's x coordinate
        if (player.currentState != Knight.State.DEAD)
        {
            //camera.position.x = player.getX();
        }

        //update camera with correct coordinates
        camera.update();
        hud.update(dt);

        //tell renderer to draw only what camera can see in game world.
        renderer.setView(camera);

        game.batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public Hud getHud() {
        return hud;
    }

    @Override
    public TiledMap getMap() {
        return map;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta)
    {
        update(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClearColor(0f, 0f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        player.draw(game.batch);
        game.batch.end();

        // render tile map
        renderer.render();

        b2dr.render(world, camera.combined.scl(PPM));

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
        {
            debugOn = !debugOn; // TODO: replace with a pause screen
        }

        /// player mechanics ///

        // if player falls to bottom of screen, kill them
        if (player.getY() < 0)
        {
            player.die();
        }

        if(gameOver())
        {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }

        if (player.getLevelComplete())
        {
            //game.setScreen(new Win(game)); // TODO: Create Win Screen
        }

        /// end player mechanics ///

        elapsedTime += delta;
    }

    @Override
    public void resize(int width, int height)
    {
        camera.setToOrtho(false, width/SCALE, height/SCALE);
    }

    // player controls
    public void playerControls()
    {
        if (player.currentState != Knight.State.DEAD)
        {
            // walk left
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2 || Gdx.input.isKeyPressed(Input.Keys.A) && player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);

            // walk right
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2 || Gdx.input.isKeyPressed(Input.Keys.D) && player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);

            // jump
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.W))
                player.jump();
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    public boolean gameOver()
    {
        if(player.currentState == Knight.State.DEAD && player.getStateTimer() > 3)
            return true;

        return false;
    }

    @Override
    public void dispose()
    {
        world.dispose();
        b2dr.dispose();
        renderer.dispose();
        map.dispose();
        hud.dispose();
    }
}
