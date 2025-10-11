@echo off
echo 数据结构与算法可视化模拟器
echo Data Structure Visualizer
echo.

echo 正在检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误：未找到Java环境，请确保已安装Java 11或更高版本
    pause
    exit /b 1
)

echo 正在检查Maven环境...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误：未找到Maven环境，请确保已安装Maven
    pause
    exit /b 1
)

echo.
echo 正在编译项目...
mvn clean compile
if %errorlevel% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)

echo.
echo 正在启动应用程序...
echo 尝试使用简化版本启动...
mvn exec:java -Dexec.mainClass="com.datastruct.visualizer.DataStructureVisualizerAppSimple" -Dexec.args="--add-modules javafx.controls,javafx.fxml"
if %errorlevel% neq 0 (
    echo.
    echo 简化版本启动失败，尝试使用JavaFX插件...
    mvn javafx:run
    if %errorlevel% neq 0 (
        echo.
        echo 所有启动方式都失败了，请查看TROUBLESHOOTING.md文件
        echo 或尝试运行 run-simple.bat 脚本
    )
)

pause

