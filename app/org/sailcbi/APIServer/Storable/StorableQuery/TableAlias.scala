package org.sailcbi.APIServer.Storable.StorableQuery

import org.sailcbi.APIServer.Storable.{StorableClass, StorableObject}

case class TableAlias(name: String, obj: StorableObject[_ <: StorableClass]) {
	// TODO: throw if name has invalid characters.  for safety i think only [a-zA-Z]
}