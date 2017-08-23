package Services

import javax.inject.Inject

import play.api.inject.ApplicationLifecycle

class OracleBroker @Inject() (lifecycle: ApplicationLifecycle, cp: ConnectionPoolConstructor)
  extends RelationalBroker(lifecycle: ApplicationLifecycle, cp: ConnectionPoolConstructor) {

}