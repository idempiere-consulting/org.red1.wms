/**

import java.sql.ResultSet;
import java.util.Properties;

public class MWM_TypeTable extends X_WM_TypeTable{

	private static final long serialVersionUID = -1L;

	public MWM_TypeTable(Properties ctx, int id, String trxName) {
		super(ctx,id,trxName);
		}

	public MWM_TypeTable(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
}