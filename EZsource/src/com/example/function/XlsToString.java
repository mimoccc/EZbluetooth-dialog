package com.example.function;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.example.source.CustomerMasterfile;
import com.example.source.UserMaster;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import android.os.Environment;

public class XlsToString {
	
	
	public List catchUsermaster() throws BiffException, IOException
	{
		  
		  String path = Environment.getExternalStorageDirectory().getPath();
		  File file = new File(path + "/Ezsource/UserMaster");
		  String filename = file.listFiles()[0].getPath();
	//	  file = new File(filename);
		  file = new File("/mnt/sdcard/Ezsource/UserMaster/UserMaster.xls");
	//	  filename = path + "/Ezsource/UserMaster/" + "UserMaster.xls";

	//	  InputStream is = new FileInputStream(filename);
		  Workbook rwb;
		  List<UserMaster> list = new ArrayList<UserMaster>();
		  try{
			  rwb = Workbook.getWorkbook(file);
			  Sheet rs = rwb.getSheet(0);
	//		  Cell c00 = rs.getCell(0, 0);
			  int columnsnum = rs.getColumns();
			  int rowsnum = rs.getRows();
			  for(int i=1; i<rowsnum ; i++)
			  {				
			//	  Cell cc = rs.getCell(i,0);
				  Cell[] cc = rs.getRow(i);
				  UserMaster um = new UserMaster(cc[0].getContents(),
						  cc[1].getContents(),
						  cc[2].getContents(), 
						  cc[3].getContents(), 
						  cc[4].getContents(), 
						  cc[5].getContents()) ;
				  list.add(um);
			  }
		  }
		  catch(Exception e)
		  {
			  
		  }
		  return list;
	}
	
	
	public List catchCustomermaster() throws BiffException, IOException
	{
		  
		  String path = Environment.getExternalStorageDirectory().getPath();
		  File file = new File(path + "/Ezsource/UserMaster");
		  String filename = file.listFiles()[0].getPath();
	//	  file = new File(filename);
		  file = new File("/mnt/sdcard/Ezsource/CustomerMaster/CustomerMaster.xls");
	//	  filename = path + "/Ezsource/UserMaster/" + "UserMaster.xls";

	//	  InputStream is = new FileInputStream(filename);
		  Workbook rwb;
		  List<CustomerMasterfile> list = new ArrayList<CustomerMasterfile>();
		  try{
			  rwb = Workbook.getWorkbook(file);
			  Sheet rs = rwb.getSheet(0);
	//		  Cell c00 = rs.getCell(0, 0);
			  int columnsnum = rs.getColumns();
			  int rowsnum = rs.getRows();
			  for(int i=1; i<rowsnum ; i++)
			  {				
			//	  Cell cc = rs.getCell(i,0);
				  Cell[] cc = rs.getRow(i);
				  CustomerMasterfile cm= new CustomerMasterfile(cc[0].getContents(), 
						  cc[1].getContents(), 
						  cc[2].getContents(), 
						  cc[3].getContents(), 
						  cc[4].getContents(), 
						  cc[5].getContents(), 
						  cc[6].getContents(), 
						  cc[7].getContents(), 
						  cc[8].getContents(), 
						  cc[9].getContents(), 
						  cc[10].getContents(), 
						  cc[11].getContents(), 
						  cc[12].getContents(), 
						  cc[13].getContents(), 
						  cc[14].getContents(), 
						  cc[15].getContents());

				  list.add(cm);
			  }
		  }
		  catch(Exception e)
		  {
			  
		  }
		  return list;
	}
}
