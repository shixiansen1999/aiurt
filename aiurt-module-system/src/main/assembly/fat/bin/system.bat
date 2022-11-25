@echo off

setlocal

set "CURRENT_DIR=%cd%"

set JAVA_OPTIONS_MIN=-Xms2048M
set JAVA_OPTIONS_MAX=-Xmx2048M

rem 打包完成后的jar包名称
set APP_JAR_NAME=aiurt-module-system-3.2.0.jar

rem 应用名称
set APP_NAME=bdyw

cd "%CURRENT_DIR%"
cd ..
java %JAVA_OPTIONS_MIN% %JAVA_OPTIONS_MAX% -jar %APP_JAR_NAME%
