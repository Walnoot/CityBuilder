package walnoot.citybuilder;

import walnoot.citybuilder.screens.LoadingScreen;
import walnoot.citybuilder.screens.UpdateScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class CityGame extends Game{
	public static final float UPDATES_PER_SECOND = 60f;
	public static final float SECONDS_PER_UPDATE = 1 / UPDATES_PER_SECOND;
	
	private UpdateScreen updateScreen;
	private float unprocessedSeconds;
	
	private InputMultiplexer inputs = new InputMultiplexer();
	
	private Stage stage;
	
	@Override
	public void create(){
		Gdx.input.setInputProcessor(inputs);
		
		stage = new Stage();
		addInputProcessor(stage);
		addInputProcessor(InputHandler.get());
		
		setScreen(new LoadingScreen(this));
	}
	
	@Override
	public void render(){
		if(Gdx.input.isKeyPressed(Keys.ESCAPE)) Gdx.app.exit();
		
		unprocessedSeconds += Gdx.graphics.getDeltaTime();
		while(unprocessedSeconds > SECONDS_PER_UPDATE){
			unprocessedSeconds -= SECONDS_PER_UPDATE;
			
			updateScreen.update();
			InputHandler.get().update();
		}
		
		super.render();
		
		stage.act(CityGame.SECONDS_PER_UPDATE);
		stage.draw();
		
		Table.drawDebug(stage);
	}
	
	public boolean needsGL20(){
		return true;
	}
	
	@Override
	public void setScreen(Screen screen){
		inputs.removeProcessor(updateScreen);
		
		if(!(screen instanceof UpdateScreen))
			throw new IllegalArgumentException("Screen must be instace of UpdateScreen");
		
		super.setScreen(screen);
		updateScreen = (UpdateScreen) screen;
		
		inputs.addProcessor((InputProcessor) screen);
	}
	
	public void addInputProcessor(InputProcessor processor){
		inputs.addProcessor(processor);
	}
	
	public Stage getStage(){
		return stage;
	}
}
