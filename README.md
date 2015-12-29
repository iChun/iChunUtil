If you have an error with protected/private access etc, you need to put /src/main/resources/META-INF/iChunUtil_at.cfg into Forge's src folder (same folder structure) and rerun setupDecompWorkspace. Make sure the Forge setup finds the AT config or you're doing it wrong.

How to set up this and another mod of mine to be compiled:
1. Clone this repository.
2. Due to morph's API requirement, get Morph's API and put it in src/api/java/. Maintain the folder structure
3. Build iChunUtil (use build.bat if you're on Windows for easy double clicky goodness)
4. Go to build/libs/ and copy iChunUtil-<version>-deobf.jar.
5. Clone/set up a gradle project (in a different folder).
6. Put the jar you copied into the libs/ folder.
7. If the module needs any other libraries/APIs, go get them and put them in libs/ or src/api/(java or resources).
8. Build the project.
9. If project fails because protected/private access, copy the META-INF/ folder from iChunUtil and put it in your project's src/api/resources/ folder. Build the project again.
10. Success, hopefully.