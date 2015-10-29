import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class UserPanel extends JPanel{

	private JLabel userLabel1, userLabel2, userLabel3, userLabel4, userLabel5, userLabel6;
	JLabel scoreLabel1, scoreLabel2, scoreLabel3, scoreLabel4, scoreLabel5, scoreLabel6;
	JLabel nameLabel1, nameLabel2, nameLabel3, nameLabel4, nameLabel5, nameLabel6;
	
	UserPanel(){
		userLabel1 = new JLabel();
		userLabel1.setIcon(new ImageIcon("resource/1.jpg"));
		userLabel2 = new JLabel();
		userLabel2.setIcon(new ImageIcon("resource/1.jpg"));
		userLabel3 = new JLabel();
		userLabel3.setIcon(new ImageIcon("resource/1.jpg"));
		userLabel4 = new JLabel();
		userLabel4.setIcon(new ImageIcon("resource/1.jpg"));
		userLabel5 = new JLabel();
		userLabel5.setIcon(new ImageIcon("resource/1.jpg"));
		userLabel6 = new JLabel();
		userLabel6.setIcon(new ImageIcon("resource/1.jpg"));
		scoreLabel1 = new JLabel("积分");
		scoreLabel2 = new JLabel("积分");
		scoreLabel3 = new JLabel("积分");
		scoreLabel4 = new JLabel("积分");
		scoreLabel5 = new JLabel("积分");
		scoreLabel6 = new JLabel("积分");
		nameLabel1 = new JLabel("姓名");
		nameLabel2 = new JLabel("姓名");
		nameLabel3 = new JLabel("姓名");
		nameLabel4 = new JLabel("姓名");
		nameLabel5 = new JLabel("姓名");
		nameLabel6 = new JLabel("姓名");
		
		Box box1 = new Box(BoxLayout.Y_AXIS);
		box1.add(userLabel1);
		box1.add(nameLabel1);
		box1.add(scoreLabel1);
		
		Box box2 = new Box(BoxLayout.Y_AXIS);
		box2.add(userLabel2);
		box2.add(nameLabel2);
		box2.add(scoreLabel2);
		
		Box box3 = new Box(BoxLayout.Y_AXIS);
		box3.add(userLabel3);
		box3.add(nameLabel3);
		box3.add(scoreLabel3);
		
		Box box4 = new Box(BoxLayout.Y_AXIS);
		box4.add(userLabel4);
		box4.add(nameLabel4);
		box4.add(scoreLabel4);
		
		Box box5 = new Box(BoxLayout.Y_AXIS);
		box5.add(userLabel5);
		box5.add(nameLabel5);
		box5.add(scoreLabel5);
		
		Box box6 = new Box(BoxLayout.Y_AXIS);
		box6.add(userLabel6);
		box6.add(nameLabel6);
		box6.add(scoreLabel6);
		
		
		setLayout(new FlowLayout());
		add(box1);
		add(box2);
		add(box3);
		add(box4);
		add(box5);
		add(box6);
	}
	
	
	//测试
	public static void main(String[] args){
		JFrame frame = new JFrame();
		JPanel panel = new UserPanel();
		frame.add(panel);
		frame.setSize(800, 200);
		frame.setVisible(true);
	}	
}
