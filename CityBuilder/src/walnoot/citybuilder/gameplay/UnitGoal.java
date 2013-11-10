package walnoot.citybuilder.gameplay;

import walnoot.citybuilder.Util;
import walnoot.citybuilder.gameplay.Pathfinder.Goal;
import walnoot.citybuilder.gameplay.Pathfinder.Node;
import walnoot.citybuilder.modules.Module;

import com.badlogic.gdx.utils.Array;

public class UnitGoal extends Goal{
	private static final UnitGoal instance = new UnitGoal();
	
	public static UnitGoal get(Array<Unit> units, Module start){
		instance.units = units;
		instance.start = start;
		instance.closedUnit = null;
		
		return instance;
	}
	
	private Module start;
	private Array<Unit> units;
	private Unit closedUnit;
	
	private UnitGoal(){
	}
	
	@Override
	public boolean reachedGoal(int x, int y){
		for(Unit unit : units){
			if(unit.x == x && unit.y == y){
				closedUnit = unit;
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean canWalk(City city, int x, int y){
		return super.canWalk(city, x, y) || Util.inRectangle(start.x, start.y, start.width, start.height, x, y);
	}
	
	@Override
	public boolean reverseNodes(){
		return false;
	}
	
	@Override
	public Node processPath(Node path){
		Node newPath = path;
		
		while(path != null){
			if(Util.neighbours(start.x, start.y, start.width, start.height, path.x, path.y)) path.parent = null;
			
			path = path.parent;
		}
		
		return newPath;
	}
	
	public Unit getClosedUnit(){
		return closedUnit;
	}
}
