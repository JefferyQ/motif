/*
 * Copyright (c) 2018-2019 Uber Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package motif.compiler.codegen

class UniqueNameSet {

    private val used: MutableSet<String> = mutableSetOf()

    fun unique(base: String): String {
        var name = base
        var i = 2
        while (!used.add(name)) {
            name = "$base${i++}"
        }
        return name
    }

    fun claim(name: String) {
        used.add(name)
    }
}
