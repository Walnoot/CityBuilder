package walnoot.citybuilder;

import static com.badlogic.gdx.Input.Keys.*;

import java.util.ArrayList;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;

public class InputHandler implements InputProcessor{
	private static final InputHandler instance = new InputHandler();
	
	public static InputHandler get(){
		return instance;
	}
	
	public Key camUp = new Key(W);
	public Key camDown = new Key(S);
	public Key camLeft = new Key(A);
	public Key camRight = new Key(D);
	
	public Key moveUp = new Key(UP);
	public Key moveDown = new Key(DOWN);
	public Key moveLeft = new Key(LEFT);
	public Key moveRight = new Key(RIGHT);
	
	public Key turnRight = new Key(Q);
	public Key turnLeft = new Key(E);
	
	public Key test = new Key(SPACE);
	
	public Key debug = new Key(TAB);
	
	private ArrayList<Key> keys;
	private boolean keyDown;
	private int scrollAmount;
	private boolean[] justTouched = new boolean[3];
	
	private InputHandler(){
	}
	
	/**
	 * Make sure to call after game logic update() is called
	 */
	public void update(){
		for(int i = 0; i < keys.size(); i++){
			keys.get(i).update();
		}
		
		keyDown = false;
		scrollAmount = 0;
		
		for(int i = 0; i < justTouched.length; i++){
			justTouched[i] = false;
		}
	}
	
	public boolean isAnyKeyDown(){
		return keyDown;
	}
	
	public int getScrollAmount(){
		return scrollAmount;
	}
	
	public boolean isJustTouched(){
		return isJustTouched(Buttons.LEFT);
	}
	
	public boolean isJustTouched(int button){
		return justTouched[button];
	}
	
	public boolean keyDown(int keyCode){
		for(int i = 0; i < keys.size(); i++){
			if(keys.get(i).has(keyCode)) keys.get(i).press();
		}
		
		keyDown = true;
		
		return false;
	}
	
	public boolean keyUp(int keyCode){
		for(int i = 0; i < keys.size(); i++){
			if(keys.get(i).has(keyCode)) keys.get(i).release();
		}
		
		return false;
	}
	
	public boolean keyTyped(char character){
		return false;
	}
	
	public boolean touchDown(int x, int y, int pointer, int button){
		justTouched[button] = true;
		return false;
	}
	
	public boolean touchUp(int x, int y, int pointer, int button){
		return false;
	}
	
	public boolean touchDragged(int x, int y, int pointer){
		return false;
	}
	
	public boolean touchMoved(int x, int y){
		return false;
	}
	
	public boolean scrolled(int amount){
		scrollAmount += amount;
		
		return false;
	}
	
	public boolean mouseMoved(int screenX, int screenY){
		return false;
	}
	
	public class Key{
		private final int[] keyCodes;
		private boolean pressed, justPressed;
		
		public Key(int... keyCodes){
			this.keyCodes = keyCodes;
			
			if(keys == null) keys = new ArrayList<Key>();
			keys.add(this);
		}
		
		private void update(){
			justPressed = false;
		}
		
		public boolean has(int keyCode){
			for(int i = 0; i < keyCodes.length; i++){
				if(keyCodes[i] == keyCode) return true;
			}
			
			return false;
		}
		
		private void press(){
			pressed = true;
			justPressed = true;
		}
		
		private void release(){
			pressed = false;
		}
		
		public boolean isPressed(){
			return pressed;
		}
		
		public boolean isJustPressed(){
			return justPressed;
		}
	}
}
