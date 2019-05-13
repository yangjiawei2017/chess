package xiangqi;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.io.*;
public class ServerThread extends Thread{
	Server father;
	ServerSocket ss;
	boolean flag = true;
	public ServerThread(Server father){
		this.father = father;
		ss=father.serverSocket;
	}
	public void run(){
		while(flag){
			try {
				Socket sc = ss.accept();
				//ServerAgentThread sat = new ServerAgentThread();
				//sat.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
