package org.gary.nettyrpc.serialization;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import org.junit.Test;

public class SerializationTest {
	
	@Test
	public void test() {
		ForTest ft=new ForTest();
		FastJsonSerializer fjs=new FastJsonSerializer();
		byte[] bytes=fjs.serialize(ft);
		ft=fjs.deserialize(bytes, ForTest.class);
		assertEquals(1,ft.a);
	}
	
	/*
	public static void fastJson() {
		ForTest ft=new ForTest();
		FastJsonSerializer fjs=new FastJsonSerializer();
		byte[] bytes=fjs.serialize(ft);
		ft=fjs.deserialize(bytes, ForTest.class);
	}
	
	public static void defaulta() throws IOException, ClassNotFoundException {
		ForTest ft=new ForTest();
		ByteArrayOutputStream bao=new ByteArrayOutputStream();
		ObjectOutputStream oos=new ObjectOutputStream(bao);
		oos.writeObject(ft);
		byte[] bytes=bao.toByteArray();
		ByteArrayInputStream bai=new ByteArrayInputStream(bytes);
		ObjectInputStream ois=new ObjectInputStream(bai);
		ft=(ForTest) ois.readObject();
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		long startTime=System.nanoTime();
		fastJson();
		long endTime=System.nanoTime();
		System.out.println("fastJson continued for "+(endTime-startTime)+" ns");
		long anotherStartTime=System.nanoTime();
		defaulta();
		long anotherEndTime=System.nanoTime();
		System.out.println("default  continued for "+(anotherEndTime-anotherStartTime)+" ns");
	}
	*/
	
	
	public static void main(String[] args) {
		FastJsonSerializer fjs=new FastJsonSerializer();
		ForTest ftb=new ForTest();
		ftb.setYes("no");
		//ftb.setSelfClass(FastJsonSerializer.class);
		//ftb.setSelfClass(String.class);
		byte[] bytes=fjs.serialize(ftb);
		ForTest ft=fjs.deserialize(bytes, ForTest.class);
		System.out.println(ft.getSelfClass());
		System.out.println(ft.getYes());
	}
	
}


class ForTest implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3161533491867666365L;
	private String yes; 
	public String getYes() {
		return yes;
	}
	public void setYes(String yes) {
		this.yes = yes;
	}
	private Class<?> selfClass;
	public Class<?> getSelfClass() {
		return selfClass;
	}
	public void setSelfClass(Class<?> selfClass) {
		this.selfClass = selfClass;
	}
	int a=1;
}