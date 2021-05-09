# Set the home dir of Jamocha
set-variable -name "JAMOCHA_HOME" -Value "."

# Sumatra JAR
set-variable -name "JAMOCHA_LIB" -value "./morendo.jar"

# Logging Library
set-variable -name "LOG4J" -value "./lib/log4j-1.2.14.jar"

# JLine - line editing
set-variable -name "JLINE" -value "./lib/jline-0.9.9.jar"

# Putting the things together ...
set-variable -name "CLASSPATH" -value "$JAMOCHA_HOME/$LOG4J;$JAMOCHA_HOME/$JAMOCHA_LIB;$JAMOCHA_HOME/$JLINE"

# Setting Java Opts
set-variable -name "JAVA_XMS" -value "512m"
set-variable -name "JAVA_XMX" -value "1g"

# Jamocha Main Class
set-variable -name "MORENDO_MAIN" -value "org.jamocha.Morendo"

# Starting the game
java -version
echo "Starting Jamocha ..."

#java -Xms512m -Xmx1g -XX:+UseZGC -classpath $CLASSPATH $MORENDO_MAIN $args

java -Xms512m -Xmx1g -server -classpath $CLASSPATH $MORENDO_MAIN $args
