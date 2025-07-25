mkdir -p build
find src -name "*.java" > sources.txt
javac -d build -cp "lib/*" @sources.txt
java -cp "build:lib/*" Main "$@"
