/**
 * 
 */
package hack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author prasad
 *
 */
public class TestDataanalyser {

	static Map<String, Long> userVisitMap = new HashMap<String, Long>();
	
	private static final String COMMA = ",";
	
	public static void main(String[] args) {
		for (int i = 0; i <= 255; i++) {
			// String filename =
			// "E:\\Prasad\\hackathon\\Click_Predictions\\train\\train.csv";
			String filename = "/tmp/cltrain/test-";
			if (("" + i).length() == 1) {
				filename = filename + "00" + i + ".csv";
			} else if (("" + i).length() == 2) {
				filename = filename + "0" + i + ".csv";
			} else {
				filename = filename + i + ".csv";
			}
			System.out.println("Train File --" + filename);
			processInputFile(filename);
		}
		
		System.out.println(userVisitMap);
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
		item.cuntry=""+p[1].toUpperCase().trim();
		
		if (userVisitMap.containsKey(item.cuntry)) {
			Long visitCount = userVisitMap.get(item.cuntry) + 1;
			userVisitMap.put(item.cuntry, visitCount);
		} else {
			userVisitMap.put(item.cuntry, 1l);
		}
		
		return item;
	};
}
