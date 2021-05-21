package com.example.esiea3atd1.presentation.list

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.esiea3atd1.R
import com.example.esiea3atd1.presentation.Singletons
import com.example.esiea3atd1.presentation.api.UniversityResponse
import com.tapadoo.alerter.Alerter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UniversitiesListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private val adapter = UniversityAdapter(listOf(), ::onClickedUniversity)

    private val sharedPref: SharedPreferences? = activity?.getSharedPreferences("app", Context.MODE_PRIVATE)

    private val layoutManager = LinearLayoutManager(context)

    private lateinit var textViewCountry: TextView

    private lateinit var textViewUniversityNumber: TextView

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_university_list, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Recycle view
        recyclerView = view.findViewById(R.id.university_recyclerview)
        recyclerView.apply {
            layoutManager= this@UniversitiesListFragment.layoutManager
            adapter = this@UniversitiesListFragment.adapter
        }

        textViewCountry = view.findViewById(R.id.country_name)
        textViewUniversityNumber = view.findViewById(R.id.university_number)


        val list = getListFromCache()
        if(list.isEmpty()){
            callApi()
        } else {
            showList(list)
        }

    }

    private fun getListFromCache(): List<UniversityResponse> {
        //sharedPref
        //TODO
        return emptyList()
    }

    private fun saveListIntoCache() {
        //TODO
    }

    private fun callApi() {
        val country: String = arguments?.getString("countryNameForUni")!!
        Singletons.universityApi.getUniversitiesPerCountry(country).enqueue(object: Callback<List<UniversityResponse>> {
            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call<List<UniversityResponse>>, t: Throwable) {
                //Alert
                Alerter.Companion.create(activity!!)
                    .setTitle(R.string.pop_messages)
                    .setText(R.string.enableWifi)
                    .setIcon(R.drawable.ic_baseline_flight_24)
                    .setBackgroundColorRes(R.color.alertes)
                    .setDuration(3000)
                    .setOnClickListener {
                        Toast.makeText(context, R.string.enableWifi, Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.NavigateToMenu2)
                    }
                    .show()
            }
            override fun onResponse(
                    call: Call<List<UniversityResponse>>,
                    response: Response<List<UniversityResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val universitiesResponse: List<UniversityResponse> = response.body()!!
                    textViewCountry.text = response.body()!![0].country
                    saveListIntoCache()
                    showList(universitiesResponse)
                    val text = resources.getString(R.string.number_of_university) + " " + adapter.itemCount.toString() + " " + resources.getString(R.string.number_of_university2)
                    textViewUniversityNumber.text = text
                } else {
                    textViewCountry.text = getString(R.string.ErrorApi)
                }
            }
        })
    }


    private fun showList(universitiesList: List<UniversityResponse>) {
        adapter.updateList(universitiesList)
    }


    private fun onClickedUniversity(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}