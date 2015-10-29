/*
 * 绘图画板PaintPanel
 * 实现对画笔颜色、画笔粗细的控制
 * */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JPanel;

class PaintPanel extends JPanel implements MouseListener, MouseMotionListener {// 内含两种监听器
	static double eraserStroke = 5.0d;//设置橡皮直径 	
	static float penStroke = 5.0f;// 设置画笔直径 	
	static Color color = Color.black;// 设置颜色默认为黑
	Point2D lineStart = new Point2D.Double();// 起点
	//存储绘图数据，CopyOnWriteArrayList是线程安全的
	CopyOnWriteArrayList<PaintMessage> paint = new CopyOnWriteArrayList<PaintMessage>();
	ObjectOutputStream remoteOut;
	boolean draw;

	public PaintPanel() {// 为画板注册监听器
		addMouseListener(this);// 点击的时候
		addMouseMotionListener(this);
		this.setBackground(Color.white);//设置画板背景色 		
	}

	void setRemoteOut(ObjectOutputStream remoteOut) {
		this.remoteOut = remoteOut;
	}

	void setDraw(boolean draw) {
		this.draw = draw;
	}

	public void clear() {
		paint.clear();
		repaint();
	}

	// 拖拽时
	@Override
	public void mouseDragged(MouseEvent e) {
		if (draw == true) {
			PaintMessage line;

			try {
				if (e.isMetaDown()) {// 右键被点击,橡皮
					line = new PaintMessage(2, (double) lineStart.getX(),
							(double) lineStart.getY(), (double) e.getX(),
							(double) e.getY(), Color.white, eraserStroke);
				} else {// 左键被点击并drag，画笔
					line = new PaintMessage(2, (double) lineStart.getX(),
							(double) lineStart.getY(), (double) e.getX(),
							(double) e.getY(), color, (double) penStroke);
				}
				lineStart.setLocation(e.getX(), e.getY());// 画线起始点重新定位
				remoteOut.writeInt(1);//发送画线信息
				remoteOut.flush();
				remoteOut.writeUnshared(line);
			} catch (NotSerializableException ex) {
				System.out.println(ex.getMessage() + ": Not Serializable.");
			} catch (InvalidClassException ex) {
				System.out.println(ex.getMessage() + ": Invalid class.");
			} catch (IOException ex) {
				System.out.println(ex.getMessage() + ": Write error.");
			}
			repaint();//重绘
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	// 点击时记录起点坐标
	@Override
	public void mousePressed(MouseEvent e) {
		if (draw == true) {
			PaintMessage point;

			try {
				if (e.isMetaDown()) {// 右键被点击，橡皮
					point = new PaintMessage(1, (double) e.getX(),
							(double) e.getY(), 0.0, 0.0, Color.white,
							eraserStroke);
				} else {// 左键被点击，画笔
					point = new PaintMessage(1, (double) e.getX(),
							(double) e.getY(), 0.0, 0.0, color,
							(double) penStroke);
				}
				lineStart.setLocation(e.getX(), e.getY());// 画线起始点重新定位
				remoteOut.writeInt(1);//发送绘点信息
				remoteOut.flush();
				remoteOut.writeUnshared(point);
			} catch (NotSerializableException ex) {
				System.out.println(ex.getMessage() + ": Not Serializable.");
			} catch (InvalidClassException ex) {
				System.out.println(ex.getMessage() + ": Invalid class.");
			} catch (IOException ex) {
				System.out.println(ex.getMessage() + ": Write error.");
			}
			repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		Iterator<PaintMessage> it = paint.iterator();
		Line2D.Double line;
		Ellipse2D ellipse;
		PaintMessage paintmsg;

		while (it.hasNext()) {//如果绘图数据数组没结束
			paintmsg = it.next();//依次读取绘图数据
			if (paintmsg.getType() == 1) {//如果是绘点信息
				g2d.setColor(paintmsg.getColor());//设置颜色
				ellipse = new Ellipse2D.Double(paintmsg.getX1()
						- (paintmsg.getStroke() / 2), paintmsg.getY1()
						- (paintmsg.getStroke() / 2), paintmsg.getStroke(),
						paintmsg.getStroke());//按粗细绘点
				g2d.fill(ellipse);
			} else {//如果是画线信息
				g2d.setColor(paintmsg.getColor());//设置颜色
				g2d.setStroke(new BasicStroke((float) paintmsg.getStroke(),
						BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//设置粗细
				line = new Line2D.Double(paintmsg.getX1(), paintmsg.getY1(),
						paintmsg.getX2(), paintmsg.getY2());//画线
				g2d.draw(line);
			}
		}
	}

	// 下面是用来测试的，记得删了
	/*
	 * public static void main(String args[]){ JFrame frame = new JFrame();
	 * PaintPanel panel = new PaintPanel(); frame.getContentPane().add(panel);
	 * frame.setTitle("ScribbleDemo"); frame.setSize(300, 300);
	 * frame.setVisible(true); }
	 */
}
