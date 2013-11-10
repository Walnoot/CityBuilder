import os

os.system("blender -b models/models.blend --python \"exportfbx.py\" -- models.fbx Y Z")
os.system("fbx-conv-win32 -f models.fbx CityBuilder-android/assets/models.g3db")