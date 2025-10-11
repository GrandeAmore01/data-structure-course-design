@echo off
echo 数据结构与算法可视化模拟器 - 最终运行脚本
echo ==========================================

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

REM 直接使用Java运行，不依赖Maven插件
java -cp "target/classes;%USERPROFILE%\.m2\repository\org\openjfx\javafx-controls\17.0.2\javafx-controls-17.0.2.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-fxml\17.0.2\javafx-fxml-17.0.2.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.15.2\jackson-databind-2.15.2.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.15.2\jackson-core-2.15.2.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.15.2\jackson-annotations-2.15.2.jar;%USERPROFILE%\.m2\repository\org\slf4j\slf4j-simple\1.7.36\slf4j-simple-1.7.36.jar" --add-modules javafx.controls,javafx.fxml com.datastruct.visualizer.DataStructureVisualizerAppSimple

if %errorlevel% neq 0 (
    echo.
    echo [错误] 启动失败
    echo.
    echo 请尝试以下解决方案：
    echo 1. 使用IDE运行 DataStructureVisualizerAppSimple.java
    echo 2. 检查JavaFX是否正确安装
    echo 3. 确保Java版本为11或更高
    echo.
    echo 或者尝试手动运行：
    echo java -cp "target/classes;[依赖路径]" --add-modules javafx.controls,javafx.fxml com.datastruct.visualizer.DataStructureVisualizerAppSimple
)

echo.
pause
