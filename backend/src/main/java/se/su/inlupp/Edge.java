//Prog2 VT2025, Inlämningsuppgift, del 1
//Grupp 361
//Jamal Cabanos jaca9541
package se.su.inlupp;

import java.io.Serializable;

public class Edge<T> implements Serializable {
  private static final long serialVersionUID = 1L;

  private int weight;
  private final T destination;
  private final String name;

  public Edge(int weight, T destination, String name) {
    this.weight = weight;
    this.destination = destination;
    this.name = name;
  }

  public int getWeight(){
    return weight;
  }

  public void setWeight(int weight) {
    if (weight < 0) {
      throw new IllegalArgumentException("Weight cannot be negative");
    }
    this.weight = weight;
  }

  public T getDestination(){
    return destination;
  }

  public String getName(){
    return name;
  }

  public String toString(){
    return "till " + getDestination() + " med " + getName() + " tar " + getWeight();
  }
}
