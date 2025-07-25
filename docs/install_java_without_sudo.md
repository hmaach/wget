### ✅ Step-by-Step: Install Java without `sudo`

#### 1. 🔽 Download Java (JDK)

Go to the [Adoptium (Temurin) JDK Downloads](https://adoptium.net/en-GB/temurin/releases/) – it's a reliable open-source JDK distribution.

Choose:

* **JDK 17 or JDK 21** (both are LTS and compatible with Spring Boot)
* Architecture: **Linux x64**
* Format: **tar.gz**

Or use `wget` in terminal:

```bash
cd ~
wget https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.11%2B9/OpenJDK17U-jdk_x64_linux_hotspot_17.0.11_9.tar.gz
```

> 📌 You can replace this URL with a newer version if needed.

---

#### 2. 📦 Extract the JDK

```bash
mkdir -p ~/java
tar -xzf OpenJDK17U-jdk_x64_linux_hotspot_17.0.11_9.tar.gz -C ~/java
```

Check what folder was extracted:

```bash
ls ~/java
```

You should see something like: `jdk-17.0.11+9`

---

#### 3. ⚙️ Set Up Environment Variables

Add this to your `~/.bashrc` or `~/.zshrc`:

```bash
# Java setup
echo 'export PATH=~/java/jdk-17.0.11+9/bin:$PATH' >> ~/.zshrcj

```

Then apply the changes:

```bash
source ~/.zshrc  # or source ~/.bashrc 
```

---

#### 4. ✅ Verify Installation

```bash
java -version
```

You should see output like:

```bash
openjdk version "17.0.11" 2024-04-16
OpenJDK Runtime Environment Temurin-17.0.11+9
```

---

### ✅ You're Done!

You can now:

* Compile and run Java programs.
* Use this JDK in local development.
* Run Spring Boot apps (with Gradle/Maven installed similarly).
