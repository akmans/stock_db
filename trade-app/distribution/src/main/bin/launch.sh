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

java -Dlogback.configurationFile="${HOME}/classes/resources/logback.xml" -cp $CLASSPATH com.akmans.trade.standalone.console.GenerateJapanStockWeeklyApp 1991

echo "-------- Finished ! ----------"
