package com.jackwong025.qgrouprobot.Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.naming.spi.DirStateFactory.Result;

import iqq.im.QQActionListener;
import iqq.im.QQClient;
import iqq.im.QQException;
import iqq.im.WebQQClient;
import iqq.im.action.SendMsgAction;
import iqq.im.actor.ThreadActorDispatcher;
import iqq.im.bean.QQBuddy;
import iqq.im.bean.QQGroup;
import iqq.im.bean.QQMsg;
import iqq.im.bean.QQStatus;
import iqq.im.bean.QQUser;
import iqq.im.bean.content.CFaceItem;
import iqq.im.bean.content.ContentItem;
import iqq.im.bean.content.FaceItem;
import iqq.im.bean.content.OffPicItem;
import iqq.im.bean.content.TextItem;
import iqq.im.core.QQConstants;
import iqq.im.event.QQActionEvent;
import iqq.im.event.QQNotifyEvent;
import iqq.im.event.QQNotifyEventArgs;
import iqq.im.event.QQNotifyHandler;
import iqq.im.event.QQNotifyHandlerProxy;

import com.jackwong025.qgrouprobot.Model.User;
import com.jackwong025.qgrouprobot.View.Constant;
import com.jackwong025.qgrouprobot.View.MainFrame;

public class RobotController {
	private QQClient client;
	private String qq_number="";
	private String qgroup_number="";
	MainFrame frame = null;
	public static ArrayList<User> users;
	public RobotController(MainFrame frame,String qq_number,String password, String qgroup_number) {
		this.frame = frame;
		this.qq_number=qq_number;
		this.qgroup_number=qgroup_number;
		//登录操作
		 client = new WebQQClient(qq_number, password,
	                new QQNotifyHandlerProxy(this), new ThreadActorDispatcher());
		
		//设置标头
	        String ua = "Mozilla/5.0 (@os.name; @os.version; @os.arch)"
	                + " AppleWebKit/537.36 (KHTML, like Gecko)"
	                + " @appName Safari/537.36";
	        ua = ua.replaceAll("@appName", QQConstants.USER_AGENT);
	        ua = ua.replaceAll("@os.name", System.getProperty("os.name"));
	        ua = ua.replaceAll("@os.version", System.getProperty("os.version"));
	        ua = ua.replaceAll("@os.arch", System.getProperty("os.arch"));
	        client.setHttpUserAgent(ua);

		QQActionListener loginListener = new QQActionListener() {
			@Override
			public void onActionEvent(QQActionEvent event) {
				RobotController.this.frame.showMessage("登录结果:" + event.getType());
				//登陆成功后
				if(event.getType()== QQActionEvent.Type.EVT_OK){
					//执行轮询
					client.beginPollMsg();
					RobotController.this.frame.showMessage(""+client.getAccount()+"");
                    //获取群列表
                    client.getGroupList(new QQActionListener() {
                        @Override
                        public void onActionEvent(QQActionEvent event) {
                            if (event.getType() == QQActionEvent.Type.EVT_OK) {
                            	 RobotController.this.frame.showMessage("获取群列表成功！");
                                for (QQGroup g : client.getGroupList()) {
                                    RobotController.this.frame.showMessage("群-->"+g.getName());
                                }
                            }
                        }
                    });
                    //获取用户信息
                    users = DAO.getAllUsersRanked();
				}

			}
		};
		client.login(QQStatus.ONLINE, loginListener);
	}
	
	

    /**
     * 收到消息的回调
     *
     * @param event
     * @throws QQException
     */
    @QQNotifyHandler(QQNotifyEvent.Type.CHAT_MSG)
    public void processBuddyMsg(QQNotifyEvent event) throws QQException {
    	//System.out.println("get!");
        QQMsg message = (QQMsg) event.getTarget();
        
        //只接受特定群消息
        if( message.getType() == QQMsg.Type.GROUP_MSG && 
        		String.valueOf(message.getGroup().getGid()).equals(qgroup_number) ){
        	
            StringBuilder text = new StringBuilder(100);
            List<ContentItem> items = message.getContentList();
            for (ContentItem item : items) {
                //获取文本消息
                if (item.getType() == ContentItem.Type.TEXT) {
                    String content = ((TextItem) item).getContent();
                    text.append(content);
                }
            }
            String textS = text.toString().trim();
            String uin = String.valueOf(message.getFrom().getUin());
            //获取结果
            String result=getResultByMsg(textS,uin);

            
            if (!result.isEmpty() && !result.endsWith(Constant.MEIZI)) {
            	//新建消息
                final QQMsg myMessage = new QQMsg();
                //设置类型
                myMessage.setType(message.getType());
                if (message.getType() == QQMsg.Type.GROUP_MSG) {
                    myMessage.setGroup(message.getGroup());
                }
                myMessage.setTo(message.getFrom());
                //设置内容
                myMessage.addContentItem(new TextItem(result));
                //发送消息
                this.client.sendMsg(myMessage, new QQActionListener() {
                    @Override
                    public void onActionEvent(QQActionEvent event) {
                    	if(event.getType()==QQActionEvent.Type.EVT_OK){
                    		frame.showMessage("已发送-->"+myMessage);
                    	}
                    }
                });
            }
			else if (result.endsWith(Constant.MEIZI)) {
				//新建消息
                final QQMsg myMessage = new QQMsg();
                //设置类型
                myMessage.setType(message.getType());
                if (message.getType() == QQMsg.Type.GROUP_MSG) {
                    myMessage.setGroup(message.getGroup());
                }
                myMessage.setTo(message.getFrom());
                
                //设置内容
//                CFaceItem cFaceItem = new CFaceItem();
//                cFaceItem.setFileName("test.jpg");

                FaceItem faceItemCP = new FaceItem(116);
                
                OffPicItem offPicItem = new OffPicItem();
                offPicItem.setFileName("image.jpg");
                offPicItem.setFilePath("E:/Coding/WorkSpace/Eclipse_WorkSpace/QGroupRobot/");
                offPicItem.setFileSize((int) new File("image.jpg").length());
                
                myMessage.addContentItem(offPicItem);
                //myMessage.addContentItem(faceItemCP);
                //myMessage.addContentItem(faceItemCP);
                
                //发送消息
                this.client.sendMsg(myMessage, new QQActionListener() {
                    @Override
                    public void onActionEvent(QQActionEvent event) {
                    	if(event.getType()==QQActionEvent.Type.EVT_OK){
                    		frame.showMessage("已发送-->【老婆图】");
                    	}
                    }
                });
			}
        }
    }


	private String getResultByMsg(String msg,String uin) {
		if(!msg.startsWith("#")) return null;
		
		if(msg.equals("#菜单")){
			return Constant.MENU;
		}
		String result = "";
		String userName = "未获取";
		boolean found = false;
		//根据 uin 查找用户
		for (int i = 0; i < users.size(); i++) {
			if(uin.equals(users.get(i).getUin())){
				userName = users.get(i).getName();
				found = true;
				break;
			}
		}

		if(msg.length()>4 && msg.substring(1,3).equals("注册")){
			if (found) {
				return "回复【"+userName+"】:\n"+"你已经注册过了哦~  :D";
			}
			if(msg.length()<5 || !msg.contains(" ")) result = Constant.ERROR;
			else{
				String[] signStrings = msg.split(" ");
				if(signStrings.length>8){
					result = Constant.OUTOFLENGTH;
				}else{
					if(DAO.addUser(uin, signStrings[1])){
						result = Constant.SUCCESS;
						userName = signStrings[1];
					}else{
						return Constant.FAIL;
					}
				}
			}
		}else if(!found){ 
	            //frame.showMessage(""+message.getGroup()+"");
	            frame.showMessage("来自-->【没注册的家伙】   内容-->"+msg);
				return Constant.SIGNUP_TIPS;
		}else{
			frame.showMessage("来自-->【"+userName+"】   内容-->"+msg);
		}
		
		
		//正式详细判断

    	if(msg.length()>2 ){
    		switch (msg.substring(1,3)) {
    		case "PK":
    			if(msg.length()<5 || !msg.contains(" ")) result = Constant.ERROR;
    			else{
        			String towardsName = msg.split(" ")[1];
        			result = doPK(uin,towardsName);
    			}
    			break;
    		case "榜单":
    			result = getRankString ();
    			break;
    		case "投机":
    			result = doGamble (uin);
    			break;
    		case "注册":
    			
    			break;
    		case "老婆":
    			result = Constant.MEIZI;
    			break;
    		default:
    			//获得小黄鸡结果
                result = Xiaohuangji.chat(msg);
    			break;
    		}
    	}else{
    		result = Xiaohuangji.chat(msg);
    	}
    	return "回复【"+userName+"】:\n"+result;
	}



	private String doPK(String uin, String towardsName) {
		for (int i = 0; i < users.size(); i++) {
			if(users.get(i).getName().equals(towardsName)){
				String towardUin = users.get(i).getUin();
				int number = (int) (300*((Math.random()*2) - 1));
				int fightCount = (int) (Math.random()*9999);
				if(DAO.setValue(uin, "rp", number+"", true) && DAO.setValue(towardUin, "rp", (number*-1)+"", true)){
					String result = (number<0?" 你输了！  -- $":" 你赢了！ ++ $");
					return Constant.PK_STRING.replace("###", towardsName).replace("DDD", fightCount+"") + result + Math.abs(number);
				}
				
			}
		}
		return Constant.PK_UNEXIST;
	}



	private String doGamble(String uin) {
		String name = Constant.names[(int) (Math.random()*25)];
		String result = Constant.gambles[(int) (Math.random()*25)];
		int number = (int) (Math.random()*1000);
		if(result.startsWith("+")){
			result = result.substring(1) + "   增加 $"+number;
		}else{
			result = result.substring(1) + "   减少 $"+number;
			number *= -1;
		}
		result = result.replace("###", name);
		if(DAO.setValue(uin, "rp", number+"",true)) return "投机结果: "+result;
		return Constant.GAMBLE_FAIL;
	}



	private String getRankString() {
		String result = "********财富榜单*********\n";
		System.out.println(users.size());
		for (int i = 0; i < users.size(); i++) {
			result += String.format("%-8s", users.get(i).getName())+"--->>>>  $ "+String.format("%-8s", users.get(i).getRp())+"\n";
		}
		return result;
	}



	/**
     * Kick Out 回调
     *
     * @param event
     */
    @QQNotifyHandler(QQNotifyEvent.Type.KICK_OFFLINE)
    protected void processKickOff(QQNotifyEvent event) {
        frame.showMessage("被Kicked下线了: " + (String) event.getTarget());
    }

    /**
     * 需要验证码通知
     *
     * @param event
     * @throws IOException
     */
    @QQNotifyHandler(QQNotifyEvent.Type.CAPACHA_VERIFY)
    protected void processVerify(QQNotifyEvent event) throws IOException {
        QQNotifyEventArgs.ImageVerify verify = (QQNotifyEventArgs.ImageVerify) event.getTarget();
        
        frame.showMessage("输入验证码");
//        ImageIO.write(verify.image, "png", new File("verify_code.png"));
//        System.out.println(verify.reason);
//        System.out.print("请输入在项目根目录下verify.png图片里面的验证码:");
//        String code = new BufferedReader(new InputStreamReader(System.in)).readLine();
//        client.submitVerify(code, event);
    }

	
	
}
