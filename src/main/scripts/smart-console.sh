#!/bin/bash

SMART_LIB=../lib
SMART_RESOURCES=../resources

# Setting classpath entries
CLASSPATH="$SMART_LIB/*:$SMART_RESOURCES"

# Starting JVM
java -classpath $CLASSPATH net.obvj.smart.console.enhanced.EnhancedManagementConsole
