import java.io.Serializable;
public class Assignment implements Serializable, Comparable<Assignment> {
  private String name;
  private int points;
  private double weight;

  public Assignment(String name, int points, double weight) {
    this.name = name;
    this.points = points;
    this.weight = weight;
  }

  public String getName() {
    return name;
  }

  public int getPoints() {
    return points;
  }

  public double getWeight() {
    return weight;
  }

  public String toString() {
    return String.format("{Assignment: %s Points: %d Weight: %f}", name, points, weight);
  }

  public int compareTo(Assignment assign) {
    return assign.getName().compareTo(this.getName());
  }
}
