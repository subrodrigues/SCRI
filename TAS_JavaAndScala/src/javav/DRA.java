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

// This file was genereted from "D:\\FEUP\\4o_Ano\\2o_Semestre\\SCRI\\Variant_Forma_Methods\\TASVariant\\DRA.vdmpp".

// ***** VDMTOOLS START Name=package KEEP=NO
// ***** VDMTOOLS END Name=package

// ***** VDMTOOLS START Name=imports KEEP=NO
import jp.vdmtools.VDM.UTIL;
import jp.vdmtools.VDM.Sentinel;
import jp.vdmtools.VDM.EvaluatePP;
import jp.vdmtools.VDM.CGException;
// ***** VDMTOOLS END Name=imports



public abstract class DRA implements EvaluatePP {

// ***** VDMTOOLS START Name=sentinel KEEP=NO
  volatile Sentinel sentinel;
// ***** VDMTOOLS END Name=sentinel


// ***** VDMTOOLS START Name=DRASentinel KEEP=NO
  class DRASentinel extends Sentinel {

    public final int getResult = 0;

    public final int nr_functions = 1;


    public DRASentinel () throws CGException {}


    public DRASentinel (EvaluatePP instance) throws CGException {
      init(nr_functions, instance);
    }

  }
// ***** VDMTOOLS END Name=DRASentinel
;

// ***** VDMTOOLS START Name=evaluatePP#1|int KEEP=NO
  public Boolean evaluatePP (int fnr) throws CGException {
    return Boolean.TRUE;
  }
// ***** VDMTOOLS END Name=evaluatePP#1|int


// ***** VDMTOOLS START Name=setSentinel KEEP=NO
  public void setSentinel () {
    try {
      sentinel = new DRASentinel(this);
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
// ***** VDMTOOLS END Name=setSentinel

// ***** VDMTOOLS START Name=A0 KEEP=NO
  protected static final Number A0 = new Double(661.47);
// ***** VDMTOOLS END Name=A0

// ***** VDMTOOLS START Name=T0 KEEP=NO
  protected static final Number T0 = new Double(288.15);
// ***** VDMTOOLS END Name=T0


// ***** VDMTOOLS START Name=vdm_init_DRA KEEP=NO
  private void vdm_init_DRA () {
    try {
      setSentinel();
    }
    catch (Exception e) {
      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
  }
// ***** VDMTOOLS END Name=vdm_init_DRA


// ***** VDMTOOLS START Name=DRA KEEP=NO
  public DRA () throws CGException {
    vdm_init_DRA();
  }
// ***** VDMTOOLS END Name=DRA


// ***** VDMTOOLS START Name=getResult#3|Number|Number|Number KEEP=NO
  abstract public Number getResult (final Number pe, final Number pd, final Number t) throws CGException ;
// ***** VDMTOOLS END Name=getResult#3|Number|Number|Number

}
;