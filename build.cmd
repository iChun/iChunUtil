@echo off
setlocal enabledelayedexpansion

set _darkGreen=[32m
set _red=[31m
set _reset=[0m

for %%I in (.) do set folderName=%%~nxI

set fabric=Yes
set forge=Yes
set neoforge=Yes
set clean=No
set build=Yes
set publish=No
set publishLocal=No
set publishMods=No

::========================================================================================================================================

:Options

set _noML=
set _noTask=
if "%fabric%"=="No" if "%forge%"=="No" if "%neoforge%"=="No" set _noML=y
if "%clean%"=="No" if "%build%"=="No" if "%publish%"=="No" if "%publishLocal%"=="No" if "%publishMods%"=="No" set _noTask=y

cls
title %folderName% - Build Prompt

echo:
echo:
echo:    %_darkGreen%Project - %folderName%%_reset% 
echo:    
if defined _noML ( 
echo:    %_red%Which Modloaders:%_reset%
) else ( 
echo:    Which Modloaders: 
)
echo:      [1] Fabric               : %fabric%
echo:      [2] Forge                : %forge%
echo:      [3] NeoForge             : %neoforge%
echo:
if defined _noTask ( 
echo:    %_red%What do you want to do?%_reset%
) else ( 
echo:    What do you want to do? 
)
echo:      [4] Clean                : %clean%
echo:      [5] Build                : %build%
echo:      [6] Publish Maven        : %publish%
echo:      [7] Publish Local Maven  : %publishLocal%
echo:      [8] Publish Mods         : %publishMods%
echo:
echo:      [9] Run
echo:      [0] Exit
echo:
echo:

choice /C:1234567890 /N
set _erl=%errorlevel%

if %_erl%==10 exit /b
if %_erl%==9 goto :Run
if %_erl%==8 (if "%publishMods%"=="Yes" ( set "publishMods=No" ) else ( set "build=Yes" & set "publishMods=Yes" )) & cls & goto :Options
if %_erl%==7 (if "%publishLocal%"=="Yes" ( set "publishLocal=No" ) else ( set "build=Yes" & set "publishLocal=Yes" )) & cls & goto :Options
if %_erl%==6 (if "%publish%"=="Yes" ( set "publish=No" ) else ( set "build=Yes" & set "publish=Yes" )) & cls & goto :Options
if %_erl%==5 (if "%build%"=="Yes" ( set "build=No" & set "publish=No" & set "publishLocal=No" & set "publishMods=No" ) else ( set "build=Yes" )) & cls & goto :Options
if %_erl%==4 (if "%clean%"=="Yes" ( set "clean=No" ) else ( set "clean=Yes" )) & cls & goto :Options
if %_erl%==3 (if "%neoforge%"=="Yes" ( set "neoforge=No" ) else ( set "neoforge=Yes" )) & cls & goto :Options
if %_erl%==2 (if "%forge%"=="Yes" ( set "forge=No" ) else ( set "forge=Yes" )) & cls & goto :Options
if %_erl%==1 (if "%fabric%"=="Yes" ( set "fabric=No" ) else ( set "fabric=Yes" )) & cls & goto :Options

goto :Options

::========================================================================================================================================

:Run

set "command=gradlew"

if "%fabric%"=="No" if "%forge%"=="No" if "%neoforge%"=="No" (
echo: You need to set a modloader!
goto :Options
)
if "%clean%"=="No" if "%build%"=="No" if "%publish%"=="No" if "%publishLocal%"=="No" if "%publishMods%"=="No" (
echo: Do nothing? Done!
goto :Options
)

:: it's ugly but it works
if "%fabric%"=="Yes" if "%clean%"=="Yes" set "command=!command! :fabric:clean"
if "%forge%"=="Yes" if "%clean%"=="Yes" set "command=!command! :forge:clean"
if "%neoforge%"=="Yes" if "%clean%"=="Yes" set "command=!command! :neoforge:clean"

if "%fabric%"=="Yes" if "%build%"=="Yes" set "command=!command! :fabric:build"
if "%forge%"=="Yes" if "%build%"=="Yes" set "command=!command! :forge:build"
if "%neoforge%"=="Yes" if "%build%"=="Yes" set "command=!command! :neoforge:build"

if "%fabric%"=="Yes" if "%publish%"=="Yes" set "command=!command! :fabric:publish"
if "%forge%"=="Yes" if "%publish%"=="Yes" set "command=!command! :forge:publish"
if "%neoforge%"=="Yes" if "%publish%"=="Yes" set "command=!command! :neoforge:publish"

if "%fabric%"=="Yes" if "%publishLocal%"=="Yes" set "command=!command! :fabric:publishToMavenLocal"
if "%forge%"=="Yes" if "%publishLocal%"=="Yes" set "command=!command! :forge:publishToMavenLocal"
if "%neoforge%"=="Yes" if "%publishLocal%"=="Yes" set "command=!command! :neoforge:publishToMavenLocal"

if "%fabric%"=="Yes" if "%publishMods%"=="Yes" set "command=!command! :fabric:publishMod"
if "%forge%"=="Yes" if "%publishMods%"=="Yes" set "command=!command! :forge:publishMod"
if "%neoforge%"=="Yes" if "%publishMods%"=="Yes" set "command=!command! :neoforge:publishMod"

echo Executing %command%

start %command%

exit /b

::========================================================================================================================================