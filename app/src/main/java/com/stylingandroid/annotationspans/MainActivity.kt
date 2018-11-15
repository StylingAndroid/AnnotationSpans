package com.stylingandroid.annotationspans

import android.graphics.Typeface
import android.os.Bundle
import android.text.Annotation
import android.text.SpannableStringBuilder
import android.text.SpannedString
import android.text.style.StyleSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import androidx.core.text.toSpannable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val boldSpan = StyleSpan(Typeface.BOLD)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.launch(Dispatchers.Main) {
            annotated_text.text = processAnnotations(annotated_text.text)
        }
    }

    private suspend fun processAnnotations(text: CharSequence?): CharSequence? {
        return if (text is SpannedString) {
            GlobalScope.async {
                val spannableStringBuilder = SpannableStringBuilder(text)
                text.getSpans(0, text.length, Annotation::class.java)
                    .filter { it.key == "format" && it.value == "bold" }
                    .forEach { annotation ->
                        spannableStringBuilder[text.getSpanStart(annotation)..text.getSpanEnd(annotation)] = boldSpan
                    }
                spannableStringBuilder.toSpannable()
            }.await()
        } else {
            text
        }
    }
}
