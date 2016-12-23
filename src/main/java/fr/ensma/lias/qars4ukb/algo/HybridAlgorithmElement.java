package fr.ensma.lias.qars4ukb.algo;


/**
 * @author Ibrahim DELLAL 
 */
public class HybridAlgorithmElement {
    private Double alpha;
    private Double left;
    private Double right;

    public HybridAlgorithmElement(Double alpha, Double left, Double right) {
	this.alpha = alpha;
	this.left = left;
	this.right = right;
    }

    public Double getAlpha() {
	return alpha;
    }

    public void setAlpha(Double alpha) {
	this.alpha = alpha;
    }

    public Double getLeft() {
	return left;
    }

    public void setLeft(Double left) {
	this.left = left;
    }

    public Double getRight() {
	return right;
    }

    public void setRight(Double right) {
	this.right = right;
    }

    @Override
    public String toString() {
	return "" + alpha + " -> left = " + left + " . right = " + right;
    }
}
