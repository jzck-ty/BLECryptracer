package de.ecspride;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @testcase_name BroadcastReceiverLifecycle1
 * @version 0.1
 * @author Secure Software Engineering Group (SSE), European Center for Security and Privacy by Design (EC SPRIDE) 
 * @author_mail siegfried.rasthofer@cased.de
 * 
 * @description The return value of source method is stored to a variable and sent to a sink in a condition branch
 * @dataflow source -> imei -> sink
 * @number_of_leaks 1
 * @challenges the analysis must be able to handle the broadcast receiver lifecycle correctly and
 *  evaluate the condition. 
 */
public class TestReceiver extends BroadcastReceiver{
	private BluetoothGattCharacteristic mBluetoothGattCharacteristic;

	@Override
	  public void onReceive(Context context, Intent intent) {
		byte[] characteristicValue = mBluetoothGattCharacteristic.getValue(); //source
		 int i = 2+3;
		 if(i == 5){
			 SecretKey secret = generateKey("*****");
			 byte[] decryptedValue = new byte[0];
			 try {
				 decryptedValue = decryptMsg(characteristicValue, secret);
			 } catch (InvalidKeyException e) {
				 e.printStackTrace();
			 } catch (IllegalBlockSizeException e) {
				 e.printStackTrace();
			 } catch (BadPaddingException e) {
				 e.printStackTrace();
			 } catch (UnsupportedEncodingException e) {
				 e.printStackTrace();
			 }//sink, leak
		 }
	}


	// Both methods taken from here: https://stackoverflow.com/questions/40123319/easy-way-to-encrypt-decrypt-string-in-android
	public static SecretKey generateKey(String password)
	{
		SecretKeySpec secret;
		return secret = new SecretKeySpec(password.getBytes(), "AES");
	}

	public static byte[] decryptMsg(byte[] sourceBytes, SecretKey secret)
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		Cipher cipher = null;
		cipher.init(Cipher.DECRYPT_MODE, secret);
		byte[] decryptedBytes = cipher.doFinal(sourceBytes); //Sink
		return decryptedBytes;
	}
}

