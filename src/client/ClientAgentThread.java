package client;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.io.*;
public class ClientAgentThread extends Thread{
	XiangQi father;
	boolean flag = true;
	DataInputStream din;//������������������
	DataOutputStream dout;
	String tiaoZhanZhe = null;
	public ClientAgentThread(XiangQi father){
		this.father = father;
		try {
			din = new DataInputStream(father.sc.getInputStream());
			dout = new DataOutputStream(father.sc.getOutputStream());
			String name = father.jtfNickName.getText().trim();
			dout.writeUTF("<#NICK_NAME#>"+name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void run(){
		while(flag){
			try {
				String msg = din.readUTF().trim();
				if(msg.startsWith("<#NAME_CHONGMING#>")){
					this.name_chongming();
				}else if(msg.startsWith("<#NICK_LIST#>")){
					this.nick_list(msg);
				}else if(msg.startsWith("<#SERVER_DOWN#>")){
					this.server_down();
				}else if(msg.startsWith("<#TIAO_ZHAN#>")){
					this.tiao_zhan(msg);
				}else if(msg.startsWith("<#TONG_YI#>")){
					this.tong_yi();
				}else if(msg.startsWith("<#BUTONG_YI#>")){
					this.butong_yi();
				}else if(msg.startsWith("<#BUSY#>")){
					this.busy();
				}else if(msg.startsWith("<#MOVE#>")){
					this.move(msg);
				}else if(msg.startsWith("<#RENSHU#>")){
					this.renshu();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private void butong_yi() {
		this.father.caiPan=false;
		this.father.color=0;
		this.father.jtfHost.setEnabled(false);
		this.father.jtfPort.setEnabled(false);
		this.father.jtfNickName.setEnabled(false);
		this.father.jbConnect.setEnabled(false);
		this.father.jbDisconnect.setEnabled(true);
		this.father.jbChanllenge.setEnabled(true);
		this.father.jbYChallenge.setEnabled(false);
		this.father.jbNChallenge.setEnabled(false);
		this.father.jbFail.setEnabled(true);
		JOptionPane.showMessageDialog(this.father, "�Է��ܾ�������ս","��ʾ",JOptionPane.INFORMATION_MESSAGE);
	
	}
	private void renshu() {
		JOptionPane.showMessageDialog(this.father, "�Է�������","��ʾ",JOptionPane.INFORMATION_MESSAGE);
		this.tiaoZhanZhe = null;
		this.father.color = 0;
		this.father.caiPan = false;
		this.father.next();
		this.father.jtfHost.setEnabled(false);
		this.father.jtfPort.setEnabled(false);
		this.father.jtfNickName.setEnabled(false);
		this.father.jbConnect.setEnabled(false);
		this.father.jbDisconnect.setEnabled(false);
		this.father.jbChanllenge.setEnabled(true);
		this.father.jbYChallenge.setEnabled(false);
		this.father.jbNChallenge.setEnabled(false);
		this.father.jbFail.setEnabled(false );
		JOptionPane.showMessageDialog(this.father, "�Է��ܾ�������ս","��ʾ",JOptionPane.INFORMATION_MESSAGE);

		
	}
	private void move(String msg) {
		int length=msg.length();
		int startI = Integer.parseInt(msg.substring(length-4,length-3));//������ӵ�ԭʼλ��
		int startJ = Integer.parseInt(msg.substring(length-3,length-2));
		int endI = Integer.parseInt(msg.substring(length-2,length-1));
		int endJ = Integer.parseInt(msg.substring(length-1));
		this.father.jpz.move(startI, startJ,endI,endJ);
		this.father.caiPan = true;

	}
	private void busy() {
		this.father.caiPan=false;
		this.father.color=0;
		this.father.jtfHost.setEnabled(false);
		this.father.jtfPort.setEnabled(false);
		this.father.jtfNickName.setEnabled(false);
		this.father.jbConnect.setEnabled(false);
		this.father.jbDisconnect.setEnabled(true);
		this.father.jbChanllenge.setEnabled(true);
		this.father.jbYChallenge.setEnabled(false);
		this.father.jbNChallenge.setEnabled(false);
		this.father.jbFail.setEnabled(false);
		JOptionPane.showMessageDialog(this.father, "�Է�æµ��","��ʾ",JOptionPane.INFORMATION_MESSAGE);
		this.tiaoZhanZhe=null;
	
	}
	private void tong_yi() {
		this.father.jtfHost.setEnabled(false);
		this.father.jtfPort.setEnabled(false);
		this.father.jtfNickName.setEnabled(false);
		this.father.jbConnect.setEnabled(false);
		this.father.jbDisconnect.setEnabled(false);
		this.father.jbChanllenge.setEnabled(false);
		this.father.jbYChallenge.setEnabled(false);
		this.father.jbNChallenge.setEnabled(false);
		this.father.jbFail.setEnabled(true);
		JOptionPane.showMessageDialog(this.father, "�Է�����������ս����������","��ʾ",JOptionPane.INFORMATION_MESSAGE);
	}
	private void tiao_zhan(String msg) {
		try {
			String name = msg.substring(13);
			if(this.tiaoZhanZhe==null){
				tiaoZhanZhe = msg.substring(13);
				this.father.jtfHost.setEnabled(false);
				this.father.jtfPort.setEnabled(false);
				this.father.jtfNickName.setEnabled(false);
				this.father.jbConnect.setEnabled(false);
				this.father.jbDisconnect.setEnabled(!true);
				this.father.jbChanllenge.setEnabled(!true);
				this.father.jbYChallenge.setEnabled(!false);
				this.father.jbNChallenge.setEnabled(!false);
				this.father.jbFail.setEnabled(false);
				JOptionPane.showMessageDialog(this.father, tiaoZhanZhe+"������ս������","��ʾ",JOptionPane.INFORMATION_MESSAGE);
			}else{
				this.dout.writeUTF("<#BUSY#>"+name);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	private void server_down() {
		this.father.jtfHost.setEnabled(!false);
		this.father.jtfPort.setEnabled(!false);
		this.father.jtfNickName.setEnabled(!false);
		this.father.jbConnect.setEnabled(!false);
		this.father.jbDisconnect.setEnabled(!true);
		this.father.jbChanllenge.setEnabled(!true);
		this.father.jbYChallenge.setEnabled(false);
		this.father.jbNChallenge.setEnabled(false);
		this.father.jbFail.setEnabled(false);
		this.flag = false;
	}
	private void nick_list(String msg) {
		// TODO Auto-generated method stub
		String s = msg.substring(13);
		String[] na = s.split("\\|");
		Vector v = new Vector();
		for(int i=0;i<na.length;i++){
			if(na[i].trim().length()!=0&&(!na[i].trim().equals(father.jtfNickName.getText().trim()))){
				v.add(na[i]);//���ǳ�ȫ����ӵ�Vector��
			}
		}
		father.jcbNickList.setModel(new DefaultComboBoxModel());
	}
	private void name_chongming() {
		try {
			JOptionPane.showMessageDialog(this.father,"����������Ѿ�����","����",JOptionPane.ERROR_MESSAGE);
			din.close();
			dout.close();
			this.father.jtfHost.setEnabled(!false);
			this.father.jtfPort.setEnabled(!false);
			this.father.jtfNickName.setEnabled(!false);
			this.father.jbConnect.setEnabled(!false);
			this.father.jbDisconnect.setEnabled(!true);
			this.father.jbChanllenge.setEnabled(!true);
			this.father.jbYChallenge.setEnabled(false);
			this.father.jbNChallenge.setEnabled(false);
			this.father.jbFail.setEnabled(false);
			father.sc.close();
			father.sc = null;
			father.cat = null;//******
			flag = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
