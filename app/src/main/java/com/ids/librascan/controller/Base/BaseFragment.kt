package Base


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ids.librascan.utils.AppHelper

import utils.LocaleUtils
import java.util.*

open class BaseFragment :  Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppHelper.setAllTexts(view, requireContext())
     //   AppHelper.setLocal(requireContext())

    }

}
