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

	// CONTROL VARIABLES
	String current_type = "cannon";
	boolean pause = false;

	// GAME LISTS
	static ArrayList<Zombie> zombies = new ArrayList<Zombie>();
	static ArrayList<Cannon> cannons = new ArrayList<Cannon>();
	static ArrayList<Button> buttons = new ArrayList<Button>();
	static ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	static ArrayList<Effect> effects = new ArrayList<Effect>();
	static ArrayList<Wall> walls = new ArrayList<Wall>();

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
		for(Effect e : effects) e.draw(batch);
		for(Wall e : walls) e.draw(batch);
		batch.end();
	}

	void update(){
		tap(); //first in update
		spawn_zombies();
		if (!pause){
			for(Zombie z : zombies) z.update();
			for(Cannon c : cannons) c.update();
			for(Button b : buttons) b.update();
			for(Bullet b : bullets) b.update();
			for(Wall e : walls) e.update();
		}
		for(Effect e : effects) e.update();
		housekeeping(); //last in update
	}

	void tap() {
		if(Gdx.input.justTouched()){
			int x = Gdx.input.getX(), y = Gdx.graphics.getHeight() - Gdx.input.getY();

			effects.add(new Effect("boom", x, y));

			for(Button b : buttons) {
				if (b.gethitbox().contains(x, y)) {
					if(b.type.equals("pause") || b.type.equals("play")){
						pause = !pause;
						b.type = pause ? "play" : "pause";
						return;
					}
					if (b.locked) {
						if (b.t.hidden) {
							hidett();
							b.t.hidden = false;
						} else {
							if(UI.money >= (Tables.balance.get("unlock_"+b.type) == null ? 0 : (Tables.balance.get("unlock_"+b.type))))
								UI.money -= (Tables.balance.get("unlock_"+b.type) == null ? 0 : (Tables.balance.get("unlock_"+b.type)));
								else return;
							b.locked = false;
							b.t.hidden = true;
						}
					} else {
						deselect();
						b.selected = true;
						current_type = b.type;
					}
					return;
				} else if (!b.t.hidden){
					if(b.t.close.gethitbox().contains(x, y)) { hidett(); return; }
					if(b.t.gethitbox().contains(x, y)) return;
					if(!b.t.gethitbox().contains(x, y)) hidett();
				}
			}
			if(walls.size() < 3 && (current_type.equals("wall") || current_type.equals("mounted"))) {
				walls.add(new Wall(x, 0, current_type.equals("mounted")));
				return;
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
		buttons.get(buttons.size() - 1).locked = false;
		buttons.get(buttons.size() - 1).selected = true;
		buttons.add(new Button("double", buttons.size() * 75 + 200, 525));
		buttons.add(new Button("super", buttons.size() * 75 + 200, 525));
		buttons.add(new Button("fire", buttons.size() * 75 + 200, 525));
		buttons.add(new Button("laser", buttons.size() * 75 + 200, 525));
		buttons.add(new Button("missile", buttons.size() * 75 + 200, 525));
		buttons.add(new Button("wall", buttons.size() * 75 + 200, 525));
		buttons.get(buttons.size() - 1).locked = false;
		buttons.get(buttons.size() - 1).selected = false;
		buttons.add(new Button("mounted", buttons.size() * 75 + 200, 525));

		//pause button
		buttons.add(new Button("pause", 1024 - 75, 525));
		buttons.get(buttons.size() - 1).locked = false;
		buttons.get(buttons.size() - 1).selected = false;
	}

	void housekeeping(){
		for(Zombie z : zombies) if(!z.active) { effects.add(new Effect("click", z.x + z.w / 2, z.y + z.h / 2)); zombies.remove(z); break; }
		for(Bullet b : bullets) if(!b.active) { bullets.remove(b); break; }
		for(Effect e : effects) if(!e.active) { effects.remove(e); break; }
		for(Cannon c : cannons) if(!c.active) { cannons.remove(c); break; }
		for(Wall e : walls) if(!e.active) { walls.remove(e); break; }
	}

	void spawn_zombies(){
		if(!zombies.isEmpty()) return;
		UI.wave++;
		for(int i = 0; i < 5 * UI.wave; i++){
			zombies.add(new Zombie("bob", 1024 + i * 50, r.nextInt(450)));
		}
	}

	// END OF FILE, DON'T ADD BELOW THIS
	@Override
	public void dispose () {
		batch.dispose();
	}
}
