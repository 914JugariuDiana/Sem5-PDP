package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Graph graph = new Graph(new String("C:\\Users\\diaju\\Desktop\\School\\PLP\\lab6\\src\\main\\java\\org\\example\\graph.txt"));
        graph.findHamiltonianCycle();
    }
}