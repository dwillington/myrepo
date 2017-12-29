package com.coinmarketcapohshit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App 
{
	public void writeLine(BufferedWriter bufferedWriter, String line) throws IOException {	
	    bufferedWriter.write(line);
	    bufferedWriter.newLine();
	}
	
	public double getOhShitFactor()
	{
		double randomNum = ThreadLocalRandom.current().nextInt(6000, 8000 + 1);
//		Random rand = new Random();
//	    int randomNum = rand.nextInt((8 - 6) + 1) + 6;

		return (double) (randomNum / 10000);
	}
	
	public String getOhShitFactorInPercent(double ohshitFactor)
	{
		DecimalFormat df = new DecimalFormat("0.00");
		df.setMaximumFractionDigits(2);
		String retValue = "-" + df.format((1 - ohshitFactor) * 100) + "%";
		return retValue;
	}

	public String getReducedByOhShitFactor(String value, double ohShitFactor)
	{
		String retValue = value;

		Double doubleValue = Double.parseDouble(value);
		doubleValue = ohShitFactor * doubleValue;

		if(doubleValue > 1.00)
		{
			DecimalFormat df = new DecimalFormat("0.00");
			df.setMaximumFractionDigits(2);
			retValue = df.format(doubleValue);
		} else {
			DecimalFormat df = new DecimalFormat("0.000000");
			retValue = df.format(doubleValue);
		}

		return retValue;
	}

	public String parsePrice(String line)
	{
        //<a href="https://coinmarketcap.com/currencies/bitcoin/#markets" class="price" data-usd="4141.9" data-btc="1.0">$2141.90</a>
		final Pattern pattern = Pattern.compile("<a.*>[$](.+?)</a>");
//		final Pattern pattern = Pattern.compile("^.*data-usd=\"(.+?)\"[ ]data.*$");
		final Matcher matcher = pattern.matcher(line);
		matcher.find();
		String retValue = matcher.group(1);

		return retValue;
	}
	
	public String parsePercent(String line)
	{
        //<td class="no-wrap percent-24h  negative_change text-right" data-usd="-0.17" data-btc="0.00">-0.17%</td>		 
		final Pattern pattern = Pattern.compile("<td.*>(.+?)</td>");
		final Matcher matcher = pattern.matcher(line);
		matcher.find();
		String retValue = matcher.group(1);

		return retValue;
	}

	public static void main( String[] args )
    {
        App app = new App();

        String fileName = "data/Cryptocurrency Market Capitalizations _ CoinMarketCap.html";
        String fileNameWrite = "data/Cryptocurrency Market Capitalizations _ CoinMarketCap.ohshit.html";

        String line = null;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            FileWriter fileWriter = new FileWriter(fileNameWrite);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            while((line = bufferedReader.readLine()) != null) {

            	if(line.contains("id=\"id-")) {
                    while((line = bufferedReader.readLine()) != null) {
                    	double ohShitFactor = app.getOhShitFactor();
                    	if(line.contains("class=\"price\"")) {

                        	String price = app.parsePrice(line);
                    		line = line.replaceAll(price, app.getReducedByOhShitFactor(price, ohShitFactor));

                    	}
                    	else if(line.contains("class=\"no-wrap percent-24h")) {
                        	System.out.println(line);
                        	String percent = app.parsePercent(line);
                    		line = line.replaceAll(percent, app.getOhShitFactorInPercent(ohShitFactor));
                    		System.out.println(line);
                    	}
                    	app.writeLine(bufferedWriter, line);
                    }
            	}
            	else
            	{
                	app.writeLine(bufferedWriter, line);
            	}
            
            }   



            bufferedWriter.close();
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");                  
            ex.printStackTrace();
        }
    }
}
