# Java Shell

A simple command-line shell implementation in Java.

## Features

- Basic shell commands:
  - `pwd` - Print working directory
  - `ls [path]` - List directory contents
  - `cd [path]` - Change directory
  - `echo [text]` - Print text
  - `type [file]` - Show file type
  - `exit` - Quit the shell

- Handles:
  - Ctrl+C (shows message and exits)
  - Invalid commands
  - Relative/absolute paths
  - Home directory (`~`) shortcut

## How to Run

1. Compile:
   ```bash
   javac Shell.java
2. Run:
   ```bash
   java Shell
