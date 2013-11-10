package walnoot.citybuilder;

import walnoot.citybuilder.screens.GameScreen;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class BuildListListener extends InputListener{
	private final GameScreen screen;
	private final int index;
	
	public BuildListListener(GameScreen screen, int index){
		this.screen = screen;
		this.index = index;
	}
	
	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
		screen.setBuildIndex(index);
		
		if(button == Buttons.LEFT) return true;
		return false;
	}
}
