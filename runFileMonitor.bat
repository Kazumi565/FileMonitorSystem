@echo off
setlocal
pushd "%~dp0"
javac -d out src\filemonitor\*.java
if errorlevel 1 (
    popd
    exit /b 1
)
java -cp out filemonitor.Main %*
set "monitor_exit=%errorlevel%"
popd
exit /b %monitor_exit%
