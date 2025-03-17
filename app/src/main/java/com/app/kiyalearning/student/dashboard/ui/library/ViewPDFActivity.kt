package com.app.kiyalearning.dashboard.ui.library

import android.os.Bundle
import android.util.Log
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.kiyalearning.databinding.ActivityPdfViewerBinding
import com.app.kiyalearning.util.UtilClass


class ViewPDFActivity : AppCompatActivity()  {

    lateinit var binding: ActivityPdfViewerBinding
    private var pdfLink=""
    var pdfFileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UtilClass.setStatusBarProperty(this)


        pdfLink=intent.getStringExtra("PDF_LINK").toString()
        //test pdf only
      //  pdfLink="https://www.adobe.com/devnet/acrobat/pdfs/pdf_open_parameters.pdf"
        pdfFileName=intent.getStringExtra("PDF_FILE_NAME").toString()
        binding.headerTxt.text=pdfFileName
        if(pdfLink.isNotBlank())
        {
            displayPdf(pdfLink)
            Log.d("MyTag", "onCreate pdfLink: "+pdfLink)
        }else
        {
            Toast.makeText(this,"Pdf Link is Empty",Toast.LENGTH_SHORT).show()
            onBackPressedDispatcher.onBackPressed()
        }



//        if(MyNetworks.isNetworkAvailable(this))
//            viewModel.getProfileDetails(this)

        binding.backIcon.setOnClickListener{
           onBackPressedDispatcher.onBackPressed()
        }


    }

    private fun displayPdf(assetFileName: String) {
        binding.pdfView.webViewClient = WebViewClient()
        binding.pdfView.settings.javaScriptEnabled = true
        binding.pdfView.settings.setSupportZoom(true)
       // binding.pdfView.loadUrl(assetFileName)
        binding.pdfView.loadUrl(
            "http://docs.google.com/gview?embedded=true&url=$assetFileName"
        )

    }



}