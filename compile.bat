set BINDIR=%~dp0
call mvn clean install -f "%BINDIR%pom.xml"
pause >nul