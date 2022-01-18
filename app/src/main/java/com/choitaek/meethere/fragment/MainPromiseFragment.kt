package com.choitaek.meethere.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.choitaek.meethere.R
import com.choitaek.meethere.activity.ShowBookmarkActivity
import com.choitaek.meethere.databinding.FragmentMainPromiseBinding
import com.choitaek.meethere.objects.BookmarkObject
import com.choitaek.meethere.retrofit.RetrofitManager
import com.choitaek.meethere.sharedpreferences.App
import com.choitaek.meethere.utils.Constants
import com.choitaek.meethere.utils.RESPONSE_STATE
import com.prolificinteractive.materialcalendarview.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainPromiseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainPromiseFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val bookmarkObjects = arrayListOf<BookmarkObject>()

    private lateinit var binding: FragmentMainPromiseBinding

    private val promises: ArrayList<CalendarDay> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMainPromiseBinding.inflate(inflater, container, false)

        refresh()

        binding.materialCalendar.setOnDateChangedListener(object : OnDateSelectedListener {
            override fun onDateSelected(
                widget: MaterialCalendarView,
                date: CalendarDay,
                selected: Boolean,
            ) {
                val intent = Intent(requireContext(), ShowBookmarkActivity::class.java)
                intent.putExtra("bookmarkData", bookmarkObjects)
                val fixedDate: CalendarDay = CalendarDay.from(date.year, date.month + 1, date.day)
                val dateString = fixedDate.toString().substringAfter('{').substringBefore('}')
                intent.putExtra("date", dateString)
                startActivity(intent)
            }
        })

        binding.buttonList.setOnClickListener {
            val intent = Intent(requireContext(), ShowBookmarkActivity::class.java)
            intent.putExtra("bookmarkData", bookmarkObjects)
            startActivity(intent)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    fun refresh() {
        Log.d("리프레시", "호출")
        bookmarkObjects.clear()
        promises.clear()

        binding.materialCalendar.removeDecorators()
        binding.materialCalendar.clearSelection()
        binding.materialCalendar.invalidateDecorators()
        
        val sundayDecorator = SundayDecorator()
        val saturdayDecorator = SaturdayDecorator()
        val todayDecorator = TodayDecorator(requireContext())

        binding.materialCalendar.addDecorator(sundayDecorator)
        binding.materialCalendar.addDecorator(saturdayDecorator)
        binding.materialCalendar.addDecorator(todayDecorator)

        RetrofitManager.instance.bookmarkListService(
            memberId = App.prefs.memberId,
            completion = { responseState, responseBody ->
                when (responseState) {

                    //API 호출 성공시
                    RESPONSE_STATE.OKAY -> {
                        Log.d(Constants.TAG, "API 호출 성공 : $responseBody")

                        //JSON parsing
                        //{}->JSONObject, []->JSONArray
                        val jsonObject = JSONObject(responseBody)
                        val statusCode = jsonObject.getInt("statusCode")

                        if (statusCode < 400) {
                            val dataArray = jsonObject.getJSONArray("data")

                            for (i in 0..dataArray.length() - 1) {

                                val iObject = dataArray.getJSONObject(i)
                                val id = iObject.getLong("id")
                                val dateName = iObject.getString("dateName")
//                              val username = iObject.getString("username")
                                val date = iObject.getString("date")
                                val placeName = iObject.getString("placeName")
                                val roadAddressName = iObject.getString("roadAddressName")
                                val addressName = iObject.getString("addressName")
                                val lat = iObject.getString("lat")
                                val lon = iObject.getString("lon")
                                val dateData = date.split("-")

                                bookmarkObjects.add(
                                    BookmarkObject(
                                        id,
                                        dateName,
                                        date,
                                        placeName,
                                        roadAddressName,
                                        addressName,
                                        lat,
                                        lon
                                    )
                                )

                                promises.add(CalendarDay.from(
                                    dateData[0].toInt(),
                                    dateData[1].toInt() - 1,
                                    dateData[2].toInt()
                                ))

                            }

                            for (proDay in promises) {
                                binding.materialCalendar.addDecorators(PromiseDecorator(
                                    requireActivity(),
                                    proDay))
                            }
                            Log.d("리프레시", "$promises")
                        } else {
                            val errorMessage = jsonObject.getString("message")
                            Log.d(Constants.TAG, "error message = $errorMessage")
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    RESPONSE_STATE.FAIL -> {
                        Log.d(Constants.TAG, "API 호출 실패 : $responseBody")
                    }
                }
            }
        )
    }

    class TodayDecorator(context: Context) : DayViewDecorator {
        private var date = CalendarDay.today()
        val drawable = context.resources.getDrawable(R.drawable.calendar_today)
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            val flag = (day == date)
            return day?.equals(date)!!
        }

        override fun decorate(view: DayViewFacade?) {
            view?.setBackgroundDrawable(drawable)
        }
    }

    class SundayDecorator : DayViewDecorator {
        private val calendar = Calendar.getInstance()
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            day?.copyTo(calendar)
            val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
            return weekDay == Calendar.SUNDAY
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(object : ForegroundColorSpan(Color.RED) {})
        }
    }

    class SaturdayDecorator : DayViewDecorator {
        private val calendar = Calendar.getInstance()
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            day?.copyTo(calendar)
            val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
            return weekDay == Calendar.SATURDAY
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(object : ForegroundColorSpan(Color.BLUE) {})
        }
    }

    class PromiseDecorator(context: Context, currentDay_: CalendarDay) : DayViewDecorator {
        val drawable = context.resources.getDrawable(R.drawable.calendar_promise)
        var currentDay = currentDay_
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return day?.equals(currentDay)!!
        }

        override fun decorate(view: DayViewFacade?) {
            view?.setBackgroundDrawable(drawable)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainPromiseFragment.
         */
// TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainPromiseFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}