package com.groep4.mindfulness.utils

import com.google.android.material.textfield.TextInputLayout
import android.util.Patterns
import android.widget.EditText
import java.util.regex.Pattern

class LoginValidation {

        companion object {

            // Validatie messages
            private val WACHTWOORD_BELEID = "Voer een geldig wachtwoord in"
            private val EMAIL_VALIDATIE_MSG = "Voer een geldig email adres in"

            /**
             * Haal stringgegevens op uit de parameter
             * @param data - Kan EditText of string zijn
             * @return - String vanuit EditText of data als het een String is.
             */

            private fun getText(data: Any): String {
                var str = ""
                if (data is EditText) {
                    str = data.text.toString()
                } else if (data is String) {
                    str = data
                }
                return str
            }

            /**
             * Controleert of het een geldig email adres is.
             * @param data -  Kan EditText of string zijn
             * @param updateUI - als True en als data EditText is, stelt de functie de fout in op EditText of de TextInputLayout
             * @return - true als het een juist email adres is.
             */
            fun isValidEmail(data: Any, updateUI: Boolean = true): Boolean {
                val str = getText(data)
                val valid = Patterns.EMAIL_ADDRESS.matcher(str).matches()

                if (updateUI) {
                    val error: String? = if (valid) null else EMAIL_VALIDATIE_MSG

                    setError(data, error)
                }

                return valid
            }


            /**
             * Controleert of het wachtwoord geldig is volgens het volgende wachtwoordbeleid.
             * Wachtwoord moet minimaal minimaal 8 tekens lang zijn.
             * Wachtwoord moet minstens één nummer bevatten.
             * Wachtwoord moet minstens één hoofdletter bevatten.
             * Wachtwoord moet minstens één kleine letter bevatten.
             *
             * @param data -  Kan EditText of string zijn
             * @param updateUI - als True en als data EditText is, stelt de functie de fout in op EditText of de TextInputLayout
             * @return - true als het een juist Wachtwoord is.
             */
            fun isValidPassword(data: Any, updateUI: Boolean = true): Boolean {
                val str = getText(data)
                var valid = true


                //Wachtwoordbeleid controleren
                // Wachtwoord moet minimaal minimaal 8 tekens lang zijn
                if (str.length < 8) {
                    valid = false
                }
                // Wachtwoord moet ten minste één nummer bevatten
                var exp = ".*[0-9].*"
                var pattern = Pattern.compile(exp, Pattern.CASE_INSENSITIVE)
                var matcher = pattern.matcher(str)
                if (!matcher.matches()) {
                    valid = false
                }

                // Wachtwoord moet minstens één hoofdletter bevatten
                exp = ".*[A-Z].*"
                pattern = Pattern.compile(exp)
                matcher = pattern.matcher(str)
                if (!matcher.matches()) {
                    valid = false
                }

                // Wachtwoord moet minstens één kleine letter bevatten
                exp = ".*[a-z].*"
                pattern = Pattern.compile(exp)
                matcher = pattern.matcher(str)
                if (!matcher.matches()) {
                    valid = false
                }

                // Stel indien nodig een fout in
                if (updateUI) {
                    val error: String? = if (valid) null else WACHTWOORD_BELEID
                    setError(data, error)
                }

                return valid
            }

            /**
             * Plaatst fout op EditText of TextInputLayout van de EditText.
             * @param data - Moet EditText zijn
             * @param error - Message dat als fout wordt weergegeven, kan nul zijn als er geen fout wordt ingesteld
             */
            private fun setError(data: Any, error: String?) {
                if (data is EditText) {
                    if (data.parent.parent is TextInputLayout) {
                        (data.parent.parent as TextInputLayout).isHintEnabled = false
                        (data.parent.parent as TextInputLayout).isPasswordVisibilityToggleEnabled = false
                        (data.parent.parent as TextInputLayout).setError(error)
                    } else {
                        data.setError(error)

                    }
                }
            }

        }

    }
