package name.denyago.yasc.log

import org.slf4j.LoggerFactory

/**
  * Provides a logger for any class
  */
trait Helper {
  lazy val log = LoggerFactory.getLogger(this.getClass.getName)
}