package com.cmread.cmu.css.db.utils;

import com.cmread.cmu.css.db.Row;

public interface DiffSyncFilter {
	void sameRowFilter(Row from, Row to);
}