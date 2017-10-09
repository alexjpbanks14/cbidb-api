package Storable

import Storable.Fields.FieldValue._
import Storable.Fields.IntDatabaseField

import scala.reflect.runtime.universe._

abstract class StorableClass {
  type IntFieldValueMap = Map[String, IntFieldValue]
  type NullableIntFieldValueMap = Map[String, NullableIntFieldValue]
  type StringFieldValueMap = Map[String, StringFieldValue]
  type NullableStringFieldValueMap = Map[String, NullableStringFieldValue]
  type BooleanFieldValueMap = Map[String, BooleanFieldValue]
  type DateFieldValueMap = Map[String, DateFieldValue]
  type DateTimeFieldValueMap = Map[String, DateTimeFieldValue]

  type Companion = StorableObject[_ <: StorableClass]

  val self: StorableClass = this
  val values: ValuesObject

  private var companion: Option[Companion] = None
  def getCompanion: Companion = companion match {
    case None => throw new Exception("Companion object never set for entity")
    case Some(c: Companion) => c
  }
  def setCompanion(c: Companion): Unit = companion match {
    case Some(_) => throw new Exception("Multiple calls to set companion of entity instance")
    case None => companion = Some(c)
  }

  private def getPrimaryKeyFieldValue: IntFieldValue = {
    val companion: Companion = getCompanion
    val primaryKey: IntDatabaseField = companion.primaryKey
    println(primaryKey.getPersistenceFieldName)
    println(primaryKey.getRuntimeFieldName)
    val primaryKeyFieldRuntimeName: String = primaryKey.getRuntimeFieldName
    intValueMap(primaryKeyFieldRuntimeName)
  }

  def getID: Int = getPrimaryKeyFieldValue.get


  def hasID: Boolean = getPrimaryKeyFieldValue.peek match {
    case Some(_) => true
    case None => false
  }

  // "clean" = internal state of this instance is consistent with the state of the database
  // On construction, assume dirty; the codepath used by the DB adapter will set clean afterconstructing
  private var clean: Boolean = false
  def isClean: Boolean = clean
  def setClean(): Unit =
    clean = true
  def setDirty(): Unit =
    clean = false

  private val valueMaps = {
    val rm = scala.reflect.runtime.currentMirror
    val accessors = rm.classSymbol(values.getClass).toType.members.collect {
      case m: MethodSymbol if m.isGetter && m.isPublic => m
    }
    val instanceMirror = rm.reflect(values)
    val regex = "^(value|method) (.*)$".r

    var intMap: IntFieldValueMap = Map()
    var nullableIntMap: NullableIntFieldValueMap = Map()
    var stringMap: StringFieldValueMap = Map()
    var nullableStringMap: NullableStringFieldValueMap = Map()
    var booleanMap: BooleanFieldValueMap = Map()
    var dateMap: DateFieldValueMap = Map()
    var dateTimeMap: DateTimeFieldValueMap = Map()

    for(acc <- accessors) {
      val symbol = instanceMirror.reflectMethod(acc).symbol.toString
      val name = symbol match {
        case regex(t, name1) => name1
        case _ => throw new Exception("Unparsable reflection result: " + symbol)
      }

      instanceMirror.reflectMethod(acc).apply() match {
        case i: IntFieldValue => intMap += (name -> i)
        case ni: NullableIntFieldValue => nullableIntMap += (name -> ni)
        case s: StringFieldValue => stringMap += (name -> s)
        case ns: NullableStringFieldValue => nullableStringMap += (name -> ns)
        case b: BooleanFieldValue => booleanMap += (name -> b)
        case d: DateFieldValue => dateMap += (name -> d)
        case dt: DateTimeFieldValue => dateTimeMap += (name -> dt)
        case _ => throw new Exception("Unrecognized field type")
      }
    }

    (intMap, nullableIntMap, stringMap, nullableStringMap, booleanMap, dateMap, dateTimeMap)
  }

  val intValueMap: IntFieldValueMap = valueMaps._1
  val nullableIntValueMap: NullableIntFieldValueMap = valueMaps._2
  val stringValueMap: StringFieldValueMap = valueMaps._3
  val nullableStringValueMap: NullableStringFieldValueMap = valueMaps._4
  val booleanValueMap: BooleanFieldValueMap = valueMaps._5
  val dateValueMap: DateFieldValueMap = valueMaps._6
  val dateTimeValueMap: DateTimeFieldValueMap = valueMaps._7
}
