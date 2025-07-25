# 🧠 Project Knowledge Base: Java Wget Clone (`wget-java`)

## 📌 Summary

A command-line Java application that replicates key functionalities of the popular Linux tool `wget`. It enables downloading single or multiple files, mirroring websites, and customizing download behavior using a rich CLI interface. Built in pure Java with optional dependencies for advanced features.

---

## 📁 Project Structure

```
wget-java/
├── src/
│   ├── Main.java                 # Entry point
│   ├── WgetApplication.java     # App logic dispatcher
│   ├── cli/                     # (Planned) CLI parsing logic using Apache Commons CLI
│   ├── download/Downloader.java # Core download logic (already implemented)
│   ├── network/                 # (Planned) Low-level HTTP networking
│   ├── mirror/                  # (Planned) Website mirroring logic
│   ├── concurrent/              # (Planned) Threaded/multi-download support
│   ├── utils/                   # (Planned) Utility functions (e.g., logging, validation)
│   └── models/                  # (Planned) Data models
├── build/                       # Compiled .class files (ignored by git)
├── lib/                         # External JARs (e.g., JSoup, Apache Commons CLI)
├── test/                        # Unit tests (planned)
├── build.sh                     # Compile and run script
├── run.sh                       # Simplified run script with argument support
├── README.md                    # Full feature list, usage examples
└── .gitignore                   # Ignores build/, logs, class files
```

---

## ✅ Completed Features

### ✔️ Environment & Skeleton

* Setup of project structure
* `Main.java` and `WgetApplication.java` scaffolded
* `build.sh` script compiles and runs with proper classpath handling
* Shell script supports argument forwarding using `"$@"`

### ✔️ Basic HTTP Download (`Downloader.java`)

* Accepts a URL and downloads the file using `HttpURLConnection`
* Displays:

  * Start time (`yyyy-MM-dd HH:mm:ss`)
  * HTTP response status
  * Content size (bytes + MB)
  * Filename (auto-detected)
  * Finish time
* Handles network and I/O exceptions
* Uses `try-with-resources` for safe file streaming
* Extracts filename from URL automatically, stripping query params

```java
String file = url.substring(url.lastIndexOf('/') + 1).split("\\?")[0];
```

---

## 🧱 Roadmap Overview

> Estimated project duration: **3–4 weeks**
> Suitable for a **Java beginner** with strong general programming background

### 🗓️ Week 1: Foundation

#### ✅ Day 1–2: Environment & Skeleton

* Project structure
* Hello world setup
* Shell scripts for building and running

#### ✅ Day 3–4: Basic HTTP Download

* Use `HttpURLConnection`
* Download file and print metadata

#### 🔜 Day 5–6: Progress Bar

* Show download percentage
* Estimate download speed and ETA

---

### 🗓️ Week 2: Core Features

#### Day 1: CLI Flag Parsing

* Use `Apache Commons CLI`
* Support:

  * `-O=name.zip` (custom output name)
  * `-P=/path/` (output directory)

#### Day 2: File Saving Options

* Handle:

  * File renaming
  * Directory creation

#### Day 3: Background Downloads

* Support `-B` to run in background (thread or `ProcessBuilder`)
* Log output to `wget-log`

#### Day 4–5: Rate Limiting

* Implement `--rate-limit=400k/2M`
* Control throughput using `Thread.sleep()` + byte throttling

---

### 🗓️ Week 3: Advanced Downloading

#### Day 1–2: Input File `-i`

* Read URLs from a text file
* Download each sequentially or with a queue

#### Day 3–4: Concurrent Downloads

* Use `ExecutorService` or threads
* Support multi-file download in parallel

#### Day 5: Error Handling

* Retry failed downloads
* Handle 404/403/server errors cleanly

---

### 🗓️ Week 4: Website Mirroring

#### Day 1–2: Basic HTML Parsing

* Use JSoup to extract:

  * `<img src>`
  * `<a href>`
  * `<link>` and assets

#### Day 3: Save Folder Structure

* Replicate website paths
* Save assets in local folders

#### Day 4: Filters

* `-R=jpg,png` — reject file types
* `-X=/js,/css` — exclude paths

#### Day 5: `--convert-links`

* Update local HTML to link to downloaded assets
* Make site viewable offline

---

## 🧪 Testing & Extras (ongoing)

* Unit tests in `/test` directory
* Tests for:

  * URL parsing
  * CLI flags
  * File writing
  * Rate limiter
  * Mirror mode
* Optional:

  * Graceful Ctrl+C
  * Log to file
  * Intelligent file naming from headers

---

## 🧠 Java Concepts in Use

| Concept              | Status     | Notes                                 |
| -------------------- | ---------- | ------------------------------------- |
| `HttpURLConnection`  | ✅ Done     | Used for downloads                    |
| CLI Argument Parsing | 🕒 Pending | Use Apache Commons CLI (planned)      |
| Stream I/O           | ✅ Done     | Buffered streaming with `byte[]`      |
| `try-with-resources` | ✅ Done     | Ensures input/output streams close    |
| Threads / Executors  | 🕒 Week 3  | Needed for concurrent download / `-B` |
| File system ops      | 🕒 Week 2  | For `-P` path, file renaming          |
| JSoup (HTML parsing) | 🕒 Week 4  | For `--mirror`, `<a>`, `<img>`        |

---

## 💡 Sample Usage Examples (Planned)

```bash
# Basic download
java -cp "build:lib/*" Main https://example.com/file.zip

# Custom name
java -cp "build:lib/*" Main -O=myfile.zip https://example.com/file.zip

# Save to folder
java -cp "build:lib/*" Main -P=/tmp/downloads/ https://example.com/file.zip

# Background download
java -cp "build:lib/*" Main -B https://example.com/file.zip

# Rate limit
java -cp "build:lib/*" Main --rate-limit=500k https://example.com/file.zip

# Multiple files
java -cp "build:lib/*" Main -i=urls.txt

# Mirror website
java -cp "build:lib/*" Main --mirror https://example.com
```

---

## 🧠 Example Code Fragments

### CLI forwarding in `run.sh`:

```bash
java -cp "build:lib/*" Main "$@"
```

### Looping through args in Java:

```java
for (String url : args) {
    downloader.downloadFile(url);
}
```

---

## 🧼 .gitignore Example

```
/build/
/*.class
wget-log
*.log
.DS_Store
```

---

## 👨‍💻 Author Info

* **Name**: Hamza Maach
* **Project**: `wget-java`
* **Phase**: Active development, just finished core download logic

