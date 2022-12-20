package com.badlogic.prototype;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.prototype.Screens.MainMenu;

public class Prototype extends Game {
	// Virtual Screen size and Box2D Scale (Pixels Per Meter)
	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 208;
	public static final float PPM = 100;

	//Box2D Collision Bits
	public static final short GROUND_BIT = 1;
	public static final short KNIGHT_BIT = 2;
	public static final short GOAL_BIT = 20;
	public static final short SPIKE_BIT = 32;
	public static final short ENEMY_BIT = 64;
	public static final short BARRIER_BIT = 128;
	public static final short ATTACK_BIT = 256;

	// Makes the sprite batch.
	public SpriteBatch batch;

	// Sets up game font for all menus and Hud.
	public BitmapFont font;

	// Creates batch, font, and sets the game screen to MainMenu.
	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();
		setScreen(new MainMenu(this));
	}

	// Disposes of the game, batch, and font when the game is closed.
	@Override
	public void dispose() {
		super.dispose();
		batch.dispose();
		font.dispose();
	}

	// Renders the game.
	@Override
	public void render () {
		super.render();
	}
}
