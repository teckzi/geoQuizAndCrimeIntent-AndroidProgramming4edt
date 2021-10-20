package com.example.secondbooktests.CriminalIntent


import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat.format
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.secondbooktests.CriminalIntent.classes.Crime
import com.example.secondbooktests.CriminalIntent.viewmodel.CrimeDetailViewModel
import com.example.secondbooktests.R
import java.util.*
import androidx.lifecycle.Observer
import com.example.secondbooktests.CriminalIntent.data.DatePickerFragment
import com.example.secondbooktests.CriminalIntent.data.PhotoDialogFragment
import com.example.secondbooktests.CriminalIntent.data.TimePickerFragment
import com.example.secondbooktests.CriminalIntent.data.getScaledBitmap
import kotlinx.android.synthetic.main.fragment_crime.*
import java.io.File


private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0
private const val REQUEST_CONTACT = 1
private const val REQUEST_PHOTO = 2
private const val DATE_FORMAT = "EEE, MMM, dd"

class CrimeFragment : Fragment(),DatePickerFragment.Callbacks {

    private lateinit var crime: Crime
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var reportButton:Button
    private lateinit var suspectButton:Button
    private lateinit var callButton: ImageButton
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var photoButton: ImageButton
    private lateinit var photoView: ImageButton

    private val crimeDetailViewModel:CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)
        Log.d(TAG, "crimeId $crimeId")


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        titleField = view.findViewById(R.id.crime_title)
        dateButton = view.findViewById(R.id.crime_date)
        timeButton = view.findViewById(R.id.time_button)
        reportButton = view.findViewById(R.id.crime_report)
        suspectButton = view.findViewById(R.id.crime_suspect)
        solvedCheckBox =  view.findViewById(R.id.crime_solved)
        callButton = view.findViewById(R.id.call_button)
        photoButton = view.findViewById(R.id.crime_camera)
        photoView = view.findViewById(R.id.crime_photo)



        val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null){
                    val contactURI: Uri? = data.data
                    // Указывает, для каких полей запрос должен возвращать значениея
                    val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                    // Данный запрос ContactUri похож на "where"
                    val cursor = requireActivity().contentResolver
                        .query(contactURI!!, queryFields, null,null,null)
                    cursor?.use {
                        if (it.count == 0) {
                            return@registerForActivityResult
                        }
                        it.moveToFirst()
                        val suspect = it.getString(0)
                        Log.d(TAG,"suspect name $suspect")
                        crime.suspect = suspect
                        crimeDetailViewModel.saveCrime(crime)
                        suspectButton.text = suspect
                    }

                }
            }
        }

        suspectButton.apply {
            val pickContactIntent = Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI)
            setOnClickListener {
                //getResult.launch(pickContactIntent)
                startActivityForResult(pickContactIntent, REQUEST_CONTACT)
            }
            val packageManager: PackageManager = requireActivity().packageManager
            val resolvedActivity:ResolveInfo? = packageManager.resolveActivity(pickContactIntent,PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) isEnabled = false
        }

        callButton.apply {

            setOnClickListener {
                val phoneIntent = Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI)
                val status = ContextCompat.checkSelfPermission(context,android.Manifest.permission.READ_CONTACTS)
                if(status == PackageManager.PERMISSION_GRANTED){
                    //getPhone.launch(phoneIntent)
                    val callContactIntent =
                        Intent(Intent.ACTION_DIAL).apply {

                            val phone = crime.phone
                            data = Uri.parse("tel:$phone")

                        }
                    // this intent will call the phone number given in Uri.parse("tel:$phone")
                    startActivity(callContactIntent)

                }else ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.READ_CONTACTS,android.Manifest.permission.CALL_PHONE),
                    REQUEST_CONTACT)


            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val crimeId = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            Observer { crime ->
                crime?.let {
                    this.crime = crime
                    photoFile = crimeDetailViewModel.getPhoto(crime)
                    photoUri = FileProvider.getUriForFile(requireActivity(),"com.example.secondbooktests.fileprovider",photoFile)
                    updateUI()
                }
            })

    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                crime.title = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        }

        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            crime.isSolved = isChecked
        }

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }

        timeButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timeListener = TimePickerDialog.OnTimeSetListener { timePicker: TimePicker, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                timeButton.text = java.text.SimpleDateFormat("HH:mm").format(calendar.time)
            }
            TimePickerDialog(context, timeListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }

        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT,getCrimeReport())
                putExtra(
                    Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also { intent ->
                val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }

        photoButton.apply {
            val packageManager = requireActivity().packageManager

            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(captureImage,PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) isEnabled = false

            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

                val cameraActivities:List<ResolveInfo> =
                    packageManager.queryIntentActivities(captureImage,PackageManager.MATCH_DEFAULT_ONLY)

                for (each in cameraActivities){
                    requireActivity().grantUriPermission(each.activityInfo.packageName,photoUri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
                startActivityForResult(captureImage, REQUEST_PHOTO)

            }
        }

//        photoView.viewTreeObserver.apply {
//            if (isAlive) {
//                addOnGlobalLayoutListener {
//                    updatePhotoView()
//                }
//            }
//        }
        photoView.setOnClickListener {
            val fragment = PhotoDialogFragment(photoFile)
            fragment.show(childFragmentManager, "PhotoFragmentDialog")

        }

    }

    override fun onStop(){
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(photoUri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }

    private fun updateUI(){
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState() //пропуск анимации чекбокса
        }
        if (crime.suspect.isNotEmpty()){
            suspectButton.text = crime.suspect
        }
        updatePhotoView()
    }
//    private fun updatePhotoView(){
//        if (photoFile.exists()){
//            val bitmap = getScaledBitmap(photoFile.path,requireActivity())
//            photoView.setImageBitmap(bitmap)
//        }else photoView.setImageDrawable(null)
//    }

    private fun updatePhotoView() {
        if (photoFile.exists()) {
            val bitmap = getScaledBitmap(photoFile.path, photoView.width, photoView.height)
            photoView.setImageBitmap(bitmap)
        } else {
            photoView.setImageBitmap(null)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return

            requestCode == REQUEST_CONTACT && data != null -> {

                val contactUri: Uri? = data.data
                // queryFieldsName: a List to return the DISPLAY_NAME Column Only
                val queryFieldsName = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

                // queryFieldsId: a List to return the _ID Column Only, i will use it to get the suspect Id
                val queryFieldsId = arrayOf(ContactsContract.Contacts._ID)

                val cursorName = requireActivity().contentResolver
                    .query(contactUri!!, queryFieldsName, null, null, null)
                cursorName?.use {
                    if (it.count == 0) {
                        return
                    }

                    it.moveToFirst()
                    val suspect = it.getString(0)
                    crime.suspect = suspect
                    suspectButton.text = suspect
                }

                // I created another Cursor to get the suspect Id
                val cursorId = requireActivity().contentResolver
                    .query(contactUri!!, queryFieldsId, null, null, null)
                cursorId?.use {
                    if (it.count == 0) {
                        return
                    }

                    it.moveToFirst()
                    // here i put the suspect Id in contactId to use it later (to get the phone number)
                    val contactId = it.getString(0)

                    // This is the Uri to get a Phone number
                    val phoneURI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

                    // phoneNumberQueryFields: a List to return the PhoneNumber Column Only

                    val phoneNumberQueryFields = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)

                    // phoneWhereClause: A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself)
                    val phoneWhereClause = "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?"

                    // This val replace the question mark in the phoneWhereClause  val
                    val phoneQueryParameters = arrayOf(contactId)

                    val phoneCursor = requireActivity().contentResolver
                        .query(phoneURI, phoneNumberQueryFields, phoneWhereClause, phoneQueryParameters, null )

                    phoneCursor?.use { cursorPhone ->
                        cursorPhone.moveToFirst()
                        val phoneNumValue = cursorPhone.getString(0)

                        // after retrieving the phone number i put it in the crime.phone
                        crime.phone = phoneNumValue
                    }
                    crimeDetailViewModel.saveCrime(crime)
                }
            }

            requestCode == REQUEST_PHOTO -> {
                requireActivity().revokeUriPermission(photoUri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                updatePhotoView()}
        }
    }

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()
    }

    private fun getCrimeReport():String{
        val solvedString = if (crime.isSolved) getString(R.string.crime_report_solved)
        else getString(R.string.crime_report_unsolved)

        val dateString = format(DATE_FORMAT,crime.date).toString()
        var suspect = if (crime.suspect.isBlank()) getString(R.string.crime_report_no_suspect)
        else getString(R.string.crime_report_suspect,crime.suspect)

        return getString(R.string.crime_report,crime.title,dateString,solvedString,suspect)
    }

    companion object{
        fun newInstance(crimeId: UUID):CrimeFragment{
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID,crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }

}