package woolfel.rulebenchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

public class QueryDataGenerator {

	private PrintWriter writer = null;
	private Random ran = new Random();
	public static final String[] urls = new String[]{
		"/index.jsp","/hello.jsp","login.jsp","/logout.jsp","/search.jsp","/results.jsp"};
	
	public QueryDataGenerator() {
		super();
	}
	
	public void createPrintWriter(String file) {
		try {
			writer = new PrintWriter(file);
		} catch (FileNotFoundException e) {
		}
	}
	
	public void generateData(String ip, String hostname, String serverType, int port, String url) {
		long timestamp = 1269285000000L + ran.nextInt(999999);
		int et = ran.nextInt(10000);
		int bytes = 3000 + ran.nextInt(10000);
		int trx = ran.nextInt(10000);
		String session = "abc" + ran.nextInt(500000);
		int thread = ran.nextInt(200);
		writer.println("(ApplicationRequestLog (IPAddress \"" + ip + "\")(Hostname \"" +
				hostname + "\")(ServerType \"" + serverType + "\")(PortNumber " +
				port + ")(Timestamp " + timestamp + ")(BytesSent " + bytes + ")(ElapsedTime " +
				et + ")(URL \"" + url + "\")(SessionID \"" + session + 
				"\")(RequestHeaders)(RequestParameters)(RequestBody)(StatusCode \"200\")" +
				"(StatusMessage)(LogType)(TransactionID \"" + trx + "\")(ThreadID \"" + thread + "\") )");
	}
	
	public void closeWriter() {
		this.writer.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args != null && args.length == 2) {
			String file = args[0];
			int count = Integer.parseInt(args[1]);
			QueryDataGenerator generator = new QueryDataGenerator();
			generator.createPrintWriter(file);
			for (int i=0; i < count; i++) {
				String url = urls[generator.ran.nextInt(urls.length - 1)];
				generator.generateData("129.0.0.1", "localhost", "Tomcat", 8080, url);
			}
			generator.closeWriter();
			System.out.println("Complete!");
		} else {
			System.out.println("Please provide a file name and number of records.");
		}
	}

}
