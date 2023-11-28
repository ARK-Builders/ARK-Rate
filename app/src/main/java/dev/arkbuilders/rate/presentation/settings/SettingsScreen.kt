@file:OptIn(ExperimentalMaterialApi::class)

package dev.arkbuilders.rate.presentation.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import dev.arkbuilders.rate.R
import dev.arkbuilders.rate.data.preferences.PreferenceKey
import dev.arkbuilders.rate.di.DIManager

@Destination
@Composable
fun SettingsScreen() {
    val vm: SettingsViewModel = viewModel(factory = DIManager.component.settingsVMFactory())

    if (vm.initialized) {
        Settings(vm)
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun Settings(vm: SettingsViewModel) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
        SettingsGroup(name = R.string.currencies) {
            SettingsNumberComp(name = R.string.fiat_fiat_rate_round,
                               state = vm.currencyRoundPrefs[PreferenceKey.FiatFiatRateRound]!!,
                               onSave = { state, value ->
                                   vm.onRoundSave(PreferenceKey.FiatFiatRateRound, state, value)
                               },
                               inputFilter = { input -> input.filter { it.isDigit() } },
                               onCheck = { true })
            SettingsNumberComp(name = R.string.crypto_crypto_rate_round,
                               state = vm.currencyRoundPrefs[PreferenceKey.CryptoCryptoRateRound]!!,
                               onSave = { state, value ->
                                   vm.onRoundSave(PreferenceKey.CryptoCryptoRateRound, state, value)
                               },
                               inputFilter = { input -> input.filter { it.isDigit() } },
                               onCheck = { true })
            SettingsNumberComp(name = R.string.fiat_crypto_rate_round,
                               state = vm.currencyRoundPrefs[PreferenceKey.FiatCryptoRateRound]!!,
                               onSave = { state, value ->
                                   vm.onRoundSave(PreferenceKey.FiatCryptoRateRound, state, value)
                               },
                               inputFilter = { input -> input.filter { it.isDigit() } },
                               onCheck = { true })
        }
        SettingsGroup(name = R.string.privacy) {
            SettingsSwitchComp(name = R.string.crash_reports,
                               state = vm.boolPrefs[PreferenceKey.CrashReport]!!) { state ->
                vm.onToggle(PreferenceKey.CrashReport, state)
            }
        }
    }
}

@Composable
fun SettingsGroup(@StringRes name: Int, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(stringResource(id = name), style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4),
               ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingsSwitchComp(@DrawableRes icon: Int? = null,
        @StringRes iconDesc: Int? = null,
        @StringRes name: Int,
        state: MutableState<Boolean>,
        onClick: (state: MutableState<Boolean>) -> Unit) {
    Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            onClick = {
                onClick(state)
            },
           ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Row(modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically) {
                    icon?.let {
                        Icon(painterResource(id = icon),
                             contentDescription = iconDesc?.let { stringResource(id = iconDesc) },
                             modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                            text = stringResource(id = name),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.body1,
                            textAlign = TextAlign.Start,
                        )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Switch(checked = state.value, onCheckedChange = { onClick(state) })
            }
            Divider()
        }
    }
}

@Composable
fun SettingsNumberComp(@DrawableRes icon: Int? = null,
        @StringRes iconDesc: Int? = null,
        @StringRes name: Int,
        state: MutableState<String>,
        onSave: (MutableState<String>, String) -> Unit,
        inputFilter: (String) -> String, // input filter for the preference
        onCheck: (String) -> Boolean) {

    var isDialogShown by remember {
        mutableStateOf(false)
    }

    if (isDialogShown) {
        Dialog(onDismissRequest = { isDialogShown = isDialogShown.not() }) {
            TextEditNumberDialog(name, state, inputFilter, onSave, onCheck) {
                isDialogShown = isDialogShown.not()
            }
        }
    }

    Surface(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            onClick = {
                isDialogShown = isDialogShown.not()
            },
           ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start) {
                icon?.let {
                    Icon(painterResource(id = icon),
                         contentDescription = iconDesc?.let { stringResource(id = iconDesc) },
                         modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                            text = stringResource(id = name),
                            style = MaterialTheme.typography.body1,
                            textAlign = TextAlign.Start,
                        )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                            text = state.value,
                            style = MaterialTheme.typography.body2,
                            textAlign = TextAlign.Start,
                        )
                }
            }
            Divider()
        }
    }
}

@Composable
private fun TextEditNumberDialog(@StringRes name: Int,
        state: MutableState<String>,
        inputFilter: (String) -> String, // filters out not needed letters
        onSave: (MutableState<String>, String) -> Unit,
        onCheck: (String) -> Boolean,
        onDismiss: () -> Unit) {

    var currentInput by remember {
        mutableStateOf(state.value)
    }

    var isValid by remember {
        mutableStateOf(onCheck(state.value))
    }

    Surface(shape = RoundedCornerShape(10.dp), color = Color.White) {

        Column(modifier = Modifier.wrapContentHeight().fillMaxWidth().padding(16.dp)) {
            Text(stringResource(id = name))
            Spacer(modifier = Modifier.height(8.dp))
            TextField(currentInput,
                      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                      onValueChange = {
                          // filters the input and removes redundant numbers
                          val filteredText = inputFilter(it)
                          isValid = onCheck(filteredText)
                          currentInput = filteredText
                      })
            Row {
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    onSave(state, currentInput)
                    onDismiss()
                }, enabled = isValid) {
                    Text("Save")
                }
            }
        }
    }
}