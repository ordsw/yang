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

public class RtsOriginReceiver implements MessageListener {//��������Ҫʵ�ֽӿ�MessageListener�ķ���
	
	//����log4��Ŀ��
	static Logger logger = Logger.getLogger(RtsOriginReceiver.class);
	
	public void onMessage(Message msg) {
		try {
			//�ж�message�ǲ���jms���������ֽ���Ϣ
			if (msg instanceof javax.jms.BytesMessage) {
				BytesMessage bmsg = (BytesMessage) msg;
				byte[] buf = new byte[(int) bmsg.getBodyLength()];
				bmsg.readBytes(buf);
				//���ֽ������װ�ɶ�����
				ObjectInputStream ois = new ObjectInputStream(
						new ByteArrayInputStream(buf));
				//�Ӷ��������ȡobject(����ʵ��)
				Object obj = ois.readObject();
				if (obj == null)
					return;
				//����Ӷ������ж�ȡ������ʵ��,�����ж��ǲ�������Ҫ�Ķ���,�������ͨ��proccess*�����Զ�ȡ���Ķ�����в���
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
		//����log4j�������ļ�����Ӧlog4j.properties
		org.apache.log4j.PropertyConfigurator.configure("log4j.properties");
		//��tst.xml�ļ�����                                                                    
		ApplicationContext ctx = new FileSystemXmlApplicationContext("tst.xml");
		//��tst.xml�ļ��е�beanȡ����ʵ����
		DefaultMessageListenerContainer container = (DefaultMessageListenerContainer) ctx
				.getBean("locationListenerContainer");
		//����bean���locationListenerContainer��������
		container.start();
	}

}
