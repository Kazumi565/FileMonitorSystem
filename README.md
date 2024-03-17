# File Monitor System

File Monitor System is a Java program that monitors and detects changes in documents within a designated folder. It provides an interactive command-line interface for users to monitor changes, view file information, and track status updates in real-time.

## Getting Started

Follow these steps to get the File Monitor System up and running on your local machine.

### Prerequisites

- Java Development Kit (JDK) installed on your machine
- Git for cloning the repository (optional)

### Installation

1. Clone the repository to your local machine using Git:

    ```bash
    git clone https://github.com/Kazumi565/FileMonitorSystem.git
    ```

    Alternatively, you can download the repository as a ZIP file and extract it to a location of your choice.

2. Navigate to the cloned/downloaded directory:

    ```bash
    cd FileMonitorSystem
    ```

3. Compile the Java source files:

    ```bash
    javac Main.java
    ```

   Copy the `FileMonitorApplication.jar` and `runFileMonitor.bat` files to your project directory.

## Usage

1. Navigate to the `ChangesPath` directory within the project. This directory contains the files to be monitored.

    ```bash
    cd C:\Users\Sergiu\eclipse-workspace\FileMonitor\ChangesPath
    ```

   Replace `C:\Users\Sergiu\eclipse-workspace\FileMonitor\ChangesPath` with the actual path to your ChangesPath directory if it's different.

2. Run the program by opening the `runFileMonitor.bat` file. This batch script will execute the necessary commands to start the file monitoring application.

3. Alternatively, you can run the program using the compiled main class:

    ```bash
    java Main
    ```

4. Use the following commands to interact with the program:

    - **commit**: Update the snapshot time to the current time.
    - **info**: View information about a specific file. When prompted, enter the name of the file (including the appropriate extension).
    - **status**: Check the status of files in the folder.

### Example Usage:

```bash
java Main
> commit
> info
Enter file name: test.txt

This command will display detailed information about the test.txt file, including its extension, creation and modification dates, line count, word count, and character count.

java Main
> commit
> status

This command will display the status of all files in the folder, indicating whether each file has been changed or not since the last snapshot.
```
## Repository Structure

- **src/**: Contains Java source files.
- **ChangesPath/**: Contains the files to be monitored.
- **README.md**: Instructions and information about the project.
-**FileMonitorApplication.jar**: Java executable file for the file monitoring application.
-**runFileMonitor.bat**: Batch script to run the file monitoring application.
- **.gitignore**: Lists files to be ignored by Git.

---

## Contributors

- Bargan Mihai: [Kazumi565](https://github.com/Kazumi565)

---
## Contributing

Contributions are welcome! If you'd like to contribute to the project, please fork the repository and submit a pull request with your changes.
