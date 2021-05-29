package woolfel.rulebenchmark;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class CubeTestGenerator {

	public static final String LINEBREAK = System.getProperty("line.separator");
	
	public static final String[] accountids = new String[]{
		"aaaa","bbbb","cccc","dddd","eeee","ffff","gggg","hhhh",
		"iiii","jjjj","kkkk","llll","mmmm","nnnn","oooo","pppp",
		"qqqq","rrrr","ssss","tttt","uuuu","vvvv","wwww","xxxx",
		"yyyy","zzzz"};
	
	public static final String[] countrycodes = new String[]{
		"us","uk","cn","jp","tw","ca","fr","de","nz","au","at"};
	
	public static final String[] tickersymbols = new String[]{
		"aaa","abc","abb","acc","acd","add","ade","aee","aef","aff",
		"afg","agg","agh","ahh","ahi","aii","aij","ajj","ajk","akk",
		"akl","all","alm","amm","amn","ann","ano","aoo","aop","app",
		"apq","aqq","aqr","arr","ars","ass","ast","att","atu","auu",
		"auv","avv","avw","aww","awx","axx","axy","ayy","ayz","azz",
		"baa","bbc","bbb","bcc","bcd","bdd","bde","bee","bef","bff",
		"bfg","bgg","bgh","bhh","bhi","bii","bij","bjj","bjk","bkk",
		"bkl","bll","blm","bmm","bmn","bnn","bno","boo","bop","bpp",
		"bpq","bqq","bqr","brr","brs","bss","bst","btt","btu","buu",
		"buv","bvv","bvw","bww","bwx","bxx","bxy","byy","byz","bzz",
		"caa","cbc","cbb","ccc","ccd","cdd","cde","cee","cef","cff",
		"cfg","cgg","cgh","chh","chi","cii","cij","cjj","cjk","ckk",
		"ckl","cll","clm","cmm","cmn","cnn","cno","coo","cop","cpp",
		"cpq","cqq","cqr","crr","crs","css","cst","ctt","ctu","cuu",
		"cuv","cvv","cvw","cww","cwx","cxx","cxy","cyy","cyz","czz"};
	
	public static final String[] fitchratingcodes = new String[]{
		"AAA","AA","A","BBB","BB","B","CCC","CC","C","D","NR"};
	
	public static final int[] fitchratingvalues = new int[]{
		10,9,8,7,6,5,4,3,2,1,0};
	
	public static final String[] gicsSubIndustries = new String[]{
		"10101010","10101020","10102010","10102020","10102030","10102040",
		"10102050","15101010","15101020","15101030","15101040","15101050",
		"15102010","15103010","15103020","15104010","15104020","15104030",
		"15104040","15104050","15105010","15105020","20101010","20102010",
		"20103010","20104010","20104020","20105010","20106010","20106020",
		"20107010","20201010","20201020","20201030","20201040","20201050",
		"20201060","20201070","20201080","20202010","20202020","20301010",
		"20302010","20303010","20304010","20304020","20305010","20305020",
		"20305030","25101010","25101020","25102010","25102020","25201010",
		"25201020","25201030","25201040","25201050","25202010","25202020",
		"25203010","25203020","25203030","25301010","25301020","25301030",
		"25301040","25302010","25302020","25401010","25401020","25401025",
		"25401030","25401040","25501010","25502010","25502020","25503010",
		"25503020","25504010","25504020","25504030","25504040","25504050",
		"25504060","30101010","30101020","30101030","30101040","30201010",
		"30201020","30201030","30202010","30202020","30202030","30203010",
		"30301010","30302010","35101010","35101020","35102010","35102015",
		"35102020","35102030","35103010","35201010","35202010","35203010",
		"40101010","40101015","40102010","40201010","40201020","40201030",
		"40201040","40202010","40203010","40203020","40203030","40301010",
		"40301020","40301030","40301040","40301050","40401010","40401020",
		"40402010","40402020","40402030","40402040","40402050","40402060",
		"40402070","40403010","40403020","40403030","40403040","45101010",
		"45102010","45102020","45103010","45103020","45103030","45201020",
		"45201010","45201020","45202010","45202020","45203010","45203015",
		"45203020","45203030","45204010","45205010","45205020","45301010",
		"45301020","50101010","50101020","50102010","55101010","55102010",
		"55103010","55104010","55105010"};
	
	protected DecimalFormat currency = new DecimalFormat("##########.##");
	public Random ssnRandom = new Random();
	public Random random = new Random();
	public Random randomShares = new Random();
	
	@SuppressWarnings("rawtypes")
	private HashMap symbols = new HashMap();
	private int numberOfPositions = 50;
	
	public CubeTestGenerator() {
		super();
	}
	
	public String nextAccount() {
		return accountids[ random.nextInt(accountids.length -1)];
	}
	
	public int nextSSN() {
		return ssnRandom.nextInt(100000) + 10000000;
	}
	
	public String nextTicker() {
		int index = random.nextInt( tickersymbols.length - 1);
		return tickersymbols[index];
	}
	
	public String nextCountryCode() {
		int index = random.nextInt( countrycodes.length - 1);
		return countrycodes[index];
	}
	
	public int age() {
		return random.nextInt(30) + 20;
	}
	
	public int shares() {
		return randomShares.nextInt(1000) + 20;
	}
	
	public double price() {
		return randomShares.nextDouble() + (randomShares.nextInt(100) + 10);
	}
	
	public String[] getGICSCodes() {
		String[] codes = new String[4];
		int index = random.nextInt(gicsSubIndustries.length - 1);
		String subInd = gicsSubIndustries[index];
		codes[0] = subInd.substring(0,2);
		codes[1] = subInd.substring(0,4);
		codes[2] = subInd.substring(0,6);
		codes[3] = subInd;
		return codes;
	}
	@SuppressWarnings("rawtypes")
	public void generateData(int count, String filename) {
		FileWriter accountWriter = getWriter(filename + "_account_data.dat");
		StringBuffer buf = new StringBuffer();
		for (int idx=0; idx < count; idx++) {
			String account = nextAccount() + idx;
			int ssn = nextSSN();
			generateAccount(buf, account, ssn, idx);
			generatePositions(buf, account, numberOfPositions);
			writeText(accountWriter, buf.toString());
			buf.setLength(0);
		}
		closeWriter(accountWriter);
		
		buf.setLength(0);
		
		FileWriter stockWriter = getWriter(filename + "_stock_data.dat");
		Iterator itr = symbols.keySet().iterator();
		while (itr.hasNext()) {
			String ticker = (String)itr.next();
			Double price = (Double)symbols.get(ticker);
			generateStock(buf,ticker,price.toString());
			generateRating(buf,ticker);
			writeText(stockWriter,buf.toString());
			buf.setLength(0);
		}
		closeWriter(stockWriter);
	}
	
	public void generateAccount(StringBuffer buf, String accountid, int ssn, int counter) {
		buf.append("(Account (accountId \"" + accountid+"\") (firstName \"first" + counter + "\") (middleName \"m" + counter +"\")");
		buf.append("(lastName \"last" + counter + "\") (ssn " + ssn + ") (age " + age() + ") (gender \"m\")	)" + LINEBREAK);
	}
	
	@SuppressWarnings("unchecked")
	public void generatePositions(StringBuffer buf, String accountid, int positions) {
		for (int idx=0; idx < positions; idx++) {
			String ticker = this.nextTicker();
			int shares = shares();
			double price = price();
			symbols.put(ticker, Double.valueOf(price));
			BigDecimal total = new BigDecimal(shares * price);
			String totalText = currency.format(total.doubleValue());
			String[] codes = getGICSCodes();
			buf.append("(Position (accountId \"" + accountid + "\") (ticker \"" + ticker + "\")");
			buf.append("(shares " + shares + ") (total " + totalText + ") ");
			buf.append("(countryCode \"" + nextCountryCode() + "\") ");
			buf.append("(sectorID " + codes[0] + ") (industryGroupID " + codes[1] + ") ");
			buf.append("(industryID " + codes[2] + ") (subIndustryID " + codes[3] + ") )");
			buf.append(LINEBREAK);
		}
	}
	
	public void generateStock(StringBuffer buf, String ticker, String price) {
		buf.append("(Stock (ticker \"" + ticker +"\") (closingPrice " + price + ") (exchange \"NYSE\")");
		buf.append("(closeDate \"2009-01-05\") )");
		buf.append(LINEBREAK);
	}
	
	public void generateRating(StringBuffer buf, String ticker) {
		int index = random.nextInt(7) + 2;
		String rateCode = fitchratingcodes[index];
		String rateValue = String.valueOf(fitchratingvalues[index]);
		buf.append("(Rating (name \"fitch long\") (ticker \"" + ticker + "\") ");
		buf.append("(code \"" + rateCode + "\") (numericValue " + rateValue + ") (ratingType \"long\") )");
		buf.append(LINEBREAK);
	}
	
	public FileWriter getWriter(String filename) {
		FileWriter writer;
		try {
			writer = new FileWriter(filename);
			return writer;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void writeText(FileWriter writer, String text) {
		try {
			writer.write(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeWriter(FileWriter writer) {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args != null && args.length >= 2) {
			String filename = args[0];
			int count = Integer.parseInt(args[1]);
			CubeTestGenerator generator = new CubeTestGenerator();
			generator.generateData(count, filename);
			System.out.println("Done! File " + filename + " saved.");
		} else {
			System.out.println("No parameters!");
		}
	}

}
