package walnoot.citybuilder.gameplay;

import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GroupPathfinder{
	public static final GroupPathfinder INSTANCE = new GroupPathfinder();
	
	private static final int CACHE_AREA_WIDTH = 50;
	
	private Vector2[] surroundingNodes = new Vector2[CACHE_AREA_WIDTH * CACHE_AREA_WIDTH];//an array of coordinates sorted by distance to the origin
	
	private GroupPathfinder(){
		for(int i = 0; i < CACHE_AREA_WIDTH * CACHE_AREA_WIDTH; i++){
			int x = (i % CACHE_AREA_WIDTH) - CACHE_AREA_WIDTH / 2;
			int y = (i / CACHE_AREA_WIDTH) - CACHE_AREA_WIDTH / 2;
			
			surroundingNodes[i] = new Vector2(x, y);
		}
		
		Comparator<Vector2> comparator = new Comparator<Vector2>(){
			@Override
			public int compare(Vector2 arg0, Vector2 arg1){
				if(arg0.len2() < arg1.len2()) return -1;
				else return 1;
			}
		};
		Arrays.sort(surroundingNodes, comparator);
	}
	
	public void calculatePaths(Array<Unit> units, City city, int goalX, int goalY){
		for(int i = 0, j = 0; i < units.size; i++){
			int testX = 0, testY = 0;
			
			do{
				Vector2 testVector = surroundingNodes[j++];
				
				testX = (int) (testVector.x) + goalX;
				testY = (int) (testVector.y) + goalY;
			}while(city.getModule(testX, testY) == null);
			
			units.get(i).walkTo((int) testX, (int) testY);
		}
	}
}
