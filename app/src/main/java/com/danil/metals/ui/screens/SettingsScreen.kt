package com.danil.metals.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Nightlight
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.danil.metals.R
import com.danil.metals.ui.MetalsUiState
import com.danil.metals.ui.MetalsViewModel

val languageFromCode = mapOf(
    Pair("en", "English"),
    Pair("ru", "Русский")
)

@Composable
fun SettingsScreen(
    uiState: MetalsUiState,
    viewModel: MetalsViewModel
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

        Text(
            stringResource(R.string.settings),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

        Text(
            stringResource(R.string.language),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

        var expanded by rememberSaveable { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.extraSmall
                )
                .padding(dimensionResource(id = R.dimen.padding) / 2)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    languageFromCode[uiState.languages[0]] ?: uiState.languages[0],
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary
                )

                IconButton(
                    onClick = {
                        expanded = !expanded
                    }
                ) {
                    Icon(
                        if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null, tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
            if (expanded) {
                Spacer(
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding) / 2)
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.line_height))
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.extraSmall
                        )
                )

                for (elem in uiState.languages.slice(1..uiState.languages.lastIndex)) {
                    Text(
                        languageFromCode[elem] ?: elem,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier
                            .clickable {
                                expanded = !expanded
                                viewModel.setLanguage(elem)
                            }
                            .padding(dimensionResource(id = R.dimen.padding) / 2)
                    )
                }
            }
        }

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

        Text(
            stringResource(R.string.theme),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.extraSmall
                )
                .padding(dimensionResource(id = R.dimen.padding) / 2)
        ) {
            IconButton(
                onClick = { viewModel.changeTheme() },
                modifier = Modifier
                    .weight(0.5f)
                    .background(
                        color = if (uiState.isDark) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.extraSmall
                    )
            ) {
                Icon(
                    Icons.Rounded.Nightlight,
                    contentDescription = null,
                    tint = if (uiState.isDark) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSecondary
                )
            }

            IconButton(
                onClick = { viewModel.changeTheme() },
                modifier = Modifier
                    .weight(0.5f)
                    .background(
                        color = if (!uiState.isDark) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.extraSmall
                    )
            ) {
                Icon(
                    Icons.Rounded.WbSunny,
                    contentDescription = null,
                    tint = if (!uiState.isDark) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSecondary
                )
            }
        }

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

        var aboutExpanded by rememberSaveable { mutableStateOf(false) }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.about),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(
                onClick = { aboutExpanded = !aboutExpanded },
            ) {
                Icon(
                    if (aboutExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        if (aboutExpanded) {
            Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.extraSmall
                    )
                    .padding(dimensionResource(id = R.dimen.padding) / 2)
            ) {
                Text(
                    stringResource(R.string.information),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.padding)))

        Text(
            stringResource(R.string.by),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))
    }
}