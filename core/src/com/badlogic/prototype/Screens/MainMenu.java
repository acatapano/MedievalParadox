
package com.badlogic.prototype.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.prototype.Prototype;

// Sets up MainMenu
public class MainMenu implements Screen
{
    // Sets up game and camera.
    Prototype game;
    OrthographicCamera camera;

    Texture backgroundTex;
    Sprite backgroundSprite;

    // Creates music.
    Music music;

    public MainMenu(Prototype game)
    {
        this.game = game;

        // Sets up music.
        this.music = Gdx.audio.newMusic(Gdx.files.internal("2019-06-14_-_Warm_Light_-_David_Fesliyan.mp3"));
        this.music.setVolume(0.3f);
        this.music.setLooping(true);
        this.music.play();

        init();
    }

    public MainMenu(Prototype game, Music music)
    {
        this.game = game;
        this.music = music;
        init();
    }

    private void init()
    {
        // Sets up background image.
        backgroundTex = new Texture(Gdx.files.internal("mainmenu_bg.png"));
        backgroundSprite = new Sprite(backgroundTex);
        backgroundSprite.setSize(640, 500);
        backgroundSprite.setPosition(0, 0);

        // Sets up font.
        game.font.getData().setScale(2.0f);

        // Sets up game cam.
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    // Renders background image and text.
    @Override
    public void render(float delta)
    {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // batch
        game.batch.begin();

        backgroundSprite.draw(game.batch);
        game.font.draw(game.batch, "MEDIEVAL PARADOX", 55, 430);
        game.font.draw(game.batch, "Click or tap anywhere to begin", 55, 380);

        game.batch.end();
        // end batch

        // If the player clicks, the screen is set to the StoryMenu
        if (Gdx.input.justTouched())
        {
            game.setScreen(new StoryMenu(game, music));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    // Disposes of background image and music.
    @Override
    public void dispose() {
        backgroundTex.dispose();
        //music.dispose();
    }
}
