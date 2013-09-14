package justaconcept.games.SeptemberMiniJam;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;


public class FruitCannon extends PApplet {
    AudioPlayer player;
    Minim minim;
    
    final float STEP_WIDTH = 25.f;
    final float DEFAULT_PROJECTILE_SPEED = 1.79f;
    final float DEFAULT_ROTATION_STEP = 0.049f;
    final float DEFAULT_TOMATO_SPEED = 0.15f;
    final float DEFAULT_TOMATO_STEP = 0.20f;
    final float DEFAULT_TOMATO_MAX = 6.0f;
    final int DEFAULT_TOMATO_HP = 4;
    
    final boolean draw_collision_box = false;

    void drawCollisionBox(Circle collision) {
	if (draw_collision_box) {
	    stroke(0, 200, 0);
	    fill(0, 0, 0, 0);
	    ellipse(collision.x, collision.y, collision.radius * 2, collision.radius * 2);
	}
    }

    class Circle {
	public float x;
	public float y;
	public float radius;

	public Circle(float x, float y, float radius) {
	    super();
	    this.x = x;
	    this.y = y;
	    this.radius = radius;
	}
    }

    class SeedProjectile {
	float x_;
	float y_;
	float direction_;
	float speed_;
	PImage sprite_;

	public SeedProjectile(float x_, float y_, float direction_, float speed_, PImage sprite_) {
	    this.x_ = x_;
	    this.y_ = y_;
	    this.direction_ = direction_;
	    this.speed_ = speed_;
	    this.sprite_ = sprite_;
	}

	void draw() {
	    move();
	    displayRotated(sprite_, direction_, x_ - sprite_.width / 2, y_ - sprite_.height / 2);
	    drawCollisionBox(getHitCircle());
	}

	void move() {
	    x_ += speed_ * Math.sin(direction_);
	    y_ -= speed_ * Math.cos(direction_);
	}

	public Circle getHitCircle() {
	    return new Circle(x_, y_, sprite_.width / 2);
	}
    }

    class Tomato {
	float x_;
	float y_;
	float angle_;
	float fall_speed_;
	float lateral_velocity_;
	float step_;
	float max_speed_;
	int state_;
	int hp_;
	int maxhp_;
	ArrayList<PImage> sprites_;

	public Tomato(float x_, float y_, float direction_, float speed_, ArrayList<PImage> sprites_) {
	    this.x_ = x_;
	    this.y_ = y_;
	    this.angle_ = direction_;
	    this.fall_speed_ = speed_;
	    this.lateral_velocity_ = 0f;
	    this.sprites_ = sprites_;
	    this.step_ = DEFAULT_TOMATO_STEP;
	    this.max_speed_ = DEFAULT_TOMATO_MAX;
	    this.state_ = 0;
	    this.hp_ = DEFAULT_TOMATO_HP;
	    this.maxhp_ = DEFAULT_TOMATO_HP;
	}

	public void takeDamage() {
	    hp_--;
	    if (hp_ <= maxhp_ / 2) {
		state_ = 1;
	    }
	    if (hp_ <= maxhp_ / 4) {
		state_ = 2;
	    }
	    if (hp_ <= 0) {
		game_finished = true;
		veg_won = false;
	    }
	}

	public void draw() {
	    // println(String.valueOf(x_));
	    move();
	    displayRotated(sprites_.get(state_), angle_, x_ - sprites_.get(0).width / 2, y_ - sprites_.get(0).height / 2);
	    drawCollisionBox(getHitCircle());
	}

	public void move() {
	    float distance = (float) Math.sqrt(x_ * x_ + y_ * y_);
	    float d_angle = (float) Math.atan(lateral_velocity_ / distance);
	    angle_ += d_angle;
	    x_ = (distance - fall_speed_) * (float) Math.sin(angle_);
	    y_ = -(distance - fall_speed_) * (float) Math.cos(angle_);
	}

	public void leftKey() {
	    lateral_velocity_ -= step_;
	    if (lateral_velocity_ < -max_speed_) {
		lateral_velocity_ = -max_speed_;
	    }
	}

	public void rightKey() {
	    lateral_velocity_ += step_;
	    if (lateral_velocity_ > max_speed_) {
		lateral_velocity_ = max_speed_;
	    }
	}

	public Circle getHitCircle() {
	    return new Circle(x_, y_, sprites_.get(0).width / 2);
	}
    }

    final int WIDTH = 1000;
    final int HEIGHT = 1000;

    int score_one = 0;
    int score_two = 0;
    PFont font;
    PImage melon_img, banana_img, seed_img, bg_img, mask_img, veg_win_img, fruit_win_img;
    ArrayList<PImage> tomato_imgs;
    final int TOMATO_STATE_COUNT = 3;

    float center_x;
    float center_y;

    boolean seed_shot = false;

    float melon_rotation;
    float rotation_step = DEFAULT_ROTATION_STEP;

    Tomato tomato;
    ArrayList<SeedProjectile> projectiles;

    boolean game_finished;
    boolean veg_won;
    
    boolean melon_left = false;
    boolean melon_right = false;
    boolean tomato_left = false;
    boolean tomato_right = false;
    boolean melon_shoot = false;
    boolean reset_key = false;

    public void loadAssets() {
	font = loadFont("Ziggurat.vlw");
	textFont(font, 32);
	melon_img = loadImage("fruit_melon.png");
	banana_img = loadImage("fruit_bananaWithEyeball.png");
	seed_img = loadImage("fruit_projectile.png");
	bg_img = loadImage("bg_image.png");
	mask_img = loadImage("bg_mask.png");
	veg_win_img = loadImage("veg win_disappointing.png");
	fruit_win_img = loadImage("fruit win_disappointing.png");

	tomato_imgs = new ArrayList<PImage>();
	tomato_imgs.add(loadImage("veg_tomatoFull.png"));
	tomato_imgs.add(loadImage("veg_tomatoHalf.png"));
	tomato_imgs.add(loadImage("veg_tomatoQuarter.png"));
    }

    public void setPlanetPos() {
	center_x = WIDTH / 2;
	center_y = HEIGHT / 2;
    }

    public Circle getHitCircle() {
	return new Circle(0f, 0f, 100f);
    }

    boolean isCollision(Circle c1, Circle c2) {
	float dist_sq = (c1.x - c2.x) * (c1.x - c2.x) + (c1.y - c2.y) * (c1.y - c2.y);
	println(Math.sqrt(dist_sq));
	return dist_sq <= (c1.radius + c2.radius) * (c1.radius + c2.radius);
    }

    void spawnTomato() {
	float angle = random(PI * 2);
	tomato = new Tomato(WIDTH / 2f * (float) Math.sin(angle), HEIGHT / 2f * (float) Math.cos(angle), angle, DEFAULT_TOMATO_SPEED, tomato_imgs);
    }

    void resetGame() {
	game_finished = false;
	setPlanetPos();
	spawnTomato();
	projectiles = new ArrayList<SeedProjectile>();
    }

    @Override
    public void setup() {
	size(WIDTH, HEIGHT);
	    
	minim = new Minim(this);
	player = minim.loadFile("music.mp3");
	player.play();
	
	loadAssets();
	resetGame();
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

    public void keyPressed() {
	if (keyCode == LEFT) {
	    melon_left = true;
	} else if (keyCode == RIGHT) {
	    melon_right = true;
	}

	if (keyCode == UP) {
	    tomato_left = true;
	} else if (keyCode == DOWN) {
	    tomato_right = true;
	}

	if (key == ' ' && !seed_shot) {
	    
	    seed_shot = true;
	    AffineTransform transform = AffineTransform.getRotateInstance(melon_rotation, 0, melon_img.height / 2);
	    projectiles.add(new SeedProjectile((float) transform.getTranslateX(), (float) transform.getTranslateY() - (melon_img.height / 2), melon_rotation,
		    DEFAULT_PROJECTILE_SPEED, seed_img));
	}

	if (key == 'r') {
	    reset_key = true;
	}
    }

    public void keyReleased() {
	if (keyCode == LEFT) {
	    melon_left = false;
	} else if (keyCode == RIGHT) {
	    melon_right = false;
	}

	if (keyCode == UP) {
	    tomato_left = false;
	} else if (keyCode == DOWN) {
	    tomato_right = false;
	}

	if (key == ' ') {
	    
	    seed_shot = false;
	}

	if (key == 'r') {
	    reset_key = false;
	}
    }
    
    public void doActions() {
	if (melon_left) {
	    melon_rotation -= rotation_step;
	} else if (melon_right) {
	    melon_rotation += rotation_step;
	}

	if (tomato_left) {
	    tomato.leftKey();
	} else if (tomato_right) {
	    tomato.rightKey();
	}

	if (reset_key) {
	    resetGame();
	}
    }

    void testCollisions() {
	if (isCollision(getHitCircle(), tomato.getHitCircle())) {
	    game_finished = true;
	    veg_won = true;
	}
	for (int i = 0; i < projectiles.size(); i++) {
	    if (isCollision(projectiles.get(i).getHitCircle(), tomato.getHitCircle())) {
		tomato.takeDamage();
		projectiles.remove(i);
	    }
	}
    }

    @Override
    public void draw() {
	//readKeys();
	doActions();
	if (game_finished) {
	    if (veg_won)
		image(veg_win_img, 0f, 0f);
	    else
		image(fruit_win_img, 0f, 0f);
	    return;
	}

	// println("FrameRate: " + String.valueOf(frameRate));

	background(bg_img);

	writeScore();
	pushMatrix();
	translate(center_x, center_y);

	image(melon_img, -melon_img.width / 2f, -melon_img.height / 2f);
	displayRotated(banana_img, melon_rotation, -banana_img.width / 2f, -banana_img.height / 2f);
	tomato.draw();
	displayProjectiles();
	popMatrix();

	image(mask_img, 0, 0);

	testCollisions();
    }
}
