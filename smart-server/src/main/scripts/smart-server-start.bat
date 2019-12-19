@ECHO off

ECHO                               __  ______
ECHO     _________ ___  ____  ____/ /_ \ \ \ \
ECHO    / ___/ __ `__ \/ __ `/ __/ __/  \ \ \ \
ECHO   (__  ) / / / / / /_/ / / / /_    / / / /
ECHO  /____/_/ /_/ /_/\__,_/_/  \__/   /_/_/_/
ECHO;

SET SMART_LIB=..\lib
SET SMART_RESOURCES=..\resources
SET SMART_PROPERTIES=%SMART_RESOURCES%\smart.properties

REM --------------------------------------------------
REM Fetching configuration...
REM --------------------------------------------------

FOR /F "tokens=1,2 delims==" %%G IN (%SMART_PROPERTIES%) DO (set %%G=%%H)

ECHO ------------------------------------------
ECHO jmx.remote.port=%jmx.remote.port%
ECHO jmx.remote.authenticate=%jmx.remote.authenticate%
ECHO jmx.remote.ssl=%jmx.remote.ssl%
ECHO ------------------------------------------
ECHO;

REM --------------------------------------------------
REM Setting JVM arguments
REM --------------------------------------------------

SET JVM_ARGS=-Dcom.sun.management.jmxremote.port=%jmx.remote.port%
SET JVM_ARGS=%JVM_ARGS% -Dcom.sun.management.jmxremote.authenticate=%jmx.remote.authenticate%
SET JVM_ARGS=%JVM_ARGS% -Dcom.sun.management.jmxremote.ssl=%jmx.remote.ssl%

REM --------------------------------------------------
REM Setting classpath entries
REM --------------------------------------------------

SET CLASSPATH="%SMART_LIB%\*;%SMART_RESOURCES%"

REM --------------------------------------------------
REM Starting JVM
REM --------------------------------------------------

java %JVM_ARGS% -classpath %CLASSPATH% net.obvj.smart.main.Main
