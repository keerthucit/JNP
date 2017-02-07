/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import jpos.*;
import jpos.events.*;

import java.util.*;

class CDrawer implements StatusUpdateListener
{
public CashDrawer cr1,cr2;

	// ----------------------------------------------------------------------
	public void statusUpdateOccurred( StatusUpdateEvent e)
	{
		System.out.println( " -------- crsu "
			+ "  " + String.valueOf( e.getStatus())
			+ "  " + e.getSource().toString() );
	}

	// ----------------------------------------------------------------------
	public void open1()
	{
		System.out.println( " -------- opening CR Demo");

		try {
			cr1 = new CashDrawer();

			cr1.addStatusUpdateListener( this);
                       
			cr1.open("CR Demo");
			
			cr1.claim(1000);
			cr1.setDeviceEnabled( true);
		} catch ( Exception e ) { System.out.println( e);}
	}

	// ----------------------------------------------------------------------
	public void open2()
	{
		System.out.println( " -------- opening aCR Demo");

		try {
			cr1 = new CashDrawer();

			cr1.addStatusUpdateListener( this);

			cr1.open("aCR Demo");
			cr1.claim(1000);
			cr1.setDeviceEnabled( true);
		} catch ( Exception e ) { System.out.println( e);}
	}

	// ----------------------------------------------------------------------
	public void close1()
	{
		try
		{
			System.out.println( " -------- closing cr1");
			cr1.removeStatusUpdateListener( this);

			cr1.setDeviceEnabled(false);
			cr1.release();
			cr1.close();
			System.out.println("JPOS CR Demo 1 - finished");
		}
		catch (Exception e)
		{
			System.out.println("JPOS CR Demo 1 - baddly finished \n"+e);
		}
	}

	// ----------------------------------------------------------------------
	public void opendr1()
	{
		try
		{
			System.out.println( " -------- open drawer 1");
			cr1.openDrawer();
			System.out.println( " -------- dr1 opened: "+cr1.getDrawerOpened() );
			System.out.println( " -------- wait for drawer close...");
			cr1.waitForDrawerClose( 1000,100,100,1000);
		}
		catch (Exception e)
		{
			System.out.println( e);
		}
	}
} // class crtest
