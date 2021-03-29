package com.coleji.framework.Core

class OracleBroker private[Core](dbGateway: DatabaseGateway, preparedQueriesOnly: Boolean, readOnly: Boolean)
	extends RelationalBroker(dbGateway, preparedQueriesOnly, readOnly)
