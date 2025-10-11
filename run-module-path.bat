@echo off
echo 数据结构与算法可视化模拟器 - 模块路径版本
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
echo 正在启动应用程序（使用模块路径）...

REM 使用模块路径而不是classpath
java --module-path "target/classes;%USERPROFILE%\.m2\repository\org\openjfx\javafx-controls\17.0.2;%USERPROFILE%\.m2\repository\org\openjfx\javafx-fxml\17.0.2;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.15.2;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.15.2;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.15.2;%USERPROFILE%\.m2\repository\org\slf4j\slf4j-simple\1.7.36" --add-modules javafx.controls,javafx.fxml -cp "target/classes" com.datastruct.visualizer.DataStructureVisualizerAppSimple

if %errorlevel% neq 0 (
    echo.
    echo [错误] 启动失败
    echo.
    echo 请尝试以下解决方案：
    echo 1. 使用IDE运行 DataStructureVisualizerAppSimple.java
    echo 2. 检查JavaFX依赖是否正确下载
    echo 3. 确保Java版本为11或更高
    echo.
    echo 或者尝试Maven方式：
    echo mvn exec:java
)

echo.
pause
