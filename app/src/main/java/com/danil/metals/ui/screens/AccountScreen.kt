package com.danil.metals.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.danil.metals.R
import com.danil.metals.ui.MetalsUiState
import com.danil.metals.ui.MetalsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: MetalsViewModel,
    uiState: MetalsUiState
) {
    Column(
        modifier = Modifier
            .padding(
                start = dimensionResource(id = R.dimen.padding),
                end = dimensionResource(id = R.dimen.padding)
            )
            .background(color = MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding)))

        Text(
            stringResource(R.string.account),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding)))

        var isSigningIn by rememberSaveable { mutableStateOf(true) }
        if (uiState.accountEmail == null) {
            Row {
                Button(
                    onClick = { isSigningIn = !isSigningIn },
                    colors = buttonColors(
                        containerColor = if (isSigningIn) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        text = stringResource(R.string.sign_in),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isSigningIn) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onTertiary
                    )
                }

                Button(
                    onClick = { isSigningIn = !isSigningIn },
                    colors = buttonColors(
                        containerColor = if (!isSigningIn) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary
                    ),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text(
                        text = stringResource(R.string.sign_up),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (!isSigningIn) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onTertiary
                    )
                }
            }

            var email by rememberSaveable { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.medium
                    )
                    .fillMaxWidth()
                    .padding(
                        top = dimensionResource(id = R.dimen.padding),
                        start = dimensionResource(id = R.dimen.padding) / 2,
                        bottom = dimensionResource(id = R.dimen.padding)
                    )
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text(
                            stringResource(R.string.email),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = textFieldColors(
                        textColor = MaterialTheme.colorScheme.onSecondary,
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    isError = uiState.emailError.component1()
                )
                if (uiState.emailError.first) {
                    Text(
                        stringResource(uiState.emailError.second),
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(
                            stringResource(R.string.password),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = textFieldColors(
                        textColor = MaterialTheme.colorScheme.onSecondary,
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    isError = uiState.passwordError.component1()
                )
                if (uiState.passwordError.first) {
                    Text(
                        stringResource(uiState.passwordError.second),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding)))

                Button(onClick = {
                    viewModel.resetErrors()
                    if (isSigningIn) {
                        viewModel.trySignIn(email, password)
                    } else {
                        viewModel.trySignUp(email, password)
                    }
                }) {
                    Text(
                        text = if (isSigningIn) stringResource(R.string.sign_in) else stringResource(
                            R.string.sign_up
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.extraSmall
                    )
                    .padding(dimensionResource(id = R.dimen.padding) / 2)
            ) {
                Icon(
                    Icons.Rounded.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary
                )

                Spacer(Modifier.width(dimensionResource(id = R.dimen.padding)))

                Text(
                    uiState.accountEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

            Text(
                stringResource(R.string.abilities),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )

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
                    stringResource(R.string.viewing),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                if (uiState.verified == true) {
                    Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

                    Text(
                        stringResource(R.string.editing),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }

            Spacer(Modifier.height(dimensionResource(id = R.dimen.padding)))

            Button(
                onClick = { viewModel.trySignOut() },
                colors = buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(
                    stringResource(R.string.sign_out),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding)))
    }
}