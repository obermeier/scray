package scray.loader.service

import com.twitter.finagle.Thrift
import com.twitter.util.{ Await, Duration, JavaTimer, TimerTask }
import com.typesafe.scalalogging.slf4j.LazyLogging
import java.util.concurrent.TimeUnit
import scala.collection.convert.decorateAsScala.asScalaSetConverter
import scray.core.service.{ EXPIRATION, SCRAY_QUERY_HOST_ENDPOINT, SCRAY_SEEDS, inetAddr2EndpointString }
import scray.service.qmodel.thrifscala.ScrayUUID
import scray.service.qservice.thrifscala.{ ScrayCombinedStatefulTService, ScrayTServiceEndpoint }


/**
 * handles refreshment of ip addresses for the scray service
 */
class RefreshServing extends LazyLogging {
  
  // register this endpoint with all seeds and schedule regular refresh
  // the refresh loop keeps the server running
  SCRAY_SEEDS.asScala.map(inetAddr2EndpointString(_)).foreach { seedAddr =>
    try {
      if (Await.result(getClient(seedAddr).ping(), Duration(20, TimeUnit.SECONDS))) {
        logger.debug(s"$addrStr adding local service endpoint ($endpoint) to $seedAddr.")
        val _ep = Await.result(getClient(seedAddr).addServiceEndpoint(endpoint), Duration(20, TimeUnit.SECONDS))
        // refreshTask = Some(refreshTimer.schedule(refreshPeriod.fromNow, refreshPeriod)(refresh(_ep.endpointId.get)))
        refreshTask = Some(refreshTimer.schedule(refreshPeriod.fromNow, refreshPeriod)(refresh(_ep.endpointId.get)))
      }
    } catch {
      case ex: Exception => {

      }
    }
  }

  /**
   *  endpoint registration refresh timer
   */
  private val refreshTimer = new JavaTimer(isDaemon = false) {
    override def logError(t: Throwable) {
      logger.error("Could not refresh.", t)
    }
  }

  /**
   * the client which talks to the seed nodes to ping
   */
  var client: Option[ScrayCombinedStatefulTService.FutureIface] = None

  /**
   *  refresh task handle
   */
  private var refreshTask: Option[TimerTask] = None

  /**
   * this endpoint, as provided in the config file 
   */
  lazy val endpoint = ScrayTServiceEndpoint(SCRAY_QUERY_HOST_ENDPOINT.getHostString, SCRAY_QUERY_HOST_ENDPOINT.getPort)

  val refreshPeriod = EXPIRATION * 2 / 3

  def addrStr(): String =
    s"${SCRAY_QUERY_HOST_ENDPOINT.getHostString}:${SCRAY_QUERY_HOST_ENDPOINT.getPort}"

  /**
   * return or create a thrift client through Finagle
   */
  private def getClient(seedAddr: String): ScrayCombinedStatefulTService.FutureIface = {
    client.getOrElse {
      logger.info("Initializing thrift-client ")
      val clientIface = Thrift.newIface[ScrayCombinedStatefulTService.FutureIface](seedAddr)
      client = Some(clientIface)
      clientIface
    }
  }

  /**
   * Refresh the registry entry
   */
  def refresh(id: ScrayUUID, time: Int = 1): Unit = {
    SCRAY_SEEDS.asScala.map(inetAddr2EndpointString(_)).foreach { seedAddr =>
      try {
        logger.trace(s"$addrStr trying to refresh service endpoint ($id).")
        if (Await.result(getClient(seedAddr).ping(), Duration(20, TimeUnit.SECONDS))) {
          logger.debug(s"$addrStr refreshing service endpoint ($id).")
          // client.refreshServiceEndpoint(id)
          Await.result(getClient(seedAddr).addServiceEndpoint(endpoint), Duration(20, TimeUnit.SECONDS))
        }
      } catch {
        case ex: Exception =>
          client = None
          getClient(seedAddr)
          if (time < 4) {
            logger.warn(s"Endpoint refresh failed, time $time: $ex", ex)
            Thread.sleep(10000)
            refresh(id, time + 1)
          } else {
            logger.warn("Endpoint refresh failed. Retry maximum exceeded. Exiting.")
          }
      }
    }
  }

  /**
   * might (!) be called on shutdown
   */
  def destroyResources: Unit = {}

  override def finalize = {
    client = None
    destroyResources
  }

}