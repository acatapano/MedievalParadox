package com.badlogic.prototype.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.prototype.Prototype;

public class ControlsMenu implements Screen
{
    final Prototype game;
    OrthographicCamera camera;

    Texture backgroundTex;
    Sprite backgroundSprite;

    public ControlsMenu(Prototype game)
    {
        this.game = game;

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
        game.font.draw(game.batch, "CONTROLS", 55, 430);

        // Story info
        game.font.getData().setScale(1.5f);
        game.font.draw(game.batch, "WALK........Left and Right Arrow Keys, or A and D", 55, 380);
        game.font.draw(game.batch, "RUN...........Hold Shift while Walking", 55, 350);
        game.font.draw(game.batch, "JUMP.........Spacebar or W", 55, 320);
        game.font.draw(game.batch, "ATTACK.....Enter or Left-Ctrl", 55, 290);
        game.font.draw(game.batch, "TIPS:", 55, 260);
        game.font.draw(game.batch, "  -  Avoid being struck by enemies to stay alive", 55, 230);
        game.font.draw(game.batch, "  -  Recover health by finding Hearts", 55, 200);
        game.font.draw(game.batch, "  -  Jump over spikes and dangerous gaps", 55, 170);
        game.font.draw(game.batch, "  -  Attack robots to destroy them", 55, 140);
        game.font.draw(game.batch, "  -  Reach the end of the level to progress", 55, 110);

        game.font.getData().setScale(1.2f);
        game.font.draw(game.batch, "Click or tap anywhere to start the game", 55, 70);
        game.font.draw(game.batch, "Press Esc to go back", 55, 45);

        game.batch.end();
        // end batch

        if (Gdx.input.justTouched()) {
            game.setScreen(new Level3(game)); // change this line to skip levels
            dispose();
        }
        // press Esc to go back to story screen
        else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new StoryMenu(game));
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
    }
}
