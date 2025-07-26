# wget-java

A Java implementation of the `wget` utility for downloading files from the web with support for HTTP/HTTPS protocols.

## Features

- ✅ Download single files from URLs
- ✅ Save files with custom names (`-O`)
- ✅ Specify download directory (`-P`)
- ✅ Background downloads with logging (`-B`)
- ✅ Rate limiting (`--rate-limit`)
- ✅ Download multiple files from input file (`-i`) – *sync only*
- ✅ Website mirroring (`--mirror`)
- ✅ Progress tracking with visual progress bar
- ✅ Concurrent/asynchronous downloads (*partial/in progress*)

## Requirements

- Java 17 or higher
- [Maven](https://maven.apache.org/download.cgi)

## Installation

1. Clone the repository:

```bash
git clone https://github.com/hmaach/wget
cd wget
````

2. Compile the project using Maven:

```bash
mvn compile
```

3. Make the script executable (if not already):

```bash
chmod +x wget
```

Now you can run the project using:

```bash
./wget <arguments>
```

---

## Usage

### Basic Download

```bash
./wget https://example.com/file.zip
```

### Download with Custom Name

```bash
./wget -O=custom_name.zip https://example.com/file.zip
```

### Download to Specific Directory

```bash
./wget -P=/path/to/dir https://example.com/file.zip
```

### Background Download

```bash
./wget -B https://example.com/file.zip
# Logs are written to "wget-log"
```

### Rate Limited Download

```bash
./wget --rate-limit=500k https://example.com/file.zip
# Units supported: k (kilobytes), M (megabytes)
```

### Download Multiple Files from File

```bash
./wget -i=urls.txt
# Each line in urls.txt must be a valid URL
```

### Mirror Website

```bash
./wget --mirror https://example.com
```

### Mirror with Options

```bash
./wget --mirror -R jpg,gif,png https://example.com
./wget --mirror -X /js,/css https://example.com
./wget --mirror --convert-links https://example.com
```

---

## Flags

| Flag              | Description                           | Example             |
| ----------------- | ------------------------------------- | ------------------- |
| `-O`              | Save file with custom name            | `-O=myfile.zip`     |
| `-P`              | Specify download directory            | `-P=/downloads/`    |
| `-B`              | Download in background                | `-B`                |
| `-i`              | Download multiple files from file     | `-i=urls.txt`       |
| `--rate-limit`    | Limit download speed                  | `--rate-limit=400k` |
| `--mirror`        | Mirror entire website                 | `--mirror`          |
| `-R`              | Reject file types (with `--mirror`)   | `-R=jpg,gif`        |
| `-X`              | Exclude directories (with `--mirror`) | `-X=/js,/css`       |
| `--convert-links` | Convert links for offline use         | `--convert-links`   |

---

## Output Format

```
start at 2025-07-26 14:30:25
sending request, awaiting response... status 200 OK
content size: 1048576 [~1.0MB]
saving file to: ./example.zip
 1.00 MiB / 1.00 MiB [===============================================] 100.00% 2.5 MiB/s 0s

Downloaded [https://example.com/file.zip]
finished at 2025-07-26 14:30:26
```

---

## Project Structure

```
wget/
├── src/
│   ├── main/java/wget/
│   │   ├── Main.java                # Entry point
│   │   ├── WgetApplication.java     # Main application logic
│   │   ├── cli/                  # Command line parsing
│   │   ├── download/             # Core download functionality
│   │   ├── network/              # HTTP operations
│   │   ├── mirror/               # Website mirroring
│   │   ├── concurrent/           # Multi-threading support
│   │   ├── utils/                # Utility functions
│   │   └── models/               # Data models
├── test/             # Unit tests
├── pom.xml           # Maven build file
└── wget              # Bash script entrypoint
```

---

## Dependencies

* **Apache Commons CLI** – for command-line argument parsing
* **Apache Commons IO** – for file handling utilities
* **JSoup** – for HTML parsing and website mirroring

All dependencies are managed via Maven.

---

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

---

## Author

Hamza Maach