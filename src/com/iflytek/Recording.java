package com.iflytek;

import java.awt.*;  

import javafx.scene.transform.Affine;

import javax.swing.*;  

import java.awt.event.*;  
import java.io.*;  

import javax.sound.sampled.*;  

import sun.tools.jar.resources.jar;

import com.iflytek.cloud.speech.Setting;

public class Recording<MotionEvent> extends JFrame implements ActionListener{  
  
    //定义录音格式  
    AudioFormat af = null;  
    //定义目标数据行,可以从中读取音频数据,该 TargetDataLine 接口提供从目标数据行的缓冲区读取所捕获数据的方法。  
    TargetDataLine td = null;  
    //定义源数据行,源数据行是可以写入数据的数据行。它充当其混频器的源。应用程序将音频字节写入源数据行，这样可处理字节缓冲并将它们传递给混频器。  
    SourceDataLine sd = null;  
    //定义字节数组输入输出流  
    ByteArrayInputStream bais = null;  
    ByteArrayOutputStream baos = null;  
    //定义音频输入流  
    AudioInputStream ais = null;  
    //定义停止录音的标志，来控制录音线程的运行  
    Boolean stopflag = false;  
      
      
    //定义所需要的组件  
    JPanel jp1,jp2,jp3;  
    JTextArea jTextArea;
    //JLabel jl1=null;  
    JButton captureBtn,stopBtn,playBtn,saveBtn;
    
	public static void main(String[] args) {
		Setting.showLog(true);             //识别demo里的
		Recording re=new Recording(); 
		
	}
	
    //构造函数  
    public Recording()  
    {  
        //组件初始化  
        jp1 = new JPanel();  
        jp2 = new JPanel();  
        jp3 = new JPanel();  
        jTextArea = new JTextArea(9,23); 
        JScrollPane scroll = new JScrollPane(jTextArea);
        scroll.setHorizontalScrollBarPolicy( 
        		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
        		scroll.setVerticalScrollBarPolicy( 
        		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
        //定义字体  
        //Font myFont = new Font("华文新魏",Font.BOLD,30);  
        //jl1 = new JLabel("录音机功能的实现");  
        //jl1.setFont(myFont);  
        //jp1.add(jl1);  
        jp1.add(jTextArea);
        Font myFont = new Font("微软雅黑",Font.BOLD,16);  
        jTextArea.setFont(myFont);
        jTextArea.append("小薇："+"你好，我是小薇\r\n");
        /*JScrollPane sp = new JScrollPane(ja);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jp1.add(sp);*/
        captureBtn = new JButton("按住说话");  
        //对开始录音按钮进行注册监听  
        /*
        captureBtn.addActionListener(this);  
        captureBtn.setActionCommand("captureBtn");  
        */
        captureBtn.addMouseListener(new MouseAdapter() {  
            @Override  
            public void mousePressed(MouseEvent e) {    
                capture();  
            }  
          
            @Override  
            public void mouseReleased(MouseEvent e) {  
                stop();  
                save();  
                Recognition re = new Recognition(jTextArea);     //保存录音成.pcm格式，开始识别
            }  
        });  
        this.add(jp1,BorderLayout.NORTH);  
        this.add(jp2,BorderLayout.CENTER);  
        this.add(jp3,BorderLayout.SOUTH);  
        jp3.setLayout(null);  
        jp3.setLayout(new GridLayout(1, 4,10,10));  
        jp3.add(captureBtn); 
        //设置按钮的属性  
        captureBtn.setEnabled(true);  
        //设置窗口的属性  
        this.setSize(400,300);  
        this.setTitle("语音助手");  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        this.setLocationRelativeTo(null);  
        this.setVisible(true);  
    }  
	public void actionPerformed(ActionEvent e) {  
          
    }  

    //开始录音  
    public void capture()  
    {  
        try {  
            //af为AudioFormat也就是音频格式  
            af = getAudioFormat();  
            DataLine.Info info = new DataLine.Info(TargetDataLine.class,af);  
            td = (TargetDataLine)(AudioSystem.getLine(info));  
            //打开具有指定格式的行，这样可使行获得所有所需的系统资源并变得可操作。  
            td.open(af);  
            //允许某一数据行执行数据 I/O  
            td.start();            
            //创建播放录音的线程  
            Record record = new Record();  
            Thread t1 = new Thread(record);  
            t1.start();  
              
        } catch (LineUnavailableException ex) {  
            ex.printStackTrace();  
            return;  
        }  
    }  
    //停止录音  
    public void stop()  
    {  
        stopflag = true;              
    }  
    //播放录音  
    public void play()  
    {  
        //将baos中的数据转换为字节数据  
        byte audioData[] = baos.toByteArray();  
        //转换为输入流  
        bais = new ByteArrayInputStream(audioData);  
        af = getAudioFormat();  
        ais = new AudioInputStream(bais, af, audioData.length/af.getFrameSize());  
          
        try {  
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, af);  
            sd = (SourceDataLine) AudioSystem.getLine(dataLineInfo);  
            sd.open(af);  
            sd.start();  
            //创建播放进程  
            Play py = new Play();  
            Thread t2 = new Thread(py);  
            t2.start();             
        } catch (Exception e) {  
            e.printStackTrace();  
        }finally{  
            try {  
                //关闭流  
                if(ais != null)  
                {  
                    ais.close();  
                }  
                if(bais != null)  
                {  
                    bais.close();  
                }  
                if(baos != null)  
                {  
                    baos.close();  
                }  
                  
            } catch (Exception e) {       
                e.printStackTrace();  
            }  
        }  
          
    }  
    //保存录音  
    public void save()  

    {  
         //取得录音输入流  
        af = getAudioFormat();  
  
        byte audioData[] = baos.toByteArray();  
        bais = new ByteArrayInputStream(audioData);  
        ais = new AudioInputStream(bais,af, audioData.length / af.getFrameSize());  
        //定义最终保存的文件名  
        File file = null;  
        //写入文件  
        try {     
            //以当前的时间命名录音的名字  
            //将录音的文件存放到F盘下语音文件夹下  
            File filePath = new File(".");  
            if(!filePath.exists())  
            {//如果文件不存在，则创建该目录  
                filePath.mkdir();  
            }  
            file = new File(filePath.getPath()+"/"+"test0.pcm");        
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }finally{  
            //关闭流  
            try {  
                  
                if(bais != null)  
                {  
                    bais.close();  
                }   
                if(ais != null)  
                {  
                    ais.close();          
                }  
            } catch (Exception e) {  
                e.printStackTrace();  
            }         
        }  
    }  
    //设置AudioFormat的参数  
    public ByteArrayOutputStream getBaos(){
    	return baos;
    }
    public AudioFormat getAudioFormat()   
    {  
        //下面注释部分是另外一种音频格式，两者都可以  
        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED ;  
        float rate = 16000f;  
        int sampleSize = 16;  
        String signedString = "signed";  
        boolean bigEndian = true;  
        int channels = 1;  
        return new AudioFormat(encoding, rate, sampleSize, channels,  
                (sampleSize / 8) * channels, rate, bigEndian);  
//      //采样率是每秒播放和录制的样本数  
//      float sampleRate = 16000.0F;  
//      // 采样率8000,11025,16000,22050,44100  
//      //sampleSizeInBits表示每个具有此格式的声音样本中的位数  
//      int sampleSizeInBits = 16;  
//      // 8,16  
//      int channels = 1;  
//      // 单声道为1，立体声为2  
//      boolean signed = true;  
//      // true,false  
//      boolean bigEndian = true;  
//      // true,false  
//      return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,bigEndian);  
    }  
    //录音类，因为要用到MyRecord类中的变量，所以将其做成内部类  
    class Record implements Runnable  
    {  
        //定义存放录音的字节数组,作为缓冲区  
        byte bts[] = new byte[10000];  
        //将字节数组包装到流里，最终存入到baos中  
        //重写run函数  
        public void run() {   
            baos = new ByteArrayOutputStream();       
            try {  
                System.out.println("ok3");  
                stopflag = false;  
                while(stopflag != true)  
                {  
                    //当停止录音没按下时，该线程一直执行   
                    //从数据行的输入缓冲区读取音频数据。  
                    //要读取bts.length长度的字节,cnt 是实际读取的字节数  
                    int cnt = td.read(bts, 0, bts.length);  
                    if(cnt > 0)  
                    {  
                        baos.write(bts, 0, cnt);  
                    }  
                }

            } catch (Exception e) {  
                e.printStackTrace();  
            }
            try {  
            	//关闭打开的字节数组流  
                if(baos != null)  
                {  
                	baos.close();  
                	 
                }     
            } catch (IOException e) {  
               e.printStackTrace();  
            }
            td.close();
            td=null;
        }  
          
    }  
    //播放类,同样也做成内部类  
    class Play implements Runnable  
    {  
        //播放baos中的数据即可  
        public void run() {  
            byte bts[] = new byte[10000];  
            try {  
                int cnt;  
                //读取数据到缓存数据  
                while ((cnt = ais.read(bts, 0, bts.length)) != -1)   
                {  
                    if (cnt > 0)   
                    {  
                        //写入缓存数据  
                        //将音频数据写入到混频器  
                        sd.write(bts, 0, cnt);  
                    }  
                }  
                 
            } catch (Exception e) {  
                e.printStackTrace();  
            }finally{  
                 sd.drain();  
                 sd.close();  
            }  
              
              
        }         
    } 
}