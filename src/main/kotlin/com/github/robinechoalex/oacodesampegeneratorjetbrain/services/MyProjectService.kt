package com.github.robinechoalex.oacodesampegeneratorjetbrain.services

import com.intellij.openapi.project.Project
import com.github.robinechoalex.oacodesampegeneratorjetbrain.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
