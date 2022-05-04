/**
 * Author: Tony McGraw
 * Class: IT 377
 * SubnetClassCalculator.java
 * 
 * Instructions:
 * 1. Run the program
 * 2. Enter Ip address with network mask at the end
 * Example: 193.168.4.138/24
 * 
 * 3. Enter integer amount of subnets:
 * Example 2
 */



import java.util.InputMismatchException;
import java.util.Scanner;

public class SubnetClassCalculator {

	int[] decimalIndices = new int[3];

	int octet1, octet2, octet3, octet4, rand;
	String maxHostDec = "", mout="", rout="";
	public String nxt(int subListing, char clss) {

		// ---------------------------------------ipAddressBinaryResult
		// Calculation-------------------------------------------
		int[] binaries = new int[32];
		String ipBin = "";
		int temp;

		for (int i = 0; i < 25; i += 8) {
			if (i != 0)
				ipBin += " . ";
			if (i == 0)
				temp = octet1;
			else if (i == 8)
				temp = octet2;
			else if (i == 16)
				temp = octet3;
			else
				temp = octet4;

			for (int j = 0; j < 8; j++) {
				binaries[i + j] = temp % Math.pow(2, 8 - j) > Math.pow(2, 7 - j) - 1 ? 1 : 0;

				ipBin += binaries[i + j];
			}
		}

		// --------------------------------------subnetMaskBinaryResult
		// Calculation-------------------------------------------
		int sbl = 30 - subListing;
		String subnetBin = "";
		for (int i = 0; i < 30 - sbl; i++) {
			subnetBin += "1";

			if (i == 7 || i == 15 || i == 23)
				subnetBin += " . ";
		}
		for (int i = 30 - sbl; i < 32; i++) {
			subnetBin += "0";

			if (i == 15 || i == 23)
				subnetBin += " . ";
		}

		// ------------------------------------networkAddressBinaryResult
		// Calculation-----------------------------------------
		String networkBin = "";
		for (int i = 0; i < 41; i++) {
			if (i == 8 || i == 19 || i == 30) {
				networkBin += " . ";
				i += 3;
			}

			if (ipBin.charAt(i) == '1' && subnetBin.charAt(i) == '1')
				networkBin += "1";

			else
				networkBin += "0";
		}
		// -----------------------------networkAddressDecimalResult and minHostDec
		// Calculation--------------------------------
		String networkDec = "";
		String minHostDec = "";
		int netOctet1 = 0, netOctet2 = 0, netOctet3 = 0, netOctet4 = 0;

		for (int i = 0; i < 34; i += 11) {
			if (i != 0) {
				networkDec += ".";
				minHostDec += ".";
			}
			if (i == 0)
				temp = netOctet1;
			else if (i == 11)
				temp = netOctet2;
			else if (i == 22)
				temp = netOctet3;
			else
				temp = netOctet4;

			for (int j = 0; j < 8; j++)
				temp += networkBin.charAt(j + i) == '1' ? (int) Math.pow(2, 7 - j) : 0;

			networkDec += temp;

			if (i < 33)
				minHostDec += temp;
			else
				minHostDec += ++temp;
		}

		String broadcastBin = "";

		for (int i = 0; i < 41; i++) {
			if (i == 8 || i == 19 || i == 30) {
				broadcastBin += " . ";
				i += 3;
			}

			if (networkBin.charAt(i) == '1' || subnetBin.charAt(i) == '0')
				broadcastBin += "1";

			else
				broadcastBin += '0';
		}

		String broadcastDec = "";

		int broadcastOctet1 = 0, broadcastOctet2 = 0, broadcastOctet3 = 0, broadcastOctet4 = 0;

		for (int i = 0; i < 35; i += 11) {
			if (i != 0) {
				broadcastDec += ".";
				maxHostDec += ".";
			}
			if (i == 0)
				temp = broadcastOctet1;
			else if (i == 11)
				temp = broadcastOctet2;
			else if (i == 22)
				temp = broadcastOctet3;
			else
				temp = broadcastOctet4;

			// 8-bit binary to decimal conversion.
			for (int j = 0; j < 8; j++)
				temp += broadcastBin.charAt(i + j) == '1' ? (int) Math.pow(2, 7 - j) : 0;

			broadcastDec += temp;
			if (i < 33)
				maxHostDec += temp;
			else // Last iteration/octet.
				maxHostDec += --temp; // Max host is always 1 less than the broadcast address.
		}



		return broadcastDec;

	}

	public SubnetClassCalculator(String ipAddressText, char clss, int subnetListing) {

		decimalIndices = findDecimalIndices(ipAddressText);

		try {
			if (decimalIndices[2] == 0 || decimalIndices[3] != 0 || decimalIndices[0] == 0 ||
					(decimalIndices[1] - decimalIndices[0] == 1) || (decimalIndices[2] - decimalIndices[1] == 1)) {
				System.out.println("Invalid Input");

			} else {

				octet1 = validateOctet(ipAddressText, decimalIndices[0], 0);
				octet2 = validateOctet(ipAddressText, decimalIndices[1] - decimalIndices[0] - 1, decimalIndices[0] + 1);
				octet3 = validateOctet(ipAddressText, decimalIndices[2] - decimalIndices[1] - 1, decimalIndices[1] + 1);
				octet4 = validateOctet(ipAddressText, ipAddressText.length() - (decimalIndices[2] + 1),
						decimalIndices[2] + 1);

				if (octet1 == -1 || octet2 == -1 || octet3 == -1 || octet4 == -1) {
					System.out.println("Invalid Input");

				}

				else if ((clss == 'A' && octet1 > 126 || octet1 == 0) || (clss == 'B' && (octet1 < 128 || octet1 > 191))
						|| (clss == 'C' && (octet1 < 192 || octet1 > 223))) {

					if (clss == 'A')
						if (octet1 == 127)
							System.out.println("127.0.0.0 -> 127.255.255.255 is reserved for loopback addresses");
						else
							System.out.println("Invalid first octet. Class A permits only 1-126 (inclusive)");

					else if (clss == 'B')
						System.out.println("Invalid first octet. Class B permits only 128-191 (inclusive)");

					else if (clss == 'C')
						if (octet1 > 223 && octet1 < 240)
							System.out.println("224.0.0.0 -> 239.255.255.255 is reserved for multicast addresses");
						else
							System.out.println("Invalid first octet. Class C permits only 192-223 (inclusive)");

				} else
					//for (int i=0; i<netnm; i++) {
						displayResults(subnetListing, clss);
					//}
			}
		} catch (InputMismatchException ex) {
			System.out.println("Invalid Input");

		}

	}

	public int[] findDecimalIndices(String ipAddressText) {
		int[] arr = new int[4];
		int i = 0;

		for (int j = 0; j < ipAddressText.length(); j++) {
			if (arr[3] != 0)
				break;

			if (ipAddressText.charAt(j) == '.')
				arr[i++] = j;
		}
		return arr;
	}

	public int validateOctet(String ipAddressText, int numOfChars, int start) throws InputMismatchException {
		String octet = "";
		try {
			if (ipAddressText.charAt(start) == '-')
				return -1;

			else if (numOfChars == 1)
				octet += ipAddressText.charAt(start);

			else if (numOfChars == 2)
				for (int i = 0; i < 2; i++)
					octet += ipAddressText.charAt(start + i);

			else if (numOfChars == 3)
				for (int i = 0; i < 3; i++)
					octet += ipAddressText.charAt(start + i);

			else
				return -1;

			return Integer.parseInt(octet);
		} catch (Exception e) {
			return -1;
		}
	}

	public void displayResults(int subListing, char clss) {

		// ---------------------------------------ipAddressBinaryResult
		// Calculation-------------------------------------------
		int[] binaries = new int[32];
		String ipBin = "";
		int temp;

		for (int i = 0; i < 25; i += 8) {
			if (i != 0)
				ipBin += " . ";
			if (i == 0)
				temp = octet1;
			else if (i == 8)
				temp = octet2;
			else if (i == 16)
				temp = octet3;
			else
				temp = octet4;

			for (int j = 0; j < 8; j++) {
				binaries[i + j] = temp % Math.pow(2, 8 - j) > Math.pow(2, 7 - j) - 1 ? 1 : 0;

				ipBin += binaries[i + j];
			}
		}

		// --------------------------------------subnetMaskBinaryResult
		// Calculation-------------------------------------------
		int sbl = 30 - subListing;
		String subnetBin = "";
		for (int i = 0; i < 30 - sbl; i++) {
			subnetBin += "1";

			if (i == 7 || i == 15 || i == 23)
				subnetBin += " . ";
		}
		for (int i = 30 - sbl; i < 32; i++) {
			subnetBin += "0";

			if (i == 15 || i == 23)
				subnetBin += " . ";
		}

		// ------------------------------------networkAddressBinaryResult
		// Calculation-----------------------------------------
		String networkBin = "";
		for (int i = 0; i < 41; i++) {
			if (i == 8 || i == 19 || i == 30) {
				networkBin += " . ";
				i += 3;
			}

			if (ipBin.charAt(i) == '1' && subnetBin.charAt(i) == '1')
				networkBin += "1";

			else
				networkBin += "0";
		}
		// -----------------------------networkAddressDecimalResult and minHostDec
		// Calculation--------------------------------
		String networkDec = "";
		String minHostDec = "";
		int netOctet1 = 0, netOctet2 = 0, netOctet3 = 0, netOctet4 = 0;

		for (int i = 0; i < 34; i += 11) {
			if (i != 0) {
				networkDec += ".";
				minHostDec += ".";
			}
			if (i == 0)
				temp = netOctet1;
			else if (i == 11)
				temp = netOctet2;
			else if (i == 22)
				temp = netOctet3;
			else
				temp = netOctet4;

			for (int j = 0; j < 8; j++)
				temp += networkBin.charAt(j + i) == '1' ? (int) Math.pow(2, 7 - j) : 0;

			networkDec += temp;

			if (i < 33)
				minHostDec += temp;
			else
				minHostDec += ++temp;
		}
		mout = mout+"|  Network Address   |";
		rout = rout+"     "+networkDec;
		//System.out.print("Network Address: ==>>");
		//System.out.println(networkDec);
		//System.out.println("");

		// -----------------------------------broadcastAddressBinaryResult
		// Calculation----------------------------------------
		String broadcastBin = "";

		for (int i = 0; i < 41; i++) {
			if (i == 8 || i == 19 || i == 30) {
				broadcastBin += " . ";
				i += 3;
			}

			if (networkBin.charAt(i) == '1' || subnetBin.charAt(i) == '0')
				broadcastBin += "1";

			else
				broadcastBin += '0';
		}
		// broadcastAddressBinaryResult.setText(broadcastBin);

		// -----------------------------broadcastAddressDecimalResult and maxHostDec
		// Calculation------------------------------
		String broadcastDec = "";

		int broadcastOctet1 = 0, broadcastOctet2 = 0, broadcastOctet3 = 0, broadcastOctet4 = 0;

		for (int i = 0; i < 34; i += 11) {
			if (i != 0) {
				broadcastDec += ".";
				maxHostDec += ".";
			}
			if (i == 0)
				temp = broadcastOctet1;
			else if (i == 11)
				temp = broadcastOctet2;
			else if (i == 22)
				temp = broadcastOctet3;
			else
				temp = broadcastOctet4;

			// 8-bit binary to decimal conversion.
			for (int j = 0; j < 8; j++)
				temp += broadcastBin.charAt(i + j) == '1' ? (int) Math.pow(2, 7 - j) : 0;

			broadcastDec += temp;
			if (i < 33)
				maxHostDec += temp;
			else // Last iteration/octet.
				maxHostDec += --temp; // Max host is always 1 less than the broadcast address.
		}
		mout = mout +"|   BroadCast Address   |";
		rout = rout +"          "+broadcastDec;
		//System.out.print("BroadCast Address:  ==>  ");
		//System.out.println(broadcastDec);
		//System.out.println("");
		mout = mout +"|   Mask   |";
		rout = rout +"           "+subListing;


		// -----------------------------------------numOfSubnetsResult
		// Calculation--------------------------------------------
		int numOfSubnetBits = 0;
		int numberOfSubnets = 0;

		for (int i = 0; i < subnetBin.length(); i++)
			if (subnetBin.charAt(i) == '1')
				numOfSubnetBits++;

		if (clss == 'A')
			numOfSubnetBits -= 8;

		else if (clss == 'B')
			numOfSubnetBits -= 16;

		else
			numOfSubnetBits -= 24;

		numberOfSubnets = (int) Math.pow(2, numOfSubnetBits);

		StringBuilder sb = new StringBuilder(Integer.toString(numberOfSubnets));

		for (int i = sb.length() - 3; i > 0; i -= 3)
			sb.insert(i, ",");
		// .print("Number of subnets");
		// System.out.println(sb.toString());

		// --------------------------------------hostAddressRangeResult
		// Calculation-------------------------------------------
		mout = mout +"|       IP Range for usable Address       |";
		rout = rout+"          "+minHostDec + "  -  " + maxHostDec+"     ";
		//System.out.print("IP Range for usable Address: ==>  ");
		//System.out.println(minHostDec + "  -  " + maxHostDec);
		//System.out.println("");

		// ------------------------------------------numOfHostsResult
		// Calculation---------------------------------------------
		int numOfHostBits = 0;
		int numOfHosts = 0;

		for (int i = 0; i < subnetBin.length(); i++)
			if (subnetBin.charAt(i) == '0')
				numOfHostBits++;

		numOfHosts = (int) Math.pow(2, numOfHostBits) - 2;

		sb = new StringBuilder(Integer.toString(numOfHosts));

		for (int i = sb.length() - 3; i > 0; i -= 3)
			sb.insert(i, ",");
		mout = mout+"|   Usable IP Address  |";
		rout = rout+"            "+sb+"     ";
		//System.out.println(sb + " Usable IP Address");
		//System.out.println("");
		System.out.println(mout);
		System.out.println(rout);

		// -----------------------------------------subnetBitmapResult
		// Calculation--------------------------------------------
		String networkStringOctet1 = "", networkStringOctet2 = "", networkStringOctet3 = "";
		String subnetStringOctet2 = "", subnetStringOctet3 = "", subnetStringOctet4 = "";
		String hostStringOctet2 = "", hostStringOctet3 = "", hostStringOctet4 = "";
		int currentLength = 0;
		int numOfNetworkBits = 0;

		if (clss == 'A') {
			networkStringOctet1 += 0;
			currentLength++;
			numOfNetworkBits = 8;
		} else if (clss == 'B') {
			networkStringOctet1 += 10;
			currentLength += 2;
			numOfNetworkBits = 16;
		} else {
			networkStringOctet1 += 110;
			currentLength += 3;
			numOfNetworkBits = 24;
		}

		// -----------------------------------------subnetIDResult
		// Calculation------------------------------------------------
		int subnetIDCalc = 0;

		for (int i = 0; i < numOfSubnetBits; i++)
			subnetIDCalc += binaries[numOfNetworkBits + numOfSubnetBits - (i + 1)] == 1 ? (int) Math.pow(2, i) : 0;

		sb = new StringBuilder(Integer.toString(subnetIDCalc));

		for (int i = sb.length() - 3; i > 0; i -= 3)
			sb.insert(i, ",");

	}

	public static void main(String[] args) {
		String ipAddressText;
		int subnetListing;
		char clss = 'A';
		System.out.println("Enter IP Address:");
		Scanner myObj = new Scanner(System.in);
		String Ipadd = myObj.nextLine();
		String ipAddressTex[] = Ipadd.split("/");
		//System.out.print(ipAddressTex[0]);
		ipAddressText = ipAddressTex[0];
		subnetListing = Integer.valueOf(ipAddressTex[1]);
		System.out.println("Enter number of subnets:");
		int netnm = myObj.nextInt();
		String ipAddresT = Ipadd.split("\\.")[0];
		//System.out.println(ipAddresT);
		//System.out.println("new");
		int clsss = Integer.valueOf(ipAddresT);
		//System.out.print(clsss);

		//int clssss = Integer.valueOf(clsss);
		if (clsss <= 127) {
		clss = 'A';
		//System.out.print(clss);
		} else if (clsss >= 128 && clsss <= 191) {
				clss = 'B';
			//System.out.print(clss);
				} else {
				clss = 'C';
			//System.out.print(clss);
			}



		//System.out.print(clss);
		// clss = myObj.next().charAt(0);
		// ;
		if( netnm > 1) {

			for (int i = netnm; i>1; i=i/2){
				subnetListing++;
				//System.out.print(subnetListing);
			}
			//subnetListing = subnetListing + (netnm / 2);
		}
		SubnetClassCalculator subc = new SubnetClassCalculator(ipAddressText, clss, subnetListing);;
		//System.out.print(subnetListing);
		String mxt = subc.nxt(subnetListing, clss);
		for (int i=1; i<netnm; i++) {
			//subc = new SubnetClassCalculator(subc.nxt(subnetListing, clss), clss, subnetListing);
			mxt = subc.nxt(subnetListing, clss);
			String[] parts = mxt.split("\\.");
			int mxt1 = Integer.valueOf(parts[0]);
			int mxt2 = Integer.valueOf(parts[1]);
			int mxt3 = Integer.valueOf(parts[2]);
			int mxt4 = Integer.valueOf(parts[3]);
			mxt4 = mxt4+1;

			mxt = parts[0]+"."+parts[1]+"."+parts[2]+"."+mxt4;
			//System.out.print(mxt);
			System.out.println("------------------------------------------------------------------------------------------------------------------");
			subc = new SubnetClassCalculator(mxt, clss, subnetListing);
			//System.out.print(mxt);
		}
	}
}
