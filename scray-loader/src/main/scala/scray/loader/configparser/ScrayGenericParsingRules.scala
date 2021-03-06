// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package scray.loader.configparser

import com.twitter.util.Duration
import java.util.concurrent.TimeUnit
// scalastyle:off underscore.import
import org.parboiled2._
// scalastyle:on underscore.import
import scray.loader.{ UnknownBooleanValueException, UnknownTimeUnitException }

/**
 * some generic parsing stuff for parsing whitespace and principal types
 */
// scalastyle:off method.name
abstract class ScrayGenericParsingRules extends Parser {
  
  // implicitly add whitespace handling for literals
  implicit def wspStr(s: String): Rule0 = rule { str(s) ~ zeroOrMore(WhitespaceChars) }

  /* --------------------------------- parse Durations ------------------------------------------ */
  def DurationRule: Rule1[Duration] = rule { LongNumber ~ Timeunit ~> { (amount: Long, unit: TimeUnit) => Duration(amount, unit) }} 
  def Timeunit: Rule1[TimeUnit] = rule { Identifier ~> { (timeunit: String) => timeunit.toUpperCase() match {
    case "S" | "SECONDS" | "SECOND" => TimeUnit.SECONDS
    case "MS" | "MILLISECONDS" | "MILLISECOND" | "MILLIS" | "MILLI" => TimeUnit.MILLISECONDS
    case "MICROSECONDS" | "MICROSECOND" | "MICROS" | "MICRO" => TimeUnit.MICROSECONDS
    case "NS" | "NANOSECONDS" | "NANOSECOND" | "NANOS" | "NANO" => TimeUnit.NANOSECONDS
    case "D" | "DAYS" | "DAY" => TimeUnit.DAYS
    case "H" | "HOURS" | "HOUR" => TimeUnit.HOURS
    case "M" | "MIN" | "MINUTES" | "MINUTE" => TimeUnit.MINUTES
    case _ => throw new UnknownTimeUnitException(timeunit)
  }}}
  
  /* --------------------------------- generic parsing rules ------------------------------------ */
  def Identifier: Rule1[String] = rule { capture(oneOrMore(CharPredicate.AlphaNum)) ~ zeroOrMore(WhitespaceChars) }
  def IdentifierSingle: Rule1[String] = rule { capture(oneOrMore(CharPredicate.AlphaNum)) ~ zeroOrMore(SingleLineWhitespaceChars) }
  def QuotedString: Rule1[String] = rule { '"' ~ capture(oneOrMore(QuotedValueChars)) ~ '"' ~ zeroOrMore(WhitespaceChars) }
  def QuotedSingleString: Rule1[String] = rule { '"' ~ capture(oneOrMore(QuotedValueChars)) ~ '"' ~ zeroOrMore(SingleLineWhitespaceChars) }
  def BooleanRule: Rule1[Boolean] = rule { Identifier ~> { (boolStr: String) => boolStr.toUpperCase() match {
    case "TRUE" | "1" | "YES" | "Y" => true
    case "FALSE" | "0" | "NO" | "N" => false
    case _ => throw new UnknownBooleanValueException(boolStr)
  }}}
  def IntNumber: Rule1[Int] = rule { LongNumber ~> { (number: Long) => number.toInt }}
  def LongNumber: Rule1[Long] = rule { StringNumber ~> { (number: String) => number.toLong }}
  def StringNumber: Rule1[String] = rule { capture(oneOrMore(CharPredicate.Digit)) ~ zeroOrMore(WhitespaceChars) }
  def LineBreak: Rule0 = rule { oneOrMore("\r\n" | "\n") }
  val QuotedValueChars = CharPredicate.Printable -- '\u0022'
  val SingleLineWhitespaceChars = CharPredicate.Empty ++ ' ' ++ "\t"
  val WhitespaceChars = CharPredicate.Empty ++ ' ' ++ "\r" ++ "\n" ++ "\t"
}
// scalastyle:on method.name

