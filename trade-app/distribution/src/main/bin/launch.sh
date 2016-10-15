#/bin/bash

# Set JAVA_HOME
#JAVA_HOME="echo $JAVA_HOME"

# Set HOME directory
HOME=$(cd $(dirname $0) && cd .. && pwd)

# Set current working directory
cd $HOME

# Set CLASSPATH
CLASSPATH=$(echo lib/*.jar | tr ' ' ':')

# Add classes to CLASSPATH
CLASSPATH="${CLASSPATH}:classes"

echo "-------- Starting ... --------"

#export JVM_ARGS="-Xms1024m -Xmx2048m"

java -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/share/log/ \
    -XX:+UseG1GC -Xms2g -Xmx4g -Dlogback.configurationFile="/logback.xml" \
    -cp $CLASSPATH com.akmans.trade.fx.console.GenerateCandlestickDataApp $1 $2

STATUS=$?

echo "-------- Finished ! ----------"

exit $STATUS
