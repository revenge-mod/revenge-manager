package app.revenge.manager.di

import app.revenge.manager.domain.repository.RestRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::RestRepository)
}