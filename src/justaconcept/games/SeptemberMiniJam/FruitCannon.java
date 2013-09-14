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
	    move();
	    displayRotated(sprite_, direction_, x_, y_);
	}

	void move() {
	    x_ += speed_ * Math.sin(direction_);
	    y_ -= speed_ * Math.cos(direction_);
	}
    }

    class Tomato {
	float x_;
	float y_;
	float direction_;
	float speed_;
	int state;
	ArrayList<PImage> sprites_;

	public Tomato(float x_, float y_, float direction_, float speed_, ArrayList<PImage> sprites_) {
	    this.x_ = x_ - sprites_.get(0).width / 2;
	    this.y_ = y_ - sprites_.get(0).height / 2;
	    this.direction_ = direction_;
	    this.speed_ = speed_;
	    this.sprites_ = sprites_;
	    state = 0;
	}

	public void draw() {
	    println(String.valueOf(x_));
	    displayRotated(sprites_.get(state), direction_, x_, y_);
	}

	public void move() {

	}
	
	public void leftKey() {
	    
	}
	
	public void rightKey() {
	    
	}
    }

    final int WIDTH = 1000;
    final int HEIGHT = 1000;

    int score_one = 0;
    int score_two = 0;
    PFont font;
    PImage melon_img, banana_img, seed_img;
    ArrayList<PImage> tomato_imgs;
    final int TOMATO_STATE_COUNT = 3;

    final float STEP_WIDTH = 25.f;
    final float DEFAULT_ROTATION_STEP = 0.015f;
    final float DEFAULT_PROJECTILE_SPEED = 0.85f;
    final float DEFAULT_TOMATO_SPEED = 0.85f;
    float center_x;
    float center_y;

    boolean team_one_pressed = false;
    boolean team_two_pressed = false;

    float melon_rotation;
    float rotation_step = DEFAULT_ROTATION_STEP;

    Tomato tomato;
    ArrayList<SeedProjectile> projectiles;

    public void loadAssets() {
	font = loadFont("Ziggurat.vlw");
	textFont(font, 32);
	melon_img = loadImage("fruit_melon.png");
	banana_img = loadImage("fruit_banana.png");
	seed_img = loadImage("seed.png");
	tomato_imgs = new ArrayList<PImage>();
        tomato_imgs.add(loadImage("veg_tomatoFull.png"));
        tomato_imgs.add(loadImage("veg_tomatoHalf.png"));
        tomato_imgs.add(loadImage("veg_tomatoQuarter.png"));

	projectiles = new ArrayList<SeedProjectile>();
    }

    public void setPlanetPos() {
	center_x = WIDTH / 2;
	center_y = HEIGHT / 2;
    }

    void spawnTomato() {
	float angle = random(PI * 2);
	tomato = new Tomato(WIDTH / 2f * (float) Math.sin(angle), HEIGHT / 2f * (float) Math.cos(angle), angle, DEFAULT_TOMATO_SPEED, tomato_imgs);
    }

    @Override
    public void setup() {
	size(WIDTH, HEIGHT);
	loadAssets();
	setPlanetPos();
	spawnTomato();
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
	text("Score: " + score_two, WIDTH - 215, 40);
    }

    void readKeys() {
	if (keyPressed) {
	    if (keyCode == LEFT) {
		melon_rotation -= rotation_step;
	    } else if (keyCode == RIGHT) {
		melon_rotation += rotation_step;
	    } 
	    
	    if (key == 'a') {
		tomato.leftKey();
	    } else if (key == 'd') {
		tomato.rightKey();
	    }

	    if (key == ' ') {
		AffineTransform transform = AffineTransform.getRotateInstance(melon_rotation, 0, melon_img.height / 2);
		projectiles.add(new SeedProjectile((float) transform.getTranslateX(), (float) transform.getTranslateY() - (melon_img.height / 2), melon_rotation,
			DEFAULT_PROJECTILE_SPEED, seed_img));
	    }
	}
    }

    @Override
    public void draw() {
	// println("FrameRate: " + String.valueOf(frameRate));

	background(255);

	writeScore();
	translate(center_x, center_y);

	image(melon_img, -melon_img.width / 2f, -melon_img.height / 2f);
	displayRotated(banana_img, melon_rotation, -banana_img.width / 2f, -banana_img.height / 2f);
	tomato.draw();
	displayProjectiles();

	readKeys();
    }
}
