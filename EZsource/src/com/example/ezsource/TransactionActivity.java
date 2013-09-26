package com.example.ezsource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.source.AphaseItemTemplate;
import com.example.source.UserMaster;

public class TransactionActivity extends Activity {
	BluetoothAdapter mBluetoothAdapter;
	UUID uuid;
	String tempstring ;
	int state =0;//automata
	final int STATE = 8908;
	final int ENTERUSERID = 1;
	final int ENTERPIN = 2;
	final int CHOOSECUSTOMER = 3;
	final int CHOOSERETURNABLE =4;
	final int COSTCODE = 5;
//	final int CHOOSESHIPTONUMBER = ;
	AlertDialog.Builder ad;
	String stateuserid = null;
	String statereturnable = null;
	String statecustomer = null;
	String stateshiptonumer = null;
	String stateitemid = null;
	Calendar calendar = Calendar.getInstance();  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transaction);
		state = 1;
	//	bluetooth();
		ad =new AlertDialog.Builder(this);
		showTimeDialog();
	//	enteruserid();
	//	showdialog(state);
	
	}


	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.transaction, menu);
		return true;
	}
	
	public String bluetoothGetMessage(){
		String tempStr = null;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBluetoothAdapter == null){
			new AlertDialog.Builder(TransactionActivity.this).setTitle("Alert")
			.setMessage("no bluetooth").show();
		}
/*		if(!mBluetoothAdapter.isEnabled()){
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}*/
		return tempStr;
	}
	/*
	 * bluetooth use as client*/
	private class ConnectThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final BluetoothDevice mmDevice;

	    public ConnectThread(BluetoothDevice device) {
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	        BluetoothSocket tmp = null;
	        mmDevice = device;

	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try {
	            // MY_UUID is the app's UUID string, also used by the server code
	       //     tmp = device.createRfcommSocketToServiceRecord(uuid);
	            
	            Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
	            tmp = (BluetoothSocket) m.invoke(device, 1);
	            
	        } catch (Exception e) { 
	        	Log.e("ysy", e.toString());
	        }
	        mmSocket = tmp;
	    }
	    

	    public void run() {
	        // Cancel discovery because it will slow down the connection
	        mBluetoothAdapter.cancelDiscovery();

	        try {
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	            mmSocket.connect();
	        } catch (IOException connectException) {
	            // Unable to connect; close the socket and get out
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) { }
	            return;
	        }

	        // Do work to manage the connection (in a separate thread)
	//        manageConnectedSocket(mmSocket);
	    }

	    /*  Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
	
	/*
	 * data transmit
	 * */
	private class ConnectedThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final InputStream mmInStream;
	    private final OutputStream mmOutStream;
	    private Handler mmhandler;

	    public ConnectedThread(BluetoothSocket socket,Handler mhandler) {
	        mmSocket = socket;
	        mmhandler = mhandler;
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;

	        // Get the input and output streams, using temp objects because
	        // member streams are final
	        try {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        } catch (IOException e) { }

	        mmInStream = tmpIn;
	        mmOutStream = tmpOut;
	    }

	    public void run() {
	        byte[] buffer = new byte[1024];  // buffer store for the stream
	        int bytes; // bytes returned from read()

	        // Keep listening to the InputStream until an exception occurs
	        while (true) {
	            try {
	                // Read from the InputStream
	                bytes = mmInStream.read(buffer);
	                String data = new String(buffer, 0, bytes);
	                Log.e("ysy", data);
/*	                Handler mHandler = new Handler();
	                // Send the obtained bytes to the UI activity*/
//	                mmhandler.obtainMessage(TransactionActivity.DATA_RECEIVE,data)
//	                        .sendToTarget();
	                mmhandler.obtainMessage(state, data).sendToTarget();
	            } catch (IOException e) {
	                break;
	            }
	        }
	    }

	    /*  Call this from the main activity to send data to the remote device */
	    public void write(byte[] bytes) {
	        try {
	            mmOutStream.write(bytes);
	        } catch (IOException e) { }
	    }

	    /*  Call this from the main activity to shutdown the connection */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
	
	
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
		
		public String returnDBString(String tablename,String column,String row,String column2,String row2,String returncolumn )
		{
			String returnString = null;
		//	Cursor c = db.query(tablename,new String[](column,column2),"",null,null,null,null);
			Cursor c = db.query(tablename, null, column + " = ? AND "+ column2 + " = ?", new String[]{row,row2}, null, null, null);
		//	Cursor c = db.query(tablename, null, column + " = ?", new String[]{row}, null, null, null);

			if(c.moveToFirst())
				returnString = c.getString(c.getColumnIndex(returncolumn));
			return returnString;
		}
		
		public ArrayList<UserMaster> customerNameList(String row)
		{
			ArrayList<UserMaster> list = new ArrayList<UserMaster>();
			Cursor c = db.query("usermaster", null,"UserID = ?", new String[]{row}, null, null, null);
			if(!c.moveToFirst())
				return list;
			UserMaster um = new UserMaster();
			um.setCustName(c.getString(c.getColumnIndex("CustName")));
			um.setCustomer(c.getString(c.getColumnIndex("Customer")));
			list.add(um);		
			while(c.moveToNext())
			{
				um = new UserMaster();
				um.setCustName(c.getString(c.getColumnIndex("CustName")));
				um.setCustomer(c.getString(c.getColumnIndex("Customer")));
				list.add(um);		
			}
			return list;
			
		}
		
		AphaseItemTemplate getAphaseItemTemplate()
		{
			AphaseItemTemplate aitl = new AphaseItemTemplate();
			//	Cursor c = db.query(tablename,new String[](column,column2),"",null,null,null,null);
			Cursor c = db.query("aphaseitemtemplate", null, "ItemNumber= ?" , new String[]{stateitemid}, null, null, null);
		//	Cursor c = db.query(tablename, null, column + " = ?", new String[]{row}, null, null, null);

			if(c.moveToFirst())
			{
				aitl.setCustomer(c.getString(c.getColumnIndex("Customer")));
				aitl.setItemNumber(c.getString(1));
				aitl.setCustPart(c.getString(2));
				aitl.setDescription(c.getString(3));
				aitl.setSrm(c.getString(4));
				aitl.setPrice(c.getString(5));
				aitl.setUom(c.getString(6));
				aitl.setOnOrder(c.getString(7));
				aitl.setReturnable(c.getString(8));
			}
			return aitl;
		}
		
		public void closeDB()
		{
			db.close();
		}
	}

	public void enteruserid()
	{
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.simpledialoglayout, null);
  	  	final EditText et   = (EditText)promptsView.findViewById(R.id.editTextDialogUserInput);
			ad.setTitle("enter UserID").setView(promptsView);
			ad.setPositiveButton("ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User clicked OK button
		        	   tempstring = et.getText().toString();
	        		   dialog.dismiss();
		        	   Log.e("ysy", tempstring);
		 //       	   this.state = ENTERPIN;
		        	   if(checkUserID(tempstring))
		        	   {
		        		   state = ENTERPIN;
		        		   stateuserid = tempstring;
		        		   enteruserpin();
		        		   //  state = ENTERPIN;
		        	   }
		        	   else {
		        		   Log.e("ysy","wrong UserID");
		        		   enteruserid();
		        	   }
		           }
		       }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					//User clicked cancel button
					
				}
			}).show();

	}
	
	public void enteruserpin()
	{
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.simpledialoglayout, null);
  	  	final EditText et   = (EditText)promptsView.findViewById(R.id.editTextDialogUserInput);	
		ad.setTitle("enter PIN").setView(promptsView);
		ad.setPositiveButton("ok", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User clicked OK button
	        	   tempstring = et.getText().toString();
	        	   Log.e("ysy", tempstring);
	        	   if(checkPin(tempstring))
	        	   {
	        		   state = CHOOSECUSTOMER;
	        		   UserMasterDB umdb = new UserMasterDB();
	        		   umdb.openDB();
	        		ArrayList<UserMaster> aList= umdb.customerNameList(stateuserid);
	        		   umdb.closeDB();
	        		   showlistdialog(state,aList);
	        	   }
	        	   else
	        	   {
	        		   state = ENTERUSERID;	        		   
	        		   showdialog(ENTERUSERID);
	        	   }
	           }
	       }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//User clicked cancel button			
			}
		}).show();
	}
	
	
	public void choosecustomer(ArrayList<UserMaster> alist)
	{
		final ArrayList<UserMaster> list = alist;
		final String[] st = new String[list.size()];

			for(int i = 0 ; i < list.size(); i++)
			{
				st[i] = list.get(i).getCustName();
			}
			new AlertDialog.Builder(this).setTitle("Choose customerName").setItems(st,
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Log.e("ysy",st[which]);
							
							//find the costomer in the  usermaster to see whether it is returnable customer master 
							UserMasterDB umdb = new UserMasterDB();
							umdb.openDB();
							statecustomer = list.get(which).getCustomer();
							statereturnable = umdb.returnDBString("usermaster", "UserID", stateuserid, "Customer", list.get(which).getCustomer(), "Returnable");
							Log.e("ysy", "returnable " + statereturnable);
//							if(statereturnable == "Y")
//							{
//								//whether it can be returned?
//								
//								showTwoButtonDialog("Returnable transaction?");
//							}
//							else if (statereturnable == "N") {
//								//
//								
//							}
//							showTwoButtonDialog("Returnable transaction?");
							umdb.closeDB();
							chooseReturnable();
						}
					}
					).show();
	}
	
	public void chooseReturnable()
	{
		if(statereturnable == "Y")
		{
			//whether it can be returned?
			
			//showTwoButtonDialog("Returnable transaction?");
			new AlertDialog.Builder(this).setTitle("Returnable transaction?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					statereturnable = "Y";
//					showdialog(state);
					entershipto();
				}
			}).setNegativeButton("No",new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							statereturnable = "N";
						}
				
					}).show();		

		}
		else if (statereturnable == "N") {
			//
			
		}
	
	}
	
	public void entershipto()
	{
		/*
		 * AlertDialog*/
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.simpledialoglayout, null);
  	  	final EditText et   = (EditText)promptsView.findViewById(R.id.editTextDialogUserInput);
  	  	ad.setTitle("Scan/Enter Shipto or Cost Code").setView(promptsView);
  	  	ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String tempString = et.getText().toString();
				if(checkShiptoNum(tempString))
				{
					//TODO find the autocrib
					if(autocribflag()=="Y")
					{
						//TODO add a shipdate
						showTimeDialog();
					}
					else {
						if(workorderflag() == "Y")
						{
							
						}
					}
				}
				else
				{
					Toast.makeText(TransactionActivity.this, "Some message!", Toast.LENGTH_LONG).show();
					
				}
			}
		}).show();
	}
	
	public String autocribflag()
	{
			UserMasterDB umdb = new UserMasterDB();
			umdb.openDB();
			String stateAutoCrib = umdb.returnDBString("customermaster", "Customer", statecustomer, "shiptonumber", stateshiptonumer, "Autocrib");
			umdb.closeDB();
			return stateAutoCrib;
	}
	
	public String workorderflag()
	{
		UserMasterDB umdb = new UserMasterDB();
		umdb.openDB();
		String stateWorkerString = umdb.returnDBString("customermaster", "Customer", statecustomer, "shiptonumber", stateshiptonumer, "WorkOrder");
		umdb.closeDB();
		return stateWorkerString;
	}
	
	public void workordernumber()
	{
		LayoutInflater li  = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.simpledialoglayout, null);
		final EditText et = (EditText)promptsView.findViewById(R.id.editTextDialogUserInput);
		ad.setTitle("Enter work order number").setPositiveButton("ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO add scanitem function get the string to somewhere
				Log.e("ysy", "work order" + et.getText().toString());
			}
		}).show();
	}
	
	
	//Item scanning process
	public void scanitem()
	{
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.simpledialoglayout, null);
		final EditText et = (EditText)promptsView.findViewById(R.id.editTextDialogUserInput);
		ad.setTitle("Scan or enter ItemID").setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				stateitemid = et.getText().toString();
				Log.e("ysy", "itemid "+ stateitemid);
				if(checkItemId(stateitemid))
				{
					AphaseItemTemplate newAphaseItemTemplate = getItemFromDB();
					//TODO display item information
				}
				else {
					//TODO please input the valid item number
				}
			}
		}).show();
	}
	
	AphaseItemTemplate getItemFromDB()
	{
		UserMasterDB umdb = new UserMasterDB();
		umdb.openDB();
			AphaseItemTemplate ait = umdb.getAphaseItemTemplate();		
		umdb.closeDB();
		return ait;
	}

	
	
	public void showdialog(int astate)
	{
		/*
		 * AlertDialog*/
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.simpledialoglayout, null);
  	  	final EditText et   = (EditText)promptsView.findViewById(R.id.editTextDialogUserInput);
		switch(state)
		{
		case ENTERUSERID:// enter UserID
		{
			ad.setTitle("enter UserID").setView(promptsView);
			ad.setPositiveButton("ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User clicked OK button
		        	   tempstring = et.getText().toString();
	        		   dialog.dismiss();
		        	   Log.e("ysy", tempstring);
		 //       	   this.state = ENTERPIN;
		        	   if(checkUserID(tempstring))
		        	   {
		        		   state = ENTERPIN;
		        		   stateuserid = tempstring;
		        		   showdialog(ENTERPIN);
		        		 //  state = ENTERPIN;
		        	   }
		        	   else {
		        		   Log.e("ysy","wrong UserID");
		        		   showdialog(ENTERUSERID);
		        	   }
		           }
		       }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 
					//User clicked cancel button
					
				}
			}).show();
			break;
		}
		case ENTERPIN:// enter UserID
		{
			ad.setTitle("enter PIN").setView(promptsView);
			ad.setPositiveButton("ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User clicked OK button
		        	   tempstring = et.getText().toString();
		        	   Log.e("ysy", tempstring);
		        	   if(checkPin(tempstring))
		        	   {
		        		   state = CHOOSECUSTOMER;
		        		   UserMasterDB umdb = new UserMasterDB();
		        		   umdb.openDB();
		        		ArrayList<UserMaster> aList= umdb.customerNameList(stateuserid);
		        		   umdb.closeDB();
		        		   showlistdialog(state,aList);
		        	   }
		        	   else
		        	   {
		        		   state = ENTERUSERID;
		        		   
		        		   showdialog(ENTERUSERID);
		        	   }
		 //       	   this.state = ENTERPIN;
		  //      	   showdialog(ENTERPIN);
		           }
		       }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 
					//User clicked cancel button
					
				}
			}).show();
			break;
		}
		case CHOOSERETURNABLE:
		{
			state = COSTCODE;
			
			LayoutInflater li2 = LayoutInflater.from(this);
			View promptsView2 = li2.inflate(R.layout.simpledialoglayout, null);
	  	  	final EditText et2   = (EditText)promptsView2.findViewById(R.id.editTextDialogUserInput);
	  	  	et2.setKeyListener(new DigitsKeyListener(false, true));
			
			
			ad.setTitle("Scan/Enter Shipto or Cost code").setView(promptsView);
			ad.setPositiveButton("ok", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 
					//use the database to confirm the shipto number
					String tempString = et2.getText().toString();
					if(checkShiptoNum(tempString))
					{
		//				checkautocrib;
					}
					else
					{
	//					Toast.makeText(this, " is not exist in database", Toast.LENGTH_LONG).show();
					}
				}
			}).show();
			break;
		}
//		case CHOOSESHIPTONUMBER:
		}	
	//	ad.setTitle(title)

	}
	
	public void showTimeDialog()
	{
		DatePickerDialog dialog = new DatePickerDialog(this,  
                    dateListener,  
                    calendar.get(Calendar.YEAR),  
                    calendar.get(Calendar.MONTH),  
                    calendar.get(Calendar.DAY_OF_MONTH));
	//	dialog.setButton(DialogInterface.BUTTON_NEGATIVE, listener);
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel",new  DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which==DialogInterface.BUTTON_NEGATIVE)
				Log.e("ysy", "show time");			}
		});
		dialog.show();
	}
	
	DatePickerDialog.OnDateSetListener dateListener =   
		    new DatePickerDialog.OnDateSetListener() {  
		        @Override  
		        public void onDateSet(DatePicker datePicker,   
		                int year, int month, int dayOfMonth) {  
//		            EditText editText =   
//		                (EditText) findViewById(R.id.editText);  
//		             //Calendar月份是从0开始,所以month要加1  
//		            editText.setText("你选择了" + year + "年" +   
//		                    (month+1) + "月" + dayOfMonth + "日");  
		        	Log.e("ysy","year "+ year + "month " + month + "day " + dayOfMonth);
		        	if(workorderflag()=="Y")
		        	{
		        		
		        	}
		        }  
		    };  
	
	public void showTwoButtonDialog(String title)
	{
		switch (state) {
		case CHOOSECUSTOMER:
		{
			state = CHOOSERETURNABLE;
			if(statereturnable == "Y")
			{
				//whether it can be returned?
				
				//showTwoButtonDialog("Returnable transaction?");
				showdialog(state);
			}
			else if (statereturnable == "N") {
				//
				
			}
			new AlertDialog.Builder(this).setTitle(title).setPositiveButton("yes", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					statereturnable = "Y";
					showdialog(state);
				}
			}).setNegativeButton("No",new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 
							statereturnable = "N";
						}
				
					}).show();
			break;
		}
		default:
			break;
		}

	}
	
	public void showlistdialog(int state, ArrayList<UserMaster> alist)
	{
		final ArrayList<UserMaster> list = alist;
		final String[] st = new String[list.size()];
		switch (state) {
		case CHOOSECUSTOMER:
		{
			for(int i = 0 ; i < list.size(); i++)
			{
				st[i] = list.get(i).getCustName();
			}
			new AlertDialog.Builder(this).setTitle("Choose customerName").setItems(st,
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 
							Log.e("ysy",st[which]);
							
							//find the costomer in the  usermaster to see whether it is returnable customer master 
							UserMasterDB umdb = new UserMasterDB();
							umdb.openDB();
							statecustomer = list.get(which).getCustomer();
							statereturnable = umdb.returnDBString("usermaster", "UserID", stateuserid, "Customer", list.get(which).getCustomer(), "Returnable");
							Log.e("ysy", "returnable " + statereturnable);
							if(statereturnable == "Y")
							{
								//whether it can be returned?
								
								showTwoButtonDialog("Returnable transaction?");
							}
							else if (statereturnable == "N") {
								//
								
							}
//							showTwoButtonDialog("Returnable transaction?");
							umdb.closeDB();
							
						}
					}
					).show();
			break;
		}
		default:
			break;
		}
		
		{
			UserMasterDB umdb = new UserMasterDB();
			umdb.openDB();
	//		String stateAutoCrib = umdb.returnDBString("customermaster", "Customer", statecustomer, "", row2, returncolumn)
		}

	}

	
	

	boolean checkUserID(String id)
	{
		//find id in USER master database
		UserMasterDB umdb = new UserMasterDB();
		umdb.openDB();
		boolean tpstate =  umdb.checkexit("usermaster", id, "UserID");
		umdb.closeDB();
		return tpstate;
	}
	
	boolean checkPin(String userPin)
	{
		//find id in USER master database
		UserMasterDB umdb = new UserMasterDB();
		umdb.openDB();
		boolean tpstate =  umdb.checkexit("usermaster", userPin, "UserPin");
		umdb.closeDB();
		return tpstate;		
	}
	
	boolean checkShiptoNum(String shipto)
	{
		UserMasterDB umdb = new UserMasterDB();
		umdb.openDB();
		boolean tpstate = umdb.checkexit("customermaster", shipto,"ShiptoNumber" );
		umdb.closeDB();
		return tpstate;
	}
	
	boolean checkItemId(String itemid)
	{
		UserMasterDB umdb = new UserMasterDB();
		umdb.openDB();
		boolean tpstate = umdb.checkexit("itemmaster", itemid, "ItemNumber");
		umdb.closeDB();
		return tpstate;
	}
	

	
	public void bluetooth()
	{
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
//		registerReceiver(search, filter)
		uuid = UUID.randomUUID();
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBluetoothAdapter == null)
		{
			//this will change to an alert dialog
			Log.e("ysy", "this service do not support bluetooth");
		}
		
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if(pairedDevices.size() > 0)
		{
			for(BluetoothDevice device : pairedDevices)
			{
				
			}
		}
		   // Create a BroadcastReceiver for ACTION_FOUND
		final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		    public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        // When discovery finds a device
		        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		            // Get the BluetoothDevice object from the Intent
		            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		            // Add the name and address to an array adapter to show in a ListView
		       //     mArrayAdapter.add(device.getName() + "n" + device.getAddress());
		        }
		    }
		};
		
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
	
		
		
		


		bluetoothGetMessage();
		ConnectThread clientThread = null;
		if(pairedDevices.size() > 0)
		{
			for(BluetoothDevice device : pairedDevices)
			{
				clientThread = new ConnectThread(device);
			}
		}
		clientThread.start();
		/*
		 * Handler
		 * */
		Handler handler = new Handler()
		{
		    public void handleMessage(Message msg) {
		        switch (msg.what) {
		        case ENTERUSERID: //state 1
		        {
		        	tempstring = msg.obj.toString();
		        	if(checkUserID(tempstring))
		        	{
		        		state = ENTERPIN;
		        		stateuserid = tempstring;
		        		showdialog(ENTERPIN);
		        	}
		        	else {
		        		Log.e("ysy","wrong UserID");
		        		state = ENTERUSERID;
		        		showdialog(ENTERUSERID);
		        	   }
		        	break;
		        }
		        case ENTERPIN:
		        {
		        	Log.e("ysy", "enterpin");
		        	tempstring = msg.obj.toString();
		        	   if(checkPin(tempstring))
		        	   {
		        		   state = CHOOSECUSTOMER;
		        		   UserMasterDB umdb = new UserMasterDB();
		        		   umdb.openDB();
		        		ArrayList<UserMaster> aList= umdb.customerNameList(stateuserid);
		        		   umdb.closeDB();
		        		   showlistdialog(state,aList);
		        	   }
		        	   else
		        	   {
		        		   state = ENTERUSERID;
		        		   
		        		   showdialog(ENTERUSERID);
		        	   }
		        	break;
		        }
//		          case SOCKET_CONNECTED: {
//		            mBluetoothConnection = (ConnectionThread) msg.obj;
//		            if (!mServerMode)
//		              mBluetoothConnection.write("this is a message".getBytes());
//		            break;
//		          }
//		          case DATA_RECEIVED: {
//		            data = (String) msg.obj;
//		            tv.setText(data);
//		            if (mServerMode)
//		             mBluetoothConnection.write(data.getBytes());
		      }
			    super.handleMessage(msg); 
		    }
		};
		
		ConnectedThread connectedThread = new ConnectedThread(clientThread.mmSocket,handler);
		connectedThread.start();
	}



}

