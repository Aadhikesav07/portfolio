@echo off
REM Set JAVA_HOME if not already set
if "%JAVA_HOME%"=="" (
    set JAVA_HOME=C:\java\jdk-25
)

REM Run Spring Boot application
call mvnw.cmd spring-boot:run

