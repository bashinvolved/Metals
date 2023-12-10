package com.danil.metals.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController
import com.danil.metals.R
import com.danil.metals.ui.MetalsUiState
import com.danil.metals.ui.MetalsViewModel
import com.danil.metals.ui.representation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoBlock(
    text: Int,
    action: (String) -> Unit,
    modifier: Modifier = Modifier,
    value: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    isComboBox: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(id = text),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = modifier
        )

        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding) / 2))

        if (isComboBox) {
            Column(
                modifier = Modifier
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
            ) {
                var expanded by rememberSaveable { mutableStateOf(false) }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(representation[value.toInt()]!!).replace(' ', '\n'),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
                if (expanded) {
                    for (elem in representation.entries) {
                        if (elem.key != value.toInt()) {
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
                            Text(
                                stringResource(representation[elem.key]!!),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier
                                    .clickable {
                                        action(elem.key.toString())
                                        expanded = !expanded
                                    }
                            )
                        }

                    }

                }
            }
        } else {
            OutlinedTextField(
                value = value,
                onValueChange = action,
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = textFieldColors(
                    textColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
            )
        }

    }

    Spacer(Modifier.height(dimensionResource(id = R.dimen.padding) / 2))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    uiState: MetalsUiState,
    viewModel: MetalsViewModel,
    navController: NavHostController
) {
    var name by rememberSaveable { mutableStateOf(uiState.showingLocation.name) }
    var elements by rememberSaveable { mutableStateOf(uiState.showingLocation.elements) }
    var descriptions by rememberSaveable { mutableStateOf(uiState.showingLocation.descriptions) }

    Column(
        modifier = Modifier
            .padding(
                start = dimensionResource(id = R.dimen.padding),
                end = dimensionResource(id = R.dimen.padding)
            )
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

        Row {
            IconButton(onClick = {
                viewModel.updateExploredLocation(name, elements, descriptions)
                navController.popBackStack()
            }) {
                Icon(
                    Icons.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                stringResource(id = R.string.editing),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(id = R.string.name),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding) / 2))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = textFieldColors(
                    textColor = MaterialTheme.colorScheme.onBackground,
                    containerColor = MaterialTheme.colorScheme.background
                ),
                isError = uiState.passwordError.component1(),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding)))

        Text(
            stringResource(id = R.string.metals),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding)))

        for (elem in elements.entries) {
            Column(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.extraSmall
                    )
                    .padding(dimensionResource(id = R.dimen.padding) / 2)
                    .fillMaxWidth()
            ) {
                InfoBlock(
                    value = elem.key,
                    action = { elements = replaceMapKey(elements, elem.key, it) },
                    text = R.string.name,
                    keyboardType = KeyboardType.Text
                )

                InfoBlock(
                    value = elem.value[0].toString(),
                    action = {
                        elements = replaceValueOfImmutableMap(
                            elements, elem.key,
                            listOf(it.toDouble(), elem.value[1], elem.value[2])
                        )
                    },
                    text = R.string.containment,
                    keyboardType = KeyboardType.Decimal
                )

                InfoBlock(
                    value = elem.value[1].toString(),
                    action = {
                        elements = replaceValueOfImmutableMap(
                            elements, elem.key,
                            listOf(elem.value[0], it.toDouble(), elem.value[2])
                        )
                    },
                    text = R.string.mpc,
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )

                InfoBlock(
                    value = elem.value[2].toInt().toString(),
                    action = {
                        elements = replaceValueOfImmutableMap(
                            elements, elem.key,
                            listOf(elem.value[0], elem.value[1], it.toDouble())
                        )
                    },
                    text = R.string.degree_of_pollution,
                    isComboBox = true,
                    modifier = Modifier.fillMaxWidth(0.4f)
                )

                IconButton(
                    onClick = { elements = deleteValueOfImmutableMap(elements, elem.key) }
                ) {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }

            }

            Spacer(Modifier.height(dimensionResource(id = R.dimen.padding) / 2))

        }

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding) / 2))

        Button(
            onClick = {
                elements = elements + Pair(" ".repeat({
                    var count = 0
                    while (" ".repeat(count) in elements.keys)
                        count += 1
                    count
                }()), listOf(0.0, 0.0, 1.0))
            },
            colors = buttonColors(
                containerColor = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                stringResource(R.string.create_infoblock),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

        Text(
            stringResource(id = R.string.addition),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

        for ((i, elem) in descriptions.withIndex()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.extraSmall
                    )
                    .padding(dimensionResource(id = R.dimen.padding) / 2)
            ) {
                OutlinedTextField(
                    value = elem,
                    onValueChange = {
                        descriptions = descriptions.slice(0 until i) + listOf(it) +
                                if (i == descriptions.lastIndex) listOf() else
                                    descriptions.slice(i + 1..descriptions.lastIndex)
                    },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(
                        textColor = MaterialTheme.colorScheme.onSecondary,
                        containerColor = MaterialTheme.colorScheme.secondary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary
                    ),
                    textStyle = MaterialTheme.typography.bodySmall
                )

                IconButton(onClick = {
                    descriptions = descriptions.slice(0 until i) +
                            if (i == descriptions.lastIndex) listOf() else
                                descriptions.slice(i + 1..descriptions.lastIndex)
                }) {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }

            Spacer(Modifier.height(dimensionResource(R.dimen.padding) / 2))
        }

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding) / 2))

        Button(
            onClick = {
                descriptions = descriptions + listOf("")
            },
            colors = buttonColors(
                containerColor = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                stringResource(R.string.write_addition),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))
    }
    BackHandler {
        viewModel.updateExploredLocation(name, elements, descriptions)
        navController.popBackStack()
    }
}

fun replaceMapKey(
    map: Map<String, List<Double>>,
    key: String,
    targetKey: String
): Map<String, List<Double>> {
    val targetKey = targetKey.ifEmpty { " " }
    val resultMap = mutableMapOf<String, List<Double>>()
    for (entry in map.entries) {
        if (key == entry.key) {
            if (map.containsKey(targetKey)) {
                resultMap["$targetKey "] = entry.value
            } else {
                resultMap[targetKey] = entry.value
            }
        } else {
            resultMap[entry.key] = entry.value
        }
    }
    return resultMap.toMap()
}

fun replaceValueOfImmutableMap(
    map: Map<String, List<Double>>,
    key: String,
    value: List<Double>
): Map<String, List<Double>> {
    val mutableMap = map.toMutableMap()
    mutableMap.replace(key, value)
    return mutableMap.toMap()
}

fun deleteValueOfImmutableMap(
    map: Map<String, List<Double>>,
    key: String
): Map<String, List<Double>> {
    val keys = map.keys.toMutableList()
    val values = map.values.toMutableList()
    val willRemove = keys.indexOf(key)
    keys.removeAt(willRemove)
    values.removeAt(willRemove)
    return keys.zip(values).toMap()
}

