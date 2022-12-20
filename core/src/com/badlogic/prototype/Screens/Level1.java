package com.badlogic.prototype.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.prototype.Prototype;
import com.badlogic.prototype.Scenes.Hud;
import com.badlogic.prototype.Sprites.Enemies.Enemy;
import com.badlogic.prototype.Sprites.Knight;
import com.badlogic.prototype.Tools.B2WorldCreator;
import com.badlogic.prototype.Tools.WorldContactListener;

public class Level1 extends com.badlogic.prototype.Screens.Level // Makes Level1 a subclass of the abstract class Level.
{
    // Reference to Game, used to set Screens
    private Prototype game;

    // Sets up camera, Viewport, and Hud for the level screen.
    private OrthographicCamera gamecam;             // What is displayed.
    private Viewport gamePort;                      // Controls what the tile map renderer and the camera sees.
    private Hud hud;                                // Holds the level's HUD.

    // Tile map variables:
    private TmxMapLoader maploader;                 // Used to load the tilemap.
    private TiledMap map;                           // Used to hold the tile map.
    private OrthogonalTiledMapRenderer renderer;    // Used to render the tile map.

    //Box2d variables:
    private World world;                            // Used to contain all box2d bodies and set up physics.
    private Box2DDebugRenderer b2dr;                // Used to render debug lines.
    private B2WorldCreator creator;                 // Used to create all of the box2d objects on the fro the tile map.

    //Sprites
    private Knight player;                          // Holds the player body.

    private float elapsedTime;                      // Holds the time elapsed since the level was started.
    private int violenceTime=11;                     // when has the knight realized that peace isn't an option? (counts frames)

    Music music;

    public Level1(Prototype game)
    {
        this.game = game;                           // Sets the screen's game = to the current game.
        gamecam = new OrthographicCamera();         // Creates cam to follow knight through level.

        // Creates a FitViewport to maintain virtual aspect ratio despite screen size.
        gamePort = new FitViewport(Prototype.V_WIDTH / Prototype.PPM, Prototype.V_HEIGHT / Prototype.PPM, gamecam);

        // start music
        music = Gdx.audio.newMusic(Gdx.files.internal("2021-02-23_-_Fantasy_Ambience_-_David_Fesliyan.mp3"));
        music.setVolume(0.3f);
        music.setLooping(true);
        music.play();

        // Create game HUD.
        hud = new Hud(game.batch, "1");

        // Loads map and sets up map renderer.
        maploader = new TmxMapLoader();
        map = maploader.load("Level1/level1.tmx"); // Tile map for Level 1 is located at "Level1/level1.tmx"
        renderer = new OrthogonalTiledMapRenderer(map, 1  / Prototype.PPM);

        // Initially sets gamcam to be centered correctly at the start of of map
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        // Create Box2D world, with no gravity in X, -10 gravity in Y, and allow bodies to sleep.
        world = new World(new Vector2(0, -10), true);
        // Allows for debug lines of box2d world.
        b2dr = new Box2DDebugRenderer();

        // Creates the B2WorldCreator which creates all of the physics bodies off of the tile map's object layers.
        creator = new B2WorldCreator(this);

        // Creates knight with a starting position of (50, 100)
        player = new Knight(this, 50, 100);

        // Sets the world's contact listener to the new listener to dictate all world collisions.
        world.setContactListener(new WorldContactListener());

    }

    @Override
    public void show() { }

    public void handleInput(float dt){ // Makes knight react off player input.
        //control player
        if(player.currentState != Knight.State.DEAD) {
            // If the player presses the spacebar, "W", or the up arrow key, the knight will jump.
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.W))
                player.jump();
            // If the player presses the right arrow key or "D" and the knight's current velocity is <= 2 the knight will move right by applying a positive velocity in the x direction to its body.
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2 || Gdx.input.isKeyPressed(Input.Keys.D) && player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            // If the player presses the left arrow key or "A" and the knight's current velocity is <= -2 the knight will move right by applying a negative velocity in the x direction to its body.
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2 || Gdx.input.isKeyPressed(Input.Keys.A) && player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
            if(Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
                player.beginAttack();
                violenceTime=0;
            }
        }
    }

    public void update(float dt){
        // Handles user input.
        handleInput(dt);

        // Takes 1 step in the physics simulation (60 times per second).
        world.step(1 / 60f, 6, 2);

        // Updates the player.
        player.update(elapsedTime, dt);
        if(player.isViolent() && violenceTime<=10)
        {
            violenceTime+=1;
        }
        else if(player.isViolent() && violenceTime>10)
        {
            player.endAttack();
        }
        // Updates each enemy in the level.
        for(Enemy enemy : creator.getEnemies()){
            enemy.update(elapsedTime, dt);
        }
        // Updates the HUD.
        hud.update(dt);

        // Attaches gamecam to player's x and y coordinates.
        if(player.currentState != Knight.State.DEAD) {
            gamecam.position.x = player.b2body.getPosition().x;
            gamecam.position.y = player.b2body.getPosition().y;
        }

        // Updates gamecam with the new coordinates.
        gamecam.update();
        // Tells the tile map renderer to draw only what camera can see in game world.
        renderer.setView(gamecam);

    }

    @Override
    public void render(float delta) {
        // Updates everything that has to be updated every frame.
        update(delta);

        // Clears the game screen with Black.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Renders the tile map.
        renderer.render();

        // Renders Box2DDebug lines.
        b2dr.render(world, gamecam.combined);

        // Sets the batch's projection matrix to the gamecam.combined.
        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin(); // Begins the batch.
        // Draws the player and all enemies in the level in the batch.
        player.draw(game.batch);
        for(Enemy enemy : creator.getEnemies()){
            enemy.draw(game.batch);
        }
        game.batch.end();   // Ends the batch.

        // Sets batch to draw what the Hud camera sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw(); // Draws the Hud on the screen.

        // Kills the player if they fall off the map.
        if(player.getY() < 0){
            player.die();
        }

        // Sends the player to the Game Over Screen if the knight dies.
        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }

        // If the level is completed, the game progresses to the next screen/level.
        if (player.getLevelComplete()) {
            game.setScreen(new Level2(game));
        }

        // Increments elapsedTime every frame.
        elapsedTime += delta;

    }

    // If the knight is dead for more than 3 seconds, sends the player to the Game Over screen when called.
    public boolean gameOver(){
        if(player.currentState == Knight.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
        return false;
    }

    // Resizes the screen if the window is expanded or made smaller.
    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height);
    }

    // Returns the tile map.
    @Override
    public TiledMap getMap(){
        return map;
    }

    // Returns the world.
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

    // Disposes of everything after the level is closed.
    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
        music.dispose();
    }

    // Returns the HUD.
    @Override
    public Hud getHud(){ return hud; }
}