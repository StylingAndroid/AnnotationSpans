package com.stylingandroid.annotationspans

import android.graphics.Typeface
import android.os.Bundle
import android.text.Annotation
import android.text.SpannableStringBuilder
import android.text.SpannedString
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.SuperscriptSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import androidx.core.text.toSpannable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

private const val RELATIVE_SIZE = 0.5f

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        job = Job()

        launch {
            annotated_text.text = processAnnotations(annotated_text.text)
        }
    }

    private suspend fun processAnnotations(text: CharSequence?): CharSequence? {
        return if (text is SpannedString) {
            withContext(Dispatchers.IO) {
                val spannableStringBuilder = SpannableStringBuilder(text)
                text.getSpans(0, text.length, Annotation::class.java)
                        .filter { it.key == "format" }
                        .forEach { annotation ->
                            text.processFormatAnnotations(annotation, spannableStringBuilder)
                        }
                spannableStringBuilder.toSpannable()
            }
        } else {
            text
        }
    }

    private fun SpannedString.processFormatAnnotations(annotation: Annotation, output: SpannableStringBuilder) {
        val start: Int = getSpanStart(annotation)
        val end: Int = getSpanEnd(annotation)
        when (annotation.value) {
            "bold" -> output[start..end] = StyleSpan(Typeface.BOLD)
            "italic" -> output[start..end] = StyleSpan(Typeface.ITALIC)
            "superscript" -> {
                output[start..end] = SuperscriptSpan()
                output[start..end] = RelativeSizeSpan(RELATIVE_SIZE)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
