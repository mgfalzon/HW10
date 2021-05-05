
public class GradeKey {
	private Double weight;
	private int maxScore;
	
	public GradeKey(Double weight, int maxScore) {
		this.weight = weight;
		this.maxScore = maxScore;
	}
	
	public int getScore() {
		return this.maxScore;
	}
	
	public Double getWeight() {
		return this.weight;
	}
	
	public String getGradeVal() {
		return this.maxScore + ",Weight:" + this.weight;
	}
}
