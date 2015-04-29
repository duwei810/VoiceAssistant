package com.iflytek;


import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
 
//操作查找文件的类
//引用类
 
public class SearchFile {
    static int countFiles = 0;// 声明统计文件个数的变量
    static int countFolders = 0;// 声明统计文件夹的变量
    public static String serachFilePath(File folder,final String keyWord){
    	File[] files=searchFile(folder, keyWord);
    	//如果有相等的应用，直接返回，否则找最相近的
        for(int i=0;i<files.length;i++){
        	String filePath=files[i].getAbsolutePath();
        	String file=filePath.substring(filePath.lastIndexOf("\\")+1);
        	if(file.equals(keyWord+".lnk")){
        		return filePath;
        	}
        	
        }
        for(int i=0;i<files.length;i++){
        	String filePath=files[i].getAbsolutePath();
        	String file=filePath.substring(filePath.lastIndexOf("\\")+1);
        	String type=file.substring(file.lastIndexOf(".")+1);
        	if(!type.equals("lnk") || file.indexOf("卸载")!=-1 || file.indexOf("Uninstall")!=-1 || file.indexOf("uninstall")!=-1 ){
        		continue;
        	}
        	return filePath;
        }
        return null;
    }
    public static File[] searchFile(File folder, final String keyWord) {// 递归查找包含关键字的文件
         
        File[] subFolders = folder.listFiles(new FileFilter() {// 运用内部匿名类获得文件
                    public boolean accept(File pathname) {// 实现FileFilter类的accept方法
                        if (pathname.isFile())// 如果是文件
                            countFiles++;
                        else
                            // 如果是目录
                            countFolders++;
                        if (pathname.isDirectory()
                                || (pathname.isFile() && pathname.getName()
                                        .toLowerCase()
                                        .contains(keyWord.toLowerCase())))// 目录或文件包含关键字
                            return true;
                        return false;
                    }
                });
         
        List result = new ArrayList();// 声明一个集合
        for (int i = 0; i < subFolders.length; i++) {// 循环显示文件夹或文件
            if (subFolders[i].isFile()) {// 如果是文件则将文件添加到结果列表中
                result.add(subFolders[i]);
            } else {// 如果是文件夹，则递归调用本方法，然后把所有的文件加到结果列表中
                File[] foldResult = searchFile(subFolders[i], keyWord);
                for (int j = 0; j < foldResult.length; j++) {// 循环显示文件
                    result.add(foldResult[j]);// 文件保存到集合中
                }
            }
        }
         
        File files[] = new File[result.size()];// 声明文件数组，长度为集合的长度
        result.toArray(files);// 集合数组化
        return files;
    }
}