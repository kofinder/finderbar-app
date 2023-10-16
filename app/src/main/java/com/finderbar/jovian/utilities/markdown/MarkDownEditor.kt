package com.finderbar.jovian.utilities.markdown
import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import com.finderbar.jovian.R

/**
 * Created by thein on 12/17/18.
 */

var currentNumberedListIndex = -1;
var isNumberedListOn = false;
var isBulletListOn = false;
var isQuoteOn = false;
const val doubleLineBreak = "\n\n"

inline fun setupEditor(ctx: Context, editor: EditText, btBold: ImageButton, btItalic: ImageButton, btStrike: ImageButton,
                       btCode: ImageButton, btLink: ImageButton, btIndentIncrease: ImageButton, btQuote: ImageButton,
                       btBulletList: ImageButton, btNumberedList: ImageButton, btTitle1: ImageButton, btTitle2: ImageButton,
                       btTitle3: ImageButton, btTitle4: ImageButton, btTitle5: ImageButton, btTitle6: ImageButton, llTitleOptions: LinearLayout) {

    editor?.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
            val text = try { p0?.substring(start, start+count) } catch (e: Exception) {""}
            if (text == "\n") {
                when {
                    isBulletListOn -> { editor.addBulletListItem() }
                    isQuoteOn -> { editor?.addQuote() }
                    isNumberedListOn -> {
                        if (currentNumberedListIndex == -1) {
                            currentNumberedListIndex = 1
                        } else {
                            currentNumberedListIndex ++
                        }
                        editor?.addNumberedListItem(currentNumberedListIndex)
                    }
                }
            }
        }
    })

    btQuote?.setOnClickListener {
        if (!isQuoteOn) {
            currentNumberedListIndex = -1
            isQuoteOn = true
            if (editor?.text?.count() ?: 0 > 0) {
                editor?.insertDoubleNewLine()
            }
            editor?.addQuote()
        } else {
            isQuoteOn = false
            editor?.insertDoubleNewLine()
        }
        isNumberedListOn = false
        toggleControlButton(ctx, btNumberedList, isNumberedListOn)
        isBulletListOn = false
        toggleControlButton(ctx, btBulletList, isBulletListOn)
        toggleControlButton(ctx, btQuote, isQuoteOn)
    }
    btNumberedList?.setOnClickListener {
            if (!isNumberedListOn) {
                isNumberedListOn = true
                if (editor?.text?.count() ?: 0 > 0) {
                    editor?.insertDoubleNewLine()
                }
                if (currentNumberedListIndex == -1) {
                    currentNumberedListIndex = 1
                } else {
                    currentNumberedListIndex ++
                }
                editor?.addNumberedListItem(currentNumberedListIndex)
            } else {
                isNumberedListOn = false
                currentNumberedListIndex = -1
                editor?.insertDoubleNewLine()
            }
            isBulletListOn = false
            toggleControlButton(ctx, btBulletList, isBulletListOn)
            isQuoteOn = false
            toggleControlButton(ctx, btQuote, isQuoteOn)
            toggleControlButton(ctx, btNumberedList, isNumberedListOn)
    }
    btBulletList?.setOnClickListener {
            if (!isBulletListOn) {
                currentNumberedListIndex = -1
                isBulletListOn = true
                if (editor?.text?.count() ?: 0 > 0) {
                    editor?.insertDoubleNewLine()
                }
                editor?.addBulletListItem()
            } else {
                isBulletListOn = false
                editor?.insertDoubleNewLine()
            }
            isNumberedListOn = false
            toggleControlButton(ctx, btNumberedList, isNumberedListOn)
            isQuoteOn = false
            toggleControlButton(ctx, btQuote, isQuoteOn)
            toggleControlButton(ctx, btBulletList, isBulletListOn)
    }

    btTitle1?.apply {
        setOnLongClickListener {
            if (llTitleOptions?.visibility == View.VISIBLE) {
                llTitleOptions?.visibility = View.GONE
            } else {
                llTitleOptions?.visibility = View.VISIBLE
            }
            true
        }
        setOnClickListener {
            if (llTitleOptions?.visibility == View.VISIBLE) {
                llTitleOptions?.visibility = View.GONE
            } else {
                editor?.addTitle(1)
            }
        }
    }

    btTitle2?.setOnClickListener {
        editor?.addTitle(2)
        llTitleOptions?.visibility = View.GONE
    }
    btTitle3?.setOnClickListener {
        editor?.addTitle(3)
        llTitleOptions?.visibility = View.GONE
    }
    btTitle4?.setOnClickListener {
        editor?.addTitle(4)
        llTitleOptions?.visibility = View.GONE
    }
    btTitle5?.setOnClickListener {
        editor?.addTitle(5)
        llTitleOptions?.visibility = View.GONE
    }
    btTitle6?.setOnClickListener {
        editor?.addTitle(6)
        llTitleOptions?.visibility = View.GONE
    }
    btBold?.setOnClickListener { editor?.addBold() }
    btItalic?.setOnClickListener { editor?.addItalic() }
    btStrike?.setOnClickListener { editor?.addStrikethrough() }
    btCode?.setOnClickListener { editor?.addCode() }
    btLink?.setOnClickListener { editor?.addLink() }
    btIndentIncrease?.setOnClickListener { editor?.addIndent() }
}

fun toggleControlButton(activity: Context, button: ImageButton, isNowOn: Boolean) {
    activity.let {
        if (isNowOn) {
            button.setBackgroundColor(ContextCompat.getColor(it, R.color.colorAccent))
        } else {
            val attrs = intArrayOf(R.attr.selectableItemBackground)
            val typedArray = it.obtainStyledAttributes(attrs)
            val backgroundResource = typedArray.getResourceId(0, 0)
            button.setBackgroundResource(backgroundResource)
            typedArray.recycle()
        }
    }
}

fun EditText.addQuote() { addListItemWithListMarker(">") }

fun EditText.addBulletListItem() { addListItemWithListMarker("*") }

fun EditText.addNumberedListItem(numberValue: Int) { addListItemWithListMarker("$numberValue.") }

fun EditText.addBold() { insertFormattingMarkdown("**") }

fun EditText.addItalic() { insertFormattingMarkdown("_") }

fun EditText.addStrikethrough() { insertFormattingMarkdown("~~") }

fun EditText.addCode() { insertFormattingMarkdown("`") }

fun EditText.insertDoubleNewLine() {
    setText(text.toString() + doubleLineBreak)
    setSelection(this.text.length)
}

fun EditText.insertSingleNewLine() {
    setText(text.toString() + "\n")
    setSelection(this.text.length)
}

fun EditText.addLink() {
    val currentSelectionEnd = selectionEnd
    val currentText = this.text.toString()
    val preStr = currentText.substring(0, currentSelectionEnd)
    val postStr = currentText.substring(currentSelectionEnd)
    val fullString = "$preStr [LABEL](link) $postStr"
    this.setText(fullString)
}

fun EditText.addIndent() {
    val currentSelectionStart = selectionStart
    val preStr = text.toString().substring(0, currentSelectionStart)
    val postStr = text.toString().substring(currentSelectionStart)
    setText("$preStr&nbsp;$postStr")
    setSelection("$preStr&nbsp;".count())
}

fun EditText.addTitle(titleNumber: Int) {
    var listMarker = ""
    for (i in 0 until titleNumber) {
        listMarker += "#"
    }
    addListItemWithListMarker(listMarker)
}
