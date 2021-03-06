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
package scray.common.serialization;

/**
 * simple cient-side JAVA-based row columns implementation
 */
public class JavaRowColumn<T> {

	private JavaColumn column = null;
	private T value = null;
	
	public JavaRowColumn() {}
	public JavaRowColumn(JavaColumn column, T value) {
		this.column = column;
		this.value = value;
	}
	
	public JavaColumn getColumn() {
		return column;
	}
	public void setColumn(JavaColumn column) {
		this.column = column;
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
}
