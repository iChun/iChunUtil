@echo off
del build.gradle
copy build.gradle.forge build.gradle
start gradlew build publishMod