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

package scray.querying.sync.conf

import scala.beans.BeanProperty
import com.typesafe.scalalogging.slf4j.LazyLogging

object ConsistencyLevel extends Enumeration {
  type ConsistencyLevel = Value
  val LOCAL_SERIAL, ALL = Value
}

class SyncConfiguration extends Serializable with LazyLogging {
 var dbSystem: String   = "Sync"
 var tableName: String  = "SyncTable"
 var replicationSetting: String = "{ 'class' : 'SimpleStrategy', 'replication_factor' : 1}"
 var versionUpdateConsitencyLevel: ConsistencyLevel.ConsistencyLevel = ConsistencyLevel.ALL
}