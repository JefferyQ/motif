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
package motif.compiler.errors.parsing

import motif.ast.compiler.CompilerMethod
import motif.compiler.errors.ErrorHandler
import motif.models.errors.NullableSpreadMethodError
import javax.lang.model.element.Element

class NullableSpreadMethodHandler : ErrorHandler<NullableSpreadMethodError>() {

    override fun message(error: NullableSpreadMethodError): String {
        return "@Spread is not allowed on classes with @Nullable methods. ${error.spreadable.qualifiedName} has @Nullable methods. Consider returning an Optional type instead"
    }

    override fun element(error: NullableSpreadMethodError): Element? {
        return (error.method as CompilerMethod).element
    }

}
