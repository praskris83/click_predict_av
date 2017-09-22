/**
 * 
 */
package hack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * @author prasad
 *
 */
public class TestDataanalyser {

	static Map<String, Long> userVisitMap = new HashMap<String, Long>();

	private static final String COMMA = ",";

	private static final String group = "{=9038, PR=669, PS=2009, PT=43356, PW=4, PY=83575, QA=18269, AD=50, AE=199582, AF=2478, AG=156, AI=31, AL=3255, AM=1161, AO=2376, AR=124829, AS=9, AT=11453, RE=155, AU=11908, AW=101, AX=3, AZ=5101, RO=115748, BA=971, BB=207, RS=1095, BD=2137178, RU=1060664, BE=3673, BF=271, BG=25411, RW=656, BH=7623, BI=107, BJ=729, BM=41, BN=507, BO=252565, SA=26391, SB=43, BQ=7, BR=681682, SC=88, SD=10122, BS=198, SE=4988, BT=189, SG=41485, BW=209, SI=135, BY=2363, BZ=188, SK=4019, SL=93, SM=10, SN=2798, SO=816, CA=22400, SR=2547, SS=107, CD=758, ST=72, SV=5210, CF=22, CG=1373, CH=10751, SX=7, SY=30808, CI=7203, SZ=138, CK=4, CL=91744, CM=9261, CN=69131, CO=524702, CR=14878, TC=59, TD=73, CU=30, TG=1632, CV=145, TH=1054412, CW=165, CX=1, TJ=798, CY=2540, CZ=919, TL=230, TM=316, TN=5235, TO=7, TR=70683, TT=392, DE=183953, TW=8628, TZ=11947, DJ=344, DK=2466, DM=58, DO=8633, UA=12452, UG=13856, DZ=459598, UK=549322, US=700276, EC=19395, EE=177, EG=200758, UY=105098, UZ=1513, VC=65, ER=5, ES=160189, VE=305671, ET=1924, EU=32, VG=16, VI=49, VN=41656, VU=22, FI=3267, FJ=360, FK=5, FM=19, FO=1, FR=147111, WF=3, GA=4005, WS=23, GD=74, GE=1661, GF=104, GG=103, GH=35897, GI=13, GL=15, GM=143, GN=342, GP=126, GQ=104, GR=44764, GT=87851, GU=63, GW=179, GY=359, HK=1282, HN=2497, HR=573, HT=5144, YE=2130, HU=17755, ID=3204852, YT=79, IE=24484, IL=4344, IM=297, IN=7362482, IO=8, ZA=259562, IQ=200942, IR=117234, IS=40, IT=100075, ZM=7367, JE=111, ZW=1032, **=5597, JM=1781, JO=9498, JP=24600, KE=10071, KG=15530, KH=69717, KI=10, KM=248, KN=41, KR=94158, KW=111245, KY=82, KZ=5401, LA=1611, LB=9480, LC=87, LI=3, LK=112778, LR=187, LS=465, LT=677, LU=123, LV=498, LY=34840, MA=8963, MC=5, MD=946, ME=292, MF=10, MG=1598, MH=18, MK=529, ML=470, MM=98746, MN=610, MO=132, MP=17, MQ=131, MR=641, MT=104, MU=747, MV=3671, MW=269, MX=805034, MY=795321, MZ=3689, NC=65, NE=443, NG=800328, NI=2806, NL=38551, NO=4768, NP=31075, NR=1, NZ=483, OM=29250, PA=8688, PE=268990, PF=171, PG=223, PH=578144, PK=267392, PL=28248}";

	public static void main(String[] args) {
//		for (int i = 0; i <= 255; i++) {
//			// String filename =
//			// "E:\\Prasad\\hackathon\\Click_Predictions\\train\\train.csv";
//			String filename = "/tmp/cltrain/test-";
//			if (("" + i).length() == 1) {
//				filename = filename + "00" + i + ".csv";
//			} else if (("" + i).length() == 2) {
//				filename = filename + "0" + i + ".csv";
//			} else {
//				filename = filename + i + ".csv";
//			}
//			System.out.println("Train File --" + filename);
//			processInputFile(filename);
//		}
//
//		System.out.println(userVisitMap);
		String[] lines = StringUtils.split(group,",");
		for(String line : Arrays.asList(lines)){
			String[] datas = line.split("=");
			System.out.println(datas[0] + "	" + datas[1]);
		}
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
		// System.out.println(Arrays.asList(p));
		// if(p.length > 17){
		// System.out.println(line);
		// }
		Data item = new Data();
		// more initialization goes here
		item.id = "" + p[0].toLowerCase().trim();
		item.cuntry = "" + p[1].toUpperCase().trim();

		if (userVisitMap.containsKey(item.cuntry)) {
			Long visitCount = userVisitMap.get(item.cuntry) + 1;
			userVisitMap.put(item.cuntry, visitCount);
		} else {
			userVisitMap.put(item.cuntry, 1l);
		}

		return item;
	};
}
