/**

import org.compiere.model.MOrderLine;
import org.compiere.model.Query;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

	public class CreateDeliverySchedule extends SvrProcess {

		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)
				else if(name.equals("WM_Gate_ID")){
					WM_Gate_ID = p.getParameterAsInt();
			}
				else if(name.equals("DatePromised")){
					DatePromised = p.getParameterAsTimestamp();
			}
		}
	}
	public String executeDoIt(){
		String whereClause = "EXISTS (SELECT ViewID FROM T_Selection WHERE T_Selection.AD_PInstance_ID=? AND CAST(T_Selection.ViewID AS INTEGER)=C_OrderLine.C_OrderLine_ID)";

		
		for (MOrderLine line:lines){
			int a = line.get_ID();

			log.info("Selected line ID = "+a);
			
		}

	return "RESULT: "+schedule.toString();

	}
}