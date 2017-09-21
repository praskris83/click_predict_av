package hack;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVReader;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

/**
 * @author Prasad
 *
 */
public class InstanceHelper {

	static Map<Long, Long> userVisitMap = new HashMap<Long, Long>();

	public static void main(String[] args) {
		// ArrayList<Attribute> attrs = buildAttrs();
		//
		// // Create an empty training set
		// Instances isTrainingSet = new Instances("Rel", attrs, 16);
		// // Set class index
		// isTrainingSet.setClassIndex(15);
		// String[] line;
		// createInstance(line,attrs,isTrainingSet);

		buildTrainFile();
	}

	public static void buildTrainFile() {

		ArrayList<Attribute> atts = new ArrayList<Attribute>();

		// Country Codes
		List<String> allCountryCodes = Helper.getAllCodes();

		buildAttrs(atts, allCountryCodes);

		addNewAttrs(atts);

		Instances data = new Instances("Clicks_Train", atts, 10);
		data.setClassIndex(15);

		try {
			// 203.88.6.38
			for (int i = 500; i <= 622; i++) {
//				 String filename =
//				 "E:\\Prasad\\hackathon\\Click_Predictions\\train\\train-";
				String filename = "/tmp/cltrain/train-";
				if (("" + i).length() == 1) {
					filename = filename + "00" + i + ".csv";
				} else if (("" + i).length() == 2) {
					filename = filename + "0" + i + ".csv";
				} else {
					filename = filename + i + ".csv";
				}
				System.out.println("Train File --" + filename);
//				LineIterator it = FileUtils.lineIterator(new File(filename), "UTF-8");
				List<Data> datas = FileHelper.processInputFile(filename);
				// CSVReader reader = new CSVReader(new FileReader(filename));
				// CSVReader reader = new CSVReader(new
				// FileReader("train-628.csv"));
				try {
					buildTrainEntry(allCountryCodes, data, datas);
				} catch (Exception e) {
					e.printStackTrace();
				} 
				// StringToWordVector filter = new StringToWordVector();
			}
			// System.out.println(data);
			// Create a naïve bayes classifier

			Classifier cModel = (Classifier) new Logistic();
			try {
				cModel.buildClassifier(data);

				/*
				 * We are done training the classifier, so now we serialize it
				 * to disk
				 */
				SerializationHelper.write("click_predict6.model", cModel);
				System.out.println("Saved trained model to click_predict6.model");

				Instances testData = buildTestData();
				// Test the model
				Evaluation eTest = new Evaluation(testData);
				eTest.evaluateModel(cModel, testData);

				// Print the result à la Weka explorer:
				String strSummary = eTest.toSummaryString();
				System.out.println(strSummary);

				// Get the confusion matrix
				double[][] cmMatrix = eTest.confusionMatrix();

				for (double[] row : cmMatrix) {
					printRow(row);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static void buildTrainEntry(List<String> allCountryCodes, Instances data, List<Data> datas)
			throws IOException, ParseException {
//		String header = datas.next();
//		while (datas.hasNext()) {
		for(Data line: datas){
			Instance values = new DenseInstance(34);
			try {
//				String[] lineData = datas.next().split(",");
				// System.out.println(Arrays.asList(lineData));
				values.setDataset(data);
				values.setValue(0, (line.cuntry.isEmpty() || !(allCountryCodes.contains(line.cuntry)) ? "--"
						: line.cuntry.trim().toUpperCase()));
				values.setValue(1, Helper.getNumberFromStr(line.carrier));
				values.setValue(2, (StringUtils.isNotBlank(line.traffic) ? line.traffic : "G"));
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
				values.setValue(15, line.conversion.toUpperCase().trim());
				// values.setValue(15, "TRUE");
				// values.setDataset(data);
				// values.setValue(15, "");
				// values.setValue(16, "");
				data.add(values);
				// break;
			} catch (Exception e) {
				e.printStackTrace();
				values.setValue(15, "FALSE");
				data.add(values);
				continue;
			}
		}
	}

	public static void printRow(double[] row) {
		for (double i : row) {
			System.out.print(i);
			System.out.print("\t");
		}
		System.out.println();
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

	protected static void buildAttrs(ArrayList<Attribute> atts, List<String> allCountryCodes) {
		atts.add(new Attribute("Country", allCountryCodes));

		atts.add(new Attribute("Carrier", false));

		atts.add(new Attribute("TrafficType", Arrays.asList(new String[] { "M", "A", "G" })));

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

		atts.add(new Attribute("Conversion", Arrays.asList(new String[] { "FALSE", "TRUE" })));
	}

	public static Instances buildTestData() {
		ArrayList<Attribute> atts = new ArrayList<Attribute>();

		// Country Codes
		List<String> allCountryCodes = Helper.getAllCodes();

		buildAttrs(atts, allCountryCodes);
		addNewAttrs(atts);
		Instances data = new Instances("Clicks_Test", atts, 10);
		data.setClassIndex(15);

		try {
			for (int i = 623; i <= 633; i++) {
//				 String filename =
//				 "E:\\Prasad\\hackathon\\Click_Predictions\\train\\train-";
				String filename = "/tmp/cltrain/train-";
				if (("" + i).length() == 1) {
					filename = filename + "00" + i + ".csv";
				} else if (("" + i).length() == 2) {
					filename = filename + "0" + i + ".csv";
				} else {
					filename = filename + i + ".csv";
				}
				System.out.println("Eval Files " + filename);
				// CSVReader reader = new CSVReader(new FileReader(filename));
//				LineIterator reader = FileUtils.lineIterator(new File(filename), "UTF-8");
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

	protected static void dummy(List<String> allCountryCodes, Instances data, CSVReader reader)
			throws IOException, ParseException {
		String[] line;
		line = reader.readNext();
		while ((line = reader.readNext()) != null) {
			// System.out.println(Arrays.asList(line));
			Instance values = new DenseInstance(34);
			values.setDataset(data);
			values.setValue(0,
					(line[1].isEmpty() || !(allCountryCodes.contains(line[1])) ? "--" : line[1].trim().toUpperCase()));
			values.setValue(1, Helper.getNumberFromStr(line[2]));
			values.setValue(2, (StringUtils.isNotBlank(line[3]) ? line[3].trim() : "G"));
			// String clickDate = line[4];
			// String clickTime = "UK";
			// if(StringUtils.isBlank(clickDate)){
			// clickDate="2099-01-01";
			// }else{
			// clickDate = clickDate.split(" ")
			// }
			double clkdate = data.attribute("ClickDate").parseDate(Helper.getDateStr1(line[4]));
			// values.setValue(atts.get(3), "2006-08-03");
			values.setValue(3, clkdate);
			values.setValue(4, Helper.getTimeStr1(line[4]));
			values.setValue(5, Helper.getHashFromStr(line[5]));
			setDeviceAttrs(line[5], values);
			values.setValue(6, Helper.getHashFromStr(line[6]));
			setBrowserAttrs(line[6], values);
			values.setValue(7, Helper.getHashFromStr(line[7]));
			setOsAttrs(line[7], values);
			values.setValue(8, Helper.getHashFromURL(line[8]));
			long usrIPStr = Helper.getUsrIPStr(line[9]);
			if (userVisitMap.containsKey(usrIPStr)) {
				Long visitCount = userVisitMap.get(usrIPStr) + 1;
				values.setValue(10, visitCount);
				userVisitMap.put(usrIPStr, visitCount);
			} else {
				userVisitMap.put(usrIPStr, 1l);
				values.setValue(10, 1l);
			}
			values.setValue(9, usrIPStr);
			values.setValue(11, Helper.getNumberFromStr(line[13]));
			values.setValue(12, Helper.getNumberFromStr(line[14]));
			values.setValue(13, Helper.getNumberFromStr(line[15]));
			values.setValue(14, Double.valueOf(line[16]));
			values.setValue(15, line[10].toUpperCase().trim());
			// values.setValue(15, "TRUE");
			// values.setDataset(data);
			// values.setValue(15, "");
			// values.setValue(16, "");
			data.add(values);
			// break;
		}
	}
}
