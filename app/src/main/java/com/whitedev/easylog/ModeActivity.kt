package com.whitedev.easylog

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.widget.Toast
import com.github.ivbaranov.rxbluetooth.RxBluetooth
import com.whitedev.easylog.event.EventCamera
import com.whitedev.easylog.event.EventDouchette
import com.whitedev.easylog.event.EventRestartCamera
import com.whitedev.easylog.event.EventShowDouchetteButton
import com.whitedev.easylog.pojo.ApiJerem
import com.whitedev.easylog.pojo.Barcode
import com.whitedev.easylog.utils.Constants.Companion.ERROR_EXPIRED_TOKEN
import com.whitedev.easylog.utils.Constants.Companion.PREFS_ID
import com.whitedev.easylog.utils.Constants.Companion.SUCCESS
import com.whitedev.easylog.utils.Constants.Companion.USER_TOKEN
import com.whitedev.easylog.utils.Utils
import kotlinx.android.synthetic.main.activity_mode.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ModeActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var token: String
    lateinit var barcodeList: ArrayList<Barcode>
    private var successMsg: String? = null

    var datacode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode)

        barcodeList = ArrayList()
        toolbar = toolbar_mode

        token = getSharedPreferences(PREFS_ID, AppCompatActivity.MODE_PRIVATE)!!.getString(
            USER_TOKEN, ""
        )

        prepareToolbar(-1, null)

        startChooseMode(savedInstanceState)

        getMacAddressListBt()
        prepareBt()
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
        registerReceiver(netSwitchReceiver, IntentFilter(NetworkChangeReceiver.NETWORK_SWITCH_FILTER))
    }

    override fun onPause() {
        EventBus.getDefault().unregister(this)
        unregisterReceiver(netSwitchReceiver)
        super.onPause()
    }

    //For douchette only
    override fun onKeyDown(keyCode: Int, msg: KeyEvent): Boolean {
        return when (msg.keyCode) {
            //obligatoire sinon on perd la navigation des fragments
            4 -> {
                if (fragmentManager.backStackEntryCount > 0) {
                    fragmentManager.popBackStack()
                } else {
                    super.onBackPressed()
                }
                false
            }

            //fin de scan douchette
            66 -> {
                sendData(datacode)
                datacode = ""
                false
            }

            //keycode pour les chiffres 1..9
            7, 8, 9, 10, 11, 12, 13, 14, 15, 16 -> {
                datacode += msg.unicodeChar.toChar()
                true
            }

            else -> {
                true
            }
        }
    }


    private fun startChooseMode(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.root_layout, ChooseModeFragment.newInstance(), "ChooseModeFragment")
                .commit()
        }
    }

    private fun prepareToolbar(zone: Int, description: String?) {
        if (zone > 0) {
            toolbar.title = getString(R.string.zone_space) + zone
            toolbar.subtitle = description
        } else {
            toolbar.title = getString(R.string.unknown_zone)
            toolbar.subtitle = getString(R.string.unknown_zone_subtitle)
        }

        this.setSupportActionBar(toolbar)
    }

    private fun prepareBt() {
        val rxBluetooth = RxBluetooth(this)

        rxBluetooth.observeAclEvent()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.computation())
            .subscribe { event ->
                when (event.action) {
                    BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                        EventBus.getDefault().post(EventShowDouchetteButton(false))
                        if (fragmentManager.backStackEntryCount > 0) {
                            fragmentManager.popBackStack()
                        }
                    }

                    BluetoothDevice.ACTION_ACL_CONNECTED -> {
                        val btDevice = android.provider.Settings.Secure.getString(
                            this.baseContext.contentResolver,
                            "bluetooth_address"
                        )

                        listDevicesBt.filter { s ->
                            if (s == btDevice) {
                                EventBus.getDefault().post(EventShowDouchetteButton(true))
                            }
                            true
                        }
                    }

                }
            }
    }

    private fun sendData(data: String) {
        if (!data.isEmpty()) {
            token.let {
                val retrofit = Retrofit.Builder()
                    .baseUrl(Utils.checkBaseUrl(this))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val service = retrofit.create(InterfaceApi::class.java)
                val call: Call<ApiJerem> = service.getQrCodeStatus(token, data)

                call.enqueue(object : Callback<ApiJerem> {
                    override fun onResponse(call: Call<ApiJerem>, response: Response<ApiJerem>) {
                        response.body()?.let { it ->
                            if (it.status == SUCCESS) {
                                EventBus.getDefault().post(EventRestartCamera())

                                Utils.playNotif(this@ModeActivity, true)
                                Utils.showSnackBar("SUCCESS:$data", true, activity_mode_layout)

                                if (null != it.number_zone) {
                                    prepareToolbar(it.number_zone, it.description_zone)

                                    handleSentDataResult(data, "ZONE")

                                } else if (it.success_message != null && (successMsg == null || successMsg != it.success_message) && null != it.quantity && it.quantity > 1) {
                                    successMsg = it.success_message
                                    Utils.showDialogColis(this@ModeActivity, it.quantity)
                                    handleSentDataResult(data, "NON_CONFIRME")
                                } else {
                                    handleSentDataResult(data, "ENVOYE")
                                }
                            } else {
                                handleSentDataResult(data, "ERROR")

                                when (it.error_message) {
                                    ERROR_EXPIRED_TOKEN -> {
                                        val i = Intent(this@ModeActivity, SplashActivity::class.java)
                                        startActivity(i)
                                        // finish()
                                    }
                                    else -> {
                                        EventBus.getDefault().post(EventRestartCamera())
                                        Utils.playNotif(this@ModeActivity, false)
                                        Utils.showSnackBar("ERROR:" + it.error_message, false, activity_mode_layout)
                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ApiJerem>, t: Throwable) {
                        handleSentDataResult(data, "FAILURE")

                        Toast.makeText(
                            this@ModeActivity,
                            "Problème de connexion, votre QrCode sera renvoyé plus tard !",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }

    lateinit var listDevicesBt: List<String>

    private fun getMacAddressListBt() {
        token.let { tok ->
            val retrofit = Retrofit.Builder()
                .baseUrl(Utils.checkBaseUrl(this))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(InterfaceApi::class.java)

            val call: Call<ApiJerem> = service.getListMacScanner(tok)

            call.enqueue(object : Callback<ApiJerem> {
                override fun onResponse(call: Call<ApiJerem>, response: Response<ApiJerem>) {
                    // generateAuth(response.body().getEmployeeArrayList())
                    Handler().postDelayed({
                        response.body()?.let { body ->
                            if (body.status == SUCCESS) {
                                body.mac?.let {
                                    listDevicesBt = it
                                }
                            }
                        }
                    }, 1500)
                }

                override fun onFailure(call: Call<ApiJerem>, t: Throwable) {
                    Toast.makeText(
                        this@ModeActivity,
                        "Erreur chargement liste device bluetooth compatibles !",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })

        }
    }


    private fun handleSentDataResult(code: String, status: String) {
        //on supprime ici un élément qui demande un double scan pour le rajouté ensuite avec son nouveau status (SUCCESS)
        barcodeList.filterIndexed { index, barcode ->
            if (barcode.barcodeValue == code && barcode.sentMsg == "NON_CONFIRME") {
                barcodeList.removeAt(index)
            }
            true
        }

        barcodeList.add(Barcode(code, status))
        EventBus.getDefault().post(EventDouchette(code, status))
    }

    //if network has been lost --> FAILURE
    private var netSwitchReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val isConnectionAvailable = intent.extras.getBoolean("is_connected")

            if (isConnectionAvailable) {
                if (barcodeList.size > 0) {
                    for (barcode: Barcode in barcodeList) {
                        if (barcode.sentMsg == "FAILURE") {
                            barcodeList.remove(barcode)
                            sendData(barcode.barcodeValue)
                        }
                    }
                }
                Utils.showSnackBar("SUCCESS: CONNECTIVITY", true, activity_mode_layout)
                Toast.makeText(
                    context,
                    "Yep",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Utils.showSnackBar("ERROR:NO CONNECTIVITY", false, activity_mode_layout)
                Toast.makeText(
                    context,
                    "NOPE!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @Subscribe
    fun handleSendEvent(event: EventCamera) {
        sendData(event.data)
    }
}