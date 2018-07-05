/********************************************************************
 *  This is a tool to convert the file system repo containing 
 *  .properties files into a .csv containing all relevant 
 *  information regarding each and every key value pair in the
 *  file system repo. 
 *  The .csv generated as output is intended to be used to
 *  import all properties in the file system repo into db2(database).
 ********************************************************************/
import java.io.BufferedWriter;
import java.io.File;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;


public class FileParser_Main {
	private static String subdomain;
	private static String property;
	private static String prop_value;
	
	// This is the path where the .csv file would be stored.
	private static final String SAMPLE_CSV_FILE_PATH = "./properties.csv";
	
	public static void main(String[] args) throws Exception {
		java.util.Date date= new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());

		BufferedWriter writer = Files.newBufferedWriter(Paths.get(SAMPLE_CSV_FILE_PATH));
		CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("DOMAIN", "SUBDOMAIN", "KEY", "VALUE","LABEL","PRIORITY","APPLICATION", "UpdateDttm", "UpdateUser","CoreFlag"));
		
		// This is the path from where the file system repo would be picked
		String FilePath="C:\\svnviews\\config\\src\\main\\resources\\config";
		
		Path p = Paths.get(FilePath);
		int StaticPathArrayLength =FilePath.split(Matcher.quoteReplacement(System.getProperty("file.separator"))).length;
		Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException{
				if ( (getFileExtension(file.toFile().getName()).equals("properties"))) {
					File curr_file= file.toFile();
					Scanner parser = new Scanner(curr_file);
					while(parser.hasNextLine()) {
						String new_line=parser.nextLine();
						try {
							if(new_line.indexOf("=")!=-1 && new_line.charAt(0) != '#') {
								property = new_line.split("=")[0];
								prop_value = new_line.split("=")[1];
								String homePath = file.getParent().toString();
								String[] PathArray= homePath.split(Matcher.quoteReplacement(System.getProperty("file.separator")));
								String domain = PathArray[StaticPathArrayLength];
								if (PathArray.length > StaticPathArrayLength+1) {
									String temp=PathArray[StaticPathArrayLength+1];
									int i=StaticPathArrayLength+2;
									while (i < PathArray.length) {
										temp=temp.concat("_");
										temp=temp.concat(PathArray[i]);
										i++;
									}
									subdomain=temp;
								}
								else {
									subdomain = "default";
								}
								String key = property;
								String value = prop_value;
								String application = curr_file.getName().split("\\.")[0];
							
								String label = "defaultAffiliate";
								Integer priority = PriorityCheck(application);
								csvPrinter.printRecord(domain,subdomain,key,value,label,priority,application,timestamp.toLocalDateTime().toString().replaceAll("T", " ").replaceAll(":", "\\."),"Tool",1);
								

							}
						}
						catch (ArrayIndexOutOfBoundsException e) {
							continue;	
						}
						
					}
					parser.close();
					csvPrinter.flush();
				}
				return FileVisitResult.CONTINUE;
			}
		});
		

		System.out.println("Success");

	}
	
	private enum PRIORITY{
		platform (15),
		corpbanking(14),
		international(13),
		payments(12),
		services(11),
		soa(10), 
		validation(9), 
		deployer(8),
		custom (7),
		deploy(6),
		mobile (5),
		tradefinance (4),
		rwd (3), 
		override(2),
		hosting (1)
		;
		private final int pr_rank;

	    private PRIORITY(int pr_rank) {
	        this.pr_rank = pr_rank;
	    }
	}
	
	public static Integer PriorityCheck(String application) {
		for (PRIORITY pr : PRIORITY.values()) {
			if (application.equals(pr.toString())) {
				return pr.pr_rank;
			}
		}
		return 0;
	}
		
 
	
	public static String getFileExtension(String fullName) {
	    String fileName = new File(fullName).getName();
	    int dotIndex = fileName.lastIndexOf('.');
	    return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
	}
	
	
}
