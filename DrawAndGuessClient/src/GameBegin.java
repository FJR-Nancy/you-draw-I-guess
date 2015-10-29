import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class GameBegin extends JFrame {
	private static final long serialVersionUID = 1L;

	private JLabel hintLabel;
	private PaintPanel paintPanel;
	private PaintControlPanel paintControlPanel;
	private UserPanel userPanel;
	private ChatPanel chatPanel;
	
	URL audioFile = getClass().getResource("˦�и�.wav");// �����ļ�
	AudioClip sound;
	
	String me;// ���ҡ�������
	ObjectOutputStream remoteOut;
	ObjectInputStream remoteIn;

	GameBegin(ObjectInputStream remoteIn, ObjectOutputStream remoteOut,
			String me) {
		this.remoteIn = remoteIn;
		this.remoteOut = remoteOut;
		this.me = me;

		sound = java.applet.Applet.newAudioClip(audioFile);
		sound.loop();//ѭ����������
		
		hintLabel = new JLabel(
				"�ڼ���                   ��������Ŀ��Ŀ��Ŀ                    ˭˭˭���� ",
				JLabel.CENTER);// ���þ�����ʾ
		hintLabel.setFont(new Font("΢���ź�", Font.ITALIC, 20));
		paintPanel = new PaintPanel();
		paintControlPanel = new PaintControlPanel();
		userPanel = new UserPanel();
		chatPanel = new ChatPanel();

		setLayout(new BorderLayout());
		hintLabel.setPreferredSize(new Dimension(300, 50));//����flowlayout�������С
		this.add(hintLabel, BorderLayout.NORTH);
		this.add(paintControlPanel, BorderLayout.WEST);
		paintControlPanel.setPreferredSize(new Dimension(180, 10));
		this.add(paintPanel, BorderLayout.CENTER);
		this.add(userPanel, BorderLayout.SOUTH);
		this.add(chatPanel, BorderLayout.EAST);
		chatPanel.setPreferredSize(new Dimension(180, 10));
		
		this.setTitle("������Ϸ");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000, 700);
		
		//�������������ͼ��塢������塢��ͼ�������
		paintPanel.setRemoteOut(remoteOut);
		chatPanel.setRemoteOut(remoteOut);
		paintControlPanel.setRemoteOut(remoteOut);

		new Receive2().start();// ���������߳�

		this.setVisible(true);
	}

	class Receive2 extends Thread {
		int msg;// �ж���������Ϣ
		PaintMessage paint;// ��ͼ����Ϣ
		InitMessage init;// ��Ϸ��ʼ�ĳ�ʼ����Ϣ��������Ŀ����ʾ����һ���ֵ�˭�����ڼ��֣�
		String[] players = new String[6];// ��λ��ҵ�����
		String talk;// ������Ϣ�������������������Ļش���ȷ����Ϣ��
		String countDown;// ����ʱ
		int[] scores = new int[6];// ��λ��ҵĻ���
		int[] readyState = new int[7];// ��λ��ҵ�״̬�����һλ��ʾ��Ϸ�Ƿ���Կ�ʼ
		String rank;//��Ϸ����ʱ����ǰ��������Һͻ���
		boolean draw;// �Ƿ��ֵ���ͼ
		boolean state = true;// �Ƿ����������

		Receive2() {
			init();
			// setDaemon(true); // Thread is daemon
		}

		void init() {// һ����Ϸ��ʼ�ĳ�ʼ��			
			//chatPanel.chatText.setText("");// ��������
			paintControlPanel.color.setBackground(Color.black);// ���û�����ɫĬ��Ϊ��
			paintPanel.color = Color.black;
			paintControlPanel.penSlider.setValue(5);// ����PaintControlPanelΪĬ��ѡ��
			paintControlPanel.eraserSlider.setValue(5);
			paintPanel.clear();// ��ջ���
		}

		public void run() {
			try {
				while (state) {
					msg = remoteIn.readInt();// ��ȡ���Ϊ���ж���������Ϣ

					switch (msg) {
					case 1:// ��ͼ��Ϣ
						paint = (PaintMessage) remoteIn.readUnshared();// ��ȡ��ͼ��Ϣ
						paintPanel.paint.add(paint);// �ӵ��洢��ͼ���ݵ�������
						paintPanel.repaint();// ˢ�»�ͼ���
						break;
					case 2:// �������ݣ��Լ��������������Ļش���ȷ����Ϣ
						talk = (String) remoteIn.readUnshared();// ��ȡ������Ϣ
						chatPanel.chatText.append(talk); // ������Ϣ���������Ϣδ��
						break;
					case 3:// ����ʱ
						countDown = (String) remoteIn.readUnshared();
						chatPanel.timeLabel.setText("����ʱ��" + countDown);// ��ʾ������ʱlabel
						break;
					case 4:// �������
						players = (String[]) remoteIn.readUnshared();
						userPanel.nameLabel1.setText(players[0]);
						userPanel.nameLabel2.setText(players[1]);
						userPanel.nameLabel3.setText(players[2]);
						userPanel.nameLabel4.setText(players[3]);
						userPanel.nameLabel5.setText(players[4]);
						userPanel.nameLabel6.setText(players[5]);
						break;
					case 5:// ����
						paintPanel.clear();
						break;
					case 6:// ��Ϸ��ʼ�ĳ�ʼ��Ϣ
						init = (InitMessage) remoteIn.readUnshared();
						if (init.getDrawer().equals(me)) {// ����ֵ����ҡ���
							hintLabel.setText("��" + init.turn
									+ "��                    " + "��Ŀ:"
									+ init.key + "            �������㻭");
							draw = true;
						} else {// ������ǡ��ҡ���
							hintLabel.setText("��" + init.turn
									+ "��                   " + "��ʾ:"
									+ init.hint + "          ������" + init.drawer
									+ "��");
							draw = false;
						}
						paintPanel.setDraw(draw);
						paintControlPanel.setDraw(draw);
						break;
					case 10:// Game over.
						state = false;// �رյ�ǰ�����߳�
						rank=(String)remoteIn.readUnshared();
						JOptionPane.showMessageDialog(GameBegin.this,rank);
						init();// ��ʼ����Ϸ����
						sound.stop();//����ֹͣ
						GameBegin.this.setVisible(false);// ��������Ϸ�Ľ���
						GameWait.threadState.release();// ���¿�ʼ�ȴ���ʼ��������߳�
						break;
					case 11:// ��һ���
						scores = (int[]) remoteIn.readUnshared();
						userPanel.scoreLabel1.setText("" + scores[0]);
						userPanel.scoreLabel2.setText("" + scores[1]);
						userPanel.scoreLabel3.setText("" + scores[2]);
						userPanel.scoreLabel4.setText("" + scores[3]);
						userPanel.scoreLabel5.setText("" + scores[4]);
						userPanel.scoreLabel6.setText("" + scores[5]);
						break;
					case 13:// ���ֻ�ͼ����
						JOptionPane.showMessageDialog(GameBegin.this,
								"���ֽ���������һλ���ִ��!");
						init();// ��ʼ����Ϸ����
						break;
					}
				}
			} catch (IOException e) {
				System.out.println(e.getMessage() + ": GameBegin Read error.");
			} catch (ClassNotFoundException e) {
				System.out.println(e.getMessage() + ": Class is not found.");
			} 
		}
	}
}
