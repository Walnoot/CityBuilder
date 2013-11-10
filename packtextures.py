import os
import sys

if sys.platform == 'win32':
	os.system("java -cp gdx.jar;gdx-tools.jar com.badlogic.gdx.tools.imagepacker.TexturePacker2 textures CityBuilder-android/assets/")
else:
	os.system("java -cp gdx.jar:gdx-tools.jar com.badlogic.gdx.tools.imagepacker.TexturePacker2 textures CityBuilder-android/assets/")