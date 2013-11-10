package walnoot.citybuilder.screens;

import walnoot.citybuilder.BuildListListener;
import walnoot.citybuilder.CityGame;
import walnoot.citybuilder.IconRenderer;
import walnoot.citybuilder.InputHandler;
import walnoot.citybuilder.Util;
import walnoot.citybuilder.gameplay.City;
import walnoot.citybuilder.gameplay.GroupPathfinder;
import walnoot.citybuilder.gameplay.Unit;
import walnoot.citybuilder.modules.Module;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;

public class GameScreen extends UpdateScreen{
	private static final Color BUILD_MARKER_POSSIBLE = new Color(0x00ff0088);
	private static final Color BUILD_MARKER_IMPOSSIBLE = new Color(0xff000088);
	
	private static final int MODULE_ICON_SIZE = 64;
	private static final float CAM_SPEED = 5f;//units per second
	private static final float CAM_TURN_SPEED = 120f;//degrees per second
	private static final float CAM_ZOOM_SPEED = 0.1f;
	
	private static final Vector2 TMP = new Vector2();
	
	private World world;
	private Box2DDebugRenderer renderer;
	private City city;
	private PerspectiveCamera camera;
	
	private ModelBatch modelBatch = new ModelBatch();
	
	private Plane xyPlane = new Plane(new Vector3(0f, 0f, 1f), 0f);
	private Vector3 tmpVector = new Vector3();
	
	private Image selectionImage;
	private Rectangle selection = new Rectangle();
	private Array<Unit> selectedUnits = new Array<Unit>();
	private int buildIndex = -1;
	
	private JsonValue jsonRoot;
	
	private IconRenderer fboRenderer = new IconRenderer();
	private Texture[] moduleIcons;
	private Table buildListTable;
	private Image[] buildImages;
	
	private ModelInstance rectangle;
	
	public GameScreen(CityGame game){
		super(game);
		
		world = new World(new Vector2(), true);
		
		renderer = new Box2DDebugRenderer();
		
		camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0f, 5f, 5f);
		camera.rotate(20f, 1f, 0f, 0f);
		
		city = new City(world);
		
		jsonRoot = Util.JSON_READER.parse(Gdx.files.internal("modules.json"));
		
		for(int i = 0; i < 10; i++){
			for(int j = 0; j < 10; j++){
				city.addModule(new Module(city, i, j, jsonRoot.get("raft")));
			}
		}
		
		city.addModule(new Module(city, -1, 0, jsonRoot.get("engine")));
		city.addModule(new Module(city, 10, 0, jsonRoot.get("engine")));
		city.addModule(new Module(city, -1, 2, jsonRoot.get("engine")));
		city.addModule(new Module(city, 10, 2, jsonRoot.get("engine")));
		
		city.addUnit(new Unit(city, 0, 0));
		city.addUnit(new Unit(city, 0, 1));
		city.addUnit(new Unit(city, 1, 1));
		city.addUnit(new Unit(city, 1, 0));
		
		setupUI();
		
		ModelBuilder builder = new ModelBuilder();
		Material material = new Material(ColorAttribute.createDiffuse(new Color(0xFFFFFF88)), new BlendingAttribute());
		Model rect =
				builder.createRect(0f, 0f, 0f, 1f, 0f, 0f, 1f, 1f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, material, Usage.Position
						| Usage.Normal);
		
		rectangle = new ModelInstance(rect);
	}
	
	private void setupUI(){
		game.getStage().clear();
		
		selectionImage = new Image(Util.getNinePatchDrawable("ui/selection"));
		selectionImage.setVisible(false);
		
		game.getStage().addActor(selectionImage);
	}
	
	private void setupBuildList(){
		buildListTable = new Table();
		buildListTable.setVisible(false);
		buildListTable.left().top().setFillParent(true);
		game.getStage().addActor(buildListTable);
		
		buildImages = new Image[moduleIcons.length];
		
		for(int i = 0; i < moduleIcons.length; i++){
			Texture texture = moduleIcons[i];
			
			Sprite sprite = new Sprite(texture);
			sprite.flip(false, true);
			Image image = new Image(sprite);
			image.setColor(Color.DARK_GRAY);
			image.addListener(new BuildListListener(this, i));
			buildImages[i] = image;
			
			buildListTable.add(image);
			buildListTable.row();
		}
	}
	
	@Override
	protected void render(){
		Gdx.gl.glClearColor(.3f, .3f, .8f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		if(moduleIcons == null){
			moduleIcons = new Texture[jsonRoot.size];
			
			for(int i = 0; i < jsonRoot.size; i++){
				FrameBuffer frameBuffer = new FrameBuffer(Format.RGBA8888, MODULE_ICON_SIZE, MODULE_ICON_SIZE, true);
				fboRenderer.render(frameBuffer, jsonRoot.get(i));
				moduleIcons[i] = frameBuffer.getColorBufferTexture();
			}
			
			setupBuildList();
		}
		
		camera.update();
		
		renderer.render(world, camera.combined);
		
		modelBatch.begin(camera);
		city.render(modelBatch);
		if(buildIndex != -1) modelBatch.render(rectangle);
		modelBatch.end();
	}
	
	@Override
	public void update(){
		city.update();
		world.step(CityGame.SECONDS_PER_UPDATE, 6, 2);
		
		Vector2 coords = city.getMouseCityCoordinates(camera);
		
		int x = (int) Math.floor(coords.x);
		int y = (int) Math.floor(coords.y);
		
		if(buildIndex != -1){
			city.setModelTransform(rectangle, x, y);
			
			JsonValue type = jsonRoot.get(buildIndex);
			
			int width = type.getInt("width");
			int height = type.getInt("height");
			
			rectangle.transform.scale(width, height, 1f);
			
			boolean canBuild = true;
			
			outer: for(int i = 0; i < width; i++){
				for(int j = 0; j < height; j++){
					if(city.hasModule(x + i, y + j)){
						canBuild = false;
						
						break outer;
					}
				}
			}
			
			Color color;
			
			if(canBuild){
				if(InputHandler.get().isJustTouched(Buttons.RIGHT))
					city.addPlannedModule(new Module(city, x, y, type));
				
				color = BUILD_MARKER_POSSIBLE;
			}else color = BUILD_MARKER_IMPOSSIBLE;
			
			Util.setColor(rectangle, color);
			
		}else if(InputHandler.get().isJustTouched(Buttons.RIGHT)){
			if(city.hasModule(x, y)) GroupPathfinder.INSTANCE.calculatePaths(selectedUnits, city, x, y);
		}
		
		handleCamMovement();
	}
	
	private void handleCamMovement(){
		Vector2 translation = TMP.set(0f, 0f);
		
		if(InputHandler.get().camRight.isPressed()) translation.add(1f, 0f);
		if(InputHandler.get().camLeft.isPressed()) translation.add(-1f, 0f);
		if(InputHandler.get().camUp.isPressed()) translation.add(0f, 1f);
		if(InputHandler.get().camDown.isPressed()) translation.add(0f, -1f);
		
		if(Gdx.input.getX() <= 1) translation.add(-1f, 0f);
		if(Gdx.input.getX() >= Gdx.graphics.getWidth() - 1) translation.add(1f, 0f);
		if(Gdx.input.getY() <= 1) translation.add(0f, 1f);
		if(Gdx.input.getY() >= Gdx.graphics.getHeight() - 1) translation.add(0f, -1f);
		
		translation.nor();
		
		translation.rotate(Util.TMP_2.set(camera.direction.x, camera.direction.y).nor().angle() - 90f).scl(
				CAM_SPEED * CityGame.SECONDS_PER_UPDATE);
		
		camera.translate(translation.x, translation.y, 0f);
		
		int rotateDir = 0;
		if(InputHandler.get().turnRight.isPressed()) rotateDir += 1;
		if(InputHandler.get().turnLeft.isPressed()) rotateDir -= 1;
		
		int scroll = InputHandler.get().getScrollAmount();
		
		Ray ray = null;
		if(rotateDir != 0 || scroll != 0)
			ray = camera.getPickRay(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);
		
		if(rotateDir != 0){
			Intersector.intersectRayPlane(ray, xyPlane, tmpVector);
			camera.rotateAround(tmpVector, Vector3.Z, CityGame.SECONDS_PER_UPDATE * CAM_TURN_SPEED * rotateDir);
		}
		
		if(scroll != 0) camera.translate(ray.direction.scl(-scroll * (camera.position.z - 1f) * CAM_ZOOM_SPEED));
	}
	
	public void setBuildIndex(int index){
		buildIndex = index;
		
		for(int i = 0; i < buildImages.length; i++){
			if(i == index) buildImages[i].setColor(Color.WHITE);
			else buildImages[i].setColor(Color.DARK_GRAY);
		}
	}
	
	@Override
	public void resize(int width, int height){
		super.resize(width, height);
		
		camera.viewportWidth = width;
		camera.viewportHeight = height;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button){
		if(button == Buttons.LEFT){
			selectionImage.setVisible(true);
			
			selection.set(screenX, Gdx.graphics.getHeight() - screenY, 0f, 0f);
			
			setSelectionImageSize();
			selectionImage.invalidate();
		}
		
		return super.touchDown(screenX, screenY, pointer, button);
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer){
		selection.width = screenX - selection.x;
		selection.height = (Gdx.graphics.getHeight() - screenY) - selection.y;
		
		setSelectionImageSize();
		selectionImage.invalidate();
		
		return super.touchDragged(screenX, screenY, pointer);
	}
	
	private void setSelectionImageSize(){
		float X1 = selection.x;
		float Y1 = selection.y;
		float X2 = selection.x + selection.width;
		float Y2 = selection.y + selection.height;
		
		selectionImage.setPosition(X1, Y1);
		selectionImage.setSize(X2 - X1, Y2 - Y1);
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button){
		if(button == Buttons.LEFT){
			selectionImage.setVisible(false);
			
			selectedUnits.size = 0;
			city.selectUnits(selection, camera, selectedUnits);
			buildListTable.setVisible(selectedUnits.size != 0);
			setBuildIndex(-1);
		}
		
		return super.touchUp(screenX, screenY, pointer, button);
	}
}
