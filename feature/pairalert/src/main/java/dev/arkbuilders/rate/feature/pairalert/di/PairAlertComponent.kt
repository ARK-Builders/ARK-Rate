package dev.arkbuilders.rate.feature.pairalert.di

import android.content.Context
import dagger.Component
import dev.arkbuilders.rate.core.db.dao.PairAlertDao
import dev.arkbuilders.rate.core.di.CoreComponent
import dev.arkbuilders.rate.core.domain.repo.CodeUseStatRepo
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.feature.pairalert.domain.repo.PairAlertRepo
import dev.arkbuilders.rate.feature.pairalert.domain.usecase.HandlePairAlertCheckUseCase
import dev.arkbuilders.rate.feature.pairalert.presentation.add.AddPairAlertViewModelFactory
import dev.arkbuilders.rate.feature.pairalert.presentation.main.PairAlertViewModelFactory

@PairAlertScope
@Component(dependencies = [CoreComponent::class], modules = [PairAlertModule::class])
interface PairAlertComponent {
    fun pairAlertVMFactory(): PairAlertViewModelFactory

    fun addPairAlertVMFactory(): AddPairAlertViewModelFactory.Factory

    fun currencyRepo(): CurrencyRepo

    fun pairAlertRepo(): PairAlertRepo

    fun pairAlertDao(): PairAlertDao

    fun codeUseStatRepo(): CodeUseStatRepo

    fun ctx(): Context

    fun handlePairAlertCheckUseCase(): HandlePairAlertCheckUseCase
}
