/*
 * ��ͼ����PaintPanel
 * ʵ�ֶԻ�����ɫ�����ʴ�ϸ�Ŀ���
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

class PaintPanel extends JPanel implements MouseListener, MouseMotionListener {// �ں����ּ�����
	static double eraserStroke = 5.0d;//������Ƥֱ�� 	
	static float penStroke = 5.0f;// ���û���ֱ�� 	
	static Color color = Color.black;// ������ɫĬ��Ϊ��
	Point2D lineStart = new Point2D.Double();// ���
	//�洢��ͼ���ݣ�CopyOnWriteArrayList���̰߳�ȫ��
	CopyOnWriteArrayList<PaintMessage> paint = new CopyOnWriteArrayList<PaintMessage>();
	ObjectOutputStream remoteOut;
	boolean draw;

	public PaintPanel() {// Ϊ����ע�������
		addMouseListener(this);// �����ʱ��
		addMouseMotionListener(this);
		this.setBackground(Color.white);//���û��屳��ɫ 		
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

	// ��קʱ
	@Override
	public void mouseDragged(MouseEvent e) {
		if (draw == true) {
			PaintMessage line;

			try {
				if (e.isMetaDown()) {// �Ҽ������,��Ƥ
					line = new PaintMessage(2, (double) lineStart.getX(),
							(double) lineStart.getY(), (double) e.getX(),
							(double) e.getY(), Color.white, eraserStroke);
				} else {// ����������drag������
					line = new PaintMessage(2, (double) lineStart.getX(),
							(double) lineStart.getY(), (double) e.getX(),
							(double) e.getY(), color, (double) penStroke);
				}
				lineStart.setLocation(e.getX(), e.getY());// ������ʼ�����¶�λ
				remoteOut.writeInt(1);//���ͻ�����Ϣ
				remoteOut.flush();
				remoteOut.writeUnshared(line);
			} catch (NotSerializableException ex) {
				System.out.println(ex.getMessage() + ": Not Serializable.");
			} catch (InvalidClassException ex) {
				System.out.println(ex.getMessage() + ": Invalid class.");
			} catch (IOException ex) {
				System.out.println(ex.getMessage() + ": Write error.");
			}
			repaint();//�ػ�
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

	// ���ʱ��¼�������
	@Override
	public void mousePressed(MouseEvent e) {
		if (draw == true) {
			PaintMessage point;

			try {
				if (e.isMetaDown()) {// �Ҽ����������Ƥ
					point = new PaintMessage(1, (double) e.getX(),
							(double) e.getY(), 0.0, 0.0, Color.white,
							eraserStroke);
				} else {// ��������������
					point = new PaintMessage(1, (double) e.getX(),
							(double) e.getY(), 0.0, 0.0, color,
							(double) penStroke);
				}
				lineStart.setLocation(e.getX(), e.getY());// ������ʼ�����¶�λ
				remoteOut.writeInt(1);//���ͻ����Ϣ
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

		while (it.hasNext()) {//�����ͼ��������û����
			paintmsg = it.next();//���ζ�ȡ��ͼ����
			if (paintmsg.getType() == 1) {//����ǻ����Ϣ
				g2d.setColor(paintmsg.getColor());//������ɫ
				ellipse = new Ellipse2D.Double(paintmsg.getX1()
						- (paintmsg.getStroke() / 2), paintmsg.getY1()
						- (paintmsg.getStroke() / 2), paintmsg.getStroke(),
						paintmsg.getStroke());//����ϸ���
				g2d.fill(ellipse);
			} else {//����ǻ�����Ϣ
				g2d.setColor(paintmsg.getColor());//������ɫ
				g2d.setStroke(new BasicStroke((float) paintmsg.getStroke(),
						BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//���ô�ϸ
				line = new Line2D.Double(paintmsg.getX1(), paintmsg.getY1(),
						paintmsg.getX2(), paintmsg.getY2());//����
				g2d.draw(line);
			}
		}
	}

	// �������������Եģ��ǵ�ɾ��
	/*
	 * public static void main(String args[]){ JFrame frame = new JFrame();
	 * PaintPanel panel = new PaintPanel(); frame.getContentPane().add(panel);
	 * frame.setTitle("ScribbleDemo"); frame.setSize(300, 300);
	 * frame.setVisible(true); }
	 */
}
