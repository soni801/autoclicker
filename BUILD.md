# Building

If you prefer building the project rather than installing it, you have come to the correct place. Here, you can find
build instructions and dependencies.

## Build instructions

1. Clone the repository using the method of your choice
2. Build a JAR file. You can use any method you want, as long as you
    - Set the main class as `com.autoclicker.main.Autoclicker`
    - Include an extracted version of `lib/jnativehook.jar` in the output JAR
    - Include an extracted version of `lib/swt-<platform>.jar` in the output JAR
    - Alternatively, open the project in IntelliJ. It will have artifacts already set up.
3. You can execute this jar file using `java -jar path/to/jar`, or, if you are on Windows or macOS, use the
corresponding shell script (in `scripts`) to build a native image for your OS. Alternatively, use the `jpackage` command
to build an image through the command line yourself.