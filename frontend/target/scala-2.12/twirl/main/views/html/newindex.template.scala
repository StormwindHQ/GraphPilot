
package views.html

import _root_.play.twirl.api.TwirlFeatureImports._
import _root_.play.twirl.api.TwirlHelperImports._
import _root_.play.twirl.api.Html
import _root_.play.twirl.api.JavaScript
import _root_.play.twirl.api.Txt
import _root_.play.twirl.api.Xml
import models._
import controllers._
import play.api.i18n._
import views.html._
import play.api.templates.PlayMagic._
import play.api.mvc._
import play.api.data._

object newindex extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template5[Form[Cat],Seq[Cat],Seq[Dog],AssetsFinder,MessagesRequestHeader,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*1.2*/(catForm: Form[Cat], cats: Seq[Cat], dogs: Seq[Dog])(implicit assetsFinder: AssetsFinder, requestHeader: MessagesRequestHeader):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {
/*3.2*/import views.html.helper._


Seq[Any](format.raw/*2.1*/("""
"""),format.raw/*4.1*/("""
"""),_display_(/*5.2*/main("Cat & Dog database", assetsFinder)/*5.42*/ {_display_(Seq[Any](format.raw/*5.44*/("""
    """),format.raw/*6.5*/("""<div>
        <div id="cats">
            <h2>Insert a kitty cat here:</h2>
            """),_display_(/*9.14*/form(routes.HomeController.insertCat())/*9.53*/ {_display_(Seq[Any](format.raw/*9.55*/("""
                """),_display_(/*10.18*/CSRF/*10.22*/.formField),format.raw/*10.32*/("""
                """),_display_(/*11.18*/inputText(catForm("name"), '_label -> "Cat's name", '_help -> "name your feline friend")),format.raw/*11.106*/("""
                """),_display_(/*12.18*/inputText(catForm("color"), '_label -> "Cat's color", '_help -> "enter the color of this kitty cat")),format.raw/*12.118*/("""
                """),format.raw/*13.17*/("""<input type="submit" value="Create a cat" class="btn primary">
            """)))}),format.raw/*14.14*/("""

            """),format.raw/*16.13*/("""<h2>Previously inserted cats:</h2>
            <table>
                <tr><th>Name</th><th>Color</th></tr>
                """),_display_(/*19.18*/for(c <- cats) yield /*19.32*/{_display_(Seq[Any](format.raw/*19.33*/("""
                    """),format.raw/*20.21*/("""<tr>
                        <td>"""),_display_(/*21.30*/c/*21.31*/.id),format.raw/*21.34*/("""</td>
                        <td>"""),_display_(/*22.30*/c/*22.31*/.name),format.raw/*22.36*/("""</td>
                        <td>"""),_display_(/*23.30*/c/*23.31*/.color),format.raw/*23.37*/("""</td>
                        <td>
                        """),_display_(/*25.26*/form(routes.HomeController.deleteCat(c.id))/*25.69*/ {_display_(Seq[Any](format.raw/*25.71*/("""
                            """),_display_(/*26.30*/CSRF/*26.34*/.formField),format.raw/*26.44*/("""
                            """),format.raw/*27.29*/("""<input type="submit" value="Delete this cat" class="btn danger">
                        """)))}),format.raw/*28.26*/("""
                        """),format.raw/*29.25*/("""</td>
                    </tr>
                """)))}),format.raw/*31.18*/("""
            """),format.raw/*32.13*/("""</table>
        </div>

        <div id="dogs">
            <h2>Insert a kitty dog here:</h2>

            <form action="/insert/dog" method="POST">
                <input name="name" type="text" placeholder="name your canine friend"/>
                <input name="color" type="text" placeholder="enter the color of this kitty dog"/>
                <input type="submit"/>
            </form>

            <h2>Previously inserted dogs:</h2>
            <table>
                <tr><th>Name</th><th>Color</th></tr>
                """),_display_(/*47.18*/for(d <- dogs) yield /*47.32*/{_display_(Seq[Any](format.raw/*47.33*/("""
                    """),format.raw/*48.21*/("""<tr><td>"""),_display_(/*48.30*/d/*48.31*/.name),format.raw/*48.36*/("""</td><td>"""),_display_(/*48.46*/d/*48.47*/.color),format.raw/*48.53*/("""</td></tr>
                """)))}),format.raw/*49.18*/("""
            """),format.raw/*50.13*/("""</table>
        </div>
    </div>
""")))}))
      }
    }
  }

  def render(catForm:Form[Cat],cats:Seq[Cat],dogs:Seq[Dog],assetsFinder:AssetsFinder,requestHeader:MessagesRequestHeader): play.twirl.api.HtmlFormat.Appendable = apply(catForm,cats,dogs)(assetsFinder,requestHeader)

  def f:((Form[Cat],Seq[Cat],Seq[Dog]) => (AssetsFinder,MessagesRequestHeader) => play.twirl.api.HtmlFormat.Appendable) = (catForm,cats,dogs) => (assetsFinder,requestHeader) => apply(catForm,cats,dogs)(assetsFinder,requestHeader)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  DATE: Wed Sep 12 00:40:20 AEST 2018
                  SOURCE: /home/jason/Desktop/strormwind.io/app/views/newindex.scala.html
                  HASH: 30026138ffba8c096225e4333d0f2502c5854cbf
                  MATRIX: 788->1|988->131|1043->129|1070->158|1097->160|1145->200|1184->202|1215->207|1330->296|1377->335|1416->337|1461->355|1474->359|1505->369|1550->387|1660->475|1705->493|1827->593|1872->610|1979->686|2021->700|2173->825|2203->839|2242->840|2291->861|2352->895|2362->896|2386->899|2448->934|2458->935|2484->940|2546->975|2556->976|2583->982|2670->1042|2722->1085|2762->1087|2819->1117|2832->1121|2863->1131|2920->1160|3041->1250|3094->1275|3174->1324|3215->1337|3774->1869|3804->1883|3843->1884|3892->1905|3928->1914|3938->1915|3964->1920|4001->1930|4011->1931|4038->1937|4097->1965|4138->1978
                  LINES: 21->1|24->3|27->2|28->4|29->5|29->5|29->5|30->6|33->9|33->9|33->9|34->10|34->10|34->10|35->11|35->11|36->12|36->12|37->13|38->14|40->16|43->19|43->19|43->19|44->20|45->21|45->21|45->21|46->22|46->22|46->22|47->23|47->23|47->23|49->25|49->25|49->25|50->26|50->26|50->26|51->27|52->28|53->29|55->31|56->32|71->47|71->47|71->47|72->48|72->48|72->48|72->48|72->48|72->48|72->48|73->49|74->50
                  -- GENERATED --
              */
          