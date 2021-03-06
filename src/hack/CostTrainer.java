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
import org.apache.commons.lang3.math.NumberUtils;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

/**
 * @author prasad
 *
 */
public class CostTrainer {

	public static final String country = "True";
	static Map<Long, Long> userVisitMap = new HashMap<Long, Long>();

	public static void main(String[] args) {
		buildTrainFile();
	}

	private static void buildTrainFile() {

		ArrayList<Attribute> atts = new ArrayList<Attribute>();

		// Country Codes
		List<String> allCountryCodes = Helper.getAllCodes();

		buildAttrs(atts, allCountryCodes);

		// InstanceHelper.addNewAttrs(atts);

		Instances data = new Instances("Cost_Train", atts, 10);
		data.setClassIndex(15);

		try {
			// 203.88.6.38
			for (int i = 600; i <= 632; i++) {
				String filename = "E:\\Prasad\\hackathon\\Click_Predictions\\train\\train-";
				// String filename = "/tmp/cltrain/train-";
				if (("" + i).length() == 1) {
					filename = filename + "00" + i + ".csv";
				} else if (("" + i).length() == 2) {
					filename = filename + "0" + i + ".csv";
				} else {
					filename = filename + i + ".csv";
				}
				System.out.println("Train File --" + filename);
				// LineIterator it = FileUtils.lineIterator(new File(filename),
				// "UTF-8");
				List<Data> datas = FileHelper.processInputFile(filename, country);
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
			 System.out.println(data);
			// Create a na�ve bayes classifier

			Classifier cModel = (Classifier) new LinearRegression();
			try {
				System.out.println("Starting trained model to click_predict_" + country + ".model");
				cModel.buildClassifier(data);

				/*
				 * We are done training the classifier, so now we serialize it
				 * to disk
				 */
				SerializationHelper.write("click_predict_" + country + ".model", cModel);
				System.out.println("Saved trained model to click_predict_" + country + ".model");

				Instances testData = buildTestData();
				// Test the model
//				Evaluation eTest = new Evaluation(testData);
//				eTest.evaluateModel(cModel, testData);
				Classifier classifier = (Classifier) SerializationHelper.read("click_predict_" + country + ".model");
				Enumeration testInstances = testData.enumerateInstances();
				// Print the result � la Weka explorer:
//				String strSummary = eTest.toSummaryString();
//				System.out.println(strSummary);

				while (testInstances.hasMoreElements()) {
					Instance instance = (Instance) testInstances.nextElement();
					double classification = classifier.classifyInstance(instance);
//					double classification1 = classifier1.classifyInstance(instance);
					System.out.println(classification);
//					reader.get(i).setClas(classification);
//					reader.get(i).setCost(classification1);
//					i++;
				}
				
				// Get the confusion matrix
//				double[][] cmMatrix = eTest.confusionMatrix();

//				for (double[] row : cmMatrix) {
//					InstanceHelper.printRow(row);
//				}

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
		// String header = datas.next();
		// while (datas.hasNext()) {
		for (Data line : datas) {
			Instance values = new DenseInstance(16);// 34,16
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
				// InstanceHelper.setDeviceAttrs(line.device, values);
				values.setValue(6, Helper.getHashFromStr(line.browser));
				// InstanceHelper.setBrowserAttrs(line.browser, values);
				values.setValue(7, Helper.getHashFromStr(line.os));
				// InstanceHelper.setOsAttrs(line.os, values);
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
				values.setValue(15, NumberUtils.isNumber(line.pay) ? Double.valueOf(line.pay) : 0d);
				// values.setValue(15, "TRUE");
				// values.setDataset(data);
				// values.setValue(15, "");
				// values.setValue(16, "");
				data.add(values);
				// break;
			} catch (Exception e) {
				e.printStackTrace();
				values.setValue(15, 0d);
				data.add(values);
				continue;
			}
		}
	}

	public static Instances buildTestData() {
		ArrayList<Attribute> atts = new ArrayList<Attribute>();

		// Country Codes
		List<String> allCountryCodes = Helper.getAllCodes();

		buildAttrs(atts, allCountryCodes);
		// addNewAttrs(atts);
		// atts.add(new Attribute("ID", true));
		// AddID addId = new AddID();
		// atts.add(addId);

		Instances data = new Instances("Clicks_Test", atts, 10);
		data.setClassIndex(15);

		try {
			for (int i = 633; i <= 633; i++) {
				String filename = "E:\\Prasad\\hackathon\\Click_Predictions\\train\\train-";
				// String filename = "/tmp/cltest/test-";
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
				List<Data> reader = FileHelper.processInputFile(filename, country);
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

		atts.add(new Attribute("ConversionPayOut", false));
	}

}
