@echo off
REM Delete existing bin folder if it exists
if exist bin rd /s /q bin
mkdir bin

REM Change to src folder and compile, including the exp4j JAR in the classpath
cd src
javac -d ../bin -cp "../lib/exp4j-0.4.8.jar" *.java
if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b 1
)
cd ..

REM Change to bin folder and run EquationSolverApp with the JAR on the classpath
cd bin
java -cp ".;../lib/exp4j-0.4.8.jar" EquationSolverApp
cd ..

pause
