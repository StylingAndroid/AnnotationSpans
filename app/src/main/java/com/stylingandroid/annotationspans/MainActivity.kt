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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

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
                        .filter { it.key == "format" && it.value == "bold" }
                        .forEach { annotation ->
                            spannableStringBuilder[text.getSpanStart(annotation)..text.getSpanEnd(annotation)] =
                                    StyleSpan(Typeface.BOLD)
                        }
                spannableStringBuilder.toSpannable()
            }
        } else {
            text
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
