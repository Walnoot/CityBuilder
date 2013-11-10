package walnoot.citybuilder.gameplay;

import walnoot.citybuilder.Util;
import walnoot.citybuilder.gameplay.Pathfinder.Node;
import walnoot.citybuilder.modules.Module;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class City{
	private Array<Module> modules = new Array<Module>(false, 32);
	private Array<PlannedModule> plannedModules = new Array<PlannedModule>(false, 32);
	private Array<Unit> units = new Array<Unit>(false, 32);
	private Array<Unit> tmpUnitList = new Array<Unit>(false, 32);
	
	private Body body;
	private Matrix3 matrix = new Matrix3(), invMatrix = new Matrix3();
	
	private int tableWidth = 4;//width = height
	private Module[] moduleTable = new Module[tableWidth * tableWidth];
	
	private Vector3 tmp1 = new Vector3(), tmp2 = new Vector3();
	
	public City(World world){
		body = world.createBody(Util.getBodyDef());
	}
	
	public void update(){
		matrix.idt().translate(body.getPosition().x, body.getPosition().y)
				.rotate(body.getAngle() * MathUtils.radiansToDegrees);
		
		invMatrix.set(matrix).inv();
		
		for(Module m : modules){
			m.update();
		}
		
		for(Unit u : units){
			u.update();
		}
	}
	
	public void render(ModelBatch modelBatch){
		for(Module m : modules){
			m.render(modelBatch);
		}
		
		for(Unit u : units){
			u.render(modelBatch);
		}
		
		for(PlannedModule m : plannedModules){
			m.render(modelBatch);
		}
	}
	
	public Vector2 getMouseCityCoordinates(Camera cam){
		Ray ray = cam.getPickRay(Gdx.input.getX(), Gdx.input.getY());
		Intersector.intersectRayPlane(ray, Util.XY_PLANE, Util.TMP_3);
		
		Util.TMP_2.set(Util.TMP_3.x, Util.TMP_3.y).mul(invMatrix);//transform world coordinates to city coordinates
		
		return Util.TMP_2;
	}
	
	public void addModule(Module module){
		modules.add(module);
		module.init();
		
		PolygonShape shape = new PolygonShape();
		
		float x = module.x, y = module.y;
		shape.set(new float[]{x, y, x + module.width, y, x + module.width, y + module.height, x, y + module.height});
		
		body.createFixture(Util.getFixtureDef(shape));
		shape.dispose();
		
		addToModuleTable(module);
	}
	
	private void addToModuleTable(Module module){
		for(int i = 0; i < module.width; i++){
			for(int j = 0; j < module.height; j++){
				addToModuleTable(module, module.x + i, module.y + j);
			}
		}
	}
	
	private void addToModuleTable(Module module, int x, int y){
		int i = getTableIndex(x, y);
		
		if(i != -1) moduleTable[i] = module;
		else{
			tableWidth *= 2;
			moduleTable = new Module[tableWidth * tableWidth];
			
			for(Module m : modules){
				addToModuleTable(m);
			}
			
//			System.out.printf("Lookup table size increased, wasted space: %f percent\n", 100f
//					- ((float) modules.size * 100f) / (tableWidth * tableWidth));
		}
	}
	
	public void addPlannedModule(Module module){
		PlannedModule plannedModule = PlannedModule.get(module);
		plannedModules.add(plannedModule);
		
		Array<Unit> availableUnits = tmpUnitList;
		
		for(Unit unit : units){
			if(!unit.isBusy()) availableUnits.add(unit);
		}
		
		if(availableUnits.size != 0){
			UnitGoal goal = UnitGoal.get(availableUnits, module);
			Node path = Pathfinder.get().getPath(this, module.x, module.y, goal);
			
			if(path != null){
				goal.getClosedUnit().setPath(path);
				goal.getClosedUnit().setPlannedModule(plannedModule);
				
				plannedModule.setDesignatedUnit(goal.getClosedUnit());
			}
			
			availableUnits.size = 0;
		}
	}
	
	public void markCompleted(PlannedModule plannedModule){
		plannedModules.removeValue(plannedModule, true);
	}
	
	public boolean hasModule(int x, int y){
		return getModule(x, y) != null;
	}
	
	public Module getModule(int x, int y){
		int i = getTableIndex(x, y);
		if(i != -1) return moduleTable[i];
		else return null;
	}
	
	private int getTableIndex(int x, int y){
		x += tableWidth / 2;
		y += tableWidth / 2;
		
		if(x >= 0 && x < tableWidth && y >= 0 && y < tableWidth) return x + y * tableWidth;
		else return -1;
	}
	
	public void addUnit(Unit unit){
		units.add(unit);
	}
	
	public Array<Unit> getUnits(){
		return units;
	}
	
	public Array<PlannedModule> getPlannedModules(){
		return plannedModules;
	}
	
	public void selectUnits(Rectangle selection, PerspectiveCamera camera, Array<Unit> selected){
		float minX = Math.min(selection.x, selection.x + selection.width);
		float maxX = Math.max(selection.x, selection.x + selection.width);
		float minY = Math.min(selection.y, selection.y + selection.height);
		float maxY = Math.max(selection.y, selection.y + selection.height);
		
		for(Unit unit : units){
			Vector2 localPos = Util.TMP_2.set(unit.x + 0.5f, unit.y + 0.5f).mul(matrix);
			
			Vector3 screenPos = tmp1.set(localPos.x, localPos.y, 0.5f);
			tmp2.set(screenPos).add(camera.up.x * 0.5f, camera.up.y * 0.5f, camera.up.z * 0.5f);
			
			camera.project(screenPos);
			camera.project(tmp2);
			
			//hacky way to determine the rough size of an sphere of radius 1 in screen coords
			float screensize = tmp2.y - tmp1.y;
			
			if(minX <= screenPos.x + screensize && maxX >= screenPos.x - screensize && minY <= screenPos.y + screensize
					&& maxY >= screenPos.y - screensize){
				unit.setSelected(true);
				selected.add(unit);
			}else unit.setSelected(false);
		}
	}
	
	public Body getBody(){
		return body;
	}
	
	public Matrix3 getProjMatrix(){
		return matrix;
	}
	
	public Matrix3 getInvProjMatrix(){
		return invMatrix;
	}
	
	public void setModelTransform(ModelInstance model, float x, float y){
		Util.TMP_2.set(x, y).mul(matrix);
		model.transform.idt().translate(Util.TMP_2.x, Util.TMP_2.y, 0f)
				.rotate(0f, 0f, 1f, body.getAngle() * MathUtils.radiansToDegrees);
	}
}
