:: Batch script for creating native images for Linux and Windows
:: The script must be run in Windows with wsl installed
:: The script must be executed in the project root folder
:: Make sure that up-to-date JAR binaries are available at out/artifacts before running

:: Capture start time
@echo off
set t0=%time: =0%
%*

:: Clean output directory
del /q out\native\autoclicker_win64.zip /q out\native\autoclicker_linux.zip

:: Build Windows image
echo Building Windows image...[31m
jpackage -t app-image -i out/artifacts/autoclicker_win64 --main-jar autoclicker-win64.jar -n "Soni's Autoclicker" --vendor "Soni" -d out/native/win64 --icon images/icon.ico && echo [32mSuccessfully built native Windows image!
echo | set /p="[0m"

:: Build Linux image
echo Building Linux image...[31m
wsl jpackage -t app-image -i out/artifacts/autoclicker_linux --main-jar autoclicker-linux.jar -n "Soni's Autoclicker" --vendor "Soni" -d out/native/linux --icon res/icon.png && echo [32mSuccessfully built native Linux image!
echo | set /p="[0m"

:: Compress images
echo Compressing...[31m
cd out/native
cd win64/Soni's Autoclicker
wsl zip -qr9 ../../autoclicker_win64.zip app runtime "Soni's Autoclicker.exe" && echo [32mCompressed Windows image[31m
cd ../../linux/Soni's Autoclicker
wsl zip -qr9 ../../autoclicker_linux.zip bin lib && echo [32mCompressed Linux image
cd ../..
echo | set /p="[0m"

:: Clean & exit output directory
rd /s /q win64 linux
cd ../..

:: Capture end time
set t=%time: =0%

:: Perform time calculations (from https://stackoverflow.com/a/739634)
set /a h=1%t0:~0,2%-100
set /a m=1%t0:~3,2%-100
set /a s=1%t0:~6,2%-100
set /a c=1%t0:~9,2%-100
set /a starttime = %h% * 360000 + %m% * 6000 + 100 * %s% + %c%

set /a h=1%t:~0,2%-100
set /a m=1%t:~3,2%-100
set /a s=1%t:~6,2%-100
set /a c=1%t:~9,2%-100
set /a endtime = %h% * 360000 + %m% * 6000 + 100 * %s% + %c%

set /a runtime = %endtime% - %starttime%
set runtime = %s%.%c%

:: Output confirmation message
echo Done building! - Took %runtime%0 ms