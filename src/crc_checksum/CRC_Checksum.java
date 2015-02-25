/**
 * Allows the checksum to be carried out by both implementations 
 * 
 * @author 120011995
 *
 */

package crc_checksum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;


public class CRC_Checksum {

	/**
	 * @param fileName
	 * @return The file's CRC value before transfer
	 */
	
	public static long CalculateCRC32(String fileName){

		//Convert file passed in to bytes to carry out checksum
		File file = new File(fileName);
		byte[] fileInBytes = new byte[(int)file.length()];

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		for(int i = 0; i < fileInBytes.length; i++){
			try {
				fileInBytes[i] = (byte) fis.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//create a checksum for the file
		Checksum checksum = new CRC32();
		//compute the CRC32 checksum for byte array
		checksum.update(fileInBytes, 0, fileInBytes.length);
		//Get the generated checksum using getValue method of CRC32 class.
		long checksumValue = checksum.getValue();
		System.out.println("Checksum value was: " + checksumValue);
		return checksumValue;
	}

}
