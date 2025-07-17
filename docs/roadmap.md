# 📆 Project Roadmap: Java Wget Clone

This roadmap breaks down the wget project into weekly, daily, and feature-based goals to help you complete the project in 3–4 weeks. It's optimized for someone with Java experience at a beginner level, but strong general programming skills.

---

## 🗓️ Week 1: Project Setup & Basic Download

**Goal:** Build the foundation — one working download with clean output and structure.

### ✅ Day 1-2: Environment & Skeleton

- [ ] Initialize `wget-java` project structure
- [ ] Create `Main.java` and `WgetApplication.java`
- [ ] Set up `build/` and `lib/` folders
- [ ] Add README, `.gitignore`, and helper shell scripts (optional)
- [ ] Print `"Hello, world!"` to verify setup

### ✅ Day 3-4: Basic HTTP Download

- [ ] Learn `HttpURLConnection` or `HttpClient`
- [ ] Send GET request and handle response
- [ ] Download a file given a URL
- [ ] Print:
  - Start time (yyyy-mm-dd hh:mm:ss)
  - HTTP response status
  - Content size (bytes and MB)
  - File name and path

### ✅ Day 5-6: Basic Progress Bar

- [ ] Show current download size and percentage
- [ ] Calculate ETA and download speed
- [ ] Log finish time

---

## 🗓️ Week 2: Core Feature Flags

**Goal:** Make the CLI useful with file naming, directories, backgrounding, and rate-limiting.

### ✅ Day 1: Argument Parsing

- [ ] Use `Apache Commons CLI` to handle arguments
- [ ] Implement:
  - `Main <url>`
  - `-O=name.zip`
  - `-P=/path/`

### ✅ Day 2: File Saving Options

- [ ] Save to custom name
- [ ] Save to specified directory
- [ ] Handle missing directories and create them

### ✅ Day 3: Background Download (`-B`)

- [ ] Use `ProcessBuilder` or thread-based backgrounding
- [ ] Write output logs to `wget-log`
- [ ] Support both standard and background modes

### ✅ Day 4-5: Rate Limiting

- [ ] Implement `--rate-limit=400k/2M` flag
- [ ] Limit throughput using `Thread.sleep()` and stream control

---

## 🗓️ Week 3: Advanced Downloads

**Goal:** Support downloading multiple files at once, with concurrent threads and clean UI.

### ✅ Day 1-2: Multiple Files from Input File (`-i`)

- [ ] Read a file with a list of URLs
- [ ] Queue downloads from the list
- [ ] Cleanly report progress for each

### ✅ Day 3-4: Concurrent Downloads

- [ ] Use `ExecutorService` or custom threading
- [ ] Download multiple files in parallel
- [ ] Display status separately or as a queue

### ✅ Day 5: Error Handling

- [ ] Handle:
  - 404 / 403 / 500 errors
  - Unreachable URLs
  - Write failures
- [ ] Print error messages clearly
- [ ] Retry logic (optional)

---

## 🗓️ Week 4: Website Mirroring

**Goal:** Implement recursive mirroring using HTML parsing and file system mapping.

### ✅ Day 1-2: Basic Mirroring (`--mirror`)

- [ ] Download HTML page
- [ ] Parse `<a>`, `<img>`, `<link>` with JSoup
- [ ] Queue downloads for assets

### ✅ Day 3: Save Website Structure

- [ ] Create folder `domain.com/`
- [ ] Save files to relative paths (e.g., `domain.com/images/logo.png`)

### ✅ Day 4: Filtering

- [ ] Reject file types (`-R=jpg,png`)
- [ ] Exclude directories (`-X=/css,/js`)
- [ ] Use simple filter logic on URLs before queuing

### ✅ Day 5: Convert Links (`--convert-links`)

- [ ] Update HTML files to link to local resources
- [ ] Replace absolute URLs with relative paths

---

## 🧪 Testing (Ongoing Throughout)

- [ ] Write unit tests in `test/` folder
- [ ] Add test cases for:
  - CLI parsing
  - Downloader logic
  - Mirror logic
  - Rate limiter

---

## 🧹 Optional Polish & Optimization

- [ ] Progress bar UI (with console refresh)
- [ ] Graceful shutdown on Ctrl+C
- [ ] Log download stats to file
- [ ] Automatic file name detection from headers

---

## 🧠 Java Topics to Deepen As You Go

| Topic              | Importance     |
|-------------------|----------------|
| HTTP (Java Client)| ⭐⭐⭐⭐⭐          |
| Streams & Buffers | ⭐⭐⭐⭐⭐          |
| File I/O          | ⭐⭐⭐⭐⭐          |
| Threading         | ⭐⭐⭐⭐           |
| CLI Parsing (Commons CLI) | ⭐⭐⭐        |
| Recursion         | ⭐⭐⭐            |
| HTML Parsing (JSoup)| ⭐⭐⭐⭐         |

---

## ⏱️ Time Management

| Daily Hours | Timeline     |
|-------------|--------------|
| 2–3 hours   | 4 weeks      |
| 4–5 hours   | 3 weeks      |
| Full-time   | 2 weeks      |

---

## ✅ Milestone Checklist

- [ ] Basic HTTP download
- [ ] CLI with `-O`, `-P`, `-B`, `--rate-limit`
- [ ] Multiple and concurrent downloads
- [ ] Website mirroring with filters
- [ ] Clean terminal output with timestamps
- [ ] Error handling and logging

---

> Keep commits small and test after each feature.
> Don’t block on styling or optional flags early — get the core logic working.

Happy coding! 🧠💻