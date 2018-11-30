package org.openiot.lsm.reasoning.test;

public class Test1 {
	
	public static void main(String[] args) {
		int mid = 1;
		String smid = String.format("http://www.insight-centre.org/MeetingEvent_%d", mid);
		for(int uid = 1; uid <= 10; uid++){
			if(uid%10 ==0){
				mid ++;
				smid = String.format("http://www.insight-centre.org/MeetingEvent_%d", mid);
			}
			String suid = String.format("user%d", uid);
			Client user = new Client(smid, suid);
			(new Thread(user)).start();
		}
	}

}
