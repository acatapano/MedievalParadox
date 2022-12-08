
package com.badlogic.prototype.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.prototype.Prototype;

public class MainMenu implements Screen
{
    final Prototype game;
    OrthographicCamera camera;

    Texture backgroundTex;
    Sprite backgroundSprite;

    public MainMenu(Prototype game)
    {
        this.game = game;

        backgroundTex = new Texture(Gdx.files.internal("mainmenu_bg.png"));
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
        game.font.draw(game.batch, "MEDIEVAL PARADOX", 55, 430);
        game.font.draw(game.batch, "Click or tap anywhere to begin", 55, 380);

        game.batch.end();
        // end batch

        if (Gdx.input.isTouched())
        {
            game.setScreen(new StoryMenu(game));
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

    @Override
    public void dispose() {
        backgroundTex.dispose();
    }
}
