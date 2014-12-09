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

	//MainActivity的上下文
	private MainActivity context = null;
	//识别对象
	private SpeechRecognizer iatRecognizer = null;
	//合成对象
	private SpeechSynthesizer mSpeechSynthesizer = null;
	//用户说的话
	private String txtUserSay = "";
	//机器人返回的对话
	private String txtBotSay = "";

	public ChatActivity(MainActivity context) {

		this.context = context;

		//用户登录
		SpeechUser.getUser().login(this.context, null, null
				, "appid=" + context.getString(R.string.app_id), listener);

		//创建听写对象,如果只使用无UI听写功能,不需要创建RecognizerDialog
		iatRecognizer=SpeechRecognizer.createRecognizer(this.context);
		//创建 SpeechSynthesizer 对象。
		mSpeechSynthesizer=SpeechSynthesizer.createSynthesizer(this.context);
	}

	/**
	 * 用户登录回调监听器.
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
					System.out.println("用户登录成功");
				}
			}			
		}

		@Override
		public void onEvent(int arg0, Bundle arg1) {
		}		
	};

	//开始语音交互
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

	//转写监听器
	RecognizerListener mRecoListener = new RecognizerListener()
	{
		//  识别结果回调接口(返回Json格式结果，用户可参见附录)；
		//  一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
		//关于解析Json的代码可参见MscDemo中JsonParser类；
		//isLast等于true时会话结束。
		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			//String txtUserSay = JsonParser.parseIatResult(results.getResultString());
			txtUserSay += JsonParser.parseIatResult(results.getResultString());
			if(!isLast)  return;

			context.onIatCompleted();

			for(int i=0;i<50;i++){
				System.out.println("哈哈哈哈哈哈哈哈哈哈哈哈");
				System.out.println(txtUserSay);
			}
			txtUserSay = removeSym(txtUserSay);
			txtBotSay = chatToAIML(txtUserSay);
			for(int i=0;i<50;i++){
				System.out.println("嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻");
				System.out.println(txtBotSay);
			}
			if(txtBotSay.equals("No AIML category found. This is a Default Response.") || txtBotSay.equals("")){
				txtBotSay = "不好意思，我没理解你的意思\n";
			}
			textToSpeech(txtBotSay);
			txtUserSay = "";
			txtBotSay = "";
		}

		//去除字符串最后的标点符号（。，？ ！）
		public String removeSym(String str){
			int length = str.length();
			if(str.charAt(length-1) == '。' || str.charAt(length-1) == '。' || str.charAt(length-1) == '？' || str.charAt(length-1) == '！')
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

		//  会话发生错误回调接口
		@Override
		public void onError(SpeechError error) {
			context.onIatCompleted();
			context.onTtsCompleted();
			for(int i=0;i<50;i++){
				System.out.println("呵呵呵呵呵呵呵呵呵");
				System.out.println("error");
			}
		}
		//开始录音
		@Override
		public void onBeginOfSpeech() {
		}
		//音量值0~30
		@Override
		public void onVolumeChanged(int volume){
		}
		//结束录音
		@Override
		public void onEndOfSpeech() {
		} 
		//扩展用接口
		@Override
		public void onEvent(int eventType,int arg1,int arg2,String msg) {
		}
	};

	private void textToSpeech(String text){
		//设置发音人
		mSpeechSynthesizer .setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
		//设置语速，范围 0~100
		mSpeechSynthesizer .setParameter(SpeechConstant.SPEED, "50");
		//设置音量，范围 0~100
		mSpeechSynthesizer .setParameter(SpeechConstant.VOLUME, "100");
		//开始合成
		mSpeechSynthesizer .startSpeaking(text,mSynListener);
	}

	//合成监听器
	private SynthesizerListener mSynListener = new SynthesizerListener()
	{
		//  会话结束回调接口，没有错误时，error 为 null
		@Override
		public void onCompleted(SpeechError error) {
			context.onTtsCompleted();
		}
		//  缓冲进度回调
		//percent 为缓冲进度 0~100，beginPos 为缓冲音频在文本中开始位置，endPos 表示缓冲音
		//频在文本中结束位置，info 为附加信息。
		@Override
		public void onBufferProgress(int progress, int beginPos,int endPos, String info) {
		}
		//开始播放
		@Override
		public void onSpeakBegin() {
		}
		//暂停播放
		@Override
		public void onSpeakPaused() {
		}
		//播放进度回调
		//percent 为播放进度 0~100,beginPos 为播放音频在文本中开始位置， endPos 表示播放音频
		//在文本中结束位置.
		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
		}
		//恢复播放回调接口
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
