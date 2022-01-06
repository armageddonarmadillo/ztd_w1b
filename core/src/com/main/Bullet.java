package com.main;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Bullet {
    int x, y, w, h;
    int speed, dt, md; //dt = distance travelled, md = maximum distance that the bullet can travel
    float angle;
    String type;
    boolean active = true;
    Sprite sprite;

    Bullet(String type, int x, int y){
        this.type = type;
        this.x = x;
        this.y = y;
        sprite = new Sprite(Tables.resources.get("bullet_" + type) == null ? Resources.bullet : Tables.resources.get("bullet_" + type));
        w = Tables.bullet_resources.get(type) == null ? Resources.bullet.getWidth() : Tables.bullet_resources.get(type).getWidth();
        h = Tables.bullet_resources.get(type) == null ? Resources.bullet.getHeight() : Tables.bullet_resources.get(type).getHeight();
        speed = 5;
        dt = 0;
        md = 300;
        angle = calc_angle();
        sprite.setPosition(x, y);
        sprite.setRotation((float)Math.toDegrees(calc_angle()) - 90f);
    }

    void draw(SpriteBatch batch){
        sprite.draw(batch);
    }

    void update(){
        x += Math.cos(angle) * speed;
        y += Math.sin(angle) * speed;
        sprite.setPosition(x, y);
        dt += Math.abs(Math.cos(angle)) * speed + Math.abs(Math.sin(angle)) * speed;
        active = dt < md;
        hitzombie();
    }

    Rectangle hitbox(){ return new Rectangle(x, y, w, h); }

    float calc_angle(){
        Zombie closest = null;
        for(Zombie z : Main.zombies){
            if(closest == null) { closest = z; continue; }
            float hyp_closest = (float)Math.sqrt(((x - closest.x) * (x - closest.x)) + ((y - closest.y) * (y - closest.y)));
            float hyp_z = (float)Math.sqrt(((x - z.x) * (x - z.x)) + ((y - z.y) * (y - z.y)));
            if(hyp_z < hyp_closest) closest = z;
        }
        float zx = closest.x + (float)closest.w / 2, zy = closest.y + (float)closest.h / 2;
        return (float)(Math.atan((y - zy)/(x - zx)) + (x >= zx ? Math.PI : 0));
    }

    void hitzombie(){
        if(Main.zombies.isEmpty()) return;
        for(Zombie z : Main.zombies) {
            if(z.gethitbox().contains(hitbox())){
                z.hp--;
                this.active = false;
            }
        }
    }
}
