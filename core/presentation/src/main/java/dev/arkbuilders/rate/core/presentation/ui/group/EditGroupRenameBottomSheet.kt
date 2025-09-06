@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.core.presentation.ui.group

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.usecase.ValidateGroupNameUseCase
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppButton
import dev.arkbuilders.rate.core.presentation.ui.AppHorDiv
import dev.arkbuilders.rate.core.presentation.ui.RateBottomSheet
import dev.arkbuilders.rate.core.presentation.ui.RateBottomSheetTitle

data class EditGroupRenameSheetState(val group: Group)

@Composable
fun EditGroupRenameBottomSheet(
    sheetState: SheetState,
    state: EditGroupRenameSheetState,
    validateGroupNameUseCase: ValidateGroupNameUseCase,
    onDismiss: () -> Unit,
    onDone: (String) -> Unit,
) {
    var groupRename by remember { mutableStateOf(state.group.name) }
    val doneEnabled by remember {
        derivedStateOf {
            validateGroupNameUseCase(groupRename)
        }
    }
    RateBottomSheet(sheetState, onDismiss) {
        Box {
            RateBottomSheetTitle(stringResource(R.string.rename_group), onDismiss)
            Column(
                modifier =
                    Modifier.padding(
                        top = 72.dp,
                        bottom = 16.dp,
                    ),
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(CoreRString.group_name),
                    fontWeight = FontWeight.Medium,
                    color = ArkColor.TextSecondary,
                )
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = ArkColor.Border,
                        ),
                    textStyle =
                        TextStyle.Default.copy(
                            fontSize = 16.sp,
                            color = ArkColor.TextPrimary,
                        ),
                    value = groupRename,
                    onValueChange = {
                        groupRename = it.replace(System.lineSeparator(), "")
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions =
                        KeyboardActions {
                            if (doneEnabled) {
                                onDone(groupRename)
                                onDismiss()
                            }
                        },
                )
                Spacer(Modifier.height(16.dp))
                AppHorDiv()
                Spacer(Modifier.height(16.dp))
                AppButton(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    onClick = { onDone(groupRename) },
                    enabled = doneEnabled,
                ) {
                    Text(
                        text = stringResource(CoreRString.save),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
