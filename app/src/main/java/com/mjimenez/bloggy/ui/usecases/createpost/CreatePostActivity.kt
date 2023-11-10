package com.mjimenez.bloggy.ui.usecases.createpost

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import com.mjimenez.bloggy.databinding.ActivityCreatePostBinding
import com.mjimenez.bloggy.service.RetrofitInstance
import com.mjimenez.bloggy.service.dto.SavingPost
import com.mjimenez.bloggy.ui.usecases.home.HomeActivity
import com.mjimenez.bloggy.ui.validator.EmptyValidator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class CreatePostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreatePostBinding
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.title.addTextChangedListener {
            binding.titleLay.error = null
        }
        binding.author.addTextChangedListener {
            binding.authorLay.error = null
        }
        binding.content.addTextChangedListener {
            binding.contentLay.error = null
        }

        binding.date.setText(dateFormat.format(Date()))

        binding.save.setOnClickListener { view ->
            if (validate()) {
                val tz = TimeZone.getTimeZone("UTC")
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
                dateFormat.timeZone = tz
                val savingPost = SavingPost(
                    title = binding.title.text.toString(),
                    date = dateFormat.format(Date()),
                    author = binding.author.text.toString(),
                    content = binding.content.text.toString()
                )
                println(savingPost)
                scope.launch {
                    savePost(savingPost = savingPost)
                }
            } else {
                Snackbar.make(view, "Uno o más campos no son válidos", Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    private suspend fun savePost(savingPost: SavingPost) {
        scope.launch {
            try {
                RetrofitInstance.api.savePost(savingPost)
                binding.title.setText("")
                binding.author.setText("")
                binding.content.setText("")
                Snackbar.make(binding.save, "Publicación guardada", Snackbar.LENGTH_LONG)
                    .show()
            } catch (e: Exception) {
                println(e)
                return@launch
            }
        }
    }

    private fun validate(): Boolean {
        val titleValidation = EmptyValidator(binding.title.text!!.toString().trim()).validate()
        binding.titleLay.error =
            if (!titleValidation.isSuccess) getString(titleValidation.message) else null

        val authorValidation = EmptyValidator(binding.author.text!!.toString().trim()).validate()
        binding.authorLay.error =
            if (!authorValidation.isSuccess) getString(authorValidation.message) else null

        val contentValidation = EmptyValidator(binding.content.text!!.toString().trim()).validate()
        binding.contentLay.error =
            if (!contentValidation.isSuccess) getString(contentValidation.message) else null

        return titleValidation.isSuccess && authorValidation.isSuccess && contentValidation.isSuccess
    }

    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(applicationContext, HomeActivity::class.java))
        finish()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(applicationContext, HomeActivity::class.java))
    }
}