/*
 * Copyright (c) 2018 Uber Technologies, Inc.
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
package motif.stubcompiler

import com.google.auto.common.MoreElements
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import motif.Scope
import motif.ast.compiler.CompilerClass
import motif.ast.compiler.CompilerMethod
import java.util.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType

class StubProcessor : AbstractProcessor() {

    private val env: ProcessingEnvironment by lazy { processingEnv }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return Collections.singleton(Scope::class.java.name)
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(Scope::class.java)
                .map { it as TypeElement }
                .forEach { scopeElement ->
                    val packageName: String = MoreElements.getPackage(scopeElement).qualifiedName.toString()
                    val spec: TypeSpec = spec(scopeElement.asType() as DeclaredType) ?: return@forEach
                    JavaFile.builder(packageName, spec).build().writeTo(processingEnv.filer)
                }
        return true
    }

    private fun spec(scopeType: DeclaredType): TypeSpec? {
        val scopeClassName = ClassName.get(scopeType)
        val scopeImplClassName = scopeImpl(scopeType)
        if (env.elementUtils.getTypeElement(scopeImplClassName.qualifiedName()) != null) {
            return null
        }
        val builder = TypeSpec.classBuilder(scopeImplClassName)
                .addMethod(MethodSpec.constructorBuilder().build())
                .addType(TypeSpec.interfaceBuilder("Dependencies").build())

        if (scopeType.asElement().kind == ElementKind.INTERFACE) {
            builder.addSuperinterface(scopeClassName)
        } else {
            builder.superclass(scopeClassName)
        }

        CompilerClass(env, scopeType).methods
                .map { it as CompilerMethod }
                .forEach { method: CompilerMethod ->
                    val methodSpec: MethodSpec = MethodSpec.overriding(method.element, scopeType, processingEnv.typeUtils)
                            .addStatement("throw new \$T()", IllegalStateException::class.java)
                            .build()
                    builder.addMethod(methodSpec)
                }

        return builder.build()
    }

    private fun scopeImpl(scopeType: DeclaredType): ClassName {
        val scopeClassName = ClassName.get(scopeType) as ClassName
        val prefix = scopeClassName.simpleNames().joinToString("")
        return ClassName.get(scopeClassName.packageName(), "${prefix}Impl")
    }

    private fun ClassName.qualifiedName(): String {
        return "${packageName()}.${simpleNames().joinToString(".")}"
    }
}