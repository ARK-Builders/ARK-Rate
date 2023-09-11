package dev.arkbuilders.rate.presentation.shared

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import dev.arkbuilders.rate.data.db.PairAlertConditionRepo
import dev.arkbuilders.rate.data.model.CurrencyCode
import dev.arkbuilders.rate.data.model.PairAlertCondition
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

class SharedViewModel(
    private val alertConditionRepo: PairAlertConditionRepo
) : ViewModel() {

    var pairAlertConditions = mutableStateListOf<PairAlertCondition>()
    var newCondition by mutableStateOf(PairAlertCondition.defaultInstance())

    var quickCurrencyPickedFlow = MutableSharedFlow<CurrencyCode>()

    init {
        viewModelScope.launch {
            pairAlertConditions.addAll(alertConditionRepo.getAll())
        }
    }

    fun onAlertConditionCodePicked(
        code: CurrencyCode,
        numeratorNotDenominator: Boolean,
        conditionId: Long
    ) {
        if (conditionId == 0L) {
            newCondition = if (numeratorNotDenominator)
                newCondition.copy(numeratorCode = code)
            else
                newCondition.copy(denominatorCode = code)

            return
        }

        val oldCondition = pairAlertConditions.find { it.id == conditionId }!!
        val conditionIndex = pairAlertConditions.indexOf(oldCondition)
        val updatedCondition = if (numeratorNotDenominator)
            oldCondition.copy(numeratorCode = code)
        else
            oldCondition.copy(denominatorCode = code)

        pairAlertConditions[conditionIndex] = updatedCondition
        viewModelScope.launch {
            alertConditionRepo.insert(updatedCondition)
        }
    }

    fun onConditionMoreLessChanged(condition: PairAlertCondition) {
        if (newCondition == condition) {
            newCondition = newCondition.copy(moreNotLess = !newCondition.moreNotLess)
            return
        }

        val conditionIndex = pairAlertConditions.indexOf(condition)
        val updatedCondition =
            condition.copy(moreNotLess = !condition.moreNotLess)
        pairAlertConditions[conditionIndex] = updatedCondition
        viewModelScope.launch {
            alertConditionRepo.insert(updatedCondition)
        }
    }

    fun onRatioChanged(
        condition: PairAlertCondition,
        oldInput: String,
        newInput: String
    ): String {
        val containsDigitsAndDot = Regex("[0-9]*\\.?[0-9]*")
        if (!containsDigitsAndDot.matches(newInput))
            return oldInput

        val containsDigit = Regex(".*[0-9].*")
        if (!containsDigit.matches(newInput)) {
            return newInput
        }

        val ratio = newInput.toFloat()

        viewModelScope.launch {
            if (newCondition == condition) {
                newCondition = newCondition.copy(ratio = ratio)
                return@launch
            }

            val conditionIndex = pairAlertConditions.indexOf(condition)
            val updatedCondition =
                condition.copy(ratio = ratio)
            pairAlertConditions[conditionIndex] = updatedCondition
            alertConditionRepo.insert(updatedCondition)
        }

        val leadingZeros = "^0+(?=\\d)".toRegex()

        return newInput.replace(leadingZeros, "")
    }

    fun onRemoveCondition(condition: PairAlertCondition) = viewModelScope.launch {
        alertConditionRepo.delete(condition.id)
        pairAlertConditions.remove(condition)
    }

    fun onNewConditionSave() {
        viewModelScope.launch {
            val id = alertConditionRepo.insert(newCondition)
            pairAlertConditions.add(newCondition.copy(id = id))
            newCondition = PairAlertCondition.defaultInstance()
        }
    }

    fun onQuickCurrencyPicked(code: CurrencyCode) {
        viewModelScope.launch {
            quickCurrencyPickedFlow.emit(code)
        }
    }
}

@Singleton
class SharedViewModelFactory @Inject constructor(
    private val alertConditionRepo: PairAlertConditionRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SharedViewModel(alertConditionRepo) as T
    }
}