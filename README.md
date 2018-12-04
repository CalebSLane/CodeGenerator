# CodeGenerator

How to use:

Clone git repository locally

File > Import

Select "Existing Maven Project"

Browse and select the toplevel CodeGenerator directory for Root directory

Ensure CodeGenerator/src is configured on buildpath Source tab (not src/java/main or anything else)

Ensure java 1.8 on build path

add all OE jars to build path (easiest to go to Java build path > Add External Jars > navigate to OE lib directory > select all > open )

Change compiler level to 1.8

right click project > Maven > update project

Run > run configurations

ensure Main class is codegenerator.main.App in Main tab

open Arguments tab

enter under Program arguments path/to/your/OE/code ie. C:\Users\caleb\Documents\UofW\OpenELIS\openelisglobal-core

Apply and run (ignore errors exist warning)

newly generated controllers and forms should be in src/spring/generated/ (refresh project if not visible)
