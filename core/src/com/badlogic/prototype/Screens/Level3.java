
package com.badlogic.prototype.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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
import com.badlogic.prototype.Tools.B2WorldCreator3;
import com.badlogic.prototype.Tools.WorldContactListener;

// Refer to Level1 for comments. Code is the same besides some minor details that don't change the functionality of the code.
public class Level3 extends com.badlogic.prototype.Screens.Level implements Screen
{
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
    private B2WorldCreator3 creator;

    //Sprites
    private Knight player;

    private Music music;

    // Time variables.
    private float elapsedTime;
    private float violenceTime=11;

    public Level3(Prototype game)
    {
        this.game = game;
        //create cam to follow knight through level
        gamecam = new OrthographicCamera();

        //create a FitViewport to maintain virtual aspect ratio despite screen size
        gamePort = new FitViewport(Prototype.V_WIDTH / Prototype.PPM, Prototype.V_HEIGHT / Prototype.PPM, gamecam);

        // start music
        music = Gdx.audio.newMusic(Gdx.files.internal("2017-06-16_-_The_Dark_Castle_-_David_Fesliyan.mp3"));
        music.setVolume(0.3f);
        music.setLooping(true);
        music.play();

        //create game HUD
        hud = new Hud(game.batch, "3");

        //Load tile map and setup map renderer
        maploader = new TmxMapLoader();
        map = maploader.load("Level3/level3.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1  / Prototype.PPM);

        //initially set gamcam to be centered correctly at the start of of map
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        //create Box2D world, with no gravity in X, -10 gravity in Y, and allow bodies to sleep
        world = new World(new Vector2(0, -10), true);
        //allows for debug lines of box2d world.
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator3(this);

        //create knight
        player = new Knight(this, 50, 100);
        // Sets up the world's contact listener.
        world.setContactListener(new WorldContactListener());
    }

    @Override
    public void show() { }

    // Handles player input.
    public void handleInput(float dt){
        // If the knight isn't dead allow movements.
        if(player.currentState != Knight.State.DEAD) {
            // If the player presses up, space, or w, make the knight jump.
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.W))
                player.jump();
            // If the player presses the right arrow key or A, make the knight run right by applying a positive linear velocity (max 2).
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2 || Gdx.input.isKeyPressed(Input.Keys.D) && player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            // If the player presses the left arrow key or D, make the knight left right by applying a negative linear velocity (max -2).
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2 || Gdx.input.isKeyPressed(Input.Keys.A) && player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
            // If the player presses z or left control, make the knight attack. Linear velocity is set to 0 to ensure no movement occurs while attacking.
            if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.Z)) {
                player.beginAttack();
                player.b2body.setLinearVelocity(0,0);
                violenceTime = 0;
            }
        }
    }

    public void update(float dt){
        //handle user input
        handleInput(dt);

        //takes 1 step in the physics simulation(60 times per second)
        world.step(1 / 60f, 6, 2);

        // Updates player.
        player.update(elapsedTime, dt);
        // Controls attack timing.
        if(player.isViolent() && violenceTime<=10)
        {
            violenceTime+=1;
        }
        else if(player.isViolent() && violenceTime>10)
        {
            player.endAttack();
        }
        // Updates all enemies on the map.
        for(Enemy enemy : creator.getEnemies()){
            enemy.update(elapsedTime, dt);
        }
        // Updates the Hud
        hud.update(dt);

        //attach gamecam to player's x and y coordinates
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
        // Updtae everything that has to update every frame.
        update(delta);

        //Clear the game screen with Black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render tile map
        renderer.render();

        //renderer Box2DDebug lines
        // b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        // Draw knight and all enemies in the batch.
        game.batch.begin();
        player.draw(game.batch);
        for(Enemy enemy : creator.getEnemies()){
            enemy.draw(game.batch);
        }
        game.batch.end();

        //Set batch to draw the Hud/what the Hud camera sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        // If the player falls off the map, kill them.
        if(player.getY() < 0)
        {
            player.die();
        }

        // If the game is over (player died) send player to game over screen.
        if(gameOver())
        {
            music.stop();
            game.setScreen(new GameOverScreen3(game));
            dispose();
        }

        elapsedTime += delta;

        if (player.getLevelComplete())
        {
            music.stop();
            game.setScreen(new Credits(game));
            dispose();
        }

    }

    public boolean gameOver()
    {
        if(player.currentState == Knight.State.DEAD && player.getStateTimer() > 3)
            return true;

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
        //music.dispose();
    }

    @Override
    public Hud getHud(){ return hud; }
}
