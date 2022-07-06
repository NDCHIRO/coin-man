package com.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	//man
	Texture []man;
	int manState=0;
	int pause=0;
	float velocity=10f;
	float gravity=0.3f;
	int manY;
	Rectangle manRectangle;
	Random random = new Random();
	int score=0;
	Texture dizzy;
	BitmapFont font;
	int gameState=0;
	//coins
	ArrayList<Rectangle> coinRectangle = new ArrayList<>();
	ArrayList<Integer> coinX = new ArrayList<>();
	ArrayList<Integer> coinY = new ArrayList<>();
	Texture coin;
	int coinCount=0;
	//bombs
	ArrayList<Rectangle> bombRectangle = new ArrayList<>();
	ArrayList<Integer> bombX = new ArrayList<>();
	ArrayList<Integer> bombY = new ArrayList<>();
	Texture bomb;
	int bombCount=0;


	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		//move states
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		//death state
		dizzy = new Texture("dizzy-1.png");
		//man starting position at Y axis
		manY = Gdx.graphics.getHeight() / 2;
		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		//font for the score text
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(5);
	}


	public void makeCoins()
	{
		float height = random.nextFloat()*Gdx.graphics.getHeight();
		coinY.add((int)height);
		coinX.add(Gdx.graphics.getWidth());
	}

	public void makeBombs()
	{
		float height = random.nextFloat()*Gdx.graphics.getHeight();
		bombY.add((int)height);
		bombX.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == 0)
			//touch the screen to start the game
			if(Gdx.input.justTouched())
				gameState=1;

		else if(gameState == 1)
			//game is alive
			startTheGame();

		else if(gameState == 2)
			//game ended and touch to restart the game
			if(Gdx.input.justTouched())
				resetTheGame();

		// if game state = 2 then its a dizzy state so draw dizzy man
		if(gameState==2)
			batch.draw(dizzy, Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2,  manY);
		// else draw dizzy man
		else
			batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2,  manY);
		//make rectangle for the man to make collisions
		manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2,manY,man[manState].getWidth(),man[manState].getHeight());
		//decide if man collide with coin or no
		for(int i=0;i<coinRectangle.size();i++)
			if(Intersector.overlaps(manRectangle,coinRectangle.get(i)))
			{
				score++;
				coinRectangle.remove(i);
				coinX.remove(i);
				coinY.remove(i);
				break;
			}
		//if the man collide with bomb make game state = 2 for the dizzy state
		for(int i=0;i<bombRectangle.size();i++)
			if(Intersector.overlaps(manRectangle,bombRectangle.get(i)))
				gameState=2;

		font.draw(batch,"Score: "+score,50,1700);
		batch.end();
	}

	public void startTheGame()
	{
		//coins
		if(coinCount<100)
			coinCount++;
		else
		{
			coinCount=0;
			makeCoins();
		}
		coinRectangle.clear();
		for(int i = 0; i< coinX.size(); i++) {
			batch.draw(coin, coinX.get(i), coinY.get(i));
			coinX.set(i, coinX.get(i)-4);
			coinRectangle.add(new Rectangle(coinX.get(i),coinY.get(i),coin.getWidth(),coin.getHeight()));
		}

		//bombs
		if(bombCount<250)
			bombCount++;
		else
		{
			bombCount=0;
			makeBombs();
		}
		bombRectangle.clear();
		for(int i = 0; i< bombY.size(); i++) {
			batch.draw(bomb, bombX.get(i), bombY.get(i));
			bombX.set(i, bombX.get(i)-6);
			bombRectangle.add(new Rectangle(bombX.get(i),bombY.get(i),bomb.getWidth(),bomb.getHeight()));
		}

		//while playing if the user clicks the man will jump
		if(Gdx.input.justTouched())
			velocity=-10;
		if (pause < 8) {
			pause++;
		} else {
			pause=0;
			if (manState < 3) {
				manState++;
			} else {
				manState = 0;
			}
		}

		velocity+=gravity;
		manY-=velocity;
		if(manY <= 0)
			manY=0;
	}

	public void resetTheGame()
	{
		gameState=1;
		manY=Gdx.graphics.getHeight() / 2;
		score = 0;

		coinRectangle.clear();
		coinX.clear();
		coinY.clear();
		coinCount=0;

		bombRectangle.clear();
		bombX.clear();
		bombY.clear();
		bombCount=0;
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
