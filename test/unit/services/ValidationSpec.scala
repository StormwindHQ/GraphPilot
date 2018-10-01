import org.scalatestplus.play._
import play.api.libs.json._
import services.Validation

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
  "validateTaskPayload" should {
    "invalidate string" in {
      val valid = new Validation {
        override def readTaskAsString(path: String): String = schema1
      }

      valid.validateTaskPayload(
        "github",
        "hello",
        "hmm",
        null
      )
    }
  }
}