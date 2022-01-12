#!/bin/zsh

# Shell script for creating native images for macOS
# The script must be executed in macOS in the project root folder

# Set up environment
rm -rf "out/native/soni's_autoclicker_macos.app"

# Build macOS image
print -P "Building macOS image...%F{red}"
jpackage -t app-image -i out/artifacts/autoclicker_macos --main-jar autoclicker-macos.jar -n "soni's_autoclicker_macos" --vendor "Soni" -d out/native/ --icon res/icon.icns --mac-package-name "Autoclicker" --java-options "-XstartOnFirstThread" && print -P "%F{green}Successfully built native macOS image!"
print -P "%f\n"