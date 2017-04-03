package com.hust.emotion.view

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.hust.emotion.R
import com.hust.emotion.datasource.RecognizeRepo
import com.hust.emotion.datasource.RecognizeReposity
import com.hust.emotion.model.RecognizeResult
import com.hust.emotion.util.EmotionUtils
import com.hust.emotion.util.PicassoImageLoader
import com.lzy.imagepicker.ImagePicker
import com.lzy.imagepicker.bean.ImageItem
import com.lzy.imagepicker.ui.ImageGridActivity
import me.shaohui.shareutil.ShareUtil
import me.shaohui.shareutil.share.ShareListener
import me.shaohui.shareutil.share.SharePlatform
import java.util.*

class MainActivity : AppCompatActivity(), RecognizeReposity.RecognizeCallBack {
    var ivEmotion: ImageView? = null
    var tvAge: TextView? = null
    var tvGender: TextView? = null
    var tvEmotion: TextView? = null
    var tvTime: TextView? = null

    var reposity: RecognizeRepo? = null
    var dialog: MaterialDialog? = null
    var html_url = ""
    var image_path = ""
    val imagePicker = ImagePicker.getInstance()
    companion object {
        private val IMAGE_PICKER = 0x20
        private val SHARE_HTML_TEMPLATE = "http://112.74.204.138:5000/share/%s"
        private val SHARE_TITLE = "HUST Emotion"
        private val SHARE_SUMMARY = "What emotion is it?"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        ivEmotion = findViewById(R.id.iv_emotion) as ImageView
        tvAge = findViewById(R.id.tv_age) as TextView
        tvGender = findViewById(R.id.tv_gender) as TextView
        tvEmotion = findViewById(R.id.tv_emotion) as TextView
        tvTime = findViewById(R.id.tv_time) as TextView

        imagePicker.imageLoader = PicassoImageLoader()
        imagePicker.isShowCamera = true  //显示拍照按钮
        imagePicker.isCrop = false       //允许裁剪（单选才有效）
        imagePicker.isSaveRectangle = true //是否按矩形区域保存
        imagePicker.selectLimit = 1    //选中数量限制

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            if (EmotionUtils.isAvailable(applicationContext)) {
                val intent = Intent(this, ImageGridActivity::class.java)
                startActivityForResult(intent, IMAGE_PICKER)
            } else {
                showSnackbar("Network error!")
            }
        }

        reposity = RecognizeRepo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                val images = data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS) as? ArrayList<ImageItem>
                // sync image and show progress, when success, show result
                if (images != null && images.size == 1) {
                    dialog = MaterialDialog.Builder(this)
                            .title(R.string.progress_dialog)
                            .content(R.string.please_wait)
                            .progress(true, 0)
                            .cancelable(false)
                            .show()
                    image_path = images[0].path
                    reposity?.recognize(image_path, this)
                } else {
                    fileLoadError()
                }
            } else {
                fileLoadError()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        val shareListener = object: ShareListener() {
            override fun shareFailure(e: Exception?) {
                e?.printStackTrace()
                showSnackbar("Share failed!")
            }

            override fun shareCancel() {
                showSnackbar("Share canceled!")
            }

            override fun shareSuccess() {
                showSnackbar("Share Succeed!")
            }
        }
        if (html_url.isEmpty()) {
            showSnackbar("Please select images first!")
            return true
        }
        when (id) {
            R.id.menu_share2wx_friend -> {
                ivEmotion?.buildDrawingCache()
                val bitmap = ivEmotion?.drawingCache
                ShareUtil.shareMedia(this, SharePlatform.WX, SHARE_TITLE, SHARE_SUMMARY, html_url, bitmap, shareListener)
                return true
            }
            R.id.menu_share2wx_timeline -> {
                ivEmotion?.buildDrawingCache()
                val bitmap = ivEmotion?.drawingCache
                ShareUtil.shareMedia(this, SharePlatform.WX_TIMELINE, SHARE_TITLE, SHARE_SUMMARY, html_url, bitmap, shareListener)
                return true
            }
            else ->  return super.onOptionsItemSelected(item)
        }
    }

    fun dismissDialog() = if (dialog!!.isShowing) dialog!!.dismiss() else {}
    fun showSnackbar(text: String) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT)
        snackbar.duration = 5000
        snackbar.setAction("OK") {
            snackbar.dismiss()
        }
        snackbar.show()
    }

    override fun onSucccess(result: RecognizeResult?) {
        when(result?.code) {
            RecognizeReposity.CODE_ERROR -> onFailure()
            RecognizeReposity.CODE_SIZE_LIMIT -> fileSizeLimitError()
            RecognizeReposity.CODE_OK -> {
                val face = result.face
                val emotion = face.faceAttributes.emotion
                val attr = mapOf(
                        "Happy" to emotion.happiness,
                        "Sad" to emotion.sadness,
                        "Surprise" to emotion.surprise,
                        "Disgust" to emotion.disgust,
                        "Angry" to emotion.anger,
                        "Scared" to emotion.fear
                )
                val rect = face.faceRectangle
                html_url = String.format(SHARE_HTML_TEMPLATE, result.msg)
                tvAge?.text = String.format(getString(R.string.age), face.faceAttributes.age.toInt())
                tvGender?.text = String.format(getString(R.string.gender), face.faceAttributes.gender)
                tvTime?.text = String.format(getString(R.string.time_used), result.time.toString())
                tvEmotion?.text = String.format(getString(R.string.emotion), attr.maxBy { it.value }?.key)
                val src = BitmapFactory.decodeFile(image_path)
                val srcCopy = src?.copy(src.config, true)
                val canvas = Canvas(srcCopy)
                val paint = Paint()
                paint.color = Color.RED
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 10f

                canvas.drawRect(rect.left.toFloat(),
                        rect.top.toFloat(),
                        (rect.width + rect.left).toFloat(),
                        (rect.top + rect.height).toFloat(),
                        paint)
                ivEmotion?.setImageBitmap(srcCopy)

                dismissDialog()
            }
        }
    }

    override fun onFailure() {
        dismissDialog()
        showSnackbar("Network error!")
    }

    override fun fileLoadError() {
        showSnackbar("Load file failed!")
    }

    override fun fileSizeLimitError() {
        dismissDialog()
        showSnackbar("Image size over limit!")
    }
}