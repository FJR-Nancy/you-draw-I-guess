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
	
	URL audioFile = getClass().getResource("甩葱歌.wav");// 声音文件
	AudioClip sound;
	
	String me;// “我”的名字
	ObjectOutputStream remoteOut;
	ObjectInputStream remoteIn;

	GameBegin(ObjectInputStream remoteIn, ObjectOutputStream remoteOut,
			String me) {
		this.remoteIn = remoteIn;
		this.remoteOut = remoteOut;
		this.me = me;

		sound = java.applet.Applet.newAudioClip(audioFile);
		sound.loop();//循环播放音乐
		
		hintLabel = new JLabel(
				"第几轮                   这里是题目题目题目                    谁谁谁画画 ",
				JLabel.CENTER);// 设置居中显示
		hintLabel.setFont(new Font("微软雅黑", Font.ITALIC, 20));
		paintPanel = new PaintPanel();
		paintControlPanel = new PaintControlPanel();
		userPanel = new UserPanel();
		chatPanel = new ChatPanel();

		setLayout(new BorderLayout());
		hintLabel.setPreferredSize(new Dimension(300, 50));//重设flowlayout的组件大小
		this.add(hintLabel, BorderLayout.NORTH);
		this.add(paintControlPanel, BorderLayout.WEST);
		paintControlPanel.setPreferredSize(new Dimension(180, 10));
		this.add(paintPanel, BorderLayout.CENTER);
		this.add(userPanel, BorderLayout.SOUTH);
		this.add(chatPanel, BorderLayout.EAST);
		chatPanel.setPreferredSize(new Dimension(180, 10));
		
		this.setTitle("正在游戏");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000, 700);
		
		//把输出流传到绘图面板、聊天面板、绘图控制面板
		paintPanel.setRemoteOut(remoteOut);
		chatPanel.setRemoteOut(remoteOut);
		paintControlPanel.setRemoteOut(remoteOut);

		new Receive2().start();// 开启监听线程

		this.setVisible(true);
	}

	class Receive2 extends Thread {
		int msg;// 判断是哪种信息
		PaintMessage paint;// 画图的信息
		InitMessage init;// 游戏开始的初始化信息（包括题目、提示、这一局轮到谁画、第几轮）
		String[] players = new String[6];// 各位玩家的名字
		String talk;// 聊天信息（包括服务器传过来的回答正确的信息）
		String countDown;// 倒计时
		int[] scores = new int[6];// 各位玩家的积分
		int[] readyState = new int[7];// 各位玩家的状态，最后一位表示游戏是否可以开始
		String rank;//游戏结束时排名前三名的玩家和积分
		boolean draw;// 是否轮到画图
		boolean state = true;// 是否监听服务器

		Receive2() {
			init();
			// setDaemon(true); // Thread is daemon
		}

		void init() {// 一局游戏开始的初始化			
			//chatPanel.chatText.setText("");// 清空聊天版
			paintControlPanel.color.setBackground(Color.black);// 设置画笔颜色默认为黑
			paintPanel.color = Color.black;
			paintControlPanel.penSlider.setValue(5);// 设置PaintControlPanel为默认选择
			paintControlPanel.eraserSlider.setValue(5);
			paintPanel.clear();// 清空画板
		}

		public void run() {
			try {
				while (state) {
					msg = remoteIn.readInt();// 读取标记为，判断是哪种信息

					switch (msg) {
					case 1:// 绘图信息
						paint = (PaintMessage) remoteIn.readUnshared();// 读取绘图信息
						paintPanel.paint.add(paint);// 加到存储绘图数据的数组中
						paintPanel.repaint();// 刷新画图面板
						break;
					case 2:// 聊天内容，以及服务器发过来的回答正确的消息
						talk = (String) remoteIn.readUnshared();// 读取聊天信息
						chatPanel.chatText.append(talk); // 插入消息到聊天版信息未端
						break;
					case 3:// 倒计时
						countDown = (String) remoteIn.readUnshared();
						chatPanel.timeLabel.setText("倒计时：" + countDown);// 显示到倒计时label
						break;
					case 4:// 玩家名称
						players = (String[]) remoteIn.readUnshared();
						userPanel.nameLabel1.setText(players[0]);
						userPanel.nameLabel2.setText(players[1]);
						userPanel.nameLabel3.setText(players[2]);
						userPanel.nameLabel4.setText(players[3]);
						userPanel.nameLabel5.setText(players[4]);
						userPanel.nameLabel6.setText(players[5]);
						break;
					case 5:// 清屏
						paintPanel.clear();
						break;
					case 6:// 游戏开始的初始信息
						init = (InitMessage) remoteIn.readUnshared();
						if (init.getDrawer().equals(me)) {// 如果轮到“我”画
							hintLabel.setText("第" + init.turn
									+ "轮                    " + "题目:"
									+ init.key + "            现在是你画");
							draw = true;
						} else {// 如果不是“我”画
							hintLabel.setText("第" + init.turn
									+ "轮                   " + "题示:"
									+ init.hint + "          现在是" + init.drawer
									+ "画");
							draw = false;
						}
						paintPanel.setDraw(draw);
						paintControlPanel.setDraw(draw);
						break;
					case 10:// Game over.
						state = false;// 关闭当前监听线程
						rank=(String)remoteIn.readUnshared();
						JOptionPane.showMessageDialog(GameBegin.this,rank);
						init();// 初始化游戏界面
						sound.stop();//音乐停止
						GameBegin.this.setVisible(false);// 隐藏玩游戏的界面
						GameWait.threadState.release();// 重新开始等待开始界面监听线程
						break;
					case 11:// 玩家积分
						scores = (int[]) remoteIn.readUnshared();
						userPanel.scoreLabel1.setText("" + scores[0]);
						userPanel.scoreLabel2.setText("" + scores[1]);
						userPanel.scoreLabel3.setText("" + scores[2]);
						userPanel.scoreLabel4.setText("" + scores[3]);
						userPanel.scoreLabel5.setText("" + scores[4]);
						userPanel.scoreLabel6.setText("" + scores[5]);
						break;
					case 13:// 本局画图结束
						JOptionPane.showMessageDialog(GameBegin.this,
								"本局结束，请下一位玩家执笔!");
						init();// 初始化游戏界面
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
