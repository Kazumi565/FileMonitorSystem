# File Monitor System

A Java console app that checks a folder every five seconds and reports added,
changed and deleted entries. You can also inspect text, image, Java and Python
files. It watches one directory at a time, without scanning subfolders.

## Build and run

Install a JDK (verified with Java 21), then run from the repository root:

```sh
javac -d out src/filemonitor/*.java
java -cp out filemonitor.Main
```

The default folder is `ChangesPath`. To watch another folder:

```sh
java -cp out filemonitor.Main "path/to/folder"
```

On Windows, `runFileMonitor.bat` compiles the current source before launching it.
It accepts an optional folder argument too; use an absolute path when launching
it from another directory. The bundled JAR and `bin/` classes
may be out of date; the commands above compile and run the source.

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
