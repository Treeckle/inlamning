package se.su.inlupp;

import java.util.*;

public class ListGraph<T> implements Graph<T> {

  private Map<T, Set<Edge>> nodes = new HashMap<>();
  @Override
  public void add(T node) {
    nodes.putIfAbsent(node, new HashSet<>());
  }

  @Override
  public void connect(T node1, T node2, String name, int weight) {
    add(node1);
    add(node2);
    Set<Edge> fromNode = nodes.get(node1);
    Set<Edge> toNode = nodes.get(node2);

    fromNode.add(new Edge(weight, node2, name));
    toNode.add(new Edge(weight, node1, name));

    nodes.put(node1, fromNode);
    nodes.put(node2, toNode);

  }

  @Override
  public void setConnectionWeight(T node1, T node2, int weight) {
    throw new UnsupportedOperationException("Unimplemented method 'setConnectionWeight'");
  }

  @Override
  public Set<T> getNodes() {
    return Collections.unmodifiableSet(nodes.keySet());
  }

  @Override
  public Collection<Edge<T>> getEdgesFrom(T node) {
    throw new UnsupportedOperationException("Unimplemented method 'getEdgesFrom'");
  }

  @Override
  public Edge<T> getEdgeBetween(T node1, T node2) {
    throw new UnsupportedOperationException("Unimplemented method 'getEdgeBetween'");
  }

  @Override
  public void disconnect(T node1, T node2) {
    throw new UnsupportedOperationException("Unimplemented method 'disconnect'");
  }

  @Override
  public void remove(T node) {
    throw new UnsupportedOperationException("Unimplemented method 'remove'");
  }

  @Override
  public boolean pathExists(T from, T to) {
    throw new UnsupportedOperationException("Unimplemented method 'pathExists'");
  }

  @Override
  public List<Edge<T>> getPath(T from, T to) {
    throw new UnsupportedOperationException("Unimplemented method 'getPath'");
  }
}
