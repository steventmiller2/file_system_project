/**
 * Homework #3
 * Steve Miller
 * 7/29/23
 * 
 * I developed Authentication and UserInterface classes to take the responsibility of login and user interaction, respectively.
 * On startup, the Filesystem created, or loaded if it already exists. Then the user is prompted to login. 
 * Upon authentication, the user interface is initialized with the file system and user, then and begins to run. 
 * This welcomes the user and opens the input scanner for commands. 
 * The accepted commands based on the rubric for this assignment are ls, mkdir, touch, cat, and chmod. The user interface handles 
 * each of these by calling the appropriate methods on the FileEntry being prompted. In each user interface command, the user is
 * being passed into the methods to verify user permissions of the command.
 */

import java.nio.file.FileSystemException;
import javax.security.sasl.AuthenticationException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/*
 * Use multiple files - provides higher level view of composition
 * Likes UI, breakout of loadstate savestate, likes UI/API
 * 
 */

//path is a representation of the physical address on hard-drive to store and receive instance/metadata
public class FileSystem {
    private static FileSystem instance = null;
    private Directory root;
    private Directory currentDirectory;

    // private constructor to enforce Singleton pattern
    private FileSystem() {
        root = new Directory("/", null, null);
        currentDirectory = root;
    }

    public static FileSystem getInstance() {
        if (instance == null) {
            instance = new FileSystem();
        }
        return instance;
    }

    public Directory getCurrentDirectory() {
        return currentDirectory;
    }

    public void saveState(String path) throws FileSystemException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(root);
        } catch (IOException e) {
            throw new FileSystemException("Error saving state to file: " + e.getMessage());
        }
    }

    public void loadState(String path) throws FileSystemException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            root = (Directory) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new FileSystemException("Error loading state from file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
          //get FileSystem instance
            FileSystem fileSystem = FileSystem.getInstance();
            fileSystem.loadState("path");

            //Request Login to get User
            Authentication auth = new Authentication();
            User user = auth.login();

            //Initialize and run User Interface
            UserInterface userInterface = new UserInterface(fileSystem, user);
            userInterface.run();
            
            //Save state of FileSystem after UI closes
            fileSystem.saveState("path");
        } catch (FileSystemException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

public class Authentication {

    private User authenticateUser(String userName, String password) throws AuthenticationException {
        User user = getUserFromLocalDB(userName, password);
        if (user != null) {
            return user;
        } else {
            throw AuthenticationException;
        }
    }

    public User login() throws AuthenticationException {
        scanner = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter username");
        String userName = scanner.nextLine();  // Read user input
        System.out.println("Enter password");
        String password = scanner.nextLine();  // Read user input
        try {
            return authenticateUser(userName, password);
        } catch (AuthenticationException e) {
            System.out.println("User doesn't exist. Please talk to an admin");
            throw e;
        }
    }
}

//I didn't use singleton on UserInterface because in the case that there are multiple users logged in
public class UserInterface {
    private FileSystem fileSystem;
    private User currentUser;

    public UserInterface(FileSystem fileSystem, User currentUser) {
        this.fileSystem = fileSystem;
        this.currentUser = currentUser;
    }

    public void run() {
        System.out.println("Welcome to the File System!");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            Directory currDir = fileSystem.getCurrentDirectory();
            System.out.print(currDir.toPath() + "> ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            } else if (input.equalsIgnoreCase("ls")) {
                System.out.println(currDir.listChildren(currentUser));
            } else if (input.toLowerCase().startsWith("mkdir ")) {
                String dirName = input.substring(6).trim();
                currDir.createDirectory(currentUser, dirName, currDir);
            } else if (input.toLowerCase().startsWith("touch ")) {
                int indexOfDot = input.indexOf(".");
                String fileName = input.substring(6, indexOfDot).trim();
                String fileType = input.substring(indexOfDot).trim();
                currDir.createFile(currentUser, fileName, fileType, currDir);
            } else if (input.toLowerCase().startsWith("cat ")) {
                String fileName = input.substring(4).trim();
                currDir.getChildEntry(currentUser, fileName).readFile();
            } else if (input.toLowerCase().startsWith("chmod ")) {
                //assuming user is using correct 9-digit permission string mentioned in Permissions class
                String permString = input.substring(6, 16).trim();
                String fileEntry = input.substring(16).trim();
                currDir.getChildEntry(currentUser, fileEntry).setPermissions(currentUser, permString);
            } else {
                System.out.println("Invalid command.");
            }
        }
    }
}

abstract class FileSystemEntry {
    User user;
    String name;
    int id;
    Date create_date = new Date();
    Date modified_date;
    Permissions permissions;

    public Permissions getPermissions() {
      return permissions;
    }

    public void setPermissions(User user, String permissions) {
        this.permissions.changePermission(user, permissions);
    }

    // Constructor for FileSystemEntry
    public FileSystemEntry(String name, User user, FileSystemEntry parent) {
        this.name = name;
        this.user = user;
        this.id = customHash(name);
        this.permissions = new Permissions(user);
    }

    public String getName() {
        return name;
    }
}

public class Directory extends FileSystemEntry {
    ArrayList<FileSystemEntry> children = new ArrayList<>();

    public Directory(String name, User user, FileSystemEntry parent) {
        super(name, user, parent);
    }

    public String listChildren(User user) {
        StringBuilder builder = new StringBuilder();
        for (FileSystemEntry child : children) {
            if (child.getPermissions().checkPermission(user, 'r')) {
                builder.append(child.getName());
            }
        }
        return builder.toString();
    }

    public ArrayList<FileSystemEntry> openDirectory(User user) {
        if (permissions.checkPermission(user, 'r')) {
            return this.getChildren();
        }
        return null;
    }

    public FileSystemEntry getChildEntry(User user, String name) {
        for (FileSystemEntry child : children) {
            if (child.getName().equalsIgnoreCase(name) && child.getPermissions().checkPermission(user, 'r')) {
                return child;
            }
        }
    }

    public void createFile(User user, String name, String type, FileSystemEntry parent) {
        if (permissions.checkPermission(user, 'w')) {
            File file = new File(name, type, user, this);
            children.add(file);
        }
    }

    public void removeFile(User user, File file) {
        if (permissions.checkPermission(user, 'w')) {
            children.remove(file);
        }
    }

    public void createDirectory(User user, String name, FileSystemEntry parent) {
        if (permissions.checkPermission(user, 'w')) {
            Directory directory = new Directory(name, user, this);
            children.add(directory);
        }
    }

    public void removeDirectory(User user, Directory directory) {
        if (permissions.checkPermission(user, 'w')) {
            children.remove(directory);
        }
    }

    public long calcSize() {
        long size = 0;
        for (FileSystemEntry child : children) {
            if (child instanceof Directory) {
                size += ((Directory) child).calcSize();
            } else {
                size += ((File) child).getContent();
            }
        }
        return size;
    }
}

public class File extends FileSystemEntry {
    String type;
    String physical_address;
    long content;

    public File(String name, String type, User user, FileSystemEntry parent) {
        super(name, user, parent);
        this.type = type;
        this.physical_address = HardDrive.findEmptyLocation();
    }

    public long readFile(User user) {
        if (permissions.checkPermission(user, 'r')) {
            return HardDrive.get(physical_address, content);
        }
        return -1;
    }

    public void editFile(User user, long newContent) {
        if (permissions.checkPermission(user, 'w')) {
            content = newContent;
            HardDrive.set(physical_address, content);
            this.modified_date = new Date();
        }
    }

    public void executeFile(User user) {
        if (permissions.checkPermission(user, 'x')) {
            HardDrive.exe(physical_address, content);
        }
    }

    public long getContent() {
        return content;
    }
}

// The write permission on a directory gives you the authority to add, remove and rename files stored in the directory
public class Permissions {
    User owner;
    String permissions;

    // Default permissions set by the constructor
    public Permissions(User user) {
        changePermission(user, "rw-rw-r--");
    }

    // This method takes in a user and a 9-character string defined here: https://www.redhat.com/sysadmin/linux-file-permissions-explained
    public void changePermission(User user, String permissions) {
        // Only owner and admins can change permissions
        if (user == owner || user.getAdmin()) {
            owner = user;
            this.permissions = permissions;
        }
    }

    public Boolean checkPermission(User user, char action) {
        if (user.getAdmin()) {
            return true;
        } else if (user == owner) {
            return permissions.substring(0, 3).contains(String.valueOf(action));
        } else if (user.getGroup() == owner.getGroup()) {
            return permissions.substring(3, 6).contains(String.valueOf(action));
        } else {
            return permissions.substring(6, 9).contains(String.valueOf(action));
        }
    }
}

public class User {
    String username;
    String password;
    Group group;
    Boolean admin;

    public User(String username, String password, Group group, Boolean admin) {
        this.username = username;
        this.password = password;
        this.group = group;
        this.admin = admin;
    }

    public String getUsername() {
      return username;
    }

    public Group getGroup() {
        return group;
    }

    public Boolean getAdmin() {
        return admin;
    }
}

//singleton Admin 
public class Admin extends User {
    private static Admin instance = null;

    public Admin(String password) {
        super("admin", password, null, true);
    }

    public static Admin getInstance(String username, String password, Group group) {
        if (instance == null) {
            instance = new Admin(username, password, group);
        }
        return instance;
    }

}

public class Group {
    User[] users;
    int id;

    public Group(User[] users) {
        this.users = users;
    }

    public User[] getUsers() {
        return users;
    }
}
