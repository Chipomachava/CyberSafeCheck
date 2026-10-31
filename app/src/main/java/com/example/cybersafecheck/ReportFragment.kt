package com.example.cybersafecheck

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cybersafecheck.database.ReportEntity
import com.example.cybersafecheck.databinding.FragmentReportBinding
import com.example.cybersafecheck.databinding.ListItemReportBinding
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private lateinit var repository: RiskRepository
    private var photoName: String? = null
    private var pendingPhotoName: String? = null

    private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        if (didTakePhoto && pendingPhotoName != null) {
            photoName = pendingPhotoName
            showPhoto(binding.reportPhoto, photoName)
        }
        pendingPhotoName = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoName = savedInstanceState?.getString(KEY_PHOTO_NAME)
        pendingPhotoName = savedInstanceState?.getString(KEY_PENDING_PHOTO_NAME)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        binding.reportRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = RiskRepository.get(requireContext())

        val captureImageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        binding.takePhotoButton.isEnabled = canResolveIntent(captureImageIntent)

        binding.takePhotoButton.setOnClickListener {
            val newPhotoName = "IMG_${System.currentTimeMillis()}.JPG"
            pendingPhotoName = newPhotoName
            takePhoto.launch(getPhotoUri(newPhotoName))
        }

        binding.saveReportButton.setOnClickListener {
            saveReport()
        }

        showPhoto(binding.reportPhoto, photoName)
        loadReports()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_PHOTO_NAME, photoName)
        outState.putString(KEY_PENDING_PHOTO_NAME, pendingPhotoName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun saveReport() {
        val description = binding.reportDescription.text?.toString()?.trim().orEmpty()
        if (description.isEmpty()) {
            binding.reportDescriptionLayout.error = getString(R.string.report_description_required)
            return
        }
        binding.reportDescriptionLayout.error = null

        val photoUri = photoName?.let { getPhotoUri(it).toString() }

        viewLifecycleOwner.lifecycleScope.launch {
            repository.addReport(description, photoUri)
            binding.reportDescription.setText("")
            photoName = null
            showPhoto(binding.reportPhoto, null)
            loadReports()
            Toast.makeText(requireContext(), R.string.report_saved, Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadReports() {
        viewLifecycleOwner.lifecycleScope.launch {
            val reports = repository.getReports()
            binding.reportRecyclerView.adapter = ReportAdapter(reports)
            binding.reportsEmptyText.visibility =
                if (reports.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun getPhotoUri(fileName: String): Uri {
        val photoFile = File(requireContext().applicationContext.filesDir, fileName)
        return FileProvider.getUriForFile(requireContext(), FILE_PROVIDER_AUTHORITY, photoFile)
    }

    private fun showPhoto(imageView: ImageView, fileName: String?) {
        if (imageView.tag == fileName) return

        val photoFile = fileName?.let {
            File(requireContext().applicationContext.filesDir, it)
        }

        if (photoFile?.exists() == true && photoFile.length() > 0) {
            imageView.doOnLayout { measuredView ->
                val scaledBitmap = PictureUtils().getScaledBitmap(
                    photoFile.path,
                    measuredView.width,
                    measuredView.height
                )
                imageView.setImageBitmap(scaledBitmap)
                imageView.tag = fileName
            }
        } else {
            imageView.setImageBitmap(null)
            imageView.tag = null
        }
    }

    private fun canResolveIntent(intent: Intent): Boolean {
        val packageManager: PackageManager = requireActivity().packageManager
        val resolvedActivity: ResolveInfo? =
            packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolvedActivity != null
    }

    private inner class ReportHolder(val row: ListItemReportBinding) :
        RecyclerView.ViewHolder(row.root) {

        fun bind(report: ReportEntity) {
            row.reportItemDate.text = dateFormat.format(Date(report.timestamp))
            row.reportItemDescription.text = report.description

            val fileName = report.photoUri?.let { Uri.parse(it).lastPathSegment }
            row.reportItemPhoto.contentDescription = getString(
                if (fileName == null) R.string.report_no_photo else R.string.report_photo_description
            )
            showPhoto(row.reportItemPhoto, fileName)
        }
    }

    private inner class ReportAdapter(val list: List<ReportEntity>) :
        RecyclerView.Adapter<ReportHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportHolder {
            val view = ListItemReportBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ReportHolder(view)
        }

        override fun onBindViewHolder(holder: ReportHolder, position: Int) {
            holder.bind(list[position])
        }

        override fun getItemCount(): Int = list.size
    }

    companion object {
        private const val FILE_PROVIDER_AUTHORITY = "com.example.cybersafecheck.fileprovider"
        private const val KEY_PHOTO_NAME = "photo_name"
        private const val KEY_PENDING_PHOTO_NAME = "pending_photo_name"
    }
}