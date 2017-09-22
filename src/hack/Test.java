package hack;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

/**
 * @author Prasad
 *
 */
public class Test {

	public static final String country = "IN";

	static Map<Long, Long> userVisitMap = new HashMap<Long, Long>();
	
	public static void main(String[] args) {

		try {
			Instances testDataSet = buildTestData();
			/*
			 * Now we tell the data set which attribute we want to classify, in
			 * our case, we want to classify the first column: survived
			 */
			Attribute testAttribute = testDataSet.attribute(15);
			testDataSet.setClass(testAttribute);
			/*
			 * Now we read in the serialized model from disk
			 */

			Classifier classifier = (Classifier) SerializationHelper.read("click_predict_"
					+country+ ".model");

			Enumeration testInstances = testDataSet.enumerateInstances();
			while (testInstances.hasMoreElements()) {
				Instance instance = (Instance) testInstances.nextElement();
				double classification = classifier.classifyInstance(instance);
				System.out.println(instance.stringValue(15) + "," +classification);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Instances buildTestData() {
		ArrayList<Attribute> atts = new ArrayList<Attribute>();

		// Country Codes
		List<String> allCountryCodes = Helper.getAllCodes();

		buildAttrs(atts, allCountryCodes);
//		addNewAttrs(atts);
		Instances data = new Instances("Clicks_Test", atts, 10);
		data.setClassIndex(15);

		try {
			for (int i = 623; i <= 633; i++) {
				// String filename =
				// "E:\\Prasad\\hackathon\\Click_Predictions\\train\\train-";
				String filename = "/tmp/cltrain/test-";
				if (("" + i).length() == 1) {
					filename = filename + "00" + i + ".csv";
				} else if (("" + i).length() == 2) {
					filename = filename + "0" + i + ".csv";
				} else {
					filename = filename + i + ".csv";
				}
				System.out.println("Test Files " + filename);
				// CSVReader reader = new CSVReader(new FileReader(filename));
				// LineIterator reader = FileUtils.lineIterator(new
				// File(filename), "UTF-8");
				List<Data> reader = FileHelper.processInputFile(filename);
				// 203.88.6.38
				try {
					buildTrainEntry(allCountryCodes, data, reader);
					// dummy(allCountryCodes, data, reader);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	protected static void buildTrainEntry(List<String> allCountryCodes, Instances data, List<Data> datas)
			throws IOException, ParseException {
		// String header = datas.next();
		// while (datas.hasNext()) {
		for (Data line : datas) {
			Instance values = new DenseInstance(17);
			try {
				// String[] lineData = datas.next().split(",");
				// System.out.println(Arrays.asList(lineData));
				values.setDataset(data);
				values.setValue(0, (line.cuntry.isEmpty() || !(allCountryCodes.contains(line.cuntry)) ? "--"
						: line.cuntry.trim().toUpperCase()));
				values.setValue(1, Helper.getNumberFromStr(line.carrier));
				values.setValue(2, (StringUtils.isNotBlank(line.traffic) ? line.traffic : "g"));
				// String clickDate = line[4];
				// String clickTime = "UK";
				// if(StringUtils.isBlank(clickDate)){
				// clickDate="2099-01-01";
				// }else{
				// clickDate = clickDate.split(" ")
				// }
				double clkdate = data.attribute("ClickDate").parseDate(Helper.getDateStr1(line.clickDate));
				// values.setValue(atts.get(3), "2006-08-03");
				values.setValue(3, clkdate);
				values.setValue(4, Helper.getTimeStr1(line.clickDate));
				values.setValue(5, Helper.getHashFromStr(line.device));
//				setDeviceAttrs(line.device, values);
				values.setValue(6, Helper.getHashFromStr(line.browser));
//				setBrowserAttrs(line.browser, values);
				values.setValue(7, Helper.getHashFromStr(line.os));
//				setOsAttrs(line.os, values);
				values.setValue(8, Helper.getHashFromURL(line.reffer));
				long usrIPStr = Helper.getUsrIPStr(line.ip);
				if (userVisitMap.containsKey(usrIPStr)) {
					Long visitCount = userVisitMap.get(usrIPStr) + 1;
					values.setValue(10, visitCount);
					userVisitMap.put(usrIPStr, visitCount);
				} else {
					userVisitMap.put(usrIPStr, 1l);
					values.setValue(10, 1l);
				}
				values.setValue(9, usrIPStr);
				values.setValue(11, Helper.getNumberFromStr(line.pub));
				values.setValue(12, Helper.getNumberFromStr(line.sub));
				values.setValue(13, Helper.getNumberFromStr(line.add));
				values.setValue(14, Double.valueOf(line.frd));
				values.setValue(15, Long.parseLong(line.id));
				// values.setValue(15, "TRUE");
				// values.setDataset(data);
				// values.setValue(15, "");
				// values.setValue(16, "");
				data.add(values);
				// break;
			} catch (Exception e) {
				e.printStackTrace();
				values.setValue(15, "false");
				data.add(values);
				continue;
			}
		}
	}

	protected static void buildAttrs(ArrayList<Attribute> atts, List<String> allCountryCodes) {
		atts.add(new Attribute("Country", allCountryCodes));

		atts.add(new Attribute("Carrier", false));

		atts.add(new Attribute("TrafficType", Arrays.asList(new String[] { "m", "a", "g" })));

		atts.add(new Attribute("ClickDate", "yyyy-mm-dd"));

		atts.add(new Attribute("TrafficTime", Arrays.asList(new String[] { "BO", "IO", "AO", "NA" })));

		atts.add(new Attribute("Device", false));

		atts.add(new Attribute("Browser", false));

		atts.add(new Attribute("OS", false));

		atts.add(new Attribute("RefererUrl", false));

		atts.add(new Attribute("UserIp", false));

		atts.add(new Attribute("HasSeenBefore", false));

		atts.add(new Attribute("publisherId", false));

		atts.add(new Attribute("subPublisherId", false));

		atts.add(new Attribute("advertiserCampaignId", false));

		atts.add(new Attribute("Fraud", false));
		
		atts.add(new Attribute("id", false));

		atts.add(new Attribute("Conversion", Arrays.asList(new String[] { "false", "true" })));
	}
}
