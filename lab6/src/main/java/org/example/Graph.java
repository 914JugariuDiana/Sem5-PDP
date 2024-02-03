package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Graph {
    ArrayList<Node> nodes;
    boolean found = false;
    ReadWriteLock rwLock;
    ArrayList<Node> result;
    ArrayList<Thread> threads;
    Lock threadArrayMutex;

    public Graph(String filename) throws IOException {
        this.nodes = new ArrayList<>();
        this.readGraph(filename);
        this.rwLock = new ReentrantReadWriteLock();
        this.result = new ArrayList<>();
        this.threads = new ArrayList<>();
        this.threadArrayMutex = new ReentrantLock();
    }

    private void readGraph(String filename) throws IOException {
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;

        st = br.readLine();
        st = st.trim();
        for (int i = 0; i < Integer.parseInt(st); i++){
            this.nodes.add(new Node(i));
        }

        while ((st = br.readLine()) != null){
            String[] res = st.trim().split(" ");
            nodes.get(Integer.parseInt(res[0])).addNextNode(nodes.get(Integer.parseInt(res[1])));
        }

    }

    public void findHamiltonianCycle() throws InterruptedException {
        ArrayList<Node> currentNodes = new ArrayList<>();
        currentNodes.add(nodes.get(0));
//        searchCycleSequentially(currentNodes);
        searchCycleParallel(currentNodes);
//
        Thread.sleep(10000);
//        threadArrayMutex.lock();
        for (Thread thread : threads){
            thread.join();
        }
//        threadArrayMutex.unlock();

        System.out.println(result);
    }

    public void searchCycleSequentially(ArrayList<Node> currentNodes){
        System.out.println("Current nodes traveled: " + currentNodes.size() + ", at thread with ID: " + Thread.currentThread().threadId());
        Node currentNode = currentNodes.get(currentNodes.size() - 1);
        rwLock.readLock().lock();
        if (found || currentNodes.size() > nodes.size()) {
            rwLock.readLock().unlock();
        } else if (currentNodes.size() == nodes.size()) {
            rwLock.readLock().unlock();
            if (currentNode.getNextNodes().contains(currentNodes.get(0))){
                rwLock.writeLock().lock();
                found = true;
                result = new ArrayList<>(currentNodes);
                rwLock.writeLock().unlock();
            }
        }  else {
            rwLock.readLock().unlock();
            ArrayList<Node> neighbours = currentNode.getNextNodes();
            for (Node neighbour : neighbours) {
                if (!currentNodes.contains(neighbour)) {
                    rwLock.readLock().lock();
                    if (!found) {
                        currentNodes.add(neighbour);
                        rwLock.readLock().unlock();
                        searchCycleSequentially(currentNodes);
                        currentNodes.remove(neighbour);
                    }
//                    rwLock.readLock().unlock();
                }
            }
        }
    }

    public void searchCycleParallel(ArrayList<Node> currentNodes){
        Node currentNode = currentNodes.get(currentNodes.size() - 1);
        rwLock.readLock().lock();
        if (found || currentNodes.size() > this.nodes.size()) {
            rwLock.readLock().unlock();
//        } else if (currentNodes.size() == nodes.size()) {
//            rwLock.readLock().unlock();
//            if (currentNode.getNextNodes().contains(currentNodes.get(0))){
//                rwLock.writeLock().lock();
//                found = true;
//                result = currentNodes;
//                rwLock.writeLock().unlock();
//            }
        } else if (currentNodes.size() <= Math.sqrt(nodes.size())) {
            System.out.println("Current nodes traveled: " + currentNodes.size() + ", at thread with ID: " + Thread.currentThread().threadId());
            rwLock.readLock().unlock();
            ArrayList<Node> neighbours = currentNode.getNextNodes();
            for (Node neighbour : neighbours) {
                if (!currentNodes.contains(neighbour)) {
                    rwLock.readLock().lock();
                    if (!found) {
                        this.threadArrayMutex.lock();
                        rwLock.readLock().unlock();
                        this.threads.add(new Thread(new Runnable() {
                            final ArrayList<Node> nodes = new ArrayList<>(currentNodes);

                            @Override
                            public void run() {
                                nodes.add(neighbour);
                                searchCycleParallel(nodes);
                            }
                        }));

                        this.threadArrayMutex.unlock();
                        this.threads.get(this.threads.size() - 1).start();
                    } else {
                        rwLock.readLock().unlock();
                    }
                }
            }
        } else {
                rwLock.readLock().unlock();
                searchCycleSequentially(currentNodes);
        }
    }
}
