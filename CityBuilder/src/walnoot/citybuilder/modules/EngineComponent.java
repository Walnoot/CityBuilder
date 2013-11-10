package walnoot.citybuilder.modules;

import walnoot.citybuilder.InputHandler;
import walnoot.citybuilder.Util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class EngineComponent extends Component{
	private Vector2 force = new Vector2();
	
	@Override
	public void update(){
		force.set(0f, 10f).rotate(city.getBody().getAngle() * MathUtils.radiansToDegrees);
		Vector2 forcePos = Util.TMP_2.set(module.x + module.width / 2f, module.y + module.height / 2f);
		
		boolean right = forcePos.x > city.getBody().getLocalCenter().x;
		
		int moveDir = 0;
		if(InputHandler.get().moveUp.isPressed()) moveDir = 1;
		if(InputHandler.get().moveDown.isPressed()) moveDir = -1;
		if(InputHandler.get().moveLeft.isPressed()) moveDir = right ? 1 : -1;
		if(InputHandler.get().moveRight.isPressed()) moveDir = right ? -1 : 1;
		
		if(moveDir == -1) force.scl(-1f);
		if(moveDir != 0) city.getBody().applyForce(force, city.getBody().getWorldPoint(forcePos), true);
	}
}
