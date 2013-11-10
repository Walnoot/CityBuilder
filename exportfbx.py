#/usr/bin/python
import sys
import bpy 

if (len(sys.argv) < 9):
  print("usage: exportfbx.py\n")
  exit(1)

# Extract params from command-line that we'll need for the fbx-export operation.
# This assumes exportfbx.py is called via the following console command:
#
#blender -b myfile.blend --python "exportfbx.py" -- ModelName myfile.fbx -Y Z
#

fbxPathname = sys.argv[6]
axisFwd = sys.argv[7]
axisUp = sys.argv[8]

#bpy.context.scene.objects.active = bpy.context.scene.objects[blenderObjectName]

modelNames = ["Raft", "Boat", "Unit"]

# Make sure all other objects are deselected
for ob in bpy.data.objects:
  ob.select = ob.name in modelNames

bpy.ops.export_scene.fbx(
    use_selection=1,
    filepath=fbxPathname,
    axis_forward=axisFwd,
    axis_up=axisUp)