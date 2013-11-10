package walnoot.citybuilder.modules;

import walnoot.citybuilder.Util;
import walnoot.citybuilder.gameplay.City;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.JsonValue;

public class Module{
	protected final City city;
	
	public final int x, y;
	public final int width, height;
	
	private ModelInstance modelInstance;
	
	private Component[] components;
	
	public Module(City city, int x, int y, JsonValue type){
		this.city = city;
		this.x = x;
		this.y = y;
		
		width = type.getInt("width");
		height = type.getInt("height");
		
		JsonValue jsonComponents = type.get("components");
		if(jsonComponents != null){
			components = new Component[jsonComponents.size];
			
			for(int i = 0; i < components.length; i++){
				components[i] = Component.getComponent(jsonComponents.getString(i), this, city);
			}
		}else{
			components = new Component[0];
		}
		
		modelInstance = new ModelInstance(Util.MODEL, type.getString("model", "unit"));
		city.setModelTransform(modelInstance, x, y);
		
		Util.setColor(modelInstance, Color.DARK_GRAY);
	}
	
	public void init(){
		Util.setColor(modelInstance, Color.WHITE);
	}
	
	public void update(){
		for(Component component : components){
			if(component != null) component.update();
		}
	}
	
	public void render(ModelBatch modelBatch){
		city.setModelTransform(modelInstance, x, y);
		
		modelBatch.render(modelInstance);
	}
}
