/**


/**
	public class MigrateProcess extends SvrProcess {




		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)
				else if(name.equals("M_PriceList_Version_ID")){
					M_PriceList_Version_ID = p.getParameterAsInt();
			}
				else if(name.equals("M_Warehouse_ID")){
					M_Warehouse_ID = p.getParameterAsInt();
			}
				else if(name.equals("M_Locator_ID")){
					M_Locator_ID = p.getParameterAsInt();
			}
				else if(name.equals("M_Product_ID")){
					M_Product_ID = p.getParameterAsInt();
			}
		}
	}

	}
}