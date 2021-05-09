package org.jamocha.cube;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

public class DatabaseLogGenerator {

	public static final String[] ips = new String[]{
		"192.168.0.50","192.168.0.51","192.68.0.52","192.168.0.53"
	};
	
	protected PrintWriter writer = null;
	protected Random random = new Random();
	public Date defaultDate = null;
	public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	protected GregorianCalendar calendar = new GregorianCalendar();
	
	public DatabaseLogGenerator() {
		try {
			defaultDate = dateFormat.parse("2010/03/01 08:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void generateData(String file, int count) {
		try {
			writer = new PrintWriter(file);
			for (int i=0; i < count; i++) {
				String ip = ips[random.nextInt(ips.length -1)];
				int bytes = 2000 + random.nextInt(4000);
				int et = 5000 + random.nextInt(10000);
				int trandom = random.nextInt(800000);
				calendar.setTime(defaultDate);
				calendar.add(Calendar.SECOND, trandom);
				String start = calendar.getTime().toString();
				calendar.add(Calendar.MILLISECOND, et);
				String end = calendar.getTime().toString();
				int transactionId = random.nextInt(10000);
				printLog(ip, bytes, et, start, end, String.valueOf(transactionId));
			}
			writer.close();
			System.out.println("Completed generating data!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printLog(String ip, int bytesSent, int elapsedTime, String start, String end, String transactionId) {
		writer.write("(DatabaseQueryLog (IPAddress \"" + ip + "\")(bytesSent " + bytesSent + 
				")(elapsedTime " + elapsedTime + ")(databaseName \"Prod01\")(databaseType \"oracle 10g\")");
		writer.write("(portNumber 1526)(queryString \"select * from Content where\")(queryType \"select\")");
		writer.write("(statusCode \"200\")(timestamp \"" + start + "\")(endTime \"" + 
				end + "\")(transactionID \"" + transactionId + "\"))\n");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args != null && args.length >= 2) {
			String file = args[0];
			int count = Integer.parseInt(args[1]);
			DatabaseLogGenerator gen = new DatabaseLogGenerator();
			gen.generateData(file, count);
		}
	}

}
