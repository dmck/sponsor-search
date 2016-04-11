package parser;


import com.opencsv.*;

import java.util.List;
import java.util.ArrayList;
import java.nio.file.*;
import java.nio.charset.*;
import java.io.*;
import java.util.*;

import org.xml.sax.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class Parser {
	public static List<Legislator> people = new ArrayList<Legislator>();
	public static List<String> idList = new ArrayList<String>();
    
	public Parser() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		
		// Make and sort list of type Legislator
		// - Data obtained from: https://www.govtrack.us/data/congress-legislators/
		// - Note that legislators-current.csv and legislators-historic.csv 
		//   are mutually exclusive. I manually combined them into all.csv
		System.out.println("Making people list...");
		String[] files = new String[2];
		files[0] = "dat/people/legislators-historic.csv";
		files[1] = "dat/people/legislators-current.csv";
        makePeopleList(files);
        System.out.println("Sorting People...");
        Collections.sort(people);
        
        
        /*
         * - Datasets for each session must be located in "dat/"
         *   within a folder with their session number, e.g. "112"
         *   for: "dat/112/"
         * - Each session is approximately 50 megabytes and 9,000 files.
         * - Data obtained from: https://www.govtrack.us/developers/rsync
         * - The command used for each session (e.g. 112) (using linux) was:
         *   rsync -avz --delete --delete-excluded govtrack.us::govtrackdata/us/112/bills .
         */
        long startTime = System.nanoTime();
        List<Bill> bills = new ArrayList<Bill>();
		for (int i = 104; i < 115; i++) {
	        System.out.println("Processing bills of " + i + ".d congress...");
			List<Bill> yearBills = makeBillList("dat/" + i);
			bills.addAll(yearBills);
		}
		long endTime = System.nanoTime();
		double estTime = (endTime-startTime) / 1000000000.0;
        System.out.println("Completed processing of " + bills.size() 
        	+ " bills in "+ estTime + "seconds.");
		
        /*
         * MAIN LOOP FOR ASSOCIATING BILLS AND CONGRESSPERSONS
         */
        startTime = System.nanoTime();
		List<String> lines = new ArrayList<String>();
		System.out.println("Associating " + bills.size() + " bills...");
		//HashMap billSponsors = new HashMap();
		// Add column labels for CSV:
		lines.add("STATE" + "," + "REPRESENTATIVE ID"
		  + "," + "LAST NAME" + "," + "FIRST NAME" + "," + "ENACTED"
		  + "," + "SESSION" + "," + "TYPE" + "," + "BILL NUMBER");
        for (Bill bill : bills) {
        	int billIndex = Collections.binarySearch(idList, bill.getSponsor());
        	if(billIndex > people.size() || billIndex < 0) {
        		System.out.println("Error: No sponsor for " + bill.getSession()
        				+ "\t" + bill.getType() + "\t" + bill.getNumber()
        				+ "\tEnacted: " + bill.getEnacted());
        		System.out.println("Saving sponsor cells as 'NONE'.");
        		lines.add("NONE"+","+"NONE"
        				+","+"NONE"+","+"NONE"+","+bill.getEnacted()
        				+","+bill.getSession()+","+bill.getType()+","+bill.getNumber());
        	} else {
        		Legislator person = people.get(billIndex);

        		lines.add(person.getState()+","+person.getId()
        		  +","+person.getLastName()+","+person.getFirstName()+","+bill.getEnacted()
        		  +","+bill.getSession()+","+bill.getType()+","+bill.getNumber());
        	}

    		//person.getBills().add(bill);
    		//System.out.println(person.getState() + "\t" + bill.getType() + "." + bill.getNumber());

        }
    	endTime = System.nanoTime();
    	estTime = endTime - startTime;
        System.out.println("Completed association in "+ estTime + "seconds.");
        
        // Print all lines (for debugging purposes)
        //for (String line : lines) { System.out.println(line);}
        
        // Save output file
        System.out.println("Saving " + lines.size() + "lines to out.csv...");
        Path file = Paths.get("out.csv");
		Files.write(file, lines, Charset.forName("UTF-8"));
		System.out.println("Completed.");
	}
	
	public static void makePeopleList(String[] paths) throws IOException {
		for (String path : paths) {
			CSVReader reader = new CSVReader(new FileReader(path));
		    String[] nextLine;
		    int i = 0; // Header of csv is line [0], so ignore it.
		    while ((nextLine = reader.readNext()) != null) {
		    	String id = nextLine[23];
		    	int idIndex = Collections.binarySearch(idList, id);
		    	if (i > 0) {
		    		//System.out.println(idList.get(idIndex));
		    		if (idIndex > 0) {
		    			System.out.println("Ignoring duplicate person id# " + idList.get(idIndex));
		    		} else {
				    	people.add(new Legislator());
				    	int person = people.size()-1;
				    	people.get(person).setLastName(nextLine[0]);
				    	people.get(person).setFirstName(nextLine[1]);
				    	people.get(person).setState(nextLine[5]);
				    	people.get(person).setId(id);
				    	idList.add(id);
				    	Collections.sort(idList);
		    		}
		    	}
		    	i++; // Header of csv is line [0], so ignore it.
		    }
		    reader.close();
		}

	}
	
	public static List<Bill> makeBillList(String path) throws IOException {
		List<Bill> bills = new ArrayList<Bill>();
		Files.walk(Paths.get(path)).forEach(filePath -> {
		    if (Files.isRegularFile(filePath)) {
		    	String pathName = filePath.toString();
		    	DocumentBuilder dBuilder = null;
		    	Document doc = null;
		    	
		    	// http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
		    	File file = new File(pathName);
		    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    	try {
		    		dBuilder = dbFactory.newDocumentBuilder();
		    	} catch ( ParserConfigurationException e ) { 
		    	     e. printStackTrace( ); 
		    	}
		    	
		    	try {
		    		doc = dBuilder.parse(file);
		    	} catch ( IOException e) {
		    		e. printStackTrace();
		    	} catch ( SAXException e) {
		    		e. printStackTrace();
		    	}
		    	
		    	doc.getDocumentElement().normalize();
		    	
		    	String session = doc.getDocumentElement().getAttribute("session");
		    	String type = doc.getDocumentElement().getAttribute("type");
		    	String number = doc.getDocumentElement().getAttribute("number");
		    	boolean enacted = false;
		    	NodeList enactedNodes = doc.getDocumentElement().getElementsByTagName("enacted");
		    	if (enactedNodes.getLength() > 0) { enacted = true; }
		    	String enactedAs = "Not Enacted.";
		    	if (enacted) {
		    		enactedAs = enactedNodes.item(0).getAttributes().getNamedItem("number").getTextContent();
		    	}
		    	
		    	String sponsor = "error none in xml";
		    	Element docElement = doc.getDocumentElement();
		    	NodeList sponsorNodeList = docElement.getElementsByTagName("sponsor");
		    	if (sponsorNodeList.getLength() > 0) {
		    		Node sponsorNode = sponsorNodeList.item(0);
		    		//System.out.println(session + "\t" + type + "\t" + number);
		    		NamedNodeMap sponsorNodeMap = sponsorNode.getAttributes();
		    		if(sponsorNodeMap.getLength() > 0 ) {
		    			sponsor = sponsorNodeMap.getNamedItem("id").getTextContent();
		    		}
		    	}
		    	
		    	Bill bill = new Bill(sponsor, session, type, number, enacted);
		    	
		    	if (bill.getEnacted() ) {
		    		bill.setEnactedNumber(enactedAs);
		    	}
		    	
		    	bills.add(bill);
		    	
		    } // End if file is regular file.
		}); // End Files.walk 
		return bills;
	}
}
