package com.iflytek;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.iflytek.cloud.speech.DataUploader;
import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.Setting;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechListener;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SpeechUtility;
import com.iflytek.cloud.speech.SynthesizeToUriListener;
import com.iflytek.cloud.speech.UserWords;

import javax.swing.*;  

import java.awt.*;
import java.io.*;  

import org.omg.CORBA.BAD_QOS;
public class Recognition {
	private static final String APPID = "5508db2f";
	private static Recognition mObject;
	private static StringBuffer mResult = new StringBuffer();
	private JTextArea jTextArea;
	public Recognition(JTextArea jTextArea){
		this.jTextArea=jTextArea;
		SpeechUtility.createUtility("appid=" + APPID);
		onLoop();
	}
	/*
	private static Recognition getMscObj() {
		if (mObject == null)
			mObject = new Recognition();
		return mObject;
	}
	*/
	private void onLoop() {
		try {
			DebugLog.Log("*********************************");
			
			 recognize();
		} catch (Exception e) {
			//onLoop();
		}
	}
	private void recognize() {
		if (SpeechRecognizer.getRecognizer() == null)
			SpeechRecognizer.createRecognizer();
		recognizePcmfileByte();
	}
	public void recognizePcmfileByte() {
		// 1、读取音频文件
		FileInputStream fis = null;
		byte[] voiceBuffer=null;// = baos.toByteArray();
		try {
			fis = new FileInputStream(new File("./test0.pcm"));
			voiceBuffer = new byte[fis.available()];
			//voiceBuffer = new byte[baos.toByteArray()];  
			fis.read(voiceBuffer);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fis) {
					fis.close();
					fis = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 2、音频流听写
		if (0 == voiceBuffer.length) {
			mResult.append("no audio avaible!");
		} else {
			SpeechRecognizer recognizer = SpeechRecognizer.getRecognizer();
			recognizer.setParameter(SpeechConstant.DOMAIN, "iat");
			recognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			recognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
			recognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH,
					"./iflytek.pcm");
			recognizer.setParameter(SpeechConstant.ENGINE_MODE, "sms");
			recognizer.setParameter("asr_sch", "1");
			recognizer.setParameter("plain_result", "1");
			recognizer.setParameter(SpeechConstant.NLP_VERSION, "2.0");
			recognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");
			recognizer.startListening(recListener);
			//recognizer.startListening(recListener, "sms", "asr_sch=1,plain_result=1,nlp_version=2.0,rst=json", null);
			ArrayList<byte[]> buffers = splitBuffer(voiceBuffer,
					voiceBuffer.length, 4800);
			mResult.append("recognizeStream");
			for (int i = 0; i < buffers.size(); i++) {
				// 每次写入msc数据4.8K,相当150ms录音数据
				recognizer.writeAudio(buffers.get(i), 0, buffers.get(i).length);
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			recognizer.stopListening();
			mResult.append("stopListening");
		}
	}

	/**
	 * 将字节缓冲区按照固定大小进行分割成数组
	 * 
	 * @param buffer
	 *            缓冲区
	 * @param length
	 *            缓冲区大小
	 * @param spsize
	 *            切割块大小
	 * @return
	 */
	public ArrayList<byte[]> splitBuffer(byte[] buffer, int length, int spsize) {
		ArrayList<byte[]> array = new ArrayList<byte[]>();
		if (spsize <= 0 || length <= 0 || buffer == null
				|| buffer.length < length)
			return array;
		int size = 0;
		while (size < length) {
			int left = length - size;
			if (spsize < left) {
				byte[] sdata = new byte[spsize];
				System.arraycopy(buffer, size, sdata, 0, spsize);
				array.add(sdata);
				size += spsize;
			} else {
				byte[] sdata = new byte[left];
				System.arraycopy(buffer, size, sdata, 0, left);
				array.add(sdata);
				size += left;
			}
		}
		return array;
	}

	/**
	 * 听写监听器
	 */
	private RecognizerListener recListener = new RecognizerListener() {

		public void onBeginOfSpeech() {
			DebugLog.Log("*************开始录音*************");
		}

		public void onEndOfSpeech() {
			String jsonString=mResult.toString();
			//jsonString=jsonString.substring(28);
			jsonString=jsonString.substring(jsonString.indexOf("{"));
			DebugLog.Log("识别结果为:" + jsonString);
			JsonParse jsonParse=new JsonParse(jsonString);
            jTextArea.append("主人："+jsonParse.getText()+"\r\n");
            jTextArea.append("小薇："+jsonParse.getResult()+"\r\n");
			mResult.delete(0, mResult.length());
		}

		public void onVolumeChanged(int volume) {
			if (volume > 0)
				DebugLog.Log("*************音量值:" + volume + "*************");

		}

		public void onResult(RecognizerResult result, boolean islast) {

			mResult.append(result.getResultString());
		}

		public void onError(SpeechError error) {
			DebugLog.Log("*************" + error.getErrorCode()
					+ "*************");
		}

		public void onEvent(int eventType, int arg1, int agr2, String msg) {

		}

	};
}
