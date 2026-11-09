 @echo off
echo ============================================
echo   Building Smart Expense Tracker
echo ============================================

if not exist "out" mkdir out

echo Compiling...
javac -d out -cp "lib\sqlite-jdbc-3.45.1.0.jar;lib\slf4j-api-1.7.36.jar;lib\slf4j-simple-1.7.36.jar" --source-path src\main\java src\main\java\com\expensetracker\App.java src\main\java\com\expensetracker\model\*.java src\main\java\com\expensetracker\db\*.java src\main\java\com\expensetracker\service\*.java src\main\java\com\expensetracker\ui\*.java

if %errorlevel% neq 0 (
    echo Compilation FAILED!
    exit /b 1
)

echo Build successful!
echo.
echo Run app:   run.bat
echo Run tests: test.bat
