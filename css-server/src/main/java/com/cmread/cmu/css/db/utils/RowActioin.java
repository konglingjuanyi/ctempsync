package com.cmread.cmu.css.db.utils;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public interface RowActioin {

	Future<Integer> execute() throws SQLException, InterruptedException, ExecutionException;

}