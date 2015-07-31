# SingleBlockMods

## Why small mods
The main goal of the project is to create quailty content which can easily be added to modpacks. This is easily done with a small scope of a few items or blocks. 

The small scope also provides developers with a good focus. Allowing for something that is normally simple to have a wide range of behaviors. Including support for features that are normally ignored because of scope or size of a mod.

## Why the sub folders

Each sub folder is it's own complete Minecraft Forge standalone mod. The idea behind doing it this way is to cut down on the number of github repos required to work on so many mods. Especially with the already long list of repos on the BuiltBrokenModding organization account.

## How to Build The mods

Currently each sub project acts like its own gradle project. All you need to do is go into BuiltBrokenScripts and run built-local.bat. This will do everything for you including injecting some runtime arguments.


## How to setup a workspace

### How I do it
Personally I setup an Empty Module in IDEA that contains all the Minecraft & Forge content. Then extend this module for each mod with a new module. Which includes the source code and resource folder. After this I create another empty project and add all of the modules to it. This part allows IDEA to load all of the mods up in the run config. 

If I plan to do unit testing I also add the MinecraftJUnit project to my workspace extending the empty module. Then I include any testing folder that comes with the mods.

### Normal setup

If you don't plan to use my setup just run the dev.bat in the BuiltBrokenScripts folder. This will setup a new eclipse or IDEA workspace for the mod. It will however only setup the workspace for a single mod. Which means you will need to do this for every single mod you plan to mess around with.

## Pull Requests

The code is not open sourced so this will not be strait forward. First you need to understand that anything you commit to the repo belongs to Built Broken Modding. Second you need to agree to Built Broken Modding's CLA which covers the first step plus a little more legal speak. The idea behind the CLA is to protect the project and its team.

By submitting anything to the repo you agree to this CLA.

## Using the code

The project is not open source so there are rules to using the code. Mainly you can't use the code directly in another project without permission. Unless this project is for educational use and credit is given. Beyond this limit feel free to learn from the code freely. After all that is the reason behind having the code visualable. Well beyond getting translations, requests, and pull requests.