@ECHO off

SET SMART_LIB=..\lib
SET SMART_RESOURCES=..\resources

REM Setting classpath entries
SET CLASSPATH="%SMART_LIB%\*;%SMART_RESOURCES%"

REM Starting JVM
java -classpath %CLASSPATH% net.obvj.smart.console.enhanced.EnhancedManagementConsole
