package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;


public class MyGdxGame extends ApplicationAdapter {
	
	private SpriteBatch batch;//possibly we want to make it private, not sure
	private Texture backgroundTest; //maybe we make it private
	private Texture greenTest;
	private Texture blueTest;
	private Texture yellowTest;
	private Texture redTest;
	private Texture arrowTest;
	private OrthographicCamera camera;
	private Rectangle hitAreaUno; //this is like the band of permissible inputs
	private Rectangle hitAreaDos;
	private Rectangle hitAreaTres;
	private Rectangle hitAreaQuat;
	private Rectangle longAreaUno; //this makes sure to prevent button spamming by having a long band that removes missed inputs; also an invisible rectangle
	private Rectangle longAreaDos;
	private Rectangle longAreaTres;
	private Rectangle longAreaQuat;
    private Array<Rectangle> skyfallArray;
    // private Array<Rectangle> mostRecent; //the 4 most recent notes, for antispam implementation
    private Rectangle mostRecentUno; //for first column
	long lastDropTime;
	long lastHeldTime;
	int rng;
	float timeElapsed = 0;
	private int score = 0;
	private int total = 0;
	
    
	@Override
	public void create () {
		
		batch = new SpriteBatch();
		backgroundTest = new Texture("backgroundTest.png");
		greenTest = new Texture("greenTest.png");
		redTest = new Texture("redTest.png");
		yellowTest = new Texture("yellowTest.png");
		blueTest = new Texture("blueTest.png");
		arrowTest = new Texture("arrowTest.png");
				
		
		
		
		
		camera = new OrthographicCamera();
		   camera.setToOrtho(false, 480, 800);
		   
		batch = new SpriteBatch();
		
		//this all creates rectangle objects for hit maps, both for rendering input and doing calculations
		
		hitAreaUno = new Rectangle();
		hitAreaUno.x = 20;
		hitAreaUno.y = 20;
		hitAreaUno.width = 64;
		hitAreaUno.height = 64;
		
		hitAreaDos = new Rectangle();
		hitAreaDos.x = 140;
		hitAreaDos.y = 20;
		hitAreaDos.width = 64;
		hitAreaDos.height = 64;
		
		hitAreaTres = new Rectangle();
		hitAreaTres.x = 260;
		hitAreaTres.y = 20;
		hitAreaTres.width = 64;
		hitAreaTres.height = 64;
		
		hitAreaQuat = new Rectangle();
		hitAreaQuat.x = 380;
		hitAreaQuat.y = 20;
		hitAreaQuat.width = 64;
		hitAreaQuat.height = 64;
		
		//anti spammer band creation, x position moved by 10 less than hitAreas, but length increased by 20
		
		longAreaUno = new Rectangle();
		longAreaUno.x = 10;
		longAreaUno.y = 20;
		longAreaUno.width = 84;
		longAreaUno.height = 128;
		
		longAreaDos = new Rectangle();
		longAreaDos.x = 130;
		longAreaDos.y = 20;
		longAreaDos.width = 84;
		longAreaDos.height = 128;
		
		longAreaTres = new Rectangle();
		longAreaTres.x = 250;
		longAreaTres.y = 20;
		longAreaTres.width = 84;
		longAreaTres.height = 128;
		
		longAreaQuat = new Rectangle();
		longAreaQuat.x = 370;
		longAreaQuat.y = 20;
		longAreaQuat.width = 84;
		longAreaQuat.height = 128;
		
		// mostRecent = new Array<Rectangle>();
		
		mostRecentUno = new Rectangle(); //testing most recent to see how it can work
		
		
		skyfallArray = new Array<Rectangle>();
		spawnSkyfall();
	}
	
	private void spawnSkyfall() {
		Rectangle skyfall = new Rectangle();
		rng = MathUtils.random(0,3);
		if(rng == 0) {
			skyfall.x = 20;
		} else if(rng == 1) {
			skyfall.x = 140;
		} else if(rng == 2) {
			skyfall.x = 260;
		} else if(rng == 3) {
			skyfall.x = 380;
		}
		skyfall.y = 800;
		skyfall.width = 8;
		skyfall.height = 64;
		skyfallArray.add(skyfall);
		lastDropTime = TimeUtils.nanoTime();
	}
	
	
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 1, 1); //this changes a color, RGB and somethin else
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(backgroundTest, 0, 0);
		
		
		for(Rectangle skyfall: skyfallArray) {
			batch.draw(arrowTest, skyfall.x, skyfall.y);
		}
		
		
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			batch.draw(greenTest, hitAreaUno.x, hitAreaUno.y);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			batch.draw(blueTest, hitAreaDos.x, hitAreaDos.y);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			batch.draw(redTest, hitAreaTres.x, hitAreaTres.y);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.F)) {
			batch.draw(yellowTest, hitAreaQuat.x, hitAreaQuat.y);
		}
		
		batch.end();
		
		
		if(TimeUtils.nanoTime()-lastDropTime > 250000000) {
			spawnSkyfall();
		}
		
		
		for(Iterator<Rectangle> iter = skyfallArray.iterator(); iter.hasNext();) {
			Rectangle skyfall = iter.next();
			skyfall.y -= 1500*Gdx.graphics.getDeltaTime();
			
			/**
			 * now the newest problem is that mostRecentUno is 
			 * 
			 */
			/*
			
			if(mostRecentUno == null && skyfall.overlaps(longAreaUno)){ //this being unaccessable causes the null pointer exception
				mostRecentUno = skyfall;
				skyfall = mostRecentUno;
			}
			if(mostRecentUno!=null) {
				if(mostRecentUno.y < 24 ) { //null pointer exception
					System.out.println("Replaced!"); //if it doesn't work, this code isn't working
					mostRecentUno = null;  //ok im ending it off here where its starting to work
				}
			}
			
			*/
			
			///here is the next problem, the tiles should clear when it overlaps with the hitmap, however, there isn't
			//a reliable call that keeps track of how long a key is pressed for
			//so we can't punish bad play ie holding down the keys as example
			
			if(Gdx.input.isKeyJustPressed(Input.Keys.A)) {
				 if(skyfall.overlaps(hitAreaUno)) { //seemingly timeUtils is greater than a billion)
				iter.remove();
				//mostRecentUno = null;
				score++;
				total++;
				System.out.println("Hit!   "+score+"/"+total);
				 //here is a crappy score implementation, wanna polish it a lot more
				} else if(skyfall.overlaps(longAreaUno)){ //this is KINDA hacky in the sense that it can remove multiple notes at once, 
					//but that only really a problem in high note density songs, which we probably aren't doing; it works as a fix to spam
					//good thing about it is I don't have to create a whole new iterator
					iter.remove();
					// mostRecentUno = null;
					
					total++;
					System.out.println("Miss! "+score+"/"+total);
				}
			}
			if(Gdx.input.isKeyJustPressed(Input.Keys.S)) {
				if(skyfall.overlaps(hitAreaDos) ) {
					iter.remove();
					score++;
					total++;
					System.out.println("Hit!   "+score+"/"+total);
				} else if(skyfall.overlaps(longAreaDos)) {
					iter.remove();
					total++;
					System.out.println("Miss! "+score+"/"+total);
				}
			}
			if(Gdx.input.isKeyJustPressed(Input.Keys.D)) {
				if(skyfall.overlaps(hitAreaTres)) {
					iter.remove();
					score++;
					total++;
					System.out.println("Hit!   "+score+"/"+total);
				} else if(skyfall.overlaps(longAreaTres)) {
					iter.remove();
					total++;
					System.out.println("Miss! "+score+"/"+total);
				}
			}
			if(Gdx.input.isKeyJustPressed(Input.Keys.F)) {
				if(skyfall.overlaps(hitAreaQuat)) {
					iter.remove();
					score++;
					total++;
					System.out.println("Hit!   "+score+"/"+total);
				}else if(skyfall.overlaps(longAreaQuat)) {
					iter.remove();
					total++;
					System.out.println("Miss! "+score+"/"+total);
				}
				
			}
			
			
			
			
			if(skyfall.y+64 < 0) {
				iter.remove();
				total++;
				System.out.println("Miss! "+score+"/"+total);
			}
		}
		
		
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		backgroundTest.dispose();
		greenTest.dispose();
		blueTest.dispose();
		yellowTest.dispose();
		redTest.dispose();
		arrowTest.dispose();
	}
}
