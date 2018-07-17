package com.uber.motif.compiler.codegen

import com.squareup.javapoet.MethodSpec
import com.uber.motif.compiler.model.ExposerMethod

class ExposerMethodImplSpec(
        componentSpec: ComponentSpec,
        componentFieldSpec: ComponentFieldSpec,
        exposerMethod: ExposerMethod) {

    val spec: MethodSpec = MethodSpec.overriding(exposerMethod.element)
            .addStatement("return \$N.\$N()", componentFieldSpec.spec, componentSpec.provisionMethods[exposerMethod.dependency])
            .build()
}