/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.*
import org.jetbrains.kotlin.gradle.utils.isGradleVersionAtLeast

internal object ProjectLocalConfigurations {
    val ATTRIBUTE = Attribute.of("org.jetbrains.kotlin.localToProject", String::class.java)

    fun setupAttributesMatchingStrategy(attributesSchema: AttributesSchema) = with(attributesSchema) {
        attribute(ATTRIBUTE) {
            if (isGradleVersionAtLeast(4, 0)) {
                it.compatibilityRules.add(ProjectLocalCompatibility::class.java)
                it.disambiguationRules.add(ProjectLocalDisambiguation::class.java)
            }
        }
    }

    class ProjectLocalCompatibility : AttributeCompatibilityRule<String> {
        override fun execute(details: CompatibilityCheckDetails<String>) {
            details.compatible()
        }
    }

    class ProjectLocalDisambiguation : AttributeDisambiguationRule<String> {
        override fun execute(details: MultipleCandidatesDetails<String?>) = with(details) {
            if (candidateValues.contains(null)) {
                @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                closestMatch(null as String?)
            }
        }
    }
}

internal fun Configuration.localToProject(project: Project) {
    if (isGradleVersionAtLeast(4, 0)) {
        attributes.attribute(ProjectLocalConfigurations.ATTRIBUTE, project.path)
    }
}
