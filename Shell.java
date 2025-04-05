import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.util.stream.*;

public class Shell {
    private static boolean running=true;
    private static Path currentDir= Paths.get(System.getProperty("user.dir"));

    public static void main(String args[]){
        Scanner scanner= new Scanner(System.in);

        while(running){
            try{
                System.out.print("$ ");
                String input = scanner.nextLine().trim();
                if(input.isEmpty()){
                    continue;
                }
                String[] parts= input.split(" ",2);
                String command = parts[0];
                String value= parts.length > 1 ? parts[1] : "";

                executeCommand(command, value);
            }catch (Exception e) {
                System.err.println("shell: error: " + e.getMessage());
            }
        }
    }

    private static void executeCommand(String command, String value) {
        try{
            switch (command){
                case "pwd":
                    pwd();
                    break;
                case "ls":
                    ls(value);
                    break;
                case "cd":
                    cd(value);
                    break;
                case "echo":
                    echo(value);
                    break;
                case "type":
                    type(value);
                    break;
                case "exit":
                    exit();
                    break;    
            }
        }catch (Exception e) {
            System.err.println("shell: " + command + ": " + e.getMessage());
        }
    }

    private static void pwd() {
        System.out.println(currentDir);
    }

    private static void ls(String value) {
        Path dir;
        if(value.isEmpty()){
            dir = currentDir;
        }else{
            dir = currentDir.resolve(value).normalize();
        }
        try {
            if (!Files.exists(dir)) {
                throw new IOException("Path does not exist: " + dir);
            }
            if (Files.isDirectory(dir)) {
                try(Stream<Path> stream = Files.list(dir)){;
                    stream.forEach(d->{
                        try{
                            String type=Files.isDirectory(d) ? "d" : "-";
                            System.out.printf("%s %-20s %6d bytes%n",
                                    type,
                                    d.getFileName(),
                                    Files.size(d));
                        }catch(IOException e){
                            System.err.println("Error accessing file: " + d);
                        }
                    });
                }
            }
            else{
                System.out.println(dir.getFileName());
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void cd(String value) {
        if (value.isEmpty()) {
            value = System.getProperty("user.home");
        }
        try {
            Path newPath = currentDir.resolve(value).normalize();
            if (!Files.exists(newPath)) {
                throw new IOException("Directory does not exist: " + newPath);
            }
            if (!Files.isDirectory(newPath)) {
                throw new IOException("Not a directory: " + newPath);
            }
            currentDir = newPath;
        } catch (InvalidPathException e) {
            throw new RuntimeException("Invalid path: " + value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void echo(String text) {
        System.out.println(text);
    }

    private static void type(String filename) {
        if (filename.isEmpty()) {
            throw new RuntimeException("Missing filename");
        }

        Path filePath = currentDir.resolve(filename).normalize();
        try {
            if (Files.notExists(filePath)) {
                throw new IOException("File does not exist");
            }

            String type;
            if (Files.isDirectory(filePath)) {
                type = "directory";
            } else {
                String contentType = Files.probeContentType(filePath);
                type = contentType != null ? contentType : "unknown file type";
            }

            System.out.println(filename + ": " + type);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    private static void exit() {
        System.out.println("Goodbye!");
        running = false;
    }
}
