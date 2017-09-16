/**

import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

	public class AssignHandlingUnit extends SvrProcess {


		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)
				else if(name.equals("WM_HandlingUnit_ID")){
					WM_HandlingUnit_ID = p.getParameterAsInt();
			}	else if(name.equals("IsSameDistribution")){
		}
	}

		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE T_Selection.AD_PInstance_ID=? AND T_Selection.T_Selection_ID=WM_InOutLine.WM_InOutLine_ID)";

		List<MWM_InOutLine> lines = new Query(Env.getCtx(),MWM_InOutLine.Table_Name,whereClause,get_TrxName())
		.setParameters(getAD_PInstance_ID()).list();

		for (MWM_InOutLine line:lines){
		}

	return "OLD UNITS RELEASED: "+old+" NEW UNITS ASSIGNED: "+units;

	}
}