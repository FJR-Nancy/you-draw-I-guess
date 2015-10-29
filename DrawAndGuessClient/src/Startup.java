import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JOptionPane;

public class Startup {
	Socket sock = null;
	private int port = 5001;
	private String host = "local";// ��ʼ���������ڱ���
	private String me;
	ObjectOutputStream remoteOut;
	ObjectInputStream remoteIn;
	String[] players;
	int i;

	Startup() {
		while (true) {
			host = JOptionPane.showInputDialog(null, "�����������ip(����Ϊlocal):",
					"local");// ���������ip
			try {
				if (host == null)// ����û�ȡ������
					System.exit(0);
				else {
					if (host.equals("local"))
						host = null;
					sock = new Socket(host, port);// ���ӷ�����
					remoteOut = new ObjectOutputStream(sock.getOutputStream());// ��socket���������ͨ��
					remoteIn = new ObjectInputStream(sock.getInputStream());// ��socket����������ͨ��
					// ��ȡ��ǰ�ѽ�����Ϸ������ǳƣ������ж��ǳ��Ƿ��ͻ
					remoteIn.readInt();
					players = (String[]) remoteIn.readUnshared();
					while (true) {
						try {
							me = JOptionPane.showInputDialog("�ף����ǽ���ʲô���أ���");// �����ǳ�
							if (me == null)// ����û�ȡ������
								System.exit(0);
							else {
								for (i = 0; i < 6; i++) {
									if (me.equals(players[i])
											|| me.equals("��λ�Դ�")) {// ����ǳƳ�ͻ
										JOptionPane.showMessageDialog(null,
												"���ǳ��Ѵ��ڣ����������룡");
										break;
									}
								}
								if (i == 6) {// ����ǳƲ���ͻ
									remoteOut.writeInt(12);// �����ǳ�
									remoteOut.flush();
									remoteOut.writeUnshared(me);
									new GameWait(remoteIn, remoteOut, me);// ����ȴ���ʼ����
									break;
								}
							}
						} catch (Exception e) {
							System.out.println(e.getMessage() + ":Error.");
						}
					}
					break;
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "�������ip�������������룡");// ������ӷ�����ʧ��
				try {
					if (sock != null)
						sock.close();// �ر�socket
				} catch (IOException x) {
					System.out.println(e.getMessage()
							+ ": Failed to close client socket.");
				}
			} catch (ClassNotFoundException e) {
				System.out.println(e.getMessage() + ": Class Not Found.");
			}
		}
	}

	public static void main(String[] args) {
		new Startup();
	}
}
