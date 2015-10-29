/*
 * 游戏开始界面
 * 右侧的倒计时+聊天内容+发送按钮  面板
 * */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
//import javax.swing.JFrame;

public class ChatPanel extends JPanel {
	JLabel timeLabel;//倒计时
	JTextArea chatText;//聊天区
	private JTextField sendText;
	private JButton sendButton;

	ObjectOutputStream remoteOut;

	ChatPanel() {
		timeLabel = new JLabel("倒计时：60");
		chatText = new JTextArea();
		sendText = new JTextField();
		sendButton = new JButton("");

		// 放置chatText和sendButton的盒型布局容器
		Box box = new Box(BoxLayout.X_AXIS);
		box.add(new JScrollPane(sendText));
		box.add(sendButton);

		// 放置所有组件
		setLayout(new BorderLayout());
		add(timeLabel, BorderLayout.NORTH);
		add(new JScrollPane(chatText), BorderLayout.CENTER);
		add(box, BorderLayout.SOUTH);

		// 处理键盘事件
		Action sendMessage = new AbstractAction() { // 发送消息Action
			public void actionPerformed(ActionEvent e) {
				replaceMessage(); // 更新消息显示框
			}
		};
		sendText.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "send"); // 键盘事件处理,按受回车事件
		sendText.getActionMap().put("send", sendMessage); // 回车时的处理(调用发送消息Action)
		sendButton.setAction(sendMessage); // 设置命令为发送消息
		sendButton.setText("发送");
	}

	void setRemoteOut(ObjectOutputStream remoteOut) {
		this.remoteOut = remoteOut;
	}

	private void replaceMessage() {
		String message = sendText.getText() + "\n"; // 得到消息文本
		// chatText.insert(message,chatText.getDocument().getLength());
		// chatText.append(message);//插入消息到显示域未端
		sendText.setText(""); // 清空输入消息域
		try {
			remoteOut.writeInt(2);
			remoteOut.flush();
			remoteOut.writeUnshared(message);
		} catch (IOException ex) {
			System.out.println(ex.getMessage() + ": Write error.");
		}
	}

	/*
	 * 测试用 public static void main(String[] args){ JFrame frame = new JFrame();
	 * ChatPanel panel = new ChatPanel(); frame.add(panel); frame.setSize(250,
	 * 500); frame.setVisible(true); }
	 */
}
