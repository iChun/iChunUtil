@echo off
del build.gradle
copy build.gradle.neoforge build.gradle
start gradlew build publishMod