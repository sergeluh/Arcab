package com.serg.arcab.ui.auth

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.serg.arcab.R
import com.serg.arcab.base.BaseFragment
import kotlinx.android.synthetic.main.navigation_view.view.*
import kotlinx.android.synthetic.main.fragment_birth.*
import org.koin.android.architecture.ext.sharedViewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class BirthFragment : BaseFragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()

    private lateinit var callback: Callback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_birth, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navBar.nextBtn.setOnClickListener {
            viewModel.saveBirth()
        }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.female -> viewModel.onGenderInputChanged(1)
                R.id.male -> viewModel.onGenderInputChanged(2)
                R.id.unspecified -> viewModel.onGenderInputChanged(3)
            }
        }

        viewModel.user.observe(viewLifecycleOwner, Observer {
            it?.also { user ->
                Timber.d("user ${user.gender}, ${user.birth_date}")
                when(user.gender) {
                    1 -> genderRadioGroup.check(R.id.female)
                    2 -> genderRadioGroup.check(R.id.male)
                    3 -> genderRadioGroup.check(R.id.unspecified)
                    null -> genderRadioGroup.check(0)
                }
                initDatePicker(user.birth_date)
            }
        })

        viewModel.birth.observe(viewLifecycleOwner, Observer {
            Timber.d("birth $it")
            navBar.nextBtn.isEnabled = it?.first != null && it.second != null

            it?.second?.also { timeMillis ->
                dateTextView.text = getDate(timeMillis)
            }
        })

        viewModel.goToIdInput.observe(viewLifecycleOwner, Observer {
            callback.goToEmiratesId()
        })
    }

    private fun initDatePicker(timeInMillis: Long?) {
        val calendar = Calendar.getInstance()
        if (timeInMillis != null) {
            calendar.timeInMillis = timeInMillis
        }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        datePicker.init(year,month,day) {_, y, m, d ->
            viewModel.onBirthDayInputChanged(y, m, d)
        }
    }

    private fun getDate(timeInMillis: Long): String {
        return try {
            val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            val netDate = Date(timeInMillis)
            sdf.format(netDate)
        } catch (ex: Exception) {
            ""
        }
    }

    interface Callback {
        fun goToEmiratesId()
    }

    companion object {

        const val TAG = "BirthFragment"

        @JvmStatic
        fun newInstance() = BirthFragment()
    }
}
