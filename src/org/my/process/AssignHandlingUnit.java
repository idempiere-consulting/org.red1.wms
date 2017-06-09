/**


import org.compiere.model.Query;
import org.compiere.util.Env;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import org.compiere.util.DB;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MSequence;
import org.my.model.MWM_InOutLine;
import org.compiere.process.SvrProcess;

	public class AssignHandlingUnit extends SvrProcess {


		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)
				else if(name.equals("WM_HandlingUnit_ID")){
					WM_HandlingUnit_ID = p.getParameterAsInt();
			}
		}
	}

		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE T_Selection.AD_PInstance_ID=? AND T_Selection.T_Selection_ID=WM_InOutLine.WM_InOutLine_ID)";

		List<MWM_InOutLine> lines = new Query(Env.getCtx(),MWM_InOutLine.Table_Name,whereClause,get_TrxName())
		.setParameters(getAD_PInstance_ID()).list();

		for (MWM_InOutLine line:lines){
			MWM_HandlingUnit hu = new Query(Env.getCtx(),MWM_HandlingUnit.Table_Name,MWM_HandlingUnit.COLUMNNAME_WM_HandlingUnit_ID+">=?",get_TrxName())
		}

	return "OLD UNITS RELEASED: "+old+" NEW UNITS ASSIGNED: "+units;

	}
}