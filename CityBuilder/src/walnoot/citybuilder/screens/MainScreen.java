package walnoot.citybuilder.screens;

import walnoot.citybuilder.CityGame;
import walnoot.citybuilder.Util;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MainScreen extends UpdateScreen{
	private Stage stage;
	
	public MainScreen(final CityGame game){
		super(game);
		
		stage = game.getStage();
		stage.clear();
		
		Table table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		
		TextButton button = new TextButton("START GAME", Util.SKIN);
		button.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				game.setScreen(new GameScreen(game));
			}
		});
		table.add(button);
	}
	
	@Override
	protected void render(){
	}
	
	@Override
	public void update(){
	}
}
