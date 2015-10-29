import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

public class InitMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	String key;// 题目
	String hint;// 提示
	int id; // 画的人的id，从1开始
	String drawer;// 本局轮到画图的人的名字
	int turn;// 第几轮

	public InitMessage() {
		key = null;
		hint = null;
		id = 1;
		drawer = "player1";
		turn = 1;
	}

	public InitMessage(String key, String hint, int id, String drawer, int turn) {
		super();
		this.key = key;
		this.hint = hint;
		this.id = id;
		this.drawer = drawer;
		this.turn = turn;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDrawer() {
		return drawer;
	}

	public void setDrawer(String drawer) {
		this.drawer = drawer;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public void randomkey() // 随机生成题目
	{
		int rand = 2 * (int) (Math.random() * 95);
		int i;
		File file = new File("resource/title.txt");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			for (i = 1; i <= rand; i++)
				tempString = reader.readLine();

			if ((tempString = reader.readLine()) != null)
				key = tempString;
			if ((tempString = reader.readLine()) != null)
				hint = tempString;
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

}
