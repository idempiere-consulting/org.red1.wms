/**

import java.sql.ResultSet;
import java.util.Properties;

public class MWM_ESLine extends X_WM_ESLine{

	private static final long serialVersionUID = -1L;

	public MWM_ESLine(Properties ctx, int id, String trxName) {
		super(ctx,id,trxName);
		}

	public MWM_ESLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
}