iChunUtil
=========

Rebuilt mostly from scratch and some salvage from the 1.18 partial port. 

Completely revamped workspace to allow Fabric, Forge and NeoForge versions to be built from one project. (That took too much time to figure out)

Allows for a specific `all` composite build to be in the same parent folder as iChunUtil to pull in all projects in the folder (see `settings.gradle`)

Building The Mod
----------------

On Windows: Run `build.cmd`.

On other OSes: Gradle command line. Subprojects with output: `fabric`, `forge`, `neoforge`. Subproject `common` also exists and is compiled against Fabric, and the srcDirs are pulled in on modloader-based build. The Gradle workspace also sets up an `api` subproject if that exists.

If the build fails due to ReobfJar/SignJar (Forge) or OverlappingFileLock (NeoForge), just attempt the build again, it'll work eventually.

Building Mods Dependent on iChunUtil that were made by iChun
------------------------------------------------------------

Just make sure the two (or however many) projects share the same parent folder. iChunUtil and dependent mods share the same Gradle workspace setup.

Build as above.

The other project will pull in iChunUtil as an included build:

```
Parent Folder 
|--- All (optional - I do my development in this project. See below for more info)
|--- Dependent Mod - eg Better Than Bunnies
|--- Dependent Mod - eg Ding
|--- iChunUtil - automatically included by Dependent Mods
\--- Temp - Any folder starting with "Temp" will be ignored by the "all" project
```

Depending on iChunUtil
----------------------

My advice: Have iChunUtil in the same parent folder as your project and include it as a dependent build. If possible, use the same gradle workspace as iChunUtil, otherwise, reassign iChunUtil's subprojects to `project(":<modloader>")`. See `settings.gradle` to see how it's done or look up Composite Builds.

Alternatively: Build iChunUtil and publish to maven local and depend on that. iChunUtil is published under: `me.ichun.mods:ichunutil:<mc version>-<modloader>-<mod version>` 

The "All" Project
-----------------

I spent way too much time figuring out Gradle and setting it up so I can open up a project and have the dependencies all set up properly against one another. Thus came the `all` project.

The `all` project is made out of iChunUtil's entire gradle workspace with no source files in any of the subprojects and an empty `build-conventions-variations.gradle`. 

In `gradle.properties`, change the `mod_id`, `mod_name` and `archive_base_name` properties to `all` and open up the gradle project in IntelliJ IDEA.

IDEA will automatically import all folders in the same parent folder that have a `build.gradle` and `build.cmd` file, with exception to any folder starting with `temp`.

NeoForge is not included as I do my development against Fabric. Forge is included because the workspace breaks without it.

Builds are likely to fail initially because of NeoForge being set up in other projects - Overlapping File Lock. Just keep re-running build until it all passes.

When running: DO NOT RUN WITH GRADLE - IT WILL FAIL. Run with the IDE. Be sure to generate the fabric dependencies override task - found in iChunUtil under the fabric subproject.