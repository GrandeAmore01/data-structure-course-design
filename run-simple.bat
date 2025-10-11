@echo off
echo 数据结构与算法可视化模拟器 - 简化启动
echo Data Structure Visualizer - Simple Launch
echo.

echo 正在检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo 错误：未找到Java环境，请确保已安装Java 11或更高版本
    pause
    exit /b 1
)

echo.
echo 正在编译项目...
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo 编译失败！请检查代码错误
    pause
    exit /b 1
)

echo 编译成功！
echo.
echo 正在启动应用程序...

REM 尝试直接运行（非模块化）
java -cp "target/classes;%USERPROFILE%\.m2\repository\org\openjfx\javafx-controls\17.0.2\javafx-controls-17.0.2.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-fxml\17.0.2\javafx-fxml-17.0.2.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.15.2\jackson-databind-2.15.2.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.15.2\jackson-core-2.15.2.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.15.2\jackson-annotations-2.15.2.jar;%USERPROFILE%\.m2\repository\org\slf4j\slf4j-api\1.7.36\slf4j-api-1.7.36.jar;%USERPROFILE%\.m2\repository\ch\qos\logback\logback-classic\1.2.12\logback-classic-1.2.12.jar;%USERPROFILE%\.m2\repository\ch\qos\logback\logback-core\1.2.12\logback-core-1.2.12.jar" --add-modules javafx.controls,javafx.fxml com.datastruct.visualizer.DataStructureVisualizerApp

if %errorlevel% neq 0 (
    echo.
    echo 启动失败，请尝试以下解决方案：
    echo 1. 确保已安装JavaFX 17
    echo 2. 检查Java版本是否为11或更高
    echo 3. 尝试使用IDE运行主类
    echo.
    echo 主类位置：src\main\java\com\datastruct\visualizer\DataStructureVisualizerApp.java
)

pause
