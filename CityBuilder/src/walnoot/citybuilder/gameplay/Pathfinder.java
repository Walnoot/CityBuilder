package walnoot.citybuilder.gameplay;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class Pathfinder{
	private static final Pathfinder instance = new Pathfinder();
	
	public static Pathfinder get(){
		return instance;
	}
	
	//a field for reduced GC
	private Array<Node> openList = new Array<Node>(false, 64), closedList = new Array<Node>(false, 64);
	
	private DefaultGoal defaultGoal = new DefaultGoal();
	
	private Pool<Node> nodePool = new Pool<Pathfinder.Node>(){
		@Override
		protected Node newObject(){
			return new Node();
		}
	};
	
	private Pathfinder(){
	}
	
	public void poolNode(Node n){
		if(n != null) nodePool.free(n);
	}
	
	/**
	 * Calculates the shortest path to the goal
	 * 
	 * @param startX
	 * @param startY
	 * @param goalX
	 * @param goalY
	 * @return - A node whose parent is the next node on the path
	 */
	public Node getPath(City city, int startX, int startY, int goalX, int goalY){
		defaultGoal.setGoal(goalX, goalY);
		
		return getPath(city, startX, startY, defaultGoal);
	}
	
	public Node getPath(City city, int startX, int startY, Goal goal){
		openList.size = 0;
		closedList.size = 0;
		
		openList.add(nodePool.obtain().init(startX, startY, null, goal));
		
		while(openList.size > 0){
			Node current = getLowestF();
			
			if(goal.reachedGoal(current.x, current.y)){
				if(goal.reverseNodes()) return reverse(current);
				else return current;
			}
			
			openList.removeValue(current, true);
			closedList.add(current);
			
			checkNewNode(nodePool.obtain().init(current.x + 1, current.y, current, goal), city);
			checkNewNode(nodePool.obtain().init(current.x - 1, current.y, current, goal), city);
			checkNewNode(nodePool.obtain().init(current.x, current.y + 1, current, goal), city);
			checkNewNode(nodePool.obtain().init(current.x, current.y - 1, current, goal), city);
		}
		
		return null;
	}
	
	private Node reverse(Node head){
		//totally self made and totally not copied from the interwebs
		
		Node cursor = null;
		Node next = null;
		
		while(head != null){
			next = head.parent;
			head.parent = cursor;
			cursor = head;
			head = next;
		}
		
		return cursor;
	}
	
	private Node getLowestF(){
		int lowestF = Integer.MAX_VALUE;
		Node lowestNode = null;
		
		for(int i = 0; i < openList.size; i++){
			Node node = openList.get(i);
			int f = node.g + node.h;
			
			if(f < lowestF){
				lowestF = f;
				lowestNode = node;
			}
		}
		
		return lowestNode;
	}
	
	private void checkNewNode(Node node, City city){
		if(city.getModule(node.x, node.y) == null) return;
		if(isOnList(closedList, node) != null) return;
		
		Node openNode = isOnList(openList, node);
		if(openNode == null) openList.add(node);
		else{
			if(node.g < openNode.g){
				openNode.parent = node.parent;
				openNode.g = node.g;
			}
		}
	}
	
	private Node isOnList(Array<Node> nodes, Node node){
		for(Node n : nodes){
			if(n.x == node.x && n.y == node.y) return n;
		}
		
		return null;
	}
	
	public static class Node{
		public int x, y;
		private int g, h;
		public Node parent;
		
		private Node init(int x, int y, Node parent, Goal goal){
			this.x = x;
			this.y = y;
			this.parent = parent;
			
			if(parent != null) g = parent.g + 1;
			h = goal.getH(x, y);
			
			return this;
		}
	}
	
	public static interface Goal{
		public abstract boolean reachedGoal(int x, int y);
		
		public abstract int getH(int x, int y);
		
		public abstract boolean reverseNodes();
	}
	
	private static class DefaultGoal implements Goal{
		private int goalX, goalY;
		
		@Override
		public boolean reachedGoal(int x, int y){
			return x == goalX && y == goalY;
		}
		
		@Override
		public int getH(int x, int y){
			return Math.abs(x - goalX) + Math.abs(y - goalY);
		}
		
		private void setGoal(int x, int y){
			goalX = x;
			goalY = y;
		}
		
		@Override
		public boolean reverseNodes(){
			return true;
		}
	}
}
