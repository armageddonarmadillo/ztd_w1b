package com.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Random;

public class Main extends ApplicationAdapter {
	// GAME VARIABLES
	SpriteBatch batch;
	Random r;
	String current_type = "ccc";

	// CONTROL VARIABLES

	// GAME LISTS
	static ArrayList<Zombie> zombies = new ArrayList<Zombie>();
	static ArrayList<Cannon> cannons = new ArrayList<Cannon>();
	static ArrayList<Button> buttons = new ArrayList<Button>();
	static ArrayList<Bullet> bullets = new ArrayList<Bullet>();

	@Override
	public void create () {
		batch = new SpriteBatch();
		r = new Random();
		setup();
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);
		update();
		batch.begin();
		batch.draw(Resources.bg, 0, 0);
		UI.draw(batch);
		for(Zombie z : zombies) z.draw(batch);
		for(Cannon c : cannons) c.draw(batch);
		for(Button b : buttons) b.draw(batch);
		for(Bullet b : bullets) b.draw(batch);
		batch.end();
	}

	void update(){
		tap(); //first in update
		spawn_zombies();
		for(Zombie z : zombies) z.update();
		for(Cannon c : cannons) c.update();
		for(Button b : buttons) b.update();
		for(Bullet b : bullets) b.update();
		housekeeping(); //last in update
	}

	void tap() {
		if(Gdx.input.justTouched()){
			int x = Gdx.input.getX(), y = Gdx.graphics.getHeight() - Gdx.input.getY();

			for(Button b : buttons) {
				if (b.gethitbox().contains(x, y)) {
					System.out.println(b.type);
					if (b.locked) {
						if (b.t.hidden) {
							hidett();
							b.t.hidden = false;
						} else {
							b.locked = false;
							b.t.hidden = true;
						}
					} else {
						deselect();
						b.selected = true;
						current_type = b.type;
					}
					return;
				} else {
					if(b.t.close.gethitbox().contains(x, y) && !b.t.hidden) { hidett(); return; }
					if(b.t.gethitbox().contains(x, y) && !b.t.hidden) return;
					if(!b.t.gethitbox().contains(x, y) && !b.t.hidden) { hidett(); return; }
				}
			}

			for(Cannon c : cannons) if(c.gethitbox().contains(x, y)) return;
			if(buildable(x, y)) if(UI.money >= (Tables.balance.get("cost_"+current_type) == null ? 10 : Tables.balance.get("cost_"+current_type))) {
				UI.money -= (Tables.balance.get("cost_"+current_type) == null ? 10 : Tables.balance.get("cost_"+current_type));
				cannons.add(new Cannon(current_type, x, y));
			}
		}
	}

	void hidett(){
		for (Button b : buttons) b.t.hidden = true;
	}

	void deselect(){
		for(Button b : buttons) b.selected = false;
	}

	boolean buildable(int x, int y){
		return (x < 1000 && ((y < 200 || y > 300) && y < 500));
	}

	void setup() {
		//init all the tables
		Tables.init();

		//make some buttons
		buttons.add(new Button("cannon", buttons.size() * 75 + 200, 525));
		buttons.add(new Button("double", buttons.size() * 75 + 200, 525));
		buttons.add(new Button("super", buttons.size() * 75 + 200, 525));
		buttons.add(new Button("fire", buttons.size() * 75 + 200, 525));
		buttons.add(new Button("laser", buttons.size() * 75 + 200, 525));

	}

	void housekeeping(){
		for(Zombie z : zombies) if(!z.active) { zombies.remove(z); break; }
		for(Bullet b : bullets) if(!b.active) { bullets.remove(b); break; }
	}

	void spawn_zombies(){
		if(!zombies.isEmpty()) return;
		UI.wave++;
		for(int i = 0; i < 5 * UI.wave; i++){
			zombies.add(new Zombie("speedy", 1024 + i * 50, r.nextInt(450)));
		}
	}

	// END OF FILE, DON'T ADD BELOW THIS
	@Override
	public void dispose () {
		batch.dispose();
	}
}
