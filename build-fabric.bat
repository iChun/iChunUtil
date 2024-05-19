@echo off
del build.gradle
copy build.gradle.fabric build.gradle
start gradlew build