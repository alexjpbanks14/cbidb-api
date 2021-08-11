package com.coleji.framework.API

import com.coleji.framework.Storable.Fields.DatabaseField
import com.coleji.framework.Storable.{DTOClass, Filter, StorableClass, StorableObject}
import org.sailcbi.APIServer.Entities.EntityDefinitions.User
import org.sailcbi.APIServer.UserTypes.StaffRequestCache

abstract class RestController[S <: StorableClass](obj: StorableObject[S]) {
	protected def getOne(rc: StaffRequestCache, id: Int, fieldShutter: Set[DatabaseField[_]]): Option[S] = {
		val u = User.getAuthedUser(rc)
		if (u.values.userName.get != "JCOLE") {
			throw new Exception("Locked to jcole only")
		}

		rc.getObjectById(obj, id, fieldShutter)
	}

	protected def getByFilters(rc: StaffRequestCache, filters: List[String => Filter], fieldShutter: Set[DatabaseField[_]]): List[S] = {
		val u = User.getAuthedUser(rc)
		if (u.values.userName.get != "JCOLE") {
			throw new Exception("Locked to jcole only")
		}

		rc.getObjectsByFilters(obj, filters, fieldShutter)
	}
}
