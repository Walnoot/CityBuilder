package walnoot.citybuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.JsonValue;

public class IconRenderer{
	private final float SQRT_2 = (float) Math.sqrt(2);
	
	private ModelBatch modelBatch = new ModelBatch();
	
	public void render(FrameBuffer fbo, JsonValue moduleType){
		int width = moduleType.getInt("width");
		int height = moduleType.getInt("height");
		
		OrthographicCamera camera =
				new OrthographicCamera(SQRT_2 * ((width + height) / 2f), SQRT_2 * ((width + height) / 2f));
		camera.position.set(-1f, -1f, 1f);
		camera.rotate(45f, 1f, 0f, 0f);
		camera.rotate(-45f, 0f, 0f, 1f);
		camera.update();
		
		fbo.begin();
		
		Gdx.gl20.glClearColor(1f, 1f, 1f, 0f);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		modelBatch.begin(camera);
		
		ModelInstance instance = new ModelInstance(Util.MODEL, moduleType.getString("model"));
		
		//enable blending so the modelbatch writes alpha stuff
		for(Material material : instance.materials){
			material.set(new BlendingAttribute());
		}
		
		instance.transform.translate(-(width + 1) * 0.5f, -(height + 1) * 0.5f, 0f);
		modelBatch.render(instance);
		
		modelBatch.end();
		
		fbo.end();
	}
}
