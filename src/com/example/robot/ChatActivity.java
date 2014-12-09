package com.example.robot;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.example.robot.util.JsonParser;
import com.example.robot1.R;
import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechListener;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SpeechUser;
import com.iflytek.cloud.speech.SynthesizerListener;

import android.os.Bundle;

public class ChatActivity{

	//MainActivity��������
	private MainActivity context = null;
	//ʶ�����
	private SpeechRecognizer iatRecognizer = null;
	//�ϳɶ���
	private SpeechSynthesizer mSpeechSynthesizer = null;
	//�û�˵�Ļ�
	private String txtUserSay = "";
	//�����˷��صĶԻ�
	private String txtBotSay = "";

	public ChatActivity(MainActivity context) {

		this.context = context;

		//�û���¼
		SpeechUser.getUser().login(this.context, null, null
				, "appid=" + context.getString(R.string.app_id), listener);

		//������д����,���ֻʹ����UI��д����,����Ҫ����RecognizerDialog
		iatRecognizer=SpeechRecognizer.createRecognizer(this.context);
		//���� SpeechSynthesizer ����
		mSpeechSynthesizer=SpeechSynthesizer.createSynthesizer(this.context);
	}

	/**
	 * �û���¼�ص�������.
	 */
	private SpeechListener listener = new SpeechListener()
	{

		@Override
		public void onData(byte[] arg0) {
		}

		@Override
		public void onCompleted(SpeechError error) {
			if(error == null) {
				for(int i=0;i<50;i++){
					System.out.println("�û���¼�ɹ�");
				}
			}			
		}

		@Override
		public void onEvent(int arg0, Bundle arg1) {
		}		
	};

	//��ʼ��������
	public void startIatListening(){
		if(null == iatRecognizer) {
			iatRecognizer=SpeechRecognizer.createRecognizer(this.context);
		}
		if(iatRecognizer.isListening()) {
			iatRecognizer.stopListening();
		} else {
			iatRecognizer. setParameter(SpeechConstant.DOMAIN,"iat");
			iatRecognizer. setParameter(SpeechConstant.LANGUAGE,"zh_cn");
			iatRecognizer.startListening(mRecoListener);
		}
	}

	//תд������
	RecognizerListener mRecoListener = new RecognizerListener()
	{
		//  ʶ�����ص��ӿ�(����Json��ʽ������û��ɲμ���¼)��
		//  һ������»�ͨ��onResults�ӿڶ�η��ؽ����������ʶ�������Ƕ�ν�����ۼӣ�
		//���ڽ���Json�Ĵ���ɲμ�MscDemo��JsonParser�ࣻ
		//isLast����trueʱ�Ự������
		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			//String txtUserSay = JsonParser.parseIatResult(results.getResultString());
			txtUserSay += JsonParser.parseIatResult(results.getResultString());
			if(!isLast)  return;

			context.onIatCompleted();

			for(int i=0;i<50;i++){
				System.out.println("������������������������");
				System.out.println(txtUserSay);
			}
			txtUserSay = removeSym(txtUserSay);
			txtBotSay = chatToAIML(txtUserSay);
			for(int i=0;i<50;i++){
				System.out.println("������������������������");
				System.out.println(txtBotSay);
			}
			if(txtBotSay.equals("No AIML category found. This is a Default Response.") || txtBotSay.equals("")){
				txtBotSay = "������˼����û��������˼\n";
			}
			textToSpeech(txtBotSay);
			txtUserSay = "";
			txtBotSay = "";
		}

		//ȥ���ַ������ı����ţ������� ����
		public String removeSym(String str){
			int length = str.length();
			if(str.charAt(length-1) == '��' || str.charAt(length-1) == '��' || str.charAt(length-1) == '��' || str.charAt(length-1) == '��')
				length = length - 1;
			char[] value = new char[length];
			for (int i=0; i<length; ++i) {
				value[i] = str.charAt(i);
			}
			return new String(value);
		}

		private String chatToAIML(String userSay)
		{
			userSay = appendPlus(userSay);
			HttpClient client = new DefaultHttpClient();
			StringBuilder builder = new StringBuilder();
			String botSay = null;
			String text = "http://192.168.1.104/Program-O-master/gui/plain/index.php?say="+userSay+"&convo_id=123456&bot_id=1&format=json";
			HttpGet myget = new HttpGet(text);
			try {
				HttpResponse response = client.execute(myget);
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					builder.append(s);
				}
				JSONObject jsonObject = new JSONObject(builder.toString());
				botSay = jsonObject.getString("botsay");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return botSay;
		}

		private String appendPlus(String  para){
			int length = para.length();
			char[] value = new char[length << 1];
			for (int i=0, j=0; i<length; ++i, j = i << 1) {
				value[j] = para.charAt(i);
				value[1 + j] = '+';
			}
			return new String(value);
		}

		//  �Ự��������ص��ӿ�
		@Override
		public void onError(SpeechError error) {
			context.onIatCompleted();
			context.onTtsCompleted();
			for(int i=0;i<50;i++){
				System.out.println("�ǺǺǺǺǺǺǺǺ�");
				System.out.println("error");
			}
		}
		//��ʼ¼��
		@Override
		public void onBeginOfSpeech() {
		}
		//����ֵ0~30
		@Override
		public void onVolumeChanged(int volume){
		}
		//����¼��
		@Override
		public void onEndOfSpeech() {
		} 
		//��չ�ýӿ�
		@Override
		public void onEvent(int eventType,int arg1,int arg2,String msg) {
		}
	};

	private void textToSpeech(String text){
		//���÷�����
		mSpeechSynthesizer .setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
		//�������٣���Χ 0~100
		mSpeechSynthesizer .setParameter(SpeechConstant.SPEED, "50");
		//������������Χ 0~100
		mSpeechSynthesizer .setParameter(SpeechConstant.VOLUME, "100");
		//��ʼ�ϳ�
		mSpeechSynthesizer .startSpeaking(text,mSynListener);
	}

	//�ϳɼ�����
	private SynthesizerListener mSynListener = new SynthesizerListener()
	{
		//  �Ự�����ص��ӿڣ�û�д���ʱ��error Ϊ null
		@Override
		public void onCompleted(SpeechError error) {
			context.onTtsCompleted();
		}
		//  ������Ȼص�
		//percent Ϊ������� 0~100��beginPos Ϊ������Ƶ���ı��п�ʼλ�ã�endPos ��ʾ������
		//Ƶ���ı��н���λ�ã�info Ϊ������Ϣ��
		@Override
		public void onBufferProgress(int progress, int beginPos,int endPos, String info) {
		}
		//��ʼ����
		@Override
		public void onSpeakBegin() {
		}
		//��ͣ����
		@Override
		public void onSpeakPaused() {
		}
		//���Ž��Ȼص�
		//percent Ϊ���Ž��� 0~100,beginPos Ϊ������Ƶ���ı��п�ʼλ�ã� endPos ��ʾ������Ƶ
		//���ı��н���λ��.
		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
		}
		//�ָ����Żص��ӿ�
		@Override
		public void onSpeakResumed() {
		}
	};

	//	@Override
	//	protected void onStop() {
	//		if (null != iatRecognizer) {
	//			iatRecognizer.cancel();
	//		}
	//		super.onStop();
	//	}

}
