package walnoot.citybuilder.gameplay;

import walnoot.citybuilder.modules.Module;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Pool;

/**
 * A module that is planned to be built
 */
public class PlannedModule{
	private static Pool<PlannedModule> pool = new Pool<PlannedModule>(){
		@Override
		protected PlannedModule newObject(){
			return new PlannedModule();
		}
	};
	
	public static PlannedModule get(Module module){
		PlannedModule plannedModule = pool.obtain();
		plannedModule.module = module;
		plannedModule.designatedUnit = null;
		
		return plannedModule;
	}
	
	public static void free(PlannedModule plan){
		pool.free(plan);
	}
	
	private Module module;
	private Unit designatedUnit;
	
	private PlannedModule(){
	}
	
	public void render(ModelBatch batch){
		module.render(batch);
	}
	
	public Module getModule(){
		return module;
	}
	
	public void setDesignatedUnit(Unit designatedUnit){
		if(isDesignated()) throw new RuntimeException("Designated Unit already set!");
		this.designatedUnit = designatedUnit;
	}
	
	public boolean isDesignated(){
		return designatedUnit != null;
	}
}
