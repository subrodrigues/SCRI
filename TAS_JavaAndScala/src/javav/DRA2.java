package javav;
//

// THIS FILE IS AUTOMATICALLY GENERATED!!
//
// Generated at 2014-05-10 by the VDM++ to JAVA Code Generator
// (v9.0.2 - Thu 14-Mar-2013 12:36:47 +0900)
//
// Supported compilers: jdk 1.4/1.5/1.6
//

// ***** VDMTOOLS START Name=HeaderComment KEEP=NO
// ***** VDMTOOLS END Name=HeaderComment

// This file was genereted from "D:\\FEUP\\4o_Ano\\2o_Semestre\\SCRI\\Variant_Forma_Methods\\TASVariant\\DRA2.vdmpp".

// ***** VDMTOOLS START Name=package KEEP=NO
// ***** VDMTOOLS END Name=package

// ***** VDMTOOLS START Name=imports KEEP=NO
import jp.vdmtools.VDM.UTIL;
import jp.vdmtools.VDM.Sentinel;
import jp.vdmtools.VDM.EvaluatePP;
import jp.vdmtools.VDM.CGException;
// ***** VDMTOOLS END Name=imports



public class DRA2 extends DRA {


// ***** VDMTOOLS START Name=DRA2Sentinel KEEP=NO
  class DRA2Sentinel extends DRASentinel {

    public final int getResult = 1;

    public final int nr_functions = 2;


    public DRA2Sentinel () throws CGException {}


    public DRA2Sentinel (EvaluatePP instance) throws CGException {
      init(nr_functions, instance);
    }

  }
// ***** VDMTOOLS END Name=DRA2Sentinel
;

// ***** VDMTOOLS START Name=setSentinel KEEP=NO
  public void setSentinel () {
    try {
      sentinel = new DRA2Sentinel(this);
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
// ***** VDMTOOLS END Name=setSentinel


// ***** VDMTOOLS START Name=vdm_init_DRA2 KEEP=NO
  private void vdm_init_DRA2 () {}
// ***** VDMTOOLS END Name=vdm_init_DRA2


// ***** VDMTOOLS START Name=DRA2 KEEP=NO
  public DRA2 () throws CGException {
    vdm_init_DRA2();
  }
// ***** VDMTOOLS END Name=DRA2


// ***** VDMTOOLS START Name=getResult#3|Number|Number|Number KEEP=NO
  public Number getResult (final Number pe, final Number pd, final Number t) throws CGException {
    sentinel.entering(((DRA2Sentinel)sentinel).getResult);
    try {
      return A0.doubleValue()*m(pd,pe).doubleValue()*Math.sqrt(t(pe,pd,t)/T0.doubleValue());
    }
    finally {
      sentinel.leaving(((DRA2Sentinel)sentinel).getResult);
    }
  }
// ***** VDMTOOLS END Name=getResult#3|Number|Number|Number
  
  private Number m(Number pd, Number pe){
	  double power = 2.0/7.0;
	  double div =  pd.doubleValue()/pe.doubleValue();
	  div = div +1;
	  div = Math.pow(div, power);
	  return Math.sqrt(5*(div-1));
  }
  
  private double t(Number pe, Number pd, Number t) {
	  double power = 2.0/7.0;
	  double div =  pd.doubleValue()/pe.doubleValue();
	  div = div +1;
	  div = Math.pow(div, power);
	  
		return t.doubleValue()/div;
	}

}
;