package com.badlogic.prototype.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.prototype.Prototype;
import com.badlogic.prototype.Scenes.Hud;
import com.badlogic.prototype.Sprites.Enemies.Enemy;
import com.badlogic.prototype.Sprites.Knight;
import com.badlogic.prototype.Tools.B2WorldCreator2;
import com.badlogic.prototype.Tools.WorldContactListener;

public class Level2 extends com.badlogic.prototype.Screens.Level{
    //Reference to Game, used to set Screens
    private Prototype game;

    //basic PlayScreen variables
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;

    //Tiled map variables
    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator2 creator;

    //Sprites
    private Knight player;

    private float elapsedTime;

    public Level2(Prototype game){
        this.game = game;
        //create cam to follow knight through level
        gamecam = new OrthographicCamera();

        //create a FitViewport to maintain virtual aspect ratio despite screen size
        gamePort = new FitViewport(Prototype.V_WIDTH / Prototype.PPM, Prototype.V_HEIGHT / Prototype.PPM, gamecam);

        //create game HUD
        hud = new Hud(game.batch, "2");

        //Load map and setup map renderer
        maploader = new TmxMapLoader();
        map = maploader.load("Level2/level2.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1  / Prototype.PPM);

        //initially set gamcam to be centered correctly at the start of of map
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        //create Box2D world, with no gravity in X, -10 gravity in Y, and allow bodies to sleep
        world = new World(new Vector2(0, -10), true);
        //allows for debug lines of box2d world.
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator2(this);

        //create knight
        player = new Knight(this, 50, 100);
        world.setContactListener(new WorldContactListener());

    }

    @Override
    public void show() { }

    public void handleInput(float dt){
        //control player
        if(player.currentState != Knight.State.DEAD) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.W))
                player.jump();
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2 || Gdx.input.isKeyPressed(Input.Keys.D) && player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2 || Gdx.input.isKeyPressed(Input.Keys.A) && player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
        }
    }

    public void update(float dt){
        //handle user input
        handleInput(dt);

        //takes 1 step in the physics simulation(60 times per second)
        world.step(1 / 60f, 6, 2);

        player.update(elapsedTime, dt);
        for(Enemy enemy : creator.getEnemies()){
            enemy.update(elapsedTime, dt);
        }
        hud.update(dt);

        //attach gamecam to player's x coordinate
        if(player.currentState != Knight.State.DEAD) {
            gamecam.position.x = player.b2body.getPosition().x;
            gamecam.position.y = player.b2body.getPosition().y;
        }

        //update gamecam with correct coordinates
        gamecam.update();
        //tell renderer to draw only what camera can see in game world.
        renderer.setView(gamecam);

    }

    @Override
    public void render(float delta) {
        //separate update logic from render
        update(delta);

        //Clear the game screen with Black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render tile map
        renderer.render();

        //renderer Box2DDebug lines
        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for(Enemy enemy : creator.getEnemies()){
            enemy.draw(game.batch);
        }
        game.batch.end();

        //Set batch to draw what the Hud camera sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(player.getY() < 0){
            player.die();
        }

        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }

        elapsedTime += delta;

        /*if (player.getLevel1Complete()) {
            game.setScreen(new Level3(game));
        }*/

    }

    public boolean gameOver(){
        if(player.currentState == Knight.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height);
    }

    public TiledMap getMap(){
        return map;
    }
    @Override
    public World getWorld(){
        return world;
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

    @Override
    public Hud getHud(){ return hud; }
}
