package com.notdroid.notnews.db.newsApi

/**
 * Error messages of the api
 * */
enum class Errors(val message: String) {
  API_KEY_DISABLED("apiKeyDisabled"),
  API_KEY_EXHAUSTED("apiKeyExhausted"),
  API_KEY_INVALID("apiKeyInvalid"),
  API_KEY_MISSING("apiKeyMissing"),
  PARAMETER_INVALID("parameterInvalid"),
  PARAMETER_MISSING("parametersMissing"),
  RATE_LIMITED("rateLimited"),
  TOO_MANY_SOURCES("sourcesTooMany"),
  WE_DONT_HAVE_THAT_HERE("sourceDoesNotExist"),
  WE_DONT_KNOW_ACTUALLY("unexpectedError");

  companion object {
    fun isEumError(string: String): Errors? {
      return entries.filter { it.message == string }.getOrNull(0)
    }
  }
}