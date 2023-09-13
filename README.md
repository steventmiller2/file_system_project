# file_system_project

Running the application:
1. Download the file-system-project.jar from build/libs
2. run `java -jar file-system-project.jar`
3. The database will connect and prompt a login screen which can be entered using one of three default users:<br />
    a. username: admin, password: admin<br />
    b. username: user1, password: password1<br />
    c. username: user2, password: password2
4. Inputting the commands `pwd`, `ls`, `mkdir`, `rmd`, `rm` should act as expected
5. Command `cd <dirName>` should act as expected, but only moves 1 directory up or down at a time
6. Command `touch <fileName>` will create a file and prompt you to add its contents
7. Command `cat <fileName>` will return its contents
8. Command `update-file <fileName>` will prompt you to update its contents
9. Command `chmod <entryName>` will allow you to change the public/private permissions on the file
10. Command `exit` will close the persistent database connection and close the application
11. Rerunning the application will prompt login, then place the user in the root directory and maintain the previously created directories and files

Permissions:
1. Two permission types:<br />
    Private - not able write or delete file entries you do not own<br />
    Public - open to read, write, and delete by all users
2. By default, any files and directories created are marked as private.
3. Admin has permissions to read/write/delete everything.
4. The owner/admin of the file entry can change the permissions by using the `chmod <entryName>` command
5. Example: if user1 creates file1, user2 will be able to see it but not read/write/delete it, however admin could


Build and Run Steps (if cloning repo):
<br>
./gradlew build
<br>
java -jar build/libs/file-system-project.jar
