del *.class
del *.jar
javac -classpath %CLASSPATH% *.java
jar cf testplugin.jar *.class 
