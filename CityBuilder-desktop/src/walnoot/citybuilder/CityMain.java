package walnoot.citybuilder;

import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class CityMain{
	private static final boolean FULLSCREEN = false;
	
	public static void main(String[] args){
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "CityBuilder";
		cfg.useGL20 = true;
		cfg.width = 1280;
		cfg.height = 720;
		cfg.resizable = false;
		
		LwjglApplication app = new LwjglApplication(new CityGame(), cfg);
		
		if(FULLSCREEN){
			DisplayMode highestResMode = null;
			for(DisplayMode mode : app.getGraphics().getDisplayModes()){
				if(highestResMode == null) highestResMode = mode;
				else if(mode.width * mode.height > highestResMode.width * highestResMode.height) highestResMode = mode;
			}
			
			app.getGraphics().setDisplayMode(highestResMode);
		}
	}
}
