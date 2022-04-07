// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.intellij.sdk.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Document
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.TextRange
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.swing.Icon


class MyAction : AnAction {
    /**
     * This default constructor is used by the IntelliJ Platform framework to instantiate this class based on plugin.xml
     * declarations. Only needed in [PopupDialogAction] class because a second constructor is overridden.
     *
     * @see AnAction.AnAction
     */
    constructor() : super() {}

    /**
     * This constructor is used to support dynamically added menu actions.
     * It sets the text, description to be displayed for the menu item.
     * Otherwise, the default AnAction constructor is used by the IntelliJ Platform.
     *
     * @param text        The text to be displayed as a menu item.
     * @param description The description of the menu item.
     * @param icon        The icon to be used with the menu item.
     */
    constructor(text: String?, description: String?, icon: Icon?) : super(text, description, icon) {}

    /**
     * Gives the user feedback when the dynamic action menu is chosen.
     * Pops a simple message dialog. See the psi_demo plugin for an
     * example of how to use [AnActionEvent] to access data.
     *
     * @param event Event received when the associated menu item is chosen.
     */
    override fun actionPerformed(event: AnActionEvent) {
        // Using the event, create and show a dialog
        val currentProject = event.project
        val dlgMsg = StringBuilder(event.presentation.text + " Selected!")
        val dlgTitle = event.presentation.description
        // If an element is selected in the editor, add info about it.
        val nav = event.getData(CommonDataKeys.NAVIGATABLE)
        if (nav != null) {
            dlgMsg.append(String.format("\nSelected Element: %s", nav.toString()))
        }
        Messages.showMessageDialog(currentProject, dlgMsg.toString(), dlgTitle, Messages.getInformationIcon())

//        val editor = event.getData(PlatformDataKeys.EDITOR)
//        val selectedText = editor?.getSelectionModel()?.getSelectedText()
//        println(selectedText)

        val editor = event.getRequiredData(CommonDataKeys.EDITOR)
        val project = event.getRequiredData(CommonDataKeys.PROJECT)
        val document: Document = editor.document

        val primaryCaret: Caret = editor.caretModel.primaryCaret
        val start: Int = primaryCaret.getSelectionStart()
        val end: Int = primaryCaret.getSelectionEnd()

        val url = document.getText(TextRange.from(start, end - start))
        println(url)

        apiCall(url)

        WriteCommandAction.runWriteCommandAction(project
        ) { document.replaceString(start, end, "editor_basics") }

        // De-select the text range that was just replaced

        // De-select the text range that was just replaced
        primaryCaret.removeSelection()
    }

    fun apiCall(url: String) {
        if (url.length == 0) {
            return
        }

        // create a client
        val client = HttpClient.newHttpClient()

        // create a request
        val request = HttpRequest.newBuilder(
                URI.create("https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY"))
                .header("accept", "application/json")
                .build()

        // use the client to send the request
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        // the response:
        System.out.println(response.body())

    }

    /**
     * Determines whether this menu item is available for the current context.
     * Requires a project to be open.
     *
     * @param e Event received when the associated group-id menu is chosen.
     */
    override fun update(event: AnActionEvent) {
        // Set the availability based on whether a project is open
        val project = event.getProject()
        val editor = event.getData(CommonDataKeys.EDITOR)

        if (project == null || editor == null) {
            event.presentation.isEnabledAndVisible = false;
        } else {
            event.presentation.isEnabledAndVisible = editor.getSelectionModel().hasSelection();
        }
    }
}
