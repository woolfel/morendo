#!/bin/sh
# set -x

# Set the home dir of Jamocha
JAMOCHA_HOME=.

# Sumatra JAR
JAMOCHA_LIB=./jamocha.jar

# Logging Library
LOG4J=./lib/log4j-1.2.14.jar

# JLine - line editing
JLINE=./lib/jline-0.9.9.jar

# Putting the things together ...
CLASSPATH=$JAMOCHA_HOME/$LOG4J:$JAMOCHA_HOME/$JAMOCHA_LIB:$JAMOCHA_HOME/$JLINE

# Setting Java Opts
JAVA_XMS=256m
JAVA_XMX=512m

# Jamocha Main Class
JAMOCHA_MAIN=org.jamocha.Jamocha

# Starting the game
echo "Starting Jamocha ..."
echo 

# deactivated, because it doesn't work when the Shell is started in a new Thread.
#java -Xms$JAVA_XMS \
#	-Xmx$JAVA_XMX \
#	-server \
#	-classpath $CLASSPATH \
#	jline.ConsoleRunner $JAMOCHA_MAIN $@

java -Xms$JAVA_XMS \
	-Xmx$JAVA_XMX \
	-server \
	-classpath $CLASSPATH \
	$JAMOCHA_MAIN $@

