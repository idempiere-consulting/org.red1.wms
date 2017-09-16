/**

import org.compiere.model.Query;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

	public class ChangeLocator extends SvrProcess {


		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)
				else if(name.equals("M_Locator_ID")){
					M_Locator_ID = p.getParameterAsInt();
			}
		}
	}

		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE T_Selection.AD_PInstance_ID=? AND T_Selection.T_Selection_ID=WM_InOutLine.WM_InOutLine_ID)";

		List<MWM_InOutLine> lines = new Query(Env.getCtx(),MWM_InOutLine.Table_Name,whereClause,get_TrxName())
		.setParameters(getAD_PInstance_ID()).list();
		
		for (MWM_InOutLine line:lines){ 
			line.setM_Locator_ID(M_Locator_ID);
	}

	return "RESULT: "+lines.toString();

	}
}