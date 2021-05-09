#!/bin/sh

# Set the home dir of Jamocha
JAMOCHA_HOME=.

# Sumatra JAR
JAMOCHA_LIB=./morendo.jar

# Logging Library
LOG4J=./lib/log4j-1.2.14.jar

# JLine - line editing
JLINE=./lib/jline-0.9.9.jar

# Putting the things together ...
CLASSPATH=$JAMOCHA_HOME/$LOG4J:$JAMOCHA_HOME/$JAMOCHA_LIB:$JAMOCHA_HOME/$JLINE

# Setting Java Opts
JAVA_XMS=512m
JAVA_XMX=1g

# Jamocha Main Class
MORENDO_MAIN=org.jamocha.Morendo

JAVA_OPTS=-server -Xms256m -Xmx2048m
	
# Starting the game
echo "Starting Jamocha ..."

java -Xms$JAVA_XMS -Xmx$JAVA_XMX -server -classpath $CLASSPATH $MORENDO_MAIN $@
