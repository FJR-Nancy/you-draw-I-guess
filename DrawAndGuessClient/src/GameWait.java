import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.Semaphore;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameWait extends JFrame {
	private ImageIcon background;
	private JPanel imagePanel;
	leftPanel lPanel = new leftPanel();
	rightPanel rPanel = new rightPanel();
	static Semaphore threadState = new Semaphore(0);//信号量，用来控制线程暂停与恢复
	private String me;
	private ObjectOutputStream remoteOut;
	private ObjectInputStream remoteIn;

	GameWait(ObjectInputStream remoteIn,ObjectOutputStream remoteOut,String me) {
		this.remoteIn=remoteIn;
		this.remoteOut=remoteOut;
		this.me=me;
		
		this.getContentPane().setLayout(
				new FlowLayout(FlowLayout.CENTER, 0, 50));
		this.add(lPanel);
		this.add(rPanel);
		
		//设置背景
		background = new ImageIcon("resource/bg2.jpg");// 背景图片		
		JLabel label = new JLabel(background);// 把背景图片显示在一个标签里面
		label.setBounds(0, 0, background.getIconWidth(),
				background.getIconHeight());// 把标签的大小位置设置为图片刚好填充整个面板
		imagePanel = (JPanel) this.getContentPane();// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
		imagePanel.setOpaque(false);
		imagePanel.setLayout(new FlowLayout());// 内容窗格默认的布局管理器为BorderLayout
		this.getLayeredPane().setLayout(null);		
		this.getLayeredPane().add(label, new Integer(Integer.MIN_VALUE));// 把背景图片添加到分层窗格的最底层作为背景
		lPanel.setOpaque(false);
		rPanel.setOpaque(false);

		this.setTitle("准备开始");
		this.setLocation(170,  10);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 使用 System exit方法退出应用程序
		this.setSize(background.getIconWidth(), background.getIconHeight());
		
		rPanel.readyLabel0.setText(stateTest(0));//设置“我”的昵称
		rPanel.nameLabel0.setText(me);//初始化“我”的状态

		new Receive1().start();// 开启监听线程

		this.setVisible(true);
	}

	class Receive1 extends Thread {
		int msg;// 判断是哪种信息
		String[] players = new String[6];// 各位玩家的名字
		int[] readyState = new int[7];// 各位玩家的状态，最后一位表示游戏是否可以开始
		GameBegin gameBegin;

		Receive1() {
			// setDaemon(true); // Thread is daemon
		}

		public void run() {
			try {
				while (true) {
					msg = remoteIn.readInt();// 读取标记为，判断是哪种信息

					switch (msg) {
					case 4:// 玩家名称（在等待开始界面，游戏界面均有）
						players = (String[]) remoteIn.readUnshared();
						lPanel.nameLabel1.setText(players[0]);
						lPanel.nameLabel2.setText(players[1]);
						lPanel.nameLabel3.setText(players[2]);
						lPanel.nameLabel4.setText(players[3]);
						lPanel.nameLabel5.setText(players[4]);
						lPanel.nameLabel6.setText(players[5]);
						break;
					case 9:// 玩家状态
						readyState = (int[]) remoteIn.readUnshared();
						lPanel.readyLabel1.setText(stateTest(readyState[0]));
						lPanel.readyLabel2.setText(stateTest(readyState[1]));
						lPanel.readyLabel3.setText(stateTest(readyState[2]));
						lPanel.readyLabel4.setText(stateTest(readyState[3]));
						lPanel.readyLabel5.setText(stateTest(readyState[4]));
						lPanel.readyLabel6.setText(stateTest(readyState[5]));
						if (readyState[6] == 1) {// 游戏开始,新生成GameBegin面板，暂停当前监听线程
							gameBegin = new GameBegin(remoteIn, remoteOut,
										me);
							gameBegin.getToolkit().getImage("resource/bg3.jpg"); 
							gameBegin.setSize(1000, 700);
							gameBegin.setVisible(true);
							GameWait.this.setVisible(false);
							
							threadState.acquire();
							rPanel.readyLabel0.setText(stateTest(0));
							GameWait.this.setVisible(true);
						}
						break;	
						}
				}
			} catch (IOException e) {
				System.out.println(e.getMessage() + ": GameWait Read error.");
			} catch (ClassNotFoundException e) {
				System.out.println(e.getMessage() + ": Class is not found.");
			} catch (InterruptedException e) {
				System.out.println(e.getMessage() + ": Interrupted error.");
			} 
		}
	}

	String stateTest(int test) {// 判断用户readyState
		String str = new String();

		switch (test) {
		case 0:
			str = "未准备";
			break;
		case 1:
			str = "准备中...";
			break;
		case -1:
			str = "快进坑里来！";
			break;
		}

		return str;
	}

	public class leftPanel extends JPanel {
		private JLabel userLabel1, userLabel2, userLabel3, userLabel4,
				userLabel5, userLabel6;
		JLabel readyLabel1, readyLabel2, readyLabel3, readyLabel4, readyLabel5,
				readyLabel6;
		JLabel nameLabel1, nameLabel2, nameLabel3, nameLabel4, nameLabel5,
				nameLabel6;

		leftPanel() {
			userLabel1 = new JLabel();
			userLabel1.setIcon(new ImageIcon("resource/1.jpg"));
			userLabel1.setAlignmentX(CENTER_ALIGNMENT);
			userLabel2 = new JLabel();
			userLabel2.setIcon(new ImageIcon("resource/1.jpg"));
			userLabel2.setAlignmentX(CENTER_ALIGNMENT);
			userLabel3 = new JLabel();
			userLabel3.setIcon(new ImageIcon("resource/1.jpg"));
			userLabel3.setAlignmentX(CENTER_ALIGNMENT);
			userLabel4 = new JLabel();
			userLabel4.setIcon(new ImageIcon("resource/1.jpg"));
			userLabel4.setAlignmentX(CENTER_ALIGNMENT);
			userLabel5 = new JLabel();
			userLabel5.setIcon(new ImageIcon("resource/1.jpg"));
			userLabel5.setAlignmentX(CENTER_ALIGNMENT);
			userLabel6 = new JLabel();
			userLabel6.setIcon(new ImageIcon("resource/1.jpg"));
			userLabel6.setAlignmentX(CENTER_ALIGNMENT);
			readyLabel1 = new JLabel("准备中...");
			readyLabel1.setFont(new Font("微软雅黑", Font.ITALIC, 15));
			readyLabel1.setAlignmentX(CENTER_ALIGNMENT);
			readyLabel2 = new JLabel("准备中...");
			readyLabel2.setFont(new Font("微软雅黑", Font.ITALIC, 15));
			readyLabel2.setAlignmentX(CENTER_ALIGNMENT);
			readyLabel3 = new JLabel("准备中...");
			readyLabel3.setFont(new Font("微软雅黑", Font.ITALIC, 15));
			readyLabel3.setAlignmentX(CENTER_ALIGNMENT);
			readyLabel4 = new JLabel("准备中...");
			readyLabel4.setFont(new Font("微软雅黑", Font.ITALIC, 15));
			readyLabel4.setAlignmentX(CENTER_ALIGNMENT);
			readyLabel5 = new JLabel("准备中...");
			readyLabel5.setFont(new Font("微软雅黑", Font.ITALIC, 15));
			readyLabel5.setAlignmentX(CENTER_ALIGNMENT);
			readyLabel6 = new JLabel("准备中...");
			readyLabel6.setFont(new Font("微软雅黑", Font.ITALIC, 15));
			readyLabel6.setAlignmentX(CENTER_ALIGNMENT);
			nameLabel1 = new JLabel("姓名");
			nameLabel1.setFont(new Font("微软雅黑", Font.BOLD, 15));
			nameLabel1.setAlignmentX(CENTER_ALIGNMENT);
			nameLabel2 = new JLabel("姓名");
			nameLabel2.setFont(new Font("微软雅黑", Font.BOLD, 15));
			nameLabel2.setAlignmentX(CENTER_ALIGNMENT);
			nameLabel3 = new JLabel("姓名");
			nameLabel3.setFont(new Font("微软雅黑", Font.BOLD, 15));
			nameLabel3.setAlignmentX(CENTER_ALIGNMENT);
			nameLabel4 = new JLabel("姓名");
			nameLabel4.setFont(new Font("微软雅黑", Font.BOLD, 15));
			nameLabel4.setAlignmentX(CENTER_ALIGNMENT);
			nameLabel5 = new JLabel("姓名");
			nameLabel5.setFont(new Font("微软雅黑", Font.BOLD, 15));
			nameLabel5.setAlignmentX(CENTER_ALIGNMENT);
			nameLabel6 = new JLabel("姓名");
			nameLabel6.setFont(new Font("微软雅黑", Font.BOLD, 15));
			nameLabel6.setAlignmentX(CENTER_ALIGNMENT);

			Box box1 = new Box(BoxLayout.Y_AXIS);
			box1.add(Box.createVerticalStrut(150));// 在顶部加柱
			box1.add(userLabel1);
			box1.add(Box.createVerticalStrut(10));
			box1.add(nameLabel1);
			box1.add(Box.createVerticalStrut(10));
			box1.add(readyLabel1);

			Box box2 = new Box(BoxLayout.Y_AXIS);
			box2.add(Box.createVerticalStrut(150));// 在顶部加柱
			box2.add(userLabel2);
			box2.add(Box.createVerticalStrut(10));
			box2.add(nameLabel2);
			box2.add(Box.createVerticalStrut(10));
			box2.add(readyLabel2);

			Box box3 = new Box(BoxLayout.Y_AXIS);
			box3.add(Box.createVerticalStrut(150));// 在顶部加柱
			box3.add(userLabel3);
			box3.add(Box.createVerticalStrut(10));
			box3.add(nameLabel3);
			box3.add(Box.createVerticalStrut(10));
			box3.add(readyLabel3);

			Box box4 = new Box(BoxLayout.Y_AXIS);
			box4.add(userLabel4);
			box4.add(Box.createVerticalStrut(10));
			box4.add(nameLabel4);
			box4.add(Box.createVerticalStrut(10));
			box4.add(readyLabel4);

			Box box5 = new Box(BoxLayout.Y_AXIS);
			box5.add(userLabel5);
			box5.add(Box.createVerticalStrut(10));
			box5.add(nameLabel5);
			box5.add(Box.createVerticalStrut(10));
			box5.add(readyLabel5);

			Box box6 = new Box(BoxLayout.Y_AXIS);
			box6.add(userLabel6);
			box6.add(Box.createVerticalStrut(10));
			box6.add(nameLabel6);
			box6.add(Box.createVerticalStrut(10));
			box6.add(readyLabel6);

			setLayout(new GridLayout(2, 3, 50, 50));// 后面两个是组件间距
			add(box1);
			add(box2);
			add(box3);
			add(box4);
			add(box5);
			add(box6);
		}

	}

	public class rightPanel extends JPanel implements ActionListener {
		private JButton exitButton, introButton, readyButton;
		private JLabel userLabel0, readyLabel0, nameLabel0;// 当前用户的名字和就绪状态

		rightPanel() {
			userLabel0 = new JLabel();
			userLabel0.setIcon(new ImageIcon("resource/1.jpg"));
			readyLabel0 = new JLabel("准备中...");
			readyLabel0.setFont(new Font("微软雅黑", Font.ITALIC, 15));
			nameLabel0 = new JLabel("姓名");
			nameLabel0.setFont(new Font("微软雅黑", Font.BOLD, 15));
			exitButton = new JButton("退出游戏");
			exitButton.setFont(new Font("微软雅黑", Font.BOLD, 15));
			introButton = new JButton("游戏介绍");
			introButton.setFont(new Font("微软雅黑", Font.BOLD, 15));
			readyButton = new JButton("准       备");
			readyButton.setFont(new Font("微软雅黑", Font.BOLD, 15));

			// 设置组件位置居中
			userLabel0.setAlignmentX(CENTER_ALIGNMENT);
			readyLabel0.setAlignmentX(CENTER_ALIGNMENT);
			nameLabel0.setAlignmentX(CENTER_ALIGNMENT);
			readyButton.setAlignmentX(CENTER_ALIGNMENT);
			introButton.setAlignmentX(CENTER_ALIGNMENT);
			exitButton.setAlignmentX(CENTER_ALIGNMENT);
			// 设置当前玩家的盒子布局
			Box box0 = new Box(BoxLayout.Y_AXIS);
			box0.add(Box.createHorizontalStrut(200));// T T给这个box加竖向立柱...
			box0.add(userLabel0);
			box0.add(Box.createVerticalStrut(10));// 添加垂直支撑柱
			box0.add(nameLabel0);
			box0.add(Box.createVerticalStrut(10));
			box0.add(readyLabel0);
			box0.add(Box.createVerticalStrut(40));
			box0.add(readyButton);
			box0.add(Box.createVerticalStrut(20));
			box0.add(exitButton);
			box0.add(Box.createVerticalStrut(20));
			box0.add(introButton);
			// 添加到当前面板
			this.add(box0);

			// 为按钮增添动作事件
			readyButton.addActionListener(this);
			introButton.addActionListener(this);
			exitButton.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == readyButton) {// 若点击"准备"按钮，所触发的事件
				rPanel.readyLabel0.setText(stateTest(1));
				try {
					remoteOut.writeInt(7);
					remoteOut.flush();
				} catch (IOException ex) {
					System.out.println(ex.getMessage() + ": Write error.");
				}
			} else if (e.getSource() == exitButton) {// 若点击"退出游戏"按钮，所触发的事件
				try {
					remoteOut.writeInt(8);
					remoteOut.flush();
				} catch (IOException ex) {
					System.out.println(ex.getMessage() + ": Write error.");
				}
				System.exit(0);
			} else if (e.getSource() == introButton) {
				new GameIntro();
			}

		}
	}
	
	/*
	public static void main(String[] args) {
		new GameWait();
	}
	*/
}
