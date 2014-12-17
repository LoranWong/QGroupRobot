package com.jackwong025.qgrouprobot.Controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.sql.RowSetInternal;
import javax.sql.rowset.WebRowSet;
import javax.sql.rowset.spi.XmlReader;
import javax.sql.rowset.spi.XmlWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import com.jackwong025.qgrouprobot.Model.User;
import com.jackwong025.qgrouprobot.View.Constant;

public class DAO {

	public static ArrayList<User> getAllUsers() {

		ArrayList<User> users = new ArrayList<User>();
		// step 1: 获得dom解析器工厂（工作的作用是用于创建具体的解析器）
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// step 2:获得具体的dom解析器
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			// step3: 解析一个xml文档，获得Document对象（根结点）
			Document document = db.parse(new File(Constant.DATAFILEPATH));
			NodeList list = document.getElementsByTagName("user");
			for (int i = 0; i < list.getLength(); i++) {
				User user = new User();
				Element element = (Element) list.item(i);
				String content = element.getElementsByTagName("uin").item(0)
						.getFirstChild().getNodeValue();
				user.setUin(content);
				content = element.getElementsByTagName("name").item(0)
						.getFirstChild().getNodeValue();
				user.setName(content);
				content = element.getElementsByTagName("rp").item(0)
						.getFirstChild().getNodeValue();
				user.setRp(content);
				users.add(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}

	
	
	public static boolean setValue( String uin, String key, String value, boolean append) {
		
		ArrayList<User> users = getAllUsers();
		boolean result = false;
		//构建XML
		org.dom4j.Element root = DocumentHelper.createElement("users");  
		org.dom4j.Document document = DocumentHelper.createDocument(root);  
  
		//先查找属性
		for (int i = 0; i < users.size(); i++) {
			
			org.dom4j.Element userEle = root.addElement("user");
			userEle.addElement("uin").setText(users.get(i).getUin());
			
			
			if(users.get(i).getUin().equals(uin)){
				result=true;
				//判断是直接设置还是进行加减
				if(append){
					String newRp=String.valueOf((Integer.parseInt((users.get(i).get(key)))+Integer.parseInt(value)));
					userEle.addElement(key).setText(newRp);
				}else{
					userEle.addElement(key).setText(value);
				}

				if(!key.equals("name")) userEle.addElement("name").setText(users.get(i).getName());
				if(!key.equals("rp")) userEle.addElement("rp").setText(users.get(i).getRp());
				continue;
			}
			userEle.addElement("name").setText(users.get(i).getName());
			userEle.addElement("rp").setText(users.get(i).getRp());
		}
		if (result) {
			XMLWriter xmlWriter;
			try {
				xmlWriter = new XMLWriter(new FileWriter(Constant.DATAFILEPATH));
				xmlWriter.write(document);  
				xmlWriter.close(); 
			} catch (Exception e) {
				e.printStackTrace();
			}  	
		}
		if(result) RobotController.users = getAllUsersRanked();
		return result;
	}
	
	public static boolean addUser(String uin,String name) {
		ArrayList<User> users = getAllUsers();
		//构建XML
		org.dom4j.Element root = DocumentHelper.createElement("users");  
		org.dom4j.Document document = DocumentHelper.createDocument(root);  
		//先增加之前的用户
		for (int i = 0; i < users.size(); i++) {
			org.dom4j.Element userEle = root.addElement("user");
			userEle.addElement("uin").setText(users.get(i).getUin());
			userEle.addElement("name").setText(users.get(i).getName());
			userEle.addElement("rp").setText(users.get(i).getRp());
		}
		//新增用户
		org.dom4j.Element userEle = root.addElement("user");
		userEle.addElement("uin").setText(uin);
		userEle.addElement("name").setText(name);
		userEle.addElement("rp").setText("0");

		XMLWriter xmlWriter;
		try {
			xmlWriter = new XMLWriter(new FileWriter(Constant.DATAFILEPATH));
			xmlWriter.write(document);  
			xmlWriter.close(); 
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		RobotController.users = getAllUsersRanked();
		return true;
	}

	public static ArrayList<User> getAllUsersRanked() {
		ArrayList<User> users = getAllUsers();
		//实现排序比较接口
		Comparator<User> comparator = new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				return Integer.parseInt(o2.getRp())-Integer.parseInt(o1.getRp());
			}
			
		};
		
		Collections.sort(users,comparator);
		
		return users;
	}
	
}
