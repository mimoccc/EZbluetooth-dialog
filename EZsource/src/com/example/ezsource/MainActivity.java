package com.example.ezsource;



import java.io.File;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.function.XlsToString;
import com.example.source.CustomerMasterfile;
import com.example.source.UserMaster;

public class MainActivity extends Activity {
	BluetoothAdapter mBluetoothAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Toast.makeText(this, "Some message!", Toast.LENGTH_LONG).show();
		creatpath();
		List<UserMaster> umlist = null;
		List<CustomerMasterfile> cmlist = null;
		 XlsToString xts = new XlsToString();
		 try {
			umlist =  xts.catchUsermaster();
			cmlist = xts.catchCustomermaster();
		} catch (Exception e) {
			// TODO: handle exception
		}
		setContentView(R.layout.mainactivity);
		UserMasterDB newDb = new UserMasterDB();
		newDb.buildDB();
		int umlistsie = umlist.size();
		int cmlistsize = cmlist.size();
		for(int i = 0; i < umlistsie; i++)
		{
			newDb.insertUserMasterDB(umlist.get(i));
		}
		for(int i = 0;i <  cmlistsize;i++)
		{
			newDb.insertCustomerMasterDB(cmlist.get(i));
		}
//		UserMaster um = new UserMaster("LOU","Lou","Ray","0201010","CANADIAN NATIONAL","Y");
//		newDb.insertUserMasterDB(um);
//		
//		um = new UserMaster("LOU", "Lou", "Ray", "C201012", "CN VOUCHER-TOM DEERING", "N");	
//		newDb.insertUserMasterDB(um);
//		
//		CustomerMasterfile cm = new CustomerMasterfile("0201010", "CANADIAN NATIONAL", "01", "01", 
//				"23644200", "CN-BRIDGE & BUILDING 3010245A","Y", "N", "N", "Y", "24002 VREELAND ROAD",
//				"FLAT ROCK", "MI", "48134", "SPECIALTIES BUYER", "");
//		newDb.insertCustomerMasterDB(cm);
//		cm = new CustomerMasterfile("0201010", "CANADIAN NATIONAL", "01", "01", "26950000", 
//				"CN-SUPERVISOR MECHANICAL", "Y", "N", "N", "Y", "24002 VREELAND ROAD", 
//				"FLAT ROCK", "MI", "48134", "CHARLES KUSE", "CHARLES.KRUSE@CN.CA");
//		newDb.insertCustomerMasterDB(cm);
		Button button;
		button = (Button)findViewById(R.id.btnstartnewtran);
		button.setOnClickListener(
			new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, TransactionActivity.class);
					startActivity(intent);
				}
			}
		);
	}
	
	public class Person
	{
		public String name;
		public int age;
	}
	

	
	
	//just for test
	private class UserMasterDB
	{
		SQLiteDatabase db;
		public UserMasterDB()
		{
			
		}
		
		public void openDB()
		{
			db = openOrCreateDatabase("EZsource.db", Context.MODE_PRIVATE, null);
		}
		
		public void buildDB()
		{
			db= openOrCreateDatabase("EZsource.db", Context.MODE_PRIVATE, null);
			db.execSQL("DROP TABLE IF EXISTS usermaster"); 
		//	db.execSQL("drop table if exists usermaster");
	    //    db.execSQL("CREATE TABLE person (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, age SMALLINT)");  
			db.execSQL("create table usermaster (_id INTEGER PRIMARY KEY AUTOINCREMENT,UserID varchar,UserName varchar,UserPin varchar,Customer varchar,CustName varchar,Returnable varchar)");
			db.execSQL("DROP TABLE if EXISTS custemermaster");
			db.execSQL("create table custemermaster (_id INTEGER PRIMARY KEY AUTOINCREMENT,Customer varchar,CustName varchar,Branch varchar,Warehouse varchar,ShiptoNumber varchar,ShipToName varchar,SRM varchar,Autocrib varchar,WorkOrder varchar,Price varchar,ShipToAddress varchar,ShipToCity varchar,ShipToState varchar,ShipToZip varchar,ShipToContactName varchar,ShipToContactEmail varchar )");
			
			/*
			 * 
			 * Customer	CustName	Branch	Warehouse	Shipto Number	Ship To Name	SRM	Autocrib	
			 * Work Order	Price	Ship to Address	Ship to City	Ship to State	Ship to Zip	
			 * Ship to Contact Name	Ship to Contact Email
0201010	CANADIAN NATIONAL	01	01	23644200	CN-BRIDGE & BUILDING 3010245A	Y	N	N	Y	24002 VREELAND ROAD	FLAT ROCK	MI	48134	SPECIALTIES BUYER	
0201010	CANADIAN NATIONAL	01	01	26950000	CN-SUPERVISOR MECHANICAL	Y	N	N	Y	24002 VREELAND ROAD	FLAT ROCK	MI	48134	CHARLES KUSE	CHARLES.KRUSE@CN.CA
*/
		}
		public void insertUserMasterDB(UserMaster um)
		{
			ContentValues cv=new ContentValues();
		//	cv.put("_id",1);
			cv.put("UserID",um.getUserID());
			cv.put("UserName", um.getUserName());
			cv.put("UserPin", um.getUserPin());
			cv.put("Customer", um.getCustomer());
			cv.put("CustName", um.getCustName());
			cv.put("Returnable", um.getReturnable());
			db.insert("usermaster", null, cv);
		}
		public void insertCustomerMasterDB(CustomerMasterfile cm)
		{
			ContentValues cv = new ContentValues();
			cv.put("Customer", cm.getCustomer());
			cv.put("CustName", cm.getCustName());
			cv.put("Branch", cm.getBranch());
			cv.put("Warehouse", cm.getWarehouse());
			cv.put("ShipToName", cm.getShiptoName());
			cv.put("ShipToNumber", cm.getShiptoNumber());
			cv.put("SRM", cm.getSRM());
			cv.put("Autocrib", cm.getAutocrib());
			cv.put("WorkOrder", cm.getWorkorder());
			cv.put("Price", cm.getPrice());
			cv.put("ShipToAddress",cm.getShipToAddress());
			cv.put("ShipToCity", cm.getShipToCity());
			cv.put("ShipToState", cm.getShipToState());
			cv.put("ShipToZip", cm.getShipToZip());
			cv.put("ShipToContactName", cm.getShipToContactName());
			cv.put("ShipToContactEmail", cm.getShipToContactEmail());
			db.insert("customermaster", null, cv);
		}
		public boolean checkexit(String tablename,String row, String column)
		{
			Cursor c = db.query(tablename, null, column + " = ?", new String[]{row}, null, null, null);

			if(c.moveToFirst())
			//c.isAfterLast();.isNull(columnIndex)
			{
				Log.e("ysy", row + " exit");
				return true;				
			}
			else 
			{
				Log.e("ysy", row + " not exit");
				return false; 
			}
		}

		
	}
	
//	
//	
//	public String bluetoothGetMessage(){
//		String tempStr = null;
//		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//		if(mBluetoothAdapter == null){
//			new AlertDialog.Builder(MainActivity.this).setTitle("Alert")
//			.setMessage("no bluetooth found").show();
//		}
///*		if(!mBluetoothAdapter.isEnabled()){
//			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//		}*/
//		return tempStr;
//	}
//	/*
//	 * bluetooth use as client*/
//	private class ConnectThread extends Thread {
//	    private final BluetoothSocket mmSocket;
//	    private final BluetoothDevice mmDevice;
//
//	    public ConnectThread(BluetoothDevice device) {
//	        // Use a temporary object that is later assigned to mmSocket,
//	        // because mmSocket is final
//	        BluetoothSocket tmp = null;
//	        mmDevice = device;
//
//	        // Get a BluetoothSocket to connect with the given BluetoothDevice
//	        try {
//	            // MY_UUID is the app's UUID string, also used by the server code
//	       //     tmp = device.createRfcommSocketToServiceRecord(uuid);
//	            
//	            Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
//	            tmp = (BluetoothSocket) m.invoke(device, 1);
//	            
//	        } catch (Exception e) { 
//	        	Log.e("ysy", e.toString());
//	        }
//	        mmSocket = tmp;
//	    }
//	    
//
//	    public void run() {
//	        // Cancel discovery because it will slow down the connection
//	        mBluetoothAdapter.cancelDiscovery();
//
//	        try {
//	            // Connect the device through the socket. This will block
//	            // until it succeeds or throws an exception
//	            mmSocket.connect();
//	        } catch (IOException connectException) {
//	            // Unable to connect; close the socket and get out
//	            try {
//	                mmSocket.close();
//	            } catch (IOException closeException) { }
//	            return;
//	        }
//
//	        // Do work to manage the connection (in a separate thread)
//	//        manageConnectedSocket(mmSocket);
//	    }
//
//	    /*  Will cancel an in-progress connection, and close the socket */
//	    public void cancel() {
//	        try {
//	            mmSocket.close();
//	        } catch (IOException e) { }
//	    }
//	}
//	
//	/*
//	 * data transmit
//	 * */
//	private class ConnectedThread extends Thread {
//	    private final BluetoothSocket mmSocket;
//	    private final InputStream mmInStream;
//	    private final OutputStream mmOutStream;
//	    private Handler mmhandler;
//
//	    public ConnectedThread(BluetoothSocket socket,Handler mhandler) {
//	        mmSocket = socket;
//	        mmhandler = mhandler;
//	        InputStream tmpIn = null;
//	        OutputStream tmpOut = null;
//
//	        // Get the input and output streams, using temp objects because
//	        // member streams are final
//	        try {
//	            tmpIn = socket.getInputStream();
//	            tmpOut = socket.getOutputStream();
//	        } catch (IOException e) { }
//
//	        mmInStream = tmpIn;
//	        mmOutStream = tmpOut;
//	    }
//
//	    public void run() {
//	        byte[] buffer = new byte[1024];  // buffer store for the stream
//	        int bytes; // bytes returned from read()
//
//	        // Keep listening to the InputStream until an exception occurs
//	        while (true) {
//	            try {
//	                // Read from the InputStream
//	                bytes = mmInStream.read(buffer);
//	                String data = new String(buffer, 0, bytes);
//	                Log.e("ysy", data);
///*	                Handler mHandler = new Handler();
//	                // Send the obtained bytes to the UI activity*/
////	                mmhandler.obtainMessage(TransactionActivity.DATA_RECEIVE,data)
////	                        .sendToTarget();
//	                mmhandler.obtainMessage(state, data).sendToTarget();
//	            } catch (IOException e) {
//	                break;
//	            }
//	        }
//	    }
//
//	    /*  Call this from the main activity to send data to the remote device */
//	    public void write(byte[] bytes) {
//	        try {
//	            mmOutStream.write(bytes);
//	        } catch (IOException e) { }
//	    }
//
//	    /*  Call this from the main activity to shutdown the connection */
//	    public void cancel() {
//	        try {
//	            mmSocket.close();
//	        } catch (IOException e) { }
//	    }
//	}
//	
//	public void bluetooth()
//	{
//		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
//		intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//		intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
//		intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
////		registerReceiver(search, filter)
//		uuid = UUID.randomUUID();
//		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//		if(mBluetoothAdapter == null)
//		{
//			//this will change to an alert dialog
//			Log.e("ysy", "this service do not support bluetooth");
//		}
//		
//		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//		if(pairedDevices.size() > 0)
//		{
//			for(BluetoothDevice device : pairedDevices)
//			{
//				
//			}
//		}
//		   // Create a BroadcastReceiver for ACTION_FOUND
//		final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//		    public void onReceive(Context context, Intent intent) {
//		        String action = intent.getAction();
//		        // When discovery finds a device
//		        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//		            // Get the BluetoothDevice object from the Intent
//		            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//		            // Add the name and address to an array adapter to show in a ListView
//		       //     mArrayAdapter.add(device.getName() + "n" + device.getAddress());
//		        }
//		    }
//		};
//		
//		// Register the BroadcastReceiver
//		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//		registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
//	
//		
//		
//		
//
//
//		bluetoothGetMessage();
//		ConnectThread clientThread = null;
//		if(pairedDevices.size() > 0)
//		{
//			for(BluetoothDevice device : pairedDevices)
//			{
//				clientThread = new ConnectThread(device);
//			}
//		}
//		clientThread.start();
//		/*
//		 * Handler
//		 * */
//		Handler handler = new Handler()
//		{
//		    public void handleMessage(Message msg) {
//		        switch (msg.what) {
//		        case ENTERUSERID: //state 1
//		        {
//		        	tempstring = msg.obj.toString();
//		        	if(checkUserID(tempstring))
//		        	{
//		        		state = ENTERPIN;
//		        		stateuserid = tempstring;
//		        		showdialog(ENTERPIN);
//		        	}
//		        	else {
//		        		Log.e("ysy","wrong UserID");
//		        		state = ENTERUSERID;
//		        		showdialog(ENTERUSERID);
//		        	   }
//		        	break;
//		        }
//		        case ENTERPIN:
//		        {
//		        	Log.e("ysy", "enterpin");
//		        }
////		          case SOCKET_CONNECTED: {
////		            mBluetoothConnection = (ConnectionThread) msg.obj;
////		            if (!mServerMode)
////		              mBluetoothConnection.write("this is a message".getBytes());
////		            break;
////		          }
////		          case DATA_RECEIVED: {
////		            data = (String) msg.obj;
////		            tv.setText(data);
////		            if (mServerMode)
////		             mBluetoothConnection.write(data.getBytes());
//		      }
//			    super.handleMessage(msg); 
//		    }
//		};
//		
//		ConnectedThread connectedThread = new ConnectedThread(clientThread.mmSocket,handler);
//		connectedThread.start();
//	}
//
//
//
//	

	public void creatpath()
	{
		String path = Environment.getExternalStorageDirectory().getPath();//.getExternalStorageDirectory();
		File destDir = new File(path + "/Ezsource/");
		if(!destDir.exists())
		{
			destDir.mkdirs();
		}
		File fileUM = new File(path + "/Ezsource/UserMaster/");
		File fileCM = new File(path + "/Ezsource/CustomerMaster/");
		File fileAIT = new File(path + "/Ezsource/AphaseItemTemplate/");
		File fileRI = new File(path + "/Ezsource/ReturnableItems/");
		if(!fileUM.exists())
		{
			fileUM.isDirectory();
			fileUM.mkdirs();
		}
		if(!fileCM.exists())
		{
			fileCM.mkdirs();
		}
		if(!fileAIT.exists())
		{
			fileAIT.mkdirs();
		}
		if(!fileRI.exists())
		{
			fileRI.mkdirs();
		}
	}
}
