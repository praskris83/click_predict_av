package hack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Prasad
 *
 */
public class Helper {

	public static final String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
	// "\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";
	public static final String NUMBER_PATTERN = "-?\\d+";

	public static final String URL_PATTERN = "^(?:https?:\\/\\/)?(?:[^@\\/\\n]+@)?(?:www\\.)?([^:\\/\\n]+)";

	public static void main(String[] args) {
		System.out.println(getAllCodes());
		// System.out.println(getTimeStr1("2017-08-30 06:10:05"));
		// System.out.println(getTimeStr("8/21/2017 08:00:01 PM"));
		// System.out.println(getTimeStr("8/21/2017 08:00:01"));
		// System.out.println(getDateStr1("2017-08-30 22:10:05"));

		// System.out.println(getUsrIPStr("--L42.111.171.131"));
		// System.out.println(getUsrIPStr("dwqdq42.111.171.131.3232"));
		// System.out.println(getUsrIPStr("ebq3jkfbkj2q"));
		// System.out.println(getUsrIPStr(""));
		// System.out.println(getUsrIPStr(" "));

		// System.out.println(getNumberFromStr("55http://162.243.73.231/dlv/c.php?cca=32424"));
		// System.out.println(getNumberFromStr("1948Ã˜Â¨Ã˜Â­Ã˜Â«
		// Ã™Â�Ã™â€žÃ˜Â§Ã™â€¦ Ã™Â�Ã™Å 4"));
		// System.out.println(getNumberFromStr("-1"));

		System.out.println(getHashFromURL("http://user:pass@google.com/?a=b#asdd"));

	}

	public static List<String> getAllCodes() {
		List<String> codes = new ArrayList<String>();
		String data = "--, UK, AF, AX, AL, DZ, AS, AD, AO, AI, AQ, AG, AR, AM, AW, AU, AT, AZ, BS, BH, BD, BB, BY, BE, BZ, BJ, BM, BT, BO, BQ, BA, BW, BV, BR, IO, UM, VG, VI, BN, BG, BF, BI, KH, CM, CA, CV, KY, CF, TD, CL, CN, CX, CC, CO, KM, CG, CD, CK, CR, HR, CU, CW, CY, CZ, DK, DJ, DM, DO, EC, EG, SV, GQ, ER, EE, ET, FK, FO, FJ, FI, FR, GF, PF, TF, GA, GM, GE, DE, GH, GI, GR, GL, GD, GP, GU, GT, GG, GN, GW, GY, HT, HM, VA, HN, HK, HU, IS, IN, ID, CI, IR, IQ, IE, IM, IL, IT, JM, JP, JE, JO, KZ, KE, KI, KW, KG, LA, LV, LB, LS, LR, LY, LI, LT, LU, MO, MK, MG, MW, MY, MV, ML, MT, MH, MQ, MR, MU, YT, MX, FM, MD, MC, MN, ME, MS, MA, MZ, MM, NA, NR, NP, NL, NC, NZ, NI, NE, NG, NU, NF, KP, MP, NO, OM, PK, PW, PS, PA, PG, PY, PE, PH, PN, PL, PT, PR, QA, XK, RE, RO, RU, RW, BL, SH, KN, LC, MF, PM, VC, WS, SM, ST, SA, SN, RS, SC, SL, SG, SX, SK, SI, SB, SO, ZA, GS, KR, SS, ES, LK, SD, SR, SJ, SZ, SE, CH, SY, TW, TJ, TZ, TH, TL, TG, TK, TO, TT, TN, TR, TM, TC, TV, UG, UA, AE, GB, US, UY, UZ, VU, VE, VN, WF, EH, YE, ZM, ZW";
		codes = Arrays.asList(StringUtils.split(data, ", "));
		// codes.
		// System.out.println(codes.size());
		return codes;
	}

	public static String getDateStr1(String input) {
		if (StringUtils.isNotEmpty(input)) {
			String[] values = input.split(" ");
			if (values.length == 2) {
				return values[0];
			}
		}
		return "2999-01-01";
	}

	public static String getTimeStr1(String input) {
		if (StringUtils.isNotEmpty(input)) {
			String[] values = input.split(" ");
			if (values.length == 2) {
				String[] times = values[1].split(":");
				if (times.length == 3) {
					int hour = Integer.parseInt(times[0]);
					if (hour <= 8) {
						return "BO";
					} else if (hour >= 16) {
						return "AO";
					} else {
						return "IO";
					}
				}
				return values[0];
			}
		}
		return "NA";
	}

	public static String getDateStr(String input) {
		if (StringUtils.isNotEmpty(input)) {
			String[] values = input.split(" ");
			if (values.length == 4) {
				return values[0];
			}
		}
		return "1/1/2999";
	}

	public static String getTimeStr(String input) {
		if (StringUtils.isNotEmpty(input)) {
			String[] values = input.split(" ");
			if (values.length == 4) {
				String[] times = values[2].split(":");
				if (times.length == 3) {
					int hour = Integer.parseInt(times[0]);
					if (values[3].toLowerCase().contains("AM") && (hour == 12 || hour <= 8)) {
						return "BO";
					} else if (values[3].toLowerCase().contains("PM") && hour >= 4) {
						return "AO";
					} else {
						return "IO";
					}
				}
				return values[0];
			}
		}
		return "NA";
	}

	public static long getUsrIPStr(String ipString) {
		if (StringUtils.isNotBlank(ipString)) {
			Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
			Matcher matcher = pattern.matcher(ipString);
			if (matcher.find()) {
				String ip = StringUtils.remove(matcher.group(), ".");
				// System.out.println("IP = " + matcher.group());
				if (StringUtils.isNumeric(ip)) {
					return Long.parseLong(ip);
				}
			}
		}
		return -1;
	}

	public static long getNumberFromStr(String value) {
		long usrIPStr = getUsrIPStr(value);
		if (usrIPStr != -1) {
			return usrIPStr;
		}
		if (StringUtils.isNotBlank(value)) {
			Pattern pattern = Pattern.compile(NUMBER_PATTERN);
			Matcher matcher = pattern.matcher(value);
			if (matcher.find()) {
				String num = matcher.group();
				// System.out.println("Number = " + matcher.group());
				if (StringUtils.isNumeric(num)) {
					return Long.parseLong(num);
				}
			}
		}

		return -1;
	}

	public static long getHashFromStr(String value) {
		long usrIPStr = getUsrIPStr(value);
		if (usrIPStr != -1) {
			return usrIPStr;
		}
		if (StringUtils.isNotBlank(value)) {
			return value.trim().toLowerCase().hashCode();
		}
		return -1;
	}

	public static long getHashFromURL(String value) {
		long usrIPStr = getUsrIPStr(value);
		if (usrIPStr != -1) {
			return usrIPStr;
		}
		if (StringUtils.isNotBlank(value)) {
			Pattern pattern = Pattern.compile(URL_PATTERN);
			Matcher matcher = pattern.matcher(value);
			if (matcher.find()) {
				String url = matcher.group();
//				System.out.println("URL = " + url);
				return url.toLowerCase().trim().hashCode();
			}
		}
		return value.trim().toLowerCase().hashCode();
	}
}
