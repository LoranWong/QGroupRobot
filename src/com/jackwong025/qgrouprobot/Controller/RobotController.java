package com.jackwong025.qgrouprobot.Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import iqq.im.QQActionListener;
import iqq.im.QQClient;
import iqq.im.QQException;
import iqq.im.WebQQClient;
import iqq.im.actor.ThreadActorDispatcher;
import iqq.im.bean.QQBuddy;
import iqq.im.bean.QQGroup;
import iqq.im.bean.QQMsg;
import iqq.im.bean.QQStatus;
import iqq.im.bean.QQUser;
import iqq.im.bean.content.ContentItem;
import iqq.im.bean.content.TextItem;
import iqq.im.core.QQConstants;
import iqq.im.event.QQActionEvent;
import iqq.im.event.QQNotifyEvent;
import iqq.im.event.QQNotifyEventArgs;
import iqq.im.event.QQNotifyHandler;
import iqq.im.event.QQNotifyHandlerProxy;

import com.jackwong025.qgrouprobot.View.MainFrame;

public class RobotController {
	private QQClient client;
	private String qq_number="";
	private String qgroup_number="";
	MainFrame frame = null;
	public RobotController(MainFrame frame,String qq_number,String password, String qgroup_number) {
		this.frame = frame;
		this.qq_number=qq_number;
		this.qgroup_number=qgroup_number;
		//登陆操作
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
				RobotController.this.frame.showMessage("登陆结果:" + event.getType());
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
        	
        	//拼接消息
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
            //frame.showMessage(""+message.getGroup()+"");
            frame.showMessage("来自-->"+message.getFrom().getUin()+"   内容-->"+textS);
            if (textS.startsWith("#")) {
                

            }else{
            	//TODO
            	
            }
            String result = "收到了";
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
