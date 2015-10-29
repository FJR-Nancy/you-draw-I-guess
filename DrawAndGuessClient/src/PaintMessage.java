import java.awt.Color;
import java.awt.Stroke;
import java.io.Serializable;

public class PaintMessage implements Serializable {
	private static final long serialVersionUID=1L;
	
	private int type;
	private double x1;
	private double y1;
	private double x2;
	private double y2;
	private Color color;	
	private double stroke;
	
	public PaintMessage(int type, double x1, double y1, double x2, double y2,
			Color color, double stroke) {
		super();
		this.type = type;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.color = color;
		this.stroke = stroke;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getX1() {
		return x1;
	}

	public void setX1(double x1) {
		this.x1 = x1;
	}

	public double getY1() {
		return y1;
	}

	public void setY1(double y1) {
		this.y1 = y1;
	}

	public double getX2() {
		return x2;
	}

	public void setX2(double x2) {
		this.x2 = x2;
	}

	public double getY2() {
		return y2;
	}

	public void setY2(double y2) {
		this.y2 = y2;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public double getStroke() {
		return stroke;
	}

	public void setStroke(double stroke) {
		this.stroke = stroke;
	}
}
