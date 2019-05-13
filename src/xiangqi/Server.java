package xiangqi;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.io.*;
public class Server extends JFrame implements ActionListener{
	JLabel jlPort = new JLabel("端口号：");
	JTextField jtfPort = new JTextField("9999");
	JButton jbStart = new JButton("启动");
	JButton jbStop = new JButton("关闭");
	JPanel jps = new JPanel();//创建一个JPanel对象
	JList jlUserOnline = new JList();//创建用于显示当前用户的JList
	JScrollPane jspx = new JScrollPane(jlUserOnline);//将显示当前用户的JList放在JScrollPane中
	JSplitPane jspz = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jspx,jps);//创建一个分割界限
	ServerSocket serverSocket;
	ServerThread st;//声明ServerThread引用
	Vector onlineList = new Vector();//创建存放当前在线用户的Vector对象
	public Server(){
		this.initialComponent();//初始化控件
		this.addListener();//为相应的控件注册事件监听器
		this.initialFrame();//初始化窗体
	}
	private void initialComponent() {
		jps.setLayout(null);//设为空布局
		jlPort.setBounds(20, 20, 50, 20);
		jps.add(jlPort);//添加用于提示输入端口号的标签
		this.jtfPort.setBounds(85,20,50,20);
		jps.add(this.jtfPort);//添加用于输入端口号的文本框
		this.jbStart.setBounds(18,50,60,20);
		jps.add(jbStart);
		this.jbStop.setBounds(85,50,60,20);
		jps.add(jbStop);
		this.jbStop.setEnabled(false);//将关闭按钮设为不可用
	}
	private void addListener() {
		this.jbStart.addActionListener(this);
		this.jbStop.addActionListener(this);
	}
	private void initialFrame() {
		this.setTitle("象棋--服务器端");
		Image image = new ImageIcon("ico.gif").getImage();
		this.setIconImage(image);
		this.add(jspz);
		jspz.setDividerLocation(250);
		jspz.setDividerSize(4);
		this.setBounds(20, 20, 420, 320);
		this.setVisible(true);//设置可见性
		this.addWindowListener(//为窗体关闭事件注册监听器
				new WindowAdapter(){
					public void windowClosing(WindowEvent e){
						if(st==null){//当服务器线程为空时直接退出
							System.exit(0);//退出
							return;
						}
						try {
							Vector v = onlineList;
							int size = v.size();
							for(int i=0;i<size;i++){
								//当不为空时，向在线用户发送离线信息
								ServerAgentThread tempSat=(ServerAgentThread) v.get(i);
								tempSat.dout.writeUTF("<#SERVER_DOWN#>");
								tempSat.flag=false;//终止服务器代理线程
							}
							st.flag = false;
							st=null;
							serverSocket.close();//关闭ServerSocket
							v.clear();//将在线用户列表清空
							refreshList();//刷新列表
						} catch (Exception e2) {
							e2.printStackTrace();
						}
						System.exit(0);//退出
					}
				}
				);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.jbStart){
			//当单击启动按钮时
			this.jbStart_event();
		}
		if(e.getSource()==this.jbStop){
			this.jbStop_event();
		}
	}
	public void jbStart_event(){
		//单击启动按钮后业务处理代码
		int port = 0;
		try {
			//获得用户输入的端口号，并转化为整型
			port = Integer.parseInt(this.jtfPort.getText().trim());
		} catch (Exception e) {
			//端口号不是整型，给出提示信息
			JOptionPane.showMessageDialog(this,"端口号只能是整数","错误",JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(port>65535||port<0){
			//端口号不合法
			JOptionPane.showMessageDialog(this, "端口号只能是0-65535的整数","错误",JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			this.jbStart.setEnabled(false);
			this.jtfPort.setEnabled(false);
			this.jbStop.setEnabled(true);
			serverSocket = new ServerSocket(port);
			st = new ServerThread(this);
			st.start();
			JOptionPane.showMessageDialog(this, "服务器启动成功","提示",JOptionPane.INFORMATION_MESSAGE);
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
				tempSat.flag=false;//关闭服务器代理线程
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
		//更新在线用户列表的业务处理代码
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
