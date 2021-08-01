package a.logic.app.extension

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.*
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewbinding.ViewBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


/*
*
    class G : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
            private set
    }
    }
*
* */

fun log(msg: String) =
    Log.i("lager", msg)


fun Context.toast(msg: String) =
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()


val Context.versionName: String?
    get() = try {
        val pInfo = packageManager.getPackageInfo(packageName, 0)
        pInfo?.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        null
    }

val Context.versionCode: Long?
    get() = try {
        val pInfo = packageManager.getPackageInfo(packageName, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pInfo?.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            pInfo?.versionCode?.toLong()
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        null
    }

val Context.screenSize: Point
    get() {
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
//        val display = wm.defaultDisplay
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size
    }


fun Context.screenSizeName(): String {
    val screenSize: Int = resources.configuration.screenLayout and
            Configuration.SCREENLAYOUT_SIZE_MASK

    return when (screenSize) {
        Configuration.SCREENLAYOUT_SIZE_LARGE -> "Large screen"
        Configuration.SCREENLAYOUT_SIZE_NORMAL -> "Normal screen"
        Configuration.SCREENLAYOUT_SIZE_SMALL -> "Small screen"
        else -> "Screen size is neither large, normal or small"
    }
}


val deviceName: String
    get() {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer))
            model.capitalize(Locale.getDefault())
        else
            manufacturer.capitalize(Locale.getDefault()) + " " + model
    }


fun Context.directionsTo(location: Location) {
    val lat = location.latitude
    val lng = location.longitude
    val uri = String.format(Locale.US, "http://maps.google.com/maps?daddr=%f,%f", lat, lng)
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
    }
}


fun AppCompatActivity.callTo(phoneNumber: String, requestCode: Int) {
    val intent = Intent(Intent.ACTION_CALL)

    intent.data = Uri.parse("tel:$phoneNumber")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOfNulls<String>(1)
            permissions[0] = Manifest.permission.CALL_PHONE
            requestPermissions(permissions, requestCode)
        } else
            startActivity(intent)
    else
        startActivity(intent)

}


fun setDialogBinding(
    binding: ViewBinding,
    gravity: Int = Gravity.BOTTOM,
    Animations: Int = R.style.dialogAnim,
    cancelableOnTouchOutside: Boolean = true
): AlertDialog {

    /*
    var binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
     val dialog = setDialogBinding( binding )
  */

    val alert = AlertDialog.Builder(binding.root.context)
    alert.setView(binding.root)
    alert.context.setTheme(Animations)
    val alertDialog = alert.create()
    if (alertDialog.isShowing) {
        alertDialog.dismiss()
    }
    alertDialog.setOnDismissListener {
        alertDialog.dismiss()
    }
    alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    alertDialog.setCanceledOnTouchOutside(cancelableOnTouchOutside)

    alertDialog.window!!.setGravity(gravity)
    alertDialog.window!!.setWindowAnimations(Animations)
    alertDialog.show()
    return (alertDialog)
}

fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

val getDate: String
    @SuppressLint("SimpleDateFormat")
    get() {
        val format = "dd/MM/yyyy hh:mm:ss.SSS"
        val formatter = SimpleDateFormat(format)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        return formatter.format(calendar.time)
    }

val Context.isNetwork: Boolean
    get() {
        val conMgr =
            getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMgr.activeNetworkInfo

        return netInfo != null

    }


fun getPriceFormat(price: String): String =
    DecimalFormat("###,###,###").format(price.toLong())


fun isEmailValid1(email: String): Boolean {
    val expression = """^[\w.-]+@([\w\-]+\.)+[A-Z]{2,4}$"""
    val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher: Matcher = pattern.matcher(email)
    return matcher.matches()
}

fun isEmailValid2(email: String): Boolean {
    return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

