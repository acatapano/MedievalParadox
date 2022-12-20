package com.badlogic.prototype.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.prototype.Prototype;

// Refer to MainMenu comments. No significant changes in code.
public class Credits implements Screen {
    final Prototype game;
    OrthographicCamera camera;

    Texture backgroundTex;
    Sprite backgroundSprite;

    private Music music;

    public Credits(Prototype game)
    {
        this.game = game;

        // start music
        music = Gdx.audio.newMusic(Gdx.files.internal("2020-02-22_-_Relaxing_Green_Nature_-_David_Fesliyan.mp3"));
        music.setVolume(0.3f);
        music.setLooping(true);
        music.play();

        backgroundTex = new Texture(Gdx.files.internal("story_controls.png"));
        backgroundSprite = new Sprite(backgroundTex);
        backgroundSprite.setSize(640, 500);
        backgroundSprite.setPosition(0, 0);

        game.font.getData().setScale(2.0f);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // batch
        game.batch.begin();

        backgroundSprite.draw(game.batch);

        // Header
        game.font.draw(game.batch, "Congratulations! You saved the King!", 55, 460);

        // Story info
        game.font.getData().setScale(1.5f);
        game.font.draw(game.batch, "CREDITS", 55, 425);
        game.font.draw(game.batch, "Assets:", 55, 400);
        game.font.draw(game.batch, "Knight: Generic Character Asset v 0.2 - brullov", 55, 375);
        game.font.draw(game.batch, "Level 1: Oak Woods - Environment Asset - brullov", 55, 350);
        game.font.draw(game.batch, "Spikes: Rocky Roads - Essssam", 55, 325);
        game.font.draw(game.batch, "Level 2: Pixel Fantasy \"Caves\" - Szadi art", 55, 300);
        game.font.draw(game.batch, "Level 3: 16x16 Dungeon Tileset - Genewheel", 55, 275);
        game.font.draw(game.batch, "King: Pixel King - BalduranCZ", 55, 250);
        game.font.draw(game.batch, "Music: Various tracks by David Fesliyan", 55, 225);
        game.font.draw(game.batch, "PARADOXICAL PRODUCTIONS:", 55, 200);
        game.font.draw(game.batch, "Andrew Catapano", 55, 175);
        game.font.draw(game.batch, "Anthony Cross", 55, 150);
        game.font.draw(game.batch, "John Costa", 55, 125);
        game.font.draw(game.batch, "Frankie Gleeson", 55, 100);

        game.font.getData().setScale(1.2f);
        game.font.draw(game.batch, "Click anywhere to return to the Main Menu.", 55, 50);

        game.batch.end();
        // end batch

        if (Gdx.input.justTouched()) {
            music.stop();
            game.setScreen(new MainMenu(game, music));
            dispose();
        }

        game.font.getData().setScale(2.0f);
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void show() { }

    @Override
    public void hide() { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void dispose() {
        backgroundTex.dispose();
        music.dispose();
    }
}
