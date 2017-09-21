package hack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author prasad
 *
 */
public class FileHelper {

	private static final String COMMA = ",";

	public static void main(String args[]) {

		String fileName = "c://lines.txt";
		List<String> list = new ArrayList<>();

		try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {

			// br returns as stream and convert it into a List
			list = br.lines().collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}

		list.forEach(System.out::println);

	}

	public static List<Data> processInputFile(String inputFilePath) {
		List<Data> inputList = new LinkedList<Data>();
		try {
			File inputF = new File(inputFilePath);
			InputStream inputFS = new FileInputStream(inputF);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
			// skip the header of the csv
			inputList = br.lines().skip(1).filter(line->line.contains(InstanceHelper.country+",")).map(mapToItem).collect(Collectors.toList());
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inputList;
	}

	private static Function<String, Data> mapToItem = (line) -> {
		String[] p = line.split(COMMA);// a CSV has comma separated lines
//		System.out.println(Arrays.asList(p));
//		if(p.length > 17){
//			System.out.println(line);
//		}
		Data item = new Data();
		// more initialization goes here
		item.id=""+p[0].toLowerCase().trim();
		item.cuntry=""+p[1].toLowerCase().trim();
		item.carrier=""+p[2].toLowerCase().trim();
		item.traffic=""+p[3].toLowerCase().trim();
		item.clickDate=""+p[4].toLowerCase().trim();
		item.device=""+p[5].toLowerCase().trim();
		item.browser=""+p[6].toLowerCase().trim();
		item.os=""+p[7].toLowerCase().trim();
		item.reffer=""+p[8].toLowerCase().trim();
		item.ip=""+p[p.length-8].toLowerCase().trim();
		item.conversion=""+p[p.length-7].toLowerCase().trim();
		item.cDate=""+p[p.length-6].toLowerCase().trim();
		item.pay=""+p[p.length-5].toLowerCase().trim();
		item.pub=""+p[p.length-4].toLowerCase().trim();
		item.sub=""+p[p.length-3].toLowerCase().trim();
		item.add=""+p[p.length-2].toLowerCase().trim();
		item.frd=""+p[p.length-1].toLowerCase().trim();
		
		return item;
	};

}
