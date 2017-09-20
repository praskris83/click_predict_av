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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import weka.core.Instances;

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
			inputList = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
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
		item.id=p[0];
		item.cuntry=p[1];
		item.carrier=p[2];
		item.traffic=p[3];
		item.clickDate=p[4];
		item.device=p[5];
		item.browser=p[6];
		item.os=p[7];
		item.reffer=p[8];
		item.ip=p[p.length-8];
		item.conversion=p[p.length-7];
		item.cDate=p[p.length-6];
		item.pay=p[p.length-5];
		item.pub=p[p.length-4];
		item.sub=p[p.length-3];
		item.add=p[p.length-2];
		item.frd=p[p.length-1];
		
		return item;
	};

}
