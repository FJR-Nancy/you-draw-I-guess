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
	int i;			//��ǰ�������
	InitMessage title = new InitMessage(null,null,1,null,0);//������Ŀ����ʾ���ֵ�˭������ǰ�ڼ���
	private int[] 	scores = {0,0,0,0,0,0};
	private String[] players={"��λ�Դ�","��λ�Դ�","��λ�Դ�","��λ�Դ�","��λ�Դ�","��λ�Դ�"};
    private int[] 	readyState={-1,-1,-1,-1,-1,-1,0};
    int[] result={-1,-1,-1,-1,-1,-1};	//��ֵ���Ϸ���������
    Semaphore CountDownSem = new Semaphore(0);		//���Ƶ���ʱ�Ŀ���
    int first=0;	//0-���ֻ�û�˴���� 1-���˴������
    int[] right={0,0,0,0,0,0};		//��¼���ִ�������1-�Ѵ��
    boolean countOver=false;		//true-����ʱ����
	
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
					System.out.println("������Ϸ�У��޷�����");
					continue;
				}
				for(i=0;i<=5;i++){
					if(players[i].equals("��λ�Դ�"))
						break;
				}
				if(i>5){
					System.out.println("�����������޷�����");
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
		int port , id;	//id���ڼ�����ң���1��ʼ
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
				readyState[id-1]=0;	//����ȴ�����
				broadcast(4,players);
				//��ǰ�ͻ��˽�ʲô����
				/*remoteOut.writeInt(12);
				remoteOut.flush();
				remoteOut.writeUnshared(name);*/
				//��ҵ����ֺ͵ȴ�״̬
				/*broadcast(4,players);
				broadcast(9,readyState);*/
				
				while(state){
					messageType = remoteIn.readInt();
					switch(messageType)
					{
					case 1:		//�����ϻ������ص�
					{
						paint=(PaintMessage)remoteIn.readUnshared();
						broadcast(1,paint);
						break;
					}
					case 2:		//�����������
					{
						buf=(String)remoteIn.readUnshared();
						if(buf.equals(title.getKey()+"\n") && id!=title.getId() && right[id-1]==0){
							System.out.println(port+":"+buf);
							buf="(**�ش���ȷ**)\n";
							right[id-1]=1;
							if(first==0){	//��һ���˴��
								scores[id-1]+=2;
								scores[title.getId()-1]+=3;
								first=1;
							}
							else{		//�ǵ�һ����Եģ�������µĸ���1��
								scores[id-1]+=1;
								scores[title.getId()-1]+=1;
							}
							//Ҫ�ط��ɼ�
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
						if(j>5)	//��Ҷ��Ѿ������
							countOver=true;
						
						break;		
					}
					case 5:		//����
						broadcast(5);
						break;
					case 7:		//�ȴ����棬��ǰ�û�׼��
					{
						readyState[id-1]=1;
						//����Ƿ��ܿ�ʼ
						for(j=0,num=0,k=0;j<=5;j++){
							if(readyState[j]!=-1)
								num++;	//ͳ�Ƶȴ�����������
							if(readyState[j]==0)
								k++;	//ͳ��û׼��������
						}
						if(num>=2 && k==0)	//���Կ�ʼ��
							readyState[6]=1;			
						//��һ��׼��״̬
						broadcast(9,readyState);
						
						if(readyState[6]==1){
							//��ҵ�����
							broadcast(4,players);
							System.out.println("��Ϸ��ʼ��");
							
							CountDownSem.release();	//��ʼ����ʱ
						}
						break;
					}
					case 8:		//�ȴ����棬��ǰ�û��˳�
					{
						readyState[id-1]=-1;
						players[id-1]="��λ�Դ�";
						System.out.println("�˿ں�"+port+"������˳��˷���");
						
						state=false;
						broadcast(4,players);
						clients.removeElement(remoteOut);
						
						//�ټ��һ���Ƿ���Ͽ�ʼ����
						for(j=0,num=0,k=0;j<=5;j++){
							if(readyState[j]!=-1)
								num++;	//ͳ�Ƶȴ�����������
							if(readyState[j]==0)
								k++;	//ͳ��û׼��������
						}
						if(num>=2 && k==0)	//���Կ�ʼ��
							readyState[6]=1;			
						//��һ��׼��״̬
						broadcast(9,readyState);
						if(readyState[6]==1){
							//��ҵ�����
							broadcast(4,players);
							System.out.println("��Ϸ��ʼ��");
							
							CountDownSem.release();	//��ʼ����ʱ
						}
						break;
					}
					case 12:	//�����û�������ǳ�
						name=(String)remoteIn.readUnshared();
						players[id-1]=name;
						broadcast(4,players);
						broadcast(9,readyState);
						break;
					//case n+1
					}
				}
			} catch(Exception e){
				System.out.println(e.getMessage() + ": �˿�Ϊ" + port + "������˳�����Ϸ����");
				players[id-1]="��λ�Դ�";
				readyState[id-1]=-1;
				scores[id-1]=0;		
				for(tmp=0;tmp<=5;tmp++){	//�ж��ǲ������һ����
					if(readyState[tmp]!=-1)
						break;
				}
				if(tmp>5)
					readyState[6]=0;
				String s = name +"�뿪�˷��䡣����\n";	
				broadcast(2,s);
				broadcast(4,players);
				broadcast(11,scores);
				clients.removeElement(remoteOut);
				if(id==title.getId())	//�������˳���
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
					if(title.getTurn()==0) //�տ�ʼ��Ϸ����ʼ����Ŀ
					{
						for(j=0;j<=5;j++){
							if(!players[j].equals("��λ�Դ�"))
								break;
						}
						title.setDrawer(players[j]);
						title.setId(j+1);
						title.setTurn(1);
						title.randomkey();
						broadcast(6,title);
						broadcast(11,scores);
					}
					broadcast(2,"-------�غϿ�ʼ-------\n");
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
					if(tmp<0 || countOver){		//һ�غϽ���
						countOver=false;
						tmp=totalTime;
						broadcast(2,"~~~~~~~�غϽ���~~~~~~~\n");
						for(int a=0;a<=5;a++)
							right[a]=0;
						//������Ŀ
						j=title.getId();
						if(j==6){
							title.setTurn(title.getTurn()+1);
							for(k=0;k<=5;k++){		//�ҵ�һ�����
								if(!players[k].equals("��λ�Դ�"))
									break;
							}
							if(k<=5){
								title.setDrawer(players[k]);
								title.setId(k+1);
							}			
						}
						else{
							for(k=j;k<=5;k++){
								if(!players[k].equals("��λ�Դ�"))
									break;
							}
							if(k<=5){
								title.setDrawer(players[k]);
								title.setId(k+1);
							}
							else{
								title.setTurn(title.getTurn()+1);
								
								for(k=0;k<=5;k++){
									if(!players[k].equals("��λ�Դ�"))
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
						
						if(title.getTurn()<=2){	//���ֻ�û����
							broadcast(13);
							broadcast(6,title);
							CountDownSem.release();							
						}
						else{			//*******���ֽ�������Ϸ����**********
							System.out.print("��Ϸ������");
							//Ҫ��������а�
							for(m=0,num=0;m<=5;m++){
								if(!players[m].equals("��λ�Դ�")){
									result[num]=m;	//��¼���м�ֵ��������
									num++;
								}
							}
							if(num==1)		//ֻʣһ����
								rank="��һ����  "+players[result[0]]+"    ���֣�  "+Integer.toString(scores[result[0]]);
							else if(num==2){
								if(scores[result[0]]>=scores[result[1]])
									rank="��һ����  "+players[result[0]]+"    ���֣�  "+Integer.toString(scores[result[0]])
										+"\n�ڶ�����  "+players[result[1]]+"    ���֣�  "+Integer.toString(scores[result[1]]);
								else
									rank="��һ����  "+players[result[1]]+"    ���֣�  "+Integer.toString(scores[result[1]])
									+"\n�ڶ�����  "+players[result[0]]+"    ���֣�  "+Integer.toString(scores[result[0]]);
							}
							else{	//���������ϣ�ȡǰ����
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
								
								
								rank="��һ����  "+players[rank1]+"    ���֣�  "+Integer.toString(scores[rank1])
									+"\n�ڶ�����  "+players[rank2]+"    ���֣�  "+Integer.toString(scores[rank2])
									+"\n��������  "+players[rank3]+"    ���֣�  "+Integer.toString(scores[rank3])	;
							}
							
							broadcast(10,rank);
							
							//����״̬��ʼ��
							for(m=0;m<=5;m++){
								scores[m]=0;
								result[m]=-1;
								if(!players[m].equals("��λ�Դ�"))
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
	
	//�����Ǹ��ֹ㲥����
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