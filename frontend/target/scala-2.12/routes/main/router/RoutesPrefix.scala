// @GENERATOR:play-routes-compiler
// @SOURCE:/home/jason/Desktop/Stormwind-new/backend/conf/routes
// @DATE:Mon Sep 17 19:53:45 AEST 2018


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
