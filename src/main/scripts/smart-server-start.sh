#!/bin/bash

echo '                              __  ______   '
echo '    _________ ___  ____  ____/ /_ \ \ \ \  '
echo '   / ___/ __ `__ \/ __ `/ __/ __/  \ \ \ \ '
echo '  (__  ) / / / / / /_/ / / / /_    / / / / '
echo ' /____/_/ /_/ /_/\__,_/_/  \__/   /_/_/_/  '
echo

SMART_LIB=../lib
SMART_RESOURCES=../resources
SMART_PROPERTIES=$SMART_RESOURCES/smart.properties

# --------------------------------------------------
# Fetching configuration...
# --------------------------------------------------

function getProperty
{
   KEY=$1
   VALUE=`cat $SMART_PROPERTIES | grep "$KEY" | cut -d'=' -f2`
   echo $VALUE
}

JMX_REMOTE_PORT=$(getProperty "jmx.remote.port")
JMX_REMOTE_AUTHENTICATE=$(getProperty "jmx.remote.authenticate")
JMX_REMOTE_SSL=$(getProperty "jmx.remote.ssl")

echo ------------------------------------------
echo jmx.remote.port=$JMX_REMOTE_PORT
echo jmx.remote.authenticate=$JMX_REMOTE_AUTHENTICATE
echo jmx.remote.ssl=$JMX_REMOTE_SSL
echo ------------------------------------------
echo

# --------------------------------------------------
# Setting JVM arguments
# --------------------------------------------------

JVM_ARGS="-Dcom.sun.management.jmxremote.port=$JMX_REMOTE_PORT"
JVM_ARGS="$JVM_ARGS -Dcom.sun.management.jmxremote.authenticate=$JMX_REMOTE_AUTHENTICATE"
JVM_ARGS="$JVM_ARGS -Dcom.sun.management.jmxremote.ssl=$JMX_REMOTE_SSL"

# --------------------------------------------------
# Setting classpath entries
# --------------------------------------------------

CLASSPATH="$SMART_LIB/*:$SMART_RESOURCES"

# --------------------------------------------------
# Starting JVM
# --------------------------------------------------

java $JVM_ARGS -classpath $CLASSPATH net.obvj.smart.main.Main
