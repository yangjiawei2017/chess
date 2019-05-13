package client;
/**
 * 
 */
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.io.*;
public class XiangQi extends JFrame implements ActionListener{
	public static final Color bgColor=new Color(245,250,160);
	public static final Color focusbg=new Color(242,242,242);
	public static final Color focuschar=new Color(96,95,91);
	public static final Color color1=new Color(249,183,173);
	public static final Color color2=Color.white;
	JLabel jlHost = new JLabel("主机名");//创建提示输入主机名的标签
	JLabel jlPort = new JLabel("端口号");//创建提示输入主机名的标签
	JLabel jlNickName = new JLabel("昵  称");//创建提示输入昵称的标签
	JTextField jtfHost = new JTextField("localhost");//创建输入主句名的文本框，默认值是“localhost”
	JTextField jtfPort = new JTextField("9999");//创建输入端口号的文本框，默认值是9999
	JTextField jtfNickName = new JTextField("play1");//
	JButton jbConnect = new JButton("连接");
	JButton jbDisconnect = new JButton("断开");
	JButton jbFail = new JButton("认输");
	JButton jbChanllenge = new JButton("挑战");
	JComboBox jcbNickList = new JComboBox();//创建存放当前用户的下拉列表框
	JButton jbYChallenge = new JButton("接受挑战");
	JButton jbNChallenge = new JButton("拒绝挑战");
	int width=60;//设置棋盘两线之间的距离
	QiZi[][] qizi = new QiZi[9][10];
	JPanel jpz = new JPanel();//创建一个JPanel暂时替代棋盘
	JPanel jpy = new JPanel();//创建一个JPanel
	JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jpz,jpy);//创建一个JSplitPane
	boolean caiPan = false;//可否走棋的标志位
	int color = 0;//0代表红旗，1代表白旗
	Socket sc;
	public XiangQi(){
		this.initialComponent();//初始化控件
		this.addListener();//为相应的控件添加注册事件
		this.initialState();
		this.initialQiZi();//
		this.initialFrame();//初始化窗体
	}
	private void initialQiZi() {
		qizi[0][0] = new QiZi(color1,"车",0,0);
		qizi[1][0] = new QiZi(color1,"马",1,0);
		qizi[2][0] = new QiZi(color1,"相",2,0);
		qizi[3][0] = new QiZi(color1,"士",3,0);
		qizi[4][0] = new QiZi(color1,"帅",4,0);
		qizi[5][0] = new QiZi(color1,"士",5,0);
		qizi[6][0] = new QiZi(color1,"相",6,0);
		qizi[7][0] = new QiZi(color1,"马",7,0);
		qizi[8][0] = new QiZi(color1,"车",8,0);
		qizi[1][2] = new QiZi(color1,"炮",1,2);
		qizi[7][2] = new QiZi(color1,"炮",7,2);
		qizi[0][2] = new QiZi(color1,"兵",0,0);
		qizi[2][3] = new QiZi(color1,"兵",2,3);
		qizi[4][3] = new QiZi(color1,"兵",4,3);
		qizi[6][3] = new QiZi(color1,"兵",6,3);
		qizi[8][3] = new QiZi(color1,"兵",8,3);
		qizi[0][9] = new QiZi(color1,"车",0,9);
		qizi[1][9] = new QiZi(color1,"马",1,9);
		qizi[2][9] = new QiZi(color1,"象",2,9);
		qizi[3][9] = new QiZi(color1,"士",3,9);
		qizi[4][9] = new QiZi(color1,"将",4,9);
		qizi[5][9] = new QiZi(color1,"士",5,9);
		qizi[6][9] = new QiZi(color1,"象",6,9);
		qizi[7][9] = new QiZi(color1,"马",7,9);
		qizi[8][9] = new QiZi(color1,"车",8,9);
		qizi[1][7] = new QiZi(color1,"炮",1,7);
		qizi[7][7] = new QiZi(color1,"炮",7,7);
		qizi[0][6] = new QiZi(color1,"兵",0,6);
		qizi[2][6] = new QiZi(color1,"兵",2,6);
		qizi[4][6] = new QiZi(color1,"兵",4,6);
		qizi[6][6] = new QiZi(color1,"兵",6,6);
		qizi[8][6] = new QiZi(color1,"兵",8,6);
	}
	private void initialFrame() {
		this.setTitle("中国象棋");//设置窗体客户端
		Image image = new ImageIcon("ico.gif").getImage();
		this.setIconImage(image);//设置图标
		this.add(this.jsp);//添加JsplitPane
		jsp.setDividerLocation(730);//设置分割线位置及宽度
		jsp.setDividerSize(4);
		this.setBounds(30,30,930,730);
		this.setVisible(true);
		this.addWindowListener(
				new WindowAdapter(){
					public void windowClosing(WindowEvent e){
						System.exit(0);//退出
					}
				}
				);
	}
	private void initialState() {
		this.jbDisconnect.setEnabled(false);
		this.jbChanllenge.setEnabled(false);
		this.jbYChallenge.setEnabled(false);
		this.jbNChallenge.setEnabled(false);
		this.jbFail.setEnabled(false);
	}
	private void addListener() {
		this.jbConnect.addActionListener(this);//为连接按钮注册点击监听事件
		this.jbDisconnect.addActionListener(this);//为断开按钮注册事件监听
		this.jbChanllenge.addActionListener(this);
		this.jbFail.addActionListener(this);
		this.jbYChallenge.addActionListener(this);
		this.jbNChallenge.addActionListener(this);
	}
	private void initialComponent() {
		jpy.setLayout(null);//位jpy设为空布局
		this.jlHost.setBounds(10, 10, 50, 20);
		jpy.add(this.jlHost);//添加主机名标签
		this.jtfHost.setBounds(70,10,80,20);
		jpy.add(this.jlHost);//添加用于输入主机名的文本框
		this.jtfHost.setBounds(10,40,50,20);
		jpy.add(this.jlPort);//添加端口号标签
		this.jtfPort.setBounds(70,40,80,20);
		jpy.add(this.jtfPort);
		this.jlNickName.setBounds(10,70,50,20);
		jpy.add(this.jlNickName);//添加昵称标签
		this.jtfNickName.setBounds(70,70,80,20);
		jpy.add(this.jtfNickName);//添加用于输入昵称的文本框
		this.jbConnect.setBounds(10,100,80,20);
		jpy.add(this.jbConnect);
		this.jbDisconnect.setBounds(100,100,80,20);
		jpy.add(this.jbDisconnect);
		this.jcbNickList.setBounds(20,130,130,20);
		jpy.add(this.jcbNickList);//添加用于显示当前用户的下拉列表框
		this.jbChanllenge.setBounds(10,160,80,20);
		jpy.add(this.jbChanllenge);//添加挑战按钮
		this.jbFail.setBounds(100,160,80,20);
		jpy.add(this.jbFail);//添加认输按钮
		this.jbYChallenge.setBounds(5,190,86,20);
		jpy.add(this.jbYChallenge);
		this.jbNChallenge.setBounds(100,190,86,20);
		jpy.add(this.jbNChallenge);
		
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.jbConnect){
			this.jbConnect_event();//单击连接按钮
		}
		if(e.getSource()==this.jbDisconnect){
			this.jbDisconnect_event();//单击断开按钮
		}
		if(e.getSource()==this.jbChanllenge){
			this.jbChanllenge_event();//单击连接按钮
		}
		if(e.getSource()==this.jbYChallenge){
			this.jbYChanllenge_event();//单击连接按钮
		}
		if(e.getSource()==this.jbNChallenge){
			this.jbNChanllenge_event();//单击连接按钮
		}
		if(e.getSource()==this.jbFail){
			this.jbFail_event();//单击连接按钮
		}
	}
	private void jbDisconnect_event() {
		try {
			this.jtfHost.setEnabled(false);
			this.jtfPort.setEnabled(false);
			this.jtfNickName.setEnabled(false);
			this.jbConnect.setEnabled(false);
			this.jbDisconnect.setEnabled(true);
			this.jbChanllenge.setEnabled(true);
			this.jbYChallenge.setEnabled(false);
			this.jbNChallenge.setEnabled(false);
			this.jbFail.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void jbFail_event() {
		try {
			this.color = 0;
			this.caiPan = false;
			this.next();
			this.jtfHost.setEnabled(false);
			this.jtfNickName.setEnabled(false);
			this.jbConnect.setEnabled(false);
			this.jbDisconnect.setEnabled(true);
			this.jbYChallenge.setEnabled(false);
			this.jbChanllenge.setEnabled(true);
			this.jbNChallenge.setEnabled(false);
			this.jbFail.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void next(){
		for(int i=0;i<9;i++){
			for(int j=0;j<10;j++){
				this.qizi[i][j]=null;
			}
		}
		this.caiPan = false;
		this.initialQiZi();
		this.repaint();
	}
	private void jbNChanllenge_event() {
		try {
			this.jtfHost.setEnabled(false);
			this.jtfPort.setEnabled(false);
			this.jtfNickName.setEnabled(false);
			this.jbConnect.setEnabled(false);
			this.jbDisconnect.setEnabled(!true);
			this.jbChanllenge.setEnabled(!true);
			this.jbYChallenge.setEnabled(false);
			this.jbNChallenge.setEnabled(false);
			this.jbFail.setEnabled(!false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void jbYChanllenge_event() {
		try {
			this.caiPan = false;
			this.color = 1;
			this.jtfHost.setEnabled(false);
			this.jtfPort.setEnabled(false);
			this.jtfNickName.setEnabled(false);
			this.jbConnect.setEnabled(!true);
			this.jbDisconnect.setEnabled(!true);
			this.jbYChallenge.setEnabled(false);
			this.jbNChallenge.setEnabled(false);
			this.jbFail.setEnabled(!false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void jbChanllenge_event() {
		Object o = this.jcbNickList.getSelectedItem();
		if(o==null||((String)o).equals("")){
			JOptionPane.showMessageDialog(this, "请选择对方名字","错误",JOptionPane.ERROR_MESSAGE);
		}else{
			String name2 = (String) this.jcbNickList.getSelectedItem();
			try {
				this.jtfHost.setEnabled(false);
				this.jtfPort.setEnabled(false);
				this.jtfNickName.setEnabled(false);
				this.jbConnect.setEnabled(false);
				this.jbDisconnect.setEnabled(!true);
				this.jbChanllenge.setEnabled(!true);
				this.jbYChallenge.setEnabled(false);
				this.jbNChallenge.setEnabled(false);
				this.jbFail.setEnabled(false);
				this.caiPan = true;
				this.color = 0;
				JOptionPane.showMessageDialog(this, "已提出挑战，请等待回复...","提示",JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private void jbConnect_event() {
		int port=0;
		try {
			port = Integer.parseInt(this.jtfPort.getText().trim());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "端口号只能是整数","错误",JOptionPane.ERROR_MESSAGE);
			return;
		}
		String name = this.jtfNickName.getText().trim();
		if(name.length()==0){
			JOptionPane.showMessageDialog(this, "玩家姓名不能为空","错误",JOptionPane.ERROR_MESSAGE);;
			return;
		}
		try {
			//*************************
			sc = new Socket(this.jtfHost.getText().trim(),port);
			this.jtfHost.setEnabled(false);
			this.jtfPort.setEnabled(false);
			this.jtfNickName.setEnabled(false);
			this.jbConnect.setEnabled(false);
			this.jbDisconnect.setEnabled(true);
			this.jbYChallenge.setEnabled(true);
			this.jbNChallenge.setEnabled(false);
			this.jbFail.setEnabled(false);
			JOptionPane.showMessageDialog(this, "已连接到服务器","提示",JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "连接服务器失败","错误",JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	public static void main(String[] args) {
		System.out.println("000");
		new XiangQi();
		System.out.println("111");
	}
	
}
