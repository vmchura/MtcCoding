package modules

import com.google.inject.AbstractModule
import models.daos.{ CreateDDL, InsertFirstRutes }

class DDLGeneratorModule extends AbstractModule {

  override protected def configure(): Unit = {
    bind(classOf[CreateDDL]).asEagerSingleton()
    bind(classOf[InsertFirstRutes]).asEagerSingleton()

  }
}
