package models.daos
import javax.inject.{ Inject, Singleton }
import models.{ DataTravel, LimiteVelocidad, Pasajero, Ruta, RutaCoordinates, Travel }
import models.mappers.{ CelaRefinedProfile, RouteMobileMapping }

import scala.concurrent.{ ExecutionContext, Future }
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import shared.{ LineStringSH, RutaSH, RutaSHMeta }
import PeruRoutes.PeruRoutes.rutasOnMemory

import scala.reflect.ClassTag
@Singleton()
class MobileDAOImpl @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider,
  protected val customColumnTypes: CustomColumnTypes)(implicit executionContext: ExecutionContext) extends MobileDAO with RouteMobileMapping with HasDatabaseConfigProvider[CelaRefinedProfile] {

  import profile.api._
  import customColumnTypes.jodaDateTimeType

  val rutaWithID = (rutaTQ returning rutaTQ.map(_.idRuta) into ((ruta, newID) => ruta.copy(idRuta = newID)))
  override def addRuta(ruta: Ruta): Future[Int] = db.run(rutaWithID += ruta).map(_.idRuta)

  override def addCoordinates(rutaCoordinates: Seq[RutaCoordinates]): Future[Int] = db.run(rutaCoordiantesTQ ++= rutaCoordinates).map(x => x.getOrElse(0))

  override def getRuta(idRuta: Int): Future[Option[Ruta]] = db.run(rutaTQ.filter(_.idRuta === idRuta).result.headOption)

  override def getCoordinates(idRuta: Int): Future[Seq[RutaCoordinates]] = db.run(rutaCoordiantesTQ.filter(_.idRuta === idRuta).sortBy(_.prog).result)

  val pasajeroWithID = (pasajeroTQ returning pasajeroTQ.map(_.idPasajero) into ((pasajero, newID) => pasajero.copy(idPasajero = newID)))

  override def addPasajero(pasajero: Pasajero): Future[Int] = db.run(pasajeroWithID += pasajero).map(_.idPasajero)

  override def getPasajero(idPasajero: Int): Future[Option[Pasajero]] = db.run(pasajeroTQ.filter(_.idPasajero === idPasajero).result.headOption)

  val travelWithID = (travelTQ returning travelTQ.map(_.idTravel) into ((travel, newID) => travel.copy(idTravel = newID)))

  override def addTravel(travel: Travel): Future[Int] = db.run(travelWithID += travel).map(_.idTravel)

  override def getTravel(idTravel: Int): Future[Option[Travel]] = db.run(travelTQ.filter(_.idTravel === idTravel).result.headOption)

  override def addDataTravel(dataTravel: DataTravel): Future[Int] = {
    if (dataTravel.velocidad >= 0) {
      db.run(dataTravelTQ += dataTravel)
    } else {
      db.run(travelTQ.filter(_.idTravel === dataTravel.idTravel).map(_.isLive).update(false)).map(_ => -1)
    }
  }

  override def getDataFromTravel(idTravel: Int): Future[Seq[DataTravel]] = db.run(dataTravelTQ.filter(_.idTravel === idTravel).result)

  override def getDataFromRuta(idRuta: Int): Future[Seq[DataTravel]] = {
    for {
      travelsID <- db.run(travelTQ.filter(_.idRuta === idRuta).map(_.idTravel).result)
      data <- db.run(dataTravelTQ.filter(_.idTravel.inSet(travelsID)).result)
    } yield {
      data
    }

  }

  val limitWithID = (limiteVelocidadTQ returning limiteVelocidadTQ.map(_.idLimite) into ((limite, newID) => limite.copy(idLimite = newID)))

  override def addLimiteVelocidad(limiteVelocidad: LimiteVelocidad): Future[Int] = db.run(limitWithID += limiteVelocidad).map(_.idLimite)

  override def getLimitesFromRuta(idRuta: Int): Future[Seq[LimiteVelocidad]] = db.run(limiteVelocidadTQ.filter(_.idRuta === idRuta).result)

  override def getAllRutas(): Future[Seq[Ruta]] = db.run(rutaTQ.result)

  override def getRutaToDraw(rutaID: Int): Future[Option[LineStringSH]] = {
    if (rutasOnMemory.contains(rutaID)) {
      val ruta = rutasOnMemory(rutaID)
      val ls = LineStringSH((255, 0, 0), 5d, ruta.coordinates.map { case (a, b, _) => (a, b) }.toIndexedSeq)
      Future.successful(Some(ls))
    } else {
      Future.successful(None)
    }
  }

  override def getAllRutasSHMeta(): Future[Seq[RutaSHMeta]] = {
    val res = rutasOnMemory.map { case (key, value) => RutaSHMeta(key, value.tagName) }.toList
    Future.successful(res)
  }

  private def findLastData(travel: Travel): Future[Option[DataTravel]] = {
    db.run(dataTravelTQ.filter(_.idTravel === travel.idTravel).sortBy(_.dataTime.desc).take(1).result.headOption)
  }
  private def sequenceOfFuture[T, F: ClassTag](data: Seq[T], futMap: T => Future[F]): Future[List[F]] = {
    data.foldLeft(Future(List.empty[F]))((prevFut, t) => {
      for {
        prev <- prevFut
        curr <- futMap(t)
      } yield {
        prev :+ curr
      }
    })
  }

  override def findDangerousVehiclesLive(idRuta: Int): Future[Seq[(Travel, DataTravel)]] = {
    for {
      travel <- db.run(travelTQ.filter(t => t.idRuta === idRuta && t.infringio === true && t.isLive === true).result)
      datatravel <- sequenceOfFuture(travel, findLastData)
    } yield {
      travel.zip(datatravel).flatMap { case (a, b) => b.map(z => (a, z)) }
    }
  }

  override def findDangerousVehicles(idRuta: Int): Future[Seq[Travel]] = {
    db.run(travelTQ.filter(t => t.idRuta === idRuta && t.infringio === true).result)
  }

  override def findInformalVehicles(idRuta: Int): Future[Seq[Travel]] = {
    db.run(travelTQ.filter(t => t.idRuta === idRuta && t.esInformal === true).result)

  }

  override def findInformalVehiclesLive(idRuta: Int): Future[Seq[(Travel, DataTravel)]] = {
    for {
      travels <- db.run(travelTQ.filter(t => t.idRuta === idRuta && t.esInformal === true && t.isLive === true).result)
      datatravel <- sequenceOfFuture(travels, findLastData)
    } yield {
      travels.zip(datatravel).flatMap { case (a, b) => b.map(z => (a, z)) }
    }
  }

  private def ratioRutaInformal(idRuta: Int): Future[Int] = {
    for {
      informales <- db.run(travelTQ.filter(t => t.esInformal === true && t.idRuta === idRuta).length.result)
      total <- db.run(travelTQ.filter(_.idRuta === idRuta).length.result)
    } yield {
      if (total == 0) {
        0
      } else {
        Math.round(informales * 100d / total).toInt
      }
    }
  }
  private def findCoreInformales(idRuta: Int): Future[Seq[LineStringSH]] = {
    for {
      initialPositions <- db.run(travelTQ.filter(t => t.esInformal === true && t.idRuta === idRuta).map(_.initialPosition).result)
    } yield {

      val z = initialPositions.groupBy(k => k / 1000).toList.map { case (k, v) => (v.length, k) }.sorted.reverse.map(_._2)

      val u = if (z.length > 2) z.take(2) else z

      if (rutasOnMemory.contains(idRuta)) {
        val ruta = rutasOnMemory(idRuta)
        u.map { k =>
          val coordinatesKilometer = ruta.coordinates.filter { case (_, _, p) => k * 1000 <= p && (p <= ((k + 1) * 1000)) }.sortBy(_._3.toInt).map { case (x, y, _) => (x, y) }
          LineStringSH((255, 0, 0), 6d, coordinatesKilometer.toIndexedSeq)
        }
      } else {
        Nil
      }

    }
  }

  private def createMiniRutasInformales(rutaIDSH: (Int, RutaSH)): Future[(String, Int, Seq[LineStringSH], LineStringSH)] = {
    val (rutaID, rutaSH) = rutaIDSH
    for {
      ratio <- ratioRutaInformal(rutaID)
      listaInformales <- findCoreInformales(rutaID)
    } yield {
      (rutaSH.tagName, ratio, listaInformales, LineStringSH((0, 0, 255), 3d, rutaSH.coordinates.map { case (a, b, _) => (a, b) }.toIndexedSeq))
    }

  }

  override def findRutasInformales(): Future[Seq[(String, Int, Seq[LineStringSH], LineStringSH)]] = {

    sequenceOfFuture(rutasOnMemory.toSeq, createMiniRutasInformales)
  }

  override def findSegmentosCriticos(rutaID: Int): Future[Option[(String, Int, Seq[LineStringSH], LineStringSH)]] = {
    rutasOnMemory.get(rutaID).map { ruta =>
      createMiniRutasInformales((rutaID, ruta)).map(z => Some(z))
    }.getOrElse(Future.successful(None))
  }

  override def getAllRoutsSH(): Future[Map[Int, RutaSH]] = {
    def getRutaSH(ruta: Ruta): Future[(Int, RutaSH)] = {
      for {
        coordinates <- db.run(rutaCoordiantesTQ.filter(_.idRuta === ruta.idRuta).sortBy(_.prog).result)
      } yield {
        ruta.idRuta -> RutaSH(ruta.tagRuta, coordinates.map(rc => (rc.lng, rc.lat, rc.prog.toDouble)).toArray, ruta.progFin)
      }
    }

    for {
      rutas <- db.run(rutaTQ.result)
      rutasSH <- {
        rutas.foldLeft(Future(List.empty[(Int, RutaSH)]))((prevFut, r) => {
          for {
            prev <- prevFut
            curr <- getRutaSH(r)
          } yield {
            prev :+ curr
          }
        })
      }
    } yield {
      rutasSH.toMap
    }
  }

  override def getDataChartVelocity(rutaID: Int): Future[Seq[Int]] = {
    if (rutasOnMemory.contains(rutaID)) {
      val ruta = rutasOnMemory(rutaID)
      for {
        allData <- getDataFromRuta(rutaID)
      } yield {
        val hmProm = allData.groupBy(dt => dt.prog / 100).toList.map {
          case (hm, seqDT) => {
            val sum = seqDT.map(_.velocidad).sum
            val length = seqDT.length
            val prom = if (length > 0)
              Math.round(sum * 1d / length).toInt
            else
              0
            (hm * 100, prom)
          }
        }.sorted

        val hmComplete = hmProm.foldLeft((0, List.empty[Int])) {
          case ((mustBe, prevList), (current, velProm)) =>

            if (current == mustBe) {
              (current + 100, velProm :: prevList)
            } else {
              assert(current > mustBe)
              val zeros = (mustBe until current by 100).map(_ => 0).toList
              (current + 100, velProm :: (zeros ++ prevList))
            }
        }._2

        hmComplete.reverse

      }
    } else {
      Future.successful(Nil)
    }
  }
}
