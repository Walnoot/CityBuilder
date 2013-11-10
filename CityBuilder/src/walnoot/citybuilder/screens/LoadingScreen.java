package walnoot.citybuilder.screens;

import walnoot.citybuilder.CityGame;
import walnoot.citybuilder.Util;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

public class LoadingScreen extends UpdateScreen{
	private static final String ATLAS_NAME = "pack.atlas";
	private static final String MODEL_NAME = "models.g3db";
	
	private AssetManager assetManager;
	
	public LoadingScreen(CityGame game){
		super(game);
		
		assetManager = new AssetManager();
		assetManager.load(ATLAS_NAME, TextureAtlas.class);
		assetManager.load(MODEL_NAME, Model.class);
	}
	
	@Override
	protected void render(){
	}
	
	@Override
	public void update(){
		if(assetManager.update()){
			Util.ATLAS = assetManager.get(ATLAS_NAME);
			Util.SKIN = Util.getSkin();
			Util.MODEL = assetManager.get(MODEL_NAME, Model.class);
			
			for(Material m : Util.MODEL.materials){
				TextureAttribute attr = m.get(TextureAttribute.class, TextureAttribute.Diffuse);
				
				if(attr != null){
					attr.textureDescription.magFilter = TextureFilter.Nearest;
					attr.textureDescription.minFilter = TextureFilter.Nearest;
				}
			}
			
//			game.setScreen(new MainScreen(game));
			game.setScreen(new GameScreen(game));
		}
	}
}
