@echo off

echo %date%

cd %~dp0

setLocal EnableDelayedExpansion
set CLASSPATH="
for /R ../lib %%a in (*.jar) do (
   set CLASSPATH=!CLASSPATH!;%%a
)
set CLASSPATH=!CLASSPATH!"
echo !CLASSPATH!

java -Dlogback.configurationFile="/logback.xml" -Djava.library.path="../classes" -cp .;!CLASSPATH! com.akmans.trade.fx.console.GenerateFXTickApp
