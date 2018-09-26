package services
import javax.inject._

@Singleton
class WskService {
  def getNamespaces(wskType: String = "web"): Unit = {
    print(wskType)
  }
}
