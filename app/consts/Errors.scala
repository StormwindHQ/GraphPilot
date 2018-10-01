package consts

/**
  * When required field is not present
  * @param fieldName
  * @param cause
  */
final case class FieldRequiredException(
  private val fieldName: String,
  private val cause: Throwable = None.orNull)
  extends Exception(s"${fieldName} is required")

/**
  * When a field type is string but the value is something else
  * @param fieldName
  * @param cause
  */
final case class FieldStringException(
  private val fieldName: String,
  private val cause: Throwable = None.orNull)
  extends Exception(s"${fieldName} should be a string")

/**
  * When a field type is an array of string but the value is something else
  * @param fieldName
  * @param cause
  */
final case class FieldStringArrayException(
  private val fieldName: String,
  private val cause: Throwable = None.orNull)
  extends Exception(s"${fieldName} should be an array of string")

final case class FieldEnumException(
  private val fieldName: String,
  private val cause: Throwable = None.orNull)
  extends Exception(s"${fieldName} should be a correct enum value")
