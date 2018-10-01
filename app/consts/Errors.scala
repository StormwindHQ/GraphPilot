package consts

/**
  *
  * @param fieldName
  * @param cause
  */
final case class FieldRequiredException(
  private val fieldName: String,
  private val cause: Throwable = None.orNull)
  extends Exception(s"${fieldName} is required")

final case class FieldStringException(
  private val fieldName: String,
  private val cause: Throwable = None.orNull)
  extends Exception(s"${fieldName} should be a string")

final case class FieldStringArrayException(
  private val fieldName: String,
  private val cause: Throwable = None.orNull)
  extends Exception(s"${fieldName} should be an array of string")

object Errors {
  val MSG_REQUIRED = "%s is required"
  val MSG_NOT_STRING = "%s is not a string"
  val MSG_NOT_ARRAY_STRING = "%s is not a string array"
}
