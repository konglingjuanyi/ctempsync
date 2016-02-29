package com.cmread.cmu.css.db.utils;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.db.utils.SqlBuilder.InsertSqlBuilder;
import com.cmread.cmu.css.db.utils.TableMetaData.Column;

public class RowInsertAction extends AbstractRowAction {
	
	private static Logger logger = LoggerFactory.getLogger(RowInsertAction.class);

	public RowInsertAction(Row row, String tableName, SqlExecutor sqlExecutor) {
		super(row, tableName, sqlExecutor);
	}
	
	@Override
	public SqlBuilder toSql(Row item, TableMetaData tableMetaData) {
		return this.toInsertSql(item, tableMetaData);
	}
	
	private SqlBuilder toInsertSql(Row item, TableMetaData tableMetaData) {
		InsertSqlBuilder insert = SqlBuilder.insert();
		insert.table(tableMetaData.getTableName());
		
		for (Map.Entry<String, Object> field : item.toMap().entrySet()) {
			if (isNotOwnedPlaceField(field.getKey())) {
				setUpdateColumn(field, insert, tableMetaData);
			}
		}
		
		Map<String, Object> extraFields = item.getExtraFieldsAtInsert();
		if (extraFields != null) {
			for (Map.Entry<String, Object> extraField : extraFields.entrySet()) {
				if (isNotOwnedPlaceField(extraField.getKey())) {
					setUpdateColumn(extraField, insert, tableMetaData);
				}
			}
		}
		
		// FIXME 这里放一个不友好的步骤，当to中包含字段OWNEDPLACE时，自动将此字段设置为1
		// 这个以后可以通过扩展或者filter的方式来实现；
		if (tableMetaData.getColumn("OWNEDPLACE") != null) {
			insert.addColumn("OWNEDPLACE", "1");
		}
		
		return insert; 
	}

	private void setUpdateColumn(Map.Entry<String, Object> field, InsertSqlBuilder insert,
			TableMetaData tableMetaData) {
//		if (field.getValue() == null ) {
//			return;
//		}
		
		Column col = tableMetaData.getColumn(field.getKey());
		if (col != null) {
			Object value = field.getValue();
			// 检查value的datatype类型与to表字段的是否一致
			// 如果一致，直接插入
			// 如果不一致，则需要做转换
			// 返回sql
			insert.addColumn(col.getName(), value);
		} else {
			// to表中没有此字段，跳过，记录日志
			logger.debug("-- ignore from column [{}]", field.getKey());
		}
	}
	
}
