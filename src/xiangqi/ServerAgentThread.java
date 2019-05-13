package xiangqi;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.io.*; 
public class ServerAgentThread extends Thread{
	Server father;
	Socket sc;
	DataInputStream din;//���������������������������
	DataOutputStream dout;
	boolean flag = true;
	public ServerAgentThread(Server father,Socket sc){
		this.father=father;
		this.sc = sc;
		try {
			din = new DataInputStream(sc.getInputStream());//��������������
			dout = new DataOutputStream(sc.getOutputStream());//�������������
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void run(){
		while(flag){
			try {
				String msg = din.readUTF().trim();//���ܿͻ��˴�������Ϣ
				if(msg.startsWith("<#NICK_NAME#>")){//�������û�����Ϣ
					this.nick_name(msg);
				}else if(msg.startsWith("<#CLIENT_LEAVE#>")){
					this.client_leave(msg);
				}else if(msg.startsWith("<#TIAO_ZHAN#>")){
					this.tiao_zhan(msg);
				}else if(msg.startsWith("<#TONG_YI#>")){
					this.tong_yi(msg);
				}else if(msg.startsWith("<#BUTONG_YI#>")){
					this.butong_yi(msg);
				}else if(msg.startsWith("<#BUSY#>")){
					this.busy(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void nick_name(String msg){
		try {
			String name=msg.substring(13);//����û����ǳ�
			this.setName(name);
			Vector v=father.onlineList;
			boolean isChongMing=false;
			int size=v.size();
			for(int i=0;i<size;i++){//�����б��鿴�Ƿ�����
				ServerAgentThread tempSat=(ServerAgentThread) v.get(i);
				if(tempSat.getName().equals(name)){
					isChongMing=true;
					break;
				}
			}
			if(isChongMing==true){
				dout.writeUTF("<#NAME_CHONGMING#>");
				din.close();
				dout.close();
				flag=false;//��ֹ�÷����������߳�
			}else{
				v.add(this);//�����߳���ӵ������б�
				father.refreshList();//ˢ�·�����������Ϣ�б�
				String nickListMsg="";
				size=v.size();//��������б��С 
				for(int i=0;i<size;i++){
					ServerAgentThread tempSat = (ServerAgentThread) v.get(i);
					nickListMsg=nickListMsg+"|"+tempSat.getName();
				}
				nickListMsg="<#NICK_LIST#>"+nickListMsg;
				Vector tempv=father.onlineList;
				size=tempv.size();
				for(int i=0;i<size;i++){
					ServerAgentThread satTemp=(ServerAgentThread) tempv.get(i);
					satTemp.dout.writeUTF(nickListMsg);
					if(satTemp!=this){
						satTemp.dout.writeUTF("<#MSG#>"+this.getName()+"������...");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void client_leave(String msg){
		try {
			Vector tempv=father.onlineList;//��������б�
			tempv.remove(this);//�Ƴ����û�
			int size=tempv.size();
			String nl="<#NICK_LIST#>";
			for(int i=0;i<size;i++){//���������б�
				ServerAgentThread satTemp = (ServerAgentThread) tempv.get(i);
				//������ͻ��˷����û�������Ϣ
				satTemp.dout.writeUTF("<#NSG#>"+this.getName()+"�����ˣ�");
				//��֯��Ϣ�������û��б�
				nl=nl+"|"+satTemp.getName();
			}
			for(int i=0;i<size;i++){//�����µ��б���Ϣ���͵������ͻ���
				ServerAgentThread satTemp=(ServerAgentThread) tempv.get(i);
				satTemp.dout.writeUTF(nl);
			}
			this.flag=false;//��ֹ�÷���Ĵ����߳�
			father.refreshList();//���·����������û��б�
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void tiao_zhan(String msg){
		try {
			String name1 = this.getName();//��÷�����ս��Ϣ�û�������
			String name2 = msg.substring(13);//��ñ���ս���û�������
			Vector v = father.onlineList;//��������û����б�
			int size = v.size();//��������û��б�Ĵ�С
			for(int i=0;i<size;i++){
				//���������߳���ִ�������սҵ��
				ServerAgentThread satTemp = (ServerAgentThread) v.get(i);
				if(satTemp.getName().equals(name2)){
					satTemp.dout.writeUTF("<#TIAO_ZHAN#>"+name1);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void tong_yi(String msg){
		try {
			String name = msg.substring(11);
			Vector v = father.onlineList;
			int size = v.size();
			for(int i=0;i<size;i++){
				ServerAgentThread satTemp = (ServerAgentThread) v.get(i);
				if(satTemp.getName().equals(name)){
					satTemp.dout.writeUTF("<#TONG_YI#>");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void butong_yi(String msg){
		try {
			String name = msg.substring(13);
			Vector v = father.onlineList;
			int size = v.size();
			for(int i=0;i<size;i++){
				ServerAgentThread satTemp = (ServerAgentThread) v.get(i);
				if(satTemp.getName().equals(name)){
					satTemp.dout.writeUTF("<#BUTONG_YI#>");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void busy(String msg){
		try {
			String name = msg.substring(8);
			Vector v = father.onlineList;
			int size = v.size();
			for(int i=0;i<size;i++){
				ServerAgentThread satTemp = (ServerAgentThread) v.get(i);
				if(satTemp.getName().equals(name)){
					satTemp.dout.writeUTF("<#BUSY#>");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void move(String msg){
		try {
			String name=msg.substring(8,msg.length()-4);//��ý��շ�������
			Vector v=father.onlineList;//��������û��б�
			int size=v.size();
			for(int i=0;i<size;i++){//�����б��������շ�
				ServerAgentThread satTemp=(ServerAgentThread) v.get(i);
				if(satTemp.getName().equals(name)){//������Ϣת�������շ�
					satTemp.dout.writeUTF(msg);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void renshu(String msg){
		try {
			String name = msg.substring(10);
			Vector v = father.onlineList;
			int size = v.size();
			for(int i=0;i<size;i++){
				ServerAgentThread satTemp = (ServerAgentThread) v.get(i);
				if(satTemp.getName().equals(name)){
					satTemp.dout.writeUTF(msg);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
