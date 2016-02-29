package com.cmread.cmu.css.db.utils;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public interface ActionFilter {

	RowActioin filter(RowActioin action) throws InterruptedException, ExecutionException, SQLException;

	//void doAction(RowActioin action);
}
