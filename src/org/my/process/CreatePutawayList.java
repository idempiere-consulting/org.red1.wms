/*** Licensed under the KARMA v.1 Law of Sharing. As others have shared freely to you, so shall you share freely back to us.* If you shall try to cheat and find a loophole in this license, then KARMA will exact your share,* and your worldly gain shall come to naught and those who share shall gain eventually above you.* In compliance with previous GPLv2.0 works of Jorg Janke, Low Heng Sin, Carlos Ruiz and contributors.* This Module Creator is an idea put together and coded by Redhuan D. Oon (red1@red1.org)*/package org.my.process;
import org.compiere.process.ProcessInfoParameter;
import java.util.List;
import org.compiere.model.Query;
import org.compiere.util.Env;
import java.sql.SQLException;import java.math.BigDecimal;
import java.sql.PreparedStatement;
import org.compiere.util.DB;
import org.adempiere.exceptions.AdempiereException;import org.compiere.model.MInOut;import org.compiere.model.MProduct;
import org.compiere.model.MSequence;import org.compiere.model.PO;
import org.my.model.MWM_DeliveryScheduleLine;import org.my.model.MWM_EmptyStorage;import org.my.model.MWM_EmptyStorageLine;import org.my.model.MWM_HandlingUnit;import org.my.model.MWM_HandlingUnitHistory;import org.my.model.MWM_InOut;import org.my.model.MWM_InOutLine;import org.my.model.MWM_PreferredProduct;import org.my.model.MWM_ProductType;import org.my.model.MWM_StorageType;import org.my.model.X_WM_HandlingUnit;
import org.compiere.process.SvrProcess;

	public class CreatePutawayList extends SvrProcess {	private int M_Warehouse_ID = 0; 	private int WM_HandlingUnit_ID = 0; 	private boolean IsSameDistribution = false;
	private boolean IsSameLine = true;	private String X = "999"; 	private String Y = "999";	private String Z = "999";	private int putaways;
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)					;
				else if(name.equals("WM_HandlingUnit_ID")){
					WM_HandlingUnit_ID = p.getParameterAsInt();
				}						else if(name.equals("IsSameLine")){					IsSameLine = (Boolean)p.getParameterAsBoolean();				}				else if(name.equals("M_Warehouse_ID")){				M_Warehouse_ID = p.getParameterAsInt();				}
				else if(name.equals("IsSameDistribution")){
					IsSameDistribution = "Y".equals(p.getParameter());
				}
				else if(name.equals("X")){
					X = (String)p.getParameter();
				}
				else if(name.equals("Y")){
					Y = (String)p.getParameter();
				}
				else if(name.equals("Z")){
					Z = (String)p.getParameter();
			}
		}
	}
	protected String doIt() {
		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE T_Selection.AD_PInstance_ID=? AND T_Selection.T_Selection_ID=WM_DeliveryScheduleLine.WM_DeliveryScheduleLine_ID)";

		List<MWM_DeliveryScheduleLine> lines = new Query(Env.getCtx(),MWM_DeliveryScheduleLine.Table_Name,whereClause,get_TrxName())
		.setParameters(getAD_PInstance_ID()).list();
		MWM_InOut inout = new MWM_InOut(Env.getCtx(),0,get_TrxName());		inout.setWM_DeliverySchedule_ID(lines.get(0).getWM_DeliverySchedule_ID());		inout.setName(lines.get(0).getWM_DeliverySchedule().getName());		inout.setIsSOTrx(lines.get(0).getWM_DeliverySchedule().isSOTrx());		inout.setWM_Gate_ID(lines.get(0).getWM_DeliverySchedule().getWM_Gate_ID());		inout.saveEx(get_TrxName());		putaways = 0;
		for (MWM_DeliveryScheduleLine line:lines){						if (line.getWM_InOutLine_ID()>0 || !line.isReceived())			continue;//already done						//running balance in use thru-out here			BigDecimal balance =line.getQtyDelivered();										//get Product from InOut Bound line			MProduct product = (MProduct) line.getM_Product();						//if Handling Unit is set, then assign while creating WM_InOuts. EmptyLocators also assigned. Can be cleared and reassigned in next Info-Window			if (WM_HandlingUnit_ID<1) {				doInOutLine(inout,line,balance);				continue;			}			//check if defined in PreferredProduct...			List<MWM_PreferredProduct> preferreds = new Query(Env.getCtx(),MWM_PreferredProduct.Table_Name,MWM_PreferredProduct.COLUMNNAME_M_Product_ID+"=?" ,get_TrxName())					.setParameters(product.get_ID())					.setOrderBy(MWM_PreferredProduct.COLUMNNAME_M_Locator_ID)					.list();			boolean done=false;			if (preferreds!=null){				for (MWM_PreferredProduct preferred:preferreds){					 					if (M_Warehouse_ID>0){						if (preferred.getM_Locator().getM_Warehouse_ID()!=M_Warehouse_ID)							continue; 					}					if (preferred.getM_Locator().getX().compareTo(X)>=0 || preferred.getM_Locator().getY().compareTo(Y)>=0  || preferred.getM_Locator().getZ().compareTo(Z)>=0 )						continue;					//get next EmptyStorage, if fit, then break, otherwise if balance, then continue					int locator_id = preferred.getM_Locator_ID();					balance = startWithEmptyStorage(inout,line,balance,locator_id);					if (balance.compareTo(Env.ZERO)>0)						continue;					else {						done=true;						break;					}				}			} 			if (done)				continue; //done but go to next DeliveryScheduleLine as this is the outermost For Loop.						//get ProductType = StorageType			MWM_ProductType prodtype = new Query(Env.getCtx(),MWM_ProductType.Table_Name,MWM_ProductType.COLUMNNAME_M_Product_ID+"=?",get_TrxName())					.setParameters(product.get_ID())					.first();			 	if (prodtype!=null){			 		String prodtypestring = prodtype.getTypeString();					if (prodtypestring==null || prodtypestring.isEmpty())						throw new AdempiereException("RUN Set Type String for faster processing"); 										List<MWM_StorageType> stortypes= new Query(Env.getCtx(),MWM_StorageType.Table_Name,MWM_StorageType.COLUMNNAME_TypeString+"=?",get_TrxName())							.setParameters(prodtypestring)							.setOrderBy("Created")							.list();									for (MWM_StorageType stortype:stortypes){												if (stortype!=null){							if (stortype.getM_Locator().getX().compareTo(X)>=0 || stortype.getM_Locator().getY().compareTo(Y)>=0  || stortype.getM_Locator().getZ().compareTo(Z)>=0 )								continue;							if (M_Warehouse_ID>0)								if (stortype.getM_Locator().getM_Warehouse_ID()!=M_Warehouse_ID)									continue;														//get next EmptyStorage, if fit, then break, otherwise if balance, then continue							int locator_id = stortype.getM_Locator_ID(); 							balance = startWithEmptyStorage(inout,line,balance,locator_id);							if (balance.compareTo(Env.ZERO)>0)								continue;							else {								done=true;								break;							}						}						}			 	}							 if (done)					continue; //enough, i already putaway all.			//get non reserved empty storage			List<MWM_EmptyStorage> empties = new Query(Env.getCtx(),MWM_EmptyStorage.Table_Name,MWM_EmptyStorage.COLUMNNAME_IsFull+"=?",get_TrxName())				.setParameters(false)				.list();							if (empties==null)				throw new AdempiereException("NO MORE EMPTY STORAGE");						for (MWM_EmptyStorage empty:empties){				if (M_Warehouse_ID>0)					if (empty.getM_Locator().getM_Warehouse_ID()!=M_Warehouse_ID)						continue;				if (empty.getM_Locator().getX().compareTo(X)>=0 || empty.getM_Locator().getY().compareTo(Y)>=0  || empty.getM_Locator().getZ().compareTo(Z)>=0 )						continue;				//get next EmptyStorage, if fit, then break, otherwise if balance, then continue				int locator_id = empty.getM_Locator_ID();				balance = startWithEmptyStorage(inout,line,balance,locator_id);				if (balance.compareTo(Env.ZERO)>0)					continue;				else {					break;				}			}		}		sortFinalList(inout);		
	return "Successful Putaways: "+putaways;

	}	/**	 * 	 * @param balance	 * @param locator_id	 * @return balance of unallocated qty to empty storage	 */	private BigDecimal startWithEmptyStorage(MWM_InOut inout, MWM_DeliveryScheduleLine dsline, BigDecimal balance, int locator_id) {		MWM_EmptyStorage empty = new Query(Env.getCtx(),MWM_EmptyStorage.Table_Name,MWM_EmptyStorage.COLUMNNAME_M_Locator_ID+"=?",get_TrxName())				.setParameters(locator_id)				.first();		if (empty==null)			throw new AdempiereException("No Empty Storage set for locator id: "+locator_id);				//if its full go back and look for next EmptyStorage		if (empty.isFull())			return balance;		//allotted holds for underlying EmtpyStorageLine detail		BigDecimal alloted = balance;		//check if its vacant capacity can handle the balance.		//pack factor is multiplied into VacantCapacity denominator.		MProduct product = (MProduct) dsline.getM_Product();		BigDecimal PackFactor = new BigDecimal(product.getUnitsPerPack());		 		BigDecimal available = empty.getAvailableCapacity().divide(PackFactor);		if (balance.compareTo(available)>=0 && IsSameLine==false){			empty.setAvailableCapacity(Env.ZERO);			balance = balance.subtract(available);				alloted = available;		} else {			available = available.subtract(balance);			if (available.compareTo(Env.ZERO)<0)//available insufficient				return balance;			empty.setAvailableCapacity(available.multiply(PackFactor));			alloted = balance;			balance = Env.ZERO;		}  		MWM_InOutLine inoutline = doInOutLine(inout,dsline,alloted); 				setPutawayLocator(inoutline,locator_id);				//create EmptyStorageDetails		setEmptyStorageLine(alloted,empty,inoutline);				//calculate Percentage Vacant		calculatePercentageVacant(empty);				//assign Handling Unit		assignHandlingUnit(inoutline,empty,alloted);		 		return balance;	}	private MWM_InOutLine doInOutLine(MWM_InOut inout, MWM_DeliveryScheduleLine dsline, BigDecimal alloted) {		MWM_InOutLine inoutline = new MWM_InOutLine(Env.getCtx(),0,get_TrxName());		inoutline.setWM_InOut_ID(inout.get_ID());		inoutline.setC_UOM_ID(dsline.getC_UOM_ID());		inoutline.setC_OrderLine_ID(dsline.getC_OrderLine_ID());		inoutline.setM_Product_ID(dsline.getM_Product_ID());		inoutline.setQtyPicked(alloted);		inoutline.setWM_DeliveryScheduleLine_ID(dsline.get_ID());		inoutline.saveEx(get_TrxName());		return inoutline;	}	private void setPutawayLocator(MWM_InOutLine line, int putaway) { 		line.setM_Locator_ID(putaway);		line.saveEx(get_TrxName());		putaways++;	}	private void setEmptyStorageLine(BigDecimal alloted, MWM_EmptyStorage empty, MWM_InOutLine inoutline) {		MWM_EmptyStorageLine storline = new MWM_EmptyStorageLine(Env.getCtx(),0,get_TrxName());		storline.setWM_EmptyStorage_ID(empty.get_ID());		storline.setWM_InOutLine_ID(inoutline.getWM_InOutLine_ID());		storline.setQtyMovement(alloted);		storline.setC_UOM_ID(inoutline.getC_UOM_ID());		storline.setM_Product_ID(inoutline.getM_Product_ID());		storline.saveEx(get_TrxName()); 	}	private void calculatePercentageVacant(MWM_EmptyStorage empty) {		empty.setPercentage((empty.getAvailableCapacity().divide(empty.getVacantCapacity())).multiply(Env.ONEHUNDRED));		//set is Full if 0% vacant		if (empty.getPercentage().compareTo(Env.ZERO)==0)			empty.setIsFull(true);		empty.saveEx(get_TrxName());	}	private void assignHandlingUnit(MWM_InOutLine inoutline, MWM_EmptyStorage empty, BigDecimal qty) { 		MWM_HandlingUnit hu = new Query(Env.getCtx(),MWM_HandlingUnit.Table_Name,MWM_HandlingUnit.COLUMNNAME_WM_HandlingUnit_ID+"=?",get_TrxName())				.setParameters(WM_HandlingUnit_ID)				.first();		if (hu==null)			throw new AdempiereException("Handling Unit not found: "+WM_HandlingUnit_ID);				hu.setDocStatus(X_WM_HandlingUnit.DOCSTATUS_InProgress);		hu.setQtyMovement(qty);		if (!IsSameDistribution){			hu.setC_UOM_ID(inoutline.getC_UOM_ID());			hu.setM_Locator_ID(empty.getM_Locator_ID());			MWM_HandlingUnit nexhu = new Query(Env.getCtx(),MWM_HandlingUnit.Table_Name,MWM_HandlingUnit.COLUMNNAME_WM_HandlingUnit_ID+"=? AND "+MWM_HandlingUnit.COLUMNNAME_QtyMovement+"=?",get_TrxName())					.setParameters(WM_HandlingUnit_ID+1,Env.ZERO)					.first();			if (nexhu!=null) {				WM_HandlingUnit_ID = nexhu.get_ID();			}		}		hu.saveEx(get_TrxName());				//create new history		MWM_HandlingUnitHistory huh = new MWM_HandlingUnitHistory(Env.getCtx(),0,get_TrxName());		huh.setWM_HandlingUnit_ID(WM_HandlingUnit_ID);		huh.setWM_InOutLine_ID(inoutline.get_ID());		huh.setC_Order_ID(inoutline.getC_OrderLine().getC_Order_ID());		huh.setQtyMovement(qty);		huh.setC_UOM_ID(inoutline.getC_UOM_ID());		huh.setM_Product_ID(inoutline.getM_Product_ID());		huh.setDateStart(hu.getUpdated());		huh.saveEx(get_TrxName());				inoutline.setWM_HandlingUnit_ID(WM_HandlingUnit_ID);		inoutline.saveEx(get_TrxName());	}	private void sortFinalList(MWM_InOut inout) {		//sort inout list according to XYZ		List<MWM_InOutLine>iolines = new Query(Env.getCtx(),MWM_InOutLine.Table_Name,MWM_InOutLine.COLUMNNAME_WM_InOut_ID+"=?",get_TrxName())				.setParameters(inout.get_ID())				.setOrderBy(MWM_InOutLine.COLUMNNAME_M_Locator_ID)				.list();		int seq = 1;		for (MWM_InOutLine line:iolines){				line.setSequence(new BigDecimal(seq));			line.saveEx(get_TrxName());			seq++;					}	}		
}
