# wget-java

A Java implementation of the wget utility for downloading files from the web with support for HTTP/HTTPS protocols.

## Features

- ✅ Download single files from URLs
- ✅ Save files with custom names (-O flag)
- ✅ Specify download directory (-P flag)
- ✅ Background downloads with logging (-B flag)
- ✅ Rate limiting (--rate-limit flag)
- ✅ Multiple file downloads from input file (-i flag)
- ✅ Website mirroring (--mirror flag)
- ✅ Progress tracking with visual progress bar
- ✅ Concurrent/asynchronous downloads

## Requirements

- Java 8 or higher
- Internet connection for downloads

## Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd wget-java
```

2. Compile the project:
```bash
javac -d build -cp "lib/*" src/**/*.java
```

3. Run the application:
```bash
java -cp "build:lib/*" Main <arguments>
```

## Usage

### Basic Download
```bash
java -cp "build:lib/*" Main https://example.com/file.zip
```

### Download with Custom Name
```bash
java -cp "build:lib/*" Main -O=custom_name.zip https://example.com/file.zip
```

### Download to Specific Directory
```bash
java -cp "build:lib/*" Main -P=/path/to/directory/ https://example.com/file.zip
```

### Background Download
```bash
java -cp "build:lib/*" Main -B https://example.com/file.zip
# Output will be written to "wget-log"
```

### Rate Limited Download
```bash
java -cp "build:lib/*" Main --rate-limit=500k https://example.com/file.zip
# Supports: k (kilobytes), M (megabytes)
```

### Multiple Files Download
```bash
java -cp "build:lib/*" Main -i=urls.txt
# urls.txt contains one URL per line
```

### Mirror Website
```bash
java -cp "build:lib/*" Main --mirror https://example.com
```

### Mirror with Options
```bash
# Reject specific file types
java -cp "build:lib/*" Main --mirror -R=jpg,gif,png https://example.com

# Exclude directories
java -cp "build:lib/*" Main --mirror -X=/js,/css https://example.com

# Convert links for offline viewing
java -cp "build:lib/*" Main --mirror --convert-links https://example.com
```

## Flags

| Flag | Description | Example |
|------|-------------|---------|
| `-O` | Save file with custom name | `-O=myfile.zip` |
| `-P` | Specify download directory | `-P=/downloads/` |
| `-B` | Download in background | `-B` |
| `-i` | Download multiple files from file | `-i=urls.txt` |
| `--rate-limit` | Limit download speed | `--rate-limit=400k` |
| `--mirror` | Mirror entire website | `--mirror` |
| `-R` | Reject file types (with --mirror) | `-R=jpg,gif` |
| `-X` | Exclude directories (with --mirror) | `-X=/js,/css` |
| `--convert-links` | Convert links for offline (with --mirror) | `--convert-links` |

## Output Format

```
start at 2024-01-15 14:30:25
sending request, awaiting response... status 200 OK
content size: 1048576 [~1.0MB]
saving file to: ./example.zip
 1.00 MiB / 1.00 MiB [===============================================] 100.00% 2.5 MiB/s 0s

Downloaded [https://example.com/file.zip]
finished at 2024-01-15 14:30:26
```

## Project Structure

```
wget-java/
├── src/
│   ├── Main.java                 # Entry point
│   ├── WgetApplication.java      # Main application logic
│   ├── cli/                      # Command line parsing
│   ├── download/                 # Core download functionality
│   ├── network/                  # HTTP operations
│   ├── mirror/                   # Website mirroring
│   ├── concurrent/               # Multi-threading support
│   ├── utils/                    # Utility functions
│   └── models/                   # Data models
├── test/                         # Unit tests
├── lib/                          # External dependencies
└── build/                        # Compiled classes
```

## Dependencies

- Apache Commons CLI - Command line argument parsing
- JSoup - HTML parsing for website mirroring
- Apache Commons IO - File operations utilities

## Building

### Simple Compilation
```bash
javac -d build -cp "lib/*" src/**/*.java
```

### Running Tests
```bash
java -cp "build:lib/*" org.junit.runner.JUnitCore download.DownloaderTest
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## Author

Hamza Maach