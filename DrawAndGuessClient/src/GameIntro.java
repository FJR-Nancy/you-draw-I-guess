import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class GameIntro extends JFrame {
	private JLabel introLabel01 = new JLabel("��Ϸ����");
	private JTextArea introArea01 = new JTextArea(
			"    �㻭�Ҳ���һ������򵥡����ٽ��˵�������������Ϸ���ô�����¶�ʱͿѻ����Ȥ��ͬʱ�������Գ�ֵص�����ҵ�˼ά��������������˫Ӯ����Ϸ�趨����Ϸ�ܿ��˼��ҵĶԿ����棬ʹ��Ҽ��������ܰ��Э�����գ����⿴�Ƽ򵥵���Ϸ�������÷��ӵ����쾡�£�ǧ��ٹֵ�ͼ������ֱ�Ц�Ĵ𰸣��㻭�Ҳ����ܹ��������Ц�㡣");
	private JLabel introLabel02 = new JLabel("��Ϸ�淨");
	private JTextArea introArea02 = new JTextArea(
			"    ������Ϸ����󣬵ȴ�������ҽ��롣Ȼ�������ڻ����ϻ���(������Ҽ��򻭱ʱ�Ϊ��Ƥ����������ҽ��в��ա�����ʾ�ֵ���滭ʱ����ɸ�����Ŀ�������Լ����������滭��������Ҳ��գ����ֵ����ʱ������������һ���ͼ�����Լ���Ϊ�Ĵ�д���ı��򣬷��ͼ��ɡ�������Ҳ¶Ի��Ǽ�ʱ����ʱ�þ���Ϸ����������һλ��ҽ��л�����ÿ����һ�һ����һ�֣����ֽ���ʱ��Ϸ������");
	private JLabel introLabel03 = new JLabel("���ּ������");
	private JTextArea introArea03 = new JTextArea(
			"    ÿ��֮�ڵ�һ������Ҳ¶�ʱ���µ��˵�2�֣������˵�3�֡�֮��ÿ������Ҳ¶ԣ��µ��˺ͻ����˸���1�֡�����֮�ڣ������ۼơ�");
	private ImageIcon background;
	private JPanel imagePanel;

	GameIntro() {

		/*
		 * //introLabel11.setBounds(40, 40, 200, 300);
		 * //introLabel11.setSize(300, 300); //introLabel11.setFont(new
		 * Font("΢���ź�", Font.PLAIN, 15)); //introLabel11.setLocation(100, 200);
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

		// ��������label
		introLabel01.setBounds(800, 160, 100, 30);
		introLabel01.setFont(new Font("΢���ź�", Font.BOLD, 25));
		introLabel02.setBounds(800, 280, 100, 30);
		introLabel02.setFont(new Font("΢���ź�", Font.BOLD, 25));
		introLabel03.setBounds(780, 410, 100, 30);
		introLabel03.setSize(300, 40);
		introLabel03.setFont(new Font("΢���ź�", Font.BOLD, 25));

		// Ϊ�ı������������
		introArea01.setBounds(450, 200, 1000, 50);// λ��
		introArea01.setSize(500, 100);// ��С
		introArea01.setFont(new Font("����", Font.PLAIN, 15));// ����
		introArea02.setBounds(450, 320, 1000, 50);// λ��
		introArea02.setSize(500, 120);// ��С
		introArea02.setFont(new Font("����", Font.PLAIN, 15));// ����
		introArea03.setBounds(450, 455, 1000, 50);// λ��
		introArea03.setSize(500, 100);// ��С
		introArea03.setFont(new Font("����", Font.PLAIN, 15));// ����
		// ��������ʹ�ı������Ի���
		introArea01.setEditable(false);
		introArea01.setLineWrap(true);
		introArea01.setWrapStyleWord(true);
		introArea02.setEditable(false);
		introArea02.setLineWrap(true);
		introArea02.setWrapStyleWord(true);
		introArea03.setEditable(false);
		introArea03.setLineWrap(true);
		introArea03.setWrapStyleWord(true);
		// ����͸��!!
		introArea01.setOpaque(false);
		introArea02.setOpaque(false);
		introArea03.setOpaque(false);

		JPanel panel = new JPanel();
		panel.setLayout(null);// null����
		panel.setBounds(0, 0, 1000, 700);// null����һ��Ҫ�����
		panel.setLocation(0, 0);// null����һ��Ҫ�����
		// setContentPane(panel);

		// this.getContentPane().setLayout(null);
		panel.add(introLabel01);
		panel.add(introArea01);
		panel.add(introLabel02);
		panel.add(introArea02);
		panel.add(introLabel03);
		panel.add(introArea03);

		// ���ñ���
		background = new ImageIcon("resource/bg3.jpg");// ����ͼƬ
		JLabel label = new JLabel(background);// �ѱ���ͼƬ��ʾ��һ����ǩ����
		// �ѱ�ǩ�Ĵ�Сλ������ΪͼƬ�պ�����������
		label.setBounds(0, 0, background.getIconWidth(),
				background.getIconHeight());
		// �����ݴ���ת��ΪJPanel���������÷���setOpaque()��ʹ���ݴ���͸��
		imagePanel = (JPanel) this.getContentPane();
		imagePanel.setOpaque(false);
		// ���ݴ���Ĭ�ϵĲ��ֹ�����ΪBorderLayout
		imagePanel.setLayout(new FlowLayout());
		// imagePanel.add(panel);

		this.getLayeredPane().setLayout(null);
		// �ѱ���ͼƬ��ӵ��ֲ㴰�����ײ���Ϊ����
		this.getLayeredPane().add(label, new Integer(Integer.MIN_VALUE));
		this.getLayeredPane().add(panel, new Integer(Integer.MAX_VALUE));
		panel.setOpaque(false);

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle("��Ϸ����");
		this.setLocation(170,  10);
		this.setSize(background.getIconWidth(), background.getIconHeight());
		this.setVisible(true);
	}

	// ������
	public static void main(String[] args) {
		new GameIntro();
	}

}