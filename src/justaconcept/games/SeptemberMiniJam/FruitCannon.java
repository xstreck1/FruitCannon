package justaconcept.games.SeptemberMiniJam;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

public class FruitCannon extends PApplet {
    class SeedProjectile {
	float x_;
	float y_;
	float direction_;
	float speed_;
	PImage sprite_;
	
	public SeedProjectile(float x_, float y_, float direction_, float speed_, PImage sprite_) {
	    this.x_ = x_ - sprite_.width / 2;
	    this.y_ = y_ - sprite_.height / 2;
	    this.direction_ = direction_;
	    this.speed_ = speed_;
	    this.sprite_ = sprite_;
	}

	void draw() {
	    displayRotated(sprite_, direction_, x_, y_);
	}
	
	void move() {
	    
	}
    }
    
    final int WIDHT = 1000;
    final int HEIGHT = 1000;

    int score_one = 0;
    int score_two = 0;
    PFont font;
    PImage melon;
    PImage seed_img;

    final float STEP_WIDTH = 25.f;
    final float DEFAULT_ROTATION_STEP = 0.015f;
    float center_x;
    float center_y;

    boolean team_one_pressed = false;
    boolean team_two_pressed = false;

    float melon_rotation;
    float rotation_step = DEFAULT_ROTATION_STEP;
    
    ArrayList<SeedProjectile> projectiles;

    public void loadAssets() {
	font = loadFont("Ziggurat.vlw");
	textFont(font, 32);
	melon = loadImage("melon.png");
	seed_img = loadImage("seed.png");
	
	projectiles = new ArrayList<SeedProjectile>();
    }

    public void setPlanetPos() {
	center_x = WIDHT / 2 - melon.width / 2;
	center_y = HEIGHT / 2 - melon.height / 2;
    }

    @Override
    public void setup() {
	size(WIDHT, HEIGHT);
	loadAssets();
	setPlanetPos();
    }
    
    void displayRotated(PImage object, float rotation, float x, float y) {
	pushMatrix();
	translate(x, y);
	translate(object.width / 2, object.height / 2);
	rotate(rotation);
	translate(-object.width / 2, -object.height / 2);
	image(object, 0, 0);
	popMatrix();
    }
    
    void displayProjectiles() {
	for (SeedProjectile projectile : projectiles) {
	    projectile.draw();
	}
    }

    void writeScore() {
	noStroke();
	fill(0);
	text("Score: " + score_one, 60, 40);
	text("Score: " + score_two, WIDHT - 215, 40);
    }
    
    void readKeys() {
	if (keyPressed) {
	    if (keyCode == LEFT) {
		melon_rotation -= rotation_step;
	    } else if (keyCode == RIGHT) {
		melon_rotation += rotation_step;
	    }
	    
	    if (keyCode == UP ) {
		AffineTransform transform = AffineTransform.getRotateInstance(melon_rotation, 0, melon.height/2);
		projectiles.add(new SeedProjectile((float) transform.getTranslateX() + melon.width/2, (float) transform.getTranslateY(), melon_rotation, 0f, seed_img));
	    }
	}
    }

    @Override
    public void draw() {

	background(255);

	writeScore();
	translate(center_x, center_y);
	
	displayRotated(melon, melon_rotation, 0f, 0f);
	displayProjectiles();

	readKeys();
    }
}
