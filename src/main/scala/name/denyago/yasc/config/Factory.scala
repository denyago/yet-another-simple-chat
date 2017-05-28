package name.denyago.yasc.config

import com.typesafe.config.{Config, ConfigFactory}

/**
  * Returns config objects for different parts of the server
  */
object Factory {
  def httpServiceConf: HttpServiceConf = {
    val hscPath = conf.getConfig("yasc.server.http")

    new HttpServiceConf(
      host = hscPath.getString("host"),
      port = hscPath.getInt("port")
    )
  }

  private val conf: Config = ConfigFactory.load()
}
