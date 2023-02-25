# Makefile para compilar o projeto de TP01
# Autores: Gabriel Vargas Bento de Souza e Nilson Deon Cordeiro Filho
# Data: 02/2023

# Arquivos de origem
SOURCES := Musica.java CRUD.java OrdenacaoExterna.java Main.java

# Compilar
all: $(SOURCES:.java=.class)

%.class: %.java
	javac $<

# Executar
run: Main.class
	java Main

# Limpar
clean:
	rm -f *.class