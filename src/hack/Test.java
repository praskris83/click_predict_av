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

	public static final String country = "BD";

	static Map<Long, Long> userVisitMap = new HashMap<Long, Long>();
	static List<Data> reader = new ArrayList<Data>();

	public static void main(String[] args) {

		try {
			Instances testDataSet = buildTestData();
			/*
			 * Now we tell the data set which attribute we want to classify, in
			 * our case, we want to classify the first column: survived
			 */
			Attribute testAttribute = testDataSet.attribute(15);
			testDataSet.setClass(testAttribute);
			// testDataSet.
			// testDataSet.
			// testDataSet.deleteStringAttributes();
			// Remove remove = new Remove();
			// remove.setAttributeIndicesArray(new int[]{34});
			// remove.setInvertSelection(false);
			// remove.setInputFormat(testDataSet);
			//
			// Instances filtered = Filter.useFilter(data, remove);
			/*
			 * Now we read in the serialized model from disk
			 */

			Classifier classifier = (Classifier) SerializationHelper.read("click_predict_" + country + ".model");
//			Classifier classifier1 = (Classifier) SerializationHelper.read("click_predict_" + "True" + ".model");
			Enumeration testInstances = testDataSet.enumerateInstances();
			int i = 0;
			while (testInstances.hasMoreElements()) {
				Instance instance = (Instance) testInstances.nextElement();
				double classification = classifier.classifyInstance(instance);
//				double classification1 = classifier1.classifyInstance(instance);
				System.out.println(classification);
				reader.get(i).setClas(classification);
//				reader.get(i).setCost(classification1);
				i++;
			}
			System.out.println(reader);
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
		addNewAttrs(atts);
		// atts.add(new Attribute("ID", true));
		// AddID addId = new AddID();
		// atts.add(addId);

		Instances data = new Instances("Clicks_Test", atts, 10);
		data.setClassIndex(15);

		try {
			for (int i = 0; i <= 255; i++) {
				// String filename =
				// "E:\\Prasad\\hackathon\\Click_Predictions\\test\\test-";
				String filename = "/tmp/cltrain/train-";
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
				reader = FileHelper.processInputFile(filename, country);
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

	private static void addNewAttrs(ArrayList<Attribute> atts) {
		atts.add(new Attribute("Generic_device", false));
		atts.add(new Attribute("Samsung_device", false));
		atts.add(new Attribute("Apple_device", false));
		atts.add(new Attribute("Symphony_device", false));
		atts.add(new Attribute("Sony_device", false));
		atts.add(new Attribute("other_device", false));
		atts.add(new Attribute("46.0.2490.76_Browser", false));
		atts.add(new Attribute("android_webkit_Browser", false));
		atts.add(new Attribute("chrome_Browser", false));
		atts.add(new Attribute("opera_Browser", false));
		atts.add(new Attribute("iphone_Browser", false));
		atts.add(new Attribute("Other_Browser", false));
		atts.add(new Attribute("Android_OS", false));
		atts.add(new Attribute("Ios_OS", false));
		atts.add(new Attribute("Mac_OS", false));
		atts.add(new Attribute("Windows_OS", false));
		atts.add(new Attribute("Fire_OS", false));
		atts.add(new Attribute("Other_OS", false));

	}

	protected static void buildTrainEntry(List<String> allCountryCodes, Instances data, List<Data> datas)
			throws IOException, ParseException {
		// String header = datas.next();
		// while (datas.hasNext()) {
		for (Data line : datas) {
			Instance values = new DenseInstance(34);
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
				setDeviceAttrs(line.device, values);
				values.setValue(6, Helper.getHashFromStr(line.browser));
				setBrowserAttrs(line.browser, values);
				values.setValue(7, Helper.getHashFromStr(line.os));
				setOsAttrs(line.os, values);
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
				// values.setValue(15, Long.parseLong(line.id));
				// values.setValue(34, Long.parseLong(line.id));
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

		atts.add(new Attribute("Conversion", Arrays.asList(new String[] { "false", "true" })));

	}

	private static void setOsAttrs(String Os, Instance values) {
		// atts.add(new Attribute("Android_OS", false));
		// atts.add(new Attribute("Ios_OS", false));
		// atts.add(new Attribute("Mac_OS", false));
		// atts.add(new Attribute("Windows_OS", false));
		// atts.add(new Attribute("Fire_OS", false));
		// atts.add(new Attribute("Other_OS", false));

		if (StringUtils.containsIgnoreCase(Os, "android")) {
			setDeviceValues(1, 0, 0, 0, 0, 0, 28, values);
		} else if (StringUtils.containsIgnoreCase(Os, "ios")) {
			setDeviceValues(0, 1, 0, 0, 0, 0, 28, values);
		} else if (StringUtils.containsIgnoreCase(Os, "Mac")) {
			setDeviceValues(0, 0, 1, 0, 0, 0, 28, values);
		} else if (StringUtils.containsIgnoreCase(Os, "Window")) {
			setDeviceValues(0, 0, 0, 1, 0, 0, 28, values);
		} else if (StringUtils.containsIgnoreCase(Os, "fire")) {
			setDeviceValues(0, 0, 0, 0, 1, 0, 28, values);
		} else {
			setDeviceValues(0, 0, 0, 0, 0, 1, 28, values);
		}
	}

	private static void setBrowserAttrs(String browser, Instance values) {
		// atts.add(new Attribute("46.0.2490.76_Browser", false));
		// atts.add(new Attribute("android_webkit_Browser", false));
		// atts.add(new Attribute("chrome_Browser", false));
		// atts.add(new Attribute("opera_Browser", false));
		// atts.add(new Attribute("iphone_Browser", false));
		// atts.add(new Attribute("Other_Browser", false));
		if (StringUtils.containsIgnoreCase(browser, "46.0.2490.76")) {
			setDeviceValues(1, 0, 0, 0, 0, 0, 22, values);
		} else if (StringUtils.containsIgnoreCase(browser, "android")) {
			setDeviceValues(0, 1, 0, 0, 0, 0, 22, values);
		} else if (StringUtils.containsIgnoreCase(browser, "chrom")) {
			setDeviceValues(0, 0, 1, 0, 0, 0, 22, values);
		} else if (StringUtils.containsIgnoreCase(browser, "opera")) {
			setDeviceValues(0, 0, 0, 1, 0, 0, 22, values);
		} else if (StringUtils.containsIgnoreCase(browser, "iphone")) {
			setDeviceValues(0, 0, 0, 0, 1, 0, 22, values);
		} else {
			setDeviceValues(0, 0, 0, 0, 0, 1, 22, values);
		}
	}

	private static void setDeviceAttrs(String device, Instance values) {
		// atts.add(new Attribute("Generic_device", false));
		// atts.add(new Attribute("Samsung_device", false));
		// atts.add(new Attribute("Apple_device", false));
		// atts.add(new Attribute("Symphony_device", false));
		// atts.add(new Attribute("Sony_device", false));
		// atts.add(new Attribute("other_device", false));

		if (StringUtils.containsIgnoreCase(device, "generic")) {
			setDeviceValues(1, 0, 0, 0, 0, 0, 16, values);
		} else if (StringUtils.containsIgnoreCase(device, "samsung")) {
			setDeviceValues(0, 1, 0, 0, 0, 0, 16, values);
		} else if (StringUtils.containsIgnoreCase(device, "apple")) {
			setDeviceValues(0, 0, 1, 0, 0, 0, 16, values);
		} else if (StringUtils.containsIgnoreCase(device, "symphony")) {
			setDeviceValues(0, 0, 0, 1, 0, 0, 16, values);
		} else if (StringUtils.containsIgnoreCase(device, "sony")) {
			setDeviceValues(0, 0, 0, 0, 1, 0, 16, values);
		} else {
			setDeviceValues(1, 0, 0, 0, 0, 1, 16, values);
		}
	}

	private static void setDeviceValues(int i, int j, int k, int l, int m, int n, int index, Instance values) {
		values.setValueSparse(index++, i);
		values.setValueSparse(index++, j);
		values.setValueSparse(index++, k);
		values.setValueSparse(index++, l);
		values.setValueSparse(index++, m);
		values.setValueSparse(index++, n);
	}

}
