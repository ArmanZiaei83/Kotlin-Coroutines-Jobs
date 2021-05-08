package com.example.coroutinejobs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    private val MAX_LEN = 100
    private val MIN_LEN = 0
    private val JOB_TIME = 4000

    //Job :
    lateinit var job : CompletableJob

    lateinit var progressBar : ProgressBar
    lateinit var click : Button
    lateinit var resultTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUi()
        click.setOnClickListener{
            if (!::job.isInitialized){
                createJob()
            }
            setProgressBar(job)
        }
    }

    private fun setProgressBar(job : Job) {
        if (progressBar.progress > 0) {
            println("Job is started, cancelling the job ... ")
            resetJob()
        }else{
            click.text = "Cancel The Job"
            CoroutineScope(IO + job).launch {
                println("Job Is Activated ... ")
                for (i in MIN_LEN.. MAX_LEN){
                    delay((JOB_TIME / MAX_LEN).toLong())
                    progressBar.progress = i
                }
                addText("Job Is Completed ... ")
            }
        }
    }

    private fun addText(s: String) {
        GlobalScope.launch(Main){
            resultTextView.text = s
        }
    }

    private fun resetJob() {
        if (job.isActive || job.isCompleted){
            job.cancel(CancellationException("Resetting The Job"))
        }
        createJob()
    }

    private fun createJob() {
        click.text = "Start The Job"
        addText("")
        job = Job()
        job.invokeOnCompletion {
            it?.message.let {
                var message = it
                if (message.isNullOrBlank()){
                    println("Job was canceled by an unknown cancellation ...")
                }
                println("$job was canceled. Reason : $message")
                makeToast(message)
            }
        }
        progressBar.max = MAX_LEN
        progressBar.progress = MIN_LEN
    }

    private fun makeToast(message: String?) {
        CoroutineScope(Main).launch {
            Toast.makeText(this@MainActivity , message , Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUi() {
        progressBar = findViewById(R.id.progressBar)
        click = findViewById(R.id.startStop)
        resultTextView = findViewById(R.id.showResult)
    }
}