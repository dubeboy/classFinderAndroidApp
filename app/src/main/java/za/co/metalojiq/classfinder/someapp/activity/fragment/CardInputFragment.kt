package za.co.metalojiq.classfinder.someapp.activity.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import com.stripe.android.view.CardInputWidget;
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import za.co.metalojiq.classfinder.someapp.R
import za.co.metalojiq.classfinder.someapp.model.StatusRespose
import za.co.metalojiq.classfinder.someapp.rest.ApiClient
import za.co.metalojiq.classfinder.someapp.rest.ApiInterface
import java.lang.Exception
import kotlinx.android.synthetic.main.fragment_card_input_dialog.*

class CardInputFragment : BottomSheetDialogFragment() {
    lateinit var layout: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        layout = inflater.inflate(R.layout.fragment_card_input_dialog, container)
        Log.d(TAG, "Returning the view man!")
        return layout
    }

    override fun onViewCreated(vie: View?, savedInstanceState: Bundle?) {
        val mCardInputWidget = vie!!.findViewById(R.id.card_input_widget) as CardInputWidget
        val progresDialog: ProgressDialog = ProgressDialog.show(vie.context, "", "please wait...,  it takes some time to securely transact (<45sec)")
        progresDialog.dismiss()
        btn_pay.setText("Secure by depositing R${arguments.getString(ARG_DEPOSIT)}")
        val frag = this@CardInputFragment
        btn_pay.setOnClickListener {

            val cardToSave: Card? = mCardInputWidget.card
            if (cardToSave == null) { // if its null it means that something wrong happened
                Toast.makeText(vie.context, "Please validate your card!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(vie.context, "Securely connecting to the cloud", Toast.LENGTH_LONG).show()
                frag.dismiss()
                progresDialog.show() // show dialog
                val stripe: Stripe = Stripe(vie.context, "pk_test_dCdiutYbeaze5OvSDgwhz7UQ")
                stripe.createToken(cardToSave, object : TokenCallback {
                    override fun onSuccess(token: Token?) {
                        Log.d(TAG, "String tokeen type: " + token!!.type)
                        Log.d(TAG, "String token " + token)
                        // we must send the token to the server here
                        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
                        apiService
                                .postStripeToken(arguments.getInt(ARG_ACCOM_ID), token.id, arguments.getString(ARG_EMAIL),  arguments.getString(ARG_DEPOSIT) )
                                .enqueue(object : Callback<StatusRespose?> {
                                    override fun onResponse(call: Call<StatusRespose?>?, response: Response<StatusRespose?>) {
                                        if (response.body() != null) {
                                            if (response.body()!!.isStatus) {
                                                Toast.makeText(vie.context, "congratulations you have successfully secured this room",
                                                        Toast.LENGTH_LONG).show()
                                                progresDialog.dismiss()
//                                               here we have to notify the user and the owner send sms to to the owner and notify them as well
                                            } else {
                                                Toast.makeText(vie.context, """Oops could not procees with paying
                                                                    the deposit here is the error ${response.body()!!.message}""",
                                                        Toast.LENGTH_LONG).show()
                                                Log.d(TAG, "card error: ${response.body()!!.message}")
                                                progresDialog.dismiss()
                                            }
                                        } else {
                                            Toast.makeText(vie.context, "Oops could not process with paying the deposit because " +
                                                    "server returned nothing this should not have happened", Toast.LENGTH_LONG).show()
                                            progresDialog.dismiss()
                                        }
                                    }

                                    override fun onFailure(call: Call<StatusRespose?>?, t: Throwable?) {
                                        Toast.makeText(vie.context, "Server error, please try again latter", Toast.LENGTH_LONG).show()
                                        progresDialog.dismiss()
                                    }
                                })
                    }

                    override fun onError(error: Exception?) {
                        Toast.makeText(vie.context, "an arror happend, please try again", Toast.LENGTH_LONG).show()
                        progresDialog.dismiss()
                    }
                })
            }
        }
    }





    companion object {

        // TODO: Customize parameter argument names
        private val ARG_ACCOM_ID = "accomId"
        private val ARG_EMAIL = "accomEmail"
        private val ARG_DEPOSIT = "deposit"
        private val TAG = "CardInputFragment"

        // TODO: Customize parameters
        fun newInstance(accomId: Int, email: String , deposit: String): CardInputFragment {

            val fragment = CardInputFragment()
            val args = Bundle()
            args.putInt(ARG_ACCOM_ID, accomId)
            args.putString(ARG_EMAIL, email)
            args.putString(ARG_DEPOSIT, deposit)
            fragment.arguments = args
            return fragment
        }
    }

}
