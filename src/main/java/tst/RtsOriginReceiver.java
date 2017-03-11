package tst;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import cn.org.gddsn.rts.RtsOrigin;
import cn.org.gddsn.rts.RtsPick;

public class RtsOriginReceiver implements MessageListener {//监听程序要实现接口MessageListener的方法
	
	//设置log4的目标
	static Logger logger = Logger.getLogger(RtsOriginReceiver.class);
	
	public void onMessage(Message msg) {
		try {
			//判断message是不是jms发过来的字节消息
			if (msg instanceof javax.jms.BytesMessage) {
				BytesMessage bmsg = (BytesMessage) msg;
				byte[] buf = new byte[(int) bmsg.getBodyLength()];
				bmsg.readBytes(buf);
				//将字节属组封装成对象流
				ObjectInputStream ois = new ObjectInputStream(
						new ByteArrayInputStream(buf));
				//从对象流里读取object(对象实例)
				Object obj = ois.readObject();
				if (obj == null)
					return;
				//如果从对象流中读取到对象实例,首先判断是不是你想要的对象,如果是则通过proccess*方法对读取出的对象进行操作
				if (obj instanceof RtsOrigin) {
					RtsOrigin org = (RtsOrigin) obj;
					proccessRtsOrigin(org);
				}
				if (obj instanceof RtsPick) {
					RtsPick pick = (RtsPick) obj;
					proccessRtsPick(pick);
				}
			}
		} catch (Exception ex) {
			logger.warn(ex.getMessage(), ex);
		}
	}

	private void proccessRtsOrigin(RtsOrigin org) {
		logger.info(org);
	}

	
	private void proccessRtsPick(RtsPick pick) {
		logger.info(pick);
	}

	public static void main(String[] args) {
		//设置log4j的配置文件，对应log4j.properties
		org.apache.log4j.PropertyConfigurator.configure("log4j.properties");
		//将tst.xml文件加载                                                                    
		ApplicationContext ctx = new FileSystemXmlApplicationContext("tst.xml");
		//将tst.xml文件中的bean取出来实例化
		DefaultMessageListenerContainer container = (DefaultMessageListenerContainer) ctx
				.getBean("locationListenerContainer");
		//启动bean里的locationListenerContainer监听程序
		container.start();
	}

}
