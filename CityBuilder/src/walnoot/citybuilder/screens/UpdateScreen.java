package walnoot.citybuilder.screens;

import walnoot.citybuilder.CityGame;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;

public abstract class UpdateScreen implements Screen, InputProcessor{
	protected CityGame game;
	
	public UpdateScreen(CityGame game){
		this.game = game;
	}
	
	@Override
	public void render(float delta){
		render();
	}
	
	protected abstract void render();
	
	public abstract void update();
	
	@Override
	public void resize(int width, int height){
	}
	
	@Override
	public void show(){
	}
	
	@Override
	public void hide(){
	}
	
	@Override
	public void pause(){
	}
	
	@Override
	public void resume(){
	}
	
	@Override
	public void dispose(){
	}
	
	@Override
	public boolean keyDown(int keycode){
		return false;
	}
	
	@Override
	public boolean keyTyped(char character){
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode){
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY){
		return false;
	}
	
	@Override
	public boolean scrolled(int amount){
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button){
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer){
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button){
		return false;
	}
}
