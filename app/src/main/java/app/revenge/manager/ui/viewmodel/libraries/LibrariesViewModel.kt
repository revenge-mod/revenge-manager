package app.revenge.manager.ui.viewmodel.libraries

import android.content.Context
import cafe.adriel.voyager.core.model.ScreenModel
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.util.withContext

class LibrariesViewModel(
    context: Context
): ScreenModel {

    val libraries = Libs.Builder().withContext(context).build()

}