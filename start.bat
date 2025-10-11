@echo off
echo 数据结构与算法可视化模拟器
echo ================================

echo.
echo 正在检查环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到Java环境
    pause
    exit /b 1
)

mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到Maven环境
    pause
    exit /b 1
)

echo [成功] 环境检查通过

echo.
echo 正在编译项目...
mvn clean compile -q
if %errorlevel% neq 0 (
    echo [错误] 编译失败
    pause
    exit /b 1
)

echo [成功] 编译完成

echo.
echo 正在启动应用程序...
echo 使用exec插件启动简化版本...

mvn exec:java -Dexec.mainClass="com.datastruct.visualizer.DataStructureVisualizerAppSimple" -Dexec.args="--add-modules javafx.controls,javafx.fxml"

if %errorlevel% neq 0 (
    echo.
    echo [错误] 启动失败
    echo.
    echo 请尝试以下解决方案：
    echo 1. 检查JavaFX是否正确安装
    echo 2. 尝试使用IDE运行 DataStructureVisualizerAppSimple.java
    echo 3. 查看 TROUBLESHOOTING.md 文件
    echo.
    pause
    exit /b 1
)

echo.
echo [成功] 程序已退出
pause
