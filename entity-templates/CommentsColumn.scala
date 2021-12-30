package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class CommentsColumn extends StorableClass(CommentsColumn) {
	object values extends ValuesObject {
		val columnName = new NullableStringFieldValue(self, CommentsColumn.fields.columnName)
		val commentText = new NullableUnknownFieldType(self, CommentsColumn.fields.commentText)
		val columnId = new IntFieldValue(self, CommentsColumn.fields.columnId)
		val tableId = new IntFieldValue(self, CommentsColumn.fields.tableId)
	}
}

object CommentsColumn extends StorableObject[CommentsColumn] {
	val entityName: String = "COMMENTS_COLUMNS"

	object fields extends FieldsObject {
		val columnName = new NullableStringDatabaseField(self, "COLUMN_NAME", 50)
		val commentText = new NullableUnknownFieldType(self, "COMMENT_TEXT")
		val columnId = new IntDatabaseField(self, "COLUMN_ID")
		val tableId = new IntDatabaseField(self, "TABLE_ID")
	}

	def primaryKey: IntDatabaseField = fields.columnId
}