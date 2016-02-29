package com.cmread.cmu.css.db.utils;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.db.Row;
import com.cmread.cmu.css.db.asyncsql.SqlExecutor;
import com.cmread.cmu.css.db.utils.RowSetDiff.SqlDataSetDiffItem;
import com.cmread.cmu.css.db.utils.SqlBuilder.UpdateSqlBuilder;
import com.cmread.cmu.css.db.utils.TableMetaData.Column;

public class RowUpdateAction extends AbstractRowAction {

	private static Logger logger = LoggerFactory.getLogger(RowUpdateAction.class);
	
	private String[] primaryKeyNames;
	private SqlDataSetDiffItem diffItem;
	
	public RowUpdateAction(SqlDataSetDiffItem diffItem, String tableName, SqlExecutor sqlExecutor, String[] primaryKeyNames) {
		super(diffItem.getFromRow(), tableName, sqlExecutor);
		this.primaryKeyNames = primaryKeyNames;
		this.diffItem = diffItem;
	}
	
	public RowUpdateAction(Row toRow, String tableName, SqlExecutor sqlExecutor, String[] primaryKeyNames) {
		super(toRow, tableName, sqlExecutor);
		this.primaryKeyNames = primaryKeyNames;
	}

	@Override
	public SqlBuilder toSql(Row row, TableMetaData tableMetaData) {
		return this.toUpdateSql(row, tableMetaData);
	}

	private SqlBuilder toUpdateSql(Row row, TableMetaData tableMetaData) {
		UpdateSqlBuilder update = SqlBuilder.update();
		update.table(tableMetaData.getTableName());
		
		if (diffItem != null) {
			for (String diffField : diffItem.getRowDiff().getDiffFields()) {
				if (isNotOwnedPlaceField(diffField)) {
					addUpdateField(diffField, row.getFieldValue(diffField), tableMetaData, update);
				}
			}			
		} else {
			for (Map.Entry<String, Object> field : row.toMap().entrySet()) {
				if (isNotOwnedPlaceField(field.getKey())) {
					addUpdateField(field.getKey(), field.getValue(), tableMetaData, update);
				}
			}
		}
		
		// FIXME 这里放一个不友好的步骤，当to中包含字段OWNEDPLACE时，自动将此字段设置为1
		// 这个以后可以通过扩展或者filter的方式来实现，或者在共存期后，此字段已经无意义，可以直接删除下面对OWNEDPLACE的特殊处理部分；
		if (tableMetaData.getColumn("OWNEDPLACE") != null) {
			update.addSet("OWNEDPLACE", "1");
		}
		
		// 在这里增加where条件,也需要考虑数据转换（数据转换最后放到一个地方去吧 TODO )
		String[] keys = primaryKeyNames;
		Object[] keyValues = row.getFieldValues(keys);
		for (int i=0; i<keys.length; ++i) {
			update.addCondition(keys[i], keyValues[i]);
		}
		
		return update; 
	}

	private void addUpdateField(String fieldName, Object fieldValue, TableMetaData tableMetaData, UpdateSqlBuilder update) {
		Column col = tableMetaData.getColumn(fieldName);
		if (col != null) {
			// 检查value的datatype类型与to表字段的是否一致
			// 如果一致，直接插入
			// 如果不一致，则需要做转换
			// 返回sql
			update.addSet(col.getName(), fieldValue);
		} else {
			// to表中没有此字段，跳过，记录日志
			logger.debug("ignore from column [{}]", fieldName);
		}
	}
		
}
