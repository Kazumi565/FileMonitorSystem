# File Monitor System

A Java console app that checks a folder every five seconds and reports added,
changed and deleted entries. You can also inspect text, image, Java and Python
files. It watches one directory at a time, without scanning subfolders.

## Run the bundled app

The bundled JAR and `bin/` classes require Java 17 or later. Run the JAR
from the repository root to watch `ChangesPath`.

```sh
java -jar FileMonitorApplication.jar
```

Pass a directory path to watch a different folder.

```sh
java -jar FileMonitorApplication.jar "path/to/folder"
```

You can also run the bundled classes with `java -cp bin filemonitor.Main`.

## Build from source

Run these commands from the repository root with JDK 17 or later.

```sh
javac -d out src/filemonitor/*.java
java -cp out filemonitor.Main
```

Pass a directory path to watch a different folder.

```sh
java -cp out filemonitor.Main "path/to/folder"
```

On Windows, `runFileMonitor.bat` compiles the current source before launching it.
It accepts an optional folder argument. Use an absolute path for the watched
folder when launching the script from another directory.

## Commands

| Command | Action |
| --- | --- |
| `status` | Compare entries with the snapshot from startup or the last `commit`. |
| `commit` | Refresh the in-memory snapshot used by `status`. This does not run Git. |
| `info` | Prompt for a file name and display information about it. |
| `exit` or `quit` | Stop polling and close the application. |

Background notifications compare consecutive polls. A change that is undone
between checks may go unnoticed. Code statistics come from simple text matching
and can be inaccurate. The app does not keep a permanent change log.
