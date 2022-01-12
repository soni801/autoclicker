:: Batch script for creating native images for Linux and Windows
:: The script must be run in Windows with wsl installed
:: The script must be executed in the project root folder

:: Set up environment
@echo off
rd /s /q out\native\soni's_autoclicker_win64
rd /s /q out\native\soni's_autoclicker_linux

:: Build Windows image
echo Building Windows image...[31m
jpackage -t app-image -i out/artifacts/autoclicker_win64 --main-jar autoclicker-win64.jar -n "soni's_autoclicker_win64" --vendor "Soni" -d out/native/ --icon res/icon.ico --win-console && del out\native\soni's_autoclicker_win64\soni's_autoclicker_win64.ico && echo [32mSuccessfully built native Windows image!
echo | set /p="[0m"

:: Build Linux image
echo Building Linux image...[31m
wsl jpackage -t app-image -i out/artifacts/autoclicker_linux --main-jar autoclicker-linux.jar -n "soni's_autoclicker_linux" --vendor "Soni" -d out/native/ --icon res/icon.png && echo [32mSuccessfully built native Linux image!
echo | set /p="[0m"