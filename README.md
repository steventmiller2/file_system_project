# file_system_project

Running the application:
1. Download the file-system-project.jar from build/libs
2. run ***java -jar file-system-project.jar***
3. The database will connect and prompt a login screen which can be entered using one of three default users (see explaination of how I managed permissions):\
    a. username: admin, password: admin\
    b. username: user1, password: password1\
    c. username: user2, password: password2
4. Inputting the commands ***pwd***, ***ls***, ***mkdir***, ***rmd***, ***rm*** should act as expected
5. Command ***touch <file name>*** will create a file and prompt you to add its contents
6. Command ***cat <file name>*** will return its contents
7. Command ***update-file <file name>*** will prompt you to update its contents
8. Command ***chmod <file/directory name>*** will allow you to change the public/private permissions on the file
9. Command ***exit*** will close the persistent database connection and close the application
10. Rerunning the application will prompt login, then place the user in the root directory and maintain the previously created directories and files

Permissions:
1. Two permission types:\
    Private - not able read, write, or delete file entries you do not own\
    Public - open to read, write, and delete by all users
2. By default, any files and directories created are marked as private.
3. Admin has permissions to read/write/delete everything.
4. The owner/admin of the file entry can change the permissions by using the ***chmod <file entry name>*** command


Build and Run Steps (if cloning repo):
<br>
./gradlew build
<br>
java -jar build/libs/file-system-project.jar
