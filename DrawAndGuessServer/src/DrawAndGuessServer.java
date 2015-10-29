import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Semaphore;

public class DrawAndGuessServer
{
	private int     port = 5001;
	ServerSocket    serverSock = null;
	private Vector  clients = new Vector();
	int i;			//当前几个玩家
	InitMessage title = new InitMessage(null,null,1,null,0);//包括题目、提示、轮到谁画、当前第几轮
	private int[] 	scores = {0,0,0,0,0,0};
	private String[] players={"虚位以待","虚位以待","虚位以待","虚位以待","虚位以待","虚位以待"};
    private int[] 	readyState={-1,-1,-1,-1,-1,-1,0};
    int[] result={-1,-1,-1,-1,-1,-1};	//坚持到游戏结束的玩家
    Semaphore CountDownSem = new Semaphore(0);		//控制倒计时的开关
    int first=0;	//0-当局还没人答出来 1-有人答出来了
    int[] right={0,0,0,0,0,0};		//记录当局答对情况，1-已答对
    boolean countOver=false;		//true-倒计时结束
	
    public  static void main(String args[])
    {
        new DrawAndGuessServer().server();
    }
    
    DrawAndGuessServer()
	{
        try {
            serverSock = new ServerSocket(port, 50);
        } catch(IOException e) {
            System.out.println(e);	
            return;
        }
	}

    private void server()
    {
    	new CountDown(60).start();
        while(true){
            try {
				Socket socket = serverSock.accept();
				System.out.println("New Client:"+socket);
				if(readyState[6]==1){
					System.out.println("正在游戏中，无法加入");
					continue;
				}
				for(i=0;i<=5;i++){
					if(players[i].equals("虚位以待"))
						break;
				}
				if(i>5){
					System.out.println("房间已满，无法加入");
					continue;
				}
				i++;
				ObjectOutputStream remoteOut = new ObjectOutputStream(socket.getOutputStream());
				clients.addElement(remoteOut);
			
				ObjectInputStream remoteIn = new ObjectInputStream(socket.getInputStream());
				new ServerHelder(remoteIn,remoteOut,socket.getPort(),i).start();
			} catch(IOException e){
				System.out.println(e.getMessage() + ": Failed to connect to client.");
				try {
					serverSock.close();
				} catch(IOException x){
				System.out.println(e.getMessage() + ": Failed to close server socket.");
				}
			}
		}
	}

	class ServerHelder extends Thread
	{
		ObjectInputStream remoteIn;
		ObjectOutputStream remoteOut;
		int port , id;	//id：第几个玩家，从1开始
		String name;
		
		ServerHelder(ObjectInputStream remoteIn,ObjectOutputStream remoteOut,int port,int no)
		{	
			this.remoteIn = remoteIn;
			this.remoteOut = remoteOut;
			this.port = port;
			id = no;
			name = "";
			setDaemon(true);                 // Thread is daemon
		}
		
		public synchronized void run()
		{
			int messageType;
			String buf;
			PaintMessage paint;
			int j,k,num,tmp;
			boolean state=true;
			
			try {
				readyState[id-1]=0;	//进入等待房间
				broadcast(4,players);
				//当前客户端叫什么名字
				/*remoteOut.writeInt(12);
				remoteOut.flush();
				remoteOut.writeUnshared(name);*/
				//大家的名字和等待状态
				/*broadcast(4,players);
				broadcast(9,readyState);*/
				
				while(state){
					messageType = remoteIn.readInt();
					switch(messageType)
					{
					case 1:		//画板上画的像素点
					{
						paint=(PaintMessage)remoteIn.readUnshared();
						broadcast(1,paint);
						break;
					}
					case 2:		//聊天框中内容
					{
						buf=(String)remoteIn.readUnshared();
						if(buf.equals(title.getKey()+"\n") && id!=title.getId() && right[id-1]==0){
							System.out.println(port+":"+buf);
							buf="(**回答正确**)\n";
							right[id-1]=1;
							if(first==0){	//第一个人答对
								scores[id-1]+=2;
								scores[title.getId()-1]+=3;
								first=1;
							}
							else{		//非第一个答对的，画的与猜的各加1分
								scores[id-1]+=1;
								scores[title.getId()-1]+=1;
							}
							//要重发成绩
							broadcast(11,scores);
						}
						else if(buf.equals(title.getKey()+"\n") && id!=title.getId() && right[id-1]==1)
							buf="(*@#$*/%(*!)\n";
						if(buf.equals(title.getKey()+"\n") && id==title.getId())
							buf="(*@#$*/%(*!)\n";
						SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
						buf="["+df.format(new Date())+"]"+players[id-1]+":"+buf;
						broadcast(2,buf);
						
						for(j=0;j<=5;j++){
							if(readyState[j]!=-1 && right[j]==0 && j!=(title.getId()-1))
								break;
						}
						if(j>5)	//大家都已经答对了
							countOver=true;
						
						break;		
					}
					case 5:		//清屏
						broadcast(5);
						break;
					case 7:		//等待界面，当前用户准备
					{
						readyState[id-1]=1;
						//检测是否能开始
						for(j=0,num=0,k=0;j<=5;j++){
							if(readyState[j]!=-1)
								num++;	//统计等待房间内人数
							if(readyState[j]==0)
								k++;	//统计没准备的人数
						}
						if(num>=2 && k==0)	//可以开始了
							readyState[6]=1;			
						//传一下准备状态
						broadcast(9,readyState);
						
						if(readyState[6]==1){
							//大家的名字
							broadcast(4,players);
							System.out.println("游戏开始！");
							
							CountDownSem.release();	//开始倒计时
						}
						break;
					}
					case 8:		//等待界面，当前用户退出
					{
						readyState[id-1]=-1;
						players[id-1]="虚位以待";
						System.out.println("端口号"+port+"的玩家退出了房间");
						
						state=false;
						broadcast(4,players);
						clients.removeElement(remoteOut);
						
						//再检测一下是否符合开始条件
						for(j=0,num=0,k=0;j<=5;j++){
							if(readyState[j]!=-1)
								num++;	//统计等待房间内人数
							if(readyState[j]==0)
								k++;	//统计没准备的人数
						}
						if(num>=2 && k==0)	//可以开始了
							readyState[6]=1;			
						//传一下准备状态
						broadcast(9,readyState);
						if(readyState[6]==1){
							//大家的名字
							broadcast(4,players);
							System.out.println("游戏开始！");
							
							CountDownSem.release();	//开始倒计时
						}
						break;
					}
					case 12:	//传入用户输入的昵称
						name=(String)remoteIn.readUnshared();
						players[id-1]=name;
						broadcast(4,players);
						broadcast(9,readyState);
						break;
					//case n+1
					}
				}
			} catch(Exception e){
				System.out.println(e.getMessage() + ": 端口为" + port + "的玩家退出了游戏。。");
				players[id-1]="虚位以待";
				readyState[id-1]=-1;
				scores[id-1]=0;		
				for(tmp=0;tmp<=5;tmp++){	//判断是不是最后一个人
					if(readyState[tmp]!=-1)
						break;
				}
				if(tmp>5)
					readyState[6]=0;
				String s = name +"离开了房间。。。\n";	
				broadcast(2,s);
				broadcast(4,players);
				broadcast(11,scores);
				clients.removeElement(remoteOut);
				if(id==title.getId())	//画的人退出了
					countOver=true;
			}
		}	
	}
	
	class CountDown extends Thread
	{
		int totalTime,tmp;
		
		CountDown(int i)
		{
			totalTime=i;
			tmp=i;
		}
		
		public void run()
		{
			int j;
			while(true){
				try {
					CountDownSem.acquire();
					if(title.getTurn()==0) //刚开始游戏，初始化题目
					{
						for(j=0;j<=5;j++){
							if(!players[j].equals("虚位以待"))
								break;
						}
						title.setDrawer(players[j]);
						title.setId(j+1);
						title.setTurn(1);
						title.randomkey();
						broadcast(6,title);
						broadcast(11,scores);
					}
					broadcast(2,"-------回合开始-------\n");
					timeVoid();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
			}
		}

		public void timeVoid()
		{
			final Timer timer = new Timer();
			TimerTask tt=new TimerTask() {
				@Override
				public void run(){
					int j,k;
					int m,index,rank1,rank2,rank3;
					int num; 
					String rank;
					broadcast(3,Integer.toString(tmp));
					tmp--;
					if(tmp<0 || countOver){		//一回合结束
						countOver=false;
						tmp=totalTime;
						broadcast(2,"~~~~~~~回合结束~~~~~~~\n");
						for(int a=0;a<=5;a++)
							right[a]=0;
						//更新题目
						j=title.getId();
						if(j==6){
							title.setTurn(title.getTurn()+1);
							for(k=0;k<=5;k++){		//找第一个玩家
								if(!players[k].equals("虚位以待"))
									break;
							}
							if(k<=5){
								title.setDrawer(players[k]);
								title.setId(k+1);
							}			
						}
						else{
							for(k=j;k<=5;k++){
								if(!players[k].equals("虚位以待"))
									break;
							}
							if(k<=5){
								title.setDrawer(players[k]);
								title.setId(k+1);
							}
							else{
								title.setTurn(title.getTurn()+1);
								
								for(k=0;k<=5;k++){
									if(!players[k].equals("虚位以待"))
										break;
								}
								if(k<=5){
									title.setDrawer(players[k]);
									title.setId(k+1);
								}		
							}					
						}					
						title.randomkey();
						first=0;
						
						if(title.getTurn()<=2){	//两轮还没结束
							broadcast(13);
							broadcast(6,title);
							CountDownSem.release();							
						}
						else{			//*******两轮结束，游戏结束**********
							System.out.print("游戏结束！");
							//要发最后排行榜
							for(m=0,num=0;m<=5;m++){
								if(!players[m].equals("虚位以待")){
									result[num]=m;	//记录所有坚持到最后的玩家
									num++;
								}
							}
							if(num==1)		//只剩一个人
								rank="第一名：  "+players[result[0]]+"    积分：  "+Integer.toString(scores[result[0]]);
							else if(num==2){
								if(scores[result[0]]>=scores[result[1]])
									rank="第一名：  "+players[result[0]]+"    积分：  "+Integer.toString(scores[result[0]])
										+"\n第二名：  "+players[result[1]]+"    积分：  "+Integer.toString(scores[result[1]]);
								else
									rank="第一名：  "+players[result[1]]+"    积分：  "+Integer.toString(scores[result[1]])
									+"\n第二名：  "+players[result[0]]+"    积分：  "+Integer.toString(scores[result[0]]);
							}
							else{	//三个人以上，取前三名
								for(index=1,rank1=result[0];index<=5 && result[index]!=-1;index++){
									if(scores[result[index]]>scores[rank1])
										rank1=result[index];
								}
								
								if(rank1==result[0])
									rank2=result[1];
								else
									rank2=result[0];
								for(index=0;index<=5 && result[index]!=-1;index++){
									if(result[index]==rank1)
										continue;
									if(scores[result[index]]>scores[rank2])
										rank2=result[index];
								}
								
								for(index=0;index<=5;index++){
									if(result[index]!=rank1 && result[index]!=rank2)
										break;
								}
								rank3=result[index];				
								for(index=0;index<=5 && result[index]!=-1;index++){
									if(result[index]==rank1 || result[index]==rank2)
										continue;
									if(scores[result[index]]>scores[rank3])
										rank3=result[index];
								}
								
								
								rank="第一名：  "+players[rank1]+"    积分：  "+Integer.toString(scores[rank1])
									+"\n第二名：  "+players[rank2]+"    积分：  "+Integer.toString(scores[rank2])
									+"\n第三名：  "+players[rank3]+"    积分：  "+Integer.toString(scores[rank3])	;
							}
							
							broadcast(10,rank);
							
							//所有状态初始化
							for(m=0;m<=5;m++){
								scores[m]=0;
								result[m]=-1;
								if(!players[m].equals("虚位以待"))
									readyState[m]=0;
							}
							readyState[6]=0;
							title.setTurn(0);
							broadcast(9,readyState);
							broadcast(4,players);
						}
						timer.cancel();
					}
				}
			};		
			timer.schedule(tt, 3000 , 1000);
		}		
	}
	
	//以下是各种广播方法
	private synchronized void broadcast(int i,Object obj)
	{
		ObjectOutputStream	dataOut = null;
			
		for(Enumeration e = clients.elements(); e.hasMoreElements();){
			dataOut = (ObjectOutputStream)(e.nextElement());
			try {
				dataOut.writeInt(i);
				dataOut.flush();
				dataOut.writeUnshared(obj);
			} catch(Exception x){
				System.out.println(x.getMessage() + ": Failed to broadcast to client.");
				clients.removeElement(dataOut);
			}
		}
	}
		
	private synchronized void broadcast(int i)
	{
		ObjectOutputStream	dataOut = null;
			
		for(Enumeration e = clients.elements(); e.hasMoreElements();){
			dataOut = (ObjectOutputStream)(e.nextElement());
			try {
				dataOut.writeInt(i);
				dataOut.flush();
			} catch(Exception x){
				System.out.println(x.getMessage() + ": Failed to broadcast to client.");
				clients.removeElement(dataOut);
			}
		}
	}
}