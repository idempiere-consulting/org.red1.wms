/**

import java.sql.ResultSet;
import java.util.Properties;

public class MWM_EmptyStorage extends X_WM_EmptyStorage{

	private static final long serialVersionUID = -1L;

	public MWM_EmptyStorage(Properties ctx, int id, String trxName) {
		super(ctx,id,trxName);
		}

	public MWM_EmptyStorage(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
}