package com.qingjie;

import java.io.File;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		getFile("/Users/zhao/dev/eclipse-workspace/spring-boot-kafka-producer/Images");
	}

	private static void getFile(String path) {
		// get file list where the path has
		File file = new File(path);
		// get the folder list
		File[] array = file.listFiles();
		
		for (int i = 0; i < array.length; i++) {
			if (array[i].isFile()) {
				// only take file name
				System.out.println("^^^^^" + array[i].getName());
				// take file path and name
				System.out.println("#####" + array[i]);
				// take file path and name
				System.out.println("*****" + array[i].getPath());
			} else if (array[i].isDirectory()) {
				getFile(array[i].getPath());
			}
		}
	}

}
