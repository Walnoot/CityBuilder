package walnoot.citybuilder.modules;

import walnoot.citybuilder.gameplay.City;

public abstract class Component{
	private static final String PACKAGE_PREFIX = Component.class.getPackage().getName() + ".";
	
	protected Module module;
	protected City city;
	
	public abstract void update();
	
	public static Component getComponent(String string, Module module, City city){
		try{
			Object object = Class.forName(PACKAGE_PREFIX + string).newInstance();
			
			if(object instanceof Component){
				Component component = (Component) object;
				component.city = city;
				component.module = module;
				
				return component;
			}
		}catch(Exception e){
			System.out.println("Couldnt instantiate the component by name " + string);
			e.printStackTrace();
		}
		
		return null;
	}
}
