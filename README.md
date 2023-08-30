# file_system_project

Running the application:
1. Download the file-system-project.jar from build/libs
2. run ***java -jar file-system-project.jar***
3. The database will connect and prompt a user input
4. Inputting the command ***pwd*** will print the working directory root
5. Command ***ls*** will print the directories and files in the current directory
6. Command ***mkdir*** will prompt you to make a new directory in the current directory
7. Command ***rmd*** will prompt you to select a directory to delete
8. Command ***touch*** will prompt you to make a new file with content in the current directory
9. Command ***cat*** will prompt you to select a file and will return its contents
10. Command ***update-file*** will prompt you to select a file and update its contents
11. Command ***rm*** will prompt you to select a file to delete
12. Command ***exit*** will close the persistent database connection and close the application
13. Rerunning the application will place the user in the root directory and maintain the previously created directories and files


Build and Run Steps (if cloning repo):
<br>
./gradlew build
<br>
java -jar build/libs/file-system-project.jar
