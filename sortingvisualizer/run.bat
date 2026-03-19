@echo off
REM ===============================================
REM Compile all Java files recursively and run program
REM ===============================================

REM Step 0: Go to folder where batch file is
cd /d %~dp0

REM Step 1: Find all .java files and store in a temp file
echo Finding all Java files...
dir /b /s *.java > all_java_files.txt

REM Step 2: Compile all files at once
echo Compiling all Java files...
javac -d . @all_java_files.txt
if errorlevel 1 (
    echo Compilation failed!
    pause
    del all_java_files.txt
    exit /b 1
)

REM Step 3: Cleanup temp file
del all_java_files.txt

REM Step 4: Run the main class
echo.
echo Compilation finished successfully!
echo Launching Sorting Visualizer...
java -cp . sortingvisualizer.SortingVisualizer
pause