J = java
JC = javac

BIN = ./bin/
SRC = ./src/
TARGET = Main

classes:
	$(JC) -d $(BIN) $(SRC)*.java

run:
	$(J) -cp $(BIN) $(TARGET)

clean:
	rm -rfv -- $(BIN)/
