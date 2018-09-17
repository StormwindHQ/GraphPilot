// @GENERATOR:play-routes-compiler
// @SOURCE:/home/jason/Desktop/Stormwind-new/backend/conf/routes
// @DATE:Mon Sep 17 19:53:45 AEST 2018

import play.api.mvc.Call


import _root_.controllers.Assets.Asset

// @LINE:6
package controllers {

  // @LINE:17
  class ReverseAsyncController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:17
    def message(): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "message")
    }
  
  }

  // @LINE:6
  class ReverseHomeController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:12
    def deleteCat(id:Option[Long]): Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "delete/cat/" + play.core.routing.queryString(List(Some(implicitly[play.api.mvc.QueryStringBindable[Option[Long]]].unbind("id", id)))))
    }
  
    // @LINE:10
    def insertDog(): Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "insert/dog")
    }
  
    // @LINE:6
    def index(): Call = {
      
      Call("GET", _prefix)
    }
  
    // @LINE:9
    def insertCat(): Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "insert/cat")
    }
  
  }

  // @LINE:20
  class ReverseAssets(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:20
    def versioned(file:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "assets/" + implicitly[play.api.mvc.PathBindable[String]].unbind("file", file))
    }
  
  }

  // @LINE:15
  class ReverseCountController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:15
    def count(): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "count")
    }
  
  }


}
