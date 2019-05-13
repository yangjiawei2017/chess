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
	JLabel jlHost = new JLabel("������");//������ʾ�����������ı�ǩ
	JLabel jlPort = new JLabel("�˿ں�");//������ʾ�����������ı�ǩ
	JLabel jlNickName = new JLabel("��  ��");//������ʾ�����ǳƵı�ǩ
	JTextField jtfHost = new JTextField("localhost");//�����������������ı���Ĭ��ֵ�ǡ�localhost��
	JTextField jtfPort = new JTextField("9999");//��������˿ںŵ��ı���Ĭ��ֵ��9999
	JTextField jtfNickName = new JTextField("play1");//
	JButton jbConnect = new JButton("����");
	JButton jbDisconnect = new JButton("�Ͽ�");
	JButton jbFail = new JButton("����");
	JButton jbChanllenge = new JButton("��ս");
	JComboBox jcbNickList = new JComboBox();//������ŵ�ǰ�û��������б��
	JButton jbYChallenge = new JButton("������ս");
	JButton jbNChallenge = new JButton("�ܾ���ս");
	int width=60;//������������֮��ľ���
	QiZi[][] qizi = new QiZi[9][10];
	JPanel jpz = new JPanel();//����һ��JPanel��ʱ�������
	JPanel jpy = new JPanel();//����һ��JPanel
	JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jpz,jpy);//����һ��JSplitPane
	boolean caiPan = false;//�ɷ�����ı�־λ
	int color = 0;//0������죬1�������
	Socket sc;
	public XiangQi(){
		this.initialComponent();//��ʼ���ؼ�
		this.addListener();//Ϊ��Ӧ�Ŀؼ����ע���¼�
		this.initialState();
		this.initialQiZi();//
		this.initialFrame();//��ʼ������
	}
	private void initialQiZi() {
		qizi[0][0] = new QiZi(color1,"��",0,0);
		qizi[1][0] = new QiZi(color1,"��",1,0);
		qizi[2][0] = new QiZi(color1,"��",2,0);
		qizi[3][0] = new QiZi(color1,"ʿ",3,0);
		qizi[4][0] = new QiZi(color1,"˧",4,0);
		qizi[5][0] = new QiZi(color1,"ʿ",5,0);
		qizi[6][0] = new QiZi(color1,"��",6,0);
		qizi[7][0] = new QiZi(color1,"��",7,0);
		qizi[8][0] = new QiZi(color1,"��",8,0);
		qizi[1][2] = new QiZi(color1,"��",1,2);
		qizi[7][2] = new QiZi(color1,"��",7,2);
		qizi[0][2] = new QiZi(color1,"��",0,0);
		qizi[2][3] = new QiZi(color1,"��",2,3);
		qizi[4][3] = new QiZi(color1,"��",4,3);
		qizi[6][3] = new QiZi(color1,"��",6,3);
		qizi[8][3] = new QiZi(color1,"��",8,3);
		qizi[0][9] = new QiZi(color1,"��",0,9);
		qizi[1][9] = new QiZi(color1,"��",1,9);
		qizi[2][9] = new QiZi(color1,"��",2,9);
		qizi[3][9] = new QiZi(color1,"ʿ",3,9);
		qizi[4][9] = new QiZi(color1,"��",4,9);
		qizi[5][9] = new QiZi(color1,"ʿ",5,9);
		qizi[6][9] = new QiZi(color1,"��",6,9);
		qizi[7][9] = new QiZi(color1,"��",7,9);
		qizi[8][9] = new QiZi(color1,"��",8,9);
		qizi[1][7] = new QiZi(color1,"��",1,7);
		qizi[7][7] = new QiZi(color1,"��",7,7);
		qizi[0][6] = new QiZi(color1,"��",0,6);
		qizi[2][6] = new QiZi(color1,"��",2,6);
		qizi[4][6] = new QiZi(color1,"��",4,6);
		qizi[6][6] = new QiZi(color1,"��",6,6);
		qizi[8][6] = new QiZi(color1,"��",8,6);
	}
	private void initialFrame() {
		this.setTitle("�й�����");//���ô���ͻ���
		Image image = new ImageIcon("ico.gif").getImage();
		this.setIconImage(image);//����ͼ��
		this.add(this.jsp);//���JsplitPane
		jsp.setDividerLocation(730);//���÷ָ���λ�ü����
		jsp.setDividerSize(4);
		this.setBounds(30,30,930,730);
		this.setVisible(true);
		this.addWindowListener(
				new WindowAdapter(){
					public void windowClosing(WindowEvent e){
						System.exit(0);//�˳�
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
		this.jbConnect.addActionListener(this);//Ϊ���Ӱ�ťע���������¼�
		this.jbDisconnect.addActionListener(this);//Ϊ�Ͽ���ťע���¼�����
		this.jbChanllenge.addActionListener(this);
		this.jbFail.addActionListener(this);
		this.jbYChallenge.addActionListener(this);
		this.jbNChallenge.addActionListener(this);
	}
	private void initialComponent() {
		jpy.setLayout(null);//λjpy��Ϊ�ղ���
		this.jlHost.setBounds(10, 10, 50, 20);
		jpy.add(this.jlHost);//�����������ǩ
		this.jtfHost.setBounds(70,10,80,20);
		jpy.add(this.jlHost);//��������������������ı���
		this.jtfHost.setBounds(10,40,50,20);
		jpy.add(this.jlPort);//��Ӷ˿ںű�ǩ
		this.jtfPort.setBounds(70,40,80,20);
		jpy.add(this.jtfPort);
		this.jlNickName.setBounds(10,70,50,20);
		jpy.add(this.jlNickName);//����ǳƱ�ǩ
		this.jtfNickName.setBounds(70,70,80,20);
		jpy.add(this.jtfNickName);//������������ǳƵ��ı���
		this.jbConnect.setBounds(10,100,80,20);
		jpy.add(this.jbConnect);
		this.jbDisconnect.setBounds(100,100,80,20);
		jpy.add(this.jbDisconnect);
		this.jcbNickList.setBounds(20,130,130,20);
		jpy.add(this.jcbNickList);//���������ʾ��ǰ�û��������б��
		this.jbChanllenge.setBounds(10,160,80,20);
		jpy.add(this.jbChanllenge);//�����ս��ť
		this.jbFail.setBounds(100,160,80,20);
		jpy.add(this.jbFail);//������䰴ť
		this.jbYChallenge.setBounds(5,190,86,20);
		jpy.add(this.jbYChallenge);
		this.jbNChallenge.setBounds(100,190,86,20);
		jpy.add(this.jbNChallenge);
		
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.jbConnect){
			this.jbConnect_event();//�������Ӱ�ť
		}
		if(e.getSource()==this.jbDisconnect){
			this.jbDisconnect_event();//�����Ͽ���ť
		}
		if(e.getSource()==this.jbChanllenge){
			this.jbChanllenge_event();//�������Ӱ�ť
		}
		if(e.getSource()==this.jbYChallenge){
			this.jbYChanllenge_event();//�������Ӱ�ť
		}
		if(e.getSource()==this.jbNChallenge){
			this.jbNChanllenge_event();//�������Ӱ�ť
		}
		if(e.getSource()==this.jbFail){
			this.jbFail_event();//�������Ӱ�ť
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
			JOptionPane.showMessageDialog(this, "��ѡ��Է�����","����",JOptionPane.ERROR_MESSAGE);
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
				JOptionPane.showMessageDialog(this, "�������ս����ȴ��ظ�...","��ʾ",JOptionPane.INFORMATION_MESSAGE);
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
			JOptionPane.showMessageDialog(this, "�˿ں�ֻ��������","����",JOptionPane.ERROR_MESSAGE);
			return;
		}
		String name = this.jtfNickName.getText().trim();
		if(name.length()==0){
			JOptionPane.showMessageDialog(this, "�����������Ϊ��","����",JOptionPane.ERROR_MESSAGE);;
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
			JOptionPane.showMessageDialog(this, "�����ӵ�������","��ʾ",JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "���ӷ�����ʧ��","����",JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	public static void main(String[] args) {
		System.out.println("000");
		new XiangQi();
		System.out.println("111");
	}
	
}
