# Scheduled Command Executor

This utility allows you to schedule and execute commands from a text file either as one-time or recurring tasks. It supports both **Windows** and **Unix-like systems**, and logs all output to `output.txt`.

---

## 🚀 Quick Start (Windows)

To run the scheduler easily, just double-click the provided `run.bat` file:


> ✅ This will launch the Java-based scheduler and begin executing commands defined in `/tmp/commands.txt` (or modify the path in the code if needed).

---

## 🔧 Manual Compilation and Execution

If you prefer to compile and run manually:

### Compile & Run:
```bash
javac ScheduledCommandExecutor.java
java ScheduledCommandExecutor
```

## 📄 Command File Format

All scheduled commands must be stored in the file:
/tmp/commands.txt

> 💡 If you're on Windows, create a folder like `C:\tmp\commands.txt` or change the path in the Java code (`COMMAND_FILE` constant).

---

### 🔹 One-Time Scheduled Commands

These commands run **only once** at a specific date and time.

**Format:**
Minute Hour Day Month Year command

**Example:**
30 17 30 4 2025 date /t && echo At Amex, We Do What's Right.


This command will run at **5:30 PM on April 30, 2025**, and output the system date followed by a message.

---

### 🔁 Recurring Scheduled Commands

These commands run **repeatedly** every `n` minutes.

**Format:**
*/n <command>

Where `n` can be one of:
1, 2, 3, 4, 5, 6, 10, 12, 15, 20, 30, 60

**Examples:**
*/1 date /t && echo Amex' motto is 'Don't live life without it!'
*/2 date /t && echo Amex was founded in 1850.
*/5 date /t && echo Amex is headquartered in New York.


These commands will execute every 1, 2, and 5 minutes respectively.

> ⚠️ Use `date /t` and `echo` on Windows, and `date && echo` on Linux/macOS.
