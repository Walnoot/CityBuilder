package walnoot.citybuilder.gameplay;

import walnoot.citybuilder.CityGame;
import walnoot.citybuilder.Util;
import walnoot.citybuilder.gameplay.Pathfinder.Node;
import walnoot.citybuilder.modules.Module;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class Unit{
	private static final int WALK_TIME = (int) (0.25f * CityGame.UPDATES_PER_SECOND);
	private static final int BUILD_TIME = (int) (2f * CityGame.UPDATES_PER_SECOND);
	
	private final City city;
	public int x, y;
	private float renderX, renderY;
	
	private Node path;
	private int walkTimer = 0;
	
	private boolean selected;
	
	private ModelInstance modelInstance;
	
	private PlannedModule plannedModule;
	
	private int buildTimer;
	
	public Unit(City city, int x, int y){
		this.city = city;
		this.x = x;
		this.y = y;
		
		modelInstance = new ModelInstance(Util.MODEL, "Unit");
		setSelected(false);
		city.setModelTransform(modelInstance, x, y);
	}
	
	public void update(){
		float targetX, targetY;
		
		if(path != null && path.parent != null){
			targetX = Util.lerp(x, path.parent.x, (float) walkTimer / WALK_TIME);
			targetY = Util.lerp(y, path.parent.y, (float) walkTimer / WALK_TIME);
		}else{
			targetX = x;
			targetY = y;
		}
		
		renderX += (targetX - renderX) * 0.1f;
		renderY += (targetY - renderY) * 0.1f;
		
		city.setModelTransform(modelInstance, renderX, renderY);
		
		if(walkTimer-- == 0){
			walkTimer = WALK_TIME;
			
			if(!(path == null || path.parent == null)){
				x = path.parent.x;
				y = path.parent.y;
				
				Pathfinder.get().poolNode(path);
				
				path = path.parent;
			}
		}
		
		if(plannedModule != null){
			Module goal = plannedModule.getModule();
			if(Util.neighbours(goal.x, goal.y, goal.width, goal.height, x, y)){
				if(buildTimer++ == BUILD_TIME){
					buildTimer = 0;
					
					city.addModule(goal);
					
					PlannedModule.free(plannedModule);
					plannedModule = null;
				}
			}
		}
	}
	
	public void render(ModelBatch modelBatch){
		modelBatch.render(modelInstance);
	}
	
	public void walkTo(int x, int y){
		setPath(Pathfinder.get().getPath(city, getFutureX(), getFutureY(), x, y));
		
		walkTimer = WALK_TIME;
	}
	
	public void setPath(Node newPath){
		if(path == null) path = newPath;
		else path.parent = newPath;
	}
	
	public int getFutureX(){
		if(path == null || path.parent == null) return x;
		else return path.parent.x;
	}
	
	public int getFutureY(){
		if(path == null || path.parent == null) return y;
		else return path.parent.y;
	}
	
	public boolean isBusy(){
		return plannedModule != null;
	}
	
	public void setSelected(boolean selected){
		this.selected = selected;
		
		Util.setColor(modelInstance, selected ? Color.RED : Color.WHITE);
	}
	
	public void setPlannedModule(PlannedModule plannedModule){
		this.plannedModule = plannedModule;
	}
}
