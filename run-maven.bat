@echo off
echo 数据结构与算法可视化模拟器 - Maven方式
echo =====================================

echo.
echo 正在编译项目...
mvn clean compile
if %errorlevel% neq 0 (
    echo [错误] 编译失败
    pause
    exit /b 1
)

echo.
echo 正在生成classpath...
mvn dependency:build-classpath -Dmdep.outputFile=classpath.txt -q

echo.
echo 正在启动应用程序...

REM 读取classpath并运行
for /f "delims=" %%i in (classpath.txt) do (
    java -cp "target/classes;%%i" --add-modules javafx.controls,javafx.fxml com.datastruct.visualizer.DataStructureVisualizerAppSimple
)

if %errorlevel% neq 0 (
    echo.
    echo [错误] 启动失败
    echo.
    echo 请尝试使用IDE运行 DataStructureVisualizerAppSimple.java
    echo 或查看 TROUBLESHOOTING.md 文件
)

echo.
pause
