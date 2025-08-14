package se.su.inlupp;

import java.io.Serializable;
import java.util.*;

public class ListGraph<T> implements Graph<T>, Serializable {
  private static final long serialVersionUID = 1L;

  private Map<T, Set<Edge<T>>> nodes = new HashMap<>();
  @Override
  public void add(T node) {
    nodes.putIfAbsent(node, new HashSet<>());
  }

  @Override
  public void connect(T node1, T node2, String name, int weight) {

    if(weight < 0){
      throw new IllegalArgumentException("weight must be positive");
    }
    if(!nodes.containsKey(node1) || !nodes.containsKey(node2)){
      throw new NoSuchElementException("one of or both nodes don't exist");
    }
    for(Edge<T> e : nodes.get(node1)){
      if(e.getDestination().equals(node2)){
        throw new IllegalStateException("Connection already exists");
      }
    }

    add(node1);
    add(node2);
    Set<Edge<T>> fromNode = nodes.get(node1);
    Set<Edge<T>> toNode = nodes.get(node2);

    fromNode.add(new Edge<T>(weight, node2, name));
    toNode.add(new Edge<T>(weight, node1, name));

    nodes.put(node1, fromNode);
    nodes.put(node2, toNode);

  }

  @Override
  public void setConnectionWeight(T node1, T node2, int weight) {
    if (weight < 0){
      throw new IllegalArgumentException("weight must be positive");
    }
    if (!nodes.containsKey(node1) || !nodes.containsKey(node2)){
      throw new NoSuchElementException("one of or both nodes don't exist");
    }

    for(Edge<T> e : nodes.get(node1)) {
      if(e.getDestination().equals(node2)){
        e.setWeight(weight);
      }
    }
    for(Edge<T> e : nodes.get(node2)) {
      if(e.getDestination().equals(node1)){
        e.setWeight(weight);
      }
    }
  }

  @Override
  public Set<T> getNodes() {
    return Collections.unmodifiableSet(nodes.keySet());
  }

  @Override
  public Collection<Edge<T>> getEdgesFrom(T node) {
    if(!nodes.containsKey(node)){
      throw new NoSuchElementException("node does not exist");
    }
    return Collections.unmodifiableSet(nodes.get(node));
  }

  @Override
  public Edge<T> getEdgeBetween(T node1, T node2) {

    if(!nodes.containsKey(node1) || !nodes.containsKey(node2)){
      throw new NoSuchElementException("one of or both nodes don't exist");
    }

    for(Edge<T> e : nodes.get(node1)) {
      if(e.getDestination().equals(node2)){
        return e;
      }

    }
    return null;
  }

  @Override
  public void disconnect(T node1, T node2) {
    if(!nodes.containsKey(node1) || !nodes.containsKey(node2)){
      throw new NoSuchElementException("one of or both nodes don't exist");
    }
    if(nodes.get(node1).stream().noneMatch(e->e.getDestination().equals(node2))) {
      throw new IllegalStateException("connection does not exist");
    }

    nodes.get(node1).removeIf(e->e.getDestination().equals(node2));
    nodes.get(node2).removeIf(e->e.getDestination().equals(node1));

  }

  @Override
  public void remove(T node) {
    if(!nodes.containsKey(node)){
      throw new NoSuchElementException("node does not exist");
    }
    for(Edge<T> e : new ArrayList<>(nodes.get(node))){
      disconnect(node, e.getDestination());
    }
    nodes.remove(node);
  }

  @Override
  public boolean pathExists(T from, T to) {
    if(!nodes.containsKey(from) || !nodes.containsKey(to)) return false;
    Set<T> visited = new HashSet<>();
    recursiveVisitDFS(from, visited);
    return visited.contains(to);
  }

  @Override
  public List<Edge<T>> getPath(T from, T to) {
    if(!pathExists(from, to)) return null;
    List<T> visited = new LinkedList<>();
    List<Edge<T>> path = new ArrayList<>();
    LinkedList<T> queue = new LinkedList<>();
    queue.add(from);

    //find a path
    while (!queue.isEmpty()) {

      T current = queue.poll();
      visited.add(current);
      if (current.equals(to)) {
        System.out.println("found path");
        break;
      }
      for (Edge<T> e : nodes.get(current)) {
        if (!visited.contains(e.getDestination())) {
          queue.add(e.getDestination());
        }
      }
    }

    //replicate path to save it
    while(to != from){
      for(T node : visited){
        if (getEdgeBetween(node, to) != null){
          path.add(getEdgeBetween(node, to));
          to = node;
          break;
        }
      }
    }
    Collections.reverse(path);
    return Collections.unmodifiableList(path);

  }

  public String toString(){
    String fullString = "";
    for(T node : nodes.keySet()){
      fullString += node.toString() + "\n";
      for(Edge<T> e : nodes.get(node)){
        fullString += "     " + e.toString() + "\n";
      }
    }
    return fullString;
  }

  //hjälpmetoder
  private void recursiveVisitDFS(T node, Set<T> visited) {
    visited.add(node);
    for (Edge<T> e : nodes.get(node)) {
      if (!visited.contains(e.getDestination())) {
        recursiveVisitDFS(e.getDestination(), visited);
      }
    }
  }
}

