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
package scray.querying.caching

import scray.common.serialization.numbers.KryoRowTypeNumber

/**
 * identification numbers for caching serialization
 */
package object serialization {
  val SIMPLE_ROW = KryoRowTypeNumber.simplerow.getNumber()
  val COMPOSITE_ROW = KryoRowTypeNumber.compositerow.getNumber()
  
  val CACHE_INCOMPLETE_MARKER_ROW = KryoRowTypeNumber.values().maxBy(_.getNumber()).getNumber() + 1
  val CACHE_COMPLETE_MARKER_ROW = KryoRowTypeNumber.values().maxBy(_.getNumber()).getNumber() + 2
}
