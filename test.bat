@echo off
echo 数据结构可视化器 - 快速测试
echo ================================

echo.
echo 1. 检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo [错误] Java未安装或未配置到PATH
    goto :end
)
echo [成功] Java环境正常

echo.
echo 2. 检查Maven环境...
mvn -version
if %errorlevel% neq 0 (
    echo [错误] Maven未安装或未配置到PATH
    goto :end
)
echo [成功] Maven环境正常

echo.
echo 3. 编译项目...
mvn clean compile -q
if %errorlevel% neq 0 (
    echo [错误] 项目编译失败
    goto :end
)
echo [成功] 项目编译成功

echo.
echo 4. 运行测试类...
java -cp "target/classes;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.15.2\jackson-databind-2.15.2.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.15.2\jackson-core-2.15.2.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.15.2\jackson-annotations-2.15.2.jar;%USERPROFILE%\.m2\repository\org\slf4j\slf4j-api\1.7.36\slf4j-api-1.7.36.jar;%USERPROFILE%\.m2\repository\ch\qos\logback\logback-classic\1.2.12\logback-classic-1.2.12.jar;%USERPROFILE%\.m2\repository\ch\qos\logback\logback-core\1.2.12\logback-core-1.2.12.jar" com.datastruct.visualizer.TestMain
if %errorlevel% neq 0 (
    echo [警告] 测试类运行失败，但项目可能仍然可以正常使用
) else (
    echo [成功] 核心功能测试通过
)

echo.
echo 5. 检查JavaFX依赖...
dir "%USERPROFILE%\.m2\repository\org\openjfx\javafx-controls\17.0.2\javafx-controls-17.0.2.jar" >nul 2>&1
if %errorlevel% neq 0 (
    echo [警告] JavaFX依赖可能未下载，尝试下载...
    mvn dependency:resolve -q
) else (
    echo [成功] JavaFX依赖已就绪
)

echo.
echo ================================
echo 测试完成！
echo.
echo 如果所有检查都通过，您可以尝试运行：
echo   run.bat          - 标准启动方式
echo   run-simple.bat   - 简化启动方式
echo.
echo 如果遇到问题，请查看 TROUBLESHOOTING.md 文件

:end
pause
