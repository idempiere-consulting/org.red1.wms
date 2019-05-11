/**


import org.compiere.model.Query;
import org.compiere.util.Env;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import org.compiere.util.DB;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MSequence;
import org.wms.model.MWM_DeliveryScheduleLine;
import org.compiere.process.SvrProcess;

	public class DivideByPalletSize extends SvrProcess {

	private int productPalletSize = 0;
		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)
				else if(name.equals("UnitsPerPallet")){
					UnitsPerPallet = p.getParameterAsInt();
			}
		}
	}

		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE T_Selection.AD_PInstance_ID=? AND T_Selection.T_Selection_ID=WM_DeliveryScheduleLine.WM_DeliveryScheduleLine_ID)";

		List<MWM_DeliveryScheduleLine> lines = new Query(Env.getCtx(),MWM_DeliveryScheduleLine.Table_Name,whereClause,get_TrxName())
		.setParameters(getAD_PInstance_ID()).list();
		if (UnitsPerPallet>0)
		for (MWM_DeliveryScheduleLine line:lines){
	}
	return "New Lines Created: "+count;

	}
}