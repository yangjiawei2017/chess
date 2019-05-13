package xiangqi;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.io.*;
public class Server extends JFrame implements ActionListener{
	JLabel jlPort = new JLabel("�˿ںţ�");
	JTextField jtfPort = new JTextField("9999");
	JButton jbStart = new JButton("����");
	JButton jbStop = new JButton("�ر�");
	JPanel jps = new JPanel();//����һ��JPanel����
	JList jlUserOnline = new JList();//����������ʾ��ǰ�û���JList
	JScrollPane jspx = new JScrollPane(jlUserOnline);//����ʾ��ǰ�û���JList����JScrollPane��
	JSplitPane jspz = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jspx,jps);//����һ���ָ����
	ServerSocket serverSocket;
	ServerThread st;//����ServerThread����
	Vector onlineList = new Vector();//������ŵ�ǰ�����û���Vector����
	public Server(){
		this.initialComponent();//��ʼ���ؼ�
		this.addListener();//Ϊ��Ӧ�Ŀؼ�ע���¼�������
		this.initialFrame();//��ʼ������
	}
	private void initialComponent() {
		jps.setLayout(null);//��Ϊ�ղ���
		jlPort.setBounds(20, 20, 50, 20);
		jps.add(jlPort);//���������ʾ����˿ںŵı�ǩ
		this.jtfPort.setBounds(85,20,50,20);
		jps.add(this.jtfPort);//�����������˿ںŵ��ı���
		this.jbStart.setBounds(18,50,60,20);
		jps.add(jbStart);
		this.jbStop.setBounds(85,50,60,20);
		jps.add(jbStop);
		this.jbStop.setEnabled(false);//���رհ�ť��Ϊ������
	}
	private void addListener() {
		this.jbStart.addActionListener(this);
		this.jbStop.addActionListener(this);
	}
	private void initialFrame() {
		this.setTitle("����--��������");
		Image image = new ImageIcon("ico.gif").getImage();
		this.setIconImage(image);
		this.add(jspz);
		jspz.setDividerLocation(250);
		jspz.setDividerSize(4);
		this.setBounds(20, 20, 420, 320);
		this.setVisible(true);//���ÿɼ���
		this.addWindowListener(//Ϊ����ر��¼�ע�������
				new WindowAdapter(){
					public void windowClosing(WindowEvent e){
						if(st==null){//���������߳�Ϊ��ʱֱ���˳�
							System.exit(0);//�˳�
							return;
						}
						try {
							Vector v = onlineList;
							int size = v.size();
							for(int i=0;i<size;i++){
								//����Ϊ��ʱ���������û�����������Ϣ
								ServerAgentThread tempSat=(ServerAgentThread) v.get(i);
								tempSat.dout.writeUTF("<#SERVER_DOWN#>");
								tempSat.flag=false;//��ֹ�����������߳�
							}
							st.flag = false;
							st=null;
							serverSocket.close();//�ر�ServerSocket
							v.clear();//�������û��б����
							refreshList();//ˢ���б�
						} catch (Exception e2) {
							e2.printStackTrace();
						}
						System.exit(0);//�˳�
					}
				}
				);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.jbStart){
			//������������ťʱ
			this.jbStart_event();
		}
		if(e.getSource()==this.jbStop){
			this.jbStop_event();
		}
	}
	public void jbStart_event(){
		//����������ť��ҵ�������
		int port = 0;
		try {
			//����û�����Ķ˿ںţ���ת��Ϊ����
			port = Integer.parseInt(this.jtfPort.getText().trim());
		} catch (Exception e) {
			//�˿ںŲ������ͣ�������ʾ��Ϣ
			JOptionPane.showMessageDialog(this,"�˿ں�ֻ��������","����",JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(port>65535||port<0){
			//�˿ںŲ��Ϸ�
			JOptionPane.showMessageDialog(this, "�˿ں�ֻ����0-65535������","����",JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			this.jbStart.setEnabled(false);
			this.jtfPort.setEnabled(false);
			this.jbStop.setEnabled(true);
			serverSocket = new ServerSocket(port);
			st = new ServerThread(this);
			st.start();
			JOptionPane.showMessageDialog(this, "�����������ɹ�","��ʾ",JOptionPane.INFORMATION_MESSAGE);
			this.jbStart.setEnabled(true);
			this.jtfPort.setEnabled(true);
			this.jbStop.setEnabled(false);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	public void jbStop_event(){
		try {
			Vector v = onlineList;
			int size = v.size();
			for(int i=0;i<size;i++){
				ServerAgentThread tempSat = (ServerAgentThread) v.get(i);
				tempSat.dout.writeUTF("<#SERVER_DOWN#>");
				tempSat.flag=false;//�رշ����������߳�
			}
			st.flag=false;
			st=null;
			serverSocket.close();
			v.clear();
			refreshList();
			this.jbStart.setEnabled(true);
			this.jtfPort.setEnabled(true);
			this.jbStop.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void refreshList(){
		//���������û��б��ҵ�������
		Vector v = new Vector();
		int size=this.onlineList.size();
		for(int i=0;i<size;i++){
			ServerAgentThread tempSat = (ServerAgentThread) v.get(i);
			String temps = tempSat.sc.getInetAddress().toString();
			temps = temps+"|"+tempSat.getName();
			v.add(temps);
		}
		this.jlUserOnline.setListData(v);
	}
	public static void main(String[] args) {
		new Server();
	}
}
