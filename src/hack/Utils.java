package hack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

import com.opencsv.CSVReader;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SparseInstance;

/**
 * @author Prasad
 *
 */
public class Utils {

  public static void main(String[] args) {
    // String csvFile = "E:\\Prasad\\hackathon\\Click_Predictions\\train\\train.csv";
    String csvFile = "E:\\Prasad\\hackathon\\Click_Predictions\\test\\test.csv";
    int count = 0;
    CSVReader reader = null;
    try {
      reader = new CSVReader(new FileReader(csvFile));
      String[] line;
       for(int i = 0 ; i < 100 ; i++){
       line = reader.readNext();
       System.out.println(Arrays.toString(line));
       }
       while ((line = reader.readNext()) != null) {
       count++;
      // System.out.println(line[0]);
       }
      long start = 63367289;
      File fout = new File("allbad.csv");
      FileOutputStream fos = new FileOutputStream(fout);
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
      bw.write("ID,ConversionPayOut");
      bw.newLine();
      for (long i = 0; i < 25548874; i++) {
        bw.write("" + start + "," + "0.0");
        bw.newLine();
        start = start + 1;
      }
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println(count);
  }

  public static void buildTrainFile() {

    ArrayList<Attribute> atts = new ArrayList<Attribute>();

    // Country Codes
    atts.add(new Attribute("Country", Helper.getAllCodes()));

    atts.add(new Attribute("Carrier"));

    atts.add(new Attribute("TrafficType", Arrays.asList(new String[] {"M", "A", "G"})));

    atts.add(new Attribute("ClickDate"/*, "yyyy-MM-dd"*/));

    atts.add(new Attribute("TrafficTime", Arrays.asList(new String[] {"BO", "IO", "AO"})));

    atts.add(new Attribute("Device", true));

    atts.add(new Attribute("Browser", true));

    atts.add(new Attribute("OS", true));
    
    atts.add(new Attribute("RefererUrl", Arrays.asList(new String[] {"YES", "NO"})));

    atts.add(new Attribute("UserIp", true));

    atts.add(new Attribute("HasSeenBefore", Arrays.asList(new String[] {"YES", "NO"})));

    atts.add(new Attribute("publisherId", true));

    atts.add(new Attribute("subPublisherId", true));

    atts.add(new Attribute("advertiserCampaignId", true));

    atts.add(new Attribute("Fraud"));

    atts.add(new Attribute("Conversion", Arrays.asList(new String[] {"FALSE", "TRUE"})));
    
    Instances data = new Instances("Clicks_Train", atts, 0);
    try {
      CSVReader reader = new CSVReader(new FileReader("train-632.csv"));
      String[] line;
      while ((line = reader.readNext()) != null) {
        SparseInstance values = new SparseInstance(16);
        values.setDataset(data);
        values.setValue(0, line[1]);
        values.setValue(1, line[2]);
        values.setValue(2, (line[3].isEmpty() ? "G" : line[3]));
        values.setValue(3, line[4]);
        values.setValue(4, "AO");
        values.setValue(5, line[5]);
        values.setValue(6, line[6]);
        values.setValue(7, line[6]);
        values.setValue(8, (line[8].isEmpty() ? "NO" : "YES"));
        values.setValue(9, line[9]);
        values.setValue(10, "NO");
        values.setValue(11, line[13]);
        values.setValue(12, line[14]);
        values.setValue(13, line[15]);
        values.setValue(14, line[16]);
        values.setValue(15, line[10]);
//        values.setDataset(data);
//        values.setValue(15, "");
//        values.setValue(16, "");
      }      
      System.out.println(data);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
