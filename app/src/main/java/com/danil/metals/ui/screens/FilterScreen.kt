package com.danil.metals.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController
import com.danil.metals.R
import com.danil.metals.ui.MetalsUiState
import com.danil.metals.ui.MetalsViewModel

@Composable
fun FilterScreen(
    uiState: MetalsUiState,
    viewModel: MetalsViewModel,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(
                start = dimensionResource(id = R.dimen.padding),
                end = dimensionResource(id = R.dimen.padding)
            )
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

        Row {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                stringResource(R.string.filter),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

        Text(
            stringResource(id = R.string.zc),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.extraSmall
                )
                .padding(dimensionResource(id = R.dimen.padding) / 2)
        ) {
            Text(
                stringResource(R.string.range),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondary
            )

            Column {
                RangeItem(uiState.zcRangeStart) { viewModel.setFilter(1, true, it) }
                Spacer(Modifier.height(dimensionResource(id = R.dimen.padding) / 4))
                RangeItem(uiState.zcRangeEnd) { viewModel.setFilter(1, false, it) }
            }

        }

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

        Text(
            stringResource(id = R.string.containment),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.extraSmall
                )
                .padding(dimensionResource(id = R.dimen.padding) / 2)
        ) {
            Text(
                stringResource(R.string.range),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondary
            )

            Column {
                RangeItem(uiState.containmentRangeStart) { viewModel.setFilter(2, true, it) }
                Spacer(Modifier.height(dimensionResource(id = R.dimen.padding) / 4))
                RangeItem(uiState.containmentRangeEnd) { viewModel.setFilter(2, false, it) }
            }

        }

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

        Text(
            stringResource(id = R.string.coefficient),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.extraSmall
                )
                .padding(dimensionResource(id = R.dimen.padding) / 2)
        ) {
            Text(
                stringResource(R.string.range),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondary
            )

            Column {
                RangeItem(uiState.coefficientRangeStart) { viewModel.setFilter(3, true, it) }
                Spacer(Modifier.height(dimensionResource(id = R.dimen.padding) / 4))
                RangeItem(uiState.coefficientRangeEnd) { viewModel.setFilter(3, false, it) }
            }

        }

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RangeItem(
    value: String,
    action: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = action,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        colors = textFieldColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            textColor = MaterialTheme.colorScheme.onSecondary
        ),
        placeholder = {
            Text(
                stringResource(R.string.not_applicable),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondary
            )
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        singleLine = true,
        modifier = Modifier
            .padding(
                start = dimensionResource(id = R.dimen.padding) / 2
            )
    )
}