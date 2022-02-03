package com.main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class UI {
    static int money, life, wave, score;
    static BitmapFont font = new BitmapFont();

    static void draw(SpriteBatch batch){
        //Draw all text
        font.getData().setScale(1.6f);
        font.setColor(Color.GOLD);
        font.draw(batch, "Money: " + money, 6, (600 - 6));
        font.setColor(Color.PINK);
        font.draw(batch, "Wave: " + wave, 6, (600 - 9) - 12 * font.getData().scaleX);
        font.setColor(Color.LIME);
        font.draw(batch, "Life: " + life, 6, (600 - 12) - 12 * font.getData().scaleX * 2);
        font.setColor(Color.CYAN);
        font.draw(batch, "Score: " + score, 6, (600 - 15) - 12 * font.getData().scaleX * 3);
    }
}
