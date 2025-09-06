@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.core.presentation.ui.group

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.RateBottomSheet
import dev.arkbuilders.rate.core.presentation.ui.RateBottomSheetTitle

data class EditGroupOptionsSheetState(val group: Group)

@Composable
fun EditGroupOptionsBottomSheet(
    sheetState: SheetState,
    state: EditGroupOptionsSheetState,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    RateBottomSheet(sheetState, onDismiss) {
        Box {
            RateBottomSheetTitle(
                stringResource(R.string.edit_group_name, state.group.name),
                onDismiss,
            )
            Column(
                modifier =
                    Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 72.dp,
                        bottom = 16.dp,
                    ),
            ) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onRename,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, ArkColor.BorderSecondary),
                ) {
                    Icon(
                        painter = painterResource(CoreRDrawable.ic_edit),
                        contentDescription = null,
                        tint = ArkColor.TextSecondary,
                    )
                    Text(
                        modifier = Modifier.padding(start = 6.dp),
                        text = stringResource(R.string.rename),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ArkColor.TextSecondary,
                    )
                }
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDelete,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, ArkColor.BorderError),
                ) {
                    Icon(
                        modifier = Modifier,
                        painter = painterResource(id = CoreRDrawable.ic_delete),
                        contentDescription = null,
                        tint = ArkColor.TextError,
                    )
                    Text(
                        modifier = Modifier.padding(start = 6.dp),
                        text = stringResource(CoreRString.delete),
                        color = ArkColor.TextError,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                    )
                }
            }
        }
    }
}
