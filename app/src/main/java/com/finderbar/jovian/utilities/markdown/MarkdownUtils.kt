package com.finderbar.jovian.utilities.markdown
import android.widget.EditText
import android.widget.TextView
import ru.noties.markwon.Markwon;
import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.il.AsyncDrawableLoader
import ru.noties.markwon.renderer.html2.MarkwonHtmlRenderer

private val urlProcessor = CustomUrlProcessor()
private val specialChars = mapOf(
        "&lt;" to "<",
        "&gt;" to ">",
        "&quot;" to "\"",
        "&nbsp;" to " ",
        "&amp;" to "&",
        "&apos;" to "'",
        "&#39;" to "'",
        "&#40;" to "(",
        "&#41;" to ")",
        "&#215;" to "×"
)
private fun String.stripSpecials(): String {
    var result = this
    specialChars.forEach { (key, value) ->
        result = result.replace(key, value)
    }
    return result
}

fun TextView.setMarkdown(markdown: String) {
    Markwon.setMarkdown(this,
            SpannableConfiguration.builder(context)
                    .urlProcessor(urlProcessor)
                    .asyncDrawableLoader(AsyncDrawableLoader.create())
                    .softBreakAddsNewLine(true)
                    .htmlRenderer(MarkwonHtmlRenderer.create())
                    .build(),
            markdown.stripSpecials()
    )
}



internal fun insertMarkdownInString(string: String, markdown: String, insertAtPosition: Int) : String {
    val preStr = string.substring(0, insertAtPosition)
    val postStr = try {
        string.substring(insertAtPosition)
    } catch (e: Exception) {
        ""
    }
    return "$preStr$markdown$postStr"
}

internal fun wrapStringWithMarkdown(string: String, splitMarkdown: String, selectionStart: Int, selectionEnd: Int) : String {
    val stringToWrap = string.substring(selectionStart, selectionEnd)
    val preStr = string.substring(0, selectionStart)
    val postStr = string.substring(selectionEnd)
    return "$preStr$splitMarkdown$stringToWrap$splitMarkdown$postStr"
}

internal fun getFormattingMarkdown(string: String, splitMarkdown: String, selectionStart: Int, selectionEnd: Int) : String {
    return if (selectionStart == selectionEnd) {
        insertMarkdownInString(string, splitMarkdown + splitMarkdown, selectionEnd)
    } else {
        wrapStringWithMarkdown(string, splitMarkdown, selectionStart, selectionEnd)
    }
}

internal fun EditText.insertFormattingMarkdown(splitMarkdown: String) {
    val currentSelectionStart = selectionStart
    val currentSelectionEnd = selectionEnd
    val currentText = this.text.toString()
    val adjustedText = getFormattingMarkdown(currentText, splitMarkdown, currentSelectionStart, currentSelectionEnd)
    setText(adjustedText)
    setSelection(currentSelectionEnd + splitMarkdown.length)
}


internal fun EditText.removeMostImmediatelyPreviousIndent() {
    val currentSelectionEnd = selectionEnd
    var newSelection = selectionEnd
    val currentTextEndingAtSelectionEnd = text.toString().substring(0, currentSelectionEnd)
    val postStr = text.toString().substring(currentSelectionEnd)
    val modifiedpreStr = if (currentTextEndingAtSelectionEnd.lastIndexOf("&nbsp;") >= 0) {
        val preSubString = currentTextEndingAtSelectionEnd.substring(0, currentTextEndingAtSelectionEnd.lastIndexOf("&nbsp;"))
        val postSubString = currentTextEndingAtSelectionEnd.substring(currentTextEndingAtSelectionEnd.lastIndexOf("&nbsp;") + "&nbsp;".count())
        newSelection -= "&nbsp;".count()
        preSubString + postSubString
    } else {
        currentTextEndingAtSelectionEnd
    }
    setText("$modifiedpreStr$postStr")
    try {
        setSelection(newSelection)
    } catch (e: Exception) {

    }
}

internal fun EditText.addListItemWithListMarker(listMarker: String) {
    val currentSelectionStart = selectionStart
    val currentSelectionEnd = selectionEnd
    val currentText = this.text.toString()
    var selectionToSet = 0
    val lineBreak = "\n"
    if (currentSelectionEnd != currentSelectionStart) {
        val preStr = currentText.substring(0, currentSelectionStart)
        val stringToPutInList = currentText.substring(currentSelectionStart, currentSelectionEnd)
        val postStr = currentText.substring(currentSelectionEnd)
        val stringToInsert = if (preStr.count() == 0) {
            selectionToSet = "$listMarker $stringToPutInList".count()
            if (postStr.count() == 0) {
                "$listMarker $stringToPutInList"
            } else {
                if (stringToPutInList.endsWith("\n")) {
                    "$listMarker $stringToPutInList$postStr"
                } else {
                    "$listMarker $stringToPutInList $lineBreak$postStr"
                }
            }
        } else {
            if (postStr.count() == 0) {
                if (preStr.endsWith("\n")) {
                    selectionToSet = "$preStr$listMarker $stringToPutInList".count()
                    "$preStr$listMarker $stringToPutInList"
                } else {
                    selectionToSet = "$preStr $lineBreak$listMarker $stringToPutInList".count()
                    "$preStr $lineBreak$listMarker $stringToPutInList"
                }
            } else {
                if (preStr.endsWith("\n")) {
                    selectionToSet = "$preStr$listMarker $stringToPutInList".count()
                    if (stringToPutInList.endsWith("\n")) {
                        "$preStr$listMarker $stringToPutInList$postStr"
                    } else {
                        "$preStr$listMarker $stringToPutInList $lineBreak$postStr"
                    }
                } else {
                    selectionToSet = "$preStr $lineBreak$listMarker $stringToPutInList".count()
                    if (stringToPutInList.endsWith("\n")) {
                        "$preStr $lineBreak$listMarker $stringToPutInList$postStr"
                    } else {
                        "$preStr $lineBreak$listMarker $stringToPutInList $lineBreak$postStr"
                    }
                }
            }
        }
        setText(stringToInsert)
    } else {
        val stringToInsert = if (currentText.count() > 0) {
            val preStr = currentText.substring(0, currentSelectionEnd)
            val postStr = currentText.substring(currentSelectionEnd)
            val fullString = if (!currentText.endsWith("\n")) {
                "$preStr $lineBreak$listMarker "
            } else {
                "$preStr$listMarker "
            }
            selectionToSet = fullString.length
            if (postStr.count() > 0) {
                "$fullString $lineBreak$postStr"
            } else {
                fullString
            }
        } else {
            selectionToSet = listMarker.count() + 1
            "$listMarker "
        }
        setText(stringToInsert)
    }
    try { setSelection(selectionToSet) } catch (e: Exception) {
        setSelection(this.text.length)
    }
}
