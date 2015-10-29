import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JOptionPane;

public class Startup {
	Socket sock = null;
	private int port = 5001;
	private String host = "local";// 初始化服务器在本地
	private String me;
	ObjectOutputStream remoteOut;
	ObjectInputStream remoteIn;
	String[] players;
	int i;

	Startup() {
		while (true) {
			host = JOptionPane.showInputDialog(null, "请输入服务器ip(本地为local):",
					"local");// 输入服务器ip
			try {
				if (host == null)// 如果用户取消输入
					System.exit(0);
				else {
					if (host.equals("local"))
						host = null;
					sock = new Socket(host, port);// 连接服务器
					remoteOut = new ObjectOutputStream(sock.getOutputStream());// 给socket输出流建立通道
					remoteIn = new ObjectInputStream(sock.getInputStream());// 给socket输入流建立通道
					// 读取当前已进入游戏的玩家昵称，用以判断昵称是否冲突
					remoteIn.readInt();
					players = (String[]) remoteIn.readUnshared();
					while (true) {
						try {
							me = JOptionPane.showInputDialog("亲，我们叫你什么好呢？：");// 输入昵称
							if (me == null)// 如果用户取消输入
								System.exit(0);
							else {
								for (i = 0; i < 6; i++) {
									if (me.equals(players[i])
											|| me.equals("虚位以待")) {// 如果昵称冲突
										JOptionPane.showMessageDialog(null,
												"此昵称已存在，请重新输入！");
										break;
									}
								}
								if (i == 6) {// 如果昵称不冲突
									remoteOut.writeInt(12);// 发送昵称
									remoteOut.flush();
									remoteOut.writeUnshared(me);
									new GameWait(remoteIn, remoteOut, me);// 进入等待开始界面
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
				JOptionPane.showMessageDialog(null, "您输入的ip有误，请重新输入！");// 如果连接服务器失败
				try {
					if (sock != null)
						sock.close();// 关闭socket
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
