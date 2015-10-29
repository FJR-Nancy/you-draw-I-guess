/*
 * ��Ϸ��ʼ����
 * �Ҳ�ĵ���ʱ+��������+���Ͱ�ť  ���
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
	JLabel timeLabel;//����ʱ
	JTextArea chatText;//������
	private JTextField sendText;
	private JButton sendButton;

	ObjectOutputStream remoteOut;

	ChatPanel() {
		timeLabel = new JLabel("����ʱ��60");
		chatText = new JTextArea();
		sendText = new JTextField();
		sendButton = new JButton("");

		// ����chatText��sendButton�ĺ��Ͳ�������
		Box box = new Box(BoxLayout.X_AXIS);
		box.add(new JScrollPane(sendText));
		box.add(sendButton);

		// �����������
		setLayout(new BorderLayout());
		add(timeLabel, BorderLayout.NORTH);
		add(new JScrollPane(chatText), BorderLayout.CENTER);
		add(box, BorderLayout.SOUTH);

		// ��������¼�
		Action sendMessage = new AbstractAction() { // ������ϢAction
			public void actionPerformed(ActionEvent e) {
				replaceMessage(); // ������Ϣ��ʾ��
			}
		};
		sendText.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "send"); // �����¼�����,���ܻس��¼�
		sendText.getActionMap().put("send", sendMessage); // �س�ʱ�Ĵ���(���÷�����ϢAction)
		sendButton.setAction(sendMessage); // ��������Ϊ������Ϣ
		sendButton.setText("����");
	}

	void setRemoteOut(ObjectOutputStream remoteOut) {
		this.remoteOut = remoteOut;
	}

	private void replaceMessage() {
		String message = sendText.getText() + "\n"; // �õ���Ϣ�ı�
		// chatText.insert(message,chatText.getDocument().getLength());
		// chatText.append(message);//������Ϣ����ʾ��δ��
		sendText.setText(""); // ���������Ϣ��
		try {
			remoteOut.writeInt(2);
			remoteOut.flush();
			remoteOut.writeUnshared(message);
		} catch (IOException ex) {
			System.out.println(ex.getMessage() + ": Write error.");
		}
	}

	/*
	 * ������ public static void main(String[] args){ JFrame frame = new JFrame();
	 * ChatPanel panel = new ChatPanel(); frame.add(panel); frame.setSize(250,
	 * 500); frame.setVisible(true); }
	 */
}
