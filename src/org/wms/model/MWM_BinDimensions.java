/**

import java.sql.ResultSet;
import java.util.Properties;

public class MWM_BinDimensions extends X_WM_BinDimensions{

	private static final long serialVersionUID = -1L;

	public MWM_BinDimensions(Properties ctx, int id, String trxName) {
		super(ctx,id,trxName);
		}

	public MWM_BinDimensions(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
}