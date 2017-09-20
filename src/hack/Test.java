package hack;

import java.util.Enumeration;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

/**
 * @author Prasad
 *
 */
public class Test {

  public static void main(String[] args) {


    try {
      Instances testDataSet = InstanceHelper.buildTestData();
      /*
       * Now we tell the data set which attribute we want to classify, in our case, we want to
       * classify the first column: survived
       */
      Attribute testAttribute = testDataSet.attribute(15);
      testDataSet.setClass(testAttribute);
      /*
       * Now we read in the serialized model from disk
       */

      Classifier classifier = (Classifier) SerializationHelper.read("click_predict2.model");

      Enumeration testInstances = testDataSet.enumerateInstances();
      while (testInstances.hasMoreElements()) {
        Instance instance = (Instance) testInstances.nextElement();
        double classification = classifier.classifyInstance(instance);
        System.out.println(classification);
      }

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
