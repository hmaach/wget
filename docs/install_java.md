# Installing Java on Ubuntu (OpenJDK)

This guide will help you install the Java Development Kit (JDK) on Ubuntu-based systems.

## 🧰 Requirements

- Ubuntu 22.04, 24.04 or later
- sudo privileges

## 🧭 Step-by-Step Installation

### 1. Update Package Index

```bash
sudo apt update
````

### 2. Install OpenJDK

Choose the version you want:

#### 🔹 Recommended (LTS): Java 21

```bash
sudo apt install openjdk-21-jdk
```

#### 🔸 Other Options

* Java 17 (LTS):

  ```bash
  sudo apt install openjdk-17-jdk
  ```

* Java 22 (Latest, non-LTS):

  ```bash
  sudo apt install openjdk-22-jdk
  ```

### 3. Verify Installation

```bash
java -version
javac -version
```

Expected output (for example):

```
openjdk version "21.0.6" 2024-07-16
OpenJDK Runtime Environment (build 21.0.6+7)
OpenJDK 64-Bit Server VM (build 21.0.6+7, mixed mode, sharing)
```

### 4. (Optional) Set JAVA\_HOME

Add this to your shell config file (`~/.bashrc`, `~/.zshrc`, etc.):

```bash
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
export PATH=$JAVA_HOME/bin:$PATH
```

Then reload your shell:

```bash
source ~/.bashrc
```

---

## ✅ Done!

You now have Java installed and ready for compiling and running Java applications.
