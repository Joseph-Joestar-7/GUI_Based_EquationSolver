@echo off
REM Delete existing bin folder if it exists
if exist bin rd /s /q bin
mkdir bin

REM Change to src folder and compile, including the exp4j and MySQL JARs in the classpath
cd src
javac -d ../bin -cp "../lib/exp4j-0.4.8.jar;../lib/mysql-connector-j-9.2.0.jar" *.java
if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b 1
)
cd ..

REM Change to bin folder and run EquationSolverApp with the JARs on the classpath
cd bin
java -cp ".;../lib/exp4j-0.4.8.jar;../lib/mysql-connector-j-9.2.0.jar" EquationSolverApp
if errorlevel 1 (
    echo Program execution failed. Please check your code or database connection.
    pause
    exit /b 1
)
cd ..

pause
