package org.jamocha.cube;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

public class HTTPLogGenerator {

	public static final String[] ips = new String[]{
		"192.168.0.10","192.168.0.11","192.68.0.12","192.168.0.13",
		"192.168.0.14","192.168.0.15","192.68.0.16","192.168.0.17",
		"192.168.0.18","192.168.0.19","192.68.0.20","192.168.0.21"
	};
	public static final String[] dbips = new String[]{
		"192.168.0.50","192.168.0.51","192.68.0.52","192.168.0.53"
	};	
	public static final String[] urls = new String[] {
		"/GetContent","/StoreContent","/GetListing","/SearchContent","/GetContentHistory","/QueueContent"
	};
	
	protected PrintWriter writer = null;
	protected Random random = new Random();
	public Date defaultDate = null;
	public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	protected GregorianCalendar calendar = new GregorianCalendar();
	
	public HTTPLogGenerator() {
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
				String dbip = dbips[random.nextInt(dbips.length - 1)];
				String url = urls[random.nextInt(urls.length -1)];
				int bytes = 2000 + random.nextInt(4000);
				int et = 1000 + random.nextInt(40000);
				int trandom = random.nextInt(900000);
				calendar.setTime(defaultDate);
				calendar.add(Calendar.SECOND, trandom);
				long startlong = calendar.getTimeInMillis();
				String start = String.valueOf(startlong);
				long startlong2 = startlong + random.nextInt(350);
				String start2 = String.valueOf(startlong2);
				calendar.add(Calendar.MILLISECOND, et);
				long endlong = calendar.getTimeInMillis();
				String end = String.valueOf(endlong);
				long endlong2 = endlong - random.nextInt(350);
				String end2 = String.valueOf(endlong2);
				int transactionId = random.nextInt(count);
				printLog(ip, url, bytes, et, start, end, String.valueOf(transactionId));
				printDatabaseLog(dbip, (bytes - 200), (int)(endlong2 - startlong2), start2, end2, String.valueOf(transactionId));
			}
			writer.close();
			System.out.println("Completed generating data!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printLog(String ip, String url, int bytesSent, int elapsedTime, String start, String end, String transactionId) {
		writer.write("(HTTPLog (IPAddress \"" + ip + "\")(URL \"" + url + "\")(action \"\")(bytesSent " + bytesSent + 
				")(elapsedTime " + elapsedTime + ")(hostname \"\")(logType \"\")");
		writer.write("(portNumber 8080)(protocol \"HTTP/1.1\")(requestBody \"\")(requestHeaders \"\")(requestParameters \"\")(serverType \"Tomcat 6\")");
		writer.write("(sessionID \"\")(statusCode \"200\")(statusMessage \"\")(threadID \"\")(timestamp " + start + 
				")(endTime " + end + ")(transactionID \"" + transactionId + "\"))\n");
	}
	
	public void printDatabaseLog(String ip, int bytesSent, int elapsedTime, String start, String end, String transactionId) {
		writer.write("(DatabaseQueryLog (IPAddress \"" + ip + "\")(bytesSent " + bytesSent + 
				")(elapsedTime " + elapsedTime + ")(databaseName \"Prod01\")(databaseType \"oracle 10g\")");
		writer.write("(portNumber 1526)(queryString \"select * from Content where\")(queryType \"select\")");
		writer.write("(timestamp " + start + ")(endTime " + 
				end + ")(transactionID \"" + transactionId + "\"))\n");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args != null && args.length >= 2) {
			String file = args[0];
			int count = Integer.parseInt(args[1]);
			HTTPLogGenerator gen = new HTTPLogGenerator();
			gen.generateData(file, count);
		}
	}

}
