@echo off
echo 数据结构与算法可视化模拟器 - 直接运行
echo =====================================

echo.
echo 正在检查环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到Java环境
    pause
    exit /b 1
)

echo [成功] Java环境正常

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
echo 正在下载依赖...
mvn dependency:resolve -q

echo.
echo 正在启动应用程序...

REM 构建classpath
set CLASSPATH=target/classes
for /f "tokens=*" %%i in ('mvn dependency:build-classpath -Dmdep.outputFile=classpath.txt -q') do set CLASSPATH=!CLASSPATH!;%%i

REM 直接运行Java程序
java -cp "%CLASSPATH%" --add-modules javafx.controls,javafx.fxml com.datastruct.visualizer.DataStructureVisualizerAppSimple

if %errorlevel% neq 0 (
    echo.
    echo [错误] 启动失败
    echo.
    echo 请尝试以下解决方案：
    echo 1. 使用IDE运行 DataStructureVisualizerAppSimple.java
    echo 2. 检查JavaFX是否正确安装
    echo 3. 查看 TROUBLESHOOTING.md 文件
    echo.
    echo 或者尝试手动运行：
    echo java -cp "target/classes;[依赖路径]" --add-modules javafx.controls,javafx.fxml com.datastruct.visualizer.DataStructureVisualizerAppSimple
)

echo.
pause
