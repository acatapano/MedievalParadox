package com.badlogic.prototype;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.prototype.Screens.MainMenu;

public class Prototype extends Game {
	//Virtual Screen size and Box2D Scale(Pixels Per Meter)
	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 208;
	public static final float PPM = 100;

	//Box2D Collision Bits
	public static final short GROUND_BIT = 1;
	public static final short KNIGHT_BIT = 2;
	public static final short SPIKE_BIT = 32;
	public static final short ENEMY_BIT = 64;
	public static final short BARRIER_BIT = 128;
	public static final short ATTACK_BIT = 256;

	public SpriteBatch batch;

	public BitmapFont font;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();
		setScreen(new MainMenu(this));
	}


	@Override
	public void dispose() {
		super.dispose();
		batch.dispose();
		font.dispose();
	}

	@Override
	public void render () {
		super.render();
	}
}
