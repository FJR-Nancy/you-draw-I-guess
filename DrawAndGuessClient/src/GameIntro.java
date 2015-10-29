import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class GameIntro extends JFrame {
	private JLabel introLabel01 = new JLabel("游戏介绍");
	private JTextArea introArea01 = new JTextArea(
			"    你画我猜是一款操作简单、老少皆宜的休闲益智类游戏，让大家重温儿时涂鸦的乐趣的同时，还可以充分地调动玩家的思维创造能力；合作双赢的游戏设定让游戏避开了激烈的对抗场面，使玩家间充满了温馨的协作气氛，将这看似简单的游戏互动作用发挥得淋漓尽致！千奇百怪的图画，搞怪爆笑的答案，你画我猜总能够戳中你的笑点。");
	private JLabel introLabel02 = new JLabel("游戏玩法");
	private JTextArea introArea02 = new JTextArea(
			"    进入游戏房间后，等待其他玩家进入。然后轮流在画板上画画(鼠标点击右键则画笔变为橡皮），其他玩家进行猜谜。当提示轮到你绘画时，你可根据题目来发挥自己的想象力绘画给其他玩家猜谜；而轮到你猜时，根据其他玩家画的图案把自己认为的答案写入文本框，发送即可。所有玩家猜对或是计时结束时该局游戏结束，换下一位玩家进行画画。每个玩家画一次算一轮，两轮结束时游戏结束。");
	private JLabel introLabel03 = new JLabel("积分计算规则");
	private JTextArea introArea03 = new JTextArea(
			"    每局之内第一次有玩家猜对时，猜的人得2分，画的人得3分。之后每次有玩家猜对，猜的人和画的人各得1分。两轮之内，积分累计。");
	private ImageIcon background;
	private JPanel imagePanel;

	GameIntro() {

		/*
		 * //introLabel11.setBounds(40, 40, 200, 300);
		 * //introLabel11.setSize(300, 300); //introLabel11.setFont(new
		 * Font("微软雅黑", Font.PLAIN, 15)); //introLabel11.setLocation(100, 200);
		 * //Box box = new Box(BoxLayout.Y_AXIS);
		 * //box.add(Box.createVerticalStrut(50));
		 * introText01.setEditable(false); introText01.setForeground(Color.red);
		 * //this.add(introText01); //this.add(introText11);
		 * this.add(introLabel02); this.add(introLabel22);
		 * //this.add(introLabel03); //this.add(introLabel33);
		 * 
		 * //introLabel01.setAlignmentX(CENTER_ALIGNMENT);
		 * //introLabel02.setAlignmentX(CENTER_ALIGNMENT);
		 * //introLabel03.setAlignmentX(CENTER_ALIGNMENT); //this.add(box);
		 */

		// 设置三个label
		introLabel01.setBounds(800, 160, 100, 30);
		introLabel01.setFont(new Font("微软雅黑", Font.BOLD, 25));
		introLabel02.setBounds(800, 280, 100, 30);
		introLabel02.setFont(new Font("微软雅黑", Font.BOLD, 25));
		introLabel03.setBounds(780, 410, 100, 30);
		introLabel03.setSize(300, 40);
		introLabel03.setFont(new Font("微软雅黑", Font.BOLD, 25));

		// 为文本区域进行设置
		introArea01.setBounds(450, 200, 1000, 50);// 位置
		introArea01.setSize(500, 100);// 大小
		introArea01.setFont(new Font("仿宋", Font.PLAIN, 15));// 字体
		introArea02.setBounds(450, 320, 1000, 50);// 位置
		introArea02.setSize(500, 120);// 大小
		introArea02.setFont(new Font("仿宋", Font.PLAIN, 15));// 字体
		introArea03.setBounds(450, 455, 1000, 50);// 位置
		introArea03.setSize(500, 100);// 大小
		introArea03.setFont(new Font("仿宋", Font.PLAIN, 15));// 字体
		// 以下三行使文本区可以换行
		introArea01.setEditable(false);
		introArea01.setLineWrap(true);
		introArea01.setWrapStyleWord(true);
		introArea02.setEditable(false);
		introArea02.setLineWrap(true);
		introArea02.setWrapStyleWord(true);
		introArea03.setEditable(false);
		introArea03.setLineWrap(true);
		introArea03.setWrapStyleWord(true);
		// 背景透明!!
		introArea01.setOpaque(false);
		introArea02.setOpaque(false);
		introArea03.setOpaque(false);

		JPanel panel = new JPanel();
		panel.setLayout(null);// null布局
		panel.setBounds(0, 0, 1000, 700);// null布局一定要加这个
		panel.setLocation(0, 0);// null布局一定要加这个
		// setContentPane(panel);

		// this.getContentPane().setLayout(null);
		panel.add(introLabel01);
		panel.add(introArea01);
		panel.add(introLabel02);
		panel.add(introArea02);
		panel.add(introLabel03);
		panel.add(introArea03);

		// 设置背景
		background = new ImageIcon("resource/bg3.jpg");// 背景图片
		JLabel label = new JLabel(background);// 把背景图片显示在一个标签里面
		// 把标签的大小位置设置为图片刚好填充整个面板
		label.setBounds(0, 0, background.getIconWidth(),
				background.getIconHeight());
		// 把内容窗格转化为JPanel，否则不能用方法setOpaque()来使内容窗格透明
		imagePanel = (JPanel) this.getContentPane();
		imagePanel.setOpaque(false);
		// 内容窗格默认的布局管理器为BorderLayout
		imagePanel.setLayout(new FlowLayout());
		// imagePanel.add(panel);

		this.getLayeredPane().setLayout(null);
		// 把背景图片添加到分层窗格的最底层作为背景
		this.getLayeredPane().add(label, new Integer(Integer.MIN_VALUE));
		this.getLayeredPane().add(panel, new Integer(Integer.MAX_VALUE));
		panel.setOpaque(false);

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle("游戏介绍");
		this.setLocation(170,  10);
		this.setSize(background.getIconWidth(), background.getIconHeight());
		this.setVisible(true);
	}

	// 测试用
	public static void main(String[] args) {
		new GameIntro();
	}

}