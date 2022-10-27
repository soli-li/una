@echo off
set CURR_DIR=%cd%
cd %~dp0..
set ROOT_DIR=%cd%
cd %CURR_DIR%
set EXECJAVA=java

if not exist %ROOT_DIR%\bin\setenv.bat goto setEnv
call %ROOT_DIR%\bin\setenv.bat

if "%JAVA_HOME%" == "" goto setJREHome
set EXECJAVA=%JAVA_HOME%\bin\java
goto setEnv

:setJREHome
if "%JRE_HOME%" == "" goto setEnv
set EXECJAVA=%JRE_HOME%\bin\java

:setEnv

set MAIN_CLASS=com.una.system.sharing.SharingApplication
"%EXECJAVA%" -classpath "%ROOT_DIR%/conf;%ROOT_DIR%/lib/*" %MAIN_CLASS%