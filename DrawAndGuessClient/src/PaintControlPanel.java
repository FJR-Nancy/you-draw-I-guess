import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class PaintControlPanel extends JPanel {
	// ������ɫ���
	private JLabel colorLabel;
	private JLabel currentColor;
	JButton color;//��ʾ��ǰ��ɫ
	private ColorPanel colorPanel;//ɫ��
	// ������ť
	private JButton clearButton;
	// ���ʺ���Ƥ��label
	private JLabel penLabel;
	private JLabel penVal;
	private JLabel eraserLabel;
	private JLabel eraserVal;
	// ���ʺ���Ƥ��ϸ������
	JSlider penSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 5);
	JSlider eraserSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 5);

	boolean draw;
	ObjectOutputStream remoteOut;

	PaintControlPanel() {
		colorLabel = new JLabel();
		colorLabel.setIcon(new ImageIcon("resource/palette.jpg"));
		currentColor = new JLabel("      ��ǰ��ɫ:    ");
		currentColor.setFont(new Font("΢���ź�", Font.PLAIN, 15));
		colorPanel = new ColorPanel();
		color = new JButton();
		color.setBackground(Color.black);

		clearButton = new JButton("����");
		clearButton.setFont(new Font("΢���ź�", Font.PLAIN, 12));
		clearButton.setAlignmentX(CENTER_ALIGNMENT);// ��ť������ʾ

		penLabel = new JLabel();
		penLabel.setIcon(new ImageIcon("resource/pen.jpg"));
		penVal = new JLabel("5");
		penVal.setFont(new Font("����", Font.BOLD, 15));

		eraserLabel = new JLabel();
		eraserLabel.setIcon(new ImageIcon("resource/eraser.png"));
		eraserVal = new JLabel("5");
		eraserVal.setFont(new Font("����", Font.BOLD, 15));

		// �����ڲ�����
		Box colorLabelBox = new Box(BoxLayout.X_AXIS);
		Box colorBox = new Box(BoxLayout.Y_AXIS);
		Box penStrokeBox = new Box(BoxLayout.X_AXIS);
		Box eraserStrokeBox = new Box(BoxLayout.X_AXIS);

		colorLabelBox.add(colorLabel);
		colorLabelBox.add(currentColor);
		colorLabelBox.add(color);
		colorBox.add(colorPanel);
		colorBox.add(Box.createVerticalStrut(10));
		
		penStrokeBox.add(penLabel);
		penStrokeBox.add(penSlider);
		penStrokeBox.add(penVal);
		penSlider.setPaintTicks(true);
		penSlider.setMajorTickSpacing(20);
		penSlider.setPaintLabels(true);
		penSlider.addChangeListener(new ChangeListener() {//���ʴ�ϸ���������ü������ݱ仯
			public void stateChanged(ChangeEvent penChange) {
				if (draw == true) {
					JSlider pen = (JSlider) penChange.getSource();
					int val = pen.getValue();
					PaintPanel.penStroke = (float) val;
					penVal.setText("" + val);
				}
			}
		});

		penSlider.addMouseListener(new MouseListener() {//���ʴ�ϸ���������ü��������
			public void mouseClicked(MouseEvent e) {
				if (draw == true) {
					int x = e.getX();
					penSlider.setValue(x);
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		eraserStrokeBox.add(eraserLabel);
		eraserStrokeBox.add(eraserSlider);
		eraserStrokeBox.add(eraserVal);
		eraserSlider.setPaintTicks(true);
		eraserSlider.setMajorTickSpacing(20);
		eraserSlider.setPaintLabels(true);
		eraserSlider.addChangeListener(new ChangeListener() {//��Ƥ��ϸ���������ü������ݱ仯
			public void stateChanged(ChangeEvent eraserChange) {
				if (draw == true) {
					JSlider eraser = (JSlider) eraserChange.getSource();
					int val = eraser.getValue();
					PaintPanel.eraserStroke = (double) val;
					eraserVal.setText("" + val);
				}
			}
		});

		eraserSlider.addMouseListener(new MouseListener() {//��Ƥ��ϸ���������ü��������
			public void mouseClicked(MouseEvent e) {
				if (draw == true) {
					int x = e.getX();
					eraserSlider.setValue(x);
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		clearButton.addActionListener(new ActionListener() {//�����¼�
			public void actionPerformed(ActionEvent event) {
				if (draw == true) {
					try {
						remoteOut.writeInt(5);
						remoteOut.flush();
					} catch (IOException e) {
						System.out.println(e.getMessage() + ": Write error.");
					}
				}
			}
		});

		// ����������岼��
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));// ע����Ͳ��ֵ�д��
		add(colorLabelBox);
		add(colorBox);
		add(Box.createVerticalStrut(10));
		add(penStrokeBox);
		add(Box.createVerticalStrut(10));
		add(eraserStrokeBox);
		add(Box.createVerticalStrut(10));
		add(clearButton);
	}

	void setRemoteOut(ObjectOutputStream remoteOut) {
		this.remoteOut = remoteOut;
	}

	void setDraw(boolean draw) {
		this.draw = draw;
	}

	// ��ɫ��
	public class ColorPanel extends JPanel {

		JButton redButton = new JButton();
		JButton blueButton = new JButton();
		JButton yellowButton = new JButton();
		JButton greenButton = new JButton();
		JButton cyanButton = new JButton();
		JButton orangeButton = new JButton();
		JButton lightgrayButton = new JButton();
		JButton pinkButton = new JButton();
		JButton magentaButton = new JButton();
		JButton darkgrayButton = new JButton();
		JButton whiteButton = new JButton();
		JButton blackButton = new JButton();

		ColorPanel() {
			redButton.setBackground(Color.red);
			blueButton.setBackground(Color.blue);
			yellowButton.setBackground(Color.yellow);
			greenButton.setBackground(Color.green);
			cyanButton.setBackground(Color.cyan);
			orangeButton.setBackground(Color.orange);
			pinkButton.setBackground(Color.PINK);
			lightgrayButton.setBackground(Color.LIGHT_GRAY);
			magentaButton.setBackground(Color.magenta);
			darkgrayButton.setBackground(Color.DARK_GRAY);
			whiteButton.setBackground(Color.WHITE);
			blackButton.setBackground(Color.black);
			
			//��ɫ��ע������¼�
			redButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (draw == true) {
						PaintPanel.color = Color.red;
						color.setBackground(Color.red);
					}
				}
			});

			blueButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (draw == true) {
						PaintPanel.color = Color.blue;
						color.setBackground(Color.blue);
					}
				}
			});

			yellowButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (draw == true) {
						PaintPanel.color = Color.yellow;
						color.setBackground(Color.yellow);
					}
				}
			});

			greenButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (draw == true) {
						PaintPanel.color = Color.green;
						color.setBackground(Color.green);
					}
				}
			});

			cyanButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (draw == true) {
						PaintPanel.color = Color.cyan;
						color.setBackground(Color.cyan);
					}
				}
			});

			orangeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (draw == true) {
						PaintPanel.color = Color.orange;
						color.setBackground(Color.orange);
					}
				}
			});

			pinkButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (draw == true) {
						PaintPanel.color = Color.pink;
						color.setBackground(Color.pink);
					}
				}
			});

			lightgrayButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (draw == true) {
						PaintPanel.color = Color.LIGHT_GRAY;
						color.setBackground(Color.LIGHT_GRAY);
					}
				}
			});

			magentaButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (draw == true) {
						PaintPanel.color = Color.magenta;
						color.setBackground(Color.magenta);
					}
				}
			});

			darkgrayButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (draw == true) {
						PaintPanel.color = Color.DARK_GRAY;
						color.setBackground(Color.DARK_GRAY);
					}
				}
			});

			whiteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (draw == true) {
						PaintPanel.color = Color.white;
						color.setBackground(Color.WHITE);
					}
				}
			});

			blackButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (draw == true) {
						PaintPanel.color = Color.black;
						color.setBackground(Color.black);
					}
				}
			});

			setLayout(new GridLayout(4, 3));
			this.add(redButton);
			this.add(blueButton);
			this.add(yellowButton);
			this.add(greenButton);
			this.add(cyanButton);
			this.add(orangeButton);
			this.add(lightgrayButton);
			this.add(pinkButton);
			this.add(magentaButton);
			this.add(darkgrayButton);
			this.add(whiteButton);
			this.add(blackButton);

		}

	}

	/*
	 * ������������ public static void main(String[] args){ JFrame frame = new
	 * JFrame(); PaintControlPanel panel = new PaintControlPanel();
	 * frame.add(panel); frame.setSize(400, 400); frame.setVisible(true); }
	 */
}
