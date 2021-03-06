# Contributing guidelines

If you want to contribute to the project, please read the following guidelines:

## Helping with issues

If you see an issue you would like to help with, please leave a comment letting others know that you are working on it
to avoid duplicated effort.

## Project environment

- The recommended IDE is IntelliJ IDEA, as the project will already be correctly set up.
  - If using a different IDE, do not include project configuration files or any other IDE-specific files in the Git
repository.
- Use JDK >=17, and use language level 17. Do not modify the version of Java or any dependencies
- Use `/src/` for Java source files and `/res/` for resource files (e.g. images). `/lib/` is reserved for libraries. Do
not modify this.
- Exclude compilation output and other non-necessary files from Git.

## Code style

- Use `PascalCase` for
    - Class names
- Use `camelCase` for
    - Non-static variables
    - Object names
    - Method names
- Use `snake_case` for
    - Resource files
- Use `UPPER_CASE_SNAKE_CASE` for
    - Static variables
- Do not use Hungarian notation
- Do not use any characters outside the english alphabet in code nor comments
- Use en_US (e.g. *color* instead of *colour*) in both code and comments
- Use newlines for
  - `{` where needed, e.g. after `if` or `class`
  - Continuation of previous statements, e.g. `else` or `catch`
- Omit `{}` where possible
  - When omitting brackets, do not place content on newline
