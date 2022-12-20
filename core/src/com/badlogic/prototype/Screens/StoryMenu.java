package com.badlogic.prototype.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.prototype.Prototype;

// Refer to MainMenu comments. No significant changes in code.
public class StoryMenu implements Screen
{
    final Prototype game;
    OrthographicCamera camera;

    Texture backgroundTex;
    Sprite backgroundSprite;

    Music music;

    public StoryMenu(Prototype game, Music music)
    {
        this.game = game;

        this.music = music;

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

        // Headers
        game.font.draw(game.batch, "STORY", 55, 430);

        // Story info
        game.font.getData().setScale(1.5f);
        game.font.draw(game.batch, "    You are a loyal knight working to defend your great", 55, 380);
        game.font.draw(game.batch, "kingdom from those who would do it harm. All seems", 55, 350);
        game.font.draw(game.batch, "quiet until one day, a flash of light erupts across the sky", 55, 320);
        game.font.draw(game.batch, "and strange mechanical beings invade! Robots are", 55, 290);
        game.font.draw(game.batch, "here from the future, and they want the kingdom for", 55, 260);
        game.font.draw(game.batch, "themselves. It is up to you, the last surviving knight,", 55, 230);
        game.font.draw(game.batch, "to fight through the metal menace and get to the king.", 55, 200);
        game.font.draw(game.batch, "Save him and recover his royal crown, the symbol of", 55, 170);
        game.font.draw(game.batch, "his sovereignty over the land, before the robots do.", 55, 140);
        game.font.draw(game.batch, "Go, brave knight! Only you can save your kingdom!", 55, 110);

        game.font.getData().setScale(1.2f);
        game.font.draw(game.batch, "Click or tap anywhere to continue", 55, 70);
        game.font.draw(game.batch, "Press Esc to go back", 55, 45);

        game.batch.end();
        // end batch

        if (Gdx.input.justTouched())
        {
            game.setScreen(new ControlsMenu(game, music));
            dispose();
        }
        // Press Esc to go back to story screen
        else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
        {
            game.setScreen(new MainMenu(game, music));
            dispose();
        }

        game.font.getData().setScale(2.0f);
    }

    @Override
    public void resize(int width, int height) {
    }

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

    @Override
    public void dispose() {
        backgroundTex.dispose();
    }
}
