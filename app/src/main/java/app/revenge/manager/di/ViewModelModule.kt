package app.revenge.manager.di

import app.revenge.manager.ui.viewmodel.home.HomeViewModel
import app.revenge.manager.ui.viewmodel.installer.InstallerViewModel
import app.revenge.manager.ui.viewmodel.installer.LogViewerViewModel
import app.revenge.manager.ui.viewmodel.libraries.LibrariesViewModel
import app.revenge.manager.ui.viewmodel.settings.AdvancedSettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val viewModelModule = module {
    factoryOf(::InstallerViewModel)
    factoryOf(::AdvancedSettingsViewModel)
    factoryOf(::HomeViewModel)
    factoryOf(::LogViewerViewModel)
    factoryOf(::LibrariesViewModel)
}