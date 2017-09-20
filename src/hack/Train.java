package hack;

import java.io.File;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

/**
 * @author Prasad
 *
 */
public class Train {

  public static void main(String[] args) throws Exception {

//    buildArff();
    
    Utils.buildTrainFile();
    
  }
  
  protected static void buildArff() throws IOException {
    CSVLoader csvLoader = new CSVLoader();
    csvLoader.setSource(new File("train-628.csv"));
    Instances data = csvLoader.getDataSet();
    
    // save ARFF
    ArffSaver saver = new ArffSaver();
    saver.setInstances(data);
    File arffFile = new File("train-628.arff");
    saver.setFile(arffFile);
//    saver.setDestination(arffFile);
    saver.writeBatch();
  }
}
