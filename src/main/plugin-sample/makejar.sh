#!/bin/sh

DIRLIB=$PWD/../../lib/1.0

for LIB in `ls $DIRLIB`
do
  CLASSPATH=$DIRLIB/$LIB:$CLASSPATH
done

rm *.class *.jar
javac -classpath $CLASSPATH *.java
jar cf testplugin.jar *.class 
