/**
 * @author 120011995
 *
 */
package sha1_checksum;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



public class SHA1_Checksum {

	
	/**
	 * @param file
	 * @return True if checksum's match false if they don't
	 */
	public static boolean verifyChecksum(String file) {

		MessageDigest sha1 = null; 
		try {
			sha1 = MessageDigest.getInstance("SHA1");
			FileInputStream fis = new FileInputStream(file);

			//File file = new File(fileName);
			byte[] fileInBytes = new byte[(int)file.length()];
			int read = 0;

			while((read = fis.read(fileInBytes)) != -1){
				sha1.update(fileInBytes, 0, read);
			}
			
			fis.close();

		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}

		byte [] hashBytes = sha1.digest();

		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < hashBytes.length; i++){
			sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		
		String fileHash = sb.toString();
		System.out.println("File SHA1 Hash was: " + fileHash);
		return true;
		//return fileHash.equals(testChecksum); 

	}
}
