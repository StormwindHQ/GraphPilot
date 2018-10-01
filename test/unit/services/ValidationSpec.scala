import org.scalatestplus.play._
import play.api.libs.json._
import scala.xml.dtd.ValidationException
import services.Validation
import consts.{
  FieldRequiredException,
  FieldStringException,
  FieldStringArrayException
}

class ValidationSpec extends PlaySpec {
  val schema1 = """
    {
      "reference": "https://developer.github.com/v3/issues/#create-an-issue",
      "env": [
      {
        "name": "REPO",
        "type": "string",
        "required": true
      }, {
      "name": "USER",
      "type": "string",
      "required": true
    }, {
      "name": "ACCESS_TOKEN",
      "type": "string",
      "required": true
    }, {
      "name": "WEBHOOK_URL",
      "type": "webhook_url"
    }
      ],
      "inputs": [
      {
        "name": "title",
        "type": "string",
        "required": true
      },
      {
        "name": "body",
        "type": "string",
        "required": true
      },
      {
        "name": "assignees",
        "type": "string[]",
        "required": true
      },
      {
        "name": "state",
        "type": "enum",
        "choices": ["open", "closed"],
        "required": true
      },
      {
        "name": "labels",
        "type": "string[]",
        "required": false
      }
      ],
      "outputs": [
      {
        "name": "id",
        "type": "number"
      }
      ]
    }
  """
  val payload1: JsValue = Json.parse("""
    {
      "title": "hey",
      "body": "hello",
      "assignees": [1, 2],
      "state": "open",
      "labels": ["bug"]
    }
  """)
  "validateTaskPayload" should {
    "invalidate required" in {
      val valid = new Validation {
        override def readTaskAsString(path: String): String = schema1
      }

      // 1. Undefined test
      val newPayload = payload1.as[JsObject] - "title"
      a[FieldRequiredException] must be thrownBy {
        valid.validateTaskPayload("github", "actions", "hmm", newPayload)
      }

      // 2. null test
      // TODO: Finish implementing
      /* val newPayload2 = payload1.as[JsObject] ++ Json.obj("title" -> null)
      a[FieldRequiredException] must be thrownBy {
        valid.validateTaskPayload("github", "actions", "hmm", newPayload2)
      } */
    }

    "invalidate string" in {
      val valid = new Validation {
        override def readTaskAsString(path: String): String = schema1
      }
      val newPayload = payload1.as[JsObject] ++ Json.obj("title" -> JsNumber(1))

      a[FieldStringException] must be thrownBy {
        valid.validateTaskPayload(
          appName="github",
          taskType="actions",
          taskName="hmm",
          newPayload
        )
      }
    }

    "invalidate string array" in {
      val valid = new Validation {
        override def readTaskAsString(path: String): String = schema1
      }
      // Fixture with title number but it's meant to be string
      val newPayload = payload1.as[JsObject] ++ Json.obj("assignees" -> Json.parse("[1, 2]"))

      a[FieldStringArrayException] must be thrownBy {
        valid.validateTaskPayload(
          appName="github",
          taskType="actions",
          taskName="hmm",
          newPayload
        )
      }
    }

  }
}