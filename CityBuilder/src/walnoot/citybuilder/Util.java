package walnoot.citybuilder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.JsonReader;

public class Util{
	public static final Vector2 TMP_2 = new Vector2();
	public static final Vector3 TMP_3 = new Vector3();
	
	public static final Plane XY_PLANE = new Plane(Vector3.Z, 0f);
	
	public static final JsonReader JSON_READER = new JsonReader();
	
	public static final CircleShape CIRCLE_SHAPE = getCircleShape(1f);
	public static TextureAtlas ATLAS;
	public static Skin SKIN;
	public static NinePatchDrawable BORDER_DRAWABLE;
	public static Model MODEL;
	
	public static Body getCircleBody(World world, Vector2 position){
		return getCircleBody(world, position, CIRCLE_SHAPE);
	}
	
	public static Skin getSkin(){
		Skin skin = new Skin();
		
		BitmapFont font = new BitmapFont();
		
		BORDER_DRAWABLE = getNinePatchDrawable("ui/border");
		
		skin.add("default", new TextButtonStyle(BORDER_DRAWABLE, null, null, font));
		skin.add("default", new ListStyle(font, Color.WHITE, Color.GRAY, getNinePatchDrawable("ui/selection")));
		
		return skin;
	}
	
	public static NinePatchDrawable getNinePatchDrawable(String regionName){
		AtlasRegion region = ATLAS.findRegion(regionName);
		
		int border = region.getRegionWidth() / 2 - 1;
		NinePatch ninePatch = new NinePatch(region, border, border, border, border);
		return new NinePatchDrawable(ninePatch);
	}
	
	public static Body getCircleBody(World world, Vector2 position, CircleShape shape){
		BodyDef bodyDef = getBodyDef();
		
		bodyDef.position.set(position);
		Body body = world.createBody(bodyDef);
		
		body.createFixture(getFixtureDef(shape));
		
		return body;
	}
	
	public static FixtureDef getFixtureDef(Shape shape){
		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 1f; // Make it bounce a little bit
		
		return fixtureDef;
	}
	
	public static CircleShape getCircleShape(float radius){
		CircleShape circle = new CircleShape();
		circle.setRadius(radius);
		
		return circle;
	}
	
	public static BodyDef getBodyDef(){
		// First we create a body definition
		BodyDef bodyDef = new BodyDef();
		bodyDef.linearDamping = 0.5f;
		bodyDef.angularDamping = 0.9f;
		// We set our body to dynamic, for something like ground which doesnt move we would set it to StaticBody
		bodyDef.type = BodyType.DynamicBody;
		// Set our body's starting position in the world
		bodyDef.position.set(0f, 0f);
		
		return bodyDef;
	}
	
	public static void setColor(ModelInstance instance, Color color){
		for(Material material : instance.materials){
			Color oldColor = material.get(ColorAttribute.class, ColorAttribute.Diffuse).color;
			oldColor.set(color);
		}
	}
	
	public static boolean inRectangle(int rectX, int rectY, int width, int height, int x, int y){
		int dx = x - rectX;
		int dy = y - rectY;
		
		return(dx >= 0 && dy >= 0 && dx < width && dy < height);
	}
	
	public static boolean neighbours(int rectX, int rectY, int width, int height, int x, int y){
		return Util.inRectangle(rectX - 1, rectY, width + 2, height, x, y)
				|| Util.inRectangle(rectX, rectY - 1, width, height + 2, x, y);
	}
	
	public static final float lerp(float start, float end, float t){
		return t * start + (1f - t) * end;
	}
}
