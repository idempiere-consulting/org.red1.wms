/**



	public class GenerateHandlingUnit extends SvrProcess {




		ProcessInfoParameter[] para = getParameter();
			for (ProcessInfoParameter p:para) {
				String name = p.getParameterName();
				if (p.getParameter() == null)
				else if(name.equals("Counter")){
					Counter = p.getParameterAsInt();
			}
				else if(name.equals("Prefix")){
					Prefix = (String)p.getParameter();
			}
				else if(name.equals("Capacity")){
					Capacity = p.getParameterAsInt();
			}
		}
	}

		return "";
	}
}