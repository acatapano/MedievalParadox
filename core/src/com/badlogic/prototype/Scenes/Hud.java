package com.badlogic.prototype.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.prototype.Prototype;

public class Hud implements Disposable{

    //Scene2D.ui Stage and its own Viewport for HUD
    public Stage stage;
    private Viewport viewport;

    //Mario score/time Tracking Variables
    private Integer worldTimer;
    private boolean timeUp; // true when the world timer reaches 0
    private float timeCount;
    private static Integer score;

    //Scene2D widgets
    private Label countdownLabel;
    private static Label barLabel;
    private Label timeLabel;
    private Label levelLabel;
    private Label worldLabel;
    private Label healthLabel;

    public Hud(SpriteBatch sb){
        //define tracking variables
        worldTimer = 300;
        timeCount = 0;
        score = 0;

        //setup the HUD viewport using a new camera seperate from gamecam
        //define stage using that viewport and games spritebatch
        viewport = new FitViewport(Prototype.V_WIDTH, Prototype.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        //define a table used to organize hud's labels
        Table table = new Table();
        //Top-Align table
        table.top();
        //make the table fill the entire stage
        table.setFillParent(true);

        //define labels using the String, and a Label style consisting of a font and color
        countdownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        barLabel =new Label("BAR", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("LEVEL", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        healthLabel = new Label("HEALTH", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        //add labels to table, padding the top, and giving them all equal width with expandX
        table.add(healthLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        //add a second row to table
        table.row();
        table.add(barLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();

        //add table to the stage
        stage.addActor(table);

    }

    public void update(float dt){
        timeCount += dt;
        if(timeCount >= 1){
            if (worldTimer > 0) {
                worldTimer--;
            } else {
                timeUp = true;
            }
            countdownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }

    @Override
    public void dispose() { stage.dispose(); }

    public boolean isTimeUp() { return timeUp; }
}
