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
	DataInputStream din;//声明数据输入流与输出流的引用
	DataOutputStream dout;
	boolean flag = true;
	public ServerAgentThread(Server father,Socket sc){
		this.father=father;
		this.sc = sc;
		try {
			din = new DataInputStream(sc.getInputStream());//创建数据输入流
			dout = new DataOutputStream(sc.getOutputStream());//创建数据输出流
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void run(){
		while(flag){
			try {
				String msg = din.readUTF().trim();//接受客户端传来的信息
				if(msg.startsWith("<#NICK_NAME#>")){//接受新用户的信息
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
			String name=msg.substring(13);//获得用户的昵称
			this.setName(name);
			Vector v=father.onlineList;
			boolean isChongMing=false;
			int size=v.size();
			for(int i=0;i<size;i++){//遍历列表，查看是否重名
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
				flag=false;//终止该服务器代理线程
			}else{
				v.add(this);//将该线程添加到在线列表
				father.refreshList();//刷新服务器在线信息列表
				String nickListMsg="";
				size=v.size();//获得在线列表大小 
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
						satTemp.dout.writeUTF("<#MSG#>"+this.getName()+"上线了...");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void client_leave(String msg){
		try {
			Vector tempv=father.onlineList;//获得在线列表
			tempv.remove(this);//移除该用户
			int size=tempv.size();
			String nl="<#NICK_LIST#>";
			for(int i=0;i<size;i++){//遍历在线列表
				ServerAgentThread satTemp = (ServerAgentThread) tempv.get(i);
				//向各个客户端发送用户离线信息
				satTemp.dout.writeUTF("<#NSG#>"+this.getName()+"离线了！");
				//组织信息的在线用户列表
				nl=nl+"|"+satTemp.getName();
			}
			for(int i=0;i<size;i++){//将最新的列表信息发送到各个客户端
				ServerAgentThread satTemp=(ServerAgentThread) tempv.get(i);
				satTemp.dout.writeUTF(nl);
			}
			this.flag=false;//终止该服务的代理线程
			father.refreshList();//更新服务器在线用户列表
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void tiao_zhan(String msg){
		try {
			String name1 = this.getName();//获得发出挑战信息用户的名字
			String name2 = msg.substring(13);//获得被挑战的用户的名字
			Vector v = father.onlineList;//获得在线用户的列表
			int size = v.size();//获得在线用户列表的大小
			for(int i=0;i<size;i++){
				//单独分配线程来执行这个挑战业务
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
			String name=msg.substring(8,msg.length()-4);//获得接收方的名字
			Vector v=father.onlineList;//获得在线用户列表
			int size=v.size();
			for(int i=0;i<size;i++){//遍历列表，搜索接收方
				ServerAgentThread satTemp=(ServerAgentThread) v.get(i);
				if(satTemp.getName().equals(name)){//将该信息转发给接收方
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
