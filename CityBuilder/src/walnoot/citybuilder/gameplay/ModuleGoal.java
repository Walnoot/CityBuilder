package walnoot.citybuilder.gameplay;

import walnoot.citybuilder.Util;
import walnoot.citybuilder.gameplay.Pathfinder.Goal;
import walnoot.citybuilder.modules.Module;

public class ModuleGoal extends Goal{
	private static ModuleGoal instance = new ModuleGoal();
	
	/**
	 * @return A temporary instance, don't safe references, not thread-safe
	 */
	public static ModuleGoal get(Module goal){
		instance.goal = goal;
		return instance;
	}
	
	private Module goal;
	
	private ModuleGoal(){
	}
	
	@Override
	public boolean reachedGoal(int x, int y){
		return Util.neighbours(goal.x, goal.y, goal.width, goal.height, x, y);
	}
	
	@Override
	public int getH(int x, int y){
		return Math.abs(x - goal.x) + Math.abs(y - goal.y);
	}
	
	@Override
	public boolean reverseNodes(){
		return true;
	}
}
