# 1. Java Shell

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
   
# 2. Maze Generator in Java

This is a simple maze generator implemented in Java (DFS) algorithm.

##  Features

- Generates a random maze of any odd-sized dimensions.
- Ensures there's always a start (`S`) and exit (`E`) point.
- Uses stack-based DFS for maze generation.
- Carves paths by breaking walls between cells.


## How to Run

1. Compile:
   ```bash
   javac Shell.java
2. Run:
   ```bash
   java Shell
